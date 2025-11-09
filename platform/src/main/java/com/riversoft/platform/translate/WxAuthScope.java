package com.riversoft.platform.translate;

import com.riversoft.core.db.po.Code2NameVO;

/**
 * Created by exizhai on 12/27/2015.
 */
public enum WxAuthScope implements Code2NameVO {

    SNS_BASE(0, "不弹出授权页面"), SNS_USERINFO(1, "弹出授权页面");

    private int code;
    private String showName;

    WxAuthScope(int code, String showName) {
        this.code = code;
        this.showName = showName;
    }

    @Override
    public Object getCode() {
        return code;
    }

    @Override
    public String getShowName() {
        return showName;
    }
}
