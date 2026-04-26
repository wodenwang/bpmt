/*
 * $HeadURL: $
 * $Id: $
 * Copyright (c) 2013 by Riversoft System, all rights reserved.
 */
package com.riversoft.dbtool.util;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import javax.sql.DataSource;

import org.apache.commons.collections.CollectionUtils;
import org.jumpmind.db.alter.AddColumnChange;
import org.jumpmind.db.alter.IModelChange;
import org.jumpmind.db.alter.ModelComparator;
import org.jumpmind.db.model.Column;
import org.jumpmind.db.model.Database;
import org.jumpmind.db.model.Table;
import org.jumpmind.db.platform.IDatabasePlatform;
import org.jumpmind.db.platform.JdbcDatabasePlatformFactory;
import org.jumpmind.db.sql.DmlStatement;
import org.jumpmind.db.sql.DmlStatement.DmlType;
import org.jumpmind.db.sql.ISqlTemplate;
import org.jumpmind.db.sql.SqlException;
import org.jumpmind.db.sql.SqlTemplateSettings;
import org.jumpmind.db.util.MultiInstanceofPredicate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Borball
 * 
 */
public class DatabaseManager {

	private static Logger logger = LoggerFactory.getLogger(DatabaseManager.class);
	private DataSource dataSource;
	private IDatabasePlatform databasePlatform;

	public DatabaseManager(DataSource dataSource) {
		this.dataSource = dataSource;
		this.databasePlatform = JdbcDatabasePlatformFactory.createNewPlatformInstance(dataSource, new SqlTemplateSettings(), true);
	}

	public IDatabasePlatform getDatabasePlatform() {
		return this.databasePlatform;
	}

	public DmlStatement createDml(Table table, DmlType dmlType) {
		return databasePlatform.createDmlStatement(dmlType, table, null);
	}

	public DataSource getDataSource() {
		return dataSource;
	}

	public ISqlTemplate getJdbcSqlTemplate() {
		return databasePlatform.getSqlTemplate();
	}

	/**
	 * Read database information, need to read it again if the database is
	 * changed.
	 * 
	 * @return
	 */
	public Database readDatabase() {
		return databasePlatform.readDatabase(databasePlatform.getDefaultCatalog(), databasePlatform.getDefaultSchema(), new String[] {});
	}

	/**
	 * Drop 掉dataSource里面所有的objects
	 *
	 */
	public void dropDatabase() {
		databasePlatform.dropDatabase(readDatabase(), true);
	}

	/**
	 * 在dataSource里面增加一个表
	 *
	 * @param table
	 */
	public void createTable(Table table) {
		createTables(new Table[] { table });
	}

	/**
	 * 在dataSource里面增加多个表
	 *
	 * @param tables
	 */
	public void createTables(Table... tables) {
		createTables(false, tables);
	}

	/**
	 * 在dataSource里面增加多个表
	 *
	 * @param tables
	 */
	public void createTables(boolean dropFirst, Table... tables) {
		databasePlatform.createTables(dropFirst, false, tables);
	}

	/**
	 * drop 掉某些表
	 *
	 * @param tables
	 */
	public void dropTables(Table... tables) {
		Database droppedDatabase = new Database();

		for (Table table : tables) {
			droppedDatabase.addTable(table);
		}
		databasePlatform.dropDatabase(droppedDatabase, false);

	}

	/**
	 * drop 掉某一个表
	 *
	 * @param table
	 */
	public void dropTable(Table table) {
		dropTables(table);
	}

	/**
	 * 修改dataSource里面某个表的结构<br>
	 * 此操作可能会清空表中的数据
	 *
	 * @param table
	 */
	public void alterTable(Table table) {
		alterTables(table);
	}

	/**
	 * 修改dataSource里面某个表的结构<br>
	 * 此操作不会清空表中数据,若新增必填字段,并且无设置默认值时会抛出{@link org.jumpmind.db.sql.SqlException}.
	 *
	 * @param table
	 */
	@SuppressWarnings("unchecked")
	public void alterTableSafe(Table table) {
		// 增加前置校验,避免jumpmind框架对数据表进行drop-create操作.
		// 此步骤非常重要,此步骤验证不严谨会造成灾难性的的故障.by woden,2013-11-8
		ModelComparator comparator = new ModelComparator(readDatabase().getName(), databasePlatform.getDatabaseInfo(), false);

		Table currentTable = findTable(table.getName());
		logger.debug("当前DB:{}", currentTable.getName());

		// 解决表名大小写的问题
		table.setName(currentTable.getName());

		List<IModelChange> detectedChanges = comparator.compareTables(null, currentTable, null, table);
		Collection<AddColumnChange> columnChanges = (List<AddColumnChange>) CollectionUtils.select(detectedChanges,
				new MultiInstanceofPredicate(new Class[] { AddColumnChange.class }));
		for (Iterator<AddColumnChange> it = columnChanges.iterator(); it.hasNext();) {
			AddColumnChange addColumnChange = it.next();
			if (addColumnChange.getNewColumn().isRequired() && !addColumnChange.getNewColumn().isAutoIncrement()
					&& (addColumnChange.getNewColumn().getDefaultValue() == null)) {
				String errorMsg = "Data cannot be retained in table " + addColumnChange.getChangedTable().getName()
						+ " because of the addition of the required column " + addColumnChange.getNewColumn().getName();
				logger.error(errorMsg);
				throw new SqlException(errorMsg);
			}
		}

		alterTables(table);
	}

