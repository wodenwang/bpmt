/*
 * $HeadURL: $
 * $Id: $
 * Copyright (c) 2013 by Riversoft System, all rights reserved.
 */
package com.riversoft.platform.queue;

import com.riversoft.platform.db.model.ModelKey;
import com.riversoft.platform.po.TbColumn;

import java.sql.Types;

/**
 * 异步队列表模型
 * 
 * @author exizhai
 * 
 */
public enum QueueTableModelKeys implements ModelKey {

	ID("ID", true, Types.BIGINT),
	/**
	 *
	 */
	TYPE("类别", false, Types.VARCHAR),
	/**
	 *
	 */
	STATUS("状态", false, Types.INTEGER),
	/**
	 *
	 */
	CREATED_DATE("创建时间", false, Types.TIMESTAMP),

	/**
	 *
	 */
	NEXTACTION_DATE("下次处理时间", false, Types.TIMESTAMP),
	/**
	 *
	 */
	RETRIES("重试次数", false, Types.INTEGER);

	private TbColumn column;

	private QueueTableModelKeys(String description, boolean primaryKey, int mappedTypeCode) {
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
