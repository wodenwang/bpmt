/*
 * $HeadURL: $
 * $Id: $
 * Copyright (c) 2013 by Riversoft System, all rights reserved.
 */
package com.riversoft.platform.db;

import java.math.BigDecimal;
import java.util.Date;

import com.riversoft.core.db.po.Code2NameVO;

/**
 * 动态表字段类型枚举
 * 
 * @author Woden
 * 
 */
public enum Types implements Code2NameVO {

    /**
     * 
     */
    String(java.sql.Types.VARCHAR, "字符串/文本", String.class),
    /**
     * 
     */
    Integer(java.sql.Types.INTEGER, "整数", Integer.class),
    /**
     * 
     */
    BigDecimal(java.sql.Types.NUMERIC, "复数/数字(小数)", BigDecimal.class),
    /**
     * 
     */
    Date(java.sql.Types.TIMESTAMP, "日期时间", Date.class),
    /**
     * 
     */
    Long(java.sql.Types.BIGINT, "长整数", Long.class),
    /**
     * 
     */
    Clob(java.sql.Types.CLOB, "大文本", String.class),
    /**
     * 
     */
    Blob(java.sql.Types.BLOB, "二进制数据", byte[].class);

    private int type;// 对应类型
    private String showName;// 页面展示名
    private Class<?> javaType;// java映射类

    private Types(int type, String showName, Class<?> klass) {
        this.type = type;
        this.showName = showName;
        this.javaType = klass;
    }

    @Override
    public Object getCode() {
        return type;
    }

    @Override
    public String getShowName() {
        return showName;
    }

    public Class<?> getJavaType() {
        return javaType;
    }

    /**
     * 根据jdbc code映射java class
     * 
     * @param code
     * @return
     */
    public static Types findByCode(Integer code) {
        for (Types types : values()) {
            if (types.type == code.intValue()) {
                return types;
            }
        }

        return String;
    }

    /**
     * 根据 java class映射java cdoe
     * 
     * @param javaType
     * @return
     */
    public static Types findByClass(Class<?> javaType) {
        for (Types types : values()) {
            if (types.javaType == javaType) {
                return types;
            }
        }

        return String;
    }

}
