/*
 * $HeadURL: $
 * $Id: $
 * Copyright (c) 2015 by Riversoft System, all rights reserved.
 */
package com.riversoft.platform.translate;

import com.riversoft.core.db.po.Code2NameVO;

/**
 * @author woden
 *
 */
public enum AppStatus implements Code2NameVO {
	// 状态.0:已创建未连接;1:已对接;2:应用不存在
	unconnect(0, "未对接"), connected(1, "已对接"), none(2, "无法对接(应用不存在)")

	;
	private int code;
	private String showName;

	AppStatus(int code, String showName) {
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
