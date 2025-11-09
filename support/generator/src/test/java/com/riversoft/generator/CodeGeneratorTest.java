/*
 * $HeadURL: $
 * $Id: $
 * Copyright (c) 2013 by Riversoft System, all rights reserved.
 */
package com.riversoft.generator;

import java.io.IOException;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.riversoft.core.BeanFactory;

/**
 * @author Borball
 * 
 */
public class CodeGeneratorTest {

    @BeforeClass
    public static void beforeClass() {
        BeanFactory.init("classpath:applicationContext-freemarker.xml");
    }

    @Test
    public void testInit() {
        Settings settings = new Settings();
        settings.setModule("demo");
        settings.setAuthor("junit");
        settings.setOutdir("src/test/java/");
        settings.setHbmName("hbm_test/DEMO.hbm.xml");
        Assert.assertNotNull(new CodeGenerator(settings));
    }

    @Test
    public void testGenerate() throws IOException {
        Settings settings = new Settings();
        settings.setModule("demo");
        settings.setAuthor("junit");
        settings.setOutdir("src/test/java/");
        settings.setPagePath("/module/demo/DemoCRUDAction/");
        settings.setHasList(true);
        settings.setHasBatch(true);
        settings.setHbmName("hbm_test/DEMO.hbm.xml");
        
        CodeGenerator generator = new CodeGenerator(settings);
        generator.generate();
    }

}
