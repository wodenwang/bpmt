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
public enum TaskNotifyType implements Code2NameVO {
	user("1", "通知接收人"), forward("2", "通知被转发人"), group("3", "通知接收群组(群发)");
	private String code;
	private String showName;

	private TaskNotifyType(String code, String showName) {
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
