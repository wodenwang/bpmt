/*
 * $HeadURL: $
 * $Id: $
 * Copyright (c) 2013 by Riversoft System, all rights reserved.
 */
package com.riversoft.core.db.hbm.model;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

/**
 * @author Woden
 * 
 */
@XStreamAlias("column")
class ColumnModel {

    @XStreamAsAttribute
    private Integer length;

    @XStreamAlias("not-null")
    @XStreamAsAttribute
    private Boolean notNull;

    private String comment;

    @XStreamAsAttribute
    private String name;

    @XStreamAlias("default")
    @XStreamAsAttribute
    private String defValue;

    @XStreamAsAttribute
    private Integer precision;

    @XStreamAsAttribute
    private Integer scale;

    /**
     * @return the defVal
     */
    public String getDefValue() {
        return defValue;
    }

    /**
     * @param defVal the defVal to set
     */
    public void setDefValue(String defValue) {
        this.defValue = defValue;
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
    public Boolean getNotNull() {
        return notNull;
    }

    /**
     * @param notNull the notNull to set
     */
    public void setNotNull(boolean notNull) {
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

}
