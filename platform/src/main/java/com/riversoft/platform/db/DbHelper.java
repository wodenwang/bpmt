/*
 * $HeadURL: $
 * $Id: $
 * Copyright (c) 2013 by Riversoft System, all rights reserved.
 */
package com.riversoft.platform.db;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import com.riversoft.core.db.JdbcService;
import com.riversoft.core.script.annotation.ScriptSupport;

/**
 * 数据库操作类
 * 
 * @author woden
 * 
 */
@ScriptSupport("db")
public class DbHelper {

	/**
	 * 保存对象
	 * 
	 * @param vo
	 */
	public void save(Map<String, Object> vo) {
		ORMAdapterService service = ORMAdapterService.getInstance();
		service.save(vo);
	}

	/**
	 * 更新对象
	 * 
	 * @param vo
	 */
	public void update(Map<String, Object> vo) {
		ORMAdapterService service = ORMAdapterService.getInstance();
		service.update(vo);
	}

	/**
	 * 保存或更新
	 * 
	 * @param vo
	 */
	public void saveOrUpdate(Map<String, Object> vo) {
		ORMAdapterService service = ORMAdapterService.getInstance();
		service.saveOrUpdate(vo);
	}

	/**
	 * 删除
	 * 
	 * @param vo
	 */
	public void delete(Map<String, Object> vo) {
		ORMAdapterService service = ORMAdapterService.getInstance();
		service.remove(vo);
	}

	/**
	 * 查询唯一值
	 * 
	 * @param entityName
	 * @param pk
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public Map<String, Object> findByPk(String entityName, Serializable pk) {
		ORMAdapterService service = ORMAdapterService.getInstance();
		return (Map<String, Object>) service.findByPk(entityName, pk);
	}

	/**
	 * 查询唯一值
	 * 
	 * @param vo
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public Map<String, Object> findByPk(Map<String, Object> vo) {
		ORMAdapterService service = ORMAdapterService.getInstance();
		return (Map<String, Object>) service.findByPk((String) vo.get("$type$"), (Serializable) vo);
	}

	/**
	 * 根据sql查询唯一值
	 * 
	 * @param sql
	 * @param args
	 * @return
	 */
	public Map<String, Object> find(String sql, Object... args) {
		JdbcService service = JdbcService.getInstance();
		return service.findSQL(sql, args);
	}

	/**
	 * 根据sql查询列表
	 * 
	 * @param sql
	 * @param args
	 * @return
	 */
	public List<?> query(String sql, Object... args) {
		JdbcService service = JdbcService.getInstance();
		return service.querySQL(sql, args);
	}

	/**
	 * 执行sql
	 * 
	 * @param sql
	 * @param params
	 */
	public void exec(String sql, Map<String, Object> params) {
		JdbcService service = JdbcService.getInstance();
		service.executeSQL(sql, params);
	}

	/**
	 * 执行sql
	 * 
	 * @param sql
	 * @param args
	 */
	public void exec(String sql, Object... args) {
		JdbcService service = JdbcService.getInstance();
		service.executeSQL(sql, args);
	}

	/**
	 * 执行新增语句并返回自动递增ID
	 * 
	 * @param sql
	 * @param args
	 * @return
	 */
	public Long save(String sql, Object... args) {
		JdbcService service = JdbcService.getInstance();
		return service.saveSQL(sql, args);
	}
}
