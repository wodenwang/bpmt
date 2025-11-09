/*
 * $HeadURL: $
 * $Id: $
 * Copyright (c) 2014 by Riversoft System, all rights reserved.
 */
package com.riversoft.module.report.handler;

import com.riversoft.core.context.RequestContext;
import com.riversoft.core.db.DataPO;
import com.riversoft.platform.web.handler.Handler;

/**
 * @author woden
 * 
 */
public class JsHandler implements Handler {

    @Override
    public void handle(DataPO tablePO) {
        RequestContext request = RequestContext.getCurrent();
        if (!"true".equalsIgnoreCase(request.getString("hasJs"))) {
            return;
        }

        tablePO.set("listJsType", request.getInteger("js.listJsType"));
        tablePO.set("listJsScript", request.getString("js.listJsScript"));
    }

}
