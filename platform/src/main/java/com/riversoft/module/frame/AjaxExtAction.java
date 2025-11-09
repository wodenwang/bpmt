/*
 * $HeadURL: $
 * $Id: $
 * Copyright (c) 2014 by Riversoft System, all rights reserved.
 */
package com.riversoft.module.frame;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.riversoft.core.db.po.Code2NameVO;
import com.riversoft.core.exception.SystemRuntimeException;
import com.riversoft.core.web.Actions;
import com.riversoft.core.web.RequestUtils;
import com.riversoft.core.web.annotation.ActionAccess;
import com.riversoft.platform.SessionManager;
import com.riversoft.platform.db.BaseDataService;
import com.riversoft.platform.db.Code2NameService;
import com.riversoft.platform.script.ScriptHelper;
import com.riversoft.platform.script.ScriptTypes;
import com.riversoft.platform.web.WebLogManager;
import com.riversoft.util.ReflectionUtils;

/**
 * 客户端脚本封装调用的服务
 * 
 * @author woden
 * 
 */
public class AjaxExtAction {

	/**
	 * 获取LOG
	 * 
	 * @param request
	 * @param response
	 */
	@ActionAccess(login = false)
	public void sessionLog(HttpServletRequest request, HttpServletResponse response) {
		Object logVO = WebLogManager.getLog(RequestUtils.getStringValue(request, "random"));
		Actions.showJson(request, response, logVO);
	}

	/**
	 * 异步加载下拉框
	 * 
	 * @param request
	 * @param response
	 */
	public void code2name(HttpServletRequest request, HttpServletResponse response) {
		String type = RequestUtils.getStringValue(request, "type");
		String code = RequestUtils.getStringValue(request, "code");
		String name = RequestUtils.getStringValue(request, "name");
		String condition = RequestUtils.getStringValue(request, "condition");

		List<Code2NameVO> list = new ArrayList<>();
		if (type.startsWith("$")) {// ORM模式
			String entity = type.substring(1);
			list.addAll(Code2NameService.getInstance().getListORM(entity, code, name, condition));
		} else if (type.startsWith("#")) {// JDBC模式
			String table = type.substring(1);
			list.addAll(Code2NameService.getInstance().getListJDBC(table, code, name, condition));
		} else if (type.startsWith("@")) {// 枚举翻译
			String className = type.substring(1);
			Class<?> klass;
			try {
				klass = Class.forName(className);
			} catch (ClassNotFoundException e) {
				throw new SystemRuntimeException("枚举类[" + className + "]不存在。", e);
			}
			Code2NameVO[] vos = (Code2NameVO[]) ReflectionUtils.getMethodValue(klass, klass, "values", new Class[] {}, new Object[] {});
			list.addAll(Arrays.asList(vos));
		} else {
			list.addAll(BaseDataService.getInstance().getList(type, condition));
		}
		Actions.showJson(request, response, list);
	}

	/**
	 * 调用自定义函数
	 * 
	 * @param request
	 * @param response
	 */
	public void function(HttpServletRequest request, HttpServletResponse response) {
		String fn = RequestUtils.getStringValue(request, "fn");
		HashMap<String, Object> arg = RequestUtils.getJsonValue(request, "arg");
		Map<String, Object> context = new HashMap<String, Object>();
		context.put("arg", arg);
		Object result = ScriptHelper.evel(ScriptTypes.GROOVY, "return cm.invoke('" + fn + "',arg);", context);
		Actions.showJson(request, response, result);
	}

	/**
	 * 校验用户密码
	 * 
	 * @param request
	 * @param response
	 */
	public void checkPassword(HttpServletRequest request, HttpServletResponse response) {
		Map<String, Object> result = new HashMap<>();
		result.put("flag", SessionManager.checkUserPassword(RequestUtils.getStringValue(request, "password")));
		Actions.showJson(request, response, result);
	}

	/**
	 * 展示html内容
	 * 
	 * @param request
	 * @param response
	 */
	@ActionAccess(login = false)
	public void html(HttpServletRequest request, HttpServletResponse response) {
		Actions.showHtml(request, response, RequestUtils.getStringValue(request, "html"));
	}
}
