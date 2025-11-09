/*
 * $HeadURL: $
 * $Id: $
 * Copyright (c) 2013 by Riversoft System, all rights reserved.
 */
package com.riversoft.module.flow.view;

import java.util.HashMap;
import java.util.Map;

/**
 * 动态表模块-系统内置按钮
 * 
 * @author woden
 * 
 */
public enum SysBtn {

	// 明细按钮
	/**
	 * 查看
	 */
	SHOW(1, "showTask", "查看", "circle-zoomin", "left"),
	/**
	 * 处理
	 */
	INVOKE(1, "invokeTask", "处理", "circle-triangle-e", "left"),
	/**
	 * 删除
	 */
	DEL(1, "removeOrder", "删除", "trash", "left"),

	// 汇总按钮
	/**
	 * 新增
	 */
	CREATE(2, "createOrder", "新增", "circle-plus", "left"),
	/**
	 * 导出
	 */
	DOWNLOAD(2, "download", "批量导出", "arrowthickstop-1-s", "right");

	private String name;
	private String busiName;
	private String icon;
	private Integer type;
	private String styleClass;

	private SysBtn(Integer type, String name, String busiName, String icon, String styleClass) {
		this.name = name;
		this.busiName = busiName;
		this.icon = icon;
		this.type = type;
		this.styleClass = styleClass;
	}

	/**
	 * 获取类型
	 * 
	 * @return
	 */
	public Integer getType() {
		return type;
	}

	/**
	 * 获取按钮name
	 * 
	 * @return
	 */
	public String getName() {
		return name;
	}

	/**
	 * 转换成map
	 * 
	 * @return
	 */
	public Map<String, Object> toMap() {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("name", name);
		map.put("busiName", busiName);
		map.put("icon", icon);
		map.put("type", type);
		map.put("styleClass", styleClass);
		return map;
	}
}
