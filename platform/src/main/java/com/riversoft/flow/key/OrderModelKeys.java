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
public enum OrderModelKeys implements ModelKey {

	/**
     * 
     */
	ORD_ID("订单ID", true, false, true, Types.VARCHAR, 100, 0),
	/**
     * 
     */
	REMARK("摘要", false, false, false, Types.VARCHAR, 0, 0),
	/**
	 * 
	 */
	MEMO("备注", false, false, false, Types.CLOB, 0, 0),
	/**
	 * 
	 */
	ATTACHMENT("附件", false, false, false, Types.BLOB, 0, 0),
	/**
	 * 0:未启动<br>
	 * 1:运行中<br>
	 * 2:正常结束<br>
	 * 3:异常结束<br>
	 */
	ORD_STATE("订单状态", false, false, true, Types.INTEGER, 8, 0),
	/**
	 * 业务上不关心,标识属于哪个流程,这样在流程升级之后,依然可以正常使用订单模型.<br>
	 * 如此设计可以让多种不同流程共享同一种订单模型.
	 */
	PD_ID("关联流程定义ID", false, false, true, Types.VARCHAR, 100, 0),
	/**
     * 
     */
	OWNER("创建者", false, false, false, Types.VARCHAR, 100, 0),
	/**
     * 
     */
	OWNER_GROUP("创建者组织", false, false, false, Types.VARCHAR, 100, 0),
	/**
     * 
     */
	OWNER_ROLE("创建者角色", false, false, false, Types.VARCHAR, 100, 0),
	/**
     * 
     */
	CREATE_DATE("创建时间", false, false, true, Types.TIMESTAMP, 0, 0),
	/**
     * 
     */
	UIDS("关系处理人", false, false, false, Types.CLOB, 0, 0);

	private TbColumn column;

	private OrderModelKeys(String description, boolean primaryKey, boolean autoIncrement, boolean required,
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
