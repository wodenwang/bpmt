/*
 * $HeadURL: $
 * $Id: $
 * Copyright (c) 2013 by Riversoft System, all rights reserved.
 */
package com.riversoft.platform.service;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.sql.DataSource;

import org.apache.commons.beanutils.BeanUtils;
import org.jumpmind.db.model.Column;
import org.jumpmind.db.model.Table;
import org.jumpmind.db.sql.SqlException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.riversoft.core.BeanFactory;
import com.riversoft.core.db.ORMService;
import com.riversoft.core.exception.ExceptionType;
import com.riversoft.core.exception.SystemRuntimeException;
import com.riversoft.dbtool.util.DatabaseManager;
import com.riversoft.platform.SessionManager;
import com.riversoft.platform.po.TbColumn;
import com.riversoft.platform.po.TbIndex;
import com.riversoft.platform.po.TbIndexedColumn;
import com.riversoft.platform.po.TbTable;
import com.riversoft.platform.template.DevelopmentOperation;

/**
 * @author Woden
 * 
 */
public class TableService extends ORMService {
	/**
	 * Logger for this class
	 */
	private static final Logger logger = LoggerFactory.getLogger(TableService.class);
	private static final String DATASOURCE_NAME = "dataSource";
	private static final String[] SYS_TABLE_NAME_PIXEL = new String[] { "CM_", "DEV_", "VW_", "WDG_", "ACT_GE_",
			"ACT_RE_", "US_", "WF_", "TB_", "TPL_", "WX_" };

	private DatabaseManager databaseManager;

	public TableService() {
		this.databaseManager = new DatabaseManager((DataSource) BeanFactory.getInstance().getBean(DATASOURCE_NAME));
	}

	/**
	 * 校验表名是否合法
	 * 
	 * @param name
	 * @return
	 */
	private void checkTableNameValidated(String name) {
		// 系统级别表不允许被管理
		for (String sysName : SYS_TABLE_NAME_PIXEL) {
			if (name.toUpperCase().startsWith(sysName)) {
				throw new SystemRuntimeException(ExceptionType.BUSINESS, "[" + sysName + "]开头的表是系统表,无法获取信息.");
			}
		}
	}

	/**
	 * 获取可以复制的系统表名
	 * 
	 * @return
	 */
	public Collection<String> getSyncableSystemTables() {
		Table[] all = databaseManager.readDatabase().getTables();
		logger.debug("当前数据库共有表:" + all.length);
		List<String> sysPixels = new ArrayList<>();
		sysPixels.addAll(Arrays.asList(SYS_TABLE_NAME_PIXEL));

		// 过滤掉用户模型相关的数据表
		sysPixels.remove("US_");
		// 过滤掉快照开发相关的数据表
		sysPixels.remove("TPL_");

		sysPixels.remove("WX_MP");
		sysPixels.remove("WX_AGENT");
		sysPixels.remove("WX_URL");

		Set<String> tables = new HashSet<>();
		outerLoop: for (Table table : all) {
			String tableName = table.getName().toUpperCase();
			innerLoop: for (String pixel : sysPixels) {
				if (tableName.startsWith(pixel)) {
					// 过滤微信
					if (tableName.startsWith("WX_MP") || tableName.startsWith("WX_AGENT") || tableName.startsWith("WX_URL")) {
						continue outerLoop;
					}

					tables.add(tableName);
					break innerLoop;
				}
			}
		}

		return tables;
	}

	/**
	 * 校验表是否存在
	 * 
	 * @param name
	 * @return
	 */
	public boolean checkTableExists(String name) {
		Table table = databaseManager.findTable(name);
		return table != null;
	}

	/**
	 * 关联游离表
	 * 
	 * @param name
	 * @param description
	 */
	@DevelopmentOperation("关联游离表")
	public void executeLinkTable(String name, String description) {
		// 合法性校验
		checkTableNameValidated(name);

		Table table = databaseManager.findTable(name);

		// 验证表名
		if (table == null) {
			throw new SystemRuntimeException(ExceptionType.BUSINESS, "表[" + name + "]不存在.请确认输入的表名是否正确.");
		}

		// 验证关联信息是否存在
		if (findByPk(TbTable.class.getName(), name) != null) {
			throw new SystemRuntimeException(ExceptionType.BUSINESS, "表[" + name + "]已被系统关联为动态表,无需重复关联.");
		}

		TbTable tbTable = new TbTable();
		try {
			BeanUtils.copyProperties(tbTable, table);
		} catch (IllegalAccessException | InvocationTargetException e) {
			throw new SystemRuntimeException(ExceptionType.BUSINESS, "表[" + name + "]功能异常,无法纳入管理.", e);
		}
		Set<TbColumn> tbColumns = new HashSet<>();
		Integer sort = 0;
		for (Column column : table.getColumns()) {
			TbColumn tbColumn = new TbColumn();
			try {
				BeanUtils.copyProperties(tbColumn, column);
			} catch (IllegalAccessException | InvocationTargetException e) {
				throw new SystemRuntimeException(ExceptionType.BUSINESS, "表[" + name + "]功能异常,无法纳入管理.", e);
			}
			tbColumn.setTableName(name);
			tbColumn.setTotalSize(Integer.valueOf(column.getSizeAsInt()));
			tbColumn.setSort(sort++);
			tbColumns.add(tbColumn);
		}
		tbTable.setTbColumns(tbColumns);
		tbTable.setDescription(description);
		tbTable.setCreateUid(SessionManager.getUser().getUid());// 设置建表人
		tbTable.setLockFlag(0);
		tbTable.setCacheFlag(0);
		super.savePO(tbTable);
	}

