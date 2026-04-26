/*
 * $HeadURL: $
 * $Id: $
 * Copyright (c) 2013 by Riversoft System, all rights reserved.
 */
package com.riversoft.wx.mp.model;

import com.riversoft.platform.db.model.ModelKey;
import com.riversoft.platform.po.TbColumn;

/**
 * 公众号标签模型
 *
 * @author woden
 */
public enum MpVisitorTagModelKeys implements ModelKey {

	TAG_ID("ID", true, false, true, 4, 8, 0),
	MP_KEY("公众号KEY", false, false, true, 12, 100, 0),
	TAG_NAME("标签名称", false, false, true, 12, 100, 0),
	SYNC_TIME("同步时间", false, false, true, 93, 0, 0),
	REMARK("备注", false, false, false, 12, 300, 0);

	private TbColumn column;

	MpVisitorTagModelKeys(String description, boolean primaryKey, boolean autoIncrement, boolean required, int mappedTypeCode, int totalSize, int scale) {
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
