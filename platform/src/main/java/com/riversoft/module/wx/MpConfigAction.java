/*
 * $HeadURL: $
 * $Id: $
 * Copyright (c) 2015 by Riversoft System, all rights reserved.
 */
package com.riversoft.module.wx;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import com.riversoft.core.Config;
import com.riversoft.core.IDGenerator;
import com.riversoft.core.db.DataCondition;
import com.riversoft.core.db.DataPO;
import com.riversoft.core.db.DataPackage;
import com.riversoft.core.db.ORMService;
import com.riversoft.core.exception.ExceptionType;
import com.riversoft.core.exception.SystemRuntimeException;
import com.riversoft.core.script.function.FormatterFunction;
import com.riversoft.core.web.Actions;
import com.riversoft.core.web.Actions.Util;
import com.riversoft.core.web.RequestUtils;
import com.riversoft.platform.db.ORMAdapterService;
import com.riversoft.platform.db.model.ModelKeyUtils;
import com.riversoft.platform.po.TbTable;
import com.riversoft.platform.web.FileManager;
import com.riversoft.platform.web.WebLogManager;
import com.riversoft.weixin.common.media.MediaType;
import com.riversoft.weixin.mp.base.AppSetting;
import com.riversoft.weixin.mp.media.Materials;
import com.riversoft.weixin.mp.media.bean.Counts;
import com.riversoft.weixin.mp.media.bean.MaterialPagination;
import com.riversoft.weixin.mp.media.bean.MpNewsPagination;
import com.riversoft.weixin.mp.template.Industry;
import com.riversoft.weixin.mp.template.Template;
import com.riversoft.weixin.mp.template.Templates;
import com.riversoft.weixin.mp.user.Tags;
import com.riversoft.weixin.mp.user.Users;
import com.riversoft.weixin.mp.user.bean.Tag;
import com.riversoft.weixin.mp.user.bean.User;
import com.riversoft.weixin.mp.user.bean.UserPagination;
import com.riversoft.wx.mp.model.LocationReportLogModelKeys;
import com.riversoft.wx.mp.model.MessageLogModelKeys;
import com.riversoft.wx.mp.model.MpVisitorModelKeys;
import com.riversoft.wx.mp.model.MpVisitorTagModelKeys;
import com.riversoft.wx.mp.model.OrderModelKeys;
import com.riversoft.wx.mp.model.PaymentNotificationModelKeys;
import com.riversoft.wx.mp.model.TemplateMsgLogModelKeys;

/**
 * 应用管理
 * 
 * @author woden
 *
 */
public class MpConfigAction {

	/**
	 * 公众号管理首页
	 * 
	 * @param request
	 * @param response
	 */
	public void index(HttpServletRequest request, HttpServletResponse response) {
		Actions.includePage(request, response, Util.getPagePath(request, "mp_main.jsp"));
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

		DataPackage dp = ORMService.getInstance().queryPackage("WxMp", start, limit, condition.toEntity());
		// 设置到页面
		request.setAttribute("dp", dp);

		Actions.includePage(request, response, Util.getPagePath(request, "mp_list.jsp"));
	}

	/**
	 * 新增应用
	 * 
	 * @param request
	 * @param response
	 */
	public void createZone(HttpServletRequest request, HttpServletResponse response) {
		List<TbTable> visitorTables = new ArrayList<>();
		List<TbTable> visitorTagTables = new ArrayList<>();
		for (TbTable model : (List<TbTable>) ORMService.getInstance().queryAll(TbTable.class.getName())) {
			if (ModelKeyUtils.checkModel(MpVisitorModelKeys.class, model)) {
				visitorTables.add(model);
			}
			if (ModelKeyUtils.checkModel(MpVisitorTagModelKeys.class, model)) {
				visitorTagTables.add(model);
			}
		}
		request.setAttribute("visitorTables", visitorTables);
		request.setAttribute("visitorTagTables", visitorTagTables);

		Actions.includePage(request, response, Util.getPagePath(request, "mp_create.jsp"));
	}

	/**
	 * 提交保存应用
	 *
	 * @param request
	 * @param response
	 */
	public void submitCreate(HttpServletRequest request, HttpServletResponse response) {
		Map<String, Object> mp = MpConfigService.getInstance().executeCreate();
		String name = (String) mp.get("title");
		Actions.redirectInfoPage(request, response, "公众号[" + name + "]已新建.");
	}

