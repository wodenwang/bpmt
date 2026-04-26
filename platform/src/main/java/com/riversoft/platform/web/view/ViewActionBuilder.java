/*
 * $HeadURL: $
 * $Id: $
 * Copyright (c) 2013 by Riversoft System, all rights reserved.
 */
package com.riversoft.platform.web.view;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.type.filter.AnnotationTypeFilter;

import com.riversoft.core.BeanFactory;
import com.riversoft.core.web.Actions;
import com.riversoft.platform.web.view.annotation.Conf;
import com.riversoft.platform.web.view.annotation.Conf.TargetType;
import com.riversoft.platform.web.view.annotation.Sys;
import com.riversoft.platform.web.view.annotation.Sys.SysMethod;
import com.riversoft.platform.web.view.annotation.View;
import com.riversoft.platform.web.view.annotation.View.Group;
import com.riversoft.platform.web.view.annotation.View.LoginType;

/**
 * 视图规范下模块解析器
 * 
 * @author Woden
 * 
 */
public class ViewActionBuilder {
	/**
	 * Logger for this class
	 */
	private static final Logger logger = LoggerFactory.getLogger(ViewActionBuilder.class);
	private ResourceLoader resourceLoader = new DefaultResourceLoader();

	private static abstract class BaseModuleVO {
		String description;
		Class<?> clazz;
		int sort;
		String doc;
		TargetType[] targets;

		/**
		 * @param description
		 * @param clazz
		 * @param sort
		 * @param doc
		 * @param targets
		 */
		BaseModuleVO(String description, Class<?> clazz, int sort, String doc, TargetType[] targets) {
			super();
			this.description = description;
			this.clazz = clazz;
			this.sort = sort;
			this.doc = doc;
			this.targets = targets;
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
		 * @return the doc
		 */
		public String getDoc() {
			return doc;
		}

		/**
		 * @return the targets
		 */
		public TargetType[] getTargets() {
			return targets;
		}
	}

	/**
	 * 保存视图模块信息
	 * 
	 * @author woden
	 * 
	 */
	public static class ViewVO extends BaseModuleVO {

		/**
		 * @param description
		 * @param clazz
		 * @param sort
		 * @param doc
		 * @param targets
		 * @param name
		 * @param group
		 */
		public ViewVO(String description, Class<?> clazz, int sort, String doc, TargetType[] targets, String name,
				Group group, LoginType[] loginType) {
			super(description, clazz, sort, doc, targets);
			this.name = name;
			this.group = group;
			this.loginType = loginType;
		}

		String name;
		Group group;
		LoginType[] loginType;

		/**
		 * @return the name
		 */
		public String getName() {
			return name;
		}

		/**
		 * @return the group
		 */
		public Group getGroup() {
			return group;
		}

		/**
		 * @return the loginType
		 */
		public LoginType[] getLoginType() {
			return loginType;
		}

	}

	/**
	 * 系统模块信息
	 * 
	 * @author woden
	 * 
	 */
	public static class SysVO extends BaseModuleVO {
		String name;
		String url;

		/**
		 * @param description
		 * @param clazz
		 * @param sort
		 * @param doc
		 * @param targets
		 * @param name
		 * @param url
		 */
		public SysVO(String description, Class<?> clazz, int sort, String doc, TargetType[] targets, String name,
				String url) {
			super(description, clazz, sort, doc, targets);
			this.name = name;
			this.url = url;
		}

		/**
		 * @return the name
		 */
		public String getName() {
			return name;
		}

		/**
		 * @return the url
		 */
		public String getUrl() {
			return url;
		}
	}

	/**
	 * 扫描的包名
	 */
	private String scanPackage = "com.riversoft.module";

	/**
	 * 视图模块哈希表
	 */
	private List<ViewVO> viewModuleList = new ArrayList<>();
	private List<SysVO> sysModuleList = new ArrayList<>();

	/**
	 * 获取单例
	 * 
	 * @return
	 */
	public static ViewActionBuilder getInstance() {
		return (ViewActionBuilder) BeanFactory.getInstance().getBean("viewActionBuilder");
	}

	/**
	 * 获取视图模块
	 * 
	 * @param viewClass
	 *            {@link View}中的value
	 * @return
	 */
	public Class<?> getViewClass(String viewClass) {
		ViewVO vo = getViewModule(viewClass);
		if (vo != null) {
			return vo.clazz;
		}

		return null;
	}

	/**
	 * 获取视图模块配置信息
	 * 
	 * @param viewClass
	 * @return
	 */
	public ViewVO getViewModule(String viewClass) {
		for (ViewVO o : viewModuleList) {
			if (o.name.equalsIgnoreCase(viewClass)) {
				return o;
			}
		}
		return null;
	}

	/**
	 * 通过URL找到模块
	 * 
	 * @param url
	 * @return
	 */
	public SysVO getSysModule(String url) {
		for (SysVO o : sysModuleList) {
			if (o.url.equals(url)) {
				return o;
			}
		}
		return null;
	}

	/**
	 * 获取特定模块信息列表
	 * 
	 * @param target
	 * @return
	 */
	public Collection<ViewVO> getViewModuleList(TargetType target) {
		List<ViewVO> list = new ArrayList<>();
		for (ViewVO o : viewModuleList) {
			if (ArrayUtils.contains(o.getTargets(), target)) {
				list.add(o);
			}
		}
		return list;
	}

	/**
	 * 获取所有模块
	 * 
	 * @return
	 */
	public Collection<ViewVO> getViewModuleList() {
		return viewModuleList;
	}

