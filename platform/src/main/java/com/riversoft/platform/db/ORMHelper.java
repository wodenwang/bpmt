/*
 * $HeadURL: $
 * $Id: $
 * Copyright (c) 2013 by Riversoft System, all rights reserved.
 */
package com.riversoft.platform.db;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import com.riversoft.core.db.ORMService;
import com.riversoft.core.script.annotation.ScriptSupport;

/**
 * orm函数库
 * 
 * @author woden
 * 
 */
@ScriptSupport("orm")
@SuppressWarnings("unchecked")
public class ORMHelper {

	/**
	 * 增
	 * 
	 * @param o
	 */

	public void save(Object o) {
		if (o instanceof Map) {
			ORMService.getInstance().save((Map<String, Object>) o);
		} else {
			ORMService.getInstance().savePO(o);
		}
	}

	/**
	 * 改
	 * 
	 * @param o
	 */
	public void update(Object o) {
		if (o instanceof Map) {
			ORMService.getInstance().update((Map<String, Object>) o);
		} else {
			ORMService.getInstance().updatePO(o);
		}
	}

	/**
	 * 删
	 * 
	 * @param o
	 */
	public void remove(Object o) {
		if (o instanceof Map) {
			ORMService.getInstance().remove((Map<String, Object>) o);
		} else {
			ORMService.getInstance().removePO(o);
		}
	}

	/**
	 * 根据主键删
	 * 
	 * @param entityName
	 * @param pk
	 */
	public void removeByPk(String entityName, Serializable pk) {
		ORMService.getInstance().removeByPk(entityName, pk);
	}

	/**
	 * 根据主键查找
	 * 
	 * @param entityName
	 * @param pk
	 * @return
	 */
	public Object findByPk(String entityName, Serializable pk) {
		return ORMService.getInstance().findByPk(entityName, pk);
	}

	/**
	 * 根据HQL返回唯一值
	 * 
	 * @param hql
	 * @param values
	 * @return
	 */
	public Object find(String hql, Object... values) {
		return ORMService.getInstance().findHQL(hql, values);
	}

	/**
	 * 根据HQL返回列表
	 * 
	 * @param hql
	 * @param values
	 * @return
	 */
	public List<?> query(String hql, Object... values) {
		return ORMService.getInstance().queryHQL(hql, values);
	}

    /**
     * 根据HQL执行
     *
     * @param hql
     * @param values
     */
    public void exec(String hql, Object... values) {
        ORMService.getInstance().executeHQL(hql, values);
    }}
