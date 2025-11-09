/*
 * $HeadURL: $
 * $Id: $
 * Copyright (c) 2013 by Riversoft System, all rights reserved.
 */
package com.riversoft.module.development.table;

import static com.riversoft.core.web.Actions.includePage;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

import com.riversoft.platform.po.TbIndex;
import com.riversoft.platform.po.TbIndexedColumn;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.riversoft.core.BeanFactory;
import com.riversoft.core.Config;
import com.riversoft.core.db.DataCondition;
import com.riversoft.core.db.DataPackage;
import com.riversoft.core.db.JdbcService;
import com.riversoft.core.db.ORMService;
import com.riversoft.core.db.hbm.HbmClass;
import com.riversoft.core.db.hbm.HbmProperty;
import com.riversoft.core.db.hbm.model.HbmModelConverter;
import com.riversoft.core.exception.ExceptionType;
import com.riversoft.core.exception.SystemRuntimeException;
import com.riversoft.core.web.Actions;
import com.riversoft.core.web.Actions.FileType;
import com.riversoft.core.web.Actions.Util;
import com.riversoft.core.web.RequestUtils;
import com.riversoft.core.web.annotation.ActionAccess;
import com.riversoft.core.web.annotation.ActionAccess.SafeLevel;
import com.riversoft.dbtool.export.DBOperationSignal;
import com.riversoft.dbtool.export.Exporter;
import com.riversoft.dbtool.export.Exporter.ExcelType;
import com.riversoft.dbtool.export.ImportExportResponse;
import com.riversoft.dbtool.export.Importer;
import com.riversoft.platform.db.DTableLoader;
import com.riversoft.platform.db.ORMAdapterService;
import com.riversoft.platform.db.Types;
import com.riversoft.platform.dbtool.H2Exporter;
import com.riversoft.platform.dbtool.H2Importer;
import com.riversoft.platform.po.TbColumn;
import com.riversoft.platform.po.TbTable;
import com.riversoft.platform.service.TableService;
import com.riversoft.platform.web.FileManager;
import com.riversoft.platform.web.FileManager.UploadFile;
import com.riversoft.platform.web.WebLogManager;
import com.riversoft.util.Formatter;
import com.riversoft.util.PoiUtils;
import com.riversoft.util.ValueConvertUtils;
import com.riversoft.util.jackson.JsonMapper;

/**
 * 动态表管理
 * 
 * @author Woden
 * 
 */
@ActionAccess(level = SafeLevel.DEV_R)
public class TableAction {

	private Logger logger = LoggerFactory.getLogger(TableAction.class);

	private TableService service = BeanFactory.getInstance().getBean(TableService.class);

	/**
	 * 动态表管理
	 * 
	 * @param request
	 * @param response
	 */
	public void index(HttpServletRequest request, HttpServletResponse response) {
		includePage(request, response, Util.getPagePath(request, "table_main.jsp"));
	}

	/**
	 * 列表
	 * 
	 * @param request
	 * @param response
	 */
	public void list(HttpServletRequest request, HttpServletResponse response) {
		// 获取分页信息
		int start = Util.getStart(request);
		int limit = Util.getLimit(request);

		// 获取排序信息
		String field = Util.getSortField(request);
		String dir = Util.getSortDir(request);

		// 查询条件
		DataCondition condition = new DataCondition(Util.buildQueryMap(new HashMap<String, Object>(), request));
		condition.setOrderBy(field, dir);

		DataPackage dp = service.queryPackage(TbTable.class.getName(), start, limit, condition.toEntity());
		// 设置到页面
		request.setAttribute("dp", dp);

		Actions.includePage(request, response, Util.getPagePath(request, "table_list.jsp"));
	}

	/**
	 * 删除动态表
	 * 
	 * @param request
	 * @param response
	 */
	@ActionAccess(level = SafeLevel.DEV_W)
	public void delete(HttpServletRequest request, HttpServletResponse response) {
		String name = RequestUtils.getStringValue(request, "name");
		service.executeRemoveTable(name);
		Actions.redirectInfoPage(request, response, "删除成功.");
	}

