/*
 * $HeadURL: $
 * $Id: $
 * Copyright (c) 2013 by Riversoft System, all rights reserved.
 */
package com.riversoft.module.dyn;

/**
 * 执行模式枚举
 * 
 * @author woden
 * 
 */
enum ExecMode {

    CREATE(1), EDIT(2), DELETE(3);
    private Integer value;

    private ExecMode(Integer value) {
        this.value = value;
    }

    public Integer getValue() {
        return value;
    }
}
