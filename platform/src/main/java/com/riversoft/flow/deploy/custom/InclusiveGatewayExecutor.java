/*
 * $HeadURL: $
 * $Id: $
 * Copyright (c) 2016 by Riversoft System, all rights reserved.
 */
package com.riversoft.flow.deploy.custom;

import java.util.ArrayList;
import java.util.List;

import org.activiti.bpmn.model.ActivitiListener;
import org.activiti.bpmn.model.BpmnModel;
import org.activiti.bpmn.model.ImplementationType;
import org.activiti.bpmn.model.InclusiveGateway;
import org.activiti.bpmn.model.SequenceFlow;
import org.activiti.engine.delegate.ExecutionListener;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.riversoft.flow.deploy.CustomNode;
import com.riversoft.flow.deploy.CustomNodeExecutor;
import com.riversoft.flow.key.VariableKeys;
import com.riversoft.flow.listener.InclusiveGatewayExecutionListenner;

/**
 * 包容网关
 * 
 * @author Wilmer
 * @date 2016-01-05 11:08:34
 */
@CustomNode(InclusiveGateway.class)
public class InclusiveGatewayExecutor implements CustomNodeExecutor<InclusiveGateway> {
	/**
	 * 记录日志
	 */
	private static final Logger logger = LoggerFactory.getLogger(InclusiveGatewayExecutor.class);

	/*
	 * 执行处理
	 */
	@Override
	public void execute(BpmnModel model, InclusiveGateway node) {
		logger.debug("InclusiveGateway execute begin.....");

		// 添加线段的条件
		for (SequenceFlow flow : node.getOutgoingFlows()) {
			logger.debug("InclusiveGatewayExecutor连线:" + flow.getId());
			// 设置条件
			flow.setConditionExpression(
					"${" + VariableKeys._INCLUSIVE_GATEWAY_OUTCOME.name() + ".contains('" + flow.getId() + "')}");
		}

		List<ActivitiListener> executionListeners = new ArrayList<>();
		// 加上监听器
		{
			ActivitiListener activitiListener = new ActivitiListener();
			activitiListener.setEvent(ExecutionListener.EVENTNAME_START);
			activitiListener.setImplementationType(ImplementationType.IMPLEMENTATION_TYPE_CLASS);
			activitiListener.setImplementation(InclusiveGatewayExecutionListenner.class.getName());
			executionListeners.add(activitiListener);
			node.setExecutionListeners(executionListeners);
		}

		if (StringUtils.isEmpty(node.getName())) {
			List<SequenceFlow> sequenceFlows = node.getOutgoingFlows();
			if (sequenceFlows == null || sequenceFlows.size() <= 1) {
				node.setName("包容汇合");
			} else {
				node.setName("包容拆分");
			}
		}

		logger.debug("InclusiveGateway execute end.");
	}
}
