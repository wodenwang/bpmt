/*
 * $HeadURL: $
 * $Id: $
 * Copyright (c) 2016 by Riversoft System, all rights reserved.
 */
package com.riversoft.widget.custom;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.riversoft.core.context.RequestContext;
import com.riversoft.core.db.ORMService;
import com.riversoft.core.exception.ExceptionType;
import com.riversoft.core.exception.SystemRuntimeException;
import com.riversoft.core.web.Actions.Keys;
import com.riversoft.core.web.widget.FormValue;
import com.riversoft.core.web.widget.FormWidget;
import com.riversoft.core.web.widget.Widget;
import com.riversoft.core.web.widget.WidgetAnnotation;
import com.riversoft.core.web.widget.WidgetResource;
import com.riversoft.core.web.widget.WidgetState;
import com.riversoft.platform.script.ScriptHelper;
import com.riversoft.platform.script.ScriptTypes;
import com.riversoft.platform.web.FileHelper;
import com.riversoft.platform.web.FileManager.UploadFile;

/**
 * 模板控件
 * 
 * @author woden
 *
 */
@WidgetAnnotation(cmd = "template")
public class TemplateWidget implements Widget, WidgetResource {

	private String widgetKey;

	@Override
	public InputStream getFtl() {
		Map<String, Object> config = (Map<String, Object>) ORMService.getInstance().findByPk("WdgTemplate", widgetKey);
		if (config == null) {
			throw new SystemRuntimeException(ExceptionType.CONFIG_WIDGET, "模板控件[" + widgetKey + "]不存在.");
		}

		UploadFile file = FileHelper.file((byte[]) config.get("templateFile"));
		if (file == null) {
			throw new SystemRuntimeException(ExceptionType.CONFIG_WIDGET, "模板控件[" + widgetKey + "]配置有误.");
		}

		try {
			return file.getInputStream();
		} catch (FileNotFoundException e) {
			throw new SystemRuntimeException(e);
		}
	}

	@Override
	public void prepareMap(Map<String, Object> map) {
		Map<String, Object> config = (Map<String, Object>) ORMService.getInstance().findByPk("WdgTemplate", widgetKey);
		if (config == null) {
			throw new SystemRuntimeException(ExceptionType.CONFIG_WIDGET, "模板控件[" + widgetKey + "]不存在.");
		}
		Map<String, Object> context = new HashMap<>();
		Set<Map<String, Object>> vars = (Set<Map<String, Object>>) config.get("vars");
		for (Map<String, Object> var : vars) {
			Integer type = (Integer) var.get("execType");
			String script = (String) var.get("execScript");
			Object value = ScriptHelper.evel(ScriptTypes.forCode(type), script, context);
			context.put((String) var.get("var"), value);
		}
		map.putAll(context);
	}

	@Override
	public void setParams(FormValue... values) {
		if (values != null && values.length > 0) {
			widgetKey = values[0].getName();
		} else {
			throw new SystemRuntimeException(ExceptionType.CONFIG, "template控件配置出错.");
		}
	}

	@Override
	public String show(Object value) {
		RequestContext request = RequestContext.getCurrent();
		return new FormWidget("template[" + widgetKey + "]").toHtml("_tmp", WidgetState.readonly, value, request.getString(Keys.ACTION_MODE.toString()));
	}

	@Override
	public Object code(String showName) {
		throw new SystemRuntimeException(ExceptionType.CONFIG_WIDGET, "该方法不被系统支持,请联系管理员处理.");
	}

}
