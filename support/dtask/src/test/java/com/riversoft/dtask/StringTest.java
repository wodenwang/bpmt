/*
 * $HeadURL: $
 * $Id: $
 * Copyright (c) 2013 by Riversoft System, all rights reserved.
 */
package com.riversoft.dtask;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author Borball
 * 
 */
public class StringTest {

    public String getPatchId(String updateItem) throws Exception {
        if (updateItem.contains("|")) {
            String[] items = updateItem.split("\\|");
            if (items.length == 2) {
                return items[0];
            }
        }
        throw new Exception(updateItem + "不符合 id|sql 格式，请检查!");
    }

    public String getDdl(String updateItem) throws Exception {
        if (updateItem.contains("|")) {
            String[] items = updateItem.split("\\|");
            if (items.length == 2) {
                return items[1];
            }
        }
        
        throw new Exception(updateItem + "不符合 id|sql 格式，请检查!");
    }

    @Test
    public void testStringSplit() throws Exception {
        String testString = "1038|10504.sql";

        String id = getPatchId(testString);
        String ddl = getDdl(testString);
        Assert.assertEquals("1038", id);
        Assert.assertEquals("10504.sql", ddl);
    }

}
