/*
 * $HeadURL: $
 * $Id: $
 * Copyright (c) 2013 by Riversoft System, all rights reserved.
 */
package com.riversoft.flow.listener;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.activiti.bpmn.model.BpmnModel;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.TaskListener;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.IdentityLink;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.riversoft.core.Config;
import com.riversoft.core.db.DataCondition;
import com.riversoft.core.db.DataPO;
import com.riversoft.core.db.ORMService;
import com.riversoft.core.db.ORMService.QueryVO;
import com.riversoft.core.exception.ExceptionType;
import com.riversoft.core.exception.SystemRuntimeException;
import com.riversoft.flow.BpmnHelper;
import com.riversoft.flow.FlowFactory;
import com.riversoft.flow.FlowObject;
import com.riversoft.flow.key.NodeType;
import com.riversoft.flow.key.OrderHistoryModelKeys;
import com.riversoft.flow.key.VariableKeys;
import com.riversoft.platform.SessionManager;
import com.riversoft.platform.db.ORMAdapterService;
import com.riversoft.platform.mail.script.MailHelper;
import com.riversoft.platform.po.UsGroup;
import com.riversoft.platform.po.UsRole;
import com.riversoft.platform.po.UsUser;
import com.riversoft.platform.po.WfPd;
import com.riversoft.platform.script.ScriptHelper;
import com.riversoft.platform.script.ScriptTypes;
import com.riversoft.platform.translate.NotifyMsgType;
import com.riversoft.platform.translate.NotifyReceiveType;
import com.riversoft.platform.translate.TaskNotifyType;
import com.riversoft.util.dynamicbean.DynamicBeanUtils;
import com.riversoft.util.jackson.JsonMapper;

/**
 * @author woden
 * 
 */
@SuppressWarnings("all")
public class UserTaskListener implements TaskListener {
	/**
	 * Logger for this class
	 */
	private static final Logger logger = LoggerFactory.getLogger(UserTaskListener.class);

