package com.riversoft.scheduler;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by exizhai on 9/1/2015.
 */
//@QuartzJob(cronExp = "0 0 3 * * ?", name = "ContactSyncScheduler", group = "System", desc = "微信通讯录用户和部门全量同步")
//@DisallowConcurrentExecution
public class ContactSyncScheduler implements Job {

    private Logger logger = LoggerFactory.getLogger(ContactSyncScheduler.class);

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        logger.info("开始同步通讯录.");
//        boolean result = ContactService.getInstance().sync(new NullDBOperationSignal());
//        if (result) {
//            logger.info("同步成功.");
//        } else {
//            logger.error("同步失败");
//        }
    }

}
