package com.riversoft.wx.mp.model;

import com.riversoft.platform.db.model.ModelKey;
import com.riversoft.platform.po.TbColumn;

import java.sql.Types;

/**
 * @borball on 5/25/2016.
 */
public enum PaymentNotificationModelKeys implements ModelKey {

    ID("ID", true, true, true, Types.BIGINT, 14, 0),
    MP_KEY("公众号KEY", false, false, true, Types.VARCHAR, 100, 0),
    APP_ID("AppID", false, false, true, Types.VARCHAR, 100, 0),
    MCH_ID("商户号", false, false, true, Types.VARCHAR, 100, 0),
    DEVICE_INFO("设备号", false, false, false, Types.VARCHAR, 32, 0),
    OPEN_ID("OpenID", false, false, false, Types.VARCHAR, 100, 0),
    SUBSCRIBED("是否关注公众账号", false, false, false, Types.INTEGER, 8, 0),
    TRANSACTION_ID("微信支付订单号", false, false, false, Types.VARCHAR, 100, 0),
    TRADE_NUMBER("商户订单号", false, false, false, Types.VARCHAR, 100, 0),
    RETURN_CODE("返回状态码", false, false, false, Types.VARCHAR, 32, 0),
    RETURN_MSG("返回信息", false, false, false, Types.VARCHAR, 128, 0),
    RESULT_CODE("业务结果", false, false, false, Types.VARCHAR, 32, 0),
    ERROR_CODE("错误代码", false, false, false, Types.VARCHAR, 32, 0),
    ERROR_CODE_DESC("错误代码描述", false, false, false, Types.VARCHAR, 128, 0),
    TRADE_TYPE("交易类型", false, false, false, Types.VARCHAR, 100, 0),
    BANK_TYPE("付款银行", false, false, false, Types.VARCHAR, 100, 0),
    TOTAL_FEE("订单金额", false, false, false, Types.INTEGER, 8, 0),
    SETTLE_TOTAL_FEE("应结订单金额", false, false, false, Types.INTEGER, 8, 0),
    CASH_FEE("现金支付金额", false, false, false, Types.INTEGER, 8, 0),
    COUPON_FEE("代金券金额", false, false, false, Types.INTEGER, 8, 0),
    COUPON_COUNT("代金券使用数量", false, false, false, Types.INTEGER, 8, 0),
    END_TIME("支付完成时间", false, false, false, Types.TIMESTAMP, 0, 0),
    CREATED_TIME("收到通知时间", false, false, false, Types.TIMESTAMP, 0, 0),
    ATTACH("商家数据包", false, false, false, Types.CLOB, 0, 0);

    private TbColumn column;

    private PaymentNotificationModelKeys(String description, boolean primaryKey, boolean autoIncrement, boolean required, int mappedTypeCode, int totalSize, int scale) {
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
