/*
 * $HeadURL: $
 * $Id: $
 * Copyright (c) 2015 by Riversoft System, all rights reserved.
 */
package com.riversoft.platform.translate;

import com.riversoft.core.db.po.Code2NameVO;

/**
 * 组织架构托管类型
 * 
 * @author woden
 *
 */
public enum ContactMode implements Code2NameVO {

	all(1, "全面托管"), user(2, "托管用户"), none(3, "不托管")

	;
	private int code;
	private String showName;

	ContactMode(int code, String showName) {
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

	/**
	 * 根据配置获取枚举
	 * 
	 * @param code
	 * @return
	 */
	public static ContactMode fromCode(int code) {
		for (ContactMode mode : values()) {
			if (mode.code == code) {
				return mode;
			}
		}

		return none;
	}
}
