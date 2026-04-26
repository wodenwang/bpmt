/*
 * $HeadURL: $
 * $Id: $
 * Copyright (c) 2013 by Riversoft System, all rights reserved.
 */
package com.riversoft.core.web.widget;

/**
 * freemarker模板中可用的key
 * 
 * @author Woden
 * 
 */
enum FtlKey {
	/**
	 * 组件名
	 */
	name,
	/**
	 * 控件入参数组
	 */
	params,
	/**
	 * 组件值
	 */
	value,
	/**
	 * 控件状态{@link WidgetState}
	 */
	state,
	/**
	 * 该控件实例的唯一ID
	 */
	uuid,
	/**
	 * 动态入参
	 */
	dyncParams,
	/**
	 * 当前网址
	 */
	cp,
	/**
	 * 验证数据json字符串，可直接插入到表单组件的class中
	 */
	validate,
	/**
	 * 验证数据json对象，通过validate字符串转换而来
	 */
	validateObj;

}
