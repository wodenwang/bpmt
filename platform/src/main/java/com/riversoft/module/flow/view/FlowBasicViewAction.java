/*
 * $HeadURL: $
 * $Id: $
 * Copyright (c) 2013 by Riversoft System, all rights reserved.
 */
package com.riversoft.module.flow.view;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.riversoft.core.BeanFactory;
import com.riversoft.core.db.DataCondition;
import com.riversoft.core.db.DataPO;
import com.riversoft.core.db.ORMService;
import com.riversoft.core.exception.ExceptionType;
import com.riversoft.core.exception.SystemRuntimeException;
import com.riversoft.core.web.Actions;
import com.riversoft.core.web.Actions.Util;
import com.riversoft.core.web.RequestUtils;
import com.riversoft.flow.key.OrderHistoryModelKeys;
import com.riversoft.flow.key.OrderModelKeys;
import com.riversoft.platform.db.model.ModelKeyUtils;
import com.riversoft.platform.po.TbColumn;
import com.riversoft.platform.po.TbTable;
import com.riversoft.platform.po.VwUrl;
import com.riversoft.platform.web.view.BaseDynamicViewAction;
import com.riversoft.platform.web.view.annotation.Conf;
import com.riversoft.platform.web.view.annotation.Conf.TargetType;
import com.riversoft.platform.web.view.annotation.PriConfigMethod;
import com.riversoft.platform.web.view.annotation.View;
import com.riversoft.platform.web.view.annotation.View.Group;
import com.riversoft.platform.web.view.annotation.View.LoginType;
import com.riversoft.util.dynamicbean.DynamicClassLoader;

/**
 * 工作流通用表单
 * 
 * @author woden
 * 
 */
@PriConfigMethod("priList")
@View(group = Group.SYS, value = "flowbasic", loginType = { LoginType.USER })
@Conf(description = "工作流视图", sort = 2, target = { TargetType.HOME, TargetType.MENU, TargetType.BTN, TargetType.SUB, TargetType.WX })
public class FlowBasicViewAction extends BaseDynamicViewAction {
	/**
	 * Logger for this class
	 */
	private static final Logger logger = LoggerFactory.getLogger(FlowBasicViewAction.class);

