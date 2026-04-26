package com.riversoft.scheduler;

import com.riversoft.platform.web.FileManager;
import com.riversoft.scheduler.annotation.QuartzJob;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by exizhai on 6/21/2015.
 */
@QuartzJob(cronExp = "0 30 01 * * ?", name = "UserFileSpaceCleanup", group = "System", desc = "用户中转区清理")
public class UserFileSpaceCleanup extends AbstractFileCleanupTask {

    private static final Logger logger = LoggerFactory.getLogger(UserFileSpaceCleanup.class);

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        logger.info("启动文件清理.");
        new DbTempFileCleanThread().start();// 递归清理今天以前的: attachment/temp_file/20150630/
        logger.info("清除用户中转区.");
        new DirectorCleanThread(FileManager.getRootUserFileSpace(), 1).start();//递归清理一天前的: attachment/user_file

        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
        }
    }
}
