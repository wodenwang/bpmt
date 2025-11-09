/*
 * $HeadURL: $
 * $Id: $
 * Copyright (c) 2013 by Riversoft System, all rights reserved.
 */
package com.riversoft.core.web;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.net.URLEncoder;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.fasterxml.jackson.core.type.TypeReference;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.riversoft.core.context.RequestContext;
import com.riversoft.core.exception.SystemRuntimeException;
import com.riversoft.core.web.widget.FormWidget;
import com.riversoft.util.jackson.JsonMapper;

/**
 * @author Woden
 * 
 */
public class ELUtils {

	private static final Logger logger = LoggerFactory.getLogger(ELUtils.class);

	/**
	 * 控件翻译的EL函数调用
	 * 
	 * @param cmd
	 * @param value
	 * @return
	 */
	public static String widget(String cmd, Object value) {
		return new FormWidget(cmd).show(value);
	}

	/**
	 * 将对象转换为json
	 * 
	 * @param obj
	 * @return
	 */
	public static String json(Object obj) {
		if (obj == null) {
			return "";
		} else {
			return JsonMapper.defaultMapper().toJson(obj);
		}
	}

	/**
	 * 将对象转换为json(只转换特定key)
	 * 
	 * @param obj
	 * @param keys
	 * @return
	 */
	public static String json(Object obj, String... keys) {
		Set<String> includes = new HashSet<>();
		for (String key : keys) {
			includes.add(key);
		}
		TypeReference<HashMap<String,Object>> typeRef = new TypeReference<HashMap<String,Object>>() {};
		HashMap<String, Object> map = JsonMapper.defaultMapper().getMapper().convertValue(obj, typeRef);
		Set<String> all = new HashSet<>(map.keySet());
		for (String key : all) {
			if (!includes.contains(key)) {
				map.remove(key);
			}
		}
		String json = JsonMapper.defaultMapper().toJson(map);
		return json.replaceAll("\'", "\\\\\'").replaceAll("\"", "'");
	}

	/**
	 * 动态获取值
	 * 
	 * @param var
	 * @param name
	 * @return
	 */
	public static Object property(Object var, String name) {
		if (var == null) {
			return null;
		}
		try {
			return PropertyUtils.getProperty(var, name);
		} catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
			logger.error("获取属性[" + name + "]出错.", e);
			throw new SystemRuntimeException(e);
		}
	}

	/**
	 * hashmap的put动作
	 * 
	 * @param map
	 * @param name
	 * @param value
	 * @return
	 */
	public static Map<String, Object> map(Map<String, Object> map, String name, Object value) {
		if (map == null) {
			map = new HashMap<String, Object>();
		}
		map.put(name, value);
		return map;
	}

	/**
	 * 验证对象是否存在于列表中
	 * 
	 * @param list
	 * @param vo
	 * @return
	 */
	public static boolean contains(Collection<?> list, Object vo) {
		if (list == null) {
			return false;
		}
		return list.contains(vo);
	}

	/**
	 * 获取参数
	 * 
	 * @param name
	 * @return
	 */
	public static String param(String name) {
		RequestContext request = RequestContext.getCurrent();
		String[] str = request.getStrings(name);
		if (str == null || str.length == 0) {
			return null;
		} else if (str.length == 1) {
			return str[0];
		} else {
			return StringUtils.join(str, ";");
		}
	}

	/**
	 * url encode
	 * 
	 * @param value
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	public static String urlEncode(String value) throws UnsupportedEncodingException {
		String result = URLEncoder.encode(value, "UTF-8");
		return result;
	}
}
