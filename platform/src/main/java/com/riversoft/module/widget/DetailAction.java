/*
 * $HeadURL: $
 * $Id: $
 * Copyright (c) 2014 by Riversoft System, all rights reserved.
 */
package com.riversoft.module.widget;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import com.riversoft.core.db.DataCondition;
import com.riversoft.core.db.DataPackage;
import com.riversoft.core.db.JdbcService;
import com.riversoft.core.db.ORMService;
import com.riversoft.core.db.QueryStringBuilder;
import com.riversoft.core.exception.ExceptionType;
import com.riversoft.core.exception.SystemRuntimeException;
import com.riversoft.core.web.Actions;
import com.riversoft.core.web.Actions.Keys;
import com.riversoft.core.web.Actions.Util;
import com.riversoft.core.web.RequestUtils;
import com.riversoft.platform.SessionManager;
import com.riversoft.platform.po.CmPri;
import com.riversoft.platform.script.ScriptHelper;
import com.riversoft.platform.script.ScriptTypes;
import com.riversoft.platform.web.FileManager;
import com.riversoft.platform.web.FileManager.UploadFile;
import com.riversoft.platform.web.ListFileExcelParser;

/**
 * 明细控件
 * 
 * @author woden
 */
public class DetailAction {

	/**
	 * 数据展示
	 * 
	 * @param request
	 * @param response
	 */
	@SuppressWarnings("unchecked")
	public void index(HttpServletRequest request, HttpServletResponse response) {
		String widgetKey = RequestUtils.getStringValue(request, "widgetKey");

		Map<String, Object> config = (Map<String, Object>) ORMService.getInstance().findByPk("WdgBase", widgetKey);
		if (config == null) {
			throw new SystemRuntimeException(ExceptionType.BUSINESS, "数据控件不存在.");
		}

		Map<String, Object> detail = (Map<String, Object>) ORMService.getInstance().findByPk("WdgDetail", widgetKey);
		if (detail == null) {
			throw new SystemRuntimeException(ExceptionType.BUSINESS, "数据控件扩展配置不存在.");
		}

		List<HashMap<String, Object>> list = RequestUtils.getJsonValues(request, "list");

		HashMap<String, Object> param = RequestUtils.getJsonValue(request, Keys.PARAMS.toString());
		boolean allowAdd = ((Integer) detail.get("allowAdd")).intValue() == 1;
		boolean allowDelete = ((Integer) detail.get("allowDelete")).intValue() == 1;
		if (param != null) {
			if (param.containsKey("add")) {
				allowAdd = (Boolean) param.get("add");
			}
			if (param.containsKey("delete")) {
				allowDelete = (Boolean) param.get("delete");
			}
		}
		request.setAttribute("allowAdd", allowAdd);
		request.setAttribute("allowDelete", allowDelete);

		// 初始化列表
		prepareList(request, config, detail, list);
		Actions.includePage(request, response, Util.getPagePath(request, "result_list.jsp"));
	}

	/**
	 * 数据编辑
	 * 
	 * @param request
	 * @param response
	 */
	@SuppressWarnings("unchecked")
	public void editWin(HttpServletRequest request, HttpServletResponse response) {
		String widgetKey = RequestUtils.getStringValue(request, "widgetKey");

		Map<String, Object> config = (Map<String, Object>) ORMService.getInstance().findByPk("WdgBase", widgetKey);
		if (config == null) {
			throw new SystemRuntimeException(ExceptionType.BUSINESS, "数据控件不存在.");
		}

		Map<String, Object> detail = (Map<String, Object>) ORMService.getInstance().findByPk("WdgDetail", widgetKey);
		if (detail == null) {
			throw new SystemRuntimeException(ExceptionType.BUSINESS, "数据控件扩展配置不存在.");
		}

		request.setAttribute("config", config);
		request.setAttribute("detail", detail);

		// 主SQL语句
		String mainSql = (String) ScriptHelper.evel(ScriptTypes.forCode((Integer) config.get("mainSqlType")), (String) config.get("mainSqlScript"));
		request.setAttribute("execType", StringUtils.isNotEmpty(mainSql));// 数据操作模式,选择数据还是直接增加

		List<?> list = RequestUtils.getJsonValues(request, "list");
		if (list != null && list.size() > 50) {// 超过50条警告
			request.setAttribute("warningFlag", 1);
		} else {
			request.setAttribute("warningFlag", 0);
		}

		HashMap<String, Object> param = RequestUtils.getJsonValue(request, Keys.PARAMS.toString());
		boolean allowAdd = ((Integer) detail.get("allowAdd")).intValue() == 1;
		boolean allowDelete = ((Integer) detail.get("allowDelete")).intValue() == 1;
		if (param != null) {
			if (param.containsKey("add")) {
				allowAdd = (Boolean) param.get("add");
			}
			if (param.containsKey("delete")) {
				allowDelete = (Boolean) param.get("delete");
			}
		}
		request.setAttribute("allowAdd", allowAdd);
		request.setAttribute("allowDelete", allowDelete);

		Integer pageLimit = (Integer) config.get("pageLimit");
		if (param != null && param.containsKey("pageLimit")) {
			pageLimit = (Integer) param.get("pageLimit");
		}
		request.setAttribute("pageLimit", pageLimit);

		Actions.includePage(request, response, Util.getPagePath(request, "win.jsp"));
	}

