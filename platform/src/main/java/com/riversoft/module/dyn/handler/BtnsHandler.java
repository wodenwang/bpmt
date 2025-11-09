/*
 * $HeadURL: $
 * $Id: $
 * Copyright (c) 2013 by Riversoft System, all rights reserved.
 */
package com.riversoft.module.dyn.handler;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

import com.riversoft.core.context.RequestContext;
import com.riversoft.core.db.DataPO;
import com.riversoft.core.db.ORMService;
import com.riversoft.core.exception.ExceptionType;
import com.riversoft.core.exception.SystemRuntimeException;
import com.riversoft.platform.po.CmPri;
import com.riversoft.platform.web.handler.Handler;
import com.riversoft.util.ValueConvertUtils;

/**
 * @author woden
 * 
 */
public class BtnsHandler implements Handler {

	@Override
	public void handle(DataPO tablePO) {
		RequestContext request = RequestContext.getCurrent();
		if (!"true".equalsIgnoreCase(request.getString("hasBtns"))) {
			if (tablePO.getSubList("sysBtns") == null || tablePO.getSubList("sysBtns").size() < 1) {
				throw new SystemRuntimeException(ExceptionType.BUSINESS, "请先进行[按钮]设置后再提交.");
			}
			return;
		}

		// 删除
		ORMService.getInstance().removeBath(tablePO.getSubList("itemBtns"));
		ORMService.getInstance().removeBath(tablePO.getSubList("summaryBtns"));
		ORMService.getInstance().removeBath(tablePO.getSubList("sysBtns"));

		// btn按钮
		Set<Map<String, Object>> sysBtns = new HashSet<>();
		Set<Map<String, Object>> itemBtns = new HashSet<>();
		Set<Map<String, Object>> summaryBtns = new HashSet<>();
		String[] itemPixels = request.getStrings("itemBtns");
		String[] summaryPixels = request.getStrings("summaryBtns");
		Integer sort = 1;
		if (itemPixels != null) {
			for (String pixel : itemPixels) {
				String name = request.getString(pixel + ".name");// sysBtn特有
				String busiName = request.getString(pixel + ".busiName");
				String icon = request.getString(pixel + ".icon");
				String description = request.getString(pixel + ".description");

				if (StringUtils.isNotEmpty(name)) {// 系统按钮
					String styleClass = request.getString(pixel + ".styleClass");
					DataPO btnPO = new DataPO("VwDynBtnSys");
					btnPO.set("viewKey", tablePO.getString("viewKey"));
					btnPO.set("type", 1);// 明细按钮type为1
					btnPO.set("busiName", busiName);
					btnPO.set("icon", icon);
					btnPO.set("styleClass", styleClass);
					btnPO.set("name", name);
					btnPO.set("description", description);
					btnPO.set("sort", sort++);
					CmPri pri = ValueConvertUtils.convert(request.getString(pixel + ".pri"), CmPri.class);
					pri.setDevelopmentInfo(btnPO, "明细按钮");
					btnPO.set("pri", pri);
					sysBtns.add(btnPO.toEntity());
				} else {
					String action = request.getString(pixel + ".action");
					Integer paramType = request.getInteger(pixel + ".paramType");
					String paramScript = request.getString(pixel + ".paramScript");
					String confirmMsg = request.getString(pixel + ".confirmMsg");
					Integer openType = request.getInteger(pixel + ".openType");
					DataPO btnPO = new DataPO("VwDynBtnItem");
					btnPO.set("viewKey", tablePO.getString("viewKey"));
					btnPO.set("busiName", busiName);
					btnPO.set("icon", icon);
					btnPO.set("action", action);
					btnPO.set("description", description);
					btnPO.set("openType", openType);
					btnPO.set("sort", sort++);
					btnPO.set("paramType", paramType);
					btnPO.set("paramScript", paramScript);
					btnPO.set("confirmMsg", confirmMsg);
					CmPri pri = ValueConvertUtils.convert(request.getString(pixel + ".pri"), CmPri.class);
					pri.setDevelopmentInfo(btnPO, "明细按钮");
					btnPO.set("pri", pri);
					itemBtns.add(btnPO.toEntity());
				}
			}
		}

		if (summaryPixels != null) {
			for (String pixel : summaryPixels) {
				String name = request.getString(pixel + ".name");// sysBtn特有
				String busiName = request.getString(pixel + ".busiName");
				String icon = request.getString(pixel + ".icon");
				String description = request.getString(pixel + ".description");
				String styleClass = request.getString(pixel + ".styleClass");

				if (StringUtils.isNotEmpty(name)) {// 系统按钮
					DataPO btnPO = new DataPO("VwDynBtnSys");
					btnPO.set("viewKey", tablePO.getString("viewKey"));
					btnPO.set("type", 2);// 汇总按钮type为2
					btnPO.set("busiName", busiName);
					btnPO.set("icon", icon);
					btnPO.set("styleClass", styleClass);
					btnPO.set("name", name);
					btnPO.set("description", description);
					btnPO.set("sort", sort++);
					CmPri pri = ValueConvertUtils.convert(request.getString(pixel + ".pri"), CmPri.class);
					pri.setDevelopmentInfo(btnPO, "汇总按钮");
					btnPO.set("pri", pri);
					sysBtns.add(btnPO.toEntity());
				} else {
					String action = request.getString(pixel + ".action");
					Integer paramType = request.getInteger(pixel + ".paramType");
					String paramScript = request.getString(pixel + ".paramScript");
					String confirmMsg = request.getString(pixel + ".confirmMsg");
					Integer openType = request.getInteger(pixel + ".openType");
					DataPO btnPO = new DataPO("VwDynBtnSummary");
					btnPO.set("viewKey", tablePO.getString("viewKey"));
					btnPO.set("busiName", busiName);
					btnPO.set("icon", icon);
					btnPO.set("styleClass", styleClass);
					btnPO.set("description", description);
					btnPO.set("action", action);
					btnPO.set("openType", openType);
					btnPO.set("sort", sort++);
					btnPO.set("paramType", paramType);
					btnPO.set("paramScript", paramScript);
					btnPO.set("confirmMsg", confirmMsg);
					CmPri pri = ValueConvertUtils.convert(request.getString(pixel + ".pri"), CmPri.class);
					pri.setDevelopmentInfo(btnPO, "汇总按钮");
					btnPO.set("pri", pri);
					summaryBtns.add(btnPO.toEntity());
				}
			}
		}

		// 添加
		ORMService.getInstance().saveBatch(summaryBtns);
		ORMService.getInstance().saveBatch(itemBtns);
		ORMService.getInstance().saveBatch(sysBtns);

		// 把set设置回去,更新hibernate二级缓存
		tablePO.set("summaryBtns", summaryBtns);
		tablePO.set("itemBtns", itemBtns);
		tablePO.set("sysBtns", sysBtns);
	}

}
