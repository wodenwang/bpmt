/*
 * $HeadURL: $
 * $Id: $
 * Copyright (c) 2017 by Riversoft System, all rights reserved.
 */
package com.riversoft.platform.mq;

import com.riversoft.core.script.annotation.ScriptSupport;
import com.riversoft.platform.mq.rabbitmq.RabbitMQHelper;

/**
 * @author woden
 *
 */
@ScriptSupport("mq")
public class MQHelper {

	/**
	 * 获取rabbitmq实例
	 * 
	 * @return
	 */
	public RabbitMQHelper getRabbit() {
		return RabbitMQHelper.getInstance();
	}
}
