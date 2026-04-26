package com.riversoft.dbtool.export;

import com.riversoft.dbtool.util.DatabaseManager;
import com.riversoft.dbtool.util.TablesSorter;
import com.riversoft.util.jackson.JsonMapper;
import org.h2.value.CaseInsensitiveMap;
import org.jumpmind.db.model.Column;
import org.jumpmind.db.model.Database;
import org.jumpmind.db.model.Table;
import org.jumpmind.db.sql.ISqlTemplate;
import org.jumpmind.db.sql.Row;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import javax.sql.DataSource;
import java.util.*;

/**
 * Created by exizhai on 04/12/2014.
 */
public class DataSourceCopier {

    private static List<Table> sourceSortedTableList = new ArrayList<>();
    private static List<Table> destSortedTableList = new ArrayList<>();
    private Logger logger = LoggerFactory.getLogger("DataSourceCopier");
    private DataSource sourceDataSource;
    private DataSource destDataSource;
    private NamedParameterJdbcTemplate destNamedParameterJdbcTemplate;
    private JdbcTemplate destJdbcTemplate;
    private ISqlTemplate sourceSqlTemplate;
    private DatabaseManager sourceDatabaseManager;
    private DatabaseManager destDatabaseManager;
    private Database sourceDatabase;
    private Database destDatabase;

    public DataSourceCopier(DataSource sourceDataSource, DataSource destDataSource) {
        this.sourceDataSource = sourceDataSource;
        this.destDataSource = destDataSource;
        this.destNamedParameterJdbcTemplate = new NamedParameterJdbcTemplate(this.destDataSource);
        this.destJdbcTemplate = new JdbcTemplate(this.destDataSource);
        this.sourceDatabaseManager = new DatabaseManager(this.sourceDataSource);
        this.destDatabaseManager = new DatabaseManager(this.destDataSource);
        this.sourceSqlTemplate = sourceDatabaseManager.getJdbcSqlTemplate();
        this.sourceDatabase = sourceDatabaseManager.readDatabase();
        this.destDatabase = destDatabaseManager.readDatabase();
    }

    private void createDestTablesIfNotExists(boolean exitIfError) {
        logger.info("将会在目标数据库创建或者修改表结构.");
        List<Table> reversedTableList = new ArrayList<>();
        reversedTableList.addAll(sourceSortedTableList);

        Collections.reverse(reversedTableList);
        int success = 0;
        int failed = 0;
        int ignore = 0;
        for (Table t : reversedTableList) {
            try {
                if (destDatabase.findTable(t.getName()) == null) {
                    logger.info("创建表:" + t.getName());
                    t.setCatalog(destDatabase.getCatalog());
                    t.setSchema(destDatabase.getSchema());
                    t.removeAllForeignKeys();
                    t.removeAllIndices();
                    destDatabaseManager.createTables(false, t);
                    success++;
                } else {
                    logger.warn("表{}已经存在,不做修改.", t.getName());
                    ignore++;
                }
            } catch (Exception e) {
                if (!exitIfError) {
                    logger.warn("创建表{}失败:{},跳过.", t.getName(), e.getMessage());
                    failed++;
                } else {
                    throw e;
                }
            }
        }
        logger.info("成功创建:{}张表,跳过:{}张表,失败:{}张表.", success, ignore, failed);
        // reload
        destDatabase = destDatabaseManager.readDatabase();
    }

    public void copyAllWithDDL(boolean exitIfError) throws Exception {
        copyAll(true, exitIfError, false, false, new NullDBOperationSignal());
    }

    public void copyAllWithoutDDL(boolean exitIfError, boolean clearBeforeCopy, boolean replaceIfConflict,
                                  DBOperationSignal dbOperationSignal) throws Exception {
        copyAll(false, exitIfError, clearBeforeCopy, replaceIfConflict, dbOperationSignal);
    }

    public void copyAllWithoutDDL(boolean exitIfError, boolean clearBeforeCopy, boolean replaceIfConflict)
            throws Exception {
        copyAll(false, exitIfError, clearBeforeCopy, replaceIfConflict, new NullDBOperationSignal());
    }

    private void copyAll(boolean isDDL, boolean exitIfError, boolean clearBeforeCopy, boolean replaceIfConflict,
                         DBOperationSignal dbOperationSignal) throws Exception {
        Table[] tables = sourceDatabase.getTables();
        Set<String> tableNames = new HashSet<>();
        for (Table t : tables) {
            tableNames.add(t.getName());
        }
        copy(tableNames, isDDL, exitIfError, clearBeforeCopy, replaceIfConflict, dbOperationSignal);
    }

