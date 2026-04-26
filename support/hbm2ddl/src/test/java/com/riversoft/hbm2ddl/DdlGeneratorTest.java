/*
 * $HeadURL: $
 * $Id: $
 * Copyright (c) 2013 by Wodensoft System, all rights reserved.
 */
package com.riversoft.hbm2ddl;

import java.io.File;
import java.net.URL;

import org.junit.Test;
 
/**
 * @author Borball
 *
 */
public class DdlGeneratorTest {

    @Test
    public void testGenerator() throws Exception {
        URL url = ClassLoader.getSystemClassLoader().getResource("dbdialects.properties");
        
        File root = new File(url.getFile());
        
        String basedir = root.getParentFile().getParentFile().getParentFile().getPath();
        
        String[] args = {basedir, "target/hbm", "target/test/sql"};
        DdlGenerator generator = new DdlGenerator(args);

		generator.hbm2ddl();
		        
    }
}
