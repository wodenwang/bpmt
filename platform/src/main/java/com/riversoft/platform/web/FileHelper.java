/*
 * $HeadURL: $
 * $Id: $
 * Copyright (c) 2016 by Riversoft System, all rights reserved.
 */
package com.riversoft.platform.web;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import com.riversoft.core.context.RequestContext;
import com.riversoft.core.exception.ExceptionType;
import com.riversoft.core.exception.SystemRuntimeException;
import com.riversoft.core.script.annotation.ScriptSupport;
import com.riversoft.platform.web.FileManager.UploadFile;

/**
 * 文件函数库
 * 
 * @author woden
 */
@ScriptSupport("file")
public class FileHelper {

	/**
	 * 将文件转换成disk类型(存放在磁盘)
	 * 
	 * @param file
	 * @return
	 */
	public static byte[] disk(Object file) {
		if (file == null) {
			return null;
		}

		List<UploadFile> list;
		if (file instanceof List) {
			list = (List<UploadFile>) file;
		} else if (file instanceof UploadFile) {
			list = new ArrayList<>();
			list.add((UploadFile) file);
		} else if (file instanceof byte[]) {
			list = files((byte[]) file);
		} else {
			throw new SystemRuntimeException(ExceptionType.CODING, "不支持文件类型[" + file.getClass().getName() + "].");
		}

		List<UploadFile> targetList = new ArrayList<>();
		for (UploadFile o : list) {
			if (StringUtils.equals(FileManager.DISK_MODE, o.getMode())) {// disk模式
				targetList.add(o);
			} else {
				o.setMode(FileManager.DISK_MODE);
				targetList.add(o);
			}
		}

		return FileManager.toBytes(UUID.randomUUID().toString(), targetList, "_DEV_FILE");
	}

	/**
	 * 将文件转换成db类型(存放在数据库)
	 * 
	 * @param file
	 * @return
	 */
	public static byte[] db(Object file) {
		if (file == null) {
			return null;
		}

		List<UploadFile> list;
		if (file instanceof List) {
			list = (List<UploadFile>) file;
		} else if (file instanceof UploadFile) {
			list = new ArrayList<>();
			list.add((UploadFile) file);
		} else if (file instanceof byte[]) {
			list = files((byte[]) file);
		} else {
			throw new SystemRuntimeException(ExceptionType.CODING, "不支持文件类型[" + file.getClass().getName() + "].");
		}

		List<UploadFile> targetList = new ArrayList<>();
		for (UploadFile o : list) {
			if (StringUtils.equals(FileManager.DB_MODE, o.getMode())) {// db模式
				targetList.add(o);
			} else {
				try {
					targetList.add(FileManager.saveDbFile(o.getName(), IOUtils.toByteArray(o.getInputStream())));
				} catch (IOException e) {
					throw new SystemRuntimeException(e);
				}
			}
		}

		return FileManager.toBytes(UUID.randomUUID().toString(), targetList, "_DEV_FILE");
	}

	/**
	 * 获取可以编程的文件(列表)
	 * 
	 * @param file
	 * @return
	 */
	public static List<UploadFile> files(byte[] file) {
		return FileManager.toFiles(file);
	}

	/**
	 * 获取可以编程的文件
	 * 
	 * @param file
	 * @return
	 */
	public static UploadFile file(byte[] file) {
		List<UploadFile> list = files(file);
		if (list != null && list.size() > 0) {
			return list.get(0);
		}
		return null;
	}

	/**
	 * 获取下载链接
	 * 
	 * @param file
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public static String url(Object file) {
		if (file == null) {
			return "";
		}

		UploadFile o = null;
		if (file instanceof UploadFile) {
			o = (UploadFile) file;
		} else if (file instanceof byte[]) {
			o = file((byte[]) file);
		} else if (file instanceof String) {
			return (String) file;
		} else if (file instanceof List && ((List) file).size() > 0 && ((List) file).get(0) instanceof UploadFile) {
			o = (UploadFile) ((List) file).get(0);
		}

		if (o != null) {
			String pixel = o.getName().substring(o.getName().lastIndexOf(".") + 1);
			String url = "/widget/FileAction/download.shtml?";
			if (!ArrayUtils.contains("png|jpg|jpeg|gif|svg".split("|"), pixel.toLowerCase())) {// 图片类不加download标记
				url += "download=true&";
			}
			return url + "name=" + o.getSysName() + "&type=" + o.getType() + "&fileName=" + o.getName();
		}

		return "";
	}

	/**
	 * 生成base64字符
	 * 
	 * @param file
	 * @return
	 */
	public static String base64(Object file) {
		if (file == null) {
			return "";
		}

		UploadFile o = null;
		if (file instanceof UploadFile) {
			o = (UploadFile) file;
		} else if (file instanceof byte[]) {
			o = file((byte[]) file);
		} else if (file instanceof String) {
			return (String) file;
		} else if (file instanceof List && ((List) file).size() > 0 && ((List) file).get(0) instanceof UploadFile) {
			o = (UploadFile) ((List) file).get(0);
		}

		try {
			return "data:image/" + o.getName().substring(o.getName().lastIndexOf(".") + 1) + ";base64," + Base64.encodeBase64String(o.toBytes());
		} catch (IOException e) {
			return "";
		}
	}

	/**
	 * 从request获取文件
	 * 
	 * @param name
	 * @return
	 */
	public static List<UploadFile> request(String name) {
		return FileManager.getUploadFiles(RequestContext.getCurrent(), name);
	}
}
