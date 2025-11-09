/*
 * $HeadURL: $
 * $Id: $
 * Copyright (c) 2013 by Riversoft System, all rights reserved.
 */
package com.riversoft.module.flow;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.stream.XMLStreamException;

import org.activiti.bpmn.model.BpmnModel;
import org.activiti.bpmn.model.FlowElement;
import org.activiti.bpmn.model.FlowNode;
import org.activiti.bpmn.model.GraphicInfo;
import org.activiti.bpmn.model.SequenceFlow;
import org.activiti.engine.ProcessEngineConfiguration;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.repository.ProcessDefinitionQuery;
import org.activiti.image.ProcessDiagramGenerator;
import org.activiti.image.impl.DefaultProcessDiagramGenerator;
import org.apache.commons.lang3.StringUtils;

import com.riversoft.core.BeanFactory;
import com.riversoft.core.db.DataPO;
import com.riversoft.core.db.DataPackage;
import com.riversoft.core.db.JdbcService;
import com.riversoft.core.db.ORMService;
import com.riversoft.core.exception.ExceptionType;
import com.riversoft.core.exception.SystemRuntimeException;
import com.riversoft.core.web.Actions;
import com.riversoft.core.web.Actions.Util;
import com.riversoft.core.web.RequestUtils;
import com.riversoft.core.web.annotation.ActionAccess;
import com.riversoft.core.web.annotation.ActionAccess.SafeLevel;
import com.riversoft.flow.BpmnHelper;
import com.riversoft.flow.BpmnHelper.Node;
import com.riversoft.flow.FlowFactory;
import com.riversoft.flow.FlowService;
import com.riversoft.flow.config.ActivityConfigScanner;
import com.riversoft.flow.key.NodeType;
import com.riversoft.flow.key.OrderHistoryModelKeys;
import com.riversoft.flow.key.OrderOpinionModelKeys;
import com.riversoft.platform.db.model.ModelKeyUtils;
import com.riversoft.platform.po.TbTable;
import com.riversoft.platform.po.VwUrl;
import com.riversoft.platform.po.WfPd;
import com.riversoft.platform.web.view.ViewActionBuilder;
import com.riversoft.util.jackson.JsonMapper;

/**
 * 流程定义
 * 
 * @author woden
 * 
 */
@ActionAccess(level = SafeLevel.DEV_R)
public class PdAction {

	/**
	 * 流程定义首页
	 * 
	 * @param request
	 * @param response
	 */
	public void index(HttpServletRequest request, HttpServletResponse response) {
		Actions.includePage(request, response, Util.getPagePath(request, "main.jsp"));
	}

	/**
	 * 快捷配置-编辑
	 * 
	 * @param request
	 * @param response
	 */
	@SuppressWarnings("unchecked")
	public void editBatch(HttpServletRequest request, HttpServletResponse response) {
		String hbm = RequestUtils.getStringValue(request, "hbm");
		Long id = RequestUtils.getLongValue(request, "id");
		String activityId = RequestUtils.getStringValue(request, "activityId");
		String flowId = RequestUtils.getStringValue(request, "flowId");
		String pdId = RequestUtils.getStringValue(request, "pdId");

		ProcessDefinition pd = FlowFactory.getRepositoryService().getProcessDefinition(pdId);
		request.setAttribute("pd", pd);

		Map<String, Object> vo;
		if (id == null || id == 0) {// 按钮类的
			Map<String, Object> key = new HashMap<>();
			key.put("pdId", pdId);
			if (StringUtils.isNotEmpty(activityId)) {
				key.put("activityId", activityId);
			}
			if (StringUtils.isNotEmpty(flowId)) {
				key.put("flowId", flowId);
			}
			vo = (Map<String, Object>) ORMService.getInstance().findByPk(hbm, (Serializable) key);
		} else {
			vo = (Map<String, Object>) ORMService.getInstance().findByPk(hbm, id);
		}

		BpmnModel bpmnModel = FlowFactory.getRepositoryService().getBpmnModel(pdId);
		Node node;
		if (StringUtils.isNotEmpty(activityId)) {
			node = BpmnHelper.getNode(bpmnModel, activityId);
		} else {
			node = BpmnHelper.getNode(bpmnModel, NodeType.START_EVENT);
		}

		if (StringUtils.isNotEmpty((String) vo.get("flowId"))) {
			request.setAttribute("sequenceFlow", bpmnModel.getFlowElement((String) vo.get("flowId")));
		}

		request.setAttribute("vo", vo);
		request.setAttribute("node", node);
		request.setAttribute("hbm", hbm);

		Actions.includePage(request, response, Util.getPagePath(request, "batch_form.jsp"));
	}

