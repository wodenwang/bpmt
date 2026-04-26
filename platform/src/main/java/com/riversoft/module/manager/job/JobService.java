/*
 * $HeadURL: $
 * $Id: $
 * Copyright (c) 2014 by Riversoft System, all rights reserved.
 */
package com.riversoft.module.manager.job;

import com.riversoft.core.db.ORMService;
import com.riversoft.core.exception.ExceptionType;
import com.riversoft.core.exception.SystemRuntimeException;
import com.riversoft.platform.job.JobManager;
import com.riversoft.platform.po.DevJob;
import com.riversoft.platform.template.DevelopmentOperation;

/**
 * @author woden
 * 
 */
public class JobService {

	/**
	 * 删除
	 * 
	 * @param jobKey
	 */
	@DevelopmentOperation("删除计划")
	public void executeRemove(String jobKey) {
		ORMService.getInstance().removeByPk(DevJob.class.getName(), jobKey);
		if (JobManager.getInstance().check(jobKey)) {// 已存在则移除
			JobManager.getInstance().remove(jobKey);
		}
	}

	/**
	 * 新增/修改提交
	 * 
	 * @param vo
	 */
	@DevelopmentOperation("保存计划")
	public void executeSubmit(DevJob vo) {
		ORMService.getInstance().saveOrUpdatePO(vo);
		
		if(1 == vo.getActiveFlag()) {
			JobManager.getInstance().reschedule(vo);
		}
	}

	/**
	 * 启动/挂起任务
	 * 
	 * @param jobKey
	 * @param flag
	 */
	@DevelopmentOperation("启动/挂起计划")
	public void executeActiveState(String jobKey, boolean flag) {
		DevJob job = (DevJob) ORMService.getInstance().findByPk(DevJob.class.getName(), jobKey);
		if (job == null) {
			throw new SystemRuntimeException(ExceptionType.BUSINESS, "任务不存在.");
		}
		job.setActiveFlag(flag ? 1 : 0);
		ORMService.getInstance().updatePO(job);

		if (flag) {
			JobManager.getInstance().reschedule(job);
		} else {
			JobManager.getInstance().remove(job);
		}
	}
}
