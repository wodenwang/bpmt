/*
 * $HeadURL: $
 * $Id: $
 * Copyright (c) 2016 by Riversoft System, all rights reserved.
 */
package com.riversoft.platform.translate;

import com.riversoft.core.db.po.Code2NameVO;

/**
 * @author woden
 *
 */
public enum WxScope implements Code2NameVO {

	USER_INFO("snsapi_userinfo", "询问获取资料"),
	BASE("snsapi_base", "直接跳转");

	private String code;
	private String showName;

	WxScope(String code, String showName) {
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
