/*
 * $HeadURL: $
 * $Id: $
 * Copyright (c) 2013 by Riversoft System, all rights reserved.
 */
package com.riversoft.generator;

import org.apache.commons.cli.ParseException;

import com.riversoft.core.BeanFactory;

/**
 * @author Borball
 * 
 */
public class Main {

    public static void main(String[] args) throws ParseException {
        BeanFactory.init("classpath:applicationContext-freemarker.xml");

        GeneratorCmdProcessor processor = new GeneratorCmdProcessor();
        processor.process(args);
    }

}