	/**
	 * 修改dataSource里面某个表的结构<br>
	 * 此操作可能会清空表中的数据
	 *
	 * @param tables
	 */
	public void alterTables(Table... tables) {
		databasePlatform.alterTables(false, tables);
	}

	/**
	 * 增加字段
	 *
	 * @param tableName
	 * @param column
	 */
	public void addColumn(String tableName, Column column) {
		addColumns(tableName, column);
	}

	/**
	 * 增加多个字段
	 *
	 * @param tableName
	 * @param columns
	 */
	public void addColumns(String tableName, Column... columns) {
		Table table = findTable(tableName);
		if (table != null) {
			for (Column c : columns) {
				table.addColumn(c);
			}
		}
		alterTable(table);
	}

	/**
	 * 修改表字段
	 *
	 * @param tableName
	 * @param oldColumns
	 *            需要修改的字段
	 * @param newColumns
	 *            修改后的字段
	 */
	public void modifyColumns(String tableName, Column[] oldColumns, Column[] newColumns) {
		Table table = findTable(tableName);
		if (table != null) {
			for (Column oldColumn : oldColumns) {
				table.removeColumn(oldColumn);
			}
			table.addColumns(Arrays.asList(newColumns));
		}
		alterTable(table);
	}

	/**
	 * 修改表字段
	 *
	 * @param tableName
	 * @param oldColumn
	 *            需要修改的字段
	 * @param newColumn
	 *            修改后的字段
	 */
	public void modifyColumn(String tableName, Column oldColumn, Column newColumn) {
		modifyColumns(tableName, new Column[] { oldColumn }, new Column[] { newColumn });
	}

	/**
	 * 去掉某几个字段
	 *
	 * @param tableName
	 * @param columns
	 */
	public void dropColumns(String tableName, Column... columns) {
		Table table = findTable(tableName);
		if (table != null) {
			for (Column column : columns) {
				table.removeColumn(column);
			}
		}
		alterTable(table);
	}

	/**
	 * 去掉某一个字段
	 *
	 * @param tableName
	 * @param column
	 */
	public void dropColumn(String tableName, Column column) {
		dropColumns(tableName, column);
	}

	/**
	 * 从dataSource 里面查找某个表，不区分大小写
	 *
	 * @param tableName
	 *            表名
	 * @return
	 */
	public Table findTable(String tableName) {
		Database database = readDatabase();

		return database.findTable(tableName, false);
	}

	/**
	 * 查找某表里面的字段定义，不区分大小写，没找到表或者字段返回null
	 *
	 * @param tableName
	 * @param columnName
	 * @return
	 */
	public Column findColumn(String tableName, String columnName) {
		Table table = findTable(tableName);
		if (table != null) {
			return table.findColumn(columnName);
		}
		return null;
	}

	/**
	 * 查找某表的主键,没找到表返回null
	 *
	 * @param tableName
	 * @return
	 */
	public Column[] findPrimaryKeyColumns(String tableName) {
		Table table = findTable(tableName);
		if (table != null) {
			return table.getPrimaryKeyColumns();
		}
		return null;
	}

	public static String buildInsertSql(Table t) {
		StringBuilder builder = new StringBuilder();
		builder.append("insert into ");
		builder.append(t.getName());
		builder.append(" (");
		{
			StringBuffer buff = new StringBuffer();
			for (Column column : t.getColumns()) {
				buff.append(",").append(column.getName());
			}
			builder.append(buff.substring(1));// 截取第一个逗号之后的值
		}
		builder.append(") values (");
		{
			StringBuffer buff = new StringBuffer();
			for (Column column : t.getColumns()) {
				buff.append(",:").append(column.getName());
			}
			builder.append(buff.substring(1));// 截取第一个逗号之后的值
		}
		builder.append(")");
		return builder.toString();
	}

}
