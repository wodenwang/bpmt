/*
 * $HeadURL: $
 * $Id: $
 * Copyright (c) 2012 by Riversoft System, all rights reserved.
 */
package com.riversoft.core.script;

import java.util.HashMap;
import java.util.Map;

import com.riversoft.core.script.ScriptExecutionContext;

/**
 * @author Borball
 * 
 */
public class MockHttpExecutionContext implements ScriptExecutionContext {

    private Map<String, Object> variables;

    MockHttpExecutionContext(User user) {
        variables = new HashMap<String, Object>();
        variables.put("user", user);

    }

    @Override
    public Map<String, Object> getVariableContext() {
        return variables;
    }

}
