/*
 * $HeadURL: $
 * $Id: $
 * Copyright (c) 2014 by Riversoft System, all rights reserved.
 */
package com.riversoft.platform.web.handler;

import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.RegexPatternTypeFilter;

import com.riversoft.core.db.DataPO;
import com.riversoft.core.exception.SystemRuntimeException;

/**
 * 分类数据录入框架
 * 
 * @author woden
 * 
 */
public abstract class BaseDataBuilder {
    /**
     * Logger for this class
     */
    private static final Logger logger = LoggerFactory.getLogger(BaseDataBuilder.class);

    private String packageName;
    private DataPO dataPO;

    protected BaseDataBuilder(DataPO dataPO) {
        this.dataPO = dataPO;
        this.packageName = this.getClass().getPackage().getName();
    }

    /**
     * 设置基础数据
     */
    public abstract void build();

    /**
     * 处理配置数据
     */
    public void handleConfig() {
        ClassPathScanningCandidateComponentProvider scanner = new ClassPathScanningCandidateComponentProvider(false);
        scanner.addIncludeFilter(new RegexPatternTypeFilter(Pattern.compile(packageName + ".*Handler")));
        for (BeanDefinition bd : scanner.findCandidateComponents(getClass().getPackage().getName())) {
            String clazzName = bd.getBeanClassName();
            Class<?> clazz;
            try {
                clazz = Class.forName(clazzName);
                Object object = clazz.newInstance();
                if (!(object instanceof Handler)) {
                    continue;
                }

                Handler handler = (Handler) object;
                handler.handle(dataPO);
            } catch (SystemRuntimeException e) {
                throw e;
            } catch (Exception e) {
                logger.error("找不到类.", e);
                throw new SystemRuntimeException("处理配置参数出错.", e);
            }
        }
    }

    /**
     * 获取数据结构
     * 
     * @return
     */
    public final DataPO getDataPO() {
        return dataPO;
    }
}
