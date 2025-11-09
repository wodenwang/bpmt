/*
 * $HeadURL: $
 * $Id: $
 * Copyright (c) 2013 by Riversoft System, all rights reserved.
 */
package com.riversoft.module.view.note;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;

import com.riversoft.core.context.RequestContext;
import com.riversoft.core.db.DataCondition;
import com.riversoft.core.db.DataPO;
import com.riversoft.core.db.ORMService;
import com.riversoft.core.exception.ExceptionType;
import com.riversoft.core.exception.SystemRuntimeException;
import com.riversoft.core.web.Actions;
import com.riversoft.core.web.Actions.Keys;
import com.riversoft.core.web.Actions.Util;
import com.riversoft.core.web.RequestUtils;
import com.riversoft.core.web.annotation.ActionMode;
import com.riversoft.core.web.annotation.ActionMode.Mode;
import com.riversoft.platform.SessionManager;
import com.riversoft.platform.db.ORMAdapterService;
import com.riversoft.platform.db.model.ModelKeyUtils;
import com.riversoft.platform.po.CmPri;
import com.riversoft.platform.po.TbTable;
import com.riversoft.platform.po.VwUrl;
import com.riversoft.platform.script.ScriptHelper;
import com.riversoft.platform.script.ScriptTypes;
import com.riversoft.platform.web.view.BaseDynamicViewAction;
import com.riversoft.platform.web.view.annotation.Conf;
import com.riversoft.platform.web.view.annotation.Conf.TargetType;
import com.riversoft.platform.web.view.annotation.PriConfigMethod;
import com.riversoft.platform.web.view.annotation.View;
import com.riversoft.platform.web.view.annotation.View.Group;
import com.riversoft.platform.web.view.annotation.View.LoginType;
import com.riversoft.util.ValueConvertUtils;

/**
 * 公告视图
 * 
 * @author woden
 * 
 */
@PriConfigMethod("priList")
@View(value = "note", group = Group.FUN, loginType = { LoginType.USER, LoginType.NONE })
@Conf(description = "系统公告", sort = 1000, target = { TargetType.HOME, TargetType.WX })
@SuppressWarnings("unchecked")
public class NoteViewAction extends BaseDynamicViewAction {

	/**
	 * 公告列表
	 */
	@Override
	@ActionMode(Mode.FIT)
	protected void main(HttpServletRequest request, HttpServletResponse response, String key) {
		Map<String, Object> table = (Map<String, Object>) ORMService.getInstance().findByPk("VwNoteTable", key);
		if (table == null) {
			throw new SystemRuntimeException(ExceptionType.CONFIG, "视图不存在.");
		}
		request.setAttribute("viewKey", key);
		String tableName = (String) table.get("tableName");
		DataCondition condition = new DataCondition();
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
		condition.setOrderByAsc("SORT");
		List<Map<String, Object>> list = ORMAdapterService.getInstance().query(tableName, condition.toEntity());
		// 排序
		Collections.sort(list, new Comparator<Map<String, Object>>() {
			@Override
			public int compare(Map<String, Object> o1, Map<String, Object> o2) {
				// 判断置顶
				Integer topFlag1 = (Integer) o1.get("TOP_FLAG");
				if (topFlag1 == null) {
					topFlag1 = 0;
				}
				Integer topFlag2 = (Integer) o2.get("TOP_FLAG");
				if (topFlag2 == null) {
					topFlag2 = 0;
				}
				if (topFlag1 > topFlag2) {
					return -1;
				} else if (topFlag1 == topFlag2) {
					return 0;
				} else {
					return 1;
				}
			}
		});

		Util.setTitle(request, (String) table.get("busiName"));
		request.setAttribute("list", list);

		request.setAttribute(Keys.H5_JS.toString(), "amaze");// 视图使用amaze ui
		Actions.includePage(request, response, Util.getPagePath(request, "list.jsp"));
	}

	/**
	 * 公告明细
	 * 
	 * @param request
	 * @param response
	 */
	@ActionMode(Mode.FIT)
	public void detail(HttpServletRequest request, HttpServletResponse response) {
		String viewKey = RequestUtils.getStringValue(request, "viewKey");
		Map<String, Object> table = (Map<String, Object>) ORMService.getInstance().findByPk("VwNoteTable", viewKey);
		if (table == null) {
			throw new SystemRuntimeException(ExceptionType.CONFIG, "视图不存在.");
		}
		String tableName = (String) table.get("tableName");
		String id = RequestUtils.getStringValue(request, "id");

		Map<String, Object> vo = (Map<String, Object>) ORMAdapterService.getInstance().findByPk(tableName, id);
		request.setAttribute("vo", vo);
		Util.setTitle(request, (String) table.get("busiName"));

		request.setAttribute(Keys.H5_JS.toString(), "amaze");// 视图使用amaze ui
		Actions.includePage(request, response, Util.getPagePath(request, "detail.jsp"));
	}

