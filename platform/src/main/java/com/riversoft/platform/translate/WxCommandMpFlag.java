/*
 * $HeadURL: $
 * $Id: $
 * Copyright (c) 2015 by Riversoft System, all rights reserved.
 */
package com.riversoft.platform.translate;

import com.riversoft.core.db.po.Code2NameVO;

/**
 * 
 * @author woden
 *
 */
public enum WxCommandMpFlag implements Code2NameVO {
	MP(1, "公众号(服务号/订阅号)"),
	QY(0, "企业号");

	private int code;
	private String showName;

	WxCommandMpFlag(int code, String showName) {
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
