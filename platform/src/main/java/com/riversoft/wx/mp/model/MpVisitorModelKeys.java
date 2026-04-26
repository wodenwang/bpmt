/*
 * $HeadURL: $
 * $Id: $
 * Copyright (c) 2013 by Riversoft System, all rights reserved.
 */
package com.riversoft.wx.mp.model;

import java.sql.Types;

import com.riversoft.platform.db.model.ModelKey;
import com.riversoft.platform.po.TbColumn;

/**
 * 公众号用户模型
 *
 * @author woden
 */
public enum MpVisitorModelKeys implements ModelKey {

	OPEN_ID("OPEN_ID", true, false, true, Types.VARCHAR, 100, 0),
	MP_KEY("公众号KEY", false, false, true, Types.VARCHAR, 100, 0),
	NICK_NAME("昵称", false, false, true, Types.VARCHAR, 100, 0),
	SEX("性别", false, false, false, Types.INTEGER, 8, 0),
	LANGUAGE("用户语言", false, false, false, Types.VARCHAR, 100, 0),
	CITY("城市", false, false, false, Types.VARCHAR, 100, 0),
	PROVINCE("省份", false, false, false, Types.VARCHAR, 100, 0),
	COUNTRY("国家", false, false, false, Types.VARCHAR, 100, 0),
	HEAD_IMG_URL("头像", false, false, false, Types.CLOB, 0, 0),
	SUBSCRIBE("是否关注", false, false, true, Types.INTEGER, 8, 0),
	SUBSCRIBE_TIME("关注时间", false, false, false, Types.TIMESTAMP, 0, 0),
	UNSUBSCRIBE_TIME("取消关注时间", false, false, false, Types.TIMESTAMP, 0, 0),
	REMARK("备注", false, false, false, Types.CLOB, 0, 0),
	TAGS("所属标签", false, false, false, Types.VARCHAR, 100, 0),
	UNION_ID("UNION_ID", false, false, false, Types.VARCHAR, 100, 0),
	USER_ID("系统用户ID", false, false, false, Types.VARCHAR, 100, 0),
	CREATE_TIME("创建时间", false, false, false, Types.TIMESTAMP, 0, 0);

	private TbColumn column;

	MpVisitorModelKeys(String description, boolean primaryKey, boolean autoIncrement, boolean required, int mappedTypeCode, int totalSize, int scale) {
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
