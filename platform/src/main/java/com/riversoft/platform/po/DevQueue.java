package com.riversoft.platform.po;

import com.riversoft.core.db.po.BaseItem;

import java.io.Serializable;

/**
 * Created by exizhai on 7/8/2015.
 */
public class DevQueue extends BaseItem {

    private String queueKey;// 主键
    private String description;// 描述
    private String createUid;// 创建人
    private String tableName;
    private String logTableName;// 日志表
    private Integer execType;
    private String execScript;

    public String getQueueKey() {
        return queueKey;
    }

    public void setQueueKey(String queueKey) {
        this.queueKey = queueKey;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCreateUid() {
        return createUid;
    }

    public void setCreateUid(String createUid) {
        this.createUid = createUid;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getLogTableName() {
        return logTableName;
    }

    public void setLogTableName(String logTableName) {
        this.logTableName = logTableName;
    }

    public Integer getExecType() {
        return execType;
    }

    public void setExecType(Integer execType) {
        this.execType = execType;
    }

    public String getExecScript() {
        return execScript;
    }

    public void setExecScript(String execScript) {
        this.execScript = execScript;
    }
}
