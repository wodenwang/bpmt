/*
 * $HeadURL: $
 * $Id: $
 * Copyright (c) 2013 by Riversoft System, all rights reserved.
 */
package com.riversoft.flow.config;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AnnotationTypeFilter;

import com.riversoft.flow.key.NodeType;

/**
 * 节点配置框架
 * 
 * @author woden
 * 
 */
public class ActivityConfigScanner {

    private static Map<NodeType, Class<? extends BaseActivityConfigAction>> map = new HashMap<>();

    static {
        synchronized (map) {
            init();
        }
    }

    private static void init() {
        ClassPathScanningCandidateComponentProvider scanner = new ClassPathScanningCandidateComponentProvider(false);
        scanner.addIncludeFilter(new AnnotationTypeFilter(ActivityType.class));
        for (BeanDefinition bd : scanner.findCandidateComponents("com.riversoft")) {
            String clazzName = bd.getBeanClassName();
            Class<? extends BaseActivityConfigAction> clazz;
            try {
                clazz = (Class<? extends BaseActivityConfigAction>) Class.forName(clazzName);
                ActivityType activityType = clazz.getAnnotation(ActivityType.class);
                map.put(activityType.value(), clazz);
            } catch (Exception e) {
                continue;
            }
        }
    }

    /**
     * 获取配置Action
     * 
     * @param type
     * @return
     */
    public static Class<? extends BaseActivityConfigAction> findClass(NodeType type) {
        if (map.containsKey(type)) {
            return map.get(type);
        } else {
            return null;
        }

    }

}
