/*
 * $HeadURL: $
 * $Id: $
 * Copyright (c) 2013 by Riversoft System, all rights reserved.
 */
package com.riversoft.module.widget;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;

import com.riversoft.core.db.DataCondition;
import com.riversoft.core.db.DataPackage;
import com.riversoft.core.db.ORMService;
import com.riversoft.core.web.Actions;
import com.riversoft.core.web.Actions.Util;
import com.riversoft.core.web.RequestUtils;
import com.riversoft.core.web.widget.AnnotatedWidgetProcessorsHolder;
import com.riversoft.platform.po.TbTable;
import com.riversoft.platform.web.widget.WidgetConfigBuilder;
import com.riversoft.platform.web.widget.WidgetConfigBuilder.ConfigVO;

/**
 * 控件选择器(设计器)
 * 
 * @author Woden
 * 
 */
public class WidgetAction {

	/**
	 * 设计验证字符串
	 * 
	 * @param request
	 * @param response
	 */
	public void designValidate(HttpServletRequest request, HttpServletResponse response) {

		String cmd = RequestUtils.getStringValue(request, "cmd");
		String validator = "{}";
		if (StringUtils.isNotEmpty(cmd)) {
			if (cmd.indexOf("{") > 0 && cmd.lastIndexOf("}") > 0) {
				validator = cmd.substring(cmd.indexOf("{"), cmd.lastIndexOf("}") + 1);
			}
		}
		request.setAttribute("validator", validator);

		Actions.includePage(request, response, Util.getPagePath(request, "widget_validator_design.jsp"));
	}

	/**
	 * 控件设计
	 * 
	 * @param request
	 * @param response
	 */
	public void designWidget(HttpServletRequest request, HttpServletResponse response) {
		String cmd = RequestUtils.getStringValue(request, "cmd");
		if (StringUtils.isNotEmpty(cmd)) {
			if (cmd.indexOf("{") > 0) {
				cmd = cmd.substring(0, cmd.indexOf("{"));
			}
		}
		request.setAttribute("cmd", cmd);

		Actions.includePage(request, response, Util.getPagePath(request, "widget_design.jsp"));
	}

	/**
	 * 数据字典展示
	 * 
	 * @param request
	 * @param response
	 */
	public void list(HttpServletRequest request, HttpServletResponse response) {
		// 获取分页信息
		int start = Util.getStart(request);
		int limit = Util.getLimit(request);

		// 获取排序信息
		String field = Util.getSortField(request);
		String dir = Util.getSortDir(request);

		// 查询条件
		DataCondition condition = new DataCondition(Util.buildQueryMap(new HashMap<String, Object>(), request));
		condition.setOrderBy(field, dir);

		DataPackage dp = ORMService.getInstance().queryPackage("CmBaseType", start, limit, condition.toEntity());
		// 设置到页面
		request.setAttribute("dp", dp);

		Actions.includePage(request, response, Util.getPagePath(request, "widget_design_list.jsp"));
	}

	/**
	 * 自定义控件列表
	 * 
	 * @param request
	 * @param response
	 */
	public void vwWidgetList(HttpServletRequest request, HttpServletResponse response) {
		// 获取分页信息
		int start = Util.getStart(request);
		int limit = Util.getLimit(request);

		// 获取排序信息
		String field = Util.getSortField(request);
		String dir = Util.getSortDir(request);

		// 查询条件
		DataCondition condition = new DataCondition(Util.buildQueryMap(new HashMap<String, Object>(), request));
		condition.setOrderBy(field, dir);

		DataPackage dp = ORMService.getInstance().queryPackage("WdgBase", start, limit, condition.toEntity());
		// 设置到页面
		request.setAttribute("dp", dp);

		List<ConfigVO> configs = WidgetConfigBuilder.getInstance().getList();
		request.setAttribute("widgets", configs);

		Actions.includePage(request, response, Util.getPagePath(request, "widget_design_vwwidget.jsp"));
	}

	/**
	 * 控件预览
	 * 
	 * @param request
	 * @param response
	 */
	public void preview(HttpServletRequest request, HttpServletResponse response) {
		String cmd = RequestUtils.getStringValue(request, "cmd");
		if (cmd.indexOf("{") > 0) {
			cmd = cmd.substring(0, cmd.indexOf("{"));
		}
		request.setAttribute("cmd", cmd);
		if (cmd.indexOf("[") > 0) {
			cmd = cmd.substring(0, cmd.indexOf("["));
		}
		request.setAttribute("doc", AnnotatedWidgetProcessorsHolder.getInstance().getWidgetDocs().get(cmd));

		Actions.includePage(request, response, Util.getPagePath(request, "widget_design_preview.jsp"));
	}

	/**
	 * 动态表控件
	 * 
	 * @param request
	 * @param response
	 */
	public void dynTable(HttpServletRequest request, HttpServletResponse response) {
		Actions.includePage(request, response, Util.getPagePath(request, "widget_design_dyntable.jsp"));
	}

	/**
	 * 查找动态表的列
	 * 
	 * @param request
	 * @param response
	 */
	public void dynTableJson(HttpServletRequest request, HttpServletResponse response) {
		String table = RequestUtils.getStringValue(request, "table");
		TbTable tbTable = (TbTable) ORMService.getInstance().findByPk(TbTable.class.getName(), table);
		Map<String, Object> result = new HashMap<String, Object>();
		result.put("columns", tbTable.getTbColumns());
		Actions.showJson(request, response, result);
	}
}
