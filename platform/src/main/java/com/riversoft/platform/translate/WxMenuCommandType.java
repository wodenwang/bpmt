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
public enum WxMenuCommandType implements Code2NameVO {

	click(10, "仅推送事件"), scancode_push(21, "扫码推"), scancode_waitmsg(20, "弹窗扫码"), pic_sysphoto(31, "拍照"), pic_photo_or_album(30, "拍照或相片选择"), pic_weixin(32, "相片选择"), location_select(40, "地理位置选择")

	;
	private int code;
	private String showName;

	WxMenuCommandType(int code, String showName) {
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
	 * 转换成wx-sdk的枚举
	 * 
	 * @param code
	 * @return
	 */
	public static WxMenuCommandType fromCode(int code) {

		for (WxMenuCommandType type : values()) {
			if (type.code == code) {
				return type;
			}
		}
		return null;
	}

}
