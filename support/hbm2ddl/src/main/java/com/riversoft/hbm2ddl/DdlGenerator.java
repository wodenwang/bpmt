/*
 * $HeadURL: $
 * $Id: $
 * Copyright (c) 2013 by RiverSoft System, all rights reserved.
 */
package com.riversoft.hbm2ddl;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import com.riversoft.util.PropertiesLoader;
import org.apache.commons.io.FileUtils;
import org.hibernate.cfg.Configuration;
import org.hibernate.tool.hbm2ddl.SchemaExport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Borball
 * 
 */
public class DdlGenerator {

    private Logger logger = LoggerFactory.getLogger(DdlGenerator.class);

    private Map<String, String> dbdialects = new HashMap<>();

    private static String HBM_MAPPING_DIR = "target/hbm";
    private static String SQL_OUTPUT_DIR = "target/sql";

    private String basedir;
    private String hbmdir = HBM_MAPPING_DIR;
    private String sqldir = SQL_OUTPUT_DIR;

    public DdlGenerator() {
        PropertiesLoader propertiesLoader = new PropertiesLoader("classpath:dbdialects.properties");
        Properties properties = propertiesLoader.getProperties();
        Set<String> names = properties.stringPropertyNames();

        for (String db : names) {
            dbdialects.put(db, properties.getProperty(db));
        }
    }

    public DdlGenerator(String[] args) {
        this();
        
        if (args.length == 1) {
            basedir = args[0];
        }
        if (args.length == 2) {
            basedir = args[0];
            hbmdir = args[1];
        }

        if (args.length == 3) {
            basedir = args[0];
            hbmdir = args[1];
            sqldir = args[2];
        }
    }

    private void initConfiguration(Configuration configuration) {
        String hbmParent = basedir + "/" + hbmdir;

        logger.debug("hbm file folders:" + hbmParent);

        Path hbmPath = Paths.get(hbmParent);
        Collection<File> mappings = FileUtils.listFiles(hbmPath.toFile(), new String[]{"hbm.xml"}, true);

        for (File file : mappings) {
            configuration.addFile(file);
        }
    }

    public void hbm2ddl() throws Exception {
        for (String db : dbdialects.keySet()) {
            logger.info("start generate ddl:" + db + "->" + dbdialects.get(db));

            Configuration configuration = new Configuration();
            initConfiguration(configuration);
            configuration.setProperty("hibernate.dialect", dbdialects.get(db));

            SchemaExport task = new SchemaExport(configuration);

            task.setDelimiter(";");
            task.setFormat(false);

            String output = basedir + "/" + sqldir + "/" + db.toLowerCase();
            Path outputfilepath = Paths.get(output);
            File outputfile = outputfilepath.toFile();
            outputfile.mkdirs();
            
            String createOutput = output + "/" + "create_model.sql";
            Path createOutputFilePath = Paths.get(createOutput);
            File createOutputFile = createOutputFilePath.toFile();

            String createTmp = output + "/" + "create_model.tmp";
            Path createTmpFilePath = Paths.get(createTmp);
            File createTmpFile = createTmpFilePath.toFile();

            task.setOutputFile(createTmpFile.getPath());
            
            task.execute(true, false, false, true);

            removeCreatedReferences(createTmpFile, createOutputFile);

            String dropOutput = output + "/" + "drop_model.sql";
            Path dropOutputFilePath = Paths.get(dropOutput);
            File dropOutputFile = dropOutputFilePath.toFile();

            String dropTmp = output + "/" + "drop_model.tmp";
            Path dropTmpFilePath = Paths.get(dropTmp);
            File dropTmpFile = dropTmpFilePath.toFile();

            task.setOutputFile(dropTmpFile.getPath());
            
            task.execute(true, false, true, false);

            removeDropReferences(dropTmpFile, dropOutputFile);

            logger.info("finished...................................................");
        }

    }

    private void removeDropReferences(File dropTmpFile, File dropOutputFile) throws Exception {
        BufferedReader br = new BufferedReader(new FileReader(dropTmpFile));
        BufferedWriter bw = new BufferedWriter(new FileWriter(dropOutputFile));

        String line = null;
        while ((line = br.readLine()) != null) {
            if (line.toLowerCase().contains("alter table") && line.toLowerCase().contains("drop foreign key")) {
                logger.info("跳过 {}", line);
            } else {
                bw.write(line);
                bw.write("\n");
            }
        }
        br.close();
        bw.close();

        FileUtils.forceDelete(dropTmpFile);
    }

    private void removeCreatedReferences(File createTmpFile, File createOutputFile) throws Exception {
        BufferedReader br = new BufferedReader(new FileReader(createTmpFile));
        BufferedWriter bw = new BufferedWriter(new FileWriter(createOutputFile));

        String line = null;
        while ((line = br.readLine()) != null) {
            if (line.toLowerCase().contains("alter table") && line.toLowerCase().contains("add constraint") && line.toLowerCase().contains("foreign key")) {
                logger.info("跳过 {}", line);
            } else {
                bw.write(line);
                bw.write("\n");
            }
        }
        br.close();
        bw.close();

        FileUtils.forceDelete(createTmpFile);
    }

}
