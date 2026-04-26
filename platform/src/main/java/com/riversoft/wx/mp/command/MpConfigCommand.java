package com.riversoft.wx.mp.command;

import com.riversoft.core.db.DataPO;
import com.riversoft.core.db.JdbcService;
import com.riversoft.core.db.ORMService;
import com.riversoft.platform.db.ORMAdapterService;
import com.riversoft.platform.script.ScriptHelper;
import com.riversoft.platform.script.ScriptTypes;
import com.riversoft.platform.translate.WxSendDataType;
import com.riversoft.util.jackson.JsonMapper;
import com.riversoft.weixin.mp.shop.Order;
import com.riversoft.weixin.mp.shop.Orders;
import com.riversoft.wx.annotation.WxAnnotatedCommandsHolder;
import com.riversoft.wx.command.CommandExecutionService;
import com.riversoft.wx.context.Location;
import com.riversoft.wx.mp.MpAppSetting;
import com.riversoft.wx.mp.MpHelper;
import com.riversoft.wx.mp.MpMediaHelper;
import com.riversoft.wx.mp.context.PayResult;
import com.riversoft.wx.mp.model.LocationReportLogModelKeys;
import com.riversoft.wx.mp.model.MessageLogModelKeys;

import com.riversoft.wx.mp.model.OrderModelKeys;
import com.riversoft.wx.mp.model.PaymentNotificationModelKeys;
import com.riversoft.wx.mp.service.MpAppService;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by exizhai on 12/23/2015.
 */
public class MpConfigCommand implements MpCommand {

    private static Logger logger = LoggerFactory.getLogger(MpConfigCommand.class);

