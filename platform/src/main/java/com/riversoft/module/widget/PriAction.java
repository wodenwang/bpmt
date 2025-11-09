/*
 * $HeadURL: $
 * $Id: $
 * Copyright (c) 2013 by Riversoft System, all rights reserved.
 */
package com.riversoft.module.widget;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;

import com.riversoft.core.IDGenerator;
import com.riversoft.core.db.DataCondition;
import com.riversoft.core.db.ORMService;
import com.riversoft.core.exception.ExceptionType;
import com.riversoft.core.exception.SystemRuntimeException;
import com.riversoft.core.web.Actions;
import com.riversoft.core.web.RequestUtils;
import com.riversoft.platform.po.CmPri;
import com.riversoft.platform.po.CmPriGroupRelate;
import com.riversoft.platform.script.ScriptTypes;
import com.riversoft.util.ValueConvertUtils;

/**
 * @author Woden
 * 
 */
public class PriAction {

	/**
	 * 权限功能点
	 * 
	 * @param request
	 * @param response
	 */
	public void index(HttpServletRequest request, HttpServletResponse response) {
		String pri = RequestUtils.getStringValue(request, "pri");
		CmPri cmPri;
		if (StringUtils.isNotEmpty(pri)) {
			cmPri = ValueConvertUtils.convert(pri, CmPri.class);
		} else {
			cmPri = new CmPri();
			cmPri.setPriKey(IDGenerator.uuid());
			cmPri.setCheckType((Integer) ScriptTypes.EL.getCode());
			cmPri.setCheckScript("${true}");
		}
		request.setAttribute("pri", cmPri);

		Actions.includePage(request, response, Actions.Util.getPagePath(request, "pri_main.jsp"));
	}

	/**
	 * 查询并展示功能点
	 * 
	 * @param request
	 * @param response
	 */
	public void find(HttpServletRequest request, HttpServletResponse response) {
		String priKey = RequestUtils.getStringValue(request, "priKey");
		CmPri pri = (CmPri) ORMService.getInstance().findByPk(CmPri.class.getName(), priKey);

		request.setAttribute("disabledAll", 1);// 禁止所有表单
		request.setAttribute("pri", pri);
		Actions.includePage(request, response, Actions.Util.getPagePath(request, "pri_main.jsp"));
	}

	/**
	 * 权限组关系
	 * 
	 * @param request
	 * @param response
	 */
	@SuppressWarnings("unchecked")
	public void relate(HttpServletRequest request, HttpServletResponse response) {
		String groupId = RequestUtils.getStringValue(request, "groupId");
		String priKey = RequestUtils.getStringValue(request, "priKey");
		HashMap<String, Object> json = RequestUtils.getJsonValue(request, "json");

		Map<String, Object> group = (Map<String, Object>) ORMService.getInstance().findByPk("CmPriGroup", groupId);
		if (group == null) {
			throw new SystemRuntimeException(ExceptionType.BUSINESS, "找不到权限组.");
		}
		request.setAttribute("group", group);

		CmPri pri = (CmPri) ORMService.getInstance().findByPk(CmPri.class.getName(), priKey);
		if (pri == null) {
			throw new SystemRuntimeException(ExceptionType.BUSINESS, "找不到功能点.");
		}
		request.setAttribute("pri", pri);

		CmPriGroupRelate vo = (CmPriGroupRelate) ORMService.getInstance().find(CmPriGroupRelate.class.getName(),
				new DataCondition().setStringEqual("groupId", groupId).setStringEqual("priKey", priKey).toEntity());
		if (vo == null) {
			vo = new CmPriGroupRelate();
		}

		if (json != null) {
			if (json.get("checkType") != null) {
				vo.setCheckType(Integer.parseInt(json.get("checkType").toString()));
			}

			if (json.containsKey("checkScript")) {
				vo.setCheckScript((String) json.get("checkScript"));
			}

			if (json.containsKey("description")) {
				vo.setDescription((String) json.get("description"));
			}
		}

		request.setAttribute("vo", vo);

		Actions.includePage(request, response, Actions.Util.getPagePath(request, "pri_group.jsp"));
	}

	/**
	 * 递归设置权限组
	 * 
	 * @param tips
	 * @param group
	 */
	private void setGroup(List<String> tips, Map<String, Object> group) {
		if (group != null) {
			tips.add((String) group.get("name"));
		}
		if (StringUtils.isNotEmpty((String) group.get("parentId"))) {
			setGroup(tips, (Map<String, Object>) ORMService.getInstance().findByPk("CmPriGroup", (String) group.get("parentId")));
		}
	}

	/**
	 * 快速关联查看
	 * 
	 * @param request
	 * @param response
	 */
	public void quickRelate(HttpServletRequest request, HttpServletResponse response) {
		List<String> relates = ORMService.getInstance().queryHQL("select groupId from " + CmPriGroupRelate.class.getName() + " where priKey = ?", RequestUtils.getStringValue(request, "priKey"));
		List<Map<String, Object>> list;
		if (relates != null && relates.size() > 0) {
			list = ORMService.getInstance().query("CmPriGroup", new DataCondition().setStringIn("groupId", relates.toArray(new String[0])).setOrderByAsc("sort").toEntity());
		} else {
			list = new ArrayList<>();
		}
		for (Map<String, Object> vo : list) {
			List<String> tips = new ArrayList<>();
			setGroup(tips, vo);
			Collections.reverse(tips);
			vo.put("tips", StringUtils.join(tips, " > "));
		}
		request.setAttribute("list", list);
		Actions.includePage(request, response, Actions.Util.getPagePath(request, "pri_quick.jsp"));
	}
}
