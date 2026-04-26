package com.riversoft.dtask.tasks;

import com.riversoft.dbtool.export.DataSourceCopierWithoutFK;
import com.riversoft.dbtool.util.DatabaseManager;
import com.riversoft.patch.util.ZipUtils;
import com.riversoft.util.PropertiesLoader;
import com.riversoft.util.jackson.JsonMapper;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.tools.ant.BuildException;
import org.h2.jdbcx.JdbcDataSource;
import org.jumpmind.db.model.Column;
import org.jumpmind.db.model.Table;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import java.io.File;
import java.io.IOException;
import java.sql.Types;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Created by exizhai on 25/12/2014.
 */
public class SnapshotSyncTask extends DBBaseTask {

    private Logger logger = LoggerFactory.getLogger("SnapshotSyncTask");

    private static final String DEV_UPGRADE_DIR = "dev_upgrade";
    private static final String FILE_RESOURCE_SCHEMA = "file:";
    private static final String SAVE_ROLE = "PRO_SYS";
    private Set<String> incrementalSyncTableSet = new HashSet<>();
    private Set<String> copyDataTableSet = new HashSet<>();

    DataSourceCopierWithoutFK dataSourceCopierWithoutFK = null;
    private File safe;
    private File snapshot;
    private PropertiesLoader propertiesLoader;
    private File workspace;
    private File h2;
    private DatabaseManager systemDatabaseManager;
    private NamedParameterJdbcTemplate systemJdbcTemplate;
    private SnapshotMetaInfo snapshotMetaInfo;
    private int threads = 10;
    private String currentPlatformVersion;
    private boolean fullSync = true;

    private ExecutorService executorService = null;

    public void setSafe(String safe) {
        this.safe = new File(safe);
    }

    public void setSnapshotName(String snapshotName) {
        this.snapshot = new File(snapshotName);
    }

