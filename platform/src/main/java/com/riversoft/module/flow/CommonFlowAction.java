/*
 * $HeadURL: $
 * $Id: $
 * Copyright (c) 2014 by Riversoft System, all rights reserved.
 */
package com.riversoft.module.flow;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.activiti.engine.task.TaskQuery;
import org.apache.commons.lang3.StringUtils;

import com.riversoft.core.db.DataCondition;
import com.riversoft.core.db.DataPackage;
import com.riversoft.core.db.ORMService;
import com.riversoft.core.exception.ExceptionType;
import com.riversoft.core.exception.SystemRuntimeException;
import com.riversoft.core.web.Actions;
import com.riversoft.core.web.Actions.Keys;
import com.riversoft.core.web.Actions.Util;
import com.riversoft.core.web.RequestUtils;
import com.riversoft.flow.FlowFactory;
import com.riversoft.flow.FlowHelper;
import com.riversoft.flow.FlowObject;
import com.riversoft.flow.FlowService;
import com.riversoft.flow.key.FlowKeys;
import com.riversoft.flow.key.NodeType;
import com.riversoft.flow.key.OrderHistoryModelKeys;
import com.riversoft.platform.SessionManager;
import com.riversoft.platform.db.ORMAdapterService;
import com.riversoft.platform.po.VwUrl;
import com.riversoft.platform.po.WfPd;
import com.riversoft.platform.web.view.annotation.Conf;
import com.riversoft.platform.web.view.annotation.Conf.TargetType;
import com.riversoft.platform.web.view.annotation.Sys;
import com.riversoft.platform.web.view.annotation.Sys.SysMethod;

/**
 * 通用工作流处理
 * 
 * @author woden
 * 
 */
@Sys
public class CommonFlowAction {

	@SysMethod
	@Conf(description = "工作流通用", sort = 1, doc = "classpath:/doc/module/flow.html", target = { TargetType.MENU, TargetType.HOME, TargetType.BTN })
	public void index(HttpServletRequest request, HttpServletResponse response) {
		HashMap<String, Object> params = RequestUtils.getJsonValue(request, Keys.PARAMS.toString());

		if (params != null) {
			if (params.containsKey("panel")) {
				if ("pd".equalsIgnoreCase(params.get("panel").toString())) {
					Actions.forwardAction(request, response, Util.getActionUrl(request) + "/pdPanel.shtml");
					return;
				} else if ("task".equalsIgnoreCase(params.get("panel").toString())) {// 流程汇总
					Actions.forwardAction(request, response, Util.getActionUrl(request) + "/taskPanel.shtml");
					return;
				}
			} else if (params.containsKey("detail") && "true".equalsIgnoreCase(params.get("detail").toString())) {
				Actions.forwardAction(request, response, Util.getActionUrl(request) + "/detail.shtml");
				return;
			} else if (params.containsKey("form") && "true".equalsIgnoreCase(params.get("form").toString())) {
				Actions.forwardAction(request, response, Util.getActionUrl(request) + "/form.shtml");
				return;
			} else if (params.containsKey("remove") && "true".equalsIgnoreCase(params.get("remove").toString())) {// 删除订单
				Actions.forwardAction(request, response, Util.getActionUrl(request) + "/removeOrder.shtml");
				return;
			}
		}

		// 无参数则跳转到任务面板
		Actions.forwardAction(request, response, Util.getActionUrl(request) + "/taskPanel.shtml");
	}

	/**
	 * 流程汇总
	 * 
	 * @param request
	 * @param response
	 */
	@SuppressWarnings("unchecked")
	public void pdPanel(HttpServletRequest request, HttpServletResponse response) {
		HashMap<String, Object> params = RequestUtils.getJsonValue(request, Keys.PARAMS.toString());
		List<?> list;

		if (params != null && params.containsKey("pdKey")) {
			Object pdKey = params.get("pdKey");
			List<String> keys = new ArrayList<>();

			if (pdKey instanceof List) {
				for (Iterator<Object> it = ((List) pdKey).iterator(); it.hasNext();) {
					keys.add(it.next().toString());
				}
			} else if (pdKey instanceof String) {
				keys.add(pdKey.toString());
			}
			list = FlowService.getInstance().listPd(keys.toArray(new String[0]));
		} else {
			list = FlowService.getInstance().listAllLastPd();
		}

		request.setAttribute("list", list);
		Actions.includePage(request, response, Util.getPagePath(request, "pd_panel.jsp"));
	}

