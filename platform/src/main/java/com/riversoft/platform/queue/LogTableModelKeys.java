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
 * 异步队列处理日志表模型
 * 
 * @author exizhai
 * 
 */
public enum LogTableModelKeys implements ModelKey {

	ID("ID", true, Types.BIGINT),
	/**
	 * 
	 */
	QUEUE_ID("任务ID", false, Types.BIGINT),
	/**
	 * 
	 */
	QUEUE_DATA("任务上下文", false, Types.CLOB),
	/**
	 * 
	 */
	LOG_MSG("执行结果", false, Types.CLOB),
	/**
	 * 
	 */
	SUCCESS_FLAG("成功标志", false, Types.INTEGER),
	/**
	 * 
	 */
	BEGIN_DATE("开始时间", false, Types.TIMESTAMP),
	/**
	 * 
	 */
	END_DATE("结束时间", false, Types.TIMESTAMP);

	private TbColumn column;

	private LogTableModelKeys(String description, boolean primaryKey, int mappedTypeCode) {
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
