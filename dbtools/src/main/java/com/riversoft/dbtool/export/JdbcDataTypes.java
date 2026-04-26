/*
 * $HeadURL: $
 * $Id: $
 * Copyright (c) 2013 by Riversoft System, all rights reserved.
 */
package com.riversoft.dbtool.export;

/**
 * @author Borball
 */
public enum JdbcDataTypes {

    /**
     * 数字
     */
    INTEGER, BIGINT, DECIMAL, DOUBLE, FLOAT, SMALLINT, REAL, TINYINT, NUMERIC, BIT, BOOLEAN,

    /**
     * 时间
     */
    TIME, DATE, TIMESTAMP, TIMESTAMPTZ,

    /**
     * BINARY
     */
    BLOB, BINARY, VARBINARY,

    /**
     * 文本
     */
    CHAR, VARCHAR, LONGVARCHAR, CLOB, NCHAR, NCLOB, NVARCHAR, LONGNVARCHAR;

    /**
     * 其他
     */

}