	/**
	 * 获取分组模块
	 * 
	 * @return
	 */
	public Map<String, List<ViewVO>> getViewModuleGroup() {
		Map<String, List<ViewVO>> groups = new LinkedHashMap<String, List<ViewVO>>();
		for (ViewVO o : viewModuleList) {
			String key = o.getGroup().toString();
			if (!groups.containsKey(key)) {
				groups.put(key, new ArrayList<ViewActionBuilder.ViewVO>());
			}
			groups.get(key).add(o);
		}

		return groups;
	}

	/**
	 * 系统模块列表
	 * 
	 * @param target
	 * @return
	 */
	public Collection<SysVO> getSysModuleList(TargetType target) {
		List<SysVO> list = new ArrayList<>();
		for (SysVO o : sysModuleList) {
			if (ArrayUtils.contains(o.getTargets(), target)) {
				list.add(o);
			}
		}
		return list;
	}

	/**
	 * 初始化系统模块
	 * 
	 * @param clazz
	 */
	private void initSys(Class<?> clazz) {
		for (Method method : clazz.getMethods()) {
			if (method.isAnnotationPresent(SysMethod.class)) {
				Conf annotaion = method.getAnnotation(Conf.class);
				if (annotaion == null) {// 没有Conf则无视
					continue;
				}

				String name = clazz.getName() + "." + method.getName();
				String url = Actions.Util.getActionUrl(clazz) + "/" + method.getName() + ".shtml";
				logger.info("查找到一个系统内置视图[" + name + "]");

				// 查找DOC
				String doc = null;
				Resource resource = null;
				if (StringUtils.isNotEmpty(annotaion.doc())) {// 先通过配置查找
					resource = resourceLoader.getResource(annotaion.doc());
				}

				if (resource == null || !resource.exists()) {// 查找默认位置
					resource = resourceLoader.getResource("classpath:doc/module/" + name + ".html");
				}

				if (resource != null && resource.exists()) {
					try (BufferedReader in = new BufferedReader(new InputStreamReader(resource.getInputStream()));) {
						StringBuffer buffer = new StringBuffer();
						String line = "";
						while ((line = in.readLine()) != null) {
							buffer.append(line);
						}
						doc = buffer.toString();
					} catch (IOException ignore) {
						// only log
						logger.error("视图模块[" + name + "]获取doc数据出错.", ignore);
					}
				}

				sysModuleList.add(new SysVO(annotaion.description(), clazz, annotaion.sort(), doc, annotaion.target(),
						name, url));
			}
		}
	}

	/**
	 * 初始化视图模块
	 * 
	 * @param clazz
	 */
	private void initView(Class<?> clazz) {
		String name = clazz.getAnnotation(View.class).value();
		Group group = clazz.getAnnotation(View.class).group();
		LoginType[] loginType = clazz.getAnnotation(View.class).loginType();
		Conf annotaion = clazz.getAnnotation(Conf.class);
		if (annotaion == null) {// 没有Conf则无视
			return;
		}

		// 查找DOC
		String doc = null;
		Resource resource = null;
		if (StringUtils.isNotEmpty(annotaion.doc())) {// 先通过配置查找
			resource = resourceLoader.getResource(annotaion.doc());
		}

		if (resource == null || !resource.exists()) {// 查找默认位置
			resource = resourceLoader.getResource("classpath:doc/view/" + name + ".html");
		}

		if (resource != null && resource.exists()) {
			try (BufferedReader in = new BufferedReader(new InputStreamReader(resource.getInputStream()));) {
				StringBuffer buffer = new StringBuffer();
				String line = "";
				while ((line = in.readLine()) != null) {
					buffer.append(line);
				}
				doc = buffer.toString();
			} catch (IOException ignore) {
				// only log
				logger.error("视图模块[" + name + "]获取doc数据出错.", ignore);
			}
		}

		viewModuleList.add(new ViewVO(annotaion.description(), clazz, annotaion.sort(), doc, annotaion.target(), name,
				group, loginType));
	}

	/**
	 * 初始化,spring容器调用
	 */
	public void init() {
		ClassPathScanningCandidateComponentProvider scanner = new ClassPathScanningCandidateComponentProvider(false);
		scanner.addIncludeFilter(new AnnotationTypeFilter(View.class));
		scanner.addIncludeFilter(new AnnotationTypeFilter(Sys.class));
		for (BeanDefinition bd : scanner.findCandidateComponents(scanPackage)) {
			String clazzName = bd.getBeanClassName();
			Class<?> clazz;
			try {
				clazz = Class.forName(clazzName);
				if (clazz.getAnnotation(Sys.class) != null) {// 系统模块
					logger.info("加载系统视图:[{}]", clazz.getName());
					initSys(clazz);
				} else if (clazz.getAnnotation(View.class) != null) {// 视图模块
					logger.info("加载视图:[{}]", clazz.getName());
					initView(clazz);
				} else {
					continue;
				}

			} catch (Exception e) {
				logger.warn("ExprlangAnnotationScanner scan failed:" + e.getMessage());
			}
		}

		Collections.sort(viewModuleList, new Comparator<ViewVO>() {
			@Override
			public int compare(ViewVO o1, ViewVO o2) {
				if (o1.sort < o2.sort) {
					return -1;
				} else if (o1.sort == o2.sort) {
					return 0;
				} else {
					return 1;
				}
			}
		});

		Collections.sort(sysModuleList, new Comparator<SysVO>() {
			@Override
			public int compare(SysVO o1, SysVO o2) {
				return o1.sort < o2.sort ? -1 : 1;
			}
		});
	}
}
