package com.riversoft.wx.mp.command;

import com.riversoft.wx.context.Link;
import com.riversoft.wx.context.Request;
import com.riversoft.wx.context.Scene;
import com.riversoft.wx.mp.context.OrderPay;
import com.riversoft.wx.mp.context.PayResult;

/**
 * Created by exizhai on 12/8/2015.
 */
public class MpRequest extends Request {

    /**
     * 用户
     */
    private String openId;

    /**
     * 应用
     */
    private String mpKey;
    private String appId;

    /**
     * 各种事件回调
     */
    //用户再次扫码
    private boolean sceneScan;

    /**
     * 模板消息发送结果通知
     */
    private boolean templateMsgCompleted;

    private OrderPay orderPay;
    /**
     * 各种数据
     */
    //用户发送链接
    private Link link;

    private Scene scene;

    private PayResult payResult;

    public String getOpenId() {
        return openId;
    }

    public void setOpenId(String openId) {
        this.openId = openId;
    }

    public String getMpKey() {
        return mpKey;
    }

    public void setMpKey(String mpKey) {
        this.mpKey = mpKey;
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public boolean isSceneScan() {
        return sceneScan;
    }

    public void setSceneScan(boolean sceneScan) {
        this.sceneScan = sceneScan;
    }

    public Link getLink() {
        return link;
    }

    public void setLink(Link link) {
        this.link = link;
    }

    public Scene getScene() {
        return scene;
    }

    public void setScene(Scene scene) {
        this.scene = scene;
    }

    public boolean isTemplateMsgCompleted() {
        return templateMsgCompleted;
    }

    public void setTemplateMsgCompleted(boolean templateMsgCompleted) {
        this.templateMsgCompleted = templateMsgCompleted;
    }

    public OrderPay getOrderPay() {
        return orderPay;
    }

    public void setOrderPay(OrderPay orderPay) {
        this.orderPay = orderPay;
    }

    public PayResult getPayResult() {
        return payResult;
    }

    public void setPayResult(PayResult payResult) {
        this.payResult = payResult;
    }
}
