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
import org.activiti.bpmn.model.EventDefinition;
import org.activiti.bpmn.model.ImplementationType;
import org.activiti.bpmn.model.IntermediateCatchEvent;
import org.activiti.bpmn.model.TimerEventDefinition;
import org.activiti.engine.delegate.ExecutionListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.riversoft.flow.deploy.CustomNode;
import com.riversoft.flow.deploy.CustomNodeExecutor;
import com.riversoft.flow.key.VariableKeys;
import com.riversoft.flow.listener.TimerCatchEventExecutionListenner;

/**
 * @author woden
 * 
 */
@CustomNode(IntermediateCatchEvent.class)
public class IntermediateCatchEventExecutor implements CustomNodeExecutor<IntermediateCatchEvent> {
    /**
     * Logger for this class
     */
    private static final Logger logger = LoggerFactory.getLogger(IntermediateCatchEventExecutor.class);

    @Override
    public void execute(BpmnModel model, IntermediateCatchEvent node) {

        List<ActivitiListener> executionListeners = new ArrayList<>();
        // 加上监听器
        {
            ActivitiListener activitiListener = new ActivitiListener();
            activitiListener.setEvent(ExecutionListener.EVENTNAME_TAKE);
            activitiListener.setImplementationType(ImplementationType.IMPLEMENTATION_TYPE_CLASS);
            activitiListener.setImplementation(TimerCatchEventExecutionListenner.class.getName());
            executionListeners.add(activitiListener);
        }
        node.setExecutionListeners(executionListeners);

        // 加上时间配置信息
        List<EventDefinition> eventDefinitions = new ArrayList<>();
        // 加上时间变量
        {
            TimerEventDefinition eventDefinition = new TimerEventDefinition();
            eventDefinition.setTimeDuration("${" + VariableKeys._TIMER_DURATION + "}");
            eventDefinitions.add(eventDefinition);
        }
        node.setEventDefinitions(eventDefinitions);
    }

}
