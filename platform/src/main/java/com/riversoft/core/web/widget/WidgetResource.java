/*
 * $HeadURL: $
 * $Id: $
 * Copyright (c) 2016 by Riversoft System, all rights reserved.
 */
package com.riversoft.core.web.widget;

import java.io.InputStream;

/**
 * 控件资源继承
 * 
 * @author woden
 *
 */
public interface WidgetResource {
	/**
	 * 获取ftl模板
	 * 
	 * @return
	 */
	public InputStream getFtl();
}
