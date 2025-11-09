package com.riversoft.wx.mp.model;

import com.riversoft.platform.db.model.ModelKey;
import com.riversoft.platform.po.TbColumn;

import java.sql.Types;

/**
 * @borball on 3/10/2016.
 */
public enum TemplateMsgLogModelKeys implements ModelKey {

    MSG_ID("消息ID", true, false, true, Types.BIGINT, 14, 0),
    OPEN_ID("接收用户", false, false, true, Types.VARCHAR, 100, 0),
    LOG_DATE("登记时间", false, false, true, Types.TIMESTAMP, 0, 0),
    MP_KEY("公众号KEY", false, false, true, Types.VARCHAR, 100, 0),
    TEMPLATE_ID("模板ID", false, false, true, Types.VARCHAR, 100, 0),
    MSG_CONTENT("消息内容", false, false, false, Types.CLOB, 0, 0),
    RESPOND_DATE("回复时间", false, false, false, Types.TIMESTAMP, 0, 0),
    RESULT("发送结果", false, false, false, Types.VARCHAR, 100, 0);

    private TbColumn column;

    private TemplateMsgLogModelKeys(String description, boolean primaryKey, boolean autoIncrement, boolean required, int mappedTypeCode, int totalSize, int scale) {
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
