/*
 * $HeadURL: $
 * $Id: $
 * Copyright (c) 2015 by Riversoft System, all rights reserved.
 */
package com.riversoft.module.view.viewer;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;

import com.riversoft.core.BeanFactory;
import com.riversoft.core.db.DataPO;
import com.riversoft.core.db.ORMService;
import com.riversoft.core.exception.ExceptionType;
import com.riversoft.core.exception.SystemRuntimeException;
import com.riversoft.core.web.Actions;
import com.riversoft.core.web.Actions.Keys;
import com.riversoft.core.web.Actions.Util;
import com.riversoft.core.web.RequestUtils;
import com.riversoft.platform.office.ConverterHelper;
import com.riversoft.platform.web.FileManager;
import com.riversoft.platform.web.FileManager.UploadFile;
import com.riversoft.platform.web.view.BaseDynamicViewAction;
import com.riversoft.platform.web.view.annotation.Conf;
import com.riversoft.platform.web.view.annotation.View;
import com.riversoft.platform.web.view.annotation.View.LoginType;

/**
 * 模板输出视图
 * 
 * @author woden
 */
@View(value = "viewer", group = View.Group.SYS, loginType = { LoginType.USER, LoginType.NONE })
@Conf(description = "控制器视图", sort = 100, target = { Conf.TargetType.HOME, Conf.TargetType.MENU, Conf.TargetType.BTN, Conf.TargetType.SUB, Conf.TargetType.WX })
public class ViewerViewAction extends BaseDynamicViewAction {

	@SuppressWarnings("unchecked")
	@Override
	protected void main(HttpServletRequest request, HttpServletResponse response, String key) {
		Map<String, Object> table = (Map<String, Object>) ORMService.getInstance().findByPk("VwViewer", key);
		Util.setTitle(request, (String) table.get("busiName"));
		ViewerViewService viewerViewService = BeanFactory.getInstance().getBean(ViewerViewService.class);

		// 处理变量
		Map<String, Object> params = RequestUtils.getJsonValue(request, Keys.PARAMS.toString());
		if (params != null) {
			if (params.containsKey("js")) {
				request.setAttribute(Keys.H5_JS.toString(), params.get("js"));
			}
			if (params.containsKey("head") && "false".equalsIgnoreCase(params.get("head").toString())) {// 引入weixin库
				request.setAttribute(Keys.HEAD.toString(), false);
			}
			if (params.containsKey("action_mode")) {// 指定ACTION_MODE
				request.setAttribute(Keys.ACTION_MODE.toString(), params.get("action_mode"));
			}
		}

		ResultType resultType = ResultType.fromCode((Integer) table.get("resultType"));
		if (hasTemplate(resultType)) {
			String fileName = ""; // 文件名
			// 判断文件存储类型
			if ((table.get("tempFileType") != null) && ((int) table.get("tempFileType") == 1)) {
				// 文件路径路径
				String path = table.get("tempFilePath").toString();
				fileName = path;
				if (fileName.indexOf("\\") >= 0) {
					fileName = fileName.substring(fileName.lastIndexOf("\\") + 1);
				} else if (fileName.indexOf("/") >= 0) {
					fileName = fileName.substring(fileName.lastIndexOf("/") + 1);
				}
			} else {
				// 数据库存储
				UploadFile file = FileManager.toFiles((byte[]) table.get("templateFile")).get(0);// 附件
				fileName = file.getName();
			}

			String pixel = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();// 后缀

			switch (resultType) {
			case PAGE: {
				Actions.showHtml(request, response, viewerViewService.executePage(table));
				return;
			}
			case WORD: {
				if (!"doc".equalsIgnoreCase(pixel) && !"docx".equalsIgnoreCase(pixel)) {
					fileName = fileName.substring(0, fileName.lastIndexOf(".")) + ".doc";
				}
				String html = viewerViewService.executePage(table);
				try (InputStream is = new ByteArrayInputStream(html.getBytes("utf-8"))) {
					Actions.download(request, response, fileName, is);
					return;
				} catch (IOException e) {
					throw new SystemRuntimeException(e);
				}
			}
			case EXCEL: {
				try (InputStream is = new ByteArrayInputStream(viewerViewService.getExcel(table).toByteArray())) {
					Actions.download(request, response, fileName, is);
					return;
				} catch (IOException e) {
					throw new SystemRuntimeException(e);
				}
			}
			case PDF: {
				try (ByteArrayOutputStream tmpOut = new ByteArrayOutputStream()) {
					InputStream webInputStream;
					if ("xls".equalsIgnoreCase(pixel) || "xlsx".equalsIgnoreCase(pixel)) {
						tmpOut.write(viewerViewService.getExcel(table).toByteArray());
					} else {
						String html = viewerViewService.executePage(table);
						tmpOut.write(html.getBytes("utf-8"));
					}

					try (ByteArrayInputStream tmpIs = new ByteArrayInputStream(tmpOut.toByteArray()); ByteArrayOutputStream realOut = new ByteArrayOutputStream();) {
						if (!ConverterHelper.convert(tmpIs, pixel, realOut, "pdf")) {
							throw new SystemRuntimeException(ExceptionType.WEB, "无法找到Office文档转换服务,请联系管理员处理..");
						}
						webInputStream = new ByteArrayInputStream(realOut.toByteArray());
					}
					Actions.download(request, response, fileName.substring(0, fileName.lastIndexOf(".")) + ".pdf", webInputStream);
					return;
				} catch (IOException e) {
					throw new SystemRuntimeException(e);
				}
			}
			default:
				throw new SystemRuntimeException(ExceptionType.WEB);
			}
		} else {
			switch (resultType) {
			case TEXT: {
				Actions.showText(request, response, (String) viewerViewService.executeText(table));
				return;
			}
			case MSG: {
				Object msg = viewerViewService.executeMsgPage(table);
				if (msg instanceof String) {
					Actions.redirectInfoPage(request, response, (String) msg);
					return;
				} else if (msg instanceof Map) {
					Map<String, Object> message = (Map<String, Object>) msg;
					if (message.containsKey("type") && message.containsKey("msg")) {
						String type = (String) message.get("type");
						String content = (String) message.get("msg");
						if ("info".equalsIgnoreCase(type)) {
							Actions.redirectInfoPage(request, response, content);
							return;
						} else if ("warning".equalsIgnoreCase(type)) {
							Actions.redirectWarningPage(request, response, content);
							return;
						} else if ("error".equalsIgnoreCase(type)) {
							Actions.redirectErrorPage(request, response, content);
							return;
						}
					}
					throw new SystemRuntimeException(ExceptionType.WEB, "配置信息有误,请联系管理员.");
				} else {
					throw new SystemRuntimeException(ExceptionType.WEB, "配置信息有误,请联系管理员.");
				}
			}
			case DOWNLOAD: {
				try {
					Object obj = viewerViewService.getFile(table);
					if (obj instanceof File) {
						try (InputStream webInputStream = new FileInputStream((File) obj);) {
							Actions.download(request, response, ((File) obj).getName(), webInputStream);
							return;
						}
					} else if (obj instanceof byte[]) {
						List<FileManager.UploadFile> fileList = FileManager.toFiles((byte[]) obj);
						if (fileList == null || fileList.size() < 1) {
							throw new SystemRuntimeException(ExceptionType.SCRIPT, "解析文件不存在.");
						}

						Actions.download(request, response, fileList.get(0).getName(), fileList.get(0).getInputStream());
						return;
					} else {
						throw new SystemRuntimeException(ExceptionType.WEB, "仅允许二进制与文件类型下载。");
					}

				} catch (Exception e) {
					throw new SystemRuntimeException(e);
				}
			}
			case REDIRECT: {
				Actions.redirectAction(request, response, (String) viewerViewService.executeRedirectUrl(table));
				return;
			}
			default:
				throw new SystemRuntimeException(ExceptionType.WEB);
			}
		}

	}

