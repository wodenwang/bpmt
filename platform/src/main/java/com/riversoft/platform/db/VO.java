/*
 * $HeadURL: $
 * $Id: $
 * Copyright (c) 2013 by Riversoft System, all rights reserved.
 */
package com.riversoft.platform.db;

import com.riversoft.core.db.po.Code2NameTreeVO;
import com.riversoft.core.db.po.Code2NameVO;

/**
 * 翻译所使用的VO类，一般用于界面下拉列表(select)的循环
 * 
 * @author Woden
 * 
 */
class VO implements Code2NameVO {

    private Object code;
    private String showName;

    /**
     * @return the code
     */
    public Object getCode() {
        return code;
    }

    /**
     * @param code the code to set
     */
    public void setCode(Object code) {
        this.code = code;
    }

    /**
     * @return the showName
     */
    public String getShowName() {
        return showName;
    }

    /**
     * @param showName the showName to set
     */
    public void setShowName(String showName) {
        this.showName = showName;
    }
}

class TreeVO extends VO implements Code2NameTreeVO {

    private Object parentCode;

    /**
     * @return the parentCode
     */
    public Object getParentCode() {
        return parentCode;
    }

    /**
     * @param parentCode the parentCode to set
     */
    public void setParentCode(Object parentCode) {
        this.parentCode = parentCode;
    }

}
