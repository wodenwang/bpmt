/*
 * $HeadURL: $
 * $Id: $
 * Copyright (c) 2014 by Riversoft System, all rights reserved.
 */
package com.riversoft.module.report;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
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
import com.riversoft.core.web.annotation.ActionMode;
import com.riversoft.core.web.annotation.ActionMode.Mode;
import com.riversoft.platform.SessionManager;
import com.riversoft.platform.po.CmPri;
import com.riversoft.platform.script.ScriptHelper;
import com.riversoft.platform.script.ScriptTypes;
import com.riversoft.platform.store.db.OtherDatabasePools;
import com.riversoft.platform.web.WebLogManager;
import com.riversoft.util.Formatter;

/**
 * 
 * @author woden
 * 
 */
public abstract class BaseReportListAction {
	/**
	 * Logger for this class
	 */
	static final Logger logger = LoggerFactory.getLogger(BaseReportListAction.class);

	/**
	 * 关联主键,唯一不变值
	 */
	private final String viewKey;

	/**
	 * 绑定service
	 */
	private final ReportListService service;

	protected BaseReportListAction(String viewKey) {
		this.viewKey = viewKey;
		this.service = BeanFactory.getInstance().getBean(ReportListService.class);
		synchronized (service) {
			service.setViewKey(viewKey);
		}
	}

	public static class Config {
		private Map<String, Object> table;
		private List<Map<String, Object>> querys;// 查询条件字段
		private List<Map<String, Object>> listFields;// 列表展示字段
		private List<Map<String, Object>> detailFields;// 明细展示字段
		private List<Map<String, Object>> itemBtns;// 明细按钮
		private Map<String, Object> showBtn;// 查看明细按钮
		private List<Map<String, Object>> summaryBtns;// 汇总按钮
		private List<Map<String, Object>> subs;// 子标签

		// 微信端字段,列表
		private List<Map<String, Object>> h5DetailList = null;

