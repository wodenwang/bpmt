/*
 * $HeadURL: $
 * $Id: $
 * Copyright (c) 2013 by Riversoft System, all rights reserved.
 */
package com.riversoft.platform.po;

import java.util.Date;

import com.riversoft.core.db.po.BaseItem;

/**
 * @author Woden
 */
public class UsUser extends BaseItem {

	/** */
	private static final long serialVersionUID = 1L;
	private String uid;
	private String password;
	private String busiName;

	private Integer activeFlag;
	private Date effDate;
	private Date endDate;
	private Integer sysFlag;
	private Integer selectFlag;
	private Integer sort;
	private String allowIp;
	private String mail;
	private String mobile;
	private String msgType;
	private String receiveType;// 接收消息开关

	// ===微信相关===
	private Integer wxEnable;
	private String wxid;
	private Integer wxStatus;
	private String wxAvatar;// 头像

	// ===虚拟user
	private boolean entity = true;// 是否拥有实体.即US_USER表是否有对应数据

	private String openId;// 关联openid
	private String unionId;// 关联unionid

	private boolean open = false;// 是否来源开放平台
	private String mpKey;// 对应公众号
	private String visitorGroupKey;// 访客对应组
	private String visitorRoleKey;// 访客对应角色

	/**
	 * @return the open
	 */
	public boolean isOpen() {
		return open;
	}

	/**
	 * @param open
	 *            the open to set
	 */
	public void setOpen(boolean open) {
		this.open = open;
	}

	/**
	 * @return the entity
	 */
	public boolean isEntity() {
		return entity;
	}

	/**
	 * @param entity
	 *            the entity to set
	 */
	public void setEntity(boolean entity) {
		this.entity = entity;
	}

	/**
	 * @return the visitorGroupKey
	 */
	public String getVisitorGroupKey() {
		return visitorGroupKey;
	}

	/**
	 * @param visitorGroupKey
	 *            the visitorGroupKey to set
	 */
	public void setVisitorGroupKey(String visitorGroupKey) {
		this.visitorGroupKey = visitorGroupKey;
	}

	/**
	 * @return the visitorRoleKey
	 */
	public String getVisitorRoleKey() {
		return visitorRoleKey;
	}

	/**
	 * @param visitorRoleKey
	 *            the visitorRoleKey to set
	 */
	public void setVisitorRoleKey(String visitorRoleKey) {
		this.visitorRoleKey = visitorRoleKey;
	}

	/**
	 * @return the openId
	 */
	public String getOpenId() {
		return openId;
	}

	/**
	 * @param openId
	 *            the openId to set
	 */
	public void setOpenId(String openId) {
		this.openId = openId;
	}

	/**
	 * @return the unionId
	 */
	public String getUnionId() {
		return unionId;
	}

	/**
	 * @param unionId
	 *            the unionId to set
	 */
	public void setUnionId(String unionId) {
		this.unionId = unionId;
	}

	/**
	 * @return the mpKey
	 */
	public String getMpKey() {
		return mpKey;
	}

	/**
	 * @param mpKey
	 *            the mpKey to set
	 */
	public void setMpKey(String mpKey) {
		this.mpKey = mpKey;
	}

	/**
	 * @return the wxAvatar
	 */
	public String getWxAvatar() {
		return wxAvatar;
	}

	/**
	 * @param wxAvatar
	 *            the wxAvatar to set
	 */
	public void setWxAvatar(String wxAvatar) {
		this.wxAvatar = wxAvatar;
	}

	/**
	 * @return the receiveType
	 */
	public String getReceiveType() {
		return receiveType;
	}

	/**
	 * @param receiveType
	 *            the receiveType to set
	 */
	public void setReceiveType(String receiveType) {
		this.receiveType = receiveType;
	}

	/**
	 * @return the mail
	 */
	public String getMail() {
		return mail;
	}

	/**
	 * @param mail
	 *            the mail to set
	 */
	public void setMail(String mail) {
		this.mail = mail;
	}

	/**
	 * @return the msgType
	 */
	public String getMsgType() {
		return msgType;
	}

	/**
	 * @param msgType
	 *            the msgType to set
	 */
	public void setMsgType(String msgType) {
		this.msgType = msgType;
	}

	public Integer getSort() {
		return sort;
	}

	/**
	 * @param sort
	 *            the sort to set
	 */
	public void setSort(Integer sort) {
		this.sort = sort;
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
	 * @return the uid
	 */
	public String getUid() {
		return uid;
	}

	/**
	 * @param uid
	 *            the uid to set
	 */
	public void setUid(String uid) {
		this.uid = uid;
	}

	/**
	 * @return the password
	 */
	public String getPassword() {
		return password;
	}

	/**
	 * @param password
	 *            the password to set
	 */
	public void setPassword(String password) {
		this.password = password;
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

	/**
	 * @return the activeFlag
	 */
	public Integer getActiveFlag() {
		return activeFlag;
	}

	/**
	 * @param activeFlag
	 *            the activeFlag to set
	 */
	public void setActiveFlag(Integer activeFlag) {
		this.activeFlag = activeFlag;
	}

	/**
	 * @return the effDate
	 */
	public Date getEffDate() {
		return effDate;
	}

	/**
	 * @param effDate
	 *            the effDate to set
	 */
	public void setEffDate(Date effDate) {
		this.effDate = effDate;
	}

	/**
	 * @return the endDate
	 */
	public Date getEndDate() {
		return endDate;
	}

	/**
	 * @param endDate
	 *            the endDate to set
	 */
	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	/**
	 * @return the allowIp
	 */
	public String getAllowIp() {
		return allowIp;
	}

	/**
	 * @param allowIp
	 *            the allowIp to set
	 */
	public void setAllowIp(String allowIp) {
		this.allowIp = allowIp;
	}

	/**
	 * @return the selectFlag
	 */
	public Integer getSelectFlag() {
		return selectFlag;
	}

	/**
	 * @param selectFlag
	 *            the selectFlag to set
	 */
	public void setSelectFlag(Integer selectFlag) {
		this.selectFlag = selectFlag;
	}

	public String getWxid() {
		return wxid;
	}

	public void setWxid(String wxid) {
		this.wxid = wxid;
	}

	public Integer getWxEnable() {
		return wxEnable != null ? wxEnable : 0;
	}

	public void setWxEnable(Integer wxEnable) {
		this.wxEnable = wxEnable;
	}

	public Integer getWxStatus() {
		return wxStatus != null ? wxEnable : 0;
	}

	public void setWxStatus(Integer wxStatus) {
		this.wxStatus = wxStatus;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}
}