    @SuppressWarnings("unchecked")
    @Override
    public MpResponse execute(MpRequest request) {

        Map<String, Object> config = null;

        if (request.isMenu()) {// 菜单
            if (StringUtils.isEmpty(request.getEventKey())) {
                return null;
            }
            config = (Map<String, Object>) ORMService.getInstance().findByPk("WxMpMenu", request.getEventKey());
        } else if (request.isMessage()) {// 对话框
            config = (Map<String, Object>) ORMService.getInstance().findByPk("WxMpMessage", request.getMpKey());
        } else if (request.isLocationEvent()) {// 上报位置
            config = (Map<String, Object>) ORMService.getInstance().findByPk("WxMpLocation", request.getMpKey());
        } else if (request.isSubscribe()) {
            config = (Map<String, Object>) ORMService.getInstance().findByPk("WxMpSubscribe", request.getMpKey());
        } else if (request.isUnSubscribe()) {
            config = (Map<String, Object>) ORMService.getInstance().findByPk("WxMpUnSubscribe", request.getMpKey());
        } else if (request.isSceneScan()) {
            config = (Map<String, Object>) ORMService.getInstance().findByPk("WxMpScanIn", request.getMpKey());
        } else if(request.getOrderPay() != null) {
            config = (Map<String, Object>) ORMService.getInstance().findByPk("WxMpOrder", request.getMpKey());
        } else if(request.getPayResult() != null) {
            config = (Map<String, Object>) ORMService.getInstance().findByPk("WxMpPayNotify", request.getMpKey());
        }

        if (config == null) {
            return null;
        }

        boolean eventFlag;
        boolean logFlag;
        if (request.isMenu()) {// 菜单不登记表,但必须处理事件
            eventFlag = true;
            logFlag = false;
        } else {
            eventFlag = (Integer) config.get("eventFlag") == 1;
            if(config.containsKey("logFlag")) {
                logFlag = (Integer) config.get("logFlag") == 1;
            } else {
                logFlag = false;
            }
        }

        // 登记表
        logStep:
        if (logFlag) {
            if (request.isMessage()) { // 消息模式下特殊有多一层类型拦截,登记日志
                String[] logType = StringUtils.split((String) config.get("logType"), ";");
                if (logType == null || logType.length < 1) {// 没有勾选拦截类型,不拦截
                    break logStep;
                }
                if (request.getText() != null && !ArrayUtils.contains(logType, WxSendDataType.TEXT.name())) {
                    break logStep;
                } else if (request.getImage() != null && !ArrayUtils.contains(logType, WxSendDataType.IMAGE.name())) {
                    break logStep;
                } else if (request.getShortVideo() != null && !ArrayUtils.contains(logType, WxSendDataType.SHORT_VIDEO.name())) {
                    break logStep;
                } else if (request.getVideo() != null && !ArrayUtils.contains(logType, WxSendDataType.VIDEO.name())) {
                    break logStep;
                } else if (request.getVoice() != null && !ArrayUtils.contains(logType, WxSendDataType.VOICE.name())) {
                    break logStep;
                } else if (request.getLocation() != null && !ArrayUtils.contains(logType, WxSendDataType.LOCATION.name())) {
                    break logStep;
                } else if (request.getLink() != null && !ArrayUtils.contains(logType, WxSendDataType.LINK.name())) {
                    break logStep;
                }

                String tableName = (String) config.get("logTable");
                if (StringUtils.isNotEmpty(tableName)) {
                    logger.debug("record message to table:{}...", tableName);
                    logMessage(tableName, request);
                }
            } else if (request.isLocationEvent()) {// 位置上报事件登记日志
                String tableName = (String) config.get("logTable");
                if (StringUtils.isNotEmpty(tableName)) {
                    logger.debug("record location to table:{}...", tableName);
                    logLocationReport(tableName, request);
                }
            } else if (request.getOrderPay() != null) {// 订单支付事件
                String tableName = (String) config.get("logTable");
                if (StringUtils.isNotEmpty(tableName)) {
                    logger.debug("record order to table:{}...", tableName);
                    logOrder(tableName, request);
                }
            } else if (request.getPayResult() != null) {// 微信支付事件
                String tableName = (String) config.get("logTable");
                if (StringUtils.isNotEmpty(tableName)) {
                    logger.debug("record payment notification to table:{}...", tableName);
                    logPaymentResult(tableName, request);
                }
            }
        }

        // 处理事件
        eventStep:
        if (eventFlag) {
            String commandKey = (String) config.get("commandKey");
            if (StringUtils.isEmpty(commandKey)) {
                logger.debug("commandKey is empty");
                return null;
            }

            Integer paramType = (Integer) config.get("paramType");
            String paramScript = (String) config.get("paramScript");
            Map<String, Object> params = null;
            if (StringUtils.isNotEmpty(paramScript)) {
                Object o = ScriptHelper.evel(ScriptTypes.forCode(paramType), paramScript);
                if (o != null) {
                    if (o instanceof Map) {
                        params = (Map<String, Object>) o;
                    } else {
                        params = JsonMapper.defaultMapper().json2Map(o.toString());
                    }
                }
            }

            //内置command
            if(isSystemCommand(commandKey)) {
                logger.info("系统内置command:{}", commandKey);
                MpCommand systemCommand = WxAnnotatedCommandsHolder.getInstance().getMpCommandInstanceByClassName(commandKey);
                if(params != null) {
                    request.getAttrs().putAll(params);
                }
                return systemCommand.execute(request);
            }

            // 消息模式下特殊有多一层类型拦截
            if (request.isMessage()) {
                String[] eventType = StringUtils.split((String) config.get("eventType"), ";");
                if (eventType == null || eventType.length < 1) {// 没有勾选拦截类型,不拦截
                    break eventStep;
                }
                if (request.getText() != null && !ArrayUtils.contains(eventType, WxSendDataType.TEXT.name())) {
                    break eventStep;
                } else if (request.getImage() != null && !ArrayUtils.contains(eventType, WxSendDataType.IMAGE.name())) {
                    break eventStep;
                } else if (request.getShortVideo() != null && !ArrayUtils.contains(eventType, WxSendDataType.SHORT_VIDEO.name())) {
                    break eventStep;
                } else if (request.getVideo() != null && !ArrayUtils.contains(eventType, WxSendDataType.VIDEO.name())) {
                    break eventStep;
                } else if (request.getVoice() != null && !ArrayUtils.contains(eventType, WxSendDataType.VOICE.name())) {
                    break eventStep;
                } else if (request.getLocation() != null && !ArrayUtils.contains(eventType, WxSendDataType.LOCATION.name())) {
                    break eventStep;
                } else if (request.getLocation() != null && !ArrayUtils.contains(eventType, WxSendDataType.LINK.name())) {
                    break eventStep;
                }
            }

            //自定义command
            Map<String, Object> commandConfig = (Map<String, Object>) ORMService.getInstance().findByPk("WxCommand", commandKey);
            if(commandConfig != null && !commandConfig.isEmpty()) {
                logger.debug("command {} is present.", commandKey);

                Map<String, Object> context = new HashMap<>();
                context.put("mp", new MpHelper(request.getMpKey()));
                context.put("media", new MpMediaHelper(request.getMpKey()));
                context.put("wo", request);
                if (params != null) {
                    context.putAll(params);
                }

                Integer logicType = (Integer) commandConfig.get("logicType");
                String logicScript = (String) commandConfig.get("logicScript");
                Object o = CommandExecutionService.getInstance().executeCommand(ScriptTypes.forCode(logicType), logicScript, context);

                Map<String, Object> map = null;
                if (o != null) {
                    if (o instanceof Map) {
                        logger.debug("response is map: {}", JsonMapper.defaultMapper().toJson(o));
                        map = (Map<String, Object>) o;
                    } else if (o instanceof String) {//如果是string 则默认为text消息
                        logger.debug("response is string:", o);
                        map = new HashMap<>();
                        map.put("text", o);
                    } else {
                        logger.debug("response is not map or string.", JsonMapper.defaultMapper().toJson(o));
                    }
                } else {
                    logger.debug("o == null");
                }

                if (map != null) {
                    MpResponse mpResponse = new MpResponse(map);
                    return mpResponse;
                }

            }
        }

        return null;
    }