	/**
	 * 新建动态表(页面)
	 * 
	 * @param request
	 * @param response
	 */
	public void createZone(HttpServletRequest request, HttpServletResponse response) {
		request.setAttribute("types", Types.values());

		Set<String> modes = DTableLoader.getInstance().getNames();
		Map<String, Set<String>> modeMap = new LinkedHashMap<>();
		for (String name : modes) {
			String type = StringUtils.substring(name, 0, StringUtils.lastIndexOf(name, "-"));
			if (!modeMap.containsKey(type)) {
				modeMap.put(type, new HashSet<String>());
			}
			modeMap.get(type).add(name);
		}
		request.setAttribute("modes", modeMap);
		Actions.includePage(request, response, Util.getPagePath(request, "table_form.jsp"));
	}

	/**
	 * 链接游离表(页面)
	 * 
	 * @param request
	 * @param response
	 */
	public void linkZone(HttpServletRequest request, HttpServletResponse response) {
		Actions.includePage(request, response, Util.getPagePath(request, "table_link.jsp"));
	}

	/**
	 * 编辑动态表(页面)
	 * 
	 * @param request
	 * @param response
	 */
	public void editZone(HttpServletRequest request, HttpServletResponse response) {
		String name = RequestUtils.getStringValue(request, "name");
		TbTable table = (TbTable) service.findByPk(TbTable.class.getName(), name);
		request.setAttribute("table", table);
		LinkedHashMap<String, TbColumn> columnMap = new LinkedHashMap<>();
		for (TbColumn column : table.getTbColumns()) {
			columnMap.put(column.getName(), column);
		}
		request.setAttribute("columnMap", columnMap);
		request.setAttribute("types", Types.values());
		Actions.includePage(request, response, Util.getPagePath(request, "table_form.jsp"));
	}

	/**
	 * 模式建表
	 * 
	 * @param request
	 * @param response
	 */
	public void modeZone(HttpServletRequest request, HttpServletResponse response) {
		String mode = RequestUtils.getStringValue(request, "mode");

		List<TbTable> list;
		try {
			list = resolveFile(mode, DTableLoader.getInstance().getResourceInputStream(mode), RequestUtils.getStringValues(request, "tableName"));
		} catch (IOException e) {
			throw new SystemRuntimeException(ExceptionType.BUSINESS, "无法解析模板.");
		}

		if (list == null || list.size() < 1) {
			throw new SystemRuntimeException(ExceptionType.BUSINESS, "无法获取模板信息.");
		}
		TbTable table = list.get(0);

		request.setAttribute("table", table);
		LinkedHashMap<String, TbColumn> columnMap = new LinkedHashMap<>();// 有排序
		for (TbColumn column : table.getTbColumns()) {
			columnMap.put(column.getName(), column);
		}
		request.setAttribute("columnMap", columnMap);
		request.setAttribute("types", Types.values());
		Set<String> modes = DTableLoader.getInstance().getNames();
		Map<String, Set<String>> modeMap = new LinkedHashMap<>();
		for (String name : modes) {
			String type = StringUtils.substring(name, 0, StringUtils.lastIndexOf(name, "-"));
			if (!modeMap.containsKey(type)) {
				modeMap.put(type, new HashSet<String>());
			}
			modeMap.get(type).add(name);
		}
		request.setAttribute("modes", modeMap);
		Actions.includePage(request, response, Util.getPagePath(request, "table_form.jsp"));
	}

	/**
	 * 查看表结构(页面)
	 * 
	 * @param request
	 * @param response
	 */
	public void detailZone(HttpServletRequest request, HttpServletResponse response) {
		String name = RequestUtils.getStringValue(request, "name");
		TbTable table = (TbTable) service.findByPk(TbTable.class.getName(), name);
		request.setAttribute("table", table);
		Actions.includePage(request, response, Util.getPagePath(request, "table_detail.jsp"));
	}

	/**
	 * 编辑字段
	 * 
	 * @param request
	 * @param response
	 */
	public void columnFormZone(HttpServletRequest request, HttpServletResponse response) {
		String column = RequestUtils.getStringValue(request, "column");
		request.setAttribute("column", column);
		Map<String, Integer> type = new HashMap<String, Integer>();
		for (Types t : Types.values()) {
			type.put(t.name(), (Integer) t.getCode());
		}
		request.setAttribute("type", type);
		Actions.includePage(request, response, Util.getPagePath(request, "table_column.jsp"));
	}

