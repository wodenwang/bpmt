/*
 * $HeadURL: $
 * $Id: $
 * Copyright (c) 2014 by Riversoft System, all rights reserved.
 */
package com.riversoft.module.flow.view;

import com.riversoft.core.context.RequestContext;
import com.riversoft.core.db.DataPO;
import com.riversoft.platform.web.handler.BaseDataBuilder;

/**
 * @author woden
 * 
 */
public class FlowBasicDataBuilder extends BaseDataBuilder {

	/**
	 * @param dataPO
	 */
	protected FlowBasicDataBuilder(DataPO dataPO) {
		super(dataPO);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.riversoft.platform.web.view.handler.BaseDataBuilder#build()
	 */
	@Override
	public void build() {
		RequestContext request = RequestContext.getCurrent();
		DataPO tablePO = getDataPO();
		String tableName = request.getString("table.tableName");
		String pdKey = request.getString("table.pdKey");
		String historyTableName = request.getString("table.historyTableName");
		String opinionTableName = request.getString("table.opinionTableName");
		String busiName = request.getString("table.busiName");
		String groups = request.getString("table.groups");
		Integer col = request.getInteger("table.col");
		String ordIdScript = request.getString("table.ordIdScript");
		Integer ordIdType = request.getInteger("table.ordIdType");
		String sortName = request.getString("table.sortName");
		String dir = request.getString("table.dir");
		Integer pageLimit = request.getInteger("table.pageLimit");
		Integer initQuery = request.getInteger("table.initQuery");

		tablePO.set("tableName", tableName);
		tablePO.set("pdKey", pdKey);
		tablePO.set("historyTableName", historyTableName);
		tablePO.set("opinionTableName", opinionTableName);
		tablePO.set("busiName", busiName);
		tablePO.set("groups", groups);
		tablePO.set("col", col);
		tablePO.set("ordIdType", ordIdType);
		tablePO.set("sortName", sortName);
		tablePO.set("dir", dir);
		tablePO.set("ordIdScript", ordIdScript);
		tablePO.set("pageLimit", pageLimit);
		tablePO.set("initQuery", initQuery);

	}

}
