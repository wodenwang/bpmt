/*
 * $HeadURL: $
 * $Id: $
 * Copyright (c) 2013 by Riversoft System, all rights reserved.
 */
package com.riversoft.module.development.widget;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

import com.riversoft.core.IDGenerator;
import com.riversoft.core.context.RequestContext;
import com.riversoft.core.db.DataPO;
import com.riversoft.core.db.ORMService;
import com.riversoft.core.exception.ExceptionType;
import com.riversoft.core.exception.SystemRuntimeException;
import com.riversoft.platform.SessionManager;
import com.riversoft.platform.template.DevelopmentOperation;
import com.riversoft.platform.web.FileManager;
import com.riversoft.platform.web.FileManager.UploadFile;



/**
 * 模板控件配置
 * 
 * @author Chris
 *
 */
public class TemplateWidgetService {

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
			dataPO = new DataPO("WdgTemplate");
			dataPO.set("widgetKey", widgetKey);
		} else {
			Map<String, Object> po = (Map<String, Object>) ORMService.getInstance().findByPk("WdgTemplate", widgetKey);
			if (po == null) {
				throw new SystemRuntimeException(ExceptionType.BUSINESS, "控件[" + widgetKey + "]不存在.");
			}
			dataPO = new DataPO("WdgTemplate", po);
		}
		
		//更新WdgTemplate
		RequestContext request = RequestContext.getCurrent();
		dataPO.set("busiName", request.getString("busiName"));
		dataPO.set("description", request.getString("description"));
		dataPO.set("createUid", SessionManager.getUser().getUid());
		List<UploadFile> templateFile = FileManager.getUploadFiles(request, "templateFile");
        if (templateFile != null && !templateFile.isEmpty()) {
			dataPO.set("templateFile", FileManager.toBytes(null, templateFile));
		}
        ORMService.getInstance().saveOrUpdate(dataPO.toEntity());
        
        //更新WdgTemplateVar
        if (!"true".equalsIgnoreCase(request.getString("hasVars"))) {
			return;
		} else {
			
			Set<Map<String, Object>> execs = new HashSet<>();
			String[] pixels = request.getStrings("vars");
			Integer sort = 1;
			if (pixels != null) {
				for (String pixel : pixels) {
					Integer execType = request.getInteger(pixel + ".execType");
					String execScript = request.getString(pixel + ".execScript");
					String description = request.getString(pixel + ".description");
					String var = request.getString(pixel + ".var");
					DataPO po = new DataPO("WdgTemplateVar");
					po.set("widgetKey", dataPO.getString("widgetKey"));
					po.set("description", description);
					po.set("execType", execType);
					po.set("execScript", execScript);
					po.set("var", var);
					po.set("sort", sort++);
					execs.add(po.toEntity());
				}
			}
			ORMService.getInstance().removeBath(dataPO.getSubList("vars"));
			ORMService.getInstance().saveBatch(execs);
			// 把set设置回去,更新hibernate二级缓存
			dataPO.set("vars", execs);
		
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
		ORMService.getInstance().removeByPk("WdgTemplate", widgetKey);
	}
}