	/**
	 * 快捷配置-保存修改
	 * 
	 * @param request
	 * @param response
	 */
	@SuppressWarnings("unchecked")
	public void submitBatch(HttpServletRequest request, HttpServletResponse response) {
		String hbm = RequestUtils.getStringValue(request, "hbm");
		Long id = RequestUtils.getLongValue(request, "id");
		String activityId = RequestUtils.getStringValue(request, "activityId");
		String flowId = RequestUtils.getStringValue(request, "flowId");
		String pdId = RequestUtils.getStringValue(request, "pdId");

		Map<String, Object> vo;
		if (id == null || id == 0) {// 按钮类的
			Map<String, Object> key = new HashMap<>();
			key.put("pdId", pdId);
			if (StringUtils.isNotEmpty(activityId)) {
				key.put("activityId", activityId);
			}
			if (StringUtils.isNotEmpty(flowId)) {
				key.put("flowId", flowId);
			}
			vo = (Map<String, Object>) ORMService.getInstance().findByPk(hbm, (Serializable) key);
		} else {
			vo = (Map<String, Object>) ORMService.getInstance().findByPk(hbm, id);
		}

		// 保存修改
		DataPO po = new DataPO(hbm, vo);
		switch (hbm) {

		case "WfStartEventExecBefore":
		case "WfUserTaskExecBefore":
		case "WfStartEventExecAfter":
		case "WfUserTaskExecAfter":
			po.set("description", RequestUtils.getStringValue(request, "description"));
			po.set("execType", RequestUtils.getIntegerValue(request, "execType"));
			po.set("execScript", RequestUtils.getStringValue(request, "execScript"));
			break;

		case "WfServiceTaskLogic":
			po.set("description", RequestUtils.getStringValue(request, "description"));
			po.set("logicType", RequestUtils.getIntegerValue(request, "logicType"));
			po.set("logicScript", RequestUtils.getStringValue(request, "logicScript"));
			break;

		case "WfUserTaskAssignee":
			po.set("description", RequestUtils.getStringValue(request, "description"));
			po.set("decideType", RequestUtils.getIntegerValue(request, "decideType"));
			po.set("decideScript", RequestUtils.getStringValue(request, "decideScript"));
			po.set("uidType", RequestUtils.getIntegerValue(request, "uidType"));
			po.set("uidScript", RequestUtils.getStringValue(request, "uidScript"));
			po.set("groupType", RequestUtils.getIntegerValue(request, "groupType"));
			po.set("groupScript", RequestUtils.getStringValue(request, "groupScript"));
			po.set("roleType", RequestUtils.getIntegerValue(request, "roleType"));
			po.set("roleScript", RequestUtils.getStringValue(request, "roleScript"));
			if (RequestUtils.getIntegerValue(request, "uniqueFlag") != null) {
				po.set("uniqueFlag", RequestUtils.getIntegerValue(request, "uniqueFlag"));
			}
			break;

		case "WfStartEventColumnForm":
		case "WfUserTaskColumnForm":
		case "WfStartEventColumnLine":
		case "WfUserTaskColumnLine":
		case "WfStartEventColumnExtend":
		case "WfUserTaskColumnExtend":
			po.set("description", RequestUtils.getStringValue(request, "description"));
			po.set("name", RequestUtils.getStringValue(request, "name"));
			po.set("busiName", RequestUtils.getStringValue(request, "busiName"));
			po.set("decideType", RequestUtils.getIntegerValue(request, "decideType"));
			po.set("decideScript", RequestUtils.getStringValue(request, "decideScript"));
			po.set("editDecideType", RequestUtils.getIntegerValue(request, "editDecideType"));
			po.set("editDecideScript", RequestUtils.getStringValue(request, "editDecideScript"));
			po.set("widget", RequestUtils.getStringValue(request, "widget"));
			po.set("contentScript", RequestUtils.getStringValue(request, "contentScript"));
			po.set("contentType", RequestUtils.getIntegerValue(request, "contentType"));
			po.set("execScript", RequestUtils.getStringValue(request, "execScript"));
			po.set("execType", RequestUtils.getIntegerValue(request, "execType"));
			po.set("widgetParamType", RequestUtils.getIntegerValue(request, "widgetParamType"));
			po.set("widgetParamScript", RequestUtils.getStringValue(request, "widgetParamScript"));
			po.set("showContentType", RequestUtils.getIntegerValue(request, "showContentType"));
			po.set("showContentScript", RequestUtils.getStringValue(request, "showContentScript"));
			break;

		case "WfStartEventBtnStart":
		case "WfStartEventBtnSave":
		case "WfUserTaskBtnSave":
		case "WfUserTaskBtnForward":
		case "WfUserTaskBtn":
			po.set("description", RequestUtils.getStringValue(request, "description"));
			po.set("busiName", RequestUtils.getStringValue(request, "busiName"));
			po.set("checkType", RequestUtils.getIntegerValue(request, "checkType"));
			po.set("checkScript", RequestUtils.getStringValue(request, "checkScript"));
			break;

		default:
			break;
		}
		ORMService.getInstance().update(po.toEntity());

		String title = "";
		if (StringUtils.isNotEmpty(po.getString("busiName"))) {
			title = po.getString("busiName");
		} else {
			title = po.getString("description");
		}
		Actions.redirectInfoPage(request, response, "修改配置[" + title + "]成功.");

	}