	@Override
	public void notify(DelegateTask delegateTask) {

		// 历史表
		String historyTableName = (String) delegateTask.getVariable(VariableKeys._ORDER_HISTORY_TABLE_NAME.name());
		// 订单表
		String orderTableName = (String) delegateTask.getVariable(VariableKeys._ORDER_TABLE_NAME.name());

		// 获取对应订单实例
		DelegateExecution execution = delegateTask.getExecution();
		Map<String, Object> po = (Map<String, Object>) ORMAdapterService.getInstance().findByPk(orderTableName, execution.getProcessBusinessKey());
		if (po == null) {
			throw new SystemRuntimeException(ExceptionType.FLOW_ORD_NOT_FOUND);
		}
		ProcessDefinition pd = FlowFactory.getRepositoryService().createProcessDefinitionQuery().processDefinitionId(delegateTask.getProcessDefinitionId()).singleResult();
		BpmnModel bpmnModel = FlowFactory.getRepositoryService().getBpmnModel(delegateTask.getProcessDefinitionId());

		FlowObject fo = new FlowObject();
		fo.setActivityId(execution.getCurrentActivityId());
		fo.setActivityName(BpmnHelper.getNode(bpmnModel, execution.getCurrentActivityId()).getName());
		fo.setOrdId(execution.getProcessBusinessKey());
		fo.setPdId(execution.getProcessDefinitionId());
		fo.setPdKey(pd.getKey());
		fo.setPdName(pd.getName());

		Map<String, Object> context = new HashMap<>();
		context.put("vo", po);
		context.put("task", delegateTask);
		context.put("fo", fo);

		switch (delegateTask.getEventName()) {
		case EVENTNAME_CREATE:// 任务创建时
		{
			logger.debug("任务创建时[" + delegateTask.getId() + "|" + delegateTask.getName() + "]");
			// 分配用户
			{
				List<Map<String, Object>> assignees = ORMService.getInstance().queryHQL("from WfUserTaskAssignee where pdId = ? and activityId = ? order by batchNum asc,uniqueFlag desc,sort asc",
						execution.getProcessDefinitionId(), execution.getCurrentActivityId());
				Integer successBatchNum = null;// 成功的批次号

				if (assignees != null) {
					Set<String> notifyUids = new HashSet<>();
					Set<String> notifyGroup = new HashSet<>();

					loop: for (Map<String, Object> assignee : assignees) {
						int batchNum = (int) assignee.get("batchNum");
						if (successBatchNum != null && successBatchNum.intValue() != batchNum) {
							logger.debug("执行到批次[" + batchNum + "]时已有处理结果,后续不需要执行");
							break;
						}

						Integer decideType = (Integer) assignee.get("decideType");
						String decideScript = (String) assignee.get("decideScript");
						Integer allocateType = (Integer) assignee.get("allocateType");
						boolean decide = (boolean) ScriptHelper.evel(ScriptTypes.forCode(decideType), decideScript, context);

						if (decide) {
							switch (allocateType) {
							case 0:// UID
							{
								Integer uidType = (Integer) assignee.get("uidType");
								String uidScript = (String) assignee.get("uidScript");
								String uid = (String) ScriptHelper.evel(ScriptTypes.forCode(uidType), uidScript, context);

								Integer uniqueFlag = (Integer) assignee.get("uniqueFlag");
								if (uniqueFlag.intValue() == 1) {// 独占
									delegateTask.setAssignee(uid);
									notifyUids.clear();
									notifyGroup.clear();
									notifyUids.add(uid);
									break loop;// 独占优先级最高,直接跳出循环
								} else {// 共享
									delegateTask.addCandidateUser(uid);
									notifyUids.add(uid);
								}
							}
								break;
							case 1:// GROUP
							{
								Integer groupType = (Integer) assignee.get("groupType");
								String groupScript = (String) assignee.get("groupScript");
								String groupKey = (String) ScriptHelper.evel(ScriptTypes.forCode(groupType), groupScript, context);
								delegateTask.addCandidateGroup(groupKey + ";");
								notifyGroup.add(groupKey + ";");
							}
								break;
							case 2:// ROLE
							{
								Integer roleType = (Integer) assignee.get("roleType");
								String roleScript = (String) assignee.get("roleScript");
								String roleKey = (String) ScriptHelper.evel(ScriptTypes.forCode(roleType), roleScript, context);
								delegateTask.addCandidateGroup(";" + roleKey);
								notifyGroup.add(";" + roleKey);
							}
								break;
							case 3:// GROUP+ROLE
							{
								Integer groupType = (Integer) assignee.get("groupType");
								String groupScript = (String) assignee.get("groupScript");
								String groupKey = (String) ScriptHelper.evel(ScriptTypes.forCode(groupType), groupScript, context);
								Integer roleType = (Integer) assignee.get("roleType");
								String roleScript = (String) assignee.get("roleScript");
								String roleKey = (String) ScriptHelper.evel(ScriptTypes.forCode(roleType), roleScript, context);
								delegateTask.addCandidateGroup(groupKey + ";" + roleKey);
								notifyGroup.add(groupKey + ";" + roleKey);
							}
								break;
							default:
								break;
							}
							successBatchNum = batchNum;
						}
					}

					// 通知
					WfPd wfPd = (WfPd) ORMService.getInstance().findByPk(WfPd.class.getName(), execution.getProcessDefinitionId());
					Map<String, Object> viewConfig = (Map<String, Object>) ORMService.getInstance().findByPk("VwFlowBasic", wfPd.getBasicViewKey());
					Map<String, Object> taskConfig = (Map<String, Object>) ORMService.getInstance().findHQL("from WfUserTask where pdId = ? and activityId = ?", execution.getProcessDefinitionId(),
							execution.getCurrentActivityId());
					String[] msgType = StringUtils.split((String) viewConfig.get("msgType"), ";");
					if (!"false".equalsIgnoreCase(Config.get("mail.notify.flag")) && ArrayUtils.contains(msgType, NotifyMsgType.MAIL.getCode())) {// 接收邮件
						Integer mailSubjectType = (Integer) viewConfig.get("mailSubjectType");
						String mailSubjectScript = (String) viewConfig.get("mailSubjectScript");
						Integer mailContentType = (Integer) viewConfig.get("mailContentType");
						String mailContentScript = (String) viewConfig.get("mailContentScript");
						// 若流程模板空则使用系统模板
						if (StringUtils.isEmpty(mailSubjectScript)) {
							mailSubjectType = NumberUtils.createInteger(Config.get("mail.flow.subject.type"));
							mailSubjectScript = Config.getChinese("mail.flow.subject.script", "");
						}
						if (StringUtils.isEmpty(mailContentScript)) {
							mailContentType = NumberUtils.createInteger(Config.get("mail.flow.content.type"));
							mailContentScript = Config.getChinese("mail.flow.content.script", "");
						}

						if (StringUtils.isNotEmpty(mailSubjectScript)) {
							String subject = (String) ScriptHelper.evel(ScriptTypes.forCode(mailSubjectType), mailSubjectScript, context);
							String content = "";
							if (StringUtils.isNotEmpty(mailContentScript)) {
								content = (String) ScriptHelper.evel(ScriptTypes.forCode(mailContentType), mailContentScript, context);
							}

							String[] notifyType = StringUtils.split((String) taskConfig.get("notifyType"), ";");
							if (ArrayUtils.contains(notifyType, TaskNotifyType.user.getCode())) {// 接收个人任务
								for (String uid : notifyUids) {
									UsUser targetUser = (UsUser) ORMService.getInstance().findByPk(UsUser.class.getName(), uid);
									String[] userMsgType = StringUtils.split(targetUser.getMsgType(), ";");
									String[] userReceiveType = StringUtils.split(targetUser.getReceiveType(), ";");
									if (ArrayUtils.contains(userMsgType, NotifyMsgType.MAIL.getCode()) && StringUtils.isNotEmpty(targetUser.getMail())
											&& ArrayUtils.contains(userReceiveType, NotifyReceiveType.USER.getCode())) {
										MailHelper.asyncSystemSend(subject, content, targetUser.getMail());
									}
								}
							}
							if (ArrayUtils.contains(notifyType, TaskNotifyType.group.getCode())) {// 接收群组任务
								for (String tmp : notifyGroup) {
									List<String> uids;
									if (StringUtils.endsWith(tmp, ";")) {// 组织
										uids = ORMService.getInstance().queryHQL("select uid from UsUserGroupRole where groupKey = ?", tmp.split(";")[0]);
									} else if (StringUtils.startsWith(tmp, ";")) {// 角色
										uids = ORMService.getInstance().queryHQL("select uid from UsUserGroupRole where roleKey = ?", tmp.split(";")[1]);
									} else {// 组织+角色
										uids = ORMService.getInstance().queryHQL("select uid from UsUserGroupRole where groupKey = ? and roleKey = ?", tmp.split(";")[0], tmp.split(";")[1]);
									}
									List<String> mails = ORMService.getInstance().queryHQL(
											"select mail from " + UsUser.class.getName() + " where mail is not null and msgType like :msgType and receiveType like :receiveType and uid in (:list)",
											new QueryVO("msgType", "%" + NotifyMsgType.MAIL.getCode() + "%"), new QueryVO("receiveType", "%" + NotifyReceiveType.GROUP.getCode() + "%"),
											new QueryVO("list", uids));
									if (mails.size() > 0) {
										MailHelper.asyncSystemSend(subject, content, mails.toArray(new String[0]));
									}
								}
							}

						}
					}
				}
			}

			// 登记log
			if (StringUtils.isNotEmpty(historyTableName)) {

				String activityId = execution.getCurrentActivityId();
				String activityName = execution.getCurrentActivityName();

				// 拷贝订单表
				DataPO historyPO = new DataPO(historyTableName, DynamicBeanUtils.cloneMap(po));
				historyPO.set(OrderHistoryModelKeys.ACTIVITY_ID.name(), activityId);
				historyPO.set(OrderHistoryModelKeys.ACTIVITY_NAME.name(), activityName);
				historyPO.set(OrderHistoryModelKeys.TASK_BEGIN_DATE.name(), delegateTask.getCreateTime());
				historyPO.set(OrderHistoryModelKeys.NODE_TYPE.name(), NodeType.USER_TASK.name());
				historyPO.set(OrderHistoryModelKeys.TASK_ID.name(), delegateTask.getId());
				historyPO.set(OrderHistoryModelKeys.ASSIGNEE.name(), translateAssignee(delegateTask));
				ORMAdapterService.getInstance().save(historyPO.toEntity());
				logger.debug("创建新的HISTORY记录:[" + historyPO.get(OrderHistoryModelKeys.ID.name()) + "]");
			}
		}
			break;

		case EVENTNAME_COMPLETE:// 任务完成时
		{
			logger.debug("任务完成时[" + delegateTask.getId() + "|" + delegateTask.getName() + "]");
			// 登记log
			if (StringUtils.isNotEmpty(historyTableName)) {
				String sequenceFlowId = (String) delegateTask.getVariable(VariableKeys.TaskVariableKeys._USER_TASK_OUTCOME.name());
				ProcessInstance pi = FlowFactory.getRuntimeService().createProcessInstanceQuery().processInstanceId(delegateTask.getProcessInstanceId()).singleResult();
				String activityId = pi.getActivityId();
				String sequenceFlowName;
				Map<String, Object> btn = (Map<String, Object>) ORMService.getInstance().findHQL("from WfUserTaskBtn where pdId = ? and activityId = ? and flowId = ?", pi.getProcessDefinitionId(),
						activityId, sequenceFlowId);
				if (btn != null) {
					sequenceFlowName = (String) btn.get("busiName");
				} else {
					sequenceFlowName = BpmnHelper.getNode(bpmnModel, sequenceFlowId).getName();
				}

				// 按任务ID查找,并且结束时间为空
				Map<String, Object> historyPOEntity = (Map<String, Object>) ORMAdapterService.getInstance().find(historyTableName,
						new DataCondition().setStringEqual(OrderHistoryModelKeys.TASK_ID.name(), delegateTask.getId()).setDateIsNull(OrderHistoryModelKeys.TASK_END_DATE.name()).toEntity());

				if (historyPOEntity != null) {
					DataPO historyPO = new DataPO(historyTableName, historyPOEntity);
					logger.debug("找到待回写的记录:[" + historyPO.get(OrderHistoryModelKeys.ID.name()) + "]");

					// 拷贝订单的值
					for (String key : po.keySet()) {
						if (historyPOEntity.containsKey(key)) {
							if (key.startsWith("$")) {
								continue;
							}
							historyPO.set(key, po.get(key));
						}
					}
					historyPO.set(OrderHistoryModelKeys.SEQUENCE_FLOW_ID.name(), sequenceFlowId);
					historyPO.set(OrderHistoryModelKeys.SEQUENCE_FLOW_NAME.name(), sequenceFlowName);
					historyPO.set(OrderHistoryModelKeys.TASK_END_DATE.name(), new Date());
					historyPO.set(OrderHistoryModelKeys.TASK_UID.name(), SessionManager.getUser().getUid());
					historyPO.set(OrderHistoryModelKeys.TASK_GROUP.name(), SessionManager.getGroup().getGroupKey());
					logger.debug(JsonMapper.defaultMapper().toJson(historyPO.toEntity()));
					ORMAdapterService.getInstance().update(historyPO.toEntity());
				} else {
					logger.warn("找不到回写记录.");
				}
			}
		}
			break;
		default:
			break;
		}

	}

