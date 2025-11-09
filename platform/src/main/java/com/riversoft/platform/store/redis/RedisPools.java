/*
 * $HeadURL: $
 * $Id: $
 * Copyright (c) 2017 by Riversoft System, all rights reserved.
 */
package com.riversoft.platform.store.redis;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;

import com.riversoft.core.BeanFactory;
import com.riversoft.core.Config;
import com.riversoft.core.exception.ExceptionType;
import com.riversoft.core.exception.SystemRuntimeException;

import redis.clients.jedis.JedisPoolConfig;

/**
 * redis连接池管理器
 * 
 * @author woden
 *
 */
public final class RedisPools {

	private static Logger logger = LoggerFactory.getLogger(RedisPools.class);
	private static final RedisPools INSTANCE = new RedisPools();

	/**
	 * 获取redis池单例
	 * 
	 * @return
	 */
	public static RedisPools getInstance() {
		return INSTANCE;
	}

	/**
	 * 链接池初始化
	 * 
	 * @param key
	 * @throws Exception
	 */
	private StringRedisTemplate init(String key) {

		if (!StringUtils.equalsIgnoreCase("true", Config.get("redis.flag"))) {
			throw new SystemRuntimeException(ExceptionType.CONFIG, "未打开redis配置开关.");
		}

		String pixel;// 配置文件前缀
		if (StringUtils.isEmpty(key)) {
			pixel = "redis";
			key = "";
		} else {
			pixel = "redis." + key;
		}

		// 池基本配置
		JedisPoolConfig config = new JedisPoolConfig();
		// 预设置参数

		// 最大链接数
		if (StringUtils.isNotEmpty(Config.get(pixel + ".maxTotal"))) {
			logger.info("设置参数最大链接数为{}", Config.get(pixel + ".maxTotal"));
			try {
				int MaxTotal = (int) Integer.parseInt(Config.get(pixel + ".maxTotal"));
				config.setMaxTotal(MaxTotal);
			} catch (ClassCastException e) {
				e.printStackTrace();
				logger.info("设置参数最大连接数失败");
			}
		}

		// 最大空闲资源数
		if (StringUtils.isNotEmpty(Config.get(pixel + ".maxIdle"))) {
			logger.info("设置最大空闲资源数为{}", Config.get(pixel + ".maxIdle"));
			try {
				int MaxIdle = (int) Integer.parseInt(Config.get(pixel + ".maxIdle"));
				config.setMaxIdle(MaxIdle);
			} catch (ClassCastException e) {
				e.printStackTrace();
				logger.info("设置最大空闲资源数失败");
			}
		}

		// 最小空闲资源数
		if (StringUtils.isNotEmpty(Config.get(pixel + ".minIdle"))) {
			logger.info("设置最小空闲资源数为{}", Config.get(pixel + ".minIdle"));
			try {
				int MinIdle = (int) Integer.parseInt(Config.get(pixel + ".minIdle"));
				config.setMinIdle(MinIdle);
			} catch (ClassCastException e) {
				e.printStackTrace();
				logger.info("设置最小空闲资源数失败");
			}
		}

		// 最大等待时间
		if (StringUtils.isNotEmpty(Config.get(pixel + ".maxWaitMillis"))) {
			logger.info("设置最大等待时间为{}", Config.get(pixel + ".maxWaitMillis"));
			try {
				int MaxWaitMillis = (int) Integer.parseInt(Config.get(pixel + ".maxWaitMillis"));
				config.setMaxWaitMillis(MaxWaitMillis);
			} catch (ClassCastException e) {
				e.printStackTrace();
				logger.info("设置最大等待时间失败");
			}
		}

		// 是否提前进行validate操作
		if (StringUtils.isNotEmpty(Config.get(pixel + ".testOnBorrow"))) {
			logger.info("设置是否提前进行validate操作", Config.get(pixel + ".testOnBorrow"));
			try {
				Boolean TestOnBorrow = (Boolean) Boolean.valueOf(Config.get(pixel + ".testOnBorrow"));
				config.setTestOnBorrow(TestOnBorrow);
			} catch (ClassCastException e) {
				e.printStackTrace();
				logger.info("是否提前进行validate操作失败");
			}
		}

		// 当调用return Object方法时，是否进行有效性检查
		if (StringUtils.isNotEmpty(Config.get(pixel + ".testOnReturn"))) {
			logger.info("当调用return Object方法时，是否进行有效性检查", Config.get(pixel + ".testOnReturn"));
			try {
				Boolean TestOnReturn = (Boolean) Boolean.valueOf(Config.get(pixel + ".testOnReturn"));
				config.setTestOnReturn(TestOnReturn);
			} catch (ClassCastException e) {
				e.printStackTrace();
				logger.info("当调用return Object方法时，是否进行有效性检查失败");
			}
		}

		String ip = Config.get(pixel + ".ip");
		if (StringUtils.isEmpty(ip)) {
			throw new SystemRuntimeException(ExceptionType.CONFIG, "redis Ip不能为空!");
		}

		int port = Integer.parseInt(Config.get(pixel + ".port", "6379"));
		logger.info("redis实例{},ip为{},port为{}", pixel, ip, port);

		int timeout = Integer.parseInt(Config.get(pixel + ".timeout", "2000"));
		String password = Config.get(pixel + ".password");
		int dbIndex = Integer.parseInt(Config.get(pixel + ".dbIndex", "0"));

		Map<String, Object> factoryProp = new HashMap<>();
		factoryProp.put("poolConfig", config);
		factoryProp.put("hostName", ip);
		factoryProp.put("port", port);
		factoryProp.put("password", password);
		factoryProp.put("timeout", timeout);
		factoryProp.put("database", dbIndex);
		JedisConnectionFactory jedisConnectionFactory = BeanFactory.getInstance().getBean("JedisConnectionFactory-" + key, JedisConnectionFactory.class, false, true, null, factoryProp);

		Map<String, Object> templateProp = new HashMap<>();
		templateProp.put("connectionFactory", jedisConnectionFactory);
		StringRedisTemplate redisTemplate = BeanFactory.getInstance().getBean("redisTemplate-" + key, StringRedisTemplate.class, false, true, null, templateProp);

		return redisTemplate;
	}