    private boolean isSystemCommand(String commandKey) {
        return commandKey.startsWith("com.riversoft");
    }

    /**
     * 登记订单信息
     *
     * @param tableName
     * @param request
     */
    private void logOrder(String tableName, MpRequest request) {
        StringBuffer sql = new StringBuffer();
        sql.append("insert into ").append(tableName).append(" (");
        sql.append(OrderModelKeys.MP_KEY.name()).append(",");
        sql.append(OrderModelKeys.OPEN_ID.name()).append(",");
        sql.append(OrderModelKeys.LOG_DATE.name()).append(",");

        sql.append(OrderModelKeys.NICK_NAME.name()).append(",");

        sql.append(OrderModelKeys.ORDER_ID.name()).append(",");
        sql.append(OrderModelKeys.ORDER_STATUS.name()).append(",");

        sql.append(OrderModelKeys.TOTAL_PRICE.name()).append(",");
        sql.append(OrderModelKeys.DELIVERY_PRICE.name()).append(",");

        sql.append(OrderModelKeys.CREATED_DATE.name()).append(",");

        sql.append(OrderModelKeys.PRODUCT_ID.name()).append(",");
        sql.append(OrderModelKeys.PRODUCT_NAME.name()).append(",");
        sql.append(OrderModelKeys.PRODUCT_PRICE.name()).append(",");
        sql.append(OrderModelKeys.PRODUCT_COUNT.name()).append(",");
        sql.append(OrderModelKeys.PRODUCT_SKU.name()).append(",");
        sql.append(OrderModelKeys.PRODUCT_IMG.name()).append(",");

        sql.append(OrderModelKeys.RECEIVER_NAME.name()).append(",");
        sql.append(OrderModelKeys.RECEIVER_PROVINCE.name()).append(",");
        sql.append(OrderModelKeys.RECEIVER_CITY.name()).append(",");
        sql.append(OrderModelKeys.RECEIVER_ZONE.name()).append(",");
        sql.append(OrderModelKeys.RECEIVER_ADDRESS.name()).append(",");
        sql.append(OrderModelKeys.RECEIVER_MOBILE.name()).append(",");
        sql.append(OrderModelKeys.RECEIVER_PHONE.name()).append(",");

        sql.append(OrderModelKeys.DELIVERY_ID.name()).append(",");
        sql.append(OrderModelKeys.DELIVERY_COMPANY.name()).append(",");

        sql.append(OrderModelKeys.TRANS_ID.name());
        sql.append(") values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");

        String orderId = request.getOrderPay().getOrderId();
        MpAppSetting appSetting = MpAppService.getInstance().getAppSettingByPK(request.getMpKey());
        Order order = Orders.with(appSetting).get(orderId);

        JdbcService.getInstance().executeSQL(sql.toString(), request.getMpKey(), request.getOpenId(), new Date(),
                order.getBuyerNickName(), orderId, request.getOrderPay().getOrderStatus(), order.getTotalPrice(),
                order.getExpressPrice(), order.getCreated(), order.getProductId(), order.getProductName(),
                order.getProductPrice(), order.getProductCount(), order.getProductSku(), order.getProductImage(),
                order.getReceiverName(), order.getReceiverProvince(), order.getReceiverCity(), order.getReceiverZone(),
                order.getReceiverAddress(), order.getReceiverMobile(), order.getReceiverPhone(), order.getDeliveryId(),
                order.getDeliveryCompany(), order.getTransactionId());
    }

