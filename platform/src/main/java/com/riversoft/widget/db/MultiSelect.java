/*
 * $HeadURL: $
 * $Id: $
 * Copyright (c) 2013 by Riversoft System, all rights reserved.
 */
package com.riversoft.widget.db;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.riversoft.core.db.po.Code2NameVO;
import com.riversoft.core.web.widget.WidgetAnnotation;

/**
 * 多选控件，数据处理逻辑与select相同
 * 
 * @author Woden
 * 
 */
@WidgetAnnotation(cmd = "multiselect", ftl = "classpath:widget/{mode}/db/multiselect.ftl")
public class MultiSelect extends Select {
	@Override
	public String show(Object value) {
		if (value == null) {
			return "";
		}

		List<String> array = new ArrayList<>();
		String[] vals = value.toString().split(";");
		for (String val : vals) {
			// 遍历翻译
			String key = val.replaceAll("\\[", "");
			key = key.replaceAll("\\]", "");
			for (Code2NameVO vo : getList()) {
				if (key.equals(vo.getCode().toString())) {
					array.add(vo.getShowName());
					break;
				}
			}
		}
		return StringUtils.join(array, ";");
	}

}
