/*
 * $HeadURL: $
 * $Id: $
 * Copyright (c) 2015 by Riversoft System, all rights reserved.
 */
package com.riversoft.platform.script.function;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.riversoft.core.script.annotation.ScriptSupport;
import com.riversoft.platform.web.WebLogManager;

/**
 * @author woden
 * 
 */
@ScriptSupport("log")
public class LoggerHelper {
	/**
	 * Logger for this class
	 */
	private static final Logger logger = LoggerFactory.getLogger("script.log");

	/**
	 * 登记后台日志
	 * 
	 * @param msg
	 * @param args
	 */
	public static void debug(String msg, Object... args) {
		logger.debug(msg, args);
	}

	/**
	 * 登记后台日志
	 * 
	 * @param msg
	 * @param args
	 */
	public static void info(String msg, Object... args) {
		logger.info(msg, args);
	}

	/**
	 * 登记后台日志
	 * 
	 * @param msg
	 * @param args
	 */
	public static void warn(String msg, Object... args) {
		logger.warn(msg, args);
	}

	/**
	 * 登记后台日志
	 * 
	 * @param msg
	 * @param args
	 */
	public static void error(String msg, Object... args) {
		logger.error(msg, args);
	}

	/**
	 * 登记web日志
	 * 
	 * @param msg
	 */
	public static void print(String msg) {
		WebLogManager.log(msg);
	}

	/**
	 * 开始循环
	 * 
	 * @param msg
	 * @param max
	 */
	public static void loop(String msg, int max) {
		WebLogManager.beginLoop(msg, max);
	}

	/**
	 * 循环标记
	 */
	public static void signal() {
		WebLogManager.signalLoop();
	}
}