	/**
	 * 删除应用
	 *
	 * @param request
	 * @param response
	 */
	public void removeMp(HttpServletRequest request, HttpServletResponse response) {
		Map<String, Object> mp = MpConfigService.getInstance().executeRemove();
		String name = (String) mp.get("title");
		ORMService.getInstance().remove(mp);
		Actions.redirectInfoPage(request, response, "公众号[" + name + "]已删除.");
	}

	/**
	 * 设置应用
	 *
	 * @param request
	 * @param response
	 */
	@SuppressWarnings("unchecked")
	public void editZone(HttpServletRequest request, HttpServletResponse response) {
		String mpKey = RequestUtils.getStringValue(request, "mpKey");
		Map<String, Object> vo = (Map<String, Object>) ORMService.getInstance().findByPk("WxMp", mpKey);
		request.setAttribute("vo", vo);

		Map<String, Object> subscribe = (Map<String, Object>) ORMService.getInstance().findByPk("WxMpSubscribe", mpKey);
		request.setAttribute("subscribe", subscribe);

		Map<String, Object> unsubscribe = (Map<String, Object>) ORMService.getInstance().findByPk("WxMpUnSubscribe", mpKey);
		request.setAttribute("unsubscribe", unsubscribe);

		Map<String, Object> scanin = (Map<String, Object>) ORMService.getInstance().findByPk("WxMpScanIn", mpKey);
		request.setAttribute("scanin", scanin);

		Map<String, Object> location = (Map<String, Object>) ORMService.getInstance().findByPk("WxMpLocation", mpKey);
		request.setAttribute("location", location);

		Map<String, Object> message = (Map<String, Object>) ORMService.getInstance().findByPk("WxMpMessage", mpKey);
		request.setAttribute("message", message);

		Map<String, Object> order = (Map<String, Object>) ORMService.getInstance().findByPk("WxMpOrder", mpKey);
		request.setAttribute("order", order);

		Map<String, Object> pay = (Map<String, Object>) ORMService.getInstance().findByPk("WxMpPay", mpKey);
		request.setAttribute("pay", pay);

		Map<String, Object> payNotify = (Map<String, Object>) ORMService.getInstance().findByPk("WxMpPayNotify", mpKey);
		request.setAttribute("payNotify", payNotify);

		List<TbTable> allTable = ORMService.getInstance().queryAll(TbTable.class.getName());
		Set<TbTable> locationLogTable = new HashSet<>();
		Set<TbTable> messageLogTable = new HashSet<>();
		List<TbTable> visitorTables = new ArrayList<>();
		List<TbTable> visitorTagTables = new ArrayList<>();
		List<TbTable> templateMsgLogTables = new ArrayList<>();
		List<TbTable> orderTables = new ArrayList<>();
		List<TbTable> payResultTables = new ArrayList<>();

		for (TbTable table : allTable) {
			if (ModelKeyUtils.checkModel(LocationReportLogModelKeys.class, table)) {
				locationLogTable.add(table);
			}
			if (ModelKeyUtils.checkModel(MessageLogModelKeys.class, table)) {
				messageLogTable.add(table);
			}
			if (ModelKeyUtils.checkModel(MpVisitorModelKeys.class, table)) {
				visitorTables.add(table);
			}
			if (ModelKeyUtils.checkModel(MpVisitorTagModelKeys.class, table)) {
				visitorTagTables.add(table);
			}
			if (ModelKeyUtils.checkModel(TemplateMsgLogModelKeys.class, table)) {
				templateMsgLogTables.add(table);
			}
			if (ModelKeyUtils.checkModel(OrderModelKeys.class, table)) {
				orderTables.add(table);
			}
			if (ModelKeyUtils.checkModel(PaymentNotificationModelKeys.class, table)) {
				payResultTables.add(table);
			}

		}
		request.setAttribute("locationLogTable", locationLogTable);
		request.setAttribute("messageLogTable", messageLogTable);
		request.setAttribute("visitorTables", visitorTables);
		request.setAttribute("visitorTagTables", visitorTagTables);
		request.setAttribute("templateMsgLogTables", templateMsgLogTables);
		request.setAttribute("orderTables", orderTables);
		request.setAttribute("payResultTables", payResultTables);

		Actions.includePage(request, response, Util.getPagePath(request, "mp_edit.jsp"));
	}

