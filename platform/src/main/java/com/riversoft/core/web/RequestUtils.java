/*
 * $HeadURL: $
 * $Id: $
 * Copyright (c) 2013 by Riversoft System, all rights reserved.
 */
package com.riversoft.core.web;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.Part;

import org.apache.commons.lang3.StringUtils;

import com.riversoft.core.exception.SystemRuntimeException;
import com.riversoft.util.ValueConvertUtils;
import com.riversoft.util.jackson.JsonMapper;

/**
 * 表单提交后数据获取辅助类
 * 
 * @author Woden
 * 
 */
public final class RequestUtils {

	/**
	 * 判断当前请求是否使用multipart模式提交
	 * 
	 * @param request
	 * @return
	 */
	private static boolean isMultipartMode(HttpServletRequest request) {
		if (request.getContentType() != null
				&& request.getContentType().toLowerCase().startsWith("multipart/form-data")) {
			// logger.debug("当前提交内容类型为:" + request.getContentType());
			return true;
		}

		return false;
	}

	/**
	 * 获取字符串值
	 * 
	 * @param request
	 * @param name
	 * @return
	 */
	public static String getStringValue(HttpServletRequest request, String name) {
		if (isMultipartMode(request)) {
			try {
				Part part = request.getPart(name);
				if (part == null) {
					return null;
				}
				InputStream is = part.getInputStream();
				byte[] bytes = new byte[is.available()];
				is.read(bytes);
				return new String(bytes);
			} catch (IOException | ServletException e) {
				throw new SystemRuntimeException("从表单获取[" + name + "]字段出错.", e);
			}
		} else {
			return request.getParameter(name);
		}
	}

	/**
	 * 获取字符串数组
	 * 
	 * @param request
	 * @param name
	 * @return
	 */
	public static String[] getStringValues(HttpServletRequest request, String name) {

		if (isMultipartMode(request)) {// 流模式
			List<String> list = new ArrayList<>();
			try {
				for (Part part : request.getParts()) {
					if (part == null || !name.equals(part.getName())) {
						continue;
					}

					try (InputStream is = part.getInputStream();) {
						byte[] bytes = new byte[is.available()];
						is.read(bytes);
						list.add(new String(bytes));
					}
				}
			} catch (IOException | ServletException e) {
				throw new SystemRuntimeException("从表单获取[" + name + "]字段出错.", e);
			}
			return list.toArray(new String[list.size()]);
		} else {
			// 先加[]获取
			String[] values = request.getParameterValues(name + "[]");
			if (values == null || values.length < 1) {
				values = request.getParameterValues(name);
			}
			return values;
		}
	}

	/**
	 * 获取长整形值
	 * 
	 * @param request
	 * @param name
	 * @return
	 */
	public static Long getLongValue(HttpServletRequest request, String name) {
		Long val = ValueConvertUtils.convert(getStringValue(request, name), Long.class);
		if (val == null) {
			return 0L;
		}
		return val;
	}

	/**
	 * 获取长整形数组
	 * 
	 * @param request
	 * @param name
	 * @return
	 */
	public static List<Long> getLongValues(HttpServletRequest request, String name) {
		return ValueConvertUtils.convertArray(getStringValues(request, name), Long.class);
	}

	/**
	 * 获取浮点值
	 * 
	 * @param request
	 * @param name
	 * @return
	 */
	public static Float getFloatValue(HttpServletRequest request, String name) {
		Float val = ValueConvertUtils.convert(getStringValue(request, name), Float.class);
		if (val == null) {
			return 0F;
		}
		return val;
	}

	/**
	 * 获取浮点数组
	 * 
	 * @param request
	 * @param name
	 * @return
	 */
	public static List<Float> getFloatValues(HttpServletRequest request, String name) {
		return ValueConvertUtils.convertArray(getStringValues(request, name), Float.class);
	}

	/**
	 * 获取双精度浮点值
	 * 
	 * @param request
	 * @param name
	 * @return
	 */
	public static Double getDoubleValue(HttpServletRequest request, String name) {
		Double val = ValueConvertUtils.convert(getStringValue(request, name), Double.class);
		if (val == null) {
			return 0D;
		}
		return val;
	}

