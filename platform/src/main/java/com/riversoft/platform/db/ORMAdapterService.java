/*
 * $HeadURL: $
 * $Id: $
 * Copyright (c) 2013 by Riversoft System, all rights reserved.
 */
package com.riversoft.platform.db;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.riversoft.core.BeanFactory;
import com.riversoft.core.db.DataPackage;
import com.riversoft.core.db.ORMService;
import com.riversoft.core.db.QueryStringBuilder;
import com.riversoft.platform.po.TbColumn;
import com.riversoft.platform.po.TbTable;

/**
 * 使用ORMService规范,实际是JDBCService的适配器
 * 
 * @author Woden
 * 
 */
public class ORMAdapterService extends ORMService {

    /**
     * 获取单例
     * 
     * @return
     */
    public static ORMAdapterService getInstance() {
        return BeanFactory.getInstance().getSingleBean(ORMAdapterService.class);
    }

    private DynamicTableJdbcService service = BeanFactory.getInstance().getBean(DynamicTableJdbcService.class);

    /**
     * 新增并返回主键
     * 
     * @param po
     * @return
     */
    public Map<String, Object> saveAndReturnPk(Map<String, Object> po) {
        String entityName = getEntityName(po);
        TbTable table = (TbTable) ORMService.getInstance().findByPk(TbTable.class.getName(), entityName);
        service.save(table, po);

        Map<String, Object> pk = new HashMap<String, Object>();
        for (TbColumn column : table.getTbColumns()) {
            if (column.isPrimaryKey()) {
                pk.put(column.getName(), po.get(column.getName()));
            }
        }
        return pk;
    }

    /**
     * 修改并返回主键
     * 
     * @param po
     * @return
     */
    public Map<String, Object> updateAndReturnPk(Map<String, Object> po) {
        String entityName = getEntityName(po);
        TbTable table = (TbTable) ORMService.getInstance().findByPk(TbTable.class.getName(), entityName);
        service.update(table, po);

        Map<String, Object> pk = new HashMap<String, Object>();
        for (TbColumn column : table.getTbColumns()) {
            if (column.isPrimaryKey()) {
                pk.put(column.getName(), po.get(column.getName()));
            }
        }
        return pk;
    }

    /**
     * 从PO中找回主键
     *
     * @param po
     * @return
     */
    public Map<String, Object> getPk(Map<String, Object> po) {
        String entityName = getEntityName(po);
        TbTable table = (TbTable) ORMService.getInstance().findByPk(TbTable.class.getName(), entityName);

        Map<String, Object> pk = new HashMap<String, Object>();
        for (TbColumn column : table.getTbColumns()) {
            if (column.isPrimaryKey()) {
                pk.put(column.getName(), po.get(column.getName()));
            }
        }
        return pk;
    }

    @Override
    public void save(Map<String, Object> po) {
        saveAndReturnPk(po);
    }

    @Override
    public void update(Map<String, Object> po) {
        updateAndReturnPk(po);
    }

    @Override
    public void removeByPk(String entityName, Serializable pk) {
        TbTable table = (TbTable) ORMService.getInstance().findByPk(TbTable.class.getName(), entityName);
        service.remove(table, service.getKey(table, pk));
    }

    @Override
    public void remove(Map<String, Object> po) {
        String entityName = getEntityName(po);
        TbTable table = (TbTable) ORMService.getInstance().findByPk(TbTable.class.getName(), entityName);
        service.remove(table, po);
    }

    @Override
    public Object findByPk(String entityName, Serializable pk) {
        TbTable table = (TbTable) ORMService.getInstance().findByPk(TbTable.class.getName(), entityName);
        Map<String, Object> param = service.getKey(table, pk);
        Map<String, Object> obj = service.findByPk(table, param);
        if (obj != null) {
            obj.put("$type$", entityName);
        }
        return obj;
    }

    /**
     * 根据自定义条件查询
     * 
     * @param entityName
     * @param queryMap
     * @return
     */
    @SuppressWarnings("rawtypes")
    public List<Map<String, Object>> query(String entityName, Map queryMap) {
        TbTable table = (TbTable) ORMService.getInstance().findByPk(TbTable.class.getName(), entityName);
        final String sql = table.getSelectAllSql() + " " + QueryStringBuilder.build(queryMap);
        return service.querySQL(sql);
    }

    /**
     * 分页查询
     * 
     * @param entityName
     * @param firstResult
     * @param maxResult
     * @param queryMap
     * @return
     */
    @SuppressWarnings("rawtypes")
    public List<Map<String, Object>> queryPage(String entityName, final int firstResult, final int maxResult,
            Map queryMap) {
        TbTable table = (TbTable) ORMService.getInstance().findByPk(TbTable.class.getName(), entityName);
        final String sql = table.getSelectAllSql() + " " + QueryStringBuilder.build(queryMap);
        return service.querySQLPage(sql, firstResult, maxResult);
    }

    /**
     * 获取全表数据
     * 
     * @param entityName
     * @return
     */
    public List<Map<String, Object>> queryAll(String entityName) {
        TbTable table = (TbTable) ORMService.getInstance().findByPk(TbTable.class.getName(), entityName);
        return service.queryAll(table);
    }

    /**
     * 获取表总数
     * 
     * @param table
     * @return
     */
    public Long getCount(String table) {
        return getCount(table, null);
    }

    /**
     * 获取表总数
     * 
     * @param table
     * @param queryMap
     * @return
     */
    @SuppressWarnings("rawtypes")
    public Long getCount(String table, Map queryMap) {
        String sql = "select 1 from " + table + " " + QueryStringBuilder.buildWhere(queryMap);
        return service.getSQLCount(sql);
    }

    /**
     * 分页查询表
     * 
     * @param table
     * @param start
     * @param limit
     * @return
     */
    public DataPackage queryPackage(String table, int start, int limit) {
        return queryPackage(table, start, limit, null);
    }

    /**
     * 分页查询表
     * 
     * @param table
     * @param start
     * @param limit
     * @param queryMap
     * @return
     */
    @SuppressWarnings("rawtypes")
    public DataPackage queryPackage(String table, int start, int limit, Map queryMap) {
        DataPackage result = new DataPackage();
        result.setStart(start);
        result.setLimit(limit);
        result.setTotalRecord(getCount(table, queryMap));
        result.setList(queryPage(table, result.getStart(), result.getLimit(), queryMap));
        return result;
    }

    /**
     * 查找
     */
    @SuppressWarnings("rawtypes")
    public Object find(String entityName, Map queryMap) {
        List<?> list = query(entityName, queryMap);
        if (list != null && list.size() > 0) {
            return list.get(0);
        } else {
            return null;
        }
    }
}
