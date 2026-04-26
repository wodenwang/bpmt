/*
 * $HeadURL: $
 * $Id: $
 * Copyright (c) 2013 by Riversoft System, all rights reserved.
 */
package com.riversoft.module.flow.view;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.activiti.bpmn.model.BpmnModel;
import org.activiti.bpmn.model.GraphicInfo;
import org.activiti.bpmn.model.SequenceFlow;
import org.activiti.engine.ProcessEngineConfiguration;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.Execution;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.activiti.image.ProcessDiagramGenerator;
import org.activiti.image.impl.DefaultProcessDiagramGenerator;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.riversoft.core.db.DataCondition;
import com.riversoft.core.db.DataPackage;
import com.riversoft.core.db.JdbcService;
import com.riversoft.core.db.ORMService;
import com.riversoft.core.exception.ExceptionType;
import com.riversoft.core.exception.SystemRuntimeException;
import com.riversoft.core.web.Actions;
import com.riversoft.core.web.Actions.Keys;
import com.riversoft.core.web.Actions.Util;
import com.riversoft.core.web.RequestUtils;
import com.riversoft.core.web.annotation.ActionAccess;
import com.riversoft.core.web.annotation.ActionAccess.SafeLevel;
import com.riversoft.core.web.annotation.ActionMode;
import com.riversoft.core.web.annotation.ActionMode.Mode;
import com.riversoft.flow.BpmnHelper;
import com.riversoft.flow.BpmnHelper.Node;
import com.riversoft.flow.FlowFactory;
import com.riversoft.flow.FlowHelper;
import com.riversoft.flow.FlowObject;
import com.riversoft.flow.FlowService;
import com.riversoft.flow.key.NodeType;
import com.riversoft.flow.key.OrderHistoryModelKeys;
import com.riversoft.flow.key.OrderModelKeys;
import com.riversoft.platform.SessionManager;
import com.riversoft.platform.db.ORMAdapterService;
import com.riversoft.platform.po.CmPri;
import com.riversoft.platform.po.WfPd;
import com.riversoft.platform.script.ScriptHelper;
import com.riversoft.platform.script.ScriptTypes;
import com.riversoft.platform.web.CommonHelper;
import com.riversoft.platform.web.WebLogManager;
import com.riversoft.util.Formatter;
import com.riversoft.util.jackson.JsonMapper;

/**
 * 流程基础视图实现基类
 * 
 * @author woden
 * 
 */
@SuppressWarnings("unchecked")
public class BaseFlowBasicAction {

	private Logger logger = LoggerFactory.getLogger(BaseFlowBasicAction.class);

	/**
	 * 关联主键,唯一不变值
	 */
	private final String viewKey;

	protected BaseFlowBasicAction(String viewKey) {
		this.viewKey = viewKey;
	}

	/**
	 * 视图配置包装类
	 * 
	 * @author woden
	 * 
	 */
	public static class Config {

		private Map<String, Object> table;
		private List<Map<String, Object>> columns;// 展示页面字段
		private List<Map<String, Object>> fields;// 表单界面字段
		private List<Map<String, Object>> subs;// 子标签
		private List<Map<String, Object>> querys;// 查询条件字段
		private List<Map<String, Object>> listFields;// 列表展示字段
		private List<Map<String, Object>> downloadList; //下载字段

		private List<Map<String, Object>> itemBtns;// 明细按钮
		private List<Map<String, Object>> summaryBtns;// 汇总按钮
		private Map<String, Object> createBtn = null;// "新增"按钮

		private Config(Map<String, Object> table) {
			this.table = table;

			// 查询列表
			{
				querys = new ArrayList<>();
				querys.addAll((Set<Map<String, Object>>) table.get("querys"));
				querys.addAll((Set<Map<String, Object>>) table.get("extQuerys"));
				Collections.sort(querys, new Comparator<Map<String, Object>>() {
					@Override
					public int compare(Map<String, Object> o1, Map<String, Object> o2) {
						return (Integer) o1.get("sort") < (Integer) o2.get("sort") ? -1 : 1;
					}
				});
			}

			// 子标签
			{
				subs = new ArrayList<>();
				subs.addAll((Set<Map<String, Object>>) table.get("sysSubs"));
				subs.addAll((Set<Map<String, Object>>) table.get("viewSubs"));
				Collections.sort(subs, new Comparator<Map<String, Object>>() {
					@Override
					public int compare(Map<String, Object> o1, Map<String, Object> o2) {
						return (Integer) o1.get("sort") < (Integer) o2.get("sort") ? -1 : 1;
					}
				});
			}

			// 字段列表
			{
				columns = new ArrayList<>();
				fields = new ArrayList<>();
				columns.addAll((Set<Map<String, Object>>) table.get("showColumns"));
				columns.addAll((Set<Map<String, Object>>) table.get("lineColumns"));
				
				downloadList = new ArrayList<>();
				downloadList.addAll((Set<Map<String, Object>>) table.get("showColumns"));
				
				fields.addAll(columns);

				// 排序
				Collections.sort(columns, new Comparator<Map<String, Object>>() {
					@Override
					public int compare(Map<String, Object> o1, Map<String, Object> o2) {
						return (Integer) o1.get("sort") < (Integer) o2.get("sort") ? -1 : 1;
					}
				});
			}

			// 列表页字段
			{
				listFields = new ArrayList<>();
				for (Map<String, Object> o : (Set<Map<String, Object>>) table.get("showColumns")) {
					int listSort = (int) o.get("listSort");
					if (listSort > 0) {
						listFields.add(o);
					}
				}
				// 排序
				Collections.sort(listFields, new Comparator<Map<String, Object>>() {
					@Override
					public int compare(Map<String, Object> o1, Map<String, Object> o2) {
						return (Integer) o1.get("listSort") < (Integer) o2.get("listSort") ? -1 : 1;
					}
				});
			}

			// 按钮
			{
				itemBtns = new ArrayList<>();
				summaryBtns = new ArrayList<>();

				itemBtns.addAll((Set<Map<String, Object>>) table.get("itemBtns"));
				summaryBtns.addAll((Set<Map<String, Object>>) table.get("summaryBtns"));
				for (Map<String, Object> btn : (Set<Map<String, Object>>) table.get("sysBtns")) {
					if (((Integer) btn.get("type")) == 1) {// 明细
						itemBtns.add(btn);
					} else {// 汇总
						summaryBtns.add(btn);
					}

					if (SysBtn.CREATE.getName().equals(btn.get("name"))) {
						createBtn = btn;
					}
				}

				// 排序
				Collections.sort(itemBtns, new Comparator<Map<String, Object>>() {
					@Override
					public int compare(Map<String, Object> o1, Map<String, Object> o2) {
						return (Integer) o1.get("sort") < (Integer) o2.get("sort") ? -1 : 1;
					}
				});

				// 排序
				Collections.sort(summaryBtns, new Comparator<Map<String, Object>>() {
					@Override
					public int compare(Map<String, Object> o1, Map<String, Object> o2) {
						return (Integer) o1.get("sort") < (Integer) o2.get("sort") ? -1 : 1;
					}
				});

			}
		}

		/**
		 * @return the createBtn
		 */
		public Map<String, Object> getCreateBtn() {
			return createBtn;
		}

		/**
		 * @return the itemBtns
		 */
		public List<Map<String, Object>> getItemBtns() {
			return itemBtns;
		}

		/**
		 * @return the summaryBtns
		 */
		public List<Map<String, Object>> getSummaryBtns() {
			return summaryBtns;
		}

		/**
		 * @return the querys
		 */
		public List<Map<String, Object>> getQuerys() {
			return querys;
		}

		/**
		 * @return the listFields
		 */
		public List<Map<String, Object>> getListFields() {
			return listFields;
		}

		/**
		 * @return the table
		 */
		public Map<String, Object> getTable() {
			return table;
		}

		/**
		 * @return the fields
		 */
		public List<Map<String, Object>> getFields() {
			return fields;
		}

		/**
		 * @return the subs
		 */
		public List<Map<String, Object>> getSubs() {
			return subs;
		}

		/**
		 * @return the columns
		 */
		public List<Map<String, Object>> getColumns() {
			return columns;
		}
		
		/**
		 * @return the downloadList;
		 */
		public List<Map<String, Object>> getDownloadList(){
			return downloadList;
		}

	}

	/**
	 * 查询动态表配置
	 * 
	 * @return
	 */
	private Map<String, Object> getTableConfig() {
		Map<String, Object> table = (Map<String, Object>) ORMService.getInstance().findByPk("VwFlowBasic", viewKey);
		if (table == null) {
			throw new SystemRuntimeException(ExceptionType.CONFIG, "视图已删除.");
		}
		return table;
	}

