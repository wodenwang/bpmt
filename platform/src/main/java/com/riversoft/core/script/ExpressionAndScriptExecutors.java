/*
 * $HeadURL: $
 * $Id: $
 * Copyright (c) 2012 by Riversoft System, all rights reserved.
 */
package com.riversoft.core.script;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Borball
 * 
 */
public class ExpressionAndScriptExecutors {

    Logger logger = LoggerFactory.getLogger(ExpressionAndScriptExecutors.class);

    private ExprLangExecutor elExecutor;
    private ScriptExecutor groovyExecutor;
    private ScriptExecutor jsr223Executor;
    
    public Object evaluateEL(String el, ScriptExecutionContext context) {
        //logger.debug("准备执行expression language:" + el);
        return elExecutor.evaluateEL(el, context);
    }

    public Object evaluateScript(ScriptType scriptType, String script, ScriptExecutionContext context) {
        //logger.debug("准备执行script:" + scriptType.toString() + ":\n" + script);

        switch (scriptType) {
        case JSR223:
            return jsr223Executor.evaluateScript(script, context);
        case GROOVY:
            return groovyExecutor.evaluateScript(script, context);
        default:
            return jsr223Executor.evaluateScript(script, context);
        }

    }

    public void setElExecutor(ExprLangExecutor elExecutor) {
        this.elExecutor = elExecutor;
    }

    public void setGroovyExecutor(ScriptExecutor groovyExecutor) {
        this.groovyExecutor = groovyExecutor;
    }

    public void setJsr223Executor(ScriptExecutor jsr223Executor) {
        this.jsr223Executor = jsr223Executor;
    }
}
