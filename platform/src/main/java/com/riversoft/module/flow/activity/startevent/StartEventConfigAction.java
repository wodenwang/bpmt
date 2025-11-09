/*
 * $HeadURL: $
 * $Id: $
 * Copyright (c) 2013 by Riversoft System, all rights reserved.
 */
package com.riversoft.module.flow.activity.startevent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.activiti.bpmn.model.BpmnModel;
import org.activiti.engine.RepositoryService;
import org.apache.commons.lang3.StringUtils;

import com.riversoft.core.BeanFactory;
import com.riversoft.core.db.DataCondition;
import com.riversoft.core.db.ORMService;
import com.riversoft.core.exception.ExceptionType;
import com.riversoft.core.exception.SystemRuntimeException;
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
import com.riversoft.platform.po.TbTable;
import com.riversoft.platform.po.WfPd;

/**
 * @author woden
 * 
 */
@ActivityType(NodeType.START_EVENT)
@SuppressWarnings("unchecked")
public class StartEventConfigAction extends BaseActivityConfigAction {

	@Override
	public void main(HttpServletRequest request, HttpServletResponse response, String pdId, String activityId) {

		request.setAttribute("pdId", pdId);

		// 先查找是否已关联基础视图
		WfPd pdConfig = (WfPd) ORMService.getInstance().findByPk(com.riversoft.platform.po.WfPd.class.getName(), pdId);
		if (pdConfig == null) {
			throw new SystemRuntimeException(ExceptionType.BUSINESS, "请先配置流程基础视图再对此节点进行设置.");
		}
		String viewKey = pdConfig.getBasicViewKey();// 基础视图关键字

		Map<String, Object> po = (Map<String, Object>) ORMService.getInstance().findHQL("from WfStartEvent where pdId = ?", pdId);
		if (po != null) {
			request.setAttribute("vo", po);
		}

		// 按钮设置
		List<Map<String, Object>> btns = new ArrayList<>();
		int defaultSort = 1000;// 默认排序
		// 暂存按钮
		{
			Map<String, Object> btn = (Map<String, Object>) ORMService.getInstance().findHQL("from WfStartEventBtnSave where pdId = ?", pdId);
			if (btn == null) {
				btn = new HashMap<>();
				btn.put("busiName", "保存");
				btn.put("icon", "disk");
				btn.put("checkType", "1");// groovy
				btn.put("checkScript", "return true;");
				btn.put("styleClass", "center");
				btn.put("sort", defaultSort++);
			}
			btn.put("name", "save");// 保存
			btns.add(btn);
		}
		// 启动按钮
		{
			Map<String, Object> btn = (Map<String, Object>) ORMService.getInstance().findHQL("from WfStartEventBtnStart where pdId = ?", pdId);
			if (btn == null) {
				btn = new HashMap<>();
				btn.put("busiName", "提交");
				btn.put("icon", "check");
				btn.put("checkType", "1");// groovy
				btn.put("checkScript", "return true;");
				btn.put("styleClass", "center");
				btn.put("sort", defaultSort++);
			}
			btn.put("name", "start");// 转发
			btns.add(btn);
		}

		Collections.sort(btns, new Comparator<Map<String, Object>>() {
			@Override
			public int compare(Map<String, Object> o1, Map<String, Object> o2) {
				return (int) o1.get("sort") < (int) o2.get("sort") ? -1 : 1;
			}
		});
		request.setAttribute("btns", btns);

		// 处理器
		List<Map<String, Object>> beforeExecList = ORMService.getInstance().queryHQL("from WfStartEventExecBefore where pdId = ? order by sort asc", pdId);
		List<Map<String, Object>> afterExecList = ORMService.getInstance().queryHQL("from WfStartEventExecAfter where pdId = ? order by sort asc", pdId);
		request.setAttribute("beforeExecList", beforeExecList);
		request.setAttribute("afterExecList", afterExecList);

		RepositoryService service = FlowFactory.getRepositoryService();
		BpmnModel bpmnModel = service.getBpmnModel(pdId);
		BpmnHelper.Node node = BpmnHelper.getNode(bpmnModel, activityId);
		request.setAttribute("node", node);

		// 子表tab
		List<Map<String, Object>> basicSubs = new ArrayList<>();
		basicSubs.addAll((List<Map<String, Object>>) ORMService.getInstance().query("VwFlowBasicSubView", new DataCondition().setStringEqual("viewKey", viewKey).toEntity()));
		basicSubs.addAll((List<Map<String, Object>>) ORMService.getInstance().query("VwFlowBasicSubSys", new DataCondition().setStringEqual("viewKey", viewKey).toEntity()));
		List<Map<String, Object>> subs = new ArrayList<>();
		for (Map<String, Object> o : basicSubs) {
			String subKey = (String) o.get("subKey");
			Map<String, Object> extendVO = (Map<String, Object>) ORMService.getInstance().findHQL("from WfStartEventSubExtend where pdId = ? and subKey = ?", pdId, subKey);

			if (extendVO == null) {
				extendVO = new HashMap<>();
				extendVO.put("showFlag", 0);
				extendVO.put("sort", 1000 + (int) o.get("sort"));// 无配置则按序排到尾部
			}

			extendVO.put("busiName", o.get("busiName"));
			extendVO.put("subKey", subKey);
			subs.add(extendVO);
		}
		Collections.sort(subs, new Comparator<Map<String, Object>>() {
			@Override
			public int compare(Map<String, Object> o1, Map<String, Object> o2) {
				return (int) o1.get("sort") < (int) o2.get("sort") ? -1 : 1;
			}
		});
		request.setAttribute("subs", subs);

		Actions.includePage(request, response, Util.getPagePath(request, "config.jsp"));
	}

