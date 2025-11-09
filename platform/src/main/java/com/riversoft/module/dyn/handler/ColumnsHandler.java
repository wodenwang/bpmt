/*
 * $HeadURL: $
 * $Id: $
 * Copyright (c) 2013 by Riversoft System, all rights reserved.
 */
package com.riversoft.module.dyn.handler;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.riversoft.core.IDGenerator;
import com.riversoft.core.context.RequestContext;
import com.riversoft.core.db.DataPO;
import com.riversoft.core.db.ORMService;
import com.riversoft.core.db.ORMService.QueryVO;
import com.riversoft.core.exception.ExceptionType;
import com.riversoft.core.exception.SystemRuntimeException;
import com.riversoft.platform.db.Types;
import com.riversoft.platform.po.CmPri;
import com.riversoft.platform.po.TbColumn;
import com.riversoft.platform.web.handler.Handler;
import com.riversoft.util.ValueConvertUtils;

/**
 * @author woden
 * 
 */
public class ColumnsHandler implements Handler {

	/**
	 * 根据type获取ORM对象
	 * 
	 * @param type
	 * @return
	 */
	private String getORMTable(String type) {
		switch (type) {
		case "show":
			return "VwDynColumnShow";
		case "form":
			return "VwDynColumnForm";
		case "line":
			return "VwDynColumnLine";
		case "sys":
			return "VwDynColumn";
		}

		return null;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void handle(DataPO tablePO) {
		RequestContext request = RequestContext.getCurrent();

		if (!"true".equalsIgnoreCase(request.getString("hasColumns"))) {
			if (tablePO.getSubList("columns") == null || tablePO.getSubList("columns").size() < 1) {
				throw new SystemRuntimeException(ExceptionType.BUSINESS, "请先进行动态表[字段]设置后再提交.");
			}
			return;
		}

		List<HashMap<String, Object>> deleteColumns = request.getJsons("deleteColumn");
		List<HashMap<String, Object>> waitColumns = request.getJsons("waitColumn");

		// 删除
		if (deleteColumns != null) {
			for (HashMap<String, Object> json : deleteColumns) {
				ORMService.getInstance().removeByPk(getORMTable(json.get("type").toString()),
						Long.parseLong(json.get("id").toString()));
			}
		}

		// 处理更新或新增
		if (waitColumns != null) {
			for (HashMap<String, Object> json : waitColumns) {
				String pixel = json.get("pixel").toString();
				Integer sort = (Integer) json.get("sort");
				Integer listSort = (Integer) json.get("listSort");
				String type = json.get("type").toString();

				Long id;
				if (json.containsKey("id")) {
					id = Long.parseLong(json.get("id").toString());
				} else {
					id = null;
				}

				String ormTableName = getORMTable(type);
				boolean isCreate;
				DataPO columnPO;
				if (id != null) {
					isCreate = false;
					columnPO = new DataPO(ormTableName, (Map<String, Object>) ORMService.getInstance().findByPk(
							ormTableName, id));
				} else {
					isCreate = true;
					columnPO = new DataPO(ormTableName);
					columnPO.set("viewKey", tablePO.getString("viewKey"));
				}
				columnPO.set("sort", sort);
				columnPO.set("listSort", listSort);

				// 是否有编辑
				if ("true".equalsIgnoreCase(request.getString(pixel + ".flag"))) {
					switch (type) {
					case "form":// 表单
					{
						String name = request.getString(pixel + ".name");
						String busiName = request.getString(pixel + ".busiName");
						String widget = request.getString(pixel + ".widget");
						String contentScript = request.getString(pixel + ".contentScript");
						Integer contentType = request.getInteger(pixel + ".contentType");
						Integer whole = request.getInteger(pixel + ".whole");
						String execScript = request.getString(pixel + ".execScript");
						Integer execType = request.getInteger(pixel + ".execType");
						Integer tipType = request.getInteger(pixel + ".tipType");
						String tipScript = request.getString(pixel + ".tipScript");
						Integer widgetParamType = request.getInteger(pixel + ".widgetParamType");
						String widgetParamScript = request.getString(pixel + ".widgetParamScript");
						String style = request.getString(pixel + ".style");

						columnPO.set("name", name);
						columnPO.set("busiName", busiName);
						columnPO.set("widget", widget);
						columnPO.set("contentScript", contentScript);
						columnPO.set("contentType", contentType);
						columnPO.set("style", style);
						columnPO.set("whole", whole);
						columnPO.set("execScript", execScript);
						columnPO.set("execType", execType);
						columnPO.set("tipType", tipType);
						columnPO.set("tipScript", tipScript);
						columnPO.set("widgetParamType", widgetParamType);
						columnPO.set("widgetParamScript", widgetParamScript);
						CmPri pri = ValueConvertUtils.convert(request.getString(pixel + ".pri"), CmPri.class);
						pri.setDevelopmentInfo(columnPO, "表单字段");
						CmPri editPri = ValueConvertUtils.convert(request.getString(pixel + ".editPri"), CmPri.class);
						editPri.setDevelopmentInfo(columnPO, "表单字段", "编辑");
						columnPO.set("pri", pri);
						columnPO.set("editPri", editPri);
					}
						break;
					case "show": // 展示
					{
						String busiName = request.getString(pixel + ".busiName");
						String contentScript = request.getString(pixel + ".contentScript");
						Integer contentType = request.getInteger(pixel + ".contentType");
						String style = request.getString(pixel + ".style");
						Integer whole = request.getInteger(pixel + ".whole");
						String sortField = request.getString(pixel + ".sortField");
						columnPO.set("busiName", busiName);
						columnPO.set("contentScript", contentScript);
						columnPO.set("contentType", contentType);
						columnPO.set("style", style);
						columnPO.set("whole", whole);
						columnPO.set("sortField", sortField);
						columnPO.set("expandFlag", request.getInteger(pixel + ".expandFlag"));
						CmPri pri = ValueConvertUtils.convert(request.getString(pixel + ".pri"), CmPri.class);
						pri.setDevelopmentInfo(columnPO, "展示字段");
						columnPO.set("pri", pri);
					}
						break;
					case "line": // 分割线
					{
						String busiName = request.getString(pixel + ".busiName");
						Integer tipType = request.getInteger(pixel + ".tipType");
						String tipScript = request.getString(pixel + ".tipScript");
						columnPO.set("busiName", busiName);
						columnPO.set("tipType", tipType);
						columnPO.set("tipScript", tipScript);
						columnPO.set("expandFlag", request.getInteger(pixel + ".expandFlag"));
						CmPri pri = ValueConvertUtils.convert(request.getString(pixel + ".pri"), CmPri.class);
						pri.setDevelopmentInfo(columnPO, "分割线");
						columnPO.set("pri", pri);
					}
						break;
					case "sys":// 固定字段
					{
						String name = request.getString(pixel + ".name");
						String busiName = request.getString(pixel + ".busiName");
						String widget = request.getString(pixel + ".widget");
						String widgetContentScript = request.getString(pixel + ".widgetContentScript");
						Integer widgetContentType = request.getInteger(pixel + ".widgetContentType");
						String contentScript = request.getString(pixel + ".contentScript");
						Integer contentType = request.getInteger(pixel + ".contentType");
						String style = request.getString(pixel + ".style");
						Integer whole = request.getInteger(pixel + ".whole");
						String execScript = request.getString(pixel + ".execScript");
						Integer execType = request.getInteger(pixel + ".execType");
						Integer showFlag = request.getInteger(pixel + ".showFlag");
						Integer formFlag = request.getInteger(pixel + ".formFlag");
						Integer tipType = request.getInteger(pixel + ".tipType");
						String tipScript = request.getString(pixel + ".tipScript");
						Integer widgetParamType = request.getInteger(pixel + ".widgetParamType");
						String widgetParamScript = request.getString(pixel + ".widgetParamScript");

						columnPO.set("name", name);
						columnPO.set("busiName", busiName);
						columnPO.set("widget", widget);
						columnPO.set("widgetContentType", widgetContentType);
						columnPO.set("widgetContentScript", widgetContentScript);
						columnPO.set("contentScript", contentScript);
						columnPO.set("contentType", contentType);
						columnPO.set("style", style);
						columnPO.set("whole", whole);
						columnPO.set("execScript", execScript);
						columnPO.set("execType", execType);
						columnPO.set("showFlag", showFlag);
						columnPO.set("formFlag", formFlag);
						columnPO.set("tipType", tipType);
						columnPO.set("tipScript", tipScript);
						columnPO.set("widgetParamType", widgetParamType);
						columnPO.set("widgetParamScript", widgetParamScript);

						CmPri pri = ValueConvertUtils.convert(request.getString(pixel + ".pri"), CmPri.class);
						pri.setDevelopmentInfo(columnPO, "主表字段");
						columnPO.set("pri", pri);
						String strCreatePri = request.getString(pixel + ".createPri");
						if (StringUtils.isNotEmpty(strCreatePri)) {
							CmPri cmPri = ValueConvertUtils.convert(strCreatePri, CmPri.class);
							cmPri.setDevelopmentInfo(columnPO, "主表字段", "录入");
							columnPO.set("createPri", cmPri);
						} else {
							columnPO.set("createPri", null);
						}

						String strUpdatePri = request.getString(pixel + ".updatePri");
						if (StringUtils.isNotEmpty(strUpdatePri)) {
							CmPri cmPri = ValueConvertUtils.convert(strUpdatePri, CmPri.class);
							cmPri.setDevelopmentInfo(columnPO, "主表字段", "编辑");
							columnPO.set("updatePri", cmPri);
						} else {
							columnPO.set("updatePri", null);
						}
					}
						break;
					}
				} else if (isCreate && "sys".equalsIgnoreCase(type)) {// 固定字段创建的情况
					String name = json.get("name").toString();
					String tableName = tablePO.getString("name");
					TbColumn column = (TbColumn) ORMService.getInstance().findHQL(
							"from TbColumn where tableName = ? and name = ?", tableName, name);
					if (column == null) {
						throw new SystemRuntimeException(ExceptionType.CONFIG, "固定字段[" + name
								+ "]对应表或表字段已被删除,请刷新后继续配置.");
					}

					StringBuffer buff = new StringBuffer();
					columnPO.set("whole", 0);
					if (column.getMappedTypeCode() == (int) Types.Date.getCode()) {
						buff.append("date");
					} else if (column.getMappedTypeCode() == (int) Types.Clob.getCode()) {
						buff.append("textarea");
						columnPO.set("whole", 1);
					} else if (column.getSizeAsInt() > 500) {
						buff.append("textarea");
						columnPO.set("whole", 1);
					} else if (column.getMappedTypeCode() == (int) Types.Blob.getCode()) {
						buff.append("filemanager");
					} else {
						buff.append("text");
					}

					columnPO.set("contentType", 1);// 默认groovy
					columnPO.set("contentScript", "return cm.widget('" + buff.toString() + "',vo?." + column.getName()
							+ ");");

					buff.append("{required:");
					if (column.isPrimaryKey() || column.isRequired()) {
						buff.append("true");
					} else {
						buff.append("false");
					}
					if (column.getMappedTypeCode() == (Integer) Types.Integer.getCode()
							|| column.getMappedTypeCode() == (Integer) Types.Long.getCode()) {
						buff.append(",digits:true");
					}

					if (column.getMappedTypeCode() == (Integer) Types.BigDecimal.getCode()) {
						buff.append(",number:true");
					}
					buff.append("}");
					columnPO.set("widget", buff.toString());
					columnPO.set("name", name);
					columnPO.set("busiName", column.getDescription());
					columnPO.set("formFlag", 1);
					columnPO.set("showFlag", 1);
					// 权限
					CmPri pri = new CmPri();
					pri.setPriKey(IDGenerator.uuid());
					pri.setDevelopmentInfo(columnPO, "主表字段");
					columnPO.set("pri", pri);

					// 自动递增主键没有录入权限
					if (!column.isPrimaryKey() || !column.isAutoIncrement()) {
						CmPri cmPri = new CmPri();
						cmPri.setPriKey(IDGenerator.uuid());
						cmPri.setDevelopmentInfo(columnPO, "主表字段", "录入");
						columnPO.set("createPri", cmPri);
					}

					// 主键没有修改权限
					if (!column.isPrimaryKey()) {
						CmPri cmPri = new CmPri();
						cmPri.setPriKey(IDGenerator.uuid());
						cmPri.setDevelopmentInfo(columnPO, "主表字段", "编辑");
						columnPO.set("updatePri", cmPri);
					}
				}

				if (isCreate) {
					ORMService.getInstance().save(columnPO.toEntity());
				} else {
					ORMService.getInstance().merge(columnPO.toEntity());
				}
			}
		}

		// 删除多余的视图字段
		List<String> tableColumnNames = ORMService.getInstance().queryHQL(
				"select name from " + TbColumn.class.getName() + " where tableName = ?", tablePO.getString("name"));
		ORMService.getInstance().executeHQL("delete from VwDynColumn where viewKey = :viewKey and name not in (:list)",
				new QueryVO("viewKey", tablePO.getString("viewKey")), new QueryVO("list", tableColumnNames));

		// 把set设置回去,更新hibernate二级缓存
		tablePO.set("formColumns", null);
		tablePO.set("showColumns", null);
		tablePO.set("lineColumns", null);
		tablePO.set("columns", null);
	}
}
