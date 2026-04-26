/*
 * $HeadURL: $
 * $Id: $
 * Copyright (c) 2013 by Riversoft System, all rights reserved.
 */
package com.riversoft.core.db.hbm;

/**
 * hbm文件类映射模型
 * 
 * @author Woden
 * 
 */
public class HbmClass {

    /**
     * 实体表名
     */
    private String table;

    /**
     * 实体映射名(动态模式)
     */
    private String entityName;
    /**
     * 实体映射类(静态模式)
     */
    @SuppressWarnings("rawtypes")
    private Class entityClass;

    /**
     * 表说明
     */
    private String comment;

    /**
     * 主键信息
     */
    private HbmId id;

    /**
     * 字段信息(不包含主键信息)
     */
    private HbmProperty[] propertys;

    /**
     * @return the table
     */
    public String getTable() {
        return table;
    }

    /**
     * @param table the table to set
     */
    public void setTable(String table) {
        this.table = table;
    }

    /**
     * @return the entityName
     */
    public String getEntityName() {
        return entityName;
    }

    /**
     * @param entityName the entityName to set
     */
    public void setEntityName(String entityName) {
        this.entityName = entityName;
    }

    /**
     * @return the entityClass
     */
    @SuppressWarnings("rawtypes")
    public Class getEntityClass() {
        return entityClass;
    }

    /**
     * @param entityClass the entityClass to set
     */
    @SuppressWarnings("rawtypes")
    public void setEntityClass(Class entityClass) {
        this.entityClass = entityClass;
    }

    /**
     * @return the comment
     */
    public String getComment() {
        return comment;
    }

    /**
     * @param comment the comment to set
     */
    public void setComment(String comment) {
        this.comment = comment;
    }

    /**
     * @return the id
     */
    public HbmId getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(HbmId id) {
        this.id = id;
    }

    /**
     * @return the propertys
     */
    public HbmProperty[] getPropertys() {
        return propertys;
    }

    /**
     * @param propertys the propertys to set
     */
    public void setPropertys(HbmProperty[] propertys) {
        this.propertys = propertys;
    }

}
