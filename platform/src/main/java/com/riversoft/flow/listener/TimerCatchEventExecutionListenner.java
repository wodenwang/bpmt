/*
 * $HeadURL: $
 * $Id: $
 * Copyright (c) 2013 by Riversoft System, all rights reserved.
 */
package com.riversoft.flow.listener;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.ExecutionListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.riversoft.flow.FlowFactory;
import com.riversoft.flow.key.VariableKeys;

/**
 * @author woden
 * 
 */
public class TimerCatchEventExecutionListenner implements ExecutionListener {
    /**
     * Logger for this class
     */
    private static final Logger logger = LoggerFactory.getLogger(TimerCatchEventExecutionListenner.class);

    /*
     * (non-Javadoc)
     * 
     * @see org.activiti.engine.delegate.ExecutionListener#notify(org.activiti.engine.delegate.DelegateExecution)
     */
    @Override
    public void notify(DelegateExecution execution) throws Exception {
        // TODO Auto-generated method stub
        logger.debug("当前节点:" + execution.getCurrentActivityId() + "," + execution.getCurrentActivityName());
        logger.debug("暂时就打印个ID:===========>" + execution.getId() + ",事件:" + execution.getEventName());
        // 设置变量
        FlowFactory.getRuntimeService().setVariable(execution.getId(), VariableKeys._TIMER_DURATION.name(), "PT20S");

    }

}
