/*
 * $HeadURL: $
 * $Id: $
 * Copyright (c) 2013 by Riversoft System, all rights reserved.
 */
package com.riversoft.flow;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.activiti.bpmn.model.BpmnModel;
import org.activiti.bpmn.model.SequenceFlow;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.Execution;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.IdentityLink;
import org.activiti.engine.task.Task;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import com.riversoft.core.IDGenerator;
import com.riversoft.core.context.RequestContext;
import com.riversoft.core.db.DataCondition;
import com.riversoft.core.db.DataPO;
import com.riversoft.core.db.ORMService;
import com.riversoft.core.exception.ExceptionType;
import com.riversoft.core.exception.SystemRuntimeException;
import com.riversoft.core.script.annotation.ScriptSupport;
import com.riversoft.core.web.Actions.Keys;
import com.riversoft.core.web.FreeMarkerUtils;
import com.riversoft.flow.BpmnHelper.Node;
import com.riversoft.flow.key.NodeType;
import com.riversoft.flow.key.OrderHistoryModelKeys;
import com.riversoft.flow.key.OrderModelKeys;
import com.riversoft.flow.key.OrderOpinionModelKeys;
import com.riversoft.flow.key.VariableKeys;
import com.riversoft.platform.SessionManager;
import com.riversoft.platform.db.ORMAdapterService;
import com.riversoft.platform.po.UsGroup;
import com.riversoft.platform.po.UsRole;
import com.riversoft.platform.po.UsUser;
import com.riversoft.platform.po.VwUrl;
import com.riversoft.platform.po.WfPd;

import freemarker.template.TemplateMethodModelEx;
import freemarker.template.TemplateModelException;

/**
 * 工作流脚本函数<br>
 * 
 * @author woden
 * 
 */
@ScriptSupport("flow")
public class FlowHelper {

	/**
	 * 将订单实体转换成FO(流程实体)
	 * 
	 * @param po
	 * @return
	 */
	public static FlowObject fo(Map<String, Object> po) {
		if (po == null) {
			throw new SystemRuntimeException(ExceptionType.FLOW_ORD_NOT_FOUND);
		}

		FlowObject fo = new FlowObject();
		fo.setOrdId((String) po.get(OrderModelKeys.ORD_ID.getColumn().getName()));
		// 流程定义部分
		ProcessDefinition pd = FlowService.getInstance()
				.getPd((String) po.get(OrderModelKeys.PD_ID.getColumn().getName()));
		fo.setPdKey(pd.getKey());
		fo.setPdId(pd.getId());
		fo.setPdName(pd.getName());

		// 流程实例部分
		List<ProcessInstance> pis = FlowFactory.getRuntimeService().createProcessInstanceQuery()
				.processDefinitionId(pd.getId()).processInstanceBusinessKey(fo.getOrdId()).list();
		if (pis != null && pis.size() > 0) {
			Execution pi = pis.get(0);
			fo.setActivityId(pi.getActivityId());
			// 这里太重了,后续思考有没更好方式
			BpmnModel bpmnModel = FlowFactory.getRepositoryService().getBpmnModel(pd.getId());
			Node activityNode = BpmnHelper.getNode(bpmnModel, pi.getActivityId());
			fo.setActivityName(activityNode.getName());
		}
		return fo;
	}

	/**
	 * 校验当前用户是否允许处理订单
	 * 
	 * @param task
	 * @return
	 */
	public static boolean checkTask(Task task) {
		return FlowService.getInstance().checkTask(task);
	}

	/**
	 * 校验当前用户是否允许处理订单
	 * 
	 * @param po
	 * @return
	 */
	public static boolean checkTask(Map<String, Object> po) {
		return FlowService.getInstance().checkTask(po);
	}

	/**
	 * 校验当前用户是否允许处理订单
	 * 
	 * @param po
	 * @param index
	 * @return
	 */
	public static boolean checkTask(Map<String, Object> po, int index) {
		return FlowService.getInstance().checkTask(po, index);
	}

	/**
	 * 校验当前用户是否允许删除
	 * 
	 * @param po
	 * @return
	 */
	public static boolean checkRemove(Map<String, Object> po) {
		Integer ordState = (Integer) po.get(OrderModelKeys.ORD_STATE.getColumn().getName());
		if (ordState != 0) {// 只有未启动可以删除
			return false;
		}

		String owner = (String) po.get(OrderModelKeys.OWNER.getColumn().getName());
		if (!owner.equals(SessionManager.getUser().getUid())) {// 创建者是本人才允许删除
			return false;
		}

		return true;
	}

