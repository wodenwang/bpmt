/*
 * $HeadURL: $
 * $Id: $
 * Copyright (c) 2015 by Riversoft System, all rights reserved.
 */
package com.riversoft.widget.platform;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.riversoft.core.web.widget.WidgetAnnotation;
import com.riversoft.platform.db.Code2NameService;
import com.riversoft.platform.po.UsUser;

/**
 * 多选用户
 * 
 * @author woden
 * 
 */
@WidgetAnnotation(cmd = "multiuser", ftl = "classpath:widget/{mode}/platform/multiuser.ftl")
public class MultiUser extends User {
	@Override
	public String show(Object value) {
		if (StringUtils.isEmpty((String) value)) {
			return "";
		}

		List<String> array = new ArrayList<>();
		String[] vals = value.toString().split(";");
		for (String val : vals) {
			// 遍历翻译
			array.add((String) Code2NameService.getInstance().translateORM(UsUser.class.getName(), "uid", "busiName",
					val));
		}
		return StringUtils.join(array, ";");
	}
}
