/*
 * $HeadURL: $
 * $Id: $
 * Copyright (c) 2013 by Riversoft System, all rights reserved.
 */
package com.riversoft.module.dyn;

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
	DETAIL(1, "show", "查看", "zoomin", "left"),
	/**
	 * 编辑
	 */
	EDIT(1, "edit", "编辑", "wrench", "left"),
	/**
	 * 删除
	 */
	DEL(1, "del", "删除", "trash", "left"),

	// 汇总按钮
	/**
	 * 新增
	 */
	CREATE(2, "create", "新增", "plus", "left"),
	/**
	 * 导入
	 */
	UPLOAD(2, "upload", "导入", "arrowthickstop-1-n", "right"),
	/**
	 * 导出
	 */
	DOWNLOAD(2, "download", "批量导出", "arrowthickstop-1-s", "right"),
	/**
	 * 批量删除
	 */
	DEL_ALL(2, "delAll", "批量删除", "trash", "right");

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
