/*
 * $HeadURL: $
 * $Id: $
 * Copyright (c) 2013 by Riversoft System, all rights reserved.
 */
package com.riversoft.flow.deploy.custom;

import org.activiti.bpmn.model.BpmnModel;
import org.activiti.bpmn.model.ScriptTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.riversoft.core.exception.ExceptionType;
import com.riversoft.core.exception.SystemRuntimeException;
import com.riversoft.flow.deploy.CustomNode;
import com.riversoft.flow.deploy.CustomNodeExecutor;

/**
 * @author woden
 * 
 */
@CustomNode(ScriptTask.class)
public class ScriptTaskExecutor implements CustomNodeExecutor<ScriptTask> {
    /**
     * Logger for this class
     */
    private static final Logger logger = LoggerFactory.getLogger(ScriptTaskExecutor.class);

    @Override
    public void execute(BpmnModel model, ScriptTask node) {
        throw new SystemRuntimeException(ExceptionType.CONFIG, "本系统不支持[脚本任务]节点,请使用[服务任务]代替.");
    }
}
