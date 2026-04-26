/*
 * $HeadURL: $
 * $Id: $
 * Copyright (c) 2013 by Riversoft System, all rights reserved.
 */
package com.riversoft.module.widget;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.riversoft.core.db.DataCondition;
import com.riversoft.core.db.DataPackage;
import com.riversoft.core.db.JdbcService;
import com.riversoft.core.db.ORMService;
import com.riversoft.core.db.QueryStringBuilder;
import com.riversoft.core.exception.ExceptionType;
import com.riversoft.core.exception.SystemRuntimeException;
import com.riversoft.core.web.Actions;
import com.riversoft.core.web.Actions.Keys;
import com.riversoft.core.web.Actions.Util;
import com.riversoft.core.web.RequestUtils;
import com.riversoft.platform.SessionManager;
import com.riversoft.platform.po.CmPri;
import com.riversoft.platform.script.ScriptHelper;
import com.riversoft.platform.script.ScriptTypes;

/**
 * 自定义控件
 * 
 * @author woden
 * 
 */
public class ComboAction {
	/**
	 * Logger for this class
	 */
	static final Logger logger = LoggerFactory.getLogger(ComboAction.class);

	/**
	 * 自定义控件-首页
	 * 
	 * @param request
	 * @param response
	 */
	@SuppressWarnings("unchecked")
	public void index(HttpServletRequest request, HttpServletResponse response) {
		String widgetKey = RequestUtils.getStringValue(request, "widgetKey");

		Map<String, Object> config = (Map<String, Object>) ORMService.getInstance().findByPk("WdgBase", widgetKey);
		if (config == null) {
			throw new SystemRuntimeException(ExceptionType.BUSINESS, "数据控件不存在.");
		}

		Map<String, Object> combo = (Map<String, Object>) ORMService.getInstance().findByPk("WdgCombo", widgetKey);
		if (combo == null) {
			throw new SystemRuntimeException(ExceptionType.BUSINESS, "数据控件扩展配置不存在.");
		}

		request.setAttribute("config", config);
		request.setAttribute("combo", combo);

		HashMap<String, Object> param = RequestUtils.getJsonValue(request, Keys.PARAMS.toString());
		Integer pageLimit = (Integer) config.get("pageLimit");
		if (param != null && param.containsKey("pageLimit")) {
			pageLimit = (Integer) param.get("pageLimit");
		}
		request.setAttribute("pageLimit", pageLimit);

		Actions.includePage(request, response, Util.getPagePath(request, "win.jsp"));
	}

	/**
	 * 查询
	 * 
	 * @param request
	 * @param response
	 */
	@SuppressWarnings("unchecked")
	public void list(HttpServletRequest request, HttpServletResponse response) {

		String widgetKey = RequestUtils.getStringValue(request, "widgetKey");

		Map<String, Object> config = (Map<String, Object>) ORMService.getInstance().findByPk("WdgBase", widgetKey);
		if (config == null) {
			throw new SystemRuntimeException(ExceptionType.BUSINESS, "数据控件不存在.");
		}

		Map<String, Object> combo = (Map<String, Object>) ORMService.getInstance().findByPk("WdgCombo", widgetKey);
		if (combo == null) {
			throw new SystemRuntimeException(ExceptionType.BUSINESS, "数据控件扩展配置不存在.");
		}

		request.setAttribute("config", config);
		request.setAttribute("combo", combo);

		// 获取分页信息
		int start = Util.getStart(request);
		int limit = Util.getLimit(request);

		// 获取排序信息
		String field = Util.getSortField(request);
		String dir = Util.getSortDir(request);

		// 主SQL语句
		String mainSql = (String) ScriptHelper.evel(ScriptTypes.forCode((Integer) config.get("mainSqlType")), (String) config.get("mainSqlScript"));

		// 查询条件
		DataCondition condition = new DataCondition(Util.buildQueryMap(new LinkedHashMap<String, Object>(), request));
		HashMap<String, Object> params = RequestUtils.getJsonValue(request, Keys.PARAMS.toString());
		if (StringUtils.isEmpty(field)) {
			if (params != null && params.containsKey("orderBy")) {
				condition.setOrderBySQL((String) params.get("orderBy"));
			} else {
				condition.setOrderBySQL((String) config.get("orderBy"));
			}
		} else {
			condition.setOrderBy(field, dir);
		}

		// 高级查询
		for (Map<String, Object> query : ((Set<Map<String, Object>>) config.get("querys"))) {
			String value = RequestUtils.getStringValue(request, "querys." + query.get("id"));
			if (StringUtils.isEmpty(value)) {
				continue;
			}
			Map<String, Object> context = new HashMap<String, Object>();// 构建上下文
			context.put("value", value);
			context.put("values", RequestUtils.getStringValues(request, "querys." + query.get("id")));
			ScriptTypes type = ScriptTypes.forCode((Integer) query.get("sqlType"));
			String sql = (String) ScriptHelper.evel(type, (String) query.get("sqlScript"), context);
			condition.addSql(sql);
		}

		// 数据约束
		for (Map<String, Object> dataLimit : ((Set<Map<String, Object>>) config.get("limits"))) {
			CmPri pri = (CmPri) dataLimit.get("pri");
			if (!SessionManager.check(pri)) {
				continue;
			}
			ScriptTypes type = ScriptTypes.forCode((Integer) dataLimit.get("sqlType"));
			String sql = (String) ScriptHelper.evel(type, (String) dataLimit.get("sqlScript"));
			condition.addSql(sql);
		}

		StringBuffer sql = new StringBuffer(mainSql);
		String whereCondition = QueryStringBuilder.buildWhere(condition.toEntity());
		if (StringUtils.isNotEmpty(whereCondition)) {
			sql.append(" and ").append(whereCondition.substring(5));
		}
		String orderbyCondition = QueryStringBuilder.buildOrder(condition.toEntity());
		if (StringUtils.isNotEmpty(orderbyCondition)) {
			sql.append(orderbyCondition);
		}

		DataPackage dp = JdbcService.getInstance().querySQLPackage(sql.toString(), start, limit);

		// 设置到页面
		request.setAttribute("dp", dp);

		request.setAttribute("config", config);
		Actions.includePage(request, response, Util.getPagePath(request, "list.jsp"));
	}

	/**
	 * 获取唯一值
	 * 
	 * @param request
	 * @param response
	 */
	@SuppressWarnings("unchecked")
	public void show(HttpServletRequest request, HttpServletResponse response) {
		String widgetKey = RequestUtils.getStringValue(request, "widgetKey");
		String value = RequestUtils.getStringValue(request, "val");

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
		String mainSql = (String) ScriptHelper.evel(ScriptTypes.forCode((Integer) config.get("mainSqlType")), (String) config.get("mainSqlScript"));

		// PK语句
		String pkSql = (String) ScriptHelper.evel(ScriptTypes.forCode((Integer) combo.get("pkSqlType")), (String) combo.get("pkSqlScript"), context);

		Map<String, Object> vo = JdbcService.getInstance().findSQL(mainSql + " and " + pkSql);
		String html = "";
		if (vo != null) {
			context.put("vo", vo);
			html = (String) ScriptHelper.evel(ScriptTypes.forCode((Integer) combo.get("nameType")), (String) combo.get("nameScript"), context);
		}

		Actions.showHtml(request, response, html);
	}

}
