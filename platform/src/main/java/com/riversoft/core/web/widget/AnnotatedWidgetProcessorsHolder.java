package com.riversoft.core.web.widget;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

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

/**
 * Created by borball on 14-1-12.
 */
public class AnnotatedWidgetProcessorsHolder {

    private Logger logger = LoggerFactory.getLogger(AnnotatedWidgetProcessorsHolder.class);
    private ResourceLoader resourceLoader = new DefaultResourceLoader();
    private Map<String, Class<?>> widgets = new HashMap<>();
    private Map<String, String> widgetResources = new HashMap<>();
    private Map<String, String> widgetDocs = new HashMap<>();
    private String ftlDefaultPath = "classpath:widget/";
    /**
     * 文档默认存放地址
     */
    private String docDefaultPath = "classpath:doc/widget/";

    public void setDocDefaultPath(String docDefaultPath) {
        String path = docDefaultPath;
        if (!docDefaultPath.endsWith("/")) {
            path = docDefaultPath + "/";
        }
        this.docDefaultPath = path;
    }

    public void setFtlDefaultPath(String ftlDefaultPath) {
        String ftlRootPath = ftlDefaultPath;
        if (!ftlDefaultPath.endsWith("/")) {
            ftlRootPath = ftlDefaultPath + "/";
        }
        this.ftlDefaultPath = ftlRootPath;
    }

    /**
     * @return the widgetDocs
     */
    public Map<String, String> getWidgetDocs() {
        return widgetDocs;
    }

    public Map<String, Class<?>> getWidgets() {
        return this.widgets;
    }

    public Map<String, String> getWidgetResources() {
        return widgetResources;
    }

    public static AnnotatedWidgetProcessorsHolder getInstance() {
        return (AnnotatedWidgetProcessorsHolder) BeanFactory.getInstance().getBean("annotatedWidgetProcessorsHolder");
    }

    public void init() {
        ClassPathScanningCandidateComponentProvider scanner = new ClassPathScanningCandidateComponentProvider(false);
        scanner.addIncludeFilter(new AnnotationTypeFilter(WidgetAnnotation.class));
        for (BeanDefinition bd : scanner.findCandidateComponents("com.riversoft")) {
            String clazzName = bd.getBeanClassName();
            Class<?> clazz;
            try {
                clazz = Class.forName(clazzName);
                WidgetAnnotation widgetAnnotation = clazz.getAnnotation(WidgetAnnotation.class);

                String cmd = widgetAnnotation.cmd();
                this.widgets.put(cmd, clazz);

                String ftl = widgetAnnotation.ftl();
                if (StringUtils.isEmpty(ftl)) {
                    ftl = ftlDefaultPath + cmd + ".ftl";
                }
                this.widgetResources.put(cmd, ftl);

                // 处理doc数据
                // 查找DOC
                String doc = "";
                Resource resource = null;
                if (StringUtils.isNotEmpty(widgetAnnotation.doc())) {// 先通过配置查找
                    resource = resourceLoader.getResource(widgetAnnotation.doc());
                }

                if (resource == null || !resource.exists()) {// 查找默认位置
                    resource = resourceLoader.getResource(docDefaultPath + cmd + ".html");
                }

                if (resource != null && resource.exists()) {
                    try (BufferedReader in = new BufferedReader(new InputStreamReader(resource.getInputStream()));) {
                        StringBuffer buffer = new StringBuffer();
                        String line = "";
                        while ((line = in.readLine()) != null) {
                            buffer.append(line).append(System.getProperty("line.separator"));
                        }
                        doc = buffer.toString();
                    }
                }
                this.widgetDocs.put(cmd, doc);

            } catch (Exception e) {
                logger.warn("AnnotatedWidgetProcessorsHolder scan failed:" + e.getMessage());
                // ignore the error silently
            }
        }

        for (String key : widgets.keySet()) {
            logger.info("Annotated widget: " + key + "->" + widgets.get(key).getName());
        }
        for (String key : widgetResources.keySet()) {
            logger.info("Widget resource: " + key + "->" + widgetResources.get(key));
        }
    }

}