	/**
	 * 保存设置
	 *
	 * @param request
	 * @param response
	 */
	public void submitEdit(HttpServletRequest request, HttpServletResponse response) {
		Map<String, Object> mp = MpConfigService.getInstance().executeEdit();
		String name = (String) mp.get("title");
		Actions.redirectInfoPage(request, response, "公众号[" + name + "]保存设置成功.");
	}

	/**
	 * 同步公众号
	 *
	 * @param request
	 * @param response
	 */
	public void submitSync(HttpServletRequest request, HttpServletResponse response) {
		String[] mpKeys = RequestUtils.getStringValues(request, "mpKey");
		if (mpKeys == null || mpKeys.length < 1) {
			throw new SystemRuntimeException(ExceptionType.BUSINESS, "请勾选需要同步的公众号.");
		}

		List<String> failedList = new ArrayList<>();
		for (String mpKey : mpKeys) {
			if (!MpConfigService.getInstance().executeSync(mpKey)) {
				failedList.add(mpKey);
			}
		}

		String msg;
		if (failedList.size() > 0) {// 有失败
			msg = "部分公众号发布失败:[" + StringUtils.join(failedList, " ; ") + "]";
		} else {
			msg = "所勾选公众号已发布成功.";
		}

		Actions.redirectInfoPage(request, response, msg);
	}

	/**
	 * 菜单管理
	 *
	 * @param request
	 * @param response
	 */
	@SuppressWarnings("unchecked")
	public void setMenu(HttpServletRequest request, HttpServletResponse response) {
		String mpKey = RequestUtils.getStringValue(request, "mpKey");
		String cp = Util.getContextPath(request);
		Map<String, Object> mp = (Map<String, Object>) ORMService.getInstance().findByPk("WxMp", mpKey);
		List<Map<String, Object>> list = ORMService.getInstance().query("WxMpMenu", new DataCondition().setStringEqual("mpKey", mpKey).setOrderByAsc("sort").toEntity());
		List<Map<String, Object>> menus = new ArrayList<>();
		for (Map<String, Object> o : list) {
			if ((Integer) o.get("menuType") == 0) {// 文件夹
				o.put("icon", cp + "/css/icon/folder.png");
			} else {
				o.put("icon", cp + "/css/icon/tab.png");
			}

			menus.add(o);
		}
		request.setAttribute("mp", mp);
		request.setAttribute("menus", menus);
		Actions.includePage(request, response, Util.getPagePath(request, "mp_menu.jsp"));
	}

	/**
	 * 菜单设置
	 *
	 * @param request
	 * @param response
	 */
	@SuppressWarnings("unchecked")
	public void menuForm(HttpServletRequest request, HttpServletResponse response) {
		Integer isCreate = RequestUtils.getIntegerValue(request, "isCreate");
		String menuKey = RequestUtils.getStringValue(request, "menuKey");
		String mpKey = RequestUtils.getStringValue(request, "mpKey");
		Integer menuType = RequestUtils.getIntegerValue(request, "menuType");

		if (isCreate != 1) {
			Map<String, Object> vo = ((Map<String, Object>) ORMService.getInstance().findByPk("WxMpMenu", menuKey));
			if (vo == null) {
				throw new SystemRuntimeException(ExceptionType.BUSINESS, "菜单[" + menuKey + "]不存在.");
			}
			menuType = (Integer) vo.get("menuType");
			request.setAttribute("vo", vo);
		}
		request.setAttribute("mpKey", mpKey);
		request.setAttribute("menuKey", menuKey);
		request.setAttribute("menuType", menuType);
		String domain = (StringUtils.equalsIgnoreCase(Config.get("wx.net.https", "false"), "true") ? "https://" : "http://") + Config.get("wx.net.domain") + "/wx/url/";// 网址前缀
		request.setAttribute("menuUrl", domain + "mp_" + menuKey);

		Actions.includePage(request, response, Util.getPagePath(request, "mp_menu_form.jsp"));
	}

	/**
	 * 自动生成菜单ID
	 *
	 * @param request
	 * @param response
	 */
	public void getMenuKey(HttpServletRequest request, HttpServletResponse response) {
		Map<String, Object> result = new HashMap<>();
		result.put("menuKey", IDGenerator.next());
		Actions.showJson(request, response, result);
	}

