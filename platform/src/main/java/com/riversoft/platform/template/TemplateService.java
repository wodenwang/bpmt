/*
 * $HeadURL: $
 * $Id: $
 * Copyright (c) 2014 by Riversoft System, all rights reserved.
 */
package com.riversoft.platform.template;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Blob;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.sql.DataSource;

import org.springframework.util.StreamUtils;

import com.riversoft.core.BeanFactory;
import com.riversoft.core.IDGenerator;
import com.riversoft.core.db.DataPO;
import com.riversoft.core.db.ORMService;
import com.riversoft.core.exception.SystemRuntimeException;
import com.riversoft.core.script.function.FormatterFunction;
import com.riversoft.dbtool.export.DBOperationSignal;
import com.riversoft.platform.Platform;
import com.riversoft.platform.SessionManager;
import com.riversoft.platform.dbtool.H2Exporter;
import com.riversoft.platform.po.TbTable;
import com.riversoft.platform.service.TableService;
import com.riversoft.platform.web.WebLogManager;
import com.riversoft.util.Formatter;
import com.riversoft.util.jackson.JsonMapper;

/**
 * @author woden
 * 
 */
public class TemplateService {

	/**
	 * 获取单例
	 * 
	 * @return
	 */
	public static TemplateService getInstance() {
		return BeanFactory.getInstance().getBean(TemplateService.class);
	}

	/**
	 * 创建po
	 * 
	 * @param name
	 * @param busiName
	 * @param propertyValue
	 * @return
	 */
	private DataPO buildTemplateProperty(String name, String busiName, String propertyValue) {
		DataPO po = new DataPO("TplCurrent");
		po.set("name", name);
		po.set("busiName", busiName);
		po.set("propertyValue", propertyValue);
		return po;
	}

	/**
	 * 创建当前配置列表
	 */
	private byte[] buildDBFile(Set<String> dynamicTables) {
		H2Exporter exporter = new H2Exporter((DataSource) BeanFactory.getInstance().getBean("dataSource"));
		// 过滤掉US和TPL
		Collection<String> sysTables = BeanFactory.getInstance().getBean(TableService.class).getSyncableSystemTables();

		Set<String> allTables = new HashSet<>();
		allTables.addAll(sysTables);
		allTables.addAll(dynamicTables);

		final String[] tables = allTables.toArray(new String[0]);
		try (ByteArrayOutputStream os = new ByteArrayOutputStream();) {
			// 导出到文件
			exporter.doExport(os, new DBOperationSignal() {

				@Override
				public void begin() {
					WebLogManager.beginLoop("正在备份数据.", tables.length);
				}

				@Override
				public void signal(String table, String description) {
					WebLogManager.signalLoop();
				}

				@Override
				public void end() {
					WebLogManager.log("备份数据完成.");
				}
			}, tables);
			return os.toByteArray();
		} catch (IOException e) {
			throw new SystemRuntimeException(e);
		}
	}

