/*
 * $HeadURL: $
 * $Id: $
 * Copyright (c) 2013 by Riversoft System, all rights reserved.
 */
package com.riversoft.module.manager.user;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.riversoft.core.context.RequestContext;
import com.riversoft.core.db.DataPO;
import com.riversoft.core.db.ORMService;
import com.riversoft.platform.po.UsUser;

/**
 * 用户表增删改查
 * 
 * @author Woden
 * 
 */
public class UserService extends ORMService {

	/**
	 * 更新用户,组织,角色关系
	 * 
	 * @param uid
	 * @param array
	 */
	@SuppressWarnings("unchecked")
	public void executeUpdateRelate(String uid, List<HashMap<String, Object>> array) {
		StringBuffer deleHql = new StringBuffer("delete from UsUserGroupRole where uid = ?");
		List<Map<String, Object>> updateList = new ArrayList<>();

		if (array.size() > 0) {
			for (HashMap<String, Object> json : array) {
				deleHql.append(" and not (roleKey = '" + json.get("roleKey") + "' and groupKey = '" + json.get("groupKey") + "')");
				Map<String, Object> entity = (Map<String, Object>) super.findHQL("from UsUserGroupRole where groupKey = ? and roleKey = ? and uid = ?", json.get("groupKey"), json.get("roleKey"), uid);
				sessionFactory.getCurrentSession().clear();

				DataPO po = new DataPO("UsUserGroupRole");
				po.set("uid", uid);
				po.set("roleKey", json.get("roleKey"));
				po.set("groupKey", json.get("groupKey"));
				po.set("defaultDomain", json.get("defaultDomain"));
				po.set("roleKey", json.get("roleKey"));
				po.set("defaultFlag", json.get("defaultFlag"));
				if (entity != null) {
					po.set("sysFlag", entity.get("sysFlag"));
				} else {
					po.set("sysFlag", 0);
				}
				updateList.add(po.toEntity());
			}
		}

		// 删除
		super.executeHQL(deleHql.toString(), uid);

		for (Map<String, Object> entity : updateList) {
			super.saveOrUpdate(entity);
		}
	}

	/**
	 * 保存排序
	 * 
	 * @param array
	 */
	@SuppressWarnings("rawtypes")
	public void executeSaveSort(List<HashMap<String, Object>> array) {
		for (HashMap<String, Object> obj : array) {
			UsUser user = (UsUser) findByPk(UsUser.class.getName(), obj.get("uid").toString());
			if (user == null) {
				continue;
			}
			user.setSort((Integer) obj.get("sort"));
			super.updatePO(user);
		}

	}

	/**
	 * 删除用户
	 * 
	 * @param uid
	 */
	public void executeRemoveUser(String uid) {
		super.executeHQL("delete from UsUserGroupRole where uid = ? and sysFlag = 0", uid);
		UsUser user = (UsUser) super.findByPk(UsUser.class.getName(), uid);
		if (user != null) {
			if (user.getSysFlag() != 1) {
				removePO(user);
			}
		}
	}

	/**
	 * 更新用户的组织角色分配方案
	 */
	public void executeUpdateGroupRoleRelate() {
		RequestContext request = RequestContext.getCurrent();
		String uid = request.getString("uid");
		List<HashMap<String, Object>> relates = request.getJsons("relate");

		super.executeHQL("delete from UsUserGroupRole where uid = ? and sysFlag = 0", uid);
		Iterator<HashMap<String, Object>> it = relates.iterator();
		int i = 0;
		while (it.hasNext()) {
			HashMap<String, Object> json = it.next();
			String groupKey = json.get("groupKey").toString();
			String roleKey = json.get("roleKey").toString();
			HashMap<String, Object> pk = new HashMap<>();
			pk.put("uid", uid);
			pk.put("groupKey", groupKey);
			pk.put("roleKey", roleKey);

			Map<String, Object> po = (Map<String, Object>) super.findByPk("UsUserGroupRole", pk);
			if (po != null) {
				po.put("sort", i++);
				super.update(po);
				continue;
			}

			DataPO dataPO = new DataPO("UsUserGroupRole");
			dataPO.set("uid", uid);
			dataPO.set("groupKey", groupKey);
			dataPO.set("roleKey", roleKey);
			dataPO.set("sort", i++);
			dataPO.set("sysFlag", 0);
			dataPO.set("defaultFlag", 0);
			super.save(dataPO.toEntity());
		}

		// 处理默认关系
		String defautKey = request.getString("default");
		if (StringUtils.isNotEmpty(defautKey)) {
			String groupKey = defautKey.substring(0, defautKey.indexOf(";"));
			String roleKey = defautKey.substring(defautKey.indexOf(";") + 1, defautKey.length());
			super.executeHQL("update UsUserGroupRole set defaultFlag = ? where uid = ? and groupKey = ? and roleKey = ?", 1, uid, groupKey, roleKey);
		} else {// 否则默认第一个
			super.executeHQL("update UsUserGroupRole set defaultFlag = ? where uid = ? and sort = 1", 1, uid);
		}
	}

	/**
	 * 保存用户
	 * 
	 * @param po
	 * @param tags
	 */
	public void executeSaveUser(UsUser po, String[] tags) {
		super.savePO(po);

		super.executeHQL("delete from UsUserTag where uid = ?", po.getUid());
		if (tags != null) {
			for (String tagKey : tags) {
				DataPO rel = new DataPO("UsUserTag");
				rel.set("tagKey", tagKey);
				rel.set("uid", po.getUid());
				super.save(rel.toEntity());
			}
		}
	}

	/**
	 * 保存用户
	 * 
	 * @param po
	 * @param tags
	 */
	public void executeUpdateUser(UsUser po, String[] tags) {
		super.updatePO(po);

		super.executeHQL("delete from UsUserTag where uid = ?", po.getUid());
		if (tags != null) {
			for (String tagKey : tags) {
				DataPO rel = new DataPO("UsUserTag");
				rel.set("tagKey", tagKey);
				rel.set("uid", po.getUid());
				super.save(rel.toEntity());
			}
		}

		// xxx
		// 调API处理WX相关,TODO
		// 根据wxStatus,wxEnable,activyFlag判断是否需要调用API启用/禁用微信
	}
}