	@Override
	protected void main(HttpServletRequest request, HttpServletResponse response, String key) {
		String actionName = "A" + key.replace("-", "$") + "Action";
		String javaName = FlowBasicViewAction.class.getPackage().getName() + "." + actionName;
		try {
			BaseFlowBasicAction action = (BaseFlowBasicAction) BeanFactory.getInstance().getSingleBean(Class.forName(javaName));
			logger.debug("类已创建.");
		} catch (ClassNotFoundException | NoClassDefFoundError e) {
			StringBuffer src = new StringBuffer();
			src.append("package com.riversoft.module.flow.view;\n");
			src.append("public class ").append(actionName).append(" extends BaseFlowBasicAction {\n");
			src.append("\tpublic ").append(actionName).append("() {\n");
			src.append("\t\t").append("super(\"").append(key).append("\");\n");
			src.append("\t}\n");
			src.append("}");
			logger.debug("动态java名字:" + javaName);
			logger.debug("动态编译:\n" + src);
			try {
				Class<? extends BaseFlowBasicAction> actionClass = (Class<? extends BaseFlowBasicAction>) DynamicClassLoader.getInstance().compileAndLoadClass(javaName, src.toString());
			} catch (ClassNotFoundException | NoClassDefFoundError e1) {
				logger.warn("编译出错,递归继续编译.", e1);
			}
			main(request, response, key);
			return;

		}
		String actionUrl = "/flow/view/" + actionName + "/index.shtml";
		Actions.forwardAction(request, response, actionUrl);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void configForm(HttpServletRequest request, HttpServletResponse response, String key) {

		// 工作流视图不允许新增
		if (StringUtils.isEmpty(key)) {
			throw new SystemRuntimeException(ExceptionType.BUSINESS, "[工作流基础视图]不允许主动创建,请使用[流程部署]模块自动生成.");
		}

		if (!StringUtils.isEmpty(key)) {
			Map<String, Object> table = (Map<String, Object>) ORMService.getInstance().findByPk("VwFlowBasic", key);

			request.setAttribute("mainColumns",
					ORMService.getInstance().query(TbColumn.class.getName(), new DataCondition().setStringEqual("tableName", (String) table.get("tableName")).setOrderByAsc("sort").toEntity()));
			request.setAttribute("table", table);
		}

		List<TbTable> tables = new ArrayList<>();
		List<TbTable> historyTables = new ArrayList<>();
		List<TbTable> sysTables = (List<TbTable>) ORMService.getInstance().queryAll(TbTable.class.getName());

		for (TbTable model : sysTables) {
			if (ModelKeyUtils.checkModel(OrderModelKeys.class, model)) {
				tables.add(model);
			}
		}

		for (TbTable model : sysTables) {
			if (ModelKeyUtils.checkModel(OrderHistoryModelKeys.class, model)) {
				historyTables.add(model);
			}
		}

		request.setAttribute("tables", tables);
		request.setAttribute("historyTables", historyTables);
		Actions.includePage(request, response, Actions.Util.getPagePath(request, "flow_basic_config.jsp"));
	}

	/**
	 * 字段配置
	 * 
	 * @param request
	 * @param response
	 */
	@SuppressWarnings("unchecked")
	public void columnConfigForm(HttpServletRequest request, HttpServletResponse response) {
		String key = RequestUtils.getStringValue(request, "key");
		String tableName = RequestUtils.getStringValue(request, "tableName");

		if (StringUtils.isEmpty(tableName)) {
			throw new SystemRuntimeException(ExceptionType.BUSINESS, "请先选择主表.");
		}

		Map<String, Object> table = null;
		if (!StringUtils.isEmpty(key)) {
			table = (Map<String, Object>) ORMService.getInstance().findByPk("VwFlowBasic", key);
			request.setAttribute("table", table);
		}

		final String cp = Actions.Util.getContextPath(request);
		List<Map<String, Object>> list = new ArrayList<>();
		if (table != null) {
			for (Map<String, Object> obj : (Set<Map<String, Object>>) table.get("showColumns")) {
				obj.put("icon", cp + "/css/icon/application.png");
				obj.put("_type", "show");
				obj.put("_id", "show_" + obj.get("id"));
				obj.put("title", "展示字段");
				list.add(obj);
			}
			for (Map<String, Object> obj : (Set<Map<String, Object>>) table.get("lineColumns")) {
				obj.put("icon", cp + "/css/icon/bookmark.png");
				obj.put("_type", "line");
				obj.put("_id", "line_" + obj.get("id"));
				obj.put("title", "分割线");
				list.add(obj);
			}
		}

		// 排序
		Collections.sort(list, new Comparator<Map<String, Object>>() {
			@Override
			public int compare(Map<String, Object> o1, Map<String, Object> o2) {
				return (Integer) o1.get("sort") < (Integer) o2.get("sort") ? -1 : 1;
			}
		});

		request.setAttribute("tableName", tableName);
		request.setAttribute("list", list);
		Actions.includePage(request, response, Actions.Util.getPagePath(request, "flow_basic_column.jsp"));
	}

	/**
	 * 微信企业号设置
	 * 
	 * @param request
	 * @param response
	 */
	public void weixinSetting(HttpServletRequest request, HttpServletResponse response) {
		String key = RequestUtils.getStringValue(request, "key");
		Map<String, Object> wxConfig = (Map<String, Object>) ORMService.getInstance().findByPk("VwFlowBasicWeixin", key);
		request.setAttribute("vo", wxConfig);

		Actions.includePage(request, response, Util.getPagePath(request, "flow_basic_weixin_setting.jsp"));
	}

	/**
	 * 界面排版
	 * 
	 * @param request
	 * @param response
	 */
	public void frameSetting(HttpServletRequest request, HttpServletResponse response) {
		Actions.includePage(request, response, Util.getPagePath(request, "flow_basic_frame_setting.jsp"));
	}

	/**
	 * 列表页排序
	 * 
	 * @param request
	 * @param response
	 */
	public void sortList(HttpServletRequest request, HttpServletResponse response) {
		String key = RequestUtils.getStringValue(request, "key");

		Map<String, Object> table = null;
		if (!StringUtils.isEmpty(key)) {
			table = (Map<String, Object>) ORMService.getInstance().findByPk("VwFlowBasic", key);
			request.setAttribute("table", table);
		}

		if (table == null) {
			throw new SystemRuntimeException(ExceptionType.BUSINESS, "新增时不支持界面拖拽设计.");
		}

		List<Map<String, Object>> waitList = new ArrayList<>();
		List<Map<String, Object>> list = new ArrayList<>();
		List<Map<String, Object>> columns = new ArrayList<>();
		columns.addAll((Set<Map<String, Object>>) table.get("showColumns"));
		for (Map<String, Object> column : columns) {
			Integer listSort = (Integer) column.get("listSort");

			column.put("color", "blue");
			column.put("icon", "/css/icon/application.png");
			column.put("type", "show");
			column.put("title", "展示字段");

			if (listSort >= 0) {
				list.add(column);
			} else {
				waitList.add(column);
			}
		}

		Collections.sort(list, new Comparator<Map<String, Object>>() {
			@Override
			public int compare(Map<String, Object> o1, Map<String, Object> o2) {
				Integer sort1 = (Integer) o1.get("listSort");
				Integer sort2 = (Integer) o2.get("listSort");
				if (sort1 == null) {
					sort1 = 0;
				}
				if (sort2 == null) {
					sort2 = 0;
				}

				if (sort1 < sort2) {
					return -1;
				} else if (sort1 == sort2) {
					return 0;
				} else {
					return 1;
				}
			}
		});

		request.setAttribute("waitList", waitList);
		request.setAttribute("list", list);

		Actions.includePage(request, response, Actions.Util.getPagePath(request, "flow_basic_sort_list.jsp"));
	}

	/**
	 * 字段排版设置
	 * 
	 * @param request
	 * @param response
	 */
	public void columnSort(HttpServletRequest request, HttpServletResponse response) {
		String key = RequestUtils.getStringValue(request, "key");

		Map<String, Object> table = null;
		if (!StringUtils.isEmpty(key)) {
			table = (Map<String, Object>) ORMService.getInstance().findByPk("VwFlowBasic", key);
			request.setAttribute("table", table);
		}

		if (table == null) {
			throw new SystemRuntimeException(ExceptionType.BUSINESS, "新增时不支持界面拖拽设计.");
		}

		int maxCol = (Integer) table.get("col");
		List<Map<String, Object>> list = new ArrayList<>();
		for (Map<String, Object> obj : (Set<Map<String, Object>>) table.get("showColumns")) {
			obj.put("color", "blue");
			obj.put("icon", "/css/icon/application.png");
			obj.put("type", "show");
			obj.put("title", "展示字段");
			obj.put("sizex", (Integer) obj.get("whole") >= 1 ? maxCol : 1);
			list.add(obj);
		}
		for (Map<String, Object> obj : (Set<Map<String, Object>>) table.get("lineColumns")) {
			obj.put("color", "blue");
			obj.put("icon", "/css/icon/bookmark.png");
			obj.put("type", "line");
			obj.put("title", "分割线");
			obj.put("sizex", maxCol);
			list.add(obj);
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

		Actions.includePage(request, response, Util.getPagePath(request, "flow_basic_column_sort.jsp"));
	}

	/**
	 * 展示类字段配置
	 * 
	 * @param request
	 * @param response
	 */
	public void columnShowConfigForm(HttpServletRequest request, HttpServletResponse response) {
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
			Map<String, Object> vo = (Map<String, Object>) ORMService.getInstance().findByPk("VwFlowBasicColumnShow", id);
			request.setAttribute("vo", vo);
		}

		Actions.includePage(request, response, Actions.Util.getPagePath(request, "flow_basic_column_show.jsp"));
	}

	/**
	 * 分割线字段配置
	 * 
	 * @param request
	 * @param response
	 */
	public void columnLineConfigForm(HttpServletRequest request, HttpServletResponse response) {
		Long id = RequestUtils.getLongValue(request, "id");
		if (id != null) {
			Map<String, Object> vo = (Map<String, Object>) ORMService.getInstance().findByPk("VwFlowBasicColumnLine", id);
			request.setAttribute("vo", vo);
		}

		Actions.includePage(request, response, Actions.Util.getPagePath(request, "flow_basic_column_line.jsp"));
	}

	/**
	 * 变量配置
	 * 
	 * @param request
	 * @param response
	 */
	public void varConfigForm(HttpServletRequest request, HttpServletResponse response) {
		String key = RequestUtils.getStringValue(request, "key");

		List<Map<String, Object>> prepareExecs = new ArrayList<>();

		if (!StringUtils.isEmpty(key)) {
			Map<String, Object> table = (Map<String, Object>) ORMService.getInstance().findByPk("VwFlowBasic", key);
			if (table != null) {
				prepareExecs.addAll((Set<Map<String, Object>>) table.get("prepareExecs"));
			}
		}

		request.setAttribute("prepareExecs", prepareExecs);
		Actions.includePage(request, response, Actions.Util.getPagePath(request, "flow_basic_var_config.jsp"));
	}

	/**
	 * 增加数据准备处理器
	 * 
	 * @param request
	 * @param response
	 */
	public void prepareExecsForm(HttpServletRequest request, HttpServletResponse response) {
		Actions.includePage(request, response, Util.getPagePath(request, "flow_basic_var_prepare.jsp"));
	}

	/**
	 * 处理器配置
	 * 
	 * @param request
	 * @param response
	 */
	public void execConfigForm(HttpServletRequest request, HttpServletResponse response) {
		String key = RequestUtils.getStringValue(request, "key");

		List<Map<String, Object>> beforeExecs = new ArrayList<>();
		List<Map<String, Object>> afterExecs = new ArrayList<>();
		List<Map<String, Object>> prepareExecs = new ArrayList<>();

		if (!StringUtils.isEmpty(key)) {
			Map<String, Object> table = (Map<String, Object>) ORMService.getInstance().findByPk("VwFlowBasic", key);
			if (table != null) {
				beforeExecs.addAll((Set<Map<String, Object>>) table.get("beforeExecs"));
				afterExecs.addAll((Set<Map<String, Object>>) table.get("afterExecs"));
				prepareExecs.addAll((Set<Map<String, Object>>) table.get("prepareExecs"));
			}
		}

		request.setAttribute("beforeExecs", beforeExecs);
		request.setAttribute("afterExecs", afterExecs);
		request.setAttribute("prepareExecs", prepareExecs);

		Actions.includePage(request, response, Actions.Util.getPagePath(request, "flow_basic_exec_config.jsp"));
	}

	/**
	 * 增加前置处理器
	 * 
	 * @param request
	 * @param response
	 */
	public void beforeExecsForm(HttpServletRequest request, HttpServletResponse response) {
		Actions.includePage(request, response, Util.getPagePath(request, "flow_basic_exec_before.jsp"));
	}

	/**
	 * 增加后置处理器
	 * 
	 * @param request
	 * @param response
	 */
	public void afterExecsForm(HttpServletRequest request, HttpServletResponse response) {
		Actions.includePage(request, response, Util.getPagePath(request, "flow_basic_exec_after.jsp"));
	}

	/**
	 * 子表设置配置
	 * 
	 * @param request
	 * @param response
	 */
	@SuppressWarnings("unchecked")
	public void subsConfigForm(HttpServletRequest request, HttpServletResponse response) {
		String key = RequestUtils.getStringValue(request, "key");
		String tableName = RequestUtils.getStringValue(request, "tableName");

		if (StringUtils.isEmpty(tableName)) {
			throw new SystemRuntimeException(ExceptionType.BUSINESS, "请先选择主表.");
		}
		TbTable tbTable = (TbTable) ORMService.getInstance().findByPk(TbTable.class.getName(), tableName);
		if (tbTable == null) {
			throw new SystemRuntimeException(ExceptionType.BUSINESS, "表[" + tableName + "]已删除,请重新选择表.");
		}
		request.setAttribute("tbTable", tbTable);

		List<Map<String, Object>> subs = new ArrayList<>();
		Set<Map<String, Object>> sysSubs = null;
		if (!StringUtils.isEmpty(key)) {
			Map<String, Object> table = (Map<String, Object>) ORMService.getInstance().findByPk("VwFlowBasic", key);
			subs.addAll((Set<Map<String, Object>>) table.get("viewSubs"));
			sysSubs = (Set<Map<String, Object>>) table.get("sysSubs");
		}

		// 初始化设入系统内置按钮
		if (sysSubs != null && sysSubs.size() > 0) {
			subs.addAll(sysSubs);
		} else {
			Integer sort = 0;
			for (SysTab sysTab : SysTab.values()) {
				Map<String, Object> tab = sysTab.toMap();
				tab.put("sort", sort++);
				subs.add(tab);
			}
		}

		Collections.sort(subs, new Comparator<Map<String, Object>>() {
			@Override
			public int compare(Map<String, Object> o1, Map<String, Object> o2) {
				return (Integer) o1.get("sort") < (Integer) o2.get("sort") ? -1 : 1;
			}
		});

		request.setAttribute("subs", subs);

		Actions.includePage(request, response, Actions.Util.getPagePath(request, "flow_basic_sub_config.jsp"));
	}

	/**
	 * 添加视图标签
	 * 
	 * @param request
	 * @param response
	 */
	public void viewSubViewConfig(HttpServletRequest request, HttpServletResponse response) {
		Actions.includePage(request, response, Actions.Util.getPagePath(request, "flow_basic_sub_view_config.jsp"));
	}

	/**
	 * 普通查询条件
	 * 
	 * @param request
	 * @param response
	 */
	public void normalQueryForm(HttpServletRequest request, HttpServletResponse response) {
		String tableName = RequestUtils.getStringValue(request, "tableName");
		if (StringUtils.isNotEmpty(tableName)) {
			TbTable tbTable = (TbTable) ORMService.getInstance().findByPk(TbTable.class.getName(), tableName);
			if (tbTable == null) {
				throw new SystemRuntimeException(ExceptionType.BUSINESS, "表[" + tableName + "]已删除,请重新选择表.");
			}
			request.setAttribute("tbTable", tbTable);
		}

		Actions.includePage(request, response, Actions.Util.getPagePath(request, "flow_basic_query_normal.jsp"));
	}

	/**
	 * 高级查询条件
	 * 
	 * @param request
	 * @param response
	 */
	public void extQueryForm(HttpServletRequest request, HttpServletResponse response) {
		Actions.includePage(request, response, Actions.Util.getPagePath(request, "flow_basic_query_ext.jsp"));
	}

	/**
	 * 查询条件表单配置
	 * 
	 * @param request
	 * @param response
	 */
	public void queryConfigForm(HttpServletRequest request, HttpServletResponse response) {
		String key = RequestUtils.getStringValue(request, "key");
		String tableName = RequestUtils.getStringValue(request, "tableName");

		if (StringUtils.isEmpty(tableName)) {
			throw new SystemRuntimeException(ExceptionType.BUSINESS, "请先选择主表.");
		}
		TbTable tbTable = (TbTable) ORMService.getInstance().findByPk(TbTable.class.getName(), tableName);
		if (tbTable == null) {
			throw new SystemRuntimeException(ExceptionType.BUSINESS, "表[" + tableName + "]已删除,请重新选择表.");
		}
		request.setAttribute("tbTable", tbTable);

		List<Map<String, Object>> querys = new ArrayList<>();
		if (!StringUtils.isEmpty(key)) {
			Map<String, Object> table = (Map<String, Object>) ORMService.getInstance().findByPk("VwFlowBasic", key);

			Set<Map<String, Object>> normalQuerys = (Set<Map<String, Object>>) table.get("querys");
			if (normalQuerys != null) {
				querys.addAll(normalQuerys);
			}

			Set<Map<String, Object>> extQuerys = (Set<Map<String, Object>>) table.get("extQuerys");
			if (extQuerys != null) {
				querys.addAll(extQuerys);
			}

			Collections.sort(querys, new Comparator<Map<String, Object>>() {
				@Override
				public int compare(Map<String, Object> o1, Map<String, Object> o2) {
					return (Integer) o1.get("sort") < (Integer) o2.get("sort") ? -1 : 1;
				}
			});

			request.setAttribute("querys", querys);
		}

		Actions.includePage(request, response, Actions.Util.getPagePath(request, "flow_basic_query_config.jsp"));
	}

	/**
	 * 数据约束设置
	 * 
	 * @param request
	 * @param response
	 */
	public void limitConfigForm(HttpServletRequest request, HttpServletResponse response) {
		String key = RequestUtils.getStringValue(request, "key");
		String tableName = RequestUtils.getStringValue(request, "tableName");

		if (StringUtils.isEmpty(tableName)) {
			throw new SystemRuntimeException(ExceptionType.BUSINESS, "请先选择主表.");
		}
		TbTable tbTable = (TbTable) ORMService.getInstance().findByPk(TbTable.class.getName(), tableName);
		if (tbTable == null) {
			throw new SystemRuntimeException(ExceptionType.BUSINESS, "表[" + tableName + "]已删除,请重新选择表.");
		}
		request.setAttribute("tbTable", tbTable);

		List<Map<String, Object>> limits = new ArrayList<>();
		if (!StringUtils.isEmpty(key)) {
			Map<String, Object> table = (Map<String, Object>) ORMService.getInstance().findByPk("VwFlowBasic", key);

			Set<Map<String, Object>> limitSet = (Set<Map<String, Object>>) table.get("limits");
			if (limitSet != null) {
				limits.addAll(limitSet);
			}

			Collections.sort(limits, new Comparator<Map<String, Object>>() {
				@Override
				public int compare(Map<String, Object> o1, Map<String, Object> o2) {
					return (Integer) o1.get("sort") < (Integer) o2.get("sort") ? -1 : 1;
				}
			});
			request.setAttribute("limits", limits);
		}

		Actions.includePage(request, response, Actions.Util.getPagePath(request, "flow_basic_limit_config.jsp"));
	}

	/**
	 * 按钮设置
	 * 
	 * @param request
	 * @param response
	 */
	public void btnConfigForm(HttpServletRequest request, HttpServletResponse response) {
		String key = RequestUtils.getStringValue(request, "key");

		List<Map<String, Object>> itemBtns = new ArrayList<>();
		List<Map<String, Object>> summaryBtns = new ArrayList<>();

		Map<String, Object> table = null;
		if (!StringUtils.isEmpty(key)) {
			table = (Map<String, Object>) ORMService.getInstance().findByPk("VwFlowBasic", key);
		}

		if (table == null || ((Set<Map<String, Object>>) table.get("sysBtns")).size() < 1) {
			// 初始化设入系统内置按钮
			Integer sort = 0;
			for (SysBtn sysBtn : SysBtn.values()) {
				Map<String, Object> btn = sysBtn.toMap();
				btn.put("sort", sort++);
				if (sysBtn.getType() == 1) {
					itemBtns.add(btn);
				} else {
					summaryBtns.add(btn);
				}
			}
		}

		if (table != null) {
			itemBtns.addAll((Set<Map<String, Object>>) table.get("itemBtns"));
			summaryBtns.addAll((Set<Map<String, Object>>) table.get("summaryBtns"));
			Set<Map<String, Object>> sysBtns = (Set<Map<String, Object>>) table.get("sysBtns");
			if (sysBtns != null && sysBtns.size() > 0) {
				for (Map<String, Object> btn : sysBtns) {
					if (((Integer) btn.get("type")) == 1) {// 明细
						itemBtns.add(btn);
					} else {// 汇总
						summaryBtns.add(btn);
					}
				}
				
				// chris_为了添加新的系统按钮 20170301
				if (sysBtns.size() != SysBtn.values().length) { // 若size不同可能需要添加系统按钮
					for (SysBtn sysBtn : SysBtn.values()) {
						Map<String, Object> sbtn = sysBtn.toMap();
						Boolean joinType = false; // 已增入系统标示
						for (Map<String, Object> btn : sysBtns) { // 与系统的匹配
							if (((String) btn.get("name")).equals((String) sbtn.get("name"))) { // 已加入
								joinType = true;
								break;
							}
						}
						if (!joinType) { // 若没加入即为新增系统按钮
							sbtn.put("sort", 999);
							if (sysBtn.getType() == 1) {
								itemBtns.add(sbtn);
							} else {
								summaryBtns.add(sbtn);
							}
						}
					}
				}
			}
		}

		Collections.sort(itemBtns, new Comparator<Map<String, Object>>() {
			@Override
			public int compare(Map<String, Object> o1, Map<String, Object> o2) {
				return (Integer) o1.get("sort") < (Integer) o2.get("sort") ? -1 : 1;
			}
		});
		Collections.sort(summaryBtns, new Comparator<Map<String, Object>>() {
			@Override
			public int compare(Map<String, Object> o1, Map<String, Object> o2) {
				return (Integer) o1.get("sort") < (Integer) o2.get("sort") ? -1 : 1;
			}
		});

		request.setAttribute("itemBtns", itemBtns);
		request.setAttribute("summaryBtns", summaryBtns);
		Actions.includePage(request, response, Actions.Util.getPagePath(request, "flow_basic_btn.jsp"));
	}

	/**
	 * 明细按钮新增
	 * 
	 * @param request
	 * @param response
	 */
	public void itemBtnForm(HttpServletRequest request, HttpServletResponse response) {
		Actions.includePage(request, response, Actions.Util.getPagePath(request, "flow_basic_btn_item.jsp"));
	}

	/**
	 * 汇总按钮新增
	 * 
	 * @param request
	 * @param response
	 */
	public void summaryBtnForm(HttpServletRequest request, HttpServletResponse response) {
		Actions.includePage(request, response, Actions.Util.getPagePath(request, "flow_basic_btn_summary.jsp"));
	}

	/**
	 * 添加数据约束
	 * 
	 * @param request
	 * @param response
	 */
	public void limitForm(HttpServletRequest request, HttpServletResponse response) {
		Actions.includePage(request, response, Actions.Util.getPagePath(request, "flow_basic_limit.jsp"));
	}

	/**
	 * 消息通知
	 * 
	 * @param request
	 * @param response
	 */
	public void notifySetting(HttpServletRequest request, HttpServletResponse response) {
		String key = RequestUtils.getStringValue(request, "key");
		if (!StringUtils.isEmpty(key)) {
			request.setAttribute("vo", (Map<String, Object>) ORMService.getInstance().findByPk("VwFlowBasic", key));
		}
		Actions.includePage(request, response, Actions.Util.getPagePath(request, "flow_basic_notify.jsp"));
	}

	/**
	 * 权限资源列表
	 * 
	 * @param request
	 * @param response
	 */
	@SuppressWarnings("unchecked")
	public void priList(HttpServletRequest request, HttpServletResponse response) {
		String viewKey = RequestUtils.getStringValue(request, "viewKey");
		String groupId = RequestUtils.getStringValue(request, "groupId");

		VwUrl url = (VwUrl) ORMService.getInstance().findByPk(VwUrl.class.getName(), viewKey);
		request.setAttribute("vwUrl", url);

		List<String> priKeys = ORMService.getInstance().queryHQL("select priKey from CmPriGroupRelate where groupId = ?", groupId);
		request.setAttribute("priKeys", priKeys);

		if (!StringUtils.isEmpty(viewKey)) {
			Map<String, Object> table = (Map<String, Object>) ORMService.getInstance().findByPk("VwFlowBasic", viewKey);
			request.setAttribute("table", table);

			List<Map<String, Object>> fields = new ArrayList<>();
			fields.addAll((Set<Map<String, Object>>) table.get("showColumns"));
			fields.addAll((Set<Map<String, Object>>) table.get("lineColumns"));
			// 排序
			Collections.sort(fields, new Comparator<Map<String, Object>>() {
				@Override
				public int compare(Map<String, Object> o1, Map<String, Object> o2) {
					return (Integer) o1.get("sort") < (Integer) o2.get("sort") ? -1 : 1;
				}
			});
			request.setAttribute("fields", fields);

			List<Map<String, Object>> subs = new ArrayList<>();
			subs.addAll((Set<Map<String, Object>>) table.get("sysSubs"));
			subs.addAll((Set<Map<String, Object>>) table.get("viewSubs"));
			// 排序
			Collections.sort(subs, new Comparator<Map<String, Object>>() {
				@Override
				public int compare(Map<String, Object> o1, Map<String, Object> o2) {
					return (Integer) o1.get("sort") < (Integer) o2.get("sort") ? -1 : 1;
				}
			});
			request.setAttribute("subs", subs);
		}

		request.setAttribute("groupId", groupId);

		Actions.includePage(request, response, Actions.Util.getPagePath(request, "flow_basic_pri_list.jsp"));
	}

	@Override
	public void saveConfig(String key) {
		// table部分
		DataPO tablePO = new DataPO("VwFlowBasic");
		tablePO.set("viewKey", key);
		FlowBasicDataBuilder builder = new FlowBasicDataBuilder(tablePO);
		builder.build();
		ORMService.getInstance().save(tablePO.toEntity());
		builder.handleConfig();
	}

	@Override
	public void updateConfig(String key) {
		Map<String, Object> entity = (Map<String, Object>) ORMService.getInstance().loadByPk("VwFlowBasic", key);
		// table部分
		DataPO tablePO = new DataPO("VwFlowBasic", entity);
		if (entity == null) {
			throw new SystemRuntimeException(ExceptionType.BUSINESS, "视图已删除.");
		}
		FlowBasicDataBuilder builder = new FlowBasicDataBuilder(tablePO);
		builder.handleConfig();
		builder.build();
		// 这里要使用update
		ORMService.getInstance().merge(tablePO.toEntity());
	}

	@Override
	public void removeConfig(String key) {
		Map<String, Object> table = (Map<String, Object>) ORMService.getInstance().findByPk("VwFlowBasic", key);
		if (table != null) {
			ORMService.getInstance().remove(table);
		}
	}

	@Override
	public String copyConfig(String key) {
		throw new SystemRuntimeException(ExceptionType.BUSINESS, "此视图不支持复制.");
	}
}
