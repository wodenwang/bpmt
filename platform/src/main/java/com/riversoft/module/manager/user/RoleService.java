/*
 * $HeadURL: $
 * $Id: $
 * Copyright (c) 2013 by Riversoft System, all rights reserved.
 */
package com.riversoft.module.manager.user;

import java.util.HashMap;
import java.util.List;

import org.hibernate.Session;

import com.riversoft.core.db.DataPO;
import com.riversoft.core.db.ORMService;
import com.riversoft.platform.po.UsRole;

/**
 * @author Woden
 * 
 */
public class RoleService extends ORMService {
	@Override
	public void savePO(Object po) {
		UsRole role = (UsRole) po;
		role.setSysFlag(0);
		super.savePO(role);
	}

	@Override
	public void updatePO(Object po) {
		Session session = sessionFactory.getCurrentSession();
		UsRole role = (UsRole) po;
		UsRole old = (UsRole) session.get(UsRole.class, role.getRoleKey());
		session.clear();
		role.setSysFlag(old.getSysFlag());
		super.updatePO(role);
	}

	/**
	 * 保存排序
	 * 
	 * @param array
	 */
	public void executeSaveSort(List<HashMap<String, Object>> array) {
		for (HashMap<String, Object> obj : array) {
			UsRole role = (UsRole) findByPk(UsRole.class.getName(), obj.get("roleKey").toString());
			if (role == null) {
				continue;
			}
			role.setSort(Integer.valueOf(obj.get("sort").toString()));
			super.updatePO(role);
		}
	}

	/**
	 * 删除角色
	 * 
	 * @param roleKey
	 */
	public void executeRemoveRole(String roleKey) {
		// 删除原有组织的所有角色(关系)
		super.executeHQL("delete from UsGroupRole where roleKey = ? and sysFlag = 0", roleKey);
		// 删除原有组织的所有用户(关系)
		super.executeHQL("delete from UsUserGroupRole where roleKey = ? and sysFlag = 0", roleKey);

		// 删除权限组关联
		super.executeHQL("delete from UsRoleGroupPriRelate where roleKey = ?", roleKey);
		super.executeHQL("delete from UsRolePriGroupRelate where roleKey = ? and sysFlag = 0", roleKey);

		UsRole role = (UsRole) super.findByPk(UsRole.class.getName(), roleKey);
		if (role != null && role.getSysFlag() != 1) {
			super.removePO(role);
		}
	}

	/**
	 * 更新角色对应权限组
	 * 
	 * @param roleKey
	 * @param groupIds
	 */
	public void executeUpdatePriGroup(String roleKey, String[] groupIds) {
		super.executeHQL("delete from UsRolePriGroupRelate where roleKey = ? and sysFlag = 0", roleKey);
		if (groupIds != null) {
			for (String groupId : groupIds) {

				HashMap<String, Object> pk = new HashMap<>();
				pk.put("roleKey", roleKey);
				pk.put("groupId", groupId);
				if (super.findByPk("UsRolePriGroupRelate", pk) != null) {
					continue;
				}

				DataPO po = new DataPO("UsRolePriGroupRelate");
				po.set("roleKey", roleKey);
				po.set("groupId", groupId);
				po.set("sysFlag", 0);
				super.saveOrUpdate(po.toEntity());
			}
		}
	}

}