	/**
	 * 快捷配置-查询
	 * 
	 * @param request
	 * @param response
	 */
	public void batchList(HttpServletRequest request, HttpServletResponse response) {
		String pdKey = RequestUtils.getStringValue(request, "pdKey");
		String pdName = RequestUtils.getStringValue(request, "pdName");
		String version = RequestUtils.getStringValue(request, "version");
		String flowNodeName = RequestUtils.getStringValue(request, "flowNodeName");
		String type = RequestUtils.getStringValue(request, "type");
		String script = RequestUtils.getStringValue(request, "script");
		String description = RequestUtils.getStringValue(request, "description");

		int start = Util.getStart(request);
		int limit = Util.getLimit(request);

		// 待查找的流程
		ProcessDefinitionQuery processDefinitionQuery = FlowFactory.getRepositoryService().createProcessDefinitionQuery();
		if (StringUtils.isNotEmpty(pdName)) {
			processDefinitionQuery.processDefinitionNameLike("%" + pdName + "%");
		}
		if (StringUtils.isNotEmpty(pdKey)) {
			processDefinitionQuery.processDefinitionKeyLike("%" + pdKey + "%");
		}
		if ("last".equalsIgnoreCase(version) && StringUtils.isEmpty(pdName)) {
			processDefinitionQuery.latestVersion();
		}

		List<ProcessDefinition> pds = processDefinitionQuery.list();
		if (pds.size() < 1) {
			throw new SystemRuntimeException(ExceptionType.BUSINESS, "找不到符合条件的流程.");
		}
		List<String> pdIds = new ArrayList<>();
		for (ProcessDefinition pd : pds) {
			pdIds.add(pd.getId());
		}

		// 找节点是个虐心的活
		List<String> activityIds = null;
		boolean findStartEventFlag = true;
		if (StringUtils.isNotEmpty(flowNodeName)) {
			activityIds = new ArrayList<>();
			findStartEventFlag = false;
			for (ProcessDefinition pd : pds) {
				BpmnModel bpmnModel = FlowFactory.getRepositoryService().getBpmnModel(pd.getId());
				for (FlowElement node : bpmnModel.getMainProcess().findFlowElementsOfType(NodeType.USER_TASK.getType())) {
					if (StringUtils.contains(node.getName(), flowNodeName)) {
						activityIds.add(node.getId());
					}
				}
				for (FlowElement node : bpmnModel.getMainProcess().findFlowElementsOfType(NodeType.SERVICE_TASK.getType())) {
					if (StringUtils.contains(node.getName(), flowNodeName)) {
						activityIds.add(node.getId());
					}
				}

				for (FlowElement node : bpmnModel.getMainProcess().findFlowElementsOfType(NodeType.START_EVENT.getType())) {
					if (StringUtils.contains(node.getName(), flowNodeName)) {
						findStartEventFlag = true;
						break;
					}
				}
			}
		}

		StringBuffer sql = new StringBuffer();
		// 根据类型查找配置
		switch (type) {
		case "column":
		case "column_all":
			sql.append("select * from (");

			// 任务节点
			sql.append(
					"select ID,'WfUserTaskColumnForm' as hbm,PD_ID,ACTIVITY_ID,CONTENT_SCRIPT,SHOW_CONTENT_SCRIPT,DECIDE_SCRIPT,EDIT_DECIDE_SCRIPT,'' as DESCRIPTION,NAME,BUSI_NAME from WF_USER_TASK_COLUMN_FORM");
			if (activityIds != null) {
				sql.append(" where ACTIVITY_ID in ('" + StringUtils.join(activityIds, "','") + "')");
			}
			sql.append(" union ");
			sql.append(
					"select ID,'WfUserTaskColumnLine' as hbm,PD_ID,ACTIVITY_ID,'' as CONTENT_SCRIPT,TIP_SCRIPT as SHOW_CONTENT_SCRIPT,'' as DECIDE_SCRIPT,'' as EDIT_DECIDE_SCRIPT,'' as DESCRIPTION,'' as NAME,BUSI_NAME from WF_USER_TASK_COLUMN_LINE");
			if (activityIds != null) {
				sql.append(" where ACTIVITY_ID in ('" + StringUtils.join(activityIds, "','") + "')");
			}
			sql.append(" union ");
			if ("column_all".equals(type)) {
				sql.append(
						"select a.ID,'WfUserTaskColumnExtend' as hbm,a.PD_ID,a.ACTIVITY_ID,c.CONTENT_SCRIPT as CONTENT_SCRIPT,'' as SHOW_CONTENT_SCRIPT,'' as DECIDE_SCRIPT,'' as EDIT_DECIDE_SCRIPT,a.DESCRIPTION,b.BUSI_NAME as NAME,c.BUSI_NAME as BUSI_NAME from (select * from WF_USER_TASK_COLUMN_EXTEND where SHOW_FLAG = 1) a left join VW_FLOW_BASIC_COLUMN_LINE b on a.PIXEL_KEY = b.PIXEL_KEY left join VW_FLOW_BASIC_COLUMN_SHOW c on a.PIXEL_KEY = c.PIXEL_KEY where a.SHOW_FLAG = 1");
			} else {
				sql.append(
						"select a.ID,'WfUserTaskColumnExtend' as hbm,a.PD_ID,a.ACTIVITY_ID,'' as CONTENT_SCRIPT,'' as SHOW_CONTENT_SCRIPT,'' as DECIDE_SCRIPT,'' as EDIT_DECIDE_SCRIPT,a.DESCRIPTION,'' as NAME,'' as BUSI_NAME from WF_USER_TASK_COLUMN_EXTEND a where a.SHOW_FLAG = 1");
			}
			if (activityIds != null) {
				sql.append(" and a.ACTIVITY_ID in ('" + StringUtils.join(activityIds, "','") + "')");
			}

			if (findStartEventFlag) {// 开始节点
				sql.append(" union ");
				sql.append(
						"select ID,'WfStartEventColumnForm' as hbm,PD_ID,'' as ACTIVITY_ID,CONTENT_SCRIPT,SHOW_CONTENT_SCRIPT,DECIDE_SCRIPT,EDIT_DECIDE_SCRIPT,BUSI_NAME as DESCRIPTION,NAME,BUSI_NAME from WF_START_EVENT_COLUMN_FORM");
				sql.append(" union ");
				sql.append(
						"select ID,'WfStartEventColumnLine' as hbm,PD_ID,'' as ACTIVITY_ID,'' as CONTENT_SCRIPT,TIP_SCRIPT as SHOW_CONTENT_SCRIPT,'' as DECIDE_SCRIPT,'' as EDIT_DECIDE_SCRIPT,'' as DESCRIPTION,'' as NAME,BUSI_NAME from WF_START_EVENT_COLUMN_LINE");
				sql.append(" union ");
				if ("column_all".equals(type)) {
					sql.append(
							"select a.ID,'WfStartEventColumnExtend' as hbm,a.PD_ID,'' as ACTIVITY_ID,c.CONTENT_SCRIPT as CONTENT_SCRIPT,'' as SHOW_CONTENT_SCRIPT,'' as DECIDE_SCRIPT,'' as EDIT_DECIDE_SCRIPT,a.DESCRIPTION,b.BUSI_NAME as NAME,c.BUSI_NAME as BUSI_NAME from (select * from WF_START_EVENT_COLUMN_EXTEND where SHOW_FLAG = 1) a left join VW_FLOW_BASIC_COLUMN_LINE b on a.PIXEL_KEY = b.PIXEL_KEY left join VW_FLOW_BASIC_COLUMN_SHOW c on a.PIXEL_KEY = c.PIXEL_KEY where a.SHOW_FLAG = 1");

				} else {
					sql.append(
							"select ID,'WfStartEventColumnExtend' as hbm,PD_ID,'' as ACTIVITY_ID,'' as CONTENT_SCRIPT,'' as SHOW_CONTENT_SCRIPT,'' as DECIDE_SCRIPT,'' as EDIT_DECIDE_SCRIPT,DESCRIPTION,'' as NAME,'' as BUSI_NAME from WF_START_EVENT_COLUMN_EXTEND where SHOW_FLAG = 1");
				}
			}

			sql.append(") a where a.PD_ID in ('" + StringUtils.join(pdIds, "','") + "')");
			if (StringUtils.isNotEmpty(description)) {
				for (String s : StringUtils.split(description, " ")) {
					sql.append(" and (a.DESCRIPTION like '%" + s + "%' or a.NAME like '%" + s + "%' or a.BUSI_NAME like '%" + s + "%')");
				}
			}
			if (StringUtils.isNotEmpty(script)) {
				for (String s : StringUtils.split(script, " ")) {
					sql.append(
							" and (a.CONTENT_SCRIPT like '%" + s + "%' or a.SHOW_CONTENT_SCRIPT like '%" + s + "%' or a.DECIDE_SCRIPT like '%" + s + "%' or a.EDIT_DECIDE_SCRIPT like '%" + s + "%')");
				}
			}

			break;
		case "button":
			sql.append("select * from (");

			// WF_USER_TASK_BTN_SAVE WfUserTaskBtnSave
			// WF_USER_TASK_BTN_FORWARD WfUserTaskBtnForward
			// WF_USER_TASK_BTN WfUserTaskBtn

			// WF_START_EVENT_BTN_SAVE WfStartEventBtnSave
			// WF_START_EVENT_BTN_START WfStartEventBtnStart

			// 任务节点
			sql.append("select 'WfUserTaskBtn' as hbm,PD_ID,ACTIVITY_ID,FLOW_ID,CHECK_SCRIPT,DESCRIPTION,BUSI_NAME from WF_USER_TASK_BTN");
			if (activityIds != null) {
				sql.append(" where ACTIVITY_ID in ('" + StringUtils.join(activityIds, "','") + "')");
			}
			sql.append(" union ");
			sql.append("select 'WfUserTaskBtnForward' as hbm,PD_ID,ACTIVITY_ID,'' as FLOW_ID,CHECK_SCRIPT,DESCRIPTION,BUSI_NAME from WF_USER_TASK_BTN_FORWARD");
			if (activityIds != null) {
				sql.append(" where ACTIVITY_ID in ('" + StringUtils.join(activityIds, "','") + "')");
			}

			sql.append(" union ");
			sql.append("select 'WfUserTaskBtnSave' as hbm,PD_ID,ACTIVITY_ID,'' as FLOW_ID,CHECK_SCRIPT,DESCRIPTION,BUSI_NAME from WF_USER_TASK_BTN_SAVE");
			if (activityIds != null) {
				sql.append(" where ACTIVITY_ID in ('" + StringUtils.join(activityIds, "','") + "')");
			}

			if (findStartEventFlag) {// 开始节点
				sql.append(" union ");
				sql.append("select 'WfStartEventBtnSave' as hbm,PD_ID,'' as ACTIVITY_ID,'' as FLOW_ID,CHECK_SCRIPT,DESCRIPTION,BUSI_NAME from WF_START_EVENT_BTN_SAVE");
				sql.append(" union ");
				sql.append("select 'WfStartEventBtnStart' as hbm,PD_ID,'' as ACTIVITY_ID,'' as FLOW_ID,CHECK_SCRIPT,DESCRIPTION,BUSI_NAME from WF_START_EVENT_BTN_START");
			}

			sql.append(") a where a.PD_ID in ('" + StringUtils.join(pdIds, "','") + "')");
			if (StringUtils.isNotEmpty(description)) {
				for (String s : StringUtils.split(description, " ")) {
					sql.append(" and (a.DESCRIPTION like '%" + s + "%' or a.BUSI_NAME like '%" + s + "%')");
				}
			}
			if (StringUtils.isNotEmpty(script)) {
				for (String s : StringUtils.split(script, " ")) {
					sql.append(" and a.CHECK_SCRIPT like '%" + s + "%'");
				}
			}

			break;
		case "exec":
			sql.append("select * from (");

			// 任务节点
			sql.append("select ID,'WfUserTaskExecBefore' as hbm,PD_ID,ACTIVITY_ID,FLOW_ID,EXEC_SCRIPT,DESCRIPTION from WF_USER_TASK_EXEC_BEFORE");
			if (activityIds != null) {
				sql.append(" where ACTIVITY_ID in ('" + StringUtils.join(activityIds, "','") + "')");
			}
			sql.append(" union ");
			sql.append("select ID,'WfUserTaskExecAfter' as hbm,PD_ID,ACTIVITY_ID,FLOW_ID,EXEC_SCRIPT,DESCRIPTION from WF_USER_TASK_EXEC_AFTER");
			if (activityIds != null) {
				sql.append(" where ACTIVITY_ID in ('" + StringUtils.join(activityIds, "','") + "')");
			}

			sql.append(" union ");
			sql.append("select ID,'WfServiceTaskLogic' as hbm,PD_ID,ACTIVITY_ID,'' as FLOW_ID,LOGIC_SCRIPT as EXEC_SCRIPT,DESCRIPTION from WF_SERVICE_TASK_LOGIC");
			if (activityIds != null) {
				sql.append(" where ACTIVITY_ID in ('" + StringUtils.join(activityIds, "','") + "')");
			}

			if (findStartEventFlag) {// 开始节点
				sql.append(" union ");
				sql.append("select ID,'WfStartEventExecBefore' as hbm,PD_ID,'' as ACTIVITY_ID,'' as FLOW_ID,EXEC_SCRIPT,DESCRIPTION from WF_START_EVENT_EXEC_BEFORE");
				sql.append(" union ");
				sql.append("select ID,'WfStartEventExecAfter' as hbm,PD_ID,'' as ACTIVITY_ID,'' as FLOW_ID,EXEC_SCRIPT,DESCRIPTION from WF_START_EVENT_EXEC_AFTER");
			}

			sql.append(") a where a.PD_ID in ('" + StringUtils.join(pdIds, "','") + "')");
			if (StringUtils.isNotEmpty(description)) {
				for (String s : StringUtils.split(description, " ")) {
					sql.append(" and a.DESCRIPTION like '%" + s + "%'");
				}
			}
			if (StringUtils.isNotEmpty(script)) {
				for (String s : StringUtils.split(script, " ")) {
					sql.append(" and a.EXEC_SCRIPT like '%" + s + "%'");
				}
			}

			break;
		case "person":
			// WF_USER_TASK_ASSIGNEE
			sql.append("select ID,'WfUserTaskAssignee' as hbm,PD_ID,ACTIVITY_ID,'' as FLOW_ID,DESCRIPTION,ALLOCATE_TYPE,UNIQUE_FLAG from WF_USER_TASK_ASSIGNEE");
			sql.append(" where PD_ID in ('" + StringUtils.join(pdIds, "','") + "')");

			if (activityIds != null) {
				sql.append(" and ACTIVITY_ID in ('" + StringUtils.join(activityIds, "','") + "')");
			}
			if (StringUtils.isNotEmpty(description)) {
				for (String s : StringUtils.split(description, " ")) {
					sql.append(" and DESCRIPTION like '%" + s + "%'");
				}
			}
			if (StringUtils.isNotEmpty(script)) {
				for (String s : StringUtils.split(script, " ")) {
					sql.append(" and (UID_SCRIPT like '%" + s + "%' or ROLE_SCRIPT like '%" + s + "%' or GROUP_SCRIPT like '%" + s + "%')");
				}
			}
			break;
		default:
			break;
		}

		DataPackage dp = new DataPackage();
		dp.setLimit(limit);
		dp.setStart(start);
		dp.setTotalRecord(JdbcService.getInstance().getSQLCount(sql.toString()));
		List<Map<String, Object>> list = JdbcService.getInstance().querySQLPage(sql.toString(), start, limit);
		for (Map<String, Object> vo : list) {
			String pdId = (String) vo.get("PD_ID");
			vo.put("pd", FlowFactory.getRepositoryService().getProcessDefinition(pdId));
			String activityId = (String) vo.get("ACTIVITY_ID");
			BpmnModel bpmnModel = FlowFactory.getRepositoryService().getBpmnModel(pdId);
			Node node;
			if (StringUtils.isNotEmpty(activityId)) {
				node = BpmnHelper.getNode(bpmnModel, activityId);
			} else {
				node = BpmnHelper.getNode(bpmnModel, NodeType.START_EVENT);
			}
			vo.put("node", node);
			String flowId = (String) vo.get("FLOW_ID");
			if (node != null && StringUtils.isNotEmpty(flowId)) {
				for (SequenceFlow f : node.getSequenceFlows()) {
					if (StringUtils.equalsIgnoreCase(f.getId(), flowId)) {
						vo.put("sequenceFlow", f);
						break;
					}
				}
			}
		}
		dp.setList(list);
		request.setAttribute("dp", dp);

		Actions.includePage(request, response, Util.getPagePath(request, "batch_list.jsp"));
	}

