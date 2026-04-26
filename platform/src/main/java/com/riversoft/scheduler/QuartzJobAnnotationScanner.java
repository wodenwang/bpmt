package com.riversoft.scheduler;

import com.riversoft.core.db.ORMService;
import com.riversoft.platform.job.JobManager;
import com.riversoft.platform.po.DevJob;
import com.riversoft.scheduler.annotation.QuartzJob;
import org.quartz.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AnnotationTypeFilter;

import java.util.List;
import java.util.Set;

/**
 * Created by exizhai on 6/21/2015.
 */
public class QuartzJobAnnotationScanner {

	private Logger logger = LoggerFactory.getLogger(QuartzJobAnnotationScanner.class);

	private JobManager jobManager;

	private String scanPackage = "com.riversoft";

	public void setScanPackage(String scanPackage) {
		this.scanPackage = scanPackage;
	}

	public void setJobManager(JobManager jobManager) {
		this.jobManager = jobManager;
	}

	public void init() {
		loadJobsFromClasspath();

		loadJobsFromDB();
	}

	private void loadJobsFromClasspath() {
		ClassPathScanningCandidateComponentProvider scanner = new ClassPathScanningCandidateComponentProvider(false);
		scanner.addIncludeFilter(new AnnotationTypeFilter(QuartzJob.class));

		for (BeanDefinition bd : scanner.findCandidateComponents(scanPackage)) {
			String clazzName = bd.getBeanClassName();
			Class<?> clazz;
			try {
				clazz = Class.forName(clazzName);
				QuartzJob quartzJob = clazz.getAnnotation(QuartzJob.class);
				String name = quartzJob.name();
				String group = quartzJob.group();
				String cronExp = quartzJob.cronExp();
				String desc = quartzJob.desc();

				if (Job.class.isAssignableFrom(clazz)) {
					jobManager.reschedule(clazz, name, group, desc, cronExp);
				}
			} catch (Exception e) {
				logger.warn("QuartzJobAnnotationScanner scan failed:" + e.getMessage());
			}
		}
	}

	@SuppressWarnings("unchecked")
	private void loadJobsFromDB() {
		// 由于初始化操作需要使用数据库操作,必须依赖于spring容器装载完成,所以这里使用了守护线程延时初始化
		Thread schedulerThread = new Thread() {
			@Override
			public void run() {
				while (true) {
					try {
						Thread.sleep(5000);
						logger.info("等候5秒.");
					} catch (InterruptedException ex) {
						// simply proceed
					}
					try {
						List<DevJob> jobs = (List<DevJob>) ORMService.getInstance().queryAll(DevJob.class.getName());
						if (jobs == null || jobs.isEmpty()) {
							logger.info("DevJob 没找到定时任务配置.");
						} else {
							logger.info("DevJob 找到{}个定制任务.", jobs.size());
							for (DevJob devJob : jobs) {
								logger.info("任务[{}:{}]的状态是:{}", devJob.getJobKey(), devJob.getDescription(), devJob.getActiveFlag());
								if (devJob.getActiveFlag() == 1) {
									jobManager.reschedule(devJob);
									logger.info("任务[{}:{}]已启动.", devJob.getJobKey(), devJob.getDescription());
								} else {
									if (jobManager.check(devJob)) {
										logger.warn("移除定时任务: [{}:{}]", devJob.getJobKey(), devJob.getDescription());
										jobManager.remove(devJob);
									}
								}
							}
							Set<TriggerKey> triggerKeySet = jobManager.getAllTriggers();
							logger.info("找到{}个定时任务.", triggerKeySet.size());
							int i = 0;
							for (TriggerKey triggerKey : triggerKeySet) {
								logger.info("定时任务: [{}]:[{}:{}]", i++, triggerKey.getGroup(), triggerKey.getName());
							}
						}
						break;
					} catch (Throwable e) {
						logger.warn("调度框架初始化失败,等待重试...");
						continue;
					}
				}
			}
		};
		schedulerThread.setName("调度框架延时加载线程");
		schedulerThread.setDaemon(true);
		schedulerThread.start();
	}

}
