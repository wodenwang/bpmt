/*
 * $HeadURL: $
 * $Id: $
 * Copyright (c) 2013 by Riversoft System, all rights reserved.
 */
package com.riversoft.platform.po;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Ignore;

import com.riversoft.core.BeanFactory;
import com.riversoft.core.db.JdbcService;
import com.riversoft.core.db.ORMService;

/**
 * @author Woden
 * 
 */
public class TbTableTest {

    private TbTable table;

    @Before
    public void before() {
        BeanFactory.init("classpath:applicationContext.xml");
        table = (TbTable) ORMService.getInstance().findByPk(TbTable.class.getName(), "RV_A");
    }

    @Ignore
    public void testInsert() {
        Map<String, Object> params = new HashMap<>();
        params.put("C", 1001);
        params.put("B", "H");
        // 增
        JdbcService.getInstance().executeSQL(table.getInsertSql(), params);

        // 查
        assertEquals("H", JdbcService.getInstance().findSQL(table.getFindByPkSql(), params).get("B"));

        // 改
        params.put("B", "M");
        JdbcService.getInstance().executeSQL(table.getUpdateSql(), params);

        // 查
        assertEquals("M", JdbcService.getInstance().findSQL(table.getFindByPkSql(), params).get("B"));

        // 删
        JdbcService.getInstance().executeSQL(table.getDeleteSql(), params);

        // 查
        assertNull(JdbcService.getInstance().findSQL(table.getFindByPkSql(), params));
    }
}
