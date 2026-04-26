/*
 * $HeadURL: $
 * $Id: $
 * Copyright (c) 2013 by Riversoft System, all rights reserved.
 */
package com.riversoft.module.flow.activity.endevent;

import com.riversoft.core.db.po.Code2NameVO;

/**
 * 结束状态
 * 
 * @author woden
 * 
 */
public enum StateType implements Code2NameVO {

    NORMAL(2, "正常结束"), ERROR(3, "异常结束")

    ;
    private int code;
    private String name;

    private StateType(int code, String name) {
        this.code = code;
        this.name = name;
    }

    @Override
    public Object getCode() {
        return code;
    }

    @Override
    public String getShowName() {
        return name;
    }
}
