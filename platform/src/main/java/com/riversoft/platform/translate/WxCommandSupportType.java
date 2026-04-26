/*
 * $HeadURL: $
 * $Id: $
 * Copyright (c) 2015 by Riversoft System, all rights reserved.
 */
package com.riversoft.platform.translate;

import com.riversoft.core.db.po.Code2NameVO;

/**
 * 
 * @author woden
 *
 */
public enum WxCommandSupportType implements Code2NameVO {
	// 支持类型,多选.1:菜单事件;2:对话框(文字);3:地理位置自动上报;4:用户进入应用;5:用户状态变更;
	MENU(1, 0, "菜单"),
	MESSAGE(2, 0, "对话框消息"),
	LOCATION(3, 0, "位置自动上报"),
	SUBSCRIBE(4, 0, "新关注"),
	UNSUBSCRIBE(5, 0, "取消关注"),
	SCAN(6, -1, "已关注用户扫码进入(公众号)"),
	ENTER(7, 1, "进入应用(企业号)"),
	ORDER(8, -1, "小店支付(公众号)"),
	PAYNOTIFY(9, -1, "微信支付结果");

	private int code;
	private int type;// <=0公众号;>=0企业号
	private String showName;

	WxCommandSupportType(int code, int type, String showName) {
		this.code = code;
		this.type = type;
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

	public int getType() {
		return type;
	}

	@Override
	public String toString() {
		return showName;
	}
}
