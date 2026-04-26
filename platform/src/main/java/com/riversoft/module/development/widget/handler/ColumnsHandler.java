/*
 * $HeadURL: $
 * $Id: $
 * Copyright (c) 2013 by Riversoft System, all rights reserved.
 */
package com.riversoft.module.development.widget.handler;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.riversoft.core.context.RequestContext;
import com.riversoft.core.db.DataPO;
import com.riversoft.core.db.ORMService;
import com.riversoft.platform.po.CmPri;
import com.riversoft.platform.web.handler.Handler;
import com.riversoft.util.ValueConvertUtils;

/**
 * @author woden
 * 
 */
public class ColumnsHandler implements Handler {

	/**
	 * 根据type获取ORM对象
	 * 
	 * @param type
	 * @return
	 */
	private String getORMTable(String type) {
		switch (type) {
		case "show":
			return "WdgBaseColumnShow";
		case "form":
			return "WdgBaseColumnForm";
		}

		return null;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void handle(DataPO tablePO) {
		RequestContext request = RequestContext.getCurrent();

		if (!"true".equalsIgnoreCase(request.getString("hasColumns"))) {
			return;
		}

		List<HashMap<String, Object>> deleteColumns = request.getJsons("deleteColumn");
		List<HashMap<String, Object>> waitColumns = request.getJsons("waitColumn");

		// 删除
		if (deleteColumns != null) {
			for (HashMap<String, Object> deleteColumn : deleteColumns) {
				ORMService.getInstance().removeByPk(getORMTable(deleteColumn.get("type").toString()),
						((Number) deleteColumn.get("id")).longValue());
			}
		}

		// 处理更新或新增
		if (waitColumns != null) {
			for (HashMap<String, Object> waitColumn : waitColumns) {
				String pixel = waitColumn.get("pixel").toString();
				Integer sort = ((Number) waitColumn.get("sort")).intValue();
				String type = waitColumn.get("type").toString();

				Long id;
				if (waitColumn.containsKey("id")) {
					id = ((Number) waitColumn.get("id")).longValue();
				} else {
					id = null;
				}

				String ormTableName = getORMTable(type);
				boolean isCreate;
				DataPO columnPO;
				if (id != null) {
					isCreate = false;
					columnPO = new DataPO(ormTableName, (Map<String, Object>) ORMService.getInstance().findByPk(
							ormTableName, id.longValue()));
				} else {
					isCreate = true;
					columnPO = new DataPO(ormTableName);
					columnPO.set("widgetKey", tablePO.getString("widgetKey"));
				}
				columnPO.set("sort", sort);

				// 是否有编辑
				if ("true".equalsIgnoreCase(request.getString(pixel + ".flag"))) {
					switch (type) {
					case "show": // 展示
					{
						String busiName = request.getString(pixel + ".busiName");
						String name = request.getString(pixel + ".name");
						String contentScript = request.getString(pixel + ".contentScript");
						Integer contentType = request.getInteger(pixel + ".contentType");
						String style = request.getString(pixel + ".style");
						String sortField = request.getString(pixel + ".sortField");

						Integer inWait = request.getInteger(pixel + ".inWait");
						if (inWait == null) {
							inWait = 0;
						}
						Integer inSelected = request.getInteger(pixel + ".inSelected");
						if (inSelected == null) {
							inSelected = 0;
						}
						Integer inResult = request.getInteger(pixel + ".inResult");
						if (inResult == null) {
							inResult = 0;
						}

						columnPO.set("inWait", inWait);
						columnPO.set("inSelected", inSelected);
						columnPO.set("inResult", inResult);
						columnPO.set("busiName", busiName);
						columnPO.set("name", name);
						columnPO.set("contentScript", contentScript);
						columnPO.set("contentType", contentType);
						columnPO.set("style", style);
						columnPO.set("sortField", sortField);
						CmPri pri = ValueConvertUtils.convert(request.getString(pixel + ".pri"), CmPri.class);
						pri.setDevelopmentInfo(columnPO, "展示字段");
						columnPO.set("pri", pri);
					}
						break;
					case "form":// 表单
					{
						String name = request.getString(pixel + ".name");
						String busiName = request.getString(pixel + ".busiName");
						String widget = request.getString(pixel + ".widget");
						String contentScript = request.getString(pixel + ".contentScript");
						Integer contentType = request.getInteger(pixel + ".contentType");
						String execScript = request.getString(pixel + ".execScript");
						Integer execType = request.getInteger(pixel + ".execType");
						Integer tipType = request.getInteger(pixel + ".tipType");
						String tipScript = request.getString(pixel + ".tipScript");
						Integer widgetParamType = request.getInteger(pixel + ".widgetParamType");
						String widgetParamScript = request.getString(pixel + ".widgetParamScript");
						String style = request.getString(pixel + ".style");

						columnPO.set("name", name);
						columnPO.set("busiName", busiName);
						columnPO.set("widget", widget);
						columnPO.set("contentScript", contentScript);
						columnPO.set("contentType", contentType);
						columnPO.set("style", style);
						columnPO.set("execScript", execScript);
						columnPO.set("execType", execType);
						columnPO.set("tipType", tipType);
						columnPO.set("tipScript", tipScript);
						columnPO.set("widgetParamType", widgetParamType);
						columnPO.set("widgetParamScript", widgetParamScript);

						CmPri pri = ValueConvertUtils.convert(request.getString(pixel + ".pri"), CmPri.class);
						pri.setDevelopmentInfo(columnPO, "表单字段");
						CmPri editPri = ValueConvertUtils.convert(request.getString(pixel + ".editPri"), CmPri.class);
						editPri.setDevelopmentInfo(columnPO, "表单字段", "编辑");
						columnPO.set("pri", pri);
						columnPO.set("editPri", editPri);

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

		// 把set设置回去,更新hibernate二级缓存
		tablePO.set("showColumns", null);
		tablePO.set("formColumns", null);
	}
}
