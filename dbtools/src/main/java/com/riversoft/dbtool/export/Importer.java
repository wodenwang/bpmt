/*
 * $HeadURL: $
 * $Id: $
 * Copyright (c) 2013 by Riversoft System, all rights reserved.
 */
package com.riversoft.dbtool.export;

import com.riversoft.dbtool.util.DatabaseManager;
import com.riversoft.util.PoiUtils;
import com.riversoft.util.ValueConvertUtils;
import com.riversoft.util.jackson.JsonMapper;
import org.apache.commons.collections.map.CaseInsensitiveMap;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.jumpmind.db.model.Column;
import org.jumpmind.db.model.ForeignKey;
import org.jumpmind.db.model.Table;
import org.jumpmind.db.model.TypeMap;
import org.jumpmind.db.sql.DmlStatement.DmlType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import javax.sql.DataSource;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Borball
 */
public class Importer {

    private static Logger logger = LoggerFactory.getLogger("Importer");
    AtomicInteger SCUCESS = new AtomicInteger(0);
    List<String> orderedTables = new ArrayList<>();
    List<Sheet> orderedSheets = new ArrayList<>();
    private List<String> failedTables = new ArrayList<>();
    private DataSource dataSource;
    private DatabaseManager databaseManager;
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    private StringBuffer logs = new StringBuffer();

    public Importer(DataSource dataSource) {
        this.dataSource = dataSource;
        this.databaseManager = new DatabaseManager(this.dataSource);
        this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
    }

    public ImportExportResponse doImport(File file, boolean clearBeforeImport, boolean replaceIfConflict, boolean exitIfError) throws InvalidFormatException, IOException {
        return doImport(file, clearBeforeImport, replaceIfConflict, exitIfError, new NullDBOperationSignal());
    }

    /**
     * Do import work
     *
     * @param file              excel file
     * @param clearBeforeImport clear old data before import
     * @param replaceIfConflict replace the old data
     * @param exitIfError       exit if error happen
     * @throws InvalidFormatException
     * @throws IOException
     */
    public ImportExportResponse doImport(File file, boolean clearBeforeImport, boolean replaceIfConflict, boolean exitIfError, DBOperationSignal dBOperationSignal) throws InvalidFormatException, IOException {
        dBOperationSignal.begin();

        long now = System.currentTimeMillis();
        Workbook workbook = PoiUtils.createWorkbook(file);

        DefaultTransactionDefinition tf = new DefaultTransactionDefinition();
        PlatformTransactionManager tm = new DataSourceTransactionManager(dataSource);
        TransactionStatus ts = tm.getTransaction(tf);

        // 有外键的放后面
        ignoreEmptyTablesThenSort(workbook);

        if (clearBeforeImport) {
            List<Sheet> reversedSheets = new ArrayList<>();
            reversedSheets.addAll(orderedSheets);
            Collections.reverse(reversedSheets);
            logger.info("导入前清除原有数据。");
            dBOperationSignal.signal("info", "导入前清除原有数据。");
            clearDataBeforeImport(reversedSheets);
        }
        for (Sheet sheet : orderedSheets) {
            try {
                logger.info(sheet.getSheetName() + " 开始导入.");
                dBOperationSignal.signal(sheet.getSheetName(), "开始导入");
                importTable(sheet.getSheetName(), sheet, replaceIfConflict, exitIfError);
                dBOperationSignal.signal(sheet.getSheetName(), "完成导入");
                logger.info(sheet.getSheetName() + " 完成导入.");
            } catch (Exception e) {
                if (exitIfError) {
                    tm.rollback(ts);
                    error(sheet.getSheetName(), "操作已回滚.", e);
                    dBOperationSignal.signal(sheet.getSheetName(), "导入失败，操作将会回滚， 失败原因" + e.getMessage());
                    return new ImportExportResponse(false, logs.toString());
                }
            }
        }

        tm.commit(ts);

        if (failedTables.size() > 0) {
            warn("ALL", "以下表因为定义不存在导入失败:");
            dBOperationSignal.signal("info", "以下表因为定义不存在导入失败");
            for (String table : failedTables) {
                warn("", table);
                dBOperationSignal.signal(table, table);
            }
        }

        info("ALL", "成功导入" + SCUCESS.intValue() + "张表,时间:" + (System.currentTimeMillis() - now) + "ms.");
        dBOperationSignal.signal("info", "成功导入" + SCUCESS.intValue() + "张表,时间:" + (System.currentTimeMillis() - now) + "ms.");

        dBOperationSignal.end();

        return new ImportExportResponse(true, logs.toString());

    }

