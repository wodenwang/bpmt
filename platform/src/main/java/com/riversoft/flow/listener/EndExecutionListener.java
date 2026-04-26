/*
 * $HeadURL: $
 * $Id: $
 * Copyright (c) 2013 by Riversoft System, all rights reserved.
 */
package com.riversoft.flow.listener;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.activiti.bpmn.model.BpmnModel;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.ExecutionListener;
import org.activiti.engine.impl.persistence.entity.ExecutionEntity;
import org.activiti.engine.runtime.ProcessInstance;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.riversoft.core.db.DataPO;
import com.riversoft.core.db.ORMService;
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
public class EndExecutionListener implements ExecutionListener {
	/**
	 * Logger for this class
	 */
	private static final Logger logger = LoggerFactory.getLogger(EndExecutionListener.class);

	@Override
	public void notify(DelegateExecution execution) throws Exception {
		String tableName = (String) FlowFactory.getRuntimeService().getVariable(execution.getId(),
				VariableKeys._ORDER_TABLE_NAME.name());
		Map<String, Object> po = (Map<String, Object>) ORMAdapterService.getInstance().findByPk(tableName,
				execution.getProcessBusinessKey());

		if (po == null) {
			throw new SystemRuntimeException(ExceptionType.FLOW_ORD_NOT_FOUND);
		}

		// 异常结束的情况
		int state = 2;
		{
			Map<String, Object> config = (Map<String, Object>) ORMService.getInstance().findHQL(
					"from WfEndEvent where pdId = ? and activityId = ?", execution.getProcessDefinitionId(),
					execution.getCurrentActivityId());
			if (config != null) {
				state = (int) config.get("stateType");
			}
		}
		po.put(OrderModelKeys.ORD_STATE.getColumn().getName(), state);// 已完成
		ORMAdapterService.getInstance().update(po);

		// 记录日志
		String historyTableName = (String) execution.getVariable(VariableKeys._ORDER_HISTORY_TABLE_NAME.name());
		if (StringUtils.isNotEmpty(historyTableName)) {

			String activityId = null;
			if (execution instanceof ExecutionEntity) {
				activityId = ((ExecutionEntity) execution).getActivityId();
			}

			if (StringUtils.isEmpty(activityId)) {
				ProcessInstance pi = FlowFactory.getRuntimeService().createProcessInstanceQuery()
						.processInstanceId(execution.getProcessInstanceId()).singleResult();
				activityId = pi.getActivityId();
			}

			BpmnModel bpmnModel = FlowFactory.getRepositoryService().getBpmnModel(execution.getProcessDefinitionId());
			Node activityNode = BpmnHelper.getNode(bpmnModel, activityId);
			String activityName = activityNode.getName();
			Date startDate = new Date();
			Date endDate = new Date();
			DataPO historyPO = new DataPO(historyTableName, DynamicBeanUtils.cloneMap(po));
			historyPO.set(OrderHistoryModelKeys.NODE_TYPE.name(), NodeType.END_EVENT.name());
			historyPO.set(OrderHistoryModelKeys.ACTIVITY_ID.name(), activityId);
			historyPO.set(OrderHistoryModelKeys.ACTIVITY_NAME.name(), activityName);
			historyPO.set(OrderHistoryModelKeys.TASK_BEGIN_DATE.name(), startDate);
			historyPO.set(OrderHistoryModelKeys.TASK_END_DATE.name(), endDate);
			historyPO.set(OrderHistoryModelKeys.TASK_UID.name(), SessionManager.getUser().getUid());
			historyPO.set(OrderHistoryModelKeys.TASK_GROUP.name(), SessionManager.getGroup().getGroupKey());
			ORMAdapterService.getInstance().save(historyPO.toEntity());
		}

	}

}
