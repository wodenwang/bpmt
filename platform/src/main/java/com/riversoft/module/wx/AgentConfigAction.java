/*
 * $HeadURL: $
 * $Id: $
 * Copyright (c) 2015 by Riversoft System, all rights reserved.
 */
package com.riversoft.module.wx;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
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
import com.riversoft.core.db.DataPackage;
import com.riversoft.core.db.ORMService;
import com.riversoft.core.exception.ExceptionType;
import com.riversoft.core.exception.SystemRuntimeException;
import com.riversoft.core.web.Actions;
import com.riversoft.core.web.Actions.Util;
import com.riversoft.core.web.RequestUtils;
import com.riversoft.platform.db.model.ModelKeyUtils;
import com.riversoft.platform.po.TbTable;
import com.riversoft.platform.po.UsUser;
import com.riversoft.platform.web.FileManager;
import com.riversoft.weixin.common.WxClient;
import com.riversoft.weixin.common.media.MediaType;
import com.riversoft.weixin.qy.agent.Agents;
import com.riversoft.weixin.qy.agent.bean.Agent;
import com.riversoft.weixin.qy.base.CorpSetting;
import com.riversoft.weixin.qy.base.WxEndpoint;
import com.riversoft.weixin.qy.media.Materials;
import com.riversoft.weixin.qy.media.bean.Counts;
import com.riversoft.weixin.qy.media.bean.MaterialPagination;
import com.riversoft.weixin.qy.media.bean.MpNewsPagination;
import com.riversoft.wx.qy.model.EnterAgentLogModelKeys;
import com.riversoft.wx.qy.model.LocationReportLogModelKeys;
import com.riversoft.wx.qy.model.MessageLogModelKeys;
import com.riversoft.wx.qy.service.ContactService;

/**
 * 应用管理
 * 
 * @author woden
 *
 */
public class AgentConfigAction {

	/**
	 * 获取配置
	 * 
	 * @param config
	 * @return
	 */
	private CorpSetting createAgentSetting(Map<String, Object> config) {
		String corpId = Config.get("wx.qy.corpId");
		String secret = StringUtils.isNotEmpty((String) config.get("agentSecret")) ? (String) config.get("agentSecret") : Config.get("wx.qy.corpSecret");
		return new CorpSetting(corpId, secret);

	}

	/**
	 * 应用管理首页
	 * 
	 * @param request
	 * @param response
	 */
	public void index(HttpServletRequest request, HttpServletResponse response) {
		Actions.includePage(request, response, Util.getPagePath(request, "agent_main.jsp"));
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

		DataPackage dp = ORMService.getInstance().queryPackage("WxAgent", start, limit, condition.toEntity());
		// 设置到页面
		request.setAttribute("dp", dp);

		Actions.includePage(request, response, Util.getPagePath(request, "agent_list.jsp"));
	}

	/**
	 * 新增应用
	 * 
	 * @param request
	 * @param response
	 */
	public void createZone(HttpServletRequest request, HttpServletResponse response) {
		Actions.includePage(request, response, Util.getPagePath(request, "agent_create.jsp"));
	}

	/**
	 * 校验应用新增
	 * 
	 * @param request
	 * @param response
	 */
	@SuppressWarnings("unchecked")
	public void checkCreate(HttpServletRequest request, HttpServletResponse response) {
		Integer agentId = RequestUtils.getIntegerValue(request, "agentId");
		String agentSecret = RequestUtils.getStringValue(request, "agentSecret");

		// 找agent
		Map<String, Object> config = ((Map<String, Object>) ORMService.getInstance().findByKey("WxAgent", "agentId", agentId));
		if (config != null) {
			throw new SystemRuntimeException(ExceptionType.BUSINESS, "应用[" + agentId + "]已存在,无法重复添加");
		}

		Agent agent = Agents.with(new CorpSetting(Config.get("wx.qy.corpId"), agentSecret)).get(agentId);
		Map<String, Object> result = new HashMap<>();
		result.put("flag", true);
		result.put("agent", agent);
		Actions.showJson(request, response, result);
	}

	/**
	 * 提交保存应用
	 * 
	 * @param request
	 * @param response
	 */
	public void submitCreate(HttpServletRequest request, HttpServletResponse response) {
		Map<String, Object> agent = AgentConfigService.getInstance().executeCreate();
		String name = (String) agent.get("title");
		Actions.redirectInfoPage(request, response, "应用[" + name + "]已新建.");
	}

