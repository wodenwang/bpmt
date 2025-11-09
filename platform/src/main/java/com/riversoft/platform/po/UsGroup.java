/*
 * $HeadURL: $
 * $Id: $
 * Copyright (c) 2013 by Riversoft System, all rights reserved.
 */
package com.riversoft.platform.po;

import com.riversoft.core.db.po.BaseItem;

/**
 * @author Woden
 */
public class UsGroup extends BaseItem {

    /** */
    private static final long serialVersionUID = 1L;
    private String groupKey;
    private String busiName;
    private String parentKey;
    private Integer sysFlag;
    private Integer sort;
    private Integer wxDepartmentId;

    /**
     * @return the sysFlag
     */
    public Integer getSysFlag() {
        return sysFlag;
    }

    /**
     * @param sysFlag the sysFlag to set
     */
    public void setSysFlag(Integer sysFlag) {
        this.sysFlag = sysFlag;
    }

    /**
     * @return the groupKey
     */
    public String getGroupKey() {
        return groupKey;
    }

    /**
     * @param groupKey the groupKey to set
     */
    public void setGroupKey(String groupKey) {
        this.groupKey = groupKey;
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
     * @return the parentKey
     */
    public String getParentKey() {
        return parentKey;
    }

    /**
     * @param parentKey the parentKey to set
     */
    public void setParentKey(String parentKey) {
        this.parentKey = parentKey;
    }

    /**
     * @return the sort
     */
    public Integer getSort() {
        return sort;
    }

    /**
     * @param sort the sort to set
     */
    public void setSort(Integer sort) {
        this.sort = sort;
    }

    public Integer getWxDepartmentId() {
        return wxDepartmentId;
    }

    public void setWxDepartmentId(Integer wxDepartmentId) {
        this.wxDepartmentId = wxDepartmentId;
    }
}
