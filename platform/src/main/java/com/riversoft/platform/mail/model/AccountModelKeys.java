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
 * 邮箱账号表
 * 
 * @author woden
 * 
 */
public enum AccountModelKeys implements ModelKey {

	/**
     * 
     */
	USER_ID("用户ID", true, false, true, Types.VARCHAR, 100, 0),
	/**
     * 
     */
	MAIL_ACCOUNT("邮箱账号", false, false, true, Types.VARCHAR, 100, 0),
	/**
	 * 
	 */
	MAIL_PASSWORD("邮箱密码", false, false, false, Types.VARCHAR, 100, 0),
	/**
	 * 
	 */
	MAIL_NAME("邮箱名", false, false, true, Types.VARCHAR, 100, 0),
	/**
     * 
     */
	LAST_CHECK("最后收信时间", false, false, true, Types.TIMESTAMP, 0, 0);

	private TbColumn column;

	private AccountModelKeys(String description, boolean primaryKey, boolean autoIncrement, boolean required,
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
