package com.riversoft.dbtool.export;

import com.riversoft.dbtool.util.DatabaseManager;
import com.riversoft.util.Formatter;
import org.apache.commons.io.FileUtils;
import org.h2.jdbcx.JdbcDataSource;
import org.jumpmind.db.model.Column;
import org.jumpmind.db.model.Database;
import org.jumpmind.db.model.Table;
import org.jumpmind.db.sql.Row;
import org.junit.*;

import javax.sql.DataSource;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by exizhai on 05/12/2014.
 */
public class DataSourceCopierTest {

    private static final String SOURCE_H2_FILE = "src/test/resources/db_copy/river";
    private static final String DEST_H2_FILE = "src/test/resources/db_copy/river-new";
    private static final String EMPTY_H2_FILE = "src/test/resources/db_copy/river-new-" + Formatter.formatDatetime(new Date(), "yyyyMMddHHmmss");

    private static DataSource sourceDataSource;
    private static DataSource destDataSource;
    private static DatabaseManager sourceDatabaseManager;
    private static DatabaseManager destDatabaseManager;
    private static DataSource emptyDataSource;
    private static DatabaseManager emptyDatabaseManager;

    @BeforeClass
    public static void beforeClass() throws Exception {
        Path sourceDBBackupPath = Paths.get(SOURCE_H2_FILE + ".h2.db.bak");
        Path destDBBackupPath = Paths.get(DEST_H2_FILE + ".h2.db.bak");
        Path sourceDBPath = Paths.get(SOURCE_H2_FILE + ".h2.db");
        Path destDBPath = Paths.get(DEST_H2_FILE + ".h2.db");

        File sourceDBBackup = sourceDBBackupPath.toFile();
        File sourceDB = sourceDBPath.toFile();
        File destDBBackup = destDBBackupPath.toFile();
        File destDB = destDBPath.toFile();

        FileUtils.copyFile(sourceDBBackup, sourceDB);
        FileUtils.copyFile(destDBBackup, destDB);

        JdbcDataSource ds1 = new JdbcDataSource();
        ds1.setURL("jdbc:h2:" + SOURCE_H2_FILE);
        ds1.setUser("sa");
        ds1.setPassword("");

        sourceDataSource = ds1;
        sourceDatabaseManager = new DatabaseManager(sourceDataSource);

        JdbcDataSource ds2 = new JdbcDataSource();
        ds2.setURL("jdbc:h2:" + DEST_H2_FILE);
        ds2.setUser("sa");
        ds2.setPassword("");

        destDataSource = ds2;
        destDatabaseManager = new DatabaseManager(ds2);

        JdbcDataSource ds3 = new JdbcDataSource();
        ds3.setURL("jdbc:h2:" + EMPTY_H2_FILE);
        ds3.setUser("sa");
        ds3.setPassword("");

        emptyDataSource = ds3;
        emptyDatabaseManager = new DatabaseManager(ds3);

    }

    @AfterClass
    public static void afterClass() throws IOException {
        Path emptyDBPath = Paths.get(EMPTY_H2_FILE + ".h2.db");
        FileUtils.forceDelete(emptyDBPath.toFile());
    }

    @Ignore
    public void testCopyAllExceptACT() {
        Database oldDatabase = sourceDatabaseManager.readDatabase();
        Table[] oldTables = oldDatabase.getTables();
        Set<String> copyTableNames = new HashSet<>();
        Set<Table> copyTables = new HashSet<>();
        for (Table t: oldTables) {
            if(!t.getName().startsWith("ACT_")) {
                copyTableNames.add(t.getName());
                copyTables.add(t);
            }
        }

        DataSourceCopier copier = new DataSourceCopier(sourceDataSource, destDataSource);

        try {
            copier.copyWithoutDDL(copyTableNames, false, true, true);
        } catch (Exception e) {
            Assert.fail(e.getMessage());
        }

        databaseContainSameData(copyTables);
    }


    @Ignore
    public void testCopyAllWithDDL() {
        Database oldDatabase = sourceDatabaseManager.readDatabase();
        Table[] oldTables = oldDatabase.getTables();
        Set<String> copyTableNames = new HashSet<>();
        Set<Table> copyTables = new HashSet<>();
        for (Table t: oldTables) {
            if(!t.getName().startsWith("ACT_")) {
                copyTableNames.add(t.getName());
                copyTables.add(t);
            }
        }

        DataSourceCopier copier = new DataSourceCopier(sourceDataSource, emptyDataSource);

        try {
            copier.copyWithDDL(copyTableNames, true);
        } catch (Exception e) {
            Assert.fail(e.getMessage());
        }

        databaseContainSameData(copyTables);
    }


    private void databaseContainSameData(Table table) {
        int oldCount = sourceDatabaseManager.getJdbcSqlTemplate().queryForInt(buildSelectCountSQL(table));
        System.out.println("old: " + buildSelectCountSQL(table) + " -> " + oldCount);
        int newCount = destDatabaseManager.getJdbcSqlTemplate().queryForInt(buildSelectCountSQL(table));
        System.out.println("new: " + buildSelectCountSQL(table) + " -> " + newCount);
        Assert.assertEquals(oldCount, newCount);

        List<Row> oldRows = sourceDatabaseManager.getJdbcSqlTemplate().query(buildSelectOrderedAllSQL(table));
        List<Row> newRows = sourceDatabaseManager.getJdbcSqlTemplate().query(buildSelectOrderedAllSQL(table));

        for (int i = 0; i < oldRows.size(); i++) {
            Row oldRow = oldRows.get(i);
            Row newRow = newRows.get(i);

            Assert.assertEquals(oldRow.size(), newRow.size());

            for (String key: oldRow.keySet()) {
                Object oldValue = oldRow.get(key);
                Object newValue = newRow.get(key);

                if(! (oldValue instanceof byte[])) {
                    Assert.assertEquals(oldValue, newValue);
                } else {
                    System.out.println(table.getName() + "." + key + " is byte[]");
                }
            }
        }
    }

    private String buildSelectCountSQL(Table table) {
        return "select count(1) from " + table.getName();
    }

    private String buildSelectOrderedAllSQL(Table table) {
        Column[] primaryKeys = table.getPrimaryKeyColumns();
        return "select * from " + table.getName() + " order by " + primaryKeys[0].getName();
    }


    private void databaseContainSameData(Set<Table> tables) {
        for(Table table: tables) {
            databaseContainSameData(table);
        }
    }

}