	/**
	 * 提交表编辑框
	 * 
	 * @param request
	 * @param response
	 */
	@ActionAccess(level = SafeLevel.DEV_W)
	public void submitForm(HttpServletRequest request, HttpServletResponse response) {
		Integer createFlag = RequestUtils.getIntegerValue(request, "createFlag");
		String name = RequestUtils.getStringValue(request, "name");
		String description = RequestUtils.getStringValue(request, "description");

		// 验证表名
		if (createFlag == 1 && service.checkTableExists(name)) {
			throw new SystemRuntimeException(ExceptionType.BUSINESS, "表[" + name + "]已存在,请尝试更换另一个表名.");
		}

		List<TbColumn> columns = RequestUtils.getValues(request, "column", TbColumn.class);
		if (columns == null || columns.size() < 1) {
			throw new SystemRuntimeException(ExceptionType.BUSINESS, "字段不能为空.");
		}

		// 构造TbTable
		TbTable table = new TbTable();
		table.setCacheFlag(RequestUtils.getIntegerValue(request, "cacheFlag"));
		table.setName(name);
		table.setDescription(description);
		Set<TbColumn> set = new HashSet<>();
		Integer sort = 0;
		boolean hasPrimaryKey = false;
		for (TbColumn column : columns) {
			column.setTableName(name);
			column.setDefaultValue(StringUtils.isNotEmpty(column.getDefaultValue()) ? column.getDefaultValue() : null);
			column.setSort(sort++);// 重新排序
			hasPrimaryKey = hasPrimaryKey || column.isPrimaryKey();// 是否有主键
			set.add(column);
		}
		table.setTbColumns(set);

		if (!hasPrimaryKey) {
			throw new SystemRuntimeException(ExceptionType.BUSINESS, "没有设置主键.");
		}

		String msg;
		if (createFlag != null && createFlag != 1) {// 更新

			// 查看表中是否存在数据
			Number count = (Number) JdbcService.getInstance().findSQL("select count(1) T from " + table.getName()).get("T");
			// 不存在数据则可以安全修改
			// 此操作必须在事务外判断实现
			boolean isSafe = count == null || count.intValue() < 1;

			service.executeUpdateTable(table, isSafe);
			msg = "表[" + name + "]修改成功.";
		} else {// 新增
			service.executeCreateTable(table);
			msg = "创建表[" + name + "]成功.";
		}

		Actions.redirectInfoPage(request, response, msg);
	}

	/**
	 * 链接游离表
	 * 
	 * @param request
	 * @param response
	 */
	@ActionAccess(level = SafeLevel.DEV_W)
	public void submitLink(HttpServletRequest request, HttpServletResponse response) {
		String name = RequestUtils.getStringValue(request, "name");
		String description = RequestUtils.getStringValue(request, "description");
		service.executeLinkTable(name, description);
		Actions.redirectInfoPage(request, response, "关联表[" + name + "]成功.");
	}

	/**
	 * 数据预览
	 * 
	 * @param request
	 * @param response
	 */
	public void preview(HttpServletRequest request, HttpServletResponse response) {
		String name = RequestUtils.getStringValue(request, "name");

		// 获取hbm vo对象
		TbTable table = (TbTable) service.findByPk(TbTable.class.getName(), name);

		// 获取分页信息
		int start = Util.getStart(request);
		int limit = Util.getLimit(request);

		// 获取排序信息
		String field = Util.getSortField(request);
		String dir = Util.getSortDir(request);

		DataPackage dp = ORMAdapterService.getInstance().queryPackage(name, start, limit, new DataCondition().setOrderBy(field, dir).toEntity());
		request.setAttribute("dp", dp);
		request.setAttribute("table", table);

		Actions.includePage(request, response, Actions.Util.getPagePath(request, "table_preview.jsp"));
	}

