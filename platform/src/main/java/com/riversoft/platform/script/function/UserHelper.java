/*
 * $HeadURL: $
 * $Id: $
 * Copyright (c) 2013 by Riversoft System, all rights reserved.
 */
package com.riversoft.platform.script.function;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

import com.riversoft.core.db.DataCondition;
import com.riversoft.core.db.ORMService;
import com.riversoft.core.script.annotation.ScriptSupport;
import com.riversoft.platform.SessionManager;
import com.riversoft.platform.po.UsGroup;
import com.riversoft.platform.po.UsRole;
import com.riversoft.platform.po.UsUser;

/**
 * 用户模型函数库
 * 
 * @author woden
 * 
 */
@ScriptSupport("user")
public class UserHelper {

	/**
	 * 获取当前用户
	 * 
	 * @return
	 */
	public static UsUser getUser() {
		return SessionManager.getUser();
	}

	/**
	 * 获取当前组织
	 * 
	 * @return
	 */
	public static UsGroup getGroup() {
		return SessionManager.getGroup();
	}

	/**
	 * 获取父组织
	 * 
	 * @param group
	 * @return
	 */
	public static UsGroup getParentGroup(UsGroup group) {
		return (UsGroup) ORMService.getInstance().findByPk(UsGroup.class.getName(), group.getParentKey());
	}

	/**
	 * 获取所属部门
	 * 
	 * @param level
	 *            上推层级
	 * @return
	 */
	public static UsGroup getGroup(int level) {
		UsGroup current = getGroup();

		if (level < 1) {
			return current;
		}

		for (int i = 0; i < level; i++) {
			if (StringUtils.isEmpty(current.getParentKey())) {
				return current;
			}
			current = getParentGroup(current);
		}
		return current;
	}

	/**
	 * 获取当前角色
	 * 
	 * @return
	 */
	public static UsRole getRole() {
		return SessionManager.getRole();
	}

	/**
	 * 获取当前用户主键
	 * 
	 * @return
	 */
	public static String getUid() {
		return getUser().getUid();
	}

	/**
	 * 获取当前角色主键
	 * 
	 * @return
	 */
	public static String getRoleKey() {
		return getRole().getRoleKey();
	}

	/**
	 * 获取当前组织主键
	 * 
	 * @return
	 */
	public static String getGroupKey() {
		return getGroup().getGroupKey();
	}

	/**
	 * 递归获取group数组
	 * 
	 * @param set
	 * @param parent
	 */
	@SuppressWarnings("unchecked")
	private static void addSubGroup(Set<UsGroup> set, UsGroup parent) {
		set.add(parent);
		List<UsGroup> list = ORMService.getInstance().query(UsGroup.class.getName(),
				new DataCondition().setStringEqual("parentKey", parent.getGroupKey()).toEntity());
		if (list != null) {
			for (UsGroup group : list) {
				addSubGroup(set, group);
			}
		}
	}

	/**
	 * 获取组织及其所有子组织
	 * 
	 * @param groupKey
	 * @return
	 */
	public static Collection<UsGroup> listSubGroup(String groupKey) {
		Set<UsGroup> set = new HashSet<>();
		UsGroup group = (UsGroup) ORMService.getInstance().findByPk(UsGroup.class.getName(), groupKey);
		addSubGroup(set, group);
		return set;
	}

	/**
	 * 获取所有组织主键
	 * 
	 * @param groupKey
	 * @return
	 */
	public static Collection<String> listSubGroupKey(String groupKey) {
		List<String> list = new ArrayList<>();
		Collection<UsGroup> groups = listSubGroup(groupKey);
		for (UsGroup group : groups) {
			list.add(group.getGroupKey());
		}

		return list;
	}