	/**
	 * 批量表单
	 * 
	 * @param request
	 * @param response
	 */
	public void batchWin(HttpServletRequest request, HttpServletResponse response) {
		Actions.includePage(request, response, Util.getPagePath(request, "batch_win.jsp"));
	}

	/**
	 * 下载模板
	 * 
	 * @param request
	 * @param response
	 */
	@SuppressWarnings("unchecked")
	public void downloadTemplate(HttpServletRequest request, HttpServletResponse response) {
		String widgetKey = RequestUtils.getStringValue(request, "widgetKey");

		Map<String, Object> config = (Map<String, Object>) ORMService.getInstance().findByPk("WdgBase", widgetKey);
		if (config == null) {
			throw new SystemRuntimeException(ExceptionType.BUSINESS, "数据控件不存在.");
		}

		Map<String, Object> detail = (Map<String, Object>) ORMService.getInstance().findByPk("WdgDetail", widgetKey);
		if (detail == null) {
			throw new SystemRuntimeException(ExceptionType.BUSINESS, "数据控件扩展配置不存在.");
		}

		List<String> fields = new ArrayList<String>();
		Map<String, String> titles = new HashMap<String, String>();
		List<HashMap<String, Object>> list = new ArrayList<>();
		HashMap<String, Object> example = new HashMap<>();
		if (detail.get("batchColumns") != null) {
			for (Map<String, Object> column : ((Set<Map<String, Object>>) detail.get("batchColumns"))) {
				fields.add((String) column.get("name"));
				titles.put((String) column.get("name"), (String) column.get("busiName"));
				if (column.get("example") != null) {
					example.put((String) column.get("name"), (String) column.get("example"));
				}
			}
		}
		if (example.size() > 0) {
			list.add(example);
		}

		Actions.downloadExcel(request, response, config.get("busiName") + "-批量文件模板.xls", fields.toArray(new String[1]), titles, list);
	}

	/**
	 * 下载数据
	 * 
	 * @param request
	 * @param response
	 */
	@SuppressWarnings("unchecked")
	public void downloadData(HttpServletRequest request, HttpServletResponse response) {
		String widgetKey = RequestUtils.getStringValue(request, "widgetKey");

		Map<String, Object> config = (Map<String, Object>) ORMService.getInstance().findByPk("WdgBase", widgetKey);
		if (config == null) {
			throw new SystemRuntimeException(ExceptionType.BUSINESS, "数据控件不存在.");
		}

		Map<String, Object> detail = (Map<String, Object>) ORMService.getInstance().findByPk("WdgDetail", widgetKey);
		if (detail == null) {
			throw new SystemRuntimeException(ExceptionType.BUSINESS, "数据控件扩展配置不存在.");
		}

		List<String> fields = new ArrayList<String>();
		Map<String, String> titles = new HashMap<String, String>();

		if (detail.get("batchColumns") != null) {
			for (Map<String, Object> column : ((Set<Map<String, Object>>) detail.get("batchColumns"))) {
				fields.add((String) column.get("name"));
				titles.put((String) column.get("name"), (String) column.get("busiName"));
			}
		}

		List<HashMap<String, Object>> list = RequestUtils.getJsonValues(request, "list");
		Actions.downloadExcel(request, response, config.get("busiName") + "-批量数据编辑.xls", fields.toArray(new String[1]), titles, list);
	}

