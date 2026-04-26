package com.riversoft.platform.translate;

import com.riversoft.core.db.po.Code2NameVO;

/**
 * @borball on 2/25/2016.
 */
public enum WxType implements Code2NameVO {

	QY(0, "企业号"),
	MP(1, "公众号");

	private int code;
	private String showName;

	WxType(int code, String showName) {
		this.code = code;
		this.showName = showName;
	}

	@Override
	public Object getCode() {
		return code;
	}

	@Override
	public String getShowName() {
		return showName;
	}
}
