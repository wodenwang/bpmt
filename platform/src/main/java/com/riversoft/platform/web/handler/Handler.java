/*
 * $HeadURL: $
 * $Id: $
 * Copyright (c) 2013 by Riversoft System, all rights reserved.
 */
package com.riversoft.platform.web.handler;

import com.riversoft.core.db.DataPO;

/**
 * 配置数据处理器
 * 
 * @author woden
 * 
 */
public interface Handler {

    void handle(DataPO tablePO);
}
