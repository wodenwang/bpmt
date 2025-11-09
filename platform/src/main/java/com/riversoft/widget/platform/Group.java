/*
 * $HeadURL: $
 * $Id: $
 * Copyright (c) 2014 by Riversoft System, all rights reserved.
 */
package com.riversoft.widget.platform;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.riversoft.core.context.RequestContext;
import com.riversoft.core.web.Actions.Keys;
import com.riversoft.core.web.widget.FormValue;
import com.riversoft.core.web.widget.Widget;
import com.riversoft.core.web.widget.WidgetAnnotation;
import com.riversoft.platform.db.Code2NameService;
import com.riversoft.platform.po.UsGroup;

/**
 * @author woden
 * 
 */
@WidgetAnnotation(cmd = "group", ftl = "classpath:widget/{mode}/platform/group.ftl")
public class Group implements Widget {

	@Override
	public void prepareMap(Map<String, Object> map) {
		RequestContext request = RequestContext.getCurrent();
		if (StringUtils.equals("h5", request.getString(Keys.ACTION_MODE.toString()))) {
			List<?> list = Code2NameService.getInstance().getListORM(UsGroup.class.getName(), "groupKey", "busiName");
			map.put("list", list);
		}
	}

	@Override
	public void setParams(FormValue... values) {
	}

	@Override
	public String show(Object value) {
		return (String) Code2NameService.getInstance().translateORM(UsGroup.class.getName(), "groupKey", "busiName",
				value);
	}

	@Override
	public Object code(String showName) {
		return "";
	}

}
