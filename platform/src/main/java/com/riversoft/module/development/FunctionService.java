/*
 * $HeadURL: $
 * $Id: $
 * Copyright (c) 2013 by Riversoft System, all rights reserved.
 */
package com.riversoft.module.development;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.riversoft.core.db.ORMService;
import com.riversoft.platform.template.DevelopmentOperation;

/**
 * @author woden
 * 
 */
public class FunctionService {

	/**
	 * 删除类别以及对应的函数
	 * 
	 * @param cateKey
	 */
	@SuppressWarnings("unchecked")
	@DevelopmentOperation("删除函数类别")
	public void executeRemoteCatelogAndFunction(String cateKey) {

		Map<String, Object> catelog = (Map<String, Object>) ORMService.getInstance().findByPk("DevFunctionCatelog",
				cateKey);
		if (catelog == null) {
			// 类别已删除
			return;
		}

		// 删除子类别
		List<String> cateList = ORMService.getInstance().queryHQL(
				"select cateKey from DevFunctionCatelog where parentKey = ?", cateKey);
		if (cateList != null) {
			for (String key : cateList) {
				executeRemoteCatelogAndFunction(key);
			}
		}

		// 删除数据
		ORMService.getInstance().executeHQL("delete from DevFunction where catelog = ?", cateKey);

		// 删除类别
		ORMService.getInstance().remove(catelog);

	}

	/**
	 * 保存排序
	 * 
	 * @param catalogs
	 */
	@DevelopmentOperation("函数保存排序")
	public void executeSaveSort(List<HashMap<String, Object>> catalogs) {
		for (HashMap<String, Object> catalog : catalogs) {
			String cateKey = catalog.get("cateKey").toString();

			Map<String, Object> vo = (Map<String, Object>) ORMService.getInstance().findByPk("DevFunctionCatelog",
					cateKey);
			if (vo == null) {
				continue;
			}

			vo.put("sort", catalog.get("sort"));
			String parentKey = (String) catalog.get("parentKey");
			vo.put("parentKey", parentKey);
			ORMService.getInstance().update(vo);
		}
	}
}