	/**
	 * 模型树
	 * 
	 * @param request
	 * @param response
	 */
	public void tree(HttpServletRequest request, HttpServletResponse response) {
		RepositoryService service = FlowFactory.getRepositoryService();
		String cp = Actions.Util.getContextPath(request);

		List<Object> list = new ArrayList<>();
		List<Map<String, Object>> categorys = JdbcService.getInstance().querySQL("select distinct CATEGORY_ as category from ACT_RE_PROCDEF order by CATEGORY_ asc");
		if (categorys != null) {
			for (Map<String, Object> o : categorys) {
				o.put("name", o.get("category"));
				o.put("title", o.get("category"));
				o.put("id", o.get("category"));
				o.put("parentId", null);
				o.put("icon", cp + "/css/icon/folder_image.png");
				o.put("isPd", 0);
				list.add(o);
			}
		}

		// 获取流程定义的所有KEY
		List<Map<String, Object>> pdKeys = JdbcService.getInstance().querySQL("select distinct KEY_ pdKey ,CATEGORY_ category from  ACT_RE_PROCDEF order by category asc");
		if (pdKeys != null) {
			for (Map<String, Object> pdKey : pdKeys) {
				String key = (String) pdKey.get("pdKey");
				String category = (String) pdKey.get("category");
				List<ProcessDefinition> pds = service.createProcessDefinitionQuery().processDefinitionKey(key).processDefinitionCategory(category).orderByProcessDefinitionVersion().asc().list();
				{
					// 设置流程定义
					Map<String, Object> o = new HashMap<>();
					ProcessDefinition pd = service.createProcessDefinitionQuery().processDefinitionKey(key).latestVersion().singleResult();
					o.put("name", pd.getName());
					o.put("key", key);
					o.put("title", key);
					o.put("parentId", category);
					o.put("id", category + "-" + key);
					o.put("icon", cp + "/css/icon/group_go.png");
					o.put("isPd", 1);
					list.add(o);
				}

				for (ProcessDefinition pd : pds) {
					Map<String, Object> o = new HashMap<>();
					o.put("name", "版本" + pd.getVersion() + ":" + (pd.getName() != null ? pd.getName() : "(无命名)"));
					o.put("parentId", category + "-" + key);
					o.put("pdId", pd.getId());
					o.put("id", pd.getId());
					o.put("title", pd.getKey());
					if (pd.isSuspended()) {
						o.put("icon", cp + "/css/icon/stop_red.png");
					} else {
						o.put("icon", cp + "/css/icon/play_green.png");
					}
					o.put("suspended", pd.isSuspended());
					o.put("isPd", 2);
					list.add(o);
				}
			}
		}

		request.setAttribute("list", JsonMapper.defaultMapper().toJson(list));
		Actions.includePage(request, response, Util.getPagePath(request, "tree.jsp"));
	}

