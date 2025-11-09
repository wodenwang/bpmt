/*
 * $HeadURL: $
 * $Id: $
 * Copyright (c) 2013 by Riversoft System, all rights reserved.
 */
package com.riversoft.module.manager.pri;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.Session;

import com.riversoft.core.db.ORMService;
import com.riversoft.core.exception.ExceptionType;
import com.riversoft.core.exception.SystemRuntimeException;
import com.riversoft.platform.template.DevelopmentOperation;

/**
 * 权限组管理
 * 
 * @author Woden
 * 
 */
public class PriGroupService extends ORMService {

	@Override
	@DevelopmentOperation("删除权限组")
	public void removeByPk(String entityName, Serializable pk) {
		Map<String, Object> po = (Map<String, Object>) findByPk(entityName, pk);
		if (po != null) {
			if ((Integer) po.get("sysFlag") != 1) {
				super.removeByPk(entityName, pk);
				// 删除关联表
				super.executeHQL("delete from CmPriGroupRelate where groupId = ?", pk);
				super.executeHQL("delete from UsRolePriGroupRelate where groupId = ? and sysFlag != 1", pk);
				super.executeHQL("delete from UsRoleGroupPriRelate where groupId = ?", pk);
			} else {
				throw new SystemRuntimeException(ExceptionType.BUSINESS, "系统权限组,无法删除.");
			}
		}
	}

	@Override
	@DevelopmentOperation("保存权限组")
	public void save(Map<String, Object> po) {
		po.put("sysFlag", 0);
		super.savePO(po);
	}

	@Override
	@DevelopmentOperation("更新权限组")
	public void update(Map<String, Object> po) {
		Session session = sessionFactory.getCurrentSession();
		Map<String, Object> old = (Map<String, Object>) session.get("CmPriGroup", (String) po.get("groupId"));
		session.clear();
		po.put("sysFlag", old.get("sysFlag"));
		super.update(po);
	}

	/**
	 * 保存排序
	 * 
	 * @param array
	 */
	@DevelopmentOperation("保存排序")
	public void executeSaveSort(List<HashMap<String, Object>> array) {
		for (HashMap<String, Object> obj : array) {
			Map<String, Object> group = (Map<String, Object>) findByPk("CmPriGroup", obj.get("groupId").toString());
			if (group == null) {
				continue;
			}
			group.put("sort", (Integer) obj.get("sort"));
			group.put("parentId", (String) obj.get("parentId"));
			update(group);
		}
	}
}
