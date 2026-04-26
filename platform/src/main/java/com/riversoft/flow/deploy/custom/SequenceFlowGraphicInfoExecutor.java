/*
 * $HeadURL: $
 * $Id: $
 * Copyright (c) 2013 by Riversoft System, all rights reserved.
 */
package com.riversoft.flow.deploy.custom;

import org.activiti.bpmn.model.BpmnModel;
import org.activiti.bpmn.model.GraphicInfo;
import org.activiti.bpmn.model.SequenceFlow;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.riversoft.flow.deploy.CustomNode;
import com.riversoft.flow.deploy.CustomNodeExecutor;

/**
 * 连线上加上文字
 * 
 * @author woden
 * 
 */
@CustomNode(SequenceFlow.class)
public class SequenceFlowGraphicInfoExecutor implements CustomNodeExecutor<SequenceFlow> {
    /**
     * Logger for this class
     */
    private static final Logger logger = LoggerFactory.getLogger(SequenceFlowGraphicInfoExecutor.class);

    @Override
    public void execute(BpmnModel model, SequenceFlow node) {
        logger.debug("为连线[" + node.getId() + "]加上文字[" + node.getName() + "]");
        if (StringUtils.isNotEmpty(node.getName())) {// 有文字

            double x = 0;
            double y = 0;
            double width = 0;
            double height = 0;

            GraphicInfo graphicInfo = new GraphicInfo();
            graphicInfo.setX(x);
            graphicInfo.setY(y);
            graphicInfo.setHeight(width);
            graphicInfo.setWidth(height);
            model.getLabelLocationMap().put(node.getId(), graphicInfo);
        }
    }

}
