/*
 * $HeadURL: $
 * $Id: $
 * Copyright (c) 2013 by Riversoft System, all rights reserved.
 */
package com.riversoft.platform.web;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.riversoft.core.Config;
import com.riversoft.core.context.RequestContext;
import com.riversoft.core.context.SessionContext;
import com.riversoft.core.context.VariableContext;
import com.riversoft.core.exception.ExceptionType;
import com.riversoft.core.exception.ScriptRuntimeException;
import com.riversoft.core.exception.SystemRuntimeException;
import com.riversoft.core.web.Actions;
import com.riversoft.core.web.Actions.Util;
import com.riversoft.core.web.FreeMarkerUtils;
import com.riversoft.core.web.RequestUtils;
import com.riversoft.core.web.annotation.ActionAccess;
import com.riversoft.core.web.annotation.ActionAccess.SafeRole;
import com.riversoft.platform.SessionManager;
import com.riversoft.weixin.common.exception.WxRuntimeException;

/**
 * Action拦截切面
 * 
 * @author Woden
 * 
 */
public class ActionAspect {
	/**
	 * Logger for this class
	 */
	private static final Logger logger = LoggerFactory.getLogger(ActionAspect.class);

	public Object doAround(ProceedingJoinPoint joinPoint) throws Throwable {
		Object[] args = joinPoint.getArgs();// 获取入参
		if (args == null || args.length < 2) {
			return joinPoint.proceed();// 不处理
		}

		if (!(args[0] instanceof HttpServletRequest && args[1] instanceof HttpServletResponse)) {
			return joinPoint.proceed();// 不处理
		}

		// 获取参数
		HttpServletRequest request = (HttpServletRequest) args[0];
		HttpServletResponse response = (HttpServletResponse) args[1];
		String strPathInfo = request.getServletPath();// 调用地址
		String methodName = joinPoint.getSignature().getName();// 调用方法

		initContext(request);

		// 前置验证处理
		if (args.length == 2) {
			ActionAccess access = null;
			Class<?> actionClass = joinPoint.getTarget().getClass();
			Method method = actionClass.getMethod(methodName, HttpServletRequest.class, HttpServletResponse.class);
			access = method.getAnnotation(ActionAccess.class);
			if (access == null) {// 方法无配置则使用类配置
				access = actionClass.getAnnotation(ActionAccess.class);
			}

			// 判断登陆
			if (access == null || access.login()) {// 系统用户权限登录
				if (!SessionManager.checkUserLogin() || !SessionManager.checkPlatformState()) {// 不在登陆状态,直接调整到登陆页面
					logger.debug("redirect to login page.");
					Actions.forwardAction(request, response, "/login.jsp");
					return null;
				}

				if (!SessionManager.checkAction(strPathInfo)) {
					Actions.redirectErrorPage(request, response, "权限不足，不能访问该地址。");
					return null;
				}
			} else if (!access.login()) {// 无需登录
				if (logger.isDebugEnabled()) {
					logger.debug("地址[" + strPathInfo + "]不需要判断登陆.");
				}
			} else {
				// ignore
				throw new SystemRuntimeException(ExceptionType.CODING, "模块设置有误.");
			}

			// 当前系统安全级别
			SafeRole safeRole = ActionAccess.SafeRole.forName(Config.get("safe.role", ""));
			if (access != null && !safeRole.checkLevel(access.level())) {
				if (logger.isDebugEnabled()) {
					logger.debug("当前系统[" + safeRole.getDescription() + "]不包括此Action安全级别[" + access.level().getDescription() + "],需要进行白名单验证.");
				}

				boolean whiteFlag;
				String ipRex = Config.get("safe.white.ip");
				String safeUid = Config.get("safe.white.uid");
				if (StringUtils.isEmpty(ipRex) && StringUtils.isEmpty(safeUid)) {// 没有设置白名单
					whiteFlag = false;
				} else {// 设置了白名单
					whiteFlag = true;

					if (StringUtils.isNotEmpty(ipRex)) {
						String currentIp = Util.getRealIpAddr(request);
						Pattern pattern = Pattern.compile(ipRex);
						Matcher matcher = pattern.matcher(currentIp);
						whiteFlag = matcher.matches();
					}

					if (StringUtils.isNotEmpty(safeUid) && !SessionManager.getUser().getUid().equals(safeUid)) {
						whiteFlag = false;
					}
				}

				if (!whiteFlag) {
					Actions.redirectErrorPage(request, response, "您当前客户端的安全环境无法访问此级别[" + access.level().getDescription() + "]的系统模块.");
					return null;
				}
			}

			// 验证工程管理员
			if (access != null && access.admin()) {
				if (!SessionManager.isAdmin()) {
					Actions.redirectErrorPage(request, response, "您不是工程管理员，无法使用该模块。");
					return null;
				}
			}
		}

		long begin = System.currentTimeMillis();
		try {
			// 设置LOG标识
			WebLogManager.init(request.getSession());
			try {
				// 调用
				Object result = joinPoint.proceed(args);
				return result;
			} catch (Throwable e) {// 处理系统异常
				StringBuffer jsonBuff = new StringBuffer();
				StringBuffer webBuff = new StringBuffer();
				if (e instanceof WxRuntimeException) {
					WxRuntimeException se = (WxRuntimeException) e;
					jsonBuff.append(se.getWxError() != null ? se.getWxError().getErrorMsg() : se.getMessage());
					webBuff.append("[").append(se.getCode()).append("]").append("微信端异常:").append(se.getWxError().getErrorMsg());

				} else if (e instanceof SystemRuntimeException) {
					SystemRuntimeException se = (SystemRuntimeException) e;
					if (StringUtils.isNotEmpty(se.getExtMessage())) {
						jsonBuff.append(se.getExtMessage());
					} else {
						jsonBuff.append(se.getType().getMsg());
					}
					webBuff.append("[").append(se.getType().getCode()).append("]").append(se.getType().getMsg()).append(" ").append(se.getExtMessage());

				} else {
					jsonBuff.append("系统异常");
					webBuff.append("[-1]").append("系统异常。");

				}

				// 记录loading异常
				WebLogManager.error(webBuff.toString());

				logger.error("捕捉到系统业务异常：", e);
				try {
					String html = null;
					// 详细信息
					if (e.getCause() != null) {
						Map<String, Object> model = new HashMap<>();
						model.put("msg", webBuff.toString());
						model.put("trace", StringUtils.join(ExceptionUtils.getRootCauseStackTrace(e), "\r\n"));
						model.put("title", ExceptionUtils.getRootCause(e).getMessage());
						model.put("uuid", UUID.randomUUID().toString());
						List<ScriptRuntimeException> list = new ArrayList<>();
						for (Throwable a : ExceptionUtils.getThrowableList(e)) {
							if (a instanceof ScriptRuntimeException) {// 存在脚本
								list.add((ScriptRuntimeException) a);
							}
						}
						if (list.size() > 0) {
							model.put("scripts", list);
						}
						html = FreeMarkerUtils.process("classpath:ftl/showError.ftl", model);
					}

					// 根据调用类型决定如何返回(json还是html)
					if ("json".equalsIgnoreCase(request.getParameter(Actions.Keys.DATA_TYPE.toString()))) {
						// 这一段的逻辑与ws-ajax.js里面关于json的处理呼应
						Map<String, Object> result = new HashMap<>();
						result.put("_error", true);
						if (StringUtils.isNotEmpty(html)) {// 高级页面
							result.put("html", html);
						}
						result.put("msg", jsonBuff.toString());
						response.setStatus(300);
						Actions.showJson(request, response, result);
					} else {
						if (!Util.isMobile(request) && StringUtils.isNotEmpty(html)) {// 高级页面
							Actions.showHtml(request, response, html);
						} else {// 普通页面
							if (e instanceof SystemRuntimeException) {
								SystemRuntimeException se = (SystemRuntimeException) e;
								if (se.getType() == ExceptionType.INFO) {
									Actions.redirectInfoPage(request, response, webBuff.toString());
								} else if (se.getType() == ExceptionType.WARN) {
									Actions.redirectWarningPage(request, response, webBuff.toString());
								} else {
									Actions.redirectErrorPage(request, response, webBuff.toString());
								}
							} else {
								Actions.redirectErrorPage(request, response, webBuff.toString());
							}
						}
					}

					return null;
				} catch (Exception ex) {
					throw new ServletException(e);
				}
			}
		} finally {
			long end = System.currentTimeMillis();
			// 清除LOG标识
			WebLogManager.destroy();
			if (logger.isDebugEnabled()) {
				logger.debug("Action[" + strPathInfo + "]调用耗时[" + (end - begin) + "]ms.");
			}
		}
	}

