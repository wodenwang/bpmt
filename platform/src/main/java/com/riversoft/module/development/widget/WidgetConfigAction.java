/*
 * $HeadURL: $
 * $Id: $
 * Copyright (c) 2013 by Riversoft System, all rights reserved.
 */
package com.riversoft.module.development.widget;

import static com.riversoft.core.web.Actions.includePage;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;

import com.riversoft.core.BeanFactory;
import com.riversoft.core.db.DataCondition;
import com.riversoft.core.db.DataPackage;
import com.riversoft.core.db.ORMService;
import com.riversoft.core.exception.ExceptionType;
import com.riversoft.core.exception.SystemRuntimeException;
import com.riversoft.core.web.Actions;
import com.riversoft.core.web.Actions.Util;
import com.riversoft.core.web.RequestUtils;
import com.riversoft.core.web.annotation.ActionAccess;
import com.riversoft.core.web.annotation.ActionAccess.SafeLevel;
import com.riversoft.platform.po.CmPriGroupRelate;
import com.riversoft.platform.service.PriService;
import com.riversoft.platform.web.widget.BaseWidgetConfigHandler;
import com.riversoft.platform.web.widget.WidgetConfigBuilder;
import com.riversoft.platform.web.widget.WidgetConfigBuilder.ConfigVO;

/**
 * 动态控件配置
 * 
 * @author woden
 * 
 */
@ActionAccess(level = SafeLevel.DEV_R)
public class WidgetConfigAction {

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
		includePage(request, response, Util.getPagePath(request, "widget_main.jsp"));
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

		DataPackage dp = service.queryPackage("WdgBase", start, limit, condition.toEntity());
		// 设置到页面
		request.setAttribute("dp", dp);

