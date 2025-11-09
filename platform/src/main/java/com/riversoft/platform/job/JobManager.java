/*
 * $HeadURL: $
 * $Id: $
 * Copyright (c) 2014 by Riversoft System, all rights reserved.
 */
package com.riversoft.platform.job;

import java.util.Date;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.quartz.CronScheduleBuilder;
import org.quartz.CronTrigger;
import org.quartz.Job;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.TriggerKey;
import org.quartz.impl.matchers.GroupMatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.riversoft.core.BeanFactory;
import com.riversoft.core.db.JdbcService;
import com.riversoft.core.exception.SystemRuntimeException;
import com.riversoft.platform.job.JobExecuteService.JobPerformance;
import com.riversoft.platform.po.DevJob;

/**
 * 调度管理器
 * 
 * @author woden
 * 
 */
public class JobManager {
	/**
	 * Logger for this class
	 */
	private static final Logger logger = LoggerFactory.getLogger(JobManager.class);

	/**
	 * 调度框架JOB.
	 * 
	 * @author woden
	 * 
	 */
	public static class FrameWorkJob implements Job {
		@Override
		public void execute(JobExecutionContext context) throws JobExecutionException {
			Date begin = new Date();
			String logTableName = context.getJobDetail().getJobDataMap().getString("logTableName");
			Integer isTransaction = context.getJobDetail().getJobDataMap().getInt("isTransaction");

			String jobKey = context.getJobDetail().getKey().getName();
			String description = context.getJobDetail().getDescription();
			logger.info("开始执行调度任务[{}]:{}.", jobKey, description);
			try {
				JobExecuteService service = BeanFactory.getInstance().getBean(JobExecuteService.class);
				if (isTransaction == 1) {
					// 起事务执行
					logger.info("开始起事务执行任务");
					service.executeJob(context);
				} else {
					// 不起事务执行
					logger.info("开始执行任务, 不起事务");
					JobPerformance.runJob(context);
				}
				if (StringUtils.isNotEmpty(logTableName)) {
					JdbcService.getInstance().executeSQL("insert into " + logTableName + " (JOB_KEY,DESCRIPTION,BEGIN_DATE,END_DATE,SUCCESS_FLAG,LOG_MSG) values (?,?,?,?,?,?)", jobKey, description,
							begin, new Date(), 1, "执行成功");
				}
			} catch (Throwable e) {
				if (StringUtils.isNotEmpty(logTableName)) {
					JdbcService.getInstance().executeSQL("insert into " + logTableName + " (JOB_KEY,DESCRIPTION,BEGIN_DATE,END_DATE,SUCCESS_FLAG,LOG_MSG) values (?,?,?,?,?,?)", jobKey, description,
							begin, new Date(), 0, e.getMessage());
				} else {
					throw e;
				}
			}
		}
	}

	private Scheduler scheduler;

	/**
	 * 实例化
	 * 
	 * @param scheduler
	 */
	public JobManager(Scheduler scheduler) {
		this.scheduler = scheduler;
	}

	/**
	 * 获取单例
	 * 
	 * @return
	 */
	public static JobManager getInstance() {
		return (JobManager) BeanFactory.getInstance().getBean("jobManager");
	}

	/**
	 * 转换调度配置
	 * 
	 * @param job
	 * @return
	 */
	private Trigger buildTrigger(DevJob job) {
		return buildTrigger(job.getJobKey(), null, job.getDescription(), job.getCronExpression());
	}

	/**
	 * 转换任务明细
	 * 
	 * @param job
	 * @return
	 */
	private JobDetail buildDetail(DevJob job) {
		JobDetail jobDetail = JobBuilder.newJob(FrameWorkJob.class).withIdentity(job.getJobKey()).withDescription(job.getDescription()).usingJobData("type", job.getExecType())
				.usingJobData("script", job.getExecScript()).usingJobData("logTableName", job.getLogTableName()).usingJobData("isTransaction", job.getIsTransaction()).build();
		return jobDetail;
	}

	/**
	 * 转换调度配置
	 *
	 * @param name
	 * @param group
	 * @param cronExp
	 * @return
	 */
	public Trigger buildTrigger(String name, String group, String desc, String cronExp) {
		return TriggerBuilder.newTrigger().withIdentity(name, group).withDescription(desc).withSchedule(CronScheduleBuilder.cronSchedule(cronExp)).build();
	}

	/**
	 * 转换任务明细
	 *
	 * @param clazz
	 * @param name
	 * @param group
	 * @param desc
	 * @return
	 */
	public JobDetail buildDetail(Class clazz, String name, String group, String desc) {
		return JobBuilder.newJob(clazz).withIdentity(name, group).withDescription(desc).build();
	}

