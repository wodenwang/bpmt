/*
 * $HeadURL: $
 * $Id: $
 * Copyright (c) 2013 by Riversoft System, all rights reserved.
 */
package com.riversoft.platform.web;

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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.riversoft.core.web.Actions.Keys;
import com.riversoft.core.web.Actions.Util;
import com.riversoft.weixin.common.util.URLEncoder;

/**
 * 域转发过滤器
 * 
 * @author Woden
 * 
 */
@WebFilter("*.xhtml")
public class DomainDirectFilter implements Filter {
	/**
	 * Logger for this class
	 */
	private static final Logger logger = LoggerFactory.getLogger(DomainDirectFilter.class);

	@Override
	public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain) throws IOException, ServletException {
		HttpServletRequest request = (HttpServletRequest) req;
		HttpServletResponse response = (HttpServletResponse) resp;
		String url = request.getServletPath();
		logger.debug("当前域路径:[" + url + "]");

		String domainKey;
		String menuKey;
		// 处理URL
		int separator;
		if ((separator = url.indexOf("/", 1)) > 0) {// 两端式,说明带了menu
			domainKey = url.substring(1, separator);
			menuKey = url.substring(separator + 1, url.lastIndexOf("."));
		} else {// 一段式,只有domainKey
			domainKey = url.substring(1, url.lastIndexOf("."));
			menuKey = "";
		}

		request.getRequestDispatcher("/frame/FrameAction/domain.shtml?domain=" + domainKey + "&menu=" + menuKey + "&" + Keys.FULL_URL.toString() + "=" + URLEncoder.encode(Util.getFullURL(request)))
				.forward(request,
						response);
	}

	@Override
	public void destroy() {
	}

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {

	}

}
