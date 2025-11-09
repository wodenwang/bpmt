/*
 * $HeadURL: $
 * $Id: $
 * Copyright (c) 2013 by Riversoft System, all rights reserved.
 */
package com.riversoft.module.dyn;

import java.io.IOException;
import java.io.Serializable;
import java.math.BigDecimal;
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
import com.riversoft.core.db.DataPO;
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
import com.riversoft.core.web.annotation.ActionAccess;
import com.riversoft.core.web.annotation.ActionAccess.SafeLevel;
import com.riversoft.core.web.annotation.ActionMode;
import com.riversoft.core.web.annotation.ActionMode.Mode;
import com.riversoft.platform.SessionManager;
import com.riversoft.platform.db.ORMAdapterService;
import com.riversoft.platform.db.Types;
import com.riversoft.platform.po.CmPri;
import com.riversoft.platform.po.TbColumn;
import com.riversoft.platform.po.TbTable;
import com.riversoft.platform.script.ScriptHelper;
import com.riversoft.platform.script.ScriptTypes;
import com.riversoft.platform.web.CommonHelper;
import com.riversoft.platform.web.FileManager;
import com.riversoft.platform.web.FileManager.UploadFile;
import com.riversoft.platform.web.ListFileExcelParser;
import com.riversoft.platform.web.WebLogManager;
import com.riversoft.util.Formatter;
import com.riversoft.util.ValueConvertUtils;

/**
 * 动态表CRUD基类
 * 
 * @author Woden
 * 
 */
@SuppressWarnings("unchecked")
public abstract class BaseDynamicTableCRUDAction {
	/**
	 * Logger for this class
	 */
	private static final Logger logger = LoggerFactory.getLogger(BaseDynamicTableCRUDAction.class);

	/**
	 * 关联主键,唯一不变值
	 */
	private final String viewKey;

	/**
	 * 绑定service
	 */
	private final DynamicTableCRUDService service;

	protected BaseDynamicTableCRUDAction(String viewKey) {
		this.viewKey = viewKey;
		this.service = BeanFactory.getInstance().getBean(DynamicTableCRUDService.class);
		synchronized (service) {
			service.setViewKey(viewKey);
		}
	}

	public static class Config {

		private Map<String, Object> table;
		private List<Map<String, Object>> subs;// 子标签
		private List<Map<String, Object>> querys;// 查询条件字段
		private List<Map<String, Object>> listFields;// 列表展示字段
		private List<Map<String, Object>> formFields;// 表单展示字段
		private List<Map<String, Object>> detailFields;// 明细展示字段
		private List<Map<String, Object>> downloadFields;// 下载的字段

		private String[] keysArray;// 主键列表

		private List<Map<String, Object>> itemBtns;// 明细按钮
		private List<Map<String, Object>> summaryBtns;// 汇总按钮
		private Map<String, Object> addBtn = null;// "新增"按钮
		private Map<String, Object> deleteAllBtn = null;// "删除所有"按钮
		private Map<String, Object> deleteBtn = null;// "删除"按钮
		private Map<String, Object> updateBtn = null;// "编辑"按钮
		private Map<String, Object> detailBtn = null;// "查看明细"按钮

		// 微信端字段,列表
		private List<Map<String, Object>> h5DetailList = null;
		private List<Map<String, Object>> h5FormList = null;

