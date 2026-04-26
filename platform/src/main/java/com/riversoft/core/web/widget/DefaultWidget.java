/*
 * $HeadURL: $
 * $Id: $
 * Copyright (c) 2013 by Riversoft System, all rights reserved.
 */
package com.riversoft.core.web.widget;

import java.util.Map;

/**
 * 默认数据处理器
 * 
 * @author Woden
 * 
 */
public class DefaultWidget implements Widget {

    @Override
    public void setParams(FormValue... values) {
        // do nothing
    }

    @Override
    public String show(Object value) {
        return value.toString();
    }

    @Override
    public Object code(String showName) {
        return showName;
    }

    @Override
    public void prepareMap(Map<String, Object> map) {
        // do nothing
    }
}
