/*
 * $HeadURL: $
 * $Id: $
 * Copyright (c) 2013 by Riversoft System, all rights reserved.
 */
package com.riversoft.module.dyn;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.jumpmind.db.model.Column;
import org.jumpmind.db.model.Table;
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
import com.riversoft.platform.db.Types;
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
 * 动态表管理模块<br>
 * 此视图模块依赖sub(子表视图)
 * 
 * @author Woden
 * 
 */
@PriConfigMethod("priList")
@Conf(description = "动态表视图", sort = 1, target = { TargetType.MENU, TargetType.HOME, TargetType.BTN, TargetType.SUB,
		TargetType.WX })
@View(value = "dyn", group = Group.SYS, loginType = { LoginType.USER })
@SuppressWarnings("unchecked")
public class DynViewAction extends BaseDynamicViewAction {
	/**
	 * Logger for this class
	 */
	private static final Logger logger = LoggerFactory.getLogger(DynViewAction.class);

	@Override
	@SuppressWarnings("all")
	protected void main(HttpServletRequest request, HttpServletResponse response, String key) {
		String actionName = "A" + key.replace("-", "$") + "Action";
		String javaName = DynViewAction.class.getPackage().getName() + "." + actionName;
		try {
			BaseDynamicTableCRUDAction action = (BaseDynamicTableCRUDAction) BeanFactory.getInstance()
					.getSingleBean(Class.forName(javaName));
		} catch (ClassNotFoundException | NoClassDefFoundError e) {
			StringBuffer src = new StringBuffer();
			src.append("package com.riversoft.module.dyn;\n");
			src.append("public class ").append(actionName).append(" extends BaseDynamicTableCRUDAction {\n");
			src.append("\tpublic ").append(actionName).append("() {\n");
			src.append("\t\t").append("super(\"").append(key).append("\");\n");
			src.append("\t}\n");
			src.append("}");
			logger.debug("动态java名字:" + javaName);
			logger.debug("动态编译:\n" + src);
			try {
				Class<? extends BaseDynamicTableCRUDAction> actionClass = (Class<? extends BaseDynamicTableCRUDAction>) DynamicClassLoader
						.getInstance().compileAndLoadClass(javaName, src.toString());
			} catch (ClassNotFoundException | NoClassDefFoundError e1) {
				logger.warn("编译出错,递归继续编译.", e1);
			}
			main(request, response, key);
			return;
		}
		String actionUrl = "/dyn/" + actionName + "/index.shtml";
		Actions.forwardAction(request, response, actionUrl);
	}

	@Override
	public void configForm(HttpServletRequest request, HttpServletResponse response, String key) {

		request.setAttribute("tables", ORMService.getInstance().queryAll(TbTable.class.getName()));
		if (!StringUtils.isEmpty(key)) {
			Map<String, Object> table = (Map<String, Object>) ORMService.getInstance().findByPk("VwDynTable", key);
			request.setAttribute("table", table);
			List<Map<String, String>> dynLogTables = getCandidateLogTables((String) table.get("name"));
			request.setAttribute("logTables", dynLogTables);
		}
		Actions.includePage(request, response, Actions.Util.getPagePath(request, "view_crud_config.jsp"));
	}

	public void fetchLogTables(HttpServletRequest request, HttpServletResponse response) {
		String tableName = RequestUtils.getStringValue(request, "tableName");
		List<Map<String, String>> dynLogTables = getCandidateLogTables(tableName);
		Actions.showJson(request, response, dynLogTables);

	}

