/*
 * $HeadURL: $
 * $Id: $
 * Copyright (c) 2013 by Riversoft System, all rights reserved.
 */
package com.riversoft.module.development.view;

import static com.riversoft.core.web.Actions.includePage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import com.riversoft.platform.po.CmPri;
import com.riversoft.platform.po.CmPriGroupRelate;
import com.riversoft.platform.po.VwUrl;
import com.riversoft.platform.service.PriService;
import com.riversoft.platform.web.view.ViewActionBuilder;
import com.riversoft.platform.web.view.ViewActionBuilder.ViewVO;
import com.riversoft.platform.web.view.annotation.PriConfigMethod;
import com.riversoft.platform.web.view.annotation.View.LoginType;

/**
 * 动态视图配置
 * 
 * @author Woden
 * 
 */
@ActionAccess(level = SafeLevel.DEV_R)
public class ViewConfigAction {

	private ViewConfigService service = BeanFactory.getInstance().getSingleBean(ViewConfigService.class);

	/**
	 * 动态视图管理
	 * 
	 * @param request
	 * @param response
	 */
	public void index(HttpServletRequest request, HttpServletResponse response) {
		request.setAttribute("moduleGroups", ViewActionBuilder.getInstance().getViewModuleGroup());
		includePage(request, response, Util.getPagePath(request, "view_main.jsp"));
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

		DataPackage dp = service.queryPackage(VwUrl.class.getName(), start, limit, condition.toEntity());
		// 设置到页面
		request.setAttribute("dp", dp);

		// 绑定Map
		Map<String, ViewVO> moduleMap = new HashMap<>();
		for (ViewVO viewVO : ViewActionBuilder.getInstance().getViewModuleList()) {
			moduleMap.put(viewVO.getName(), viewVO);
		}
		request.setAttribute("moduleMap", moduleMap);

		Actions.includePage(request, response, Util.getPagePath(request, "view_list.jsp"));
	}

	/**
	 * 删除动态视图
	 * 
	 * @param request
	 * @param response
	 */
	@ActionAccess(level = SafeLevel.DEV_W)
	public void delete(HttpServletRequest request, HttpServletResponse response) {
		String viewKey = RequestUtils.getStringValue(request, "viewKey");
		service.executeRemoveConfig(viewKey);
		Actions.redirectInfoPage(request, response, "删除成功.");
	}

	/**
	 * 创建页面
	 * 
	 * @param request
	 * @param response
	 */
	public void createZone(HttpServletRequest request, HttpServletResponse response) {
		request.setAttribute("moduleGroups", ViewActionBuilder.getInstance().getViewModuleGroup());
		Actions.includePage(request, response, Actions.Util.getPagePath(request, "view_form.jsp"));
	}

	/**
	 * 获取模块对应登录类型信息
	 * 
	 * @param request
	 * @param response
	 */
	public void loginTypeGetter(HttpServletRequest request, HttpServletResponse response) {
		String viewClass = RequestUtils.getStringValue(request, "viewClass");
		ViewVO viewVO = ViewActionBuilder.getInstance().getViewModule(viewClass);
		Map<String, Object> map = new HashMap<>();
		List<Object> list = new ArrayList<>();
		for (LoginType loginType : viewVO.getLoginType()) {
			list.add(loginType.getCode());
		}

		map.put("types", list);
		Actions.showJson(request, response, map);
	}

	/**
	 * 模块管理信息
	 * 
	 * @param request
	 * @param response
	 */
	public void configZone(HttpServletRequest request, HttpServletResponse response) {
		String viewClass = RequestUtils.getStringValue(request, "viewClass");
		String action = Actions.Util.getActionUrl(ViewActionBuilder.getInstance().getViewClass(viewClass));
		Actions.redirectAction(request, response, action + "/configForm.shtml");
	}

	/**
	 * 更新页面
	 * 
	 * @param request
	 * @param response
	 */
	public void updateZone(HttpServletRequest request, HttpServletResponse response) {
		String viewKey = RequestUtils.getStringValue(request, "viewKey");
		VwUrl vo = (VwUrl) service.findByPk(VwUrl.class.getName(), viewKey);
		request.setAttribute("vo", vo);

		// loginType
		ViewVO viewVO = ViewActionBuilder.getInstance().getViewModule(vo.getViewClass());
		List<String> list = new ArrayList<>();
		for (LoginType loginType : viewVO.getLoginType()) {
			list.add(String.valueOf(loginType.getCode()));
		}
		request.setAttribute("loginTypes", StringUtils.join(list, ";"));

		String action = Actions.Util.getActionUrl(ViewActionBuilder.getInstance().getViewClass(vo.getViewClass()));
		request.setAttribute("action", action + "/configForm.shtml");
		request.setAttribute("module", ViewActionBuilder.getInstance().getViewModule(vo.getViewClass()));
		Actions.includePage(request, response, Actions.Util.getPagePath(request, "view_form.jsp"));
	}

