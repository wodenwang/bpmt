/*
 * $HeadURL: $
 * $Id: $
 * Copyright (c) 2013 by Riversoft System, all rights reserved.
 */
package com.riversoft.module.flow.activity.servicetask;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.activiti.bpmn.model.BpmnModel;
import org.activiti.engine.RepositoryService;

import com.riversoft.core.BeanFactory;
import com.riversoft.core.db.DataCondition;
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
 * 判断节点设置
 * 
 * @author woden
 * 
 */
@SuppressWarnings("unchecked")
@ActivityType(NodeType.SERVICE_TASK)
public class ServiceTaskConfigAction extends BaseActivityConfigAction {

	@Override
	public void main(HttpServletRequest request, HttpServletResponse response, String pdId, String activityId) {
		Map<String, Object> pk = new DataPO("WfServiceTask").toEntity();
		pk.put("pdId", pdId);
		pk.put("activityId", activityId);
		Map<String, Object> po = (Map<String, Object>) ORMService.getInstance().findByPk("WfServiceTask",
				(Serializable) pk);
		if (po != null) {
			request.setAttribute("vo", po);
		}

		RepositoryService service = FlowFactory.getRepositoryService();
		BpmnModel bpmnModel = service.getBpmnModel(pdId);
		BpmnHelper.Node node = BpmnHelper.getNode(bpmnModel, activityId);
		request.setAttribute("node", node);

		List<Map<String, Object>> logics = ORMService.getInstance().query(
				"WfServiceTaskLogic",
				new DataCondition().setStringEqual("pdId", pdId).setStringEqual("activityId", activityId)
						.setOrderByAsc("sort").toEntity());
		request.setAttribute("logics", logics);

		Actions.includePage(request, response, Util.getPagePath(request, "config.jsp"));
	}

	/**
	 * 添加逻辑
	 * 
	 * @param request
	 * @param response
	 */
	public void addLogicForm(HttpServletRequest request, HttpServletResponse response) {
		Actions.includePage(request, response, Util.getPagePath(request, "tab.jsp"));
	}

	/**
	 * 保存配置
	 * 
	 * @param request
	 * @param response
	 * @param pdId
	 * @param activityId
	 */
	@ActionAccess(level = SafeLevel.DEV_W)
	public void submitForm(HttpServletRequest request, HttpServletResponse response) {
		String pdId = RequestUtils.getStringValue(request, "pdId");
		String activityId = RequestUtils.getStringValue(request, "activityId");
		ServiceTaskConfigService service = BeanFactory.getInstance().getBean(ServiceTaskConfigService.class);

		// 准备PO
		DataPO po = new DataPO("WfServiceTask");
		po.set("pdId", pdId);
		po.set("activityId", activityId);
		po.set("description", RequestUtils.getStringValue(request, "description"));

		List<Map<String, Object>> itmes = new ArrayList<>();
		String[] logics = RequestUtils.getStringValues(request, "logics");
		if (logics != null) {
			int sort = 0;
			for (String pixel : logics) {
				String description = RequestUtils.getStringValue(request, pixel + ".description");
				Integer logicType = RequestUtils.getIntegerValue(request, pixel + ".logicType");
				String logicScript = RequestUtils.getStringValue(request, pixel + ".logicScript");
				Integer errorType = RequestUtils.getIntegerValue(request, pixel + ".errorType");

				DataPO item = new DataPO("WfServiceTaskLogic");
				item.set("pdId", pdId);
				item.set("activityId", activityId);
				item.set("description", description);
				item.set("logicType", logicType);
				item.set("logicScript", logicScript);
				item.set("errorType", errorType);
				item.set("sort", sort++);
				itmes.add(item.toEntity());
			}
		}
		service.executeSaveConfig(po.toEntity(), itmes);

		Actions.redirectInfoPage(request, response, "保存成功.");
	}

}
