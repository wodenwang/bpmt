/*
 * $HeadURL: $
 * $Id: $
 * Copyright (c) 2013 by Riversoft System, all rights reserved.
 */
package com.riversoft.platform.web;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.Map;

import org.springframework.util.LinkedCaseInsensitiveMap;

import com.riversoft.core.db.ORMService;
import com.riversoft.platform.SessionManager;
import com.riversoft.platform.language.LanguageFitter;
import com.riversoft.platform.po.CmPri;
import com.riversoft.platform.script.ScriptHelper;
import com.riversoft.platform.script.ScriptTypes;
import com.riversoft.platform.script.function.Util;
import com.riversoft.util.Formatter;

/**
 * @author Woden
 * 
 */
public class PlatformELUtils {

	/**
	 * 根据类型获取对其方式
	 * 
	 * @param type
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public static String style(Object obj) {

		if (obj == null) {
			return "center";
		}

		Class type;
		if (obj instanceof Class) {
			type = (Class) obj;
		} else {
			type = obj.getClass();
		}

		if (type == Long.class || type == Integer.class || type == Double.class || type == BigDecimal.class
				|| type == Date.class) {
			return "right";
		} else {
			return "center";
		}
	}

	/**
	 * 权限校验
	 * 
	 * @param po
	 * @return
	 */
	public static boolean check(CmPri po) {
		return SessionManager.check(po);
	}

	/**
	 * 校验是否管理员
	 * 
	 * @return
	 */
	public static boolean checkAdmin() {
		return SessionManager.isAdmin();
	}

	/**
	 * 权限校验(扩展)
	 * 
	 * @param po
	 * @param context
	 * @return
	 */
	public static boolean checkExt(CmPri po, Map<String, Object> context) {
		return SessionManager.check(po, context);
	}

	/**
	 * script调用
	 * 
	 * @param type
	 * @param script
	 * @param context
	 * @return
	 */
	public static Object script(Integer type, String script, Map<String, Object> context) {
		ScriptTypes scriptTypes = ScriptTypes.forCode(type);
		return ScriptHelper.evel(scriptTypes, script, context);
	}

	/**
	 * 查询唯一值
	 * 
	 * @param entityName
	 * @param pk
	 * @return
	 */
	public static Object po(String entityName, Serializable pk) {
		return ORMService.getInstance().findByPk(entityName, pk);
	}

	/**
	 * 根据规则从VO中解析新的VO(用于left join关联时获取值)
	 * 
	 * @param pixel
	 * @param vo
	 * @return
	 */
	public static Map<String, Object> pixelVO(String pixel, Map<String, Object> vo) {
		if (vo == null) {
			return null;
		}
		String rulePixel = "_" + pixel + "_";// 按规则生成前缀
		Map<String, Object> newVO = new LinkedCaseInsensitiveMap<>();
		for (String key : vo.keySet()) {
			if (key.toUpperCase().startsWith(rulePixel.toUpperCase())) {// 不区分大小写
				newVO.put(key.substring(rulePixel.length()), vo.get(key));
			}
		}
		return newVO;
	}

	/**
	 * 比较两个时间差
	 * 
	 * @param date1
	 * @param date2
	 * @param partten
	 * @return
	 */
	public static Long compareDate(Date date1, Date date2, String partten) {
		return Util.compareDate(date1, date2, partten);
	}

	/**
	 * 计算时间
	 * 
	 * @param date
	 * @param offset
	 * @param partten
	 * @return
	 */
	public static Date calDate(Date date, Integer offset, String partten) {
		return Util.calDate(date, offset, partten);
	}

	/**
	 * 将秒数格式化成天-小时-分-秒
	 * 
	 * @param s
	 * @return
	 */
	public static String formatDuring(Long s) {
		return Formatter.formatDuring(s);
	}

	/**
	 * 自适应系统语言
	 * 
	 * @param value
	 * @return
	 */
	public static String language(String str) {
		return LanguageFitter.fit(str);
	}
}
