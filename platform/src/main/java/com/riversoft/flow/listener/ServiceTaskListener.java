/*
 * $HeadURL: $
 * $Id: $
 * Copyright (c) 2013 by Riversoft System, all rights reserved.
 */
package com.riversoft.flow.listener;

import java.lang.reflect.InvocationTargetException;
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

import com.riversoft.core.db.DataCondition;
import com.riversoft.core.db.DataPO;
import com.riversoft.core.db.ORMService;
import com.riversoft.core.exception.ExceptionType;
import com.riversoft.core.exception.SystemRuntimeException;
import com.riversoft.flow.BpmnHelper;
import com.riversoft.flow.BpmnHelper.Node;
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
@SuppressWarnings("all")
public class ServiceTaskListener implements ExecutionListener {
	/**
	 * Logger for this class
	 */
	private static final Logger logger = LoggerFactory.getLogger(ServiceTaskListener.class);

	@Override
	public void notify(DelegateExecution execution) {

		String pdId = execution.getProcessDefinitionId();
		String activityId = execution.getCurrentActivityId();

		String tableName = (String) FlowFactory.getRuntimeService().getVariable(execution.getId(), VariableKeys._ORDER_TABLE_NAME.name());
		Map<String, Object> po = (Map<String, Object>) ORMAdapterService.getInstance().findByPk(tableName, execution.getProcessBusinessKey());

		if (po == null) {
			throw new SystemRuntimeException(ExceptionType.FLOW_ORD_NOT_FOUND);
		}

		Map<String, Object> context = new HashMap<>();
		context.put("vo", po);

		StringBuffer executionMemo = new StringBuffer();

		List<Map<String, Object>> logics = ORMService.getInstance().query("WfServiceTaskLogic",
				new DataCondition().setStringEqual("pdId", pdId).setStringEqual("activityId", activityId).setOrderByAsc("sort").toEntity());
		if (logics != null) {
			for (Map<String, Object> logic : logics) {

				String description = (String) logic.get("description");
				Integer logicType = (Integer) logic.get("logicType");
				String logicScript = (String) logic.get("logicScript");
				int errorType = (int) logic.get("errorType");

				logger.debug("开始处理逻辑[{}]", description);
				try {
					ScriptHelper.evel(ScriptTypes.forCode(logicType), logicScript, context);
					executionMemo.append("执行逻辑[" + description + "]成功;");
				} catch (Exception e) {
					logger.warn("执行逻辑[" + description + "]出错.", e);
					if (errorType == 1) {// 回滚事务
						if (e instanceof SystemRuntimeException) {
							throw e;
						} else {
							throw new SystemRuntimeException(ExceptionType.FLOW_LOGIC_ERROR, "执行逻辑[" + description + "]出错,该操作被回滚.", e);
						}
					} else if (errorType == 0) {
						// TODO 记录日志
						executionMemo.append("执行逻辑[" + description + "]失败,跳过;");
					}
				}
			}
		}

		// 记录日志
		String historyTableName = (String) execution.getVariable(VariableKeys._ORDER_HISTORY_TABLE_NAME.name());
		if (StringUtils.isNotEmpty(historyTableName)) {

			BpmnModel bpmnModel = FlowFactory.getRepositoryService().getBpmnModel(execution.getProcessDefinitionId());

			Node activityNode = BpmnHelper.getNode(bpmnModel, activityId);
			String activityName = execution.getCurrentActivityName();
			String sequenceFlowId = activityNode.getSequenceFlows().get(0).getId();
			String sequenceFlowName = BpmnHelper.getNode(bpmnModel, sequenceFlowId).getName();
			Date startDate = new Date();
			Date endDate = new Date();
			DataPO historyPO = new DataPO(historyTableName, DynamicBeanUtils.cloneMap(po));
			historyPO.set(OrderHistoryModelKeys.NODE_TYPE.name(), NodeType.findByType(activityNode.getElement().getClass()).name());
			historyPO.set(OrderHistoryModelKeys.ACTIVITY_ID.name(), activityId);
			historyPO.set(OrderHistoryModelKeys.ACTIVITY_NAME.name(), activityName);
			historyPO.set(OrderHistoryModelKeys.SEQUENCE_FLOW_ID.name(), sequenceFlowId);
			historyPO.set(OrderHistoryModelKeys.SEQUENCE_FLOW_NAME.name(), sequenceFlowName);
			historyPO.set(OrderHistoryModelKeys.TASK_BEGIN_DATE.name(), startDate);
			historyPO.set(OrderHistoryModelKeys.TASK_END_DATE.name(), endDate);
			historyPO.set(OrderHistoryModelKeys.TASK_UID.name(), "[自动]");
			historyPO.set(OrderHistoryModelKeys.TASK_GROUP.name(), "[自动]");
			historyPO.set(OrderHistoryModelKeys.EXECUTION_MEMO.name(), executionMemo.toString());
			ORMAdapterService.getInstance().save(historyPO.toEntity());

		}
	}
}