	@Override
	public void configForm(HttpServletRequest request, HttpServletResponse response, String key) {
		if (!StringUtils.isEmpty(key)) {
			Map<String, Object> table = (Map<String, Object>) ORMService.getInstance().findByPk("VwNoteTable", key);
			request.setAttribute("table", table);
		}

		List<TbTable> tables = new ArrayList<>();
		List<TbTable> sysTables = (List<TbTable>) ORMService.getInstance().queryAll(TbTable.class.getName());

		for (TbTable model : sysTables) {
			if (ModelKeyUtils.checkModel(NoteModelKeys.class, model)) {
				tables.add(model);
			}
		}
		request.setAttribute("tables", tables);
		Actions.includePage(request, response, Actions.Util.getPagePath(request, "view_note_config.jsp"));
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
			Map<String, Object> table = (Map<String, Object>) ORMService.getInstance().findByPk("VwNoteTable", key);

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

		Actions.includePage(request, response, Actions.Util.getPagePath(request, "view_note_limit_config.jsp"));
	}

	/**
	 * 添加数据约束
	 * 
	 * @param request
	 * @param response
	 */
	public void limitForm(HttpServletRequest request, HttpServletResponse response) {
		Actions.includePage(request, response, Actions.Util.getPagePath(request, "view_note_limit.jsp"));
	}

	/**
	 * 权限设置
	 * 
	 * @param request
	 * @param response
	 */
	public void priList(HttpServletRequest request, HttpServletResponse response) {
		String viewKey = RequestUtils.getStringValue(request, "viewKey");
		String groupId = RequestUtils.getStringValue(request, "groupId");

		VwUrl url = (VwUrl) ORMService.getInstance().findByPk(VwUrl.class.getName(), viewKey);
		request.setAttribute("vwUrl", url);

		List<String> priKeys = ORMService.getInstance().queryHQL("select priKey from CmPriGroupRelate where groupId = ?", groupId);
		request.setAttribute("priKeys", priKeys);

		if (!StringUtils.isEmpty(viewKey)) {
			Map<String, Object> table = (Map<String, Object>) ORMService.getInstance().findByPk("VwNoteTable", viewKey);
			request.setAttribute("table", table);
		}

		request.setAttribute("groupId", groupId);
		Actions.includePage(request, response, Actions.Util.getPagePath(request, "view_note_pri_list.jsp"));
	}

	private void buildTablePO(DataPO tablePO) {
		RequestContext request = RequestContext.getCurrent();
		{
			String tableName = request.getString("table.tableName");
			String busiName = request.getString("table.busiName");
			tablePO.set("tableName", tableName);
			tablePO.set("busiName", busiName);
		}
		{
			// limit部分
			Set<Map<String, Object>> limits = new HashSet<>();
			String[] pixels = request.getStrings("limits");
			Integer sort = 1;
			if (pixels != null) {
				for (String pixel : pixels) {
					Integer sqlType = request.getInteger(pixel + ".sqlType");
					String sqlScript = request.getString(pixel + ".sqlScript");
					String description = request.getString(pixel + ".description");
					DataPO limitPO = new DataPO("VwNoteLimit");
					limitPO.set("viewKey", tablePO.getString("viewKey"));
					limitPO.set("sqlType", sqlType);
					limitPO.set("sqlScript", sqlScript);
					limitPO.set("description", description);
					limitPO.set("sort", sort++);
					CmPri pri = ValueConvertUtils.convert(request.getString(pixel + ".pri"), CmPri.class);
					pri.setDevelopmentInfo(limitPO, "数据约束");
					limitPO.set("pri", pri);

					limits.add(limitPO.toEntity());
				}
			}
			tablePO.set("limits", limits);
		}
	}

	@Override
	public void saveConfig(String key) {

		// table部分
		DataPO tablePO = new DataPO("VwNoteTable");
		tablePO.set("viewKey", key);
		buildTablePO(tablePO);

		ORMService.getInstance().save(tablePO.toEntity());
	}

	@Override
	public void updateConfig(String key) {
		Map<String, Object> entity = (Map<String, Object>) ORMService.getInstance().loadByPk("VwNoteTable", key);
		// table部分
		DataPO tablePO = new DataPO("VwNoteTable", entity);
		{
			Set<Map<String, Object>> set = tablePO.getSubList("limits");
			tablePO.set("limits", null);
			ORMService.getInstance().removeBath(set);
		}

		if (entity == null) {
			throw new SystemRuntimeException(ExceptionType.BUSINESS, "视图已删除.");
		}
		buildTablePO(tablePO);

		ORMService.getInstance().merge(tablePO.toEntity());

	}

	@Override
	public void removeConfig(String key) {
		Map<String, Object> table = (Map<String, Object>) ORMService.getInstance().findByPk("VwNoteTable", key);
		if (table != null) {
			ORMService.getInstance().remove(table);
		}
	}

	@Override
	public String copyConfig(String key) {
		throw new SystemRuntimeException(ExceptionType.BUSINESS, "此视图不支持复制.");
	}
}
