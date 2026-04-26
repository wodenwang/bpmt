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
import org.activiti.bpmn.model.SequenceFlow;
import org.activiti.bpmn.model.UserTask;
import org.activiti.engine.delegate.TaskListener;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.riversoft.flow.deploy.CustomNode;
import com.riversoft.flow.deploy.CustomNodeExecutor;
import com.riversoft.flow.key.VariableKeys;
import com.riversoft.flow.listener.UserTaskListener;

/**
 * @author woden
 * 
 */
@CustomNode(UserTask.class)
public class UserTaskExecutor implements CustomNodeExecutor<UserTask> {
    /**
     * Logger for this class
     */
    private static final Logger logger = LoggerFactory.getLogger(UserTaskExecutor.class);

    @Override
    public void execute(BpmnModel model, UserTask node) {
        List<ActivitiListener> taskListeners = new ArrayList<>();
        // 任务创建时
        {
            ActivitiListener activitiListener = new ActivitiListener();
            activitiListener.setEvent(TaskListener.EVENTNAME_ALL_EVENTS);
            activitiListener.setImplementationType(ImplementationType.IMPLEMENTATION_TYPE_CLASS);
            activitiListener.setImplementation(UserTaskListener.class.getName());
            taskListeners.add(activitiListener);
        }
        node.setTaskListeners(taskListeners);

        for (SequenceFlow flow : node.getOutgoingFlows()) {
            // 有多条线的,加上条件
            if (node.getOutgoingFlows().size() > 1) {
                logger.debug("为连线[" + flow.getId() + "]绑定条件.");
                flow.setConditionExpression("${" + VariableKeys.TaskVariableKeys._USER_TASK_OUTCOME.name() + "=='"
                        + flow.getId() + "'}");
            }

            // 连线无名称则自动起名
            if (StringUtils.isEmpty(flow.getName())) {
                flow.setName("下一步");
            }
        }
    }
}
