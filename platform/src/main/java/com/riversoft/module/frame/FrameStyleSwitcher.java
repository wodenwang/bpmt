/*
 * $HeadURL: $
 * $Id: $
 * Copyright (c) 2016 by Riversoft System, all rights reserved.
 */
package com.riversoft.module.frame;

import org.apache.commons.lang3.StringUtils;

import com.riversoft.core.Config;

/**
 * 新旧模板切换开关
 * 
 * @author woden
 *
 */
class FrameStyleSwitcher {

	/**
	 * 判断是否使用新模板
	 * 
	 * @return
	 */
	protected static boolean isNewFrame() {
		return StringUtils.equals("true", Config.get("page.frame.new"));
	}

	/**
	 * 框架页
	 * 
	 * @return
	 */
	protected static String getFrame() {
		if (isNewFrame()) {
			return "/frame_new/frame.jsp";
		}

		return "/frame/frame.jsp";
	}

	/**
	 * 菜单页
	 * 
	 * @return
	 */
	protected static String getMenu() {
		if (isNewFrame()) {
			return "/frame_new/menu.jsp";
		}

		return "/frame/menu.jsp";
	}

	/**
	 * 面板页
	 * 
	 * @return
	 */
	protected static String getPanel() {
		if (isNewFrame()) {
			return "/frame_new/panel.jsp";
		}
		return "/frame/panel.jsp";
	}

	/**
	 * 登录页
	 * 
	 * @return
	 */
	protected static String getLogin() {
		if (isNewFrame()) {
			return "/frame_new/login.jsp";
		}
		return "/frame/login.jsp";
	}

	/**
	 * 默认logo图片URL
	 * 
	 * @return
	 */
	protected static String getLogoUrl() {
		if (isNewFrame()) {
			return "/xhtml/frame_new/images/logo.png";
		}
		return "/css/images/logo.png";
	}

}