	/**
	 * 界面排版
	 * 
	 * @param request
	 * @param response
	 */
	public void frameSetting(HttpServletRequest request, HttpServletResponse response) {
		Actions.includePage(request, response, Util.getPagePath(request, "frame_setting.jsp"));
	}

	/**
	 * 字段排版设置
	 * 
	 * @param request
	 * @param response
	 */
	public void columnSort(HttpServletRequest request, HttpServletResponse response) {
		String pdId = RequestUtils.getStringValue(request, "pdId");

		// 先查找是否已关联基础视图
		WfPd pdConfig = (WfPd) ORMService.getInstance().findByPk(com.riversoft.platform.po.WfPd.class.getName(), pdId);
		if (pdConfig == null) {
			throw new SystemRuntimeException(ExceptionType.BUSINESS, "请先配置流程基础视图再对此节点进行设置.");
		}

		List<Map<String, Object>> list = new ArrayList<>();

		String viewKey = pdConfig.getBasicViewKey();// 基础视图关键字
		List<Map<String, Object>> basicColumns = new ArrayList<>();
		basicColumns.addAll((List<Map<String, Object>>) ORMService.getInstance().query("VwFlowBasicColumnShow", new DataCondition().setStringEqual("viewKey", viewKey).toEntity()));
		basicColumns.addAll((List<Map<String, Object>>) ORMService.getInstance().query("VwFlowBasicColumnLine", new DataCondition().setStringEqual("viewKey", viewKey).toEntity()));
		Map<String, Object> basicView = (Map<String, Object>) ORMService.getInstance().findByPk("VwFlowBasic", viewKey);
		int maxCol = (Integer) basicView.get("col");

		for (Map<String, Object> o : basicColumns) {
			String pixelKey = (String) o.get("pixelKey");
			if (StringUtils.isEmpty(pixelKey)) {
				// 无继承,不处理
				continue;
			}
			Map<String, Object> extendVO = (Map<String, Object>) ORMService.getInstance().findHQL("from WfStartEventColumnExtend where pdId = ? and pixelKey = ?", pdId, pixelKey);

			if (extendVO == null) {
				// 无继承,不处理
				continue;
			}

			extendVO.put("busiName", o.get("busiName"));
			extendVO.put("pixelKey", o.get("pixelKey"));

			String type = (String) o.get("$type$");// hibernate内置
			switch (type) {
			case "VwFlowBasicColumnShow":
				extendVO.put("color", "blue");
				extendVO.put("icon", "/css/icon/application.png");
				extendVO.put("type", "show");
				extendVO.put("sizex", (Integer) o.get("whole") >= 1 ? maxCol : 1);
				break;
			case "VwFlowBasicColumnLine":
				extendVO.put("color", "blue");
				extendVO.put("icon", "/css/icon/bookmark.png");
				extendVO.put("type", "line");
				extendVO.put("sizex", maxCol);
				break;
			default:
				break;
			}

			Integer showFlag = (Integer) extendVO.get("showFlag");
			if (showFlag != null && showFlag.intValue() == 1) {// 继承
				list.add(extendVO);
			}

		}

		// 自定义表单
		for (Map<String, Object> o : (List<Map<String, Object>>) ORMService.getInstance().queryHQL("from WfStartEventColumnForm where pdId = ?", pdId)) {
			o.put("color", "green");
			o.put("icon", "/css/icon/application_form.png");
			o.put("sizex", (Integer) o.get("whole") >= 1 ? maxCol : 1);
			o.put("type", "self_form");
			list.add(o);
		}

		// 自定义分割线
		for (Map<String, Object> o : (List<Map<String, Object>>) ORMService.getInstance().queryHQL("from WfStartEventColumnLine where pdId = ?", pdId)) {
			o.put("color", "green");
			o.put("icon", "/css/icon/bookmark.png");
			o.put("sizex", maxCol);
			o.put("type", "self_line");
			list.add(o);
		}

		Collections.sort(list, new Comparator<Map<String, Object>>() {
			@Override
			public int compare(Map<String, Object> o1, Map<String, Object> o2) {
				int sort1 = (int) o1.get("sort");
				int sort2 = (int) o2.get("sort");
				if (sort1 == sort2) {
					return 0;
				} else if (sort1 < sort2) {
					return -1;
				} else {
					return 1;
				}
			}
		});

		// 处理col和row值
		int row = 1;// 当前row
		int col = 1;// 当前col
		for (int i = 0; i < list.size(); i++) {
			// 进位
			if (col > maxCol) {
				row++;
				col = 1;
			}

			Map<String, Object> o = list.get(i);
			if ((Integer) o.get("sizex") >= maxCol) {// 占据整行
				if (col > 1) {
					row++;
				}
				o.put("col", 1);
				o.put("row", row);
				col = maxCol;
			} else {
				o.put("col", col);
				o.put("row", row);
			}
			col++;
		}

		request.setAttribute("list", list);
		request.setAttribute("maxCols", maxCol);

		Actions.includePage(request, response, Util.getPagePath(request, "column_sort.jsp"));
	}

