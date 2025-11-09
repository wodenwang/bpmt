/*
 * $HeadURL: $
 * $Id: $
 * Copyright (c) 2013 by Riversoft System, all rights reserved.
 */
package com.riversoft.module.development.widget;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.riversoft.core.BeanFactory;
import com.riversoft.core.IDGenerator;
import com.riversoft.core.context.RequestContext;
import com.riversoft.core.db.DataPO;
import com.riversoft.core.db.ORMService;
import com.riversoft.core.exception.ExceptionType;
import com.riversoft.core.exception.SystemRuntimeException;
import com.riversoft.platform.SessionManager;
import com.riversoft.platform.template.DevelopmentOperation;
import com.riversoft.platform.web.handler.BaseDataBuilder;
import com.riversoft.platform.web.widget.BaseWidgetConfigHandler;
import com.riversoft.platform.web.widget.WidgetConfigBuilder;
import com.riversoft.platform.web.widget.WidgetConfigBuilder.ConfigVO;

/**
 * 数据控件配置
 * 
 * @author woden
 * 
 */
public class WidgetConfigService {

	private static class DataBuilder extends BaseDataBuilder {

		/**
		 * @param dataPO
		 */
		protected DataBuilder(DataPO dataPO) {
			super(dataPO);
		}

		@Override
		public void build() {

			DataPO dataPO = getDataPO();
			RequestContext request = RequestContext.getCurrent();
			dataPO.set("busiName", request.getString("busiName"));
			dataPO.set("jsType", request.getInteger("jsType"));
			dataPO.set("jsScript", request.getString("jsScript"));
			dataPO.set("mainSqlType", request.getInteger("mainSqlType"));
			dataPO.set("mainSqlScript", request.getString("mainSqlScript"));
			dataPO.set("orderBy", request.getString("orderBy"));
			dataPO.set("initQuery", request.getInteger("initQuery"));
			dataPO.set("description", request.getString("description"));
			dataPO.set("createUid", SessionManager.getUser().getUid());
			dataPO.set("pageLimit", request.getInteger("pageLimit"));
			dataPO.set("width", request.getInteger("width"));
		}

	}

	/**
	 * 保存和修改
	 * 
	 * @param widgetKey
	 */
	@SuppressWarnings("unchecked")
	@DevelopmentOperation("保存控件")
	public void executeSaveOrUpdateConfig(String widgetKey) {
		// 通用部分
		DataPO dataPO;
		if (StringUtils.isEmpty(widgetKey)) {
			widgetKey = IDGenerator.next();
			dataPO = new DataPO("WdgBase");
			dataPO.set("widgetKey", widgetKey);
		} else {
			Map<String, Object> po = (Map<String, Object>) ORMService.getInstance().findByPk("WdgBase", widgetKey);
			if (po == null) {
				throw new SystemRuntimeException(ExceptionType.BUSINESS, "控件[" + widgetKey + "]不存在.");
			}
			dataPO = new DataPO("WdgBase", po);
		}

		DataBuilder builder = new DataBuilder(dataPO);
		builder.build();
		ORMService.getInstance().saveOrUpdate(dataPO.toEntity());
		builder.handleConfig();

		// 处理扩展
		List<ConfigVO> configs = WidgetConfigBuilder.getInstance().getList();
		for (ConfigVO configVO : configs) {
			BaseWidgetConfigHandler handler = (BaseWidgetConfigHandler) BeanFactory.getInstance().getSingleBean(
					configVO.getClazz());
			handler.saveOrUpdate(widgetKey, dataPO);
		}
	}

	/**
	 * 删除
	 * 
	 * @param widgetKey
	 */
	@DevelopmentOperation("删除控件")
	public void executeRemoveConfig(String widgetKey) {
		// 通用部分
		ORMService.getInstance().removeByPk("WdgBase", widgetKey);

		// 处理扩展
		List<ConfigVO> configs = WidgetConfigBuilder.getInstance().getList();
		for (ConfigVO configVO : configs) {
			BaseWidgetConfigHandler handler = (BaseWidgetConfigHandler) BeanFactory.getInstance().getSingleBean(
					configVO.getClazz());
			handler.remove(widgetKey);
		}
	}
}
