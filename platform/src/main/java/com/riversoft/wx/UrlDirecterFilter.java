/*
 * $HeadURL: $
 * $Id: $
 * Copyright (c) 2015 by Riversoft System, all rights reserved.
 */
package com.riversoft.wx;

import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.riversoft.core.context.RequestContext;
import com.riversoft.core.context.SessionContext;
import com.riversoft.core.db.ORMService;
import com.riversoft.core.exception.SystemRuntimeException;
import com.riversoft.core.web.Actions;
import com.riversoft.core.web.Actions.Keys;
import com.riversoft.core.web.Actions.Util;
import com.riversoft.core.web.RequestUtils;
import com.riversoft.platform.SessionManager.SessionAttributeKey;
import com.riversoft.platform.po.VwUrl;
import com.riversoft.platform.script.ScriptHelper;
import com.riversoft.platform.script.ScriptTypes;
import com.riversoft.platform.web.view.ViewActionBuilder;
import com.riversoft.weixin.common.exception.WxRuntimeException;
import com.riversoft.weixin.common.util.URLEncoder;

/**
 * 微信网址转发(根据配置添加动态入参,或者转换复杂网址)
 *
 * @author woden
 */
@WebFilter("/wx/url/*")
public class UrlDirecterFilter implements Filter {

	private static final Logger logger = LoggerFactory.getLogger(UrlDirecterFilter.class);

	@SuppressWarnings("unchecked")
	@Override
	public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
		HttpServletRequest request = (HttpServletRequest) req;
		HttpServletResponse response = (HttpServletResponse) res;
		String url = request.getServletPath();
		logger.debug("当前域路径:[" + url + "]");

		// 获取viewKey
		String urlKey = url.substring(url.lastIndexOf("/") + 1);

		Map<String, Object> menu;
		boolean isQy = isQyRequest(urlKey);
		boolean isMp = isMpRequest(urlKey);
		if (isQy) {
			menu = (Map<String, Object>) ORMService.getInstance().findByPk("WxAgentMenu", getMenuKey(urlKey));
		} else if (isMp) {
			// 非微信,拒绝访问
			if (!Util.fromWx(request)) {
				// 考虑更友好点?
				throw new ServletException("URL[" + url + "]只能通过微信客户端登录.");
			}
			menu = (Map<String, Object>) ORMService.getInstance().findByPk("WxMpMenu", getMenuKey(urlKey));
		} else {// 默认从url配置表里面找
			menu = (Map<String, Object>) ORMService.getInstance().findByPk("WxUrl", urlKey);
		}

		if (menu == null) {
			Actions.redirectErrorPage(request, response, "URL[" + url + "]不存在.");
			return;
		}

		String action = (String) menu.get("action");
		if (StringUtils.isEmpty(action)) {
			Actions.redirectErrorPage(request, response, "URL[" + url + "]不存在.");
			return;
		}

		String params = null;
		String paramScript = (String) menu.get("paramScript");
		if (StringUtils.isNotEmpty(paramScript)) {
			// 设置request
			{
				Enumeration<String> names = request.getParameterNames();
				Map<String, Object> map = new HashMap<>();
				while (names.hasMoreElements()) {
					String name = names.nextElement();
					// logger.debug("当前表单数据[" + name + "]以设置入threadlocal.");
					map.put(name, RequestUtils.getStringValues(request, name));
				}
				RequestContext.init(map);// 设置
			}
			// 设置session
			{
				HttpSession session = request.getSession();
				Enumeration<String> names = session.getAttributeNames();
				Map<String, Object> map = new HashMap<>();
				while (names.hasMoreElements()) {
					String name = names.nextElement();
					// logger.debug("当前会话数据[" + name + "]以设置入threadlocal.");
					map.put(name, session.getAttribute(name));
				}
				SessionContext.init(map);
			}

			try {
				params = (String) ScriptHelper.evel(ScriptTypes.forCode((Integer) menu.get("paramType")), paramScript);
			} catch (Exception e) {// 脚本出错
				StringBuffer buff = new StringBuffer();
				if (e instanceof WxRuntimeException) {
					WxRuntimeException se = (WxRuntimeException) e;
					buff.append("[").append(se.getCode()).append("]").append("微信端异常:").append(se.getWxError().getErrorMsg());
				} else if (e instanceof SystemRuntimeException) {
					SystemRuntimeException se = (SystemRuntimeException) e;
					buff.append("[").append(se.getType().getCode()).append("]").append(se.getType().getMsg()).append(" ").append(se.getExtMessage());
				} else {
					buff.append("[-1]").append("系统异常。");
				}
				Actions.redirectErrorPage(request, response, buff.toString());
				return;
			}
		}

