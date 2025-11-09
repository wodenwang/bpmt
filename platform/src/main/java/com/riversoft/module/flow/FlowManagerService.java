/*
 * $HeadURL: $
 * $Id: $
 * Copyright (c) 2013 by Riversoft System, all rights reserved.
 */
package com.riversoft.module.flow;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.activiti.bpmn.converter.BpmnXMLConverter;
import org.activiti.bpmn.model.BpmnModel;
import org.activiti.bpmn.model.FlowElement;
import org.activiti.editor.language.json.converter.BpmnJsonConverter;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.Model;
import org.activiti.engine.repository.ProcessDefinition;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.riversoft.core.BeanFactory;
import com.riversoft.core.IDGenerator;
import com.riversoft.core.db.DataCondition;
import com.riversoft.core.db.DataPO;
import com.riversoft.core.db.JdbcService;
import com.riversoft.core.db.ORMService;
import com.riversoft.core.exception.ExceptionType;
import com.riversoft.core.exception.SystemRuntimeException;
import com.riversoft.flow.FlowFactory;
import com.riversoft.flow.FlowService;
import com.riversoft.flow.deploy.CustomNodeExecutors;
import com.riversoft.flow.key.NodeType;
import com.riversoft.flow.key.OrderHistoryModelKeys;
import com.riversoft.flow.key.OrderModelKeys;
import com.riversoft.flow.key.OrderOpinionModelKeys;
import com.riversoft.module.flow.view.FlowBasicViewAction;
import com.riversoft.module.flow.view.SysBtn;
import com.riversoft.module.flow.view.SysTab;
import com.riversoft.platform.SessionManager;
import com.riversoft.platform.db.model.ModelKeyUtils;
import com.riversoft.platform.po.CmPri;
import com.riversoft.platform.po.TbTable;
import com.riversoft.platform.po.VwUrl;
import com.riversoft.platform.po.WfPd;
import com.riversoft.platform.service.TableService;
import com.riversoft.platform.template.DevelopmentOperation;
import com.riversoft.platform.translate.NotifyMsgType;
import com.riversoft.platform.web.view.annotation.View;
import com.riversoft.util.dynamicbean.DynamicBeanUtils;

/**
 * @author woden
 * 
 */
public class FlowManagerService {
	/**
	 * Logger for this class
	 */
	private static final Logger logger = LoggerFactory.getLogger(FlowManagerService.class);

	/**
	 * 部署
	 * 
	 * @param modelData
	 * @return
	 */
	private ProcessDefinition executeDeploy(Model modelData) {
		RepositoryService service = FlowFactory.getRepositoryService();
		ObjectNode modelNode;
		BpmnModel model;
		try {
			modelNode = (ObjectNode) new ObjectMapper().readTree(service.getModelEditorSource(modelData.getId()));
			model = new BpmnJsonConverter().convertToBpmnModel(modelNode);
		} catch (IOException e) {
			throw new SystemRuntimeException("流程部署失败.", e);
		}
		String processName = modelData.getName() + ".bpmn20.xml";
		model.setTargetNamespace(modelData.getCategory());// 命名空间使用分类

		// 执行自定义扩展
		CustomNodeExecutors.execute(model);

		// 部署前打印一下
		if (logger.isDebugEnabled()) {
			logger.debug("打印XML:");
			logger.debug(new String(new BpmnXMLConverter().convertToXML(model)));
		}

		Deployment deployment = service.createDeployment().name(modelData.getName()).category(modelData.getCategory()).addBpmnModel(processName, model).deploy();

		// 部署成功,获取对应的流程信息
		ProcessDefinition pd = service.createProcessDefinitionQuery().deploymentId(deployment.getId()).singleResult();
		if (StringUtils.isEmpty(pd.getName())) {
			throw new SystemRuntimeException(ExceptionType.BUSINESS, "流程[" + pd.getKey() + "]没有设置名称.");
		}

		return pd;
	}

