/*
 * $HeadURL: $
 * $Id: $
 * Copyright (c) 2013 by Riversoft System, all rights reserved.
 */
package com.riversoft.flow.key;

import com.riversoft.flow.FlowObject;

/**
 * 流程相关KEY
 * 
 * @author woden
 * 
 */
public enum FlowKeys {

	/**
	 * {@link FlowObject} 对象
	 */
	_FO("整个工作流节点信息对象"),
	/**
	 * 1:保存并启动流程;2:保存;
	 */
	_INIT("初始化表单提交状态"),
	/**
	 * 工作流任务ID
	 */
	_TASK_ID("任务ID"),
	/**
	 * 工作流定义ID
	 */
	// _PD_ID("流程ID"),
	_PD_KEY("流程定义KEY"), _PD_ID("流程定义ID"),
	/**
	 * 订单实体表ID
	 */
	_ORD_ID("订单ID"),
	/**
	 * 连线
	 */
	_FLOW_ID("连线ID"), _FLOW_NAME("连线名"),
	/**
	 * 任务节点
	 */
	_ACTIVITY_ID("节点ID"), _ACTIVITY_NAME("节点名");

	private String busiName;

	private FlowKeys(String busiName) {
		this.busiName = busiName;
	}

	public String getName() {
		return this.name();
	}

	public String getBusiName() {
		return busiName;
	}
}
