/*
 * $HeadURL: $
 * $Id: $
 * Copyright (c) 2013 by Riversoft System, all rights reserved.
 */
package com.riversoft.core.web.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

/**
 * 配置访问当前Action时的拦截情况
 * 
 * @author Woden
 * 
 */
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.METHOD, ElementType.TYPE })
public @interface ActionAccess {

	/**
	 * 安全角色
	 * 
	 * @author woden
	 * 
	 */
	public static enum SafeRole {
		/**
		 * 生产系统
		 */
		PRO_SYS("生产系统", "实际生产中使用,为了保证数据安全不允许在此系统上进行功能配置.", SafeLevel.BUSI_R, SafeLevel.BUSI_W, SafeLevel.DEV_R),
		/**
		 * 开发系统
		 */
		DEV_SYS("开发系统", "此系统仅供开发测试使用,业务数据安全性将不会被保证.", SafeLevel.BUSI_R, SafeLevel.BUSI_W, SafeLevel.DEV_R, SafeLevel.DEV_W, SafeLevel.DEV_SPC),
		/**
		 * 复合系统
		 */
		LIGHT_WEIGHT("轻量级生产系统", "实际生产中使用,允许进行功能配置.", SafeLevel.BUSI_R, SafeLevel.BUSI_W, SafeLevel.DEV_R, SafeLevel.DEV_W);
		private List<SafeLevel> levels;
		private String description;
		private String busiName;

		private SafeRole(String busiName, String description, SafeLevel... levels) {
			this.description = description;
			this.busiName = busiName;
			this.levels = Arrays.asList(levels);
		}

		/**
		 * 简要说明
		 * 
		 * @return
		 */
		public String getBusiName() {
			return busiName;
		}

		/**
		 * 详细说明
		 * 
		 * @return
		 */
		public String getDescription() {
			return description;
		}

		public static SafeRole forName(String name) {
			if (StringUtils.isEmpty(name)) {
				return LIGHT_WEIGHT;
			}

			SafeRole role = SafeRole.valueOf(name);
			if (role != null) {
				return role;
			}
			return LIGHT_WEIGHT;// 默认值
		}

		public boolean checkLevel(SafeLevel level) {
			return levels.contains(level);
		}
	}

	/**
	 * 安全级别
	 * 
	 * @author woden
	 * 
	 */
	public static enum SafeLevel {

		BUSI_R("业务读"),
		BUSI_W("业务写"),
		DEV_R("开发读"),
		DEV_W("开发写"),
		DEV_SPC("特殊开发");

		private String description;

		private SafeLevel(String description) {
			this.description = description;
		}

		public String getKey() {
			return this.name();
		}

		public String getDescription() {
			return name() + "-" + description;
		}

		public static SafeLevel fromKey(String key) {
			SafeLevel safeLevel = SafeLevel.valueOf(key);
			if (safeLevel != null) {
				return safeLevel;
			}
			return BUSI_R;
		}
	}

	/**
	 * 是否需要登录
	 * 
	 * @return
	 */
	boolean login() default true;

	/**
	 * 安全级别
	 * 
	 * @return
	 */
	SafeLevel level() default SafeLevel.BUSI_R;

	/**
	 * 是否验证工程管理员
	 * 
	 * @return
	 */
	boolean admin() default false;
}
