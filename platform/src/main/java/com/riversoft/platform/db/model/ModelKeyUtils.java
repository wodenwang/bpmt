/*
 * $HeadURL: $
 * $Id: $
 * Copyright (c) 2015 by Riversoft System, all rights reserved.
 */
package com.riversoft.platform.db.model;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.riversoft.platform.po.TbColumn;
import com.riversoft.platform.po.TbTable;
import com.riversoft.util.ReflectionUtils;

/**
 * 动态表模型模板
 * 
 * @author woden
 */
public class ModelKeyUtils {

	/**
	 * 校验表是否合法订单表
	 * 
	 * @param enumClazz
	 * @param model
	 * @return
	 */
	public static boolean checkModel(Class<? extends ModelKey> enumClazz, TbTable model) {
		if (model == null) {
			return false;
		}

		ModelKey[] keys = (ModelKey[]) ReflectionUtils.getMethodValue(enumClazz, enumClazz, "values", new Class[] {},
				new Object[] {});
		Map<ModelKey, Boolean> result = new HashMap<>();
		for (TbColumn column : model.getTbColumns()) {
			for (ModelKey key : keys) {
				TbColumn keyColumn = key.getColumn();
				if (keyColumn.getName().equals(column.getName())) {
					// 校验类型,必须是字符串,必须是主键
					if (keyColumn.isPrimaryKey() != column.isPrimaryKey()) {
						return false;
					}
					if (column.getMappedTypeCode() != keyColumn.getMappedTypeCode()) {
						return false;
					}
					result.put(key, true);
				}
			}
		}

		for (ModelKey key : keys) {
			if (!result.containsKey(key)) {
				return false;
			}

			if (!result.get(key)) {
				return false;
			}
		}
		return true;
	}

	/**
	 * 创建表
	 * 
	 * @param enumClazz
	 * @param tableName
	 * @param description
	 * @return
	 */
	public static TbTable buildTable(Class<? extends ModelKey> enumClazz, String tableName, String description) {
		TbTable table = new TbTable();
		table.setName(tableName);
		table.setDescription(description);
		table.setCacheFlag(0);
		Set<TbColumn> tbColumns = new HashSet<>();
		ModelKey[] keys = (ModelKey[]) ReflectionUtils.getMethodValue(enumClazz, enumClazz, "values", new Class[] {},
				new Object[] {});
		int i = 0;
		for (ModelKey key : keys) {
			TbColumn keyColumn = key.getColumn();
			try {
				TbColumn column = (TbColumn) keyColumn.clone();
				column.setSort(i++);
				column.setTableName(tableName);
				tbColumns.add(column);
			} catch (CloneNotSupportedException ignore) {
				// do nothing
				ignore.printStackTrace();
			}
		}
		table.setTbColumns(tbColumns);
		return table;
	}
}
