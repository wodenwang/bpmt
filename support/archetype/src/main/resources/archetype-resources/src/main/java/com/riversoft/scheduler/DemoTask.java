/*
 * $HeadURL: $
 * $Id: $
 * Copyright (c) 2013 by Riversoft System, all rights reserved.
 */
package com.riversoft.scheduler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
 
/**
 * @author Borball
 *
 */
@Component
public class DemoTask {

    Logger logger = LoggerFactory.getLogger(DemoTask.class);
            
    @Scheduled(cron="0 0 0 * * ?")//每天0点执行
    public void test(){
        logger.debug("scheduler task executed.");
    }
}
