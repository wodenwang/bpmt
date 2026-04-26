/*
 * $HeadURL: $
 * $Id: $
 * Copyright (c) 2013 by Riversoft System, all rights reserved.
 */
package com.riversoft.core.db.po;

/**
 * 定义规则基類
 * 
 * @author Woden
 * 
 */
@SuppressWarnings("serial")
public abstract class BaseRuleItem extends BaseItem {

    private String fieldKey;
    private String busiName;
    private String itemName;
    private String widgetCmd;
    private Integer sort;
    private String description;

    /**
     * @return the fieldKey
     */
    public String getFieldKey() {
        return fieldKey;
    }

    /**
     * @param fieldKey the fieldKey to set
     */
    public void setFieldKey(String fieldKey) {
        this.fieldKey = fieldKey;
    }

    /**
     * @return the busiName
     */
    public String getBusiName() {
        return busiName;
    }

    /**
     * @param busiName the busiName to set
     */
    public void setBusiName(String busiName) {
        this.busiName = busiName;
    }

    /**
     * @return the itemName
     */
    public String getItemName() {
        return itemName;
    }

    /**
     * @param itemName the itemName to set
     */
    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    /**
     * @return the sort
     */
    public Integer getSort() {
        return sort;
    }

    /**
     * @return the widgetCmd
     */
    public String getWidgetCmd() {
        return widgetCmd;
    }

    /**
     * @param widgetCmd the widgetCmd to set
     */
    public void setWidgetCmd(String widgetCmd) {
        this.widgetCmd = widgetCmd;
    }

    /**
     * @param sort the sort to set
     */
    public void setSort(Integer sort) {
        this.sort = sort;
    }

    /**
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * @param description the description to set
     */
    public void setDescription(String description) {
        this.description = description;
    }
}
