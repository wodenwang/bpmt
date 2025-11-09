/*
 * $HeadURL: $
 * $Id: $
 * Copyright (c) 2013 by Riversoft System, all rights reserved.
 */
package com.riversoft.widget.sys;

import java.io.Serializable;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.riversoft.core.db.ORMService;
import com.riversoft.core.exception.ExceptionType;
import com.riversoft.core.exception.SystemRuntimeException;
import com.riversoft.core.web.widget.FormValue;
import com.riversoft.core.web.widget.Widget;
import com.riversoft.core.web.widget.WidgetAnnotation;
import com.riversoft.platform.db.Code2NameService;

/**
 * 事件处理器控件
 * 
 * @author woden
 * 
 */
@WidgetAnnotation(cmd = "wxcommand", ftl = "classpath:widget/{mode}/sys/wxcommand.ftl")
public class WxCommand implements Widget {

	private String menuType;
	private Integer mpFlag;

	@Override
	public void prepareMap(Map<String, Object> map) {
		map.put("menuType", menuType);
		map.put("mpFlag", mpFlag);

		String commandKey = (String) map.get("value");
		if (StringUtils.isNotEmpty(commandKey)) {
			map.put("showName", Code2NameService.getInstance().translateORM("WxCommand", "commandKey", "busiName", commandKey));
		}
	}

	@Override
	public void setParams(FormValue... values) {
		if (values.length > 1) {
			mpFlag = "qy".equals(values[0].getName()) ? 0 : 1;
			menuType = values[1].getName();
		} else {
			throw new SystemRuntimeException(ExceptionType.CONFIG, "配置错误.");
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public String show(Object value) {
		Map<String, Object> vo = (Map<String, Object>) ORMService.getInstance().findByPk("WxCommand", (Serializable) value);
		if (vo != null) {
			return (String) vo.get("busiName");
		}

		return "";
	}

	@Override
	public Object code(String showName) {
		return "";
	}

}
