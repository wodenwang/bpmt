/*
 * $HeadURL: $
 * $Id: $
 * Copyright (c) 2013 by Riversoft System, all rights reserved.
 */
package com.riversoft.platform.db;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternUtils;

import com.riversoft.core.BeanFactory;

/**
 * dtable模块获取
 * 
 * @author woden
 * 
 */
public class DTableLoader implements ResourceLoaderAware {
	/**
	 * Logger for this class
	 */
	private static final Logger logger = LoggerFactory.getLogger(DTableLoader.class);

	@SuppressWarnings("unused")
	private ResourcePatternResolver resourcePatternResolver = new PathMatchingResourcePatternResolver();
	private Resource[] locations;

	private Map<String, Resource> resourceMap = new TreeMap<>();

	/**
	 * @param locations
	 *            the locations to set
	 */
	public void setLocations(Resource[] locations) {
		this.locations = locations;
	}

	@Override
	public void setResourceLoader(ResourceLoader resourceLoader) {
		this.resourcePatternResolver = ResourcePatternUtils.getResourcePatternResolver(resourceLoader);
	}

	public static DTableLoader getInstance() {
		return (DTableLoader) BeanFactory.getInstance().getBean("dTableLoader");
	}

	public void init() {
		if (this.locations != null) {
			for (Resource resource : this.locations) {
				logger.info("dtable模板-> " + resource.getFilename());
				resourceMap.put(resource.getFilename(), resource);
			}
		}
	}

	/**
	 * 获取模板列表
	 * 
	 * @return
	 */
	public Set<String> getNames() {
		return resourceMap.keySet();
	}

	/**
	 * 获取模板文件流
	 * 
	 * @return
	 * @throws IOException
	 */
	public InputStream getResourceInputStream(String name) throws IOException {
		if (resourceMap.containsKey(name)) {
			return resourceMap.get(name).getInputStream();
		} else {
			return null;
		}
	}

}
