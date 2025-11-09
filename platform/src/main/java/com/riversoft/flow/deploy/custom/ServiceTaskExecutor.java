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
import org.activiti.bpmn.model.ServiceTask;
import org.activiti.engine.delegate.ExecutionListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.riversoft.flow.delegate.ServiceTaskLogicDelegate;
import com.riversoft.flow.deploy.CustomNode;
import com.riversoft.flow.deploy.CustomNodeExecutor;
import com.riversoft.flow.listener.ServiceTaskListener;

/**
 * @author woden
 * 
 */
@CustomNode(ServiceTask.class)
public class ServiceTaskExecutor implements CustomNodeExecutor<ServiceTask> {
	/**
	 * Logger for this class
	 */
	private static final Logger logger = LoggerFactory.getLogger(ServiceTaskExecutor.class);

	@Override
	public void execute(BpmnModel model, ServiceTask node) {
		node.setType(null);
		node.setImplementationType(ImplementationType.IMPLEMENTATION_TYPE_CLASS);
		node.setImplementation(ServiceTaskLogicDelegate.class.getName());

		List<ActivitiListener> executionListeners = new ArrayList<>();
		// 加上监听器
		{
			ActivitiListener activitiListener = new ActivitiListener();
			activitiListener.setEvent(ExecutionListener.EVENTNAME_END);
			activitiListener.setImplementationType(ImplementationType.IMPLEMENTATION_TYPE_CLASS);
			activitiListener.setImplementation(ServiceTaskListener.class.getName());
			executionListeners.add(activitiListener);
		}
		node.setExecutionListeners(executionListeners);
	}
}
