/*
 * $HeadURL: $
 * $Id: $
 * Copyright (c) 2013 by Riversoft System, all rights reserved.
 */
package com.riversoft.util;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * POI工具类
 * 
 * @author Woden
 * 
 */
public class PoiUtils {

	private static final Logger logger = LoggerFactory.getLogger(PoiUtils.class);

	/**
	 * 生成文件类型
	 * 
	 * @author Woden
	 * 
	 */
	public static enum Types {
		xls(HSSFWorkbook.class), xlsx(XSSFWorkbook.class);
		private Class<? extends Workbook> type;

		private Types(Class<? extends Workbook> type) {
			this.type = type;
		}
	}

	/**
	 * excel列表数据生成(excel2003及之前格式)
	 * 
	 * @param out
	 * @param fields
	 * @param titles
	 * @param list
	 * @throws IOException
	 */
	public static void exportListWithExcel2003(OutputStream out, String[] fields, Map<String, String> titles, List<HashMap<String, Object>> list) throws IOException {
		exportList(out, fields, titles, list, Types.xls);
	}

	/**
	 * excel列表数据生成(excel2007及之后格式)
	 * 
	 * @param out
	 * @param fields
	 * @param titles
	 * @param list
	 * @throws IOException
	 */
	public static void exportListWithExcel2007(OutputStream out, String[] fields, Map<String, String> titles, List<HashMap<String, Object>> list) throws IOException {
		exportList(out, fields, titles, list, Types.xlsx);
	}

	/**
	 * excel列表数据生成
	 * 
	 * @param out
	 * @param fields
	 * @param titles
	 * @param list
	 * @param type
	 * @throws IOException
	 */
	public static void exportList(OutputStream out, String[] fields, Map<String, String> titles, List<HashMap<String, Object>> list, Types type) throws IOException {
		try {
			exportList(out, fields, titles, list, type.type);
		} catch (InstantiationException | IllegalAccessException e) {
			// ignore.
		}
	}

