/*
 * $HeadURL: $
 * $Id: $
 * Copyright (c) 2014 by Riversoft System, all rights reserved.
 */
package com.riversoft.module.dyn;

import com.riversoft.core.context.RequestContext;
import com.riversoft.core.db.DataPO;
import com.riversoft.platform.web.handler.BaseDataBuilder;

/**
 * @author woden
 * 
 */
public class DynDataBuilder extends BaseDataBuilder {

	/**
	 * @param dataPO
	 */
	protected DynDataBuilder(DataPO dataPO) {
		super(dataPO);
	}

	@Override
	public void build() {
		RequestContext request = RequestContext.getCurrent();
		DataPO tablePO = getDataPO();
		{
			String tableName = request.getString("table.tableName");
			String busiName = request.getString("table.busiName");
			String logTableName = request.getString("table.logTable");
			String sortName = request.getString("table.sortName");
			String dir = request.getString("table.dir");
			Integer col = request.getInteger("table.col");
			Integer initQuery = request.getInteger("table.initQuery");
			Integer pageLimit = request.getInteger("table.pageLimit");

			tablePO.set("name", tableName);
			tablePO.set("busiName", busiName);
			tablePO.set("logTable", logTableName);
			tablePO.set("sortName", sortName);
			tablePO.set("dir", dir);
			tablePO.set("col", col);
			tablePO.set("initQuery", initQuery);
			tablePO.set("pageLimit", pageLimit);
		}
	}

}
