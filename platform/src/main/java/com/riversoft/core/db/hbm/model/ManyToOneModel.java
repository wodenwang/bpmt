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
@XStreamAlias("many-to-one")
public class ManyToOneModel {

    @XStreamAlias("name")
    @XStreamAsAttribute
    private String name;

    @SuppressWarnings("rawtypes")
    @XStreamAlias("class")
    @XStreamAsAttribute
    private Class type;

    @XStreamAlias("unique")
    @XStreamAsAttribute
    private boolean unique;

    private ColumnModel column;

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
     * @return the unique
     */
    public boolean isUnique() {
        return unique;
    }

    /**
     * @param unique the unique to set
     */
    public void setUnique(boolean unique) {
        this.unique = unique;
    }

    /**
     * @return the column
     */
    public ColumnModel getColumn() {
        return column;
    }

    /**
     * @param column the column to set
     */
    public void setColumn(ColumnModel column) {
        this.column = column;
    }

}
