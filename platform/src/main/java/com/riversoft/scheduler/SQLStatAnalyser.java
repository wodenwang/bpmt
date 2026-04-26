package com.riversoft.scheduler;

import com.riversoft.core.Config;
import com.riversoft.platform.Platform;
import com.riversoft.platform.stat.SQLExecutionHistoryStat;
import com.riversoft.platform.stat.SingleSelectSQLStatement;
import com.riversoft.scheduler.annotation.QuartzJob;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.*;

/**
 * Created by exizhai on 05/01/2015.
 */
@QuartzJob(cronExp = "0 30 03 * * ?", name = "SQLStatAnalyser", group = "System", desc = "SQL性能分析")
public class SQLStatAnalyser implements Job {

    private Logger logger = LoggerFactory.getLogger("sql.stat.logger");

    private boolean isEnable(){
        return Boolean.valueOf(Config.get("sql.stat.enable") == null ? "false" : Config.get("sql.stat.enable"));
    }

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        if(isEnable()) {
            logger.info("开始进行SQL性能分析.");
            File sqlStatFileLog = new File(Platform.getLogPath(), "sql");
            if (sqlStatFileLog.exists()) {

                try {
                    Map<SingleSelectSQLStatement, Integer> result = SQLExecutionHistoryStat.getInstance().analyse(sqlStatFileLog);
                    if(!result.isEmpty()) {
                        logger.info("分析结果为:");
                        List<Map.Entry<SingleSelectSQLStatement, Integer>> list = new LinkedList<>(result.entrySet());
                        Collections.sort(list, new Comparator<Map.Entry<SingleSelectSQLStatement, Integer>>() {
                            public int compare(Map.Entry<SingleSelectSQLStatement, Integer> o1, Map.Entry<SingleSelectSQLStatement, Integer> o2) {
                                return (o1.getValue()).compareTo(o2.getValue());
                            }
                        });

                        Map<SingleSelectSQLStatement, Integer> sortResult = new LinkedHashMap<>();
                        for (Map.Entry<SingleSelectSQLStatement, Integer> entry : list) {
                            SingleSelectSQLStatement statement = entry.getKey();
                            statement.setTime(entry.getValue());
                            sortResult.put(statement, entry.getValue());
                            logger.info(entry.getKey().toString());
                        }
                        logger.info("分析完毕.");
                    } else {
                        logger.warn("分析结果为空,logs/sql 中可能没有足够数据.");
                    }
                } catch (Exception e) {
                    logger.error("分析失败:", e);
                }
            } else {
                logger.warn("logs/sql 不存在, 不能进行SQL性能分析.");
            }
        } else {
            logger.warn("SQL分析没有打开.");
        }
    }
}
