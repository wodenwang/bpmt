/*
 * File Name  :CommonServiceTest.java
 * Create Date:2012-11-3 下午6:12:16
 * Author     :woden
 */

package com.riversoft.core.db;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.junit.After;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.riversoft.core.BeanFactory;
import com.riversoft.core.exception.SystemRuntimeException;

public class ORMServiceTest {

    private static List<Map<String, Object>> entities;

    public void addEntity(Map<String, Object> entity) {
        entities.add(entity);
    }

    @BeforeClass
    public static void beforeClass() throws Exception {
        entities = new ArrayList<Map<String, Object>>();
        BeanFactory.init("classpath:applicationContext-ormservice-test.xml");
    }

    @After
    public void after() {
        for (Map<String, Object> entity : entities) {
            try {
                ORMService.getInstance().remove(entity);
            } catch (SystemRuntimeException e) {
                e.printStackTrace();
            }
        }
        entities.clear();
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testQueryAll() {
        @SuppressWarnings("rawtypes")
        List<Map> list = ORMService.getInstance().queryAll("CmBaseData");
        Assert.assertNotNull(list);

        list = ORMService.getInstance().queryAll("com.riversoft.platform.po.UsUser");
        Assert.assertNotNull(list);
    }
}
