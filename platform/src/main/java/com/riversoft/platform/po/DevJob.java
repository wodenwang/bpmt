/*
 * $HeadURL: $
 * $Id: $
 * Copyright (c) 2014 by Riversoft System, all rights reserved.
 */
package com.riversoft.platform.po;

import com.riversoft.core.db.po.BaseItem;

/**
 * 调度框架任务明细
 * 
 * @author woden
 * 
 */
@SuppressWarnings("serial")
public class DevJob extends BaseItem {

    private String jobKey;// 主键
    private String description;// 描述
    private Integer activeFlag;// 是否有效
    private String createUid;// 创建人

    private String logTableName;// 日志表

    private Integer isTransaction; //是否起事务
    
    private String cronExpression;
    private Integer execType;
    private String execScript;

    /**
     * @return the jobKey
     */
    public String getJobKey() {
        return jobKey;
    }

    /**
     * @param jobKey the jobKey to set
     */
    public void setJobKey(String jobKey) {
        this.jobKey = jobKey;
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

    /**
     * @return the activeFlag
     */
    public Integer getActiveFlag() {
        return activeFlag;
    }

    /**
     * @param activeFlag the activeFlag to set
     */
    public void setActiveFlag(Integer activeFlag) {
        this.activeFlag = activeFlag;
    }

    /**
     * @return the createUid
     */
    public String getCreateUid() {
        return createUid;
    }

    /**
     * @param createUid the createUid to set
     */
    public void setCreateUid(String createUid) {
        this.createUid = createUid;
    }

    /**
     * @return the logTableName
     */
    public String getLogTableName() {
        return logTableName;
    }
    
    /**

    /**
     * @param logTableName the logTableName to set
     */
    public void setLogTableName(String logTableName) {
        this.logTableName = logTableName;
    }

    /**
     * @return the cronExpression
     */
    public String getCronExpression() {
        return cronExpression;
    }

    
    /**
     * @param cronExpression the cronExpression to set
     */
    public void setCronExpression(String cronExpression) {
        this.cronExpression = cronExpression;
    }

    /**
     * @return the execType
     */
    public Integer getExecType() {
        return execType;
    }

    /**
     * @param execType the execType to set
     */
    public void setExecType(Integer execType) {
        this.execType = execType;
    }

    /**
     * @return the execScript
     */
    public String getExecScript() {
        return execScript;
    }

    /**
     * @param execScript the execScript to set
     */
    public void setExecScript(String execScript) {
        this.execScript = execScript;
    }

	public Integer getIsTransaction() {
		return isTransaction;
	}

	public void setIsTransaction(Integer isTransaction) {
		this.isTransaction = isTransaction;
	}
}
