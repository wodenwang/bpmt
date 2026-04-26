/*
 * $HeadURL: $
 * $Id: $
 * Copyright (c) 2013 by Riversoft System, all rights reserved.
 */
package com.riversoft.flow;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.activiti.bpmn.model.BpmnModel;
import org.activiti.engine.ActivitiException;
import org.activiti.engine.TaskService;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.repository.ProcessDefinitionQuery;
import org.activiti.engine.runtime.Execution;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.IdentityLink;
import org.activiti.engine.task.Task;
import org.activiti.engine.task.TaskQuery;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.riversoft.core.BeanFactory;
import com.riversoft.core.Config;
import com.riversoft.core.db.DataCondition;
import com.riversoft.core.db.DataPO;
import com.riversoft.core.db.DataPackage;
import com.riversoft.core.db.ORMService;
import com.riversoft.core.exception.ExceptionType;
import com.riversoft.core.exception.SystemRuntimeException;
import com.riversoft.flow.key.OrderHistoryModelKeys;
import com.riversoft.flow.key.OrderModelKeys;
import com.riversoft.flow.key.OrderOpinionModelKeys;
import com.riversoft.flow.key.VariableKeys;
import com.riversoft.platform.SessionManager;
import com.riversoft.platform.db.ORMAdapterService;
import com.riversoft.platform.mail.script.MailHelper;
import com.riversoft.platform.po.UsUser;
import com.riversoft.platform.po.WfPd;
import com.riversoft.platform.script.ScriptHelper;
import com.riversoft.platform.script.ScriptTypes;
import com.riversoft.platform.translate.NotifyMsgType;
import com.riversoft.platform.translate.NotifyReceiveType;
import com.riversoft.platform.translate.TaskNotifyType;
import com.riversoft.util.dynamicbean.DynamicBeanUtils;

/**
 * 封装流程相关逻辑
 * 
 * @author woden
 * 
 */
public class FlowService {

	/**
	 * Logger for this class
	 */
	static final Logger logger = LoggerFactory.getLogger(FlowService.class);

	/**
	 * 获取单例
	 * 
	 * @return
	 */
	public static FlowService getInstance() {
		return BeanFactory.getInstance().getSingleBean(FlowService.class);
	}

	/**
	 * 获取流程定义
	 * 
	 * @param pdId
	 * @return
	 */
	public ProcessDefinition getPd(String pdId) {
		ProcessDefinition pd = FlowFactory.getRepositoryService().getProcessDefinition(pdId);
		if (pd == null) {
			throw new SystemRuntimeException(ExceptionType.FLOW_PD_NOT_FOUND);
		}

		return pd;
	}

	/**
	 * 获取流程定义
	 * 
	 * @param pdKey
	 * @param version
	 * @return
	 */
	public ProcessDefinition getPd(String pdKey, int version) {
		ProcessDefinitionQuery query = FlowFactory.getRepositoryService().createProcessDefinitionQuery();
		query.processDefinitionKey(pdKey);
		if (version > 0) {
			query.processDefinitionVersion(version);// 有版本则找版本
		} else {
			query.latestVersion();
		}
		ProcessDefinition pd = query.singleResult();
		if (pd == null) {
			throw new SystemRuntimeException(ExceptionType.FLOW_PD_NOT_FOUND);
		}

		return pd;
	}

	/**
	 * 获取流程定义列表
	 * 
	 * @param keys
	 * @return
	 */
	public List<ProcessDefinition> listPd(String... keys) {
		List<ProcessDefinition> list = new ArrayList<>();
		if (keys != null) {
			for (String key : keys) {
				ProcessDefinitionQuery query = FlowFactory.getRepositoryService().createProcessDefinitionQuery();
				query.latestVersion();
				query.processDefinitionKey(key);
				ProcessDefinition pd = query.singleResult();
				if (pd == null) {
					throw new SystemRuntimeException(ExceptionType.BUSINESS, "找不到流程[" + key + "].");
				}
				list.add(pd);
			}
		}
		return list;
	}

	/**
	 * 获取所有最新流程定义列表
	 * 
	 * @return
	 */
	public List<ProcessDefinition> listAllLastPd() {
		ProcessDefinitionQuery query = FlowFactory.getRepositoryService().createProcessDefinitionQuery();
		query.latestVersion().orderByProcessDefinitionName().asc();
		return query.list();
	}

	/**
	 * 获取流程定义配置
	 * 
	 * @param pdId
	 * @return
	 */
	public WfPd findPdConfig(String pdId) {
		WfPd po = (WfPd) ORMService.getInstance().findByPk(WfPd.class.getName(), pdId);
		return po;
	}

