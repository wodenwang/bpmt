/*
 * $HeadURL: $
 * $Id: $
 * Copyright (c) 2013 by Riversoft System, all rights reserved.
 */
package com.riversoft.platform.web.widget;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AnnotationTypeFilter;

import com.riversoft.core.BeanFactory;

/**
 * 视图规范下模块解析器
 * 
 * @author Woden
 * 
 */
public class WidgetConfigBuilder {
	/**
	 * Logger for this class
	 */
	private static final Logger logger = LoggerFactory.getLogger(WidgetConfigBuilder.class);

	/**
	 * 配置值对象
	 * 
	 * @author woden
	 * 
	 */
	public static class ConfigVO {
		String description;
		Class<?> clazz;
		int sort;
		String name;

		/**
		 * @param description
		 * @param clazz
		 * @param sort
		 * @param name
		 */
		public ConfigVO(String description, Class<?> clazz, int sort, String name) {
			super();
			this.description = description;
			this.clazz = clazz;
			this.sort = sort;
			this.name = name;
		}

		/**
		 * @return the description
		 */
		public String getDescription() {
			return description;
		}

		/**
		 * @return the clazz
		 */
		public Class<?> getClazz() {
			return clazz;
		}

		/**
		 * @return the sort
		 */
		public int getSort() {
			return sort;
		}

		/**
		 * @return the name
		 */
		public String getName() {
			return name;
		}
	}

	/**
	 * 扫描的包名
	 */
	private String scanPackage = "com.riversoft.module";

	/**
	 * 模块列表
	 */
	private List<ConfigVO> list = new ArrayList<>();

	/**
	 * @return the list
	 */
	public List<ConfigVO> getList() {
		return list;
	}

	/**
	 * 获取单例
	 * 
	 * @return
	 */
	public static WidgetConfigBuilder getInstance() {
		return (WidgetConfigBuilder) BeanFactory.getInstance().getBean("widgetConfigBuilder");
	}

	/**
	 * 根据控件name获取配置
	 * 
	 * @param name
	 * @return
	 */
	public ConfigVO getConfigVO(String name) {
		for (ConfigVO vo : list) {
			if (vo.name.equals(name)) {
				return vo;
			}
		}

		return null;
	}

	/**
	 * 初始化,spring容器调用
	 */
	public void init() {
		ClassPathScanningCandidateComponentProvider scanner = new ClassPathScanningCandidateComponentProvider(false);
		scanner.addIncludeFilter(new AnnotationTypeFilter(WidgetConfig.class));
		for (BeanDefinition bd : scanner.findCandidateComponents(scanPackage)) {
			String clazzName = bd.getBeanClassName();
			Class<?> clazz;

			try {
				clazz = Class.forName(clazzName);
				WidgetConfig annotation = clazz.getAnnotation(WidgetConfig.class);
				logger.info("找到一个数据控件配置:[" + annotation.value() + "]==>[" + clazz.getName() + "]");

				list.add(new ConfigVO(annotation.description(), clazz, annotation.sort(), annotation.value()));
				Collections.sort(list, new Comparator<ConfigVO>() {
					@Override
					public int compare(ConfigVO o1, ConfigVO o2) {
						return o1.sort < o2.sort ? -1 : 1;
					}
				});

			} catch (Exception e) {
				logger.warn("ExprlangAnnotationScanner scan failed:" + e.getMessage());
			}
		}
	}
}
