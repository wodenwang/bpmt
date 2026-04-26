/*
 * $HeadURL: $
 * $Id: $
 * Copyright (c) 2013 by Wodensoft System, all rights reserved.
 */
package com.riversoft.platform.office;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;

import org.artofsolving.jodconverter.OfficeDocumentConverter;
import org.artofsolving.jodconverter.office.ExternalOfficeManagerConfiguration;
import org.artofsolving.jodconverter.office.OfficeException;
import org.artofsolving.jodconverter.office.OfficeManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.riversoft.core.Config;

/**
 * @author Woden
 * 
 */
public class ConverterHelper {
	/**
	 * Logger for this class
	 */
	private static final Logger logger = LoggerFactory.getLogger(ConverterHelper.class);

	private static ConverterHelper instance = new ConverterHelper();

	private OfficeManager officeManager = null;
	private OfficeDocumentConverter converter = null;

	private ConverterHelper() {
		init();
	}

	/**
	 * 初始化
	 */
	private synchronized void init() {
		ExternalOfficeManagerConfiguration configuration = new ExternalOfficeManagerConfiguration();
		int port;
		try {
			port = Integer.parseInt(Config.get("office.port", "-1"));
		} catch (Exception e) {
			port = -1;
		}
		if (port > 0) {
			configuration.setPortNumber(port);
		}

		officeManager = configuration.buildOfficeManager();
		converter = new OfficeDocumentConverter(officeManager);
	}

	/**
	 * 是否允许使用office服务
	 * 
	 * @return
	 */
	private static boolean isAllow() {
		return "true".equalsIgnoreCase(Config.get("office.flag"));
	}

	/**
	 * 校验服务是否运行
	 * 
	 * @return
	 */
	public static boolean touch() {
		try {
			instance.officeManager.start();
			return instance.officeManager.isRunning();
		} catch (OfficeException e) {
			return false;
		}
	}

	/**
	 * 服务重置
	 */
	public static void reset() {
		instance.init();
	}

	/**
	 * office文件格式转换
	 * 
	 * @param in
	 * @param inputPixel
	 * @param out
	 * @param outPixel
	 * @return
	 */
	public static boolean convert(InputStream in, String inputPixel, OutputStream out, String outPixel) {
		if (!isAllow()) {
			return false;
		}
		try {
			instance.converter.convert(in, inputPixel, out, outPixel);
			return true;
		} catch (OfficeException e) {
			logger.error("office文件格式转换失败:", e);
			return false;
		}
	}

	/**
	 * office文件格式转换
	 * 
	 * @param in
	 * @param out
	 * @return
	 */
	public static boolean convert(File in, File out) {
		if (!isAllow()) {
			return false;
		}
		try {
			instance.converter.convert(in, out);
			return true;
		} catch (OfficeException e) {
			logger.error("office文件格式转换失败:", e);
			return false;
		}
	}

}