	/**
	 * 同步其他版本的配置数据
	 * 
	 * @param pdId
	 *            目标流程定义ID
	 * @param version
	 *            源流程定义版本
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void executeSyncPd(String pdId, int version) {

		// 先删除原来的
		removePdConfig(pdId);

		ProcessDefinition desc = FlowService.getInstance().getPd(pdId);
		ProcessDefinition src = FlowService.getInstance().getPd(desc.getKey(), version);

		BpmnModel bpmnModel = FlowFactory.getRepositoryService().getBpmnModel(desc.getId());
		Set<String> activityIds = new HashSet<>();
		for (FlowElement element : bpmnModel.getMainProcess().getFlowElements()) {
			activityIds.add(element.getId());
		}

		// 拷贝pd配置
		WfPd po = (WfPd) ORMService.getInstance().findByPk(WfPd.class.getName(), src.getId());
		if (po == null) {
			throw new SystemRuntimeException(ExceptionType.FLOW_PD_NOT_FOUND);
		}
		// 创建流程配置
		WfPd newPO = new WfPd();
		newPO.setBasicViewKey(po.getBasicViewKey());
		newPO.setDescription(po.getDescription());
		newPO.setPdId(pdId);
		ORMService.getInstance().savePO(newPO);

		// 拷贝其他配置信息
		for (NodeType nodeType : NodeType.values()) {
			String tables = nodeType.getConfigTables();
			if (StringUtils.isNotEmpty(tables)) {
				for (String tableName : tables.split(";")) {
					List<Map<String, Object>> list = (List<Map<String, Object>>) ORMService.getInstance().query(tableName, new DataCondition().setStringEqual("pdId", src.getId()).toEntity());
					for (Map<String, Object> o : list) {
						Map<String, Object> vo = DynamicBeanUtils.cloneMap(o);
						vo.put("pdId", pdId);
						if (vo.containsKey("id")) {
							vo.remove("id");
						}

						// 节点或连线已不存在,则跳过
						if (vo.get("activityId") != null && !activityIds.contains((String) vo.get("activityId"))) {
							logger.debug("节点[{}]已不存在,跳过.", vo.get("activityId"));
							continue;
						}
						if (vo.get("flowId") != null && !activityIds.contains((String) vo.get("flowId"))) {
							logger.debug("连线[{}]已不存在,跳过.", vo.get("flowId"));
							continue;
						}

						ORMService.getInstance().saveOrUpdate(vo);
					}
				}
			}
		}

	}

	/**
	 * 删除流程配置
	 * 
	 * @param pdId
	 */
	private void removePdConfig(String pdId) {
		// 删除其他相关配置
		ORMService.getInstance().removeByPk(WfPd.class.getName(), pdId);

		// 删除节点配置信息
		for (NodeType nodeType : NodeType.values()) {
			String tables = nodeType.getConfigTables();
			if (StringUtils.isNotEmpty(tables)) {
				for (String tableName : tables.split(";")) {
					ORMService.getInstance().executeHQL("delete from " + tableName + " where pdId = ?", pdId);
				}
			}
		}
	}

	/**
	 * 部署配置包装类
	 * 
	 * @author woden
	 * 
	 */
	static class DeployConfig {
		private boolean flag;
		private String msg;
		private String extMsg;

		DeployConfig(boolean flag, String msg, String extMsg) {
			this.flag = flag;
			this.msg = msg;
			this.extMsg = extMsg;

		}
	}

