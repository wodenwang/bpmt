/*
 * $HeadURL: $
 * $Id: $
 * Copyright (c) 2013 by Riversoft System, all rights reserved.
 */
package com.riversoft.module.dyn.handler;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.riversoft.core.IDGenerator;
import com.riversoft.core.context.RequestContext;
import com.riversoft.core.db.DataPO;
import com.riversoft.core.db.ORMService;
import com.riversoft.core.exception.ExceptionType;
import com.riversoft.core.exception.SystemRuntimeException;
import com.riversoft.platform.web.handler.Handler;

/**
 * @author woden
 * 
 */
public class VarsHandler implements Handler {

	@Override
	public void handle(DataPO tablePO) {
		RequestContext request = RequestContext.getCurrent();
		if (!"true".equalsIgnoreCase(request.getString("hasVars"))) {
			return;
		}

		// 展示变量
		{
			Set<Map<String, Object>> execs = new HashSet<>();
			String[] pixels = request.getStrings("prepareExecs");
			Integer sort = 1;
			if (pixels != null) {
				for (String pixel : pixels) {
					Integer execType = request.getInteger(pixel + ".execType");
					String execScript = request.getString(pixel + ".execScript");
					String description = request.getString(pixel + ".description");
					String var = request.getString(pixel + ".var");
					DataPO po = new DataPO("VwDynExecPrepare");
					po.set("viewKey", tablePO.getString("viewKey"));
					po.set("description", description);
					po.set("execType", execType);
					po.set("execScript", execScript);
					po.set("var", var);
					po.set("sort", sort++);
					execs.add(po.toEntity());
				}
			}
			ORMService.getInstance().removeBath(tablePO.getSubList("prepareExecs"));
			ORMService.getInstance().saveBatch(execs);
			// 把set设置回去,更新hibernate二级缓存
			tablePO.set("prepareExecs", execs);
		}

		// 外表变量
		{
			ORMService.getInstance().removeBath(tablePO.getSubList("parents"));
			// 父表部分
			String[] pixels = request.getStrings("parents");// 父表
			Integer sort = 1;
			Set<Map<String, Object>> parents = new HashSet<>();
			if (pixels != null && pixels.length > 0) {
				for (String pixel : pixels) {
					DataPO parentPO = new DataPO("VwDynParent");
					String tableName = request.getString(pixel + ".tableName");// 父表名
					String var = request.getString(pixel + ".var");
					// 验证var,不能是vo
					if ("vo".equalsIgnoreCase(var)) {
						throw new SystemRuntimeException(ExceptionType.BUSINESS, "表[" + tableName + "]不能采用[vo]做为别名.");
					}

					String description = request.getString(pixel + ".description");
					String parentKey = IDGenerator.next();
					parentPO.set("parentKey", parentKey);
					parentPO.set("viewKey", tablePO.getString("viewKey"));
					parentPO.set("description", description);
					parentPO.set("var", var);
					parentPO.set("tableName", tableName);
					parentPO.set("sort", sort++);
					parents.add(parentPO.toEntity());

					// 父表外键
					Set<Map<String, Object>> foreigns = new HashSet<>();
					String[] parentPixels = request.getStrings(pixel + ".foreigns");
					if (parentPixels != null && parentPixels.length > 0) {
						for (String parentPixel : parentPixels) {
							DataPO subForeignPO = new DataPO("VwDynParentForeign");
							String mainColumn = request.getString(parentPixel + ".mainColumn");
							String parentColumn = request.getString(parentPixel + ".parentColumn");
							String parentDescription = request.getString(parentPixel + ".description");
							subForeignPO.set("parentKey", parentKey);
							subForeignPO.set("mainColumn", mainColumn);
							subForeignPO.set("parentColumn", parentColumn);
							subForeignPO.set("description", parentDescription);
							subForeignPO.set("sort", sort++);
							foreigns.add(subForeignPO.toEntity());
						}
					} else {
						throw new SystemRuntimeException(ExceptionType.BUSINESS, "父表[" + tableName + "]没有设置外键.");
					}
					parentPO.set("foreigns", foreigns);
				}
			}
			ORMService.getInstance().saveOrUpdateBatch(parents);
			// 把set设置回去,更新hibernate二级缓存
			tablePO.set("parents", parents);
		}
	}

}