	/**
	 * 获取组织下属所有员工ID
	 * 
	 * @param groupKey
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static Collection<String> listUidByGroup(String groupKey) {
		Collection<String> list = listSubGroupKey(groupKey);
		List<String> uids = ORMService.getInstance().queryHQL(
				"select uid from UsUserGroupRole where groupKey in (:list)", new ORMService.QueryVO("list", list));
		Set<String> result = new HashSet<>();
		if (uids != null) {
			for (String uid : uids) {
				result.add(uid);
			}
		}
		return result;
	}

	/**
	 * 通过名字模糊查询用户
	 * 
	 * @param name
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static Collection<UsUser> listUserByNameLike(String name) {
		List<UsUser> list = ORMService.getInstance().queryHQL(
				"from " + UsUser.class.getName() + " where busiName like ?", "%" + name + "%");
		return list;
	}

	/**
	 * 通过名字获取用户
	 * 
	 * @param name
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static Collection<UsUser> listUserByName(String name) {
		List<UsUser> list = ORMService.getInstance().queryHQL("from " + UsUser.class.getName() + " where busiName = ?",
				name);
		return list;
	}

	/**
	 * 获取组织下属所有员工
	 * 
	 * @param groupKey
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static Collection<UsUser> listUserByGroup(String groupKey) {
		Collection<String> list = listSubGroupKey(groupKey);
		List<String> uids = ORMService.getInstance().queryHQL(
				"select uid from UsUserGroupRole where groupKey in (:list)", new ORMService.QueryVO("list", list));
		Set<UsUser> result = new HashSet<>();
		if (uids != null) {
			for (String uid : uids) {
				result.add((UsUser) ORMService.getInstance().findByPk(UsUser.class.getName(), uid));
			}
		}
		return result;
	}

	/**
	 * 获取员工对应组织
	 * 
	 * @param uid
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static Collection<UsGroup> listGroupByUser(String uid) {
		List<String> list = ORMService.getInstance().queryHQL(
				"select groupKey from UsUserGroupRole where uid = ? order by defaultFlag desc,sort asc", uid);
		Set<UsGroup> result = new HashSet<>();
		for (String groupKey : list) {
			result.add((UsGroup) ORMService.getInstance().findByPk(UsGroup.class.getName(), groupKey));
		}
		return result;
	}

	/**
	 * 获取员工对应角色
	 * 
	 * @param uid
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static Collection<UsRole> listRoleByUser(String uid) {
		List<String> list = ORMService.getInstance().queryHQL(
				"select roleKey from UsUserGroupRole where uid = ? order by defaultFlag desc,sort asc", uid);
		Set<UsRole> result = new HashSet<>();
		for (String roleKey : list) {
			result.add((UsRole) ORMService.getInstance().findByPk(UsRole.class.getName(), roleKey));
		}
		return result;
	}

	/**
	 * 获取员工对应组织
	 * 
	 * @param uid
	 * @return
	 */
	public static UsGroup getGroupByUser(String uid) {
		Collection<UsGroup> list = listGroupByUser(uid);
		if (list.size() > 0) {
			return list.iterator().next();
		}
		return null;
	}

	/**
	 * 获取员工对应组织
	 * 
	 * @param uid
	 * @return
	 */
	public static UsRole getRoleByUser(String uid) {
		Collection<UsRole> list = listRoleByUser(uid);
		if (list.size() > 0) {
			return list.iterator().next();
		}
		return null;
	}

	/**
	 * 判断指定用户是否与当前用户同在一个组织
	 * 
	 * @param target
	 *            指定用户
	 * @param level
	 *            上推层级
	 * @return
	 */
	public static boolean checkSameGroup(String target, int level) {
		Collection<String> list = listUidByGroup(getGroup(level).getGroupKey());
		return list.contains(target);
	}

	/**
	 * 判断指定用户是否与当前用户同在一个组织
	 * 
	 * @param target
	 *            指定用户
	 * @return
	 */
	public static boolean checkSameGroup(String target) {
		return checkSameGroup(target, 0);
	}

	/**
	 * 翻译角色
	 * 
	 * @param roleKey
	 * @return
	 */
	public static UsRole findRole(String roleKey) {
		return (UsRole) ORMService.getInstance().findByPk(UsRole.class.getName(), roleKey);
	}

	/**
	 * 翻译组织
	 * 
	 * @param groupKey
	 * @return
	 */
	public static UsGroup findGroup(String groupKey) {
		return (UsGroup) ORMService.getInstance().findByPk(UsGroup.class.getName(), groupKey);
	}

	/**
	 * 翻译用户
	 * 
	 * @param uid
	 * @return
	 */
	public static UsUser findUser(String uid) {
		return (UsUser) ORMService.getInstance().findByPk(UsUser.class.getName(), uid);
	}

	/**
	 * 校验当前登录用户是否管理员
	 * 
	 * @return
	 */
	public static boolean checkAdmin() {
		return SessionManager.isAdmin();
	}
	
	/**
	 * 返回当前用户所在的语言环境
	 * 
	 * @return
	 */
	public static String lan(){
		return SessionManager.getCurrentLanguage();
	}
}
