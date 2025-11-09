package com.riversoft.scheduler;

import com.riversoft.platform.Platform;
import com.riversoft.platform.web.FileManager;
import com.riversoft.scheduler.annotation.QuartzJob;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by exizhai on 6/21/2015.
 */
@QuartzJob(cronExp = "0 30 02 * * ?", name = "TempFileCleanup", group = "System", desc = "临时文件清理")
public class TempFileCleanup extends AbstractFileCleanupTask {

    private static final Logger logger = LoggerFactory.getLogger(TempFileCleanup.class);

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        logger.info("清除临时目录.");
        new DirectorCleanThread(Platform.getTempPath(), 2).start();//递归清理2天以前的 tmp/

        new DirectorCleanThread(FileManager.getDbFileSpace(), 1).start(); //递归清理一天前的 attachment/temp_file/

        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
        }

    }
}
