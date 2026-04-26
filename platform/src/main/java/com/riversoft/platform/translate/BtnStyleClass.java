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
public enum BtnStyleClass implements Code2NameVO {

	LEFT("left", "左侧"), CENTER("center", "居中"), RIGHT("right", "右侧")

	;
	private String code;
	private String showName;

	private BtnStyleClass(String code, String showName) {
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
}