	/**
	 * 进入分发页
	 * 
	 * @param request
	 * @param response
	 */
	@ActionMode(Mode.FIT)
	public void index(HttpServletRequest request, HttpServletResponse response) {

		HashMap<String, Object> params = RequestUtils.getJsonValue(request, Keys.PARAMS.toString());
		if (params != null) {
			if (params.containsKey("remove") && "true".equalsIgnoreCase(params.get("remove").toString())) {// 删除订单
				Actions.forwardAction(request, response, Util.getActionUrl(request) + "/removeOrder.shtml");
				return;
			} else if (params.containsKey("form") && "true".equalsIgnoreCase(params.get("form").toString())) {// 表单页
				Actions.forwardAction(request, response, Util.getActionUrl(request) + "/form.shtml");
				return;
			} else if (params.containsKey("detail") && "true".equalsIgnoreCase(params.get("detail").toString())) {// 明细页
				Actions.forwardAction(request, response, Util.getActionUrl(request) + "/detail.shtml");
				return;
			} else if (params.containsKey("list") && "true".equalsIgnoreCase(params.get("list").toString())) {// 列表页
				Actions.forwardAction(request, response, Util.getActionUrl(request) + "/list.shtml");
				return;
			} else if (params.containsKey("main") && "true".equalsIgnoreCase(params.get("main").toString())) {// 主框架页,含查询
				Actions.forwardAction(request, response, Util.getActionUrl(request) + "/main.shtml");
				return;
			} else if (params.containsKey("picture") && "true".equalsIgnoreCase(params.get("picture").toString())) {// 流程图
				Actions.forwardAction(request, response, Util.getActionUrl(request) + "/pictureMain.shtml");
				return;
			} else if (params.containsKey("direct") && "true".equalsIgnoreCase(params.get("direct").toString())) {// 流程直接开始
				Actions.forwardAction(request, response, Util.getActionUrl(request) + "/directForm.shtml");
				return;
			} else if (params.containsKey("all") && "false".equalsIgnoreCase(params.get("all").toString())) {// 个人订单
				Actions.forwardAction(request, response, Util.getActionUrl(request) + "/portalPerson.shtml");
				return;
			} else if (params.containsKey("all") && "true".equalsIgnoreCase(params.get("all").toString())
					&& params.containsKey("type")) {
				switch (params.get("type").toString()) {
				case "myOrder":
					Actions.forwardAction(request, response, Util.getActionUrl(request) + "/portalAll.shtml?type=my");
					return;
				case "draftOrder":
					Actions.forwardAction(request, response,
							Util.getActionUrl(request) + "/portalAll.shtml?type=draft");
					return;
				case "relateOrder":
					Actions.forwardAction(request, response,
							Util.getActionUrl(request) + "/portalAll.shtml?type=relate");
					return;
				case "closeOrder":
					Actions.forwardAction(request, response,
							Util.getActionUrl(request) + "/portalAll.shtml?type=close");
					return;

				case "myTask":
					Actions.forwardAction(request, response, Util.getActionUrl(request) + "/listTask.shtml?type=my");
					return;
				case "shareTask":
					Actions.forwardAction(request, response, Util.getActionUrl(request) + "/listTask.shtml?type=share");
					return;
				}
			}
		}

		// 所有订单
		Actions.forwardAction(request, response, Util.getActionUrl(request) + "/portalAll.shtml");
	}

	/**
	 * 订单分类列表
	 * 
	 * @param request
	 * @param response
	 */
	@ActionMode(Mode.FIT)
	public void main(HttpServletRequest request, HttpServletResponse response) {
		Config config = new Config(getTableConfig());
		request.setAttribute("config", config);

		HashMap<String, Object> params = RequestUtils.getJsonValue(request, Keys.PARAMS.toString());
		Integer pageLimit = (Integer) config.getTable().get("pageLimit");
		if (params != null) {
			if (params.containsKey("pageLimit")) {
				pageLimit = ((Number) params.get("pageLimit")).intValue();
			}
		}
		request.setAttribute("pageLimit", pageLimit);

		request.setAttribute(Keys.H5_JS.toString(), "amaze");// 视图使用amaze ui
		Actions.includePage(request, response, Util.getPagePath(request, "/flow/view/main.jsp"));
	}

	/**
	 * 个人订单
	 * 
	 * @param request
	 * @param response
	 */
	@ActionMode(Mode.FIT)
	public void portalPerson(HttpServletRequest request, HttpServletResponse response) {
		Config config = new Config(getTableConfig());
		request.setAttribute("config", config);
		HashMap<String, Object> params = RequestUtils.getJsonValue(request, Keys.PARAMS.toString());
		String title = (String) config.getTable().get("busiName");
		if (params != null) {
			if (params.containsKey("title")) {
				title = params.get("title").toString();
			}
		}
		request.setAttribute("title", title);

		request.setAttribute(Keys.H5_JS.toString(), "amaze");// 视图使用amaze ui
		Actions.includePage(request, response, Util.getPagePath(request, "/flow/view/portal_person.jsp"));
	}

	/**
	 * 所有订单
	 * 
	 * @param request
	 * @param response
	 */
	@ActionMode(Mode.FIT)
	public void portalAll(HttpServletRequest request, HttpServletResponse response) {
		Config config = new Config(getTableConfig());
		request.setAttribute("config", config);

		HashMap<String, Object> params = RequestUtils.getJsonValue(request, Keys.PARAMS.toString());
		String title = (String) config.getTable().get("busiName");
		if (params != null) {
			if (params.containsKey("title")) {
				title = params.get("title").toString();
			}
		}
		request.setAttribute("title", title);

		request.setAttribute(Keys.H5_JS.toString(), "amaze");// 视图使用amaze ui
		Actions.includePage(request, response, Util.getPagePath(request, "/flow/view/portal_all.jsp"));
	}

	/**
	 * 任务列表(独享待办)
	 * 
	 * @param request
	 * @param response
	 */
	@ActionMode(Mode.FIT)
	public void listTask(HttpServletRequest request, HttpServletResponse response) {
		Config config = new Config(getTableConfig());
		String pdKey = (String) config.getTable().get("pdKey");
		String type = RequestUtils.getStringValue(request, "type");// 任务类别

		// 获取分页信息
		int start = Util.getStart(request);
		int limit = Util.getLimit(request);
		DataPackage dp = FlowService.getInstance().listTaskPackage(
				FlowService.getInstance().createTaskQuery("share".equals(type), new String[] { pdKey }), start, limit);
		request.setAttribute("dp", dp);

		// 处理任务,获取相应vo等信息
		List<Map<String, Object>> list = new ArrayList<>();
		for (Object obj : dp.getList()) {
			Task task = (Task) obj;
			Map<String, Object> vo = new HashMap<>();
			ProcessInstance pi = FlowFactory.getRuntimeService().createProcessInstanceQuery()
					.processInstanceId(task.getProcessInstanceId()).includeProcessVariables().singleResult();

			// 当前节点展示
			vo.put("activity", FlowHelper.showActivity(task));
			vo.put("assignee", FlowHelper.showAssignee(task));
			vo.put("order", FlowHelper.getOrder(pi));

			vo.put("ordId", pi.getBusinessKey());
			vo.put("task", task);
			list.add(vo);
		}
		request.setAttribute("list", list);

		String title = (String) config.getTable().get("busiName");
		Util.setTitle(request, title);

		request.setAttribute(Keys.H5_JS.toString(), "amaze");// 视图使用amaze ui
		Actions.includePage(request, response, Util.getPagePath(request, "/flow/view/task_list.jsp"));
	}

	/**
	 * 获取任务数量
	 * 
	 * @param request
	 * @param response
	 */
	@ActionMode(Mode.FIT)
	public void countTask(HttpServletRequest request, HttpServletResponse response) {
		Config config = new Config(getTableConfig());
		String pdKey = (String) config.getTable().get("pdKey");
		String type = RequestUtils.getStringValue(request, "type");// 任务类别

		Map<String, Object> result = new HashMap<>();
		long count = FlowService.getInstance()
				.countTask(FlowService.getInstance().createTaskQuery("share".equals(type), pdKey));

		result.put("flag", true);
		result.put("count", count);

		Actions.showJson(request, response, result);
	}

	/**
	 * 订单列表
	 * 
	 * @param request
	 * @param response
	 */
	@ActionMode(Mode.FIT)
	public void list(HttpServletRequest request, HttpServletResponse response) {

		Map<String, Object> table = getTableConfig();
		Config config = new Config(table);
		Map<String, Object> params = RequestUtils.getJsonValue(request, Keys.PARAMS.toString());

		// 获取分页信息
		int start = Util.getStart(request);
		int limit = Util.getLimit(request);

		// 默认分页数
		Integer pageLimit = (Integer) config.getTable().get("pageLimit");
		if (params != null) {
			if (params.containsKey("pageLimit")) {
				pageLimit = ((Number) params.get("pageLimit")).intValue();
			}
		}
		request.setAttribute("pageLimit", pageLimit);
		if (StringUtils.isEmpty(RequestUtils.getStringValue(request, Keys.LIMIT.toString())) && pageLimit != null
				&& pageLimit > 0) {
			limit = pageLimit;
		}

		String field = Util.getSortField(request);
		String dir = Util.getSortDir(request);
		// 设置默认排序条件
		if (StringUtils.isEmpty(field)) {
			if (params != null && params.containsKey("field")) {
				field = params.get("field").toString();
				if (params.containsKey("dir")) {
					dir = params.get("dir").toString();
				}
			} else {
				field = (String) table.get("sortName");
				dir = (String) table.get("dir");
			}
		}

		// 查询条件
		DataCondition condition = new DataCondition(Util.buildQueryMap(new HashMap<String, Object>(), request));
		condition.setOrderBy(field, dir);

		// 高级查询
		for (Map<String, Object> query : ((Set<Map<String, Object>>) table.get("extQuerys"))) {
			String value = RequestUtils.getStringValue(request, "querys." + query.get("id"));
			if (StringUtils.isEmpty(value)) {
				continue;
			}
			Map<String, Object> context = new HashMap<String, Object>();// 构建上下文
			context.put("value", value);
			context.put("values", RequestUtils.getStringValues(request, "querys." + query.get("id")));
			ScriptTypes type = ScriptTypes.forCode((Integer) query.get("sqlType"));
			String sql = (String) ScriptHelper.evel(type, (String) query.get("sqlScript"), context);
			condition.addSql(sql);
		}

		// 数据约束
		for (Map<String, Object> dataLimit : ((Set<Map<String, Object>>) table.get("limits"))) {
			CmPri pri = (CmPri) dataLimit.get("pri");
			if (!SessionManager.check(pri)) {
				continue;
			}
			ScriptTypes type = ScriptTypes.forCode((Integer) dataLimit.get("sqlType"));
			String sql = (String) ScriptHelper.evel(type, (String) dataLimit.get("sqlScript"));
			condition.addSql(sql);
		}

		// 约束条件
		String type = RequestUtils.getStringValue(request, "type");
		if ("my".equals(type)) {// 我的订单
			// 我发起,进行中
			condition.setNumberEqual(OrderModelKeys.ORD_STATE.name(), "1");
			condition.setStringEqual(OrderModelKeys.OWNER.name(), SessionManager.getUser().getUid());
		} else if ("relate".equals(type)) {// 我的经办
			// 我经办,不是我发起
			condition.setStringNotEqual(OrderModelKeys.OWNER.name(), SessionManager.getUser().getUid());
			condition.addSql(OrderModelKeys.UIDS.name() + " like '%~" + SessionManager.getUser().getUid() + "~%'");
		} else if ("draft".equals(type)) {// 草稿箱
			// 我发起,未开始
			condition.setNumberEqual(OrderModelKeys.ORD_STATE.name(), "0");// 未开始
			condition.setStringEqual(OrderModelKeys.OWNER.name(), SessionManager.getUser().getUid());
		} else if ("close".equals(type)) {// 我的已完成
			// 我发起,已完成
			condition.setNumberIn(OrderModelKeys.ORD_STATE.name(), "2", "3");
			condition.setStringEqual(OrderModelKeys.OWNER.name(), SessionManager.getUser().getUid());
		}

		DataPackage dp = ORMAdapterService.getInstance().queryPackage((String) table.get("tableName"), start, limit,
				condition.toEntity());

		// 设置到页面
		request.setAttribute("dp", dp);

		request.setAttribute("config", config);

		String title = (String) config.getTable().get("busiName");
		if (params != null) {
			if (params.containsKey("title")) {
				title = params.get("title").toString();
			}
		}
		Util.setTitle(request, title);

		request.setAttribute(Keys.H5_JS.toString(), "amaze");// 视图使用amaze ui
		Actions.includePage(request, response, Util.getPagePath(request, "/flow/view/list.jsp"));
	}

