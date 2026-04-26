/*
 * $HeadURL: $
 * $Id: $
 * Copyright (c) 2013 by Riversoft System, all rights reserved.
 */
package com.riversoft.dbtool.export;

/**
 * @author Borball
 */
public class ImporterMain {

    public static void main(String[] args) {
        ImporterCmdProcessor processor = new ImporterCmdProcessor();
        processor.process(args);
    }
}
