/*
 * $HeadURL: $
 * $Id: $
 * Copyright (c) 2013 by Riversoft System, all rights reserved.
 */
package com.riversoft.flow.deploy;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.activiti.bpmn.model.BpmnModel;
import org.activiti.bpmn.model.FlowElement;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AnnotationTypeFilter;

/**
 * @author woden
 * 
 */
public class CustomNodeExecutors {

    private static List<CustomNodeExecutor<? extends FlowElement>> list = new ArrayList<>();
    static {
        init();
    }

    private static void init() {
        list = new ArrayList<>();
        ClassPathScanningCandidateComponentProvider scanner = new ClassPathScanningCandidateComponentProvider(false);
        scanner.addIncludeFilter(new AnnotationTypeFilter(CustomNode.class));
        for (BeanDefinition bd : scanner.findCandidateComponents(CustomNodeExecutor.class.getPackage().getName())) {
            String clazzName = bd.getBeanClassName();
            Class<? extends CustomNodeExecutor> clazz;
            try {
                clazz = (Class<? extends CustomNodeExecutor>) Class.forName(clazzName);
                list.add(clazz.newInstance());
            } catch (Exception e) {
                throw new Error(e);
            }
        }
        Collections.sort(list, new Comparator<CustomNodeExecutor>() {
            @Override
            public int compare(CustomNodeExecutor o1, CustomNodeExecutor o2) {
                int sort1 = o1.getClass().getAnnotation(CustomNode.class).sort();
                int sort2 = o2.getClass().getAnnotation(CustomNode.class).sort();
                return sort1 < sort2 ? -1 : 1;
            }
        });
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public static void execute(BpmnModel model) {

        for (CustomNodeExecutor executor : list) {
            CustomNode customNode = executor.getClass().getAnnotation(CustomNode.class);
            for (FlowElement node : model.getMainProcess().findFlowElementsOfType(customNode.value())) {
                executor.execute(model, node);
            }
        }
    }
}
