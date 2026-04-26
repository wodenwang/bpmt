/*
 * $HeadURL: $
 * $Id: $
 * Copyright (c) 2013 by Riversoft System, all rights reserved.
 */
package com.riversoft.flow.listener;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.activiti.bpmn.model.BpmnModel;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.ExecutionListener;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang3.StringUtils;
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
 * @author woden
 * 
 */
public class ExclusiveGatewayExecutionListenner implements ExecutionListener {
	/**
	 * Logger for this class
	 */
	private static final Logger logger = LoggerFactory.getLogger(ExclusiveGatewayExecutionListenner.class);

	@Override
	public void notify(DelegateExecution execution) throws Exception {

		String tableName = (String) FlowFactory.getRuntimeService().getVariable(execution.getId(), VariableKeys._ORDER_TABLE_NAME.name());
		Map<String, Object> po = (Map<String, Object>) ORMAdapterService.getInstance().findByPk(tableName, execution.getProcessBusinessKey());

		if (po == null) {
			throw new SystemRuntimeException(ExceptionType.FLOW_ORD_NOT_FOUND);
		}

		// 获取VO,用来获取目标条件
		BpmnModel bpmnModel = FlowFactory.getRepositoryService().getBpmnModel(execution.getProcessDefinitionId());
		String sequenceFlowId = null;
		List<Map<String, Object>> decides = ORMService.getInstance().queryHQL("from WfExclusiveGatewayDecide where pdId = ? and activityId = ? order by sort asc", execution.getProcessDefinitionId(),
				execution.getCurrentActivityId());
		Map<String, Object> context = new HashMap<>();
		context.put("vo", po);
		if (decides == null) {
			throw new SystemRuntimeException(ExceptionType.FLOW_NO_CONFIG, "无法执行下一步.");
		}

		for (Map<String, Object> decide : decides) {
			Boolean result = (Boolean) ScriptHelper.evel(ScriptTypes.forCode((Integer) decide.get("decideType")), (String) decide.get("decideScript"), context);
			if (result) {
				sequenceFlowId = (String) decide.get("flowId");
				break;
			}
		}

		if (sequenceFlowId == null) {
			throw new SystemRuntimeException(ExceptionType.FLOW_CONFIG_ERROR, "无法执行下一步.");
		}

		// 设置变量
		FlowFactory.getRuntimeService().setVariable(execution.getId(), VariableKeys._EXCLUSIVE_GATEWAY_OUTCOME.name(), sequenceFlowId);

		// 记录日志
		String historyTableName = (String) execution.getVariable(VariableKeys._ORDER_HISTORY_TABLE_NAME.name());
		if (StringUtils.isNotEmpty(historyTableName)) {

			String activityId = execution.getCurrentActivityId();
			String activityName = execution.getCurrentActivityName();
			String sequenceFlowName = BpmnHelper.getNode(bpmnModel, sequenceFlowId).getName();
			Date startDate = new Date();
			Date endDate = new Date();
			DataPO historyPO = new DataPO(historyTableName, DynamicBeanUtils.cloneMap(po));
			historyPO.set(OrderHistoryModelKeys.NODE_TYPE.name(), NodeType.EXCLUSIVE_GATEWAY.name());
			historyPO.set(OrderHistoryModelKeys.ACTIVITY_ID.name(), activityId);
			historyPO.set(OrderHistoryModelKeys.ACTIVITY_NAME.name(), activityName);
			historyPO.set(OrderHistoryModelKeys.SEQUENCE_FLOW_ID.name(), sequenceFlowId);
			historyPO.set(OrderHistoryModelKeys.SEQUENCE_FLOW_NAME.name(), sequenceFlowName);
			historyPO.set(OrderHistoryModelKeys.TASK_BEGIN_DATE.name(), startDate);
			historyPO.set(OrderHistoryModelKeys.TASK_END_DATE.name(), endDate);
			historyPO.set(OrderHistoryModelKeys.TASK_UID.name(), "[自动]");
			historyPO.set(OrderHistoryModelKeys.TASK_GROUP.name(), "[自动]");
			ORMAdapterService.getInstance().save(historyPO.toEntity());
		}

	}
}
