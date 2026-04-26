/*
 * $HeadURL: $
 * $Id: $
 * Copyright (c) 2014 by Riversoft System, all rights reserved.
 */
package com.riversoft.core.web;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.HashMap;
import java.util.Map;

import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;

import com.riversoft.core.BeanFactory;
import com.riversoft.core.exception.ExceptionType;
import com.riversoft.core.exception.SystemRuntimeException;
import com.riversoft.core.script.ExprlangAnnotationScanner;

import freemarker.ext.jsp.TaglibFactory;
import freemarker.ext.servlet.FreemarkerServlet;
import freemarker.ext.servlet.HttpRequestHashModel;
import freemarker.ext.servlet.HttpSessionHashModel;
import freemarker.ext.servlet.ServletContextHashModel;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

/**
 * @author woden
 * 
 */
public class FreeMarkerUtils {

	/**
	 * 通用freemarker调用
	 * 
	 * @param resource
	 *            模板路径,例:classpath:/widget/a.ftl
	 * @param model
	 * @return
	 */
	public static String process(String resource, Map<String, Object> model) {
		Configuration configuration = (Configuration) BeanFactory.getInstance().getBean("ftlTemplateConfiguration");
		try {
			Map<String, Object> context = new HashMap<>();
			ExprlangAnnotationScanner exprlangAnnotationScanner = (ExprlangAnnotationScanner) BeanFactory.getInstance()
					.getBean("exprlangAnnotationScanner");
			context.putAll(exprlangAnnotationScanner.getElSupports());
			context.putAll(model);

			context.put(FreemarkerServlet.KEY_APPLICATION, new ServletContextHashModel(WebContext.getServlet(), configuration.getObjectWrapper()));
			context.put(FreemarkerServlet.KEY_REQUEST, new HttpRequestHashModel(WebContext.getRequest(), WebContext.getResponse(), configuration.getObjectWrapper()));
			context.put(FreemarkerServlet.KEY_SESSION, new HttpSessionHashModel(WebContext.getSession(), configuration.getObjectWrapper()));
			// taglib
			TaglibFactory taglibFactory = new TaglibFactory(WebContext.getServletContext());
			context.put(FreemarkerServlet.KEY_JSP_TAGLIBS, taglibFactory);
			Template template = configuration.getTemplate(resource);
			return FreeMarkerTemplateUtils.processTemplateIntoString(template, context);
		} catch (IOException | TemplateException e) {
			throw new SystemRuntimeException(ExceptionType.CODING, e);
		}
	}

	/**
	 * freemarker调用
	 * 
	 * @param name
	 * @param is
	 * @param model
	 * @return
	 */
	public static String process(String name, InputStream is, Map<String, Object> model) {
		Configuration configuration = (Configuration) BeanFactory.getInstance().getBean("ftlTemplateConfiguration");
		try (Reader reader = new InputStreamReader(is);) {
			Map<String, Object> context = new HashMap<>();
			ExprlangAnnotationScanner exprlangAnnotationScanner = (ExprlangAnnotationScanner) BeanFactory.getInstance()
					.getBean("exprlangAnnotationScanner");
			context.putAll(exprlangAnnotationScanner.getElSupports());
			context.putAll(model);

			context.put(FreemarkerServlet.KEY_APPLICATION, new ServletContextHashModel(WebContext.getServlet(), configuration.getObjectWrapper()));
			context.put(FreemarkerServlet.KEY_REQUEST, new HttpRequestHashModel(WebContext.getRequest(), WebContext.getResponse(), configuration.getObjectWrapper()));
			context.put(FreemarkerServlet.KEY_SESSION, new HttpSessionHashModel(WebContext.getSession(), configuration.getObjectWrapper()));
			// taglib
			TaglibFactory taglibFactory = new TaglibFactory(WebContext.getServletContext());
			context.put(FreemarkerServlet.KEY_JSP_TAGLIBS, taglibFactory);
			Template template = new Template(name, reader, configuration);
			return FreeMarkerTemplateUtils.processTemplateIntoString(template, context);
		} catch (IOException | TemplateException e) {
			throw new SystemRuntimeException(ExceptionType.CODING, e);
		}
	}
}