	/**
	 * 导出表结构
	 * 
	 * @param request
	 * @param response
	 */
	public void exportType(HttpServletRequest request, HttpServletResponse response) {
		// 查询条件
		String[] keys = RequestUtils.getStringValues(request, "_keys");

		// 声明一个工作薄
		Workbook workbook;
		String fileName = "动态表结构导出_" + Formatter.formatDatetime(new Date(), "yyyyMMddHHmmss") + Config.get("core.excel.pixel", ".xls");
		if (fileName.toLowerCase().endsWith(".xls")) {// xls
			workbook = new HSSFWorkbook();
		} else {// xlsx
			workbook = new XSSFWorkbook();
		}

		if (keys != null && keys.length > 0) {

			WebLogManager.log("正在预处理.");
			CellStyle headStyle = PoiUtils.createHeadStyle(workbook);
			CellStyle contentStyle = PoiUtils.createContentStyle(workbook);

			InputStream is = this.getClass().getResourceAsStream("/hbm/dev/TB_COLUMN.hbm.xml");// 从hbm文件读取字段信息
			HbmClass hbmClass = HbmModelConverter.toBean(is);
			List<String> fields = new ArrayList<>();
			Map<String, String> titles = new HashMap<>();
			List<HbmProperty> properties = new ArrayList<>();
			properties.addAll(Arrays.asList(hbmClass.getId().getProperties()));
			properties.addAll(Arrays.asList(hbmClass.getPropertys()));
			for (HbmProperty property : properties) {
				if ("tableName".equals(property.getName())) {
					continue;
				}
				fields.add(property.getName());
				titles.put(property.getName(), property.getComment());
			}

			WebLogManager.beginLoop("开始导出", keys.length);
			for (String key : keys) {
				WebLogManager.signalLoop();
				TbTable table = (TbTable) ORMService.getInstance().findByPk(TbTable.class.getName(), key);
				if (table == null) {
					logger.warn("表[" + key + "]不存在.");
					continue;
				}

				// 生成一个SHEET
				Sheet sheet = workbook.createSheet(StringUtils.replaceEach(table.getDescription(), new String[] { "[", "]", "*" }, new String[] { " ", " ", "@" }));
				// 设置表格默认列宽度为15个字节
				sheet.setDefaultColumnWidth((short) 15);

				// 当前行
				int currentRow = 0;
				// 产生表格标题行
				Row row = sheet.createRow(currentRow++);
				for (int i = 0; i < fields.size(); i++) {
					Cell cell = row.createCell(i);
					cell.setCellStyle(headStyle);
					cell.setCellValue("[" + fields.get(i) + "]" + titles.get(fields.get(i)));
				}

				// 遍历集合数据，产生数据行
				for (TbColumn column : table.getTbColumns()) {
					row = sheet.createRow(currentRow++);
					for (int i = 0; i < fields.size(); i++) {
						Cell cell = row.createCell(i);
						cell.setCellStyle(contentStyle);

						String value;
						try {
							Object obj = PropertyUtils.getProperty(column, fields.get(i));
							if (obj == null) {
								logger.debug("值为空，跳过。");
								continue;
							}
							value = obj.toString();
						} catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
							logger.warn("取值出错,跳过.", e);
							continue;
						}
						cell.setCellValue(value);
					}
				}
			}
		} else {
			WebLogManager.log("无数据导出.");
		}

