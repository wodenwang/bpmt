/*
 * $HeadURL: $
 * $Id: $
 * Copyright (c) 2013 by Riversoft System, all rights reserved.
 */
package com.riversoft.dbtool.export;

import com.riversoft.dbtool.util.DataSourceInstance;
import com.riversoft.util.cli.AbstractCmdProcessor;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.Collection;

/**
 * @author Borball
 */
public class ImporterCmdProcessor extends AbstractCmdProcessor {

    private static Logger logger = LoggerFactory.getLogger(ImporterCmdProcessor.class);

    public ImporterCmdProcessor() {
        this.cmdLineSyntax = "[windows]dbtools.bat; [Unix/Linux]dbtools.sh";
    }

    @Override
    protected Options getOptions() {
        Options options = new Options();

        options.addOption("d", false, "-d: 导入数据之前删除旧有记录");
        options.addOption("e", false, "-e: 如果出错则直接退出");
        options.addOption("r", false, "-r: 如果冲突用新的数据替换旧的");
        options.addOption("f", true, "-f excel: Excel文件位置");
        options.addOption("c", true, "-c conf: conf文件位置");

        return options;
    }

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
        String excel = null;
        if (cmdLine.hasOption("f")) {
            excel = cmdLine.getOptionValue("f");
            logger.info("excel 路径:" + excel);
        }

        if (StringUtils.isBlank(excel)) {
            logger.info("必须输入excel文件路径。");
            return;
        }

        String conf = null;
        if (cmdLine.hasOption("c")) {
            conf = cmdLine.getOptionValue("c");
            logger.info("jdbc.properties 路径:" + conf);
        }

        if (StringUtils.isBlank(conf)) {
            logger.info("必须输入jdbc properties 文件路径。");
            return;
        }

        boolean clearBeforeImport = false;
        boolean replaceIfConflict = false;
        boolean exitIfError = false;

        logger.info("您的选择为:");
        if (cmdLine.hasOption("d")) {
            clearBeforeImport = true;
            logger.info("导入之前清除表中所有数据");
        } else {
            logger.info("导入之前不清除原有数据");
        }


        if (cmdLine.hasOption("e")) {
            exitIfError = true;
            logger.info("如果导入出错，终止导入.");
        } else {
            logger.info("如果导入出错，则忽略错误继续导入.");
        }


        if (cmdLine.hasOption("r")) {
            replaceIfConflict = true;
            logger.info("如果有数据冲突，替换原有数据.");
        } else {
            logger.info("如果有数据冲突，不替原有数据.");
        }

        try {
            Importer importer = new Importer(DataSourceInstance.getInstance(conf).getDataSource());

            importer.doImport(new File(excel), clearBeforeImport, replaceIfConflict, exitIfError);
        } catch (Exception e) {
            logger.error("导入数据失败:", e);
        }
    }

}
