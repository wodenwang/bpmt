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
public enum WxReportLocationFlag implements Code2NameVO {

	// 企业应用是否打开地理位置上报 0：不上报；1：进入会话上报；2：持续上报
	NO(0, "不上报"), ONLY_IN_SESSION(1, "进入时上报"), ALWAYS(2, "第5秒开始上报(持续)");

	private int code;
	private String showName;

	private WxReportLocationFlag(int code, String showName) {
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

	/**
	 * 根据code获取枚举
	 * 
	 * @param code
	 * @return
	 */
	public static WxReportLocationFlag fromCode(Integer code) {
		for (WxReportLocationFlag e : values()) {
			if (e.code == code) {
				return e;
			}
		}
		return NO;
	}
}