	/**
	 * 资源管理
	 *
	 * @param request
	 * @param response
	 */
	public void resource(HttpServletRequest request, HttpServletResponse response) {
		Actions.includePage(request, response, Util.getPagePath(request, "mp_resource.jsp"));
	}

	/**
	 * 获取资源数量
	 *
	 * @param request
	 * @param response
	 */
	public void getResourceCount(HttpServletRequest request, HttpServletResponse response) {
		String mpKey = RequestUtils.getStringValue(request, "mpKey");
		Map<String, Object> config = (Map<String, Object>) ORMService.getInstance().findByPk("WxMp", mpKey);

		String appId = (String) config.get("appId");
		String appSecret = (String) config.get("appSecret");

		Counts counts = Materials.with(new AppSetting(appId, appSecret)).count();
		Actions.showJson(request, response, counts);
	}

	/**
	 * 资源列表
	 *
	 * @param request
	 * @param response
	 */
	public void listResource(HttpServletRequest request, HttpServletResponse response) {
		String mpKey = RequestUtils.getStringValue(request, "mpKey");
		String type = RequestUtils.getStringValue(request, "type");
		Map<String, Object> config = (Map<String, Object>) ORMService.getInstance().findByPk("WxMp", mpKey);

		int start = Util.getStart(request);
		int limit = Util.getLimit(request);

		String appId = (String) config.get("appId");
		String appSecret = (String) config.get("appSecret");

		DataPackage dp = new DataPackage();
		if (MediaType.valueOf(type) == MediaType.news) {
			MpNewsPagination result = Materials.with(new AppSetting(appId, appSecret)).listMpNews(start, limit);
			dp.setTotalRecord(result.getTotalCount());
			dp.setList(result.getItems());
		} else {
			MaterialPagination result = Materials.with(new AppSetting(appId, appSecret)).list(MediaType.valueOf(type), start, limit);
			dp.setTotalRecord(result.getTotalCount());
			dp.setList(result.getItems());
		}

		dp.setLimit(limit);
		dp.setStart(start);

		request.setAttribute("dp", dp);
		request.setAttribute("type", type);
		Actions.includePage(request, response, Util.getPagePath(request, "mp_resource_list.jsp"));
	}

	/**
	 * 下载资源
	 *
	 * @param request
	 * @param response
	 */
	public void downloadResource(HttpServletRequest request, HttpServletResponse response) {
		String mpKey = RequestUtils.getStringValue(request, "mpKey");
		String mediaType = RequestUtils.getStringValue(request, "mediaType");
		String mediaId = RequestUtils.getStringValue(request, "mediaId");
		String fileName = RequestUtils.getStringValue(request, "fileName");
		Map<String, Object> config = (Map<String, Object>) ORMService.getInstance().findByPk("WxMp", mpKey);

		String appId = (String) config.get("appId");
		String appSecret = (String) config.get("appSecret");

		InputStream is = null;
		switch (MediaType.valueOf(mediaType)) {
		case image:
			is = Materials.with(new AppSetting(appId, appSecret)).getImage(mediaId);
			break;
		case voice:
			is = Materials.with(new AppSetting(appId, appSecret)).getVoice(mediaId);
			break;
		case video:
			String url = Materials.with(new AppSetting(appId, appSecret)).getVideo(mediaId).getUrl();
			break;
		case news:
			break;
		default:
			break;
		}
		if (is != null) {
			Actions.download(request, response, fileName, is);
		} else {
			throw new SystemRuntimeException(ExceptionType.BUSINESS, "当前资源不支持直接下载:" + mediaType + "|" + mediaId);
		}
	}

	/**
	 * 删除永久资源
	 *
	 * @param request
	 * @param response
	 */
	public void deleteResource(HttpServletRequest request, HttpServletResponse response) {
		String mpKey = RequestUtils.getStringValue(request, "mpKey");
		String mediaId = RequestUtils.getStringValue(request, "mediaId");
		Map<String, Object> config = (Map<String, Object>) ORMService.getInstance().findByPk("WxMp", mpKey);

		String appId = (String) config.get("appId");
		String appSecret = (String) config.get("appSecret");

		Materials.with(new AppSetting(appId, appSecret)).delete(mediaId);
		Actions.redirectInfoPage(request, response, "删除成功.");
	}

