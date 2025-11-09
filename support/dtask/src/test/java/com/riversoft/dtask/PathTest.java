/*
 * $HeadURL: $
 * $Id: $
 * Copyright (c) 2013 by Riversoft System, all rights reserved.
 */
package com.riversoft.dtask;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import com.riversoft.util.PropertiesLoader;

/**
 * @author Borball
 *
 */
public class PathTest {
    
    @Ignore
    public void testPath(){
        File root = new File("D:\\xxxxx");
        File[] magicPlaces = new File[2];
        Path antLibsPath = Paths.get(root.getAbsolutePath(), "tools", "internal", "libs");
        File antLibsFir = antLibsPath.toFile();

        magicPlaces[0] = antLibsFir;

        Path platformLibsPath = Paths.get(root.getAbsolutePath(), "platform", "WEB-INF", "lib");
        File platformLibsFir = platformLibsPath.toFile();

        magicPlaces[1] = platformLibsFir;
        
        Assert.assertEquals("D:\\xxxxx\\tools\\internal\\libs", magicPlaces[0].getAbsolutePath());
        Assert.assertEquals("D:\\xxxxx\\platform\\WEB-INF\\lib", magicPlaces[1].getAbsolutePath());
        
        File antLibs = magicPlaces[0];
        File platformLibs = magicPlaces[1];
        Assert.assertTrue(antLibs.exists() && antLibs.isDirectory());
        Assert.assertTrue(platformLibs.exists() && platformLibs.isDirectory());
        
    }

    @Ignore
    public void testPropertiesLoader(){
        try {
            String jdbcConfResource = "file:/Users/borball/Riversoft/river-test/tools/internal/../../conf/jdbc.properties";
            PropertiesLoader propertiesLoader = new PropertiesLoader(jdbcConfResource);

            String jdbcDriver = propertiesLoader.getProperty("jdbc.driverClassName");
            String jdbcUrl = propertiesLoader.getProperty("jdbc.url");
            String jdbcUser = propertiesLoader.getProperty("jdbc.username");
            String jdbcPwd = propertiesLoader.getProperty("jdbc.password");
            String dbType = propertiesLoader.getProperty("database.type");
            
            System.out.println(jdbcDriver);
            System.out.println(jdbcUrl);
            System.out.println(jdbcUser);
            System.out.println(jdbcPwd);
            System.out.println(dbType);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
