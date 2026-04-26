/*
 * $HeadURL: $
 * $Id: $
 * Copyright (c) 2013 by Riversoft System, all rights reserved.
 */
package com.riversoft.dbtool.util;

import java.util.Properties;

import javax.sql.DataSource;

import org.apache.commons.dbcp.BasicDataSourceFactory;

import com.riversoft.util.PropertiesLoader;

/**
 * @author Borball
 * 
 */
public class DataSourceInstance {

    private static DataSourceInstance instance = null;
    private DataSource dataSource = null;

    private DataSourceInstance(Properties properties) throws Exception {
        dataSource = BasicDataSourceFactory.createDataSource(properties);
    }

    public synchronized static DataSourceInstance getInstance(String driver, String url, String username,
            String password) throws Exception {
        if (instance == null){
            Properties properties = new Properties();
            properties.setProperty("driverClassName", driver);
            properties.setProperty("url", url);
            properties.setProperty("username", username);
            properties.setProperty("password", password);
            properties.setProperty("maxTotal", "40");
            
            instance = new DataSourceInstance(properties);
        }

        return instance;
    }

    public synchronized static DataSourceInstance getInstance(String resourcesPath) throws Exception {
        if (instance == null){
            PropertiesLoader propertiesLoader = new PropertiesLoader(resourcesPath);
            instance = new DataSourceInstance(propertiesLoader.getProperties());
        }

        return instance;
    }
    
    public DataSource getDataSource() {
        return dataSource;
    }

}
