/*
 * $HeadURL: $
 * $Id: $
 * Copyright (c) 2014 by Riversoft System, all rights reserved.
 */
package com.riversoft.module.report;

import com.riversoft.core.context.RequestContext;
import com.riversoft.core.db.DataPO;
import com.riversoft.platform.web.handler.BaseDataBuilder;

/**
 * @author woden
 * 
 */
public class ReportListDataBuilder extends BaseDataBuilder {

	/**
	 * @param dataPO
	 */
	protected ReportListDataBuilder(DataPO dataPO) {
		super(dataPO);
	}

	@Override
	public void build() {
		RequestContext request = RequestContext.getCurrent();
		DataPO tablePO = getDataPO();
		{
			String dbKey = request.getString("config.dbKey");
			String orderBy = request.getString("config.orderBy");
			String dir = request.getString("config.dir");
			Integer col = request.getInteger("config.col");
			Integer initQuery = request.getInteger("config.initQuery");
			String busiName = request.getString("config.busiName");
			Integer summaryFlag = request.getInteger("config.summaryFlag");

			if (tablePO.get("showFlag") == null) {
				tablePO.set("showFlag", 1);
			}
			if (tablePO.get("pageFlag") == null) {
				tablePO.set("pageFlag", 1);
			}
			if (tablePO.get("sort") == null) {
				tablePO.set("sort", 0);
			}

			tablePO.set("dbKey", dbKey);
			tablePO.set("summaryFlag", summaryFlag);
			tablePO.set("orderBy", orderBy);
			tablePO.set("dir", dir);
			tablePO.set("col", col);
			tablePO.set("initQuery", initQuery);
			tablePO.set("busiName", busiName);

			tablePO.set("mainSqlType", request.getInteger("config.mainSqlType"));
			tablePO.set("mainSqlScript", request.getString("config.mainSqlScript"));

			if ("true".equalsIgnoreCase(request.getString("hasBtns"))) {// 配置信息在按钮那里
				tablePO.set("pkType", request.getInteger("config.pkType"));
				tablePO.set("pkScript", request.getString("config.pkScript"));
				tablePO.set("pkSqlType", request.getInteger("config.pkSqlType"));
				tablePO.set("pkSqlScript", request.getString("config.pkSqlScript"));
			}

			// 分页情况
			tablePO.set("pageFlag", request.getInteger("config.pageFlag"));
			tablePO.set("pageLimit", request.getInteger("config.pageLimit"));
		}
	}

}
