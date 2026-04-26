/*
 * $HeadURL: $
 * $Id: $
 * Copyright (c) 2013 by Riversoft System, all rights reserved.
 */
package com.riversoft.module.report.handler;

import java.util.Map;

import com.riversoft.core.context.RequestContext;
import com.riversoft.core.db.DataPO;
import com.riversoft.core.db.ORMService;
import com.riversoft.platform.po.CmPri;
import com.riversoft.platform.web.handler.Handler;
import com.riversoft.util.ValueConvertUtils;

/**
 * @author chris
 * 
 */
public class WeixinHandler implements Handler {

	@Override
	public void handle(DataPO tablePO) {
		RequestContext request = RequestContext.getCurrent();
		if (!"true".equalsIgnoreCase(request.getString("hasWeixin"))) {
			return;
		}

		String viewKey = tablePO.getString("viewKey");
		DataPO weixinPO;
		Map<String, Object> po = (Map<String, Object>) ORMService.getInstance().findByPk("VwReportWeixin", viewKey);
		if (po != null) {
			weixinPO = new DataPO("VwReportWeixin", po);
		} else {
			weixinPO = new DataPO("VwReportWeixin");
			weixinPO.set("viewKey", viewKey);
		}

		weixinPO.set("listMode", request.getInteger("weixin.listMode"));
		weixinPO.set("urlMode", request.getInteger("weixin.urlMode"));
		weixinPO.set("titleType", request.getInteger("weixin.titleType"));
		weixinPO.set("titleScript", request.getString("weixin.titleScript"));
		weixinPO.set("imgType", request.getInteger("weixin.imgType"));
		weixinPO.set("imgScript", request.getString("weixin.imgScript"));
		weixinPO.set("desType", request.getInteger("weixin.desType"));
		weixinPO.set("desScript", request.getString("weixin.desScript"));
		weixinPO.set("dateType", request.getInteger("weixin.dateType"));
		weixinPO.set("dateScript", request.getString("weixin.dateScript"));
		CmPri pri = ValueConvertUtils.convert(request.getString("weixin.pri"), CmPri.class);
		pri.setDevelopmentInfo(weixinPO, "微信");
		pri.setBusiName("微信");
		weixinPO.set("pri", pri);

		ORMService.getInstance().merge(weixinPO.toEntity());
		tablePO.set("weixin", null);
	}

}
