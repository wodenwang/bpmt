/*
 * $HeadURL: $
 * $Id: $
 * Copyright (c) 2013 by Riversoft System, all rights reserved.
 */
package com.riversoft.core.web.widget;

import java.util.Map;

/**
 * 表单控件逻辑处理器
 * 
 * @author Woden
 */
public interface Widget {

    /**
     * 处理设置到ftl中的数据
     * 
     * @param map
     */
    public void prepareMap(Map<String, Object> map);

    /**
     * 处理控件参数<br>
     * 传入命令中，[]里面的值
     * 
     * @param values
     */
    public void setParams(FormValue... values);

    /**
     * 控件数据展示(code -> showName)
     * 
     * @param value
     * @return
     */
    public String show(Object value);

    /**
     * 控件数据转换(showName -> code)
     * 
     * @param showName
     * @return
     */
    public Object code(String showName);

}
