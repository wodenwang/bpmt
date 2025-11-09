/*
 * $HeadURL: $
 * $Id: $
 * Copyright (c) 2013 by Riversoft System, all rights reserved.
 */
package com.riversoft.module.development;

import java.io.Serializable;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;

import com.riversoft.core.BeanFactory;
import com.riversoft.core.IDGenerator;
import com.riversoft.core.db.DataCondition;
import com.riversoft.core.db.DataPO;
import com.riversoft.core.db.DataPackage;
import com.riversoft.core.db.ORMService;
import com.riversoft.core.exception.ExceptionType;
import com.riversoft.core.exception.SystemRuntimeException;
import com.riversoft.core.web.Actions;
import com.riversoft.core.web.Actions.Util;
import com.riversoft.core.web.RequestUtils;
import com.riversoft.core.web.annotation.ActionAccess;
import com.riversoft.core.web.annotation.ActionAccess.SafeLevel;

/**
 * 函数管理
 * 
 * @author Woden
 * 
 */
@ActionAccess(level = SafeLevel.DEV_R)
public class FunctionAction {

	/**
	 * 数据字典管理
	 * 
	 * @param request
	 * @param response
	 */
	public void index(HttpServletRequest request, HttpServletResponse response) {
		Actions.includePage(request, response, Util.getPagePath(request, "main.jsp"));
	}

	/**
	 * 展示类别树
	 * 
	 * @param request
	 * @param response
	 */
	@SuppressWarnings("unchecked")
	public void catelogs(HttpServletRequest request, HttpServletResponse response) {
		String cp = Actions.Util.getContextPath(request);

		// 查找catelog
		List<Map<String, Object>> tree = ORMService.getInstance().query("DevFunctionCatelog",
				new DataCondition().setOrderByAsc("sort").toEntity());
		for (Map<String, Object> o : tree) {
			o.put("iconOpen", cp + "/css/icon/folder_font.png");
			o.put("iconClose", cp + "/css/icon/folder_font.png");
			o.put("icon", cp + "/css/icon/font.png");
		}

		request.setAttribute("tree", tree);
		Actions.includePage(request, response, Util.getPagePath(request, "catelog_tree.jsp"));
	}

	/**
	 * 创建类别
	 * 
	 * @param request
	 * @param response
	 */
	public void createCatelogZone(HttpServletRequest request, HttpServletResponse response) {
		Actions.includePage(request, response, Util.getPagePath(request, "catelog_form.jsp"));
	}

	/**
	 * 创建类别
	 * 
	 * @param request
	 * @param response
	 */
	@SuppressWarnings("unchecked")
	public void updateCatelogZone(HttpServletRequest request, HttpServletResponse response) {
		String cateKey = RequestUtils.getStringValue(request, "cateKey");

		Map<String, Object> vo = (Map<String, Object>) ORMService.getInstance().findByPk("DevFunctionCatelog", cateKey);
		if (vo == null) {
			throw new SystemRuntimeException(ExceptionType.BUSINESS, "类别[" + cateKey + "]不存在.");
		}
		request.setAttribute("vo", vo);

		Actions.includePage(request, response, Util.getPagePath(request, "catelog_form.jsp"));
	}

	/**
	 * 删除分类
	 * 
	 * @param request
	 * @param response
	 */
	@ActionAccess(level = SafeLevel.DEV_W)
	public void deleteCatelog(HttpServletRequest request, HttpServletResponse response) {
		FunctionService service = BeanFactory.getInstance().getSingleBean(FunctionService.class);
		String cateKey = RequestUtils.getStringValue(request, "cateKey");
		service.executeRemoteCatelogAndFunction(cateKey);
		Actions.redirectInfoPage(request, response, "删除成功.");
	}

