/*
 * $HeadURL: $
 * $Id: $
 * Copyright (c) 2014 by Riversoft System, all rights reserved.
 */
package com.riversoft.platform.dbtool;

import java.io.File;
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
import com.riversoft.dbtool.export.ImportExportResponse;
import com.riversoft.platform.Platform;

/**
 * @author woden
 * 
 */
public class H2Importer {

	private static final Logger logger = LoggerFactory.getLogger(H2Importer.class);
	private DataSource dataSource;// 源

	public H2Importer(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	public ImportExportResponse doImport(File file, boolean clearBeforeImport, boolean replaceIfConflict,
			boolean exitIfError, DBOperationSignal dBOperationSignal) {

		// 创建临时文件
		String h2FileName = Platform.getTempPath().getAbsolutePath() + File.separator + "_"
				+ UUID.randomUUID().toString();
		logger.debug("临时文件名:{}", h2FileName + ".h2.db");

		try {
			FileUtils.copyFile(file, new File(h2FileName + ".h2.db"));// 拷贝文件
			JdbcDataSource srcDataSource = new JdbcDataSource();
			srcDataSource.setURL("jdbc:h2:" + h2FileName);
			srcDataSource.setUser("sa");
			srcDataSource.setPassword("");

			DataSourceCopier copier = new DataSourceCopier(srcDataSource, dataSource);
			copier.copyAllWithoutDDL(exitIfError, clearBeforeImport, replaceIfConflict, dBOperationSignal);

			srcDataSource.getConnection().close();
			srcDataSource = null;
			return new ImportExportResponse(true, "导入成功.");

		} catch (Exception e) {
			throw new SystemRuntimeException(ExceptionType.DB, e);
		}

	}
}