	/**
	 * 增加调度任务
	 * 
	 * @param job
	 */
	public void add(DevJob job) {
		add(buildDetail(job), buildTrigger(job));
	}

	public void add(JobDetail jobDetail, Trigger trigger) {
		try {
			scheduler.scheduleJob(jobDetail, trigger);
		} catch (SchedulerException e) {
			throw new SystemRuntimeException(e);
		}
	}

	public void remove(String jobKey) {
		remove(new JobKey(jobKey));
	}

	/**
	 * 移除调度任务
	 *
	 * @param jobKey
	 */
	public void remove(JobKey jobKey) {
		try {
			scheduler.deleteJob(jobKey);
		} catch (SchedulerException e) {
			throw new SystemRuntimeException(e);
		}
	}

	/**
	 * 移除调度任务
	 * 
	 * @param job
	 */
	public void remove(DevJob job) {
		remove(job.getJobKey());
	}

	/**
	 * 校验调度任务是否存在
	 * 
	 * @param jobKey
	 * @return
	 */
	public boolean check(String jobKey) {
		return check(new JobKey(jobKey));
	}

	/**
	 * 校验调度任务是否存在
	 *
	 * @param jobKey
	 * @return
	 */
	public boolean check(JobKey jobKey) {
		try {
			return scheduler.checkExists(jobKey);
		} catch (SchedulerException e) {
			throw new SystemRuntimeException(e);
		}
	}

	/**
	 * 校验调度任务是否存在
	 *
	 * @param triggerKey
	 * @return
	 */
	public boolean check(TriggerKey triggerKey) {
		try {
			return scheduler.checkExists(triggerKey);
		} catch (SchedulerException e) {
			throw new SystemRuntimeException(e);
		}
	}

	/**
	 * 校验调度任务是否存在
	 * 
	 * @param job
	 * @return
	 */
	public boolean check(DevJob job) {
		return check(job.getJobKey());
	}

	/**
	 * 判断两个trigger是否相同，因为BPMT里面只使用了CronTrigger，这里的比较忽略其它类型的Trigger
	 * 
	 * @param trigger1
	 * @param trigger2
	 * @return 如果trigger key相同并且cron expression也相同则返回true
	 */
	public boolean same(Trigger trigger1, Trigger trigger2) {
		if (trigger1 instanceof CronTrigger && trigger2 instanceof CronTrigger) {
			CronTrigger cronTrigger1 = (CronTrigger) trigger1;
			CronTrigger cronTrigger2 = (CronTrigger) trigger2;
			logger.info("cronTrigger1:{},cronTrigger2:{}", cronTrigger1.getKey(), cronTrigger2.getKey());
			return trigger1.equals(trigger2) && cronTrigger1.getCronExpression().equalsIgnoreCase(cronTrigger2.getCronExpression());
		}

		return false;
	}

	public Trigger getTrigger(TriggerKey triggerKey) {
		try {
			return scheduler.getTrigger(triggerKey);
		} catch (SchedulerException e) {
			throw new SystemRuntimeException(e);
		}
	}

	/**
	 * 重新调度任务
	 * 
	 * @param job
	 */
	public void reschedule(DevJob job) {
		if (check(job)) {
			logger.warn("定时任务: [{}:{}:{}] 将会重新加载", "DEFAULT", job.getJobKey(), job.getDescription());
			remove(job);
			add(job);
		} else {
			add(job);
		}
	}

	/**
	 * 重新调度任务
	 * 
	 * @param clazz
	 * @param name
	 * @param group
	 * @param desc
	 * @param cronExp
	 */
	public void reschedule(Class clazz, String name, String group, String desc, String cronExp) {
		Trigger newTrigger = buildTrigger(name, group, desc, cronExp);
		JobDetail jobDetail = buildDetail(clazz, name, group, desc);
		TriggerKey triggerKey = new TriggerKey(name, group);

		if (check(triggerKey)) {
			Trigger oldTrigger = getTrigger(triggerKey);

			if (!same(newTrigger, oldTrigger)) {
				logger.warn("定时任务: [{}:{}:{}] 将会重新加载", group, name, desc);
				remove(oldTrigger.getJobKey());
				add(jobDetail, newTrigger);
			}
		} else {
			add(jobDetail, newTrigger);
		}
	}

	public Set<TriggerKey> getAllTriggers() {
		try {
			return scheduler.getTriggerKeys(GroupMatcher.anyTriggerGroup());
		} catch (SchedulerException e) {
			throw new SystemRuntimeException(e);
		}
	}
}