	/**
	 * 创建任务查询器
	 * 
	 * @param isShare
	 *            是否共享任务
	 * @param pdKeys
	 *            流程KEY
	 * @return
	 */
	public TaskQuery createTaskQuery(boolean isShare, String... pdKeys) {
		TaskService taskService = FlowFactory.getTaskService();
		TaskQuery query = taskService.createTaskQuery();

		if (isShare) {
			// 共享任务
			query.taskCandidateUser(SessionManager.getUser().getUid());
		} else {
			// 独享任务
			query.taskAssignee(SessionManager.getUser().getUid());
		}

		// 流程KEY
		if (pdKeys != null && pdKeys.length > 0) {
			if (pdKeys.length == 1) {
				if (StringUtils.isNotEmpty(pdKeys[0])) {
					query.processDefinitionKey(pdKeys[0]);
				}
			} else {
				List<String> deploymentIds = new ArrayList<>();
				for (String pdKey : pdKeys) {
					if (StringUtils.isEmpty(pdKey)) {
						continue;
					}
					List<ProcessDefinition> pds = FlowFactory.getRepositoryService().createProcessDefinitionQuery().processDefinitionKey(pdKey).list();
					if (pds != null) {
						for (ProcessDefinition pd : pds) {
							deploymentIds.add(pd.getDeploymentId());
						}
					}
				}
				query.deploymentIdIn(deploymentIds);
			}
		}

		return query;
	}

	/**
	 * 个人待办数
	 * 
	 * @param query
	 * @return
	 */
	public long countTask(TaskQuery query) {
		return query.count();
	}

	/**
	 * 个人待办
	 * 
	 * @param query
	 * @param start
	 * @param limit
	 * @return
	 */
	public DataPackage listTaskPackage(TaskQuery query, int start, int limit) {
		DataPackage result = new DataPackage();
		result.setStart(start);
		result.setLimit(limit);

		result.setTotalRecord(query.count());
		result.setList(query.listPage(start, limit));

		return result;
	}

	/**
	 * 校验当前用户是否具备访问task的权限
	 * 
	 * @param task
	 * @return
	 */
	public boolean checkTask(Task task) {
		// 管理员无敌
		if (SessionManager.isAdmin()) {
			return true;
		}

		// 验证用户列表
		if (StringUtils.isNotEmpty(task.getAssignee())) {// 独享模式
			return SessionManager.getUser().getUid().equals(task.getAssignee());
		} else {// 备选模式
			TaskService taskService = FlowFactory.getTaskService();
			List<IdentityLink> list = taskService.getIdentityLinksForTask(task.getId());

			if (list != null) {
				for (IdentityLink o : list) {
					if (StringUtils.isNotEmpty(o.getUserId())) {
						if (SessionManager.getUser().getUid().equals(o.getUserId())) {
							return true;
						}
					} else if (StringUtils.isNotEmpty(o.getGroupId())) {
						String str = o.getGroupId();
						if (str.startsWith(";")) {// 角色
							String roleKey = str.substring(1);
							if (SessionManager.getRole().getRoleKey().equals(roleKey)) {
								return true;
							}
						} else if (str.endsWith(";")) {// 组织
							String groupKey = str.substring(0, str.length() - 1);
							if (SessionManager.getGroup().getGroupKey().equals(groupKey)) {
								return true;
							}
						} else {// 组织+角色
							String[] strs = str.split(";");
							String groupKey = strs[0];
							String roleKey = strs[1];
							if (SessionManager.getRole().getRoleKey().equals(roleKey) && SessionManager.getGroup().getGroupKey().equals(groupKey)) {
								return true;
							}
						}
					}
				}
			}

			return false;
		}
	}

	/**
	 * 校验当前用户是否允许处理订单
	 * 
	 * @param pdKey
	 * @param po
	 * @param index
	 *            校验的任务索引
	 * @return
	 */
	public boolean checkTask(Map<String, Object> po, int index) {
		if (po == null) {
			return false;
		}

		int ordState = (int) po.get(OrderModelKeys.ORD_STATE.getColumn().getName());
		if (ordState > 1) {// 已完成
			return false;
		} else if (ordState == 0) {// 未启动
			return SessionManager.getUser().getUid().equals(po.get(OrderModelKeys.OWNER.name()));// 未启动时只有owner是自己才能处理
		}

		List<Task> tasks = FlowFactory.getTaskService().createTaskQuery().includeTaskLocalVariables().processDefinitionId((String) po.get(OrderModelKeys.PD_ID.name()))
				.processInstanceBusinessKey((String) po.get(OrderModelKeys.ORD_ID.getColumn().getName())).list();
		if (tasks == null || tasks.size() < 1) {
			return false;
		}

		if (index <= 0) {// index小于等于0时,只要有一个任务校验通过则返回true
			for (Task task : tasks) {
				boolean result = checkTask(task);
				if (result) {
					return true;
				}
			}
		} else if (tasks.size() >= index) {
			return checkTask(tasks.get(index - 1));
		}

		return false;
	}

	/**
	 * 校验当前用户是否允许处理订单
	 * 
	 * @param pdKey
	 * @param po
	 * @return
	 */
	public boolean checkTask(Map<String, Object> po) {
		return checkTask(po, 0);
	}