	/**
	 * 删除流程
	 * 
	 * @param request
	 * @param response
	 */
	@ActionAccess(level = SafeLevel.DEV_W)
	public void remove(HttpServletRequest request, HttpServletResponse response) {
		String id = RequestUtils.getStringValue(request, "id");
		BeanFactory.getInstance().getBean(FlowManagerService.class).executeRemovePd(id);
		Actions.redirectInfoPage(request, response, "删除流程[" + id + "]成功.");
	}

	/**
	 * 挂起/激活流程
	 * 
	 * @param request
	 * @param response
	 */
	@ActionAccess(level = SafeLevel.DEV_W)
	public void suspend(HttpServletRequest request, HttpServletResponse response) {
		String id = RequestUtils.getStringValue(request, "id");
		int flag = RequestUtils.getIntegerValue(request, "flag");
		RepositoryService service = FlowFactory.getRepositoryService();
		if (flag == 1) {
			service.suspendProcessDefinitionById(id, true, null);
		} else {
			service.activateProcessDefinitionById(id, true, null);
		}
		Actions.redirectInfoPage(request, response, "流程[" + id + "]已" + (flag == 1 ? "挂起" : "激活") + ".");
	}

	/**
	 * 保存流程备注
	 * 
	 * @param request
	 * @param response
	 */
	public void submitPdConfig(HttpServletRequest request, HttpServletResponse response) {
		String pdId = RequestUtils.getStringValue(request, "pdId");
		String description = RequestUtils.getStringValue(request, "description");
		WfPd po = (WfPd) ORMService.getInstance().findByPk(WfPd.class.getName(), pdId);
		if (po == null) {
			throw new SystemRuntimeException(ExceptionType.BUSINESS, "流程配置不存在.");
		}
		po.setDescription(description);
		ORMService.getInstance().updatePO(po);
		Actions.redirectInfoPage(request, response, "保存成功.");
	}

