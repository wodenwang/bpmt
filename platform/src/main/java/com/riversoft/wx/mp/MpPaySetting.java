package com.riversoft.wx.mp;

import com.riversoft.weixin.pay.base.PaySetting;

/**
 * Created by exizhai on 1/2/2016.
 */
public class MpPaySetting extends PaySetting {

    private String mpKey;
    private String payResultTable;

    public String getMpKey() {
        return mpKey;
    }

    public void setMpKey(String mpKey) {
        this.mpKey = mpKey;
    }

    public String getPayResultTable() {
        return payResultTable;
    }

    public void setPayResultTable(String payResultTable) {
        this.payResultTable = payResultTable;
    }
}