	/**
	 * 创建资源
	 *
	 * @param request
	 * @param response
	 */
	public void addResource(HttpServletRequest request, HttpServletResponse response) {
		String fileSize = Config.get("office.upload.size");
		String filePixel = Config.get("office.upload.pixel");

		request.setAttribute("fileSize", fileSize);
		request.setAttribute("filePixel", filePixel);

		Actions.includePage(request, response, Util.getPagePath(request, "mp_resource_create.jsp"));
	}

	/**
	 * 上传资源
	 *
	 * @param request
	 * @param response
	 */
	@SuppressWarnings("unchecked")
	public void submitResource(HttpServletRequest request, HttpServletResponse response) {
		String mpKey = RequestUtils.getStringValue(request, "mpKey");
		Map<String, Object> config = (Map<String, Object>) ORMService.getInstance().findByPk("WxMp", mpKey);

		String appId = (String) config.get("appId");
		String appSecret = (String) config.get("appSecret");
		AppSetting appSetting = new AppSetting(appId, appSecret);

		Map<String, Object> result = new HashMap<>();
		try {
			Part part = request.getPart("file");
			String fileName = RequestUtils.getStringValue(request, "name");
			Integer chunks = RequestUtils.getIntegerValue(request, "chunks");
			Integer chunk = RequestUtils.getIntegerValue(request, "chunk");
			File dir = FileManager.getCurrentUserTempSpace();
			File tmpFile = new File(dir, fileName + ".part");

			if (chunk == 0 && tmpFile.exists()) {// 以前存在
				tmpFile.delete();
			}
			if (!tmpFile.exists()) {
				tmpFile.createNewFile();
			}

			try (FileOutputStream fos = new FileOutputStream(tmpFile, true); BufferedInputStream bis = new BufferedInputStream(part.getInputStream());) {
				int BUFFER_SIZE = 1024;
				byte[] buf = new byte[BUFFER_SIZE];
				int size = 0;
				while ((size = bis.read(buf)) != -1) {
					fos.write(buf, 0, size);
				}
				result.put("reset", false);// 传成功了,就算出问题也不需要重置
			} catch (IOException ex) {
				result.put("reset", true);// 传一半出问题,重置
			}

			if (chunk == chunks - 1) {// 最后一个
				String extName = fileName;
				extName = extName.substring(extName.lastIndexOf(".") + 1);

				if (ArrayUtils.contains(StringUtils.split("png|jpg|jpeg", "|"), extName)) {
					Materials.with(appSetting).addImage(new FileInputStream(tmpFile), fileName);
				} else if (ArrayUtils.contains(StringUtils.split("mp4|avi|rmvb", "|"), extName)) {
					// TODO:视频需要title和description
					Materials.with(appSetting).addVideo(new FileInputStream(tmpFile), fileName, "title", "description");
				} else if (ArrayUtils.contains(StringUtils.split("mp3|wma|wav|amr", "|"), extName)) {
					Materials.with(appSetting).addVoice(new FileInputStream(tmpFile), fileName);
				} else {
					throw new SystemRuntimeException(ExceptionType.BUSINESS, "文件[" + fileName + "]类型不支持上传到微信.");
				}

				result.put("fileName", fileName);
			}

			if (chunk == 60) {
				// throw new ServletException();
			}

			result.put("code", 0);
			result.put("info", "上传成功.");
			Actions.showJson(request, response, result);

		} catch (IOException | ServletException e) {
			result.put("code", -1);
			result.put("info", "上传文件失败成功.");
			Actions.showJson(request, response, result);
		}
	}

	/**
	 * 模板消息管理
	 *
	 * @param request
	 * @param response
	 */
	public void templateMsg(HttpServletRequest request, HttpServletResponse response) {
		String mpKey = RequestUtils.getStringValue(request, "mpKey");
		Map<String, Object> config = (Map<String, Object>) ORMService.getInstance().findByPk("WxMp", mpKey);
		String appId = (String) config.get("appId");
		String appSecret = (String) config.get("appSecret");
		AppSetting appSetting = new AppSetting(appId, appSecret);

		Industry industry = Templates.with(appSetting).getIndustries();
		request.setAttribute("industry", industry);

		List<Template> templates = Templates.with(appSetting).list();
		request.setAttribute("templates", templates);

		Actions.includePage(request, response, Util.getPagePath(request, "mp_template_msg.jsp"));
	}

