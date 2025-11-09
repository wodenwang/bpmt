/*
 * $HeadURL: $
 * $Id: $
 * Copyright (c) 2014 by Riversoft System, all rights reserved.
 */
package com.riversoft.platform.web.view.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.METHOD, ElementType.TYPE })
public @interface Conf {
	String description();

	int sort() default 9999;// 默认最后

	String doc() default "";// 说明文档

	TargetType[]target();// 允许目标

	public static enum TargetType {
		/**
		 * 菜单
		 */
		MENU,
		/**
		 * 子表标签
		 */
		SUB,
		/**
		 * 按钮
		 */
		BTN,
		/**
		 * 首页区域
		 */
		HOME,
		/**
		 * 微信公众号/企业号菜单
		 */
		WX
	}

}