		private Config(Map<String, Object> table) {
			this.table = table;

			// 设置主键
			{
				DataCondition condition = new DataCondition();
				condition.setNumberEqual("primaryKey", "1").setStringEqual("tableName", (String) table.get("name"));
				List<TbColumn> list = ORMService.getInstance().query(TbColumn.class.getName(), condition.toEntity());
				keysArray = new String[list.size()];
				for (int i = 0; i < list.size(); i++) {
					keysArray[i] = list.get(i).getName();
				}
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

			{
				listFields = new ArrayList<>();
				formFields = new ArrayList<>();
				detailFields = new ArrayList<>();
				downloadFields = new ArrayList<>();

				// 固定字段
				for (Map<String, Object> field : (Set<Map<String, Object>>) table.get("columns")) {
					Integer showFlag = (Integer) field.get("showFlag");
					if (showFlag == null || showFlag.intValue() == 1) {
						detailFields.add(field);
						Integer listSort = (Integer) field.get("listSort");
						if (listSort != null && listSort.intValue() >= 0) {
							listFields.add(field);
						}
					}

					Integer formFlag = (Integer) field.get("formFlag");
					if (formFlag == null || formFlag.intValue() == 1) {
						formFields.add(field);
					}

					downloadFields.add(field);
				}

				for (Map<String, Object> field : (Set<Map<String, Object>>) table.get("showColumns")) {
					detailFields.add(field);
					formFields.add(field);
					downloadFields.add(field);
					Integer listSort = (Integer) field.get("listSort");
					if (listSort != null && listSort.intValue() >= 0) {
						listFields.add(field);
					}
				}

				detailFields.addAll((Set<Map<String, Object>>) table.get("lineColumns"));
				formFields.addAll((Set<Map<String, Object>>) table.get("lineColumns"));
				formFields.addAll((Set<Map<String, Object>>) table.get("formColumns"));

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

				Collections.sort(formFields, new Comparator<Map<String, Object>>() {
					@Override
					public int compare(Map<String, Object> o1, Map<String, Object> o2) {
						return (Integer) o1.get("sort") < (Integer) o2.get("sort") ? -1 : 1;
					}
				});

				Collections.sort(downloadFields, new Comparator<Map<String, Object>>() {
					@Override
					public int compare(Map<String, Object> o1, Map<String, Object> o2) {
						return (Integer) o1.get("sort") < (Integer) o2.get("sort") ? -1 : 1;
					}
				});
			}

			// 微信h5端配置列表
			{
				h5DetailList = new ArrayList<>();
				h5FormList = new ArrayList<>();

				// 固定字段
				for (Map<String, Object> field : (Set<Map<String, Object>>) table.get("columns")) {
					Integer showFlag = (Integer) field.get("showFlag");
					if (showFlag == null || showFlag.intValue() == 1) {
						h5DetailList.add(field);
					}

					Integer formFlag = (Integer) field.get("formFlag");
					if (formFlag == null || formFlag.intValue() == 1) {
						h5FormList.add(field);
					}
				}

				h5DetailList.addAll((Set<Map<String, Object>>) table.get("showColumns"));
				h5DetailList.addAll((Set<Map<String, Object>>) table.get("lineColumns"));
				h5FormList.addAll((Set<Map<String, Object>>) table.get("lineColumns"));
				h5FormList.addAll((Set<Map<String, Object>>) table.get("formColumns"));
				h5FormList.addAll((Set<Map<String, Object>>) table.get("showColumns"));

				Collections.sort(h5DetailList, new Comparator<Map<String, Object>>() {
					@Override
					public int compare(Map<String, Object> o1, Map<String, Object> o2) {
						return (Integer) o1.get("sort") < (Integer) o2.get("sort") ? -1 : 1;
					}
				});
				Collections.sort(h5FormList, new Comparator<Map<String, Object>>() {
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

				current = null;
				i = 0;
				for (Map<String, Object> o : h5FormList) {
					if (o.get("whole") != null && current != null) {
						current.put("form_end", i);
					}

					if (o.get("whole") == null) {
						current = o;
						current.put("form_begin", i + 1);
					}

					i++;
				}

			}

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

					// 登记删除按钮的权限,用于处理表格左侧多选框
					if (SysBtn.CREATE.getName().equals(btn.get("name"))) {
						addBtn = btn;
					}
					if (SysBtn.DEL.getName().equals(btn.get("name"))) {
						deleteBtn = btn;
					}
					if (SysBtn.DEL_ALL.getName().equals(btn.get("name"))) {
						deleteAllBtn = btn;
					}
					if (SysBtn.EDIT.getName().equals(btn.get("name"))) {
						updateBtn = btn;
					}
					if (SysBtn.DETAIL.getName().equals(btn.get("name"))) {
						detailBtn = btn;
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
		 * @return the addBtn
		 */
		public Map<String, Object> getAddBtn() {
			return addBtn;
		}

		/**
		 * @return the h5DetailList
		 */
		public List<Map<String, Object>> getH5DetailList() {
			return h5DetailList;
		}

		/**
		 * @return the h5FormList
		 */
		public List<Map<String, Object>> getH5FormList() {
			return h5FormList;
		}

		/**
		 * @return the subs
		 */
		public List<Map<String, Object>> getSubs() {
			return subs;
		}

		/**
		 * @return the updateBtn
		 */
		public Map<String, Object> getUpdateBtn() {
			return updateBtn;
		}

		/**
		 * @return the deleteAllBtn
		 */
		public Map<String, Object> getDeleteAllBtn() {
			return deleteAllBtn;
		}

		/**
		 * @return the deleteBtn
		 */
		public Map<String, Object> getDeleteBtn() {
			return deleteBtn;
		}

		/**
		 * @return the detailBtn
		 */
		public Map<String, Object> getDetailBtn() {
			return detailBtn;
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
		 * @return the downloadFields
		 */
		public List<Map<String, Object>> getDownloadFields() {
			return downloadFields;
		}

		/**
		 * @return the keysArray
		 */
		public String[] getKeysArray() {
			return keysArray;
		}

		/**
		 * @return the table
		 */
		public Map<String, Object> getTable() {
			return table;
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
		 * @return the formFields
		 */
		public List<Map<String, Object>> getFormFields() {
			return formFields;
		}

		/**
		 * @return the detailFields
		 */
		public List<Map<String, Object>> getDetailFields() {
			return detailFields;
		}

	}

	/**
	 * 查询动态表配置
	 * 
	 * @return
	 */
	private Map<String, Object> getTableConfig() {
		Map<String, Object> table = (Map<String, Object>) ORMService.getInstance().findByPk("VwDynTable", viewKey);
		if (table == null) {
			throw new SystemRuntimeException(ExceptionType.CONFIG, "视图已删除.");
		}
		return table;
	}

	/**
	 * 首页分发
	 * 
	 * @param request
	 * @param response
	 */
	@ActionMode(Mode.FIT)
	public void index(HttpServletRequest request, HttpServletResponse response) {
		Map<String, Object> params = RequestUtils.getJsonValue(request, Keys.PARAMS.toString());
		if (params != null) {
			if (params.containsKey("list") && "true".equalsIgnoreCase(params.get("list").toString())) {// 直接跳转到明细页
				Actions.forwardAction(request, response, Util.getActionUrl(request) + "/list.shtml");
				return;
			} else if (params.containsKey("detail") && "true".equalsIgnoreCase(params.get("detail").toString())) {// 明细页
				Actions.forwardAction(request, response, Util.getActionUrl(request) + "/detail.shtml");
				return;
			} else if (params.containsKey("form") && "true".equalsIgnoreCase(params.get("form").toString())) {// 表单页
				if (params.containsKey("key")) {
					Actions.forwardAction(request, response, Util.getActionUrl(request) + "/updateZone.shtml");
				} else {
					Actions.forwardAction(request, response, Util.getActionUrl(request) + "/createZone.shtml");
				}
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
		Config config = new Config(getTableConfig());
		request.setAttribute("config", config);

		HashMap<String, Object> params = RequestUtils.getJsonValue(request, Keys.PARAMS.toString());
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
		Util.setTitle(request, title);

		request.setAttribute(Keys.H5_JS.toString(), "amaze");// 视图使用amaze ui
		Actions.includePage(request, response, Util.getPagePath(request, "/dyn/main.jsp"));
	}

	/**
	 * 查询
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

		// 默认分页
		Integer pageLimit = (Integer) config.getTable().get("pageLimit");
		if (params != null) {
			if (params.containsKey("pageLimit")) {
				pageLimit = ((Number) params.get("pageLimit")).intValue();
			}
		}
		request.setAttribute("pageLimit", pageLimit);
		if (StringUtils.isEmpty(RequestUtils.getStringValue(request, Keys.LIMIT.toString())) && pageLimit != null && pageLimit > 0) {
			limit = pageLimit;
		}

		// 获取排序信息
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
			String queryName = (String) query.get("name");
			if (StringUtils.isEmpty(queryName)){ //高级查询有两种形式, 一种是没有name标识的, 一种有, 要区分
				queryName = "querys." + query.get("id");
			}
			String value = RequestUtils.getStringValue(request, queryName);
			if (StringUtils.isEmpty(value)) {
				continue;
			}
			Map<String, Object> context = new HashMap<String, Object>();// 构建上下文
			context.put("value", value);
			context.put("values", RequestUtils.getStringValues(request, queryName));
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

		DataPackage dp;
		Set<Map<String, Object>> parents = (Set<Map<String, Object>>) table.get("parents");
		if (parents == null || parents.size() < 1) {// 无设置父表
			dp = service.queryPackage((String) table.get("name"), start, limit, condition.toEntity());
		} else {
			TbTable tbTable = (TbTable) ORMService.getInstance().findByPk(TbTable.class.getName(), (String) table.get("name"));
			dp = new DataPackage();
			dp.setStart(start);
			dp.setLimit(limit);
			String sql = buildLeftJoinSql("vo", tbTable, parents) + " " + QueryStringBuilder.build("vo", condition.toEntity());
			dp.setTotalRecord(JdbcService.getInstance().getSQLCount(sql));
			dp.setList(JdbcService.getInstance().querySQLPage(sql, start, limit));
		}

		// 设置到页面
		request.setAttribute("dp", dp);

		String title = (String) config.getTable().get("busiName");
		if (params != null) {
			if (params.containsKey("title")) {
				title = params.get("title").toString();
			}
		}
		Util.setTitle(request, title);
		request.setAttribute("title", title);
		request.setAttribute("pageLimit", pageLimit);
		request.setAttribute("config", config);

		request.setAttribute(Keys.H5_JS.toString(), "amaze");// 视图使用amaze ui
		Actions.includePage(request, response, Util.getPagePath(request, "/dyn/list.jsp"));
	}

	/**
	 * 创建左关联SQL语句
	 * 
	 * @param pixel
	 * @param table
	 * @param parents
	 * @return
	 */
	private String buildLeftJoinSql(String pixel, TbTable table, Set<Map<String, Object>> parents) {

		if (parents == null || parents.size() == 0) {
			return table.getSelectAllSql();
		}
		String sql = table.getSelectAllSql(pixel);

		// 分隔sql
		String select = sql.substring(0, sql.indexOf("from"));
		String from = sql.substring(sql.indexOf("from"));

		StringBuffer builder = new StringBuffer();
		builder.append(select);
		for (Map<String, Object> parent : parents) {
			TbTable tbTable = (TbTable) ORMService.getInstance().findByPk(TbTable.class.getName(), (String) parent.get("tableName"));
			String var = (String) parent.get("var");
			StringBuffer buff = new StringBuffer();
			for (TbColumn column : tbTable.getTbColumns()) {
				// 前缀格式为:_var_
				buff.append(",").append(var).append(".").append(column.getName()).append(" as ").append("_").append(var).append("_").append(column.getName());
			}
			builder.append(buff);
		}

		builder.append(" ").append(from);

		// left join
		for (Map<String, Object> parent : parents) {
			String var = (String) parent.get("var");
			builder.append(" left join ");
			builder.append(parent.get("tableName")).append(" ").append(var).append(" on ");
			// 外键
			StringBuffer buff = new StringBuffer();
			for (Map<String, Object> foreign : (Set<Map<String, Object>>) parent.get("foreigns")) {
				// 前缀格式为:_var_
				buff.append("and");
				buff.append(" ").append(var).append(".").append(foreign.get("parentColumn")).append(" = ").append(pixel).append(".").append(foreign.get("mainColumn"));
			}
			builder.append(buff.substring(3));// 截取第一个"and"之后的值
		}
		if (logger.isDebugEnabled()) {
			logger.debug("有left join操作,转换SQL:[" + builder.toString() + "]");
		}
		return builder.toString();
	}

	/**
	 * 将主键转换为map
	 * 
	 * @param table
	 * @param pk
	 * @return
	 */
	private Map<String, Object> buildKeyParam(TbTable table, Serializable pk) {
		Map<String, Object> param;
		if (pk instanceof Map) {
			param = (Map<String, Object>) pk;
		} else {
			param = new HashMap<>();
			for (TbColumn column : table.getTbColumns()) {
				if (column.isPrimaryKey()) {
					param.put(column.getName(), pk);
					break;
				}
			}
		}
		return param;
	}

	/**
	 * 根据父表配置创建context
	 * 
	 * @param context
	 * @param parents
	 * @param vo
	 * @return
	 */
	private void buildParentContext(Map<String, Object> context, Set<Map<String, Object>> parents, Map<String, Object> vo) {
		if (parents != null && parents.size() > 0) {
			for (Map<String, Object> parent : parents) {
				String var = (String) parent.get("var");
				String rulePixel = "_" + var + "_";// 按规则生成前缀
				Map<String, Object> newVO = new HashMap<String, Object>();
				for (String key : vo.keySet()) {
					if (key.startsWith(rulePixel)) {
						newVO.put(key.substring(rulePixel.length()), vo.get(key));
					}
				}
				context.put(var, newVO);
			}
		}
	}

	/**
	 * 详细
	 * 
	 * @param request
	 * @param response
	 */
	@ActionMode(Mode.FIT)
	public void detail(HttpServletRequest request, HttpServletResponse response) {
		HashMap<String, Object> jsonObject = RequestUtils.getJsonValue(request, "_key");
		if (jsonObject == null) {
			HashMap<String, Object> params = RequestUtils.getJsonValue(request, Keys.PARAMS.toString());
			if (params != null) {
				if (params.containsKey("key")) {
					jsonObject = (HashMap<String, Object>) params.get("key");
				}
			}
		}
		if (jsonObject == null) {
			throw new SystemRuntimeException(ExceptionType.CONFIG, "无效的主键入参.");
		}

		Map<String, Object> table = getTableConfig();

		// 判断是否需要外关联
		Set<Map<String, Object>> parents = (Set<Map<String, Object>>) table.get("parents");
		Object po;
		if (parents == null || parents.size() == 0) {
			po = service.findByPk((String) table.get("name"), jsonObject);
		} else {
			TbTable tbTable = (TbTable) ORMService.getInstance().findByPk(TbTable.class.getName(), (String) table.get("name"));
			String sql = buildLeftJoinSql("vo", tbTable, parents);
			String where = tbTable.getFindByPkSql("vo");
			where = where.substring(where.indexOf("where"));
			po = JdbcService.getInstance().findSQL(sql + " " + where, buildKeyParam(tbTable, jsonObject));
			if (po != null) {
				((Map<String, Object>) po).put("$type$", tbTable.getName());
			}
		}
		request.setAttribute("vo", po);

		Config config = new Config(table);
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
		Actions.includePage(request, response, Util.getPagePath(request, "/dyn/detail.jsp"));
	}

	/**
	 * 详细
	 * 
	 * @param request
	 * @param response
	 */
	@ActionMode(Mode.FIT)
	public void sub(HttpServletRequest request, HttpServletResponse response) {
		HashMap<String, Object> jsonObject = RequestUtils.getJsonValue(request, "_key");
		Map<String, Object> table = getTableConfig();

		// 判断是否需要外关联
		Set<Map<String, Object>> parents = (Set<Map<String, Object>>) table.get("parents");
		Object po;
		if (parents == null || parents.size() == 0) {
			po = service.findByPk((String) table.get("name"), jsonObject);
		} else {
			TbTable tbTable = (TbTable) ORMService.getInstance().findByPk(TbTable.class.getName(), (String) table.get("name"));
			String sql = buildLeftJoinSql("vo", tbTable, parents);
			String where = tbTable.getFindByPkSql("vo");
			where = where.substring(where.indexOf("where"));
			po = JdbcService.getInstance().findSQL(sql + " " + where, buildKeyParam(tbTable, jsonObject));
			if (po != null) {
				((Map<String, Object>) po).put("$type$", tbTable.getName());
			}
		}
		request.setAttribute("vo", po);

		// 前置处理
		for (Map<String, Object> exec : (Set<Map<String, Object>>) table.get("prepareExecs")) {
			Integer type = (Integer) exec.get("execType");
			String script = (String) exec.get("execScript");
			if (StringUtils.isNotEmpty(script)) {
				Map<String, Object> context = new HashMap<String, Object>();
				context.put("vo", po);
				context.put("mode", DisplayMode.DETAIL.getValue());// 明细
				buildParentContext(context, parents, (Map<String, Object>) po);
				ScriptHelper.evel(ScriptTypes.forCode(type), script, context);
			}
		}

		Config config = new Config(table);
		request.setAttribute("config", config);

		request.setAttribute(Keys.H5_JS.toString(), "amaze");// 视图使用amaze ui
		Actions.includePage(request, response, Util.getPagePath(request, "/dyn/sub.jsp"));
	}

	/**
	 * 操作历史
	 * 
	 * @param request
	 * @param response
	 */
	public void log(HttpServletRequest request, HttpServletResponse response) {
		Config config = new Config(getTableConfig());
		HashMap<String, Object> jsonObject = RequestUtils.getJsonValue(request, "_key");
		String logTableName = (String) config.getTable().get("logTable");
		DataCondition dataCondition = new DataCondition(Util.buildQueryMap(new HashMap<String, Object>(), request));
		// 获取排序信息
		int start = Util.getStart(request);
		int limit = Util.getLimit(request);

		if (StringUtils.isNotEmpty(logTableName)) {
			TbTable tbTable = (TbTable) ORMService.getInstance().findByPk(TbTable.class.getName(), logTableName);
			for (TbColumn column : tbTable.getTbColumns()) {
				if (jsonObject.containsKey(column.getName())) {
					if (column.isOfTextType()) {
						dataCondition.setStringEqual(column.getName(), jsonObject.get(column.getName()).toString());
					} else if (column.isOfNumericType()) {
						dataCondition.setNumberEqual(column.getName(), String.valueOf(jsonObject.get(column.getName())));
					}
				}
			}
			dataCondition.setOrderByDesc(DynLogModelKeys.LOG_ID.getColumn().getName());

			DataPackage dp = ORMAdapterService.getInstance().queryPackage(logTableName, start, limit, dataCondition.toEntity());
			request.setAttribute("dp", dp);
		}

		request.setAttribute(Keys.H5_JS.toString(), "amaze");// 视图使用amaze ui
		Actions.includePage(request, response, Util.getPagePath(request, "/dyn/log.jsp"));
	}

	/**
	 * 新增
	 * 
	 * @param request
	 * @param response
	 */
	@ActionMode(Mode.FIT)
	@ActionAccess(level = SafeLevel.BUSI_W)
	public void createZone(HttpServletRequest request, HttpServletResponse response) {
		Map<String, Object> table = getTableConfig();
		// 前置处理
		for (Map<String, Object> exec : (Set<Map<String, Object>>) table.get("prepareExecs")) {
			Integer type = (Integer) exec.get("execType");
			String script = (String) exec.get("execScript");
			if (StringUtils.isNotEmpty(script)) {
				Map<String, Object> context = new HashMap<String, Object>();
				context.put("vo", null);
				context.put("mode", DisplayMode.FORM.getValue());// 表单
				ScriptHelper.evel(ScriptTypes.forCode(type), script, context);
			}
		}
		Config config = new Config(table);
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
		Actions.includePage(request, response, Util.getPagePath(request, "/dyn/form.jsp"));
	}

	/**
	 * 修改
	 * 
	 * @param request
	 * @param response
	 */
	@ActionMode(Mode.FIT)
	@ActionAccess(level = SafeLevel.BUSI_W)
	public void updateZone(HttpServletRequest request, HttpServletResponse response) {
		HashMap<String, Object> jsonObject = RequestUtils.getJsonValue(request, "_key");
		if (jsonObject == null) {
			HashMap<String, Object> params = RequestUtils.getJsonValue(request, Keys.PARAMS.toString());
			if (params != null) {
				if (params.containsKey("key")) {
					jsonObject = (HashMap<String, Object>) params.get("key");
				}
			}
		}
		if (jsonObject == null) {
			throw new SystemRuntimeException(ExceptionType.CONFIG, "无效的主键入参.");
		}

		Map<String, Object> table = getTableConfig();

		// 判断是否需要外关联
		Set<Map<String, Object>> parents = ((Set<Map<String, Object>>) table.get("parents"));
		Object po;
		if (parents == null || parents.size() == 0) {
			po = service.findByPk((String) table.get("name"), jsonObject);
		} else {
			TbTable tbTable = (TbTable) ORMService.getInstance().findByPk(TbTable.class.getName(), (String) table.get("name"));
			String sql = buildLeftJoinSql("vo", tbTable, parents);
			String where = tbTable.getFindByPkSql("vo");
			where = where.substring(where.indexOf("where"));
			po = JdbcService.getInstance().findSQL(sql + " " + where, buildKeyParam(tbTable, jsonObject));
			if (po != null) {
				((Map<String, Object>) po).put("$type$", tbTable.getName());
			}
		}
		request.setAttribute("vo", po);

		// 前置处理
		for (Map<String, Object> exec : (Set<Map<String, Object>>) table.get("prepareExecs")) {
			Integer type = (Integer) exec.get("execType");
			String script = (String) exec.get("execScript");
			if (StringUtils.isNotEmpty(script)) {
				Map<String, Object> context = new HashMap<String, Object>();
				context.put("vo", po);
				context.put("mode", DisplayMode.FORM.getValue());// 表单
				buildParentContext(context, parents, (Map<String, Object>) po);
				ScriptHelper.evel(ScriptTypes.forCode(type), script, context);
			}
		}

		Config config = new Config(table);
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
		Actions.includePage(request, response, Util.getPagePath(request, "/dyn/form.jsp"));
	}

	/**
	 * 删除
	 * 
	 * @param request
	 * @param response
	 */
	@ActionAccess(level = SafeLevel.BUSI_W)
	@ActionMode(Mode.FIT)
	public void delete(HttpServletRequest request, HttpServletResponse response) {
		List<HashMap<String, Object>> keys = RequestUtils.getJsonValues(request, "_keys");

		Map<String, Object> table = getTableConfig();

		List<Serializable> list = new ArrayList<>();
		list.addAll(keys);
		service.removeByPkBath((String) table.get("name"), list);

		if ("json".equalsIgnoreCase(RequestUtils.getStringValue(request, Keys.DATA_TYPE.toString()))) {
			Map<String, Object> result = new HashMap<>();
			result.put("msg", "删除成功。");
			Actions.showJson(request, response, result);
		} else {
			Actions.redirectInfoPage(request, response, "删除成功。");
		}
	}

	/**
	 * 新增或修改提交<br>
	 * 返回记录主键的JSON
	 * 
	 * @param request
	 * @param response
	 */
	@ActionMode(Mode.FIT)
	@ActionAccess(level = SafeLevel.BUSI_W)
	public void submit(HttpServletRequest request, HttpServletResponse response) {

		Map<String, Object> table = getTableConfig();
		Map<String, Object> po;
		// 获取原数据
		boolean editMode;
		HashMap<String, Object> jsonObject = RequestUtils.getJsonValue(request, "_key");
		if (jsonObject != null) {
			po = (Map<String, Object>) service.findByPk((String) table.get("name"), jsonObject);
			editMode = true;
		} else {
			po = new HashMap<String, Object>();
			editMode = false;
		}
		CommonHelper.map(po, (String) table.get("name"));

		Object pk = service.executeSubmit(po, true, editMode);

		// TODO暂时用这个方式
		Actions.showJson(request, response, pk);
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
		Actions.includePage(request, response, Util.getPagePath(request, "/dyn/download.jsp"));
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
		Set<Map<String, Object>> parents = (Set<Map<String, Object>>) table.get("parents");

		if ("selected".equals(downloadType)) {// 导出所选
			List<HashMap<String, Object>> keys = RequestUtils.getJsonValues(request, "_keys");
			datas = new ArrayList<Object>();
			if (keys != null) {
				for (HashMap key : keys) {
					Object po;
					if (parents != null && parents.size() > 0) {
						TbTable tbTable = (TbTable) ORMService.getInstance().findByPk(TbTable.class.getName(), (String) table.get("name"));
						String sql = tbTable.getFindByPkSql("vo");// 主键查询SQL
						String where = sql.substring(sql.indexOf(" where "));
						sql = buildLeftJoinSql("vo", tbTable, parents) + "  " + where;
						po = JdbcService.getInstance().findSQL(sql, key);
					} else {
						po = service.findByPk((String) table.get("name"), key);
					}
					((List<Object>) datas).add(po);
				}
			}
		} else {
			// 查询条件
			DataCondition condition;
			if ("current".equals(downloadType)) {// 导出当前条件
				// 普通查询
				condition = new DataCondition(Util.buildQueryMap(new HashMap<String, Object>(), request));

				// 高级查询
				for (Map<String, Object> query : ((Set<Map<String, Object>>) table.get("extQuerys"))) {
		            String queryName = (String) query.get("name");
					if (StringUtils.isEmpty(queryName)){ //高级查询有两种形式, 一种是没有name标识的, 一种有, 要区分
						queryName = "querys." + query.get("id");
					}
					String value = RequestUtils.getStringValue(request, queryName);
					if (StringUtils.isEmpty(value)) {
						continue;
					}
					Map<String, Object> context = new HashMap<String, Object>();// 构建上下文
					context.put("value", value);
					context.put("values", RequestUtils.getStringValues(request, queryName));
					ScriptTypes type = ScriptTypes.forCode((Integer) query.get("sqlType"));
					String sql = (String) ScriptHelper.evel(type, (String) query.get("sqlScript"), context);
					condition.addSql(sql);
				}
			} else {// 导出所有
				condition = new DataCondition();
			}

			// 获取排序信息
			String field = Util.getSortField(request);
			String dir = Util.getSortDir(request);

			// 设置默认排序条件
			if (StringUtils.isEmpty(field)) {
				HashMap<String, Object> params = RequestUtils.getJsonValue(request, Keys.PARAMS.toString());
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

			// 设置排序
			condition.setOrderBy(field, dir);

			// 数据约束
			for (Map<String, Object> dataLimit : ((Set<Map<String, Object>>) table.get("limits"))) {
				logger.debug("数据筛选器[" + dataLimit.get("id") + "]:" + dataLimit.get("sqlScript"));
				CmPri pri = (CmPri) dataLimit.get("pri");
				if (!SessionManager.check(pri)) {
					logger.debug("跳过筛选器[" + dataLimit.get("id") + "].");
					continue;
				}
				ScriptTypes type = ScriptTypes.forCode((Integer) dataLimit.get("sqlType"));
				String sql = (String) ScriptHelper.evel(type, (String) dataLimit.get("sqlScript"));
				condition.addSql(sql);
			}

			if (parents == null || parents.size() < 1) {// 无设置父表
				datas = service.query((String) table.get("name"), condition.toEntity());
			} else {
				TbTable tbTable = (TbTable) ORMService.getInstance().findByPk(TbTable.class.getName(), (String) table.get("name"));
				String sql = buildLeftJoinSql("vo", tbTable, parents) + " " + QueryStringBuilder.build("vo", condition.toEntity());
				datas = JdbcService.getInstance().querySQL(sql);
			}
		}

		// 准备好数据结构
		List<HashMap<String, Object>> list = new ArrayList<>();
		List<String> fields = new ArrayList<>();
		Map<String, String> titles = new HashMap<>();

		List<Map<String, Object>> showFields = new ArrayList<>();
		for (Integer index : RequestUtils.getIntegerValues(request, "_index")) {
			showFields.add(config.getDownloadFields().get(index));
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
			// 处理parents
			if (parents != null && parents.size() > 0) {
				buildParentContext(context, parents, (Map<String, Object>) obj);
			}
			context.put("vo", obj);
			context.put("mode", DisplayMode.EXPORT.getValue());
			
			//展示变量
			for (Map<String, Object> exec : ((Set<Map<String, Object>>) table.get("prepareExecs"))) {
				Object var = ScriptHelper.evel(ScriptTypes.forCode((Integer) exec.get("execType")),
						(String) exec.get("execScript"), context);
				context.put((String) exec.get("var"), var);
			}
			
			// 前置处理
			for (Map<String, Object> exec : (Set<Map<String, Object>>) table.get("prepareExecs")) {
				Integer type = (Integer) exec.get("execType");
				String script = (String) exec.get("execScript");
				if (StringUtils.isNotEmpty(script)) {
					ScriptHelper.evel(ScriptTypes.forCode(type), script, context);
				}
			}

			for (Map<String, Object> field : showFields) {
				String name = (String) field.get("name");
				if (StringUtils.isNotEmpty(name) && (((Map) obj).get(name) instanceof byte[])) {// 附件
					vo.put(name, "[附件不支持]");
					continue;
				}

				Object value = ScriptHelper.evel(ScriptTypes.forCode((Integer) field.get("contentType")), (String) field.get("contentScript"), context);
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
		Actions.downloadExcel(request, response, table.get("busiName") + "-批量导出" + "_" + Formatter.formatDatetime(new Date(), "yyyyMMddHHmmss") + ".xlsx", fields.toArray(new String[fields.size()]),
				titles, list);
	}

	/**
	 * 批量导入处理
	 * 
	 * @param request
	 * @param response
	 */
	@ActionAccess(level = SafeLevel.BUSI_W)
	public void uploadSettingZone(HttpServletRequest request, HttpServletResponse response) {
		request.setAttribute("config", new Config(getTableConfig()));

		request.setAttribute(Keys.H5_JS.toString(), "amaze");// 视图使用amaze ui
		Actions.includePage(request, response, Util.getPagePath(request, "/dyn/batch.jsp"));
	}

	/**
	 * 获取批量导入模板
	 * 
	 * @param request
	 * @param response
	 */
	public void getBatchFile(HttpServletRequest request, HttpServletResponse response) {
		Config config = new Config(getTableConfig());
		List<String> fields = new ArrayList<>();
		Map<String, String> titles = new HashMap<>();
		for (Map<String, Object> field : (Set<Map<String, Object>>) config.getTable().get("columns")) {
			fields.add((String) field.get("name"));
			titles.put((String) field.get("name"), (String) field.get("busiName"));
		}

		Actions.downloadExcel(request, response, "批量导入_" + config.getTable().get("viewKey") + "_.xls", fields.toArray(new String[0]), titles, new ArrayList<HashMap<String, Object>>());
	}

	/**
	 * 普通批量提交
	 * 
	 * @param request
	 * @param response
	 */
	public void submitBatch(HttpServletRequest request, HttpServletResponse response) {
		boolean editMode = "2".equals(RequestUtils.getStringValue(request, "type"));// 新增还是修改
		Config config = new Config(getTableConfig());
		UploadFile uploadFile = FileManager.getUploadFile(request, "file");
		List<String> fields = new ArrayList<>();
		for (Map<String, Object> field : (Set<Map<String, Object>>) config.getTable().get("columns")) {
			fields.add((String) field.get("name"));
		}
		List<Map<String, String>> dataList;
		try {
			dataList = new ListFileExcelParser(fields, "data", 0).parse(uploadFile.getInputStream()).getResult();
		} catch (IOException e) {
			throw new SystemRuntimeException("从文件获取数据出错.", e);
		}
		logger.debug("上传文件[" + uploadFile.getName() + "]数据记录数[" + dataList.size() + "].");

		// 处理原始数据
		List<Map<String, Object>> list = new ArrayList<>();
		if (dataList != null) {
			WebLogManager.beginLoop("正在预处理数据", dataList.size());
			for (Map<String, String> vo : dataList) {
				WebLogManager.signalLoop();
				Map<String, Object> po = map(new HashMap<String, Object>(), (String) config.getTable().get("name"), vo);
				list.add(po);
			}
		}

		WebLogManager.log("正在提交数据库事务.");
		service.executeBatchUpload(list, true, editMode);
		Actions.redirectInfoPage(request, response, editMode ? "批量更新成功。" : "批量新增成功。");
	}

	/**
	 * 从导入文件(一行)中获取信息
	 * 
	 * @param po
	 * @param tableName
	 * @param var
	 * @return
	 */
	private static Map<String, Object> map(Map<String, Object> po, String tableName, Map<String, ?> var) {
		TbTable table = (TbTable) ORMService.getInstance().findByPk(TbTable.class.getName(), tableName);
		if (table == null) {
			throw new SystemRuntimeException(ExceptionType.CONFIG, "表[" + tableName + "]不是合法动态表.");
		}
		po = new DataPO(tableName, po).toEntity();
		for (TbColumn column : table.getTbColumns()) {
			Object o = var.get(column.getName());
			String value = (o == null || o instanceof String) ? (String) o : o.toString();
			if (StringUtils.isEmpty(value)) {
				po.put(column.getName(), null);
				continue;
			}

			switch (Types.findByCode(column.getMappedTypeCode())) {
			case BigDecimal:
				po.put(column.getName(), ValueConvertUtils.convert(value, BigDecimal.class));
				break;
			case Integer:
				po.put(column.getName(), ValueConvertUtils.convert(value, Integer.class));
				break;
			case Long:
				po.put(column.getName(), ValueConvertUtils.convert(value, Long.class));
				break;
			case String:
			case Clob:
				po.put(column.getName(), value);
				break;
			case Date:
				po.put(column.getName(), ValueConvertUtils.convert(value, Date.class));
				break;
			case Blob:
				po.put(column.getName(), null);// 文件导入不处理附件
				break;
			default:
				break;
			}
		}
		return po;
	}

}
