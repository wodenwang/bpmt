package com.riversoft.dbtool.export;

import com.riversoft.dbtool.util.DatabaseManager;
import org.jumpmind.db.model.Database;
import org.jumpmind.db.model.Table;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by exizhai on 7/24/2015.
 */
public class DdlHandler {

    private Logger logger = LoggerFactory.getLogger("DdlHandler");

    private DatabaseManager databaseManager;
    private Database database;
    private List<Table> tables;

    private AtomicInteger SUCCESS = new AtomicInteger(0);
    private AtomicInteger IGNORED = new AtomicInteger(0);
    private AtomicInteger FAILED = new AtomicInteger(0);
    private List<String> SUCCESS_LIST = Collections.synchronizedList(new ArrayList());
    private int threads = 10;

    private ExecutorService executorService = null;

    public DdlHandler(DatabaseManager databaseManager, List<Table> tables, int threads) {
        this.databaseManager = databaseManager;
        this.database = this.databaseManager.readDatabase();
        this.tables = tables;
        this.threads = threads;
    }

    public boolean sync() {
        logger.info("开始表结构同步");
        executorService = Executors.newFixedThreadPool(threads);
        List<Future<Boolean>> results = new ArrayList();

        for (Table t : tables) {
            results.add(executorService.submit(new Create(t)));
        }

        boolean result = true;
        for (Future<Boolean> future : results) {
            try {
                if (!future.get()) {
                    result = false;
                }
            } catch (Exception e) {
                result = false;
            }
        }

        executorService.shutdown();
        logger.info("成功同步:{}张表,忽略:{}张表,失败:{}张表.", SUCCESS.get(), IGNORED.get(), FAILED.get());
        return result;
    }

    class Create implements Callable {

        private Table table;

        public Create(Table table) {
            this.table = table;
        }

        @Override
        public Object call() throws Exception {
            try {
                if (database.findTable(table.getName()) == null) {
                    logger.info("创建表:" + table.getName());
                    table.setCatalog(database.getCatalog());
                    table.setSchema(database.getSchema());
                    table.removeAllForeignKeys();
                    table.removeAllIndices();
                    databaseManager.createTables(false, table);
                    SUCCESS.incrementAndGet();

                    SUCCESS_LIST.add(table.getName());
                } else {
                    logger.warn("表{}已经存在,不做修改.", table.getName());
                    IGNORED.incrementAndGet();
                }

                return true;
            } catch (Exception e) {
                logger.warn("创建表{}失败:{},跳过.", table.getName(), e.getMessage());
                FAILED.incrementAndGet();
                return false;
            }
        }
    }

}
