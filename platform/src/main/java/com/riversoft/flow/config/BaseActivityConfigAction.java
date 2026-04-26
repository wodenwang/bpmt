/*
 * $HeadURL: $
 * $Id: $
 * Copyright (c) 2013 by Riversoft System, all rights reserved.
 */
package com.riversoft.flow.config;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.riversoft.core.web.RequestUtils;

/**
 * 节点设置Action
 * 
 * @author woden
 * 
 */
public abstract class BaseActivityConfigAction {

    /**
     * 节点配置首页
     * 
     * @param request
     * @param response
     */
    public void index(HttpServletRequest request, HttpServletResponse response) {
        String pdId = RequestUtils.getStringValue(request, "pdId");
        String activityId = RequestUtils.getStringValue(request, "activityId");
        main(request, response, pdId, activityId);
    }

    /**
     * 节点配置页
     * 
     * @param request
     * @param response
     * @param pdId
     * @param activityId
     */
    public abstract void main(HttpServletRequest request, HttpServletResponse response, String pdId, String activityId);

}