	/**
	 * 新的部署
	 * 
	 * @param id
	 * @param orderTableConfig
	 * @param historyTableConfig
	 * @param opinionTableConfig
	 * @param basicViewConfig
	 * @return
	 */
	public ProcessDefinition executeDeploy(String id, DeployConfig orderTableConfig, DeployConfig historyTableConfig, DeployConfig opinionTableConfig, DeployConfig basicViewConfig) {
		// 前置验证
		// 订单表创建时基础视图必须也是创建
		if (orderTableConfig.flag) {
			basicViewConfig.flag = true;
		}

		if (!orderTableConfig.flag && StringUtils.isEmpty(orderTableConfig.extMsg)) {
			throw new SystemRuntimeException(ExceptionType.BUSINESS, "请选择绑定的订单表.需要新建订单表请勾选[自动创建].");
		}

		if (StringUtils.isNotEmpty(orderTableConfig.extMsg)) {
			if (!basicViewConfig.flag && StringUtils.isEmpty(basicViewConfig.extMsg)) {
				throw new SystemRuntimeException(ExceptionType.BUSINESS, "绑定订单表时请选择相应基础视图.需要新建视图请勾选[自动创建].");
			}
		}

		// 首先部署
		RepositoryService service = FlowFactory.getRepositoryService();
		Model modelData = service.getModel(id);
		ProcessDefinition pd = executeDeploy(modelData);

		// 自动描述
		basicViewConfig.msg = pd.getName();

		// 创建流程配置
		WfPd po = new WfPd();
		po.setPdId(pd.getId());
		po.setDescription(pd.getName());

		// 表名基础校验
		if (orderTableConfig.flag && ORMService.getInstance().findByPk(TbTable.class.getName(), orderTableConfig.msg) != null) {
			throw new SystemRuntimeException(ExceptionType.BUSINESS, "订单表[" + orderTableConfig.msg + "]已存在");
		}
		if (historyTableConfig.flag && ORMService.getInstance().findByPk(TbTable.class.getName(), historyTableConfig.msg) != null) {
			throw new SystemRuntimeException(ExceptionType.BUSINESS, "历史表[" + historyTableConfig.msg + "]已存在");
		}
		if (opinionTableConfig.flag && ORMService.getInstance().findByPk(TbTable.class.getName(), opinionTableConfig.msg) != null) {
			throw new SystemRuntimeException(ExceptionType.BUSINESS, "意见表[" + opinionTableConfig.msg + "]已存在");
		}

		// 后续处理
		String orderTableName = createOrderTable(pd, orderTableConfig);
		String historyTableName = createHistoryTable(pd, historyTableConfig);
		String opinionTableName = createOpinionTable(pd, opinionTableConfig);
		createBasicView(pd.getKey(), po, orderTableName, historyTableName, opinionTableName, basicViewConfig);
		ORMService.getInstance().savePO(po);

		return pd;
	}

	/**
	 * 订单表处理
	 * 
	 * @param pd
	 * @param orderTableConfig
	 */
	private String createOrderTable(ProcessDefinition pd, DeployConfig orderTableConfig) {
		String tableName;
		if (orderTableConfig.flag) {// 自动创建
			tableName = orderTableConfig.msg;
		} else {
			return orderTableConfig.extMsg;
		}

		TableService service = BeanFactory.getInstance().getBean(TableService.class);
		TbTable table = ModelKeyUtils.buildTable(OrderModelKeys.class, tableName, pd.getName());
		service.executeCreateTable(table);

		return tableName;
	}

	/**
	 * 历史表处理
	 * 
	 * @param pd
	 * @param historyTableConfig
	 */
	private String createHistoryTable(ProcessDefinition pd, DeployConfig historyTableConfig) {
		String tableName;
		if (historyTableConfig.flag) {// 自动创建
			tableName = historyTableConfig.msg;
		} else {
			return null;
		}

		TableService service = BeanFactory.getInstance().getBean(TableService.class);
		TbTable table = ModelKeyUtils.buildTable(OrderHistoryModelKeys.class, tableName, pd.getName() + "-日志");
		service.executeCreateTable(table);

		return tableName;
	}

