/*
 * $HeadURL: $
 * $Id: $
 * Copyright (c) 2016 by Riversoft System, all rights reserved.
 */
package com.riversoft.core.web;

import javax.servlet.GenericServlet;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * web上下文
 * 
 * @author woden
 *
 */
public class WebContext {

	private static ThreadLocal<HttpServletRequest> requestLocal = new ThreadLocal<>();
	private static ThreadLocal<HttpServletResponse> responseLocal = new ThreadLocal<>();

	private static GenericServlet servlet;

	public static void initServlet(GenericServlet servlet) {
		WebContext.servlet = servlet;
	}

	public static void init(HttpServletRequest request, HttpServletResponse response) {
		requestLocal.remove();
		requestLocal.set(request);
		responseLocal.remove();
		responseLocal.set(response);
	}

	public static void destroy(HttpServletRequest request) {
		requestLocal.remove();
		responseLocal.remove();
	}

	public static GenericServlet getServlet() {
		return servlet;
	}

	public static HttpServletResponse getResponse() {
		return responseLocal.get();
	}

	public static HttpServletRequest getRequest() {
		return requestLocal.get();
	}

	public static HttpSession getSession() {
		return requestLocal.get().getSession();
	}

	public static ServletContext getServletContext() {
		return requestLocal.get().getServletContext();
	}

}
