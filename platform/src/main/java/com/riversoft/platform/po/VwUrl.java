/*
 * $HeadURL: $
 * $Id: $
 * Copyright (c) 2013 by Riversoft System, all rights reserved.
 */
package com.riversoft.platform.po;

import com.riversoft.core.db.po.BaseItem;

/**
 * 视图配置表-地址索引(门面)
 * 
 * @author Woden
 * 
 */
public class VwUrl extends BaseItem {

	/** */
	private static final long serialVersionUID = 1L;
	private String viewClass;// 模块类名
	private String viewKey;// uuid生成,用于关联模块配置表

	private String createUid;
	private String description;// 备注

	private Integer lockFlag;// 是否锁定(锁定则无法编辑)

	private Integer loginType;// 登录类型,受制于viewClass

	/**
	 * @return the loginType
	 */
	public Integer getLoginType() {
		return loginType;
	}

	/**
	 * @param loginType
	 *            the loginType to set
	 */
	public void setLoginType(Integer loginType) {
		this.loginType = loginType;
	}

	/**
	 * @return the lockFlag
	 */
	public Integer getLockFlag() {
		return lockFlag;
	}

	/**
	 * @param lockFlag
	 *            the lockFlag to set
	 */
	public void setLockFlag(Integer lockFlag) {
		this.lockFlag = lockFlag;
	}

	/**
	 * @return the viewClass
	 */
	public String getViewClass() {
		return viewClass;
	}

	/**
	 * @param viewClass
	 *            the viewClass to set
	 */
	public void setViewClass(String viewClass) {
		this.viewClass = viewClass;
	}

	/**
	 * @return the viewKey
	 */
	public String getViewKey() {
		return viewKey;
	}

	/**
	 * @param viewKey
	 *            the viewKey to set
	 */
	public void setViewKey(String viewKey) {
		this.viewKey = viewKey;
	}

	/**
	 * @return the url
	 */
	public String getUrl() {
		return "/" + viewKey + ".view";
	}

	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @param description
	 *            the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * @return the createUid
	 */
	public String getCreateUid() {
		return createUid;
	}

	/**
	 * @param createUid
	 *            the createUid to set
	 */
	public void setCreateUid(String createUid) {
		this.createUid = createUid;
	}
}
