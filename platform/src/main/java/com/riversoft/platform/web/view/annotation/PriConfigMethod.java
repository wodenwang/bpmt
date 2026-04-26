/*
 * $HeadURL: $
 * $Id: $
 * Copyright (c) 2013 by Wodensoft System, all rights reserved.
 */
package com.riversoft.platform.web.view.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 权限设置的action方法
 * 
 * @author Woden
 * 
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE })
public @interface PriConfigMethod {
    /**
     * 权限设置的方法名
     * 
     * @return
     */
    String value();
}
