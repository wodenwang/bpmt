/*
 * $HeadURL: $
 * $Id: $
 * Copyright (c) 2013 by Riversoft System, all rights reserved.
 */
package com.riversoft.dbtool.export;

import com.riversoft.dbtool.export.Exporter.ExcelType;
import com.riversoft.dbtool.util.DataSourceInstance;
import com.riversoft.util.Formatter;
import com.riversoft.util.cli.AbstractCmdProcessor;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.lang3.StringUtils;
import org.jumpmind.db.model.Database;
import org.jumpmind.db.model.Table;
import org.jumpmind.db.platform.IDatabasePlatform;
import org.jumpmind.db.platform.JdbcDatabasePlatformFactory;
import org.jumpmind.db.sql.SqlTemplateSettings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

/**
 * @author Borball
 */
public class ExporterCmdProcessor extends AbstractCmdProcessor {

    private static Logger logger = LoggerFactory.getLogger(ExporterCmdProcessor.class);

    public ExporterCmdProcessor() {
        this.cmdLineSyntax = "[windows]dbtools.bat; [Unix/Linux]dbtools.sh";
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.riversoft.util.cli.AbstractCmdProcessor#getUsageOptions(org.apache.commons.cli.Options)
     */
    @Override
    protected Options getUsageOptions(Options options) {
        Options usageOptions = new Options();
        @SuppressWarnings("unchecked")
        Collection<Option> optionCollection = options.getOptions();

        for (Option op : optionCollection) {
            usageOptions.addOption(op);
        }
        return usageOptions;
    }

    @Override
    protected void processOptions(CommandLine cmdLine) {
        String conf = null;
        if (cmdLine.hasOption("c")) {
            conf = cmdLine.getOptionValue("c");
            logger.info("jdbc.properties 路径:" + conf);
        }

        if (StringUtils.isBlank(conf)) {
            logger.info("必须输入jdbc properties 文件路径。");
            return;
        }

        String exportPath = null;
        if (cmdLine.hasOption("f")) {
            exportPath = cmdLine.getOptionValue("f");
            logger.info("导出文件路径:" + exportPath);
        }

        if (StringUtils.isBlank(exportPath)) {
            logger.info("必须输入导出文件文件路径。");
            return;
        }

        DataSource dataSource;
        IDatabasePlatform databasePlatform;
        Database database;
        Exporter exporter;
        try {
            dataSource = DataSourceInstance.getInstance(conf).getDataSource();
            databasePlatform = JdbcDatabasePlatformFactory.createNewPlatformInstance(dataSource, new SqlTemplateSettings(), true);
            database = databasePlatform.readDatabase(databasePlatform.getDefaultCatalog(), databasePlatform.getDefaultSchema(), new String[]{});
            exporter = new Exporter(dataSource);
        } catch (Exception e) {
            logger.error("导出数据失败:", e);
            return;
        }

        try {
            String filename = "";
            if (cmdLine.hasOption("a")) {
                logger.info("导出所有的数据.");
                filename = exportPath + File.separator + "ALL.xlsx";
                exporter.doExportAll(getFileOutputStream(filename), ExcelType.EXCEL_AFTER_2003);
            } else {
                String stables;
                if (cmdLine.hasOption("t")) {
                    stables = cmdLine.getOptionValue("t").replace(" ", "");
                    filename = exportPath + File.separator + randomExportFileName() + ".xlsx";
                    String[] tables = stables.split(",");
                    List<String> exportTables = new ArrayList<>();

                    Table[] all = database.getTables();
                    for (String table : tables) {
                        int length = table.length();
                        int index = table.indexOf("*");
                        int lastIndex = table.lastIndexOf("*");

                        if (index == lastIndex) { //只有一个星号或者没有
                            if (index < 0) { //没有星号，精确匹配
                                Table t = database.findTable(table, false);
                                if (t != null) {
                                    exportTables.add(t.getName());
                                }
                            } else if (index == 0) { //以星号开头
                                findTablesEndsWith(exportTables, all, table.replaceAll("\\*", ""));
                            } else if (index == (length - 1)) { //以星号结尾
                                findTablesStartsWith(exportTables, all, table.replaceAll("\\*", ""));
                            } else { //星号在中间
                                logger.warn("不支持星号在中间。");
                            }
                        } else {//多个星号
                            logger.warn("不支持多个星号。");
                        }
                    }

                    if (exportTables.isEmpty()) {
                        logger.warn("没找到要导出的表!");
                    } else {
                        String[] exports = new String[exportTables.size()];
                        exportTables.toArray(exports);
                        logger.info("共有" + exports.length + "张表匹配.");
                        exporter.doExport(getFileOutputStream(filename), ExcelType.EXCEL_AFTER_2003, new NullDBOperationSignal(), exports);
                    }
                }
            }
            if (!"".equals(filename))
                logger.info("导出文件路径为:" + filename);
        } catch (Exception e) {
            logger.error("导出数据失败:", e);
        }

    }

    private void findTablesStartsWith(List<String> exportTables, Table[] tables, String table) {
        for (Table t : tables) {
            if (t.getName().toLowerCase().startsWith(table.toLowerCase())) {
                exportTables.add(t.getName());
            }
        }
    }

    private void findTablesEndsWith(List<String> exportTables, Table[] tables, String table) {
        for (Table t : tables) {
            if (t.getName().toLowerCase().endsWith(table.toLowerCase())) {
                exportTables.add(t.getName());
            }
        }
    }


    private String randomExportFileName() {
        return "export-" + Formatter.formatDatetime(new Date(), "yyyyMMdd-HHmmss");
    }

    public OutputStream getFileOutputStream(String filename) throws FileNotFoundException {
        FileOutputStream file;
        file = new FileOutputStream(new File(filename));

        return file;
    }

    @Override
    protected Options getOptions() {
        Options options = new Options();

        options.addOption("c", true, "-c conf: JDBC properties 文件位置");
        options.addOption("f", true, "-f excel: Excel文件位置");
        options.addOption("a", false, "-a 导出所有的表");
        options.addOption("t", true, "-t 表名:导出部分表， 如 -t CM_DOMAIN,CM_BASE，则所有名字包含CM_DOMAIN的表都会被导出。");

        return options;
    }

}
