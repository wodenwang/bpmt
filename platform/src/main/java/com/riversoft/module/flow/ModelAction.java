/*
 * $HeadURL: $
 * $Id: $
 * Copyright (c) 2013 by Riversoft System, all rights reserved.
 */
package com.riversoft.module.flow;

import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.activiti.bpmn.converter.BpmnXMLConverter;
import org.activiti.bpmn.model.BpmnModel;
import org.activiti.editor.constants.ModelDataJsonConstants;
import org.activiti.editor.language.json.converter.BpmnJsonConverter;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.impl.context.Context;
import org.activiti.engine.impl.util.io.InputStreamSource;
import org.activiti.engine.repository.Model;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.image.ProcessDiagramGenerator;
import org.activiti.image.impl.DefaultProcessDiagramGenerator;
import org.activiti.spring.SpringProcessEngineConfiguration;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.riversoft.core.BeanFactory;
import com.riversoft.core.db.DataCondition;
import com.riversoft.core.db.JdbcService;
import com.riversoft.core.db.ORMService;
import com.riversoft.core.exception.ExceptionType;
import com.riversoft.core.exception.SystemRuntimeException;
import com.riversoft.core.web.Actions;
import com.riversoft.core.web.Actions.Util;
import com.riversoft.core.web.RequestUtils;
import com.riversoft.core.web.annotation.ActionAccess;
import com.riversoft.core.web.annotation.ActionAccess.SafeLevel;
import com.riversoft.flow.FlowFactory;
import com.riversoft.flow.key.OrderModelKeys;
import com.riversoft.platform.db.model.ModelKeyUtils;
import com.riversoft.platform.po.TbTable;
import com.riversoft.platform.web.FileManager;
import com.riversoft.platform.web.FileManager.UploadFile;
import com.riversoft.util.jackson.JsonMapper;

/**
 * 模型设计
 * 
 * @author woden
 * 
 */
@ActionAccess(level = SafeLevel.DEV_R)
public class ModelAction {
	/**
	 * Logger for this class
	 */
	static final Logger logger = LoggerFactory.getLogger(ModelAction.class);

	/**
	 * 框架页
	 * 
	 * @param request
	 * @param response
	 */
	public void index(HttpServletRequest request, HttpServletResponse response) {
		Actions.includePage(request, response, Util.getPagePath(request, "main.jsp"));
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
		List<Map<String, Object>> categorys = JdbcService.getInstance().querySQL(
				"select distinct CATEGORY_ as category from ACT_RE_MODEL order by CATEGORY_ asc");
		if (categorys != null) {
			for (Map<String, Object> o : categorys) {
				o.put("id", o.get("category"));
				o.put("name", o.get("category"));
				o.put("category", null);
				o.put("icon", cp + "/css/icon/folder_image.png");
				o.put("isModel", false);
				list.add(o);
			}
		}

		List<Model> models = service.createModelQuery().orderByModelName().asc().list();// 模型列表
		if (models != null) {
			for (Model model : models) {
				Map<String, Object> o = new HashMap<>();
				o.put("name", model.getName());
				o.put("category", model.getCategory());
				o.put("icon", cp + "/css/icon/image.png");
				o.put("id", model.getId());
				o.put("key", model.getKey());
				o.put("isModel", true);
				list.add(o);
			}
		}

		request.setAttribute("list", JsonMapper.defaultMapper().toJson(list));

		Actions.includePage(request, response, Util.getPagePath(request, "tree.jsp"));
	}

	/**
	 * 新增模型资源
	 * 
	 * @param request
	 * @param response
	 */
	public void createZone(HttpServletRequest request, HttpServletResponse response) {
		Actions.includePage(request, response, Util.getPagePath(request, "create.jsp"));
	}

