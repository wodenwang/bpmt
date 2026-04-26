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
public enum MailReceiverProtocol implements Code2NameVO {

	pop("pop", "pop3"), pops("pops", "pop3(SSL)"), imap("imap", "imap"), imaps("imaps", "imap(SSL)")

	;
	private String code;
	private String showName;

	private MailReceiverProtocol(String code, String showName) {
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
