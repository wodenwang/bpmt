/*
 * $HeadURL: $
 * $Id: $
 * Copyright (c) 2013 by Riversoft System, all rights reserved.
 */
package com.riversoft.module.flow.activity.inclusivegateway;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.activiti.bpmn.model.BpmnModel;
import org.activiti.bpmn.model.SequenceFlow;
import org.activiti.engine.RepositoryService;

import com.riversoft.core.BeanFactory;
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
 * @author Wilmer
 * 
 */
@ActivityType(NodeType.INCLUSIVE_GATEWAY)
public class InclusiveGatewayConfigAction extends BaseActivityConfigAction {
	@Override
	public void main(HttpServletRequest request, HttpServletResponse response, String pdId, String activityId) {
		Map<String, Object> pk = new DataPO("WfInclusiveGateway").toEntity();
		pk.put("pdId", pdId);
		pk.put("activityId", activityId);
		Map<String, Object> po = (Map<String, Object>) ORMService.getInstance().findByPk("WfInclusiveGateway",
				(Serializable) pk);
		if (po != null) {
			request.setAttribute("vo", po);
		}

		RepositoryService service = FlowFactory.getRepositoryService();
		BpmnModel bpmnModel = service.getBpmnModel(pdId);
		BpmnHelper.Node node = BpmnHelper.getNode(bpmnModel, activityId);
		request.setAttribute("node", node);

		List<Map<String, Object>> items = new ArrayList<>();
		List<SequenceFlow> sequenceFlows = BpmnHelper.getOuterSequenceFlows(bpmnModel, activityId);
		for (SequenceFlow sequenceFlow : sequenceFlows) {
			Map<String, Object> item = new HashMap<>();
			item.put("flowName", sequenceFlow.getName());
			item.put("flowId", sequenceFlow.getId());
			item.put("sort", 999);
			// 查找配置
			Map<String, Object> itemPO = (Map<String, Object>) ORMService.getInstance().findHQL(
					"from WfInclusiveGatewayDecide where pdId = ? and activityId = ? and flowId = ?", pdId, activityId,
					sequenceFlow.getId());
			if (itemPO != null) {
				item.put("decideType", itemPO.get("decideType"));
				item.put("decideScript", itemPO.get("decideScript"));
				item.put("sort", itemPO.get("sort"));
				item.put("description", itemPO.get("description"));
			}
			items.add(item);
		}

		Collections.sort(items, new Comparator<Map<String, Object>>() {
			@Override
			public int compare(Map<String, Object> o1, Map<String, Object> o2) {
				return (Integer) o1.get("sort") < (Integer) o2.get("sort") ? -1 : 1;
			}
		});
		request.setAttribute("items", items);

		Actions.includePage(request, response, Util.getPagePath(request, "config.jsp"));
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
		InclusiveGatewayConfigService service = BeanFactory.getInstance().getBean(InclusiveGatewayConfigService.class);

		// 准备PO
		DataPO po = new DataPO("WfInclusiveGateway");
		po.set("pdId", pdId);
		po.set("activityId", activityId);
		po.set("description", RequestUtils.getStringValue(request, "description"));

		List<Map<String, Object>> itmes = new ArrayList<>();
		String[] decides = RequestUtils.getStringValues(request, "decides");
		if (decides != null) {
			int sort = 0;
			for (String pixel : decides) {
				String flowId = RequestUtils.getStringValue(request, pixel + ".flowId");
				String description = RequestUtils.getStringValue(request, pixel + ".description");
				Integer decideType = RequestUtils.getIntegerValue(request, pixel + ".decideType");
				String decideScript = RequestUtils.getStringValue(request, pixel + ".decideScript");

				DataPO item = new DataPO("WfInclusiveGatewayDecide");
				item.set("pdId", pdId);
				item.set("activityId", activityId);
				item.set("flowId", flowId);
				item.set("description", description);
				item.set("decideType", decideType);
				item.set("decideScript", decideScript);
				item.set("sort", sort++);
				itmes.add(item.toEntity());
			}
		}
		service.executeSaveConfig(po.toEntity(), itmes);

		Actions.redirectInfoPage(request, response, "保存成功.");
	}
}
