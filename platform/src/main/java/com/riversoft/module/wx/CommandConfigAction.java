/*
 * $HeadURL: $
 * $Id: $
 * Copyright (c) 2015 by Riversoft System, all rights reserved.
 */
package com.riversoft.module.wx;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;

import com.riversoft.core.db.DataCondition;
import com.riversoft.core.db.DataPO;
import com.riversoft.core.db.DataPackage;
import com.riversoft.core.db.ORMService;
import com.riversoft.core.exception.ExceptionType;
import com.riversoft.core.exception.SystemRuntimeException;
import com.riversoft.core.web.Actions;
import com.riversoft.core.web.Actions.Util;
import com.riversoft.core.web.RequestUtils;
import com.riversoft.platform.SessionManager;
import com.riversoft.platform.translate.WxCommandSupportType;

/**
 * @author woden
 *
 */
public class CommandConfigAction {
	/**
	 * 首页
	 * 
	 * @param request
	 * @param response
	 */
	public void index(HttpServletRequest request, HttpServletResponse response) {
		Actions.includePage(request, response, Util.getPagePath(request, "command_main.jsp"));
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

		// 类型查询
		String[] supportType = RequestUtils.getStringValues(request, "supportType");
		if (supportType != null && supportType.length > 0) {
			List<String> sqls = new ArrayList<>();
			for (String s : supportType) {
				sqls.add("supportType like '%" + s + "%'");
			}

			condition.addSql(StringUtils.join(sqls, " or "));
		}

		DataPackage dp = ORMService.getInstance().queryPackage("WxCommand", start, limit, condition.toEntity());
		// 设置到页面
		request.setAttribute("dp", dp);

		Actions.includePage(request, response, Util.getPagePath(request, "command_list.jsp"));
	}

	/**
	 * 新增处理器
	 * 
	 * @param request
	 * @param response
	 */
	public void createZone(HttpServletRequest request, HttpServletResponse response) {
		request.setAttribute("supportTypes", WxCommandSupportType.values());
		Actions.includePage(request, response, Util.getPagePath(request, "command_form.jsp"));
	}

	/**
	 * 编辑处理器
	 * 
	 * @param request
	 * @param response
	 */
	@SuppressWarnings("unchecked")
	public void editZone(HttpServletRequest request, HttpServletResponse response) {
		String commandKey = RequestUtils.getStringValue(request, "commandKey");
		Map<String, Object> vo = (Map<String, Object>) ORMService.getInstance().findByPk("WxCommand", commandKey);
		if (vo == null) {
			throw new SystemRuntimeException(ExceptionType.CONFIG, "事件处理器[" + commandKey + "]不存在.");
		}
		request.setAttribute("vo", vo);
		request.setAttribute("supportTypes", WxCommandSupportType.values());
		Actions.includePage(request, response, Util.getPagePath(request, "command_form.jsp"));
	}

	/**
	 * 保存处理器
	 * 
	 * @param request
	 * @param response
	 */
	public void remove(HttpServletRequest request, HttpServletResponse response) {
		String commandKey = RequestUtils.getStringValue(request, "commandKey");
		ORMService.getInstance().removeByPk("WxCommand", commandKey);
		Actions.redirectInfoPage(request, response, "删除[" + commandKey + "]成功.");
	}

	/**
	 * 保存处理器
	 * 
	 * @param request
	 * @param response
	 */
	@SuppressWarnings("unchecked")
	public void submit(HttpServletRequest request, HttpServletResponse response) {
		String commandKey = RequestUtils.getStringValue(request, "commandKey");
		Integer isCreate = RequestUtils.getIntegerValue(request, "isCreate");
		DataPO po;
		if (isCreate == 1) {// 新增
			po = new DataPO("WxCommand");
			po.set("commandKey", commandKey);
			po.set("createUid", SessionManager.getUser().getUid());
		} else {
			po = new DataPO("WxCommand", ((Map<String, Object>) ORMService.getInstance().findByPk("WxCommand", commandKey)));
		}

		po.set("busiName", RequestUtils.getStringValue(request, "busiName"));
		po.set("description", RequestUtils.getStringValue(request, "description"));
		po.set("logicType", RequestUtils.getIntegerValue(request, "logicType"));
		po.set("logicScript", RequestUtils.getStringValue(request, "logicScript"));
		po.set("mpFlag", RequestUtils.getIntegerValue(request, "mpFlag"));
		po.set("supportType", StringUtils.join(RequestUtils.getStringValues(request, "supportType"), ";"));

		ORMService.getInstance().saveOrUpdate(po.toEntity());
		Actions.redirectInfoPage(request, response, "保存[" + commandKey + "]成功.");
	}

}
