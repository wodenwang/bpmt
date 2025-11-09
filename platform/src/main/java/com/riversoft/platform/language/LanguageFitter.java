/*
 * $HeadURL: $
 * $Id: $
 * Copyright (c) 2015 by Riversoft System, all rights reserved.
 */
package com.riversoft.platform.language;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

import com.riversoft.platform.SessionManager;
import com.riversoft.platform.db.ORMAdapterService;

/**
 * 语言适配器
 * 
 * @author woden
 */
public class LanguageFitter {

	/**
	 * 语言适配
	 * 
	 * @param str
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static String fit(String str) {
		String currentLan = SessionManager.getCurrentLanguage();// 当前语言

		// 非#开头和结尾
		if (!StringUtils.startsWith(str, "#") || !StringUtils.endsWith(str, "#")) {
			return str;
		}

		// :fit的逻辑判断
		{
			Pattern pattern = Pattern.compile("(?<=(:fit\\[))[^\\]]+(?=\\])");
			Matcher matcher = pattern.matcher(str);
			if (matcher.find()) {
				// 匹配得到fit模式,则按照fit模式来查表获取数据
				String[] tmps = StringUtils.split(matcher.group(0), ",");
				if (tmps == null || tmps.length != 2) {
					return str;
				}
				String tableName = tmps[0];
				String tableKey = tmps[1];

				// 通过数据库方式获取值
				Map<String, Object> o = (Map<String, Object>) ORMAdapterService.getInstance().findByPk(tableName, tableKey);
				if (o == null) {
					// 找不到翻译,返回tableKey
					return tableKey;
				}

				// 当前语言获取对应列
				String result = (String) o.get(StringUtils.upperCase(currentLan));
				if (StringUtils.isEmpty(result) && StringUtils.contains(currentLan, "_")) {
					result = (String) o.get(StringUtils.upperCase(StringUtils.substringBefore(currentLan, "_")));
				}
				// 若当前语言获取不到,则采用语言模糊匹配.如zh_CN则匹配zh
				if (StringUtils.isNotEmpty(result)) {
					return result;
				} else {
					return tableKey;
				}
			}
		}

		// 匹配当前语言所属配置
		{
			Pattern pattern = Pattern.compile("(?<=(:" + currentLan + "\\[))[^\\]]+(?=\\])");
			Matcher matcher = pattern.matcher(str);
			if (matcher.find()) {
				// 匹配到当前语言对应配置,直接放回
				return matcher.group(0);
			}

			if (StringUtils.contains(currentLan, "_")) {
				// 当前语言存在下划线_,截取下划线前面一段匹配
				pattern = Pattern.compile("(?<=(:" + StringUtils.substringBefore(currentLan, "_") + "\\[))[^\\]]+(?=\\])");
				matcher = pattern.matcher(str);
				if (matcher.find()) {
					// 匹配到当前语言对应配置,直接放回
					return matcher.group(0);
				}
			}
		}

		return str;
	}
}
