/*
 * $HeadURL: $
 * $Id: $
 * Copyright (c) 2013 by Riversoft System, all rights reserved.
 */
package com.riversoft.flow;

import org.activiti.engine.ProcessEngine;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;

import com.riversoft.core.BeanFactory;

/**
 * 工作流工厂
 * 
 * @author woden
 * 
 */
public class FlowFactory {

    // <bean id="repositoryService" factory-bean="processEngine" factory-method="getRepositoryService"/>
    // <bean id="runtimeService" factory-bean="processEngine" factory-method="getRuntimeService"/>
    // <bean id="formService" factory-bean="processEngine" factory-method="getFormService"/>
    // <bean id="identityService" factory-bean="processEngine" factory-method="getIdentityService"/>
    // <bean id="taskService" factory-bean="processEngine" factory-method="getTaskService"/>
    // <bean id="historyService" factory-bean="processEngine" factory-method="getHistoryService"/>
    // <bean id="managementService" factory-bean="processEngine" factory-method="getManagementService"/>

    /**
     * 获取引擎
     * 
     * @return
     */
    public static ProcessEngine getEngine() {
        ProcessEngine processEngine = (ProcessEngine) BeanFactory.getInstance().getBean("processEngine");
        return processEngine;
    }

    /**
     * 获取资源服务
     * 
     * @return
     */
    public static RepositoryService getRepositoryService() {
        return getEngine().getRepositoryService();
    }

    /**
     * 获取运行时服务
     * 
     * @return
     */
    public static RuntimeService getRuntimeService() {
        return getEngine().getRuntimeService();
    }

    /**
     * 获取任务服务
     * 
     * @return
     */
    public static TaskService getTaskService() {
        return getEngine().getTaskService();
    }

}
