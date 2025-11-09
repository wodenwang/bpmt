/*
 * $HeadURL: $
 * $Id: $
 * Copyright (c) 2012 by Riversoft System, all rights reserved.
 */
package com.riversoft.core.script;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * 用在web环境的上下文
 * @author Borball
 * 
 */
public class HTTPExecutionContext implements ScriptExecutionContext {

    private Map<String, Object> variables;

    public HTTPExecutionContext(HttpServletRequest request) {
        variables = new HashMap<String, Object>();

        readContextFromSession(request.getSession());
        
        readContextFromRequest(request);
    }

    private void readContextFromRequest(HttpServletRequest request) {
        Enumeration<String> attributesInRequest = request.getAttributeNames();
        while (attributesInRequest.hasMoreElements()) {
            String name = attributesInRequest.nextElement();
            Object value = request.getAttribute(name);
            variables.put(name, value);
        }
    }

    private void readContextFromSession(HttpSession session) {
        Enumeration<String> attributesInSession = session.getAttributeNames();
        while (attributesInSession.hasMoreElements()) {
            String name = attributesInSession.nextElement();
            Object value = session.getAttribute(name);

            variables.put(name, value);
        }
    }

    @Override
    public Map<String, Object> getVariableContext() {
        return variables;
    }

}
