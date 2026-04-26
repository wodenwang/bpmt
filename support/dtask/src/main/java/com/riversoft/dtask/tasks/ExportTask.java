package com.riversoft.dtask.tasks;

import com.riversoft.dbtool.export.Exporter;
import com.riversoft.util.Formatter;
import org.apache.commons.lang3.StringUtils;
import org.apache.tools.ant.input.InputHandler;
import org.apache.tools.ant.input.InputRequest;
import org.jumpmind.db.model.Table;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * A task to handle db export
 * Created by exizhai on 2/11/14.
 */
public class ExportTask extends DBBaseTask {

    private Logger logger = LoggerFactory.getLogger("ExportTask");
    private static String DOUBLE_CONFIRMATION = "Y";

    private static final String DEFAULT_SEPARATOR = ",";
    private static final String STAR = "*";
    private Set<String> searchOutTables = new LinkedHashSet<>();
    private Set<String> choiceList = new LinkedHashSet<>();

    //查询条件
    private String query;

    private String exportDir;

    public void setExportDir(String exportDir) {
        this.exportDir = exportDir;
    }

    @Override
    public void dbOperation() {
        untilGetValidCondition();

        Exporter exporter = new Exporter(dataSource);
        String[] exports = new String[searchOutTables.size()];
        searchOutTables.toArray(exports);
        logger.info("共有" + exports.length + "张表匹配.");

        String filename = exportDir + File.separator + randomFileName() + ".xlsx";
        try {
            exporter.doExport(getFileOutputStream(filename), Exporter.ExcelType.EXCEL_AFTER_2003, exports);
            logger.info("导出文件位于:" + filename);
        } catch (Exception e) {
            logger.error("导出失败：" + e.getLocalizedMessage());
        }
    }

    private void untilGetValidCondition() {
        String normalPrompt = "请输入要导出的表名称，可以逗号分隔；允许以*号开头或者结尾；全库导出请输入*；无星号表示精确匹配；此功能不区分大小写。";
        String notfoundPrompt = "没有找到要导出的表，请重新输入:";
        InputHandler inputHandler = getProject().getInputHandler();
        InputRequest ir = new InputRequest(normalPrompt);
        boolean inputAccepted = false;
        while (!inputAccepted) {
            inputHandler.handleInput(ir);
            String input = ir.getInput();
            if (isValidCondition(input)) { //条件合法
                logger.info("输入条件合法:" + input);
                findTableMatches(input);
                if (searchOutTables.isEmpty()) {//没找到记录
                    ir = new InputRequest(notfoundPrompt);
                } else {//找到记录
                    logger.info("++++++++++++++++++++++++++++++++++++++++++++++++++++");
                    String choices = generateChoices();
                    ir = new InputRequest(choices);
                    inputHandler.handleInput(ir);
                    String select = ir.getInput();
                    if (DOUBLE_CONFIRMATION.equalsIgnoreCase(select)) { //确认
                        inputAccepted = true;
                        this.query = input;
                    } else {
                        searchOutTables.clear();
                        ir = new InputRequest(normalPrompt);
                    }
                }
            } else {
                logger.info("输入条件非法，请重新输入");
                ir = new InputRequest(normalPrompt);
            }
        }
    }

    private void findTableMatches(String input) {
        Table[] all = database.getTables();
        logger.info("当前数据库共有表:" + all.length);
        String[] conditions = input.split(DEFAULT_SEPARATOR);
        for (String table : conditions) {
            if (table.contains(STAR)) {
                if (table.startsWith(STAR)) {
                    findTablesEndsWith(searchOutTables, all, table.replaceAll("\\*", ""));
                }
                if (table.endsWith(STAR)) {
                    findTablesStartsWith(searchOutTables, all, table.replaceAll("\\*", ""));
                }
            } else {
                Table t = database.findTable(table, false);
                if (t != null) {
                    searchOutTables.add(t.getName());
                }
            }
        }

    }

    private boolean isValidCondition(String input) {
        if (StringUtils.isNotEmpty(input)) {
            String[] conditions = input.split(DEFAULT_SEPARATOR);
            for (String table : conditions) {
                int length = table.length();
                int index = table.indexOf("\\*");
                int lastIndex = table.lastIndexOf("\\*");

                if (index == lastIndex) { //只有一个星号或者没有
                    if (index < 0) { //没有星号，精确匹配
                        continue;
                    } else if (index == 0) { //以星号开头
                        continue;
                    } else if (index == (length - 1)) { //以星号结尾
                        continue;
                    } else { //星号在中间
                        return false;
                    }
                } else {//多个星号
                    return false;
                }
            }
            return true;
        } else {
            return false;
        }
    }

    private void findTablesStartsWith(Set<String> exportTables, Table[] tables, String table) {
        for (Table t : tables) {
            if (t.getName().toLowerCase().startsWith(table.toLowerCase())) {
                exportTables.add(t.getName());
            }
        }
    }

    private void findTablesEndsWith(Set<String> exportTables, Table[] tables, String table) {
        for (Table t : tables) {
            if (t.getName().toLowerCase().endsWith(table.toLowerCase())) {
                exportTables.add(t.getName());
            }
        }
    }

    private String generateChoices() {
        choiceList = new HashSet<>();
        String prompt = "以下是满足条件的数据表，请选择要导出的表：确认请输入Y， 重新选择请输入任意字符";
        String mItemFormat = "[%1$s] : %2$s";
        StringBuffer sb = new StringBuffer(prompt);
        sb.append(NEW_LINE);

        int cIx = 1;
        for (String table : searchOutTables) {
            choiceList.add(table);
            sb.append(String.format(mItemFormat, cIx, table));
            sb.append(NEW_LINE);
            cIx++;
        }
        sb.append(NEW_LINE);
        sb.append("以上是满足条件的数据表，确认请输入Y， 重新选择请输入任意字符。");
        return sb.toString();
    }

    public OutputStream getFileOutputStream(String filename) throws FileNotFoundException {
        FileOutputStream file;
        file = new FileOutputStream(new File(filename));
        return file;
    }

    private String randomFileName() {
        return "export-" + Formatter.formatDatetime(new Date(), "yyyyMMdd-HHmmss");
    }
}