	/**
	 * 批量保存
	 * 
	 * @param request
	 * @param response
	 */
	@SuppressWarnings("unchecked")
	public void saveBatch(HttpServletRequest request, HttpServletResponse response) {
		String widgetKey = RequestUtils.getStringValue(request, "widgetKey");

		Map<String, Object> config = (Map<String, Object>) ORMService.getInstance().findByPk("WdgBase", widgetKey);
		if (config == null) {
			throw new SystemRuntimeException(ExceptionType.BUSINESS, "数据控件不存在.");
		}

		Map<String, Object> detail = (Map<String, Object>) ORMService.getInstance().findByPk("WdgDetail", widgetKey);
		if (detail == null) {
			throw new SystemRuntimeException(ExceptionType.BUSINESS, "数据控件扩展配置不存在.");
		}

		List<String> fields = new ArrayList<String>();
		if (detail.get("batchColumns") != null) {
			for (Map<String, Object> column : ((Set<Map<String, Object>>) detail.get("batchColumns"))) {
				fields.add((String) column.get("name"));
			}
		}

		List<Map<String, String>> list;
		UploadFile uploadFile = FileManager.getUploadFile(request, "file");
		try (InputStream is = uploadFile.getInputStream()) {
			list = new ListFileExcelParser(fields, "data", 0).parse(is).getResult();
		} catch (IOException e) {
			throw new SystemRuntimeException("从文件获取数据出错.", e);
		}

		// 数据处理器
		Map<String, Object> context = new HashMap<>();
		context.put("list", list);
		context.put("mode", 2);// 普通编辑
		for (Map<String, Object> exec : (Set<Map<String, Object>>) detail.get("execs")) {
			Integer type = (Integer) exec.get("execType");
			String script = (String) exec.get("execScript");
			if (StringUtils.isNotEmpty(script)) {
				ScriptHelper.evel(ScriptTypes.forCode(type), script, context);
			}
		}

		// 初始化列表
		prepareList(request, config, detail, list);

		Actions.includePage(request, response, Util.getPagePath(request, "result_list.jsp"));
	}

