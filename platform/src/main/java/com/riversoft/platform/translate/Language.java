/*
 * $HeadURL: $
 * $Id: $
 * Copyright (c) 2015 by Riversoft System, all rights reserved.
 */
package com.riversoft.platform.translate;

import com.riversoft.core.db.po.Code2NameVO;

/**
 * 系统支持语言枚举
 * 
 * @author woden
 *
 */
public enum Language implements Code2NameVO {

	zh_CN("zh_CN", "中文"),
	zh_TW("zh_TW", "中文(繁体)"),
	en("en", "English");

	private String code;
	private String showName;

	Language(String code, String showName) {
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