	/**
	 * 任务控制台
	 * 
	 * @param request
	 * @param response
	 */
	public void taskPanel(HttpServletRequest request, HttpServletResponse response) {
		Actions.includePage(request, response, Util.getPagePath(request, "task_panel.jsp"));
	}

	/**
	 * 分流程待办(查询)
	 * 
	 * @param request
	 * @param response
	 */
	@SuppressWarnings("unchecked")
	public void taskMain(HttpServletRequest request, HttpServletResponse response) {
		Map<String, Object> params = RequestUtils.getJsonValue(request, Keys.PARAMS.toString());
		List<ProcessDefinition> pds = new ArrayList<>(); // 可选流程
		boolean quickMode = false;// 是否展示查询条件
		if (params != null && params.containsKey("pdKey")) {
			Object key = params.get("pdKey");

			if (key instanceof List) {
				pds.addAll(FlowService.getInstance().listPd(((List<String>) key).toArray(new String[0])));
			} else if (key instanceof String) {
				ProcessDefinition pd = FlowFactory.getRepositoryService().createProcessDefinitionQuery().processDefinitionKey(key.toString()).latestVersion().singleResult();
				pds.add(pd);
			}
		} else {
			pds.addAll(FlowService.getInstance().listAllLastPd());
		}

		if (params != null && params.containsKey("quickMode")) {
			quickMode = Boolean.valueOf(params.get("quickMode").toString());
		}

		request.setAttribute("pds", pds);
		request.setAttribute("quickMode", quickMode);

		Actions.includePage(request, response, Util.getPagePath(request, "task_main.jsp"));
	}

	/**
	 * 分流程待办
	 * 
	 * @param request
	 * @param response
	 */
	public void taskList(HttpServletRequest request, HttpServletResponse response) {
		String type = RequestUtils.getStringValue(request, "type");// 任务类别
		String[] pdKey = RequestUtils.getStringValues(request, "pdKey");// 所属流程

		if (pdKey == null || pdKey.length < 1 || StringUtils.isEmpty(pdKey[0])) {
			Map<String, Object> params = RequestUtils.getJsonValue(request, Keys.PARAMS.toString());
			if (params != null && params.containsKey("pdKey")) {
				Object key = params.get("pdKey");
				List<String> keys = new ArrayList<>();
				if (key instanceof List) {
					for (Iterator<Object> it = ((List) key).iterator(); it.hasNext();) {
						keys.add(it.next().toString());
					}
				} else if (key instanceof String) {
					keys.add(pdKey.toString());
				}
				pdKey = keys.toArray(new String[0]);
			}
		}

		// 获取分页信息
		int start = Util.getStart(request);
		int limit = Util.getLimit(request);

		String field = Util.getSortField(request);
		String dir = Util.getSortDir(request);

		TaskQuery query = FlowService.getInstance().createTaskQuery("share".equals(type), pdKey);
		if (StringUtils.isNotEmpty(RequestUtils.getStringValue(request, "businessKey"))) {
			query.processInstanceBusinessKeyLike(RequestUtils.getStringValue(request, "businessKey"));
		}

		if (StringUtils.isNotEmpty(RequestUtils.getStringValue(request, "_before_createTime"))) {
			query.taskCreatedBefore(RequestUtils.getDateValue(request, "_before_createTime"));
		}

		if (StringUtils.isNotEmpty(RequestUtils.getStringValue(request, "_after_createTime"))) {
			query.taskCreatedAfter(RequestUtils.getDateValue(request, "_after_createTime"));
		}

		if (StringUtils.isNotEmpty(RequestUtils.getStringValue(request, "businessKey"))) {
			query.processInstanceBusinessKeyLike('%' + RequestUtils.getStringValue(request, "businessKey") + '%');
		}
		if (StringUtils.isNotEmpty(RequestUtils.getStringValue(request, "process"))) {
			query.processDefinitionNameLike('%' + RequestUtils.getStringValue(request, "process") + '%');
		}

		// 排序
		if (StringUtils.isNotEmpty(field)) {
			if (StringUtils.equalsIgnoreCase(field, "createTime")) {
				query.orderByTaskCreateTime();
				if (StringUtils.equalsIgnoreCase(dir, "desc")) {
					query.desc();
				} else {
					query.asc();
				}
			} else if (StringUtils.equalsIgnoreCase(field, "taskName")) {
				query.orderByTaskName();
				if (StringUtils.equalsIgnoreCase(dir, "desc")) {
					query.desc();
				} else {
					query.asc();
				}
			}
		} else {
			query.orderByTaskCreateTime().desc();
		}

		DataPackage dp = FlowService.getInstance().listTaskPackage(query, start, limit);
		request.setAttribute("dp", dp);

		// 处理任务,获取相应vo等信息
		List<Map<String, Object>> list = new ArrayList<>();
		for (Object obj : dp.getList()) {
			Task task = (Task) obj;
			Map<String, Object> vo = new HashMap<>();
			ProcessInstance pi = FlowFactory.getRuntimeService().createProcessInstanceQuery().processInstanceId(task.getProcessInstanceId()).includeProcessVariables().singleResult();
			ProcessDefinition pd = FlowFactory.getRepositoryService().getProcessDefinition(task.getProcessDefinitionId());

			// 当前节点展示
			vo.put("activity", FlowHelper.showActivity(task));
			vo.put("assignee", FlowHelper.showAssignee(task));
			vo.put("order", FlowHelper.getOrder(pi));

			vo.put("ordId", pi.getBusinessKey());
			vo.put("task", task);
			vo.put("pdName", pd.getName());
			list.add(vo);
		}
		request.setAttribute("list", list);

		Actions.includePage(request, response, Util.getPagePath(request, "task_list.jsp"));
	}

