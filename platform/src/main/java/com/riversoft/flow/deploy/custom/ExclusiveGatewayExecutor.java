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
import org.activiti.bpmn.model.ExclusiveGateway;
import org.activiti.bpmn.model.ImplementationType;
import org.activiti.bpmn.model.SequenceFlow;
import org.activiti.engine.delegate.ExecutionListener;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.riversoft.flow.deploy.CustomNode;
import com.riversoft.flow.deploy.CustomNodeExecutor;
import com.riversoft.flow.key.VariableKeys;
import com.riversoft.flow.listener.ExclusiveGatewayExecutionListenner;

/**
 * 条件判断节点
 * 
 * @author woden
 * 
 */
@CustomNode(ExclusiveGateway.class)
public class ExclusiveGatewayExecutor implements CustomNodeExecutor<ExclusiveGateway> {
    /**
     * Logger for this class
     */
    private static final Logger logger = LoggerFactory.getLogger(ExclusiveGatewayExecutor.class);

    @Override
    public void execute(BpmnModel model, ExclusiveGateway node) {

        // 添加线段的条件
        for (SequenceFlow flow : node.getOutgoingFlows()) {
            logger.debug("ExclusiveGatewayExecutor连线:" + flow.getId());
            flow.setConditionExpression("${" + VariableKeys._EXCLUSIVE_GATEWAY_OUTCOME.name() + "=='" + flow.getId()
                    + "'}");
        }

        List<ActivitiListener> executionListeners = new ArrayList<>();
        // 加上监听器
        {
            ActivitiListener activitiListener = new ActivitiListener();
            activitiListener.setEvent(ExecutionListener.EVENTNAME_START);
            activitiListener.setImplementationType(ImplementationType.IMPLEMENTATION_TYPE_CLASS);
            activitiListener.setImplementation(ExclusiveGatewayExecutionListenner.class.getName());
            executionListeners.add(activitiListener);
        }

        if (StringUtils.isEmpty(node.getName())) {
            node.setName("条件判断");
        }

        node.setExecutionListeners(executionListeners);

    }

}
