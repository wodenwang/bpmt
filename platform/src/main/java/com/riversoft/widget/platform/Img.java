/*
 * $HeadURL: $
 * $Id: $
 * Copyright (c) 2013 by Riversoft System, all rights reserved.
 */
package com.riversoft.widget.platform;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.riversoft.core.context.RequestContext;
import com.riversoft.core.exception.ExceptionType;
import com.riversoft.core.exception.SystemRuntimeException;
import com.riversoft.core.web.Actions.Keys;
import com.riversoft.core.web.widget.FormValue;
import com.riversoft.core.web.widget.FormWidget;
import com.riversoft.core.web.widget.WidgetAnnotation;
import com.riversoft.core.web.widget.WidgetState;
import com.riversoft.platform.web.FileManager;
import com.riversoft.platform.web.FileManager.UploadFile;

/**
 * @author woden
 * 
 */
@WidgetAnnotation(cmd = "img", ftl = "classpath:widget/{mode}/platform/img.ftl")
public class Img extends FileManagerWidget {

	private String width = "60";
	private String height = "";

	@Override
	public void setParams(FormValue... values) {
		if (values != null && values.length > 0) {
			width = values[0].getName();
		}

		if (values != null && values.length > 1) {
			height = values[0].getName();
		}
	}

	@Override
	public void prepareMap(Map<String, Object> map) {
		WidgetState state = (WidgetState) map.get("state");
		if (WidgetState.readonly != state) {
			throw new SystemRuntimeException(ExceptionType.CONFIG, "该控件不支持录入.");
		}

		map.put("width", width);
		map.put("height", height);

		Object value = map.get("value");
		if (value instanceof byte[]) {// 如果入参是流,则转换
			List<UploadFile> list = FileManager.toFiles((byte[]) value);
			map.put("list", list);
		} else if (value instanceof String) {

		}

		super.prepareMap(map);
	}

	@Override
	public String show(Object value) {
		StringBuffer params = new StringBuffer(width);
		if (StringUtils.isNotEmpty(height)) {
			params.append(";").append(height);
		}

		RequestContext request = RequestContext.getCurrent();
		return new FormWidget("img[" + params.toString() + "]").toHtml("_tmp", WidgetState.readonly, value, request.getString(Keys.ACTION_MODE.toString()));
	}
}