	/**
	 * 分流程获取待办任务数
	 * 
	 * @param request
	 * @param response
	 */
	public void getTaskCount(HttpServletRequest request, HttpServletResponse response) {
		String[] pdKey = RequestUtils.getStringValues(request, "pdKey");// 所属流程
		if (pdKey == null || pdKey.length < 1) {
			Map<String, Object> params = RequestUtils.getJsonValue(request, Keys.PARAMS.toString());
			if (params != null && params.containsKey("pdKey")) {
				Object key = params.get("pdKey");
				List<String> keys = new ArrayList<>();
				if (key instanceof List) {
					for (Iterator<Object> it = ((List) key).iterator(); it.hasNext();) {
						keys.add(it.next().toString());
					}
				} else if (key instanceof String) {
					keys.add(pdKey.toString());
				}
				pdKey = keys.toArray(new String[0]);
			}
		}

		Map<String, Object> result = new HashMap<>();
		long userCount = FlowService.getInstance().countTask(FlowService.getInstance().createTaskQuery(false, pdKey));
		long shareCount = FlowService.getInstance().countTask(FlowService.getInstance().createTaskQuery(true, pdKey));
		result.put("flag", userCount + shareCount > 0);// 多余1个任务才返回成功.
		result.put("userCount", userCount);
		result.put("shareCount", shareCount);
		Actions.showJson(request, response, result);
	}

	/**
	 * 删除草稿箱订单
	 * 
	 * @param request
	 * @param response
	 */
	public void removeOrder(HttpServletRequest request, HttpServletResponse response) {
		FlowObject fo = FlowObject.create(request);
		String pdId;
		String ordId;

		if (StringUtils.isNotEmpty(fo.getOrdId()) && StringUtils.isNotEmpty(fo.getPdId())) {// 订单号+流程ID入参
			pdId = fo.getPdId();
			ordId = fo.getOrdId();
		} else {
			throw new SystemRuntimeException(ExceptionType.BUSINESS, "入参错误,无法查看订单..");
		}

		WfPd config = FlowService.getInstance().findPdConfig(pdId);
		VwUrl basicUrl = (VwUrl) ORMService.getInstance().findByPk(VwUrl.class.getName(), config.getBasicViewKey());
		if (config == null || basicUrl == null) {
			throw new SystemRuntimeException(ExceptionType.BUSINESS, "流程已被删除,无法浏览订单.");
		}
		String params = "{remove:true,ordId:'" + ordId + "'}";
		Actions.redirectAction(request, response, basicUrl.getUrl() + "?" + Keys.PARAMS.toString() + "=" + params);
	}

