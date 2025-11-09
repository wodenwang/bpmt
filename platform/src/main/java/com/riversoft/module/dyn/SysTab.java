package com.riversoft.module.dyn;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by exizhai on 04/03/2015.
 */
public enum SysTab {

	log("操作日志");

    private String busiName;

    private SysTab(String busiName) {
        this.busiName = busiName;
    }

    public String getBusiName() {
        return busiName;
    }

    public String getName() {
        return this.name();
    }

    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("name", getName());
        map.put("busiName", busiName);
        return map;
    }
}
