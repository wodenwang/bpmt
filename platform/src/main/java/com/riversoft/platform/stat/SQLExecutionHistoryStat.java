package com.riversoft.platform.stat;

import org.slf4j.Logger;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.*;

/**
 * Created by Borball on 14-3-18.
 */
public class SQLExecutionHistoryStat {

    private static Logger logger = LoggerFactory.getLogger(SQLExecutionHistoryStat.class);

    private static final String SEPARATOR = "|";
    private static final String SPLIT_SEPARATOR = "\\|";

    private static SQLExecutionHistoryStat instance;
    private RunningStatus status;
    private Date lastCompletedTime;
    private Map<SingleSelectSQLStatement, Integer> result;
    private static int NORMAL_WEIGHT = 1;
    private static int SLOW_WEIGHT = 20;

    private SQLExecutionHistoryStat() {
        status = RunningStatus.NOT_START;
        lastCompletedTime = new Date();
        result = new HashMap<>();
    }

    public synchronized static SQLExecutionHistoryStat getInstance() {
        if (instance == null) {
            instance = new SQLExecutionHistoryStat();
        }
        return instance;
    }

    enum RunningStatus {
        NOT_START, RUNNING, COMPLETED
    }

    public boolean isRunning() {
        return RunningStatus.RUNNING == status;
    }

    public Map<SingleSelectSQLStatement, Integer> analyse(File folder) throws Exception {
        if (alreadyRunToday()) { //今天已经跑过了
            logger.info("今天已经分析过，直接返回当天分析结果。");
            return result;
        } else {
            if (!isRunning()) { //是否正在分析
                logger.info("当天没有分析过，也没有其他线程正在分析。");
                status = RunningStatus.RUNNING;
                result.clear();
                Collection<File> files = listAllLogsInPast7Days(folder);
                if (!files.isEmpty()) {
                    for (File file : files) {
                        logger.info("开始分析文件:" + file.getName());
                        List<String> lines = FileUtils.readLines(file);
                        for (String line : lines) parseAndIncrement(line);
                    }

                }

                status = RunningStatus.COMPLETED;
                lastCompletedTime = new Date();

                return result;
            } else {
                logger.info("可能有其他线程正在分析。");
                return Collections.emptyMap();
            }
        }
    }

    private boolean alreadyRunToday() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, -1);

        return lastCompletedTime.before(calendar.getTime()) && RunningStatus.COMPLETED == status;
    }

    private void parseAndIncrement(String line) {
        if (isCompleteLine(line)) { //一行完整的sql
            String[] items = line.split(SPLIT_SEPARATOR);
            String time = items[0];
            String sql = items[1];

            if (isSingleSelectSQL(sql)) { //单表查询的sql
                SingleSelectSQLStatement statement = SQLParser.parseSingleSelectSQL(sql);

                if (StringUtils.isEmpty(time)) { //normal sql
                    increment(statement, NORMAL_WEIGHT);
                } else { //slow sql
                    increment(statement, SLOW_WEIGHT);
                }
            } else { //其他的先登记起来
                record(line);
            }
        } else { //不完整的暂时不做处理，打印出来
            record(line);
        }

    }

    private boolean isCompleteLine(String line) {
        int index = line.indexOf(SEPARATOR);
        int lastIndex = line.lastIndexOf(SEPARATOR);
        return (index > -1) && (lastIndex > -1) && (index != lastIndex);
    }

    private void increment(SingleSelectSQLStatement statement, int weight) {
        if (result.containsKey(statement)) {
            result.put(statement, result.get(statement) + weight);
        } else {
            result.put(statement, weight);
        }
    }

    private void record(String select) {
        logger.info("Ignore:" + select);
    }

    public boolean isSingleSelectSQL(String sql) {
        int fromIndex = sql.indexOf("from");
        int whereIndex = sql.indexOf("where");

        String fromTables;
        if (whereIndex > -1) { //contains where
            fromTables = sql.substring(fromIndex + 5, whereIndex);
        } else {
            fromTables = sql.substring(fromIndex + 5);
        }
        return !fromTables.contains(",");
    }

    private Collection<File> listAllLogsInPast7Days(File root) {
        if (root.exists() && root.isDirectory()) {
            return FileUtils.listFiles(root, new IOFileFilter() {
                        @Override
                        public boolean accept(File file) {
                            Calendar calendar = Calendar.getInstance();
                            calendar.add(Calendar.DAY_OF_MONTH, -7);

                            return FileUtils.isFileNewer(file, calendar.getTime());
                        }

                        @Override
                        public boolean accept(File dir, String name) {
                            return false;
                        }
                    }, new IOFileFilter() {
                        @Override
                        public boolean accept(File file) {
                            return false;
                        }

                        @Override
                        public boolean accept(File dir, String name) {
                            return false;
                        }
                    }
            );

        }
        return Collections.emptyList();
    }

}