	/**
	 * 明细查看
	 * 
	 * @param request
	 * @param response
	 */
	public void detail(HttpServletRequest request, HttpServletResponse response) {
		FlowObject fo = FlowObject.create(request);
		String pdId;
		String ordId;

		String params = null;
		if (StringUtils.isNotEmpty(fo.getTaskId())) {// 任务入参
			Task task = FlowFactory.getTaskService().createTaskQuery().taskId(fo.getTaskId()).singleResult();
			ProcessInstance pi = FlowFactory.getRuntimeService().createProcessInstanceQuery().processInstanceId(task.getProcessInstanceId()).singleResult();
			ordId = pi.getBusinessKey();
			pdId = pi.getProcessDefinitionId();
			params = "{detail:true,taskId:'" + fo.getTaskId() + "'}";
		} else if (StringUtils.isNotEmpty(fo.getOrdId()) && StringUtils.isNotEmpty(fo.getPdId())) {// 订单号+流程ID入参
			pdId = fo.getPdId();
			ordId = fo.getOrdId();

			// 找到"我相关"的activityId
			List<Task> tasks = FlowFactory.getTaskService().createTaskQuery().processInstanceBusinessKey(ordId).processDefinitionId(pdId).list();
			for (Task task : tasks) {
				if (FlowHelper.checkTask(task)) {
					params = "{detail:true,taskId:'" + task.getId() + "'}";
					break;
				}
			}

			if (StringUtils.isEmpty(params)) {
				// 从历史表查询
				WfPd wfPd = (WfPd) ORMService.getInstance().findByPk(WfPd.class.getName(), pdId);
				if (wfPd == null) {
					throw new SystemRuntimeException(ExceptionType.CONFIG, "流程[" + pdId + "]已被删除,请联系管理员.");
				}
				Map<String, Object> basicView = (Map<String, Object>) ORMService.getInstance().findByPk("VwFlowBasic", wfPd.getBasicViewKey());
				if (basicView == null) {
					throw new SystemRuntimeException(ExceptionType.CONFIG, "视图[" + wfPd.getBasicViewKey() + "]已被删除,请联系管理员.");
				}
				String historyTableName = (String) basicView.get("historyTableName");
				if (StringUtils.isNotEmpty(historyTableName)) {
					// 查找是否存在当前用户处理过的历史节点
					List<Map<String, Object>> list = ORMAdapterService.getInstance().query(historyTableName,
							new DataCondition().setStringEqual(OrderHistoryModelKeys.ORD_ID.getColumn().getName(), ordId)
									.setStringEqual(OrderHistoryModelKeys.TASK_UID.getColumn().getName(), SessionManager.getUser().getUid())
									.setStringNotEqual(OrderHistoryModelKeys.NODE_TYPE.getColumn().getName(), NodeType.END_EVENT.name())
									.setOrderByDesc(OrderHistoryModelKeys.TASK_END_DATE.getColumn().getName()).toEntity());
					if (list.size() > 0) {
						params = "{detail:true,ordId:'" + ordId + "',activityId:'" + list.get(0).get(OrderHistoryModelKeys.ACTIVITY_ID.getColumn().getName()) + "'}";
					}
				}
			}

			if (StringUtils.isEmpty(params)) {
				params = "{detail:true,ordId:'" + ordId + "'}";
			}
		} else {
			throw new SystemRuntimeException(ExceptionType.BUSINESS, "入参错误,无法查看订单.");
		}

		WfPd config = FlowService.getInstance().findPdConfig(pdId);
		VwUrl basicUrl = (VwUrl) ORMService.getInstance().findByPk(VwUrl.class.getName(), config.getBasicViewKey());
		if (config == null || basicUrl == null) {
			throw new SystemRuntimeException(ExceptionType.BUSINESS, "流程已被删除,无法浏览订单.");
		}

		Actions.redirectAction(request, response, basicUrl.getUrl() + "?" + Keys.PARAMS.toString() + "=" + params);
	}

