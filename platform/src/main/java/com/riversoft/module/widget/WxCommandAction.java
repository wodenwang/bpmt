/*
 * $HeadURL: $
 * $Id: $
 * Copyright (c) 2013 by Riversoft System, all rights reserved.
 */
package com.riversoft.module.widget;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;

import com.riversoft.core.db.DataCondition;
import com.riversoft.core.db.DataPackage;
import com.riversoft.core.db.ORMService;
import com.riversoft.core.exception.ExceptionType;
import com.riversoft.core.exception.SystemRuntimeException;
import com.riversoft.core.web.Actions;
import com.riversoft.core.web.Actions.Util;
import com.riversoft.core.web.RequestUtils;
import com.riversoft.platform.translate.WxCommandSupportType;
import com.riversoft.wx.annotation.WxAnnotatedCommandsHolder;

/**
 * 视图选择控件
 * 
 * @author woden
 * 
 */
public class WxCommandAction {

	/**
	 * 查询框
	 * 
	 * @param request
	 * @param response
	 */
	public void index(HttpServletRequest request, HttpServletResponse response) {
		Actions.includePage(request, response, Util.getPagePath(request, "main.jsp"));
	}

	/**
	 * 选择处理器
	 * 
	 * @param request
	 * @param response
	 */
	public void list(HttpServletRequest request, HttpServletResponse response) {
		Integer mpFlag = RequestUtils.getIntegerValue(request, "mpFlag");
		String menuType = RequestUtils.getStringValue(request, "menuType");
		String type = RequestUtils.getStringValue(request, "type");

		WxCommandSupportType supportType = WxCommandSupportType.valueOf(menuType);

		if (StringUtils.equalsIgnoreCase("self", type)) {
			// 获取分页信息
			int start = Util.getStart(request);
			int limit = Util.getLimit(request);

			// 获取排序信息
			String field = Util.getSortField(request);
			String dir = Util.getSortDir(request);

			// 查询条件
			DataCondition condition = new DataCondition(Util.buildQueryMap(new HashMap<String, Object>(), request));
			condition.setOrderBy(field, dir);
			condition.setNumberEqual("mpFlag", mpFlag.toString());
			condition.addSql("supportType like '%" + supportType.getCode().toString() + "%'");

			DataPackage dp = ORMService.getInstance().queryPackage("WxCommand", start, limit, condition.toEntity());
			request.setAttribute("dp", dp);
			Actions.includePage(request, response, Util.getPagePath(request, "self_list.jsp"));
		} else {
			//系统内置
			Map<WxCommandSupportType, Set<WxAnnotatedCommandsHolder.CommandInfo>> commands = null;
			if(mpFlag == 0) {
				commands = WxAnnotatedCommandsHolder.getInstance().getQyAnnotatedCommands();
			} else {
				commands = WxAnnotatedCommandsHolder.getInstance().getMpAnnotatedCommands();
			}

			if(commands != null && !commands.isEmpty()) {
				Set<WxAnnotatedCommandsHolder.CommandInfo> systemCommands = commands.get(supportType);
				request.setAttribute("sys", systemCommands);
				Actions.includePage(request, response, Util.getPagePath(request, "sys_list.jsp"));
			} else {
				throw new SystemRuntimeException(ExceptionType.CODING, "没有合适的系统处理器");
			}
		}
	}

}
