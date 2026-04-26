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

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.riversoft.core.db.JdbcService;
import com.riversoft.platform.po.TbColumn;
import com.riversoft.platform.po.TbTable;

/**
 * 动态表通用service
 * 
 * @author Woden
 * 
 */
public class DynamicTableJdbcService extends JdbcService {
    /**
     * Logger for this class
     */
    private static final Logger logger = LoggerFactory.getLogger(DynamicTableJdbcService.class);

    /**
     * 唯一健
     * 
     * @param table
     * @param pk
     * @return
     */
    @SuppressWarnings("unchecked")
    public Map<String, Object> getKey(TbTable table, Serializable pk) {
        Map<String, Object> param = new HashMap<>();
        for (TbColumn column : table.getTbColumns()) {
            if (column.isPrimaryKey()) {
                if (pk instanceof Map) {
                    param.put(column.getName(), ((Map<String, Object>) pk).get(column.getName()));
                } else {
                    param.put(column.getName(), pk);
                    break;
                }
            }
        }
        return param;
    }

    /**
     * 保存对象
     * 
     * @param table
     * @param po
     */
    public void save(TbTable table, Map<String, Object> po) {
        String autoKey = table.getAutoKey();
        // 预处理PO,将table的字段都填齐
        for (TbColumn column : table.getTbColumns()) {
            if (!po.containsKey(column.getName())) {
                po.put(column.getName(), null);
            }
        }

        if (StringUtils.isNotEmpty(autoKey)) {
            logger.debug("[" + table.getName() + "]含有自增长ID,需要回写.");
            Long autoValue = saveSQL(table.getInsertSql(), po, autoKey);
            logger.debug("回写自增长ID:[" + autoKey + "=" + autoValue + "]");
            po.put(autoKey, autoValue);
        } else {
            executeSQL(table.getInsertSql(), po);
        }
    }

    /**
     * 更新对象
     * 
     * @param table
     * @param po
     */
    public void update(TbTable table, Map<String, Object> po) {
        executeSQL(table.getUpdateSql(), po);
    }

    /**
     * 删除对象
     * 
     * @param table
     * @param po
     */
    public void remove(TbTable table, Map<String, Object> po) {
        executeSQL(table.getDeleteSql(), po);
    }

    /**
     * 获取对象
     * 
     * @param table
     * @param pk
     */
    @SuppressWarnings("unchecked")
    public Map<String, Object> findByPk(TbTable table, Map<String, Object> pk) {
        return findSQL(table.getFindByPkSql(), pk);
    }

    /**
     * 查询所有<br>
     * 暂不是用缓存
     * 
     * @param table
     * @return
     */
    public List<Map<String, Object>> queryAll(TbTable table) {
        final String sql = table.getSelectAllSql();
        return querySQL(sql);
    }
}
