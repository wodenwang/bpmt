/*
 * $HeadURL: $
 * $Id: $
 * Copyright (c) 2014 by Riversoft System, all rights reserved.
 */
package com.riversoft.platform.job;

import org.quartz.JobExecutionContext;

import com.riversoft.platform.script.ScriptHelper;
import com.riversoft.platform.script.ScriptTypes;

/**
 * @author woden
 * 
 */
public class JobExecuteService {

	/**
	 * 执行计划任务服务(事务)
	 * 
	 * @param context
	 */
	public void executeJob(JobExecutionContext context) {
		JobPerformance.runJob(context);
	}

	/**
	 * 使用内部类的方法来避开起事务
	 * 
	 * @author Chris
	 *
	 */
	static class JobPerformance {
		/**
		 * 执行计划任务服务(非起事务)
		 * 
		 * @param context
		 */
		public static void runJob(JobExecutionContext context) {
			Integer type = context.getJobDetail().getJobDataMap().getInt("type");
			String script = context.getJobDetail().getJobDataMap().getString("script");
			ScriptHelper.evel(ScriptTypes.forCode(type), script);
		}

	}
}
