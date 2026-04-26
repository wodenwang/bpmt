/*
 * $HeadURL: $
 * $Id: $
 * Copyright (c) 2013 by Riversoft System, all rights reserved.
 */
package com.riversoft.flow.key;

import org.activiti.bpmn.model.IntermediateCatchEvent;

/**
 * 流程变量
 * 
 * @author woden
 * 
 */
public enum VariableKeys {
    /**
     * 包容节点判断用,保存目标线的ID
     */
    _INCLUSIVE_GATEWAY_OUTCOME,

    /**
     * 判断节点判断用,保存目标线的ID
     */
    _EXCLUSIVE_GATEWAY_OUTCOME,

    /**
     * 订单表名
     */
    _ORDER_TABLE_NAME,

    /**
     * 订单历史表名
     */
    _ORDER_HISTORY_TABLE_NAME,

    /**
     * {@link IntermediateCatchEvent}节点上使用的timeDuration变量
     */
    _TIMER_DURATION;

    public static enum TaskVariableKeys {
        /**
         * 用户节点有多条线时保存目标线的ID
         */
        _USER_TASK_OUTCOME

        ;
    }
}
