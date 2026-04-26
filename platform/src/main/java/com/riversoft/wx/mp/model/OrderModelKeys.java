package com.riversoft.wx.mp.model;

import com.riversoft.platform.db.model.ModelKey;
import com.riversoft.platform.po.TbColumn;

import java.sql.Types;

/**
 * 订单支付表
 * @borball on 3/17/2016.
 */
public enum OrderModelKeys implements ModelKey {

    ID("ID", true, true, true, Types.BIGINT, 14, 0),
    OPEN_ID("购买用户", false, false, true, Types.VARCHAR, 100, 0),
    LOG_DATE("登记时间", false, false, true, Types.TIMESTAMP, 0, 0),
    MP_KEY("公众号KEY", false, false, true, Types.VARCHAR, 100, 0),

    ORDER_ID("订单ID", false, false, true, Types.VARCHAR, 100, 0),
    ORDER_STATUS("订单状态", false, false, true, Types.INTEGER, 8, 0),

    TOTAL_PRICE("订单总价格", false, false, true, Types.INTEGER, 8, 0),
    DELIVERY_PRICE("运费价格", false, false, true, Types.INTEGER, 8, 0),

    CREATED_DATE("创建时间", false, false, true, Types.TIMESTAMP, 0, 0),

    PRODUCT_ID("产品ID", false, false, true, Types.VARCHAR, 100, 0),
    PRODUCT_NAME("产品名称", false, false, true, Types.VARCHAR, 100, 0),
    PRODUCT_SKU("产品SKU", false, false, false, Types.VARCHAR, 100, 0),
    PRODUCT_PRICE("产品价格", false, false, true, Types.INTEGER, 8, 0),
    PRODUCT_COUNT("产品个数", false, false, true, Types.INTEGER, 8, 0),
    PRODUCT_IMG("产品图片", false, false, false, Types.VARCHAR, 500, 0),

    NICK_NAME("微信昵称", false, false, false, Types.VARCHAR, 100, 0),

    RECEIVER_NAME("收货人姓名", false, false, false, Types.VARCHAR, 100, 0),
    RECEIVER_PROVINCE("收货地址省份", false, false, false, Types.VARCHAR, 100, 0),
    RECEIVER_CITY("收货地址城市", false, false, false, Types.VARCHAR, 100, 0),
    RECEIVER_ZONE("收货地址区县", false, false, false, Types.VARCHAR, 100, 0),
    RECEIVER_ADDRESS("收货详细地址", false, false, false, Types.VARCHAR, 100, 0),
    RECEIVER_MOBILE("收货人移动电话", false, false, false, Types.VARCHAR, 100, 0),
    RECEIVER_PHONE("收货人固定电话", false, false, false, Types.VARCHAR, 100, 0),

    DELIVERY_ID("运单ID", false, false, false, Types.VARCHAR, 100, 0),
    DELIVERY_COMPANY("物流公司编码", false, false, false, Types.VARCHAR, 100, 0),

    TRANS_ID("交易ID", false, false, false, Types.VARCHAR, 100, 0);

    private TbColumn column;

    private OrderModelKeys(String description, boolean primaryKey, boolean autoIncrement, boolean required, int mappedTypeCode, int totalSize, int scale) {
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
