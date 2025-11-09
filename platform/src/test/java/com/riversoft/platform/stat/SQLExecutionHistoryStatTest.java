package com.riversoft.platform.stat;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import java.io.File;
import java.net.URL;
import java.util.Map;

/**
 * Created by exizhai on 3/21/2014.
 */
public class SQLExecutionHistoryStatTest {

    @Test
    public void testAnalyse(){
        Assert.assertFalse(SQLExecutionHistoryStat.getInstance().isRunning());

        URL debugURL = ClassLoader.getSystemClassLoader().getResource("debug.properties");
        File debugFile = new File(debugURL.getFile());
        File root = debugFile.getParentFile();

        File sqlFolder = new File(root, "stat");
        try {
            Map<SingleSelectSQLStatement, Integer> result = SQLExecutionHistoryStat.getInstance().analyse(sqlFolder);
            for (SingleSelectSQLStatement s: result.keySet()) {
                System.out.println(s.getTable() + "@" + s.getConditions() + "  ->  " + result.get(s));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Test
    public void testIsSingleSQL() {
        Assert.assertTrue(SQLExecutionHistoryStat.getInstance().isSingleSelectSQL("select * from a"));
        Assert.assertTrue(SQLExecutionHistoryStat.getInstance().isSingleSelectSQL("select * from a aa"));
        Assert.assertTrue(SQLExecutionHistoryStat.getInstance().isSingleSelectSQL("select * from a where a = 1"));
        Assert.assertTrue(SQLExecutionHistoryStat.getInstance().isSingleSelectSQL("select * from a aa where aa.a = 1"));
        Assert.assertFalse(SQLExecutionHistoryStat.getInstance().isSingleSelectSQL("select * from a aa, b bb where aa.a = 1"));
        Assert.assertFalse(SQLExecutionHistoryStat.getInstance().isSingleSelectSQL("select aa.a, aa.b aa.c from a aa, b bb where aa.a = 1"));

    }
}
