/*
 * $HeadURL: $
 * $Id: $
 * Copyright (c) 2015 by Riversoft System, all rights reserved.
 */
package com.riversoft.util.excel;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.jxls.reader.BeanCellMapping;
import org.jxls.reader.SimpleBlockReaderImpl;
import org.jxls.reader.XLSReader;
import org.jxls.reader.XLSReaderImpl;
import org.jxls.reader.XLSSheetReaderImpl;

import com.riversoft.util.dynamicbean.DynamicBeanUtils;

/**
 * @author woden
 * 
 */
@SuppressWarnings("rawtypes")
public class MapParserImpl implements Parser<Map<String, String>> {

	/**
	 * 解析位置(map key)
	 */
	private List<String> fields;

	/**
	 * 输入解析器
	 */
	private XLSReader reader;

	/**
	 * 解析结果
	 */
	private Map<String, String> result = new HashMap<>();

	/**
	 * 最后一行
	 */
	private int endRow;

	/**
	 * 动态类
	 */
	private Class dynamicClass;

	/**
	 * 需解析的SHEET
	 */
	private String sheetName;

	public MapParserImpl(List<String> fields, String sheetName) {
		this.sheetName = sheetName;
		this.fields = fields;

		Map<String, Class> map = new HashMap<>();
		Pattern pattern = Pattern.compile("[^0-9]");
		for (String field : this.fields) {
			map.put("_" + field, String.class);

			Matcher matcher = pattern.matcher(field);
			try {
				endRow = Integer.parseInt(matcher.replaceAll(""));
			} catch (Exception e) {
				endRow = 999;
			}
		}
		this.dynamicClass = DynamicBeanUtils.transMap2Bean(map).getClass();

		buildReader();
	}

	public MapParserImpl(List<String> fields) {
		this(fields, null);
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
			if (endRow >= 0) {
				SimpleBlockReaderImpl simpleBlockReaderImpl = new SimpleBlockReaderImpl();
				for (String field : this.fields) {
					BeanCellMapping mapping = new BeanCellMapping(field, "var._" + field);
					simpleBlockReaderImpl.addMapping(mapping);
				}
				sheet.addBlockReader(simpleBlockReaderImpl);
				simpleBlockReaderImpl.setStartRow(0);
				simpleBlockReaderImpl.setEndRow(endRow);
			}

		}
		this.reader = reader;
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
	public MapParserImpl parse(InputStream is) throws InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, InvalidFormatException, IOException {
		result.clear();
		Map<String, Object> map = new HashMap<>();
		Object var = dynamicClass.newInstance();
		map.put("var", var);
		reader.read(is, map);

		// 解析结果
		for (String field : fields) {
			result.put(field, (String) PropertyUtils.getProperty(var, "_" + field));
		}

		return this;
	}

	@Override
	public Map<String, String> getResult() {
		return result;
	}

}