	/**
	 * 获取任务数量
	 * 
	 * @param request
	 * @param response
	 */
	@ActionMode(Mode.FIT)
	public void countOrder(HttpServletRequest request, HttpServletResponse response) {
		Map<String, Object> table = getTableConfig();
		String type = RequestUtils.getStringValue(request, "type");// 任务类别

		Map<String, Object> result = new HashMap<>();
		long count;
		// 查询条件
		DataCondition condition = new DataCondition(Util.buildQueryMap(new HashMap<String, Object>(), request));

		// 约束条件
		if ("my".equals(type)) {// 我的订单
			// 我发起,进行中
			condition.setNumberEqual(OrderModelKeys.ORD_STATE.name(), "1");
			condition.setStringEqual(OrderModelKeys.OWNER.name(), SessionManager.getUser().getUid());
		} else if ("relate".equals(type)) {// 我的经办
			// 我经办,不是我发起
			condition.setStringNotEqual(OrderModelKeys.OWNER.name(), SessionManager.getUser().getUid());
			condition.addSql(OrderModelKeys.UIDS.name() + " like '%~" + SessionManager.getUser().getUid() + "~%'");
		} else if ("draft".equals(type)) {// 草稿箱
			// 我发起,未开始
			condition.setNumberEqual(OrderModelKeys.ORD_STATE.name(), "0");// 未开始
			condition.setStringEqual(OrderModelKeys.OWNER.name(), SessionManager.getUser().getUid());
		} else if ("close".equals(type)) {// 我的已完成
			// 我发起,已完成
			condition.setNumberIn(OrderModelKeys.ORD_STATE.name(), "2", "3");
			condition.setStringEqual(OrderModelKeys.OWNER.name(), SessionManager.getUser().getUid());
		} else {
			throw new SystemRuntimeException(ExceptionType.BUSINESS, "缺少查询类型.");
		}

		count = ORMAdapterService.getInstance().getCount((String) table.get("tableName"), condition.toEntity());

		result.put("flag", true);
		result.put("count", count);

		Actions.showJson(request, response, result);
	}

	/**
	 * 领取任务
	 * 
	 * @param request
	 * @param response
	 */
	@ActionMode(Mode.FIT)
	public void claimTask(HttpServletRequest request, HttpServletResponse response) {
		FlowObject fo = FlowObject.create(request);
		FlowService.getInstance().executeClaimWithConfig(viewKey, fo.getTaskId(), SessionManager.getUser().getUid());

		Map<String, Object> result = new HashMap<>();
		result.put("msg", "领取任务成功.");
		Actions.showJson(request, response, result);
	}

	/**
	 * 删除草稿箱订单
	 * 
	 * @param request
	 * @param response
	 */
	@ActionMode(Mode.FIT)
	public void removeOrder(HttpServletRequest request, HttpServletResponse response) {
		Config config = new Config(getTableConfig());
		FlowObject fo = FlowObject.create(request);

		String ordId = fo.getOrdId();
		Map<String, Object> po = (Map<String, Object>) ORMAdapterService.getInstance()
				.findByPk((String) config.getTable().get("tableName"), ordId);

		if (po == null) {
			throw new SystemRuntimeException(ExceptionType.FLOW_ORD_NOT_FOUND);
		}

		Integer ordState = (Integer) po.get(OrderModelKeys.ORD_STATE.name());
		if (ordState.intValue() != 0) {
			throw new SystemRuntimeException(ExceptionType.BUSINESS, "订单[" + ordId + "]已启动,无法删除.");
		}

		// 删除
		ORMAdapterService.getInstance().remove(po);

		Map<String, Object> result = new HashMap<>();
		result.put("msg", "暂存订单[" + ordId + "]已删除.");
		Actions.showJson(request, response, result);
	}

	/**
	 * 直接跳转表单(用于直接启动流程)
	 * 
	 * @param request
	 * @param response
	 */
	@ActionMode(Mode.FIT)
	public void directForm(HttpServletRequest request, HttpServletResponse response) {
		FlowObject fo = FlowObject.create(request);
		Map<String, Object> json = RequestUtils.getJsonValue(request, Keys.PARAMS.toString());
		request.setAttribute("json", json);
		request.setAttribute("fo", fo);
		request.setAttribute("ordKeys", OrderModelKeys.values());

		request.setAttribute(Keys.H5_JS.toString(), "amaze");// 视图使用amaze ui
		Actions.includePage(request, response, Util.getPagePath(request, "/flow/view/direct_form.jsp"));
	}

	/**
	 * 带审批意见的确认框
	 * 
	 * @param request
	 * @param response
	 */
	@ActionMode(Mode.FIT)
	public void confirmWithOpinion(HttpServletRequest request, HttpServletResponse response) {
		String flowId = RequestUtils.getStringValue(request, "flowId");
		String activityId = RequestUtils.getStringValue(request, "activityId");
		String pdId = RequestUtils.getStringValue(request, "pdId");
		Map<String, Object> btn;

		if (StringUtils.isNotEmpty(flowId)) {
			btn = (Map<String, Object>) ORMService.getInstance().findHQL(
					"from WfUserTaskBtn where pdId = ? and activityId = ? and flowId = ?", pdId, activityId, flowId);
		} else {// 新增
			btn = (Map<String, Object>) ORMService.getInstance().findHQL("from WfStartEventBtnStart where pdId = ?",
					pdId);
		}

		if (btn == null) {
			throw new SystemRuntimeException(ExceptionType.CODING,
					"找不到按钮[" + pdId + "][" + activityId + "][" + flowId + "]");
		}
		request.setAttribute("btn", btn);

		String quickOpinionScript = (String) btn.get("quickOpinionScript");
		if (StringUtils.isNotEmpty(quickOpinionScript)) {
			Object quickOpinion = ScriptHelper.evel(ScriptTypes.forCode((Integer) btn.get("quickOpinionType")),
					quickOpinionScript);
			if (quickOpinion instanceof String) {// 返回字符串
				request.setAttribute("opinions", StringUtils.split((String) quickOpinion, ";"));
			} else if (quickOpinion instanceof Collection || quickOpinion instanceof Object[]) {
				request.setAttribute("opinions", quickOpinion);
			}
		}

		request.setAttribute(Keys.H5_JS.toString(), "amaze");// 视图使用amaze ui
		Actions.includePage(request, response, Util.getPagePath(request, "/flow/view/opinion_win.jsp"));
	}

	/**
	 * 转办窗口
	 * 
	 * @param request
	 * @param response
	 */
	@ActionMode(Mode.FIT)
	public void forwardWin(HttpServletRequest request, HttpServletResponse response) {
		String activityId = RequestUtils.getStringValue(request, "activityId");
		String pdId = RequestUtils.getStringValue(request, "pdId");
		String btnKey = RequestUtils.getStringValue(request, "btnKey");
		Map<String, Object> btn = (Map<String, Object>) ORMService.getInstance().findHQL(
				"from WfUserTaskBtnForward where pdId = ? and activityId = ? and btnKey = ?", pdId, activityId, btnKey);

		if (btn == null) {
			throw new SystemRuntimeException(ExceptionType.CODING,
					"找不到转办按钮[" + pdId + "][" + activityId + "][" + btnKey + "]");
		}

		Config config = new Config(getTableConfig());
		FlowObject fo = FlowObject.create(request);
		String tableName = (String) config.getTable().get("tableName");
		if (StringUtils.isEmpty(fo.getTaskId())) {
			throw new SystemRuntimeException(ExceptionType.BUSINESS, "任务[" + fo.getTaskId() + "]不存在.");
		}
		Task task = FlowFactory.getTaskService().createTaskQuery().taskId(fo.getTaskId()).singleResult();
		ProcessInstance pi = FlowFactory.getRuntimeService().createProcessInstanceQuery()
				.processInstanceId(task.getProcessInstanceId()).singleResult();
		fo.setOrdId(pi.getBusinessKey());

		Map<String, Object> vo = (Map<String, Object>) ORMAdapterService.getInstance().findByPk(tableName,
				fo.getOrdId());
		if (vo == null) {
			throw new SystemRuntimeException(ExceptionType.BUSINESS, "订单[" + fo.getOrdId() + "]不存在.");
		}

		request.setAttribute("fo", fo);
		request.setAttribute("vo", vo);
		request.setAttribute("btn", btn);

		String quickOpinionScript = (String) btn.get("quickOpinionScript");
		if (StringUtils.isNotEmpty(quickOpinionScript)) {
			Object quickOpinion = ScriptHelper.evel(ScriptTypes.forCode((Integer) btn.get("quickOpinionType")),
					quickOpinionScript);
			if (quickOpinion instanceof String) {// 返回字符串
				request.setAttribute("opinions", StringUtils.split((String) quickOpinion, ";"));
			} else if (quickOpinion instanceof Collection || quickOpinion instanceof Object[]) {
				request.setAttribute("opinions", quickOpinion);
			}
		}
		request.setAttribute(Keys.H5_JS.toString(), "amaze");// 视图使用amaze ui
		Actions.includePage(request, response, Util.getPagePath(request, "/flow/view/forward_win.jsp"));
	}

