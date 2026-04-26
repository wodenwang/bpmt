/*
 * $HeadURL: $
 * $Id: $
 * Copyright (c) 2013 by Riversoft System, all rights reserved.
 */
package com.riversoft.platform.web;

import java.io.IOException;
import java.util.Map;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.riversoft.platform.script.ScriptHelper;
import com.riversoft.platform.script.ScriptTypes;

/**
 * 展示类的脚本标签
 * 
 * @author woden
 * 
 */
@SuppressWarnings("serial")
public class ScriptTag extends TagSupport {
    /**
     * Logger for this class
     */
    private static final Logger logger = LoggerFactory.getLogger(ScriptTag.class);

    private Integer type;
    private String script;
    private Map<String, Object> context;

    public int doStartTag() throws JspException {
        try {
            ScriptTypes scriptTypes = ScriptTypes.forCode(type);
            Object value = ScriptHelper.evel(scriptTypes, script, context);
            pageContext.getOut().append(value != null ? value.toString() : "");
        } catch (Exception e) {
            logger.error("执行脚本[" + script + "]出错.", e);
            try {
                pageContext.getOut().append(ExceptionUtils.getRootCauseMessage(e));
            } catch (IOException ignore) {
                // do nothing
                logger.error("", ignore);
            }
        }
        return SKIP_BODY;
    }

    /**
     * @param type the type to set
     */
    public void setType(Integer type) {
        this.type = type;
    }

    /**
     * @param script the script to set
     */
    public void setScript(String script) {
        this.script = script;
    }

    /**
     * @param context the context to set
     */
    public void setContext(Map<String, Object> context) {
        this.context = context;
    }

}
