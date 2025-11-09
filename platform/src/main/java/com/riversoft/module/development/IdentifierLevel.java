/*
 * $HeadURL: $
 * $Id: $
 * Copyright (c) 2015 by Riversoft System, all rights reserved.
 */
package com.riversoft.module.development;

import com.riversoft.core.db.po.Code2NameVO;

/**
 * @author woden
 *
 */
public enum IdentifierLevel implements Code2NameVO {

	F(-1, "BPMT-免费版"),
	G1(1, "G1 BPMT-基础版"),
	G2(2, "G2 BPMT-标准版"),
	G3(3, "G3 BPMT-高级版 "),
	S(10, "S BPMT-高级开发版")

	;

	int code;
	String showName;

	private IdentifierLevel(int code, String showName) {
		this.code = code;
		this.showName = showName;
	}

	@Override
	public Object getCode() {
		return code;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.riversoft.core.db.po.Code2NameVO#getShowName()
	 */
	@Override
	public String getShowName() {
		return showName;
	}

}