	/**
	 * 删除应用
	 * 
	 * @param request
	 * @param response
	 */
	public void removeAgent(HttpServletRequest request, HttpServletResponse response) {
		Map<String, Object> agent = AgentConfigService.getInstance().executeRemove();
		String name = (String) agent.get("title");
		ORMService.getInstance().remove(agent);
		Actions.redirectInfoPage(request, response, "应用[" + name + "]已删除.");
	}

	/**
	 * 设置应用
	 * 
	 * @param request
	 * @param response
	 */
	@SuppressWarnings("unchecked")
	public void editZone(HttpServletRequest request, HttpServletResponse response) {
		String agentKey = RequestUtils.getStringValue(request, "agentKey");
		Map<String, Object> vo = (Map<String, Object>) ORMService.getInstance().findByPk("WxAgent", agentKey);
		request.setAttribute("vo", vo);

		Map<String, Object> subscribe = (Map<String, Object>) ORMService.getInstance().findByPk("WxAgentSubscribe", agentKey);
		request.setAttribute("subscribe", subscribe);

		Map<String, Object> unsubscribe = (Map<String, Object>) ORMService.getInstance().findByPk("WxAgentUnSubscribe", agentKey);
		request.setAttribute("unsubscribe", unsubscribe);

		Map<String, Object> enter = (Map<String, Object>) ORMService.getInstance().findByPk("WxAgentEnter", agentKey);
		request.setAttribute("enter", enter);

		Map<String, Object> location = (Map<String, Object>) ORMService.getInstance().findByPk("WxAgentLocation", agentKey);
		request.setAttribute("location", location);

		Map<String, Object> message = (Map<String, Object>) ORMService.getInstance().findByPk("WxAgentMessage", agentKey);
		request.setAttribute("message", message);

		List<TbTable> tables = ORMService.getInstance().queryAll(TbTable.class.getName());
		Set<TbTable> enterLogTable = new HashSet<>();
		Set<TbTable> locationLogTable = new HashSet<>();
		Set<TbTable> messageLogTable = new HashSet<>();
		for (TbTable table : tables) {
			if (ModelKeyUtils.checkModel(EnterAgentLogModelKeys.class, table)) {
				enterLogTable.add(table);
			}
			if (ModelKeyUtils.checkModel(LocationReportLogModelKeys.class, table)) {
				locationLogTable.add(table);
			}
			if (ModelKeyUtils.checkModel(MessageLogModelKeys.class, table)) {
				messageLogTable.add(table);
			}
		}
		request.setAttribute("enterLogTable", enterLogTable);
		request.setAttribute("locationLogTable", locationLogTable);
		request.setAttribute("messageLogTable", messageLogTable);

		Actions.includePage(request, response, Util.getPagePath(request, "agent_edit.jsp"));
	}

	/**
	 * 保存设置
	 * 
	 * @param request
	 * @param response
	 */
	public void submitEdit(HttpServletRequest request, HttpServletResponse response) {
		Map<String, Object> agent = AgentConfigService.getInstance().executeEdit();
		String name = (String) agent.get("title");
		Actions.redirectInfoPage(request, response, "应用[" + name + "]保存设置成功.");
	}