	/**
	 * 删除表
	 * 
	 * @param name
	 */
	@DevelopmentOperation("删除表")
	public void executeRemoveTable(String name) {
		// 合法性校验
		checkTableNameValidated(name);

		TbTable tbTable = (TbTable) super.findByPk(TbTable.class.getName(), name);
		Table table = tbTable.build();
		super.removePO(tbTable);

		try {
			if (databaseManager.findTable(name) != null) {
				databaseManager.dropTable(table);
			}
		} catch (SqlException e) {
			logger.error("删除表失败", e);
			throw new SystemRuntimeException(ExceptionType.DB_DDL, e.getMessage(), e);
		}
	}

	/**
	 * 更新表结构
	 * 
	 * @param table
	 */
	@DevelopmentOperation("更新表结构")
	public void executeUpdateTable(TbTable table, Boolean isSafe) {
		// 合法性校验
		checkTableNameValidated(table.getName());

		TbTable old = (TbTable) findByPk(TbTable.class.getName(), table.getName());
		sessionFactory.getCurrentSession().clear();

		List<String> columnNames = new ArrayList<>();
		for (TbColumn column : table.getTbColumns()) {
			logger.debug("字段展示:" + column.getName());
			columnNames.add(column.getName());
		}
		super.executeHQL("delete from " + TbColumn.class.getName() + " where tableName = ? and name not in (:list)",
				table.getName(), new ORMService.QueryVO("list", columnNames));
		table.setCreateUid(old.getCreateUid());
		table.setCreateDate(old.getCreateDate());
		table.setLockFlag(old.getLockFlag());
		super.saveOrUpdatePO(table);

		try {
			if (isSafe) {
				logger.debug("粗暴模式该表.");
				databaseManager.alterTable(table.build());
			} else {
				logger.debug("使用安全模式改表.");
				databaseManager.alterTableSafe(table.build());
			}
		} catch (SqlException e) {
			logger.error("修改表失败", e);
			throw new SystemRuntimeException(ExceptionType.DB_DDL, e.getMessage(), e);
		}
	}

	/**
	 * 更新索引
	 *
	 * @param table
	 */
	@DevelopmentOperation("更新索引")
	public void executeUpdateTableIndex(TbTable table, Boolean isSafe) {
		TbTable old = (TbTable) findByPk(TbTable.class.getName(), table.getName());

		sessionFactory.getCurrentSession().clear();

		// 删除老的index
		Set<TbIndex> indexSet = old.getTbIndexes();
		if (indexSet != null && !indexSet.isEmpty()) {
			for (TbIndex index : indexSet) {
				super.executeHQL("delete from " + TbIndexedColumn.class.getName() + " where tableName = ? and indexName = ?",
						old.getName(), index.getName());
				super.executeHQL("delete from " + TbIndex.class.getName() + " where tableName = ? and name = ?",
						old.getName(), index.getName());
			}
		}

		table.setCreateUid(old.getCreateUid());
		table.setCreateDate(old.getCreateDate());
		table.setLockFlag(old.getLockFlag());
		super.saveOrUpdatePO(table);

		try {
			if (isSafe) {
				databaseManager.alterTable(table.build());
			} else {
				logger.debug("使用安全模式修改索引.");
				databaseManager.alterTableSafe(table.build());
			}
		} catch (SqlException e) {
			logger.error("修改表索引", e);
			throw new SystemRuntimeException(ExceptionType.DB_DDL, e.getMessage(), e);
		}
	}

	/**
	 * 创建表
	 * 
	 * @param table
	 */
	@DevelopmentOperation("创建表")
	public void executeCreateTable(TbTable table) {
		// 合法性校验
		checkTableNameValidated(table.getName());

		table.setCreateUid(SessionManager.getUser().getUid());// 设置建表人
		table.setLockFlag(0);
		super.savePO(table);

		try {
			databaseManager.createTable(table.build());
		} catch (SqlException e) {
			logger.error("建表失败", e);
			throw new SystemRuntimeException(ExceptionType.DB_DDL, e.getMessage(), e);
		}
	}

	/**
	 * 同步表结构
	 * 
	 * @param name
	 * @param isSafe
	 */
	@DevelopmentOperation("同步表结构")
	public void executeSyncTable(String name, Boolean isSafe) {
		// 合法性校验
		checkTableNameValidated(name);

		TbTable table = (TbTable) super.findByPk(TbTable.class.getName(), name);
		try {
			if (isSafe) {
				databaseManager.alterTable(table.build());
			} else {
				logger.debug("使用安全模式改表.");
				databaseManager.alterTableSafe(table.build());
			}
		} catch (SqlException e) {
			logger.error("同步表失败", e);
			throw new SystemRuntimeException(ExceptionType.DB_DDL, e.getMessage(), e);
		}
	}
}
