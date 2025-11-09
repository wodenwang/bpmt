/*
 * $HeadURL: $
 * $Id: $
 * Copyright (c) 2013 by Riversoft System, all rights reserved.
 */
package com.riversoft.platform.db;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.PropertyUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.riversoft.core.BeanFactory;
import com.riversoft.core.db.JdbcService;
import com.riversoft.core.db.ORMService;
import com.riversoft.core.db.po.Code2NameTreeVO;
import com.riversoft.core.db.po.Code2NameVO;
import com.riversoft.core.exception.SystemRuntimeException;

/**
 * 翻译服务
 * 
 * @author Woden
 * 
 */
public class Code2NameService {
    /**
     * Logger for this class
     */
    private static final Logger logger = LoggerFactory.getLogger(Code2NameService.class);

    /**
     * 获取单例
     * 
     * @return
     */
    public static Code2NameService getInstance() {
        Code2NameService service = BeanFactory.getInstance().getSingleBean(Code2NameService.class);
        return service;
    }

    /**
     * 翻译(JDBC方式)
     * 
     * @param table
     * @param code
     * @param name
     * @param obj
     * @param hql
     * @return
     */
    public Object translateJDBC(String table, String code, String name, Object obj) {
        JdbcService service = JdbcService.getInstance();
        StringBuffer sql = new StringBuffer("select  *  from ").append(table).append("   where ").append(code)
                .append(" =  ?");
        Map<String, Object> result = service.findSQL(sql.toString(), obj);
        if (result != null && result.containsKey(name)) {
            return result.get(name);
        }

        return null;
    }

    /**
     * 翻译(ORM方式)
     * 
     * @param entity 翻译实体
     * @param code 翻译code字段
     * @param name 翻译name字段
     * @param obj 翻译code值
     * @return 翻译name值
     */
    public Object translateORM(String entity, String code, String name, Object obj) {
        ORMService service = ORMService.getInstance();
        // 查询语句： select VO.name from voName as VO where VO.code=?
        StringBuffer hql = new StringBuffer("select VO.").append(name).append(" from ").append(entity)
                .append(" as VO where VO.").append(code).append(" =  ?");
        return service.findHQL(hql.toString(), obj);
    }

    /**
     * 获取翻译列表(ORM方式)
     * 
     * @param entity
     * @param code
     * @param name
     * @param condition
     * @return
     */
    public List<Code2NameVO> getListORM(String entity, String code, String name, String condition) {
        ORMService service = ORMService.getInstance();
        StringBuffer hql = new StringBuffer();
        hql.append("from ").append(entity);
        if (condition != null) {
            hql.append(" where ").append(condition);
        }
        List<?> list = service.queryHQL(hql.toString());
        List<Code2NameVO> result = new ArrayList<>();
        try {
            if (list != null) {
                for (Object obj : list) {
                    VO vo = new VO();
                    vo.setCode(PropertyUtils.getProperty(obj, code));
                    vo.setShowName(PropertyUtils.getProperty(obj, name).toString());
                    result.add(vo);
                }
            }
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            logger.error("找不到对于的属性。", e);
            throw new SystemRuntimeException(e);
        }
        return result;
    }

    /**
     * 获取翻译列表(ORM方式)
     * 
     * @param entity
     * @param code
     * @param name
     * @return
     */
    public List<Code2NameVO> getListORM(String entity, String code, String name) {
        return getListORM(entity, code, name, null);
    }

    /**
     * 获取翻译列表(JDBC方式)
     * 
     * @param table
     * @param code
     * @param name
     * @param condition
     * @return
     */
    public List<Code2NameVO> getListJDBC(String table, String code, String name, String condition) {
        JdbcService service = JdbcService.getInstance();
        StringBuffer sql = new StringBuffer().append("select * from ").append(table);
        if (condition != null) {
            sql.append(" where ").append(condition);
        }
        List<Map<String, Object>> list = service.querySQL(sql.toString());
        List<Code2NameVO> result = new ArrayList<>();
        try {
            if (list != null) {
                for (Map<String, Object> obj : list) {
                    VO vo = new VO();
                    vo.setCode(PropertyUtils.getProperty(obj, code));
                    Object showName = PropertyUtils.getProperty(obj, name);
                    vo.setShowName(showName != null ? showName.toString() : "");
                    result.add(vo);
                }
            }
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            logger.error("找不到对于的属性。", e);
            throw new SystemRuntimeException(e);
        }
        return result;
    }

    /**
     * 获取翻译列表(JDBC方式)
     * 
     * @param table
     * @param code
     * @param name
     * @return
     */
    public List<Code2NameVO> getListJDBC(String table, String code, String name) {
        return getListJDBC(table, code, name, null);
    }

    /**
     * 获取翻译树(JDBC方式)
     * 
     * @param table
     * @param code
     * @param parentCode
     * @param name
     * @param condition
     * @return
     */
    public List<Code2NameTreeVO> getTreesJDBC(String table, String code, String parentCode, String name,
            String condition) {
        JdbcService service = JdbcService.getInstance();
        StringBuffer sql = new StringBuffer().append("select * from ").append(table);
        if (condition != null) {
            sql.append(" where ").append(condition);
        }
        List<Map<String, Object>> list = service.querySQL(sql.toString());
        List<Code2NameTreeVO> result = new ArrayList<>();
        try {
            if (list != null) {
                for (Map<String, Object> obj : list) {
                    TreeVO vo = new TreeVO();
                    vo.setCode(PropertyUtils.getProperty(obj, code));
                    vo.setShowName(PropertyUtils.getProperty(obj, name).toString());
                    vo.setParentCode(PropertyUtils.getProperty(obj, parentCode));
                    result.add(vo);
                }
            }
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            logger.error("找不到对于的属性。", e);
            throw new SystemRuntimeException(e);
        }
        return result;
    }

    /**
     * 获取翻译树(JDBC方式)
     * 
     * @param table
     * @param code
     * @param parentCode
     * @param name
     * @return
     */
    public List<Code2NameTreeVO> getTreesJDBC(String table, String code, String parentCode, String name) {
        return getTreesJDBC(table, code, parentCode, name, null);
    }

    /**
     * 获取翻译树(ORM方式)
     * 
     * @param entity
     * @param code
     * @param parentCode
     * @param name
     * @param condition
     * @return
     */
    public List<Code2NameTreeVO> getTreesORM(String entity, String code, String parentCode, String name,
            String condition) {
        ORMService service = ORMService.getInstance();
        StringBuffer hql = new StringBuffer();
        hql.append("from ").append(entity);
        if (condition != null) {
            hql.append(" where ").append(condition);
        }
        List<?> list = service.queryHQL(hql.toString());
        List<Code2NameTreeVO> result = new ArrayList<>();
        try {
            if (list != null) {
                for (Object obj : list) {
                    TreeVO vo = new TreeVO();
                    vo.setCode(PropertyUtils.getProperty(obj, code));
                    vo.setShowName(PropertyUtils.getProperty(obj, name).toString());
                    vo.setParentCode(PropertyUtils.getProperty(obj, parentCode));
                    result.add(vo);
                }
            }
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            logger.error("找不到对于的属性。", e);
            throw new SystemRuntimeException(e);
        }
        return result;
    }

    /**
     * 获取翻译树(ORM方式)
     * 
     * @param entity
     * @param code
     * @param parentCode
     * @param name
     * @return
     */
    public List<Code2NameTreeVO> getTreesORM(String entity, String code, String parentCode, String name) {
        return getTreesORM(entity, code, parentCode, name, null);
    }
}
