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
import org.activiti.bpmn.model.ReceiveTask;
import org.activiti.engine.delegate.ExecutionListener;

import com.riversoft.flow.deploy.CustomNode;
import com.riversoft.flow.deploy.CustomNodeExecutor;
import com.riversoft.flow.listener.ReceiveTaskListener;

/**
 * @author woden
 * 
 */
@CustomNode(ReceiveTask.class)
public class ReceiveTaskExecutor implements CustomNodeExecutor<ReceiveTask> {

	@Override
	public void execute(BpmnModel model, ReceiveTask node) {
		List<ActivitiListener> executionListeners = new ArrayList<>();
		// 加上监听器
		{
			ActivitiListener activitiListener = new ActivitiListener();
			activitiListener.setEvent(ExecutionListener.EVENTNAME_END);
			activitiListener.setImplementationType(ImplementationType.IMPLEMENTATION_TYPE_CLASS);
			activitiListener.setImplementation(ReceiveTaskListener.class.getName());
			executionListeners.add(activitiListener);
		}
		node.setExecutionListeners(executionListeners);
	}

}
