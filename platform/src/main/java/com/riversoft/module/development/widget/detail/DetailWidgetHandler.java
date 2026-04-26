/*
 * $HeadURL: $
 * $Id: $
 * Copyright (c) 2014 by Riversoft System, all rights reserved.
 */
package com.riversoft.module.development.widget.detail;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

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
 * 明细控件配置
 * 
 * @author woden
 * 
 */
@WidgetConfig(value = "detail", description = "明细控件", sort = 1)
public class DetailWidgetHandler extends BaseWidgetConfigHandler {

	@SuppressWarnings("unchecked")
	@Override
	public void form(HttpServletRequest request, HttpServletResponse response, String key) {
		Map<String, Object> vo = (Map<String, Object>) ORMService.getInstance().findByPk("WdgDetail", key);
		request.setAttribute("vo", vo);
		Actions.includePage(request, response, Util.getPagePath(request, "detail/config.jsp"));
	}

	/**
	 * 增加处理器
	 * 
	 * @param request
	 * @param response
	 */
	public void addExec(HttpServletRequest request, HttpServletResponse response) {
		Actions.includePage(request, response, Util.getPagePath(request, "detail/exec.jsp"));
	}

	/**
	 * 增加批量字段
	 * 
	 * @param request
	 * @param response
	 */
	public void addBatchColumn(HttpServletRequest request, HttpServletResponse response) {
		Actions.includePage(request, response, Util.getPagePath(request, "detail/batch_column.jsp"));
	}

	@SuppressWarnings("unchecked")
	@Override
	public void saveOrUpdate(String key, DataPO po) {
		RequestContext request = RequestContext.getCurrent();
		if (!"true".equals(request.getString("detail.flag"))) {
			return;
		}

		Map<String, Object> vo = (Map<String, Object>) ORMService.getInstance().findByPk("WdgDetail", key);
		if (vo == null) {
			vo = new HashMap<>();
			vo.put("widgetKey", key);
		}
		DataPO dataPO = new DataPO("WdgDetail", vo);
		dataPO.set("pkType", request.getInteger("detail.pkType"));
		dataPO.set("pkScript", request.getString("detail.pkScript"));
		dataPO.set("sumarryType", request.getInteger("detail.sumarryType"));
		dataPO.set("sumarryScript", request.getString("detail.sumarryScript"));
		dataPO.set("allowAdd", request.getInteger("detail.allowAdd"));
		dataPO.set("allowDelete", request.getInteger("detail.allowDelete"));
		dataPO.set("pageFlag", request.getInteger("detail.pageFlag"));
		dataPO.set("batchFlag", request.getInteger("detail.batchFlag"));

		ORMService.getInstance().saveOrUpdate(dataPO.toEntity());

		// 处理器
		handleExec(dataPO);

		// 批量字段
		handleBatchColumn(dataPO);
	}

	/**
	 * 处理器
	 * 
	 * @param tablePO
	 */
	private void handleExec(DataPO tablePO) {
		RequestContext request = RequestContext.getCurrent();

		// exec部分
		Set<Map<String, Object>> execs = new HashSet<>();
		String[] pixels = request.getStrings("detail.exec");
		Integer sort = 1;
		if (pixels != null) {
			for (String pixel : pixels) {
				Integer execType = request.getInteger(pixel + ".execType");
				String execScript = request.getString(pixel + ".execScript");
				String description = request.getString(pixel + ".description");
				DataPO po = new DataPO("WdgDetailExec");
				po.set("widgetKey", tablePO.getString("widgetKey"));
				po.set("description", description);
				po.set("execType", execType);
				po.set("execScript", execScript);
				po.set("sort", sort++);
				execs.add(po.toEntity());
			}
		}
		ORMService.getInstance().removeBath(tablePO.getSubList("execs"));
		ORMService.getInstance().saveBatch(execs);
		// 把set设置回去,更新hibernate二级缓存
		tablePO.set("execs", null);
	}

	/**
	 * 批量字段
	 * 
	 * @param tablePO
	 */
	private void handleBatchColumn(DataPO tablePO) {
		RequestContext request = RequestContext.getCurrent();

		Set<Map<String, Object>> batchColumns = new HashSet<>();
		String[] pixels = request.getStrings("detail.batchColumns");
		Integer sort = 1;
		if (pixels != null) {
			for (String pixel : pixels) {
				String name = request.getString(pixel + ".name");
				String busiName = request.getString(pixel + ".busiName");
				String example = request.getString(pixel + ".example");
				String description = request.getString(pixel + ".description");
				DataPO po = new DataPO("WdgDetailColumnBatch");
				po.set("widgetKey", tablePO.getString("widgetKey"));
				po.set("description", description);
				po.set("name", name);
				po.set("busiName", busiName);
				po.set("example", example);
				po.set("sort", sort++);
				batchColumns.add(po.toEntity());
			}
		}
		ORMService.getInstance().removeBath(tablePO.getSubList("batchColumns"));
		ORMService.getInstance().saveBatch(batchColumns);
		// 把set设置回去,更新hibernate二级缓存
		tablePO.set("batchColumns", null);
	}

	@Override
	public void remove(String key) {
		ORMService.getInstance().removeByPk("WdgDetail", key);
	}

}
