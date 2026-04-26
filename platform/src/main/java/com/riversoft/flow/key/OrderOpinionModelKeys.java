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
public enum OrderOpinionModelKeys implements ModelKey {
	/**
	 * 
	 */
	ID("", true, true, true, Types.BIGINT, 16, 0),
	/**
     * 
     */
	ORD_ID("订单ID", false, false, true, Types.VARCHAR, 100, 0),
	/**
	 * 
	 */
	OPINION("审批内容", false, false, false, Types.CLOB, 0, 0),
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
	OPR_USER("创建者", false, false, true, Types.VARCHAR, 100, 0),
	/**
     * 
     */
	OPR_GROUP("创建者组织", false, false, true, Types.VARCHAR, 100, 0),
	/**
     * 
     */
	OPR_ROLE("创建者角色", false, false, true, Types.VARCHAR, 100, 0),
	/**
     * 
     */
	CREATE_DATE("创建时间", false, false, true, Types.TIMESTAMP, 0, 0);

	private TbColumn column;

	private OrderOpinionModelKeys(String description, boolean primaryKey, boolean autoIncrement, boolean required,
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
