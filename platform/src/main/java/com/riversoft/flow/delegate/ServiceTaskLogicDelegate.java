/*
 * $HeadURL: $
 * $Id: $
 * Copyright (c) 2013 by Riversoft System, all rights reserved.
 */
package com.riversoft.flow.delegate;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 通用自定义任务
 * 
 * @author woden
 * 
 */
public class ServiceTaskLogicDelegate implements JavaDelegate {
    /**
     * Logger for this class
     */
    private static final Logger logger = LoggerFactory.getLogger(ServiceTaskLogicDelegate.class);

    /*
     * (non-Javadoc)
     * 
     * @see org.activiti.engine.delegate.JavaDelegate#execute(org.activiti.engine.delegate.DelegateExecution)
     */
    @Override
    public void execute(DelegateExecution execution) throws Exception {
        // do nothing
    }

}