	/**
	 * 标题样式
	 * 
	 * @param workbook
	 * @return
	 */
	public static CellStyle createHeadStyle(Workbook workbook) {
		// 标题样式
		CellStyle headStyle = workbook.createCellStyle();
		{
			headStyle.setFillForegroundColor(HSSFColor.YELLOW.index);
			headStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
			headStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN);
			headStyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);
			headStyle.setBorderRight(HSSFCellStyle.BORDER_THIN);
			headStyle.setBorderTop(HSSFCellStyle.BORDER_THIN);
			headStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);
			{
				// 设置首行字体
				Font font = workbook.createFont();
				font.setColor(HSSFColor.BLACK.index);
				font.setFontHeightInPoints((short) 12);
				font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
				headStyle.setFont(font);
			}
		}

		return headStyle;
	}

	/**
	 * 内容样式
	 * 
	 * @param workbook
	 * @return
	 */
	public static CellStyle createContentStyle(Workbook workbook) {
		// 内容样式
		CellStyle style = workbook.createCellStyle();
		{
			style.setFillForegroundColor(HSSFColor.WHITE.index);
			style.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
			style.setBorderBottom(HSSFCellStyle.BORDER_THIN);
			style.setBorderLeft(HSSFCellStyle.BORDER_THIN);
			style.setBorderRight(HSSFCellStyle.BORDER_THIN);
			style.setBorderTop(HSSFCellStyle.BORDER_THIN);
			style.setAlignment(HSSFCellStyle.ALIGN_CENTER);
			style.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
			{
				Font font = workbook.createFont();
				font.setBoldweight(HSSFFont.BOLDWEIGHT_NORMAL);
				style.setFont(font);
			}
		}
		return style;
	}

	/**
	 * excel列表数据生成
	 * 
	 * @param out
	 * @param fields
	 * @param titles
	 * @param list
	 * @param type
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws IOException
	 */
	public static void exportList(OutputStream out, String[] fields, Map<String, String> titles, List<HashMap<String, Object>> list, Class<? extends Workbook> type)
			throws InstantiationException, IllegalAccessException, IOException {

		// 声明一个工作薄
		Workbook workbook = type.newInstance();

		// 生成一个表格
		Sheet sheet = workbook.createSheet("data");

		// 设置表格默认列宽度为15个字节
		sheet.setDefaultColumnWidth((short) 15);

		// 标题样式
		CellStyle headStyle = createHeadStyle(workbook);
		// 高亮标题样式
		CellStyle lightHeadStyle = workbook.createCellStyle();
		{
			lightHeadStyle.cloneStyleFrom(headStyle);
		}

		// 内容样式
		CellStyle style = createContentStyle(workbook);
		// 高亮内容样式
		CellStyle lightStyle = workbook.createCellStyle();
		{
			lightStyle.cloneStyleFrom(style);
			lightStyle.setFillForegroundColor(HSSFColor.GREY_25_PERCENT.index);
		}

		// 当前行
		int currentRow = 0;

		// 产生表格标题行
		Row row = sheet.createRow(currentRow++);
		for (int i = 0; i < fields.length; i++) {
			Cell cell = row.createCell(i);
			if (fields[i].startsWith("_")) {
				cell.setCellStyle(lightHeadStyle);
				cell.setCellValue(titles.get(fields[i]));
			} else {
				cell.setCellStyle(headStyle);
				cell.setCellValue(titles.get(fields[i]));
			}
		}

		// 遍历集合数据，产生数据行
		for (Map<String, Object> obj : list) {
			row = sheet.createRow(currentRow++);

			for (int i = 0; i < fields.length; i++) {
				Cell cell = row.createCell(i);
				if (fields[i].startsWith("_")) {
					cell.setCellStyle(lightStyle);
				} else {
					cell.setCellStyle(style);
				}

				Object value = obj.get(fields[i]);
				if (value == null) {
					logger.debug("值为空，跳过。");
					continue;
				}
				logger.debug("value:" + fields[i]);
				cell.setCellValue(value.toString());
			}
		}

		workbook.write(out);
	}

	/**
	 * 读取excel文件
	 * 
	 * @param file
	 * @return
	 * @throws IOException
	 */
	public static Workbook createWorkbook(File file) throws IOException {
		return ExcelFactory.createExcel(file);
	}

	/**
	 * 读取excel文件
	 * 
	 * @param fileName
	 * @param is
	 * @return
	 * @throws IOException
	 */
	public static Workbook createWorkbook(String fileName, InputStream is) throws IOException {
		return ExcelFactory.createExcel(fileName, is);
	}

	private static class ExcelFactory {

		public static Workbook createExcel(String fileName, InputStream is) throws IOException {
			String[] str = fileName.split("\\.");
			String extension = str[str.length - 1];
			Workbook workbook = null;
			if (extension.equals(Types.xls.name())) {
				workbook = getHSSFWorkbookForXLS(is);
			} else if (extension.equals(Types.xlsx.toString())) {
				workbook = getXSSFWorkbookForXLSX(is);
			}
			return workbook;
		}

		public static Workbook createExcel(File file) throws IOException {
			return createExcel(file.getName(), new FileInputStream(file));
		}

		public static HSSFWorkbook getHSSFWorkbookForXLS(InputStream is) throws IOException {
			BufferedInputStream bufferedInputStream = new BufferedInputStream(is);

			POIFSFileSystem poiFSFileSystem = new POIFSFileSystem(bufferedInputStream);
			bufferedInputStream.close();
			return new HSSFWorkbook(poiFSFileSystem);
		}

		public static XSSFWorkbook getXSSFWorkbookForXLSX(InputStream is) throws IOException {
			XSSFWorkbook xssFWorkbook = new XSSFWorkbook(is);
			return xssFWorkbook;
		}
	}

}
