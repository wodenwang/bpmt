package com.riversoft.dbtool.export;

import com.riversoft.dbtool.util.DatabaseManager;
import com.riversoft.util.jackson.JsonMapper;
import org.h2.value.CaseInsensitiveMap;
import org.jumpmind.db.model.Column;
import org.jumpmind.db.model.Table;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Created by exizhai on 7/24/2015.
 */
public class DataHandler {

    private Logger logger = LoggerFactory.getLogger("DataHandler");
    private DataSource sourceDataSource;
    private DataSource destDataSource;
    private NamedParameterJdbcTemplate destNamedParameterJdbcTemplate;
    private JdbcTemplate sourceJdbcTemplate;
    private JdbcTemplate destJdbcTemplate;
    private DatabaseManager destDatabaseManager;

    private List<Table> tables;
    private boolean clearBeforeCopy;
    private boolean replaceIfConflict;
    private int threads = 10;

    private ExecutorService executorService = null;

    public DataHandler(DataSource sourceDataSource, DataSource destDataSource, List<Table> tables, boolean clearBeforeCopy, boolean replaceIfConflict, int threads) {
        this.sourceDataSource = sourceDataSource;
        this.destDataSource = destDataSource;
        this.destNamedParameterJdbcTemplate = new NamedParameterJdbcTemplate(this.destDataSource);
        this.sourceJdbcTemplate = new JdbcTemplate(this.sourceDataSource);
        this.destJdbcTemplate = new JdbcTemplate(this.destDataSource);
        this.destDatabaseManager = new DatabaseManager(this.destDataSource);
        this.tables = tables;
        this.clearBeforeCopy = clearBeforeCopy;
        this.replaceIfConflict = replaceIfConflict;
        this.threads = threads;
    }

    public boolean copy() {
        executorService = Executors.newFixedThreadPool(threads);

        List<Future<Boolean>> futures = new ArrayList<>();
        for (Table table : tables) {
            futures.add(executorService.submit(new Copy(table, destDatabaseManager.findTable(table.getName()))));
        }

        boolean result = true;
        for (Future<Boolean> future : futures) {
            try {
                if (!future.get()) {
                    result = false;
                }
            } catch (Exception e) {
                result = false;
                return result;
            }
        }

        executorService.shutdown();

        return result;
    }

    class Copy implements Callable {

        private Table sourceTable;
        private Table destTable;
		String insertSql = null;
        String countSql = null;
        String updateSql = null;
			
        Copy(Table sourceTable, Table destTable) {
            this.sourceTable = sourceTable;
            this.destTable = destTable;
        }

        @Override
        public Object call() throws Exception {
            DefaultTransactionDefinition tf = null;
            PlatformTransactionManager tm = null;
            TransactionStatus ts = null;

            try {
                long start = System.currentTimeMillis();

                tf = new DefaultTransactionDefinition();
                tm = new DataSourceTransactionManager(destDataSource);
                ts = tm.getTransaction(tf);

                List<Map<String, Object>> sourceRecords = sourceJdbcTemplate.queryForList("select * from " + sourceTable.getName());

                if (sourceRecords == null || sourceRecords.size() == 0) {
                    tm.commit(ts);
                    logger.info("[{}]没有数据忽略.", sourceTable.getName());
                } else {
                    logger.info("[{}]]有[{}]条数据.", sourceTable.getName(), sourceRecords.size());

                    if(clearBeforeCopy) {
                        destJdbcTemplate.update("delete from " + destTable.getName());
                        logger.info("[{}]清除数据成功", destTable.getName());
                    }

                    this.insertSql = buildInsertSql();
                    this.countSql = buildCountSql();
                    this.updateSql = buildUpdateSql();

                    for (Map<String, Object> row : sourceRecords) {
                        createOrUpdate(row);
                    }
                    tm.commit(ts);
                    logger.info("[{}]完成数据拷贝:{}ms.", sourceTable.getName(), System.currentTimeMillis() - start );
                }

                return true;
            } catch (Exception e) {
                logger.error("[{}]数据拷贝出错:{}", e.getMessage());
                tm.rollback(ts);
                return false;
            }
        }

        private void createOrUpdate(Map<String, Object> row) {
            Map paras = transFromSourceToDest(row);

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

        private Map transFromSourceToDest(Map<String, Object> row) {
            CaseInsensitiveMap caseInsensitiveMap = new CaseInsensitiveMap();
            caseInsensitiveMap.putAll(row);

            Map map = new HashMap();

            for (Column column : destTable.getColumns()) {
                if (caseInsensitiveMap.containsKey(column.getName())) {
                    map.put(column.getName(), caseInsensitiveMap.get(column.getName()));
                }
            }

            return map;
        }

        private String buildUpdateSql() {
            Column[] columns = destTable.getNonPrimaryKeyColumns();
            Column[] keys = destTable.getPrimaryKeyColumns();
            StringBuilder sb = new StringBuilder("update ");
            sb.append(destTable.getName());
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

        private String buildCountSql() {
            StringBuilder sb = new StringBuilder("select count(1) from ");
            sb.append(destTable.getName());
            sb.append(" where ");

            String[] keys = destTable.getPrimaryKeyColumnNames();
            for (int i = 0; i < keys.length; i++) {
                if (i == keys.length - 1) {
                    sb.append(keys[i]).append(" =:").append(keys[i]);
                } else {
                    sb.append(keys[i]).append(" =:").append(keys[i]).append(" and ");
                }
            }

            return sb.toString();
        }

        private String buildInsertSql() {
            Column[] columns = destTable.getColumns();
            StringBuilder sb = new StringBuilder("insert into ");
            sb.append(destTable.getName());
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
    }

}
