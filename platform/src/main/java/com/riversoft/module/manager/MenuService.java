/*
 * $HeadURL: $
 * $Id: $
 * Copyright (c) 2013 by Riversoft System, all rights reserved.
 */
package com.riversoft.module.manager;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.riversoft.core.db.ORMService;
import com.riversoft.platform.po.CmPri;
import com.riversoft.platform.template.DevelopmentOperation;

/**
 * 域处理
 * 
 * @author Woden
 * 
 */
public class MenuService {

	@DevelopmentOperation("删除域")
	public void executeRemoveDomain(String domainKey) {
		ORMService.getInstance().executeHQL("delete from CmHome where domainKey = ?", domainKey);
		ORMService.getInstance().removeByPk("CmDomain", domainKey);
	}

	@DevelopmentOperation("新增菜单")
	public void save(Map<String, Object> po) {
		CmPri pri = (CmPri) po.get("pri");
		if (pri != null) {
			pri.setDevelopmentInfo(po);
		}
		ORMService.getInstance().save(po);
	}

	@DevelopmentOperation("保存菜单")
	public void update(Map<String, Object> po) {
		CmPri pri = (CmPri) po.get("pri");
		if (pri != null) {
			pri.setDevelopmentInfo(po);
		}
		ORMService.getInstance().update(po);
	}

	/**
	 * 保存排序
	 * 
	 * @param array
	 */
	@DevelopmentOperation("保存菜单排序")
	public void executeSaveSort(List<HashMap<String, Object>> array) {
		Iterator it = array.iterator();
		while (it.hasNext()) {
			HashMap<String, Object> obj = (HashMap<String, Object>) it.next();
			int type = (Integer) obj.get("type");
			if (type == 1) {// 域
				Map<String, Object> domain = (Map<String, Object>) ORMService.getInstance().findByPk("CmDomain",
						obj.get("domainKey").toString());
				if (domain == null) {
					continue;
				}
				domain.put("sort", obj.get("sort"));
				ORMService.getInstance().update(domain);
			} else {// 菜单
				Map<String, Object> menu = (Map<String, Object>) ORMService.getInstance().findByPk("CmMenu",
						obj.get("id").toString());
				if (menu == null) {
					continue;
				}
				menu.put("sort", obj.get("sort"));
				menu.put("domainKey", obj.get("domainKey"));
				String parentId = (String) obj.get("parentId");
				if ("null".equalsIgnoreCase(parentId)) {
					parentId = null;
				}
				menu.put("parentId", parentId);
				ORMService.getInstance().update(menu);
			}

		}
	}

	/**
	 * 保存首页标签
	 * 
	 * @param domainKey
	 * @param columns
	 * @param batchList
	 */
	@DevelopmentOperation("保存首页标签")
	public void executeSaveHome(String domainKey, String columns, List<Map<String, Object>> batchList) {
		ORMService.getInstance().executeHQL("update CmDomain set columns = ? where domainKey = ?", columns, domainKey);
		ORMService.getInstance().executeHQL("delete from CmHome where domainKey = ? and sysFlag = 0", domainKey);
		ORMService.getInstance().saveOrUpdateBatch(batchList);
	}
}
