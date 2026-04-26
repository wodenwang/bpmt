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

import com.riversoft.core.IDGenerator;
import com.riversoft.core.web.Actions;

/**
 * @author Woden
 * 
 */
public class KeyAction {

    /**
     * 创建自动url
     * 
     * @param request
     * @param response
     */
    public void create(HttpServletRequest request, HttpServletResponse response) {

        Map<String, String> result = new HashMap<String, String>();
        result.put("result", IDGenerator.next());
        Actions.showJson(request, response, result);
    }
}
