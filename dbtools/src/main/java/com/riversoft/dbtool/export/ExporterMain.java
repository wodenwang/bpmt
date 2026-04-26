/*
 * $HeadURL: $
 * $Id: $
 * Copyright (c) 2013 by Riversoft System, all rights reserved.
 */
package com.riversoft.dbtool.export;

/**
 * @author Borball
 */
public class ExporterMain {

    /**
     * @param args
     */
    public static void main(String[] args) {
        ExporterCmdProcessor processor = new ExporterCmdProcessor();
        processor.process(args);
    }

}
