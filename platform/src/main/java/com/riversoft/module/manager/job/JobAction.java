/*
 * $HeadURL: $
 * $Id: $
 * Copyright (c) 2014 by Riversoft System, all rights reserved.
 */
package com.riversoft.module.manager.job;

import static com.riversoft.core.web.Actions.includePage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;

import com.riversoft.core.BeanFactory;
import com.riversoft.core.IDGenerator;
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
import com.riversoft.platform.SessionManager;
import com.riversoft.platform.db.model.ModelKeyUtils;
import com.riversoft.platform.job.LogTableModelKeys;
import com.riversoft.platform.po.DevJob;
import com.riversoft.platform.po.TbTable;

/**
 * @author woden
 * 
 */
@ActionAccess(level = SafeLevel.DEV_R)
public class JobAction {

	private JobService service = BeanFactory.getInstance().getBean(JobService.class);

	/**
	 * 调度管理
	 * 
	 * @param request
	 * @param response
	 */
	public void index(HttpServletRequest request, HttpServletResponse response) {
		includePage(request, response, Util.getPagePath(request, "main.jsp"));
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

		DataPackage dp = ORMService.getInstance().queryPackage(DevJob.class.getName(), start, limit,
				condition.toEntity());
		// 设置到页面
		request.setAttribute("dp", dp);

		Actions.includePage(request, response, Util.getPagePath(request, "list.jsp"));
	}

	/**
	 * 删除计划
	 * 
	 * @param request
	 * @param response
	 */
	@ActionAccess(level = SafeLevel.DEV_W)
	public void delete(HttpServletRequest request, HttpServletResponse response) {
		String jobKey = RequestUtils.getStringValue(request, "jobKey");
		service.executeRemove(jobKey);
		Actions.redirectInfoPage(request, response, "删除成功.");
	}

	/**
	 * 新建(页面)
	 * 
	 * @param request
	 * @param response
	 */
	@SuppressWarnings("unchecked")
	public void createZone(HttpServletRequest request, HttpServletResponse response) {
		List<TbTable> tables = new ArrayList<>();
		List<TbTable> sysTables = (List<TbTable>) ORMService.getInstance().queryAll(TbTable.class.getName());
		for (TbTable model : sysTables) {
			if (ModelKeyUtils.checkModel(LogTableModelKeys.class, model)) {
				tables.add(model);
			}
		}
		request.setAttribute("tables", tables);
		Actions.includePage(request, response, Util.getPagePath(request, "form.jsp"));
	}

	/**
	 * 编辑(页面)
	 * 
	 * @param request
	 * @param response
	 */
	@SuppressWarnings("unchecked")
	public void updateZone(HttpServletRequest request, HttpServletResponse response) {
		String jobKey = RequestUtils.getStringValue(request, "jobKey");
		DevJob vo = (DevJob) ORMService.getInstance().findByPk(DevJob.class.getName(), jobKey);
		if (vo == null) {
			throw new SystemRuntimeException(ExceptionType.BUSINESS, "调度任务[" + jobKey + "]不存在.");
		}
		request.setAttribute("vo", vo);

		List<TbTable> tables = new ArrayList<>();
		List<TbTable> sysTables = (List<TbTable>) ORMService.getInstance().queryAll(TbTable.class.getName());
		for (TbTable model : sysTables) {
			if (ModelKeyUtils.checkModel(LogTableModelKeys.class, model)) {
				tables.add(model);
			}
		}
		request.setAttribute("tables", tables);
		Actions.includePage(request, response, Util.getPagePath(request, "form.jsp"));
	}

	/**
	 * 提交表单
	 * 
	 * @param request
	 * @param response
	 */
	@ActionAccess(level = SafeLevel.DEV_W)
	public void submitForm(HttpServletRequest request, HttpServletResponse response) {
		String jobKey = RequestUtils.getStringValue(request, "jobKey");
		DevJob vo;
		if (StringUtils.isNotEmpty(jobKey)) {
			vo = (DevJob) ORMService.getInstance().findByPk(DevJob.class.getName(), jobKey);
			if (vo == null) {
				throw new SystemRuntimeException(ExceptionType.BUSINESS, "调度任务[" + jobKey + "]不存在.");
			}
			if (vo.getActiveFlag().intValue() != 0) {
				throw new SystemRuntimeException(ExceptionType.BUSINESS, "活跃状态中的任务不允许修改.");
			}
		} else {
			vo = new DevJob();
			vo.setJobKey(IDGenerator.next());
		}
		vo.setDescription(RequestUtils.getStringValue(request, "description"));
		vo.setActiveFlag(0);
		vo.setCreateUid(SessionManager.getUser().getUid());
		vo.setLogTableName(RequestUtils.getStringValue(request, "logTableName"));
		vo.setIsTransaction(RequestUtils.getIntegerValue(request, "isTransaction"));
		vo.setCronExpression(RequestUtils.getStringValue(request, "cronExpression"));
		vo.setExecType(RequestUtils.getIntegerValue(request, "execType"));
		vo.setExecScript(RequestUtils.getStringValue(request, "execScript"));

		service.executeSubmit(vo);
	}

	/**
	 * 修改调度任务状态
	 * 
	 * @param request
	 * @param response
	 */
	@ActionAccess(level = SafeLevel.DEV_W)
	public void active(HttpServletRequest request, HttpServletResponse response) {
		String jobKey = RequestUtils.getStringValue(request, "jobKey");
		Integer activeFlag = RequestUtils.getIntegerValue(request, "activeFlag");
		if (activeFlag == null) {
			throw new SystemRuntimeException(ExceptionType.BUSINESS, "未传递状态值.");
		}
		service.executeActiveState(jobKey, activeFlag.intValue() == 1);
		Actions.redirectInfoPage(request, response, "调度任务状态已修改.");
	}
}
