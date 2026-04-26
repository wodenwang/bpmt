/*
 * $HeadURL: $
 * $Id: $
 * Copyright (c) 2014 by Riversoft System, all rights reserved.
 */
package com.riversoft.platform.template;

import java.lang.reflect.Field;
import java.util.Date;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.riversoft.core.db.ORMService;
import com.riversoft.util.ValueConvertUtils;

/**
 * 当前平台部署项目
 * 
 * @author woden
 * 
 */
public class Template {
	/**
	 * Logger for this class
	 */
	private static final Logger logger = LoggerFactory.getLogger(Template.class);

	/**
	 * 当前模板信息
	 */
	private static Template current = new Template();

	/**
	 * 获取当前
	 * 
	 * @return
	 */
	public static Template getCurrent() {
		return current;
	}

	/**
	 * 模板唯一键
	 */
	private String key;
	/**
	 * 模板名称
	 */
	private String name;
	/**
	 * 模板描述
	 */
	private String description;
	/**
	 * 模板版本
	 */
	private Integer version;
	/**
	 * 部署/生成时间
	 */
	private Date date;

	/**
	 * 依赖平台版本
	 */
	private String platformVersion;

	/**
	 * 绑定数据表
	 */
	private String copyDataTables;

	/**
	 * @return the copyDataTables
	 */
	public String getCopyDataTables() {
		return copyDataTables;
	}

	/**
	 * @return the key
	 */
	public String getKey() {
		return key;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @return the version
	 */
	public Integer getVersion() {
		return version;
	}

	/**
	 * @return the date
	 */
	public Date getDate() {
		return date;
	}

	/**
	 * @return the platformVersion
	 */
	public String getPlatformVersion() {
		return platformVersion;
	}

	/**
	 * 构造函数
	 */
	Template() {
		init();
	}

	/**
	 * 初始化
	 */
	@SuppressWarnings("unchecked")
	void init() {

		// 未初始化
		if (ORMService.getInstance().findByPk("TplCurrent", "key") == null) {
			logger.warn("平台模板未初始化.");
			this.version = 0;
			this.date = new Date();
			return;
		}

		for (Field field : this.getClass().getDeclaredFields()) {
			Map<String, Object> vo = (Map<String, Object>) ORMService.getInstance().findByPk("TplCurrent", field.getName());
			if (vo == null) {
				logger.warn("平台模板属性[" + field.getName() + "]无法初始化.");
				continue;
			}
			try {
				logger.info("平台模板属性[" + field.getName() + "]设值:" + vo.get("propertyValue"));
				field.set(this, ValueConvertUtils.convert((String) vo.get("propertyValue"), field.getType()));
			} catch (IllegalArgumentException | IllegalAccessException e) {
				logger.error("平台模板属性[" + field.getName() + "]无法设值.", e);
			}
		}
	}
}
