/*
 * $HeadURL: $
 * $Id: $
 * Copyright (c) 2013 by Riversoft System, all rights reserved.
 */
package com.riversoft.module.manager.user;

import java.io.Serializable;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.hibernate.Session;

import com.riversoft.core.context.RequestContext;
import com.riversoft.core.db.DataPO;
import com.riversoft.core.db.ORMService;
import com.riversoft.core.exception.ExceptionType;
import com.riversoft.core.exception.SystemRuntimeException;
import com.riversoft.platform.po.UsGroup;

/**
 * 组织服务类
 * 
 * @author Woden
 * 
 */
public class GroupService extends ORMService {

	@SuppressWarnings("unchecked")
	@Override
	public void updatePO(Object po) {
		Session session = sessionFactory.getCurrentSession();
		UsGroup group = (UsGroup) po;
		UsGroup old = (UsGroup) session.get(UsGroup.class, group.getGroupKey());
		session.clear();
		group.setSysFlag(old.getSysFlag());
		super.updatePO(group);

		// 获取角色key
		String[] roleKeys = (String[]) RequestContext.getCurrent().get("roles");
		List<String> roleKeyList = roleKeys != null && roleKeys.length > 0 ? Arrays.asList(roleKeys) : Arrays
				.asList("~null~");

		// 删除原有组织的对应角色
		super.executeHQL("delete from UsGroupRole where groupKey = ? and roleKey not in (:list) and sysFlag = 0",
				group.getGroupKey(), new ORMService.QueryVO("list", roleKeyList));

		// 删除原有组织,角色对应的关系
		super.executeHQL("delete from UsUserGroupRole where groupKey = ? and roleKey not in (:list) and sysFlag = 0",
				group.getGroupKey(), new ORMService.QueryVO("list", roleKeyList));

		// 移除原有权限组关联
		super.executeHQL("delete from UsRoleGroupPriRelate where groupKey = ? and roleKey not in (:list)",
				group.getGroupKey(), new ORMService.QueryVO("list", roleKeyList));

		// 添加角色
		if (roleKeys != null) {
			for (String roleKey : roleKeys) {
				Map<String, Object> entity = (Map<String, Object>) super.findHQL(
						"from UsGroupRole where groupKey = ? and roleKey = ?", group.getGroupKey(), roleKey);
				session.clear();
				DataPO dataPO = new DataPO("UsGroupRole");
				dataPO.set("groupKey", group.getGroupKey());
				dataPO.set("roleKey", roleKey);
				if (entity != null) {
					dataPO.set("sysFlag", entity.get("sysFlag"));
				} else {
					dataPO.set("sysFlag", 0);
				}
				super.saveOrUpdate(dataPO.toEntity());
			}
		}
	}

	@Override
	public void savePO(Object po) {
		UsGroup group = (UsGroup) po;
		group.setSysFlag(0);
		super.savePO(group);

		// 获取角色key
		String[] roleKeys = (String[]) RequestContext.getCurrent().get("roles");
		// 添加角色
		if (roleKeys != null) {
			for (String roleKey : roleKeys) {
				DataPO entity = new DataPO("UsGroupRole");
				entity.set("groupKey", group.getGroupKey());
				entity.set("roleKey", roleKey);
				entity.set("sysFlag", 0);
				super.save(entity.toEntity());
			}
		}
	}