	/**
	 * 人员管理
	 * 
	 * @param request
	 * @param response
	 */
	public void visitorMain(HttpServletRequest request, HttpServletResponse response) {
		String mpKey = RequestUtils.getStringValue(request, "mpKey");
		Map<String, Object> config = (Map<String, Object>) ORMService.getInstance().findByPk("WxMp", mpKey);

		String visitorTagTable = (String) config.get("visitorTagTable");
		List<?> tags = ORMAdapterService.getInstance().query(visitorTagTable, new DataCondition().setOrderByAsc(MpVisitorTagModelKeys.TAG_ID.name()).toEntity());
		request.setAttribute("tags", tags);

		Actions.includePage(request, response, Util.getPagePath(request, "mp_visitor_main.jsp"));
	}

	/**
	 * 人员列表
	 * 
	 * @param request
	 * @param response
	 */
	public void listVisitor(HttpServletRequest request, HttpServletResponse response) {
		String mpKey = RequestUtils.getStringValue(request, "mpKey");
		Map<String, Object> config = (Map<String, Object>) ORMService.getInstance().findByPk("WxMp", mpKey);

		// 获取分页信息
		int start = Util.getStart(request);
		int limit = Util.getLimit(request);

		// 获取排序信息
		String field = Util.getSortField(request);
		String dir = Util.getSortDir(request);

		DataCondition condition = new DataCondition(Util.buildQueryMap(new HashMap<String, Object>(), request));
		condition.setOrderBy(field, dir);

		String[] tags = RequestUtils.getStringValues(request, "tags");
		if (tags != null && tags.length > 0) {
			condition.setStringLike(MpVisitorModelKeys.TAGS.name(), buildTagsLike(tags));
		}

		String visitorTable = (String) config.get("visitorTable");
		DataPackage dp = ORMAdapterService.getInstance().queryPackage(visitorTable, start, limit, condition.toEntity());
		request.setAttribute("dp", dp);

		String visitorTagTable = (String) config.get("visitorTagTable");
		request.setAttribute("tagWidget", "multiselect[#" + visitorTagTable + ";" + MpVisitorTagModelKeys.TAG_ID.name() + ";" + MpVisitorTagModelKeys.TAG_NAME.name() + "]");

		Actions.includePage(request, response, Util.getPagePath(request, "mp_visitor_list.jsp"));
	}

	private String buildTagsLike(String[] tags) {
		Integer[] integers = new Integer[tags.length];
		int i = 0;
		for (String tag : tags) {
			integers[i] = Integer.valueOf(tag);
			i++;
		}
		Arrays.sort(integers);
		StringBuffer stringBuffer = new StringBuffer();
		for (Integer tag : integers) {
			stringBuffer.append("%[").append(tag).append("]%");
		}
		return stringBuffer.toString();
	}

	/**
	 * 人员全量同步(不起大事务)
	 * 
	 * @param request
	 * @param response
	 */
	public void syncVisitor(HttpServletRequest request, HttpServletResponse response) {
		String mpKey = RequestUtils.getStringValue(request, "mpKey");

		Map<String, Object> config = ((Map<String, Object>) ORMService.getInstance().findByPk("WxMp", mpKey));
		if (config == null) {
			throw new SystemRuntimeException(ExceptionType.BUSINESS, "公众号[" + mpKey + "]不存在.");
		}

		ORMAdapterService service = ORMAdapterService.getInstance();

		String visitorTable = (String) config.get("visitorTable");
		String visitorTagTable = (String) config.get("visitorTagTable");

		String appId = (String) config.get("appId");
		String appSecret = (String) config.get("appSecret");
		AppSetting appSetting = new AppSetting(appId, appSecret);

		// 获取分组
		WebLogManager.log("正在同步[人员标签].");
		List<Tag> tagList = Tags.with(appSetting).list();
		WebLogManager.beginLoop("正在同步[人员标签].", tagList.size());
		for (Tag tag : tagList) {
			WebLogManager.signalLoop();
			Map<String, Object> o = (Map<String, Object>) service.findByPk(visitorTagTable, tag.getId());
			boolean createFlag = false;
			if (o == null) {
				o = new DataPO(visitorTagTable).toEntity();
				o.put(MpVisitorTagModelKeys.TAG_ID.name(), tag.getId());
				o.put(MpVisitorTagModelKeys.MP_KEY.name(), mpKey);
				createFlag = true;
			}
			o.put(MpVisitorTagModelKeys.TAG_NAME.name(), tag.getName());
			o.put(MpVisitorTagModelKeys.SYNC_TIME.name(), new Date());
			if (createFlag) {
				service.save(o);
			} else {
				service.update(o);
			}
		}

		WebLogManager.log("正在同步[人员].");
		Users users = Users.with(appSetting);
		UserPagination pagination = users.list();
		WebLogManager.beginLoop("正在同步[人员标签].", pagination.getTotal());
		saveOrUpdateVisitor(mpKey, users, visitorTable, pagination);

		while (pagination != null && StringUtils.isNotEmpty(pagination.getNextOpenId())) {
			pagination = users.list(pagination.getNextOpenId());
			saveOrUpdateVisitor(mpKey, users, visitorTable, pagination);
		}

		Actions.redirectInfoPage(request, response, "同步成功.");
	}

