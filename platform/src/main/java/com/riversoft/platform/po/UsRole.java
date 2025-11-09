/*
 * $HeadURL: $
 * $Id: $
 * Copyright (c) 2013 by Riversoft System, all rights reserved.
 */
package com.riversoft.platform.po;

import com.riversoft.core.db.po.BaseItem;

/**
 * @author Woden
 * 
 */
public class UsRole extends BaseItem {

	/** */
	private static final long serialVersionUID = 1L;
	private String roleKey;
	private String busiName;
	private Integer sysFlag;
	private Integer sort;

	/**
	 * @param sort
	 *            the sort to set
	 */
	public void setSort(Integer sort) {
		this.sort = sort;
	}

	public Integer getSort() {
		return sort;
	}

	/**
	 * @return the sysFlag
	 */
	public Integer getSysFlag() {
		return sysFlag;
	}

	/**
	 * @param sysFlag
	 *            the sysFlag to set
	 */
	public void setSysFlag(Integer sysFlag) {
		this.sysFlag = sysFlag;
	}

	/**
	 * @return the roleKey
	 */
	public String getRoleKey() {
		return roleKey;
	}

	/**
	 * @param roleKey
	 *            the roleKey to set
	 */
	public void setRoleKey(String roleKey) {
		this.roleKey = roleKey;
	}

	/**
	 * @return the busiName
	 */
	public String getBusiName() {
		return busiName;
	}

	/**
	 * @param busiName
	 *            the busiName to set
	 */
	public void setBusiName(String busiName) {
		this.busiName = busiName;
	}
}
