/*
 * $HeadURL: $
 * $Id: $
 * Copyright (c) 2013 by Riversoft System, all rights reserved.
 */
package com.riversoft.platform.po;

import java.sql.Types;
import java.util.Date;
import java.util.Set;

import com.riversoft.core.db.dialect.DatabaseMeta;
import org.apache.commons.lang3.StringUtils;
import org.jumpmind.db.model.Table;

/**
 * @author Woden
 * 
 */
public class TbTable extends Table {

    /** */
    private static final long serialVersionUID = 1L;
    /**
     * 关联字段
     */
    private Set<TbColumn> tbColumns;
    private Set<TbIndex> tbIndexes;

    private Date createDate;
    private Date updateDate;
    private String createUid;// 创建人

    private Integer lockFlag;// 是否锁定(锁定则无法编辑)
    private Integer cacheFlag;// 是否使用缓存

    /**
     * @return the createUid
     */
    public String getCreateUid() {
        return createUid;
    }

    /**
     * @return the lockFlag
     */
    public Integer getLockFlag() {
        return lockFlag;
    }

    /**
     * @param lockFlag the lockFlag to set
     */
    public void setLockFlag(Integer lockFlag) {
        this.lockFlag = lockFlag;
    }

    /**
     * @return the cacheFlag
     */
    public Integer getCacheFlag() {
        return cacheFlag;
    }

    /**
     * @param cacheFlag the cacheFlag to set
     */
    public void setCacheFlag(Integer cacheFlag) {
        this.cacheFlag = cacheFlag;
    }

    /**
     * @param createUid the createUid to set
     */
    public void setCreateUid(String createUid) {
        this.createUid = createUid;
    }

    /**
     * @return the tbColumns
     */
    public Set<TbColumn> getTbColumns() {
        return tbColumns;
    }

    /**
     * @param tbColumns the tbColumns to set
     */
    public void setTbColumns(Set<TbColumn> tbColumns) {
        this.tbColumns = tbColumns;
    }

    public Set<TbIndex> getTbIndexes() {
        return tbIndexes;
    }

    public void setTbIndexes(Set<TbIndex> tbIndexes) {
        this.tbIndexes = tbIndexes;
    }

    /**
     * @return the createDate
     */
    public Date getCreateDate() {
        return createDate;
    }

    /**
     * @param createDate the createDate to set
     */
    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    /**
     * @return the updateDate
     */
    public Date getUpdateDate() {
        return updateDate;
    }

    /**
     * @param updateDate the updateDate to set
     */
    public void setUpdateDate(Date updateDate) {
        this.updateDate = updateDate;
    }

    /**
     * 创建ddl table
     * 
     * @return
     */
    public Table build() {
        removeAllColumns();
        removeAllIndices();

        try {
            for (TbColumn column : tbColumns) {
                if (DatabaseMeta.isOracle() && isNumber(column.getMappedTypeCode())) {
                    TbColumn oracleColumn = (TbColumn)column.clone();
                    oracleColumn.setTypeCode(Types.NUMERIC);
                    addColumn(oracleColumn);
                } else {
                    addColumn(column);
                }
            }

            if(tbIndexes != null) {
                for (TbIndex index : tbIndexes) {
                    addIndex(index.build());
                }
            }

            return (Table) this.clone();
        } catch (CloneNotSupportedException e) {
            return this;
        }
    }

    private boolean isNumber(int mappedTypeCode) {
        return mappedTypeCode == Types.INTEGER || mappedTypeCode == Types.BIGINT;
    }

    /**
     * 当前表是否有自动ID
     * 
     * @return
     */
    public String getAutoKey() {
        for (TbColumn column : tbColumns) {
            if (column.isPrimaryKey() && column.isAutoIncrement()) {
                return column.getName();
            }
        }
        return null;
    }

