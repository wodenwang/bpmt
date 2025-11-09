/*
 * $HeadURL: $
 * $Id: $
 * Copyright (c) 2014 by Riversoft System, all rights reserved.
 */
package com.riversoft.dbtool.export;

import java.io.File;
import java.util.List;

import org.jumpmind.db.model.Table;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.jdbc.core.JdbcTemplate;

import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;
import com.riversoft.dbtool.util.DatabaseManager;

/**
 * @author woden
 * 
 */
@Ignore
public class ImporterWithMysqlTest {

	private static MysqlDataSource dataSource;
	private static DatabaseManager databaseManager;
	private static JdbcTemplate jdbcTemplate;

	@BeforeClass
	public static void beforeClass() throws Exception {

		dataSource = new MysqlDataSource();
		dataSource.setUrl("jdbc:mysql://localhost:3306/COPY_MYSQL");
		dataSource.setUser("root");
		dataSource.setPassword("root");

		databaseManager = new DatabaseManager(dataSource);
		jdbcTemplate = new JdbcTemplate(dataSource);
	}

	@Ignore
	public void testDoImportWithReplaceOld() throws Exception {
		Importer importer = new Importer(dataSource);
		ImportExportResponse response = importer.doImport(
				new File("D:/river/COPY_MYSQL2/database/bpmt_init_data.xlsx"), false, true, false);
		Assert.assertTrue(response.isSuccess());
		Table table = databaseManager.findTable("CM_BASE_CATELOG");
		Assert.assertNotNull(table);

		@SuppressWarnings("rawtypes")
		List result = jdbcTemplate.queryForList("select * from CM_BASE_CATELOG");
		Assert.assertNotNull(result);
		Assert.assertEquals(2, result.size());
	}
}