	/**
	 * 新增模型资源
	 * 
	 * @param request
	 * @param response
	 */
	@ActionAccess(level = SafeLevel.DEV_W)
	public void create(HttpServletRequest request, HttpServletResponse response) {
		RepositoryService service = FlowFactory.getRepositoryService();

		String name = RequestUtils.getStringValue(request, "name");
		String description = RequestUtils.getStringValue(request, "description");
		String category = RequestUtils.getStringValue(request, "category");
		if (StringUtils.isEmpty(description)) {
			description = "";
		}
		if (StringUtils.isEmpty(category)) {
			category = null;
		}
		UploadFile uploadFile = FileManager.getUploadFile(request, "file");// xml文件
		if (uploadFile != null) {
			String fileName = uploadFile.getFile().getName().toLowerCase();
			if (!fileName.endsWith(".bpmn") && !fileName.endsWith(".bpmn20.xml")) {
				throw new SystemRuntimeException(ExceptionType.BUSINESS, "文件[" + uploadFile.getFile().getName()
						+ "]类型错误,只允许[.bpmn]或[.bpmn20.xml]后缀.");
			}
		}

		Model modelData = service.newModel();
		ObjectMapper objectMapper = new ObjectMapper();
		ObjectNode modelObjectNode = objectMapper.createObjectNode();
		modelObjectNode.put(ModelDataJsonConstants.MODEL_NAME, name);
		modelObjectNode.put(ModelDataJsonConstants.MODEL_REVISION, 1);
		modelObjectNode.put(ModelDataJsonConstants.MODEL_DESCRIPTION, description);
		modelData.setMetaInfo(modelObjectNode.toString());
		modelData.setName(name);
		modelData.setKey(name);
		modelData.setCategory(category);
		service.saveModel(modelData);

		try {
			ObjectNode editorNode;
			if (uploadFile == null) {
				editorNode = objectMapper.createObjectNode();
				editorNode.put("id", "canvas");
				editorNode.put("resourceId", "canvas");
				ObjectNode stencilSetNode = objectMapper.createObjectNode();
				stencilSetNode.put("namespace", "http://b3mn.org/stencilset/bpmn2.0#");
				editorNode.put("stencilset", stencilSetNode);
			} else {
				BpmnXMLConverter xmlConverter = new BpmnXMLConverter();
				XMLInputFactory xif = XMLInputFactory.newInstance();
				XMLStreamReader xtr = xif.createXMLStreamReader(uploadFile.getInputStream());
				BpmnModel bpmnModel = new BpmnXMLConverter().convertToBpmnModel(xtr);
				xmlConverter.convertToBpmnModel(xtr);
				BpmnJsonConverter jsonConverter = new BpmnJsonConverter();
				editorNode = jsonConverter.convertToJson(bpmnModel);
			}
			service.addModelEditorSource(modelData.getId(), editorNode.toString().getBytes("utf-8"));
		} catch (UnsupportedEncodingException | FileNotFoundException | XMLStreamException e) {
			throw new SystemRuntimeException("生成模型出错.", e);
		}

		Actions.redirectAction(request, response, Util.getActionUrl(request) + "/detail.shtml?id=" + modelData.getId());
	}

	/**
	 * 明细
	 * 
	 * @param request
	 * @param response
	 */
	public void detail(HttpServletRequest request, HttpServletResponse response) {
		RepositoryService service = FlowFactory.getRepositoryService();
		Model modelData = service.getModel(RequestUtils.getStringValue(request, "id"));
		request.setAttribute("vo", modelData);

		// xml数据
		JsonNode editorNode;
		try {
			editorNode = new ObjectMapper().readTree(service.getModelEditorSource(modelData.getId()));
		} catch (IOException e) {
			throw new SystemRuntimeException(e);
		}

		try {
			BpmnJsonConverter jsonConverter = new BpmnJsonConverter();
			BpmnModel bpmnModel = jsonConverter.convertToBpmnModel(editorNode);// 获取bpmModel
			BpmnXMLConverter xmlConverter = new BpmnXMLConverter();

			request.setAttribute("model", bpmnModel);
			request.setAttribute("xml", new String(xmlConverter.convertToXML(bpmnModel)));
		} catch (Throwable e) {
			// do noting
			e.printStackTrace();
		}

		Actions.includePage(request, response, Util.getPagePath(request, "detail.jsp"));
	}

	/**
	 * 提交保存
	 * 
	 * @param request
	 * @param response
	 */
	@ActionAccess(level = SafeLevel.DEV_W)
	public void submit(HttpServletRequest request, HttpServletResponse response) {
		RepositoryService service = FlowFactory.getRepositoryService();

		String name = RequestUtils.getStringValue(request, "name");
		String category = RequestUtils.getStringValue(request, "category");

		Model modelData = service.getModel(RequestUtils.getStringValue(request, "id"));
		modelData.setName(name);
		modelData.setKey(modelData.getKey());
		modelData.setCategory(category);
		service.saveModel(modelData);

		Actions.redirectInfoPage(request, response, "修改成功,资源ID为[" + modelData.getId() + "].");
	}

	/**
	 * 删除模型
	 * 
	 * @param request
	 * @param response
	 */
	@ActionAccess(level = SafeLevel.DEV_W)
	public void remove(HttpServletRequest request, HttpServletResponse response) {
		RepositoryService service = FlowFactory.getRepositoryService();
		service.deleteModel(RequestUtils.getStringValue(request, "id"));
		Actions.redirectInfoPage(request, response, "删除成功.");
	}

