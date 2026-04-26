/*
 * $HeadURL: $
 * $Id: $
 * Copyright (c) 2013 by Riversoft System, all rights reserved.
 */
package com.riversoft.platform.web.view;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.riversoft.core.db.ORMService;
import com.riversoft.core.exception.ExceptionType;
import com.riversoft.core.exception.SystemRuntimeException;
import com.riversoft.core.web.Actions.Keys;
import com.riversoft.core.web.RequestUtils;
import com.riversoft.core.web.annotation.ActionAccess;
import com.riversoft.core.web.annotation.ActionMode;
import com.riversoft.core.web.annotation.ActionMode.Mode;
import com.riversoft.platform.po.VwUrl;
import com.riversoft.platform.web.view.annotation.View.LoginType;

/**
 * 动态表框架基类<br>
 * 注意:此类复合了action和service的功能,模块的action必须继承此类并实现相应方法
 * 
 * @author Woden
 * 
 */
public abstract class BaseDynamicViewAction {

	/**
	 * 首页,从过滤器引导而来<br>
	 * 默认,通过用户登录
	 * 
	 * @param request
	 * @param response
	 */
	@ActionMode(Mode.FIT)
	public void index(HttpServletRequest request, HttpServletResponse response) {
		String key = RequestUtils.getStringValue(request, Keys.VIEW_KEY.toString());
		main(request, response, key);
	}

	/**
	 * 首页<br>
	 * 无需登陆访问
	 * 
	 * @param request
	 * @param response
	 */
	@ActionAccess(login = false)
	@ActionMode(Mode.FIT)
	public void _index_none(HttpServletRequest request, HttpServletResponse response) {
		String key = RequestUtils.getStringValue(request, Keys.VIEW_KEY.toString()); // 再次鉴权,防止仿冒
		VwUrl vwUrl = (VwUrl) ORMService.getInstance().findByPk(VwUrl.class.getName(), key);
		if (LoginType.NONE.getCode() != vwUrl.getLoginType()) {
			throw new SystemRuntimeException(ExceptionType.BUSINESS_PRIVILAGE, "非法登录系统.请重新登录.");
		}
		main(request, response, key);
	}

	/**
	 * 配置页面表单
	 * 
	 * @param request
	 * @param response
	 */
	public void configForm(HttpServletRequest request, HttpServletResponse response) {
		String key = RequestUtils.getStringValue(request, "key");
		configForm(request, response, key);
	}

	// 以下为action
	/**
	 * 访问动态视图首页,模块必须重写
	 * 
	 * @param request
	 * @param response
	 * @param key
	 */
	protected abstract void main(HttpServletRequest request, HttpServletResponse response, String key);

	/**
	 * 配置界面表单
	 * 
	 * @param request
	 * @param response
	 * @param key
	 *            null表示当前是新增状态
	 */
	public abstract void configForm(HttpServletRequest request, HttpServletResponse response, String key);

	// 以下为service
	/**
	 * 保存配置
	 * 
	 * @param key
	 */
	public abstract void saveConfig(String key);

	/**
	 * 更新配置
	 * 
	 * @param key
	 */
	public abstract void updateConfig(String key);

	/**
	 * 删除配置
	 * 
	 * @param key
	 */
	public abstract void removeConfig(String key);

	/**
	 * 视图复制
	 * 
	 * @param key
	 * @return
	 */
	public abstract String copyConfig(String key);
}
