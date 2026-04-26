/*
 * $HeadURL: $
 * $Id: $
 * Copyright (c) 2013 by Riversoft System, all rights reserved.
 */
package com.riversoft.widget.custom;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.riversoft.core.db.JdbcService;
import com.riversoft.core.db.ORMService;
import com.riversoft.core.exception.ExceptionType;
import com.riversoft.core.exception.SystemRuntimeException;
import com.riversoft.core.web.widget.FormValue;
import com.riversoft.core.web.widget.Widget;
import com.riversoft.core.web.widget.WidgetAnnotation;
import com.riversoft.platform.script.ScriptHelper;
import com.riversoft.platform.script.ScriptTypes;

/**
 * @author woden
 * 
 */
@WidgetAnnotation(cmd = "combo", ftl = "classpath:widget/{mode}/custom/combo.ftl")
public class Combo implements Widget {
	/**
	 * Logger for this class
	 */
	private static final Logger logger = LoggerFactory.getLogger(Combo.class);
	private String widgetKey;
	private boolean codeFlag = false;// 默认不展示code

	@Override
	public void prepareMap(Map<String, Object> map) {
		Object value = map.get("value");
		if (value != null && StringUtils.isNotEmpty(value.toString())) {
			map.put("showName", show(value));
		}

		Map<String, Object> config = (Map<String, Object>) ORMService.getInstance().findByPk("WdgBase", widgetKey);
		if (config == null) {
			throw new SystemRuntimeException(ExceptionType.BUSINESS, "数据控件[" + widgetKey + "]不存在.");
		}
		map.put("config", config);

	}

	@Override
	public void setParams(FormValue... values) {

		if (values != null && values.length > 0) {
			widgetKey = values[0].getName();
		} else {
			throw new SystemRuntimeException(ExceptionType.CONFIG, "combo控件配置出错.");
		}

		if (values != null && values.length > 1) {
			codeFlag = "true".equalsIgnoreCase(values[1].getName());
		}

	}

	@Override
	public String show(Object value) {
		Map<String, Object> context = new HashMap<>();
		context.put("value", value);

		Map<String, Object> config = (Map<String, Object>) ORMService.getInstance().findByPk("WdgBase", widgetKey);
		if (config == null) {
			throw new SystemRuntimeException(ExceptionType.BUSINESS, "数据控件[" + widgetKey + "]不存在.");
		}
		Map<String, Object> combo = (Map<String, Object>) ORMService.getInstance().findByPk("WdgCombo", widgetKey);
		if (combo == null) {
			throw new SystemRuntimeException(ExceptionType.BUSINESS, "数据控件扩展配置[" + widgetKey + "]不存在.");
		}

		String pkSqlScript = (String) combo.get("pkSqlScript");
		if (StringUtils.isEmpty(pkSqlScript)) {
			throw new SystemRuntimeException(ExceptionType.CONFIG, "控件combo没有配置数据翻译,请联系管理员处理.");
		}

		// 主SQL语句
		String mainSql = (String) ScriptHelper.evel(ScriptTypes.forCode((Integer) config.get("mainSqlType")),
				(String) config.get("mainSqlScript"));

		// PK语句
		String pkSql = (String) ScriptHelper.evel(ScriptTypes.forCode((Integer) combo.get("pkSqlType")),
				(String) combo.get("pkSqlScript"), context);

		Map<String, Object> vo = JdbcService.getInstance().findSQL(mainSql + " and " + pkSql);
		if (vo == null) {
			return "";
		}

		context.put("vo", vo);
		String name = (String) ScriptHelper.evel(ScriptTypes.forCode((Integer) combo.get("nameType")),
				(String) combo.get("nameScript"), context);
		StringBuffer buff = new StringBuffer();
		if (codeFlag) {
			String code = (String) ScriptHelper.evel(ScriptTypes.forCode((Integer) combo.get("codeType")),
					(String) combo.get("codeScript"), context);
			buff.append("[").append(code).append("]");
		}
		buff.append(name);
		return buff.toString();
	}

	@Override
	public Object code(String showName) {
		throw new SystemRuntimeException(ExceptionType.CONFIG_WIDGET, "该方法不被系统支持,请联系管理员处理.");
	}

}
