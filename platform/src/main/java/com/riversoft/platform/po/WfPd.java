/*
 * $Id: $
 * Copyright (c) 2013 by Riversoft System, all rights reserved.
 */
package com.riversoft.platform.po;

import com.riversoft.core.db.po.BaseItem;

/**
 * @author woden
 * 
 */
@SuppressWarnings("serial")
public class WfPd extends BaseItem {
    private String pdId;
    private String description;
    private String basicViewKey;// 对应工作流基础视图

    /**
     * @return the pdId
     */
    public String getPdId() {
        return pdId;
    }

    /**
     * @param pdId the pdId to set
     */
    public void setPdId(String pdId) {
        this.pdId = pdId;
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

    /**
     * @return the basicViewKey
     */
    public String getBasicViewKey() {
        return basicViewKey;
    }

    /**
     * @param basicViewKey the basicViewKey to set
     */
    public void setBasicViewKey(String basicViewKey) {
        this.basicViewKey = basicViewKey;
    }
}
