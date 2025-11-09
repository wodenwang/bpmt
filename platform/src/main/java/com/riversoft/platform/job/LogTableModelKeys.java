/*
 * $HeadURL: $
 * $Id: $
 * Copyright (c) 2013 by Riversoft System, all rights reserved.
 */
package com.riversoft.platform.job;

import java.sql.Types;

import com.riversoft.platform.db.model.ModelKey;
import com.riversoft.platform.po.TbColumn;

/**
 * 计划任务日志表模型
 * 
 * @author woden
 * 
 */
public enum LogTableModelKeys implements ModelKey {

	ID("ID", true, Types.BIGINT),
	/**
	 * 
	 */
	JOB_KEY("任务主键", false, Types.VARCHAR),
	/**
	 * 
	 */
	DESCRIPTION("任务描述", false, Types.VARCHAR),
	/**
	 * 
	 */
	LOG_MSG("执行结果信息", false, Types.CLOB),
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
