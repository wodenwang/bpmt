package com.riversoft.dtask.tasks;

import com.riversoft.dbtool.export.DataSourceCopier;
import com.riversoft.dbtool.util.DatabaseManager;
import com.riversoft.util.Formatter;
import org.apache.commons.lang3.StringUtils;
import org.apache.tools.ant.input.InputHandler;
import org.apache.tools.ant.input.InputRequest;
import org.h2.jdbcx.JdbcDataSource;
import org.jumpmind.db.model.Database;
import org.jumpmind.db.model.Table;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Created by exizhai on 06/12/2014.
 */
public class DBCopyTask extends DBBaseTask {

    private static final String DEFAULT_SEPARATOR = ",";
    private static final String STAR = "*";
    private static Logger logger = LoggerFactory.getLogger("DBCopyTask");
    private static String DOUBLE_CONFIRMATION = "Y";
    private boolean fromH2;
    private String h2Path;
    private boolean exitIfError;
    private boolean clearBeforeCopy;
    private boolean replaceIfConflict;
    private Set<String> matchedTables = new LinkedHashSet<>();
    //查询条件
    private String query;

    public void setFromH2(String fromH2) {
        this.fromH2 = Boolean.valueOf(fromH2);
    }

    public void setH2Path(String h2Path) {
        this.h2Path = h2Path;
    }

    public void setExitIfError(boolean exitIfError) {
        this.exitIfError = Boolean.valueOf(exitIfError);
    }

    public void setClearBeforeCopy(boolean clearBeforeCopy) {
        this.clearBeforeCopy = Boolean.valueOf(clearBeforeCopy);
    }

    public void setReplaceIfConflict(boolean replaceIfConflict) {
        this.replaceIfConflict = Boolean.valueOf(replaceIfConflict);
    }

    @Override
    public void dbOperation() {
        DataSourceCopier copier;
        JdbcDataSource h2ds = new JdbcDataSource();

        h2ds.setUser("sa");
        h2ds.setPassword("");

        String h2FileName = generateH2FileName();

        if (fromH2) {
            logger.info("从H2:" + h2Path + "拷贝到系统数据库.");
            h2ds.setURL(buildJDBCURL(trimH2Ext(h2Path)));
            copier = new DataSourceCopier(h2ds, dataSource);
        } else {
            logger.info("从系统数据库拷贝到H2:" + h2Path);
            h2ds.setURL(buildJDBCURL(h2FileName));
            copier = new DataSourceCopier(dataSource, h2ds);
            logger.info("拷贝的文件位于:" + h2FileName);
        }

        untilGetValidCondition(fromH2 ? h2ds : dataSource);

        logger.info("共有" + matchedTables.size() + "表需要拷贝。");

        try {
            if (fromH2) {
                copier.copyWithoutDDL(matchedTables, exitIfError, clearBeforeCopy, replaceIfConflict);
            } else {
                copier.copyWithDDL(matchedTables, exitIfError);
                logger.info("拷贝的文件位于:" + h2FileName);
            }
            logger.info("操作完成。");
        } catch (Exception e) {
            logger.error("操作失败:", e);
        }
    }

    private String generateH2FileName() {
        return h2Path + "/river-" + Formatter.formatDatetime(new Date(), "yyyyMMddHHmmss");
    }

    private void untilGetValidCondition(DataSource fromDataSource) {
        String normalPrompt = "请输入要拷贝的表名称，可以逗号分隔；允许以*号开头或者结尾；全库导出请输入*；无星号表示精确匹配；此功能不区分大小写。";
        String notFoundPrompt = "没有找到要导出的表，请重新输入:";
        InputHandler inputHandler = getProject().getInputHandler();
        InputRequest ir = new InputRequest(normalPrompt);
        boolean inputAccepted = false;
        while (!inputAccepted) {
            inputHandler.handleInput(ir);
            String input = ir.getInput();
            if (isValidCondition(input)) { //条件合法
                logger.info("输入条件[{}]合法， 正为你查询符合条件的库表，请稍后。", input);
                findTableMatches(fromDataSource, input);
                if (matchedTables.isEmpty()) {//没找到记录
                    ir = new InputRequest(notFoundPrompt);
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
                        matchedTables.clear();
                        ir = new InputRequest(normalPrompt);
                    }
                }
            } else {
                logger.info("输入条件非法，请检查后重新输入。");
                ir = new InputRequest(normalPrompt);
            }
        }
    }

    private void findTableMatches(DataSource fromDataSource, String input) {
        DatabaseManager databaseManager = new DatabaseManager(fromDataSource);
        Database fromDatabase = databaseManager.readDatabase();

        Table[] all = fromDatabase.getTables();
        logger.info("当前数据库共有表:" + all.length);
        String[] conditions = input.split(DEFAULT_SEPARATOR);
        for (String table : conditions) {
            if (table.contains(STAR)) {
                if (table.startsWith(STAR)) {
                    findTablesEndsWith(matchedTables, all, table.replaceAll("\\*", ""));
                }
                if (table.endsWith(STAR)) {
                    findTablesStartsWith(matchedTables, all, table.replaceAll("\\*", ""));
                }
            } else {
                Table t = fromDatabase.findTable(table, false);
                if (t != null) {
                    matchedTables.add(t.getName());
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
        String prompt = "以下是满足条件的数据表，确认请输入Y， 重新选择请输入任意字符。";
        String mItemFormat = "[%1$s] : %2$s";
        StringBuffer sb = new StringBuffer(prompt);
        sb.append(NEW_LINE);

        int cIx = 1;
        for (String table : matchedTables) {
            sb.append(String.format(mItemFormat, cIx, table));
            sb.append(NEW_LINE);
            cIx++;
        }
        sb.append(NEW_LINE);
        sb.append("以上是满足条件的数据表，确认请输入Y， 重新选择请输入任意字符。");
        return sb.toString();
    }
}
