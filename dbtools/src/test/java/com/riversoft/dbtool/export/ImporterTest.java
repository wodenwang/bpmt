/*
 * $HeadURL: $
 * $Id: $
 * Copyright (c) 2013 by Riversoft System, all rights reserved.
 */
package com.riversoft.dbtool.export;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import javax.sql.DataSource;

import org.apache.commons.io.FileUtils;
import org.jumpmind.db.model.Table;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.jdbc.core.JdbcTemplate;

import com.riversoft.dbtool.util.DataSourceInstance;
import com.riversoft.dbtool.util.DatabaseManager;

/**
 * @author Borball
 * 
 */
public class ImporterTest {

    private static DataSource dataSource;
    private static DatabaseManager databaseManager;
    private static JdbcTemplate jdbcTemplate;
    
    @BeforeClass
    public static void beforeClass() throws Exception {
        Path databsebakBakPath = Paths.get("src/test/resources/database/importdb.h2.db.bak");
        Path databsetestPath = Paths.get("src/test/resources/database/importdb.h2.db");

        File databsebak = databsebakBakPath.toFile();
        File databsetest = databsetestPath.toFile();

        FileUtils.copyFile(databsebak, databsetest);

        dataSource = DataSourceInstance.getInstance("classpath:import-jdbc.properties").getDataSource();
        databaseManager = new DatabaseManager(dataSource);
        jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Test
    public void testDoImport() throws Exception {
        Importer importer = new Importer(dataSource);
        Path excelPath = Paths.get("src/test/resources/export/one.xlsx");
        ImportExportResponse response = importer.doImport(excelPath.toFile(), false, false, false);
        
        Assert.assertTrue(response.isSuccess());
        
        Table table = databaseManager.findTable("CM_BASE_CATELOG");
        Assert.assertNotNull(table);
        
        @SuppressWarnings("rawtypes")
        List result = jdbcTemplate.queryForList("select * from CM_BASE_CATELOG");
        Assert.assertNotNull(result);
        Assert.assertEquals(2, result.size());
    }

    @Test
    public void testDoImportWithClearOld() throws Exception {
        Importer importer = new Importer(dataSource);
        Path excelPath = Paths.get("src/test/resources/export/one.xlsx");
        ImportExportResponse response = importer.doImport(excelPath.toFile(), true, true, false);
        
        Assert.assertTrue(response.isSuccess());
        
        Table table = databaseManager.findTable("CM_BASE_CATELOG");
        Assert.assertNotNull(table);
        
        @SuppressWarnings("rawtypes")
        List result = jdbcTemplate.queryForList("select * from CM_BASE_CATELOG");
        Assert.assertNotNull(result);
        Assert.assertEquals(2, result.size());
    }
    

    @Test
    public void testDoImportWithReplaceOld() throws Exception {
        Importer importer = new Importer(dataSource);
        Path excelPath = Paths.get("src/test/resources/export/one.xlsx");
        ImportExportResponse response = importer.doImport(excelPath.toFile(), false, true, false);
        Assert.assertTrue(response.isSuccess());
        Table table = databaseManager.findTable("CM_BASE_CATELOG");
        Assert.assertNotNull(table);
        
        @SuppressWarnings("rawtypes")
        List result = jdbcTemplate.queryForList("select * from CM_BASE_CATELOG");
        Assert.assertNotNull(result);
        Assert.assertEquals(2, result.size());
    }
    
    @Ignore
    public void testDoImportWithErrorExit() throws Exception {
        Importer importer = new Importer(dataSource);
        Path excelPath = Paths.get("src/test/resources/export/test.xlsx");
        ImportExportResponse response = importer.doImport(excelPath.toFile(), false, true, true);
        Assert.assertTrue(response.isSuccess());
        System.out.println(response.getDetailes());
    }

}
