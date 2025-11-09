/*
 * $HeadURL: $
 * $Id: $
 * Copyright (c) 2013 by Riversoft System, all rights reserved.
 */
package com.riversoft.module.dyn;

import com.riversoft.core.db.po.Code2NameVO;

/**
 * 微信企业号展示模式
 * 
 * @author woden
 * 
 */
public enum WeixinListMode implements Code2NameVO {

	TEXT(0, "纯文模式"),
	IMAGE(1, "图文模式");

	private int code;
	private String showName;

	WeixinListMode(int code, String showName) {
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
