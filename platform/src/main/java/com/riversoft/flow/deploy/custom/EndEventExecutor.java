/*
 * $HeadURL: $
 * $Id: $
 * Copyright (c) 2013 by Riversoft System, all rights reserved.
 */
package com.riversoft.flow.deploy.custom;

import java.util.ArrayList;
import java.util.List;

import org.activiti.bpmn.model.ActivitiListener;
import org.activiti.bpmn.model.BpmnModel;
import org.activiti.bpmn.model.EndEvent;
import org.activiti.bpmn.model.ImplementationType;
import org.activiti.engine.delegate.ExecutionListener;
import org.apache.commons.lang3.StringUtils;

import com.riversoft.flow.deploy.CustomNode;
import com.riversoft.flow.deploy.CustomNodeExecutor;
import com.riversoft.flow.listener.EndExecutionListener;

/**
 * @author woden
 * 
 */
@CustomNode(EndEvent.class)
public class EndEventExecutor implements CustomNodeExecutor<EndEvent> {
    @Override
    public void execute(BpmnModel model, EndEvent node) {
        List<ActivitiListener> executionListeners = new ArrayList<>();

        // 加上监听器
        {
            ActivitiListener activitiListener = new ActivitiListener();
            activitiListener.setEvent(ExecutionListener.EVENTNAME_END);
            activitiListener.setImplementationType(ImplementationType.IMPLEMENTATION_TYPE_CLASS);
            activitiListener.setImplementation(EndExecutionListener.class.getName());
            executionListeners.add(activitiListener);
        }
        node.setExecutionListeners(executionListeners);

        if (StringUtils.isEmpty(node.getName())) {
            node.setName("结束");
        }
    }

}