    @Override
    public void dbOperation() {
        systemDatabaseManager = new DatabaseManager(dataSource);

        try {
            initJdbcTemplate();
            cleanUpWorkspace();
            unzipSnapshot();
            initParameters();
            if (isAllowed()) {
                sync();
            } else {
                throw new BuildException("校验不通过，不能只能当前操作。");
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new BuildException("开发包升级失败，", e);
        }
    }

    private void sync() {
        logger.info("开始同步");
        long start0 = System.currentTimeMillis();
        try{
            initDataSourceCopier();

            if(syncConfiguration()) {
                long start1 = System.currentTimeMillis();

                logger.info("系统配置同步完毕:{} ms,开始同步动态表.", start1 - start0);
                if(syncTables()) {
                    logger.info("动态表同步完毕:{} ms.", System.currentTimeMillis() - start1);
                    updateMetadata();
                    insertLog();
                } else {
                    logger.warn("动态表同步失败.");
                }
            } else {
                logger.warn("系统配置同步失败,不会继续动态表同步.");
            }
            logger.info("执行完毕:{} ms", System.currentTimeMillis() - start0);
        } catch (Exception e) {
            logger.error("同步失败", e);
        }

    }

    private boolean syncTables() {
        executorService = Executors.newFixedThreadPool(threads);
        List<Future<Boolean>> futures = new ArrayList<>();
        if(fullSync) {
            logger.info("将进行动态表全量同步.");
            Table table = systemDatabaseManager.findTable("TB_TABLE");
            String tableName;

            if (table != null) {
                Column column = table.findColumn("NAME");
                tableName = column.getName();
                String getAllTablesSQL = "select * from " + table.getName();
                List<Map<String, Object>> tables = systemJdbcTemplate.queryForList(getAllTablesSQL, (Map) null);

                for (Map<String, Object> m : tables) {
                    futures.add(executorService.submit(new Sync((String) m.get(tableName))));
                }
            } else {
                throw new BuildException("当前系统中不存在表:TB_TABLE");
            }
        } else {
            logger.info("将进行增量同步.");
            for (String tableName: incrementalSyncTableSet) {
                futures.add(executorService.submit(new Sync(tableName)));
            }
        }

        boolean result = true;
        for (Future<Boolean> future : futures) {
            try {
                if (!future.get()) {
                    result = false;
                }
            } catch (Exception e) {
                result = false;
            }
        }

        executorService.shutdown();

        if(!copyDataTableSet.isEmpty()) {
            if(result) {
                logger.info("需要拷贝动态表数据.");
                result = dataSourceCopierWithoutFK.copy(copyDataTableSet, null, false, true, true);
            } else {
                logger.warn("动态表结构同步没有完全成功,跳过数据同步.");
            }
        }

        return result;
    }

    private boolean syncConfiguration() throws Exception {
        logger.info("开始同步系统配置");
        return dataSourceCopierWithoutFK.copyAll(copyDataTableSet, false, true, true);
    }

    private void initDataSourceCopier() {
        JdbcDataSource h2ds = new JdbcDataSource();

        h2ds.setUser("sa");
        h2ds.setPassword("");

        h2ds.setURL(buildJDBCURL(trimH2Ext(h2.getAbsolutePath())));
        dataSourceCopierWithoutFK = new DataSourceCopierWithoutFK(h2ds, dataSource, threads);
    }

    private boolean isAllowed() {
        logger.info("检查是否允许同步");
        boolean platformVersionsMatch = isPlatformVersionsMatch();

        boolean supportDev = isSupportDev();

        boolean isKeyMatch = isKeyMatch();

        boolean isValidSnapshotVersion = isValidSnapshotVersion();

        return platformVersionsMatch && supportDev && isKeyMatch && isValidSnapshotVersion;
    }

    private boolean isValidSnapshotVersion() {
        int systemVersion = getSystemVersion();
        boolean isValidSnapshotVersion = snapshotMetaInfo.snapshotVersion > systemVersion;

        setFullSync(systemVersion);

        if(!isValidSnapshotVersion) {
            throw new BuildException("要同步的快照版本必须大于当前系统中快照版本，请检查。");
        }
        return isValidSnapshotVersion;
    }

    private void setFullSync(int systemVersion) {
        fullSync = systemVersion == 0 || snapshotMetaInfo.snapshotVersion > systemVersion + 1;
    }

    private boolean isSupportDev() {
        boolean supportDev = SAVE_ROLE.equals(propertiesLoader.getProperty("safe.role", null));
        if(!supportDev) {
            throw new BuildException("当前系统中safe.properties设置不允许做同步，请检查。");
        }
        return supportDev;
    }

    private boolean isKeyMatch() {
        String uniqueKeyInSystem = getSystemShortKey();
        boolean isKeyMatch = StringUtils.isEmpty(uniqueKeyInSystem) || snapshotMetaInfo.shortKey.equals(uniqueKeyInSystem);
        if(!isKeyMatch) {
            throw new BuildException("shortKey和目标系统不匹配，不允许做同步。");
        }
        return isKeyMatch;
    }

    private boolean isPlatformVersionsMatch() {
        currentPlatformVersion = getPlatformVersionFromJar();

        if (StringUtils.isEmpty(currentPlatformVersion)) {
            throw new BuildException("获取当前系统版本出错。");
        }

        boolean platformVersionsMatch = currentPlatformVersion.equalsIgnoreCase(snapshotMetaInfo.platformVersion);
        if(!platformVersionsMatch) {
            throw new BuildException("platform version 不匹配，不能执行同步。");
        }
        return platformVersionsMatch;
    }

    private String getSystemShortKey() {
        return getPropFromDB("key");
    }

    private int getSystemVersion() {
        String currentVersion = getPropFromDB("version");
        if(StringUtils.isEmpty(currentVersion)) {
            return 0;
        } else {
            return Integer.valueOf(currentVersion);
        }
    }

    private String getPropFromDB(String name) {
        Table table = systemDatabaseManager.findTable("TPL_CURRENT");
        if(table != null) {
            Column nameColumn = table.findColumn("NAME");
            Column propertyValueColumn = table.findColumn("PROPERTY_VALUE");

            String getPropSQL = "select " + propertyValueColumn.getName() + " from " + table.getName()
                    + " where " + nameColumn.getName() + " = :" + nameColumn.getName();

            Map<String, String> paras = new HashMap<>();
            paras.put(nameColumn.getName(), name);

            List<Map<String, Object>> result = systemJdbcTemplate.queryForList(getPropSQL, paras);

            if(!result.isEmpty()) {
                return (String)result.get(0).get(propertyValueColumn.getName());
            } else {
                return null;
            }
        } else {
            throw new BuildException("当前系统中不存在表:TPL_CURRENT");
        }
    }

    private void unzipSnapshot() {
        logger.info("解压快照");
        ZipUtils.unCompress(snapshot, workspace);

        File propFile;
        Collection<File> props = FileUtils.listFiles(workspace, new String[]{"properties"}, true);
        if (!props.isEmpty()) {
            propFile = props.iterator().next();
        } else {
            throw new BuildException("sho中没有找到properties文件");
        }

        String configurationPropUri = FILE_RESOURCE_SCHEMA + propFile.getAbsolutePath();
        String safePropUri = FILE_RESOURCE_SCHEMA + safe.getAbsolutePath();
        propertiesLoader = new PropertiesLoader(configurationPropUri, safePropUri);

        Collection<File> files = FileUtils.listFiles(workspace, new String[]{"h2.db"}, true);
        if (!files.isEmpty()) {
            h2 = files.iterator().next();
        }
    }

    private void cleanUpWorkspace() throws IOException {
        logger.info("清除工作区");
        workspace = new File("tmp", DEV_UPGRADE_DIR);
        if (workspace.exists()) {
            FileUtils.deleteDirectory(workspace);
            workspace.mkdirs();
        }
    }

    private void insertLog() {
        logger.info("开始登记操作日志。");
        Table table = systemDatabaseManager.findTable("TPL_SNAPSHOT_RECORD");
        if(table != null) {
            String insertSQL = systemDatabaseManager.buildInsertSql(table);
            String maxIdSQL = "select max(ID) from " + table.getName();

            Column idColumn = table.findColumn("ID");
            Column versionColumn = table.findColumn("VERSION");
            Column uidColumn = table.findColumn("CREATE_UID");
            Column oprMemoColumn = table.findColumn("OPR_MEMO");
            Column createDateColumn = table.findColumn("CREATE_DATE");
            Column oprClassColumn = table.findColumn("OPR_CLASS");
            Column oprMethodColumn = table.findColumn("OPR_METHOD");
            Column oprArgsColumn = table.findColumn("OPR_ARGS");

            Long id = systemJdbcTemplate.queryForObject(maxIdSQL, (Map)null, Long.class);
            if(id == null) id = 0l;

            Map<String, Object> paras = new HashMap<>();
            paras.put(idColumn.getName(), id + 1);
            paras.put(versionColumn.getName(), snapshotMetaInfo.snapshotVersion);
            paras.put(uidColumn.getName(), "admin");
            paras.put(oprMemoColumn.getName(), "快照[版本:" + snapshotMetaInfo.snapshotVersion + "]部署");
            paras.put(createDateColumn.getName(), new Date());
            paras.put(oprClassColumn.getName(), "");
            paras.put(oprMethodColumn.getName(), "");
            paras.put(oprArgsColumn.getName(), "");

            systemJdbcTemplate.update(insertSQL, paras);
        }
        logger.info("完成操作日志登记。");
    }

    private void updateMetadata() {
        logger.info("开始更新TPL_CURRENT。");
        Table table = systemDatabaseManager.findTable("TPL_CURRENT");
        if(table != null) {
            systemJdbcTemplate.update("delete from " + table.getName(), (Map) null);
            insert(table, "id", snapshotMetaInfo.ID, "ID");
            insert(table, "key", snapshotMetaInfo.shortKey, "唯一键");
            insert(table, "version", snapshotMetaInfo.snapshotVersion, "快照版本");
            insert(table, "date", snapshotMetaInfo.createDate, "快照生成时间");
            insert(table, "name", snapshotMetaInfo.name, "快照名");
            insert(table, "description", snapshotMetaInfo.description, "快照描述");
            insert(table, "platformVersion", currentPlatformVersion, "平台版本");
        }
        logger.info("完成TPL_CURRENT更新。");
    }

    private void insert(Table table, String name, Object prop, String desc) {
        Column nameColumn = table.findColumn("NAME");
        Column propertyColumn = table.findColumn("PROPERTY_VALUE");
        Column descColumn = table.findColumn("BUSI_NAME");

        String insertSQL = systemDatabaseManager.buildInsertSql(table);

        Map<String, Object> paras = new HashMap<>();
        paras.put(nameColumn.getName(), name);
        paras.put(propertyColumn.getName(), prop);
        paras.put(descColumn.getName(), desc);

        systemJdbcTemplate.update(insertSQL, paras);
    }

    private void initParameters() {
        logger.info("初始化参数");
        this.threads = Integer.valueOf(propertiesLoader.getProperty("safe.sync.threads"), 10);

        snapshotMetaInfo = new SnapshotMetaInfo();
        snapshotMetaInfo.ID = propertiesLoader.getProperty("snapshot.id", "");
        snapshotMetaInfo.name = propertiesLoader.getProperty("snapshot.name", "");
        snapshotMetaInfo.description = propertiesLoader.getProperty("snapshot.description", "");

        if (StringUtils.isEmpty(propertiesLoader.getProperty("snapshot.version", null))) {
            throw new BuildException("config.properties中snapshot.version不存在。");
        }
        snapshotMetaInfo.snapshotVersion = Integer.valueOf(propertiesLoader.getProperty("snapshot.version"));

        String sourcePlatformVersion = propertiesLoader.getProperty("snapshot.platformVersion", null);
        if (StringUtils.isEmpty(sourcePlatformVersion)) {
            throw new BuildException("config.properties中snapshot.platformVersion不存在。");
        }
        snapshotMetaInfo.platformVersion = sourcePlatformVersion;

        snapshotMetaInfo.shortKey = propertiesLoader.getProperty("snapshot.shortKey", null);
        if (StringUtils.isEmpty(snapshotMetaInfo.shortKey)) {
            throw new BuildException("config.properties中snapshot.shortKey不存在。");
        }

        snapshotMetaInfo.createDate = propertiesLoader.getProperty("snapshot.createDate", null);
        if(StringUtils.isEmpty(snapshotMetaInfo.createDate)) {
            throw new BuildException("config.properties中snapshot.createDate不存在。");
        }
        snapshotMetaInfo.modifiedTables = propertiesLoader.getProperty("snapshot.modifiedTables", "");

        String incrementalSyncTables = snapshotMetaInfo.modifiedTables;
        if(StringUtils.isNotBlank(incrementalSyncTables)) {
            logger.info("增加或者修改动态表:{}", incrementalSyncTables);
            incrementalSyncTableSet = JsonMapper.defaultMapper().fromJson(incrementalSyncTables, HashSet.class);
        }

        snapshotMetaInfo.copyDataTables = propertiesLoader.getProperty("snapshot.copyDataTables", "");
        String copyDataTables = snapshotMetaInfo.copyDataTables;
        if(StringUtils.isNotBlank(copyDataTables)) {
            logger.info("需要拷贝动态表数据:{}", copyDataTables);
            copyDataTableSet = JsonMapper.defaultMapper().fromJson(copyDataTables, HashSet.class);
        }
    }

    private void initJdbcTemplate() {
        systemJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
    }

    private class SnapshotMetaInfo {
        public String ID;
        public String name;
        public String description;
        public String shortKey;
        public int snapshotVersion;
        public String createDate;
        public String platformVersion;
        public String modifiedTables;
        public String copyDataTables;
    }

    class Sync implements Callable {

        private String table;

        public Sync(String table) {
            this.table = table;
        }

        @Override
        public Object call() throws Exception {
            logger.info("开始同步表结构:" + table);

            try {
                Table columnTable = systemDatabaseManager.findTable("TB_COLUMN");
                if (table != null) {
                    Column tableNameColumn = columnTable.findColumn("TABLE_NAME");

                    String getAllColumnsSQL = "select * from " + columnTable.getName()
                            + " where " + tableNameColumn.getName() + " = :" + tableNameColumn.getName();

                    Map<String, String> paras = new HashMap<>();
                    paras.put(tableNameColumn.getName(), table);

                    List<Map<String, Object>> columns = systemJdbcTemplate.queryForList(getAllColumnsSQL, paras);

                    logger.info("表:" + table + "有" + columns.size() + "个字段.");

                    Table tbTable = new Table();
                    tbTable.setName(table);
                    for (Map<String, Object> column : columns) {
                        Column tbColumn = new Column();
                        tbColumn.setName((String) column.get("NAME"));
                        tbColumn.setDescription((String) column.get("DESCRIPTION"));
                        tbColumn.setPrimaryKey(((Number) column.get("PRIMARY_KEY")).intValue() > 0);
                        tbColumn.setAutoIncrement(((Number) column.get("AUTO_INCREMENT")).intValue() > 0);
                        tbColumn.setRequired(((Number) column.get("REQUIRED")).intValue() > 0);
                        tbColumn.setMappedTypeCode(((Number) column.get("MAPPED_TYPE_CODE")).intValue());
                        tbColumn.setSizeAndScale(((Number) column.get("TOTAL_SIZE")).intValue(), ((Number) column.get("SCALE")).intValue());
                        tbColumn.setDefaultValue((String) column.get("DEFAULT_VALUE"));

                        if(isOracle() && isNumber(tbColumn.getMappedTypeCode())) {
                            tbColumn.setTypeCode(Types.NUMERIC);
                        }

                        tbTable.addColumn(tbColumn);
                    }

                    if (systemDatabaseManager.findTable(table) == null) {
                        logger.info("新增动态表{}.", table);
                        systemDatabaseManager.createTable(tbTable);
                    } else {
                        logger.info("更新动态表{}.", table);
                        Table t = systemDatabaseManager.findTable(table);
                        t.removeAllColumns();
                        t.addColumns(tbTable.getColumnsAsList());
                        systemDatabaseManager.alterTableSafe(t);
                    }
                    logger.info("完成表结构同步{}:", table);
                }

                return true;
            } catch (Exception e) {
                logger.error("[{}]同步失败,{}", table, e.getMessage());
                return false;
            }
        }
    }

    private boolean isOracle(){
        return systemDatabaseManager.getDatabasePlatform().getName().toLowerCase().contains("oracle");
    }

    private boolean isNumber(int mappedTypeCode) {
        return mappedTypeCode == Types.INTEGER || mappedTypeCode == Types.BIGINT;
    }
}
