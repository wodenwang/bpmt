/*
 * $HeadURL: $
 * $Id: $
 * Copyright (c) 2015 by Riversoft System, all rights reserved.
 */
package com.riversoft.platform.translate;

import com.riversoft.core.db.po.Code2NameVO;
import com.riversoft.platform.language.LanguageFitter;

/**
 * @author woden
 *
 */
public enum NotifyReceiveType implements Code2NameVO {
	USER("USER", LanguageFitter.fit("#:zh[个人通知]:en[Personal notice]#")), GROUP("GROUP", LanguageFitter.fit("#:zh[群组通知]:en[Group notice]#"))

	;
	private String code;
	private String showName;

	private NotifyReceiveType(String code, String showName) {
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
