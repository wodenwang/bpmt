/*
 * $HeadURL: $
 * $Id: $
 * Copyright (c) 2013 by Riversoft System, all rights reserved.
 */
package com.riversoft.core.db.po;

import java.io.Serializable;

/**
 * 树模型
 * 
 * @author Woden
 * 
 */
public interface Tree extends Serializable {

    public String getId();

    public String getParentId();

    public String getName();

    public Integer getSort();
}
