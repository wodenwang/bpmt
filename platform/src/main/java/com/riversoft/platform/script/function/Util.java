/*
 * $HeadURL: $
 * $Id: $
 * Copyright (c) 2013 by Riversoft System, all rights reserved.
 */
package com.riversoft.platform.script.function;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipFile;
import org.apache.commons.compress.utils.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.tools.zip.ZipEntry;
import org.apache.tools.zip.ZipOutputStream;

import com.riversoft.core.exception.ExceptionType;
import com.riversoft.core.exception.SystemRuntimeException;
import com.riversoft.core.script.annotation.ScriptSupport;
import com.riversoft.platform.web.FileManager;
import com.riversoft.platform.web.FileManager.UploadFile;
import com.riversoft.util.Formatter;

/**
 * @author woden
 * 
 */
@ScriptSupport("util")
public class Util {

	/**
	 * 计算两个时间之间的差值
	 * 
	 * @param date1
	 * @param date2
	 * @return
	 */
	public static Long compareDate(Date date1, Date date2) {
		return compareDate(date1, date2, "D");
	}

	/**
	 * 计算两个时间之间的差值
	 * 
	 * @param date1
	 * @param date2
	 * @param pattern
	 * @return
	 */
	public static Long compareDate(Date date1, Date date2, String pattern) {
		Long gap = date1.getTime() - date2.getTime();
		switch (pattern) {
		case "Y":
		case "y":
			return diffYear(date1, date2);
		case "M":
			return diffMonth(date1, date2);
		case "D":
			return gap / 1000 / 60 / 60 / 24;
		case "d":
			return gap / 1000 / 60 / 60 / 24;
		case "H":
			return gap / 1000 / 60 / 60;
		case "h":
			return gap / 1000 / 60 / 60;
		case "m":
			return gap / 1000 / 60;
		case "s":// 秒
			return gap / 1000;
		case "S":// 毫秒
			return gap;
		default:// 默认是天
			return gap / 1000 / 60 / 60 / 24;
		}
	}

	private static Long diffYear(Date date1, Date date2) {
		Calendar calendar1 = Calendar.getInstance();
		calendar1.setTime(date1);
		int year1 = calendar1.get(Calendar.YEAR);

		Calendar calendar2 = Calendar.getInstance();
		calendar2.setTime(date2);
		int year2 = calendar2.get(Calendar.YEAR);

		int diff = year1 - year2;
		return Long.valueOf(diff);
	}

	private static Long diffMonth(Date date1, Date date2) {
		Calendar calendar1 = Calendar.getInstance();
		calendar1.setTime(date1);
		int year1 = calendar1.get(Calendar.YEAR);
		int month1 = calendar1.get(Calendar.MONTH);

		Calendar calendar2 = Calendar.getInstance();
		calendar2.setTime(date2);
		int year2 = calendar2.get(Calendar.YEAR);
		int month2 = calendar2.get(Calendar.MONTH);

		int diff = (year1 - year2) * 12 + (month1 - month2);
		return Long.valueOf(diff);
	}

	/**
	 * 日期加减计算(单位:天)
	 * 
	 * @param date
	 * @param offset
	 * @return
	 */
	public static Date calDate(Date date, Integer offset) {
		return calDate(date, offset, null);
	}

	/**
	 * 日期加减计算
	 * 
	 * @param date
	 *            待计算时间
	 * @param offset
	 *            正数为加,复数为减
	 * @param pattern
	 *            单位,默认为天
	 * @return
	 */
	public static Date calDate(Date date, Integer offset, String pattern) {
		if (pattern == null) {
			// 日
			return DateUtils.addDays(date, offset);
		} else {
			switch (pattern) {
			case "Y":
			case "y":
				return DateUtils.addYears(date, offset);
			case "M":
				return DateUtils.addMonths(date, offset);
			case "D":
			case "d":
				return DateUtils.addDays(date, offset);
			case "H":
			case "h":
				return DateUtils.addHours(date, offset);
			case "m":
				return DateUtils.addMinutes(date, offset);
			case "s":
				return DateUtils.addSeconds(date, offset);
			case "S":
				return DateUtils.addMilliseconds(date, offset);
			default:
				// 日
				return DateUtils.addDays(date, offset);
			}
		}
	}

