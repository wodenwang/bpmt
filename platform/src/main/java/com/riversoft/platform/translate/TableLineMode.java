/*
 * $HeadURL: $
 * $Id: $
 * Copyright (c) 2015 by Riversoft System, all rights reserved.
 */
package com.riversoft.platform.translate;

import com.riversoft.core.db.po.Code2NameVO;

/**
 * 表格行展示模式
 * 
 * @author woden
 * 
 */
public enum TableLineMode implements Code2NameVO {

	NORMAL(0, "单元格"), WHOLE(1, "表格整行"), SELF(2, "独立区域")

	;
	private Object code;
	private String showName;

	private TableLineMode(Integer code, String showName) {
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
	 * @param code
	 *            the code to set
	 */
	public void setCode(Object code) {
		this.code = code;
	}

	/**
	 * @return the showName
	 */
	public String getShowName() {
		return showName;
	}

	/**
	 * @param showName
	 *            the showName to set
	 */
	public void setShowName(String showName) {
		this.showName = showName;
	}
}
