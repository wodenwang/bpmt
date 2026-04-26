/*
 * $HeadURL: $
 * $Id: $
 * Copyright (c) 2013 by Riversoft System, all rights reserved.
 */
package com.riversoft.wx.mp.model;

import com.riversoft.platform.db.model.ModelKey;
import com.riversoft.platform.po.TbColumn;

import java.sql.Types;

/**
 * 公众号消息日志模型
 *
 * @author woden
 */
public enum MessageLogModelKeys implements ModelKey {

    ID("ID", true, true, true, Types.BIGINT, 14, 0),
    OPEN_ID("当前用户", false, false, true, Types.VARCHAR, 100, 0),
    LOG_DATE("登记时间", false, false, true, Types.TIMESTAMP, 0, 0),
    MP_KEY("公众号KEY", false, false, true, Types.VARCHAR, 100, 0),

    MSG_TYPE("消息类型", false, false, true, Types.VARCHAR, 100, 0),

    MSG_X("地理位置纬度", false, false, false, Types.VARCHAR, 100, 0),
    MSG_Y("地理位置经度", false, false, false, Types.VARCHAR, 100, 0),
    MSG_SCALE("地图缩放大小", false, false, false, Types.VARCHAR, 100, 0),
    MSG_LABEL("地理位置信息", false, false, false, Types.VARCHAR, 100, 0),
    MSG_TEXT("消息文本", false, false, false, Types.CLOB, 0, 0),
    MSG_ATTACHMENT("附件", false, false, false, Types.BLOB, 0, 0);

    private TbColumn column;

    private MessageLogModelKeys(String description, boolean primaryKey, boolean autoIncrement, boolean required, int mappedTypeCode, int totalSize, int scale) {
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
