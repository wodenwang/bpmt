/*
 * $HeadURL: $
 * $Id: $
 * Copyright (c) 2013 by Riversoft System, all rights reserved.
 */
package com.riversoft.module.manager.db;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.riversoft.core.db.DataCondition;
import com.riversoft.core.db.DataPO;
import com.riversoft.core.db.ORMService;
import com.riversoft.core.exception.ExceptionType;
import com.riversoft.core.exception.SystemRuntimeException;
import com.riversoft.platform.template.DevelopmentOperation;

/**
 * @author woden
 * 
 */
public class DbService {

	/**
	 * 删除数据字典
	 * 
	 * @param dataType
	 */
	@DevelopmentOperation("删除整组字典")
	public void executeRemoveTypeAndData(String dataType) {
		ORMService.getInstance().executeHQL("delete from CmBaseType where dataType = ?", dataType);
		ORMService.getInstance().executeHQL("delete from CmBaseData where dataType = ?", dataType);
	}

	/**
	 * 删除数据
	 * 
	 * @param dataType
	 * @param dataCodes
	 */
	@DevelopmentOperation("删除字典数据")
	public void executeRemoveData(String dataType, String[] dataCodes) {
		if (dataCodes != null) {
			for (String dataCode : dataCodes) {
				ORMService.getInstance().executeHQL("delete from CmBaseData where dataType = ? and dataCode = ?",
						dataType, dataCode);
			}
		}
	}

	/**
	 * 批量添加
	 * 
	 * @param list
	 */
	@DevelopmentOperation("批量添加字典")
	public void executeBatchCreate(List<Map<String, String>> list) {
		if (list == null) {
			return;
		}

		// 需要更新或修改的类别
		Map<String, Map<String, Object>> typeMap = new HashMap<>();

		for (Map<String, String> vo : list) {

			// type
			String dataType = vo.get("dataType");
			String busiName = vo.get("_busiName");
			String catelog = vo.get("_catelog");
			if (!typeMap.containsKey(dataType)) {
				DataPO po = new DataPO("CmBaseType");
				po.set("dataType", dataType);
				po.set("busiName", busiName);
				po.set("catelog", catelog);
				po.set("sort", 0);
				po.set("createDate", new Date());
				po.set("updateDate", new Date());
				typeMap.put(dataType, po.toEntity());
			}

			// data
			String dataCode = vo.get("dataCode");
			String showName = vo.get("showName");
			String parentCode = vo.get("parentCode");
			Integer sort = StringUtils.isNotEmpty(vo.get("sort")) ? Integer.valueOf(vo.get("sort")) : 0;
			String extra = vo.get("extra");
			String description = vo.get("description");
			{
				DataPO po = new DataPO("CmBaseData");
				po.set("dataCode", dataCode);
				po.set("dataType", dataType);
				po.set("showName", showName);
				po.set("parentCode", parentCode);
				po.set("sort", sort);
				po.set("extra", extra);
				po.set("description", description);
				ORMService.getInstance().save(po.toEntity());
			}
		}

		ORMService.getInstance().saveOrUpdateBatch(typeMap.values());
	}

	/**
	 * 批量修改
	 * 
	 * @param list
	 */
	@DevelopmentOperation("批量更新字典")
	@SuppressWarnings("unchecked")
	public void executeBatchUpdate(List<Map<String, String>> list) {
		if (list == null) {
			return;
		}

		// 需要更新或修改的类别
		Map<String, Map<String, Object>> typeMap = new HashMap<>();

		for (Map<String, String> vo : list) {

			// type
			String dataType = vo.get("dataType");
			String busiName = vo.get("_busiName");
			String catelog = vo.get("_catelog");
			if (!typeMap.containsKey(dataType)) {
				DataPO po = new DataPO("CmBaseType");
				po.set("dataType", dataType);
				po.set("busiName", busiName);
				po.set("catelog", catelog);
				po.set("sort", 0);
				po.set("createDate", new Date());
				po.set("updateDate", new Date());
				typeMap.put(dataType, po.toEntity());
			}

			// data
			String dataCode = vo.get("dataCode");
			String showName = vo.get("showName");
			String parentCode = vo.get("parentCode");
			Integer sort = StringUtils.isNotEmpty(vo.get("sort")) ? Integer.valueOf(vo.get("sort")) : 0;
			String extra = vo.get("extra");
			String description = vo.get("description");
			{
				Map<String, Object> entity = (Map<String, Object>) ORMService.getInstance().find(
						"CmBaseData",
						new DataCondition().setStringEqual("dataType", dataType).setStringEqual("dataCode", dataCode)
								.toEntity());
				if (entity == null) {
					throw new SystemRuntimeException(ExceptionType.BUSINESS, "数据[" + dataType + "][" + dataCode
							+ "]不存在,无法更新.");
				}
				DataPO po = new DataPO("CmBaseData", entity);
				po.set("showName", showName);
				po.set("parentCode", parentCode);
				po.set("sort", sort);
				po.set("extra", extra);
				po.set("description", description);
				ORMService.getInstance().update(po.toEntity());
			}
		}

		ORMService.getInstance().saveOrUpdateBatch(typeMap.values());
	}

	/**
	 * 保存排序
	 * 
	 * @param catalogs
	 * @param types
	 */
	@DevelopmentOperation("保存字典排序")
	public void executeSaveSort(List<HashMap<String, Object>> catalogs, List<HashMap<String, Object>> types) {
		{
			// 保存类别
			for(HashMap<String, Object> catalog : catalogs) {
				Map<String, Object> vo = (Map<String, Object>) ORMService.getInstance().findByPk("CmBaseCatelog",
						catalog.get("id").toString());
				if (vo == null) {
					continue;
				}
				vo.put("sort", catalog.get("sort"));
				ORMService.getInstance().update(vo);
			}

		}

		{
			// 保存模型
			for(HashMap<String, Object> type: types) {
				Map<String, Object> vo = (Map<String, Object>) ORMService.getInstance().findByPk("CmBaseType",
						type.get("dataType").toString());

				if (vo == null) {
					continue;
				}
				vo.put("sort", type.get("sort"));
				vo.put("catelog", type.get("catelog"));
				ORMService.getInstance().update(vo);
			}
		}
	}
}
