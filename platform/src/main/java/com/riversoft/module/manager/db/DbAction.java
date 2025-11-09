/*
 * $HeadURL: $
 * $Id: $
 * Copyright (c) 2013 by Riversoft System, all rights reserved.
 */
package com.riversoft.module.manager.db;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;

import com.riversoft.core.BeanFactory;
import com.riversoft.core.Config;
import com.riversoft.core.IDGenerator;
import com.riversoft.core.db.DataCondition;
import com.riversoft.core.db.DataPO;
import com.riversoft.core.db.DataPackage;
import com.riversoft.core.db.ORMService;
import com.riversoft.core.exception.ExceptionType;
import com.riversoft.core.exception.SystemRuntimeException;
import com.riversoft.core.web.Actions;
import com.riversoft.core.web.Actions.Util;
import com.riversoft.core.web.RequestUtils;
import com.riversoft.core.web.annotation.ActionAccess;
import com.riversoft.core.web.annotation.ActionAccess.SafeLevel;
import com.riversoft.platform.db.BaseDataService;
import com.riversoft.platform.web.FileManager;
import com.riversoft.platform.web.ListFileExcelParser;
import com.riversoft.platform.web.FileManager.UploadFile;
import com.riversoft.util.Formatter;

/**
 * 数据字典管理
 * 
 * @author woden
 * 
 */
@ActionAccess(level = SafeLevel.DEV_R)
public class DbAction {

	private DbService service = BeanFactory.getInstance().getSingleBean(DbService.class);

	/**
	 * 数据字典管理
	 * 
	 * @param request
	 * @param response
	 */
	public void index(HttpServletRequest request, HttpServletResponse response) {
		Actions.includePage(request, response, Util.getPagePath(request, "main.jsp"));
	}

	/**
	 * 展示类别树
	 * 
	 * @param request
	 * @param response
	 */
	public void typeTree(HttpServletRequest request, HttpServletResponse response) {
		String cp = Actions.Util.getContextPath(request);
		// 查找catelog
		List<Map<String, Object>> list = new ArrayList<>();
		List<Map<String, Object>> catelogs = ORMService.getInstance().query("CmBaseCatelog",
				new DataCondition().setOrderByAsc("sort").toEntity());
		List<Map<String, Object>> types = ORMService.getInstance().query("CmBaseType",
				new DataCondition().setOrderByAsc("sort").toEntity());

		for (Map<String, Object> vo : catelogs) {
			Map<String, Object> o = new HashMap<>();
			o.put("parentId", null);
			o.put("busiName", vo.get("busiName"));
			o.put("id", "_catelog_" + vo.get("id"));
			o.put("icon", cp + "/css/icon/folder_bookmark.png");
			o.put("dataType", null);
			o.put("catelogId", vo.get("id"));
			o.put("isDataType", false);
			list.add(o);
		}

		for (Map<String, Object> vo : types) {
			String catelog = (String) vo.get("catelog");
			Map<String, Object> o = new HashMap<>();
			o.put("parentId", "_catelog_" + catelog);
			o.put("busiName", vo.get("busiName"));
			o.put("id", "_type_" + vo.get("dataType"));
			o.put("icon", cp + "/css/icon/book.png");
			o.put("dataType", vo.get("dataType"));
			o.put("isDataType", true);
			list.add(o);
		}
		request.setAttribute("tree", list);
		Actions.includePage(request, response, Util.getPagePath(request, "type_tree.jsp"));
	}

	/**
	 * 创建类别
	 * 
	 * @param request
	 * @param response
	 */
	public void createTypeZone(HttpServletRequest request, HttpServletResponse response) {
		Actions.includePage(request, response, Util.getPagePath(request, "type_form.jsp"));
	}

	/**
	 * 创建类别
	 * 
	 * @param request
	 * @param response
	 */
	@SuppressWarnings("unchecked")
	public void updateTypeZone(HttpServletRequest request, HttpServletResponse response) {
		String dataType = RequestUtils.getStringValue(request, "dataType");

		Map<String, Object> vo = (Map<String, Object>) ORMService.getInstance().findByPk("CmBaseType", dataType);
		if (vo == null) {
			throw new SystemRuntimeException(ExceptionType.BUSINESS, "数据字典类别[" + dataType + "]不存在.");
		}
		request.setAttribute("vo", vo);

		Actions.includePage(request, response, Util.getPagePath(request, "type_form.jsp"));
	}

	/**
	 * 删除分类
	 * 
	 * @param request
	 * @param response
	 */
	@ActionAccess(level = SafeLevel.DEV_R)
	public void deleteType(HttpServletRequest request, HttpServletResponse response) {
		String dataType = RequestUtils.getStringValue(request, "dataType");
		service.executeRemoveTypeAndData(dataType);
		Actions.redirectInfoPage(request, response, "删除成功.");
	}