	/**
	 * 校验当前用户是否允许查看
	 * 
	 * @param po
	 * @return
	 */
	public static boolean checkShow(Map<String, Object> po) {
		String owner = (String) po.get(OrderModelKeys.OWNER.getColumn().getName());
		if (owner.equals(SessionManager.getUser().getUid())) {// 创建者是本人,可查看
			return true;
		}

		String uids = (String) po.get(OrderModelKeys.UIDS.getColumn().getName());
		if (StringUtils.isNotEmpty(uids)) {
			if (ArrayUtils.contains(StringUtils.split(uids, "~"), SessionManager.getUser().getUid())) {// 已办
				return true;
			}
		}

		if (checkTask(po)) {// 是否待办
			return true;
		}

		return false;
	}

	/**
	 * 校验当前用户是否拥有查看某个节点的权限
	 * 
	 * @param po
	 * @param activityId
	 * @return
	 */
	public static boolean checkActivityShow(Map<String, Object> po, String activityId) {

		if (po == null) {
			return false;
		}

		WfPd wfPd = (WfPd) ORMService.getInstance().findByPk(WfPd.class.getName(),
				(String) po.get(OrderModelKeys.PD_ID.getColumn().getName()));
		if (wfPd == null) {
			return false;
		}

		// 未发起的情况
		if ((Integer) po.get(OrderModelKeys.ORD_STATE.name()) == 0) {// 未开始
			String owner = (String) po.get(OrderModelKeys.OWNER.getColumn().getName());
			if (owner.equals(SessionManager.getUser().getUid())) {
				return true;
			} else {
				return false;
			}
		}

		// 已发起
		if (StringUtils.isNotEmpty(activityId)) {
			List<ProcessInstance> piList = FlowFactory.getRuntimeService().createProcessInstanceQuery()
					.processInstanceBusinessKey((String) po.get(OrderModelKeys.ORD_ID.name()))
					.processDefinitionId((String) po.get(OrderModelKeys.PD_ID.name())).orderByProcessInstanceId().desc()
					.list();
			if (piList != null && piList.size() > 0) {
				// 获取当前流程信息
				List<Execution> executions = FlowFactory.getRuntimeService().createExecutionQuery()
						.processInstanceId(piList.get(0).getId()).activityId(activityId).list();
				if (executions.size() > 0) {
					// 判断当前节点
					for (Execution execution : executions) {
						List<Task> tasks = FlowFactory.getTaskService().createTaskQuery().executionId(execution.getId())
								.list();
						for (Task task : tasks) {
							if (checkTask(task)) {
								return true;
							}
						}
					}
				}
			}

			// 获取历史表
			Map<String, Object> basicView = (Map<String, Object>) ORMService.getInstance().findByPk("VwFlowBasic",
					wfPd.getBasicViewKey());
			if (basicView == null) {
				return false;
			}
			String historyTableName = (String) basicView.get("historyTableName");
			if (StringUtils.isEmpty(historyTableName)) {
				return false;
			}

			// 查找是否存在当前用户处理过的历史节点
			List<?> list = ORMAdapterService.getInstance().query(historyTableName,
					new DataCondition()
							.setStringEqual(OrderHistoryModelKeys.ACTIVITY_ID.getColumn().getName(), activityId)
							.setStringEqual(OrderHistoryModelKeys.TASK_UID.getColumn().getName(),
									SessionManager.getUser().getUid())
							.toEntity());
			if (list.size() > 0) {
				return true;
			}
		}

		return false;
	}

	/**
	 * 展示当前节点处理人
	 * 
	 * @param po
	 * @return
	 */
	public static String showAssignee(Map<String, Object> po) {
		String ordId = (String) po.get(OrderModelKeys.ORD_ID.getColumn().getName());
		String pdId = (String) po.get(OrderModelKeys.PD_ID.getColumn().getName());
		List<Task> tasks = FlowFactory.getTaskService().createTaskQuery().processDefinitionId(pdId)
				.processInstanceBusinessKey(ordId).orderByTaskCreateTime().asc().list();
		if (tasks.size() < 1) {
			return "";
		} else if (tasks.size() == 1) {
			return showAssignee(tasks.get(0));
		}

		List<String> list = new ArrayList<>();
		for (Task task : tasks) {
			list.add(task.getName() + ":" + showAssignee(task));
		}
		return StringUtils.join(list, "<br />");
	}

