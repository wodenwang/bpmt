/*
 * $HeadURL: $
 * $Id: $
 * Copyright (c) 2017 by Riversoft System, all rights reserved.
 */
package com.riversoft.platform.mq.rabbitmq;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;

import com.riversoft.core.BeanFactory;
import com.riversoft.core.Config;
import com.riversoft.util.jackson.JsonMapper;

/**
 * @author woden
 *
 */
public class RabbitMQHelper {

	private static Logger logger = LoggerFactory.getLogger(RabbitMQHelper.class);
	private static RabbitMQHelper INSTANCE = new RabbitMQHelper();

	@Autowired
	private AmqpTemplate amqpTemplate;

	private RabbitMQHelper() {
		CachingConnectionFactory connectionFactory = BeanFactory.getInstance().getBean(CachingConnectionFactory.class);
		connectionFactory.setUsername(Config.get("rabbitmq.username"));
		connectionFactory.setPassword(Config.get("rabbitmq.password"));
		connectionFactory.setHost(Config.get("rabbitmq.ip"));
		connectionFactory.setPort(Integer.parseInt(Config.get("rabbitmq.port")));
		connectionFactory.setVirtualHost(Config.get("rabbitmq.vhost"));
		amqpTemplate = BeanFactory.getInstance().getBean(RabbitTemplate.class, connectionFactory);
	}

	public static RabbitMQHelper getInstance() {
		return INSTANCE;
	}

	public void send(String queueKey, Object obj, final int i) {
		try {

			amqpTemplate.convertAndSend(queueKey, (Object) JsonMapper.defaultMapper().toJson(obj),
					new MessagePostProcessor() {
						@Override
						public Message postProcessMessage(Message message) {
							message.getMessageProperties().setPriority(i);
							return message;
						}
					});
		} catch (Exception e) {
			logger.error("发送MQ队列失败", e);
		}
	}

	public void send(String queueKey, Object obj) {
		try {
			amqpTemplate.convertAndSend(queueKey, JsonMapper.defaultMapper().toJson(obj));
		} catch (Exception e) {
			logger.error("发送MQ队列失败", e);
		}
	}
}
