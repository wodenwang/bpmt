package com.riversoft.platform.po;

import com.riversoft.core.db.po.BaseItem;

/**
 * Created by exizhai on 2/2/2016.
 */
public class UsTag extends BaseItem {

    private String tagKey;
    private String busiName;
    private Integer wxTagId;

    public String getTagKey() {
        return tagKey;
    }

    public void setTagKey(String tagKey) {
        this.tagKey = tagKey;
    }

    public String getBusiName() {
        return busiName;
    }

    public void setBusiName(String busiName) {
        this.busiName = busiName;
    }

    public Integer getWxTagId() {
        return wxTagId;
    }

    public void setWxTagId(Integer wxTagId) {
        this.wxTagId = wxTagId;
    }
}