	/**
	 * 查询
	 * 
	 * @param request
	 * @param response
	 */
	@SuppressWarnings("unchecked")
	public void waitList(HttpServletRequest request, HttpServletResponse response) {

		String widgetKey = RequestUtils.getStringValue(request, "widgetKey");

		Map<String, Object> config = (Map<String, Object>) ORMService.getInstance().findByPk("WdgBase", widgetKey);
		if (config == null) {
			throw new SystemRuntimeException(ExceptionType.BUSINESS, "数据控件不存在.");
		}

		Map<String, Object> detail = (Map<String, Object>) ORMService.getInstance().findByPk("WdgDetail", widgetKey);
		if (detail == null) {
			throw new SystemRuntimeException(ExceptionType.BUSINESS, "数据控件扩展配置不存在.");
		}

		request.setAttribute("config", config);
		List<Map<String, Object>> fields = new ArrayList<>();
		for (Map<String, Object> filed : (Set<Map<String, Object>>) config.get("showColumns")) {
			Integer inWait = (Integer) filed.get("inWait");
			if (inWait.intValue() == 1) {
				fields.add(filed);
			}
		}
		request.setAttribute("fields", fields);
		request.setAttribute("detail", detail);

		// 获取分页信息
		int start = Util.getStart(request);
		int limit = Util.getLimit(request);

		// 获取排序信息
		String field = Util.getSortField(request);
		String dir = Util.getSortDir(request);

		// 主SQL语句
		String mainSql = (String) ScriptHelper.evel(ScriptTypes.forCode((Integer) config.get("mainSqlType")), (String) config.get("mainSqlScript"));

		// 查询条件
		DataCondition condition = new DataCondition(Util.buildQueryMap(new LinkedHashMap<String, Object>(), request));
		HashMap<String, Object> params = RequestUtils.getJsonValue(request, Keys.PARAMS.toString());
		if (StringUtils.isEmpty(field)) {
			if (params != null && params.containsKey("orderBy")) {
				condition.setOrderBySQL((String) params.get("orderBy"));
			} else {
				condition.setOrderBySQL((String) config.get("orderBy"));
			}
		} else {
			condition.setOrderBy(field, dir);
		}

		// 高级查询
		for (Map<String, Object> query : ((Set<Map<String, Object>>) config.get("querys"))) {
			// 表单名称参数
			String name = (String) query.get("name");
			if (StringUtils.isEmpty(name)) {
				name = "querys." + query.get("id");
			}
			
			String value = RequestUtils.getStringValue(request, name);
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
		for (Map<String, Object> dataLimit : ((Set<Map<String, Object>>) config.get("limits"))) {
			CmPri pri = (CmPri) dataLimit.get("pri");
			if (!SessionManager.check(pri)) {
				continue;
			}
			ScriptTypes type = ScriptTypes.forCode((Integer) dataLimit.get("sqlType"));
			String sql = (String) ScriptHelper.evel(type, (String) dataLimit.get("sqlScript"));
			condition.addSql(sql);
		}

		StringBuffer sql = new StringBuffer(mainSql);
		String whereCondition = QueryStringBuilder.buildWhere(condition.toEntity());
		if (StringUtils.isNotEmpty(whereCondition)) {
			sql.append(" and ").append(whereCondition.substring(5));
		}
		String orderbyCondition = QueryStringBuilder.buildOrder(condition.toEntity());
		if (StringUtils.isNotEmpty(orderbyCondition)) {
			sql.append(orderbyCondition);
		}

		DataPackage dp = JdbcService.getInstance().querySQLPackage(sql.toString(), start, limit);

		// 设置到页面
		request.setAttribute("dp", dp);

		request.setAttribute("config", config);

		HashMap<String, Object> param = RequestUtils.getJsonValue(request, Keys.PARAMS.toString());
		boolean allowAdd = ((Integer) detail.get("allowAdd")).intValue() == 1;
		boolean allowDelete = ((Integer) detail.get("allowDelete")).intValue() == 1;
		if (param != null) {
			if (param.containsKey("add")) {
				allowAdd = (Boolean) param.get("add");
			}
			if (param.containsKey("delete")) {
				allowDelete = (Boolean) param.get("delete");
			}
		}
		request.setAttribute("allowAdd", allowAdd);
		request.setAttribute("allowDelete", allowDelete);

		Actions.includePage(request, response, Util.getPagePath(request, "wait_list.jsp"));
	}

	/**
	 * 编辑页面
	 * 
	 * @param request
	 * @param response
	 */
	@SuppressWarnings("unchecked")
	public void editList(HttpServletRequest request, HttpServletResponse response) {
		String widgetKey = RequestUtils.getStringValue(request, "widgetKey");

		Map<String, Object> config = (Map<String, Object>) ORMService.getInstance().findByPk("WdgBase", widgetKey);
		if (config == null) {
			throw new SystemRuntimeException(ExceptionType.BUSINESS, "数据控件不存在.");
		}

		Map<String, Object> detail = (Map<String, Object>) ORMService.getInstance().findByPk("WdgDetail", widgetKey);
		if (detail == null) {
			throw new SystemRuntimeException(ExceptionType.BUSINESS, "数据控件扩展配置不存在.");
		}

		request.setAttribute("config", config);
		List<Map<String, Object>> fields = new ArrayList<>();
		fields.addAll((Set<Map<String, Object>>) config.get("formColumns"));
		for (Map<String, Object> filed : (Set<Map<String, Object>>) config.get("showColumns")) {
			Integer inSelected = (Integer) filed.get("inSelected");
			if (inSelected.intValue() == 1) {
				fields.add(filed);
			}
		}
		Collections.sort(fields, new Comparator<Map<String, Object>>() {
			@Override
			public int compare(Map<String, Object> o1, Map<String, Object> o2) {
				return (Integer) o1.get("sort") < (Integer) o2.get("sort") ? -1 : 1;
			}
		});
		request.setAttribute("fields", fields);
		request.setAttribute("detail", detail);

		List<HashMap<String, Object>> list = RequestUtils.getJsonValues(request, "list");
		request.setAttribute("list", list);

		HashMap<String, Object> param = RequestUtils.getJsonValue(request, Keys.PARAMS.toString());
		boolean allowAdd = ((Integer) detail.get("allowAdd")).intValue() == 1;
		boolean allowDelete = ((Integer) detail.get("allowDelete")).intValue() == 1;
		if (param != null) {
			if (param.containsKey("add")) {
				allowAdd = (Boolean) param.get("add");
			}
			if (param.containsKey("delete")) {
				allowDelete = (Boolean) param.get("delete");
			}
		}
		request.setAttribute("allowAdd", allowAdd);
		request.setAttribute("allowDelete", allowDelete);

		Actions.includePage(request, response, Util.getPagePath(request, "edit_list.jsp"));
	}

	/**
	 * 保存选中
	 * 
	 * @param request
	 * @param response
	 */
	@SuppressWarnings("unchecked")
	public void submitSelect(HttpServletRequest request, HttpServletResponse response) {
		String widgetKey = RequestUtils.getStringValue(request, "widgetKey");

		Map<String, Object> config = (Map<String, Object>) ORMService.getInstance().findByPk("WdgBase", widgetKey);
		if (config == null) {
			throw new SystemRuntimeException(ExceptionType.BUSINESS, "数据控件不存在.");
		}

		Map<String, Object> detail = (Map<String, Object>) ORMService.getInstance().findByPk("WdgDetail", widgetKey);
		if (detail == null) {
			throw new SystemRuntimeException(ExceptionType.BUSINESS, "数据控件扩展配置不存在.");
		}

		request.setAttribute("config", config);
		List<Map<String, Object>> fields = new ArrayList<>();
		fields.addAll((Set<Map<String, Object>>) config.get("formColumns"));
		for (Map<String, Object> filed : (Set<Map<String, Object>>) config.get("showColumns")) {
			Integer inSelected = (Integer) filed.get("inSelected");
			if (inSelected.intValue() == 1) {
				fields.add(filed);
			}
		}
		Collections.sort(fields, new Comparator<Map<String, Object>>() {
			@Override
			public int compare(Map<String, Object> o1, Map<String, Object> o2) {
				return (Integer) o1.get("sort") < (Integer) o2.get("sort") ? -1 : 1;
			}
		});
		request.setAttribute("fields", fields);
		request.setAttribute("detail", detail);

		// 已选
		String[] strSelectedPks = RequestUtils.getStringValues(request, "selectedPks");
		List<String> selectedPks;
		if (strSelectedPks != null) {
			selectedPks = Arrays.asList(strSelectedPks);
		} else {
			selectedPks = new ArrayList<String>();
		}

		// 处理数据
		List<Object> list = new ArrayList<>();
		String[] pks = RequestUtils.getStringValues(request, "pk");
		if (pks == null || pks.length == 0) {
			throw new SystemRuntimeException(ExceptionType.BUSINESS, "没有数据被选中.");
		}

		for (String pk : pks) {
			if (selectedPks.contains(pk)) {
				throw new SystemRuntimeException(ExceptionType.BUSINESS, "数据[" + pk + "]已存在,无需重复选择.");
			}
			list.add(RequestUtils.getJsonValue(request, pk + ".vo"));
		}
		request.setAttribute("list", list);

		HashMap<String, Object> param = RequestUtils.getJsonValue(request, Keys.PARAMS.toString());
		boolean allowAdd = ((Integer) detail.get("allowAdd")).intValue() == 1;
		boolean allowDelete = ((Integer) detail.get("allowDelete")).intValue() == 1;
		if (param != null) {
			if (param.containsKey("add")) {
				allowAdd = (Boolean) param.get("add");
			}
			if (param.containsKey("delete")) {
				allowDelete = (Boolean) param.get("delete");
			}
		}
		request.setAttribute("allowAdd", allowAdd);
		request.setAttribute("allowDelete", allowDelete);

		Actions.includePage(request, response, Util.getPagePath(request, "edit_list.jsp"));
	}

	/**
	 * 单独增加数据
	 * 
	 * @param request
	 * @param response
	 */
	@SuppressWarnings("unchecked")
	public void addData(HttpServletRequest request, HttpServletResponse response) {
		String widgetKey = RequestUtils.getStringValue(request, "widgetKey");

		Map<String, Object> config = (Map<String, Object>) ORMService.getInstance().findByPk("WdgBase", widgetKey);
		if (config == null) {
			throw new SystemRuntimeException(ExceptionType.BUSINESS, "数据控件不存在.");
		}

		Map<String, Object> detail = (Map<String, Object>) ORMService.getInstance().findByPk("WdgDetail", widgetKey);
		if (detail == null) {
			throw new SystemRuntimeException(ExceptionType.BUSINESS, "数据控件扩展配置不存在.");
		}

		request.setAttribute("config", config);
		List<Map<String, Object>> fields = new ArrayList<>();
		fields.addAll((Set<Map<String, Object>>) config.get("formColumns"));
		for (Map<String, Object> filed : (Set<Map<String, Object>>) config.get("showColumns")) {
			Integer inSelected = (Integer) filed.get("inSelected");
			if (inSelected.intValue() == 1) {
				fields.add(filed);
			}
		}
		Collections.sort(fields, new Comparator<Map<String, Object>>() {
			@Override
			public int compare(Map<String, Object> o1, Map<String, Object> o2) {
				return (Integer) o1.get("sort") < (Integer) o2.get("sort") ? -1 : 1;
			}
		});
		request.setAttribute("fields", fields);
		request.setAttribute("detail", detail);

		// 处理数据
		List<Object> list = new ArrayList<>();
		list.add(new HashMap<>());
		request.setAttribute("list", list);// 增加空白数据

		HashMap<String, Object> param = RequestUtils.getJsonValue(request, Keys.PARAMS.toString());
		boolean allowAdd = ((Integer) detail.get("allowAdd")).intValue() == 1;
		boolean allowDelete = ((Integer) detail.get("allowDelete")).intValue() == 1;
		if (param != null) {
			if (param.containsKey("add")) {
				allowAdd = (Boolean) param.get("add");
			}
			if (param.containsKey("delete")) {
				allowDelete = (Boolean) param.get("delete");
			}
		}
		request.setAttribute("allowAdd", allowAdd);
		request.setAttribute("allowDelete", allowDelete);

		Actions.includePage(request, response, Util.getPagePath(request, "edit_list.jsp"));
	}

	/**
	 * 保存表单
	 * 
	 * @param request
	 * @param response
	 */
	@SuppressWarnings("unchecked")
	public void saveEditZone(HttpServletRequest request, HttpServletResponse response) {
		String widgetKey = RequestUtils.getStringValue(request, "widgetKey");

		Map<String, Object> config = (Map<String, Object>) ORMService.getInstance().findByPk("WdgBase", widgetKey);
		if (config == null) {
			throw new SystemRuntimeException(ExceptionType.BUSINESS, "数据控件不存在.");
		}

		Map<String, Object> detail = (Map<String, Object>) ORMService.getInstance().findByPk("WdgDetail", widgetKey);
		if (detail == null) {
			throw new SystemRuntimeException(ExceptionType.BUSINESS, "数据控件扩展配置不存在.");
		}

		List<Map<String, Object>> list = new ArrayList<>();
		// 处理数据
		String[] pks = RequestUtils.getStringValues(request, "pk");
		if (pks != null) {
			for (String pixel : pks) {
				HashMap<String, Object> o = RequestUtils.getJsonValue(request, pixel + ".vo");
				Map<String, Object> vo = new HashMap<>();
				vo.putAll(o);
				for (Map<String, Object> filed : (Set<Map<String, Object>>) config.get("formColumns")) {
					String name = (String) filed.get("name");
					vo.put(name, RequestUtils.getStringValue(request, pixel + "." + name));
				}
				list.add(vo);
			}
		}

		// 数据处理器
		Map<String, Object> context = new HashMap<>();
		context.put("list", list);
		context.put("mode", 1);// 普通编辑
		for (Map<String, Object> exec : (Set<Map<String, Object>>) detail.get("execs")) {
			Integer type = (Integer) exec.get("execType");
			String script = (String) exec.get("execScript");
			if (StringUtils.isNotEmpty(script)) {
				ScriptHelper.evel(ScriptTypes.forCode(type), script, context);
			}
		}

		// 初始化列表
		prepareList(request, config, detail, list);

		Actions.includePage(request, response, Util.getPagePath(request, "result_list.jsp"));
	}

	/**
	 * 初始化列表
	 * 
	 * @param request
	 * @param config
	 * @param detail
	 * @param list
	 */
	@SuppressWarnings("unchecked")
	private void prepareList(HttpServletRequest request, Map<String, Object> config, Map<String, Object> detail, List<?> list) {
		request.setAttribute("config", config);
		request.setAttribute("detail", detail);

		List<Map<String, Object>> fields = new ArrayList<>();
		for (Map<String, Object> filed : (Set<Map<String, Object>>) config.get("showColumns")) {
			Integer inResult = (Integer) filed.get("inResult");
			if (inResult.intValue() == 1) {
				fields.add(filed);
			}
		}
		request.setAttribute("fields", fields);
		request.setAttribute("list", list);

		Integer pageFlag = (Integer) detail.get("pageFlag");// 是否分页
		DataPackage dp = new DataPackage();
		if (pageFlag != null && pageFlag.intValue() == 1) {// 需要分页
			int start = Util.getStart(request);
			int limit = Util.getLimit(request);
			Object[] array = ArrayUtils.subarray(list.toArray(new Object[0]), start, start + limit);
			dp.setLimit(limit);
			dp.setStart(start);
			dp.setTotalRecord(list.size());
			dp.setList(Arrays.asList(array));
		} else {// 无需分页
			dp.setList(list);
			dp.setTotalRecord(list.size());
			dp.setStart(0);
			dp.setLimit(list.size());
		}
		request.setAttribute("dp", dp);
	}
}
