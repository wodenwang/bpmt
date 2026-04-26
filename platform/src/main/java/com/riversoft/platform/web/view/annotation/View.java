/*
 * $HeadURL: $
 * $Id: $
 * Copyright (c) 2013 by Riversoft System, all rights reserved.
 */
package com.riversoft.platform.web.view.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 视图模块配置
 * 
 * @author woden
 * 
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE })
public @interface View {

	String value();

	Group group();

	public static enum Group {

		SYS("系统开发"),
		FUN("功能插件");

		private String description;

		private Group(String description) {
			this.description = description;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.lang.Enum#toString()
		 */
		@Override
		public String toString() {
			return this.description;
		}
	}

	LoginType[]loginType();// 允许登录方式

	public static enum LoginType {
		/**
		 * 免登陆
		 */
		NONE(0),
		/**
		 * 用户登录
		 */
		USER(1);
		int code;

		LoginType(int code) {
			this.code = code;
		}

		public int getCode() {
			return code;
		}
	}
}
