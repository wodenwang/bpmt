/*
 * $HeadURL: $
 * $Id: $
 * Copyright (c) 2013 by Riversoft System, all rights reserved.
 */
package com.riversoft.platform.translate;

import com.riversoft.core.db.po.Code2NameVO;

/**
 * 邮件发送类型
 * 
 * @author woden
 */
public enum MailSenderProtocol implements Code2NameVO {

	smtp("smtp", "smtp"), smtps("smtps", "smtps(SSL)")

	;
	private String code;
	private String showName;

	private MailSenderProtocol(String code, String showName) {
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
