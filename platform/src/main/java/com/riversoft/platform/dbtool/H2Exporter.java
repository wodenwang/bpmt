/*
 * $HeadURL: $
 * $Id: $
 * Copyright (c) 2014 by Riversoft System, all rights reserved.
 */
package com.riversoft.platform.dbtool;

import java.io.File;
import java.io.OutputStream;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import javax.sql.DataSource;

import org.apache.commons.io.FileUtils;
import org.h2.jdbcx.JdbcDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.riversoft.core.exception.ExceptionType;
import com.riversoft.core.exception.SystemRuntimeException;
import com.riversoft.dbtool.export.DBOperationSignal;
import com.riversoft.dbtool.export.DataSourceCopier;
import com.riversoft.dbtool.export.NullDBOperationSignal;
import com.riversoft.platform.Platform;

/**
 * @author woden
 * 
 */
public class H2Exporter {

	private static final Logger logger = LoggerFactory.getLogger(H2Exporter.class);
	private DataSource dataSource;// 源

	private DataSource destDataSource;// h2数据源
	private String h2FileName;

	public H2Exporter(DataSource dataSource) {
		this.dataSource = dataSource;
		this.h2FileName = Platform.getTempPath().getAbsolutePath() + File.separator + "_"
				+ UUID.randomUUID().toString();
		logger.debug("生成h2文件名:{}", this.h2FileName);
	}

	/**
	 * 获取目标数据源
	 * 
	 * @return
	 */
	private synchronized DataSource getH2DataSource() {
		if (destDataSource == null) {
			this.destDataSource = new JdbcDataSource();
			((JdbcDataSource) this.destDataSource).setURL("jdbc:h2:" + h2FileName);
			((JdbcDataSource) this.destDataSource).setUser("sa");
			((JdbcDataSource) this.destDataSource).setPassword("");
		}
		return destDataSource;
	}

	private synchronized void closeH2DataSource() {
		if (destDataSource != null) {
			try {
				destDataSource.getConnection().close();
				destDataSource = null;
			} catch (SQLException e) {
				throw new SystemRuntimeException(ExceptionType.DB, e);
			}
		}
	}

	/**
	 * 执行导出
	 * 
	 * @param ouputStream
	 * @param dbOperationSignal
	 * @param tables
	 */
	public void doExport(OutputStream ouputStream, DBOperationSignal dbOperationSignal, String... tables) {
		DataSourceCopier copier = new DataSourceCopier(dataSource, getH2DataSource());
		Set<String> tableNames = new HashSet<>();
		tableNames.addAll(Arrays.asList(tables));

		try {
			copier.copyWithDDL(tableNames, dbOperationSignal);
			closeH2DataSource();
			ouputStream.write(FileUtils.readFileToByteArray(new File(h2FileName + ".h2.db")));
		} catch (Exception e) {
			throw new SystemRuntimeException(ExceptionType.DB, e);
		}
	}

	/**
	 * 批量导出
	 * 
	 * @param ouputStream
	 * @param tables
	 */
	public void doExport(OutputStream ouputStream, String... tables) {
		doExport(ouputStream, new NullDBOperationSignal(), tables);
	}

}