	/**
	 * 获取双精度浮点数组
	 * 
	 * @param request
	 * @param name
	 * @return
	 */
	public static List<Double> getDoubleValues(HttpServletRequest request, String name) {
		return ValueConvertUtils.convertArray(getStringValues(request, name), Double.class);
	}

	/**
	 * 获取大数据值
	 * 
	 * @param request
	 * @param name
	 * @return
	 */
	public static BigDecimal getBigDecimalValue(HttpServletRequest request, String name) {
		BigDecimal val = ValueConvertUtils.convert(getStringValue(request, name), BigDecimal.class);
		if (val == null) {
			return BigDecimal.ZERO;
		}
		return val;
	}

	/**
	 * 获取大树据数组
	 * 
	 * @param request
	 * @param name
	 * @return
	 */
	public static List<BigDecimal> getBigDecimalValues(HttpServletRequest request, String name) {
		return ValueConvertUtils.convertArray(getStringValues(request, name), BigDecimal.class);
	}

	/**
	 * 获取整形值
	 * 
	 * @param request
	 * @param name
	 * @return
	 */
	public static Integer getIntegerValue(HttpServletRequest request, String name) {
		Integer val = ValueConvertUtils.convert(getStringValue(request, name), Integer.class);
		if (val == null) {
			return 0;
		}
		return val;
	}

	/**
	 * 获取整形数组
	 * 
	 * @param request
	 * @param name
	 * @return
	 */
	public static List<Integer> getIntegerValues(HttpServletRequest request, String name) {
		return ValueConvertUtils.convertArray(getStringValues(request, name), Integer.class);
	}

	/**
	 * 获取时间日期类型
	 * 
	 * @param request
	 * @param name
	 * @return
	 */
	public static Date getDateValue(HttpServletRequest request, String name) {
		return ValueConvertUtils.convert(getStringValue(request, name), Date.class);
	}

	/**
	 * 获取时间日期数组
	 * 
	 * @param request
	 * @param name
	 * @return
	 */
	public static List<Date> getDateValues(HttpServletRequest request, String name) {
		return ValueConvertUtils.convertArray(getStringValues(request, name), Date.class);
	}

	/**
	 * 获取JSON值
	 * 
	 * @param request
	 * @param name
	 * @return
	 */
	public static HashMap<String, Object> getJsonValue(HttpServletRequest request, String name) {
		String value = getStringValue(request, name);
		return JsonMapper.defaultMapper().fromJson(value, HashMap.class);
	}

	/**
	 * 获取JSON数组 ["key1" : "value1", "key2" : "value2" ...]
	 * 
	 * @param request
	 * @param name
	 * @return
	 */
	public static List<HashMap<String, Object>> getJsonValues(HttpServletRequest request, String name) {
		String[] values = getStringValues(request, name);
		if (values != null && values.length > 0) {
			List<HashMap<String, Object>> list = new ArrayList<>();
			for (String value : values) {
				if (StringUtils.isEmpty(value)) {
					continue;
				}
				if (value.startsWith("[")) {
					list.addAll(JsonMapper.defaultMapper().fromJson(value, List.class));
				} else {
					list.add(JsonMapper.defaultMapper().fromJson(value, HashMap.class));
				}
			}
			return list;
		} else {
			return Collections.emptyList();
		}
	}

	/**
	 * 根据数据类型获取值
	 * 
	 * @param request
	 * @param name
	 * @param type
	 */
	public static <T> T getValue(HttpServletRequest request, String name, Class<T> type) {
		return ValueConvertUtils.convert(getStringValue(request, name), type);
	}

	/**
	 * 根据数据类型获取值
	 * 
	 * @param request
	 * @param name
	 * @param type
	 * @return
	 */
	public static <T> List<T> getValues(HttpServletRequest request, String name, Class<T> type) {
		return ValueConvertUtils.convertArray(getStringValues(request, name), type);
	}
}
