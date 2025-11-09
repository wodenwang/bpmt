/*
 * $HeadURL: $
 * $Id: $
 * Copyright (c) 2013 by Riversoft System, all rights reserved.
 */
package com.riversoft.module.view.viewer;

import com.riversoft.core.db.po.Code2NameVO;

/**
 * @author woden
 * 
 */
public enum ResultType implements Code2NameVO {

	PAGE("1", "页面展示"),
	WORD("2", "Word文档"),
	EXCEL("3", "Excel文档(支持ZIP)"),
	PDF("4", "PDF文档"),
	DOWNLOAD("5", "下载"),
	TEXT("6", "文本(JSON或XML)"),
	MSG("7", "页面提示"),
	REDIRECT("8", "页面跳转");

	private String code;
	private String showName;

	private ResultType(String code, String showName) {
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
	static ResultType fromCode(Integer code) {
		for (ResultType type : ResultType.values()) {
			if (type.code.equals(String.valueOf(code))) {
				return type;
			}
		}

		return MSG;
	}

}
