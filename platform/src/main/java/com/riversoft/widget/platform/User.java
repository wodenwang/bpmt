/*
 * $HeadURL: $
 * $Id: $
 * Copyright (c) 2013 by Riversoft System, all rights reserved.
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
import com.riversoft.platform.po.UsUser;

/**
 * 用户选择器
 * 
 * @author woden
 * 
 */
@WidgetAnnotation(cmd = "user", ftl = "classpath:widget/{mode}/platform/user.ftl")
public class User implements Widget {

	private boolean codeFlag = false;// 是否展示账号

	@Override
	public void prepareMap(Map<String, Object> map) {
		map.put("codeFlag", codeFlag ? 1 : 0);

		RequestContext request = RequestContext.getCurrent();
		if (StringUtils.equals("h5", request.getString(Keys.ACTION_MODE.toString()))) {
			List<?> list = Code2NameService.getInstance().getListORM(UsUser.class.getName(), "uid", "busiName");
			map.put("list", list);
		}
	}

	@Override
	public void setParams(FormValue... values) {
		if (values.length > 0) {
			codeFlag = "true".equalsIgnoreCase(values[0].getName());
		}
	}

	@Override
	public String show(Object value) {
		String showValue = (String) Code2NameService.getInstance().translateORM(UsUser.class.getName(), "uid", "busiName", value);
		if (codeFlag) {
			return "[" + value + "]" + showValue;
		} else {
			return showValue;
		}
	}

	@Override
	public Object code(String showName) {
		return "";
	}

}
