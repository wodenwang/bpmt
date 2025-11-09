/*
 * $HeadURL: $
 * $Id: $
 * Copyright (c) 2015 by Riversoft System, all rights reserved.
 */
package com.riversoft.platform.translate;

import com.riversoft.core.db.po.Code2NameVO;

/**
 * 回调模式接收到的数据类型
 * 
 * @author woden
 *
 */
public enum WxSendDataType implements Code2NameVO {
	// TEXT;IMAGE;VOICE;VIDEO;SHORT_VIDEO;LOCATION;LINK
	TEXT("文字"), IMAGE("图片"), VOICE("语音"), VIDEO("视频"), SHORT_VIDEO("微信小视屏"), LOCATION("地理信息"), LINK("链接消息");

	private String showName;

	WxSendDataType(String showName) {
		this.showName = showName;
	}

	@Override
	public Object getCode() {
		return name();
	}

	@Override
	public String getShowName() {
		return showName;
	}

}
