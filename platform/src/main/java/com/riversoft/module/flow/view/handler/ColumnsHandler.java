/*
 * $HeadURL: $
 * $Id: $
 * Copyright (c) 2013 by Riversoft System, all rights reserved.
 */
package com.riversoft.module.flow.view.handler;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.riversoft.core.IDGenerator;
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
			return "VwFlowBasicColumnShow";
		case "line":
			return "VwFlowBasicColumnLine";
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
				ORMService.getInstance().removeByPk(getORMTable(deleteColumn.get("type").toString()), ((Number) deleteColumn.get("id")).longValue());
			}
		}

		// 处理更新或新增
		if (waitColumns != null) {
			for (HashMap<String, Object> waitColumn : waitColumns) {
				String pixel = waitColumn.get("pixel").toString();
				Number sort = (Number) waitColumn.get("sort");
				Number listSort = waitColumn.containsKey("listSort") ? (Number) waitColumn.get("listSort") : -1;
				Number id;
				if (waitColumn.containsKey("id")) {
					id = (Number) waitColumn.get("id");
				} else {
					id = null;
				}

				String ormTableName = getORMTable(waitColumn.get("type").toString());
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
					switch (waitColumn.get("type").toString()) {
					case "show": // 展示
					{
						String busiName = request.getString(pixel + ".busiName");
						String contentScript = request.getString(pixel + ".contentScript");
						Integer contentType = request.getInteger(pixel + ".contentType");
						String style = request.getString(pixel + ".style");
						Integer whole = request.getInteger(pixel + ".whole");
						String pixelKey = request.getString(pixel + ".pixelKey");
						String sortField = request.getString(pixel + ".sortField");
						if (StringUtils.isEmpty(pixelKey)) {
							pixelKey = IDGenerator.uuid();
						}
						columnPO.set("busiName", busiName);
						columnPO.set("contentScript", contentScript);
						columnPO.set("contentType", contentType);
						columnPO.set("style", style);
						columnPO.set("whole", whole);
						columnPO.set("pixelKey", pixelKey);
						columnPO.set("sortField", sortField);
						CmPri pri = ValueConvertUtils.convert(request.getString(pixel + ".pri"), CmPri.class);
						pri.setDevelopmentInfo(columnPO, "展示字段");
						columnPO.set("pri", pri);
					}
						break;
					case "line": // 分割线
					{
						String busiName = request.getString(pixel + ".busiName");
						String pixelKey = request.getString(pixel + ".pixelKey");
						Integer tipType = request.getInteger(pixel + ".tipType");
						String tipScript = request.getString(pixel + ".tipScript");
						if (StringUtils.isEmpty(pixelKey)) {
							pixelKey = IDGenerator.uuid();
						}
						columnPO.set("busiName", busiName);
						columnPO.set("pixelKey", pixelKey);
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
