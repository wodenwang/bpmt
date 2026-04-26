/*
 * $HeadURL: $
 * $Id: $
 * Copyright (c) 2013 by Riversoft System, all rights reserved.
 */
package com.riversoft.module.widget;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.riversoft.util.jackson.JsonMapper;
import org.apache.commons.lang3.StringUtils;

import com.riversoft.core.db.DataCondition;
import com.riversoft.core.db.ORMService;
import com.riversoft.core.web.Actions;
import com.riversoft.core.web.Actions.Util;
import com.riversoft.core.web.RequestUtils;
import com.riversoft.platform.po.VwUrl;
import com.riversoft.platform.web.view.ViewActionBuilder;
import com.riversoft.platform.web.view.ViewActionBuilder.SysVO;
import com.riversoft.platform.web.view.ViewActionBuilder.ViewVO;
import com.riversoft.platform.web.view.annotation.Conf.TargetType;

/**
 * 视图选择控件
 * 
 * @author woden
 * 
 */
public class ViewAction {

	/**
	 * 分发
	 * 
	 * @param request
	 * @param response
	 */
	public void index(HttpServletRequest request, HttpServletResponse response) {
		if ("view".equalsIgnoreCase(RequestUtils.getStringValue(request, "type"))) {
			Actions.forwardAction(request, response, Util.getActionUrl(request) + "/view.shtml");
		} else {
			Actions.forwardAction(request, response, Util.getActionUrl(request) + "/sys.shtml");
		}
	}

	/**
	 * 选择视图
	 * 
	 * @param request
	 * @param response
	 */
	@SuppressWarnings("unchecked")
	public void view(HttpServletRequest request, HttpServletResponse response) {
		final String cp = Actions.Util.getContextPath(request);
		List<Map<String, Object>> treeList = new ArrayList<>();

		String target = RequestUtils.getStringValue(request, "target");

		// 视图分类
		for (ViewVO module : ViewActionBuilder.getInstance().getViewModuleList(TargetType.valueOf(target))) {
			// 增加视图根节点
			Map<String, Object> obj = new HashMap<>();
			obj.put("name", module.getDescription());
			obj.put("id", "_view_" + module.getName());
			obj.put("parentId", null);
			obj.put("isModule", false);
			obj.put("icon", cp + "/css/icon/folder_picture.png");
			treeList.add(obj);

			// 查找视图
			List<VwUrl> views = ORMService.getInstance().query(
					VwUrl.class.getName(),
					new DataCondition().setStringEqual("viewClass", module.getName()).setOrderByDesc("updateDate")
							.toEntity());
			for (VwUrl view : views) {
				Map<String, Object> o = new HashMap<>();
				o.put("id", "view_" + view.getViewKey());
				o.put("action", "/" + view.getViewKey() + ".view");// 实际网址
				o.put("name", view.getDescription());// 展示名
				o.put("parentId", "_view_" + view.getViewClass());
				o.put("isModule", true);
				o.put("icon", cp + "/css/icon/picture.png");
				treeList.add(o);
			}
		}

		request.setAttribute("tree", JsonMapper.defaultMapper().toJson(treeList));
		Actions.includePage(request, response, Util.getPagePath(request, "selector.jsp"));
	}

	/**
	 * 选择系统内置模块
	 * 
	 * @param request
	 * @param response
	 */
	public void sys(HttpServletRequest request, HttpServletResponse response) {
		final String cp = Actions.Util.getContextPath(request);
		List<Map<String, Object>> treeList = new ArrayList<>();

		String target = RequestUtils.getStringValue(request, "target");

		// 根
		Map<String, Object> obj = new HashMap<>();
		obj.put("name", "系统内置视图");
		obj.put("id", "_sys_");
		obj.put("parentId", null);
		obj.put("isModule", false);
		obj.put("icon", cp + "/css/icon/folder_picture.png");
		treeList.add(obj);

		// 视图分类
		for (SysVO module : ViewActionBuilder.getInstance().getSysModuleList(TargetType.valueOf(target))) {
			Map<String, Object> o = new HashMap<>();
			o.put("id", "sys_" + module.getName());
			o.put("action", module.getUrl());// 实际网址
			o.put("name", module.getDescription());// 展示名
			o.put("parentId", "_sys_");
			o.put("isModule", true);
			o.put("icon", cp + "/css/icon/picture.png");
			treeList.add(o);
		}

		request.setAttribute("tree", JsonMapper.defaultMapper().toJson(treeList));
		Actions.includePage(request, response, Util.getPagePath(request, "selector.jsp"));

	}

	/**
	 * 展示DOC信息
	 * 
	 * @param request
	 * @param response
	 */
	public void doc(HttpServletRequest request, HttpServletResponse response) {
		String action = RequestUtils.getStringValue(request, "action");
		if (StringUtils.isNotEmpty(action)) {
			if (action.endsWith(".view")) {
				String viewKey = action.substring(1, action.lastIndexOf("."));
				VwUrl vwUrl = (VwUrl) ORMService.getInstance().findByPk(VwUrl.class.getName(), viewKey);
				if (vwUrl != null) {
					ViewVO vo = ViewActionBuilder.getInstance().getViewModule(vwUrl.getViewClass());
					request.setAttribute("vo", vo);
				}
			} else if (action.endsWith(".shtml")) {
				SysVO vo = ViewActionBuilder.getInstance().getSysModule(action);
				request.setAttribute("vo", vo);
			}
		}

		Actions.includePage(request, response, Util.getPagePath(request, "doc.jsp"));
	}
}