	/**
	 * 保存字典类别表单
	 * 
	 * @param request
	 * @param response
	 */
	@ActionAccess(level = SafeLevel.DEV_W)
	@SuppressWarnings("unchecked")
	public void submitCatelogForm(HttpServletRequest request, HttpServletResponse response) {
		String cateKey = RequestUtils.getStringValue(request, "cateKey");
		DataPO po;
		boolean editFlag;
		if (StringUtils.isEmpty(cateKey)) {// 新增
			po = new DataPO("DevFunctionCatelog");
			po.set("cateKey", IDGenerator.next());
			editFlag = false;
		} else {// 修改
			po = new DataPO("DevFunctionCatelog", (Map<String, Object>) ORMService.getInstance().findByPk(
					"DevFunctionCatelog", cateKey));
			editFlag = true;
		}

		po.set("busiName", RequestUtils.getStringValue(request, "busiName"));
		po.set("parentKey", RequestUtils.getStringValue(request, "parentKey"));
		po.set("sort", RequestUtils.getIntegerValue(request, "sort"));
		po.set("description", RequestUtils.getStringValue(request, "description"));

		if (!editFlag) {// 新增
			ORMService.getInstance().save(po.toEntity());
		} else {// 修改
			ORMService.getInstance().update(po.toEntity());
		}

		Actions.redirectInfoPage(request, response, editFlag ? "修改成功." : "新增成功.");
	}

	/**
	 * 函数框架页
	 * 
	 * @param request
	 * @param response
	 */
	@SuppressWarnings("unchecked")
	public void functionMainZone(HttpServletRequest request, HttpServletResponse response) {
		String cateKey = RequestUtils.getStringValue(request, "cateKey");

		if (StringUtils.isNotEmpty(cateKey)) {
			Map<String, Object> vo = (Map<String, Object>) ORMService.getInstance().findByPk("DevFunctionCatelog",
					cateKey);
			if (vo != null) {
				request.setAttribute("vo", vo);
			}
		}

		Actions.includePage(request, response, Util.getPagePath(request, "function_main.jsp"));
	}

	/**
	 * 函数列表
	 * 
	 * @param request
	 * @param response
	 */
	public void functionList(HttpServletRequest request, HttpServletResponse response) {

		// 获取分页信息
		int start = Util.getStart(request);
		int limit = Util.getLimit(request);

		// 获取排序信息
		String field = Util.getSortField(request);
		String dir = Util.getSortDir(request);

		// 查询条件
		DataCondition condition = new DataCondition(Util.buildQueryMap(new HashMap<String, Object>(), request));
		condition.setOrderBy(field, dir);

		DataPackage dp = ORMService.getInstance().queryPackage("DevFunction", start, limit, condition.toEntity());
		// 设置到页面
		request.setAttribute("dp", dp);
		Actions.includePage(request, response, Util.getPagePath(request, "function_list.jsp"));
	}

	/**
	 * 删除字典数据
	 * 
	 * @param request
	 * @param response
	 */
	@ActionAccess(level = SafeLevel.DEV_W)
	public void functionDelete(HttpServletRequest request, HttpServletResponse response) {
		Serializable[] functionKeys = RequestUtils.getStringValues(request, "functionKey");
		if (functionKeys != null) {
			ORMService.getInstance().removeByPkBath("DevFunction", Arrays.asList(functionKeys));
		}
		Actions.redirectInfoPage(request, response, "删除成功.");
	}

	/**
	 * 新增字典数据
	 * 
	 * @param request
	 * @param response
	 */
	public void createFunctionZone(HttpServletRequest request, HttpServletResponse response) {
		String catelog = RequestUtils.getStringValue(request, "catelog");
		request.setAttribute("catelog", catelog);
		Actions.includePage(request, response, Util.getPagePath(request, "function_form.jsp"));
	}

	/**
	 * 编辑字典数据
	 * 
	 * @param request
	 * @param response
	 */
	@SuppressWarnings("unchecked")
	public void editFunctionZone(HttpServletRequest request, HttpServletResponse response) {
		String catelog = RequestUtils.getStringValue(request, "catelog");
		String functionKey = RequestUtils.getStringValue(request, "functionKey");
		request.setAttribute("catelog", catelog);
		Map<String, Object> vo = (Map<String, Object>) ORMService.getInstance().findByPk("DevFunction", functionKey);
		if (vo == null) {
			throw new SystemRuntimeException(ExceptionType.BUSINESS, "函数[" + functionKey + "]不存在.");
		}
		request.setAttribute("vo", vo);
		Actions.includePage(request, response, Util.getPagePath(request, "function_form.jsp"));
	}

