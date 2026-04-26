/*
 * $HeadURL: $
 * $Id: $
 * Copyright (c) 2013 by Riversoft System, all rights reserved.
 */
package com.riversoft.widget.common;

import java.util.HashMap;
import java.util.Map;

import com.riversoft.core.web.widget.FormValue;
import com.riversoft.core.web.widget.Widget;
import com.riversoft.core.web.widget.WidgetAnnotation;
import com.riversoft.platform.web.StyleManager;

/**
 * 样式设计器
 * 
 * @author woden
 * 
 */
@WidgetAnnotation(cmd = "style", ftl = "classpath:widget/{mode}/common/style.ftl")
public class Style implements Widget {

	private Map<String, String> filter = new HashMap<>();

	@Override
	public void prepareMap(Map<String, Object> map) {
		// do nothing
	}

	@Override
	public void setParams(FormValue... values) {
		if (values != null) {
			for (FormValue val : values) {
				String key = val.getName();
				String value = val.getParam();
				filter.put(key, value);
			}
		}
	}

	@Override
	public String show(Object value) {
		return new StyleManager.Style(value.toString()).toCss(filter);
	}

	@Override
	public Object code(String showName) {
		return showName;
	}

}