	/**
	 * 分配用户
	 */
	@SuppressWarnings("unchecked")
	public void executeUserSetting() {
		RequestContext request = RequestContext.getCurrent();
		String roleKey = request.getString("roleKey");
		String groupKey = request.getString("groupKey");
		String[] uids = request.getStrings("uid");

		super.executeHQL("delete from UsUserGroupRole where groupKey = ? and roleKey = ? and sysFlag = 0", groupKey,
				roleKey);
		if (uids != null) {
			int i = 0;
			for (String uid : uids) {
				Map<String, Object> obj;
				if ((obj = (Map<String, Object>) super.findHQL(
						"from UsUserGroupRole where groupKey = ? and roleKey = ? and uid = ?", groupKey, roleKey, uid)) != null) {
					obj.put("sort", i++);
					super.update(obj);
					continue;
				}

				sessionFactory.getCurrentSession().clear();
				DataPO po = new DataPO("UsUserGroupRole");
				po.set("groupKey", groupKey);
				po.set("roleKey", roleKey);
				po.set("uid", uid);
				po.set("sort", i++);
				po.set("sysFlag", 0);
				po.set("defaultFlag", 0);
				super.save(po.toEntity());
			}
		}

		String[] defaults = request.getStrings("default");
		if (defaults != null) {
			for (String uid : defaults) {
				super.executeHQL("update UsUserGroupRole set defaultFlag = 0 where uid = ?", uid);
				super.executeHQL(
						"update UsUserGroupRole set defaultFlag = 1 where uid = ? and groupKey = ? and roleKey = ?",
						uid, groupKey, roleKey);
			}
		}
	}

	/**
	 * 删除组织
	 * 
	 * @param groupKey
	 */
	public void executeRemoveGroup(String groupKey) {
		// 将所有子组织移到根路径
		super.executeHQL("update " + UsGroup.class.getName() + " set parentKey = null where parentKey = ?", groupKey);
		// 删除原有组织的所有角色(关系)
		super.executeHQL("delete from UsGroupRole where groupKey = ? and sysFlag = 0", groupKey);
		// 删除原有组织的所有用户(关系)
		super.executeHQL("delete from UsUserGroupRole where groupKey = ? and sysFlag = 0", groupKey);
		// 移除权限组关联
		super.executeHQL("delete from UsRoleGroupPriRelate where groupKey = ?", groupKey);

		super.removeByPk(UsGroup.class.getName(), groupKey);
	}

	/**
	 * 保存组织排序
	 * 
	 * @param array
	 */
	@SuppressWarnings("rawtypes")
	public void executeSaveSort(List<HashMap<String, Object>> array) {
		Iterator it = array.iterator();
		while (it.hasNext()) {
			HashMap<String, Object> obj = (HashMap<String, Object>) it.next();
			UsGroup group = (UsGroup) findByPk(UsGroup.class.getName(), obj.get("groupKey").toString());
			if (group == null) {
				continue;
			}
			group.setSort((Integer) obj.get("sort"));
			group.setParentKey((String) obj.get("parentKey"));
			super.updatePO(group);
		}
	}

	/**
	 * 删除组织下角色
	 * 
	 * @param groupKey
	 * @param roleKey
	 */
	@SuppressWarnings("unchecked")
	public void executeRemoveRoleRelate(String groupKey, String roleKey) {
		Map<String, Object> pk = new HashMap<>();
		pk.put("groupKey", groupKey);
		pk.put("roleKey", roleKey);

		Map<String, Object> po = (Map<String, Object>) super.findByPk("UsGroupRole", (Serializable) pk);
		if (po == null) {
			throw new SystemRuntimeException(ExceptionType.BUSINESS, "角色已删除.");
		}

		if (((int) po.get("sysFlag")) == 1) {
			throw new SystemRuntimeException(ExceptionType.BUSINESS, "角色无法删除.");
		}

		super.remove(po);

		// 移除用户关系
		super.executeHQL("delete from UsUserGroupRole where groupKey = ? and roleKey = ? and sysFlag = ?", groupKey,
				roleKey, 0);

		// 移除权限组关联
		super.executeHQL("delete from UsRoleGroupPriRelate where groupKey = ? and roleKey = ?", groupKey, roleKey);
	}

	/**
	 * 更新组织-角色-权限组关系
	 * 
	 * @param groupKey
	 * @param roleKey
	 * @param groupIds
	 */
	public void executeUpdatePriGroup(String groupKey, String roleKey, String[] groupIds) {
		super.executeHQL("delete from UsRoleGroupPriRelate where groupKey = ? and roleKey = ?", groupKey, roleKey);
		if (groupIds != null) {
			for (String groupId : groupIds) {
				DataPO po = new DataPO("UsRoleGroupPriRelate");
				po.set("groupKey", groupKey);
				po.set("roleKey", roleKey);
				po.set("groupId", groupId);
				super.saveOrUpdate(po.toEntity());
			}
		}
	}
}