		WebLogManager.log("正在下载文件.");
		try {
			FileType type = new FileType(fileName);
			type.prepareWeb(request, response);

			OutputStream out = response.getOutputStream();
			workbook.write(response.getOutputStream());
			out.flush();
			out.close();
			response.flushBuffer();
		} catch (IOException e) {
			logger.error("生成excel出错", e);
			throw new SystemRuntimeException(ExceptionType.WEB, "excel数据生成出错。", e);
		}
	}

	/**
	 * 批量上传设置区域
	 * 
	 * @param request
	 * @param response
	 */
	public void batchZone(HttpServletRequest request, HttpServletResponse response) {
		Actions.includePage(request, response, Util.getPagePath(request, "table_batch.jsp"));
	}

	/**
	 * 批量上传设置区域
	 * 
	 * @param request
	 * @param response
	 */
	public void tableNameZone(HttpServletRequest request, HttpServletResponse response) {
		UploadFile file = FileManager.getUploadFile(request, "file");

		if (file == null) {
			throw new SystemRuntimeException(ExceptionType.BUSINESS, "请先选择文件.");
		}

		// 分析文件
		try {
			Workbook workbook = PoiUtils.createWorkbook(file.getFile());
			// 获取sheet name
			String[] sheetNames = new String[workbook.getNumberOfSheets()];
			if (sheetNames.length > 0) {
				for (int i = 0; i < sheetNames.length; i++) {
					sheetNames[i] = workbook.getSheetName(i);
				}
			}

			request.setAttribute("sheetNames", sheetNames);
		} catch (IOException e) {
			throw new SystemRuntimeException(e);
		}

		Actions.includePage(request, response, Util.getPagePath(request, "table_name_list.jsp"));
	}

	/**
	 * 从excel中解析TbTable
	 *
	 * @param fileName
	 * @param fileInputSteam
	 * @param tableNames
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private List<TbTable> resolveFile(String fileName, InputStream fileInputSteam, String... tableNames) {

		List<TbTable> list = new ArrayList<>();
		// 解析table
		try {
			Workbook workbook = PoiUtils.createWorkbook(fileName, fileInputSteam);
			// 获取sheet name
			if (workbook.getNumberOfSheets() > 0) {

				if (workbook.getNumberOfSheets() != tableNames.length) {
					throw new SystemRuntimeException(ExceptionType.BUSINESS, "某些表无分配表名,请重新设置.");
				}

				InputStream is = this.getClass().getResourceAsStream("/hbm/dev/TB_COLUMN.hbm.xml");// 从hbm文件读取字段信息
				HbmClass hbmClass = HbmModelConverter.toBean(is);
				List<HbmProperty> properties = new ArrayList<>();
				properties.addAll(Arrays.asList(hbmClass.getId().getProperties()));
				properties.addAll(Arrays.asList(hbmClass.getPropertys()));

				for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
					Sheet sheet = workbook.getSheetAt(i);
					String tableName = tableNames[i];
					String sheetName = sheet.getSheetName();
					String description = sheetName;
					TbTable table = new TbTable();
					table.setName(tableName);
					table.setDescription(description);
					table.setCacheFlag(0);
					Set<TbColumn> set = new LinkedHashSet<>();
					Iterator<Row> it = sheet.rowIterator();
					if (!it.hasNext()) {
						logger.debug("SHEET[" + sheetName + "]为空,跳过.");
						continue;
					}

					// 跳过首行
					{
						it.next();
					}

					while (it.hasNext()) {
						TbColumn column = new TbColumn();
						Row row = it.next();
						Iterator<Cell> itCell = row.cellIterator();
						for (int j = 0; itCell.hasNext(); j++) {
							Cell cell = itCell.next();
							String value;
							switch (cell.getCellType()) {
							case Cell.CELL_TYPE_STRING:
								value = cell.getStringCellValue();
								break;
							case Cell.CELL_TYPE_NUMERIC:
								value = String.valueOf(cell.getNumericCellValue());
								break;
							case Cell.CELL_TYPE_BOOLEAN:
								value = String.valueOf(cell.getBooleanCellValue());
								break;
							default:
								value = cell.toString();
								break;
							}

							if (StringUtils.isNotEmpty(value)) {
								HbmProperty property = properties.get(j);
								if ("tableName".equals(property.getName())) {
									j++;
									property = properties.get(j);
								}
								String fieldName = property.getName();

								try {
									Object obj = ValueConvertUtils.convert(value, property.getType());
									logger.info("column[" + fieldName + "]=>[" + value + "]==>[" + obj + "]");
									PropertyUtils.setProperty(column, fieldName, obj);
								} catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException | SecurityException e) {
									logger.error(fieldName + "设置出错.", e);
									continue;
								}
							}
						}
						column.setTableName(tableName);
						logger.debug("解析字段:" + JsonMapper.defaultMapper().toJson(column));
						set.add(column);
					}
					table.setTbColumns(set);

					list.add(table);
				}
			}
		} catch (IOException e) {
			logger.error("上传文件格式有误.", e);
			throw new SystemRuntimeException(ExceptionType.BUSINESS, "上传文件格式有误.", e);
		}

		return list;
	}

	/**
	 * 批量上传
	 * 
	 * @param request
	 * @param response
	 */
	@ActionAccess(level = SafeLevel.DEV_W)
	public void submitBatch(HttpServletRequest request, HttpServletResponse response) {
		UploadFile file = FileManager.getUploadFile(request, "file");
		String[] tableNames = RequestUtils.getStringValues(request, "tableName");

		if (tableNames == null || tableNames.length < 1) {
			throw new SystemRuntimeException(ExceptionType.BUSINESS, "请先分析文件.");
		}

		WebLogManager.log("开始分析文件.");
		// 解析表
		List<TbTable> list;
		try {
			list = resolveFile(file.getFile().getName(), new FileInputStream(file.getFile()), tableNames);
		} catch (FileNotFoundException ignore) {
			// ignore
			list = new ArrayList<>();
		}

		List<String> errorList = new ArrayList<>();

		WebLogManager.beginLoop("开始处理,共[" + list.size() + "]个表.", list.size());
		for (TbTable table : list) {
			try {
				WebLogManager.signalLoop();
				service.executeCreateTable(table);
			} catch (SystemRuntimeException e) {
				errorList.add("[" + table.getName() + "]导入失败,原因:" + e.getExtMessage());
			}
		}
		StringBuffer msg = new StringBuffer("本次批量操作工[" + list.size() + "]个表,成功[" + (list.size() - errorList.size()) + "]个.");
		WebLogManager.log(msg.toString());
		if (errorList.size() > 0) {
			msg.append("失败[" + errorList.size() + "]个.");
			for (String error : errorList) {
				msg.append("<br/>").append(error);
			}
		}

		Actions.redirectInfoPage(request, response, msg.toString());
	}

	/**
	 * 数据导出
	 * 
	 * @param request
	 * @param response
	 */
	public void exportData(HttpServletRequest request, HttpServletResponse response) {
		// 查询条件
		final String[] keys = RequestUtils.getStringValues(request, "_keys");

		String exportFileName = "批量数据导出_" + Formatter.formatDatetime(new Date(), "yyyyMMddHHmmss") + Config.get("core.excel.pixel", ".xls");
		Exporter.ExcelType excelType = exportFileName.toLowerCase().endsWith(".xls") ? ExcelType.EXCEL_2003 : ExcelType.EXCEL_AFTER_2003;
		DataSource datasource = (DataSource) BeanFactory.getInstance().getBean("dataSource");
		Exporter exporter = new Exporter(datasource);
		try {
			FileType type = new FileType(exportFileName);
			type.prepareWeb(request, response);

			OutputStream out = response.getOutputStream();
			exporter.doExport(out, excelType, new DBOperationSignal() {

				@Override
				public void signal(String tableName, String description) {
					WebLogManager.signalLoop();
				}

				@Override
				public void end() {
					WebLogManager.log("生成数据完成.开始下载文件.");
				}

				@Override
				public void begin() {
					WebLogManager.beginLoop("开始生成数据文件,共[" + keys.length + "]个表.", keys.length);
				}

			}, keys);

			out.flush();
			out.close();
			response.flushBuffer();
		} catch (Exception e) {
			logger.error("生成excel出错", e);
			throw new SystemRuntimeException(ExceptionType.WEB, "excel数据生成出错。", e);
		}

	}

	/**
	 * 数据导出(高级,采用H2)
	 * 
	 * @param request
	 * @param response
	 */
	public void exportDataExt(HttpServletRequest request, HttpServletResponse response) {
		// 查询条件
		final String[] keys = RequestUtils.getStringValues(request, "_keys");

		String exportFileName = "批量数据导出_" + Formatter.formatDatetime(new Date(), "yyyyMMddHHmmss") + ".h2.db";
		DataSource datasource = (DataSource) BeanFactory.getInstance().getBean("dataSource");
		H2Exporter exporter = new H2Exporter(datasource);
		try {
			FileType type = new FileType(exportFileName);
			type.prepareWeb(request, response);

			OutputStream out = response.getOutputStream();
			exporter.doExport(out, new DBOperationSignal() {

				@Override
				public void signal(String tableName, String description) {
					WebLogManager.signalLoop();
				}

				@Override
				public void end() {
					WebLogManager.log("生成数据完成.开始下载文件.");
				}

				@Override
				public void begin() {
					WebLogManager.beginLoop("开始生成数据文件,共[" + keys.length + "]个表.", keys.length);
				}

			}, keys);

			out.flush();
			out.close();
			response.flushBuffer();
		} catch (Exception e) {
			logger.error("生成h2出错", e);
			throw new SystemRuntimeException(ExceptionType.WEB, "数据文件生成出错。", e);
		}

	}

	/**
	 * 数据导入
	 * 
	 * @param request
	 * @param response
	 */
	@ActionAccess(level = SafeLevel.DEV_W)
	public void importDataZone(HttpServletRequest request, HttpServletResponse response) {
		Actions.includePage(request, response, Util.getPagePath(request, "table_import_data.jsp"));
	}

	/**
	 * 批量数据导入
	 * 
	 * @param request
	 * @param response
	 */
	@ActionAccess(level = SafeLevel.DEV_W)
	public void submitImportData(HttpServletRequest request, HttpServletResponse response) {
		UploadFile file = FileManager.getUploadFile(request, "file");

		boolean clearBeforeImport = "1".equals(RequestUtils.getStringValue(request, "clearBeforeImport"));
		boolean replaceIfConflict = "1".equals(RequestUtils.getStringValue(request, "replaceIfConflict"));
		boolean exitIfError = "1".equals(RequestUtils.getStringValue(request, "exitIfError"));

		DataSource datasource = (DataSource) BeanFactory.getInstance().getBean("dataSource");
		ImportExportResponse importExportResponse;
		if (file.getFile().getName().toLowerCase().endsWith(".h2.db")) {// h2文件
			importExportResponse = new H2Importer(datasource).doImport(file.getFile(), clearBeforeImport, replaceIfConflict, exitIfError, new DBOperationSignal() {
				@Override
				public void signal(String tableName, String description) {
				}

				@Override
				public void end() {
					WebLogManager.log("正在转向页面.");
				}

				@Override
				public void begin() {
					WebLogManager.log("开始导入.");
				}
			});
		} else {
			try {
				Workbook workbook = PoiUtils.createWorkbook(file.getFile());
				final int size = workbook.getNumberOfSheets();
				importExportResponse = new Importer(datasource).doImport(file.getFile(), clearBeforeImport, replaceIfConflict, exitIfError, new DBOperationSignal() {
					@Override
					public void signal(String tableName, String description) {
						WebLogManager.signalLoop();
					}

					@Override
					public void end() {
						WebLogManager.log("正在转向页面.");
					}

					@Override
					public void begin() {
						WebLogManager.beginLoop("开始处理,共[" + size + "]个表.", size);
					}

				});
			} catch (InvalidFormatException | IOException e) {
				logger.error("导入数据失败.", e);
				throw new SystemRuntimeException(ExceptionType.WEB, "excel数据导入出错。", e);
			}
		}

		WebLogManager.log("正在等待后端响应.");
		Actions.redirectInfoPage(request, response,
				(importExportResponse.isSuccess() ? "导入成功." : "导入失败," + ("1".equals(RequestUtils.getStringValue(request, "exitIfError")) ? "错误已被跳过." : "所有数据均没有被提交.")));
	}

	/**
	 * 锁定/解锁
	 * 
	 * @param request
	 * @param response
	 */
	@ActionAccess(level = SafeLevel.DEV_W)
	public void lock(HttpServletRequest request, HttpServletResponse response) {
		TbTable tbTable = (TbTable) ORMService.getInstance().findByPk(TbTable.class.getName(), RequestUtils.getStringValue(request, "name"));
		tbTable.setLockFlag(RequestUtils.getIntegerValue(request, "lockFlag"));
		ORMService.getInstance().updatePO(tbTable);
		Actions.redirectInfoPage(request, response, "操作成功.");
	}

	/**
	 * 同步表结构
	 * 
	 * @param request
	 * @param response
	 */
	@ActionAccess(level = SafeLevel.DEV_W)
	public void syncType(HttpServletRequest request, HttpServletResponse response) {
		// 查询条件
		String[] keys = RequestUtils.getStringValues(request, "_keys");

		WebLogManager.beginLoop("正在进行表结构同步.", keys.length);
		for (String name : keys) {
			// 查看表中是否存在数据
			Number count = 0;
			try {
				count = (Number) JdbcService.getInstance().findSQL("select count(1) T from " + name).get("T");
				// 不存在数据则可以安全修改
				// 此操作必须在事务外判断实现
			} catch (Exception e) {
				// do nothing
			}
			boolean isSafe = count == null || count.intValue() < 1;
			service.executeSyncTable(name, isSafe);
			WebLogManager.signalLoop();
		}

		Actions.redirectInfoPage(request, response, "同步表结构成功.");
	}

	/**
	 * 编辑动态表索引(页面)
	 *
	 * @param request
	 * @param response
	 */
	public void editIndex(HttpServletRequest request, HttpServletResponse response) {
		String tableName = RequestUtils.getStringValue(request, "tableName");
		TbTable table = (TbTable) service.findByPk(TbTable.class.getName(), tableName);
		request.setAttribute("table", table);

		LinkedHashMap<String, TbIndex> indexMap = new LinkedHashMap<>();
		if(table.getTbIndexes() != null) {
			for (TbIndex index : table.getTbIndexes()) {
				indexMap.put(index.getName(), index);
			}
		}
		request.setAttribute("indexMap", indexMap);

		Actions.includePage(request, response, Util.getPagePath(request, "table_index_form.jsp"));
	}

	/**
	 * 编辑index
	 *
	 * @param request
	 * @param response
	 */
	public void indexFormZone(HttpServletRequest request, HttpServletResponse response) {
		String tableName = RequestUtils.getStringValue(request, "tableName");
		String index = RequestUtils.getStringValue(request, "index");
		request.setAttribute("index", index);

		Set<TbIndexedColumn> selectedColumns = null;
		if(StringUtils.isNotEmpty(index)) {
			TbIndex tbIndex = RequestUtils.getValue(request, "index", TbIndex.class);
			selectedColumns = (tbIndex == null ? new HashSet<TbIndexedColumn>() : tbIndex.getIndexedColumns());
			request.setAttribute("selectedColumns", selectedColumns);
		}

		TbTable table = (TbTable) service.findByPk(TbTable.class.getName(), tableName);
		request.setAttribute("table", table);
		Set<TbColumn> allColumns = new LinkedHashSet<>();
		for (TbColumn column : table.getTbColumns()) {
			if(!column.isPrimaryKey() && column.isRequired() && !isSelected(selectedColumns, column)) {
				allColumns.add(column);
			}
		}
		request.setAttribute("allColumns", allColumns);

		Actions.includePage(request, response, Util.getPagePath(request, "table_index.jsp"));
	}

	private boolean isSelected(Set<TbIndexedColumn> selectedColumns, TbColumn column) {
		if(selectedColumns == null) return false;
		for (TbIndexedColumn tbIndexedColumn : selectedColumns) {
			if(tbIndexedColumn.getName().equalsIgnoreCase(column.getName())) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 提交索引编辑框
	 *
	 * @param request
	 * @param response
	 */
	@ActionAccess(level = SafeLevel.DEV_W)
	public void submitIndexForm(HttpServletRequest request, HttpServletResponse response) {
		String tableName = RequestUtils.getStringValue(request, "tableName");
		List<TbIndex> indexes = RequestUtils.getValues(request, "index", TbIndex.class);

		TbTable table = (TbTable) service.findByPk(TbTable.class.getName(), tableName);

		for(TbIndex index : indexes) {
			index.setTableName(tableName);
			Set<TbIndexedColumn> indexedColumns = index.getIndexedColumns();
			int i = 0;
			for(TbIndexedColumn tbIndexedColumn : indexedColumns) {
				tbIndexedColumn.setIndexName(index.getName());
				tbIndexedColumn.setOrdinalPosition(i);
				tbIndexedColumn.setPrimaryKey(false);
				i++;
			}
		}
		Set<TbIndex> indexSet = new HashSet<>();
		indexSet.addAll(indexes);

		table.setTbIndexes(indexSet);

		service.executeUpdateTableIndex(table, true);
		Actions.redirectInfoPage(request, response, "索引创建成功.");
	}

}
