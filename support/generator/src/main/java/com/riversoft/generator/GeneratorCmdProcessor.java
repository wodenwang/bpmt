/*
 * $HeadURL: $
 * $Id: $
 * Copyright (c) 2013 by Riversoft System, all rights reserved.
 */
package com.riversoft.generator;

import java.io.IOException;
import java.util.Collection;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.lang3.StringUtils;

import com.riversoft.util.PropertiesLoader;
import com.riversoft.util.cli.AbstractCmdProcessor;

/**
 * @author Borball
 * 
 */
public class GeneratorCmdProcessor extends AbstractCmdProcessor {

    private final String DEFAULT_OUT_DIR = "src/test/java/";

    public GeneratorCmdProcessor() {
        this.cmdLineSyntax = "com.riversoft.generator.Main [-h] [-f 配置文件位置]\n";
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
        String config = null;
        if (cmdLine.hasOption("f")) {
            config = cmdLine.getOptionValue("f");
        }

        if (StringUtils.isBlank(config)) {
            out("必须输入配置文件。");
            return;
        }

        Settings settings = readSettings(config);

        CodeGenerator generator = new CodeGenerator(settings);

        try {
            generator.generate();
        } catch (IOException e) {
            out("生成代码异常:" + e.getMessage());
        }
    }

    @Override
    protected Options getOptions() {
        Options options = new Options();

        options.addOption("f", true, "配置文件，文件路径使用Spring Resource格式");

        return options;
    }

    private Settings readSettings(String config) {
        PropertiesLoader propertiesLoader = new PropertiesLoader(config);

        Settings settings = new Settings();
        settings.setModule(StringUtils.lowerCase(propertiesLoader.getProperty("module")));
        settings.setAuthor(propertiesLoader.getProperty("author"));

        settings.setOutdir(propertiesLoader.getProperty("outdir"));

        if (StringUtils.isBlank(settings.getOutdir()))
            settings.setOutdir(DEFAULT_OUT_DIR);

        settings.setHbmName(propertiesLoader.getProperty("hbmName"));
        settings.setExtHbmName(propertiesLoader.getProperty("extHbmName"));

        settings.setPagePath(propertiesLoader.getProperty("pagePath"));
        if (StringUtils.isBlank(settings.getPagePath()))
            settings.setPagePath("/module/" + settings.getModule() + "/" + StringUtils.capitalize(settings.getModule())
                    + "CRUDAction");

        settings.setHasMain(propertiesLoader.getBoolean("hasPageMain"));
        settings.setHasList(propertiesLoader.getBoolean("hasPageList"));
        settings.setHasForm(propertiesLoader.getBoolean("hasPageForm"));
        settings.setHasDetail(propertiesLoader.getBoolean("hasPageDetail"));
        settings.setHasBatch(propertiesLoader.getBoolean("hasPageBatch"));

        settings.setListOverwrite(propertiesLoader.getBoolean("isListOverwrite"));

        return settings;
    }

    private void out(String message) {
        System.out.println(message);
    }

}
