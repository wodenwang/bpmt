/*
 * $HeadURL: $
 * $Id: $
 * Copyright (c) 2013 by Riversoft System, all rights reserved.
 */
package com.riversoft.dbtool.export;

import com.riversoft.dbtool.util.DatabaseManager;
import com.riversoft.util.Formatter;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.jumpmind.db.model.Column;
import org.jumpmind.db.model.Table;
import org.jumpmind.db.model.TypeMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 给定一个或者多个table,导出数据到OutputStream
 *
 * @author Borball
 */
public class Exporter {

    private static Logger logger = LoggerFactory.getLogger("Exporter");
    DataSource dataSource;
    AtomicInteger SCUCESS = new AtomicInteger(0);
    private DatabaseManager databaseManager;
    private JdbcTemplate jdbcTemplate;

    public Exporter(DataSource dataSource) {
        this.dataSource = dataSource;
        this.databaseManager = new DatabaseManager(dataSource);
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public void doExportAll(OutputStream ouputStream, ExcelType type) throws InstantiationException,
            IllegalAccessException, IOException {
        doExportAll(ouputStream, type, new NullDBOperationSignal());
    }

    public void doExportAll(OutputStream ouputStream, ExcelType type, DBOperationSignal dbOperationSignal)
            throws InstantiationException, IllegalAccessException, IOException {
        Table[] tables = databaseManager.readDatabase().getTables();
        String[] tableNames = new String[tables.length];
        for (int i = 0; i < tables.length; i++) {
            tableNames[i] = tables[i].getName();
        }

        doExport(ouputStream, type, dbOperationSignal, tableNames);
    }

    public void doExport(OutputStream ouputStream, ExcelType type, String... tables) throws InstantiationException,
            IllegalAccessException, IOException {
        doExport(ouputStream, type, new NullDBOperationSignal(), tables);
    }

    public void doExport(OutputStream ouputStream, ExcelType type, DBOperationSignal dbOperationSignal,
                         String... tables) throws InstantiationException, IllegalAccessException, IOException {
        dbOperationSignal.begin();
        logger.info("准备导出数据,共有表:" + tables.length + "个...");

        // 声明一个工作薄
        Workbook workbook = type.type.newInstance();
        for (String table : tables) {
            if (databaseManager.findTable(table) != null) {
                exportTable(workbook, table);
                logger.info(table + " 导出完成.");
                dbOperationSignal.signal(table, "导出完成");
            } else {
                logger.warn("表:" + table + "不存在, 跳过...");
                dbOperationSignal.signal(table, "表不存在, 跳过.");
                continue;
            }
        }
        logger.info("一共成功导出:" + SCUCESS.intValue() + "个表...");

        dbOperationSignal.end();
        workbook.write(ouputStream);
    }

    private void exportTable(Workbook workbook, String table) throws IllegalAccessException {
        // 生成一个表格
        Sheet sheet = workbook.createSheet(table);
        // 设置表格默认列宽度为15个字节
        sheet.setDefaultColumnWidth((short) 15);
        exportWithJdbcService(workbook, table, sheet);
    }

    private void exportWithJdbcService(Workbook workbook, String table, Sheet sheet) {
        Column columns[] = databaseManager.findTable(table).getColumns();
        exportHeader(workbook, sheet, columns);

        try {
            String findAll = "select * from " + table;
            @SuppressWarnings("rawtypes")
            List result = jdbcTemplate.queryForList(findAll);
            if (!result.isEmpty()) {
                exportContents(workbook, sheet, result, columns);
            }
            SCUCESS.incrementAndGet();
        } catch (Exception e) {
            logger.warn("表:" + table + "数据查询出错," + e.getMessage());
            // ignore
        }
    }

    private void exportContents(Workbook workbook, Sheet sheet, @SuppressWarnings("rawtypes") List result,
                                Column[] columns) throws IllegalAccessException {
        // 内容样式
        CellStyle contentStyle = createContentStyle(workbook);
        // 遍历集合数据，产生数据行
        for (int i = 0; i < result.size(); i++) {
            Row row = sheet.createRow(i + 1);
            for (int j = 0; j < columns.length; j++) {
                Cell cell = row.createCell(j);
                cell.setCellStyle(contentStyle);
                Object value;
                Object formatValue;
                try {
                    value = PropertyUtils.getProperty(result.get(i), columns[j].getName());
                } catch (InvocationTargetException | NoSuchMethodException e) {
                    value = null;
                }
                if (value == null) {
                    continue;
                }

                formatValue = format(columns[j], value);

                cell.setCellValue(formatValue.toString());
            }
        }
    }

    private Object format(Column column, Object value) {
        if (TypeMap.isDateTimeType(column.getJdbcTypeCode())) {
            return Formatter.formatDatetime((Date) value);
        } else if (TypeMap.isBinaryType(column.getJdbcTypeCode())) {
            return "该字段为 Binary 将不予显示和导出.";
        } else {
            return value;
        }
    }

    private void exportHeader(Workbook workbook, Sheet sheet, Column[] columns) {
        // 标题样式
        CellStyle headStyle = createHeadStyle(workbook);
        Row row = sheet.createRow(0);
        for (int i = 0; i < columns.length; i++) {
            Cell cell = row.createCell(i);
            cell.setCellStyle(headStyle);
            cell.setCellValue("[" + columns[i].getName() + "]"
                    + (columns[i].getDescription() == null ? "" : columns[i].getDescription()));
        }
    }

    private CellStyle createContentStyle(Workbook workbook) {
        CellStyle contentStyle = workbook.createCellStyle();
        contentStyle.setFillForegroundColor(HSSFColor.LIGHT_YELLOW.index);
        contentStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
        contentStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN);
        contentStyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);
        contentStyle.setBorderRight(HSSFCellStyle.BORDER_THIN);
        contentStyle.setBorderTop(HSSFCellStyle.BORDER_THIN);
        contentStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);
        contentStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
        {
            Font font = workbook.createFont();
            font.setBoldweight(HSSFFont.BOLDWEIGHT_NORMAL);
            contentStyle.setFont(font);
        }

        return contentStyle;
    }

    private CellStyle createHeadStyle(Workbook workbook) {
        CellStyle headStyle = workbook.createCellStyle();
        headStyle.setFillForegroundColor(HSSFColor.SKY_BLUE.index);
        headStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
        headStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN);
        headStyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);
        headStyle.setBorderRight(HSSFCellStyle.BORDER_THIN);
        headStyle.setBorderTop(HSSFCellStyle.BORDER_THIN);
        headStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);
        {
            // 设置首行字体
            Font font = workbook.createFont();
            font.setColor(HSSFColor.VIOLET.index);
            font.setFontHeightInPoints((short) 12);
            font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
            headStyle.setFont(font);
        }

        return headStyle;
    }

    public static enum ExcelType {
        EXCEL_2003(HSSFWorkbook.class), EXCEL_AFTER_2003(XSSFWorkbook.class);
        private Class<? extends Workbook> type;

        private ExcelType(Class<? extends Workbook> type) {
            this.type = type;
        }
    }

}