	/**
	 * 启动流程
	 * 
	 * @param pdId
	 *            流程定义ID
	 * @param po
	 *            订单实体
	 * @param historyTableName
	 *            历史订单表名;留空则不记录历史日志
	 */
	public void executeStart(String pdId, Map<String, Object> po, String historyTableName) {
		if (po == null) {
			throw new SystemRuntimeException(ExceptionType.FLOW_ORD_NOT_FOUND);
		}
		String ordId = (String) po.get(OrderModelKeys.ORD_ID.getColumn().getName());
		if (StringUtils.isEmpty(ordId)) {
			throw new SystemRuntimeException(ExceptionType.FLOW_ORD_NOT_FOUND);
		}

		// 校验business_key
		long count = FlowFactory.getRuntimeService().createExecutionQuery().processInstanceBusinessKey(ordId).processDefinitionId(pdId).count();
		if (count > 0) {
			throw new SystemRuntimeException(ExceptionType.FLOW_START_REPEATED, "已存在单号为[" + ordId + "]的流程.");
		}

		int ordState = (int) po.get(OrderModelKeys.ORD_STATE.getColumn().getName());

		if (ordState != 0) {
			throw new SystemRuntimeException(ExceptionType.FLOW_START_REPEATED, "无法启动流程,该流程已启动.");
		}

		Map<String, Object> vars = new HashMap<>();
		vars.put(VariableKeys._ORDER_TABLE_NAME.name(), po.get("$type$"));// 保存表名
		vars.put(VariableKeys._ORDER_HISTORY_TABLE_NAME.name(), historyTableName);// 保存历史表名
		try {
			FlowFactory.getRuntimeService().startProcessInstanceById(pdId, ordId, vars);
		} catch (ActivitiException e) {
			throw new SystemRuntimeException(ExceptionType.FLOW_ORD_PAUSE, e);
		}
	}

	/**
	 * 启动流程
	 * 
	 * @param pdKey
	 *            流程KEY
	 * @param version
	 *            流程版本
	 * @param po
	 *            订单实体
	 * @param historyTableName
	 *            历史订单表名;留空则不记录历史日志
	 */
	public void executeStart(String pdKey, int version, Map<String, Object> po, String historyTableName) {
		executeStart(FlowService.getInstance().getPd(pdKey, version).getId(), po, historyTableName);
	}

	/**
	 * 提交任务
	 * 
	 * @param taskId
	 *            任务ID
	 * @param sequenceFlowId
	 *            点击按钮(连线)
	 * @param po
	 *            订单实体
	 */
	public void executeComplete(String taskId, String sequenceFlowId, Map<String, Object> po) {

		if (logger.isDebugEnabled()) {
			logger.debug("========处理任务[" + taskId + "]=========");
		}

		// 校验任务权限
		Task task = FlowFactory.getTaskService().createTaskQuery().taskId(taskId).singleResult();
		if (task == null) {
			throw new SystemRuntimeException(ExceptionType.FLOW_ORD_NOT_FOUND);
		}
		if (logger.isDebugEnabled()) {
			logger.debug("找到任务:[" + task.getId() + "],[" + task.getName() + "]");
		}

		// 管理员无敌,否则验证独占任务
		if (!SessionManager.isAdmin() && StringUtils.isNotEmpty(task.getAssignee())) {
			if (!task.getAssignee().equals(SessionManager.getUser().getUid())) {
				throw new SystemRuntimeException(ExceptionType.FLOW_TASK_ASSIGNEE, "当前任务已被[" + task.getAssignee() + "]独占.");
			}
		} else if (!StringUtils.isNotEmpty(task.getAssignee())) {
			if (logger.isDebugEnabled()) {
				logger.debug("管理员[" + SessionManager.getUser().getUid() + "]直接处理.");
			}
		}

		Map<String, Object> vars = new HashMap<>();
		vars.put(VariableKeys.TaskVariableKeys._USER_TASK_OUTCOME.name(), sequenceFlowId);// 指定连线
		try {
			FlowFactory.getTaskService().complete(taskId, vars);
			if (logger.isDebugEnabled()) {
				logger.debug("执行任务完成.");
			}
		} catch (ActivitiException e) {
			logger.error("执行任务出错.", e);
			throw new SystemRuntimeException(ExceptionType.FLOW_ORD_PAUSE, e);
		}

		if (logger.isDebugEnabled()) {
			logger.debug("========完成任务[" + taskId + "]=========");
		}
	}

	/**
	 * 代理任务<br>
	 * FIXME 未支持
	 * 
	 * @param taskId
	 * @param uid
	 */
	public void executeDelegate(String taskId, String uid) {
		try {
			FlowFactory.getTaskService().delegateTask(taskId, uid);
		} catch (ActivitiException e) {
			throw new SystemRuntimeException(ExceptionType.FLOW_ORD_PAUSE, e);
		}
	}