    /**
     * 登记对话框消息
     *
     * @param tableName
     * @param request
     */
    private void logMessage(String tableName, MpRequest request) {
        MpMediaHelper mpMediaHelper = new MpMediaHelper(request.getMpKey());
        StringBuffer sql = new StringBuffer();
        sql.append("insert into ").append(tableName).append(" (");
        sql.append(MessageLogModelKeys.MP_KEY.name()).append(",");
        sql.append(MessageLogModelKeys.OPEN_ID.name()).append(",");
        sql.append(MessageLogModelKeys.LOG_DATE.name()).append(",");

        sql.append(MessageLogModelKeys.MSG_TYPE.name()).append(",");
        sql.append(MessageLogModelKeys.MSG_TEXT.name()).append(",");
        sql.append(MessageLogModelKeys.MSG_ATTACHMENT.name()).append(",");
        sql.append(MessageLogModelKeys.MSG_X.name()).append(",");
        sql.append(MessageLogModelKeys.MSG_Y.name()).append(",");
        sql.append(MessageLogModelKeys.MSG_SCALE.name()).append(",");
        sql.append(MessageLogModelKeys.MSG_LABEL.name());

        sql.append(") values (?,?,?,?,?,?,?,?,?,?)");

        String type;
        String text = null;
        byte[] attachment = null;
        String x = null;
        String y = null;
        String scale = null;
        String label = null;

        if (request.getText() != null) {
            type = WxSendDataType.TEXT.name();
            text = request.getText();
        } else if (request.getImage() != null) {
            type = WxSendDataType.IMAGE.name();
            text = request.getImage().getMediaId();
            attachment = mpMediaHelper.download(text);
        } else if (request.getVoice() != null) {
            type = WxSendDataType.VOICE.name();
            text = request.getVoice().getMediaId();
            attachment = mpMediaHelper.download(text);
        } else if (request.getShortVideo() != null) {
            type = WxSendDataType.SHORT_VIDEO.name();
            text = request.getShortVideo().getMediaId();
            attachment = mpMediaHelper.download(text);
        } else if (request.getVideo() != null) {
            type = WxSendDataType.VIDEO.name();
            text = request.getVideo().getMediaId();
            attachment = mpMediaHelper.download(text);
        } else if (request.getLocation() != null) {
            type = WxSendDataType.LOCATION.name();
            x = request.getLocation().getX();
            y = request.getLocation().getY();
            scale = request.getLocation().getScale();
            label = request.getLocation().getLabel();
        } else if (request.getLink() != null) {
            type = WxSendDataType.LINK.name();
            text = request.getLink().getUrl();//暂时只存URL
        } else {
            return;
        }

        JdbcService.getInstance().executeSQL(sql.toString(), request.getMpKey(), request.getOpenId(), new Date(), type, text, attachment, x, y, scale, label);
    }

