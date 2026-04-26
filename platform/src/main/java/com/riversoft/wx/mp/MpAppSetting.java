package com.riversoft.wx.mp;

import com.riversoft.weixin.mp.base.AppSetting;

/**
 * Created by exizhai on 12/13/2015.
 */
public class MpAppSetting extends AppSetting {

    private String mpKey;

    private String scope;

    public String getMpKey() {
        return mpKey;
    }

    public void setMpKey(String mpKey) {
        this.mpKey = mpKey;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

}