	/**
	 * 转办
	 * 
	 * @param request
	 * @param response
	 */
	@ActionMode(Mode.FIT)
	public void submitForward(HttpServletRequest request, HttpServletResponse response) {
		FlowObject fo = FlowObject.create(request);
		String uid = RequestUtils.getStringValue(request, "_FORWARD_UID");
		FlowService.getInstance().executeForwardWithConfig(viewKey, fo.getTaskId(), uid,
				RequestUtils.getStringValue(request, "btnName"), RequestUtils.getStringValue(request, "_OPINION"));
		Actions.redirectInfoPage(request, response, "转发成功.");
	}

	/**
	 * 表单页
	 * 
	 * @param request
	 * @param response
	 */
	@ActionMode(Mode.FIT)
	@ActionAccess(level = SafeLevel.BUSI_W)
	public void form(HttpServletRequest request, HttpServletResponse response) {
		Config config = new Config(getTableConfig());

		FlowObject fo = FlowObject.create(request);
		Task task = null;

		if (StringUtils.isNotEmpty(fo.getTaskId())) {// 根据taskId找到ordId
			task = FlowFactory.getTaskService().createTaskQuery().taskId(fo.getTaskId()).singleResult();
			ProcessInstance pi = FlowFactory.getRuntimeService().createProcessInstanceQuery()
					.processInstanceId(task.getProcessInstanceId()).singleResult();
			fo.setOrdId(pi.getBusinessKey());
		}

		String tableName = (String) config.getTable().get("tableName");
		String ordId = fo.getOrdId();// 获取订单号
		Map<String, Object> vo = null;// 订单对象
		ProcessDefinition pd;// 流程定义
		if (StringUtils.isNotEmpty(ordId)) {// 获取得到订单号
			vo = (Map<String, Object>) ORMAdapterService.getInstance().findByPk(tableName, ordId);
			request.setAttribute("vo", vo);
			pd = FlowFactory.getRepositoryService().getProcessDefinition((String) vo.get(OrderModelKeys.PD_ID.name()));

			// 根据订单号找到任务
			if (task == null) {
				List<Task> tasks = FlowFactory.getTaskService().createTaskQuery().processDefinitionId(pd.getId())
						.processInstanceBusinessKey(ordId).orderByTaskCreateTime().asc().list();
				if (tasks != null) {
					for (Task t : tasks) {
						if (FlowService.getInstance().checkTask(t)) {
							task = t;
							fo.setTaskId(task.getId());
							break;
						}
					}
				}
			}
		} else {
			// 不需要传pdKey
			pd = FlowService.getInstance().getPd((String) config.getTable().get("pdKey"), 0);
		}
		fo.setPdId(pd.getId());
		request.setAttribute("pd", pd);

		// 自定义表单
		List<Map<String, Object>> columns = new ArrayList<>();
		// 按钮
		List<Map<String, Object>> btns = new ArrayList<>();
		// 子表
		List<Map<String, Object>> subs = new ArrayList<>();
		BpmnHelper.Node node;// 当前节点

		// 已有流程
		if (task != null) {
			Execution execution = FlowFactory.getRuntimeService().createExecutionQuery()
					.executionId(task.getExecutionId()).singleResult();

			request.setAttribute("task", task);
			request.setAttribute("execution", execution);

			// 初始化按钮列表
			BpmnModel bpmnModel = FlowFactory.getRepositoryService()
					.getBpmnModel((String) vo.get(OrderModelKeys.PD_ID.name()));
			node = BpmnHelper.getNode(bpmnModel, execution.getActivityId());
			fo.setActivityId(node.getId());
			fo.setActivityName(node.getName());

			Map<String, Object> taskConfig = (Map<String, Object>) ORMService.getInstance().findHQL(
					"from WfUserTask where pdId = ? and activityId = ?", task.getProcessDefinitionId(), node.getId());
			if (taskConfig != null) {
				request.setAttribute("taskConfig", taskConfig);
			} else {
				throw new SystemRuntimeException(ExceptionType.FLOW_CONFIG_ERROR, "未配置任务表单.");
			}
			request.setAttribute("jsType", taskConfig.get("jsType"));
			request.setAttribute("jsScript", taskConfig.get("jsScript"));

			List<SequenceFlow> sequenceFlows = node.getSequenceFlows();
			int defaultSort = 1000;// 默认排序
			if (sequenceFlows != null) {
				for (SequenceFlow flow : sequenceFlows) {
					Map<String, Object> btn = (Map<String, Object>) ORMService.getInstance().findHQL(
							"from WfUserTaskBtn where pdId = ? and activityId = ? and flowId = ?",
							task.getProcessDefinitionId(), node.getId(), flow.getId());
					if (btn == null) {
						btn = new HashMap<>();
						btn.put("flowId", flow.getId());
						btn.put("busiName", flow.getName());
						btn.put("icon", "check");
						btn.put("checkScript", null);
						btn.put("enabledTipScript", null);
						btn.put("disabledTipScript", null);
						btn.put("sort", defaultSort++);
					}
					btn.put("sequenceFlowName", flow.getName());
					btns.add(btn);
				}
			}

			// 转办按钮
			{
				List<Map<String, Object>> btnList = ORMService.getInstance().queryHQL(
						"from WfUserTaskBtnForward where pdId = ? and activityId = ?", task.getProcessDefinitionId(),
						node.getId());
				if (btnList != null) {
					for (Map<String, Object> btn : btnList) {
						btn.put("name", "forward");
						btns.add(btn);
					}
				}
			}

			// 保存按钮
			{
				Map<String, Object> btn = (Map<String, Object>) ORMService.getInstance().findHQL(
						"from WfUserTaskBtnSave where pdId = ? and activityId = ?", task.getProcessDefinitionId(),
						node.getId());
				if (btn != null) {
					btn.put("name", "save");
					btns.add(btn);
				}
			}

			// 自定义表单
			columns.addAll(
					ORMService.getInstance().queryHQL("from WfUserTaskColumnForm where pdId = ? and activityId = ?",
							task.getProcessDefinitionId(), node.getId()));
			columns.addAll(
					ORMService.getInstance().queryHQL("from WfUserTaskColumnLine where pdId = ? and activityId = ?",
							task.getProcessDefinitionId(), node.getId()));
			for (Map<String, Object> o : config.getFields()) {
				String pixelKey = (String) o.get("pixelKey");
				Map<String, Object> extendVO = null;
				if (StringUtils.isNotEmpty(pixelKey)) {
					extendVO = (Map<String, Object>) ORMService.getInstance().findHQL(
							"from WfUserTaskColumnExtend where pdId = ? and activityId = ? and pixelKey = ?",
							task.getProcessDefinitionId(), node.getId(), pixelKey);
				}

				if (extendVO == null || 1 != (int) extendVO.get("showFlag")) {
					logger.debug("字段[" + o.get("busiName") + "]不需要展示");
					continue;
				}
				o.put("sort", extendVO.get("sort"));
				columns.add(o);
			}
		} else {// 未有流程,调用开始节点配置
			// 初始化按钮列表
			BpmnModel bpmnModel = FlowFactory.getRepositoryService().getBpmnModel(pd.getId());
			node = BpmnHelper.getNode(bpmnModel, NodeType.START_EVENT);
			fo.setActivityId(node.getId());
			fo.setActivityName(node.getName());

			Map<String, Object> startConfig = (Map<String, Object>) ORMService.getInstance().findByPk("WfStartEvent",
					pd.getId());
			if (startConfig != null) {
				request.setAttribute("startConfig", startConfig);
			} else {
				throw new SystemRuntimeException(ExceptionType.FLOW_CONFIG_ERROR, "未配置开始表单.");
			}
			request.setAttribute("jsType", startConfig.get("jsType"));
			request.setAttribute("jsScript", startConfig.get("jsScript"));

			columns.addAll(ORMService.getInstance().queryHQL("from WfStartEventColumnForm where pdId = ?", pd.getId()));
			columns.addAll(ORMService.getInstance().queryHQL("from WfStartEventColumnLine where pdId = ?", pd.getId()));
			for (Map<String, Object> o : config.getFields()) {
				String pixelKey = (String) o.get("pixelKey");
				Map<String, Object> extendVO = null;
				if (StringUtils.isNotEmpty(pixelKey)) {
					extendVO = (Map<String, Object>) ORMService.getInstance().findHQL(
							"from WfStartEventColumnExtend where pdId = ? and pixelKey = ?", pd.getId(), pixelKey);
				}

				if (extendVO == null || ((Integer) extendVO.get("showFlag")).intValue() != 1) {
					logger.debug("字段[" + o.get("busiName") + "]不需要展示");
					continue;
				}

				o.put("sort", extendVO.get("sort"));
				if (vo == null) {// 新增时,表单内容脚本使用开始节点配置
					o.put("contentType", extendVO.get("contentType"));
					o.put("contentScript", extendVO.get("contentScript"));
				}
				columns.add(o);
			}

			// 开始按钮
			{
				Map<String, Object> btn = (Map<String, Object>) ORMService.getInstance()
						.findHQL("from WfStartEventBtnStart where pdId = ?", pd.getId());
				if (btn != null) {
					btn.put("name", "start");
					btns.add(btn);
				}
			}

			// 保存按钮
			{
				Map<String, Object> btn = (Map<String, Object>) ORMService.getInstance()
						.findHQL("from WfStartEventBtnSave where pdId = ?", pd.getId());
				if (btn != null) {
					btn.put("name", "save");
					btns.add(btn);
				}
			}
		}

		// 子表数据
		{
			for (Map<String, Object> o : config.getSubs()) {
				Map<String, Object> extendVO = null;
				if (node.getNodeType().equals(NodeType.USER_TASK)) {// 任务节点
					extendVO = (Map<String, Object>) ORMService.getInstance().findHQL(
							"from WfUserTaskSubExtend where pdId = ? and activityId = ? and subKey = ?", fo.getPdId(),
							fo.getActivityId(), o.get("subKey"));
				} else if (node.getNodeType().equals(NodeType.START_EVENT)) {// 开始节点
					extendVO = (Map<String, Object>) ORMService.getInstance().findHQL(
							"from WfStartEventSubExtend where pdId = ? and subKey = ?", fo.getPdId(), o.get("subKey"));
				}

				if (extendVO == null || ((Integer) extendVO.get("showFlag")) != 1) {
					continue;
				}

				o.put("sort", extendVO.get("sort"));
				o.put("pri", null);
				subs.add(o);
			}
			// 子表排序
			Collections.sort(subs, new Comparator<Map<String, Object>>() {
				@Override
				public int compare(Map<String, Object> o1, Map<String, Object> o2) {
					return (int) o1.get("sort") < (int) o2.get("sort") ? -1 : 1;
				}
			});
		}

		// "查看节点"
		if (vo != null) {
			// 获取历史表
			WfPd wfPd = (WfPd) ORMService.getInstance().findByPk(WfPd.class.getName(), fo.getPdId());
			if (wfPd == null) {
				throw new SystemRuntimeException(ExceptionType.CONFIG, "流程[" + fo.getPdId() + "]已被删除,请联系管理员.");
			}
			Map<String, Object> basicView = (Map<String, Object>) ORMService.getInstance().findByPk("VwFlowBasic",
					wfPd.getBasicViewKey());
			if (basicView == null) {
				throw new SystemRuntimeException(ExceptionType.CONFIG,
						"视图[" + wfPd.getBasicViewKey() + "]已被删除,请联系管理员.");
			}
			String historyTableName = (String) basicView.get("historyTableName");
			List<Map<String, Object>> historyList = new ArrayList<>();
			if (StringUtils.isNotEmpty(historyTableName)) {
				// 查找是否存在当前用户处理过的历史节点
				List<Map<String, Object>> list = JdbcService.getInstance().querySQL("select "
						+ OrderHistoryModelKeys.ACTIVITY_ID.name() + "," + OrderHistoryModelKeys.ACTIVITY_NAME.name()
						+ " from " + historyTableName + " where " + OrderHistoryModelKeys.TASK_UID.name() + " = ? and "
						+ OrderHistoryModelKeys.ORD_ID.name() + " = ? and " + OrderHistoryModelKeys.NODE_TYPE.name()
						+ " in ('" + NodeType.START_EVENT.name() + "','" + NodeType.USER_TASK.name() + "') order by "
						+ OrderHistoryModelKeys.TASK_END_DATE + " asc", SessionManager.getUser().getUid(), ordId);
				for (Map<String, Object> o : list) {
					if (!historyList.contains(o)) {
						historyList.add(o);
					}
				}
			}

			// 通过订单找到我的任务
			List<Task> tasks = FlowFactory.getTaskService().createTaskQuery().includeTaskLocalVariables()
					.processDefinitionId((String) vo.get(OrderModelKeys.PD_ID.name()))
					.processInstanceBusinessKey((String) vo.get(OrderModelKeys.ORD_ID.getColumn().getName()))
					.orderByTaskCreateTime().asc().list();
			for (Task t : tasks) {
				if (FlowHelper.checkTask(t)) {// 当前任务属于我
					Map<String, Object> o = new HashMap<>();
					Execution e = FlowFactory.getRuntimeService().createExecutionQuery().executionId(t.getExecutionId())
							.singleResult();
					o.put(OrderHistoryModelKeys.ACTIVITY_ID.name(), e.getActivityId());
					o.put(OrderHistoryModelKeys.ACTIVITY_NAME.name(), t.getName());
					if (!historyList.contains(o)) {
						historyList.add(o);
					}
				}
			}
			request.setAttribute("historyList", historyList);
		}

		// 按钮排序
		Collections.sort(btns, new Comparator<Map<String, Object>>() {
			@Override
			public int compare(Map<String, Object> o1, Map<String, Object> o2) {
				return (int) o1.get("sort") < (int) o2.get("sort") ? -1 : 1;
			}
		});
		request.setAttribute("btns", btns);

		// 字段排序
		Collections.sort(columns, new Comparator<Map<String, Object>>() {
			@Override
			public int compare(Map<String, Object> o1, Map<String, Object> o2) {
				return (int) o1.get("sort") < (int) o2.get("sort") ? -1 : 1;
			}
		});

		// h5 columns
		// 写入begin和end
		Map<String, Object> current = null;
		int i = 0;
		for (Map<String, Object> o : columns) {
			if (o.get("whole") != null && current != null) {
				current.put("form_end", i);
			}

			if (o.get("whole") == null) {
				current = o;
				current.put("form_begin", i + 1);
			}

			i++;
		}
		request.setAttribute("columns", columns);
		request.setAttribute("subs", subs);

		String title;
		if (vo == null) {
			title = pd.getName() + "[新建]";
		} else if (task != null) {
			title = pd.getName() + "[" + vo.get(OrderModelKeys.ORD_ID.name()) + "]:" + task.getName();
		} else {
			title = pd.getName() + "[" + vo.get(OrderModelKeys.ORD_ID.name()) + "]";
		}
		Util.setTitle(request, title);

		request.setAttribute("ordKeys", OrderModelKeys.values());
		request.setAttribute("config", config);
		request.setAttribute("fo", fo);

		request.setAttribute(Keys.H5_JS.toString(), "amaze");// 视图使用amaze ui
		Actions.includePage(request, response, Util.getPagePath(request, "/flow/view/form.jsp"));
	}

