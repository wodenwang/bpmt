/*
 * $HeadURL: $
 * $Id: $
 * Copyright (c) 2013 by Riversoft System, all rights reserved.
 */
package com.riversoft.module.flow.activity.endevent;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.activiti.bpmn.model.BpmnModel;
import org.activiti.engine.RepositoryService;

import com.riversoft.core.db.DataPO;
import com.riversoft.core.db.ORMService;
import com.riversoft.core.web.Actions;
import com.riversoft.core.web.Actions.Util;
import com.riversoft.core.web.RequestUtils;
import com.riversoft.core.web.annotation.ActionAccess;
import com.riversoft.core.web.annotation.ActionAccess.SafeLevel;
import com.riversoft.flow.BpmnHelper;
import com.riversoft.flow.FlowFactory;
import com.riversoft.flow.config.ActivityType;
import com.riversoft.flow.config.BaseActivityConfigAction;
import com.riversoft.flow.key.NodeType;

/**
 * @author woden
 * 
 */
@ActivityType(NodeType.END_EVENT)
public class EndEventConfigAction extends BaseActivityConfigAction {

	@Override
	public void main(HttpServletRequest request, HttpServletResponse response, String pdId, String activityId) {
		Map<String, Object> po = (Map<String, Object>) ORMService.getInstance().findHQL(
				"from WfEndEvent where pdId = ? and activityId = ?", pdId, activityId);
		if (po != null) {
			request.setAttribute("vo", po);
		}

		RepositoryService service = FlowFactory.getRepositoryService();
		BpmnModel bpmnModel = service.getBpmnModel(pdId);
		BpmnHelper.Node node = BpmnHelper.getNode(bpmnModel, activityId);
		request.setAttribute("node", node);

		Actions.includePage(request, response, Util.getPagePath(request, "config.jsp"));
	}

	/**
	 * 提交保存
	 * 
	 * @param request
	 * @param response
	 */
	@ActionAccess(level = SafeLevel.DEV_W)
	public void submitForm(HttpServletRequest request, HttpServletResponse response) {
		String pdId = RequestUtils.getStringValue(request, "pdId");
		String activityId = RequestUtils.getStringValue(request, "activityId");
		Map<String, Object> po = (Map<String, Object>) ORMService.getInstance().findHQL(
				"from WfEndEvent where pdId = ? and activityId = ?", pdId, activityId);
		if (po == null) {
			po = new DataPO("WfEndEvent").toEntity();
			po.put("pdId", pdId);
			po.put("activityId", activityId);
		}

		po.put("stateType", RequestUtils.getIntegerValue(request, "stateType"));
		po.put("description", RequestUtils.getStringValue(request, "description"));

		ORMService.getInstance().saveOrUpdate(po);

		Actions.redirectInfoPage(request, response, "保存成功.");

	}
}
