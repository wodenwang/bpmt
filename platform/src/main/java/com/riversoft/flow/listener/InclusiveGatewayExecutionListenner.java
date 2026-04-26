/*
 * $HeadURL: $
 * $Id: $
 * Copyright (c) 2016 by Riversoft System, all rights reserved.
 */
package com.riversoft.flow.listener;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.activiti.bpmn.model.BpmnModel;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.ExecutionListener;
import org.activiti.engine.impl.persistence.entity.ExecutionEntity;
import org.activiti.engine.impl.pvm.PvmTransition;
import org.apache.commons.beanutils.BeanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.riversoft.core.db.DataPO;
import com.riversoft.core.db.ORMService;
import com.riversoft.core.exception.ExceptionType;
import com.riversoft.core.exception.SystemRuntimeException;
import com.riversoft.flow.BpmnHelper;
import com.riversoft.flow.FlowFactory;
import com.riversoft.flow.key.NodeType;
import com.riversoft.flow.key.OrderHistoryModelKeys;
import com.riversoft.flow.key.VariableKeys;
import com.riversoft.platform.db.ORMAdapterService;
import com.riversoft.platform.script.ScriptHelper;
import com.riversoft.platform.script.ScriptTypes;
import com.riversoft.util.dynamicbean.DynamicBeanUtils;

/**
 * 包容网关监听
 * 
 * @author Wilmer
 * @date 2016-01-05 11:24:12
 *
 */
public class InclusiveGatewayExecutionListenner implements ExecutionListener {
	/**
	 * 记录日志
	 */
	private static final Logger logger = LoggerFactory.getLogger(InclusiveGatewayExecutionListenner.class);

	@Override
	public void notify(DelegateExecution execution) throws Exception {
		logger.debug("InclusiveGatewayExecutionListenner execution begin.....");

		String tableName = (String) FlowFactory.getRuntimeService().getVariable(execution.getId(),
				VariableKeys._ORDER_TABLE_NAME.name());

		// 获取VO,用来获取目标条件
		BpmnModel bpmnModel = FlowFactory.getRepositoryService().getBpmnModel(execution.getProcessDefinitionId());
		List<Map<String, Object>> decides = ORMService.getInstance().queryHQL(
				"from WfInclusiveGatewayDecide where pdId = ? and activityId = ? order by sort asc",
				execution.getProcessDefinitionId(), execution.getCurrentActivityId());

		if (decides == null) {
			throw new SystemRuntimeException(ExceptionType.FLOW_NO_CONFIG, "无法执行下一步.");
		}

		List<String> outcomes = new ArrayList<>();
		for (Map<String, Object> decide : decides) {
			Map<String, Object> po = (Map<String, Object>) ORMAdapterService.getInstance().findByPk(tableName,
					execution.getProcessBusinessKey());

			Map<String, Object> context = new HashMap<>();
			context.put("vo", po);

			Boolean result = (Boolean) ScriptHelper.evel(ScriptTypes.forCode((Integer) decide.get("decideType")),
					(String) decide.get("decideScript"), context);
			if (result) {
				String sequenceFlowId = (String) decide.get("flowId");
				// 条件列表
				outcomes.add(sequenceFlowId);
				// 保存流程日志
				saveHistory(execution, sequenceFlowId, po, bpmnModel);
			}
		}

		// 如果配置的分支中没有一个返回true,则报错（约束至少分支中有一个返回true）
		if (outcomes.size() <= 0) {
			throw new SystemRuntimeException(ExceptionType.FLOW_CONFIG_ERROR, "无法执行下一步,请确认流程配置分支至少有一个返回真!");
		}

		// 设置变量
		FlowFactory.getRuntimeService().setVariable(execution.getId(), VariableKeys._INCLUSIVE_GATEWAY_OUTCOME.name(),
				outcomes);

		PvmTransition transaction = ((ExecutionEntity) execution).getTransition();// 获取实时分支信息
		String sequenceFlowId = transaction != null ? transaction.getId() : null;
		String sequenceFlowName = transaction != null ? BpmnHelper.getNode(bpmnModel, sequenceFlowId).getName() : null;

		Map<String, Object> po = (Map<String, Object>) ORMAdapterService.getInstance().findByPk(tableName,
				execution.getProcessBusinessKey());

		// 保存流程日志
		saveHistory(execution, sequenceFlowId, po, bpmnModel);

		logger.debug("InclusiveGatewayExecutionListenner execution end.");
	}

	/**
	 * 保存流程日志
	 * 
	 * @param execution
	 * @param sequenceFlowId
	 * @param po
	 * @param bpmnModel
	 */
	private void saveHistory(DelegateExecution execution, String sequenceFlowId, Map<String, Object> po, BpmnModel bpmnModel) {
		if (po == null) {
			throw new SystemRuntimeException(ExceptionType.FLOW_ORD_NOT_FOUND);
		}

		String historyTableName = (String) execution.getVariable(VariableKeys._ORDER_HISTORY_TABLE_NAME.name());
		String activityId = execution.getCurrentActivityId();
		String activityName = execution.getCurrentActivityName();
		String sequenceFlowName = BpmnHelper.getNode(bpmnModel, sequenceFlowId).getName();

		Date startDate = new Date();
		Date endDate = new Date();

		DataPO historyPO = new DataPO(historyTableName, DynamicBeanUtils.cloneMap(po));
		historyPO.set(OrderHistoryModelKeys.NODE_TYPE.name(), NodeType.INCLUSIVE_GATEWAY.name());
		historyPO.set(OrderHistoryModelKeys.ACTIVITY_ID.name(), activityId);
		historyPO.set(OrderHistoryModelKeys.ACTIVITY_NAME.name(), activityName);
		historyPO.set(OrderHistoryModelKeys.SEQUENCE_FLOW_ID.name(), sequenceFlowId);
		historyPO.set(OrderHistoryModelKeys.SEQUENCE_FLOW_NAME.name(), sequenceFlowName);
		historyPO.set(OrderHistoryModelKeys.TASK_BEGIN_DATE.name(), startDate);
		historyPO.set(OrderHistoryModelKeys.TASK_END_DATE.name(), endDate);
		historyPO.set(OrderHistoryModelKeys.TASK_UID.name(), "[自动]");
		historyPO.set(OrderHistoryModelKeys.TASK_GROUP.name(), "[自动]");

		// 保存日志
		ORMAdapterService.getInstance().save(historyPO.toEntity());
	}
}