	/**
	 * 流程图查看
	 * 
	 * @param request
	 * @param response
	 */
	public void picture(HttpServletRequest request, HttpServletResponse response) {
		FlowObject fo = FlowObject.create(request);
		String pdId;
		String ordId;

		if (StringUtils.isNotEmpty(fo.getTaskId())) {// 任务入参
			Task task = FlowFactory.getTaskService().createTaskQuery().taskId(fo.getTaskId()).singleResult();
			ProcessInstance pi = FlowFactory.getRuntimeService().createProcessInstanceQuery().processInstanceId(task.getProcessInstanceId()).singleResult();
			ordId = pi.getBusinessKey();
			pdId = pi.getProcessDefinitionId();
		} else if (StringUtils.isNotEmpty(fo.getOrdId()) && StringUtils.isNotEmpty(fo.getPdId())) {// 订单号+流程ID入参
			pdId = fo.getPdId();
			ordId = fo.getOrdId();
		} else {
			throw new SystemRuntimeException(ExceptionType.BUSINESS, "入参错误,无法查看订单..");
		}

		WfPd config = FlowService.getInstance().findPdConfig(pdId);
		VwUrl basicUrl = (VwUrl) ORMService.getInstance().findByPk(VwUrl.class.getName(), config.getBasicViewKey());
		if (config == null || basicUrl == null) {
			throw new SystemRuntimeException(ExceptionType.BUSINESS, "流程已被删除,无法浏览订单.");
		}
		String params = "{detail:true,ordId:'" + ordId + "'}";
		Actions.redirectAction(request, response, basicUrl.getUrl() + "?" + Keys.PARAMS.toString() + "=" + params);
	}

	/**
	 * 任务处理
	 * 
	 * @param request
	 * @param response
	 */
	public void form(HttpServletRequest request, HttpServletResponse response) {
		FlowObject fo = FlowObject.create(request);
		String taskId;
		String pdId;
		String pdKey;

		if (StringUtils.isNotEmpty(fo.getTaskId())) {// 任务入参
			Task task = FlowFactory.getTaskService().createTaskQuery().taskId(fo.getTaskId()).singleResult();
			if (task == null) {
				throw new SystemRuntimeException(ExceptionType.FLOW_TASK_ASSIGNEE, "任务不存在,可能已被处理.");
			}
			ProcessInstance pi = FlowFactory.getRuntimeService().createProcessInstanceQuery().processInstanceId(task.getProcessInstanceId()).singleResult();
			pdId = pi.getProcessDefinitionId();
			taskId = task.getId();
			pdKey = "";
		} else if (StringUtils.isNotEmpty(fo.getOrdId()) && StringUtils.isNotEmpty(fo.getPdId())) {// 订单号+流程ID入参
			pdId = fo.getPdId();
			// 若是订单号则获取第一个任务
			List<Task> tasks = FlowFactory.getTaskService().createTaskQuery().processDefinitionId(pdId).processInstanceBusinessKey(fo.getOrdId()).orderByTaskCreateTime().asc().list();
			taskId = null;
			if (tasks.size() > 0) {
				for (Task o : tasks) {
					if (FlowHelper.checkTask(o)) {
						taskId = o.getId();
						break;
					}
				}
				if (taskId == null) {
					throw new SystemRuntimeException(ExceptionType.BUSINESS, "无权处理订单.");
				}
			} else {// 还未发起任务
				taskId = "";
			}
			pdKey = "";

		} else if (StringUtils.isNotEmpty(fo.getPdKey())) {
			taskId = "";
			ProcessDefinition pd = FlowService.getInstance().getPd(fo.getPdKey(), 0);
			if (pd == null) {
				throw new SystemRuntimeException(ExceptionType.BUSINESS, "入参错误,找不到流程[" + fo.getPdKey() + "].");
			}
			pdKey = fo.getPdKey();
			pdId = pd.getId();

		} else {
			throw new SystemRuntimeException(ExceptionType.BUSINESS, "入参错误,无法查看订单.");
		}

		WfPd config = FlowService.getInstance().findPdConfig(pdId);
		VwUrl basicUrl = (VwUrl) ORMService.getInstance().findByPk(VwUrl.class.getName(), config.getBasicViewKey());
		if (config == null || basicUrl == null) {
			throw new SystemRuntimeException(ExceptionType.BUSINESS, "流程已被删除,无法浏览订单.");
		}
		String params = "{form:true,pdKey:'" + pdKey + "'}";
		Actions.redirectAction(request, response,
				basicUrl.getUrl() + "?" + Keys.PARAMS.toString() + "=" + params + "&" + FlowKeys._TASK_ID.getName() + "=" + taskId + "&" + FlowKeys._ORD_ID.getName() + "=" + fo.getOrdId());
	}

}
