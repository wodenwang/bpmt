/*
 * $HeadURL: $
 * $Id: $
 * Copyright (c) 2014 by Riversoft System, all rights reserved.
 */
package com.riversoft.widget.platform;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.riversoft.core.Config;
import com.riversoft.core.context.RequestContext;
import com.riversoft.core.exception.ExceptionType;
import com.riversoft.core.exception.SystemRuntimeException;
import com.riversoft.core.web.Actions.Keys;
import com.riversoft.core.web.widget.FormWidget;
import com.riversoft.core.web.widget.WidgetAnnotation;
import com.riversoft.core.web.widget.WidgetState;
import com.riversoft.platform.web.FileManager;
import com.riversoft.platform.web.FileManager.UploadFile;

/**
 * office文档在线查看
 * 
 * @author woden
 */
@WidgetAnnotation(cmd = "office", ftl = "classpath:widget/{mode}/platform/office.ftl")
public class OfficeWidget extends FileManagerWidget {

	@Override
	public void prepareMap(Map<String, Object> map) {
		WidgetState state = (WidgetState) map.get("state");
		if (WidgetState.readonly != state) {
			throw new SystemRuntimeException(ExceptionType.CONFIG, "该控件不支持编辑.");
		}

		super.prepareMap(map);// 解析流

		String value = (String) map.get("value");// 获取文件列表
		List<UploadFile> fileList = FileManager.getUploadFiles(value);
		List<Map<String, Object>> list = new ArrayList<>();

		int maxSize;
		try {
			maxSize = Integer.parseInt(Config.get("office.file.size", "10"));
		} catch (Exception e) {
			maxSize = 10;
		}
		map.put("maxSize", maxSize);// 没有配置则设置一个较大数

		for (UploadFile o : fileList) {
			Map<String, Object> vo = new HashMap<>();
			vo.put("name", o.getName());
			vo.put("sysName", o.getSysName());
			vo.put("type", o.getType());
			vo.put("size", o.getFile().length());
			list.add(vo);
		}
		map.put("list", list);

	}

	@Override
	public String show(Object value) {

		RequestContext request = RequestContext.getCurrent();
		if (StringUtils.equals(request.getString(Keys.ACTION_MODE.toString()), "h5")) {
			return new FormWidget("img").toHtml("_tmp", WidgetState.readonly, value, request.getString(Keys.ACTION_MODE.toString()));
		}

		return new FormWidget("office").toHtml("_tmp", WidgetState.readonly, value, null);
	}
}
