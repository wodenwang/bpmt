/*
 * $HeadURL: $
 * $Id: $
 * Copyright (c) 2015 by Riversoft System, all rights reserved.
 */
package com.riversoft.module.flow.view.handler;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.riversoft.core.context.RequestContext;
import com.riversoft.core.db.DataPO;
import com.riversoft.core.db.ORMService;
import com.riversoft.platform.web.handler.Handler;

/**
 * @author woden
 * 
 */
public class FrameSettingHandler implements Handler {

	@Override
	public void handle(DataPO tablePO) {

		boolean columnSortFlag = handleColumnSort(tablePO);
		boolean listSortFlag = handleListSort(tablePO);

		if (columnSortFlag || listSortFlag) {
			// 把set设置回去,更新hibernate二级缓存
			tablePO.set("formColumns", null);
			tablePO.set("showColumns", null);
			tablePO.set("lineColumns", null);
		}

	}

	private boolean handleColumnSort(DataPO tablePO) {
		RequestContext request = RequestContext.getCurrent();
		if (!"true".equalsIgnoreCase(request.getString("hasColumnSort"))) {
			return false;
		}

		List<HashMap<String, Object>> columns = request.getJsons("sortColumn");
		if (columns != null) {
			for (Map<String, Object> column : columns) {
				ORMService.getInstance().executeHQL(
						"update " + getORMTable((String) column.get("type")) + " set sort = ? where id = ?",
						((Number) column.get("sort")).intValue(), Long.parseLong(column.get("id").toString()));
			}
		}

		return true;
	}

	private boolean handleListSort(DataPO tablePO) {
		RequestContext request = RequestContext.getCurrent();
		if (!"true".equalsIgnoreCase(request.getString("hasListSort"))) {
			return false;
		}

		// 先重置listSort
		ORMService.getInstance().executeHQL("update VwFlowBasicColumnShow set listSort = -1 where viewKey = ?",
				tablePO.getString("viewKey"));

		List<HashMap<String, Object>> columns = request.getJsons("listSortColumn");
		if (columns != null) {
			int sort = 0;
			for (Map<String, Object> column : columns) {
				sort++;
				ORMService.getInstance().executeHQL(
						"update " + getORMTable((String) column.get("type").toString())
								+ " set listSort = ? where id = ?", sort, ((Number) column.get("id")).longValue());
			}
		}

		return true;
	}

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
		case "form":
			return "VwFlowBasicColumnForm";
		case "line":
			return "VwFlowBasicColumnLine";
		}

		return null;
	}

}