	/**
	 * 锁定表
	 * 
	 * @param request
	 * @param response
	 */
	@ActionAccess(level = SafeLevel.DEV_W)
	public void lockTable(HttpServletRequest request, HttpServletResponse response) {
		String name = RequestUtils.getStringValue(request, "name");
		String pdId = RequestUtils.getStringValue(request, "pdId");
		String tableType = RequestUtils.getStringValue(request, "tableType");// 表类型
		WfPd po = (WfPd) ORMService.getInstance().findByPk(WfPd.class.getName(), pdId);
		if (po == null) {
			throw new SystemRuntimeException(ExceptionType.BUSINESS, "流程配置不存在.");
		}

		Map<String, Object> basicView = (Map<String, Object>) ORMService.getInstance().findByPk("VwFlowBasic", po.getBasicViewKey());
		basicView.put(tableType, name);
		ORMService.getInstance().update(basicView);

		Actions.redirectInfoPage(request, response, "绑定表[" + name + "]成功.");
	}

	/**
	 * 接触绑定表
	 * 
	 * @param request
	 * @param response
	 */
	@ActionAccess(level = SafeLevel.DEV_W)
	public void unlockTable(HttpServletRequest request, HttpServletResponse response) {
		String pdId = RequestUtils.getStringValue(request, "pdId");
		String tableType = RequestUtils.getStringValue(request, "tableType");// 表类型
		WfPd po = (WfPd) ORMService.getInstance().findByPk(WfPd.class.getName(), pdId);
		if (po == null) {
			throw new SystemRuntimeException(ExceptionType.BUSINESS, "流程配置不存在.");
		}

		Map<String, Object> basicView = (Map<String, Object>) ORMService.getInstance().findByPk("VwFlowBasic", po.getBasicViewKey());
		basicView.put(tableType, null);
		ORMService.getInstance().update(basicView);

		Actions.redirectInfoPage(request, response, "解除绑定成功.");
	}

