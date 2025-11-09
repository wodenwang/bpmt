/*
 * $HeadURL: $
 * $Id: $
 * Copyright (c) 2014 by Riversoft System, all rights reserved.
 */
package com.riversoft.widget.custom;

import java.util.Map;

import com.riversoft.core.context.RequestContext;
import com.riversoft.core.exception.ExceptionType;
import com.riversoft.core.exception.SystemRuntimeException;
import com.riversoft.core.web.Actions.Keys;
import com.riversoft.core.web.widget.FormValue;
import com.riversoft.core.web.widget.FormWidget;
import com.riversoft.core.web.widget.Widget;
import com.riversoft.core.web.widget.WidgetAnnotation;
import com.riversoft.core.web.widget.WidgetState;
import com.riversoft.util.jackson.JsonMapper;

/**
 * 明细控件
 * 
 * @author woden
 * 
 */
@WidgetAnnotation(cmd = "detail", ftl = "classpath:widget/{mode}/custom/detail.ftl")
public class Detail implements Widget {

	@Override
	public void prepareMap(Map<String, Object> map) {
		Object value = map.get("value");
		if (value == null) {
			value = "";
		}
		if (!(value instanceof String)) {// 非字符串需要先转换
			map.put("value", JsonMapper.defaultMapper().toJson(value));
		}

		map.put("widgetKey", widgetKey);
	}

	private String widgetKey;

	@Override
	public void setParams(FormValue... values) {
		if (values == null || values.length < 1) {
			throw new SystemRuntimeException(ExceptionType.CONFIG, "控件配置出错.");
		}

		this.widgetKey = values[0].getName();
	}

	@Override
	public String show(Object value) {
		RequestContext request = RequestContext.getCurrent();
		return new FormWidget("detail[" + widgetKey + "]").toHtml("_tmp", WidgetState.readonly, value, request.getString(Keys.ACTION_MODE.toString()));
	}

	@Override
	public Object code(String showName) {
		return null;
	}

}
