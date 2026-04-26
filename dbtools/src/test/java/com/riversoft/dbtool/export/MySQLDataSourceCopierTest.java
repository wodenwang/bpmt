package com.riversoft.dbtool.export;

import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;
import com.riversoft.dbtool.util.DatabaseManager;
import com.riversoft.util.Formatter;
import org.apache.commons.io.FileUtils;
import org.h2.jdbcx.JdbcDataSource;
import org.jumpmind.db.model.Column;
import org.jumpmind.db.model.Table;
import org.jumpmind.db.sql.Row;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.List;

/**
 * Created by exizhai on 09/12/2014.
 */
public class MySQLDataSourceCopierTest {

    private static final String SOURCE_H2_FILE = "src/test/resources/db_copy/river-from-woden";
    private static final String SOURCE_H2_FILE_NEW = "src/test/resources/db_copy/river-from-woden-new";


    private static DataSource h2DataSource;
    private static DataSource mySQLDataSource;
    private static DataSource h2NewDataSource;

    private static DatabaseManager h2DatabaseManager;
    private static DatabaseManager mySQLDatabaseManager;
    private static DatabaseManager h2NewDatabaseManager;

    @BeforeClass
    public static void beforeClass() throws Exception {
        Path sourceDBBackupPath = Paths.get(SOURCE_H2_FILE + ".h2.db.bak");
        Path sourceDBPath = Paths.get(SOURCE_H2_FILE + ".h2.db");

        File sourceDBBackup = sourceDBBackupPath.toFile();
        File sourceDB = sourceDBPath.toFile();

        FileUtils.copyFile(sourceDBBackup, sourceDB);

        JdbcDataSource ds1 = new JdbcDataSource();
        ds1.setURL("jdbc:h2:" + SOURCE_H2_FILE);
        ds1.setUser("sa");
        ds1.setPassword("");
        h2DataSource = ds1;
        h2DatabaseManager = new DatabaseManager(h2DataSource);

        MysqlDataSource ds2 = new MysqlDataSource();
        ds2.setURL("jdbc:mysql://localhost:3306/riversoft");
        ds2.setUser("riversoft");
        ds2.setPassword("123456");
        mySQLDataSource = ds2;
        mySQLDatabaseManager = new DatabaseManager(mySQLDataSource);

        JdbcDataSource ds3 = new JdbcDataSource();
        ds3.setURL("jdbc:h2:" + SOURCE_H2_FILE_NEW + Formatter.formatDatetime(new Date(), "yyyyMMddHHmmss") + ";DATABASE_TO_UPPER=false");
        ds3.setUser("sa");
        ds3.setPassword("");
        h2NewDataSource = ds3;
        h2NewDatabaseManager = new DatabaseManager(h2NewDataSource);
    }

    @Ignore
    public void testCopyAllFromH2ToMySQLThenToH2() {
        DataSourceCopier copier = new DataSourceCopier(h2DataSource, mySQLDataSource);

        try {
            copier.copyAllWithoutDDL(true, false, true);
        } catch (Exception e) {
            Assert.fail(e.getMessage());
        }

        databaseContainSameData(h2DatabaseManager, mySQLDatabaseManager);

        copier = new DataSourceCopier(mySQLDataSource, h2NewDataSource);

        try {
            copier.copyAllWithDDL(true);
        } catch (Exception e) {
            Assert.fail(e.getMessage());
        }

        databaseContainSameData(mySQLDatabaseManager, h2NewDatabaseManager);
    }

    private void databaseContainSameData(Table table, DatabaseManager dm1, DatabaseManager dm2) {
        JdbcTemplate h2JdbcTemplate = new JdbcTemplate(dm1.getDataSource());
        int oldCount = h2JdbcTemplate.queryForObject(buildSelectCountSQL(dm1.findTable(table.getName())), Integer.class);

        JdbcTemplate mySQLJdbcTemplate = new JdbcTemplate(dm2.getDataSource());
        int newCount = mySQLJdbcTemplate.queryForObject(buildSelectCountSQL(dm2.findTable(table.getName())), Integer.class);

        Assert.assertEquals(oldCount, newCount);

        List<Row> oldRows = dm1.getJdbcSqlTemplate().query(buildSelectOrderedAllSQL(table).toUpperCase());
        List<Row> newRows = dm2.getJdbcSqlTemplate().query(buildSelectOrderedAllSQL(table).toUpperCase());

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


    private void databaseContainSameData(DatabaseManager dm1, DatabaseManager dm2) {
        Table[] tables1 = dm1.readDatabase().getTables();
        Table[] tables2 = dm2.readDatabase().getTables();

        System.out.println("Tables in DM1:");
        for (Table t: tables1) {
            System.out.println(t.getName());
        }

        System.out.println("Tables in DM2:");
        for (Table t: tables2) {
            System.out.println(t.getName());
        }

        for (Table t: tables1) {
            //databaseContainSameData(t, dm1, dm2);
        }
    }
}