	/**
	 * 保存字典类别表单
	 * 
	 * @param request
	 * @param response
	 */
	@ActionAccess(level = SafeLevel.DEV_W)
	@SuppressWarnings("unchecked")
	public void submitTypeForm(HttpServletRequest request, HttpServletResponse response) {
		String dataType = RequestUtils.getStringValue(request, "dataType");
		boolean editFlag = RequestUtils.getIntegerValue(request, "editFlag") == 1;
		DataPO po;
		Map<String, Object> entity = (Map<String, Object>) ORMService.getInstance().findByPk("CmBaseType", dataType);

		if (!editFlag) {// 新增
			if (entity != null) {
				throw new SystemRuntimeException(ExceptionType.BUSINESS, "类别[" + dataType + "]已存在.");
			}
			po = new DataPO("CmBaseType");
			po.set("dataType", dataType);
		} else {// 修改
			if (entity == null) {
				throw new SystemRuntimeException(ExceptionType.BUSINESS, "类别[" + dataType + "]不存在.");
			}
			po = new DataPO("CmBaseType", entity);
		}

		po.set("busiName", RequestUtils.getStringValue(request, "busiName"));
		po.set("sort", RequestUtils.getIntegerValue(request, "sort"));
		po.set("catelog", RequestUtils.getStringValue(request, "catelog"));
		po.set("description", RequestUtils.getStringValue(request, "description"));

		if (!editFlag) {// 新增
			ORMService.getInstance().save(po.toEntity());
		} else {// 修改
			ORMService.getInstance().update(po.toEntity());
		}

		Actions.redirectInfoPage(request, response, editFlag ? "修改成功." : "新增成功.");
	}

	/**
	 * 数据字典
	 * 
	 * @param request
	 * @param response
	 */
	@SuppressWarnings("unchecked")
	public void dataMainZone(HttpServletRequest request, HttpServletResponse response) {
		String dataType = RequestUtils.getStringValue(request, "dataType");
		Map<String, Object> vo = (Map<String, Object>) ORMService.getInstance().findByPk("CmBaseType", dataType);
		if (vo == null) {
			throw new SystemRuntimeException(ExceptionType.BUSINESS, "数据字典类别[" + dataType + "]不存在.");
		}
		request.setAttribute("vo", vo);
		Actions.includePage(request, response, Util.getPagePath(request, "data_main.jsp"));
	}

	/**
	 * 数据列表
	 * 
	 * @param request
	 * @param response
	 */
	public void dataList(HttpServletRequest request, HttpServletResponse response) {

		// 获取分页信息
		int start = Util.getStart(request);
		int limit = Util.getLimit(request);

		// 获取排序信息
		String field = Util.getSortField(request);
		String dir = Util.getSortDir(request);

		// 查询条件
		DataCondition condition = new DataCondition(Util.buildQueryMap(new HashMap<String, Object>(), request));
		condition.setOrderBy(field, dir);

		DataPackage dp = ORMService.getInstance().queryPackage("CmBaseData", start, limit, condition.toEntity());
		// 设置到页面
		request.setAttribute("dp", dp);
		Actions.includePage(request, response, Util.getPagePath(request, "data_list.jsp"));
	}

	/**
	 * 删除字典数据
	 * 
	 * @param request
	 * @param response
	 */
	@ActionAccess(level = SafeLevel.DEV_W)
	public void dataDelete(HttpServletRequest request, HttpServletResponse response) {
		String dataType = RequestUtils.getStringValue(request, "dataType");
		String[] dataCodes = RequestUtils.getStringValues(request, "dataCode");
		service.executeRemoveData(dataType, dataCodes);
		Actions.redirectInfoPage(request, response, "删除成功.");
	}

	/**
	 * 新增字典数据
	 * 
	 * @param request
	 * @param response
	 */
	public void createDataZone(HttpServletRequest request, HttpServletResponse response) {
		String dataType = RequestUtils.getStringValue(request, "dataType");
		request.setAttribute("dataType", dataType);
		Actions.includePage(request, response, Util.getPagePath(request, "data_form.jsp"));
	}

	/**
	 * 编辑字典数据
	 * 
	 * @param request
	 * @param response
	 */
	@SuppressWarnings("unchecked")
	public void editDataZone(HttpServletRequest request, HttpServletResponse response) {
		String dataType = RequestUtils.getStringValue(request, "dataType");
		String dataCode = RequestUtils.getStringValue(request, "dataCode");
		request.setAttribute("dataType", dataType);
		Map<String, Object> vo = (Map<String, Object>) ORMService.getInstance().find(
				"CmBaseData",
				new DataCondition().setStringEqual("dataType", dataType).setStringEqual("dataCode", dataCode)
						.toEntity());
		if (vo == null) {
			throw new SystemRuntimeException(ExceptionType.BUSINESS, "字典数据[" + dataType + "]不存在.");
		}
		request.setAttribute("vo", vo);

		Actions.includePage(request, response, Util.getPagePath(request, "data_form.jsp"));
	}

