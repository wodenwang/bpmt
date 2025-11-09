/*
 * $HeadURL: $
 * $Id: $
 * Copyright (c) 2017 by Riversoft System, all rights reserved.
 */
package com.riversoft.platform.store.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.dialect.Dialect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.ColumnMapRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import com.jolbox.bonecp.BoneCPDataSource;
import com.riversoft.core.BeanFactory;
import com.riversoft.core.Config;
import com.riversoft.core.db.JdbcService;
import com.riversoft.core.exception.ExceptionType;
import com.riversoft.core.exception.SystemRuntimeException;

/**
 * 额外的DB连接池
 * 
 * @author woden
 */
public class OtherDatabasePools {

	private static Logger logger = LoggerFactory.getLogger(OtherDatabasePools.class);
	private static final OtherDatabasePools INSTANCE = new OtherDatabasePools();

	/**
	 * 获取池单例
	 * 
	 * @return
	 */
	public static OtherDatabasePools getInstance() {
		return INSTANCE;
	}

	/**
	 * 初始化jdbc模板
	 * 
	 * @param key
	 * @return
	 */
	private JdbcTemplate init(String key) {

		logger.debug("初始化外部数据库:{}", key);
		String driverClassName = Config.get("db." + key + ".driverClassName");
		String jdbcUrl = Config.get("db." + key + ".url");
		String username = Config.get("db." + key + ".username");
		String password = Config.get("db." + key + ".password");

		if (StringUtils.isEmpty(driverClassName) || StringUtils.isEmpty(jdbcUrl) || StringUtils.isEmpty(username)) {
			throw new SystemRuntimeException(ExceptionType.CODING, "外部数据库[" + key + "]未配置链接.");
		}
		logger.debug("参数校验成功,链接:{}", jdbcUrl);

		Map<String, Object> propertyMap = new HashMap<>();
		propertyMap.put("driverClass", driverClassName);
		propertyMap.put("jdbcUrl", jdbcUrl);
		propertyMap.put("username", username);
		propertyMap.put("password", password);
		propertyMap.put("logStatementsEnabled", Config.get("sql.log", "false"));

		DataSource dataSource = BeanFactory.getInstance().getBean("database-" + key, BoneCPDataSource.class, false,
				true, null, propertyMap);
		JdbcTemplate jdbcTemplate = BeanFactory.getInstance().getBean("jdbcTemplate-" + key, JdbcTemplate.class, false,
				true, new Object[] { dataSource }, null);
		logger.debug("外部数据库:{}初始化成功.", key);
		return jdbcTemplate;
	}

	public DbHelper getHelper(String key) {
		DbHelper ins = DbHelper.getInstance(key);
		if (ins != null) {
			return ins;
		}

		// 初始化
		JdbcTemplate jdbcTemplate = init(key);
		DbHelper.init(key, jdbcTemplate);
		return DbHelper.getInstance(key);
	}

	public JdbcService getService(String key) {
		DbHelper dbHelper = getHelper(key);
		return JdbcService.newInstance(key, dbHelper.jdbcTemplate, dbHelper.dialect);
	}

	/**
	 * 外部db函数库
	 * 
	 * @author woden
	 *
	 */
	public static class DbHelper {
		private String key;
		private JdbcTemplate jdbcTemplate;
		private Dialect dialect;
		private static Map<String, DbHelper> INS_MAP = new HashMap<>();// 多例模式

		static synchronized void init(String key, JdbcTemplate jdbcTemplate) {
			DbHelper ins = new DbHelper();
			ins.jdbcTemplate = jdbcTemplate;
			ins.key = key;

			// 数据库方言
			String className = Config.get("db." + key + ".dialect");
			logger.info("数据库:{},使用了方言:{}", key, className);
			try {
				if (StringUtils.isNotEmpty(className)) {
					Class klass = Class.forName(className);
					ins.dialect = (Dialect) klass.newInstance();
				}
			} catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
				logger.error("初始化数据库方言失败", e);
			}

			INS_MAP.put(key, ins);
		}

		static DbHelper getInstance(String key) {
			return INS_MAP.get(key);
		}

		/**
		 * 根据sql查询唯一值
		 * 
		 * @param sql
		 * @param args
		 * @return
		 */
		public Map<String, Object> find(String sql, Object... args) {
			try {
				return (Map) jdbcTemplate.queryForObject(sql, args, new ColumnMapRowMapper());
			} catch (EmptyResultDataAccessException e) {
				return null;
			}
		}

		/**
		 * 根据sql查询列表
		 * 
		 * @param sql
		 * @param args
		 * @return
		 */
		public List<?> query(String sql, Object... args) {
			try {
				return jdbcTemplate.query(sql, args, new ColumnMapRowMapper());
			} catch (EmptyResultDataAccessException e) {
				return new ArrayList<>();
			}
		}

		/**
		 * 执行sql<br>
		 * FIXME 未实现分布式事务
		 * 
		 * @param sql
		 * @param args
		 */
		public void exec(String sql, Object... args) {
			jdbcTemplate.update(sql, args);
		}

		/**
		 * 执行新增语句并返回自动递增ID<br>
		 * FIXME 未实现分布式事务
		 * 
		 * @param sql
		 * @param args
		 * @return
		 */
		public Long save(final String sql, final Object... args) {
			KeyHolder keyHolder = new GeneratedKeyHolder();
			jdbcTemplate.update(new PreparedStatementCreator() {
				@Override
				public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
					PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
					if (args != null) {
						int i = 1;
						for (Object obj : args) {
							ps.setObject(i, obj);
							i++;
						}
					}
					return ps;
				}
			}, keyHolder);
			logger.debug("自动流水号:" + keyHolder.getKey().intValue());
			return keyHolder.getKey().longValue();
		}
	}
}
