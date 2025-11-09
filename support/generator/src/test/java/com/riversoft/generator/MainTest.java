/*
 * $HeadURL: $
 * $Id: $
 * Copyright (c) 2013 by Riversoft System, all rights reserved.
 */
package com.riversoft.generator;

import org.apache.commons.cli.ParseException;
import org.junit.Test;
 
/**
 * @author Borball
 *
 */
public class MainTest {
    
    @Test
    public void testMain() throws ParseException{
        Main.main(new String[]{"-f", "file:src/main/scripts/config.properties"});
    }

}
