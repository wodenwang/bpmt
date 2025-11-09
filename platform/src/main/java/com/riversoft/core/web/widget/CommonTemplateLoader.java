package com.riversoft.core.web.widget;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

import freemarker.cache.TemplateLoader;

/**
 * Created by borball on 14-1-12.
 */
public class CommonTemplateLoader implements TemplateLoader {

	private Logger logger = LoggerFactory.getLogger(CommonTemplateLoader.class);
	private ResourceLoader resourceLoader = new DefaultResourceLoader();

	public CommonTemplateLoader() {
	}

	@Override
	public Object findTemplateSource(String name) throws IOException {
		if (logger.isDebugEnabled()) {
			logger.debug("Looking for FreeMarker template with name [" + name + "]");
		}
		Resource resource = resourceLoader.getResource(name);
		return (resource.exists() ? resource : null);
	}

	@Override
	public long getLastModified(Object templateSource) {
		Resource resource = (Resource) templateSource;
		try {
			return resource.lastModified();
		} catch (IOException ex) {
			logger.error("Could not obtain last-modified timestamp for FreeMarker template in " + resource + ": " + ex);
			return -1;
		}
	}

	@Override
	public Reader getReader(Object templateSource, String encoding) throws IOException {
		Resource resource = (Resource) templateSource;
		try {
			return new InputStreamReader(resource.getInputStream(), encoding);
		} catch (IOException ex) {
			logger.error("Could not find FreeMarker template: " + resource);
			throw ex;
		}
	}

	@Override
	public void closeTemplateSource(Object templateSource) throws IOException {
	}

}
