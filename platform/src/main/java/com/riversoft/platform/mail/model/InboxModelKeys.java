/*
 * $HeadURL: $
 * $Id: $
 * Copyright (c) 2013 by Riversoft System, all rights reserved.
 */
package com.riversoft.platform.mail.model;

import java.sql.Types;

import com.riversoft.platform.db.model.ModelKey;
import com.riversoft.platform.po.TbColumn;

/**
 * 收件箱表
 * 
 * @author woden
 * 
 */
public enum InboxModelKeys implements ModelKey {

	/**
	 * 
	 */
	ID("ID", true, true, true, Types.BIGINT, 14, 0),
	/**
	 * 
	 */
	SUBJECT("标题", false, false, false, Types.CLOB, 0, 0),
	/** 
	 * 
	 */
	FROM_ADDR("发件人", false, false, false, Types.CLOB, 0, 0),
	/** 
	 * 
	 */
	TO_ADDRS("收件人", false, false, false, Types.CLOB, 0, 0),
	/**
	 * 
	 */
	CC_ADDRS("抄送人", false, false, false, Types.CLOB, 0, 0),
	/**
	 * 
	 */
	BCC_ADDRS("密送人", false, false, false, Types.CLOB, 0, 0),
	/**
	 * 
	 */
	CONTENT("内容", false, false, false, Types.CLOB, 0, 0),
	/** 
	 * 
	 */
	ATTACHMENT("附件", false, false, false, Types.BLOB, 0, 0),
	/** 
	 * 
	 */
	CONTENT_ATTACHMENT(" 内置附件", false, false, false, Types.BLOB, 0, 0),
	/** 
	 * 
	 */
	SENT_DATE("发送时间", false, false, true, Types.TIMESTAMP, 0, 0),
	/** 
	 * 
	 */
	RECEIVE_DATE("接收时间", false, false, true, Types.TIMESTAMP, 0, 0),
	/**
	 * 
	 */
	USER_ID("关联用户", false, false, true, Types.VARCHAR, 100, 0),
	/**
	 * 0:未读 1:已读
	 */
	STATE("状态", false, false, true, Types.INTEGER, 8, 0);

	private TbColumn column;

	private InboxModelKeys(String description, boolean primaryKey, boolean autoIncrement, boolean required,
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
