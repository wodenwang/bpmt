/*
 * $HeadURL: $
 * $Id: $
 * Copyright (c) 2013 by Riversoft System, all rights reserved.
 */
package com.riversoft.core.web.widget;

import com.riversoft.core.db.po.Code2NameVO;

/**
 * web字段状态
 * 
 * @author Woden
 * 
 */
public enum WidgetState implements Code2NameVO {
    /**
     * 普通
     */
    normal("标准"),
    /**
     * 只读，用于表单不可修改
     */
    readonly("只读"),
    /**
     * 控件不可用
     */
    disabled("不可用"),
    /**
     * 不展示
     */
    none("不展示");

    private String showName;

    private WidgetState(String showName) {
        this.showName = showName;
    }

    @Override
    public Object getCode() {
        return name();
    }

    @Override
    public String getShowName() {
        return showName;
    }
}