	/**
	 * 获取任务处理人
	 * 
	 * @param delegateTask
	 * @return
	 */
	private String translateAssignee(DelegateTask delegateTask) {
		if (StringUtils.isNotEmpty(delegateTask.getAssignee())) {// 独享处理人的情况
			String uid = delegateTask.getAssignee();
			return ((UsUser) ORMService.getInstance().findByPk(UsUser.class.getName(), uid)).getBusiName();
		} else {
			// 共享处理人
			List<IdentityLink> indentityList = FlowFactory.getTaskService().getIdentityLinksForTask(delegateTask.getId());

			if (indentityList == null || indentityList.size() < 1) {
				return "(无主任务)";
			}

			List<String> list = new ArrayList<>();
			for (IdentityLink o : indentityList) {
				if (StringUtils.isNotEmpty(o.getUserId())) {// 用户
					String uid = o.getUserId();
					UsUser user = (UsUser) ORMService.getInstance().findByPk(UsUser.class.getName(), uid);
					list.add("<span tip=\"true\" title=\"用户\">" + (user != null ? user.getBusiName() : uid) + "</span>");
				} else {// 组织
					String str = o.getGroupId();
					if (str.startsWith(";")) {// 角色
						String roleKey = str.substring(1);
						UsRole role = (UsRole) ORMService.getInstance().findByPk(UsRole.class.getName(), roleKey);
						list.add("<span tip=\"true\" title=\"角色\" style=\"color:blue;\">" + (role != null ? role.getBusiName() : roleKey) + "</span>");
					} else if (str.endsWith(";")) {// 组织
						String groupKey = str.substring(0, str.length() - 1);
						UsGroup group = (UsGroup) ORMService.getInstance().findByPk(UsGroup.class.getName(), groupKey);

						list.add("<span tip=\"true\" title=\"组织\" style=\"color:blue;\">" + (group != null ? group.getBusiName() : groupKey) + "</span>");
					} else {// 组织+角色
						String[] strs = str.split(";");
						String groupKey = strs[0];
						String roleKey = strs[1];
						UsGroup group = (UsGroup) ORMService.getInstance().findByPk(UsGroup.class.getName(), groupKey);
						UsRole role = (UsRole) ORMService.getInstance().findByPk(UsRole.class.getName(), roleKey);

						list.add("<span tip=\"true\" title=\"组织+角色\" style=\"color:blue;\">" + (group != null ? group.getBusiName() : groupKey) + "+" + (role != null ? role.getBusiName() : roleKey)
								+ "</span>");
					}
				}
			}

			return StringUtils.join(list, ",");
		}
	}
}
