/*
 * $HeadURL: $
 * $Id: $
 * Copyright (c) 2013 by Riversoft System, all rights reserved.
 */
package com.riversoft.core.db.hbm;

/**
 * hbm文件ID模型
 * 
 * @author Woden
 * 
 */
public class HbmId {

    /**
     * 主键生成规则
     */
    private String generator;
    /**
     * 主键字段
     */
    private HbmProperty[] properties;

    /**
     * @return the generator
     */
    public String getGenerator() {
        return generator;
    }

    /**
     * @param generator the generator to set
     */
    public void setGenerator(String generator) {
        this.generator = generator;
    }

    /**
     * @return the properties
     */
    public HbmProperty[] getProperties() {
        return properties;
    }

    /**
     * @param properties the properties to set
     */
    public void setProperties(HbmProperty[] properties) {
        this.properties = properties;
    }

}
