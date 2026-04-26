/*
 * $HeadURL: $
 * $Id: $
 * Copyright (c) 2013 by Riversoft System, all rights reserved.
 */
package com.riversoft.widget.sys;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.riversoft.util.jackson.JsonMapper;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.riversoft.core.IDGenerator;
import com.riversoft.core.db.ORMService;
import com.riversoft.core.web.widget.FormValue;
import com.riversoft.core.web.widget.Widget;
import com.riversoft.core.web.widget.WidgetAnnotation;
import com.riversoft.platform.po.CmPri;
import com.riversoft.util.ValueConvertUtils;

/**
 * @author Woden
 * 
 */
@WidgetAnnotation(cmd = "pri", ftl = "classpath:widget/{mode}/sys/pri.ftl")
public class Pri implements Widget {
	/**
	 * Logger for this class
	 */
	private static final Logger logger = LoggerFactory.getLogger(Pri.class);

	private String tip;// 提示信息

	@Override
	public void setParams(FormValue... values) {
		if (values != null && values.length > 0) {
			tip = values[0].getName();
		}
	}

	@Override
	public String show(Object value) {
		CmPri pri = (CmPri) value;
		StringBuffer buff = new StringBuffer();
		buff.append("[").append(pri.getPriKey()).append("]")
				.append(CmPri.Catelog.fromCode(pri.getCatelogType()).getShowName()).append("-")
				.append(pri.getBusiName());
		return buff.toString();
	}

	@Override
	public Object code(String showName) {
		CmPri pri = null;
		{
			Pattern p = Pattern.compile("\\[([^\\]]*)\\]");
			Matcher m = p.matcher(showName);
			while (m.find()) {
				String result = m.group(1);
				pri = (CmPri) ORMService.getInstance().findByPk(CmPri.class.getName(), result);
			}
		}

		if (pri == null) {
			try {
				pri = ValueConvertUtils.convert(showName, CmPri.class);
				// 没有key的就自动创建一个
				if (StringUtils.isEmpty(pri.getPriKey())) {
					pri.setPriKey(IDGenerator.uuid());
				}
			} catch (Exception e) {
				logger.warn("转换json[" + showName + "]出错.", e);
				pri = new CmPri();
				pri.setPriKey(IDGenerator.uuid());
			}
		}

		return pri;
	}

	@Override
	public void prepareMap(Map<String, Object> map) {
		map.put("tip", tip);
		Object value = map.get("value");
		if (value == null) {
			CmPri pri = new CmPri();
			pri.setPriKey(IDGenerator.uuid());
			value = pri;
			map.put("value", pri);
		}
		map.put("stringValue", JsonMapper.defaultMapper().toJson(value));
	}

}
