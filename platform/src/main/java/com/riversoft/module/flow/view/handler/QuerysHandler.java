/*
 * $HeadURL: $
 * $Id: $
 * Copyright (c) 2013 by Riversoft System, all rights reserved.
 */
package com.riversoft.module.flow.view.handler;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

import com.riversoft.core.context.RequestContext;
import com.riversoft.core.db.DataPO;
import com.riversoft.core.db.ORMService;
import com.riversoft.platform.web.handler.Handler;

/**
 * @author woden
 * 
 */
public class QuerysHandler implements Handler {

	@Override
	public void handle(DataPO tablePO) {
		RequestContext request = RequestContext.getCurrent();

		if (!"true".equalsIgnoreCase(request.getString("hasQuerys"))) {
			return;
		}

		ORMService.getInstance().removeBath(tablePO.getSubList("querys"));
		ORMService.getInstance().removeBath(tablePO.getSubList("extQuerys"));
		{
			// query部分
			Set<Map<String, Object>> querys = new HashSet<>();
			Set<Map<String, Object>> extQuerys = new HashSet<>();
			String[] pixels = request.getStrings("querys");
			Integer sort = 1;
			if (pixels != null) {
				for (String pixel : pixels) {

					String name = request.getString(pixel + ".name");
					if (StringUtils.isNotEmpty(name)) {
						String busiName = request.getString(pixel + ".busiName");
						String widget = request.getString(pixel + ".widget");
						String defVal = request.getString(pixel + ".defVal");
						DataPO queryPO = new DataPO("VwFlowBasicQuery");
						queryPO.set("viewKey", tablePO.getString("viewKey"));
						queryPO.set("name", name);
						queryPO.set("busiName", busiName);
						queryPO.set("widget", widget);
						queryPO.set("defVal", defVal);
						queryPO.set("widgetParamType", request.getInteger(pixel + ".widgetParamType"));
						queryPO.set("widgetParamScript", request.getString(pixel + ".widgetParamScript"));
						queryPO.set("sort", sort++);
						querys.add(queryPO.toEntity());
					} else {
						String busiName = request.getString(pixel + ".busiName");
						String widget = request.getString(pixel + ".widget");
						String defVal = request.getString(pixel + ".defVal");
						Integer sqlType = request.getInteger(pixel + ".sqlType");
						String sqlScript = request.getString(pixel + ".sqlScript");
						String description = request.getString(pixel + ".description");
						DataPO queryPO = new DataPO("VwFlowBasicQueryExt");
						queryPO.set("viewKey", tablePO.getString("viewKey"));
						queryPO.set("busiName", busiName);
						queryPO.set("widget", widget);
						queryPO.set("defVal", defVal);
						queryPO.set("sqlType", sqlType);
						queryPO.set("sqlScript", sqlScript);
						queryPO.set("description", description);
						queryPO.set("widgetParamType", request.getInteger(pixel + ".widgetParamType"));
						queryPO.set("widgetParamScript", request.getString(pixel + ".widgetParamScript"));
						queryPO.set("sort", sort++);
						extQuerys.add(queryPO.toEntity());
					}
				}
			}
			ORMService.getInstance().saveBatch(querys);
			ORMService.getInstance().saveBatch(extQuerys);

			// 把set设置回去,更新hibernate二级缓存
			tablePO.set("querys", querys);
			tablePO.set("extQuerys", extQuerys);
		}
	}

}
