/*
 * $HeadURL: $
 * $Id: $
 * Copyright (c) 2013 by Riversoft System, all rights reserved.
 */
package com.riversoft.flow.listener;

import java.util.Date;
import java.util.Map;

import org.activiti.bpmn.model.BpmnModel;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.ExecutionListener;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.riversoft.core.db.DataPO;
import com.riversoft.core.exception.ExceptionType;
import com.riversoft.core.exception.SystemRuntimeException;
import com.riversoft.flow.BpmnHelper;
import com.riversoft.flow.BpmnHelper.Node;
import com.riversoft.flow.FlowFactory;
import com.riversoft.flow.key.NodeType;
import com.riversoft.flow.key.OrderHistoryModelKeys;
import com.riversoft.flow.key.OrderModelKeys;
import com.riversoft.flow.key.VariableKeys;
import com.riversoft.platform.SessionManager;
import com.riversoft.platform.db.ORMAdapterService;
import com.riversoft.util.dynamicbean.DynamicBeanUtils;

/**
 * 开始节点监听器
 * 
 * @author woden
 * 
 */
@SuppressWarnings("all")
public class StartExecutionListener implements ExecutionListener {
	/**
	 * Logger for this class
	 */
	private static final Logger logger = LoggerFactory.getLogger(StartExecutionListener.class);

	@Override
	public void notify(DelegateExecution execution) throws Exception {
		String tableName = (String) FlowFactory.getRuntimeService().getVariable(execution.getId(),
				VariableKeys._ORDER_TABLE_NAME.name());
		Map<String, Object> po = (Map<String, Object>) ORMAdapterService.getInstance().findByPk(tableName,
				execution.getProcessBusinessKey());
		if (po == null) {
			throw new SystemRuntimeException(ExceptionType.FLOW_ORD_NOT_FOUND);
		}

		// 更新订单状态
		po.put(OrderModelKeys.ORD_STATE.getColumn().getName(), 1);// 已启动,运行中
		ORMAdapterService.getInstance().update(po);

		// 记录日志
		String historyTableName = (String) execution.getVariable(VariableKeys._ORDER_HISTORY_TABLE_NAME.name());
		if (StringUtils.isNotEmpty(historyTableName)) {

			BpmnModel bpmnModel = FlowFactory.getRepositoryService().getBpmnModel(execution.getProcessDefinitionId());

			String activityId = execution.getCurrentActivityId();
			Node activityNode = BpmnHelper.getNode(bpmnModel, activityId);
			String activityName = execution.getCurrentActivityName();
			String sequenceFlowId = activityNode.getSequenceFlows().get(0).getId();
			String sequenceFlowName = BpmnHelper.getNode(bpmnModel, sequenceFlowId).getName();
			Date startDate = new Date();
			Date endDate = new Date();

			DataPO historyPO = new DataPO(historyTableName, DynamicBeanUtils.cloneMap(po));
			historyPO.set(OrderHistoryModelKeys.NODE_TYPE.name(), NodeType.START_EVENT.name());
			historyPO.set(OrderHistoryModelKeys.ACTIVITY_ID.name(), activityId);
			historyPO.set(OrderHistoryModelKeys.ACTIVITY_NAME.name(), activityName);
			historyPO.set(OrderHistoryModelKeys.SEQUENCE_FLOW_ID.name(), sequenceFlowId);
			historyPO.set(OrderHistoryModelKeys.SEQUENCE_FLOW_NAME.name(), sequenceFlowName);
			historyPO.set(OrderHistoryModelKeys.TASK_BEGIN_DATE.name(), new Date());
			historyPO.set(OrderHistoryModelKeys.TASK_END_DATE.name(), new Date());
			historyPO.set(OrderHistoryModelKeys.TASK_UID.name(), SessionManager.getUser() != null ? SessionManager.getUser().getUid() : "_system");
			historyPO.set(OrderHistoryModelKeys.TASK_GROUP.name(), SessionManager.getGroup() != null ? SessionManager.getGroup().getGroupKey() : "_system");
			ORMAdapterService.getInstance().save(historyPO.toEntity());
		}

	}

}