	/**
	 * PO前置处理
	 * 
	 * @param po
	 * @param fo
	 * @param formColumns
	 */
	private void preparePO(Map<String, Object> po, FlowObject fo, Collection<Map<String, Object>> formColumns) {
		// 调用字段后置处理器
		for (Map<String, Object> column : formColumns) {
			String execScript = (String) column.get("execScript");
			Integer execType = (Integer) column.get("execType");
			if (StringUtils.isEmpty(execScript) || execType == null) {
				continue;
			}

			// 需要前置粗粒
			Map<String, Object> context = new HashMap<>();
			logger.debug("前置处理[" + column.get("name") + "]时VO值:" + JsonMapper.defaultMapper().toJson(po));
			context.put("vo", po);
			context.put("fo", fo);
			context.put("mode", StringUtils.isEmpty(fo.getOrdId()) ? 1 : 2);
			Object value = ScriptHelper.evel(ScriptTypes.forCode(execType), execScript, context);
			po.put((String) column.get("name"), value);
			if (logger.isDebugEnabled()) {
				logger.debug("前置处理[" + column.get("name") + "]后VO值:" + JsonMapper.defaultMapper().toJson(po));
			}
		}
	}

	/**
	 * 表单提交
	 * 
	 * @param request
	 * @param response
	 */
	@ActionAccess(level = SafeLevel.BUSI_W)
	public void submit(HttpServletRequest request, HttpServletResponse response) {
		Config config = new Config(getTableConfig());
		FlowObject fo = FlowObject.create(request);

		String ordId = fo.getOrdId();
		Map<String, Object> po;
		String pdId;

		// 获取原数据
		if (StringUtils.isNotEmpty(ordId)) {
			po = (Map<String, Object>) ORMAdapterService.getInstance()
					.findByPk((String) config.getTable().get("tableName"), ordId);
			pdId = (String) po.get(OrderModelKeys.PD_ID.getColumn().getName());
		} else {
			po = new HashMap<String, Object>();
			pdId = FlowService.getInstance().getPd((String) config.getTable().get("pdKey"), 0).getId();
		}

		// 获取表单数值
		CommonHelper.map(po, (String) config.getTable().get("tableName"));

		// 字段处理器
		List<Map<String, Object>> formColumns = new ArrayList<>();
		if (StringUtils.isNotEmpty(fo.getTaskId())) {// 有任务,节点前置处理器
			// 自定义表单
			formColumns.addAll(ORMService.getInstance()
					.queryHQL("from WfUserTaskColumnForm where pdId = ? and activityId = ?", pdId, fo.getActivityId()));
		} else {
			// 自定义表单
			formColumns.addAll(ORMService.getInstance().queryHQL("from WfStartEventColumnForm where pdId = ?", pdId));
		}
		preparePO(po, fo, formColumns);

		// 审批意见
		String opinion = RequestUtils.getStringValue(request, "_OPINION");

		// 提交
		FlowService.getInstance().executeFlowWithConfig(viewKey, po, fo, opinion);

		// 处理成功,返回json
		Map<String, Object> result = new HashMap<>();
		result.put("ordId", fo.getOrdId());
		result.put("activityId", fo.getActivityId());
		Actions.showJson(request, response, result);
	}

