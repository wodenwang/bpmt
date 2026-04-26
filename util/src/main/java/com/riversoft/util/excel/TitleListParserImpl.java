/*
 * $HeadURL: $
 * $Id: $
 * Copyright (c) 2013 by Riversoft System, all rights reserved.
 */
package com.riversoft.util.excel;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.springframework.util.StreamUtils;

/**
 * 标准批量excel模板解析器
 * 
 * @author Woden
 * 
 */
public class TitleListParserImpl implements Parser<List<Map<String, String>>> {

	/**
	 * 最后结果,包装成hashmap
	 */
	private List<Map<String, String>> result = new ArrayList<>();
	private int titleRow;
	private MapParserImpl mapParserImpl;
	private ListParserImpl listParserImpl;

	private static final String[] FIELDS = new String[] { "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z", "AA", "AB",
			"AC", "AD", "AE", "AF", "AG", "AH", "AI", "AJ", "AK", "AL", "AM", "AN", "AO", "AP", "AQ", "AR", "AS", "AT", "AU", "AV", "AW", "AX", "AY", "AZ", "BA", "BB", "BC", "BD", "BE", "BF", "BG",
			"BH", "BI", "BJ", "BK", "BL", "BM", "BN", "BO", "BP", "BQ", "BR", "BS", "BT", "BU", "BV", "BW", "BX", "BY", "BZ", "CA", "CB", "CC", "CD", "CE", "CF", "CG", "CH", "CI", "CJ", "CK", "CL",
			"CM", "CN", "CO", "CP", "CQ", "CR", "CS", "CT", "CU", "CV", "CW", "CX", "CY", "CZ", "DA", "DB", "DC", "DD", "DE", "DF", "DG", "DH", "DI", "DJ", "DK", "DL", "DM", "DN", "DO", "DP", "DQ",
			"DR", "DS", "DT", "DU", "DV", "DW", "DX", "DY", "DZ" };

	public TitleListParserImpl(String sheetName, Integer startRow, String loopChecker) {

		titleRow = startRow <= 1 ? 1 : startRow;// 最小1

		List<String> fileds = new ArrayList<>();
		for (String field : FIELDS) {
			fileds.add(field + titleRow);
		}
		mapParserImpl = new MapParserImpl(fileds, sheetName);
		listParserImpl = new ListParserImpl(Arrays.asList(FIELDS), sheetName, titleRow + 1, loopChecker);

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
	public TitleListParserImpl parse(InputStream is) throws InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, InvalidFormatException, IOException {
		byte[] bytes = StreamUtils.copyToByteArray(is);
		Map<String, String> titleMap = mapParserImpl.parse(new ByteArrayInputStream(bytes)).getResult();
		List<Map<String, String>> contentList = listParserImpl.parse(new ByteArrayInputStream(bytes)).getResult();

		result.clear();
		for (Map<String, String> o : contentList) {
			Map<String, String> vo = new HashMap<>();
			for (String field : FIELDS) {
				String title = titleMap.get(field + titleRow);
				if (StringUtils.isNotEmpty(title)) {
					vo.put(title, o.get(field));
				}
			}
			result.add(vo);
		}

		return this;
	}

	@Override
	public List<Map<String, String>> getResult() {
		return result;
	}

}
