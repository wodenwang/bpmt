/*
 * $HeadURL: $
 * $Id: $
 * Copyright (c) 2013 by Riversoft System, all rights reserved.
 */
package com.riversoft.util.excel;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.jxls.reader.BeanCellMapping;
import org.jxls.reader.OffsetCellCheckImpl;
import org.jxls.reader.OffsetRowCheckImpl;
import org.jxls.reader.SimpleBlockReaderImpl;
import org.jxls.reader.SimpleSectionCheck;
import org.jxls.reader.XLSForEachBlockReaderImpl;
import org.jxls.reader.XLSReader;
import org.jxls.reader.XLSReaderImpl;
import org.jxls.reader.XLSSheetReaderImpl;

import com.riversoft.util.dynamicbean.DynamicBeanUtils;

/**
 * 标准批量excel模板解析器
 * 
 * @author Woden
 * 
 */
@SuppressWarnings("rawtypes")
public class ListParserImpl implements Parser<List<Map<String, String>>> {

	/**
	 * 第一行，字段
	 */
	private List<String> fields;

	/**
	 * 动态类
	 */
	private Class dynamicClass;

	/**
	 * 输入解析器
	 */
	private XLSReader reader;

	/**
	 * 解析结果
	 */
	private List<?> list = new ArrayList<>();

	/**
	 * 需解析的SHEET
	 */
	private String sheetName;
	/**
	 * 标题行索引
	 */
	private Integer startRow;

	/**
	 * 循环截至字符串(首行)
	 */
	private String loopChecker = "";

	/**
	 * 最后结果,包装成hashmap
	 */
	private List<Map<String, String>> result = new ArrayList<>();

	private static String FIELDS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";

	public ListParserImpl(List<String> fields, String sheetName, Integer startRow, String loopChecker) {
		this.sheetName = sheetName;
		if (StringUtils.isNotEmpty(loopChecker)) {
			this.loopChecker = loopChecker;
		}
		if (startRow <= 1) {// excel的技术习惯是从1开始
			this.startRow = -1;
		} else {
			this.startRow = startRow - 2;
		}
		if (fields == null || fields.size() < 1) {
			this.fields = new ArrayList<String>();
			for (int i = 0; i < FIELDS.length(); i++) {
				this.fields.add(FIELDS.substring(i, i + 1));
			}
		} else {
			this.fields = fields;
		}

		Map<String, Class> map = new HashMap<>();
		for (String key : this.fields) {
			map.put("_" + key, String.class);
		}
		this.dynamicClass = DynamicBeanUtils.transMap2Bean(map).getClass();
		buildReader();
	}

	public ListParserImpl() {
		this(null);
	}

	public ListParserImpl(List<String> fields) {
		this(fields, null, 0, "");
	}

	private void buildReader() {
		XLSReaderImpl reader = new XLSReaderImpl();
		{
			XLSSheetReaderImpl sheet = new XLSSheetReaderImpl();

			if (StringUtils.isNotEmpty(sheetName)) {
				sheet.setSheetName(sheetName);
			} else {// 默认第一张sheet
				sheet.setSheetIdx(0);
			}

			reader.addSheetReader(sheet);
			// 首行占坑
			if (startRow >= 0) {
				SimpleBlockReaderImpl simpleBlockReaderImpl = new SimpleBlockReaderImpl();
				sheet.addBlockReader(simpleBlockReaderImpl);
				simpleBlockReaderImpl.setStartRow(0);
				simpleBlockReaderImpl.setEndRow(startRow);
			}

			// 循环
			{
				XLSForEachBlockReaderImpl forEachBlockReaderImpl = new XLSForEachBlockReaderImpl();
				sheet.addBlockReader(forEachBlockReaderImpl);
				forEachBlockReaderImpl.setStartRow(startRow + 1);
				forEachBlockReaderImpl.setEndRow(startRow + 1);
				forEachBlockReaderImpl.setItems("list");
				forEachBlockReaderImpl.setVar("var");
				forEachBlockReaderImpl.setVarType(dynamicClass);

				SimpleBlockReaderImpl simpleBlockReaderImpl = new SimpleBlockReaderImpl();
				forEachBlockReaderImpl.addBlockReader(simpleBlockReaderImpl);
				simpleBlockReaderImpl.setStartRow(startRow + 1);
				simpleBlockReaderImpl.setEndRow(startRow + 1);

				// mapping
				for (String key : fields) {
					BeanCellMapping mapping = new BeanCellMapping();
					simpleBlockReaderImpl.addMapping(mapping);
					mapping.setCol(translateField(key));
					mapping.setBeanKey("var");
					mapping.setRow(startRow + 1);
					mapping.setPropertyName("_" + key);
				}

				// checker
				SimpleSectionCheck sectionCheck = new SimpleSectionCheck();
				{
					OffsetRowCheckImpl rowCheckImpl = new OffsetRowCheckImpl();
					rowCheckImpl.setOffset(0);
					OffsetCellCheckImpl cellCheckImpl = new OffsetCellCheckImpl();
					cellCheckImpl.setOffset((short) 0);// imp
					cellCheckImpl.setValue(loopChecker);// imp
					rowCheckImpl.addCellCheck(cellCheckImpl);
					sectionCheck.addRowCheck(rowCheckImpl);// imp
				}
				forEachBlockReaderImpl.setLoopBreakCondition(sectionCheck);
				simpleBlockReaderImpl.setLoopBreakCondition(sectionCheck);

			}
		}
		this.reader = reader;
	}

	/**
	 * 将A,B,C等excel列标识转换为数字
	 * 
	 * @param field
	 * @return
	 */
	private static short translateField(String field) {
		String str = field.toUpperCase();
		int result = 0;
		for (int i = 0; i < str.length(); i++) {// 每个字母循环
			int index = FIELDS.indexOf(str.substring(i, i + 1));
			result += Math.pow(26, str.length() - 1 - i) * (index + 1);
		}
		return (short) (result - 1);
	}

	/**
	 * 解析
	 * 
	 * @param is
	 * @return
	 * @throws IllegalAccessException
	 * @throws InstantiationException
	 * @throws NoSuchMethodException
	 * @throws InvocationTargetException
	 * @throws IOException
	 * @throws InvalidFormatException
	 */
	@Override
	public Parser<List<Map<String, String>>> parse(InputStream is)
			throws InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, InvalidFormatException, IOException {
		result.clear();
		Map<String, Object> map = new HashMap<>();
		map.put("list", list);
		map.put("var", dynamicClass.newInstance());
		reader.read(is, map);

		// 解析结果
		if (list != null) {
			for (Object obj : list) {
				Map<String, String> vo = new HashMap<>();
				for (String key : fields) {
					vo.put(key, (String) PropertyUtils.getProperty(obj, "_" + key));
				}
				result.add(vo);
			}
		}

		return this;
	}

	@Override
	public List<Map<String, String>> getResult() {
		return result;
	}
}
