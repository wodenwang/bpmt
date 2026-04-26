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
public enum NotifyMsgType implements Code2NameVO {
	MAIL("MAIL", LanguageFitter.fit("#:zh[邮件]:en[Mail]#")), WX("WX", LanguageFitter.fit("#:zh[微信企业号]:en[WeChat]#"))

	;
	private String code;
	private String showName;

	private NotifyMsgType(String code, String showName) {
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