	/**
	 * 展示流程图
	 * 
	 * @param request
	 * @param response
	 * @throws XMLStreamException
	 */
	public void picture(HttpServletRequest request, HttpServletResponse response) {
		String id = RequestUtils.getStringValue(request, "id");
		RepositoryService service = FlowFactory.getRepositoryService();
		ProcessDefinition pd = service.createProcessDefinitionQuery().processDefinitionId(id).singleResult();

		InputStream is;
		// 先从extra里面获取
		byte[] imgBytes = service.getModelEditorSourceExtra(pd.getDeploymentId());
		if (imgBytes != null) {
			is = new ByteArrayInputStream(imgBytes);
		} else if (StringUtils.isNotEmpty(pd.getDiagramResourceName())) {
			is = service.getResourceAsStream(pd.getDeploymentId(), pd.getDiagramResourceName());
		} else {
			BpmnModel bpmnModel = service.getBpmnModel(id);
			ProcessDiagramGenerator generator = new DefaultProcessDiagramGenerator();
			ProcessEngineConfiguration processEngineConfiguration = FlowFactory.getEngine().getProcessEngineConfiguration();
			is = generator.generateDiagram(bpmnModel, "png", processEngineConfiguration.getActivityFontName(), processEngineConfiguration.getLabelFontName(), null, 1.0);
		}

		Actions.download(request, response, pd.getDiagramResourceName(), is);
	}

