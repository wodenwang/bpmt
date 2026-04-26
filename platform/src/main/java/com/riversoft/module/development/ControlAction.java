/*
 * $HeadURL: $
 * $Id: $
 * Copyright (c) 2013 by Riversoft System, all rights reserved.
 */
package com.riversoft.module.development;

import static com.riversoft.core.web.Actions.includePage;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.riversoft.core.BeanFactory;
import com.riversoft.core.db.JdbcService;
import com.riversoft.core.exception.ExceptionType;
import com.riversoft.core.exception.SystemRuntimeException;
import com.riversoft.core.web.Actions;
import com.riversoft.core.web.Actions.Util;
import com.riversoft.core.web.RequestUtils;
import com.riversoft.core.web.annotation.ActionAccess;
import com.riversoft.core.web.annotation.ActionAccess.SafeLevel;
import com.riversoft.platform.script.ScriptHelper;
import com.riversoft.platform.script.ScriptTypes;

/**
 * @author Woden
 * 
 */
@ActionAccess(level = SafeLevel.DEV_W, admin = true)
public class ControlAction {

	private static Logger logger = LoggerFactory.getLogger(ControlAction.class);

	/**
	 * 开发控制台
	 * 
	 * @param request
	 * @param response
	 */
	public void index(HttpServletRequest request, HttpServletResponse response) {
		includePage(request, response, Util.getPagePath(request, "control_main.jsp"));
	}

	/**
	 * sql控制台
	 * 
	 * @param request
	 * @param response
	 */
	public void sqlPanel(HttpServletRequest request, HttpServletResponse response) {
		Actions.includePage(request, response, Util.getPagePath(request, "sql_panel.jsp"));
	}

	/**
	 * 脚本控制台
	 * 
	 * @param request
	 * @param response
	 */
	public void scriptPanel(HttpServletRequest request, HttpServletResponse response) {
		Actions.includePage(request, response, Util.getPagePath(request, "script_panel.jsp"));
	}

	/**
	 * 新开脚本变量tab
	 * 
	 * @param request
	 * @param response
	 */
	public void scriptVariableTab(HttpServletRequest request, HttpServletResponse response) {
		Actions.includePage(request, response, Util.getPagePath(request, "script_var_tab.jsp"));
	}

	/**
	 * sql执行
	 * 
	 * @param request
	 * @param response
	 */
	@SuppressWarnings("rawtypes")
	public void submitSqlPanel(HttpServletRequest request, HttpServletResponse response) {
		Integer type = RequestUtils.getIntegerValue(request, "type");
		String cmd = RequestUtils.getStringValue(request, "cmd").trim();

		if (StringUtils.isEmpty(cmd)) {
			throw new SystemRuntimeException(ExceptionType.BUSINESS, "语句为空.");
		}

		if (type == 1) {
			List<?> list = JdbcService.getInstance().querySQL(cmd);
			if (list != null && list.size() > 0) {
				request.setAttribute("fields", ((Map) list.get(0)).keySet());
			} else {
				request.setAttribute("fields", Arrays.asList("查询不到结果"));
			}
			request.setAttribute("list", list);
			Actions.includePage(request, response, Util.getPagePath(request, "sql_list.jsp"));
		} else if (type == 2) {
			ControlService service = BeanFactory.getInstance().getBean(ControlService.class);
			String result = "";
			try {
				service.executeSQL(cmd);
			} catch (SandboxException e) {
				result = (String) e.getResult();
			}
			Actions.redirectInfoPage(request, response, result);
		} else {
			throw new SystemRuntimeException(ExceptionType.BUSINESS, "不支持的类型.");
		}
	}

	/**
	 * 真实执行SQL脚本
	 * 
	 * @param request
	 * @param response
	 */
	@ActionAccess(level = SafeLevel.DEV_SPC, admin = true)
	public void submitSqlPanelReal(HttpServletRequest request, HttpServletResponse response) {
		String cmd = RequestUtils.getStringValue(request, "cmd").trim();
		if (StringUtils.isEmpty(cmd)) {
			throw new SystemRuntimeException(ExceptionType.BUSINESS, "语句为空.");
		}

		JdbcService.getInstance().executeSQL(cmd);
		Actions.redirectInfoPage(request, response, "执行SQL成功.");
	}

	/**
	 * 执行脚本
	 * 
	 * @param request
	 * @param response
	 */
	public void submitScript(HttpServletRequest request, HttpServletResponse response) {

		Map<String, Object> context = new HashMap<String, Object>();
		String[] vars = RequestUtils.getStringValues(request, "vars");
		if (vars != null) {
			for (String pixel : vars) {
				String name = RequestUtils.getStringValue(request, pixel + ".name");
				Integer type = RequestUtils.getIntegerValue(request, pixel + ".type");
				String script = RequestUtils.getStringValue(request, pixel + ".script");
				logger.debug("上下文:[" + name + "]");
				Object value = ScriptHelper.evel(ScriptTypes.forCode(type), script);
				logger.debug("上下文[" + name + "]执行结果:" + value);
				context.put(name, value);
			}
		}

		String script = RequestUtils.getStringValue(request, "script");
		Integer type = RequestUtils.getIntegerValue(request, "type");
		ControlService service = BeanFactory.getInstance().getBean(ControlService.class);
		Object result = null;
		try {
			service.executeScript(type, script, context);
		} catch (SandboxException e) {
			result = e.getResult();
		}
		logger.debug("脚本执行结果:" + result);
		request.setAttribute("result", result);
		Actions.includePage(request, response, Util.getPagePath(request, "script_list.jsp"));
	}

	/**
	 * 真是执行脚本
	 * 
	 * @param request
	 * @param response
	 */
	@ActionAccess(level = SafeLevel.DEV_SPC, admin = true)
	public void submitScriptReal(HttpServletRequest request, HttpServletResponse response) {
		Map<String, Object> context = new HashMap<String, Object>();
		String[] vars = RequestUtils.getStringValues(request, "vars");
		if (vars != null) {
			for (String pixel : vars) {
				String name = RequestUtils.getStringValue(request, pixel + ".name");
				Integer type = RequestUtils.getIntegerValue(request, pixel + ".type");
				String script = RequestUtils.getStringValue(request, pixel + ".script");
				logger.debug("上下文:[" + name + "]");
				Object value = ScriptHelper.evel(ScriptTypes.forCode(type), script);
				logger.debug("上下文[" + name + "]执行结果:" + value);
				context.put(name, value);
			}
		}

		String script = RequestUtils.getStringValue(request, "script");
		Integer type = RequestUtils.getIntegerValue(request, "type");
		ControlService service = BeanFactory.getInstance().getBean(ControlService.class);

		Object result = service.executeScriptReal(type, script, context);
		logger.debug("脚本执行结果:" + result);
		request.setAttribute("result", result);
		Actions.includePage(request, response, Util.getPagePath(request, "script_list.jsp"));
	}
}