	/**
	 * 文件解压
	 * 
	 * @param obj
	 */
	public static Map<String, Object> unzip(Object obj) {
		try {
			File file;
			if (obj instanceof byte[]) {
				List<FileManager.UploadFile> fileList = FileManager.toFiles((byte[]) obj);
				if (fileList == null || fileList.size() < 1) {
					throw new SystemRuntimeException(ExceptionType.SCRIPT, "解析文件不存在.");
				}
				file = File.createTempFile(fileList.get(0).getName() + "_", ".zip");
				try (FileOutputStream zipFileOs = new FileOutputStream(file);) {
					IOUtils.copy(fileList.get(0).getInputStream(), zipFileOs);
				}
			} else if (obj instanceof InputStream) {
				file = File.createTempFile("_TMP_", ".zip");
				try (FileOutputStream zipFileOs = new FileOutputStream(file);) {
					IOUtils.copy((InputStream) obj, zipFileOs);
				}
			} else if (obj instanceof File) {
				file = (File) obj;
			} else if (obj instanceof UploadFile) {
				file = File.createTempFile(((UploadFile) obj).getName() + "_", ".zip");
				try (FileOutputStream zipFileOs = new FileOutputStream(file);) {
					IOUtils.copy(((UploadFile) obj).getInputStream(), zipFileOs);
				}
			} else {
				throw new SystemRuntimeException(ExceptionType.SCRIPT, "解析文件不符合格式.");
			}

			Map<String, Object> result = new HashMap<>();

			try (ZipFile zipFile = new ZipFile(file, System.getProperty("sun.jnu.encoding"));) {
				Enumeration<ZipArchiveEntry> e = zipFile.getEntries();
				while (e.hasMoreElements()) {
					ZipArchiveEntry entry = e.nextElement();
					if (entry.isDirectory()) {// 文件夹
						continue;
					}

					try (InputStream ins = zipFile.getInputStream(entry);) {
						UploadFile uploadFile = new FileManager.DevFile(StringUtils.substring(entry.getName(), StringUtils.lastIndexOf(entry.getName(), "/") + 1), IOUtils.toByteArray(ins));
						result.put(entry.getName(), uploadFile);
					}
				}
			}

			return result;
		} catch (Exception e) {
			throw new SystemRuntimeException(e);
		}
	}

	/**
	 * 压缩附件
	 * 
	 * @param objs
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static File zip(Object obj) {
		try {
			File result = File.createTempFile(Formatter.formatDatetime(new Date(), "yyyyMMddHHmmss"), ".zip");
			if (obj == null) {
				return result;
			}

			try (ZipOutputStream zos = new ZipOutputStream(result)) {
				zos.setEncoding(System.getProperty("sun.jnu.encoding"));
				if (obj instanceof Map) {// map压缩
					Map<String, Object> map = (Map<String, Object>) obj;
					for (Map.Entry<String, Object> entry : map.entrySet()) {
						zos.putNextEntry(new ZipEntry(entry.getKey()));
						if (entry.getValue() instanceof byte[]) {
							List<FileManager.UploadFile> fileList = FileManager.toFiles((byte[]) entry.getValue());
							if (fileList == null || fileList.size() < 1) {
								throw new SystemRuntimeException(ExceptionType.SCRIPT, "解析文件不存在.");
							}
							IOUtils.copy(fileList.get(0).getInputStream(), zos);
						} else if (entry.getValue() instanceof File) {
							try (InputStream in = new FileInputStream((File) entry.getValue())) {
								IOUtils.copy(in, zos);
							}
						} else if (entry.getValue() instanceof UploadFile) {
							IOUtils.copy(((UploadFile) entry.getValue()).getInputStream(), zos);
						} else {
							throw new SystemRuntimeException(ExceptionType.SCRIPT, "解析文件不符合格式.");
						}
					}
				} else {// 普通附件
					if (obj instanceof byte[]) {
						List<FileManager.UploadFile> fileList = FileManager.toFiles((byte[]) obj);
						if (fileList == null || fileList.size() < 1) {
							throw new SystemRuntimeException(ExceptionType.SCRIPT, "解析文件不存在.");
						}
						for (UploadFile uploadFile : fileList) {
							zos.putNextEntry(new ZipEntry(uploadFile.getName()));
							IOUtils.copy(uploadFile.getInputStream(), zos);
						}
					} else if (obj instanceof File) {
						zos.putNextEntry(new ZipEntry(((File) obj).getName()));
						try (InputStream in = new FileInputStream((File) obj)) {
							IOUtils.copy(in, zos);
						}
					} else {
						throw new SystemRuntimeException(ExceptionType.SCRIPT, "解析文件不符合格式.");
					}
				}
			}
			return result;
		} catch (IOException e) {
			throw new SystemRuntimeException(e);
		}
	}

	/**
	 * url encode
	 * 
	 * @param value
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	public static String urlEncode(String value) throws UnsupportedEncodingException {
		String result = URLEncoder.encode(value, "UTF-8");
		return result;
	}
}