    private void clearDataBeforeImport(List<Sheet> reversedSheets) {
        for (Sheet sheet : reversedSheets) {
            Table table = databaseManager.findTable(sheet.getSheetName());
            clearTableData(table);
        }
    }

    private void ignoreEmptyTablesThenSort(Workbook workbook) {
        long now = System.currentTimeMillis();
        info("none", "开始对要导入的表排序...");
        Map<String, Sheet> sheetMap = new CaseInsensitiveMap();
        Map<String, Table> tableMap = new CaseInsensitiveMap();
        Sheet sheet;
        int count = workbook.getNumberOfSheets();
        for (int i = 0; i < count; i++) {
            sheet = workbook.getSheetAt(i);
            if (isNotEmpty(sheet)) {
                Table table = databaseManager.findTable(sheet.getSheetName());
                if (table != null) {
                    sheetMap.put(table.getName().toUpperCase(), sheet);
                    tableMap.put(table.getName().toUpperCase(), table);
                } else {
                    info(sheet.getSheetName(), "没有表结构的定义将被忽略.");
                }
            } else {
                info(sheet.getSheetName(), "没有数据将被忽略.");
            }
        }

        for (String tableName : tableMap.keySet()) {
            getReferenceTables(tableMap, tableName.toUpperCase());
        }

        info("none", "排序结果:");
        int i = 0;
        for (String table : orderedTables) {
            i++;
            info(table, String.valueOf(i));
            orderedSheets.add(sheetMap.get(table));
        }
        info("none", "排序完成,时间:" + (System.currentTimeMillis() - now) + "ms;共" + orderedSheets.size() + "张表需要导入.");

    }

    private boolean isNotEmpty(Sheet sheet) {
        Iterator<Row> rowIterator = sheet.rowIterator();
        if (rowIterator.hasNext()) {
            rowIterator.next();//第一行是表头
            return rowIterator.hasNext();
        } else {
            return false;
        }
    }

    private void getReferenceTables(Map<String, Table> tableMap, String upperTableName) {
        if (tableMap.get(upperTableName).getForeignKeyCount() > 0) {
            ForeignKey[] fks = tableMap.get(upperTableName).getForeignKeys();
            for (ForeignKey foreignKey : fks) {
                String fkTableName = foreignKey.getForeignTableName();
                if (fkTableName == null) {
                    logger.warn("外键:" + foreignKey.getName() + "的库表" + "不存在，将会跳过该表.");
                } else {
                    getReferenceTables(tableMap, fkTableName.toUpperCase());
                }

                if (!orderedTables.contains(upperTableName)) {
                    orderedTables.add(upperTableName);
                }
            }
        } else {
            if (!orderedTables.contains(upperTableName)) {
                orderedTables.add(upperTableName);
            }
        }
    }

