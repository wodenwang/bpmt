/*
 * $HeadURL: $
 * $Id: $
 * Copyright (c) 2013 by Riversoft System, all rights reserved.
 */
package com.riversoft.core.db.hbm;

import java.math.BigDecimal;
import java.util.Date;

import com.riversoft.core.db.po.Code2NameVO;

/**
 * 动态表字段类型枚举
 * 
 * @author Woden
 * 
 */
public enum FieldType implements Code2NameVO {

    STRING(String.class, "字符"), INTEGER(Integer.class, "整形"), LONG(Long.class, "长整形"), FLOAT(BigDecimal.class, "复数"), DATE(
            Date.class, "日期/时间");

    @SuppressWarnings("rawtypes")
    private Class klass;// 对应java类型
    private String showName;// 页面展示名

    @SuppressWarnings("rawtypes")
    private FieldType(Class klass, String showName) {
        this.klass = klass;
        this.showName = showName;
    }

    @Override
    public Object getCode() {
        return klass.getName();
    }

    @Override
    public String getShowName() {
        return showName;
    }

}
