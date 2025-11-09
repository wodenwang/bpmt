package com.riversoft.core.db.po;

import java.util.HashMap;
import java.util.Map;

/**
 * 扩展(纵表)模式实体类基类。
 * 
 * @author Woden
 * 
 * @param <T>
 */
@SuppressWarnings("serial")
public abstract class BaseExtendItem<T extends BaseVarItem, E extends BaseRuleItem> extends BaseItem {
    private Map<String, T> vars = new HashMap<String, T>();

    public Map<String, T> getVars() {
        return vars;
    }

    public void setVars(Map<String, T> vars) {
        this.vars = vars;
    }
}
