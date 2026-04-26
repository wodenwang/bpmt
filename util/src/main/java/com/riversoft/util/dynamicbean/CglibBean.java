/*
 * $HeadURL: $
 * $Id: $
 * Copyright (c) 2013 by Riversoft System, all rights reserved.
 */
package com.riversoft.util.dynamicbean;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import net.sf.cglib.beans.BeanGenerator;
import net.sf.cglib.beans.BeanMap;

/**
 * 将HashMap转换为实体bean
 * 
 * @author Woden
 * 
 */
public class CglibBean {
    /**
     * 实体Object
     */
    public Object object = null;

    /**
     * 属性map
     */
    public BeanMap beanMap = null;

    public CglibBean() {
        super();
    }

    @SuppressWarnings("rawtypes")
    public CglibBean(Map<String, Class> propertyMap) {
        this.object = generateBean(propertyMap);
        this.beanMap = BeanMap.create(this.object);
    }

    /**
     * 给bean属性赋值
     * 
     * @param property 属性名
     * @param value 值
     */
    public void setValue(String property, Object value) {
        beanMap.put(property, value);
    }

    /**
     * 通过属性名得到属性值
     * 
     * @param property 属性名
     * @return 值
     */
    public Object getValue(String property) {
        return beanMap.get(property);
    }

    /**
     * 得到该实体bean对象
     * 
     * @return
     */
    public Object getObject() {
        return this.object;
    }

    @SuppressWarnings("rawtypes")
    private Object generateBean(Map<String, Class> propertyMap) {
        BeanGenerator generator = new BeanGenerator();
        Set<String> keySet = propertyMap.keySet();
        for (Iterator<String> i = keySet.iterator(); i.hasNext();) {
            String key = i.next();
            generator.addProperty(key, propertyMap.get(key));
        }
        return generator.create();
    }
}
