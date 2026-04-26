/*
 * $HeadURL: $
 * $Id: $
 * Copyright (c) 2015 by Riversoft System, all rights reserved.
 */
package com.riversoft.platform.script.function;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;

import com.riversoft.core.exception.ExceptionType;
import com.riversoft.core.exception.SystemRuntimeException;
import com.riversoft.core.script.annotation.ScriptSupport;
import com.riversoft.platform.script.ScriptHelper;
import com.riversoft.platform.web.FileManager;
import com.riversoft.platform.web.FileManager.UploadFile;
import com.riversoft.util.excel.ListParserImpl;
import com.riversoft.util.excel.MapParserImpl;
import com.riversoft.util.excel.TitleListParserImpl;

/**
 * Excel处理函数库
 * 
 * @author woden
 */
@ScriptSupport("excel")
public class ExcelHelper {

	/**
	 * 默认表头
	 */
	private static final String[] FIELDS = new String[] { "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z", "AA", "AB",
			"AC", "AD", "AE", "AF", "AG", "AH", "AI", "AJ", "AK", "AL", "AM", "AN", "AO", "AP", "AQ", "AR", "AS", "AT", "AU", "AV", "AW", "AX", "AY", "AZ", "BA", "BB", "BC", "BD", "BE", "BF", "BG",
			"BH", "BI", "BJ", "BK", "BL", "BM", "BN", "BO", "BP", "BQ", "BR", "BS", "BT", "BU", "BV", "BW", "BX", "BY", "BZ", "CA", "CB", "CC", "CD", "CE", "CF", "CG", "CH", "CI", "CJ", "CK", "CL",
			"CM", "CN", "CO", "CP", "CQ", "CR", "CS", "CT", "CU", "CV", "CW", "CX", "CY", "CZ", "DA", "DB", "DC", "DD", "DE", "DF", "DG", "DH", "DI", "DJ", "DK", "DL", "DM", "DN", "DO", "DP", "DQ",
			"DR", "DS", "DT", "DU", "DV", "DW", "DX", "DY", "DZ" };