	/**
	 * 展示当前节点处理人
	 * 
	 * @param task
	 * @return
	 */
	public static String showAssignee(Task task) {
		String onlyUser = null;
		List<Map<String, Object>> list = new ArrayList<>();
		if (StringUtils.isNotEmpty(task.getAssignee())) {// 独享处理人的情况
			String uid = task.getAssignee();
			onlyUser = ((UsUser) ORMService.getInstance().findByPk(UsUser.class.getName(), uid)).getBusiName();
		} else {
			// 共享处理人
			List<IdentityLink> indentityList = FlowFactory.getTaskService().getIdentityLinksForTask(task.getId());
			if (indentityList != null && indentityList.size() > 0) {
				for (IdentityLink o : indentityList) {
					Map<String, Object> vo = new HashMap<>();

					if (StringUtils.isNotEmpty(o.getUserId())) {// 用户
						String uid = o.getUserId();
						UsUser user = (UsUser) ORMService.getInstance().findByPk(UsUser.class.getName(), uid);
						vo.put("user", (user != null ? user.getBusiName() : uid));
					} else {// 组织
						String str = o.getGroupId();
						if (str.startsWith(";")) {// 角色
							String roleKey = str.substring(1);
							UsRole role = (UsRole) ORMService.getInstance().findByPk(UsRole.class.getName(), roleKey);
							vo.put("role", (role != null ? role.getBusiName() : roleKey));
						} else if (str.endsWith(";")) {// 组织
							String groupKey = str.substring(0, str.length() - 1);
							UsGroup group = (UsGroup) ORMService.getInstance().findByPk(UsGroup.class.getName(),
									groupKey);
							vo.put("group", (group != null ? group.getBusiName() : groupKey));
						} else {// 组织+角色
							String[] strs = str.split(";");
							String groupKey = strs[0];
							String roleKey = strs[1];
							UsGroup group = (UsGroup) ORMService.getInstance().findByPk(UsGroup.class.getName(),
									groupKey);
							UsRole role = (UsRole) ORMService.getInstance().findByPk(UsRole.class.getName(), roleKey);
							vo.put("role", (role != null ? role.getBusiName() : roleKey));
							vo.put("group", (group != null ? group.getBusiName() : groupKey));
						}
					}
					list.add(vo);
				}
			}
		}

		Map<String, Object> model = new HashMap<>();
		model.put("list", list);
		model.put("user", onlyUser);

		String actionMode = RequestContext.getCurrent().getString(Keys.ACTION_MODE.toString());
		return FreeMarkerUtils.process("classpath:ftl/" + actionMode + "/showAssignee.ftl", model);
	}

	/**
	 * 展示当前节点
	 * 
	 * @param po
	 * @return
	 */
	public static String showActivity(Map<String, Object> po) {
		FlowObject fo = fo(po);
		String text;
		String color;
		switch ((int) po.get(OrderModelKeys.ORD_STATE.name())) {
		case 2:
			text = "已结束";
			color = "green";
			break;
		case 0:
			text = "未开始";
			color = "gray";
			break;
		case 3:
			text = "已取消";
			color = "red";
			break;
		default:
			text = fo.getActivityName();
			color = "blue";
			break;
		}

		WfPd config = FlowService.getInstance().findPdConfig(fo.getPdId());
		VwUrl basicUrl = (VwUrl) ORMService.getInstance().findByPk(VwUrl.class.getName(), config.getBasicViewKey());
		if (basicUrl == null) {
			throw new SystemRuntimeException(ExceptionType.BUSINESS, "流程不存在.");
		}

		Map<String, Object> model = new HashMap<>();
		model.put("uuid", IDGenerator.uuid());
		model.put("value", text);
		model.put("url", basicUrl.getUrl());
		model.put("color", color);
		model.put("ordId", fo.getOrdId());
		model.put("vo", po);
		model.put("translateUser", new TemplateMethodModelEx() {
			@SuppressWarnings("rawtypes")
			@Override
			public Object exec(List arguments) throws TemplateModelException {
				String uid = arguments.get(0).toString();
				UsUser user = (UsUser) ORMService.getInstance().findByPk(UsUser.class.getName(), uid);
				if (user == null) {
					return "";
				}

				return user.getBusiName();
			}
		});

		String actionMode = RequestContext.getCurrent().getString(Keys.ACTION_MODE.toString());
		return FreeMarkerUtils.process("classpath:ftl/" + actionMode + "/showActivity.ftl", model);
	}

