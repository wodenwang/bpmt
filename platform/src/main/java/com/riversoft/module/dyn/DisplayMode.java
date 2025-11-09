/*
 * $HeadURL: $
 * $Id: $
 * Copyright (c) 2013 by Riversoft System, all rights reserved.
 */
package com.riversoft.module.dyn;

/**
 * 展示模式枚举
 * 
 * @author woden
 * 
 */
enum DisplayMode {

    LIST(1), DETAIL(2), FORM(3), EXPORT(4);
    private Integer value;

    private DisplayMode(Integer value) {
        this.value = value;
    }

    public Integer getValue() {
        return value;
    }
}