	private List<Map<String, String>> getCandidateLogTables(String tableName) {
		List<Map<String, String>> dynLogTables = new ArrayList<>();

		if (StringUtils.isNotEmpty(tableName)) {

			List<TbTable> allDynTables = (List<TbTable>) ORMService.getInstance().queryAll(TbTable.class.getName());
			TbTable tbTable = (TbTable) ORMService.getInstance().findByPk(TbTable.class.getName(), tableName);

			for (TbTable model : allDynTables) {
				if (ModelKeyUtils.checkModel(DynLogModelKeys.class, model) && samePK(tbTable.build(), model.build())
						&& (!sameTable(tbTable, model))) {
					Map<String, String> table = new HashMap<>();
					table.put("name", model.getName());
					table.put("description", model.getDescription());
					dynLogTables.add(table);
				}
			}
		}
		return dynLogTables;
	}

	private boolean sameTable(TbTable tbTable, TbTable model) {
		return tbTable.getName().equals(model.getName());
	}

	private boolean samePK(Table mainTable, Table logTable) {
		boolean result = true;
		List<Column> pks = mainTable.getPrimaryKeyColumnsAsList();
		for (Column mainPK : pks) {
			Column logPK = logTable.getColumnWithName(mainPK.getName());
			if (logPK == null) {
				result = false;
				break;
			} else {
				if (mainPK.getJdbcTypeCode() != logPK.getJdbcTypeCode()) {
					result = false;
					break;
				}
			}
		}

		return result;
	}

	/**
	 * 微信企业号设置
	 * 
	 * @param request
	 * @param response
	 */
	public void weixinSetting(HttpServletRequest request, HttpServletResponse response) {
		String key = RequestUtils.getStringValue(request, "key");
		Map<String, Object> wxConfig = (Map<String, Object>) ORMService.getInstance().findByPk("VwDynWeixin", key);
		request.setAttribute("vo", wxConfig);

		Actions.includePage(request, response, Util.getPagePath(request, "view_crud_weixin_setting.jsp"));
	}

	/**
	 * 界面排版
	 * 
	 * @param request
	 * @param response
	 */
	public void frameSetting(HttpServletRequest request, HttpServletResponse response) {
		Actions.includePage(request, response, Util.getPagePath(request, "view_crud_frame_setting.jsp"));
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
			table = (Map<String, Object>) ORMService.getInstance().findByPk("VwDynTable", key);
			request.setAttribute("table", table);
		}

		if (table == null) {
			throw new SystemRuntimeException(ExceptionType.BUSINESS, "新增时不支持界面拖拽设计.");
		}

		List<Map<String, Object>> waitList = new ArrayList<>();
		List<Map<String, Object>> list = new ArrayList<>();
		List<Map<String, Object>> columns = new ArrayList<>();
		columns.addAll((Set<Map<String, Object>>) table.get("columns"));
		columns.addAll((Set<Map<String, Object>>) table.get("showColumns"));
		for (Map<String, Object> column : columns) {
			if (column.get("showFlag") != null && ((Integer) column.get("showFlag")).intValue() == 0) {
				continue;
			}
			Integer listSort = (Integer) column.get("listSort");

			if (column.get("name") != null) {// 系统字段
				column.put("color", "black");
				column.put("icon", "/css/icon/table.png");
				column.put("type", "sys");
				column.put("title", "系统字段");
			} else {
				column.put("color", "blue");
				column.put("icon", "/css/icon/application.png");
				column.put("type", "show");
				column.put("title", "展示字段");
			}

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

		Actions.includePage(request, response, Actions.Util.getPagePath(request, "view_crud_sort_list.jsp"));
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
			table = (Map<String, Object>) ORMService.getInstance().findByPk("VwDynTable", key);
			request.setAttribute("table", table);
		}

		if (table == null) {
			throw new SystemRuntimeException(ExceptionType.BUSINESS, "新增时不支持界面拖拽设计.");
		}

