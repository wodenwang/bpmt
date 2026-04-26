/*
 * $HeadURL: $
 * $Id: $
 * Copyright (c) 2013 by Riversoft System, all rights reserved.
 */
package com.riversoft.widget.sys;

import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.riversoft.core.exception.ExceptionType;
import com.riversoft.core.exception.SystemRuntimeException;
import com.riversoft.core.web.widget.FormValue;
import com.riversoft.core.web.widget.Widget;
import com.riversoft.core.web.widget.WidgetAnnotation;
import com.riversoft.platform.db.Code2NameService;
import com.riversoft.platform.po.VwUrl;
import com.riversoft.platform.web.view.ViewActionBuilder;
import com.riversoft.platform.web.view.ViewActionBuilder.SysVO;

/**
 * @author woden
 * 
 */
@WidgetAnnotation(cmd = "view", ftl = "classpath:widget/{mode}/sys/view.ftl")
public class View implements Widget {

	private String target;

	@Override
	public void prepareMap(Map<String, Object> map) {
		String action = (String) map.get("value");
		if (StringUtils.isNotEmpty(action)) {

			if (action.endsWith(".view")) {
				String viewKey = action.substring(1, action.lastIndexOf("."));
				map.put("showName",
						Code2NameService.getInstance().translateORM(VwUrl.class.getName(), "viewKey", "description",
								viewKey));
			} else if (action.endsWith(".shtml")) {
				SysVO sysVo = ViewActionBuilder.getInstance().getSysModule(action);
				map.put("showName", sysVo != null ? sysVo.getDescription() : "(无法获取说明)");
			}
		}
		map.put("target", target);
	}

	@Override
	public void setParams(FormValue... values) {
		if (values.length > 0) {
			target = values[0].getName();
		} else {
			throw new SystemRuntimeException(ExceptionType.CONFIG, "控件配置错误.");
		}
	}

	@Override
	public String show(Object value) {
		String action = (String) value;
		if (action.endsWith(".view")) {
			String viewKey = action.substring(1, action.lastIndexOf("."));
			return (String) Code2NameService.getInstance().translateORM(VwUrl.class.getName(), "viewKey",
					"description", viewKey);
		} else if (action.endsWith(".shtml")) {
			SysVO sysVo = ViewActionBuilder.getInstance().getSysModule(action);
			if (sysVo != null) {
				return sysVo.getDescription();
			}
		}
		return action;
	}

	@Override
	public Object code(String showName) {
		return "";
	}

}