	/**
	 * 导出
	 * 
	 * @param request
	 * @param response
	 */
	public void export(HttpServletRequest request, HttpServletResponse response) {
		RepositoryService service = FlowFactory.getRepositoryService();
		Model modelData = service.getModel(RequestUtils.getStringValue(request, "id"));

		BpmnJsonConverter jsonConverter = new BpmnJsonConverter();
		JsonNode editorNode;
		try {
			editorNode = new ObjectMapper().readTree(service.getModelEditorSource(modelData.getId()));
		} catch (IOException e) {
			throw new SystemRuntimeException(e);
		}
		byte[] bpmnBytes;
		try {
			BpmnModel bpmnModel = jsonConverter.convertToBpmnModel(editorNode);
			BpmnXMLConverter xmlConverter = new BpmnXMLConverter();
			bpmnBytes = xmlConverter.convertToXML(bpmnModel);
		} catch (Throwable e) {// 处理空指针
			bpmnBytes = new byte[0];
		}

		Actions.download(request, response, modelData.getName() + ".bpmn20.xml", new ByteArrayInputStream(bpmnBytes));
	}

	/**
	 * 图片预览
	 * 
	 * @param request
	 * @param response
	 */
	public void picture(HttpServletRequest request, HttpServletResponse response) {
		RepositoryService service = FlowFactory.getRepositoryService();
		Model modelData = service.getModel(RequestUtils.getStringValue(request, "id"));

		BpmnJsonConverter jsonConverter = new BpmnJsonConverter();
		JsonNode editorNode;
		try {
			editorNode = new ObjectMapper().readTree(service.getModelEditorSource(modelData.getId()));
		} catch (IOException e) {
			throw new SystemRuntimeException(e);
		}
		InputStream imageStream;
		try {
			// 先从extra里获取资源
			byte[] editorSourceExtra = service.getModelEditorSourceExtra(modelData.getId());
			if (editorSourceExtra != null) {
				imageStream = new ByteArrayInputStream(editorSourceExtra);
			} else {
				// 这一句用来解决图片乱码问题
				Context.setProcessEngineConfiguration((SpringProcessEngineConfiguration) BeanFactory.getInstance()
						.getBean("processEngineConfiguration"));
				BpmnModel bpmnModel = jsonConverter.convertToBpmnModel(editorNode);
				BpmnXMLConverter xmlConverter = new BpmnXMLConverter();
				// 这里十分奇怪,非要把BpmnModel转成xml再转回去,输出的图片才正常.
				BpmnModel bpmnModel2 = xmlConverter.convertToBpmnModel(new InputStreamSource(new ByteArrayInputStream(
						xmlConverter.convertToXML(bpmnModel))), true, false);
				ProcessDiagramGenerator generator = new DefaultProcessDiagramGenerator();
				imageStream = generator.generatePngDiagram(bpmnModel2);
			}
		} catch (Throwable e) {// 处理空指针
			imageStream = new ByteArrayInputStream(new byte[0]);
		}
		Actions.download(request, response, modelData.getName() + ".png", imageStream);
	}

	/**
	 * 新的部署窗口
	 * 
	 * @param request
	 * @param response
	 */
	@SuppressWarnings("unchecked")
	public void deployWin(HttpServletRequest request, HttpServletResponse response) {

		RepositoryService service = FlowFactory.getRepositoryService();

		ObjectNode modelNode;
		try {
			modelNode = (ObjectNode) new ObjectMapper().readTree(service.getModelEditorSource(RequestUtils
					.getStringValue(request, "id")));
		} catch (IOException e) {
			throw new SystemRuntimeException(e);
		}
		String key;
		try {
			BpmnModel model = new BpmnJsonConverter().convertToBpmnModel(modelNode);
			key = model.getProcesses().get(0).getId();
			String name = model.getProcesses().get(0).getName();
			request.setAttribute("key", key);
			request.setAttribute("name", name);
		} catch (Exception e) {
			throw new SystemRuntimeException(ExceptionType.BUSINESS, "流程图有误,请打开[设计]模块检查.");
		}

		// 获取订单表
		List<TbTable> orderTables = new ArrayList<>();
		for (TbTable model : (List<TbTable>) ORMService.getInstance().queryAll(TbTable.class.getName())) {
			if (ModelKeyUtils.checkModel(OrderModelKeys.class, model)) {
				orderTables.add(model);
			}
		}
		request.setAttribute("orderTables", orderTables);

		Actions.includePage(request, response, Util.getPagePath(request, "deploy_win.jsp"));
	}