		Actions.includePage(request, response, Util.getPagePath(request, "widget_list.jsp"));
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
		BeanFactory.getInstance().getSingleBean(WidgetConfigService.class).executeRemoveConfig(widgetKey);
		Actions.redirectInfoPage(request, response, "删除成功.");
	}

	/**
	 * 创建页面
	 * 
	 * @param request
	 * @param response
	 */
	public void createZone(HttpServletRequest request, HttpServletResponse response) {
		Actions.includePage(request, response, Actions.Util.getPagePath(request, "widget_form.jsp"));
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
		Map<String, Object> vo = (Map<String, Object>) service.findByPk("WdgBase", widgetKey);
		request.setAttribute("config", vo);
		Actions.includePage(request, response, Actions.Util.getPagePath(request, "widget_form.jsp"));
	}

	// 字段设置

	/**
	 * 字段表单配置
	 * 
	 * @param request
	 * @param response
	 */
	public void columnConfigForm(HttpServletRequest request, HttpServletResponse response) {
		String widgetKey = RequestUtils.getStringValue(request, "widgetKey");
		String cp = Util.getContextPath(request);

		Map<String, Object> config = null;
		if (!StringUtils.isEmpty(widgetKey)) {
			config = (Map<String, Object>) ORMService.getInstance().findByPk("WdgBase", widgetKey);
			request.setAttribute("config", config);
		}

		List<Map<String, Object>> list = new ArrayList<>();
		if (config != null) {
			for (Map<String, Object> obj : (Set<Map<String, Object>>) config.get("showColumns")) {
				obj.put("icon", cp + "/css/icon/application.png");
				obj.put("_type", "show");
				obj.put("_id", "show_" + obj.get("id"));
				obj.put("title", "展示字段");
				list.add(obj);
			}
			for (Map<String, Object> obj : (Set<Map<String, Object>>) config.get("formColumns")) {
				obj.put("icon", cp + "/css/icon/application_form.png");
				obj.put("_type", "form");
				obj.put("_id", "form_" + obj.get("id"));
				obj.put("title", "表单字段");
				list.add(obj);
			}
		}

		// 排序
		Collections.sort(list, new Comparator<Map<String, Object>>() {
			@Override
			public int compare(Map<String, Object> o1, Map<String, Object> o2) {
				return (Integer) o1.get("sort") < (Integer) o2.get("sort") ? -1 : 1;
			}
		});
		request.setAttribute("list", list);

		Actions.includePage(request, response, Actions.Util.getPagePath(request, "widget_column_config.jsp"));
	}

	/**
	 * 展示类字段表单
	 * 
	 * @param request
	 * @param response
	 */
	public void columnShowConfigForm(HttpServletRequest request, HttpServletResponse response) {
		Long id = RequestUtils.getLongValue(request, "id");
		if (id != null) {
			Map<String, Object> vo = (Map<String, Object>) ORMService.getInstance().findByPk("WdgBaseColumnShow", id);
			request.setAttribute("vo", vo);
		}

		Actions.includePage(request, response, Actions.Util.getPagePath(request, "widget_column_show.jsp"));
	}

	/**
	 * 表单字段配置
	 * 
	 * @param request
	 * @param response
	 */
	public void columnFormConfigForm(HttpServletRequest request, HttpServletResponse response) {
		Long id = RequestUtils.getLongValue(request, "id");
		if (id != null) {
			Map<String, Object> vo = (Map<String, Object>) ORMService.getInstance().findByPk("WdgBaseColumnForm", id);
			request.setAttribute("vo", vo);
		}

		Actions.includePage(request, response, Actions.Util.getPagePath(request, "widget_column_form.jsp"));
	}

	/**
	 * 查询条件表单配置
	 * 
	 * @param request
	 * @param response
	 */
	public void queryConfigForm(HttpServletRequest request, HttpServletResponse response) {
		String widgetKey = RequestUtils.getStringValue(request, "widgetKey");
		if (!StringUtils.isEmpty(widgetKey)) {
			Map<String, Object> config = (Map<String, Object>) ORMService.getInstance().findByPk("WdgBase", widgetKey);
			request.setAttribute("config", config);
		}

		Actions.includePage(request, response, Actions.Util.getPagePath(request, "widget_query_config.jsp"));
	}

	/**
	 * 高级查询条件
	 * 
	 * @param request
	 * @param response
	 */
	public void queryForm(HttpServletRequest request, HttpServletResponse response) {
		Actions.includePage(request, response, Actions.Util.getPagePath(request, "widget_query.jsp"));
	}

	/**
	 * 数据约束设置
	 * 
	 * @param request
	 * @param response
	 */
	public void limitConfigForm(HttpServletRequest request, HttpServletResponse response) {
		String widgetKey = RequestUtils.getStringValue(request, "widgetKey");
		if (!StringUtils.isEmpty(widgetKey)) {
			Map<String, Object> config = (Map<String, Object>) ORMService.getInstance().findByPk("WdgBase", widgetKey);
			request.setAttribute("config", config);
		}

		Actions.includePage(request, response, Actions.Util.getPagePath(request, "widget_limit_config.jsp"));
	}

	/**
	 * 添加数据约束
	 * 
	 * @param request
	 * @param response
	 */
	public void limitForm(HttpServletRequest request, HttpServletResponse response) {
		Actions.includePage(request, response, Actions.Util.getPagePath(request, "widget_limit.jsp"));
	}

	/**
	 * 扩展控件配置
	 * 
	 * @param request
	 * @param response
	 */
	public void extConfig(HttpServletRequest request, HttpServletResponse response) {
		String cp = Util.getContextPath(request);
		List<ConfigVO> configs = WidgetConfigBuilder.getInstance().getList();
		List<Map<String, Object>> list = new ArrayList<>();
		for (ConfigVO vo : configs) {
			Map<String, Object> o = new HashMap<>();
			o.put("name", vo.getName());
			o.put("description", vo.getDescription());
			o.put("icon", cp + "/css/icon/application_form.png");
			list.add(o);
		}
		request.setAttribute("list", list);

		Actions.includePage(request, response, Actions.Util.getPagePath(request, "widget_ext.jsp"));
	}

	/**
	 * 扩展控件配置
	 * 
	 * @param request
	 * @param response
	 */
	public void extForm(HttpServletRequest request, HttpServletResponse response) {
		ConfigVO configVO = WidgetConfigBuilder.getInstance().getConfigVO(RequestUtils.getStringValue(request, "name"));
		BaseWidgetConfigHandler handler = (BaseWidgetConfigHandler) BeanFactory.getInstance().getSingleBean(
				configVO.getClazz());

		handler.form(request, response, RequestUtils.getStringValue(request, "widgetKey"));
	}

	/**
	 * 代理调用
	 * 
	 * @param request
	 * @param response
	 */
	public void extDelegate(HttpServletRequest request, HttpServletResponse response) {
		ConfigVO configVO = WidgetConfigBuilder.getInstance().getConfigVO(RequestUtils.getStringValue(request, "name"));
		BaseWidgetConfigHandler handler = (BaseWidgetConfigHandler) BeanFactory.getInstance().getSingleBean(
				configVO.getClazz());
		String methodName = RequestUtils.getStringValue(request, "method");

		try {
			Method method = handler.getClass().getMethod(methodName, HttpServletRequest.class,
					HttpServletResponse.class);
			method.invoke(handler, request, response);
		} catch (Exception e) {
			throw new SystemRuntimeException(ExceptionType.BUSINESS, "扩展控件配置页面出错.", e);
		}

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
		BeanFactory.getInstance().getSingleBean(WidgetConfigService.class).executeSaveOrUpdateConfig(widgetKey);
		Actions.redirectInfoPage(request, response, isCreate ? "创建成功." : "更新成功.");
	}

	/**
	 * 权限设置界面
	 * 
	 * @param request
	 * @param response
	 */
	public void priSetZone(HttpServletRequest request, HttpServletResponse response) {
		List<Map<String, Object>> groups = ORMService.getInstance().query("CmPriGroup",
				new DataCondition().setOrderByAsc("sort").toEntity());
		request.setAttribute("groups", groups);
		request.setAttribute("action", Actions.Util.getActionUrl(request) + "/priList.shtml");
		Actions.includePage(request, response, Actions.Util.getPagePath(request, "widget_pri.jsp"));
	}

	/**
	 * 权限资源列表
	 * 
	 * @param request
	 * @param response
	 */
	public void priList(HttpServletRequest request, HttpServletResponse response) {
		String widgetKey = RequestUtils.getStringValue(request, "widgetKey");
		String groupId = RequestUtils.getStringValue(request, "groupId");

		List<String> priKeys = ORMService.getInstance().queryHQL(
				"select priKey from CmPriGroupRelate where groupId = ?", groupId);
		request.setAttribute("priKeys", priKeys);

		Map<String, Object> config = (Map<String, Object>) service.findByPk("WdgBase", widgetKey);
		request.setAttribute("config", config);
		List<Map<String, Object>> fields = new ArrayList<>();
		fields.addAll((Set<Map<String, Object>>) config.get("formColumns"));
		fields.addAll((Set<Map<String, Object>>) config.get("showColumns"));
		Collections.sort(fields, new Comparator<Map<String, Object>>() {
			@Override
			public int compare(Map<String, Object> o1, Map<String, Object> o2) {
				return (Integer) o1.get("sort") < (Integer) o2.get("sort") ? -1 : 1;
			}
		});
		request.setAttribute("fields", fields);

		request.setAttribute("groupId", groupId);
		Actions.includePage(request, response, Actions.Util.getPagePath(request, "widget_pri_list.jsp"));
	}

	/**
	 * 保存权限设置
	 * 
	 * @param request
	 * @param response
	 */
	@ActionAccess(level = SafeLevel.DEV_W)
	public void submitPris(HttpServletRequest request, HttpServletResponse response) {
		String groupId = RequestUtils.getStringValue(request, "groupId");
		List<CmPriGroupRelate> relates = RequestUtils.getValues(request, "relate", CmPriGroupRelate.class);
		String[] checkedPriKeys = RequestUtils.getStringValues(request, "priKey");

		PriService service = BeanFactory.getInstance().getSingleBean(PriService.class);
		service.executePriGroupRelate(groupId, checkedPriKeys, relates);

		Actions.redirectInfoPage(request, response, "保存成功.");
	}

}