		@SuppressWarnings("unchecked")
		private Config(Map<String, Object> table) {
			this.table = table;

			// 按钮
			{
				itemBtns = new ArrayList<>();
				summaryBtns = new ArrayList<>();

				itemBtns.addAll((Set<Map<String, Object>>) table.get("itemBtns"));
				summaryBtns.addAll((Set<Map<String, Object>>) table.get("summaryBtns"));
				for (Map<String, Object> btn : (Set<Map<String, Object>>) table.get("sysBtns")) {
					if (((Integer) btn.get("type")) == 1) {// 明细
						if ("show".equalsIgnoreCase((String) btn.get("name"))) {
							showBtn = btn;
						}
						itemBtns.add(btn);
					} else {// 汇总
						summaryBtns.add(btn);
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

			// 查询列表
			{
				querys = new ArrayList<>();
				querys.addAll((Set<Map<String, Object>>) table.get("querys"));
				Collections.sort(querys, new Comparator<Map<String, Object>>() {
					@Override
					public int compare(Map<String, Object> o1, Map<String, Object> o2) {
						return (Integer) o1.get("sort") < (Integer) o2.get("sort") ? -1 : 1;
					}
				});
			}

			// sub子标签
			{
				subs = new ArrayList<>();
				subs.addAll((Set<Map<String, Object>>) table.get("viewSubs"));
			}

			{
				listFields = new ArrayList<>();
				detailFields = new ArrayList<>();

				for (Map<String, Object> field : (Set<Map<String, Object>>) table.get("showColumns")) {
					detailFields.add(field);
					Integer listSort = (Integer) field.get("listSort");
					if (listSort != null && listSort.intValue() >= 0) {
						listFields.add(field);
					}
				}
				detailFields.addAll((Set<Map<String, Object>>) table.get("lineColumns"));

				Collections.sort(listFields, new Comparator<Map<String, Object>>() {
					@Override
					public int compare(Map<String, Object> o1, Map<String, Object> o2) {
						return (Integer) o1.get("listSort") < (Integer) o2.get("listSort") ? -1 : 1;
					}
				});

				Collections.sort(detailFields, new Comparator<Map<String, Object>>() {
					@Override
					public int compare(Map<String, Object> o1, Map<String, Object> o2) {
						return (Integer) o1.get("sort") < (Integer) o2.get("sort") ? -1 : 1;
					}
				});

			}

			// 微信h5端配置列表
			{
				h5DetailList = new ArrayList<>();

				h5DetailList.addAll((Set<Map<String, Object>>) table.get("showColumns"));
				h5DetailList.addAll((Set<Map<String, Object>>) table.get("lineColumns"));

				Collections.sort(h5DetailList, new Comparator<Map<String, Object>>() {
					@Override
					public int compare(Map<String, Object> o1, Map<String, Object> o2) {
						return (Integer) o1.get("sort") < (Integer) o2.get("sort") ? -1 : 1;
					}
				});

				// 写入begin和end
				Map<String, Object> current = null;
				int i = 0;
				for (Map<String, Object> o : h5DetailList) {
					if (o.get("whole") != null && current != null) {
						current.put("detail_end", i);
					}

					if (o.get("whole") == null) {
						current = o;
						current.put("detail_begin", i + 1);
					}

					i++;
				}

			}
		}

		/**
		 * @return the showBtn
		 */
		public Map<String, Object> getShowBtn() {
			return showBtn;
		}

		/**
		 * @return the listFields
		 */
		public List<Map<String, Object>> getListFields() {
			return listFields;
		}

		/**
		 * @return the detailFields
		 */
		public List<Map<String, Object>> getDetailFields() {
			return detailFields;
		}

		/**
		 * @return the table
		 */
		public Map<String, Object> getTable() {
			return table;
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
		 * @return the subs
		 */
		public List<Map<String, Object>> getSubs() {
			return subs;
		}

		/**
		 * @return the querys
		 */
		public List<Map<String, Object>> getQuerys() {
			return querys;
		}

		/**
		 * @return the h5DetailList
		 */
		public List<Map<String, Object>> getH5DetailList() {
			return h5DetailList;
		}

	}

	/**
	 * 获取数据库服务
	 * 
	 * @param config
	 * @return
	 */
	private JdbcService getJdbcService(Map<String, Object> config) {
		if (config != null && StringUtils.isNotEmpty((String) config.get("dbKey"))) {
			return OtherDatabasePools.getInstance().getService((String) config.get("dbKey"));
		}
		return JdbcService.getInstance();
	}

	/**
	 * 查询动态表配置
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private Map<String, Object> getTableConfig() {
		Map<String, Object> table = (Map<String, Object>) ORMService.getInstance().findByPk("VwReport", viewKey);
		if (table == null) {
			throw new SystemRuntimeException(ExceptionType.CONFIG, "视图已删除.");
		}
		return table;
	}

	/**
	 * 分发入口
	 * 
	 * @param request
	 * @param response
	 */
	@ActionMode(Mode.FIT)
	public void index(HttpServletRequest request, HttpServletResponse response) {
		HashMap<String, Object> params = RequestUtils.getJsonValue(request, Keys.PARAMS.toString());
		if (params != null) {
			if (params.containsKey("list") && "true".equalsIgnoreCase(params.get("list").toString())) {// 直接跳转到明细页
				Actions.forwardAction(request, response, Util.getActionUrl(request) + "/list.shtml");
				return;
			} else if (params.containsKey("detail") && "true".equalsIgnoreCase(params.get("detail").toString())) {// 明细页
				Actions.redirectAction(request, response,
						Util.getActionUrl(request) + "/detail.shtml?_key=" + params.get("key"));
				return;
			}
		}

		// 无参数则转发到框架页面
		Actions.forwardAction(request, response, Util.getActionUrl(request) + "/main.shtml");
	}

	/**
	 * 进入框架页
	 * 
	 * @param request
	 * @param response
	 */
	@ActionMode(Mode.FIT)
	public void main(HttpServletRequest request, HttpServletResponse response) {
		HashMap<String, Object> params = RequestUtils.getJsonValue(request, Keys.PARAMS.toString());
		Config config = new Config(getTableConfig());
		request.setAttribute("config", config);

		String title = (String) config.getTable().get("busiName");
		Integer pageLimit = (Integer) config.getTable().get("pageLimit");
		if (params != null) {
			if (params.containsKey("title")) {
				title = params.get("title").toString();
			}

			if (params.containsKey("pageLimit")) {
				pageLimit = ((Number) params.get("pageLimit")).intValue();
			}
		}
		request.setAttribute("title", title);
		request.setAttribute("pageLimit", pageLimit);

		request.setAttribute(Keys.H5_JS.toString(), "amaze");// 视图使用amaze ui
		Actions.includePage(request, response, Util.getPagePath(request, "/report/main.jsp"));
	}

	/**
	 * 明细列表
	 * 
	 * @param request
	 * @param response
	 */
	@ActionMode(Mode.FIT)
	@SuppressWarnings("unchecked")
	public void list(HttpServletRequest request, HttpServletResponse response) {
		WebLogManager.log("正在查询数据.");
		Map<String, Object> table = getTableConfig();
		Config config = new Config(table);
		JdbcService jdbcService = getJdbcService(table);
		Map<String, Object> params = RequestUtils.getJsonValue(request, Keys.PARAMS.toString());

		// 设置标题
		String title = (String) config.getTable().get("busiName");
		if (params != null) {
			if (params.containsKey("title")) {
				title = params.get("title").toString();
			}
		}
		request.setAttribute("title", title);

		// 获取分页信息
		int start = Util.getStart(request);
		int limit = Util.getLimit(request);

		// 默认分页
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

		// 获取排序信息
		String field = Util.getSortField(request);
		String dir = Util.getSortDir(request);

		// 主SQL语句
		String mainSql = (String) ScriptHelper.evel(ScriptTypes.forCode((Integer) table.get("mainSqlType")),
				(String) table.get("mainSqlScript"));

		// 查询条件
		DataCondition condition = new DataCondition();
		if (StringUtils.isEmpty(field)) {
			if (params != null && params.containsKey("orderBy")) {
				condition.setOrderBySQL((String) params.get("orderBy"));
			} else {
				condition.setOrderBySQL((String) table.get("orderBy"));
			}
		} else {
			condition.setOrderBy(field, dir);
		}

		// 高级查询
		for (Map<String, Object> query : ((Set<Map<String, Object>>) table.get("querys"))) {
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
		for (Map<String, Object> dataLimit : ((Set<Map<String, Object>>) table.get("limits"))) {
			// logger.debug("数据筛选器[" + dataLimit.get("id") + "]:" +
			// dataLimit.get("sqlScript"));
			CmPri pri = (CmPri) dataLimit.get("pri");
			Map<String, Object> context = new HashMap<>();
			if (!SessionManager.check(pri, context)) {
				// logger.debug("跳过筛选器[" + dataLimit.get("id") + "].");
				continue;
			}

			ScriptTypes type = ScriptTypes.forCode((Integer) dataLimit.get("sqlType"));
			String sql = (String) ScriptHelper.evel(type, (String) dataLimit.get("sqlScript"), context);
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

		DataPackage dp;
		boolean pageFlag = ((Integer) table.get("pageFlag")).intValue() == 1;
		if (params != null && params.containsKey("pageFlag")) {
			pageFlag = "true".equalsIgnoreCase(params.get("pageFlag").toString());
		}

		if (pageFlag) {
			// 设置到页面
			dp = jdbcService.querySQLPackage(sql.toString(), start, limit);
		} else {
			List<?> list = jdbcService.querySQL(sql.toString());
			dp = new DataPackage();
			dp.setList(list);
		}

		Util.setTitle(request, title);
		request.setAttribute("pageFlag", pageFlag);// 是否分页
		request.setAttribute("dp", dp);

		request.setAttribute("config", config);

		request.setAttribute(Keys.H5_JS.toString(), "amaze");// 视图使用amaze ui
		Actions.includePage(request, response, Util.getPagePath(request, "/report/list.jsp"));
	}

	/**
	 * 详细页
	 * 
	 * @param request
	 * @param response
	 */
	@ActionMode(Mode.FIT)
	public void detail(HttpServletRequest request, HttpServletResponse response) {
		String key = RequestUtils.getStringValue(request, "_key");

		Config config = new Config(getTableConfig());
		Map<String, Object> table = config.getTable();
		JdbcService jdbcService = getJdbcService(table);

		// 主SQL语句
		String mainSql = (String) ScriptHelper.evel(ScriptTypes.forCode((Integer) table.get("mainSqlType")),
				(String) table.get("mainSqlScript"));
		String pkSqlScript = (String) table.get("pkSqlScript");
		StringBuffer sql = new StringBuffer(mainSql);
		if (StringUtils.isNotEmpty(pkSqlScript)) {
			Map<String, Object> context = new HashMap<String, Object>();
			context.put("value", key);
			sql.append(" and ").append(ScriptHelper.evel(ScriptTypes.forCode((Integer) table.get("pkSqlType")),
					(String) table.get("pkSqlScript"), context));
		}

		request.setAttribute("vo", jdbcService.findSQL(sql.toString()));

		HashMap<String, Object> params = RequestUtils.getJsonValue(request, Keys.PARAMS.toString());
		String title = (String) config.getTable().get("busiName");
		if (params != null) {
			if (params.containsKey("title")) {
				title = params.get("title").toString();
			}
		}
		request.setAttribute("title", title);
		Util.setTitle(request, title);
		request.setAttribute("config", config);

		request.setAttribute(Keys.H5_JS.toString(), "amaze");// 视图使用amaze ui
		Actions.includePage(request, response, Util.getPagePath(request, "/report/detail.jsp"));
	}

	/**
	 * 详细
	 * 
	 * @param request
	 * @param response
	 */
	@ActionMode(Mode.FIT)
	public void sub(HttpServletRequest request, HttpServletResponse response) {
		String key = RequestUtils.getStringValue(request, "_key");
		Map<String, Object> table = getTableConfig();
		JdbcService jdbcService = getJdbcService(table);
		// 主SQL语句
		String mainSql = (String) ScriptHelper.evel(ScriptTypes.forCode((Integer) table.get("mainSqlType")),
				(String) table.get("mainSqlScript"));
		String pkSqlScript = (String) table.get("pkSqlScript");
		StringBuffer sql = new StringBuffer(mainSql);
		if (StringUtils.isNotEmpty(pkSqlScript)) {
			Map<String, Object> context = new HashMap<String, Object>();
			context.put("value", key);
			sql.append(" and ").append(ScriptHelper.evel(ScriptTypes.forCode((Integer) table.get("pkSqlType")),
					(String) table.get("pkSqlScript"), context));
		}

		request.setAttribute("vo", jdbcService.findSQL(sql.toString()));

		request.setAttribute("config", new Config(table));

		request.setAttribute(Keys.H5_JS.toString(), "amaze");// 视图使用amaze ui
		Actions.includePage(request, response, Util.getPagePath(request, "/report/sub.jsp"));
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
		Actions.includePage(request, response, Util.getPagePath(request, "/report/download.jsp"));
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
		JdbcService jdbcService = getJdbcService(table);

		WebLogManager.log("正在查询数据.");
		List<?> datas;

		{
			// 查询条件
			DataCondition condition = new DataCondition();
			// 获取排序信息
			String field = Util.getSortField(request);
			String dir = Util.getSortDir(request);
			Map<String, Object> params = RequestUtils.getJsonValue(request, Keys.PARAMS.toString());

			// 主SQL语句
			String mainSql = (String) ScriptHelper.evel(ScriptTypes.forCode((Integer) table.get("mainSqlType")),
					(String) table.get("mainSqlScript"));

			if ("current".equals(downloadType)) {// 导出当前条件
				if (StringUtils.isEmpty(field)) {
					if (params != null && params.containsKey("orderBy")) {
						condition.setOrderBySQL((String) params.get("orderBy"));
					} else {
						condition.setOrderBySQL((String) table.get("orderBy"));
					}
				} else {
					condition.setOrderBy(field, dir);
				}

				// 高级查询
				for (Map<String, Object> query : ((Set<Map<String, Object>>) table.get("querys"))) {
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

			} else {// 导出所有
				condition = new DataCondition();
			}

			// 数据约束
			for (Map<String, Object> dataLimit : ((Set<Map<String, Object>>) table.get("limits"))) {
				// logger.debug("数据筛选器[" + dataLimit.get("id") + "]:" +
				// dataLimit.get("sqlScript"));
				CmPri pri = (CmPri) dataLimit.get("pri");
				Map<String, Object> context = new HashMap<>();
				if (!SessionManager.check(pri, context)) {
					// logger.debug("跳过筛选器[" + dataLimit.get("id") + "].");
					continue;
				}

				ScriptTypes type = ScriptTypes.forCode((Integer) dataLimit.get("sqlType"));
				String sql = (String) ScriptHelper.evel(type, (String) dataLimit.get("sqlScript"), context);
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
			datas = jdbcService.querySQL(sql.toString());

		}

		// 准备好数据结构
		List<HashMap<String, Object>> list = new ArrayList<>();
		List<String> fields = new ArrayList<>();
		Map<String, String> titles = new HashMap<>();

		List<Map<String, Object>> showFields = new ArrayList<>();
		for (Integer index : RequestUtils.getIntegerValues(request, "_index")) {
			showFields.add(config.getListFields().get(index));
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

			for (Map<String, Object> exec : ((Set<Map<String, Object>>) table.get("prepareExecs"))) {
				Object var = ScriptHelper.evel(ScriptTypes.forCode((Integer) exec.get("execType")),
						(String) exec.get("execScript"), context);
				context.put((String) exec.get("var"), var);
			}

			for (Map<String, Object> field : showFields) {
				String name = (String) field.get("name");
				if (StringUtils.isNotEmpty(name) && (((HashMap) obj).get(name) instanceof byte[])) {// 附件
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
