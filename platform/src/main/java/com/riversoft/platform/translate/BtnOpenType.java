/*
 * $HeadURL: $
 * $Id: $
 * Copyright (c) 2013 by Riversoft System, all rights reserved.
 */
package com.riversoft.platform.translate;

import com.riversoft.core.db.po.Code2NameVO;

/**
 * @author woden
 * 
 */
public enum BtnOpenType implements Code2NameVO {

	TAB(1, "标签TAB"), WIN(2, "弹出框"), MSG(3, "信息提示"), DOWNLOAD(4, "下载/导出"), PRINT(5, "打印")

	;
	private Object code;
	private String showName;

	private BtnOpenType(Integer code, String showName) {
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
