/*
 * $HeadURL: $
 * $Id: $
 * Copyright (c) 2014 by Riversoft System, all rights reserved.
 */
package com.riversoft.platform.web.widget;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.riversoft.core.db.DataPO;

/**
 * 数据控件配置
 * 
 * @author woden
 * 
 */
public abstract class BaseWidgetConfigHandler {

	/**
	 * 表单页
	 * 
	 * @param request
	 * @param response
	 * @param key
	 */
	public abstract void form(HttpServletRequest request, HttpServletResponse response, String key);

	/**
	 * 保存
	 * 
	 * @param key
	 * @param po
	 */
	public abstract void saveOrUpdate(String key, DataPO po);

	/**
	 * 删除
	 * 
	 * @param key
	 */
	public abstract void remove(String key);

}
