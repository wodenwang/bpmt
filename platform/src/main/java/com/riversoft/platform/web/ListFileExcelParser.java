/*
 * $HeadURL: $
 * $Id: $
 * Copyright (c) 2013 by Riversoft System, all rights reserved.
 */
package com.riversoft.platform.web;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.PropertyUtils;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.riversoft.core.exception.SystemRuntimeException;
import com.riversoft.util.dynamicbean.DynamicBeanUtils;

/**
 * 标准批量excel模板解析器
 * 
 * @author Woden
 * 
 */
@SuppressWarnings("rawtypes")
public class ListFileExcelParser {
    /**
     * Logger for this class
     */
    private static final Logger logger = LoggerFactory.getLogger(ListFileExcelParser.class);

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
    private Integer titleRow;

    /**
     * 最后结果,包装成hashmap
     */
    private List<Map<String, String>> result = new ArrayList<>();

    public ListFileExcelParser(List<String> fields, String sheetName, Integer titleRow) {
        this.sheetName = sheetName;
        this.titleRow = titleRow;
        this.fields = fields;
        Map<String, Class> map = new HashMap<>();
        for (String key : fields) {
            map.put("_" + key, String.class);
        }
        this.dynamicClass = DynamicBeanUtils.transMap2Bean(map).getClass();
        buildReader();
    }

    public ListFileExcelParser(List<String> fields) {
        this(fields, "data", 0);
    }

    private void buildReader() {
        XLSReaderImpl reader = new XLSReaderImpl();
        {
            XLSSheetReaderImpl sheet = new XLSSheetReaderImpl();
            sheet.setSheetName(sheetName);
            reader.addSheetReader(sheet);
            // 首行占坑
            {
                SimpleBlockReaderImpl simpleBlockReaderImpl = new SimpleBlockReaderImpl();
                sheet.addBlockReader(simpleBlockReaderImpl);
                simpleBlockReaderImpl.setStartRow(0);
                simpleBlockReaderImpl.setEndRow(titleRow);
            }

            // 循环
            {
                XLSForEachBlockReaderImpl forEachBlockReaderImpl = new XLSForEachBlockReaderImpl();
                sheet.addBlockReader(forEachBlockReaderImpl);
                forEachBlockReaderImpl.setStartRow(titleRow + 1);
                forEachBlockReaderImpl.setEndRow(titleRow + 1);
                forEachBlockReaderImpl.setItems("list");
                forEachBlockReaderImpl.setVar("var");
                forEachBlockReaderImpl.setVarType(dynamicClass);

                SimpleBlockReaderImpl simpleBlockReaderImpl = new SimpleBlockReaderImpl();
                forEachBlockReaderImpl.addBlockReader(simpleBlockReaderImpl);
                simpleBlockReaderImpl.setStartRow(titleRow + 1);
                simpleBlockReaderImpl.setEndRow(titleRow + 1);

                // mapping
                int i = 0;
                for (String key : fields) {
                    BeanCellMapping mapping = new BeanCellMapping();
                    simpleBlockReaderImpl.addMapping(mapping);
                    mapping.setCol((short) i);
                    mapping.setBeanKey("var");
                    mapping.setRow(1);
                    mapping.setPropertyName("_" + key);
                    i++;
                }

                SimpleSectionCheck sectionCheck = new SimpleSectionCheck();
                forEachBlockReaderImpl.setLoopBreakCondition(sectionCheck);
                simpleBlockReaderImpl.setLoopBreakCondition(sectionCheck);
                // checker
                {
                    OffsetRowCheckImpl rowCheckImpl = new OffsetRowCheckImpl();
                    rowCheckImpl.setOffset(0);
                    sectionCheck.addRowCheck(rowCheckImpl);// imp
                    OffsetCellCheckImpl cellCheckImpl = new OffsetCellCheckImpl();
                    cellCheckImpl.setOffset((short) 0);// imp
                    cellCheckImpl.setValue("");// imp
                    rowCheckImpl.addCellCheck(cellCheckImpl);
                }

            }
        }
        this.reader = reader;
    }

    /**
     * 解析
     * 
     * @param is
     * @return
     */
    public ListFileExcelParser parse(InputStream is) {
        Map<String, Object> map = new HashMap<>();
        map.put("list", list);
        try {
            map.put("var", dynamicClass.newInstance());
            reader.read(is, map);
        } catch (InstantiationException | IllegalAccessException | InvalidFormatException | IOException e) {
            throw new SystemRuntimeException("解析Excel文件出错。", e);
        }

        // 解析结果
        if (list != null) {
            for (Object obj : list) {
                Map<String, String> vo = new HashMap<>();
                for (String key : fields) {
                    try {
                        vo.put(key, (String) PropertyUtils.getProperty(obj, "_" + key));
                    } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                        logger.error("解析excel文件出错.", e);
                        throw new SystemRuntimeException("解析excel文件出错.", e);
                    }
                }
                result.add(vo);
            }
        }

        return this;
    }

    public List<Map<String, String>> getResult() {
        return result;
    }
}