	/**
	 * 触发流程(触发等待节点)
	 * 
	 * @param executionId
	 */
	public void executeSignal(String executionId) {
		FlowFactory.getRuntimeService().signal(executionId);
	}

	/**
	 * 领取任务
	 * 
	 * @param viewKey
	 * @param taskId
	 * @param uid
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void executeClaimWithConfig(String viewKey, String taskId, String uid) {
		Map<String, Object> config = (Map<String, Object>) ORMService.getInstance().findByPk("VwFlowBasic", viewKey);
		String historyTableName = (String) config.get("historyTableName");

		if (StringUtils.isNotEmpty(historyTableName)) {
			// 获取节点log对象
			Map<String, Object> historyPOEntity = (Map<String, Object>) ORMAdapterService.getInstance().find(historyTableName,
					new DataCondition().setStringEqual(OrderHistoryModelKeys.TASK_ID.name(), taskId).setDateIsNull(OrderHistoryModelKeys.TASK_END_DATE.name()).toEntity());

			if (historyPOEntity != null) {
				DataPO taskHistoryPO = new DataPO(historyTableName, historyPOEntity);// 任务已存在的节点vo
				// 拷贝日志vo
				DataPO historyPO = new DataPO(historyTableName, DynamicBeanUtils.cloneMap(historyPOEntity));
				historyPO.set(OrderHistoryModelKeys.ID.name(), null);
				historyPO.set(OrderHistoryModelKeys.SEQUENCE_FLOW_ID.name(), null);
				historyPO.set(OrderHistoryModelKeys.SEQUENCE_FLOW_NAME.name(), "领取任务");
				historyPO.set(OrderHistoryModelKeys.TASK_END_DATE.name(), new Date());
				historyPO.set(OrderHistoryModelKeys.TASK_UID.name(), SessionManager.getUser().getUid());
				historyPO.set(OrderHistoryModelKeys.TASK_GROUP.name(), SessionManager.getGroup().getGroupKey());
				historyPO.set(OrderHistoryModelKeys.EXECUTION_MEMO.name(), "用户[" + SessionManager.getUser().getBusiName() + "]领取了任务.");
				ORMAdapterService.getInstance().save(historyPO.toEntity());

				// 更新已有节点日志
				taskHistoryPO.set(OrderHistoryModelKeys.TASK_BEGIN_DATE.name(), new Date());
				taskHistoryPO.set(OrderHistoryModelKeys.ASSIGNEE.name(), SessionManager.getUser().getBusiName());// 修改目标处理人
				ORMAdapterService.getInstance().update(taskHistoryPO.toEntity());
			}
		}

		try {
			FlowFactory.getTaskService().claim(taskId, uid);
		} catch (ActivitiException e) {
			throw new SystemRuntimeException(ExceptionType.FLOW_ORD_PAUSE, e);
		}
	}

	/**
	 * 根据流程配置信息处理
	 * 
	 * @param viewKey
	 * @param po
	 * @param fo
	 * @param opinion
	 *            审批意见
	 */
	@SuppressWarnings("unchecked")
	public void executeFlowWithConfig(String viewKey, Map<String, Object> po, FlowObject fo, String opinion) {
		ORMAdapterService ormAdapterService = ORMAdapterService.getInstance();
		Map<String, Object> config = (Map<String, Object>) ORMService.getInstance().findByPk("VwFlowBasic", viewKey);
		String ordId = fo.getOrdId();
		String pdId;

		boolean isCreate = StringUtils.isEmpty(ordId);// 订单号空说明需要创建订单

		if (!isCreate) {
			pdId = (String) po.get(OrderModelKeys.PD_ID.getColumn().getName());
		} else {
			pdId = FlowService.getInstance().getPd((String) config.get("pdKey"), 0).getId();
		}

		if (logger.isDebugEnabled()) {
			logger.debug("当前订单号[" + ordId + "].(若空表示执行新增)");
		}

		// ===============STEP:1===============
		if (logger.isDebugEnabled()) {
			logger.debug("[" + ordId + "]节点前置处理.");
		}
		// 节点特有前置处理器
		List<Map<String, Object>> beforeExecs;

		if (fo.isInit()) {// 调用引擎
			if (StringUtils.isNotEmpty(fo.getTaskId())) {// 用户节点
				beforeExecs = ORMService.getInstance().queryHQL("from WfUserTaskExecBefore where pdId = ? and activityId = ? and flowId = ? order by sort asc", pdId, fo.getActivityId(),
						fo.getFlowId());
			} else {// 开始节点
				beforeExecs = ORMService.getInstance().queryHQL("from WfStartEventExecBefore where pdId = ? order by sort asc", pdId);
			}
		} else {
			beforeExecs = null;
		}
		if (beforeExecs != null) {
			for (Map<String, Object> exec : beforeExecs) {
				Integer type = (Integer) exec.get("execType");
				String script = (String) exec.get("execScript");
				if (StringUtils.isNotEmpty(script)) {
					Map<String, Object> context = new HashMap<String, Object>();
					context.put("vo", po);
					context.put("fo", fo);
					context.put("mode", isCreate ? 1 : 2);
					ScriptHelper.evel(ScriptTypes.forCode(type), script, context);
				}
			}
		}

		// ===============STEP:2===============
		if (logger.isDebugEnabled()) {
			logger.debug("[" + ordId + "]视图前置处理.");
		}
		// 前置处理
		for (Map<String, Object> exec : (Set<Map<String, Object>>) config.get("beforeExecs")) {
			Integer type = (Integer) exec.get("execType");
			String script = (String) exec.get("execScript");
			if (StringUtils.isNotEmpty(script)) {
				Map<String, Object> context = new HashMap<String, Object>();
				context.put("vo", po);
				context.put("fo", fo);
				context.put("mode", isCreate ? 1 : 2);
				ScriptHelper.evel(ScriptTypes.forCode(type), script, context);
			}
		}

		// ===============STEP:3===============
		// 以前没有ordId,说明本次需要save订单
		if (logger.isDebugEnabled()) {
			logger.debug("[" + ordId + "]订单保存.");
		}
		if (isCreate) {
			// 生成订单号
			Map<String, Object> context = new HashMap<>();
			Integer ordIdType = (Integer) config.get("ordIdType");
			String ordIdScript = (String) config.get("ordIdScript");
			context.put("vo", po);
			context.put("fo", fo);
			context.put("mode", isCreate ? 1 : 2);
			Object value = ScriptHelper.evel(ScriptTypes.forCode(ordIdType), ordIdScript, context);
			if (value == null) {
				throw new SystemRuntimeException(ExceptionType.FLOW_CREATE_ORD_ID);
			}
			ordId = value.toString();
			if (logger.isDebugEnabled()) {
				logger.debug("生成新的订单号:[" + ordId + "]");
			}
			po.put(OrderModelKeys.ORD_ID.getColumn().getName(), ordId);
			po.put(OrderModelKeys.ORD_STATE.getColumn().getName(), 0);// 默认状态是0
			po.put(OrderModelKeys.OWNER.getColumn().getName(), SessionManager.getUser() != null ? SessionManager.getUser().getUid() : "_system");
			po.put(OrderModelKeys.OWNER_GROUP.name(), SessionManager.getGroup() != null ? SessionManager.getGroup().getGroupKey() : "_system");
			po.put(OrderModelKeys.OWNER_ROLE.name(), SessionManager.getRole() != null ? SessionManager.getRole().getRoleKey() : "_system");
			po.put(OrderModelKeys.CREATE_DATE.getColumn().getName(), new Date());
			po.put(OrderModelKeys.PD_ID.getColumn().getName(), pdId);
			po.put(OrderModelKeys.UIDS.getColumn().getName(), SessionManager.getUser() != null ? "~" + SessionManager.getUser().getUid() + "~" : "_system" + "~");
			fo.setOrdId(ordId);
			ormAdapterService.save(po);
		} else {
			// 保存当前处理人
			String uids = (String) po.get(OrderModelKeys.UIDS.getColumn().getName());
			if (StringUtils.isEmpty(uids)) {
				uids = "~";
			}
			if (!StringUtils.contains(uids, SessionManager.getUser() != null ? "~" + SessionManager.getUser().getUid() + "~" : "_system" + "~")) {
				po.put(OrderModelKeys.UIDS.getColumn().getName(), uids + SessionManager.getUser() != null ? "~" + SessionManager.getUser().getUid() + "~" : "_system" + "~");
			}
			ormAdapterService.update(po);
		}

		// ===============STEP:4===============
		// 保存审批意见
		String opinionTableName = (String) config.get("opinionTableName");
		Map<String, Object> btn;
		if (fo.isInit()) {// 调用流程引擎
			if (StringUtils.isNotEmpty(fo.getTaskId())) {// 用户节点
				btn = (Map<String, Object>) ORMService.getInstance().findHQL("from WfUserTaskBtn where pdId = ? and activityId = ? and flowId = ? order by sort asc", pdId, fo.getActivityId(),
						fo.getFlowId());
			} else {// 开始节点
				btn = (Map<String, Object>) ORMService.getInstance().findHQL("from WfStartEventBtnStart where pdId = ? ", pdId);
			}
		} else {
			btn = null;
		}
		if (btn != null && ((Integer) btn.get("opinionFlag")).intValue() == 1 && StringUtils.isNotEmpty(opinionTableName)) {
			DataPO opinionPO = new DataPO(opinionTableName);
			opinionPO.set(OrderOpinionModelKeys.ORD_ID.name(), ordId);
			opinionPO.set(OrderOpinionModelKeys.OPR_USER.name(), SessionManager.getUser() != null ? SessionManager.getUser().getUid() : "_system");
			opinionPO.set(OrderOpinionModelKeys.OPR_GROUP.name(), SessionManager.getGroup() != null ? SessionManager.getGroup().getGroupKey() : "_system");
			opinionPO.set(OrderOpinionModelKeys.OPR_ROLE.name(), SessionManager.getRole() != null ? SessionManager.getRole().getRoleKey() : "_system");
			opinionPO.set(OrderOpinionModelKeys.CREATE_DATE.name(), new Date());
			opinionPO.set(OrderOpinionModelKeys.ACTIVITY_ID.name(), fo.getActivityId());
			opinionPO.set(OrderOpinionModelKeys.ACTIVITY_NAME.name(), StringUtils.isNotEmpty(fo.getActivityName()) ? fo.getActivityName() : "开始节点");
			opinionPO.set(OrderOpinionModelKeys.SEQUENCE_FLOW_ID.name(), fo.getFlowId());
			opinionPO.set(OrderOpinionModelKeys.SEQUENCE_FLOW_NAME.name(), StringUtils.isNotEmpty(fo.getFlowName()) ? fo.getFlowName() : "新建流程");
			opinionPO.set(OrderOpinionModelKeys.OPINION.name(), opinion);
			ormAdapterService.save(opinionPO.toEntity());
		}

		// ===============STEP:5===============
		if (logger.isDebugEnabled()) {
			logger.debug("[" + ordId + "]视图后置处理.");
		}
		// 后置处理
		for (Map<String, Object> exec : (Set<Map<String, Object>>) config.get("afterExecs")) {
			Integer type = (Integer) exec.get("execType");
			String script = (String) exec.get("execScript");
			if (StringUtils.isNotEmpty(script)) {
				Map<String, Object> context = new HashMap<String, Object>();
				context.put("vo", po);
				context.put("fo", fo);
				context.put("mode", isCreate ? 1 : 2);
				ScriptHelper.evel(ScriptTypes.forCode(type), script, context);
			}
		}

		// ===============STEP:6===============
		if (logger.isDebugEnabled()) {
			logger.debug("[" + ordId + "]节点后置处理.");
		}
		// 节点特有后置处理器
		List<Map<String, Object>> afterExecs;
		if (fo.isInit()) {// 调用引擎
			if (StringUtils.isNotEmpty(fo.getTaskId())) {// 用户节点
				afterExecs = ORMService.getInstance().queryHQL("from WfUserTaskExecAfter where pdId = ? and activityId = ? and flowId = ? order by sort asc", pdId, fo.getActivityId(), fo.getFlowId());
			} else {// 开始节点
				afterExecs = ORMService.getInstance().queryHQL("from WfStartEventExecAfter where pdId = ? order by sort asc", pdId);
			}
		} else {
			afterExecs = null;
		}
		if (afterExecs != null) {
			for (Map<String, Object> exec : afterExecs) {
				Integer type = (Integer) exec.get("execType");
				String script = (String) exec.get("execScript");
				if (StringUtils.isNotEmpty(script)) {
					Map<String, Object> context = new HashMap<String, Object>();
					context.put("vo", po);
					context.put("fo", fo);
					context.put("mode", isCreate ? 1 : 2);
					ScriptHelper.evel(ScriptTypes.forCode(type), script, context);
				}
			}
		}

		// ===============STEP:99===============
		if (logger.isDebugEnabled()) {
			logger.debug("[" + ordId + "]工作流处理.");
		}
		// 处理工作流
		if (fo.isInit()) {// 流程需要初始化
			if (logger.isDebugEnabled()) {
				logger.debug("[" + ordId + "]流程引擎调用.");
			}
			if (StringUtils.isNotEmpty(fo.getTaskId())) {// 处理节点数据
				if (logger.isDebugEnabled()) {
					logger.debug("[" + ordId + "]提交任务.");
					logger.debug("任务号:[" + fo.getTaskId() + "],动作:[" + fo.getFlowId() + "].");
				}
				FlowService.getInstance().executeComplete(fo.getTaskId(), fo.getFlowId(), po);
			} else {
				if (logger.isDebugEnabled()) {
					logger.debug("[" + ordId + "]启动流程.");
				}
				FlowService.getInstance().executeStart((String) po.get(OrderModelKeys.PD_ID.getColumn().getName()), po, (String) config.get("historyTableName"));
			}
		} else if (!isCreate) {// 点保存的情况
			if (!checkTask(po)) {// 权限校验不通过,不让保存
				throw new SystemRuntimeException(ExceptionType.FLOW_TASK_ASSIGNEE);
			}
		}
	}

