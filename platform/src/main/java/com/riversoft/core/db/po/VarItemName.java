/*
 * $HeadURL: $
 * $Id: $
 * Copyright (c) 2013 by Riversoft System, all rights reserved.
 */
package com.riversoft.core.db.po;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 扩展模式下的ItemName类型枚举
 * 
 * @author Woden
 * 
 */
@SuppressWarnings("rawtypes")
public enum VarItemName implements Code2NameVO {
    /**
     * 字符串类型
     */
    valueString("字符串", String.class),
    /**
     * 整数类型
     */
    valueInteger("整数", Long.class),
    /**
     * 复数类型
     */
    valueFloat("复数", BigDecimal.class),
    /**
     * 日期类型
     */
    valueDate("日期时间", Date.class);

    private String name;
    private Class type;

    private VarItemName(String name, Class type) {
        this.name = name;
        this.type = type;
    }

    @Override
    public Object getCode() {
        return name();
    }

    @Override
    public String getShowName() {
        return name;
    }

    public Class getType() {
        return type;
    }
}
