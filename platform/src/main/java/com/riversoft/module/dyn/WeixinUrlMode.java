/*
 * $HeadURL: $
 * $Id: $
 * Copyright (c) 2013 by Riversoft System, all rights reserved.
 */
package com.riversoft.module.dyn;

import com.riversoft.core.db.po.Code2NameVO;

/**
 * 入口超链接模式
 * 
 * @author woden
 * 
 */
public enum WeixinUrlMode implements Code2NameVO {

	SHOW(0, "查看优先"),
	EDIT(1, "编辑优先");

	private int code;
	private String showName;

	WeixinUrlMode(int code, String showName) {
		this.code = code;
		this.showName = showName;
	}

	@Override
	public Object getCode() {
		return code;
	}

	@Override
	public String getShowName() {
		return showName;
	}
}
