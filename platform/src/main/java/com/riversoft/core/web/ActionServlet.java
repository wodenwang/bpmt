/*
 * File Name  :ActionServlet.java
 * Create Date:2012-11-6 上午12:10:36
 * Author     :woden
 */

package com.riversoft.core.web;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.riversoft.core.BeanFactory;
import com.riversoft.core.Config;

/**
 * 基础Action处理框架.
 * 
 */
@SuppressWarnings("serial")
@MultipartConfig
public final class ActionServlet extends HttpServlet {
	private static final Logger logger = LoggerFactory.getLogger(ActionServlet.class);

	public ActionServlet() {
		super();
	}

	public void init() throws ServletException {
		super.init();
		WebContext.initServlet(this);
	}

	public void destroy() {
		super.destroy();
	}

	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		service(request, response);
	}

	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		service(request, response);
	}

	public void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		// 保存web上下文
		WebContext.init(request, response);

		initAttributes(request);
		Class<?> clazz = Actions.Util.getActionClass(request);
		if (clazz == null) {
			String strErrMsg = "类不存在.";
			logger.error(strErrMsg);
			throw new ServletException(strErrMsg);
		}

		Method method = Actions.Util.getActionMethod(request, clazz);
		if (method == null) {
			String strErrMsg = "方法不存在.";
			logger.error(strErrMsg);
			throw new ServletException(strErrMsg);
		}

		try {
			// 从spring工厂生成实例,action是无状态的，所以一个实例即可
			Object obj = BeanFactory.getInstance().getSingleBean(clazz);
			method.invoke(obj, new Object[] { request, response });
		} catch (Throwable e) {
			String strErrMsg = ExceptionUtils.getRootCauseMessage(e) + ".";
			logger.error("未知异常", e);
			try {
				// 根据调用类型决定如何返回(json还是html)
				if ("json".equalsIgnoreCase(request.getParameter(Actions.Keys.DATA_TYPE.toString()))) {
					// 这一段的逻辑与ws-ajax.js里面关于json的处理呼应
					Map<String, Object> result = new HashMap<>();
					result.put("_error", true);
					result.put("msg", strErrMsg);
					response.setStatus(300);
					Actions.showJson(request, response, result);
				} else {
					Actions.redirectErrorPage(request, response, strErrMsg);
				}
			} catch (Exception ex) {
				throw new ServletException(e);
			}
		} finally {
			WebContext.destroy(request);
		}
	}

	/**
	 * 设置规范参数
	 * 
	 * @param request
	 */
	private void initAttributes(HttpServletRequest request) {

		// 获取zoneid
		String zoneId = RequestUtils.getStringValue(request, Actions.Keys.ZONE.toString());
		if (StringUtils.isEmpty(zoneId)) {
			zoneId = "_body";
		}
		request.setAttribute(Actions.Keys.ZONE.toString(), zoneId);
		request.setAttribute(Actions.Keys.FORM.toString(), RequestUtils.getStringValue(request, Actions.Keys.FORM.toString()));

		String params = RequestUtils.getStringValue(request, Actions.Keys.PARAMS.toString());
		if (StringUtils.isNotEmpty(params)) {
			params = params.replaceAll("\\\"", "\\\'");
			request.setAttribute(Actions.Keys.PARAMS.toString(), params);
		}

		// 设置根路径
		request.setAttribute(Actions.Keys.CP.toString(), Actions.Util.getContextPath(request));

		// 设置当前时间
		request.setAttribute(Actions.Keys.NOW.toString(), new Date());

		// 设置当前action url
		request.setAttribute(Actions.Keys.ACTION.toString(), Actions.Util.getActionUrl(request));
		request.setAttribute(Actions.Keys.CUR_URL.toString(), request.getServletPath());
		request.setAttribute(Actions.Keys.ACP.toString(), request.getAttribute(Actions.Keys.CP.toString()).toString() + request.getAttribute(Actions.Keys.ACTION.toString()).toString());

		// 当前网址
		request.setAttribute(Actions.Keys.FULL_URL.toString(), Actions.Util.getFullURL(request));

		// 设置样式
		request.setAttribute(Actions.Keys.ICO.toString(), Config.get("page.ico.url", "/css/images/favicon.ico"));
		request.setAttribute(Actions.Keys.STYLE.toString(), Config.get("page.theme"));
		request.setAttribute(Actions.Keys.EXT_STYLE.toString(), Config.get("page.theme.ext"));
		request.setAttribute(Actions.Keys.BACKGROUD_STYLE.toString(), Config.getChinese("page.theme.backgroud", "0"));
		request.setAttribute(Actions.Keys.TITLE.toString(), Config.getChinese("page.title", ""));

		// 分页和排序
		request.setAttribute(Actions.Keys.LIMIT.toString(), Actions.Util.getLimit(request));
		request.setAttribute(Actions.Keys.PAGE.toString(), RequestUtils.getStringValue(request, Actions.Keys.PAGE.toString()));
		request.setAttribute(Actions.Keys.FIELD.toString(), RequestUtils.getStringValue(request, Actions.Keys.FIELD.toString()));
		request.setAttribute(Actions.Keys.DIR.toString(), RequestUtils.getStringValue(request, Actions.Keys.DIR.toString()));
	}
}