    public void copyWithDDL(Set<String> tableNames, DBOperationSignal dbOperationSignal) throws Exception {
        copy(tableNames, true, true, false, false, dbOperationSignal);
    }

    public void copyWithDDL(Set<String> tableNames, boolean exitIfError) throws Exception {
        copy(tableNames, true, exitIfError, false, false, new NullDBOperationSignal());
    }

    public void copyWithoutDDL(Set<String> tableNames, boolean exitIfError, boolean clearBeforeCopy,
                               boolean replaceIfConflict) throws Exception {
        copy(tableNames, false, exitIfError, clearBeforeCopy, replaceIfConflict, new NullDBOperationSignal());
    }

    private void copy(Set<String> tableNames, boolean isDDL, boolean exitIfError, boolean clearBeforeCopy,
                      boolean replaceIfConflict, DBOperationSignal dbOperationSignal) throws Exception {

        sortSourceTableList(tableNames, exitIfError);

        if (isDDL) {
            logger.info("开始在目标数据库中创建不存在的库表:");
            createDestTablesIfNotExists(exitIfError);
        }

        sortDestTableList(tableNames, exitIfError);

        dbOperationSignal.begin();
        logger.info("开始数据拷贝,有{}张表可以拷贝.", destSortedTableList.size());

        int success = 0;
        int failed = 0;

        try {
            DefaultTransactionDefinition tf = new DefaultTransactionDefinition();
            PlatformTransactionManager tm = new DataSourceTransactionManager(destDataSource);
            TransactionStatus ts = tm.getTransaction(tf);

            try {
                if ((!isDDL) && clearBeforeCopy) {
                    for (Table t : destSortedTableList) {
                        clear(t);
                    }
                }

                List<Table> reversedTableList = new ArrayList<>();
                reversedTableList.addAll(destSortedTableList);

                Collections.reverse(reversedTableList);

                for (Table t : reversedTableList) {
                    dbOperationSignal.signal(t.getName(), "正在拷贝");
                    doCopy(t, replaceIfConflict);
                    success++;
                }
                tm.commit(ts);
            } catch (Exception e) {
                if (exitIfError) {
                    logger.error("拷贝数据失败,将会回滚所有数据拷贝操作.", e);
                    tm.rollback(ts);
                    throw new Exception("拷贝数据失败,将会回滚所有数据拷贝操作.", e);
                } else {
                    logger.warn("拷贝数据失败," + e.getMessage());
                    logger.warn("将会忽略改错误继续拷贝.");
                    failed++;
                }
            }

            logger.info("完成所有拷贝,成功:{}, 失败:{}", success, failed);
        } catch (Exception e) {
            logger.error("不能执行拷贝.", e);
            throw new Exception("不能执行拷贝.", e);
        } finally {
            dbOperationSignal.end();
        }
    }

    private void sortSourceTableList(Set<String> tableNames, boolean exitIfError) throws Exception {
        List<Table> tables = fetchTables(tableNames, exitIfError, sourceDatabase);

        sourceSortedTableList = TablesSorter.sort(tables);
    }

    private void sortDestTableList(Set<String> tableNames, boolean exitIfError) throws Exception {
        List<Table> tables = fetchTables(tableNames, exitIfError, destDatabase);

        destSortedTableList = TablesSorter.sort(tables);
    }

    private void clear(Table t) {
        logger.info("准备清除" + t.getName() + "数据.");
        destJdbcTemplate.update("delete from " + t.getName());
    }

    private void doCopy(Table sourceTable, boolean replaceIfConflict) {
        List<Row> sourceRecords = sourceSqlTemplate.query("select * from " + sourceTable.getName());
        Table destTable = destDatabaseManager.findTable(sourceTable.getName());
        if (sourceRecords == null || sourceRecords.size() == 0) {
            logger.info("表[{}]没有数据,忽略.", sourceTable.getName());
        } else {
            logger.info("表[{}]有[{}]条数据.", sourceTable.getName(), sourceRecords.size());
            for (Row row : sourceRecords) {
                createOrUpdate(destTable, row, replaceIfConflict);
            }
            logger.info("完成[{}]中数据拷贝.", sourceTable.getName());
        }
    }

