/*
 * $HeadURL: $
 * $Id: $
 * Copyright (c) 2013 by Riversoft System, all rights reserved.
 */
package com.riversoft.dbtool.export;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.sql.DataSource;

import org.apache.commons.io.FileUtils;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import com.riversoft.dbtool.util.DataSourceInstance;

/**
 * @author Borball
 * 
 */
public class ExporterTest {

    private static DataSource dataSource;
    
    @BeforeClass
    public static void beforeClass() throws Exception {
        Path databsebakBakPath = Paths.get("src/test/resources/database/exportdb.h2.db.bak");
        Path databsetestPath = Paths.get("src/test/resources/database/exportdb.h2.db");
        
        File databsebak = databsebakBakPath.toFile();
        File databsetest = databsetestPath.toFile();
        
        FileUtils.copyFile(databsebak, databsetest);
        dataSource = DataSourceInstance.getInstance("classpath:export-jdbc.properties").getDataSource();
    }

    @Test
    public void testDoExport() throws Exception {
        Path excelPath = Paths.get("target/sequence.xlsx");
        FileOutputStream file = new FileOutputStream(excelPath.toFile());
        Exporter exporter = new Exporter(dataSource);
        exporter.doExport(file, Exporter.ExcelType.EXCEL_AFTER_2003, "CM_SEQUENCE");
    }

    @Ignore
    public void testDoExportAll() throws Exception {
        Path excelPath = Paths.get("target/all.xlsx");
        FileOutputStream file = new FileOutputStream(excelPath.toFile());
        Exporter exporter = new Exporter(dataSource);
        exporter.doExportAll(file, Exporter.ExcelType.EXCEL_AFTER_2003);

    }

}
