/*
 * $HeadURL: $
 * $Id: $
 * Copyright (c) 2013 by Riversoft System, all rights reserved.
 */
package com.riversoft.platform.po;

import java.io.Serializable;

/**
 * @author woden
 * 
 */
@SuppressWarnings("serial")
public class CmPriGroupRelate implements Serializable {

	private String priKey;
	private String groupId;
	private Integer checkType;
	private String checkScript;
	private String description;

	/**
	 * @return the priKey
	 */
	public String getPriKey() {
		return priKey;
	}

	/**
	 * @param priKey
	 *            the priKey to set
	 */
	public void setPriKey(String priKey) {
		this.priKey = priKey;
	}

	/**
	 * @return the groupId
	 */
	public String getGroupId() {
		return groupId;
	}

	/**
	 * @param groupId
	 *            the groupId to set
	 */
	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}

	/**
	 * @return the checkType
	 */
	public Integer getCheckType() {
		return checkType;
	}

	/**
	 * @param checkType
	 *            the checkType to set
	 */
	public void setCheckType(Integer checkType) {
		this.checkType = checkType;
	}

	/**
	 * @return the checkScript
	 */
	public String getCheckScript() {
		return checkScript;
	}

	/**
	 * @param checkScript
	 *            the checkScript to set
	 */
	public void setCheckScript(String checkScript) {
		this.checkScript = checkScript;
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

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}

		if (obj instanceof CmPriGroupRelate) {
			return this.groupId.equals(((CmPriGroupRelate) obj).groupId) && this.priKey.equals(((CmPriGroupRelate) obj).priKey);
		}
		return super.equals(obj);
	}

	@Override
	public int hashCode() {
		return super.hashCode();
	}
}