	/**
	 * 初始化request和session的localthread
	 * 
	 * @param request
	 */
	protected void initContext(HttpServletRequest request) {
		// 设置threadlocal
		// 设置request
		{
			Enumeration<String> names = request.getParameterNames();
			Map<String, Object> params = new HashMap<>();
			while (names.hasMoreElements()) {
				String name = names.nextElement();
				// logger.debug("当前表单数据[" + name + "]以设置入threadlocal.");
				params.put(name, RequestUtils.getStringValues(request, name));
			}
			// 设置_cp
			params.put(Actions.Keys.CP.toString(), new String[] { Actions.Util.getContextPath(request) });
			params.put(Actions.Keys.ACP.toString(), new String[] { Actions.Util.getActionUrl(request) });

			// 设置设备来源
			params.put(Actions.Keys.ACTION_MODE.toString(), new String[] { Actions.Util.isMobile(request) ? "h5" : "xhtml" });
			RequestContext.init(request, params);// 设置
		}

		// 设置session
		{
			HttpSession session = request.getSession();
			Enumeration<String> names = session.getAttributeNames();
			Map<String, Object> params = new HashMap<>();
			while (names.hasMoreElements()) {
				String name = names.nextElement();
				// logger.debug("当前会话数据[" + name + "]以设置入threadlocal.");
				params.put(name, session.getAttribute(name));
			}
			SessionContext.init(session, params);
		}

		// 设置variable
		{
			VariableContext.init();
		}
	}
}
