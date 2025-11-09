/*
 * $HeadURL: $
 * $Id: $
 * Copyright (c) 2013 by Riversoft System, all rights reserved.
 */
package com.riversoft.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

/**
 * @author Woden
 * 
 */
public class PoiUtilsTest {

    @Test
    public void testExcel2003() throws IOException {
        File file = new File(System.getProperty("java.io.tmpdir"), "测试.xls");
        if (!file.exists()) {
            file.createNewFile();
        }

        String[] fields = new String[] { "a", "b", "c" };
        HashMap<String, String> titles = new HashMap<>();
        titles.put("a", "a");
        titles.put("b", "b");
        titles.put("c", "c");
        List<HashMap<String, Object>> list = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            HashMap<String, Object> map = new HashMap<>();
            map.put("a", "中文" + i);
            map.put("b", "中文" + i);
            map.put("c", "中文" + i);
            list.add(map);
        }

        PoiUtils.exportListWithExcel2003(new FileOutputStream(file), fields, titles, list);
    }

    @Test
    public void testExcel2007() throws IOException {
        File file = new File(System.getProperty("java.io.tmpdir"), "测试.xls");
        if (!file.exists()) {
            file.createNewFile();
        }

        String[] fields = new String[] { "a", "b", "c" };
        Map<String, String> titles = new HashMap<>();
        titles.put("a", "a");
        titles.put("b", "b");
        titles.put("c", "c");
        List<HashMap<String, Object>> list = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            HashMap<String, Object> map = new HashMap<>();
            map.put("a", "中文" + i);
            map.put("b", "中文" + i);
            map.put("c", "中文" + i);
            list.add(map);
        }

        PoiUtils.exportListWithExcel2007(new FileOutputStream(file), fields, titles, list);
    }

}
