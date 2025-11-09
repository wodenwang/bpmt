/*
 * $HeadURL: $
 * $Id: $
 * Copyright (c) 2013 by Riversoft System, all rights reserved.
 */
package com.riversoft.module.dyn.handler;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

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
public class LimitsHandler implements Handler {

	@Override
	public void handle(DataPO tablePO) {
		RequestContext request = RequestContext.getCurrent();
		if (!"true".equalsIgnoreCase(request.getString("hasLimits"))) {
			return;
		}

		ORMService.getInstance().removeBath(tablePO.getSubList("limits"));

		// limit部分
		Set<Map<String, Object>> limits = new HashSet<>();
		String[] pixels = request.getStrings("limits");
		Integer sort = 1;
		if (pixels != null) {
			for (String pixel : pixels) {
				Integer sqlType = request.getInteger(pixel + ".sqlType");
				String sqlScript = request.getString(pixel + ".sqlScript");
				String description = request.getString(pixel + ".description");

				DataPO limitPO = new DataPO("VwDynLimit");
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
			tablePO.set("limits", limits);
		}
		ORMService.getInstance().saveBatch(limits);
		// 把set设置回去,更新hibernate二级缓存
		tablePO.set("limits", limits);
	}

}
