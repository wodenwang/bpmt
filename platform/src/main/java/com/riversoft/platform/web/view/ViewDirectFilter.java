/*
 * $HeadURL: $
 * $Id: $
 * Copyright (c) 2013 by Riversoft System, all rights reserved.
 */
package com.riversoft.platform.web.view;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.riversoft.core.db.ORMService;
import com.riversoft.core.web.Actions;
import com.riversoft.core.web.Actions.Keys;
import com.riversoft.core.web.Actions.Util;
import com.riversoft.platform.po.VwUrl;
import com.riversoft.weixin.common.util.URLEncoder;

/**
 * 视图转发过滤器
 * 
 * @author Woden
 * 
 */
@WebFilter("*.view")
public class ViewDirectFilter implements Filter {
	/**
	 * Logger for this class
	 */
	private static final Logger logger = LoggerFactory.getLogger(ViewDirectFilter.class);

	@Override
	public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain) throws IOException, ServletException {
		HttpServletRequest request = (HttpServletRequest) req;
		HttpServletResponse response = (HttpServletResponse) resp;
		String url = request.getServletPath();
		logger.debug("当前域路径:[" + url + "]");

		// 获取viewKey
		String viewKey = url.substring(1, url.lastIndexOf("."));

		// 查找域并跳转
		VwUrl vwUrl = (VwUrl) ORMService.getInstance().findByPk(VwUrl.class.getName(), viewKey);

		if (vwUrl == null || StringUtils.isEmpty(vwUrl.getViewClass())) {
			throw new ServletException("指定视图不存在.");
		}

		String realUrl;
		String action = Actions.Util.getActionUrl(ViewActionBuilder.getInstance().getViewClass(vwUrl.getViewClass()));
		if (vwUrl.getLoginType() == 1) {// 需要登录
			realUrl = action + "/index.shtml?" + Keys.VIEW_KEY.toString() + "=" + vwUrl.getViewKey();
		} else {
			realUrl = action + "/_index_none.shtml?" + Keys.VIEW_KEY.toString() + "=" + vwUrl.getViewKey();
		}
		// 补充store main url标识
		if (realUrl.indexOf("?") > 0) {
			realUrl += "&";
		} else {
			realUrl += "?";
		}
		realUrl += Keys.FULL_URL.toString() + "=" + URLEncoder.encode(Util.getFullURL(request));

		request.getRequestDispatcher(realUrl).forward(request, response);
	}

	@Override
	public void destroy() {
	}

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {

	}

}
