/*
 * $HeadURL: $
 * $Id: $
 * Copyright (c) 2013 by Riversoft System, all rights reserved.
 */
package com.riversoft.core.web.widget;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Woden
 * 
 */
@SuppressWarnings("serial")
public class WidgetTag extends TagSupport {
	/**
	 * Logger for this class
	 */
	private static final Logger logger = LoggerFactory.getLogger(WidgetTag.class);

	private String cmd;
	private String name;
	private Object value;
	private String state;// 控件状态
	private String params;// 动态入参
	private String actionMode;// 对应的actionMode

	/**
	 * @param cmd
	 *            the cmd to set
	 */
	public void setCmd(String cmd) {
		this.cmd = cmd;
	}

	/**
	 * @param name
	 *            the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @param value
	 *            the value to set
	 */
	public void setValue(Object value) {
		this.value = value;
	}

	public void setState(String state) {
		this.state = state;
	}

	public void setParams(String params) {
		this.params = params;
	}

	/**
	 * @param actionMode
	 *            the actionMode to set
	 */
	public void setActionMode(String actionMode) {
		this.actionMode = actionMode;
	}

	public int doStartTag() throws JspException {
		try {
			FormWidget formWidget = new FormWidget(cmd);
			WidgetState widgetState;
			try {
				if (!StringUtils.isEmpty(state)) {
					widgetState = WidgetState.valueOf(state);
				} else {
					widgetState = WidgetState.normal;
				}
			} catch (Throwable e) {
				widgetState = WidgetState.normal;
			}

			// 设置动态参数
			if (StringUtils.isNotEmpty(params)) {
				formWidget.setDyncParams(params);
			}

			pageContext.getOut().append(formWidget.toHtml(name, widgetState, value, actionMode));
		} catch (Exception e) {
			logger.error("打印出错。", e);
			return EVAL_BODY_INCLUDE;
		}
		return SKIP_BODY;
	}
}