    private void createOrUpdate(Table destTable, Row row, boolean replaceIfConflict) {
        String insertSql = buildInsertSql(destTable);
        String countSql = buildCountSql(destTable);
        String updateSql = buildUpdateSql(destTable);

        Map paras = transFromSourceToDest(destTable, row);

        try {
            if (destNamedParameterJdbcTemplate.queryForObject(countSql, paras, Integer.class) == 0) {
                destNamedParameterJdbcTemplate.update(insertSql, paras);
            } else {
                if (replaceIfConflict) {
                    destNamedParameterJdbcTemplate.update(updateSql, paras);
                } else {
                    logger.warn(destTable.getName() + ":该主键标示的数据已经存在,将跳过该数据:" + JsonMapper.defaultMapper().toJson(paras));
                }
            }
        } catch (DataAccessException e) {
            if (e instanceof DuplicateKeyException) {
                logger.warn(destTable.getName(), ":主键冲突:" + e.getLocalizedMessage());
            } else {
                logger.error(destTable.getName(), "数据操作异常:", e);
            }
            throw e;
        }
    }

    private Map transFromSourceToDest(Table table, Row row) {
        CaseInsensitiveMap caseInsensitiveMap = new CaseInsensitiveMap();
        caseInsensitiveMap.putAll(row);

        Map map = new HashMap();

        for (Column column : table.getColumns()) {
            if (caseInsensitiveMap.containsKey(column.getName())) {
                map.put(column.getName(), caseInsensitiveMap.get(column.getName()));
            }
        }

        return map;
    }

    private String buildUpdateSql(Table table) {
        Column[] columns = table.getNonPrimaryKeyColumns();
        Column[] keys = table.getPrimaryKeyColumns();
        StringBuilder sb = new StringBuilder("update ");
        sb.append(table.getName());
        sb.append(" set ");
        for (int i = 0; i < columns.length; i++) {
            if (i == columns.length - 1) {
                sb.append(columns[i].getName()).append(" =:").append(columns[i].getName());
            } else {
                sb.append(columns[i].getName()).append(" =:").append(columns[i].getName()).append(", ");
            }
        }
        sb.append(" where ");
        for (int i = 0; i < keys.length; i++) {
            if (i == keys.length - 1) {
                sb.append(keys[i].getName()).append(" =:").append(keys[i].getName());
            } else {
                sb.append(keys[i].getName()).append(" =:").append(keys[i].getName()).append(" and ");
            }
        }

        return sb.toString();
    }

    private String buildCountSql(Table table) {
        StringBuilder sb = new StringBuilder("select count(1) from ");
        sb.append(table.getName());
        sb.append(" where ");

        String[] keys = table.getPrimaryKeyColumnNames();
        for (int i = 0; i < keys.length; i++) {
            if (i == keys.length - 1) {
                sb.append(keys[i]).append(" =:").append(keys[i]);
            } else {
                sb.append(keys[i]).append(" =:").append(keys[i]).append(" and ");
            }
        }

        return sb.toString();
    }

    private String buildInsertSql(Table table) {
        Column[] columns = table.getColumns();
        StringBuilder sb = new StringBuilder("insert into ");
        sb.append(table.getName());
        sb.append("(");
        for (int i = 0; i < columns.length; i++) {
            if (i == columns.length - 1) {
                sb.append(columns[i].getName()).append(")");
            } else {
                sb.append(columns[i].getName()).append(", ");
            }
        }
        sb.append("values(");
        for (int i = 0; i < columns.length; i++) {
            if (i == columns.length - 1) {
                sb.append(":").append(columns[i].getName()).append(")");
            } else {
                sb.append(":").append(columns[i].getName()).append(", ");
            }
        }

        return sb.toString();
    }

    private Map<String, ?> toUpperCase(Row r) {
        Map<String, Object> record = new HashMap<>();

        for (String key : r.keySet()) {
            record.put(key.toUpperCase(), r.get(key));
        }

        return record;
    }

    private List<Table> fetchTables(Set<String> tableNames, boolean exitIfError, Database database) throws Exception {
        List<Table> tables = new ArrayList<>();

        int i = 0;
        for (String name : tableNames) {
            Table table = database.findTable(name);
            if (table == null) {
                if (exitIfError) {
                    throw new Exception("表[" + name + "]不存在.");
                } else {
                    logger.warn("表{}不存在,将会跳过.", name);
                }
            } else {
                tables.add(table);
            }
        }

        return tables;
    }

}
