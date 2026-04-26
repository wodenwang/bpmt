/*
 * $HeadURL: $
 * $Id: $
 * Copyright (c) 2013 by Riversoft System, all rights reserved.
 */
package com.riversoft.module.flow.activity.inclusivegateway;

import java.util.Collection;
import java.util.Map;

import com.riversoft.core.db.ORMService;
import com.riversoft.platform.template.DevelopmentOperation;

/**
 * 包容网关节点配置
 * 
 * @author Wilmer
 * 
 */
@DevelopmentOperation("")
public class InclusiveGatewayConfigService {
	@DevelopmentOperation("保存包容网关配置")
	public void executeSaveConfig(Map<String, Object> po, Collection<Map<String, Object>> items) {
		ORMService.getInstance().executeHQL("delete from WfInclusiveGatewayDecide where pdId = ? and activityId = ?",
				po.get("pdId"), po.get("activityId"));
		ORMService.getInstance().saveBatch(items);

		ORMService.getInstance().saveOrUpdate(po);
	}
}
