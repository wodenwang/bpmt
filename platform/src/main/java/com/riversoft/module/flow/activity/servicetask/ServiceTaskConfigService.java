/*
 * $HeadURL: $
 * $Id: $
 * Copyright (c) 2013 by Riversoft System, all rights reserved.
 */
package com.riversoft.module.flow.activity.servicetask;

import java.util.List;
import java.util.Map;

import com.riversoft.core.db.ORMService;
import com.riversoft.platform.template.DevelopmentOperation;

/**
 * @author woden
 * 
 */
public class ServiceTaskConfigService {

	/**
	 * 保存配置
	 * 
	 * @param po
	 * @param logics
	 */
	@DevelopmentOperation("保存服务节点配置")
	public void executeSaveConfig(Map<String, Object> po, List<Map<String, Object>> logics) {
		ORMService.getInstance().executeHQL("delete from WfServiceTaskLogic where pdId = ? and activityId = ?",
				po.get("pdId"), po.get("activityId"));
		ORMService.getInstance().saveBatch(logics);
		ORMService.getInstance().saveOrUpdate(po);
	}
}
