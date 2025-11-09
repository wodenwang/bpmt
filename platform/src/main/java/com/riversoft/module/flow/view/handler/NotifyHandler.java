/*
 * $HeadURL: $
 * $Id: $
 * Copyright (c) 2013 by Riversoft System, all rights reserved.
 */
package com.riversoft.module.flow.view.handler;

import org.apache.commons.lang3.StringUtils;

import com.riversoft.core.context.RequestContext;
import com.riversoft.core.db.DataPO;
import com.riversoft.platform.web.handler.Handler;

/**
 * @author woden
 * 
 */
public class NotifyHandler implements Handler {

	@Override
	public void handle(DataPO tablePO) {
		RequestContext request = RequestContext.getCurrent();

		if (!"true".equalsIgnoreCase(request.getString("hasNotify"))) {
			return;
		}

		tablePO.set("msgType", StringUtils.join(request.getStrings("notify.msgType"), ";"));
		tablePO.set("mailSubjectType", request.getInteger("notify.mailSubjectType"));
		tablePO.set("mailSubjectScript", request.getString("notify.mailSubjectScript"));
		tablePO.set("mailContentType", request.getInteger("notify.mailContentType"));
		tablePO.set("mailContentScript", request.getString("notify.mailContentScript"));

	}

}
