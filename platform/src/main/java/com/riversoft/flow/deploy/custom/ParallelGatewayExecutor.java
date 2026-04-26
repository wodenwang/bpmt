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
import org.activiti.bpmn.model.ImplementationType;
import org.activiti.bpmn.model.ParallelGateway;
import org.activiti.bpmn.model.SequenceFlow;
import org.activiti.engine.delegate.ExecutionListener;
import org.apache.commons.lang3.StringUtils;

import com.riversoft.flow.deploy.CustomNode;
import com.riversoft.flow.deploy.CustomNodeExecutor;
import com.riversoft.flow.listener.ParallelGatewayListener;

/**
 * @author woden
 * 
 */
@CustomNode(ParallelGateway.class)
public class ParallelGatewayExecutor implements CustomNodeExecutor<ParallelGateway> {

    @Override
    public void execute(BpmnModel model, ParallelGateway node) {
        List<ActivitiListener> executionListeners = new ArrayList<>();

        // 加上监听器
        {
            ActivitiListener activitiListener = new ActivitiListener();
            activitiListener.setEvent(ExecutionListener.EVENTNAME_END);
            activitiListener.setImplementationType(ImplementationType.IMPLEMENTATION_TYPE_CLASS);
            activitiListener.setImplementation(ParallelGatewayListener.class.getName());
            executionListeners.add(activitiListener);
        }
        node.setExecutionListeners(executionListeners);

        if (StringUtils.isEmpty(node.getName())) {

            List<SequenceFlow> sequenceFlows = node.getOutgoingFlows();
            if (sequenceFlows == null || sequenceFlows.size() <= 1) {
                node.setName("汇合");
            } else {
                node.setName("拆分");
            }
        }

    }

}
