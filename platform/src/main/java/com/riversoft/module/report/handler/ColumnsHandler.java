/*
 * $HeadURL: $
 * $Id: $
 * Copyright (c) 2013 by Riversoft System, all rights reserved.
 */
package com.riversoft.module.report.handler;

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
			return "VwReportColumnShow";
		case "line":
			return "VwReportColumnLine";
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

		List<HashMap<String, Object>> deleteColumns = request.getJsons("column.deleteColumn");
		List<HashMap<String, Object>> waitColumns = request.getJsons("column.waitColumn");

		// 删除
		if (deleteColumns != null) {
			for (HashMap<String, Object> deleteColumn : deleteColumns) {
				ORMService.getInstance().removeByPk(getORMTable(deleteColumn.get("type").toString()), ((Number) deleteColumn.get("id")).longValue());
			}
		}

		// 处理更新或新增
		if (waitColumns != null) {
			for (HashMap<String, Object> waitColumn : waitColumns) {
				String pixel = waitColumn.get("pixel").toString();
				Number sort = (Number) waitColumn.get("sort");
				Number listSort = (Number) waitColumn.get("listSort");
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
					columnPO = new DataPO(ormTableName, (Map<String, Object>) ORMService.getInstance().findByPk(ormTableName, id.longValue()));
				} else {
					isCreate = true;
					columnPO = new DataPO(ormTableName);
					columnPO.set("viewKey", tablePO.getString("viewKey"));
				}
				columnPO.set("sort", sort);
				columnPO.set("listSort", listSort);

				// 是否有编辑
				if ("true".equalsIgnoreCase(request.getString(pixel + ".flag"))) {
					switch (type) {
					case "show": // 展示
					{
						String busiName = request.getString(pixel + ".busiName");
						String contentScript = request.getString(pixel + ".contentScript");
						Integer contentType = request.getInteger(pixel + ".contentType");
						String summaryContentScript = request.getString(pixel + ".summaryContentScript");
						Integer summaryContentType = request.getInteger(pixel + ".summaryContentType");
						String style = request.getString(pixel + ".style");
						Integer whole = request.getInteger(pixel + ".whole");
						String sortField = request.getString(pixel + ".sortField");
						columnPO.set("busiName", busiName);
						columnPO.set("contentScript", contentScript);
						columnPO.set("contentType", contentType);
						columnPO.set("summaryContentScript", summaryContentScript);
						columnPO.set("summaryContentType", summaryContentType);
						columnPO.set("style", style);
						columnPO.set("whole", whole);
						columnPO.set("sortField", sortField);
						CmPri pri = ValueConvertUtils.convert(request.getString(pixel + ".pri"), CmPri.class);
						pri.setDevelopmentInfo(columnPO, "展示字段");
						columnPO.set("pri", pri);
					}
						break;
					case "line": // 分割线
					{
						String busiName = request.getString(pixel + ".busiName");
						Integer tipType = request.getInteger(pixel + ".tipType");
						String tipScript = request.getString(pixel + ".tipScript");
						columnPO.set("busiName", busiName);
						columnPO.set("tipType", tipType);
						columnPO.set("tipScript", tipScript);
						columnPO.set("expandFlag", request.getInteger(pixel + ".expandFlag"));
						CmPri pri = ValueConvertUtils.convert(request.getString(pixel + ".pri"), CmPri.class);
						pri.setDevelopmentInfo(columnPO, "分割线");
						columnPO.set("pri", pri);
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
		tablePO.set("lineColumns", null);
	}
}
