/*
 * $HeadURL: $
 * $Id: $
 * Copyright (c) 2013 by Riversoft System, all rights reserved.
 */
package com.riversoft.widget.db;

import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.riversoft.core.web.widget.WidgetAnnotation;

/**
 * @author Borball
 * 
 */
@WidgetAnnotation(cmd = "radio", ftl = "classpath:widget/{mode}/db/radio.ftl")
public class Radio extends Select {

	@Override
	public void prepareMap(Map<String, Object> map) {
		super.prepareMap(map);

		Object value = map.get("value");
		if (value == null || StringUtils.isEmpty(value.toString())) {// 无值则默认选择第一个
			map.put("value", getList().get(0).getCode());
		}
	}
}