	/**
	 * 同步应用
	 * 
	 * @param request
	 * @param response
	 */
	public void submitSync(HttpServletRequest request, HttpServletResponse response) {
		String[] agentKeys = RequestUtils.getStringValues(request, "agentKey");
		if (agentKeys == null || agentKeys.length < 1) {
			throw new SystemRuntimeException(ExceptionType.BUSINESS, "请勾选需要同步的应用.");
		}

		List<String> faildList = new ArrayList<>();
		for (String agentKey : agentKeys) {
			if (!AgentConfigService.getInstance().executeSync(agentKey)) {
				faildList.add(agentKey);
			}
		}

		String msg;
		if (faildList.size() > 0) {// 有失败
			msg = "部分应用发布失败:[" + StringUtils.join(faildList, " ; ") + "]";
		} else {
			msg = "所勾选应用已发布成功.";
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
		String agentKey = RequestUtils.getStringValue(request, "agentKey");
		String cp = Util.getContextPath(request);
		Map<String, Object> agent = (Map<String, Object>) ORMService.getInstance().findByPk("WxAgent", agentKey);
		List<Map<String, Object>> list = ORMService.getInstance().query("WxAgentMenu", new DataCondition().setStringEqual("agentKey", agentKey).setOrderByAsc("sort").toEntity());
		List<Map<String, Object>> menus = new ArrayList<>();
		for (Map<String, Object> o : list) {
			if ((Integer) o.get("menuType") == 0) {// 文件夹
				o.put("icon", cp + "/css/icon/folder.png");
			} else {
				o.put("icon", cp + "/css/icon/tab.png");
			}

			menus.add(o);
		}
		request.setAttribute("agent", agent);
		request.setAttribute("menus", menus);
		Actions.includePage(request, response, Util.getPagePath(request, "agent_menu.jsp"));
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
		String agentKey = RequestUtils.getStringValue(request, "agentKey");
		Integer menuType = RequestUtils.getIntegerValue(request, "menuType");

		if (isCreate != 1) {
			Map<String, Object> vo = ((Map<String, Object>) ORMService.getInstance().findByPk("WxAgentMenu", menuKey));
			if (vo == null) {
				throw new SystemRuntimeException(ExceptionType.BUSINESS, "菜单[" + menuKey + "]不存在.");
			}
			menuType = (Integer) vo.get("menuType");
			request.setAttribute("vo", vo);
		}
		request.setAttribute("agentKey", agentKey);
		request.setAttribute("menuKey", menuKey);
		request.setAttribute("menuType", menuType);
		String domain = (StringUtils.equalsIgnoreCase(Config.get("wx.net.https", "false"), "true") ? "https://" : "http://") + Config.get("wx.net.domain") + "/wx/url/";// 网址前缀
		request.setAttribute("menuUrl", domain + "qy_" + menuKey);

		Actions.includePage(request, response, Util.getPagePath(request, "agent_menu_form.jsp"));
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
	@Deprecated
	public void resource(HttpServletRequest request, HttpServletResponse response) {
		Actions.includePage(request, response, Util.getPagePath(request, "agent_resource.jsp"));
	}

	/**
	 * 获取资源数量
	 * 
	 * @param request
	 * @param response
	 */
	@Deprecated
	public void getResourceCount(HttpServletRequest request, HttpServletResponse response) {
		String agentKey = RequestUtils.getStringValue(request, "agentKey");
		Map<String, Object> config = (Map<String, Object>) ORMService.getInstance().findByPk("WxAgent", agentKey);
		Counts counts = Materials.defaultMaterials().count((Integer) config.get("agentId"));
		Actions.showJson(request, response, counts);
	}

	/**
	 * 资源列表
	 * 
	 * @param request
	 * @param response
	 */
	@Deprecated
	public void listResource(HttpServletRequest request, HttpServletResponse response) {
		String agentKey = RequestUtils.getStringValue(request, "agentKey");
		String type = RequestUtils.getStringValue(request, "type");
		Map<String, Object> config = (Map<String, Object>) ORMService.getInstance().findByPk("WxAgent", agentKey);

		int start = Util.getStart(request);
		int limit = Util.getLimit(request);

		DataPackage dp = new DataPackage();

		if (MediaType.valueOf(type) == MediaType.mpnews) {
			MpNewsPagination result = Materials.defaultMaterials().listMpNews((Integer) config.get("agentId"), start, limit);
			dp.setTotalRecord(result.getTotalCount());
			dp.setList(result.getItems());
		} else {
			MaterialPagination result = Materials.defaultMaterials().list((Integer) config.get("agentId"), MediaType.valueOf(type), start, limit);
			dp.setTotalRecord(result.getTotalCount());
			dp.setList(result.getItems());
		}

		dp.setLimit(limit);
		dp.setStart(start);

		request.setAttribute("dp", dp);
		request.setAttribute("type", type);
		Actions.includePage(request, response, Util.getPagePath(request, "agent_resource_list.jsp"));
	}

	/**
	 * 下载资源
	 * 
	 * @param request
	 * @param response
	 */
	@Deprecated
	public void downloadResource(HttpServletRequest request, HttpServletResponse response) {
		String agentKey = RequestUtils.getStringValue(request, "agentKey");
		String mediaId = RequestUtils.getStringValue(request, "mediaId");
		Map<String, Object> config = (Map<String, Object>) ORMService.getInstance().findByPk("WxAgent", agentKey);
		File file = Materials.defaultMaterials().download((Integer) config.get("agentId"), mediaId);

		try (InputStream is = new FileInputStream(file)) {
			Actions.download(request, response, file.getName(), is);
		} catch (IOException e) {
			throw new SystemRuntimeException(e);
		}
	}

	/**
	 * 删除永久资源
	 * 
	 * @param request
	 * @param response
	 */
	@Deprecated
	public void deleteResource(HttpServletRequest request, HttpServletResponse response) {
		String agentKey = RequestUtils.getStringValue(request, "agentKey");
		String mediaId = RequestUtils.getStringValue(request, "mediaId");
		Map<String, Object> config = (Map<String, Object>) ORMService.getInstance().findByPk("WxAgent", agentKey);
		Materials.defaultMaterials().delete((Integer) config.get("agentId"), mediaId);
		Actions.redirectInfoPage(request, response, "删除成功.");
	}

	/**
	 * 创建资源
	 * 
	 * @param request
	 * @param response
	 */
	@Deprecated
	public void addResource(HttpServletRequest request, HttpServletResponse response) {
		String fileSize = Config.get("office.upload.size");
		String filePixel = Config.get("office.upload.pixel");

		request.setAttribute("fileSize", fileSize);
		request.setAttribute("filePixel", filePixel);

		Actions.includePage(request, response, Util.getPagePath(request, "agent_resource_create.jsp"));
	}

	/**
	 * 上传资源
	 * 
	 * @param request
	 * @param response
	 */
	@SuppressWarnings("unchecked")
	@Deprecated
	public void submitResource(HttpServletRequest request, HttpServletResponse response) {
		String agentKey = RequestUtils.getStringValue(request, "agentKey");
		Map<String, Object> config = (Map<String, Object>) ORMService.getInstance().findByPk("WxAgent", agentKey);

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
				MediaType type;

				if (ArrayUtils.contains(StringUtils.split("png|jpg|jpeg", "|"), extName)) {
					type = MediaType.image;
				} else if (ArrayUtils.contains(StringUtils.split("mp4|avi|rmvb", "|"), extName)) {
					type = MediaType.video;
				} else if (ArrayUtils.contains(StringUtils.split("mp3|wma|wav|amr", "|"), extName)) {
					type = MediaType.voice;
				} else if (ArrayUtils.contains(StringUtils.split("doc|docx|ppt|pptx|xls|xlsx|txt|zip|xml|pdf", "|"), extName)) {
					type = MediaType.file;
				} else {
					throw new SystemRuntimeException(ExceptionType.BUSINESS, "文件[" + fileName + "]类型不支持上传到微信.");
				}

				try {
					Materials.defaultMaterials().upload((Integer) config.get("agentId"), type, new FileInputStream(tmpFile), fileName);
				} catch (FileNotFoundException e) {
					throw new SystemRuntimeException(e);
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

	// ==企业号设置
	/**
	 * 控制台首页
	 * 
	 * @param request
	 * @param response
	 */
	public void config(HttpServletRequest request, HttpServletResponse response) {
		Map<String, Object> config = new HashMap<>();

		config.put("wx.qy.corpId", Config.get("wx.qy.corpId", ""));
		config.put("wx.qy.corpSecret", Config.get("wx.qy.corpSecret", ""));
		config.put("wx.qy.flag", Config.get("wx.qy.flag", "true").equalsIgnoreCase("true") ? 1 : 0);
		config.put("wx.qy.contactmode", Config.get("wx.qy.contactmode"));

		config.put("wx.qy.pay.flag", Config.get("wx.qy.pay.flag", "true").equalsIgnoreCase("true") ? 1 : 0);
		config.put("wx.qy.pay.mchId", Config.get("wx.qy.pay.mchId", ""));
		config.put("wx.qy.pay.key", Config.get("wx.qy.pay.key", ""));
		config.put("wx.qy.pay.certPath", Config.get("wx.qy.pay.certPath", ""));
		config.put("wx.qy.pay.certPassword", Config.get("wx.qy.pay.certPassword", ""));

		request.setAttribute("config", config);
		Actions.includePage(request, response, Util.getPagePath(request, "config.jsp"));
	}

	/**
	 * 校验corpId和corpSecret是否正确
	 * 
	 * @param request
	 * @param response
	 */
	public void touch(HttpServletRequest request, HttpServletResponse response) {
		String corpId = RequestUtils.getStringValue(request, "corpId");
		String corpSecret = RequestUtils.getStringValue(request, "corpSecret");

		int count = getAgentsCount(corpId, corpSecret);
		Actions.redirectInfoPage(request, response, "连接成功,您的企业号有[" + count + "]个应用.");
	}

	/**
	 * 保存公共配置
	 * 
	 * @param request
	 * @param response
	 */
	public void saveConfig(HttpServletRequest request, HttpServletResponse response) {
		String corpId = RequestUtils.getStringValue(request, "wx.qy.corpId");
		String corpSecret = RequestUtils.getStringValue(request, "wx.qy.corpSecret");

		Config.set("wx.qy.corpId", corpId);
		Config.set("wx.qy.corpSecret", corpSecret);
		Config.set("wx.qy.flag", RequestUtils.getIntegerValue(request, "wx.qy.flag").intValue() == 1 ? "true" : "false");
		Config.set("wx.qy.contactmode", RequestUtils.getStringValue(request, "wx.qy.contactmode"));

		Config.set("wx.qy.pay.flag", RequestUtils.getIntegerValue(request, "wx.qy.pay.flag").intValue() == 1 ? "true" : "false");
		Config.set("wx.qy.pay.mchId", RequestUtils.getStringValue(request, "wx.qy.pay.mchId"));
		Config.set("wx.qy.pay.key", RequestUtils.getStringValue(request, "wx.qy.pay.key"));
		Config.set("wx.qy.pay.certPath", RequestUtils.getStringValue(request, "wx.qy.pay.certPath"));
		Config.set("wx.qy.pay.certPassword", RequestUtils.getStringValue(request, "wx.qy.pay.certPassword"));

		Config.store("wx");// 保存

		CorpSetting defaultSettings = new CorpSetting(corpId, corpSecret);
		CorpSetting.setDefault(defaultSettings);

		Actions.redirectInfoPage(request, response, "保存成功.");
	}

	private int getAgentsCount(String corpId, String corpSecret) {
		String tokenUrl = WxEndpoint.get("url.token.get");
		WxClient wxClient = new WxClient(tokenUrl, corpId, corpSecret);
		Agents agents = new Agents();
		agents.setWxClient(wxClient);

		return agents.list().size();
	}

	private Agent getAgent(String corpId, String corpSecret, int agentId) {
		String tokenUrl = WxEndpoint.get("url.token.get");
		WxClient wxClient = new WxClient(tokenUrl, corpId, corpSecret);
		Agents agents = new Agents();
		agents.setWxClient(wxClient);
		return agents.get(agentId);
	}

	// ==成员管理
	/**
	 *
	 * @param request
	 * @param response
	 */
	public void userSetting(HttpServletRequest request, HttpServletResponse response) {
		Actions.includePage(request, response, Util.getPagePath(request, "user_main.jsp"));
	}

	/**
	 * 同步微信通信录状态
	 * 
	 * @param request
	 * @param response
	 */
	public void syncUser(HttpServletRequest request, HttpServletResponse response) {
		ContactService.getInstance().executeSync();
		Actions.redirectInfoPage(request, response, "微信通信录同步完成.");
	}

	/**
	 * 成员列表
	 * 
	 * @param request
	 * @param response
	 */
	@SuppressWarnings("unchecked")
	public void listUser(HttpServletRequest request, HttpServletResponse response) {

		// 带入条件
		DataCondition condition = new DataCondition(Util.buildQueryMap(null, request));
		String field = Util.getSortField(request);
		String dir = Util.getSortDir(request);
		condition.setOrderBy(field, dir);

		String groupKey = RequestUtils.getStringValue(request, "groupKey");
		String roleKey = RequestUtils.getStringValue(request, "roleKey");
		List<String> uids = null;
		if (StringUtils.isNotEmpty(groupKey) && StringUtils.isNotEmpty(roleKey)) {
			uids = ORMService.getInstance().queryHQL("select uid from UsUserGroupRole where groupKey = ? and roleKey = ?", groupKey, roleKey);
		} else if (StringUtils.isNotEmpty(groupKey)) {
			uids = ORMService.getInstance().queryHQL("select uid from UsUserGroupRole where groupKey = ?", groupKey);
		} else if (StringUtils.isNotEmpty(roleKey)) {
			uids = ORMService.getInstance().queryHQL("select uid from UsUserGroupRole where roleKey = ?", roleKey);
		}

		if (uids != null) {
			if (uids.size() > 0) {
				condition.setStringIn("uid", uids.toArray(new String[0]));
			} else {
				condition.addSql("1=2");
			}
		}

		// 强制限制微信开关未开的用户
		condition.setNumberEqual("wxEnable", "1");

		int start = Util.getStart(request);
		int limit = Util.getLimit(request);

		DataPackage dp = ORMService.getInstance().queryPackage(UsUser.class.getName(), start, limit, condition.toEntity());
		request.setAttribute("dp", dp);

		Actions.includePage(request, response, Util.getPagePath(request, "user_list.jsp"));
	}

}
