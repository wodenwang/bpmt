/*
 * $HeadURL: $
 * $Id: $
 * Copyright (c) 2013 by Riversoft System, all rights reserved.
 */
package com.riversoft.module.flow.view;

import java.util.HashMap;
import java.util.Map;

/**
 * 系统内置子标签
 * 
 * @author woden
 * 
 */
public enum SysTab {

    picture("流程图"), history("流程历史");

    private String busiName;

    private SysTab(String busiName) {
        this.busiName = busiName;
    }

    public String getBusiName() {
        return busiName;
    }

    public String getName() {
        return this.name();
    }

    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("name", getName());
        map.put("busiName", busiName);
        return map;
    }
}