	/**
	 * 联动选择基础视图
	 * 
	 * @param request
	 * @param response
	 */
	@SuppressWarnings("unchecked")
	public void selectBasicView(HttpServletRequest request, HttpServletResponse response) {
		String tableName = RequestUtils.getStringValue(request, "tableName");
		String pdKey = RequestUtils.getStringValue(request, "pdKey");
		List<Map<String, Object>> list = ORMService.getInstance().query(
				"VwFlowBasic",
				new DataCondition().setStringEqual("tableName", tableName).setStringEqual("pdKey", pdKey)
						.setOrderByAsc("busiName").toEntity());
		List<Map<String, Object>> basicViews = new ArrayList<>();
		for (Map<String, Object> o : list) {
			Map<String, Object> view = new HashMap<>();
			view.put("viewKey", o.get("viewKey"));
			view.put("busiName", o.get("busiName"));
			basicViews.add(view);
		}
		Actions.showJson(request, response, basicViews);
	}

	/**
	 * 新的部署
	 * 
	 * @param request
	 * @param response
	 */
	@ActionAccess(level = SafeLevel.DEV_W)
	public void submitDeploy(HttpServletRequest request, HttpServletResponse response) {
		String id = RequestUtils.getStringValue(request, "id");
		int a1 = RequestUtils.getIntegerValue(request, "A1");
		int a2 = RequestUtils.getIntegerValue(request, "A2");
		int a3 = RequestUtils.getIntegerValue(request, "A3");
		int b1 = RequestUtils.getIntegerValue(request, "B1");

		String a1Text = RequestUtils.getStringValue(request, "A1_text");
		String a2Text = RequestUtils.getStringValue(request, "A2_text");
		String a3Text = RequestUtils.getStringValue(request, "A3_text");

		String tableName = RequestUtils.getStringValue(request, "tableName");
		String basicView = RequestUtils.getStringValue(request, "basicView");

		FlowManagerService service = BeanFactory.getInstance().getBean(FlowManagerService.class);
		ProcessDefinition pd = service.executeDeploy(id,
				new FlowManagerService.DeployConfig(a1 == 1, a1Text, tableName), new FlowManagerService.DeployConfig(
						a2 == 1, a2Text, null), new FlowManagerService.DeployConfig(a3 == 1, a3Text, null),
				new FlowManagerService.DeployConfig(b1 == 1, null, basicView));
		Actions.redirectInfoPage(request, response, "[" + pd.getName() + "]全新部署成功.唯一KEY:[" + pd.getKey() + "],当前版本["
				+ pd.getVersion() + "],请到[流程设置]菜单中进行配置.");
	}

	/**
	 * 升级部署窗口
	 * 
	 * @param request
	 * @param response
	 */
	public void upgradeWin(HttpServletRequest request, HttpServletResponse response) {
		RepositoryService service = FlowFactory.getRepositoryService();

		ObjectNode modelNode;
		try {
			modelNode = (ObjectNode) new ObjectMapper().readTree(service.getModelEditorSource(RequestUtils
					.getStringValue(request, "id")));
		} catch (IOException e) {
			throw new SystemRuntimeException(e);
		}
		String key;
		try {
			BpmnModel model = new BpmnJsonConverter().convertToBpmnModel(modelNode);
			key = model.getProcesses().get(0).getId();
			String name = model.getProcesses().get(0).getName();
			request.setAttribute("key", key);
			request.setAttribute("name", name);
		} catch (Exception e) {
			throw new SystemRuntimeException(ExceptionType.BUSINESS, "流程图有误,请打开[设计]模块检查.");
		}

		// 同步列表
		request.setAttribute("syncList", service.createProcessDefinitionQuery().processDefinitionKey(key)
				.orderByProcessDefinitionVersion().asc().list());

		Actions.includePage(request, response, Util.getPagePath(request, "upgrade_win.jsp"));
	}

	/**
	 * 升级部署
	 * 
	 * @param request
	 * @param response
	 */
	@ActionAccess(level = SafeLevel.DEV_W)
	public void submitUpgrade(HttpServletRequest request, HttpServletResponse response) {
		int version = RequestUtils.getIntegerValue(request, "version");
		if (version == 0) {
			throw new SystemRuntimeException(ExceptionType.BUSINESS, "请选择一个版本做为升级模板.");
		}

		FlowManagerService service = BeanFactory.getInstance().getBean(FlowManagerService.class);
		ProcessDefinition pd = service.executeUpgrade(RequestUtils.getStringValue(request, "id"), version);
		Actions.redirectInfoPage(request, response, "[" + pd.getName() + "]升级部署成功.唯一KEY:[" + pd.getKey() + "],从旧版本["
				+ version + "]升级到新版本[" + pd.getVersion() + "],请到[流程设置]菜单中进行配置.");
	}

	/**
	 * 打开设计图
	 * 
	 * @param request
	 * @param response
	 */
	@ActionAccess(level = SafeLevel.DEV_W)
	public void editor(HttpServletRequest request, HttpServletResponse response) {
		Actions.jump(request, response, "/service/editor?id=" + RequestUtils.getStringValue(request, "id"));
	}
}