	/**
	 * 提交字典数据
	 * 
	 * @param request
	 * @param response
	 */
	@ActionAccess(level = SafeLevel.DEV_W)
	@SuppressWarnings("unchecked")
	public void submitDataForm(HttpServletRequest request, HttpServletResponse response) {
		String dataType = RequestUtils.getStringValue(request, "dataType");
		String dataCode = RequestUtils.getStringValue(request, "dataCode");
		request.setAttribute("dataType", dataType);
		Map<String, Object> entity = (Map<String, Object>) ORMService.getInstance().find(
				"CmBaseData",
				new DataCondition().setStringEqual("dataType", dataType).setStringEqual("dataCode", dataCode)
						.toEntity());
		DataPO po;
		boolean editFlag;
		if (entity == null) {
			po = new DataPO("CmBaseData");
			po.set("dataType", dataType);
			po.set("dataCode", dataCode);
			editFlag = false;
		} else {
			po = new DataPO("CmBaseData", entity);
			editFlag = true;
		}

		po.set("showName", RequestUtils.getStringValue(request, "showName"));
		po.set("parentCode", RequestUtils.getStringValue(request, "parentCode"));
		po.set("extra", RequestUtils.getStringValue(request, "extra"));
		po.set("description", RequestUtils.getStringValue(request, "description"));
		po.set("sort", RequestUtils.getIntegerValue(request, "sort"));

		if (editFlag) {
			ORMService.getInstance().update(po.toEntity());
		} else {
			ORMService.getInstance().save(po.toEntity());
		}

		Actions.redirectInfoPage(request, response, editFlag ? "编辑成功." : "新增成功.");
	}

	/**
	 * 批量导出
	 * 
	 * @param request
	 * @param response
	 */
	@SuppressWarnings("unchecked")
	public void downloadData(HttpServletRequest request, HttpServletResponse response) {
		String[] fields = new String[] { "dataType", "_busiName", "_catelog", "dataCode", "showName", "parentCode",
				"sort", "extra", "description", "createDate", "updateDate" };
		Map<String, String> titles = new HashMap<String, String>();
		{
			titles.put("dataType", "数据分类");
			titles.put("_busiName", "数据分类展示名");
			titles.put("_catelog", "分类类别");
			titles.put("dataCode", "代码");
			titles.put("showName", "翻译值");
			titles.put("parentCode", "父代码");
			titles.put("sort", "排序");
			titles.put("extra", "辅助字段");
			titles.put("description", "描述");
			titles.put("createDate", "创建时间");
			titles.put("updateDate", "更新时间");
		}
		List<HashMap<String, Object>> list = new ArrayList<>();

		String dataType = RequestUtils.getStringValue(request, "dataType");
		DataCondition condition = new DataCondition().setOrderByAsc("catelog");
		if (StringUtils.isNotEmpty(dataType)) {
			condition.setStringEqual("dataType", dataType);
		}
		List<Map<String, Object>> typeList = ORMService.getInstance().query("CmBaseType", condition.toEntity());
		for (Map<String, Object> type : typeList) {
			List<Map<String, Object>> dataList = ORMService.getInstance().query("CmBaseData",
					new DataCondition().setStringEqual("dataType", (String) type.get("dataType")).toEntity());
			for (Map<String, Object> data : dataList) {
				HashMap<String, Object> vo = new HashMap<>();
				// type
				vo.put("dataType", (String) type.get("dataType"));
				vo.put("_busiName", (String) type.get("busiName"));
				vo.put("_catelog", (String) type.get("catelog"));
				// data
				vo.put("dataCode", (String) data.get("dataCode"));
				vo.put("showName", (String) data.get("showName"));
				vo.put("parentCode", (String) data.get("parentCode"));
				vo.put("sort", data.get("sort").toString());
				vo.put("extra", (String) data.get("extra"));
				vo.put("description", (String) data.get("description"));
				vo.put("createDate", Formatter.formatDatetime((Date) data.get("createDate")));
				vo.put("updateDate", Formatter.formatDatetime((Date) data.get("updateDate")));
				list.add(vo);
			}
		}

		Actions.downloadExcel(
				request,
				response,
				"数据字典导出" + "_" + Formatter.formatDatetime(new Date(), "yyyyMMddHHmmss")
						+ Config.get("core.excel.pixel", ".xls"), fields, titles, list);
	}