	/**
	 * 解析文件-列表
	 * 
	 * @param file
	 * @param sheetName
	 * @param endChecker
	 * @param titleRow
	 * @param fields
	 * @return
	 */
	public static List<Map<String, String>> parseSheetListWithEnd(Object file, String sheetName, String endChecker, int titleRow, String... fields) {
		try {
			InputStream is = null;
			if (file instanceof byte[]) {
				List<UploadFile> fileList = FileManager.toFiles((byte[]) file);
				if (fileList == null || fileList.size() < 1) {
					throw new SystemRuntimeException(ExceptionType.SCRIPT, "解析文件不存在.");
				}
				is = fileList.get(0).getInputStream();
			} else if (file instanceof InputStream) {
				is = (InputStream) file;
			} else if (file instanceof File) {
				is = new FileInputStream((File) file);
			} else {
				throw new SystemRuntimeException(ExceptionType.SCRIPT, "解析文件不符合格式.");
			}

			List<Map<String, String>> list = new ListParserImpl(Arrays.asList(fields), sheetName, titleRow, endChecker).parse(is).getResult();
			return list;
		} catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException | InvalidFormatException | IOException e) {
			throw new SystemRuntimeException(e);
		}
	}

	/**
	 * 解析文件-列表
	 * 
	 * @param file
	 * @param sheetName
	 * @param titleRow
	 * @param fields
	 * @return
	 */
	public static List<Map<String, String>> parseSheetList(Object file, String sheetName, int titleRow, String... fields) {
		return parseSheetListWithEnd(file, sheetName, "", titleRow, fields);
	}

	/**
	 * 解析文件-列表
	 * 
	 * @param file
	 * @param sheetName
	 * @param titleRow
	 * @return
	 */
	public static List<Map<String, String>> parseSheetList(Object file, String sheetName, int titleRow) {
		return parseSheetList(file, sheetName, titleRow, FIELDS);
	}

	/**
	 * 解析文件-列表
	 * 
	 * @param file
	 * @param sheetName
	 * @param fields
	 * @return
	 */
	public static List<Map<String, String>> parseSheetList(Object file, String sheetName, String... fields) {
		return parseSheetList(file, sheetName, -1, fields);
	}

	/**
	 * 解析文件-列表
	 * 
	 * @param file
	 * @param sheetName
	 * @return
	 */
	public static List<Map<String, String>> parseSheetList(Object file, String sheetName) {
		return parseSheetList(file, sheetName, FIELDS);
	}

	/**
	 * 解析文件-列表
	 * 
	 * @param file
	 * @param fields
	 * @return
	 */
	public static List<Map<String, String>> parseList(Object file, String... fields) {
		return parseSheetList(file, null, -1, fields);
	}

	/**
	 * 解析文件-列表
	 * 
	 * @param file
	 * @return
	 */
	public static List<Map<String, String>> parseList(Object file) {
		return parseList(file, FIELDS);
	}

	/**
	 * 解析文件-列表
	 * 
	 * @param file
	 * @param titleRow
	 * @param fields
	 * @return
	 */
	public static List<Map<String, String>> parseList(Object file, int titleRow, String... fields) {
		return parseSheetList(file, null, titleRow, fields);
	}

	/**
	 * 解析文件-列表
	 * 
	 * @param file
	 * @param titleRow
	 * @return
	 */
	public static List<Map<String, String>> parseList(Object file, int titleRow) {
		return parseList(file, titleRow, FIELDS);
	}

	/**
	 * 解析文件-按表头生成列表
	 * 
	 * @param file
	 * @param sheetName
	 * @param endChecker
	 * @param titleRow
	 * @return
	 */
	public static List<Map<String, String>> parseTitleListWhithEnd(Object file, String sheetName, String endChecker, int titleRow) {
		try {
			InputStream is = null;
			if (file instanceof byte[]) {
				List<UploadFile> fileList = FileManager.toFiles((byte[]) file);
				if (fileList == null || fileList.size() < 1) {
					throw new SystemRuntimeException(ExceptionType.SCRIPT, "解析文件不存在.");
				}
				is = fileList.get(0).getInputStream();
			} else if (file instanceof InputStream) {
				is = (InputStream) file;
			} else if (file instanceof File) {
				is = new FileInputStream((File) file);
			} else {
				throw new SystemRuntimeException(ExceptionType.SCRIPT, "解析文件不符合格式.");
			}

			List<Map<String, String>> list = new TitleListParserImpl(sheetName, titleRow, endChecker).parse(is).getResult();
			return list;
		} catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException | InvalidFormatException | IOException e) {
			throw new SystemRuntimeException(e);
		}
	}

	/**
	 * 解析文件-按表头生成列表
	 * 
	 * @param file
	 * @param titleRow
	 * @return
	 */
	public static List<Map<String, String>> parseTitleList(Object file, int titleRow) {
		return parseTitleListWhithEnd(file, null, "", titleRow);
	}

	/**
	 * 解析文件-按表头生成列表
	 * 
	 * @param file
	 * @return
	 */
	public static List<Map<String, String>> parseTitleList(Object file) {
		return parseTitleList(file, -1);
	}

	/**
	 * 解析文件-按表头生成列表
	 * 
	 * @param file
	 * @param sheetName
	 * @param titleRow
	 * @return
	 */
	public static List<Map<String, String>> parseSheetTitleList(Object file, String sheetName, int titleRow) {
		return parseTitleListWhithEnd(file, sheetName, "", titleRow);
	}

	/**
	 * 解析文件-按表头生成列表
	 * 
	 * @param file
	 * @param sheetName
	 * @return
	 */
	public static List<Map<String, String>> parseSheetTitleList(Object file, String sheetName) {
		return parseSheetTitleList(file, sheetName, -1);
	}

	/**
	 * 解析文件-MAP
	 * 
	 * @param file
	 * @param sheetName
	 * @param fields
	 * @return
	 */
	public static Map<String, String> parseSheetMap(Object file, String sheetName, String... fields) {

		try {
			InputStream is = null;
			if (file instanceof byte[]) {
				List<UploadFile> fileList = FileManager.toFiles((byte[]) file);
				if (fileList == null || fileList.size() < 1) {
					throw new SystemRuntimeException(ExceptionType.SCRIPT, "解析文件不存在.");
				}
				is = fileList.get(0).getInputStream();
			} else if (file instanceof InputStream) {
				is = (InputStream) file;
			} else if (file instanceof File) {
				is = new FileInputStream((File) file);
			} else {
				throw new SystemRuntimeException(ExceptionType.SCRIPT, "解析文件不符合格式.");
			}
			// 没有字段则无需要解析
			if (fields == null | fields.length < 1) {
				return new HashMap<>();
			} else {
				return new MapParserImpl(Arrays.asList(fields), sheetName).parse(is).getResult();
			}
		} catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException | InvalidFormatException | IOException e) {
			throw new SystemRuntimeException(e);
		}
	}

	/**
	 * 解析文件-MAP
	 * 
	 * @param file
	 * @param fields
	 * @return
	 */
	public static Map<String, String> parseMap(Object file, String... fields) {
		return parseSheetMap(file, null, fields);
	}

	/**
	 * 生成文件
	 * 
	 * @param template
	 * @param fileName
	 * @param context
	 * @return
	 */
	public static byte[] toFile(Object template, String fileName, Map<String, Object> context) {
		try {
			InputStream is = null;
			if (template instanceof byte[]) {
				List<UploadFile> fileList = FileManager.toFiles((byte[]) template);
				if (fileList == null || fileList.size() < 1) {
					throw new SystemRuntimeException(ExceptionType.SCRIPT, "解析文件不存在.");
				}
				is = fileList.get(0).getInputStream();
				if (StringUtils.isEmpty(fileName)) {
					fileName = fileList.get(0).getName();
				}
			} else if (template instanceof InputStream) {
				is = (InputStream) template;
			} else if (template instanceof File) {
				is = new FileInputStream((File) template);
			} else {
				throw new SystemRuntimeException(ExceptionType.SCRIPT, "解析文件不符合格式.");
			}

			try (ByteArrayOutputStream out = new ByteArrayOutputStream();) {
				if (StringUtils.isEmpty(fileName)) {
					// 随机文件名
					fileName = "AUTO_" + SequenceHelper.pattern("{now}", "yyyyMMddHHmmss") + ".xls";
				}

				ScriptHelper.export(out, is, context);
				FileManager.UploadFile file = new FileManager.DevFile(fileName, out.toByteArray());
				return FileManager.toBytes(null, Arrays.asList(file));
			}

		} catch (IOException e) {
			throw new SystemRuntimeException(e);
		}
	}

	/**
	 * 生成文件
	 * 
	 * @param template
	 * @param context
	 * @return
	 */
	public static byte[] toFile(Object template, Map<String, Object> context) {
		return toFile(template, null, context);
	}

}
