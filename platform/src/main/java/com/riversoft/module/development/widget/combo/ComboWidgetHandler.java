/*
 * $HeadURL: $
 * $Id: $
 * Copyright (c) 2014 by Riversoft System, all rights reserved.
 */
package com.riversoft.module.development.widget.combo;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.riversoft.core.context.RequestContext;
import com.riversoft.core.db.DataPO;
import com.riversoft.core.db.ORMService;
import com.riversoft.core.web.Actions;
import com.riversoft.core.web.Actions.Util;
import com.riversoft.platform.web.widget.BaseWidgetConfigHandler;
import com.riversoft.platform.web.widget.WidgetConfig;

/**
 * @author woden
 * 
 */
@WidgetConfig(value = "combo", description = "选择控件", sort = 2)
public class ComboWidgetHandler extends BaseWidgetConfigHandler {

	@Override
	public void form(HttpServletRequest request, HttpServletResponse response, String key) {
		Map<String, Object> vo = (Map<String, Object>) ORMService.getInstance().findByPk("WdgCombo", key);
		request.setAttribute("vo", vo);
		Actions.includePage(request, response, Util.getPagePath(request, "combo/config.jsp"));
	}

	@SuppressWarnings("unchecked")
	@Override
	public void saveOrUpdate(String key, DataPO po) {
		RequestContext request = RequestContext.getCurrent();
		if (!"true".equals(request.getString("combo.flag"))) {
			return;
		}

		Map<String, Object> vo = (Map<String, Object>) ORMService.getInstance().findByPk("WdgCombo", key);
		if (vo == null) {
			vo = new HashMap<>();
			vo.put("widgetKey", key);
		}
		DataPO dataPO = new DataPO("WdgCombo", vo);

		dataPO.set("codeType", request.getInteger("combo.codeType"));
		dataPO.set("codeScript", request.getString("combo.codeScript"));
		dataPO.set("nameType", request.getInteger("combo.nameType"));
		dataPO.set("nameScript", request.getString("combo.nameScript"));
		dataPO.set("pkSqlType", request.getInteger("combo.pkSqlType"));
		dataPO.set("pkSqlScript", request.getString("combo.pkSqlScript"));

		ORMService.getInstance().saveOrUpdate(dataPO.toEntity());
	}

	@Override
	public void remove(String key) {
		ORMService.getInstance().removeByPk("WdgCombo", key);
	}

}