	/**
	 * 转办任务
	 * 
	 * @param viewKey
	 * @param taskId
	 * @param uid
	 * @param btnName
	 * @param opinion
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void executeForwardWithConfig(String viewKey, String taskId, String uid, String btnName, String opinion) {
		ORMAdapterService ormAdapterService = ORMAdapterService.getInstance();
		Map<String, Object> config = (Map<String, Object>) ORMService.getInstance().findByPk("VwFlowBasic", viewKey);
		String opinionTableName = (String) config.get("opinionTableName");
		String historyTableName = (String) config.get("historyTableName");
		String tableName = (String) config.get("tableName");
		UsUser targetUser = (UsUser) ORMService.getInstance().findByPk(UsUser.class.getName(), uid);
		Task task = FlowFactory.getTaskService().createTaskQuery().taskId(taskId).singleResult();

		// 校验任务权限
		if (task == null) {
			throw new SystemRuntimeException(ExceptionType.FLOW_ORD_NOT_FOUND);
		}

		Execution execution = FlowFactory.getRuntimeService().createExecutionQuery().executionId(task.getExecutionId()).singleResult();
		ProcessInstance pi = FlowFactory.getRuntimeService().createProcessInstanceQuery().processInstanceId(task.getProcessInstanceId()).singleResult();
		BpmnModel bpmnModel = FlowFactory.getRepositoryService().getBpmnModel(pi.getProcessDefinitionId());
		Map<String, Object> po = (Map<String, Object>) ormAdapterService.findByPk(tableName, pi.getBusinessKey());
		FlowObject fo = new FlowObject();
		fo.setActivityId(execution.getActivityId());
		fo.setActivityName(BpmnHelper.getNode(bpmnModel, pi.getActivityId()).getName());
		fo.setOrdId(pi.getBusinessKey());
		fo.setPdKey(pi.getProcessDefinitionKey());
		fo.setPdName(pi.getProcessDefinitionName());
		fo.setPdId(pi.getProcessDefinitionId());

		// 管理员无敌,否则验证独占任务
		if (!SessionManager.isAdmin() && StringUtils.isNotEmpty(task.getAssignee())) {
			if (!task.getAssignee().equals(SessionManager.getUser().getUid())) {
				throw new SystemRuntimeException(ExceptionType.FLOW_TASK_ASSIGNEE);
			}
		} else if (!StringUtils.isNotEmpty(task.getAssignee())) {
			if (logger.isDebugEnabled()) {
				logger.debug("管理员[" + SessionManager.getUser().getUid() + "]直接处理.");
			}
		}

		// 意见
		if (StringUtils.isNotEmpty(opinionTableName)) {
			DataPO opinionPO = new DataPO(opinionTableName);
			opinionPO.set(OrderOpinionModelKeys.ORD_ID.name(), pi.getBusinessKey());
			opinionPO.set(OrderOpinionModelKeys.OPR_USER.name(), SessionManager.getUser().getUid());
			opinionPO.set(OrderOpinionModelKeys.OPR_GROUP.name(), SessionManager.getGroup().getGroupKey());
			opinionPO.set(OrderOpinionModelKeys.OPR_ROLE.name(), SessionManager.getRole().getRoleKey());
			opinionPO.set(OrderOpinionModelKeys.CREATE_DATE.name(), new Date());
			opinionPO.set(OrderOpinionModelKeys.ACTIVITY_ID.name(), execution.getActivityId());
			opinionPO.set(OrderOpinionModelKeys.ACTIVITY_NAME.name(), BpmnHelper.getNode(bpmnModel, execution.getActivityId()).getName());
			opinionPO.set(OrderOpinionModelKeys.SEQUENCE_FLOW_ID.name(), null);
			opinionPO.set(OrderOpinionModelKeys.SEQUENCE_FLOW_NAME.name(), btnName);
			opinionPO.set(OrderOpinionModelKeys.OPINION.name(), opinion);
			ormAdapterService.save(opinionPO.toEntity());
		}

		// 保存当前处理人
		{
			String uids = (String) po.get(OrderModelKeys.UIDS.getColumn().getName());
			if (StringUtils.isEmpty(uids)) {
				uids = "~";
			}
			if (!StringUtils.contains(uids, "~" + SessionManager.getUser().getUid() + "~")) {
				po.put(OrderModelKeys.UIDS.getColumn().getName(), uids + SessionManager.getUser().getUid() + "~");
			}
			ormAdapterService.update(po);
		}

		// 工作流日志
		if (StringUtils.isNotEmpty(historyTableName)) {
			// 拷贝最近的日志
			{
				List<Map<String, Object>> historyPOEntityList = ORMAdapterService.getInstance().query(historyTableName,
						new DataCondition().setStringEqual(OrderHistoryModelKeys.TASK_ID.name(), taskId).setOrderByDesc(OrderHistoryModelKeys.TASK_BEGIN_DATE.name()).toEntity());
				if (historyPOEntityList != null && historyPOEntityList.size() > 0) {
					// 拷贝日志vo
					DataPO historyPO = new DataPO(historyTableName, DynamicBeanUtils.cloneMap(historyPOEntityList.get(0)));
					if (historyPO.get(OrderHistoryModelKeys.TASK_END_DATE.name()) != null) {
						historyPO.set(OrderHistoryModelKeys.TASK_BEGIN_DATE.name(), historyPO.get(OrderHistoryModelKeys.TASK_END_DATE.name()));
					}
					historyPO.set(OrderHistoryModelKeys.ID.name(), null);
					historyPO.set(OrderHistoryModelKeys.SEQUENCE_FLOW_ID.name(), null);
					historyPO.set(OrderHistoryModelKeys.SEQUENCE_FLOW_NAME.name(), btnName);
					historyPO.set(OrderHistoryModelKeys.TASK_END_DATE.name(), new Date());
					historyPO.set(OrderHistoryModelKeys.TASK_UID.name(), SessionManager.getUser().getUid());
					historyPO.set(OrderHistoryModelKeys.TASK_GROUP.name(), SessionManager.getGroup().getGroupKey());
					historyPO.set(OrderHistoryModelKeys.EXECUTION_MEMO.name(), "用户[" + SessionManager.getUser().getBusiName() + "]转交[" + targetUser.getBusiName() + "].");
					ORMAdapterService.getInstance().save(historyPO.toEntity());
				}
			}

			// 更新基础日志
			{
				// 获取节点log对象
				Map<String, Object> historyPOEntity = (Map<String, Object>) ORMAdapterService.getInstance().find(historyTableName,
						new DataCondition().setStringEqual(OrderHistoryModelKeys.TASK_ID.name(), taskId).setDateIsNull(OrderHistoryModelKeys.TASK_END_DATE.name()).toEntity());
				if (historyPOEntity != null) {
					DataPO taskHistoryPO = new DataPO(historyTableName, historyPOEntity);// 任务已存在的节点vo
					// 更新已有节点日志
					taskHistoryPO.set(OrderHistoryModelKeys.TASK_BEGIN_DATE.name(), new Date());
					taskHistoryPO.set(OrderHistoryModelKeys.ASSIGNEE.name(), (targetUser.getBusiName()));// 修改目标处理人
					ORMAdapterService.getInstance().update(taskHistoryPO.toEntity());
				}
			}
		}

		try {
			FlowFactory.getTaskService().setAssignee(taskId, uid);
			// 通知
			Map<String, Object> taskConfig = (Map<String, Object>) ORMService.getInstance().findHQL("from WfUserTask where pdId = ? and activityId = ?", pi.getProcessDefinitionId(),
					execution.getActivityId());
			String[] msgType = StringUtils.split((String) config.get("msgType"), ";");
			if (!"false".equalsIgnoreCase(Config.get("mail.notify.flag")) && ArrayUtils.contains(msgType, NotifyMsgType.MAIL.getCode())) {// 接收邮件
				Integer mailSubjectType = (Integer) config.get("mailSubjectType");
				String mailSubjectScript = (String) config.get("mailSubjectScript");
				Integer mailContentType = (Integer) config.get("mailContentType");
				String mailContentScript = (String) config.get("mailContentScript");
				// 若流程模板空则使用系统模板
				if (StringUtils.isEmpty(mailSubjectScript)) {
					mailSubjectType = NumberUtils.createInteger(Config.get("mail.flow.subject.type"));
					mailSubjectScript = Config.getChinese("mail.flow.subject.script", "");
				}
				if (StringUtils.isEmpty(mailContentScript)) {
					mailContentType = NumberUtils.createInteger(Config.get("mail.flow.content.type"));
					mailContentScript = Config.getChinese("mail.flow.content.script", "");
				}
				Map<String, Object> context = new HashMap<>();
				context.put("vo", po);
				context.put("task", task);
				context.put("fo", fo);
				if (StringUtils.isNotEmpty(mailSubjectScript)) {
					String subject = (String) ScriptHelper.evel(ScriptTypes.forCode(mailSubjectType), mailSubjectScript, context);
					String content = "";
					if (StringUtils.isNotEmpty(mailContentScript)) {
						content = (String) ScriptHelper.evel(ScriptTypes.forCode(mailContentType), mailContentScript, context);
					}
					logger.debug("准备发送邮件...{}...{}...", subject, content);
					String[] notifyType = StringUtils.split((String) taskConfig.get("notifyType"), ";");
					if (ArrayUtils.contains(notifyType, TaskNotifyType.forward.getCode())) {// 接收转发任务
						String[] userMsgType = StringUtils.split(targetUser.getMsgType(), ";");
						String[] userReceiveType = StringUtils.split(targetUser.getReceiveType(), ";");
						if (ArrayUtils.contains(userMsgType, NotifyMsgType.MAIL.getCode()) && StringUtils.isNotEmpty(targetUser.getMail())
								&& ArrayUtils.contains(userReceiveType, NotifyReceiveType.USER.getCode())) {
							MailHelper.asyncSystemSend(subject, content, targetUser.getMail());
						}
					}
				}
			}

		} catch (ActivitiException e) {
			throw new SystemRuntimeException(ExceptionType.FLOW_ORD_PAUSE, e);
		}
	}

}
