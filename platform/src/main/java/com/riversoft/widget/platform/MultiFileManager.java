/*
 * $HeadURL: $
 * $Id: $
 * Copyright (c) 2013 by Riversoft System, all rights reserved.
 */
package com.riversoft.widget.platform;

import java.util.Map;

import com.riversoft.core.web.widget.WidgetAnnotation;

/**
 * @author woden
 * 
 */
@WidgetAnnotation(cmd = "multifilemanager", ftl = "classpath:widget/{mode}/platform/filemanager.ftl")
public class MultiFileManager extends FileManagerWidget {

	@Override
	public void prepareMap(Map<String, Object> map) {
		super.prepareMap(map);
		map.put("multiFlag", "true");
	}
}