	/**
	 * 生成快照
	 * 
	 * @param name
	 * @param description
	 */
	public void executeBuild(String name, String description, Set<String> copyDataTables) {
		Template template = Template.getCurrent();

		String key;
		if (template.getKey() == null) {
			key = IDGenerator.next();
			DataPO po = buildTemplateProperty("key", "唯一健", key);
			ORMService.getInstance().saveOrUpdate(po.toEntity());
		} else {
			key = template.getKey();
		}

		WebLogManager.log("正在生成快照.");
		ORMService.getInstance().saveOrUpdate(buildTemplateProperty("name", "模板名", name).toEntity());
		ORMService.getInstance().saveOrUpdate(buildTemplateProperty("description", "模板描述", description).toEntity());
		ORMService.getInstance().saveOrUpdate(buildTemplateProperty("version", "版本", String.valueOf(template.getVersion() + 1)).toEntity());
		ORMService.getInstance().saveOrUpdate(buildTemplateProperty("platformVersion", "平台版本", Platform.getVersion() == null ? "snapshot" : Platform.getVersion()).toEntity());
		ORMService.getInstance().saveOrUpdate(buildTemplateProperty("date", "生成时间", FormatterFunction.formatDatetime(new Date())).toEntity());
		//ORMService.getInstance().saveOrUpdate(buildTemplateProperty("copyDataTables", "附加数据表", StringUtils.join(copyDataTables, ";")).toEntity());

		// 快照历史表登记
		{
			DataPO po = new DataPO("TplSnapshot");
			po.set("id", IDGenerator.uuid());
			po.set("name", name);
			po.set("description", description);
			po.set("shortKey", key);
			po.set("createUid", SessionManager.getUser().getUid());
			po.set("version", template.getVersion() + 1);
			po.set("platformVersion", Platform.getVersion() == null ? "snapshot" : Platform.getVersion());
			po.set("dbFile", ORMService.getInstance().getBlob(buildDBFile(copyDataTables)));
			po.set("modifiedTables", getModifiedTables(template.getDate()));
			po.set("copyDataTables", JsonMapper.defaultMapper().toJson(copyDataTables));
			ORMService.getInstance().save(po.toEntity());
		}

		// 登记快照记录
		{
			DataPO po = new DataPO("TplSnapshotRecord");
			po.set("createUid", SessionManager.getUser().getUid());
			po.set("version", template.getVersion());
			po.set("oprMemo", "快照[版本:" + template.getVersion() + "]封结.");
			po.set("createDate", new Date());
			ORMService.getInstance().save(po.toEntity());
		}

		// 登记快照记录
		{
			DataPO po = new DataPO("TplSnapshotRecord");
			po.set("createUid", SessionManager.getUser().getUid());
			po.set("version", template.getVersion() + 1);
			po.set("oprMemo", "快照[版本:" + (template.getVersion() + 1) + "]初始化.");
			po.set("createDate", new Date());
			ORMService.getInstance().save(po.toEntity());
		}

		WebLogManager.log("正在更新模板.");
		template.init();
	}

	private String getModifiedTables(Date date) {
		List<TbTable> tables = (List<TbTable>) ORMService.getInstance().queryHQL("from TbTable where updateDate >= ?", date);
		Set<String> set = new HashSet<>();
		if (tables != null) {
			for (TbTable tbTable : tables) {
				set.add(tbTable.getName());
			}
		}

		return set.isEmpty() ? "" : JsonMapper.defaultMapper().toJson(set);

	}

	/**
	 * 获取快照sho包
	 * 
	 * @param os
	 * @param id
	 */
	@SuppressWarnings({ "unchecked" })
	public void buildShoPackage(OutputStream os, String id) {
		Map<String, Object> vo = (Map<String, Object>) ORMService.getInstance().findByPk("TplSnapshot", id);
		Blob h2File = (Blob) vo.get("dbFile");

		try (ZipOutputStream zos = new ZipOutputStream(os); InputStream is = h2File.getBinaryStream()) {
			zos.putNextEntry(new ZipEntry("config.h2.db"));
			StreamUtils.copy(is, zos);

			zos.putNextEntry(new ZipEntry("config.properties"));
			Properties prop = new Properties();
			prop.put("snapshot.id", id);
			prop.put("snapshot.name", vo.get("name"));
			prop.put("snapshot.version", vo.get("version").toString());
			prop.put("snapshot.description", vo.get("description"));
			prop.put("snapshot.platformVersion", vo.get("platformVersion"));
			prop.put("snapshot.shortKey", vo.get("shortKey"));
			prop.put("snapshot.createDate", Formatter.formatDatetime((Date) vo.get("createDate")));
			prop.put("snapshot.modifiedTables", vo.get("modifiedTables"));
			prop.put("snapshot.copyDataTables", vo.get("copyDataTables"));

			prop.store(zos, "snapshot meta-info");

		} catch (IOException | SQLException e) {
			throw new SystemRuntimeException(e);
		}

	}
}