	/**
	 * 审批意见表处理
	 * 
	 * @param pd
	 * @param tableConfig
	 * @return
	 */
	private String createOpinionTable(ProcessDefinition pd, DeployConfig tableConfig) {
		String tableName;
		if (tableConfig.flag) {// 自动创建
			tableName = tableConfig.msg;
		} else {
			return null;
		}

		TableService service = BeanFactory.getInstance().getBean(TableService.class);
		TbTable table = ModelKeyUtils.buildTable(OrderOpinionModelKeys.class, tableName, pd.getName() + "-审批意见");
		service.executeCreateTable(table);

		return tableName;
	}

	/**
	 * 工作流视图处理
	 * 
	 * @param pdKey
	 * @param po
	 * @param orderTableName
	 * @param historyTableName
	 * @param opinionTableName
	 * @param basicViewConfig
	 */
	private void createBasicView(String pdKey, WfPd po, String orderTableName, String historyTableName, String opinionTableName, DeployConfig basicViewConfig) {
		if (!basicViewConfig.flag) {
			po.setBasicViewKey(basicViewConfig.extMsg);
			return;
		}

		// 自动创建
		String viewKey = IDGenerator.next();
		// 框架配置
		VwUrl vwUrl = new VwUrl();
		vwUrl.setViewKey(viewKey);
		vwUrl.setLoginType(1);// 默认.工作流限制只支持用户登录
		vwUrl.setLockFlag(0);
		vwUrl.setCreateUid(SessionManager.getUser().getUid());
		vwUrl.setDescription(StringUtils.isNotEmpty(basicViewConfig.msg) ? basicViewConfig.msg : "无命名");
		vwUrl.setViewClass(FlowBasicViewAction.class.getAnnotation(View.class).value());// 工作流视图
		ORMService.getInstance().savePO(vwUrl);

		// 工作流视图
		{
			DataPO basicViewPO = new DataPO("VwFlowBasic");
			basicViewPO.set("viewKey", viewKey);
			basicViewPO.set("tableName", orderTableName);
			basicViewPO.set("pdKey", pdKey);
			basicViewPO.set("historyTableName", historyTableName);
			basicViewPO.set("opinionTableName", opinionTableName);
			basicViewPO.set("busiName", vwUrl.getDescription());
			basicViewPO.set("col", 2);
			basicViewPO.set("sortName", "ORD_ID");
			basicViewPO.set("dir", "desc");
			basicViewPO.set("ordIdType", 1);
			basicViewPO.set("ordIdScript", "return seq.pattern('AUTO-{now}{seq}','yyyyMMdd','" + orderTableName + "','ORD_ID',3);");
			basicViewPO.set("initQuery", 1);
			basicViewPO.set("msgType", NotifyMsgType.MAIL.getCode());// 默认打开mail

			ORMService.getInstance().save(basicViewPO.toEntity());

			int sort = 1;
			// 按钮
			{
				for (SysBtn sysBtn : SysBtn.values()) {
					Map<String, Object> btn = sysBtn.toMap();
					btn.put("sort", sort++);
					DataPO btnPO = new DataPO("VwFlowBasicBtnSys", btn);
					btnPO.set("viewKey", viewKey);
					if (sysBtn == SysBtn.CREATE) {// 创建按钮有权限
						CmPri btnPri = new CmPri();
						btnPri.setPriKey(IDGenerator.uuid());
						btnPri.setDevelopmentInfo(btnPO, "系统按钮");
						btnPO.set("pri", btnPri);
					}
					ORMService.getInstance().save(btnPO.toEntity());
				}
			}

			sort = 1;
			// 字段
			{
				DataPO columnPO = new DataPO("VwFlowBasicColumnLine");
				columnPO.set("viewKey", viewKey);
				columnPO.set("pixelKey", IDGenerator.next());
				columnPO.set("busiName", "基础信息");
				columnPO.set("expandFlag", 1);
				columnPO.set("sort", sort++);
				CmPri columnPri = new CmPri();
				columnPri.setPriKey(IDGenerator.uuid());
				columnPri.setDevelopmentInfo(columnPO, "分割线");
				columnPO.set("pri", columnPri);
				ORMService.getInstance().save(columnPO.toEntity());
			}
			{
				DataPO columnPO = new DataPO("VwFlowBasicColumnShow");
				columnPO.set("viewKey", viewKey);
				columnPO.set("pixelKey", IDGenerator.next());
				columnPO.set("busiName", "订单号");
				columnPO.set("whole", 1);
				columnPO.set("contentType", 1);
				columnPO.set("contentScript", "return vo?.ORD_ID;");
				columnPO.set("sort", sort);
				columnPO.set("listSort", sort++);
				columnPO.set("sortField", "ORD_ID");
				columnPO.set("style", "min-width:80px;");
				CmPri columnPri = new CmPri();
				columnPri.setPriKey(IDGenerator.uuid());
				columnPri.setDevelopmentInfo(columnPO, "展示字段");
				columnPO.set("pri", columnPri);
				ORMService.getInstance().save(columnPO.toEntity());
			}
			{
				DataPO columnPO = new DataPO("VwFlowBasicColumnShow");
				columnPO.set("viewKey", viewKey);
				columnPO.set("pixelKey", IDGenerator.next());
				columnPO.set("busiName", "摘要");
				columnPO.set("sort", sort);
				columnPO.set("listSort", sort++);
				columnPO.set("sortField", "REMARK");
				columnPO.set("whole", 1);
				columnPO.set("contentType", 1);// 默认groovy
				columnPO.set("contentScript", "return vo?.REMARK;");
				columnPO.set("style", "min-width:150px;");
				CmPri columnPri = new CmPri();
				columnPri.setPriKey(IDGenerator.uuid());
				columnPri.setDevelopmentInfo(columnPO, "展示字段");
				columnPO.set("pri", columnPri);
				ORMService.getInstance().save(columnPO.toEntity());
			}
			// 审批意见
			{
				DataPO columnPO = new DataPO("VwFlowBasicColumnLine");
				columnPO.set("viewKey", viewKey);
				columnPO.set("pixelKey", IDGenerator.next());
				columnPO.set("busiName", "流程意见");
				columnPO.set("expandFlag", 1);
				columnPO.set("sort", sort++);
				CmPri columnPri = new CmPri();
				columnPri.setPriKey(IDGenerator.uuid());
				columnPri.setDevelopmentInfo(columnPO, "分割线");
				columnPO.set("pri", columnPri);
				ORMService.getInstance().save(columnPO.toEntity());
			}
			{
				DataPO columnPO = new DataPO("VwFlowBasicColumnShow");
				columnPO.set("viewKey", viewKey);
				columnPO.set("pixelKey", IDGenerator.next());
				columnPO.set("busiName", "流程意见");
				columnPO.set("sort", sort);
				columnPO.set("listSort", -1);
				columnPO.set("whole", 2);
				columnPO.set("contentType", 1);// 默认groovy
				columnPO.set("contentScript", "return flow.showOpinion(vo);");
				CmPri columnPri = new CmPri();
				columnPri.setPriKey(IDGenerator.uuid());
				columnPri.setDevelopmentInfo(columnPO, "展示字段");
				columnPO.set("pri", columnPri);
				ORMService.getInstance().save(columnPO.toEntity());
			}
			// 业务信息
			{
				DataPO columnPO = new DataPO("VwFlowBasicColumnLine");
				columnPO.set("viewKey", viewKey);
				columnPO.set("pixelKey", IDGenerator.next());
				columnPO.set("busiName", "业务信息");
				columnPO.set("expandFlag", 1);
				columnPO.set("sort", sort++);
				CmPri columnPri = new CmPri();
				columnPri.setPriKey(IDGenerator.uuid());
				columnPri.setDevelopmentInfo(columnPO, "分割线");
				columnPO.set("pri", columnPri);
				ORMService.getInstance().save(columnPO.toEntity());
			}
			{
				DataPO columnPO = new DataPO("VwFlowBasicColumnShow");
				columnPO.set("viewKey", viewKey);
				columnPO.set("pixelKey", IDGenerator.next());
				columnPO.set("busiName", "附件");
				columnPO.set("sort", sort);
				columnPO.set("listSort", -1);
				columnPO.set("whole", 1);
				columnPO.set("contentType", 1);// 默认groovy
				columnPO.set("contentScript", "cm.widget('filemanager',vo?.ATTACHMENT);");
				CmPri columnPri = new CmPri();
				columnPri.setPriKey(IDGenerator.uuid());
				columnPri.setDevelopmentInfo(columnPO, "展示字段");
				columnPO.set("pri", columnPri);
				ORMService.getInstance().save(columnPO.toEntity());
			}
			{
				DataPO columnPO = new DataPO("VwFlowBasicColumnShow");
				columnPO.set("viewKey", viewKey);
				columnPO.set("pixelKey", IDGenerator.next());
				columnPO.set("busiName", "备注");
				columnPO.set("sort", sort);
				columnPO.set("listSort", -1);
				columnPO.set("whole", 1);
				columnPO.set("contentType", 1);// 默认groovy
				columnPO.set("contentScript", "vo?.MEMO");
				CmPri columnPri = new CmPri();
				columnPri.setPriKey(IDGenerator.uuid());
				columnPri.setDevelopmentInfo(columnPO, "展示字段");
				columnPO.set("pri", columnPri);
				ORMService.getInstance().save(columnPO.toEntity());
			}
			// 流程信息
			{
				DataPO columnPO = new DataPO("VwFlowBasicColumnLine");
				columnPO.set("viewKey", viewKey);
				columnPO.set("pixelKey", IDGenerator.next());
				columnPO.set("busiName", "流程信息");
				columnPO.set("expandFlag", 1);
				columnPO.set("sort", sort++);
				CmPri columnPri = new CmPri();
				columnPri.setPriKey(IDGenerator.uuid());
				columnPri.setDevelopmentInfo(columnPO, "分割线");
				columnPO.set("pri", columnPri);
				ORMService.getInstance().save(columnPO.toEntity());
			}
			{
				DataPO columnPO = new DataPO("VwFlowBasicColumnShow");
				columnPO.set("viewKey", viewKey);
				columnPO.set("pixelKey", IDGenerator.next());
				columnPO.set("busiName", "下单人");
				columnPO.set("sort", sort);
				columnPO.set("listSort", sort++);
				columnPO.set("whole", 0);
				columnPO.set("sortField", "OWNER");
				columnPO.set("contentType", 1);// 默认grrovy
				columnPO.set("contentScript", "return cm.widget('user',vo?.OWNER);");
				columnPO.set("style", "min-width:60px;");
				CmPri columnPri = new CmPri();
				columnPri.setPriKey(IDGenerator.uuid());
				columnPri.setDevelopmentInfo(columnPO, "展示字段");
				columnPO.set("pri", columnPri);
				ORMService.getInstance().save(columnPO.toEntity());
			}
			{
				DataPO columnPO = new DataPO("VwFlowBasicColumnShow");
				columnPO.set("viewKey", viewKey);
				columnPO.set("pixelKey", IDGenerator.next());
				columnPO.set("busiName", "下单时间");
				columnPO.set("sort", sort);
				columnPO.set("listSort", sort++);
				columnPO.set("whole", 0);
				columnPO.set("sortField", "CREATE_DATE");
				columnPO.set("contentType", 1);// 默认grrovy
				columnPO.set("contentScript", "return fmt.formatDatetime(vo?.CREATE_DATE);");
				columnPO.set("style", "min-width:80px;");
				CmPri columnPri = new CmPri();
				columnPri.setPriKey(IDGenerator.uuid());
				columnPri.setDevelopmentInfo(columnPO, "展示字段");
				columnPO.set("pri", columnPri);
				ORMService.getInstance().save(columnPO.toEntity());
			}
			{
				DataPO columnPO = new DataPO("VwFlowBasicColumnShow");
				columnPO.set("viewKey", viewKey);
				columnPO.set("pixelKey", IDGenerator.next());
				columnPO.set("busiName", "待处理人");
				columnPO.set("sort", sort);
				columnPO.set("listSort", sort++);
				columnPO.set("whole", 0);
				columnPO.set("contentType", 1);// 默认groovy
				columnPO.set("contentScript", "return flow.showAssignee(vo);");
				columnPO.set("style", "min-width:60px;");
				CmPri columnPri = new CmPri();
				columnPri.setPriKey(IDGenerator.uuid());
				columnPri.setDevelopmentInfo(columnPO, "展示字段");
				columnPO.set("pri", columnPri);
				ORMService.getInstance().save(columnPO.toEntity());
			}
			{
				DataPO columnPO = new DataPO("VwFlowBasicColumnShow");
				columnPO.set("viewKey", viewKey);
				columnPO.set("pixelKey", IDGenerator.next());
				columnPO.set("busiName", "当前节点");
				columnPO.set("sort", sort);
				columnPO.set("listSort", sort++);
				columnPO.set("whole", 0);
				columnPO.set("contentType", 1);// 默认groovy
				columnPO.set("contentScript", "return flow.showActivity(vo);");
				columnPO.set("style", "min-width:80px;");
				CmPri columnPri = new CmPri();
				columnPri.setPriKey(IDGenerator.uuid());
				columnPri.setDevelopmentInfo(columnPO, "展示字段");
				columnPO.set("pri", columnPri);
				ORMService.getInstance().save(columnPO.toEntity());
			}

			// 固定的系统子表
			sort = 0;
			for (SysTab sysTab : SysTab.values()) {
				Map<String, Object> tab = sysTab.toMap();
				DataPO subPO = new DataPO("VwFlowBasicSubSys", tab);
				subPO.set("subKey", IDGenerator.next());
				subPO.set("viewKey", viewKey);
				subPO.set("sort", sort++);
				CmPri subPri = new CmPri();
				subPri.setPriKey(IDGenerator.uuid());
				subPri.setDevelopmentInfo(subPO, "子表标签");
				subPri.setCheckType(1);
				subPri.setCheckScript("return true;");
				subPO.set("pri", subPri);
				ORMService.getInstance().save(subPO.toEntity());
			}
		}

		po.setBasicViewKey(viewKey);
	}

