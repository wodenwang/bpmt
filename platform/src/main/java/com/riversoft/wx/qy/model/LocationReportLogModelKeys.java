/*
 * $HeadURL: $
 * $Id: $
 * Copyright (c) 2013 by Riversoft System, all rights reserved.
 */
package com.riversoft.wx.qy.model;

import com.riversoft.platform.db.model.ModelKey;
import com.riversoft.platform.po.TbColumn;

import java.sql.Types;

/**
 * 工作流对应PO字段常量
 *
 * @author woden
 */
public enum LocationReportLogModelKeys implements ModelKey {

    ID("ID", true, true, true, Types.BIGINT, 14, 0),
    USER_ID("当前用户", false, false, true, Types.VARCHAR, 100, 0),
    LOG_DATE("登记时间", false, false, true, Types.TIMESTAMP, 0, 0),
    AGENT_KEY("微信应用KEY", false, false, true, Types.VARCHAR, 100, 0),
    AGENT_ID("微信应用ID", false, false, true, Types.INTEGER, 8, 0),

    X("地理位置纬度", false, false, false, Types.VARCHAR, 100, 0),
    Y("地理位置经度", false, false, false, Types.VARCHAR, 100, 0),
    SCALE("地图缩放大小", false, false, false, Types.VARCHAR, 100, 0),
    LABEL("地理位置信息", false, false, false, Types.VARCHAR, 100, 0);

    private TbColumn column;

    private LocationReportLogModelKeys(String description, boolean primaryKey, boolean autoIncrement, boolean required, int mappedTypeCode, int totalSize, int scale) {
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