	/**
	 * 展示当前节点
	 * 
	 * @param task
	 * @return
	 */
	public static String showActivity(Task task) {
		String pdId = task.getProcessDefinitionId();
		WfPd config = FlowService.getInstance().findPdConfig(pdId);
		VwUrl basicUrl = (VwUrl) ORMService.getInstance().findByPk(VwUrl.class.getName(), config.getBasicViewKey());
		if (basicUrl == null) {
			throw new SystemRuntimeException(ExceptionType.BUSINESS, "流程不存在.");
		}

		Map<String, Object> model = new HashMap<>();
		model.put("uuid", IDGenerator.uuid());
		model.put("value", task.getName());
		model.put("url", basicUrl.getUrl());
		model.put("color", "blue");
		model.put("taskId", task.getId());
		model.put("vo", getOrder(task));
		model.put("translateUser", new TemplateMethodModelEx() {
			@SuppressWarnings("rawtypes")
			@Override
			public Object exec(List arguments) throws TemplateModelException {
				String uid = arguments.get(0).toString();
				UsUser user = (UsUser) ORMService.getInstance().findByPk(UsUser.class.getName(), uid);
				if (user == null) {
					return "";
				}

				return user.getBusiName();
			}
		});

		String actionMode = RequestContext.getCurrent().getString(Keys.ACTION_MODE.toString());
		return FreeMarkerUtils.process("classpath:ftl/" + actionMode + "/showActivity.ftl", model);
	}

	/**
	 * 展示审批意见
	 * 
	 * @param po
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static String showOpinion(Map<String, Object> po) {
		FlowObject fo = fo(po);
		WfPd config = FlowService.getInstance().findPdConfig(fo.getPdId());
		Map<String, Object> basicView = (Map<String, Object>) ORMService.getInstance().findByPk("VwFlowBasic",
				config.getBasicViewKey());
		if (basicView == null) {
			throw new SystemRuntimeException(ExceptionType.BUSINESS, "流程不存在.");
		}

		String opinionTableName = (String) basicView.get("opinionTableName");
		if (StringUtils.isEmpty(opinionTableName)) {
			throw new SystemRuntimeException(ExceptionType.BUSINESS, "该流程没有对应的审批意见表.");
		}

		List<Map<String, Object>> list = ORMAdapterService.getInstance().query(opinionTableName,
				new DataCondition().setStringEqual(OrderOpinionModelKeys.ORD_ID.name(), fo.getOrdId())
						.setOrderByAsc(OrderOpinionModelKeys.CREATE_DATE.name()).toEntity());
		Map<String, Object> model = new HashMap<>();
		model.put("list", list);
		model.put("showUser", new TemplateMethodModelEx() {
			@SuppressWarnings("rawtypes")
			@Override
			public Object exec(List arguments) throws TemplateModelException {

				String uid = arguments.get(0).toString();
				String groupKey = arguments.get(1).toString();
				String roleKey = arguments.get(2).toString();

				// user
				UsUser user = (UsUser) ORMService.getInstance().findByPk(UsUser.class.getName(), uid);
				if (user == null) {
					return uid;
				}

				StringBuffer html = new StringBuffer();
				html.append("<span tip=\"true\" selector=\".showuser\"><font color=\"blue\" class=\"showuser\">");
				// group
				UsGroup group = (UsGroup) ORMService.getInstance().findByPk(UsGroup.class.getName(), groupKey);
				List<String> groupNames = new ArrayList<String>();
				for (int i = 0; i < 3; i++) {// 最多循环三层
					if (group == null) {
						break;
					}
					groupNames.add(group.getBusiName());
					if (StringUtils.isEmpty(group.getParentKey())) {
						break;
					}
					group = (UsGroup) ORMService.getInstance().findByPk(UsGroup.class.getName(), group.getParentKey());
				}
				Collections.reverse(groupNames);
				html.append(StringUtils.join(groupNames, "-"));
				// role
				UsRole role = (UsRole) ORMService.getInstance().findByPk(UsRole.class.getName(), roleKey);
				html.append(" [" + (role != null ? role.getBusiName() : roleKey) + "]");
				html.append("</font>");
				html.append(user.getBusiName());
				html.append("</span>");
				return html.toString();
			}
		});

		String actionMode = RequestContext.getCurrent().getString(Keys.ACTION_MODE.toString());
		return FreeMarkerUtils.process("classpath:ftl/" + actionMode + "/showOpinion.ftl", model);
	}

	/**
	 * 根据任务获取订单实体
	 * 
	 * @param task
	 * @return
	 */
	public static Map<String, Object> getOrder(Task task) {
		ProcessInstance pi = FlowFactory.getRuntimeService().createProcessInstanceQuery()
				.processInstanceId(task.getProcessInstanceId()).singleResult();
		return getOrder(pi);
	}

