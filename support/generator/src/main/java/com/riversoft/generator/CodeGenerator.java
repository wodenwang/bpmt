/*
 * $HeadURL: $
 * $Id: $
 * Copyright (c) 2013 by Riversoft System, all rights reserved.
 */
package com.riversoft.generator;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.riversoft.core.db.hbm.HbmClass;
import com.riversoft.core.db.hbm.model.HbmModelConverter;

/**
 * @author Borball
 * 
 */
public class CodeGenerator {

    private final String DEFAULT_PACKAGE_NAME = "com/riversoft/module/";

    Logger logger = LoggerFactory.getLogger(CodeGenerator.class);

    List<Generator> generators;
    Settings settings;
    Map<String, Object> context = new HashMap<>();

    void initGenerators() {
        generators = new ArrayList<>();

        Generator actionGenerator = new FreemarkerGenerator(StringUtils.capitalize(settings.getModule())
                + "CRUDAction.java", "CRUDAction.ftl");
        generators.add(actionGenerator);

        if (settings.isHasMain()) {
            Generator mainGenerator = new FreemarkerGenerator("main.jsp", "main.ftl");
            generators.add(mainGenerator);
        }

        if (settings.isHasList()) {
            Generator listGenerator = new FreemarkerGenerator("list.jsp", "list.ftl");
            generators.add(listGenerator);
        }

        if (settings.isHasDetail()) {
            Generator detailGenerator = new FreemarkerGenerator("detail.jsp", "detail.ftl");
            generators.add(detailGenerator);
        }

        if (settings.isHasForm()) {
            Generator formGenerator = new FreemarkerGenerator("form.jsp", "form.ftl");
            generators.add(formGenerator);

        }
        if (settings.isHasBatch()) {
            Generator batchGenerator = new FreemarkerGenerator("batch.jsp", "batch.ftl");
            generators.add(batchGenerator);
        }

    }

    /**
     * TODO 扩展表如何处理?
     * 
     * @param hbm
     */
    void initContexts() {
        context.put("settings", settings);

        InputStream in = this.getClass().getClassLoader().getResourceAsStream(settings.getHbmName());
        HbmClass processed = (HbmClass) HbmModelConverter.toBean(in);
        context.put("ids", processed.getId().getProperties());
        context.put("columns", processed.getPropertys());

        context.put("vars", null);

    }

    public CodeGenerator(Settings settings) {
        this.settings = settings;
        initGenerators();
        initContexts();

    }

    public void generate() throws IOException {
        logger.info("准备开始生成代码......");
        String outDir = settings.getOutdir();
        
        String dir = outDir + DEFAULT_PACKAGE_NAME + StringUtils.lowerCase(settings.getModule());
        logger.info("代码存放目录:" + dir);
        File parent = new File(dir);
        if (!parent.exists()){
            parent.mkdirs();
            logger.info(dir + "已经创建。");
        }

        for (Generator generator : generators) {
            logger.info("正在生成:" + generator.getName());
            File file = new File(parent, generator.getName());
            String output = generator.generate(context);
            FileUtils.copyInputStreamToFile(new ByteArrayInputStream(output.getBytes()), file);
            logger.info(generator.getName() + ": 成功生成.");
        }
    }

}
