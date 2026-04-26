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

import org.apache.commons.lang3.StringUtils;

import com.riversoft.core.context.RequestContext;
import com.riversoft.core.db.DataPO;
import com.riversoft.core.db.ORMService;
import com.riversoft.platform.template.DevelopmentOperation;

/**
 * @author woden
 * 
 */
public class StartEventConfigService {

	/**
	 * 保存配置
	 */
	@DevelopmentOperation("保存开始节点配置")
	@SuppressWarnings("unchecked")
	public void executeConfig() {

		RequestContext request = RequestContext.getCurrent();

		String pdId = request.getString("pdId");
		Map<String, Object> po = (Map<String, Object>) ORMService.getInstance().findHQL("from WfStartEvent where pdId = ?", pdId);
		if (po == null) {
			po = new DataPO("WfStartEvent").toEntity();
			po.put("pdId", pdId);
		}
		po.put("description", request.getString("description"));
		po.put("jsType", request.getInteger("jsType"));
		po.put("jsScript", request.getString("jsScript"));

		ORMService.getInstance().saveOrUpdate(po);

		// 按钮设置
		{
			List<Map<String, Object>> btnList = new ArrayList<>();
			String[] btns = request.getStrings("btns");
			if (btns != null) {
				int sort = 0;
				for (String pixel : btns) {
					String name = request.getString(pixel + ".name");
					if (StringUtils.isEmpty(name)) {
						continue;
					} else if (name.equals("start")) {
						String description = request.getString(pixel + ".description");
						Integer checkType = request.getInteger(pixel + ".checkType");
						String checkScript = request.getString(pixel + ".checkScript");
						Integer enabledTipType = request.getInteger(pixel + ".enabledTipType");
						String enabledTipScript = request.getString(pixel + ".enabledTipScript");
						Integer disabledTipType = request.getInteger(pixel + ".disabledTipType");
						String disabledTipScript = request.getString(pixel + ".disabledTipScript");
						String busiName = request.getString(pixel + ".busiName");
						String icon = request.getString(pixel + ".icon");
						Integer confirmType = request.getInteger(pixel + ".confirmType");
						String confirmScript = request.getString(pixel + ".confirmScript");
						Integer opinionFlag = request.getInteger(pixel + ".opinionFlag");
						Integer quickOpinionType = request.getInteger(pixel + ".quickOpinionType");
						String quickOpinionScript = request.getString(pixel + ".quickOpinionScript");
						String styleClass = request.getString(pixel + ".styleClass");
						Integer loading = request.getInteger(pixel + ".loading");

						DataPO item = new DataPO("WfStartEventBtnStart");
						item.set("pdId", pdId);
						item.set("description", description);
						item.set("checkType", checkType);
						item.set("checkScript", checkScript);
						item.set("enabledTipType", enabledTipType);
						item.set("enabledTipScript", enabledTipScript);
						item.set("disabledTipType", disabledTipType);
						item.set("disabledTipScript", disabledTipScript);
						item.set("confirmType", confirmType);
						item.set("confirmScript", confirmScript);
						item.set("opinionFlag", opinionFlag);
						item.set("quickOpinionType", quickOpinionType);
						item.set("quickOpinionScript", quickOpinionScript);
						item.set("styleClass", styleClass);
						item.set("icon", icon);
						item.set("busiName", busiName);
						item.set("loading", loading);
						item.set("sort", sort++);
						btnList.add(item.toEntity());
					} else if (name.equals("save")) {// 保存
						String description = request.getString(pixel + ".description");
						Integer checkType = request.getInteger(pixel + ".checkType");
						String checkScript = request.getString(pixel + ".checkScript");
						String busiName = request.getString(pixel + ".busiName");
						String icon = request.getString(pixel + ".icon");
						String styleClass = request.getString(pixel + ".styleClass");
						Integer loading = request.getInteger(pixel + ".loading");

						DataPO item = new DataPO("WfStartEventBtnSave");
						item.set("pdId", pdId);
						item.set("description", description);
						item.set("checkType", checkType);
						item.set("checkScript", checkScript);
						item.set("styleClass", styleClass);
						item.set("icon", icon);
						item.set("busiName", busiName);
						item.set("loading", loading);
						item.set("sort", sort++);
						btnList.add(item.toEntity());
					}
				}
			}
			ORMService.getInstance().saveOrUpdateBatch(btnList);
		}

		// 前后置处理器
		{
			ORMService.getInstance().executeHQL("delete from WfStartEventExecBefore where pdId = ?", po.get("pdId"));
			ORMService.getInstance().executeHQL("delete from WfStartEventExecAfter where pdId = ?", po.get("pdId"));
			List<Map<String, Object>> execList = new ArrayList<>();
			for (String type : "beforeExecs;afterExecs".split(";")) {
				String[] execs = request.getStrings(type);
				if (execs != null) {
					int sort = 0;
					for (String pixel : execs) {
						String description = request.getString(pixel + ".description");
						Integer execType = request.getInteger(pixel + ".execType");
						String execScript = request.getString(pixel + ".execScript");

						DataPO item;
						if (type.startsWith("before")) {// 前置处理器
							item = new DataPO("WfStartEventExecBefore");
						} else {
							item = new DataPO("WfStartEventExecAfter");
						}
						item.set("pdId", pdId);
						item.set("execType", execType);
						item.set("execScript", execScript);
						item.set("description", description);
						item.set("sort", sort++);
						execList.add(item.toEntity());
					}
				}
			}
			ORMService.getInstance().saveOrUpdateBatch(execList);
		}

		// 子表tab继承
		{
			ORMService.getInstance().executeHQL("delete from WfStartEventSubExtend where pdId = ?", po.get("pdId"));
			List<Map<String, Object>> subList = new ArrayList<>();
			String[] subs = request.getStrings("subs");
			if (subs != null) {
				int sort = 0;
				for (String pixel : subs) {
					String description = request.getString(pixel + ".description");
					Integer showFlag = request.getInteger(pixel + ".showFlag");
					String subKey = request.getString(pixel + ".subKey");

					DataPO item = new DataPO("WfStartEventSubExtend");
					item.set("pdId", pdId);
					item.set("description", description);
					item.set("showFlag", showFlag);
					item.set("description", description);
					item.set("subKey", subKey);
					item.set("sort", sort++);
					subList.add(item.toEntity());
				}
			}
			ORMService.getInstance().saveBatch(subList);
		}

		// 处理字段变更
		executeColumnConfig();

		// 处理字段排序
		executeColumnSort();
	}

