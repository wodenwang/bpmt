/*
 * $HeadURL: $
 * $Id: $
 * Copyright (c) 2014 by Riversoft System, all rights reserved.
 */
package com.riversoft.module.development;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.riversoft.core.db.DataCondition;
import com.riversoft.core.db.DataPackage;
import com.riversoft.core.db.ORMService;
import com.riversoft.core.exception.ExceptionType;
import com.riversoft.core.exception.SystemRuntimeException;
import com.riversoft.core.web.Actions;
import com.riversoft.core.web.Actions.FileType;
import com.riversoft.core.web.Actions.Util;
import com.riversoft.core.web.RequestUtils;
import com.riversoft.core.web.annotation.ActionAccess;
import com.riversoft.core.web.annotation.ActionAccess.SafeLevel;
import com.riversoft.platform.Platform;
import com.riversoft.platform.template.Template;
import com.riversoft.platform.template.TemplateService;

/**
 * 系统模板开发(快照)
 * 
 * @author woden
 */
@ActionAccess(level = SafeLevel.DEV_SPC)
public class TemplateAction {

	/**
	 * 模板开发
	 * 
	 * @param request
	 * @param response
	 */
	public void index(HttpServletRequest request, HttpServletResponse response) {
		Actions.includePage(request, response, Util.getPagePath(request, "main.jsp"));
	}

	/**
	 * 模板开发
	 * 
	 * @param request
	 * @param response
	 */
	public void dev(HttpServletRequest request, HttpServletResponse response) {
		request.setAttribute("template", Template.getCurrent());
		request.setAttribute("platformVersion", Platform.getVersion() == null ? "snapshot" : Platform.getVersion());
		Actions.includePage(request, response, Util.getPagePath(request, "template_dev.jsp"));
	}

	/**
	 * 生成模板
	 * 
	 * @param request
	 * @param response
	 */
	public void submitDev(HttpServletRequest request, HttpServletResponse response) {
		String[] copyDataTables = RequestUtils.getStringValues(request, "copyDataTables");
		Set<String> tables = new HashSet<>();
		if (copyDataTables != null) {
			Arrays.sort(copyDataTables);// 排序
			tables.addAll(Arrays.asList(copyDataTables));
		}
		TemplateService.getInstance().executeBuild(RequestUtils.getStringValue(request, "name"), RequestUtils.getStringValue(request, "description"), tables);
		Actions.redirectInfoPage(request, response, "生成模板成功,您可以在所有模板列表中导出模板.");
	}

	/**
	 * 列出快照
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

		DataPackage dp = ORMService.getInstance().queryPackage("TplSnapshot", start, limit, condition.toEntity());
		// 设置到页面
		request.setAttribute("dp", dp);

		Actions.includePage(request, response, Util.getPagePath(request, "list.jsp"));
	}

	/**
	 * 下载sho包
	 * 
	 * @param request
	 * @param response
	 */
	@SuppressWarnings({ "unchecked" })
	public void download(HttpServletRequest request, HttpServletResponse response) {
		String id = RequestUtils.getStringValue(request, "id");
		Map<String, Object> vo = (Map<String, Object>) ORMService.getInstance().findByPk("TplSnapshot", id);

		try (OutputStream os = response.getOutputStream();) {
			FileType type = new FileType("BPMT_" + vo.get("shortKey").toString() + "_" + vo.get("version").toString() + ".sho");
			type.prepareWeb(request, response);
			TemplateService.getInstance().buildShoPackage(os, id);// 下载zip
			os.flush();
			os.close();
			response.flushBuffer();
		} catch (IOException e) {
			throw new SystemRuntimeException(ExceptionType.WEB, "服务器找不到对应文件.", e);
		}
	}

	/**
	 * 开发日志列表
	 * 
	 * @param request
	 * @param response
	 */
	public void oprList(HttpServletRequest request, HttpServletResponse response) {
		// 获取分页信息
		int start = Util.getStart(request);
		int limit = Util.getLimit(request);

		// 获取排序信息
		String field = Util.getSortField(request);
		String dir = Util.getSortDir(request);

		// 查询条件
		DataCondition condition = new DataCondition(Util.buildQueryMap(new HashMap<String, Object>(), request));
		condition.setOrderBy(field, dir);

		DataPackage dp = ORMService.getInstance().queryPackage("TplSnapshotRecord", start, limit, condition.toEntity());
		// 设置到页面
		request.setAttribute("dp", dp);

		Actions.includePage(request, response, Util.getPagePath(request, "opr_list.jsp"));
	}

	/**
	 * 开发日志明细
	 * 
	 * @param request
	 * @param response
	 */
	public void oprDetail(HttpServletRequest request, HttpServletResponse response) {
		Map<String, Object> vo = (Map<String, Object>) ORMService.getInstance().findByPk("TplSnapshotRecord", RequestUtils.getLongValue(request, "id"));
		// 设置到页面
		request.setAttribute("vo", vo);
		Actions.includePage(request, response, Util.getPagePath(request, "opr_detail.jsp"));
	}
}