    private void importTable(String tableName, Sheet sheet, boolean replaceIfConflict, boolean exitIfError) {
        Table table = databaseManager.findTable(tableName);
        if (table != null) {
            long now = System.currentTimeMillis();
            info(table.getName(), "开始导入...");
            Iterator<Row> rowIterator = sheet.iterator();
            if (rowIterator != null && rowIterator.hasNext()) {
                Row header = rowIterator.next();// 第一行是字段名
                String[] sortedColumnNames = getSortedColumnNames(header, table);
                Map<String, Column> columnMap = getColumnMap(header, table);// 字段对应的类型

                try {
                    while (rowIterator.hasNext()) {
                        Map<String, Object> paras = new HashMap<>();
                        Row row = rowIterator.next();

                        Iterator<Cell> cellIterator = row.iterator();
                        int j = 0;
                        while (cellIterator.hasNext()) {
                            Cell cell = cellIterator.next();

                            Object jdbcValue = toJavaObject(columnMap.get(sortedColumnNames[j]), getCellValue(cell));
                            paras.put(sortedColumnNames[j], jdbcValue);
                            j++;
                        }

                        createOrUpdate(table, paras, replaceIfConflict);

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    if (exitIfError) {
                        throw e;
                    } else {
                        warn(tableName, "数据操作异常,跳过出错记录:" + e.getMessage());
                    }
                }

            }
            info(table.getName(), "导入完成,时间:" + (System.currentTimeMillis() - now) + "ms.");
            SCUCESS.incrementAndGet();

        } else {
            failedTables.add(tableName);
            warn(tableName, "表不存在.");
        }
    }

    private String[] getSortedColumnNames(Row row, Table table) {
        String[] columns = new String[table.getColumnCount()];
        Iterator<Cell> cellHeaderIterator = row.iterator();
        int i = 0;
        while (cellHeaderIterator.hasNext()) {
            Cell cell = cellHeaderIterator.next();
            String cellName = cell.getStringCellValue();
            if (cellName.contains("[") && cellName.contains("]")) {
                String fieldName = cellName.substring(1, cellName.indexOf("]"));
                Column column = table.getColumnWithName(fieldName);
                columns[i] = column.getName();

                i++;
            }
        }

        return columns;
    }

    private Map<String, Column> getColumnMap(Row row, Table table) {
        Map<String, Column> columns = new HashMap<>();
        Iterator<Cell> cellHeaderIterator = row.iterator();
        while (cellHeaderIterator.hasNext()) {
            Cell cell = cellHeaderIterator.next();
            String cellName = cell.getStringCellValue();
            if (cellName.contains("[") && cellName.contains("]")) {
                String fieldName = cellName.substring(1, cellName.indexOf("]"));
                Column column = table.getColumnWithName(fieldName);
                columns.put(column.getName(), column);
            }
        }

        return columns;
    }

    private void clearTableData(Table table) {
        long now = System.currentTimeMillis();
        info(table.getName(), "清理数据开始...");
        String deleteSql = databaseManager.createDml(table, DmlType.DELETE).getSql();
        if (deleteSql.contains("where")) {
            deleteSql = deleteSql.substring(0, deleteSql.indexOf("where"));
        }

        namedParameterJdbcTemplate.getJdbcOperations().execute(deleteSql);
        info(table.getName(), "清理数据完成, 时间:" + (System.currentTimeMillis() - now) + "ms.");
    }

    private void createOrUpdate(Table table, Map<String, Object> paras, boolean replaceIfConflict) {
        String insertSql = buildInsertSql(table);
        String countSql = buildCountSql(table);
        String updateSql = buildUpdateSql(table);
        try {
            if (namedParameterJdbcTemplate.queryForObject(countSql, paras, Integer.class) == 0) {
                info(table.getName(), "插入新数据：" + JsonMapper.defaultMapper().toJson(paras));
                namedParameterJdbcTemplate.update(insertSql, paras);
            } else {
                if (replaceIfConflict) {
                    info(table.getName(), "该主键标示的数据已经存在,将用新数据:" + JsonMapper.defaultMapper().toJson(paras) + "替换.");

                    namedParameterJdbcTemplate.update(updateSql, paras);
                } else {
                    warn(table.getName(), "该主键标示的数据已经存在,将跳过该数据:" + JsonMapper.defaultMapper().toJson(paras));
                }
            }
        } catch (Exception e) {
            if (e instanceof DuplicateKeyException) {
                warn(table.getName(), "主键冲突:" + e.getLocalizedMessage());
            } else {
                error(table.getName(), "数据操作异常:", e);
            }
            throw e;
        }
    }

    private String buildUpdateSql(Table table) {
        Column[] columns = table.getNonPrimaryKeyColumns();
        List<Column> columnList = removeBinaryColumns(columns);
        Column[] keys = table.getPrimaryKeyColumns();
        StringBuilder sb = new StringBuilder("update ");
        sb.append(table.getName());
        sb.append(" set ");
        for (int i = 0; i < columnList.size(); i++) {
            if (i == columnList.size() - 1) {
                sb.append(columnList.get(i).getName()).append(" =:").append(columnList.get(i).getName());
            } else {
                sb.append(columnList.get(i).getName()).append(" =:").append(columnList.get(i).getName()).append(", ");
            }
        }
        sb.append(" where ");
        for (int i = 0; i < keys.length; i++) {
            if (i == keys.length - 1) {
                sb.append(keys[i].getName()).append(" =:").append(keys[i].getName());
            } else {
                sb.append(keys[i].getName()).append(" =:").append(keys[i].getName()).append(" and ");
            }
        }

        return sb.toString();
    }

    private String buildCountSql(Table table) {
        StringBuilder sb = new StringBuilder("select count(1) from ");
        sb.append(table.getName());
        sb.append(" where ");

        String[] keys = table.getPrimaryKeyColumnNames();
        for (int i = 0; i < keys.length; i++) {
            if (i == keys.length - 1) {
                sb.append(keys[i]).append(" =:").append(keys[i]);
            } else {
                sb.append(keys[i]).append(" =:").append(keys[i]).append(" and ");
            }
        }

        return sb.toString();
    }

    private String buildInsertSql(Table table) {
        Column[] columns = table.getColumns();
        List<Column> columnList = removeBinaryColumns(columns);
        StringBuilder sb = new StringBuilder("insert into ");
        sb.append(table.getName());
        sb.append("(");
        for (int i = 0; i < columnList.size(); i++) {
            if (i == columnList.size() - 1) {
                sb.append(columnList.get(i).getName()).append(")");
            } else {
                sb.append(columnList.get(i).getName()).append(", ");
            }
        }
        sb.append("values(");
        for (int i = 0; i < columnList.size(); i++) {
            if (i == columnList.size() - 1) {
                sb.append(":").append(columnList.get(i).getName()).append(")");
            } else {
                sb.append(":").append(columnList.get(i).getName()).append(", ");
            }
        }

        return sb.toString();
    }

    private List<Column> removeBinaryColumns(Column[] columns) {
        List<Column> columnsList = new ArrayList<>();
        for (Column column : columns) {
            if (!TypeMap.isBinaryType(column.getJdbcTypeCode())) {
                columnsList.add(column);
            }
        }

        return columnsList;
    }

    private Object getCellValue(Cell cell) {
        int type = cell.getCellType();
        switch (type) {
            case Cell.CELL_TYPE_STRING:
                return cell.getStringCellValue();
            case Cell.CELL_TYPE_NUMERIC:
                return cell.getNumericCellValue();
            case Cell.CELL_TYPE_BOOLEAN:
                return cell.getBooleanCellValue();
            case Cell.CELL_TYPE_BLANK:
                return null;
            default:
                return cell.getStringCellValue();
        }
    }

    private Object toJavaObject(Column column, Object value) {
        if (TypeMap.isTextType(column.getJdbcTypeCode())) {
            return value;
        } else if (TypeMap.isDateTimeType(column.getJdbcTypeCode())) {
            if (value == null)
                return null;
            return ValueConvertUtils.convert(value.toString(), Date.class);
        } else if (TypeMap.isBinaryType(column.getJdbcTypeCode())) {
            return null;
        } else if (TypeMap.isNumericType(column.getJdbcTypeCode())) {
            JdbcDataTypes type = JdbcDataTypes.valueOf(column.getMappedType());
            switch (type) {
                // 数字:int
                case INTEGER:
                case SMALLINT:
                case TINYINT:
                    if (value != null) {
                        if (value instanceof Double) {
                            return Double.valueOf(value.toString());
                        }
                        if (value instanceof Boolean) {
                            if (Boolean.valueOf(value.toString())) {
                                return 1;
                            } else {
                                return 0;
                            }
                        }
                        if (value instanceof String) {
                            return Integer.valueOf(value.toString());
                        }
                    }
                    return 0;

                // 数字:long
                case BIGINT:
                    if (value != null) {
                        if (value instanceof Double) {
                            return ((Double) value).longValue();
                        }
                        if (value instanceof String) {
                            return Long.valueOf(value.toString());
                        }
                    }
                    return 0l;

                // 数字:double
                case FLOAT:
                case DOUBLE:
                    if (value != null) {
                        if (value instanceof Double) {
                            return value;
                        }
                        if (value instanceof String) {
                            return Double.valueOf(value.toString());
                        }
                    }
                    return 0d;

                // 数字:bigDecimal
                case DECIMAL:
                case NUMERIC:
                    if (value != null) {
                        if (value instanceof Double) {
                            return new BigDecimal((Double) value);
                        }
                        if (value instanceof String) {
                            return new BigDecimal((String) value);
                        }
                    }
                    return new BigDecimal(0);
                default:
                    return Integer.valueOf(value.toString());
            }
        } else {
            return value;
        }
    }

    private void error(String table, String message, Throwable t) {
        logs.append("[" + table + "]:" + message + t.getLocalizedMessage()).append("\n");
        logger.error("[" + table + "]:" + message, t);
    }

    private void warn(String table, String message) {
        logs.append("[" + table + "]:" + message).append("\n");
        logger.warn("[" + table + "]:" + message);
    }

    private void info(String table, String message) {
        logs.append("[" + table + "]:" + message).append("\n");
        logger.info("[" + table + "]:" + message);
    }

}
