/*
 * $HeadURL: $
 * $Id: $
 * Copyright (c) 2013 by Riversoft System, all rights reserved.
 */
package com.riversoft.core.web.widget;

/**
 * 参数对象<br>
 * name(param1,param2...)
 * 
 * @author Woden
 * 
 */
public final class FormValue {

    private String name;

    private String param;

    FormValue(String cmd) {
        if (cmd == null || cmd.equalsIgnoreCase("null")) {
            return;
        }

        if (cmd.lastIndexOf("(") > 0 && cmd.lastIndexOf(")") > 0 && cmd.endsWith(")")) {
            this.name = cmd.substring(0, cmd.lastIndexOf("("));
            this.param = cmd.substring(cmd.lastIndexOf("(") + 1, cmd.lastIndexOf(")"));
        } else {
            this.name = cmd;
        }
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @return the param
     */
    public String getParam() {
        return param;
    }
}
