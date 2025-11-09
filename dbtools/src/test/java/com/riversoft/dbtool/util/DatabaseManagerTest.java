/*
 * $HeadURL: $
 * $Id: $
 * Copyright (c) 2013 by Riversoft System, all rights reserved.
 */
package com.riversoft.dbtool.util;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Types;
import java.util.List;

import javax.sql.DataSource;

import org.apache.commons.io.FileUtils;
import org.jumpmind.db.model.*;
import org.jumpmind.db.sql.DmlStatement.DmlType;
import org.jumpmind.db.sql.Row;
import org.jumpmind.db.sql.SqlException;
import org.junit.*;

/**
 * @author Borball
 *
 */
public class DatabaseManagerTest {
    
    private static DataSource dataSource;
    private static DatabaseManager databaseManager;
    private String testTableName = "TEST_TABLE";

    @BeforeClass
    public static void beforeClass() throws Exception {
        Path databsebakBakPath = Paths.get("src/test/resources/database/dbutildb.h2.db.bak");
        Path databsetestPath = Paths.get("src/test/resources/database/dbutildb.h2.db");
        
        File databsebak = databsebakBakPath.toFile();
        File databsetest = databsetestPath.toFile();
        
        FileUtils.copyFile(databsebak, databsetest);
        dataSource = DataSourceInstance.getInstance("classpath:dbutil-jdbc.properties").getDataSource();
        databaseManager = new DatabaseManager(dataSource);
    }

    @Before
    public void before() {
        dropTables();
    }

    @After
    public void after() {
        dropTables();
    }

    private void dropTables() {
        databaseManager.dropTable(databaseManager.findTable(testTableName));
    }

    @Test
    public void testDeleteDml(){
        String deleteSql = databaseManager.createDml( databaseManager.findTable("CM_BASE_DATA"), DmlType.DELETE).getSql();
        System.out.println(deleteSql);
    }
    
    @Test
    public void testCountDml(){
        String countSql = databaseManager.createDml(databaseManager.findTable("CM_BASE_DATA"), DmlType.COUNT).getSql();
        System.out.println(countSql);
    }
    
    @Test
    public void testFromDml(){
        String fromSql = databaseManager.createDml(databaseManager.findTable("CM_BASE_DATA"), DmlType.FROM).getSql();
        System.out.println(fromSql);
    }
    
    @Test
    public void testInsertDml(){
        String insertSql = databaseManager.createDml(databaseManager.findTable("CM_BASE_DATA"), DmlType.INSERT).getSql();
        System.out.println(insertSql);
    }
    
    @Test
    public void testSelectDml(){
        String selectSql = databaseManager.createDml(databaseManager.findTable("CM_BASE_DATA"), DmlType.SELECT).getSql();
        System.out.println(selectSql);
    }
    
    @Test
    public void testSelectAllDml(){
        String selectAllSql = databaseManager.createDml(databaseManager.findTable("CM_BASE_DATA"), DmlType.SELECT_ALL).getSql();
        System.out.println(selectAllSql);
    }

    @Test
    public void testUpdateDml(){
        String UpdateSql = databaseManager.createDml(databaseManager.findTable("CM_BASE_DATA"), DmlType.UPDATE).getSql();
        System.out.println(UpdateSql);
    }

    @Test
    public void testCreateNewTable() {
        createTestTable();

        Assert.assertNotNull(databaseManager.findTable(testTableName));
        Assert.assertEquals(1, databaseManager.findPrimaryKeyColumns(testTableName).length);
        Assert.assertEquals("id", databaseManager.findPrimaryKeyColumns(testTableName)[0].getName());
    }

    @Test
    public void testCreateConflictTable() {
        createTestTable();

        Assert.assertNotNull(databaseManager.findTable(testTableName));

        try {
            createTestTable();
            Assert.fail();
        } catch (SqlException e) {
            System.out.println(e.getMessage());
        }

    }

    @Test
    public void testDropTable() {
        createTestTable();
        Assert.assertNotNull(databaseManager.findTable(testTableName));

        Table table = databaseManager.findTable(testTableName);
        databaseManager.dropTable(table);

        Assert.assertNull(databaseManager.findTable(testTableName));

    }

