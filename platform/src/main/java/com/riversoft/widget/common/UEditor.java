/*
 * $HeadURL: $
 * $Id: $
 * Copyright (c) 2015 by Riversoft System, all rights reserved.
 */
package com.riversoft.widget.common;

import java.util.Map;

import com.riversoft.core.web.widget.FormValue;
import com.riversoft.core.web.widget.FormWidget;
import com.riversoft.core.web.widget.Widget;
import com.riversoft.core.web.widget.WidgetAnnotation;
import com.riversoft.core.web.widget.WidgetState;

/**
 * 百度UEDITOR<link>http://ueditor.baidu.com/</link>
 * 
 * @author woden
 * 
 */
@WidgetAnnotation(cmd = "ueditor", ftl = "classpath:widget/{mode}/common/ueditor.ftl")
public class UEditor implements Widget {

	private String width = "null";
	private String height = "null";

	@Override
	public void setParams(FormValue... values) {
		if (values.length > 0) {
			width = values[0].getName();
		}

		if (values.length > 1 && !"null".equalsIgnoreCase(values[1].getName())) {
			height = values[1].getName();
		}
	}

	@Override
	public void prepareMap(Map<String, Object> map) {
	}

	@Override
	public String show(Object value) {
		if (value == null) {
			return "";
		}

		return new FormWidget("ueditor[" + width + ";" + height + "]").toHtml("_tmp", WidgetState.readonly, value, null);
	}

	@Override
	public Object code(String showName) {
		return showName;
	}

}
