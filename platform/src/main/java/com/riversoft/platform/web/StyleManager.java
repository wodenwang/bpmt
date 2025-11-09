/*
 * $HeadURL: $
 * $Id: $
 * Copyright (c) 2013 by Riversoft System, all rights reserved.
 */
package com.riversoft.platform.web;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

/**
 * 样式管理器
 * 
 * @author woden
 * 
 */
public class StyleManager {

	/**
	 * Style对象
	 * 
	 * @author woden
	 * 
	 */
	public static class Style {
		private Map<String, String> map = new HashMap<>();

		private static Map<String, String> extField = new HashMap<>();
		static {
			extField.put("min-width", "px");
			extField.put("min-height", "px");
		}

		public Style(String str) {
			if (StringUtils.isEmpty(str)) {
				return;
			}

			for (String m : str.split(";")) {
				String[] val = m.split(":");
				if (val == null || val.length != 2) {
					continue;
				}

				String key = val[0];
				String value = val[1];

				if (extField.containsKey(key)) {
					value = value.replaceAll(extField.get(key), "");
				}

				map.put(key, value);
			}
		}

		/**
		 * 转换成css字符串
		 * 
		 * @param filter
		 * @return
		 */
		public String toCss(Map<String, String> filter) {
			StringBuffer buff = new StringBuffer();
			Set<String> keys = new HashSet<>();
			keys.addAll(map.keySet());
			keys.addAll(filter.keySet());

			for (String key : keys) {
				if (map.containsKey(key) && filter.containsKey(key) && StringUtils.isEmpty(filter.get(key))) {// key存在,并且需要过滤
					continue;
				} else if (map.containsKey(key)) {// 否则以实际值为准
					buff.append(key).append(":").append(map.get(key));
				} else if (StringUtils.isNotEmpty(filter.get(key))) {// 否则写入默认值
					buff.append(key).append(":").append(filter.get(key));
				} else {
					continue;
				}

				if (extField.containsKey(key)) {
					buff.append(extField.get(key));
				}
				buff.append(";");
			}

			return buff.toString();
		}

		/**
		 * 转换成css字符串
		 * 
		 * @return
		 */
		public String toCss() {
			return toCss(new HashMap<String, String>());
		}

		/**
		 * 设置值
		 * 
		 * @param key
		 * @param value
		 */
		public void set(String key, String value) {
			map.put(key, value);
		}

		/**
		 * 获取值
		 * 
		 * @param key
		 * @return
		 */
		public String get(String key) {
			return map.get(key);
		}

		/**
		 * 获取值
		 * 
		 * @return
		 */
		public Map<String, String> getMap() {
			return map;
		}
	}

}