		if (isQy) {
			String agentKey = (String) menu.get("agentKey");
			request.getSession().setAttribute(SessionAttributeKey.WX_TYPE.name(), "agent");
			request.getSession().setAttribute(SessionAttributeKey.WX_KEY.name(), agentKey);
		} else if (isMp) {
			String mpKey = (String) menu.get("mpKey");
			request.getSession().setAttribute(SessionAttributeKey.WX_TYPE.name(), "mp");
			request.getSession().setAttribute(SessionAttributeKey.WX_KEY.name(), mpKey);
		} else {
			Integer wxType = (Integer) menu.get("wxType");// mp或者agent
			// 非微信,不需要走此过滤器
			if (!Util.fromWx(request) && wxType == 1) {
				throw new ServletException("URL[" + url + "]只能通过微信客户端登录.");
			}
			String wxKey = (String) menu.get("wxKey");// mpKey或者空
			String wxScope = (String) menu.get("wxScope");
			request.getSession().setAttribute(SessionAttributeKey.WX_TYPE.name(), wxType == 1 ? "mp" : "agent");
			request.getSession().setAttribute(SessionAttributeKey.WX_KEY.name(), wxKey);
			request.getSession().setAttribute(SessionAttributeKey.WX_SCOPE.name(), wxScope);
		}

		String realUrl;
		if (StringUtils.endsWith(action, ".view")) {
			String viewKey = action.substring(1, action.lastIndexOf("."));
			// 查找域并跳转
			VwUrl vwUrl = (VwUrl) ORMService.getInstance().findByPk(VwUrl.class.getName(), viewKey);

			if (vwUrl == null || StringUtils.isEmpty(vwUrl.getViewClass())) {
				throw new ServletException("指定视图不存在.");
			}

			if (vwUrl.getLoginType() == 1) {// 需要登录
				realUrl = Actions.Util.getActionUrl(ViewActionBuilder.getInstance().getViewClass(vwUrl.getViewClass()))
						+ "/index.shtml?" + Keys.VIEW_KEY.toString() + "=" + vwUrl.getViewKey();
			} else {
				realUrl = Actions.Util.getActionUrl(ViewActionBuilder.getInstance().getViewClass(vwUrl.getViewClass())) + "/_index_none.shtml?" + Keys.VIEW_KEY.toString() + "=" + vwUrl.getViewKey();
			}
		} else {
			realUrl = action;
		}

		// 补充store main url标识
		if (realUrl.indexOf("?") > 0) {
			realUrl += "&";
		} else {
			realUrl += "?";
		}
		// 全量网址
		realUrl += Keys.FULL_URL.toString() + "=" + URLEncoder.encode(Util.getFullURL(request));
		realUrl += "&" + Keys.FULL_URL.toString() + "_flag" + "=1";// _full_url_flag=1

		// params
		if (StringUtils.isNotEmpty(params)) {
			realUrl += "&" + Keys.PARAMS.toString() + "=" + params;
		}

		// 获取viewKey
		request.getRequestDispatcher(realUrl).forward(request, response);
	}

	@Override
	public void destroy() {
	}

	@Override
	public void init(FilterConfig config) throws ServletException {
	}

	private boolean isQyRequest(String viewKey) {
		return viewKey.startsWith("qy_");
	}

	private boolean isMpRequest(String viewKey) {
		return viewKey.startsWith("mp_");
	}

	private String getMenuKey(String viewKey) {
		return viewKey.substring(3);
	}
}
