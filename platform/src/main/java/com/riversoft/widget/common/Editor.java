/*
 * $HeadURL: $
 * $Id: $
 * Copyright (c) 2014 by Riversoft System, all rights reserved.
 */
package com.riversoft.widget.common;

import java.util.Map;

import com.riversoft.core.web.widget.FormValue;
import com.riversoft.core.web.widget.FormWidget;
import com.riversoft.core.web.widget.Widget;
import com.riversoft.core.web.widget.WidgetAnnotation;
import com.riversoft.core.web.widget.WidgetState;

/**
 * @author woden
 * 
 */
@WidgetAnnotation(cmd = "editor", ftl = "classpath:widget/{mode}/common/editor.ftl")
public class Editor implements Widget {

	@Override
	public void prepareMap(Map<String, Object> map) {
	}

	@Override
	public void setParams(FormValue... values) {
	}

	@Override
	public String show(Object value) {
		if (value == null) {
			return "";
		}

		return new FormWidget("editor").toHtml("_tmp", WidgetState.readonly, value, null);
	}

	@Override
	public Object code(String showName) {
		return showName;
	}

}