	private boolean hasTemplate(ResultType resultType) {
		return !(resultType == ResultType.TEXT || resultType == ResultType.MSG || resultType == ResultType.DOWNLOAD || resultType == ResultType.REDIRECT);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void configForm(HttpServletRequest request, HttpServletResponse response, String key) {
		if (!StringUtils.isEmpty(key)) {
			Map<String, Object> table = (Map<String, Object>) ORMService.getInstance().findByPk("VwViewer", key);
			request.setAttribute("table", table);
		}

		Actions.includePage(request, response, Actions.Util.getPagePath(request, "view_viewer_config.jsp"));
	}

	/**
	 * 变量配置
	 * 
	 * @param request
	 * @param response
	 */
	@SuppressWarnings("unchecked")
	public void varConfigForm(HttpServletRequest request, HttpServletResponse response) {
		String key = RequestUtils.getStringValue(request, "key");
		if (!StringUtils.isEmpty(key)) {
			Map<String, Object> table = (Map<String, Object>) ORMService.getInstance().findByPk("VwViewer", key);
			if (table != null) {
				request.setAttribute("vars", (Set<Map<String, Object>>) table.get("vars"));
			}
		}

		Actions.includePage(request, response, Actions.Util.getPagePath(request, "view_viewer_var_config.jsp"));
	}

	/**
	 * 增加变量
	 * 
	 * @param request
	 * @param response
	 */
	public void varsForm(HttpServletRequest request, HttpServletResponse response) {
		Actions.includePage(request, response, Util.getPagePath(request, "view_viewer_var_prepare.jsp"));
	}

	@Override
	public void saveConfig(String key) {
		// table部分
		DataPO tablePO = new DataPO("VwViewer");
		tablePO.set("viewKey", key);
		ViewerDataBuilder builder = new ViewerDataBuilder(tablePO);
		builder.build();
		ORMService.getInstance().save(tablePO.toEntity());

		builder.handleConfig();
	}

	@SuppressWarnings("unchecked")
	@Override
	public void updateConfig(String key) {
		Map<String, Object> entity = (Map<String, Object>) ORMService.getInstance().loadByPk("VwViewer", key);
		// table部分
		DataPO tablePO = new DataPO("VwViewer", entity);
		if (entity == null) {
			throw new SystemRuntimeException(ExceptionType.BUSINESS, "视图已删除.");
		}
		ViewerDataBuilder builder = new ViewerDataBuilder(tablePO);
		builder.handleConfig();
		builder.build();
		// 这里要使用update
		ORMService.getInstance().update(tablePO.toEntity());
	}

	@SuppressWarnings("unchecked")
	@Override
	public void removeConfig(String key) {
		Map<String, Object> table = (Map<String, Object>) ORMService.getInstance().findByPk("VwViewer", key);
		if (table != null) {
			ORMService.getInstance().remove(table);
		}
	}

	@Override
	public String copyConfig(String key) {
		throw new SystemRuntimeException(ExceptionType.BUSINESS, "此视图不支持复制.");
	}

}
