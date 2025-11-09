/*
 * $HeadURL: $
 * $Id: $
 * Copyright (c) 2013 by Riversoft System, all rights reserved.
 */
package com.riversoft.module.flow.activity.servicetask;

import com.riversoft.core.db.po.Code2NameVO;

/**
 * @author woden
 * 
 */
public enum ErrorType implements Code2NameVO {

	ERROR(1, "回滚事务"), NORMAL(0, "忽略错误,跳过")

	;
	private int code;
	private String name;

	private ErrorType(int code, String name) {
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