		int maxCol = (Integer) table.get("col");
		List<Map<String, Object>> list = new ArrayList<>();
		for (Map<String, Object> obj : (Set<Map<String, Object>>) table.get("columns")) {
			obj.put("color", "black");
			obj.put("icon", "/css/icon/table.png");
			obj.put("type", "sys");
			obj.put("title", "系统字段");
			obj.put("sizex", (Integer) obj.get("whole") >= 1 ? maxCol : 1);
			list.add(obj);
		}
		for (Map<String, Object> obj : (Set<Map<String, Object>>) table.get("showColumns")) {
			obj.put("color", "blue");
			obj.put("icon", "/css/icon/application.png");
			obj.put("type", "show");
			obj.put("title", "展示字段");
			obj.put("sizex", (Integer) obj.get("whole") >= 1 ? maxCol : 1);
			list.add(obj);
		}
		for (Map<String, Object> obj : (Set<Map<String, Object>>) table.get("formColumns")) {
			obj.put("color", "green");
			obj.put("icon", "/css/icon/application_form.png");
			obj.put("type", "form");
			obj.put("title", "表单字段");
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

		Actions.includePage(request, response, Util.getPagePath(request, "view_crud_column_sort.jsp"));
	}

	/**
	 * 字段表单配置
	 * 
	 * @param request
	 * @param response
	 */
	public void columnConfigForm(HttpServletRequest request, HttpServletResponse response) {
		String key = RequestUtils.getStringValue(request, "key");
		String tableName = RequestUtils.getStringValue(request, "tableName");
		String cp = Util.getContextPath(request);

		if (StringUtils.isEmpty(tableName)) {
			throw new SystemRuntimeException(ExceptionType.BUSINESS, "请先选择主表.");
		}

		Map<String, Object> table = null;
		if (!StringUtils.isEmpty(key)) {
			table = (Map<String, Object>) ORMService.getInstance().findByPk("VwDynTable", key);
			request.setAttribute("table", table);
		}

		TbTable tbTable = (TbTable) ORMService.getInstance().findByPk(TbTable.class.getName(), tableName);
		if (tbTable == null) {
			throw new SystemRuntimeException(ExceptionType.BUSINESS, "表[" + tableName + "]已删除,请重新选择表.");
		}

		List<Map<String, Object>> list = new ArrayList<>();
		{
			for (TbColumn column : tbTable.getTbColumns()) {
				Map<String, Object> vo = null;

				if (!StringUtils.isEmpty(key)) {
					vo = (Map<String, Object>) ORMService.getInstance().find("VwDynColumn", new DataCondition()
							.setStringEqual("viewKey", key).setStringEqual("name", column.getName()).toEntity());
				}

				if (vo == null) {
					vo = new HashMap<>();
					vo.put("name", column.getName());
					vo.put("busiName", column.getDescription());
					vo.put("sort", 999);
					vo.put("listSort", 999);
					vo.put("showFlag", 1);
				}

				vo.put("color", "blue");// 固定键蓝色
				vo.put("icon", cp + "/css/icon/table.png");
				vo.put("_type", "sys");
				vo.put("_id", "sys_" + UUID.randomUUID().toString());
				vo.put("title", "固定字段");

				list.add(vo);
			}

			if (!StringUtils.isEmpty(key)) {
				if (table != null) {
					for (Map<String, Object> obj : (Set<Map<String, Object>>) table.get("showColumns")) {
						obj.put("icon", cp + "/css/icon/application.png");
						obj.put("_type", "show");
						obj.put("_id", "show_" + obj.get("id"));
						obj.put("title", "展示字段");
						obj.put("showFlag", obj.get("showFlag"));
						list.add(obj);
					}
					for (Map<String, Object> obj : (Set<Map<String, Object>>) table.get("formColumns")) {
						obj.put("icon", cp + "/css/icon/application_form.png");
						obj.put("_type", "form");
						obj.put("_id", "form_" + obj.get("id"));
						obj.put("title", "表单字段");
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
			}

			// 排序
			Collections.sort(list, new Comparator<Map<String, Object>>() {
				@Override
				public int compare(Map<String, Object> o1, Map<String, Object> o2) {
					if ((Integer) o1.get("sort") < (Integer) o2.get("sort")) {
						return -1;
					} else if ((Integer) o1.get("sort") > (Integer) o2.get("sort")) {
						return 1;
					} else {
						return 0;
					}
				}
			});
		}
		request.setAttribute("list", list);

		Actions.includePage(request, response, Actions.Util.getPagePath(request, "view_crud_column_config.jsp"));
	}

	/**
	 * 固有字段表单
	 * 
	 * @param request
	 * @param response
	 */
	public void columnSysConfigForm(HttpServletRequest request, HttpServletResponse response) {
		String tableName = RequestUtils.getStringValue(request, "tableName");
		String name = RequestUtils.getStringValue(request, "name");
		if (StringUtils.isEmpty(tableName) || StringUtils.isEmpty(name)) {
			throw new SystemRuntimeException(ExceptionType.CONFIG, "无传入[tableName]或[name].");
		}

		TbColumn column = (TbColumn) ORMService.getInstance().findHQL("from TbColumn where tableName = ? and name = ?",
				tableName, name);
		if (column == null) {
			throw new SystemRuntimeException(ExceptionType.CONFIG, "固定字段对应表或表字段已被删除,请刷新后继续配置.");
		}

		Map<String, Object> vo = null;
		Long id = RequestUtils.getLongValue(request, "id");
		if (id != null && id.longValue() > 0) {
			vo = (Map<String, Object>) ORMService.getInstance().findByPk("VwDynColumn", id);
		}
		if (vo == null) {
			vo = new HashMap<>();
			vo.put("name", column.getName());
			vo.put("busiName", column.getDescription());
			vo.put("whole", "0");

			StringBuffer buff = new StringBuffer();
			if (column.getMappedTypeCode() == (int) Types.Date.getCode()) {
				buff.append("date");
			} else if (column.getMappedTypeCode() == (int) Types.Clob.getCode()) {
				buff.append("textarea");
				vo.put("whole", "1");
			} else if (column.getSizeAsInt() > 500) {
				buff.append("textarea");
				vo.put("whole", "1");
			} else if (column.getMappedTypeCode() == (int) Types.Blob.getCode()) {
				buff.append("multifilemanager");
				vo.put("whole", "1");
			} else {
				buff.append("text");
			}

			vo.put("contentType", 1);// 默认groovy
			vo.put("contentScript", "return cm.widget('" + buff.toString() + "',vo?." + column.getName() + ");");

			buff.append("{required:");
			if (column.isPrimaryKey() || column.isRequired()) {
				buff.append("true");
			} else {
				buff.append("false");
			}
			if (column.getMappedTypeCode() == (Integer) Types.Integer.getCode()
					|| column.getMappedTypeCode() == (Integer) Types.Long.getCode()) {
				buff.append(",digits:true");
			}

			if (column.getMappedTypeCode() == (Integer) Types.BigDecimal.getCode()) {
				buff.append(",number:true");
			}
			buff.append("}");
			vo.put("widget", buff.toString());

			// 默认值
			vo.put("formFlag", 1);
			vo.put("showFlag", 1);
		}

		vo.put("primaryKey", column.isPrimaryKey());
		vo.put("autoIncrement", column.isAutoIncrement());
		vo.put("required", column.isRequired());

		request.setAttribute("vo", vo);

		Actions.includePage(request, response, Actions.Util.getPagePath(request, "view_crud_column_sys.jsp"));
	}

	/**
	 * 展示类字段表单
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
			Map<String, Object> vo = (Map<String, Object>) ORMService.getInstance().findByPk("VwDynColumnShow", id);
			request.setAttribute("vo", vo);
		}

		Actions.includePage(request, response, Actions.Util.getPagePath(request, "view_crud_column_show.jsp"));
	}

	/**
	 * 展示类字段表单
	 * 
	 * @param request
	 * @param response
	 */
	public void columnFormConfigForm(HttpServletRequest request, HttpServletResponse response) {
		Long id = RequestUtils.getLongValue(request, "id");
		if (id != null) {
			Map<String, Object> vo = (Map<String, Object>) ORMService.getInstance().findByPk("VwDynColumnForm", id);
			request.setAttribute("vo", vo);
		}

		Actions.includePage(request, response, Actions.Util.getPagePath(request, "view_crud_column_form.jsp"));
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
			Map<String, Object> vo = (Map<String, Object>) ORMService.getInstance().findByPk("VwDynColumnLine", id);
			request.setAttribute("vo", vo);
		}

		Actions.includePage(request, response, Actions.Util.getPagePath(request, "view_crud_column_line.jsp"));
	}

	/**
	 * 字段下拉框
	 * 
	 * @param request
	 * @param response
	 */
	public void sortNameSelect(HttpServletRequest request, HttpServletResponse response) {
		String tableName = RequestUtils.getStringValue(request, "tableName");
		if (StringUtils.isNotEmpty(tableName)) {
			TbTable tbTable = (TbTable) ORMService.getInstance().findByPk(TbTable.class.getName(), tableName);
			if (tbTable == null) {
				throw new SystemRuntimeException(ExceptionType.BUSINESS, "表[" + tableName + "]已删除,请重新选择表.");
			}
			request.setAttribute("table", tbTable);

		}
		Actions.includePage(request, response, Actions.Util.getPagePath(request, "view_crud_sortname_config.jsp"));
	}

	/**
	 * 权限资源列表
	 * 
	 * @param request
	 * @param response
	 */
	public void priList(HttpServletRequest request, HttpServletResponse response) {
		String viewKey = RequestUtils.getStringValue(request, "viewKey");
		String groupId = RequestUtils.getStringValue(request, "groupId");

		VwUrl url = (VwUrl) ORMService.getInstance().findByPk(VwUrl.class.getName(), viewKey);
		request.setAttribute("vwUrl", url);

		List<String> priKeys = ORMService.getInstance()
				.queryHQL("select priKey from CmPriGroupRelate where groupId = ?", groupId);
		request.setAttribute("priKeys", priKeys);

		if (!StringUtils.isEmpty(viewKey)) {
			Map<String, Object> table = (Map<String, Object>) ORMService.getInstance().findByPk("VwDynTable", viewKey);
			request.setAttribute("table", table);

			List<Map<String, Object>> fields = new ArrayList<>();
			fields.addAll((Set<Map<String, Object>>) table.get("columns"));
			fields.addAll((Set<Map<String, Object>>) table.get("showColumns"));
			fields.addAll((Set<Map<String, Object>>) table.get("lineColumns"));
			fields.addAll((Set<Map<String, Object>>) table.get("formColumns"));
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
		Actions.includePage(request, response, Actions.Util.getPagePath(request, "view_crud_pri_list.jsp"));
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

		Actions.includePage(request, response, Actions.Util.getPagePath(request, "view_crud_query_normal.jsp"));
	}

	/**
	 * 高级查询条件
	 * 
	 * @param request
	 * @param response
	 */
	public void extQueryForm(HttpServletRequest request, HttpServletResponse response) {
		Actions.includePage(request, response, Actions.Util.getPagePath(request, "view_crud_query_ext.jsp"));
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
			Map<String, Object> table = (Map<String, Object>) ORMService.getInstance().findByPk("VwDynTable", key);

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

		Actions.includePage(request, response, Actions.Util.getPagePath(request, "view_crud_query_config.jsp"));
	}

	/**
	 * 子表设置配置
	 * 
	 * @param request
	 * @param response
	 */
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
		if (StringUtils.isNotEmpty(key)) {
			Map<String, Object> table = (Map<String, Object>) ORMService.getInstance().findByPk("VwDynTable", key);
			subs.addAll((Set<Map<String, Object>>) table.get("viewSubs"));
			subs.addAll((Set<Map<String, Object>>) table.get("sysSubs"));
		}

		// 初始化设入系统内置按钮
		Integer sort = 0;
		for (SysTab sysTab : SysTab.values()) {
			if (ORMService.getInstance().findHQL("from VwDynSubSys where viewKey = ? and name = ?", key,
					sysTab.getName()) == null) {
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

		Actions.includePage(request, response, Actions.Util.getPagePath(request, "view_crud_sub_config.jsp"));
	}

	/**
	 * 添加视图标签
	 * 
	 * @param request
	 * @param response
	 */
	public void viewSubViewConfig(HttpServletRequest request, HttpServletResponse response) {
		Actions.includePage(request, response, Actions.Util.getPagePath(request, "view_crud_sub_view_config.jsp"));
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
			Map<String, Object> table = (Map<String, Object>) ORMService.getInstance().findByPk("VwDynTable", key);

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

		Actions.includePage(request, response, Actions.Util.getPagePath(request, "view_crud_limit_config.jsp"));
	}

	/**
	 * 添加数据约束
	 * 
	 * @param request
	 * @param response
	 */
	public void limitForm(HttpServletRequest request, HttpServletResponse response) {
		Actions.includePage(request, response, Actions.Util.getPagePath(request, "view_crud_limit.jsp"));
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
			Map<String, Object> table = (Map<String, Object>) ORMService.getInstance().findByPk("VwDynTable", key);
			if (table != null) {
				beforeExecs.addAll((Set<Map<String, Object>>) table.get("beforeExecs"));
				afterExecs.addAll((Set<Map<String, Object>>) table.get("afterExecs"));
				prepareExecs.addAll((Set<Map<String, Object>>) table.get("prepareExecs"));
			}
		}

		request.setAttribute("beforeExecs", beforeExecs);
		request.setAttribute("afterExecs", afterExecs);
		request.setAttribute("prepareExecs", prepareExecs);

		Actions.includePage(request, response, Actions.Util.getPagePath(request, "view_crud_exec_config.jsp"));
	}

	/**
	 * 增加前置处理器
	 * 
	 * @param request
	 * @param response
	 */
	public void beforeExecsForm(HttpServletRequest request, HttpServletResponse response) {
		Actions.includePage(request, response, Util.getPagePath(request, "view_crud_exec_before.jsp"));
	}

	/**
	 * 增加后置处理器
	 * 
	 * @param request
	 * @param response
	 */
	public void afterExecsForm(HttpServletRequest request, HttpServletResponse response) {
		Actions.includePage(request, response, Util.getPagePath(request, "view_crud_exec_after.jsp"));
	}

	/**
	 * 增加数据准备处理器
	 * 
	 * @param request
	 * @param response
	 */
	public void prepareExecsForm(HttpServletRequest request, HttpServletResponse response) {
		Actions.includePage(request, response, Util.getPagePath(request, "view_crud_var_prepare.jsp"));
	}

	/**
	 * 数据变量配置
	 * 
	 * @param request
	 * @param response
	 */
	public void varConfigForm(HttpServletRequest request, HttpServletResponse response) {
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

		List<Map<String, Object>> parents = new ArrayList<>();
		List<Map<String, Object>> prepareExecs = new ArrayList<>();
		if (!StringUtils.isEmpty(key)) {
			Map<String, Object> table = (Map<String, Object>) ORMService.getInstance().findByPk("VwDynTable", key);
			if (table != null) {
				prepareExecs.addAll((Set<Map<String, Object>>) table.get("prepareExecs"));
				parents.addAll((Set<Map<String, Object>>) table.get("parents"));
			}
		}

		request.setAttribute("parents", parents);
		request.setAttribute("prepareExecs", prepareExecs);
		Actions.includePage(request, response, Actions.Util.getPagePath(request, "view_crud_var_config.jsp"));
	}

	/**
	 * 父表添加
	 * 
	 * @param request
	 * @param response
	 */
	public void parentViewConfig(HttpServletRequest request, HttpServletResponse response) {
		Actions.includePage(request, response,
				Actions.Util.getPagePath(request, "view_crud_var_parent_normal_config.jsp"));
	}

	/**
	 * 父表外键添加(主界面)
	 * 
	 * @param request
	 * @param response
	 */
	public void parentViewForeignConfig(HttpServletRequest request, HttpServletResponse response) {
		Actions.includePage(request, response,
				Actions.Util.getPagePath(request, "view_crud_var_parent_foreign_config.jsp"));
	}

	/**
	 * 父表外键添加(添加一个外键)
	 * 
	 * @param request
	 * @param response
	 */
	public void parentViewForeignOneConfig(HttpServletRequest request, HttpServletResponse response) {
		String parentTableName = RequestUtils.getStringValue(request, "parentTableName");
		String tableName = RequestUtils.getStringValue(request, "tableName");

		request.setAttribute("columns",
				((TbTable) ORMService.getInstance().findByPk(TbTable.class.getName(), tableName)).getTbColumns());
		request.setAttribute("parentColumns",
				((TbTable) ORMService.getInstance().findByPk(TbTable.class.getName(), parentTableName)).getTbColumns());

		Actions.includePage(request, response,
				Actions.Util.getPagePath(request, "view_crud_var_parent_foreign_one_config.jsp"));
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
			table = (Map<String, Object>) ORMService.getInstance().findByPk("VwDynTable", key);
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
		Actions.includePage(request, response, Actions.Util.getPagePath(request, "view_crud_btn.jsp"));
	}

	/**
	 * 明细按钮新增
	 * 
	 * @param request
	 * @param response
	 */
	public void itemBtnForm(HttpServletRequest request, HttpServletResponse response) {
		Actions.includePage(request, response, Actions.Util.getPagePath(request, "view_crud_btn_item.jsp"));
	}

	/**
	 * 汇总按钮新增
	 * 
	 * @param request
	 * @param response
	 */
	public void summaryBtnForm(HttpServletRequest request, HttpServletResponse response) {
		Actions.includePage(request, response, Actions.Util.getPagePath(request, "view_crud_btn_summary.jsp"));
	}

	/**
	 * JS设置
	 * 
	 * @param request
	 * @param response
	 */
	public void jsConfigForm(HttpServletRequest request, HttpServletResponse response) {
		String key = RequestUtils.getStringValue(request, "key");
		if (!StringUtils.isEmpty(key)) {
			request.setAttribute("vo", (Map<String, Object>) ORMService.getInstance().findByPk("VwDynTable", key));
		}
		Actions.includePage(request, response, Actions.Util.getPagePath(request, "view_crud_js.jsp"));
	}

	@Override
	public void saveConfig(String key) {
		// table部分
		DataPO tablePO = new DataPO("VwDynTable");
		tablePO.set("viewKey", key);
		DynDataBuilder builder = new DynDataBuilder(tablePO);
		builder.build();
		ORMService.getInstance().save(tablePO.toEntity());

		builder.handleConfig();
	}

	@Override
	public void updateConfig(String key) {
		Map<String, Object> entity = (Map<String, Object>) ORMService.getInstance().loadByPk("VwDynTable", key);
		// table部分
		DataPO tablePO = new DataPO("VwDynTable", entity);
		if (entity == null) {
			throw new SystemRuntimeException(ExceptionType.BUSINESS, "视图已删除.");
		}
		DynDataBuilder builder = new DynDataBuilder(tablePO);
		builder.handleConfig();
		builder.build();
		// 这里要使用update
		ORMService.getInstance().update(tablePO.toEntity());
	}

	@Override
	public void removeConfig(String key) {
		ORMService.getInstance().removeByPk("VwDynWeixin", key);
		Map<String, Object> table = (Map<String, Object>) ORMService.getInstance().findByPk("VwDynTable", key);
		if (table != null) {
			ORMService.getInstance().remove(table);
		}
	}

	@Override
	public String copyConfig(String key) {
		throw new SystemRuntimeException(ExceptionType.BUSINESS, "此视图不支持复制.");
	}
}