	/**
	 * 删除流程定义
	 * 
	 * @param pdId
	 */
	@DevelopmentOperation("删除流程")
	public void executeRemovePd(String pdId) {
		// 删除流程定义
		RepositoryService service = FlowFactory.getRepositoryService();
		ProcessDefinition pd = service.createProcessDefinitionQuery().processDefinitionId(pdId).singleResult();
		service.deleteDeployment(pd.getDeploymentId(), true);

		removePdConfig(pdId);
	}

	/**
	 * 执行升级
	 * 
	 * @param id
	 * @param version
	 */
	@DevelopmentOperation("流程升级")
	public ProcessDefinition executeUpgrade(String id, int version) {
		// 首先部署
		RepositoryService service = FlowFactory.getRepositoryService();
		Model modelData = service.getModel(id);
		ProcessDefinition pd = executeDeploy(modelData);

		// 同步版本
		executeSyncPd(pd.getId(), version);
		return pd;
	}

	/**
	 * 更新流程类别
	 * 
	 * @param pdKey
	 * @param category
	 */
	@DevelopmentOperation("类别更新")
	public void executeUpdateCategory(String pdKey, String category) {
		JdbcService.getInstance().executeSQL("update ACT_RE_PROCDEF set CATEGORY_ = ? where KEY_ = ?", category, pdKey);
	}

}
