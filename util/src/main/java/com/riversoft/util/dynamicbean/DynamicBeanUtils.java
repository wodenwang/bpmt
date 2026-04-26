/*
 * $HeadURL: $
 * $Id: $
 * Copyright (c) 2013 by Riversoft System, all rights reserved.
 */
package com.riversoft.util.dynamicbean;

import java.util.HashMap;
import java.util.Map;

import org.springframework.util.LinkedCaseInsensitiveMap;

/**
 * @author Woden
 * 
 */
public class DynamicBeanUtils {

	/**
	 * 根据类型转换BEAN
	 * 
	 * @param propertyMap
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public static Object transMap2Bean(Map<String, Class> propertyMap) {
		return new CglibBean(propertyMap).getObject();
	}

	/**
	 * map对象克隆
	 * 
	 * @param obj
	 * @return
	 */
	public static Map<String, Object> cloneMap(Map<String, Object> obj) {
		if (obj instanceof LinkedCaseInsensitiveMap) {
			return ((LinkedCaseInsensitiveMap) obj).clone();
		} else if (obj instanceof HashMap) {
			return (Map<String, Object>) ((HashMap) obj).clone();
		}
		return null;
	}
}
