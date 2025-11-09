package com.riversoft.wx.mp.context;

import com.riversoft.weixin.pay.payment.bean.PaymentNotification;

/**
 * @borball on 6/4/2016.
 */
public class PayResult extends PaymentNotification{

    private boolean mpPay;
    private boolean scanPay;

    public boolean isMpPay() {
        return mpPay;
    }

    public void setMpPay(boolean mpPay) {
        this.mpPay = mpPay;
    }

    public boolean isScanPay() {
        return scanPay;
    }

    public void setScanPay(boolean scanPay) {
        this.scanPay = scanPay;
    }
}