	/**
	 * 同步用户
	 * 
	 * @param mpKey
	 * @param users
	 * @param visitorTable
	 * @param pagination
	 */
	private void saveOrUpdateVisitor(String mpKey, Users users, String visitorTable, UserPagination pagination) {
		if (pagination.getCount() < 1) {
			return;
		}

		ORMAdapterService service = ORMAdapterService.getInstance();
		String[] allUsers = pagination.getUsers().toArray(new String[0]);
		for (int i = 0; i < allUsers.length; i += 100) {
			int end = i + 100;
			if (end >= pagination.getCount()) {
				end = pagination.getCount();
			}
			String[] openIds = ArrayUtils.subarray(allUsers, i, end);
			List<User> userList = users.batchGet(openIds);

			for (User u : userList) {
				WebLogManager.signalLoop();
				Map<String, Object> o = (Map<String, Object>) service.findByPk(visitorTable, u.getOpenId());
				boolean createFlag = false;
				if (o == null) {
					o = new DataPO(visitorTable).toEntity();
					o.put(MpVisitorModelKeys.MP_KEY.name(), mpKey);
					o.put(MpVisitorModelKeys.OPEN_ID.name(), u.getOpenId());
					o.put(MpVisitorModelKeys.CREATE_TIME.name(), new Date()); // 新增时增加创建时间
					createFlag = true;
				}
				o.put(MpVisitorModelKeys.NICK_NAME.name(), u.getNickName());//
				o.put(MpVisitorModelKeys.HEAD_IMG_URL.name(), u.getHeadImgUrl());
				o.put(MpVisitorModelKeys.COUNTRY.name(), u.getCountry());
				o.put(MpVisitorModelKeys.PROVINCE.name(), u.getProvince());
				o.put(MpVisitorModelKeys.CITY.name(), u.getCity());
				o.put(MpVisitorModelKeys.SEX.name(), u.getSex() != null ? u.getSex().getCode() : 0);
				o.put(MpVisitorModelKeys.SUBSCRIBE.name(), u.isSubscribed() ? 1 : 0);
				o.put(MpVisitorModelKeys.SUBSCRIBE_TIME.name(), u.getSubscribedTime());
				o.put(MpVisitorModelKeys.LANGUAGE.name(), u.getLanguage());
				List<Integer> tags = u.getTags();
				if (tags != null && !tags.isEmpty()) {
					o.put(MpVisitorModelKeys.TAGS.name(), buildTags(tags));
				}
				o.put(MpVisitorModelKeys.UNION_ID.name(), u.getUnionId());
				o.put(MpVisitorModelKeys.REMARK.name(), u.getRemark());
				System.out.println(FormatterFunction.formatJson(o));
				if (createFlag) {
					service.save(o);
				} else {
					service.update(o);
				}
			}
		}
	}

	private String buildTags(List<Integer> tags) {
		Object[] integers = tags.toArray();
		Arrays.sort(integers);
		StringBuffer sb = new StringBuffer();
		for (Object tag : integers) {
			sb.append("[").append(tag).append("]").append(";");
		}
		sb.deleteCharAt(sb.length() - 1);
		return sb.toString();
	}
}