	/**
	 * 根据流程实例获取订单实体
	 * 
	 * @param pi
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static Map<String, Object> getOrder(ProcessInstance pi) {
		// 订单表
		String orderTableName = (String) FlowFactory.getRuntimeService().getVariable(pi.getId(),
				VariableKeys._ORDER_TABLE_NAME.name());

		Map<String, Object> vo = (Map<String, Object>) ORMAdapterService.getInstance().findByPk(orderTableName,
				pi.getBusinessKey());
		if (vo == null) {
			throw new SystemRuntimeException(ExceptionType.FLOW_ORD_NOT_FOUND, "不存在[" + pi.getBusinessKey() + "]的订单..");
		}
		return vo;
	}

	/**
	 * 创建订单(启动流程)
	 * 
	 * @param pdKey
	 * @param vars
	 * @return 订单对象
	 */
	public static Map<String, Object> start(String pdKey, Map<String, Object> vars) {
		return start(pdKey, vars, true);
	}

	/**
	 * 创建订单(不启动流程)
	 * 
	 * @param pdKey
	 * @param vars
	 * @return 订单对象
	 */
	public static Map<String, Object> save(String pdKey, Map<String, Object> vars) {
		return start(pdKey, vars, false);
	}

	/**
	 * 启动流程
	 * 
	 * @param pdKey
	 * @param vars
	 * @param init  是否启动流程
	 * @return 订单对象
	 */
	@SuppressWarnings("unchecked")
	public static Map<String, Object> start(String pdKey, Map<String, Object> vars, boolean init) {
		ProcessDefinition pd = FlowService.getInstance().getPd(pdKey, 0);// 最新的流程配置
		WfPd config = FlowService.getInstance().findPdConfig(pd.getId());
		Map<String, Object> flowView = (Map<String, Object>) ORMService.getInstance().findByPk("VwFlowBasic",
				config.getBasicViewKey());

		FlowObject fo = new FlowObject();
		fo.setInit(init);

		fo.setPdKey(pd.getKey());
		fo.setPdId(pd.getId());
		fo.setPdName(pd.getName());

		BpmnModel bpmnModel = FlowFactory.getRepositoryService().getBpmnModel(pd.getId());
		Node startNode = BpmnHelper.getNode(bpmnModel, NodeType.START_EVENT);
		fo.setActivityId(startNode.getId());
		fo.setActivityName(startNode.getName());
		SequenceFlow sequenceFlow = startNode.getSequenceFlows().get(0);
		fo.setFlowId(sequenceFlow.getId());
		fo.setFlowName(sequenceFlow.getName());

		if (vars.get(OrderModelKeys.ORD_ID) != null) {
			fo.setOrdId((String) vars.get(OrderModelKeys.ORD_ID));
		}

		Map<String, Object> po = new DataPO((String) flowView.get("tableName"), vars).toEntity();
		FlowService.getInstance().executeFlowWithConfig(config.getBasicViewKey(), po, fo, null);

		return po;
	}

	/**
	 * 触发等待任务
	 * 
	 * @param po
	 * @return 订单对象
	 */
	public static Map<String, Object> signal(Map<String, Object> po) {
		if (po == null) {
			throw new SystemRuntimeException(ExceptionType.FLOW_ORD_NOT_FOUND);
		}

		String ordId = (String) po.get(OrderModelKeys.ORD_ID.getColumn().getName());

		ProcessInstance pi = FlowFactory.getRuntimeService().createProcessInstanceQuery()
				.processDefinitionId((String) po.get(OrderModelKeys.PD_ID.getColumn().getName()))
				.processInstanceBusinessKey(ordId).singleResult();
		if (pi != null) {
			// 流程实例部分
			List<Execution> executions = FlowFactory.getRuntimeService().createExecutionQuery()
					.processInstanceId(pi.getId()).list();
			if (executions != null) {
				BpmnModel bpmnModel = FlowFactory.getRepositoryService()
						.getBpmnModel((String) po.get(OrderModelKeys.PD_ID.getColumn().getName()));
				for (Execution execution : executions) {
					// 这里太重了,后续思考有没更好方式
					Node activityNode = BpmnHelper.getNode(bpmnModel, execution.getActivityId());
					if (activityNode.getNodeType() == NodeType.RECEIVE_TASK) {
						FlowService.getInstance().executeSignal(execution.getId());
					}
				}
			}
		}

		return po;
	}

}