	/**
	 * 根据类型获取对应配置表
	 * 
	 * @param type
	 * @return
	 */
	private String getORMTable(String type) {
		switch (type) {
		case "self_form":
			return "WfStartEventColumnForm";
		case "self_line":
			return "WfStartEventColumnLine";
		}

		return "WfStartEventColumnExtend";
	}

	/**
	 * 字段排版设置
	 */
	@SuppressWarnings("unchecked")
	private void executeColumnSort() {
		RequestContext request = RequestContext.getCurrent();
		String pdId = request.getString("pdId");

		if (!"true".equalsIgnoreCase(request.getString("hasColumnSort"))) {
			return;
		}

		List<HashMap<String, Object>> columns = request.getJsons("sortColumn");
		// 先将未继承字段放到底部
		ORMService.getInstance().executeHQL("update WfStartEventColumnExtend set sort = 9999 where pdId = ?", pdId);

		Collections.sort(columns, new Comparator<HashMap<String, Object>>() {
			@Override
			public int compare(HashMap<String, Object> o1, HashMap<String, Object> o2) {
				Number sort1 = (Number) o1.get("sort");
				Number sort2 = (Number) o2.get("sort");

				if (sort1.intValue() < sort2.intValue()) {
					return -1;
				} else if (sort1.intValue() > sort2.intValue()) {
					return 1;
				} else {
					return 0;
				}
			}
		});

		int sort = 1;
		for (HashMap<String, Object> o : columns) {
			Long id = Long.parseLong(o.get("id").toString());
			String type = (String) o.get("type");
			ORMService.getInstance().executeHQL("update " + getORMTable(type) + " set sort = ? where id = ?", sort++, id.longValue());

		}

	}

