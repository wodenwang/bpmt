/*
 * $HeadURL: $
 * $Id: $
 * Copyright (c) 2013 by Riversoft System, all rights reserved.
 */
package com.riversoft.core.db.hbm.model;

import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 * @author Woden
 * 
 */
@XStreamAlias("hibernate-mapping")
class HibernateMappingModel {

    @XStreamAlias("class")
    private ClassModel classModel;

    /**
     * @return the classModel
     */
    public ClassModel getClassModel() {
        return classModel;
    }

    /**
     * @param classModel the classModel to set
     */
    public void setClassModel(ClassModel classModel) {
        this.classModel = classModel;
    }
}
