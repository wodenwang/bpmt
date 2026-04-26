/*
 * $HeadURL: $
 * $Id: $
 * Copyright (c) 2013 by Riversoft System, all rights reserved.
 */
package com.riversoft.core.db.hbm;

import com.riversoft.core.db.po.Code2NameVO;

/**
 * 主键生成规则
 * 
 * @author Woden
 * 
 */
public enum GeneratorType implements Code2NameVO {
    NATIVE("主动输入", "native"), INCREMENT("自动递增", "increment");

    private String classType;
    private String showName;

    private GeneratorType(String showName, String classType) {
        this.showName = showName;
        this.classType = classType;
    }

    @Override
    public Object getCode() {
        return classType;
    }

    @Override
    public String getShowName() {
        return showName;
    }

}