    /**
     * 登记地理位置上报
     *
     * @param tableName
     * @param request
     */
    private void logLocationReport(String tableName, MpRequest request) {
        Location location = request.getLocation();
        if (location == null) {
            return;
        }

        StringBuffer sql = new StringBuffer();
        sql.append("insert into ").append(tableName).append(" (");
        sql.append(LocationReportLogModelKeys.MP_KEY.name()).append(",");
        sql.append(LocationReportLogModelKeys.OPEN_ID.name()).append(",");
        sql.append(LocationReportLogModelKeys.LOG_DATE.name()).append(",");
        sql.append(LocationReportLogModelKeys.X.name()).append(",");
        sql.append(LocationReportLogModelKeys.Y.name()).append(",");
        sql.append(LocationReportLogModelKeys.SCALE.name()).append(",");
        sql.append(LocationReportLogModelKeys.LABEL.name());
        sql.append(") values (?,?,?,?,?,?,?)");
        JdbcService.getInstance().executeSQL(sql.toString(), request.getMpKey(), request.getOpenId(), new Date(), location.getX(), location.getY(), location.getScale(),
                location.getLabel());

    }

    /**
     * 微信支付结果通知记录到表
     * @param tableName
     * @param request
     */
    private void logPaymentResult(String tableName, MpRequest request) {
        Map<String, Object> o = new DataPO(tableName).toEntity();
        o.put(PaymentNotificationModelKeys.MP_KEY.name(), request.getMpKey());
        PayResult payResult = request.getPayResult();
        o.put(PaymentNotificationModelKeys.APP_ID.name(), payResult.getAppId());
        o.put(PaymentNotificationModelKeys.MCH_ID.name(), payResult.getMchId());
        o.put(PaymentNotificationModelKeys.DEVICE_INFO.name(), payResult.getDeviceInfo());
        o.put(PaymentNotificationModelKeys.TRANSACTION_ID.name(), payResult.getTransactionId());
        o.put(PaymentNotificationModelKeys.TRADE_NUMBER.name(), payResult.getTradeNumber());
        o.put(PaymentNotificationModelKeys.TRADE_TYPE.name(), payResult.getTradeType());
        o.put(PaymentNotificationModelKeys.BANK_TYPE.name(), payResult.getBankType());
        o.put(PaymentNotificationModelKeys.OPEN_ID.name(), payResult.getOpenId());
        o.put(PaymentNotificationModelKeys.RETURN_CODE.name(), payResult.getReturnCode());
        o.put(PaymentNotificationModelKeys.RETURN_MSG.name(), payResult.getReturnMessage());
        o.put(PaymentNotificationModelKeys.RESULT_CODE.name(), payResult.getResultCode());
        o.put(PaymentNotificationModelKeys.ERROR_CODE.name(), payResult.getErrorCode());
        o.put(PaymentNotificationModelKeys.ERROR_CODE_DESC.name(), payResult.getErrorCodeDesc());
        o.put(PaymentNotificationModelKeys.TOTAL_FEE.name(), payResult.getTotalFee());
        o.put(PaymentNotificationModelKeys.SETTLE_TOTAL_FEE.name(), payResult.getSettlementTotalFee());
        o.put(PaymentNotificationModelKeys.CASH_FEE.name(), payResult.getCashFee());
        o.put(PaymentNotificationModelKeys.COUPON_FEE.name(), payResult.getCouponFee());
        o.put(PaymentNotificationModelKeys.COUPON_COUNT.name(), payResult.getCouponFeeCount());
        o.put(PaymentNotificationModelKeys.ATTACH.name(), payResult.getAttach());
        o.put(PaymentNotificationModelKeys.END_TIME.name(), payResult.getTimeEnd());
        o.put(PaymentNotificationModelKeys.CREATED_TIME.name(), new Date());
        o.put(PaymentNotificationModelKeys.SUBSCRIBED.name(), payResult.subscribed() ? 1 : 0);
        ORMAdapterService.getInstance().save(o);
    }

}