	/**
	 * 订单明细查看
	 * 
	 * @param request
	 * @param response
	 */
	@ActionMode(Mode.FIT)
	public void detail(HttpServletRequest request, HttpServletResponse response) {
		Config config = new Config(getTableConfig());
		FlowObject fo = FlowObject.create(request);

		Task task = null;
		if (StringUtils.isNotEmpty(fo.getTaskId())) {// 根据taskId找到ordId
			task = FlowFactory.getTaskService().createTaskQuery().taskId(fo.getTaskId()).singleResult();
			if (task != null) {
				ProcessInstance pi = FlowFactory.getRuntimeService().createProcessInstanceQuery()
						.processInstanceId(task.getProcessInstanceId()).singleResult();
				fo.setOrdId(pi.getBusinessKey());
				if (StringUtils.isEmpty(fo.getActivityId())) {
					Execution execution = FlowFactory.getRuntimeService().createExecutionQuery()
							.executionId(task.getExecutionId()).singleResult();
					fo.setActivityId(execution.getActivityId());
				}
			}
		}

		String ordId = fo.getOrdId();// 获取订单号
		if (StringUtils.isEmpty(ordId)) {// 获取得到订单号
			throw new SystemRuntimeException(ExceptionType.FLOW_ORD_NOT_FOUND, "订单不存在.");
		}

		String tableName = (String) config.getTable().get("tableName");
		Map<String, Object> vo = (Map<String, Object>) ORMAdapterService.getInstance().findByPk(tableName, ordId);
		if (vo == null) {
			throw new SystemRuntimeException(ExceptionType.FLOW_ORD_NOT_FOUND, "订单不存在.");
		}

		ProcessDefinition pd = FlowFactory.getRepositoryService()
				.getProcessDefinition((String) vo.get(OrderModelKeys.PD_ID.name()));
		request.setAttribute("pd", pd);
		fo.setPdId(pd.getId());

		boolean ordFlag = false;// 是否订单展示界面
		HashMap<String, Object> params = RequestUtils.getJsonValue(request, Keys.PARAMS.toString());
		if (params != null && params.get("ordFlag") != null) {
			if ("true".equalsIgnoreCase(params.get("ordFlag").toString())) {
				ordFlag = true;
			} else if (Integer.valueOf(params.get("ordFlag").toString()) == 1) {
				ordFlag = true;
			}
		}
		if (RequestUtils.getIntegerValue(request, "ordFlag") == 1) {
			ordFlag = true;
		}
		request.setAttribute("ordFlag", ordFlag);
		List<Map<String, Object>> columns;
		List<Map<String, Object>> subs;

		if (!ordFlag) {
			if (!FlowHelper.checkActivityShow(vo, fo.getActivityId())) {
				throw new SystemRuntimeException(ExceptionType.FLOW, "您没有订单[" + ordId + "]的查看权限.");
			}

			// 名字
			BpmnModel bpmnModel = FlowFactory.getRepositoryService().getBpmnModel(pd.getId());
			Node node;
			if (StringUtils.isNotEmpty(fo.getActivityId())) {
				node = BpmnHelper.getNode(bpmnModel, fo.getActivityId());
			} else {
				node = BpmnHelper.getNode(bpmnModel, NodeType.START_EVENT);
				fo.setActivityId(node.getId());
			}
			fo.setActivityName(node.getName());

			// 自定义表单
			columns = new ArrayList<>();
			subs = new ArrayList<>();

			if (node.getNodeType().equals(NodeType.USER_TASK)) {// 用户节点
				columns.addAll(ORMService.getInstance().queryHQL(
						"from WfUserTaskColumnForm where pdId = ? and activityId = ?", pd.getId(), node.getId()));
				columns.addAll(ORMService.getInstance().queryHQL(
						"from WfUserTaskColumnLine where pdId = ? and activityId = ?", pd.getId(), node.getId()));
				for (Map<String, Object> o : config.getFields()) {
					String pixelKey = (String) o.get("pixelKey");
					Map<String, Object> extendVO = null;
					if (StringUtils.isNotEmpty(pixelKey)) {
						extendVO = (Map<String, Object>) ORMService.getInstance().findHQL(
								"from WfUserTaskColumnExtend where pdId = ? and activityId = ? and pixelKey = ?",
								pd.getId(), node.getId(), pixelKey);
					}

					if (extendVO == null || 1 != (int) extendVO.get("showFlag")) {
						logger.debug("字段[" + o.get("busiName") + "]不需要展示");
						continue;
					}
					o.put("sort", extendVO.get("sort"));
					columns.add(o);
				}
			} else if (node.getNodeType().equals(NodeType.START_EVENT)) {
				columns.addAll(
						ORMService.getInstance().queryHQL("from WfStartEventColumnForm where pdId = ?", pd.getId()));
				columns.addAll(
						ORMService.getInstance().queryHQL("from WfStartEventColumnLine where pdId = ?", pd.getId()));
				for (Map<String, Object> o : config.getFields()) {
					String pixelKey = (String) o.get("pixelKey");
					Map<String, Object> extendVO = null;
					if (StringUtils.isNotEmpty(pixelKey)) {
						extendVO = (Map<String, Object>) ORMService.getInstance().findHQL(
								"from WfStartEventColumnExtend where pdId = ? and pixelKey = ?", pd.getId(), pixelKey);
					}

					if (extendVO == null || ((Integer) extendVO.get("showFlag")).intValue() != 1) {
						logger.debug("字段[" + o.get("busiName") + "]不需要展示");
						continue;
					}

					o.put("sort", extendVO.get("sort"));
					columns.add(o);
				}
			} else {
				throw new SystemRuntimeException(ExceptionType.CODING,
						"节点[" + fo.getActivityId() + ":" + fo.getActivityName() + "]不存在.");
			}

			// 字段排序
			Collections.sort(columns, new Comparator<Map<String, Object>>() {
				@Override
				public int compare(Map<String, Object> o1, Map<String, Object> o2) {
					return (int) o1.get("sort") < (int) o2.get("sort") ? -1 : 1;
				}
			});

			// 子表
			for (Map<String, Object> o : config.getSubs()) {
				Map<String, Object> extendVO = null;
				if (node.getNodeType().equals(NodeType.USER_TASK)) {// 任务节点
					extendVO = (Map<String, Object>) ORMService.getInstance().findHQL(
							"from WfUserTaskSubExtend where pdId = ? and activityId = ? and subKey = ?", fo.getPdId(),
							fo.getActivityId(), o.get("subKey"));
				} else if (node.getNodeType().equals(NodeType.START_EVENT)) {// 开始节点
					extendVO = (Map<String, Object>) ORMService.getInstance().findHQL(
							"from WfStartEventSubExtend where pdId = ? and subKey = ?", fo.getPdId(), o.get("subKey"));
				}

				if (extendVO == null || ((Integer) extendVO.get("showFlag")) != 1) {
					continue;
				}

				o.put("sort", extendVO.get("sort"));
				o.put("pri", null);
				subs.add(o);
			}
			// 子表排序
			Collections.sort(subs, new Comparator<Map<String, Object>>() {
				@Override
				public int compare(Map<String, Object> o1, Map<String, Object> o2) {
					return (int) o1.get("sort") < (int) o2.get("sort") ? -1 : 1;
				}
			});

			// 获取历史表
			WfPd wfPd = (WfPd) ORMService.getInstance().findByPk(WfPd.class.getName(), fo.getPdId());
			if (wfPd == null) {
				throw new SystemRuntimeException(ExceptionType.CONFIG, "流程[" + fo.getPdId() + "]已被删除,请联系管理员.");
			}
			Map<String, Object> basicView = (Map<String, Object>) ORMService.getInstance().findByPk("VwFlowBasic",
					wfPd.getBasicViewKey());
			if (basicView == null) {
				throw new SystemRuntimeException(ExceptionType.CONFIG,
						"视图[" + wfPd.getBasicViewKey() + "]已被删除,请联系管理员.");
			}
			String historyTableName = (String) basicView.get("historyTableName");
			List<Map<String, Object>> historyList = new ArrayList<>();
			if (StringUtils.isNotEmpty(historyTableName)) {
				// 查找是否存在当前用户处理过的历史节点
				List<Map<String, Object>> list = JdbcService.getInstance().querySQL("select "
						+ OrderHistoryModelKeys.ACTIVITY_ID.name() + "," + OrderHistoryModelKeys.ACTIVITY_NAME.name()
						+ " from " + historyTableName + " where " + OrderHistoryModelKeys.TASK_UID.name() + " = ? and "
						+ OrderHistoryModelKeys.ORD_ID.name() + " = ? and " + OrderHistoryModelKeys.NODE_TYPE.name()
						+ " in ('" + NodeType.START_EVENT.name() + "','" + NodeType.USER_TASK.name() + "') order by "
						+ OrderHistoryModelKeys.TASK_END_DATE + " asc", SessionManager.getUser().getUid(), ordId);
				for (Map<String, Object> o : list) {
					if (!historyList.contains(o)) {
						historyList.add(o);
					}
				}
			}

			// 通过订单找到我的任务
			List<Task> tasks = FlowFactory.getTaskService().createTaskQuery().includeTaskLocalVariables()
					.processDefinitionId((String) vo.get(OrderModelKeys.PD_ID.name()))
					.processInstanceBusinessKey((String) vo.get(OrderModelKeys.ORD_ID.getColumn().getName()))
					.orderByTaskCreateTime().asc().list();
			for (Task t : tasks) {
				if (FlowHelper.checkTask(t)) {// 当前任务属于我
					Map<String, Object> o = new HashMap<>();
					Execution e = FlowFactory.getRuntimeService().createExecutionQuery().executionId(t.getExecutionId())
							.singleResult();
					o.put(OrderHistoryModelKeys.ACTIVITY_ID.name(), e.getActivityId());
					o.put(OrderHistoryModelKeys.ACTIVITY_NAME.name(), t.getName());
					if (!historyList.contains(o)) {
						historyList.add(o);
					}
				}
			}
			request.setAttribute("historyList", historyList);

		} else {
			// 校验是否能够查看
			if (!FlowHelper.checkShow(vo)) {// 若不允许查看
				// 查询条件
				DataCondition condition = new DataCondition(Util.buildQueryMap(new HashMap<String, Object>(), request));

				// 数据约束
				for (Map<String, Object> dataLimit : ((Set<Map<String, Object>>) config.getTable().get("limits"))) {
					CmPri pri = (CmPri) dataLimit.get("pri");
					if (!SessionManager.check(pri)) {
						continue;
					}
					ScriptTypes type = ScriptTypes.forCode((Integer) dataLimit.get("sqlType"));
					String sql = (String) ScriptHelper.evel(type, (String) dataLimit.get("sqlScript"));
					condition.addSql(sql);
				}

				condition.setStringEqual(OrderModelKeys.ORD_ID.name(), ordId);
				vo = (Map<String, Object>) ORMAdapterService.getInstance().find(tableName, condition.toEntity());
				if (vo == null) {
					throw new SystemRuntimeException(ExceptionType.FLOW_ORD_NOT_FOUND, "您没有订单[" + ordId + "]的权限.");
				}
			}
			columns = config.getColumns();
			subs = config.getSubs();
		}

		// h5 columns
		// 写入begin和end
		Map<String, Object> current = null;
		int i = 0;
		for (Map<String, Object> o : columns) {
			if (o.get("whole") != null && current != null) {
				current.put("detail_end", i);
			}

			if (o.get("whole") == null) {
				current = o;
				current.put("detail_begin", i + 1);
			}

			i++;
		}
		request.setAttribute("columns", columns);
		request.setAttribute("subs", subs);

		String title = pd.getName() + "[" + vo.get(OrderModelKeys.ORD_ID.name()) + "]";
		Util.setTitle(request, title);

		request.setAttribute("vo", vo);
		request.setAttribute("config", config);
		request.setAttribute("fo", fo);

		request.setAttribute(Keys.H5_JS.toString(), "amaze");// 视图使用amaze ui
		Actions.includePage(request, response, Util.getPagePath(request, "/flow/view/detail.jsp"));
	}