	/**
	 * 表单字段设置
	 * 
	 * @param request
	 * @param response
	 */
	public void columnFrom(HttpServletRequest request, HttpServletResponse response) {

		String pdId = RequestUtils.getStringValue(request, "pdId");
		boolean extendFlag = StringUtils.equalsIgnoreCase(RequestUtils.getStringValue(request, "extendFlag"), "true");// 一键继承标识
		boolean sortFlag = StringUtils.equalsIgnoreCase(RequestUtils.getStringValue(request, "sortFlag"), "true");// 一键继承排序

		// 先查找是否已关联基础视图
		WfPd pdConfig = (WfPd) ORMService.getInstance().findByPk(com.riversoft.platform.po.WfPd.class.getName(), pdId);
		if (pdConfig == null) {
			throw new SystemRuntimeException(ExceptionType.BUSINESS, "请先配置流程基础视图再对此节点进行设置.");
		}

		final String cp = Actions.Util.getContextPath(request);
		List<Map<String, Object>> columns = new ArrayList<>();
		String viewKey = pdConfig.getBasicViewKey();// 基础视图关键字
		List<Map<String, Object>> basicColumns = new ArrayList<>();
		basicColumns.addAll((List<Map<String, Object>>) ORMService.getInstance().query("VwFlowBasicColumnShow", new DataCondition().setStringEqual("viewKey", viewKey).toEntity()));
		basicColumns.addAll((List<Map<String, Object>>) ORMService.getInstance().query("VwFlowBasicColumnLine", new DataCondition().setStringEqual("viewKey", viewKey).toEntity()));

		for (Map<String, Object> o : basicColumns) {
			String pixelKey = (String) o.get("pixelKey");
			if (StringUtils.isEmpty(pixelKey)) {
				pixelKey = "_tmp";
			}
			Map<String, Object> extendVO = (Map<String, Object>) ORMService.getInstance().findHQL("from WfStartEventColumnExtend where pdId = ? and pixelKey = ?", pdId, pixelKey);

			if (extendVO == null) {
				extendVO = new HashMap<>();
				extendVO.put("showFlag", 0);
				extendVO.put("sort", 1000 + (int) o.get("sort"));// 无配置则按序排到尾部
			}

			extendVO.put("busiName", o.get("busiName"));
			extendVO.put("id", o.get("id"));
			extendVO.put("pixelKey", o.get("pixelKey"));

			String type = (String) o.get("$type$");// hibernate内置
			switch (type) {
			case "VwFlowBasicColumnShow":
				extendVO.put("title", "展示字段");
				extendVO.put("icon", cp + "/css/icon/application.png");
				extendVO.put("_type", "show");
				extendVO.put("_id", "show_" + o.get("id"));
				break;
			case "VwFlowBasicColumnLine":
				extendVO.put("title", "分割线");
				extendVO.put("icon", cp + "/css/icon/bookmark.png");
				extendVO.put("_type", "line");
				extendVO.put("_id", "line_" + o.get("id"));
				break;

			default:
				break;
			}

			if (extendFlag) {// 一键继承时,并且showFlag默认值为1
				extendVO.put("showFlag", 1);
			}

			if (sortFlag) {// 继承基础字段的排序
				extendVO.put("sort", o.get("sort"));
			}

			Integer showFlag = (Integer) extendVO.get("showFlag");
			if (showFlag != null && showFlag.intValue() == 1) {// 继承
				extendVO.put("color", "blue");
			} else {// 不继承
				extendVO.put("color", "gray");
			}
			extendVO.put("hasExtend", true);// 允许继承

			columns.add(extendVO);
		}

		// 自定义表单
		for (Map<String, Object> o : (List<Map<String, Object>>) ORMService.getInstance().queryHQL("from WfStartEventColumnForm where pdId = ?", pdId)) {
			o.put("hasRemove", true);// 允许删除
			o.put("title", "用户节点表单");
			o.put("color", "green");
			o.put("icon", cp + "/css/icon/application_form.png");
			o.put("_type", "self_form");
			o.put("_id", "selftform_" + o.get("id"));

			if (sortFlag) {// 继承基础字段的排序
				// 查基础表单有没有相应的名字
				List<Map<String, Object>> tmpColumns = (List<Map<String, Object>>) ORMService.getInstance().query("VwFlowBasicColumnShow",
						new DataCondition().setStringEqual("viewKey", viewKey).setStringEqual("busiName", (String) o.get("busiName")).setOrderByAsc("sort").toEntity());
				if (tmpColumns != null && tmpColumns.size() > 0) {
					o.put("sort", tmpColumns.get(0).get("sort"));
				} else {
					o.put("sort", 9999);
				}
			}

			columns.add(o);
		}

		// 自定义分割线
		for (Map<String, Object> o : (List<Map<String, Object>>) ORMService.getInstance().queryHQL("from WfStartEventColumnLine where pdId = ?", pdId)) {
			o.put("hasRemove", true);// 允许删除
			o.put("title", "用户节点分割线");
			o.put("color", "green");
			o.put("icon", cp + "/css/icon/bookmark.png");
			o.put("_type", "self_line");
			o.put("_id", "selftline_" + o.get("id"));
			if (sortFlag) {// 继承基础字段的排序
				List<Map<String, Object>> tmpColumns = (List<Map<String, Object>>) ORMService.getInstance().query("VwFlowBasicColumnLine",
						new DataCondition().setStringEqual("viewKey", viewKey).setStringEqual("busiName", (String) o.get("busiName")).setOrderByAsc("sort").toEntity());
				if (tmpColumns != null && tmpColumns.size() > 0) {
					o.put("sort", tmpColumns.get(0).get("sort"));
				} else {
					o.put("sort", 9999);
				}
			}
			columns.add(o);
		}

		Collections.sort(columns, new Comparator<Map<String, Object>>() {
			@Override
			public int compare(Map<String, Object> o1, Map<String, Object> o2) {
				int sort1 = (int) o1.get("sort");
				int sort2 = (int) o2.get("sort");
				if (sort1 == sort2) {
					return 0;
				} else if (sort1 < sort2) {
					return -1;
				} else {
					return 1;
				}
			}
		});
		request.setAttribute("columns", columns);

		// 获取对应表名
		Map<String, Object> viewConfig = (Map<String, Object>) ORMService.getInstance().findByPk("VwFlowBasic", pdConfig.getBasicViewKey());
		if (viewConfig != null) {
			request.setAttribute("tableName", viewConfig.get("tableName"));
		}

		Actions.includePage(request, response, Util.getPagePath(request, "column.jsp"));
	}

