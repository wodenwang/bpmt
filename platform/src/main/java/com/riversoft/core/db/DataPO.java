/*
 * $HeadURL: $
 * $Id: $
 * Copyright (c) 2013 by Riversoft System, all rights reserved.
 */
package com.riversoft.core.db;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * 动态PO代理.
 * 
 * @author Woden
 * 
 */
public class DataPO {

    private static final String ENTITY_NAME_KEY = "$type$";

    private Map<String, Object> po;

    public String getEntityName() {
        return getString(ENTITY_NAME_KEY);
    }

    public DataPO(String entityName) {
        this(entityName, new HashMap<String, Object>());
    }

    public DataPO(String entityName, Map<String, Object> po) {
        this.po = po;
        this.po.put(ENTITY_NAME_KEY, entityName);
    }

    public void set(String name, Object value) {
        po.put(name, value);
    }

    /**
     * 一对多关系仲的<code>Map</code>结构.
     * 
     * @param entityName
     * @param name
     * @param value
     */
    public void addSubEntity(String entityName, String index, Map<String, Object> value) {
        if (!po.containsKey(entityName)) {
            po.put(entityName, new HashMap<String, Map<String, Map<String, Object>>>());
        }
        Map<String, Map<String, Object>> subs = (Map<String, Map<String, Object>>) getSubEntitys(entityName);
        subs.put((String) value.get(index), value);
    }

    /**
     * 一对多关系<code>List</code>结构.
     * 
     * @param entityName
     * @param value
     */
    public void addSubItem(String entityName, Map<String, Object> value) {
        if (!po.containsKey(entityName)) {
            po.put(entityName, new HashSet<Map<String, Object>>());
        }
        Set<Map<String, Object>> subs = (Set<Map<String, Object>>) getSubList(entityName);
        subs.add(value);
    }

    public Object get(String name) {
        return po.get(name);
    }

    public String getString(String name) {
        return (String) get(name);
    }

    public Long getLong(String name) {
        return (Long) get(name);
    }

    public Integer getIntger(String name) {
        return (Integer) get(name);
    }

    public Date getDate(String name) {
        return (Date) get(name);
    }

    public BigDecimal getMoney(String name) {
        return (BigDecimal) get(name);
    }

    @SuppressWarnings("unchecked")
    public Map<String, Map<String, Object>> getSubEntitys(String name) {
        return (Map<String, Map<String, Object>>) get(name);
    }

    @SuppressWarnings("unchecked")
    public Set<Map<String, Object>> getSubList(String name) {
        return (Set<Map<String, Object>>) get(name);
    }

    /**
     * 将动态PO包装类转换成MAP(直接放到dao层处理).
     * 
     * @return 动态对象<code>Map</code>.
     */
    public final Map<String, Object> toEntity() {
        return po;
    }
}