	/**
	 * 子表数据
	 * 
	 * @param request
	 * @param response
	 */
	@ActionMode(Mode.FIT)
	public void sub(HttpServletRequest request, HttpServletResponse response) {
		Config config = new Config(getTableConfig());
		FlowObject fo = FlowObject.create(request);

		String ordId = fo.getOrdId();// 获取订单号
		if (StringUtils.isEmpty(ordId)) {// 获取得到订单号
			throw new SystemRuntimeException(ExceptionType.FLOW_ORD_NOT_FOUND);
		}

		String tableName = (String) config.getTable().get("tableName");
		Map<String, Object> vo = (Map<String, Object>) ORMAdapterService.getInstance().findByPk(tableName, ordId);
		request.setAttribute("vo", vo);

		request.setAttribute("pd",
				FlowFactory.getRepositoryService().getProcessDefinition((String) vo.get(OrderModelKeys.PD_ID.name())));
		request.setAttribute("config", config);
		request.setAttribute("fo", fo);

		boolean ordFlag = false;// 是否订单展示界面
		HashMap<String, Object> params = RequestUtils.getJsonValue(request, Keys.PARAMS.toString());
		if (params != null && params.get("ordFlag") != null) {
			if ("true".equalsIgnoreCase(params.get("ordFlag").toString())) {
				ordFlag = true;
			} else if (Integer.valueOf(params.get("ordFlag").toString()) == 1) {
				ordFlag = true;
			}
		}
		if (RequestUtils.getIntegerValue(request, "ordFlag") == 1) {
			ordFlag = true;
		}
		request.setAttribute("ordFlag", ordFlag);

		if (ordFlag) {// 展示所有
			request.setAttribute("subs", config.getSubs());
		} else {
			if (!FlowHelper.checkActivityShow(vo, fo.getActivityId())) {
				throw new SystemRuntimeException(ExceptionType.FLOW, "您没有订单[" + ordId + "]的查看权限.");
			}

			BpmnModel bpmnModel = FlowFactory.getRepositoryService().getBpmnModel(fo.getPdId());
			Node node = BpmnHelper.getNode(bpmnModel, fo.getActivityId());
			fo.setActivityName(node.getName());

			List<Map<String, Object>> subs = new ArrayList<>();
			for (Map<String, Object> o : config.getSubs()) {
				Map<String, Object> extendVO = null;
				if (node.getNodeType().equals(NodeType.USER_TASK)) {// 任务节点
					extendVO = (Map<String, Object>) ORMService.getInstance().findHQL(
							"from WfUserTaskSubExtend where pdId = ? and activityId = ? and subKey = ?", fo.getPdId(),
							fo.getActivityId(), o.get("subKey"));
				} else if (node.getNodeType().equals(NodeType.START_EVENT)) {// 开始节点
					extendVO = (Map<String, Object>) ORMService.getInstance().findHQL(
							"from WfStartEventSubExtend where pdId = ? and subKey = ?", fo.getPdId(), o.get("subKey"));
				}

				if (extendVO == null || ((Integer) extendVO.get("showFlag")) != 1) {
					continue;
				}

				o.put("sort", extendVO.get("sort"));
				o.put("pri", null);
				subs.add(o);
			}

			// 排序
			Collections.sort(subs, new Comparator<Map<String, Object>>() {
				@Override
				public int compare(Map<String, Object> o1, Map<String, Object> o2) {
					return (int) o1.get("sort") < (int) o2.get("sort") ? -1 : 1;
				}
			});
			request.setAttribute("subs", subs);
		}

		request.setAttribute(Keys.H5_JS.toString(), "amaze");// 视图使用amaze ui
		Actions.includePage(request, response, Util.getPagePath(request, "/flow/view/sub.jsp"));
	}

	/**
	 * 获取订单流程图实例
	 * 
	 * @param request
	 * @param response
	 */
	@ActionMode(Mode.FIT)
	@ActionAccess(login = false)
	public void picture(HttpServletRequest request, HttpServletResponse response) {
		FlowObject fo = FlowObject.create(request);
		Config config = new Config(getTableConfig());
		RepositoryService repositoryService = FlowFactory.getRepositoryService();
		RuntimeService runtimeService = FlowFactory.getRuntimeService();
		ProcessEngineConfiguration processEngineConfiguration = FlowFactory.getEngine().getProcessEngineConfiguration();
		ProcessDiagramGenerator generator = new DefaultProcessDiagramGenerator();
		BpmnModel bpmnModel = repositoryService.getBpmnModel(fo.getPdId());

		List<String> activityIds = new ArrayList<>();
		List<String> flowIds = new ArrayList<>();

		String ordId = fo.getOrdId();

		if (StringUtils.isNotEmpty(fo.getOrdId())) {
			String tableName = (String) config.getTable().get("tableName");
			Map<String, Object> vo = (Map<String, Object>) ORMAdapterService.getInstance().findByPk(tableName, ordId);
			if (vo == null) {
				throw new SystemRuntimeException(ExceptionType.FLOW_ORD_NOT_FOUND);
			}
			String historyTableName = (String) config.getTable().get("historyTableName");
			List<Execution> executions = runtimeService.createExecutionQuery().processInstanceBusinessKey(fo.getOrdId())
					.processDefinitionId((String) vo.get(OrderModelKeys.PD_ID.name())).list();
			for (Execution execution : executions) {
				activityIds.addAll(runtimeService.getActiveActivityIds(execution.getId()));
			}
			if (StringUtils.isNotEmpty(historyTableName)) {
				List<Map<String, Object>> historys = ORMAdapterService.getInstance().query(historyTableName,
						new DataCondition().setStringEqual(OrderHistoryModelKeys.ORD_ID.name(), fo.getOrdId())
								.toEntity());
				if (historys != null) {
					for (Map<String, Object> history : historys) {
						activityIds.add((String) history.get(OrderHistoryModelKeys.ACTIVITY_ID.name()));
						flowIds.add((String) history.get(OrderHistoryModelKeys.SEQUENCE_FLOW_ID.name()));
					}
				}
			}
		} else {
			activityIds.add(fo.getActivityId());
		}

		String imageType = RequestUtils.getStringValue(request, "imageType");
		if (StringUtils.isEmpty(imageType)) {
			imageType = "png";
		}
		InputStream is = generator.generateDiagram(bpmnModel, imageType, activityIds, flowIds,
				processEngineConfiguration.getActivityFontName(), processEngineConfiguration.getLabelFontName(), null,
				1.0);

		Actions.showFile(request, response,
				(StringUtils.isEmpty(fo.getOrdId()) ? fo.getPdId().replaceAll(":", "-") : fo.getOrdId()) + "."
						+ imageType,
				is);
	}

	/**
	 * 流程图查看
	 * 
	 * @param request
	 * @param response
	 */
	@ActionMode(Mode.FIT)
	public void pictureMain(HttpServletRequest request, HttpServletResponse response) {
		FlowObject fo = FlowObject.create(request);
		RepositoryService repositoryService = FlowFactory.getRepositoryService();

		Config config = new Config(getTableConfig());

		Task task = null;
		if (StringUtils.isNotEmpty(fo.getTaskId())) {// 根据taskId找到ordId
			task = FlowFactory.getTaskService().createTaskQuery().taskId(fo.getTaskId()).singleResult();
			if (task != null) {
				ProcessInstance pi = FlowFactory.getRuntimeService().createProcessInstanceQuery()
						.processInstanceId(task.getProcessInstanceId()).singleResult();
				fo.setOrdId(pi.getBusinessKey());
			}
		}

		String ordId = fo.getOrdId();
		if (StringUtils.isEmpty(ordId)) {
			throw new SystemRuntimeException(ExceptionType.FLOW_ORD_NOT_FOUND);
		}

		String tableName = (String) config.getTable().get("tableName");
		Map<String, Object> vo = (Map<String, Object>) ORMAdapterService.getInstance().findByPk(tableName, ordId);
		if (vo == null) {
			throw new SystemRuntimeException(ExceptionType.FLOW_ORD_NOT_FOUND);
		}

		BpmnModel bpmnModel = repositoryService.getBpmnModel((String) vo.get(OrderModelKeys.PD_ID.name()));
		request.setAttribute("baseG", BpmnHelper.getCoordinateInfo(bpmnModel));// 基础坐标
		fo.setPdId((String) vo.get(OrderModelKeys.PD_ID.name()));

		String currentActivityId = null;// 当前节点
		if (StringUtils.isNotEmpty(fo.getActivityId())) {
			currentActivityId = fo.getActivityId();
		} else if (task != null) {// 有任务
			Execution execution = FlowFactory.getRuntimeService().createExecutionQuery()
					.executionId(task.getExecutionId()).singleResult();
			currentActivityId = execution.getActivityId();
		} else {
			// 流程实例部分
			List<Execution> executions = FlowFactory.getRuntimeService().createExecutionQuery()
					.processDefinitionId((String) vo.get(OrderModelKeys.PD_ID.name()))
					.processInstanceBusinessKey(fo.getOrdId()).list();
			if (executions != null && executions.size() > 0) {
				Execution execution = executions.get(0);
				currentActivityId = execution.getActivityId();
			}
		}

		String historyTableName = (String) config.getTable().get("historyTableName");
		if (StringUtils.isNotEmpty(historyTableName)) {
			List<Map<String, Object>> list = ORMAdapterService.getInstance().query(historyTableName,
					new DataCondition()
							.setStringEqual(OrderHistoryModelKeys.ORD_ID.getColumn().getName(), fo.getOrdId())
							.setOrderByAsc(OrderHistoryModelKeys.ID.getColumn().getName()).toEntity());
			if (list != null) {
				// 获取坐标
				Map<String, GraphicInfo> gs = new HashMap<>();

				for (Map<String, Object> o : list) {
					String activityId = (String) o.get(OrderHistoryModelKeys.ACTIVITY_ID.name());
					Node node = BpmnHelper.getNode(bpmnModel, activityId);
					GraphicInfo graphicInfo = bpmnModel.getGraphicInfo(node.getId());
					gs.put(node.getId(), graphicInfo);
				}
				request.setAttribute("gs", gs);
			}
		}
		request.setAttribute("currentActivityId", currentActivityId);
		request.setAttribute("fo", fo);

		request.setAttribute(Keys.H5_JS.toString(), "amaze");// 视图使用amaze ui
		Actions.includePage(request, response, Util.getPagePath(request, "/flow/view/picture_main.jsp"));
	}

