/*
 * $HeadURL: $
 * $Id: $
 * Copyright (c) 2013 by Riversoft System, all rights reserved.
 */
package com.riversoft.core.db;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.hibernate.SessionFactory;
import org.hibernate.metadata.ClassMetadata;
import org.hibernate.type.EmbeddedComponentType;

import com.riversoft.core.BeanFactory;

/**
 * ORM映射服务
 * 
 * @author woden
 * 
 */
public class ORMEntityManager {

    public static class Column {
        private String name;
        private Class<?> javaClass;
        private String description;

        /**
         * @return the name
         */
        public String getName() {
            return name;
        }

        /**
         * @param name the name to set
         */
        public void setName(String name) {
            this.name = name;
        }

        /**
         * @return the javaClass
         */
        public Class<?> getJavaClass() {
            return javaClass;
        }

        /**
         * @param javaClass the javaClass to set
         */
        public void setJavaClass(Class<?> javaClass) {
            this.javaClass = javaClass;
        }

        /**
         * @return the description
         */
        public String getDescription() {
            return description;
        }

        /**
         * @param description the description to set
         */
        public void setDescription(String description) {
            this.description = description;
        }
    }

    /**
     * 获取通用数据库服务.<br>
     * 单例模式.
     * 
     * @return 通用数据库服务对象 <code>CommonService</code>.
     */
    public static ORMEntityManager getInstance() {
        ORMEntityManager service = BeanFactory.getInstance().getSingleBean(ORMEntityManager.class);
        return service;
    }

    /**
     * <code>SessionFactory</code>对象.
     */
    protected SessionFactory sessionFactory;

    /**
     * Spring容器使用.
     * 
     * @param sessionFactory
     */
    public void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    /**
     * 获取hibernate映射
     * 
     * @param entityName
     * @return
     */
    public ClassMetadata getMetadata(String entityName) {
        try {
            Class klass = Class.forName(entityName);
            return sessionFactory.getClassMetadata(klass);
        } catch (Exception e) {
            return sessionFactory.getClassMetadata(entityName);
        }
    }

    /**
     * 获取列信息
     * 
     * @param entityName
     * @return
     */
    public List<Column> getColumns(String entityName) {
        ClassMetadata metadata = getMetadata(entityName);
        List<Column> list = new ArrayList<>();
        if (metadata == null) {
            return list;
        }

        // 主键
        {
            if (StringUtils.isNotEmpty(metadata.getIdentifierPropertyName())) {// 单一主键
                String name = metadata.getIdentifierPropertyName();
                Column column = new Column();
                column.setName(name);
                column.setJavaClass(metadata.getPropertyType(name).getReturnedClass());
                list.add(column);
            } else {// 联合主键
                EmbeddedComponentType type = (EmbeddedComponentType) metadata.getIdentifierType();
                for (String name : type.getPropertyNames()) {
                    Column column = new Column();
                    column.setName(name);
                    column.setJavaClass(metadata.getPropertyType(name).getReturnedClass());
                    list.add(column);
                }
            }
        }

        for (String name : metadata.getPropertyNames()) {
            Column column = new Column();
            column.setName(name);
            column.setJavaClass(metadata.getPropertyType(name).getReturnedClass());
            list.add(column);
        }
        return list;
    }
}