	/**
	 * 提交表单
	 * 
	 * @param request
	 * @param response
	 */
	@ActionAccess(level = SafeLevel.DEV_W)
	public void submit(HttpServletRequest request, HttpServletResponse response) {
		String viewKey = RequestUtils.getStringValue(request, "viewKey");
		boolean isCreate = StringUtils.isEmpty(viewKey);// 是否新增
		String description = RequestUtils.getStringValue(request, "description");
		String viewClass = RequestUtils.getStringValue(request, "viewClass");
		Integer loginType = RequestUtils.getIntegerValue(request, "loginType");

		VwUrl vwUrl = new VwUrl();
		vwUrl.setDescription(description);
		vwUrl.setLoginType(loginType);
		if (isCreate) {
			vwUrl.setViewClass(viewClass);
			service.executeSaveConfig(vwUrl);
		} else {
			vwUrl.setViewKey(viewKey);
			service.executeUpdateConfig(vwUrl);
		}

		Actions.redirectInfoPage(request, response, isCreate ? "创建成功." : "更新成功.");
	}

	/**
	 * 权限设置界面
	 * 
	 * @param request
	 * @param response
	 */
	@SuppressWarnings("unchecked")
	public void priSetZone(HttpServletRequest request, HttpServletResponse response) {
		List<Map<String, Object>> groups = ORMService.getInstance().query("CmPriGroup",
				new DataCondition().setOrderByAsc("sort").toEntity());
		String cp = Actions.Util.getContextPath(request);
		for (Map<String, Object> o : groups) {
			if (((Integer) o.get("leafFlag")).intValue() == 1) {// 叶子
				o.put("icon", cp + "/css/icon/user_key.png");
			} else {
				o.put("icon", cp + "/css/icon/folder_key.png");
			}
		}
		request.setAttribute("groups", groups);

		String viewKey = RequestUtils.getStringValue(request, "viewKey");
		VwUrl vo = (VwUrl) service.findByPk(VwUrl.class.getName(), viewKey);
		PriConfigMethod annotation = ViewActionBuilder.getInstance().getViewClass(vo.getViewClass())
				.getAnnotation(PriConfigMethod.class);
		if (annotation == null) {// 默认设置方法
			request.setAttribute("action", Actions.Util.getActionUrl(request) + "/priList.shtml");
		} else {
			String action = Actions.Util.getActionUrl(ViewActionBuilder.getInstance().getViewClass(vo.getViewClass()));
			request.setAttribute("action", action + "/" + annotation.value() + ".shtml");
		}

		Actions.includePage(request, response, Actions.Util.getPagePath(request, "view_pri.jsp"));
	}

	/**
	 * 权限资源列表
	 * 
	 * @param request
	 * @param response
	 */
	public void priList(HttpServletRequest request, HttpServletResponse response) {
		String viewKey = RequestUtils.getStringValue(request, "viewKey");
		String groupId = RequestUtils.getStringValue(request, "groupId");

		List<String> priKeys = ORMService.getInstance().queryHQL(
				"select priKey from CmPriGroupRelate where groupId = ?", groupId);
		request.setAttribute("priKeys", priKeys);

		DataCondition condition = new DataCondition();
		condition.setStringEqual("catelogKey", viewKey);
		condition.setOrderByAsc("busiName");
		List<Map<String, Object>> pris = ORMService.getInstance().query(CmPri.class.getName(), condition.toEntity());
		request.setAttribute("pris", pris);
		request.setAttribute("groupId", groupId);
		Actions.includePage(request, response, Actions.Util.getPagePath(request, "view_pri_list.jsp"));
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

	/**
	 * 可复制下拉框
	 * 
	 * @param request
	 * @param response
	 */
	public void copyConfig(HttpServletRequest request, HttpServletResponse response) {
		String viewClass = RequestUtils.getStringValue(request, "viewClass");
		// 查询可用列表
		List<?> list = ORMService.getInstance().query(VwUrl.class.getName(),
				new DataCondition().setStringEqual("viewClass", viewClass).toEntity());
		request.setAttribute("list", list);
		String action = Actions.Util.getActionUrl(ViewActionBuilder.getInstance().getViewClass(viewClass));
		request.setAttribute("action", action + "/configForm.shtml");
		Actions.includePage(request, response, Actions.Util.getPagePath(request, "view_copy_config.jsp"));
	}

	/**
	 * 锁定/解锁
	 * 
	 * @param request
	 * @param response
	 */
	@ActionAccess(level = SafeLevel.DEV_W)
	public void lock(HttpServletRequest request, HttpServletResponse response) {
		VwUrl url = (VwUrl) ORMService.getInstance().findByPk(VwUrl.class.getName(),
				RequestUtils.getStringValue(request, "viewKey"));
		url.setLockFlag(RequestUtils.getIntegerValue(request, "lockFlag"));
		ORMService.getInstance().updatePO(url);
		Actions.redirectInfoPage(request, response, "操作成功.");
	}

	/**
	 * 视图复制
	 * 
	 * @param request
	 * @param response
	 */
	@ActionAccess(level = SafeLevel.DEV_W)
	public void copy(HttpServletRequest request, HttpServletResponse response) {
		String targetViewKey = service.executeCopyConfig(RequestUtils.getStringValue(request, "viewKey"));
		Actions.redirectInfoPage(request, response, "复制视图成功,请查看[" + targetViewKey + "].");
	}
}