	public RedisHelper getRedis(String key) {
		RedisHelper ins = RedisHelper.getInstance(key);
		if (ins != null) {
			return ins;
		}

		// 初始化
		StringRedisTemplate redisTemplate = init(key);
		RedisHelper.init(key, redisTemplate);
		return RedisHelper.getInstance(key);
	}

	public static class RedisHelper {
		private String key;
		private StringRedisTemplate redisTemplate;
		private static Map<String, RedisHelper> INS_MAP = new HashMap<>();// 多例模式

		static synchronized void init(String key, StringRedisTemplate redisTemplate) {
			RedisHelper ins = new RedisHelper();
			ins.redisTemplate = redisTemplate;
			ins.key = key;
			if (StringUtils.isEmpty(key)) {
				INS_MAP.put("_key_", ins);
			} else {
				INS_MAP.put(key, ins);
			}
		}

		static RedisHelper getInstance(String key) {
			if (StringUtils.isEmpty(key)) {
				return INS_MAP.get("_key_");
			}
			return INS_MAP.get(key);
		}

		/**
		 * 获取模板
		 * 
		 * @return
		 */
		public StringRedisTemplate getTemplate() {
			return redisTemplate;
		}

		public StringRedisTemplate template() {
			return redisTemplate;
		}

		/* ===========包装方法============ */

		public void set(String key, String value) {
			redisTemplate.opsForValue().set(key, value);
		}

		public void set(String key, String value, long timeout) {
			redisTemplate.opsForValue().set(key, value, timeout, TimeUnit.SECONDS);
		}

		public String get(String key) {
			return redisTemplate.opsForValue().get(key);
		}

		public String getAndSet(String key, String value) {
			return redisTemplate.opsForValue().getAndSet(key, value);
		}

		public void delete(String key) {
			redisTemplate.delete(key);
		}

	}
}
