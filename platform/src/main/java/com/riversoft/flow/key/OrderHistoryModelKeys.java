/*
 * $HeadURL: $
 * $Id: $
 * Copyright (c) 2013 by Riversoft System, all rights reserved.
 */
package com.riversoft.flow.key;

import java.sql.Types;

import com.riversoft.platform.db.model.ModelKey;
import com.riversoft.platform.po.TbColumn;

/**
 * 工作流对应PO字段常量
 * 
 * @author woden
 * 
 */
public enum OrderHistoryModelKeys implements ModelKey {
	/**
     * 
     */
	ID("自动ID", true, true, true, Types.BIGINT, 14, 0),
	/**
     * 
     */
	ORD_ID("订单ID", false, false, true, Types.VARCHAR, 100, 0),
	/**
     * 
     */
	REMARK("摘要", false, false, false, Types.VARCHAR, 500, 0),
	/**
     * 
     */
	TASK_ID("任务ID", false, false, false, Types.VARCHAR, 100, 0),
	/**
     * 
     */
	TASK_BEGIN_DATE("任务开始时间", false, false, true, Types.TIMESTAMP, 0, 0),
	/**
     * 
     */
	TASK_END_DATE("任务完成时间", false, false, false, Types.TIMESTAMP, 0, 0),
	/**
     * 
     */
	NODE_TYPE("节点类型", false, false, true, Types.VARCHAR, 100, 0),
	/**
     * 
     */
	ACTIVITY_ID("节点ID", false, false, true, Types.VARCHAR, 100, 0),
	/**
      * 
      */
	ACTIVITY_NAME("节点名", false, false, false, Types.VARCHAR, 200, 0),
	/**
     * 
     */
	SEQUENCE_FLOW_ID("连线ID", false, false, false, Types.VARCHAR, 100, 0),
	/**
     * 
     */
	SEQUENCE_FLOW_NAME("连线名", false, false, false, Types.VARCHAR, 200, 0),
	/**
     * 
     */
	TASK_UID("处理人", false, false, false, Types.VARCHAR, 100, 0),
	/**
     * 
     */
	TASK_GROUP("处理组织", false, false, false, Types.VARCHAR, 100, 0),
	/**
     * 
     */
	EXECUTION_MEMO("流程执行备注", false, false, false, Types.CLOB, 0, 0),
	/**
     * 
     */
	ASSIGNEE("待处理人", false, false, false, Types.CLOB, 0, 0);

	private TbColumn column;

	private OrderHistoryModelKeys(String description, boolean primaryKey, boolean autoIncrement, boolean required,
			int mappedTypeCode, int totalSize, int scale) {
		column = new TbColumn();
		column.setName(this.name());
		column.setPrimaryKey(primaryKey);
		column.setRequired(required);
		column.setAutoIncrement(autoIncrement);
		column.setDescription(description);
		column.setMappedTypeCode(mappedTypeCode);
		column.setTotalSize(totalSize);
		column.setScale(scale);
		column.setSort(0);
	}

	@Override
	public TbColumn getColumn() {
		return column;
	}
}
