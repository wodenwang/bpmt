/*
 * $HeadURL: $
 * $Id: $
 * Copyright (c) 2013 by Riversoft System, all rights reserved.
 */
package com.riversoft.flow.deploy;

import org.activiti.bpmn.model.BpmnModel;
import org.activiti.bpmn.model.FlowElement;

/**
 * @author woden
 * 
 */
public interface CustomNodeExecutor<T extends FlowElement> {

    void execute(BpmnModel model, T node);
}
