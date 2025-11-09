/*
 * $HeadURL: $
 * $Id: $
 * Copyright (c) 2013 by Wodensoft System, all rights reserved.
 */
package com.riversoft.core.db.hbm.model;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

/**
 * @author Woden
 * 
 */
@XStreamAlias("cache")
class CacheModel {

    @XStreamAsAttribute
    private String usage;
}