	/**
	 * 字段更新
	 */
	@SuppressWarnings("unchecked")
	private void executeColumnConfig() {
		RequestContext request = RequestContext.getCurrent();
		String pdId = request.getString("pdId");

		if (!"true".equalsIgnoreCase(request.getString("hasColumns"))) {
			return;
		}

		List<HashMap<String, Object>> deleteColumns = request.getJsons("deleteColumn");
		List<HashMap<String, Object>> waitColumns = request.getJsons("waitColumn");

		// 删除
		if (deleteColumns != null) {
			for (HashMap<String, Object> json : deleteColumns) {
				ORMService.getInstance().removeByPk(getORMTable(json.get("type").toString()), Long.parseLong(json.get("id").toString()));
			}
		}

		// 处理更新或新增
		if (waitColumns != null) {
			for (HashMap<String, Object> json : waitColumns) {
				String pixel = json.get("pixel").toString();
				Integer sort = (Integer) json.get("sort");
				Integer showFlag = json.containsKey("showFlag") ? (Integer) json.get("showFlag") : 0;
				String pixelKey;
				if (json.containsKey("pixelKey")) {
					pixelKey = json.get("pixelKey").toString();
				} else {
					pixelKey = null;
				}
				Long id;
				if (json.containsKey("id")) {
					id = new Long(json.get("id").toString());
				} else {
					id = null;
				}

				String ormTableName = getORMTable(json.get("type").toString());
				boolean isCreate;

				DataPO columnPO;
				if (StringUtils.isNotEmpty(pixelKey)) {
					Map<String, Object> extendVO = (Map<String, Object>) ORMService.getInstance().findHQL("from WfStartEventColumnExtend where pdId = ? and pixelKey = ?", pdId, pixelKey);
					if (extendVO != null) {
						isCreate = false;
						columnPO = new DataPO(ormTableName, extendVO);
					} else {
						isCreate = true;
						columnPO = new DataPO(ormTableName);
						columnPO.set("pdId", pdId);
						columnPO.set("pixelKey", pixelKey);
						columnPO.set("showFlag", 0);
					}
				} else if (id != null) {
					isCreate = false;
					columnPO = new DataPO(ormTableName, (Map<String, Object>) ORMService.getInstance().findByPk(ormTableName, id));
				} else {
					isCreate = true;
					columnPO = new DataPO(ormTableName);
					columnPO.set("pdId", pdId);
				}
				columnPO.set("sort", sort);
				columnPO.set("showFlag", showFlag);

				// 是否有编辑
				if ("true".equalsIgnoreCase(request.getString(pixel + ".flag"))) {
					switch (json.get("type").toString()) {
					case "self_form":// 表单
					{
						String name = request.getString(pixel + ".name");
						String busiName = request.getString(pixel + ".busiName");
						String widget = request.getString(pixel + ".widget");
						String contentScript = request.getString(pixel + ".contentScript");
						Integer contentType = request.getInteger(pixel + ".contentType");
						Integer whole = request.getInteger(pixel + ".whole");
						String execScript = request.getString(pixel + ".execScript");
						Integer execType = request.getInteger(pixel + ".execType");
						Integer tipType = request.getInteger(pixel + ".tipType");
						String tipScript = request.getString(pixel + ".tipScript");
						Integer widgetParamType = request.getInteger(pixel + ".widgetParamType");
						String widgetParamScript = request.getString(pixel + ".widgetParamScript");
						Integer decideType = request.getInteger(pixel + ".decideType");
						String decideScript = request.getString(pixel + ".decideScript");
						Integer editDecideType = request.getInteger(pixel + ".editDecideType");
						String editDecideScript = request.getString(pixel + ".editDecideScript");
						Integer showContentType = request.getInteger(pixel + ".showContentType");
						String showContentScript = request.getString(pixel + ".showContentScript");

						columnPO.set("decideType", decideType);
						columnPO.set("decideScript", decideScript);
						columnPO.set("editDecideType", editDecideType);
						columnPO.set("editDecideScript", editDecideScript);
						columnPO.set("name", name);
						columnPO.set("busiName", busiName);
						columnPO.set("widget", widget);
						columnPO.set("contentScript", contentScript);
						columnPO.set("contentType", contentType);
						columnPO.set("whole", whole);
						columnPO.set("execScript", execScript);
						columnPO.set("execType", execType);
						columnPO.set("tipType", tipType);
						columnPO.set("tipScript", tipScript);
						columnPO.set("widgetParamType", widgetParamType);
						columnPO.set("widgetParamScript", widgetParamScript);
						columnPO.set("showContentType", showContentType);
						columnPO.set("showContentScript", showContentScript);
					}
						break;
					case "self_line": // 分割线
					{
						String busiName = request.getString(pixel + ".busiName");
						Integer tipType = request.getInteger(pixel + ".tipType");
						String tipScript = request.getString(pixel + ".tipScript");
						Integer decideType = request.getInteger(pixel + ".decideType");
						String decideScript = request.getString(pixel + ".decideScript");
						columnPO.set("decideType", decideType);
						columnPO.set("decideScript", decideScript);
						columnPO.set("busiName", busiName);
						columnPO.set("tipType", tipType);
						columnPO.set("tipScript", tipScript);
						columnPO.set("expandFlag", request.getInteger(pixel + ".expandFlag"));
					}
						break;
					default:// 继承
					{
						String description = request.getString(pixel + ".description");
						Integer contentType = request.getInteger(pixel + ".contentType");
						String contentScript = request.getString(pixel + ".contentScript");
						columnPO.set("description", description);
						columnPO.set("contentType", contentType);
						columnPO.set("contentScript", contentScript);
					}
						break;
					}
				}

				if (isCreate) {
					ORMService.getInstance().save(columnPO.toEntity());
				} else {
					ORMService.getInstance().merge(columnPO.toEntity());
				}
			}
		}
	}
}