	/**
	 * 继承字段配置
	 * 
	 * @param request
	 * @param response
	 */
	public void columnExtendConfigForm(HttpServletRequest request, HttpServletResponse response) {
		Long id = RequestUtils.getLongValue(request, "id");
		String type = RequestUtils.getStringValue(request, "type");// 类型;show,form,line
		String pdId = RequestUtils.getStringValue(request, "pdId");

		if (id == null || id.intValue() == 0) {
			throw new SystemRuntimeException(ExceptionType.CONFIG, "字段不存在.");
		}

		String ormTableName;
		switch (type) {
		case "show":
			ormTableName = "VwFlowBasicColumnShow";
			break;
		case "line":
			ormTableName = "VwFlowBasicColumnLine";
			break;
		default:
			throw new SystemRuntimeException(ExceptionType.CONFIG, "字段类型不存在.");
		}
		Map<String, Object> vo = (Map<String, Object>) ORMService.getInstance().findByPk(ormTableName, id);

		String pixelKey = (String) vo.get("pixelKey");
		Map<String, Object> extendVO = null;
		if (StringUtils.isEmpty(pixelKey)) {
			pixelKey = "_tmp";
		}
		extendVO = (Map<String, Object>) ORMService.getInstance().findHQL("from WfStartEventColumnExtend where pdId = ? and pixelKey = ?", pdId, pixelKey);

		if (extendVO == null) {
			extendVO = new HashMap<>();
			extendVO.put("pixelKey", vo.get("pixelKey"));
			extendVO.put("showFlag", 0);
		}
		request.setAttribute("type", type);
		request.setAttribute("vo", extendVO);

		Actions.includePage(request, response, Util.getPagePath(request, "column_extend.jsp"));
	}

