/*
 * $HeadURL: $
 * $Id: $
 * Copyright (c) 2016 by Riversoft System, all rights reserved.
 */
package com.riversoft.module.development.widget;

import static com.riversoft.core.web.Actions.includePage;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;

import com.riversoft.core.BeanFactory;
import com.riversoft.core.db.DataCondition;
import com.riversoft.core.db.DataPackage;
import com.riversoft.core.db.ORMService;
import com.riversoft.core.web.Actions;
import com.riversoft.core.web.Actions.Util;
import com.riversoft.core.web.RequestUtils;
import com.riversoft.core.web.annotation.ActionAccess;
import com.riversoft.core.web.annotation.ActionAccess.SafeLevel;

/**
 * 模板控件
 * 
 * @author woden
 *
 */
@ActionAccess(level = SafeLevel.DEV_R)
public class TemplateWidgetAction {

	/**
	 * service对象
	 */
	private ORMService service = ORMService.getInstance();

	/**
	 * 明细控件
	 * 
	 * @param request
	 * @param response
	 */
	public void index(HttpServletRequest request, HttpServletResponse response) {
		includePage(request, response, Util.getPagePath(request, "widget_template_main.jsp"));
	}

	/**
	 * 列表
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

		DataPackage dp = service.queryPackage("WdgTemplate", start, limit, condition.toEntity());
		// 设置到页面
		request.setAttribute("dp", dp);

		Actions.includePage(request, response, Util.getPagePath(request, "widget_template_list.jsp"));
	}

	/**
	 * 删除自定义控件
	 * 
	 * @param request
	 * @param response
	 */
	@ActionAccess(level = SafeLevel.DEV_W)
	public void delete(HttpServletRequest request, HttpServletResponse response) {
		String widgetKey = RequestUtils.getStringValue(request, "widgetKey");
		BeanFactory.getInstance().getSingleBean(TemplateWidgetService.class).executeRemoveConfig(widgetKey);
		Actions.redirectInfoPage(request, response, "删除成功.");
	}

	/**
	 * 创建页面
	 * 
	 * @param request
	 * @param response
	 */
	public void createZone(HttpServletRequest request, HttpServletResponse response) {
		Actions.includePage(request, response, Actions.Util.getPagePath(request, "widget_template_form.jsp"));
	}

	/**
	 * 更新页面
	 * 
	 * @param request
	 * @param response
	 */
	@SuppressWarnings("unchecked")
	public void updateZone(HttpServletRequest request, HttpServletResponse response) {
		String widgetKey = RequestUtils.getStringValue(request, "widgetKey");
		Map<String, Object> vo = (Map<String, Object>) service.findByPk("WdgTemplate", widgetKey);
		request.setAttribute("config", vo);
		Actions.includePage(request, response, Actions.Util.getPagePath(request, "widget_template_form.jsp"));
	}

	/**
	 * 变量配置
	 * 
	 * @param request
	 * @param response
	 */
	@SuppressWarnings("unchecked")
	public void varConfigForm(HttpServletRequest request, HttpServletResponse response) {
		String key = RequestUtils.getStringValue(request, "widgetKey");
		if (!StringUtils.isEmpty(key)) {
			Map<String, Object> table = (Map<String, Object>) ORMService.getInstance().findByPk("WdgTemplate", key);
			if (table != null) {
				request.setAttribute("vars", (Set<Map<String, Object>>) table.get("vars"));
			}
		}

		Actions.includePage(request, response, Actions.Util.getPagePath(request, "widget_template_var_config.jsp"));
	}

	/**
	 * 增加变量
	 * 
	 * @param request
	 * @param response
	 */
	public void varsForm(HttpServletRequest request, HttpServletResponse response) {
		Actions.includePage(request, response, Util.getPagePath(request, "widget_template_var_prepare.jsp"));
	}
	
	/**
	 * 提交表单
	 * 
	 * @param request
	 * @param response
	 */
	@ActionAccess(level = SafeLevel.DEV_W)
	public void submit(HttpServletRequest request, HttpServletResponse response) {
		String widgetKey = RequestUtils.getStringValue(request, "widgetKey");
		boolean isCreate = StringUtils.isEmpty(widgetKey);// 是否新增
		BeanFactory.getInstance().getSingleBean(TemplateWidgetService.class).executeSaveOrUpdateConfig(widgetKey);
		Actions.redirectInfoPage(request, response, isCreate ? "创建成功." : "更新成功.");
	}
}
