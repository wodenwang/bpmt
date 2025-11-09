/*
 * $HeadURL: $
 * $Id: $
 * Copyright (c) 2013 by Riversoft System, all rights reserved.
 */
package com.riversoft.module.report.handler;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

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
public class SubsHandler implements Handler {

	@Override
	public void handle(DataPO tablePO) {
		RequestContext request = RequestContext.getCurrent();
		if (!"true".equalsIgnoreCase(request.getString("hasSubs"))) {
			return;
		}

		ORMService.getInstance().removeBath(tablePO.getSubList("viewSubs"));

		String[] subNames = request.getStrings("subs");// 子表
		// 子表部分
		Integer sort = 1;
		Set<Map<String, Object>> viewSubs = new HashSet<>();
		if (subNames != null && subNames.length > 0) {
			for (String pixel : subNames) {
				String action = request.getString(pixel + ".action");
				String subKey = IDGenerator.next();
				String busiName = request.getString(pixel + ".busiName");
				String style = request.getString(pixel + ".style");
				if (StringUtils.isNotEmpty(action)) {// 视图子标签
					DataPO subPO = new DataPO("VwReportSubView");
					subPO.set("subKey", subKey);
					subPO.set("viewKey", tablePO.getString("viewKey"));
					subPO.set("action", action);
					subPO.set("paramType", request.getInteger(pixel + ".paramType"));
					subPO.set("paramScript", request.getString(pixel + ".paramScript"));
					subPO.set("busiName", busiName);
					subPO.set("sort", sort++);
					subPO.set("style", style);
					CmPri pri = ValueConvertUtils.convert(request.getString(pixel + ".pri"), CmPri.class);
					pri.setDevelopmentInfo(subPO, "子表标签");
					subPO.set("pri", pri);
					viewSubs.add(subPO.toEntity());
				}
			}
		}

		ORMService.getInstance().saveOrUpdateBatch(viewSubs);

		// 把set设置回去,更新hibernate二级缓存
		tablePO.set("viewSubs", null);
	}
}
