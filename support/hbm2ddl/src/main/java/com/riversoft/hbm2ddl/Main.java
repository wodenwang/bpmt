/*
 * $HeadURL: $
 * $Id: $
 * Copyright (c) 2013 by Riversoft System, all rights reserved.
 */
package com.riversoft.hbm2ddl;

/**
 * @author Borball
 * 
 */
public class Main {

    public static void main(String[] args) throws Exception {
        DdlGenerator generator = new DdlGenerator(args);
        generator.hbm2ddl();
    }
}
