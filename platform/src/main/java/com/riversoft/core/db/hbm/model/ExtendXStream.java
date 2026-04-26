/*
 * $HeadURL: $
 * $Id: $
 * Copyright (c) 2013 by Riversoft System, all rights reserved.
 */
package com.riversoft.core.db.hbm.model;

import java.io.OutputStream;
import java.io.Writer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.thoughtworks.xstream.XStream;

/**
 * 
 * @author Borball
 * 
 */
public class ExtendXStream extends XStream {

    private static Logger log = LoggerFactory.getLogger(ExtendXStream.class);

    public ExtendXStream() {
        super();
        setMode(XStream.NO_REFERENCES);
        super.aliasSystemAttribute(null, "class");
        processAnnotations(HibernateMappingModel.class);
    }

    private String getDeclaration() {
        return "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n"
                + "<!DOCTYPE hibernate-mapping PUBLIC \"-//Hibernate/Hibernate Mapping DTD 3.0//EN\"\n"
                + "\"http://www.hibernate.org/dtd/hibernate-mapping-3.0.dtd\">\n";
    }

    @Override
    public void toXML(Object arg0, OutputStream arg1) {
        try {
            String dec = this.getDeclaration();
            byte[] bytesOfDec = dec.getBytes("utf-8");
            arg1.write(bytesOfDec);
        } catch (Exception e) {
            log.error("输出Declaration时候出现异常", e);
            return;
        }
        super.toXML(arg0, arg1);
    }

    @Override
    public void toXML(Object arg0, Writer arg1) {
        try {
            arg1.write(getDeclaration());
        } catch (Exception e) {
            log.error("输出Declaration时候出现异常", e);
            return;
        }
        super.toXML(arg0, arg1);
    }

}
