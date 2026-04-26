/*
 * $HeadURL: $
 * $Id: $
 * Copyright (c) 2013 by Riversoft System, all rights reserved.
 */
package com.riversoft.module.widget;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.riversoft.core.web.Actions;
import com.riversoft.core.web.Actions.Util;
import com.riversoft.core.web.RequestUtils;
import com.riversoft.platform.web.StyleManager;

/**
 * 样式设计器
 * 
 * @author woden
 * 
 */
public class StyleAction {

    /**
     * 样式设计器首页
     * 
     * @param request
     * @param response
     */
    public void index(HttpServletRequest request, HttpServletResponse response) {
        String style = RequestUtils.getStringValue(request, "style");
        request.setAttribute("style", style);
        request.setAttribute("vo", new StyleManager.Style(style).getMap());
        Actions.includePage(request, response, Util.getPagePath(request, "style_design.jsp"));
    }

    /**
     * 将表单值转换成json
     * 
     * @param request
     * @param response
     */
    public void json(HttpServletRequest request, HttpServletResponse response) {
        String style = RequestUtils.getStringValue(request, "style");
        Map<String, String> result = new HashMap<>();
        result.put("result", new StyleManager.Style(style).toCss());
        Actions.showJson(request, response, result);
    }
}