	/**
	 * 配置
	 * 
	 * @param request
	 * @param response
	 */
	public void config(HttpServletRequest request, HttpServletResponse response) {
		String id = RequestUtils.getStringValue(request, "id");
		RepositoryService service = FlowFactory.getRepositoryService();
		ProcessDefinition pd = service.createProcessDefinitionQuery().processDefinitionId(id).singleResult();
		request.setAttribute("pd", pd);
		Deployment dm = service.createDeploymentQuery().deploymentId(pd.getDeploymentId()).singleResult();
		request.setAttribute("dm", dm);

		WfPd config = FlowService.getInstance().findPdConfig(id);
		if (config == null) {
			throw new SystemRuntimeException(ExceptionType.BUSINESS, "流程配置已被删除,无法进行配置.请删除当前流程版本并使用流程图重新部署.");
		}
		request.setAttribute("config", config);

		Map<String, Object> viewVO = (Map<String, Object>) ORMService.getInstance().findByPk("VwFlowBasic", config.getBasicViewKey());
		request.setAttribute("view", viewVO);

		// 可选历史表和审批意见表
		List<TbTable> historyTables = new ArrayList<>();
		List<TbTable> opinionTables = new ArrayList<>();
		List<TbTable> sysTables = (List<TbTable>) ORMService.getInstance().queryAll(TbTable.class.getName());
		for (TbTable model : sysTables) {
			if (ModelKeyUtils.checkModel(OrderHistoryModelKeys.class, model)) {
				historyTables.add(model);
			}
			if (ModelKeyUtils.checkModel(OrderOpinionModelKeys.class, model)) {
				opinionTables.add(model);
			}
		}
		request.setAttribute("historyTables", historyTables);
		request.setAttribute("opinionTables", opinionTables);

		// 获取坐标
		Map<String, GraphicInfo> gs = new HashMap<>();
		BpmnModel bpmnModel = service.getBpmnModel(id);
		for (FlowNode node : bpmnModel.getMainProcess().findFlowElementsOfType(FlowNode.class)) {
			GraphicInfo graphicInfo = bpmnModel.getGraphicInfo(node.getId());
			gs.put(node.getId(), graphicInfo);
		}
		request.setAttribute("baseG", BpmnHelper.getCoordinateInfo(bpmnModel));// 基础坐标
		request.setAttribute("gs", gs);

		Actions.includePage(request, response, Util.getPagePath(request, "config.jsp"));
	}

	/**
	 * 修改类别
	 * 
	 * @param request
	 * @param response
	 */
	public void editCategory(HttpServletRequest request, HttpServletResponse response) {
		String pdKey = RequestUtils.getStringValue(request, "pdKey");

		ProcessDefinition pd = FlowFactory.getRepositoryService().createProcessDefinitionQuery().processDefinitionKey(pdKey).latestVersion().singleResult();
		request.setAttribute("pd", pd);

		List<ProcessDefinition> pds = FlowFactory.getRepositoryService().createProcessDefinitionQuery().processDefinitionKey(pdKey).orderByProcessDefinitionVersion().desc().list();
		request.setAttribute("pds", pds);

		Actions.includePage(request, response, Util.getPagePath(request, "category.jsp"));
	}

	/**
	 * 提交修改类别
	 * 
	 * @param request
	 * @param response
	 */
	@ActionAccess(level = SafeLevel.DEV_W)
	public void submitCategory(HttpServletRequest request, HttpServletResponse response) {
		String pdKey = RequestUtils.getStringValue(request, "pdKey");
		String category = RequestUtils.getStringValue(request, "category");
		BeanFactory.getInstance().getBean(FlowManagerService.class).executeUpdateCategory(pdKey, category);
		Actions.redirectInfoPage(request, response, "修改成功.");
	}

	/**
	 * 展示节点明细
	 * 
	 * @param request
	 * @param response
	 */
	public void detail(HttpServletRequest request, HttpServletResponse response) {
		String pdId = RequestUtils.getStringValue(request, "pdId");
		String activityId = RequestUtils.getStringValue(request, "activityId");
		RepositoryService service = FlowFactory.getRepositoryService();
		BpmnModel bpmnModel = service.getBpmnModel(pdId);
		BpmnHelper.Node node = BpmnHelper.getNode(bpmnModel, activityId);

		// 校验是否需要特殊配置
		Class<?> configClass = ActivityConfigScanner.findClass(node.getNodeType());
		if (configClass != null) {
			String action = Actions.Util.getActionUrl(configClass);
			Actions.redirectAction(request, response, action + "/index.shtml?pdId=" + pdId + "&activityId=" + activityId);
		} else {
			request.setAttribute("node", node);
			Actions.includePage(request, response, Util.getPagePath(request, "detail.jsp"));
		}
	}

	/**
	 * 直接编辑动态表
	 * 
	 * @param request
	 * @param response
	 */
	public void tableZone(HttpServletRequest request, HttpServletResponse response) {
		String tableName = RequestUtils.getStringValue(request, "tableName");
		request.setAttribute("tableName", tableName);
		request.setAttribute("tableConfigAction", "/development/table/TableAction/editZone.shtml");
		Actions.includePage(request, response, Actions.Util.getPagePath(request, "table_form.jsp"));
	}

	/**
	 * 直接编辑视图
	 * 
	 * @param request
	 * @param response
	 */
	public void viewZone(HttpServletRequest request, HttpServletResponse response) {
		String viewKey = RequestUtils.getStringValue(request, "viewKey");
		VwUrl vo = (VwUrl) ORMService.getInstance().findByPk(VwUrl.class.getName(), viewKey);
		if (vo == null) {
			throw new SystemRuntimeException(ExceptionType.BUSINESS, "视图不存在,无法编辑.");
		}

		request.setAttribute("viewConfigAction", "/development/view/ViewConfigAction");
		request.setAttribute("vo", vo);
		String action = Actions.Util.getActionUrl(ViewActionBuilder.getInstance().getViewClass(vo.getViewClass()));
		request.setAttribute("action", action + "/configForm.shtml");
		request.setAttribute("module", ViewActionBuilder.getInstance().getViewModule(vo.getViewClass()));
		Actions.includePage(request, response, Actions.Util.getPagePath(request, "view_form.jsp"));
	}

}
