/*
 * $HeadURL: $
 * $Id: $
 * Copyright (c) 2013 by Riversoft System, all rights reserved.
 */
package com.riversoft.widget.sys;

import java.util.Map;

import com.riversoft.util.jackson.JsonMapper;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.riversoft.core.db.DataCondition;
import com.riversoft.core.db.ORMService;
import com.riversoft.core.exception.ExceptionType;
import com.riversoft.core.exception.SystemRuntimeException;
import com.riversoft.core.web.widget.FormValue;
import com.riversoft.core.web.widget.Widget;
import com.riversoft.core.web.widget.WidgetAnnotation;
import com.riversoft.platform.po.CmPri;
import com.riversoft.platform.po.CmPriGroupRelate;

/**
 * @author Woden
 * 
 */
@WidgetAnnotation(cmd = "prigroup", ftl = "classpath:widget/{mode}/sys/prigroup.ftl")
public class PriGroup implements Widget {
	/**
	 * Logger for this class
	 */
	private static final Logger logger = LoggerFactory.getLogger(PriGroup.class);

	private String groupId;
	private String priKey;
	private String tip;// 提示信息

	@Override
	public void setParams(FormValue... values) {
		if (values != null && values.length >= 2) {
			groupId = values[0].getName();
			priKey = values[1].getName();
		} else {
			throw new SystemRuntimeException(ExceptionType.CODING, "控件调用出错.");
		}

		if (values != null && values.length > 2) {
			tip = values[2].getName();
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public String show(Object value) {
		if (value == null) {
			return "";
		} else {
			Map<String, Object> vo = ((Map<String, Object>) value);
			if (StringUtils.isNotEmpty((String) vo.get("checkScript"))) {
				return "[已设脚本]";
			} else {
				return "";
			}
		}
	}

	@Override
	public Object code(String showName) {
		throw new SystemRuntimeException(ExceptionType.CODING, "方法不支持.");
	}

	@Override
	public void prepareMap(Map<String, Object> map) {
		map.put("priKey", priKey);
		map.put("groupId", groupId);
		map.put("tip", tip);
		Object value = ORMService.getInstance().find("CmPriGroupRelate",
				new DataCondition().setStringEqual("groupId", groupId).setStringEqual("priKey", priKey).toEntity());
		if (value == null) {
			CmPriGroupRelate vo = new CmPriGroupRelate();
			vo.setPriKey(priKey);
			vo.setGroupId(groupId);
			value = vo;
		}
		map.put("value", value);

		CmPri pri = (CmPri) ORMService.getInstance().findByPk(CmPri.class.getName(), priKey);
		map.put("pri", pri);

		if (value != null) {
			map.put("stringValue", JsonMapper.defaultMapper().toJson(value));
		}
	}
}