    /**
     * 创建insert语句
     * 
     * @return
     */
    public String getInsertSql() {
        StringBuilder builder = new StringBuilder();
        builder.append("insert into ");
        builder.append(getName());
        builder.append(" (");
        {
            StringBuffer buff = new StringBuffer();
            for (TbColumn column : tbColumns) {
                if (column.isPrimaryKey() && column.isAutoIncrement()) {// 主键且自增不需要生成
                    continue;
                }
                buff.append(",").append(column.getName());
            }
            builder.append(buff.substring(1));// 截取第一个逗号之后的值
        }
        builder.append(") values (");
        {
            StringBuffer buff = new StringBuffer();
            for (TbColumn column : tbColumns) {
                if (column.isPrimaryKey() && column.isAutoIncrement()) {// 主键且自增不需要生成
                    continue;
                }
                buff.append(",:").append(column.getName());
            }
            builder.append(buff.substring(1));// 截取第一个逗号之后的值
        }
        builder.append(")");
        return builder.toString();
    }

    /**
     * 创建update语句
     * 
     * @return
     */
    public String getUpdateSql() {
        StringBuilder builder = new StringBuilder();
        builder.append("update ");
        builder.append(getName());
        builder.append(" set ");
        {
            StringBuffer buff = new StringBuffer();
            for (TbColumn column : tbColumns) {
                if (column.isPrimaryKey()) {// 主键不需要修改
                    continue;
                }
                buff.append(", ").append(column.getName()).append(" = :").append(column.getName()).append(" ");
            }
            builder.append(buff.substring(1));// 截取第一个逗号之后的值
        }
        builder.append(" where ");
        {
            StringBuffer buff = new StringBuffer();
            for (TbColumn column : tbColumns) {
                if (!column.isPrimaryKey()) {// 非主键跳过
                    continue;
                }
                buff.append(" and ").append(column.getName()).append(" = :").append(column.getName());
            }
            builder.append(buff.substring(5));// 截取第一个and之后的值
        }

        return builder.toString();
    }

    /**
     * 创建delete语句
     * 
     * @return
     */
    public String getDeleteSql() {
        StringBuilder builder = new StringBuilder();
        builder.append("delete from ");
        builder.append(getName());
        builder.append(" where ");
        {
            StringBuffer buff = new StringBuffer();
            for (TbColumn column : tbColumns) {
                if (!column.isPrimaryKey()) {// 非主键跳过
                    continue;
                }
                buff.append(" and ").append(column.getName()).append(" = :").append(column.getName());
            }
            builder.append(buff.substring(5));// 截取第一个and之后的值
        }

        return builder.toString();
    }

    /**
     * 查询所有值的语句
     * 
     * @param pixel sql别名
     * @return
     */
    public String getSelectAllSql(String pixel) {
        StringBuilder builder = new StringBuilder();
        builder.append("select ");
        {
            StringBuffer buff = new StringBuffer();
            for (TbColumn column : tbColumns) {
                buff.append(",");
                if (StringUtils.isNotEmpty(pixel)) {
                    buff.append(pixel).append(".");
                }
                buff.append(column.getName()).append(" as ").append(column.getName());
            }
            builder.append(buff.substring(1));// 截取第一个逗号之后的值
        }
        builder.append(" from ");
        builder.append(getName());
        if (StringUtils.isNotEmpty(pixel)) {
            builder.append(" ").append(pixel);
        }

        return builder.toString();
    }

    /**
     * 查询所有值的语句
     * 
     * @return
     */
    public String getSelectAllSql() {
        return getSelectAllSql(null);
    }

    /**
     * 创建findByPk语句
     * 
     * @return
     */
    public String getFindByPkSql() {
        return getFindByPkSql(null);
    }

    /**
     * 创建findByPk语句
     * 
     * @param pixel
     * @return
     */
    public String getFindByPkSql(String pixel) {

        StringBuilder builder = new StringBuilder();
        builder.append("select ");
        if (StringUtils.isNotEmpty(pixel)) {
            builder.append(pixel).append(".");
        }
        builder.append("* from ");
        builder.append(getName());
        builder.append(" where ");
        {
            StringBuffer buff = new StringBuffer();
            for (TbColumn column : tbColumns) {
                if (!column.isPrimaryKey()) {// 非主键跳过
                    continue;
                }
                buff.append(" and ");
                if (StringUtils.isNotEmpty(pixel)) {
                    buff.append(pixel).append(".");
                }
                buff.append(column.getName()).append(" = :").append(column.getName());
            }
            builder.append(buff.substring(5));// 截取第一个and之后的值
        }

        return builder.toString();
    }
}
