package com.riversoft.dbtool.util;

import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;
import org.jumpmind.db.model.Table;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import javax.sql.DataSource;
import java.util.Arrays;
import java.util.List;

/**
 * Created by exizhai on 11/12/2014.
 */
public class TablesSorterTest {

    private static DataSource dataSource;
    private static DatabaseManager databaseManager;

    @BeforeClass
    public static void beforeClass() throws Exception {
        MysqlDataSource ds = new MysqlDataSource();
        ds.setURL("jdbc:mysql://localhost:3306/riversoft");
        ds.setUser("riversoft");
        ds.setPassword("123456");

        dataSource = ds;
        databaseManager = new DatabaseManager(dataSource);

    }

    @Ignore
    public void testSort(){
        List<Table> tables = Arrays.asList(databaseManager.readDatabase().getTables());
        List<Table> sortedTables = TablesSorter.sort(tables);

        for (Table t: sortedTables) {
            System.out.println(t.getName());
        }

    }


}
