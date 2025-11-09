/*
 * $HeadURL: $
 * $Id: $
 * Copyright (c) 2013 by Riversoft System, all rights reserved.
 */
package com.riversoft.flow.key;

import org.activiti.bpmn.model.EndEvent;
import org.activiti.bpmn.model.ExclusiveGateway;
import org.activiti.bpmn.model.FlowElement;
import org.activiti.bpmn.model.FlowNode;
import org.activiti.bpmn.model.ParallelGateway;
import org.activiti.bpmn.model.ReceiveTask;
import org.activiti.bpmn.model.ServiceTask;
import org.activiti.bpmn.model.StartEvent;
import org.activiti.bpmn.model.UserTask;
import org.activiti.bpmn.model.InclusiveGateway;

import com.riversoft.core.db.po.Code2NameVO;

/**
 * 节点类型(框架所支持的节点枚举)
 * 
 * @author woden
 * 
 */
public enum NodeType implements Code2NameVO {
	/**
	 * 未知
	 */
	DEFAULT(FlowNode.class, "未知节点", null),

	/**
	 * 事件EVENT
	 */
	START_EVENT(
			StartEvent.class,
			"开始节点",
			"WfStartEvent;WfStartEventColumnForm;WfStartEventColumnLine;WfStartEventColumnExtend;WfStartEventExecBefore;WfStartEventExecAfter;WfStartEventBtnStart;WfStartEventBtnSave;WfStartEventSubExtend"), END_EVENT(
			EndEvent.class, "结束节点", "WfEndEvent"),

	/**
	 * 任务TASK
	 */
	USER_TASK(
			UserTask.class,
			"用户任务",
			"WfUserTask;WfUserTaskAssignee;WfUserTaskBtn;WfUserTaskBtnSave;WfUserTaskBtnForward;WfUserTaskExecBefore;WfUserTaskExecAfter;WfUserTaskColumnExtend;WfUserTaskColumnForm;WfUserTaskSubExtend"), SERVICE_TASK(
			ServiceTask.class, "自动处理节点", "WfServiceTask;WfServiceTaskLogic"), RECEIVE_TASK(ReceiveTask.class, "等待节点",
			null),

	/**
	 * 网关GATEWAY
	 */
	PARALLEL_GATEWAY(ParallelGateway.class, "拆分/合并节点", null), EXCLUSIVE_GATEWAY(ExclusiveGateway.class, "条件判断节点",
			"WfExclusiveGateway;WfExclusiveGatewayDecide"),INCLUSIVE_GATEWAY(InclusiveGateway.class, "包容网关判断节点",
					"WfInclusiveGateway;WfInclusiveGatewayDecide"),
	;

	private Class<? extends FlowElement> type;// 对应节点类型
	private String name;// 展示名
	private String configTables;// 配置表(实体),多个用;分隔

	private NodeType(Class<? extends FlowElement> type, String name, String configTables) {
		this.type = type;
		this.name = name;
		this.configTables = configTables;
	}

	/**
	 * 根据节点类型翻译
	 * 
	 * @param type
	 * @return
	 */
	public static NodeType findByType(Class<? extends FlowElement> type) {
		for (NodeType nodeType : values()) {
			if (nodeType.type == type) {
				return nodeType;
			}
		}

		return null;
	}

	/**
	 * @return the type
	 */
	public Class<? extends FlowElement> getType() {
		return type;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return the configTables
	 */
	public String getConfigTables() {
		return configTables;
	}

	@Override
	public Object getCode() {
		return name();
	}

	@Override
	public String getShowName() {
		// TODO Auto-generated method stub
		return name;
	}
}