	/**
	 * 批量上传设置区域
	 * 
	 * @param request
	 * @param response
	 */
	public void batchZone(HttpServletRequest request, HttpServletResponse response) {
		Actions.includePage(request, response, Util.getPagePath(request, "batch.jsp"));
	}

	/**
	 * 批量上传
	 * 
	 * @param request
	 * @param response
	 */
	@ActionAccess(level = SafeLevel.DEV_W)
	public void submitBatch(HttpServletRequest request, HttpServletResponse response) {
		Integer type = RequestUtils.getIntegerValue(request, "type");
		UploadFile file = FileManager.getUploadFile(request, "file");
		String[] fields = new String[] { "dataType", "_busiName", "_catelog", "dataCode", "showName", "parentCode",
				"sort", "extra", "description", "createDate", "updateDate" };
		List<Map<String, String>> list;
		try (InputStream is = file.getInputStream()) {
			// 解析excel
			list = new ListFileExcelParser(Arrays.asList(fields)).parse(is).getResult();
		} catch (IOException e) {
			throw new SystemRuntimeException("设值出错", e);
		}

		if (type == 1) {// 新增
			service.executeBatchCreate(list);
			Actions.redirectInfoPage(request, response, "批量添加成功.");
		} else {// 修改
			service.executeBatchUpdate(list);
			Actions.redirectInfoPage(request, response, "批量修改成功.");
		}
	}

	/**
	 * 控件预览
	 * 
	 * @param request
	 * @param response
	 */
	public void preview(HttpServletRequest request, HttpServletResponse response) {
		String type = RequestUtils.getStringValue(request, "type");
		request.setAttribute("list", BaseDataService.getInstance().getTrees(type));
		Actions.includePage(request, response, Util.getPagePath(request, "type_preview.jsp"));
	}

	/**
	 * 类别窗口
	 * 
	 * @param request
	 * @param response
	 */
	public void catelogWin(HttpServletRequest request, HttpServletResponse response) {
		String id = RequestUtils.getStringValue(request, "id");

		if (StringUtils.isNotEmpty(id)) {
			Map<String, Object> vo = (Map<String, Object>) ORMService.getInstance().findByPk("CmBaseCatelog", id);
			request.setAttribute("vo", vo);
		}

		Actions.includePage(request, response, Util.getPagePath(request, "catelog_win.jsp"));
	}

	/**
	 * 删除类别
	 * 
	 * @param request
	 * @param response
	 */
	@ActionAccess(level = SafeLevel.DEV_W)
	public void deleteCatelog(HttpServletRequest request, HttpServletResponse response) {
		String id = RequestUtils.getStringValue(request, "id");

		if (StringUtils.isNotEmpty(id)) {
			ORMService.getInstance().removeByPk("CmBaseCatelog", id);
			Actions.redirectInfoPage(request, response, "删除成功.");
		} else {
			throw new SystemRuntimeException(ExceptionType.BUSINESS, "ID不存在.");
		}
	}

	/**
	 * 保存类别
	 * 
	 * @param request
	 * @param response
	 */
	@ActionAccess(level = SafeLevel.DEV_W)
	public void submitCatelog(HttpServletRequest request, HttpServletResponse response) {
		String id = RequestUtils.getStringValue(request, "id");
		String busiName = RequestUtils.getStringValue(request, "busiName");
		String description = RequestUtils.getStringValue(request, "description");

		Map<String, Object> vo;
		if (StringUtils.isNotEmpty(id)) {// 修改
			vo = (Map<String, Object>) ORMService.getInstance().findByPk("CmBaseCatelog", id);
		} else {// 新增
			vo = new DataPO("CmBaseCatelog").toEntity();
			vo.put("id", IDGenerator.next());
			vo.put("sort", 999);
		}

		vo.put("busiName", busiName);
		vo.put("description", description);
		ORMService.getInstance().saveOrUpdate(vo);

		Actions.redirectInfoPage(request, response, "保存成功.");

	}

	/**
	 * 保存排序
	 * 
	 * @param request
	 * @param response
	 */
	@ActionAccess(level = SafeLevel.DEV_W)
	public void saveSort(HttpServletRequest request, HttpServletResponse response) {
		HashMap<String, Object> tree = RequestUtils.getJsonValue(request, "tree");
		DbService service = BeanFactory.getInstance().getBean(DbService.class);
		service.executeSaveSort((List<HashMap<String, Object>>)tree.get("catelogs"), (List<HashMap<String, Object>>)tree.get("types"));
		Actions.redirectInfoPage(request, response, "保存成功.");
	}

}