    @Test(expected = SqlException.class)
    public void testDropUnExistingTable() {
        Assert.assertNull(databaseManager.findTable(testTableName));

        databaseManager.dropTable(new Table("TABLE_UNEXISTING"));
    }

    @Test
    public void testAlterTableWithNewColumnAdded() {
        createTestTable();

        Table table = databaseManager.findTable(testTableName);

        Column newColumn = new Column("newcolumn");
        newColumn.setDescription("newcolumn desc");
        newColumn.setTypeCode(Types.NVARCHAR);
        table.addColumn(newColumn);

        databaseManager.alterTables(new Table[] { table });

        Assert.assertNotNull(databaseManager.findTable(testTableName));

        Column column = databaseManager.findColumn(testTableName, "newcolumn");

        Assert.assertNotNull(column);

    }

    @Test
    public void testAlterTableWithOldColumnUpdate() {
        createTestTable();
        List<Row> rows = databaseManager.getJdbcSqlTemplate().query("select * from " + testTableName);

        Assert.assertEquals(1, rows.size());
        Assert.assertEquals(10, rows.get(0).getString("name").getBytes().length);

        Table table = databaseManager.findTable(testTableName);
        table.getColumnWithName("name").setSize("5");
        databaseManager.alterTables(new Table[] { table });

        Assert.assertEquals("10", databaseManager.findTable(table.getName()).getColumnWithName("name").getSize());

        rows = databaseManager.getJdbcSqlTemplate().query("select * from " + testTableName);
        Assert.assertEquals(1, rows.size());
        Assert.assertEquals(10, rows.get(0).getString("name").getBytes().length);

    }

    @Test
    public void testAlterTableWithDropColumn() {
        createTestTable();

        Table table = databaseManager.findTable(testTableName);

        table.removeColumn(databaseManager.findColumn(testTableName, "name"));

        databaseManager.alterTables(new Table[] { table });

        Assert.assertNull(databaseManager.findColumn(testTableName, "name"));

    }

    @Test
    public void testAddColumn() {
        createTestTable();

        Column newColumn = new Column("newcolumn");
        newColumn.setDescription("newcolumn desc");
        newColumn.setTypeCode(Types.NVARCHAR);

        databaseManager.addColumn(testTableName, newColumn);

        Assert.assertNotNull(databaseManager.findTable(testTableName));

        Column column = databaseManager.findColumn(testTableName, "newcolumn");

        Assert.assertNotNull(column);

    }

    @Test
    public void testModifyColumn() {
        createTestTable();
        Table table = databaseManager.findTable(testTableName);
        Column oldColumn = table.getColumnWithName("name");

        Column newColumn = oldColumn;
        oldColumn.setSize("5");

        databaseManager.modifyColumn(testTableName, oldColumn, newColumn);
        Assert.assertEquals("10", databaseManager.findTable(table.getName()).getColumnWithName("name").getSize());

    }

    @Test
    public void testDropColumn() {
        createTestTable();

        databaseManager.dropColumn(testTableName, databaseManager.findColumn(testTableName, "name"));

        Assert.assertNull(databaseManager.findColumn(testTableName, "name"));

    }

    private void createTestTable() {
        Table table = new Table("TEST_TABLE");
        table.setDescription("Test Table");

        Column id = new Column("id");
        id.setAutoIncrement(true);
        id.setPrimaryKey(true);
        id.setTypeCode(Types.INTEGER);
        id.setDescription("id desc");

        Column name = new Column("name");
        name.setDescription("name desc");
        name.setTypeCode(Types.VARCHAR);
        name.setSize("10");

        table.addColumn(id);
        table.addColumn(name);

        IndexColumn indexColumn = new IndexColumn();
        indexColumn.setColumn(name);

        IIndex uniqueIndex = new UniqueIndex();
        uniqueIndex.addColumn(indexColumn);
        uniqueIndex.setName(("U_" + testTableName + "_" + name.getName()).toUpperCase());
        table.addIndex(uniqueIndex);

        databaseManager.createTable(table);

        String insert = "insert into TEST_TABLE(\"name\") values (\'namenamena\');";
        databaseManager.getJdbcSqlTemplate().update(true, false, 3, insert);

    }
}
