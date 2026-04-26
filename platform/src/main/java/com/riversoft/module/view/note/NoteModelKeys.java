/*
 * $HeadURL: $
 * $Id: $
 * Copyright (c) 2013 by Riversoft System, all rights reserved.
 */
package com.riversoft.module.view.note;

import java.sql.Types;

import com.riversoft.platform.db.model.ModelKey;
import com.riversoft.platform.po.TbColumn;

/**
 * 工作流对应PO字段常量
 * 
 * @author woden
 * 
 */
public enum NoteModelKeys implements ModelKey {
	/**
	 * 
	 */
	ID("ID", true, Types.BIGINT),
	/**
	 * 
	 */
	TITLE("标题", false, Types.VARCHAR),
	/**
	 * 
	 */
	CONTENT("内容", false, Types.CLOB),
	/**
	 * 
	 */
	AUTHOR("发布人", false, Types.VARCHAR),
	/**
	 * 
	 */
	SORT("排序", false, Types.INTEGER),
	/**
	 * 
	 */
	ATTACHMENT("附件", false, Types.BLOB),
	/**
	 * 
	 */
	PUBLISH_DATE("发布时间", false, Types.TIMESTAMP);

	private TbColumn column;

	private NoteModelKeys(String description, boolean primaryKey, int mappedTypeCode) {
		column = new TbColumn();
		column.setName(this.name());
		column.setPrimaryKey(primaryKey);
		column.setDescription(description);
		column.setMappedTypeCode(mappedTypeCode);
		column.setSort(0);
	}

	@Override
	public TbColumn getColumn() {
		return column;
	}
}
