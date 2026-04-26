/*
 * $HeadURL: $
 * $Id: $
 * Copyright (c) 2013 by Riversoft System, all rights reserved.
 */
package com.riversoft.core.db.hbm;

/**
 * hbm文件属性映射模型
 * 
 * @author Woden
 * 
 */
public class HbmProperty {

    /**
     * 字段名(实体映射名)
     */
    private String name;
    /**
     * 字段类型
     */
    @SuppressWarnings("rawtypes")
    private Class type;
    /**
     * 表字段名
     */
    private String columnName;
    /**
     * 字段长度
     */
    private Integer length;
    /**
     * 是否非空
     */
    private Boolean notNull;
    /**
     * 字段描述
     */
    private String comment;
    /**
     * 字段默认值
     */
    private String defValue;
    /**
     * 整数,复数总长度
     */
    private Integer precision;
    /**
     * 复数小数点后位数
     */
    private Integer scale;

    /**
     * @return the precision
     */
    public Integer getPrecision() {
        return precision;
    }

    /**
     * @param precision the precision to set
     */
    public void setPrecision(Integer precision) {
        this.precision = precision;
    }

    /**
     * @return the scale
     */
    public Integer getScale() {
        return scale;
    }

    /**
     * @param scale the scale to set
     */
    public void setScale(Integer scale) {
        this.scale = scale;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the type
     */
    @SuppressWarnings("rawtypes")
    public Class getType() {
        return type;
    }

    /**
     * @param type the type to set
     */
    @SuppressWarnings("rawtypes")
    public void setType(Class type) {
        this.type = type;
    }

    /**
     * @return the columnName
     */
    public String getColumnName() {
        return columnName;
    }

    /**
     * @param columnName the columnName to set
     */
    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }

    /**
     * @return the length
     */
    public Integer getLength() {
        return length;
    }

    /**
     * @param length the length to set
     */
    public void setLength(Integer length) {
        this.length = length;
    }

    /**
     * @return the notNull
     */
    public Boolean isNotNull() {
        return notNull;
    }

    /**
     * @param notNull the notNull to set
     */
    public void setNotNull(Boolean notNull) {
        this.notNull = notNull;
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
     * @return the defValue
     */
    public String getDefValue() {
        return defValue;
    }

    /**
     * @param defValue the defValue to set
     */
    public void setDefValue(String defValue) {
        this.defValue = defValue;
    }

}
