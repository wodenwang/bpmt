/*
 * $HeadURL: $
 * $Id: $
 * Copyright (c) 2013 by Riversoft System, all rights reserved.
 */
package com.riversoft.flow;

import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;

import com.riversoft.core.web.Actions.Keys;
import com.riversoft.core.web.RequestUtils;
import com.riversoft.flow.key.FlowKeys;

/**
 * 工作流用户节点表单提交是带入的信息
 * 
 * @author woden
 * 
 */
public class FlowObject {

	public static FlowObject create(HttpServletRequest request) {
		FlowObject vo = RequestUtils.getValue(request, FlowKeys._FO.getName(), FlowObject.class);
		if (vo == null) {
			vo = new FlowObject();
		}

		HashMap<String, Object> params = RequestUtils.getJsonValue(request, Keys.PARAMS.toString());
		{
			String taskId = RequestUtils.getStringValue(request, FlowKeys._TASK_ID.getName());
			if (StringUtils.isNotEmpty(taskId)) {
				vo.taskId = taskId;
			}
			if (StringUtils.isEmpty(vo.taskId) && params != null && params.containsKey("taskId")) {
				vo.taskId = params.get("taskId").toString();
			}
		}
		{
			String ordId = RequestUtils.getStringValue(request, FlowKeys._ORD_ID.getName());
			if (StringUtils.isNotEmpty(ordId)) {
				vo.ordId = ordId;
			}
			if (StringUtils.isEmpty(vo.ordId) && params != null && params.containsKey("ordId")) {
				vo.ordId = params.get("ordId").toString();
			}
		}
		{
			String pdKey = RequestUtils.getStringValue(request, FlowKeys._PD_KEY.getName());
			if (StringUtils.isNotEmpty(pdKey)) {
				vo.pdKey = pdKey;
			}
			if (StringUtils.isEmpty(vo.pdKey) && params != null && params.containsKey("pdKey")) {
				vo.pdKey = params.get("pdKey").toString();
			}
		}
		{
			String pdId = RequestUtils.getStringValue(request, FlowKeys._PD_ID.getName());
			if (StringUtils.isNotEmpty(pdId)) {
				vo.pdId = pdId;
			}
			if (StringUtils.isEmpty(vo.pdId) && params != null && params.containsKey("pdId")) {
				vo.pdId = params.get("pdId").toString();
			}
		}
		{
			Integer init = RequestUtils.getIntegerValue(request, FlowKeys._INIT.getName());
			vo.init = (init != null && init.intValue() == 1);
		}

		{
			String flowId = RequestUtils.getStringValue(request, FlowKeys._FLOW_ID.getName());
			if (StringUtils.isNotEmpty(flowId)) {
				vo.flowId = flowId;
			}

			if (StringUtils.isEmpty(vo.flowId) && params != null && params.containsKey("flowId")) {
				vo.flowId = params.get("flowId").toString();
			}
		}
		{
			String flowName = RequestUtils.getStringValue(request, FlowKeys._FLOW_NAME.getName());
			if (StringUtils.isNotEmpty(flowName)) {
				vo.flowName = flowName;
			}
		}
		{
			String activityId = RequestUtils.getStringValue(request, FlowKeys._ACTIVITY_ID.getName());
			if (StringUtils.isNotEmpty(activityId)) {
				vo.activityId = activityId;
			}
			if (StringUtils.isEmpty(vo.activityId) && params != null && params.containsKey("activityId")) {
				vo.activityId = params.get("activityId").toString();
			}
		}
		{
			String activityName = RequestUtils.getStringValue(request, FlowKeys._ACTIVITY_NAME.getName());
			if (StringUtils.isNotEmpty(activityName)) {
				vo.activityName = activityName;
			}
		}

		return vo;
	}

	/**
	 * 是否调用流程引擎
	 */
	private boolean init = true;

	private String pdKey;
	private String pdId;
	private String pdName;

	/**
	 * 任务ID
	 */
	private String taskId;

	/**
	 * 订单ID
	 */
	private String ordId;

	/**
	 * 节点
	 */
	private String activityId;
	private String activityName;

	/**
	 * 连线
	 */
	private String flowId;
	private String flowName;

	/**
	 * @return the pdName
	 */
	public String getPdName() {
		return pdName;
	}

	/**
	 * @param pdName
	 *            the pdName to set
	 */
	public void setPdName(String pdName) {
		this.pdName = pdName;
	}

	/**
	 * @return the activityId
	 */
	public String getActivityId() {
		return activityId;
	}

	/**
	 * @param activityId
	 *            the activityId to set
	 */
	public void setActivityId(String activityId) {
		this.activityId = activityId;
	}

	/**
	 * @return the activityName
	 */
	public String getActivityName() {
		return activityName;
	}

	/**
	 * @param activityName
	 *            the activityName to set
	 */
	public void setActivityName(String activityName) {
		this.activityName = activityName;
	}

	/**
	 * @return the flowId
	 */
	public String getFlowId() {
		return flowId;
	}

	/**
	 * @param flowId
	 *            the flowId to set
	 */
	public void setFlowId(String flowId) {
		this.flowId = flowId;
	}

	/**
	 * @return the flowName
	 */
	public String getFlowName() {
		return flowName;
	}

	/**
	 * @param flowName
	 *            the flowName to set
	 */
	public void setFlowName(String flowName) {
		this.flowName = flowName;
	}

	/**
	 * @return the taskId
	 */
	public String getTaskId() {
		return taskId;
	}

	/**
	 * @param taskId
	 *            the taskId to set
	 */
	public void setTaskId(String taskId) {
		this.taskId = taskId;
	}

	/**
	 * @return the ordId
	 */
	public String getOrdId() {
		return ordId;
	}

	/**
	 * @param ordId
	 *            the ordId to set
	 */
	public void setOrdId(String ordId) {
		this.ordId = ordId;
	}

	/**
	 * @return the pdKey
	 */
	public String getPdKey() {
		return pdKey;
	}

	/**
	 * @param pdKey
	 *            the pdKey to set
	 */
	public void setPdKey(String pdKey) {
		this.pdKey = pdKey;
	}

	/**
	 * @return the init
	 */
	public boolean isInit() {
		return init;
	}

	/**
	 * @param init
	 *            the init to set
	 */
	public void setInit(boolean init) {
		this.init = init;
	}

	/**
	 * @return the pdId
	 */
	public String getPdId() {
		return pdId;
	}

	/**
	 * @param pdId
	 *            the pdId to set
	 */
	public void setPdId(String pdId) {
		this.pdId = pdId;
	}

}
