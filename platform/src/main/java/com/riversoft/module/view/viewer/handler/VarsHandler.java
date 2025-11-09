/*
 * $HeadURL: $
 * $Id: $
 * Copyright (c) 2013 by Riversoft System, all rights reserved.
 */
package com.riversoft.module.view.viewer.handler;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.riversoft.core.context.RequestContext;
import com.riversoft.core.db.DataPO;
import com.riversoft.core.db.ORMService;
import com.riversoft.platform.web.handler.Handler;

/**
 * @author woden
 * 
 */
public class VarsHandler implements Handler {

	@Override
	public void handle(DataPO tablePO) {
		RequestContext request = RequestContext.getCurrent();
		if (!"true".equalsIgnoreCase(request.getString("hasVars"))) {
			return;
		}

		// 展示变量
		{
			Set<Map<String, Object>> execs = new HashSet<>();
			String[] pixels = request.getStrings("vars");
			Integer sort = 1;
			if (pixels != null) {
				for (String pixel : pixels) {
					Integer execType = request.getInteger(pixel + ".execType");
					String execScript = request.getString(pixel + ".execScript");
					String description = request.getString(pixel + ".description");
					String var = request.getString(pixel + ".var");
					DataPO po = new DataPO("VwViewerVar");
					po.set("viewKey", tablePO.getString("viewKey"));
					po.set("description", description);
					po.set("execType", execType);
					po.set("execScript", execScript);
					po.set("var", var);
					po.set("sort", sort++);
					execs.add(po.toEntity());
				}
			}
			ORMService.getInstance().removeBath(tablePO.getSubList("vars"));
			ORMService.getInstance().saveBatch(execs);
			// 把set设置回去,更新hibernate二级缓存
			tablePO.set("vars", execs);
		}
	}

}