	/**
	 * 节点详细日志
	 * 
	 * @param request
	 * @param response
	 */
	@ActionMode(Mode.FIT)
	public void nodeDetail(HttpServletRequest request, HttpServletResponse response) {
		FlowObject fo = FlowObject.create(request);
		String activityId = RequestUtils.getStringValue(request, "activityId");

		Config config = new Config(getTableConfig());
		String historyTableName = (String) config.getTable().get("historyTableName");
		if (StringUtils.isNotEmpty(historyTableName)) {
			List<Map<String, Object>> list = ORMAdapterService.getInstance().query(historyTableName,
					new DataCondition()
							.setStringEqual(OrderHistoryModelKeys.ORD_ID.getColumn().getName(), fo.getOrdId())
							.setStringEqual(OrderHistoryModelKeys.ACTIVITY_ID.getColumn().getName(), activityId)
							.setOrderByAsc(OrderHistoryModelKeys.TASK_BEGIN_DATE.getColumn().getName()).toEntity());
			request.setAttribute("list", list);
		}

		request.setAttribute(Keys.H5_JS.toString(), "amaze");// 视图使用amaze ui
		Actions.includePage(request, response, Util.getPagePath(request, "/flow/view/node_history.jsp"));
	}

	/**
	 * 流程历史
	 * 
	 * @param request
	 * @param response
	 */
	@ActionMode(Mode.FIT)
	public void history(HttpServletRequest request, HttpServletResponse response) {
		FlowObject fo = FlowObject.create(request);
		Config config = new Config(getTableConfig());
		String historyTableName = (String) config.getTable().get("historyTableName");
		if (StringUtils.isNotEmpty(historyTableName)) {
			List<?> list = ORMAdapterService.getInstance().query(historyTableName,
					new DataCondition()
							.setStringEqual(OrderHistoryModelKeys.ORD_ID.getColumn().getName(), fo.getOrdId())
							.setOrderByAsc(OrderHistoryModelKeys.TASK_BEGIN_DATE.getColumn().getName()).toEntity());
			request.setAttribute("list", list);
		}

		request.setAttribute(Keys.H5_JS.toString(), "amaze");// 视图使用amaze ui
		Actions.includePage(request, response, Util.getPagePath(request, "/flow/view/history.jsp"));
	}

	/**
	 * 批量导出选择
	 * 
	 * @param request
	 * @param response
	 */
	public void downloadSettingZone(HttpServletRequest request, HttpServletResponse response) {
		List<HashMap<String, Object>> keys = RequestUtils.getJsonValues(request, "_keys");
		int selectedCount = 0;
		if (keys != null) {
			selectedCount = keys.size();
		}
		request.setAttribute("selectedCount", selectedCount);

		request.setAttribute("config", new Config(getTableConfig()));

		request.setAttribute(Keys.H5_JS.toString(), "amaze");// 视图使用amaze ui
		Actions.includePage(request, response, Util.getPagePath(request, "/flow/view/download.jsp"));
	}

	/**
	 * 批量导出
	 * 
	 * @param request
	 * @param response
	 */
	@SuppressWarnings("rawtypes")
	public void downloadBatch(HttpServletRequest request, HttpServletResponse response) {
		Map<String, Object> table = getTableConfig();
		Config config = new Config(table);
		String downloadType = RequestUtils.getStringValue(request, "type");

		WebLogManager.log("正在查询数据.");
		List<?> datas;

		{
			String field = Util.getSortField(request);
			String dir = Util.getSortDir(request);
			Map<String, Object> params = RequestUtils.getJsonValue(request, Keys.PARAMS.toString());

			// 设置默认排序条件
			if (StringUtils.isEmpty(field)) {
				if (params != null && params.containsKey("field")) {
					field = params.get("field").toString();
					if (params.containsKey("dir")) {
						dir = params.get("dir").toString();
					}
				} else {
					field = (String) table.get("sortName");
					dir = (String) table.get("dir");
				}
			}

			// 查询条件
			DataCondition condition = new DataCondition(Util.buildQueryMap(new HashMap<String, Object>(), request));

			if ("current".equals(downloadType)) {// 导出当前条件
				condition.setOrderBy(field, dir);

				// 高级查询
				for (Map<String, Object> query : ((Set<Map<String, Object>>) table.get("extQuerys"))) {
					String value = RequestUtils.getStringValue(request, "querys." + query.get("id"));
					if (StringUtils.isEmpty(value)) {
						continue;
					}
					Map<String, Object> context = new HashMap<String, Object>();// 构建上下文
					context.put("value", value);
					context.put("values", RequestUtils.getStringValues(request, "querys." + query.get("id")));
					ScriptTypes type = ScriptTypes.forCode((Integer) query.get("sqlType"));
					String sql = (String) ScriptHelper.evel(type, (String) query.get("sqlScript"), context);
					condition.addSql(sql);
				}
			} else {
				condition = new DataCondition();
			}

			// 数据约束
			for (Map<String, Object> dataLimit : ((Set<Map<String, Object>>) table.get("limits"))) {
				CmPri pri = (CmPri) dataLimit.get("pri");
				if (!SessionManager.check(pri)) {
					continue;
				}
				ScriptTypes type = ScriptTypes.forCode((Integer) dataLimit.get("sqlType"));
				String sql = (String) ScriptHelper.evel(type, (String) dataLimit.get("sqlScript"));
				condition.addSql(sql);
			}

			// 约束条件
			String type = RequestUtils.getStringValue(request, "type");
			if ("my".equals(type)) {// 我的订单
				// 我发起,进行中
				condition.setNumberEqual(OrderModelKeys.ORD_STATE.name(), "1");
				condition.setStringEqual(OrderModelKeys.OWNER.name(), SessionManager.getUser().getUid());
			} else if ("relate".equals(type)) {// 我的经办
				// 我经办,不是我发起
				condition.setStringNotEqual(OrderModelKeys.OWNER.name(), SessionManager.getUser().getUid());
				condition.addSql(OrderModelKeys.UIDS.name() + " like '%~" + SessionManager.getUser().getUid() + "~%'");
			} else if ("draft".equals(type)) {// 草稿箱
				// 我发起,未开始
				condition.setNumberEqual(OrderModelKeys.ORD_STATE.name(), "0");// 未开始
				condition.setStringEqual(OrderModelKeys.OWNER.name(), SessionManager.getUser().getUid());
			} else if ("close".equals(type)) {// 我的已完成
				// 我发起,已完成
				condition.setNumberIn(OrderModelKeys.ORD_STATE.name(), "2", "3");
				condition.setStringEqual(OrderModelKeys.OWNER.name(), SessionManager.getUser().getUid());
			}

			datas = ORMAdapterService.getInstance().query((String) table.get("tableName"), condition.toEntity());
		}

		// 准备好数据结构
		List<HashMap<String, Object>> list = new ArrayList<>();
		List<String> fields = new ArrayList<>();
		Map<String, String> titles = new HashMap<>();

		List<Map<String, Object>> showFields = new ArrayList<>();
		for (Integer index : RequestUtils.getIntegerValues(request, "_index")) {
			showFields.add(config.getDownloadList().get(index));
		}

		for (Map<String, Object> field : showFields) {
			String name = (String) field.get("name");
			if (StringUtils.isNotEmpty(name)) {
				fields.add(name);
				titles.put(name, (String) field.get("busiName"));
			} else {
				fields.add("_" + field.get("id"));
				titles.put("_" + field.get("id"), (String) field.get("busiName"));
			}
		}

		WebLogManager.beginLoop("正在处理数据.", datas.size());
		for (Object obj : datas) {
			WebLogManager.signalLoop();
			HashMap<String, Object> vo = new HashMap<>();
			Map<String, Object> context = new HashMap<>();
			context.put("vo", obj);
			context.put("mode", 4); // 赋予导出时的状态
			
			//展示变量
			for (Map<String, Object> exec : ((Set<Map<String, Object>>) table.get("prepareExecs"))) {
				Object var = ScriptHelper.evel(ScriptTypes.forCode((Integer) exec.get("execType")),
						(String) exec.get("execScript"), context);
				context.put((String) exec.get("var"), var);
			}
			
			for (Map<String, Object> field : showFields) {
				String name = (String) field.get("name");
				if (StringUtils.isNotEmpty(name) && (((Map) obj).get(name) instanceof byte[])) {// 附件
					vo.put(name, "[附件不支持]");
					continue;
				}

				Object value = ScriptHelper.evel(ScriptTypes.forCode((Integer) field.get("contentType")),
						(String) field.get("contentScript"), context);
				if (value == null) {
					continue;
				}
				if (StringUtils.isNotEmpty(name)) {
					vo.put(name, value.toString());
				} else {
					vo.put("_" + field.get("id"), value.toString());
				}
			}
			list.add(vo);
		}

		WebLogManager.log("正在下载文件.");
		Actions.downloadExcel(
				request, response, table.get("busiName") + "-批量导出" + "_"
						+ Formatter.formatDatetime(new Date(), "yyyyMMddHHmmss") + ".xlsx",
				fields.toArray(new String[fields.size()]), titles, list);
	}
}
