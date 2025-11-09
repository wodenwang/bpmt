/*
 * $HeadURL: $
 * $Id: $
 * Copyright (c) 2013 by Riversoft System, all rights reserved.
 */
package com.riversoft.module.view.viewer;

import com.riversoft.core.db.po.Code2NameVO;

/**
 * @author Wilmer
 * @date 2016/06/21
 */
public enum TempletType implements Code2NameVO {

	FILE("0", "模板文件"),
	FILE_PATH("1", "模板文件路径");

	private String code;
	private String showName;

	private TempletType(String code, String showName) {
		this.code = code;
		this.showName = showName;
	}

	/**
	 * @return the code
	 */
	public Object getCode() {
		return code;
	}

	/**
	 * @return the showName
	 */
	public String getShowName() {
		return showName;
	}

	/**
	 * 转换获取枚举
	 * 
	 * @param code
	 * @return
	 */
	static TempletType fromCode(Integer code) {
		for (TempletType type : TempletType.values()) {
			if (type.code.equals(String.valueOf(code))) {
				return type;
			}
		}

		return FILE;
	}
}
