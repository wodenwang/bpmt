/*
 * $HeadURL: $
 * $Id: $
 * Copyright (c) 2013 by Riversoft System, all rights reserved.
 */
package com.riversoft.widget.common;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.riversoft.core.exception.SystemRuntimeException;
import com.riversoft.core.web.widget.FormValue;
import com.riversoft.core.web.widget.Widget;
import com.riversoft.core.web.widget.WidgetAnnotation;

/**
 * @author Woden
 * 
 */
@WidgetAnnotation(cmd = "date", ftl = "classpath:widget/{mode}/common/date.ftl")
public class Date implements Widget {

	private static final Logger logger = LoggerFactory.getLogger(Date.class);

	private String patten = "yyyy-MM-dd";
	private String width;
	private String defaultDate;
	private DateFormat dateFormat;
	private Map<String, Object> map = new HashMap<>();

	@Override
	public void setParams(FormValue... values) {

		if (values != null && values.length > 0) {
			map.put("param", values[0].getName());
			switch (values[0].getName()) {
			case "datetime":
				patten = "yyyy-MM-dd HH:mm:ss";
				break;
			case "time":
				patten = "HH:mm:ss";
				break;
			default:
				patten = "yyyy-MM-dd";
				break;
			}
		}
		width = values.length > 1 ? values[1].getName() : null;
		defaultDate = values.length > 2 ? values[2].getName() : null;

		dateFormat = new SimpleDateFormat(patten);
		map.put("patten", patten);
		map.put("width", width);
		map.put("defaultDate", defaultDate);
	}

	@Override
	public String show(Object value) {
		if (value instanceof String) {
			return value.toString();
		} else if (value != null && !"".equals(value)) {
			return this.dateFormat.format(value);
		} else {
			return null;
		}
	}

	@Override
	public Object code(String showName) {
		try {
			logger.debug("待转换值：[" + showName + "],转换格式:[" + patten + "]");
			return dateFormat.parseObject(showName);
		} catch (ParseException e) {
			throw new SystemRuntimeException("日期时间转换出错。无法将值[" + showName + "]转换格式:[" + patten + "]", e);
		}
	}

	@Override
	public void prepareMap(Map<String, Object> map) {
		map.putAll(this.map);
	}
}
