package com.riversoft.dbtool.export;

import com.riversoft.dbtool.util.DataSourceInstance;
import org.h2.jdbcx.JdbcDataSource;

import org.junit.Assert;
import org.junit.BeforeClass;

import org.junit.Ignore;
import org.junit.Test;

import javax.sql.DataSource;

/**
 * Created by exizhai on 09/12/2014.
 */
public class H2ToMySQLTest {

    private static final String SOURCE_H2_FILE = "c:\\tmp\\config";

    private static DataSource h2DataSource;
    private static DataSource mySQLDataSource;

    @BeforeClass
    public static void beforeClass() throws Exception {
        JdbcDataSource ds1 = new JdbcDataSource();
        ds1.setURL("jdbc:h2:" + SOURCE_H2_FILE);
        ds1.setUser("sa");
        ds1.setPassword("");
        h2DataSource = ds1;

        mySQLDataSource = DataSourceInstance.getInstance("com.mysql.jdbc.Driver", "jdbc:mysql://localhost:3306/prod", "bxtest", "bxtest").getDataSource();
    }

    @Ignore
    public void testCopyAllFromH2ToMySQL() {
        DataSourceCopierWithoutFK copier = new DataSourceCopierWithoutFK(h2DataSource, mySQLDataSource, 30);

        try {
            copier.copyAll(null, false, true, true);
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }
    }

}

