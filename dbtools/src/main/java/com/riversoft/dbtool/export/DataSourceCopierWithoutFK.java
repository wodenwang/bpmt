package com.riversoft.dbtool.export;

import com.riversoft.dbtool.util.DatabaseManager;
import org.jumpmind.db.model.Database;
import org.jumpmind.db.model.Table;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


/**
 * Created by exizhai on 7/24/2015.
 */
public class DataSourceCopierWithoutFK {

    private Logger logger = LoggerFactory.getLogger("DataSourceCopierWithoutFK");
    private DataSource sourceDataSource;
    private DataSource destDataSource;
    private DatabaseManager sourceDatabaseManager;
    private DatabaseManager destDatabaseManager;
    private Database sourceDatabase;
    private int threads = 10;

    public DataSourceCopierWithoutFK(DataSource sourceDataSource, DataSource destDataSource, int threads) {
        this.sourceDataSource = sourceDataSource;
        this.destDataSource = destDataSource;
        this.sourceDatabaseManager = new DatabaseManager(this.sourceDataSource);
        this.destDatabaseManager = new DatabaseManager(this.destDataSource);
        this.sourceDatabase = sourceDatabaseManager.readDatabase();
        this.threads = threads;
    }

    public boolean copyAll(Set<String> ignoreTables, boolean ddl, boolean clearBeforeCopy, boolean replaceIfConflict) throws Exception {
        return copyAll(ignoreTables, ddl, clearBeforeCopy, replaceIfConflict, new NullDBOperationSignal());
    }

    public boolean copyAll(Set<String> ignoreTables, boolean ddl, boolean clearBeforeCopy, boolean replaceIfConflict, DBOperationSignal dbOperationSignal) {
        Table[] tables = sourceDatabase.getTables();
        Set<String> tableNames = new HashSet<>();
        for (Table t : tables) {
            tableNames.add(t.getName());
        }

        return copy(tableNames, ignoreTables, ddl, clearBeforeCopy, replaceIfConflict, dbOperationSignal);
    }

    public boolean copy(Set<String> tableNames, Set<String> ignoreTables, boolean ddl, boolean clearBeforeCopy, boolean replaceIfConflict) {
        return copy(tableNames, ignoreTables, ddl, clearBeforeCopy, replaceIfConflict, new NullDBOperationSignal());
    }

    public boolean copy(Set<String> tableNames, Set<String> ignoreTables, boolean ddl, boolean clearBeforeCopy, boolean replaceIfConflict, DBOperationSignal dbOperationSignal)  {
        if(ignoreTables != null) {
            tableNames.removeAll(ignoreTables);
        }

        List<Table> tables;
        try {
            tables = fetchTables(tableNames);
        } catch (Exception e){
            logger.error("不能开始复制", e);
            return false;
        }

        dbOperationSignal.begin();
        DdlHandler ddlHandler = new DdlHandler(destDatabaseManager, tables, threads);

        boolean success = true;
        if(ddl) {
            success = ddlHandler.sync();
        }

        if(success) {
            DataHandler dataHandler = new DataHandler(sourceDataSource, destDataSource, tables, clearBeforeCopy, replaceIfConflict, threads);
            success = dataHandler.copy();
        }
        return success;
    }

    private List<Table> fetchTables(Set<String> tableNames) throws Exception {
        List<Table> tables = new ArrayList<>();

        int i = 0;
        for (String name : tableNames) {
            Table table = sourceDatabase.findTable(name);
            if (table == null) {
                throw new Exception("表[" + name + "]不存在.");
            } else {
                tables.add(table);
            }
        }

        return tables;
    }

}
