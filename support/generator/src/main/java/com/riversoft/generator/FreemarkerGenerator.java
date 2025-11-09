/*
 * $HeadURL: $
 * $Id: $
 * Copyright (c) 2013 by Riversoft System, all rights reserved.
 */
package com.riversoft.generator;

import java.io.IOException;
import java.util.Map;

import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;

import com.riversoft.core.BeanFactory;
import com.riversoft.core.exception.ExceptionType;
import com.riversoft.core.exception.SystemRuntimeException;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

/**
 * @author Borball
 * 
 */
public class FreemarkerGenerator implements Generator {
    
    private final static String CODE_TEMPLATE_CONFIG_KEY = "codeGeneratorTemplateConfiguration";

    private String name;
    private String templateFile;
    
    public FreemarkerGenerator(){
    }
    
    public FreemarkerGenerator(String name, String templateFile){
        this.name = name;
        this.templateFile = templateFile;
    }
    
    public String getName() {
        return name;
    }

    @Override
    public String generate(Map<String, Object> context) {
        Configuration configuration = (Configuration) BeanFactory.getInstance().getBean(CODE_TEMPLATE_CONFIG_KEY);

        try {
            Template template = configuration.getTemplate(templateFile);

            return FreeMarkerTemplateUtils.processTemplateIntoString(template, context);
        } catch (IOException | TemplateException e) {
            throw new SystemRuntimeException(ExceptionType.CODING, e);
        }
    }

}