	/**
	 * 提交字典数据
	 * 
	 * @param request
	 * @param response
	 */
	@ActionAccess(level = SafeLevel.DEV_W)
	@SuppressWarnings("unchecked")
	public void submitFunctionForm(HttpServletRequest request, HttpServletResponse response) {
		String functionKey = RequestUtils.getStringValue(request, "functionKey");
		DataPO po;
		boolean editFlag = RequestUtils.getIntegerValue(request, "editFlag").intValue() == 1;
		if (!editFlag) {// 新增
			po = new DataPO("DevFunction");
			po.set("functionKey", functionKey.trim());
		} else {
			po = new DataPO("DevFunction", (Map<String, Object>) ORMService.getInstance().findByPk("DevFunction",
					functionKey));
		}

		po.set("catelog", RequestUtils.getStringValue(request, "catelog"));
		po.set("functionType", RequestUtils.getIntegerValue(request, "functionType"));
		po.set("functionScript", RequestUtils.getStringValue(request, "functionScript"));
		po.set("description", RequestUtils.getStringValue(request, "description"));
		po.set("example", RequestUtils.getStringValue(request, "example"));

		if (editFlag) {
			ORMService.getInstance().update(po.toEntity());
		} else {
			ORMService.getInstance().save(po.toEntity());
		}

		Actions.redirectInfoPage(request, response, editFlag ? "编辑成功." : "新增成功.");
	}

	/**
	 * 展示脚本明细
	 * 
	 * @param request
	 * @param response
	 */
	@SuppressWarnings("unchecked")
	public void functionDetailTab(HttpServletRequest request, HttpServletResponse response) {
		String functionKey = RequestUtils.getStringValue(request, "functionKey");
		Map<String, Object> vo = (Map<String, Object>) ORMService.getInstance().findByPk("DevFunction", functionKey);
		if (vo == null) {
			throw new SystemRuntimeException(ExceptionType.BUSINESS, "函数[" + functionKey + "]不存在.");
		}
		request.setAttribute("vo", vo);
		Actions.includePage(request, response, Util.getPagePath(request, "function_detail_tab.jsp"));
	}

	/**
	 * 保存脚本与脚本类型
	 * 
	 * @param request
	 * @param response
	 */
	@ActionAccess(level = SafeLevel.DEV_W)
	@SuppressWarnings("unchecked")
	public void submitScript(HttpServletRequest request, HttpServletResponse response) {
		String functionKey = RequestUtils.getStringValue(request, "functionKey");
		Map<String, Object> vo = (Map<String, Object>) ORMService.getInstance().findByPk("DevFunction", functionKey);
		if (vo == null) {
			throw new SystemRuntimeException(ExceptionType.BUSINESS, "函数[" + functionKey + "]不存在.");
		}
		DataPO po = new DataPO("DevFunction", vo);
		po.set("functionType", RequestUtils.getIntegerValue(request, "functionType"));
		po.set("functionScript", RequestUtils.getStringValue(request, "functionScript"));

		ORMService.getInstance().update(po.toEntity());
		Actions.redirectInfoPage(request, response, "修改成功.");
	}

	/**
	 * 保存排序
	 * 
	 * @param request
	 * @param response
	 */
	@ActionAccess(level = SafeLevel.DEV_W)
	public void saveSort(HttpServletRequest request, HttpServletResponse response) {
		HashMap<String, Object> tree = RequestUtils.getJsonValue(request, "tree");
		FunctionService service = BeanFactory.getInstance().getBean(FunctionService.class);
		service.executeSaveSort((List<HashMap<String, Object>>) tree.get("catelogs"));
		Actions.redirectInfoPage(request, response, "保存成功.");
	}

}