	/**
	 * 表单字段配置
	 * 
	 * @param request
	 * @param response
	 */
	public void columnSelfLineConfigForm(HttpServletRequest request, HttpServletResponse response) {
		Long id = RequestUtils.getLongValue(request, "id");
		if (id != null) {
			Map<String, Object> vo = (Map<String, Object>) ORMService.getInstance().findByPk("WfStartEventColumnLine", id);
			request.setAttribute("vo", vo);
		}

		Actions.includePage(request, response, Util.getPagePath(request, "column_line.jsp"));
	}

	/**
	 * 表单字段配置
	 * 
	 * @param request
	 * @param response
	 */
	public void columnSelfFormConfigForm(HttpServletRequest request, HttpServletResponse response) {
		String tableName = RequestUtils.getStringValue(request, "tableName");
		if (StringUtils.isNotEmpty(tableName)) {
			TbTable tbTable = (TbTable) ORMService.getInstance().findByPk(TbTable.class.getName(), tableName);
			if (tbTable == null) {
				throw new SystemRuntimeException(ExceptionType.BUSINESS, "表[" + tableName + "]已删除,请重新选择表.");
			}
			request.setAttribute("table", tbTable);
		}

		Long id = RequestUtils.getLongValue(request, "id");
		if (id != null) {
			Map<String, Object> vo = (Map<String, Object>) ORMService.getInstance().findByPk("WfStartEventColumnForm", id);
			request.setAttribute("vo", vo);
		}

		Actions.includePage(request, response, Util.getPagePath(request, "column_form.jsp"));
	}

	/**
	 * 增加前/后置处理器
	 * 
	 * @param request
	 * @param response
	 */
	public void addExecTab(HttpServletRequest request, HttpServletResponse response) {
		Actions.includePage(request, response, Util.getPagePath(request, "exec.jsp"));
	}

	/**
	 * 提交保存
	 * 
	 * @param request
	 * @param response
	 */
	@ActionAccess(level = SafeLevel.DEV_W)
	public void submitForm(HttpServletRequest request, HttpServletResponse response) {
		BeanFactory.getInstance().getBean(StartEventConfigService.class).executeConfig();
		Actions.redirectInfoPage(request, response, "保存成功.");
	}
}
