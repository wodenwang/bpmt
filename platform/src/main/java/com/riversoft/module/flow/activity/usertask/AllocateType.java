/*
 * $HeadURL: $
 * $Id: $
 * Copyright (c) 2013 by Riversoft System, all rights reserved.
 */
package com.riversoft.module.flow.activity.usertask;

import com.riversoft.core.db.po.Code2NameVO;

/**
 * @author woden
 * 
 */
public enum AllocateType implements Code2NameVO {

    GROUP(1, "组织"), ROLE(2, "角色"), GROUP_ROLE(3, "组织+角色"), UID(0, "用户");
    private int code;
    private String name;

    private AllocateType(int code, String name) {
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
