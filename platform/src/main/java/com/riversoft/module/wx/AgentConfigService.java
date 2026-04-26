/*
 * $HeadURL: $
 * $Id: $
 * Copyright (c) 2015 by Riversoft System, all rights reserved.
 */
package com.riversoft.module.wx;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import com.riversoft.core.BeanFactory;
import com.riversoft.core.Config;
import com.riversoft.core.context.RequestContext;
import com.riversoft.core.db.DataPO;
import com.riversoft.core.db.ORMService;
import com.riversoft.core.exception.ExceptionType;
import com.riversoft.core.exception.SystemRuntimeException;
import com.riversoft.platform.SessionManager;
import com.riversoft.platform.translate.WxMenuCommandType;
import com.riversoft.platform.translate.WxReportLocationFlag;
import com.riversoft.weixin.common.exception.WxRuntimeException;
import com.riversoft.weixin.common.menu.Menu;
import com.riversoft.weixin.common.menu.MenuItem;
import com.riversoft.weixin.common.menu.MenuType;
import com.riversoft.weixin.qy.agent.Agents;
import com.riversoft.weixin.qy.agent.bean.Agent;
import com.riversoft.weixin.qy.agent.bean.WritableAgent.ReportLocation;
import com.riversoft.weixin.qy.base.CorpSetting;
import com.riversoft.weixin.qy.menu.Menus;

/**
 * @author woden
 *
 */
public class AgentConfigService {

	/**
	 * 获取单例
	 * 
	 * @return
	 */
	public static AgentConfigService getInstance() {
		return BeanFactory.getInstance().getBean(AgentConfigService.class);
	}

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
	 * 保存
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public Map<String, Object> executeCreate() {
		RequestContext request = RequestContext.getCurrent();
		Integer agentId = request.getInteger("agentId");
		String token = request.getString("token");
		String encodingAESKey = request.getString("encodingAESKey");
		String agentKey = request.getString("agentKey");
		String agentSecret = request.getString("agentSecret");

		// 找agent
		Map<String, Object> config = ((Map<String, Object>) ORMService.getInstance().findByKey("WxAgent", "agentId", agentId));
		if (config != null) {
			throw new SystemRuntimeException(ExceptionType.BUSINESS, "应用[" + agentId + "]已存在,无法重复添加");
		}

		// 查找agent
		String corpId = Config.get("wx.qy.corpId");
		Agent agent = Agents.with(new CorpSetting(corpId, agentSecret)).get(agentId);
		DataPO po = new DataPO("WxAgent");
		po.set("agentId", agentId);
		po.set("title", agent.getName());
		po.set("description", agent.getDescription());
		po.set("logoUrl", agent.getSquareLogUrl());
		po.set("token", token);
		po.set("encodingAESKey", encodingAESKey);
		po.set("closeFlag", agent.isClose() ? 1 : 0);
		po.set("status", 0);
		po.set("createUid", SessionManager.getUser().getUid());
		po.set("agentKey", agentKey);
		po.set("reportLocationFlag", WxReportLocationFlag.valueOf(agent.getReportLocationFlag().name()).getCode());
		po.set("reportUserEnter", 1);
		po.set("reportUserChange", 1);
		po.set("agentSecret", agentSecret);

		ORMService.getInstance().save(po.toEntity());// 保存
		return po.toEntity();
	}

	/**
	 * 删除
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public Map<String, Object> executeRemove() {
		RequestContext request = RequestContext.getCurrent();
		String agentKey = request.getString("agentKey");
		Map<String, Object> agent = ((Map<String, Object>) ORMService.getInstance().findByPk("WxAgent", agentKey));

		ORMService.getInstance().remove(agent);
		ORMService.getInstance().removeByPk("WxAgentEnter", agentKey);
		ORMService.getInstance().removeByPk("WxAgentLocation", agentKey);
		ORMService.getInstance().removeByPk("WxAgentMessage", agentKey);
		ORMService.getInstance().executeHQL("delete from WxAgentMenu where agentKey = ?", agentKey);

		return agent;
	}

	/**
	 * 提交修改
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public Map<String, Object> executeEdit() {
		RequestContext request = RequestContext.getCurrent();
		String agentKey = request.getString("agentKey");

		// 找agent
		Map<String, Object> config = ((Map<String, Object>) ORMService.getInstance().findByPk("WxAgent", agentKey));
		if (config == null) {
			throw new SystemRuntimeException(ExceptionType.BUSINESS, "应用不存在.");
		}

		// 查找agent
		DataPO po = new DataPO("WxAgent", config);
		po.set("reportLocationFlag", request.getInteger("reportLocationFlag"));
		po.set("reportUserEnter", request.getInteger("reportUserEnter"));

		// 看看是否重新验证
		if (StringUtils.equalsIgnoreCase(request.getString("tokenFlag"), "true")) {
			po.set("token", request.getString("token"));
			po.set("encodingAESKey", request.getString("encodingAESKey"));
			po.set("agentSecret", request.getString("agentSecret"));
			po.set("status", 0);
		}
		ORMService.getInstance().update(po.toEntity());// 保存

		// 进入事件
		{
			DataPO eventPo;
			Map<String, Object> evnet = (Map<String, Object>) ORMService.getInstance().findByPk("WxAgentEnter", agentKey);
			if (evnet != null) {
				eventPo = new DataPO("WxAgentEnter", evnet);
			} else {
				eventPo = new DataPO("WxAgentEnter");
				eventPo.set("agentKey", agentKey);
			}

			String[] type = request.getStrings("enter.type");
			if (ArrayUtils.contains(type, "event")) {
				eventPo.set("eventFlag", 1);
			} else {
				eventPo.set("eventFlag", 0);
			}
			if (ArrayUtils.contains(type, "log")) {
				eventPo.set("logFlag", 1);
			} else {
				eventPo.set("logFlag", 0);
			}

			eventPo.set("description", request.getString("enter.description"));
			eventPo.set("commandKey", request.getString("enter.commandKey"));
			eventPo.set("paramType", request.getInteger("enter.paramType"));
			eventPo.set("paramScript", request.getString("enter.paramScript"));
			eventPo.set("logTable", request.getString("enter.logTable"));

			ORMService.getInstance().saveOrUpdate(eventPo.toEntity());
		}

		// 用户关注
		{
			DataPO eventPo;
			Map<String, Object> event = (Map<String, Object>) ORMService.getInstance().findByPk("WxAgentSubscribe", agentKey);
			if (event != null) {
				eventPo = new DataPO("WxAgentSubscribe", event);
			} else {
				eventPo = new DataPO("WxAgentSubscribe");
				eventPo.set("agentKey", agentKey);
			}

			String[] type = request.getStrings("subscribe.type");
			if (ArrayUtils.contains(type, "event")) {
				eventPo.set("eventFlag", 1);
			} else {
				eventPo.set("eventFlag", 0);
			}
			eventPo.set("description", request.getString("subscribe.description"));
			eventPo.set("commandKey", request.getString("subscribe.commandKey"));
			eventPo.set("paramType", request.getInteger("subscribe.paramType"));
			eventPo.set("paramScript", request.getString("subscribe.paramScript"));

			ORMService.getInstance().saveOrUpdate(eventPo.toEntity());
		}

		// 用户取消关注
		{
			DataPO eventPo;
			Map<String, Object> event = (Map<String, Object>) ORMService.getInstance().findByPk("WxAgentUnSubscribe", agentKey);
			if (event != null) {
				eventPo = new DataPO("WxAgentUnSubscribe", event);
			} else {
				eventPo = new DataPO("WxAgentUnSubscribe");
				eventPo.set("agentKey", agentKey);
			}

			String[] type = request.getStrings("unsubscribe.type");
			if (ArrayUtils.contains(type, "event")) {
				eventPo.set("eventFlag", 1);
			} else {
				eventPo.set("eventFlag", 0);
			}

			eventPo.set("description", request.getString("unsubscribe.description"));
			eventPo.set("commandKey", request.getString("unsubscribe.commandKey"));
			eventPo.set("paramType", request.getInteger("unsubscribe.paramType"));
			eventPo.set("paramScript", request.getString("unsubscribe.paramScript"));

			ORMService.getInstance().saveOrUpdate(eventPo.toEntity());
		}

		// 位置上报事件
		{
			DataPO eventPo;
			Map<String, Object> evnet = (Map<String, Object>) ORMService.getInstance().findByPk("WxAgentLocation", agentKey);
			if (evnet != null) {
				eventPo = new DataPO("WxAgentLocation", evnet);
			} else {
				eventPo = new DataPO("WxAgentLocation");
				eventPo.set("agentKey", agentKey);
			}

			String[] type = request.getStrings("location.type");
			if (ArrayUtils.contains(type, "event")) {
				eventPo.set("eventFlag", 1);
			} else {
				eventPo.set("eventFlag", 0);
			}
			if (ArrayUtils.contains(type, "log")) {
				eventPo.set("logFlag", 1);
			} else {
				eventPo.set("logFlag", 0);
			}

			eventPo.set("description", request.getString("location.description"));
			eventPo.set("commandKey", request.getString("location.commandKey"));
			eventPo.set("paramType", request.getInteger("location.paramType"));
			eventPo.set("paramScript", request.getString("location.paramScript"));
			eventPo.set("logTable", request.getString("location.logTable"));

			ORMService.getInstance().saveOrUpdate(eventPo.toEntity());
		}

		// 对话框
		{
			DataPO eventPo;
			Map<String, Object> evnet = (Map<String, Object>) ORMService.getInstance().findByPk("WxAgentMessage", agentKey);
			if (evnet != null) {
				eventPo = new DataPO("WxAgentMessage", evnet);
			} else {
				eventPo = new DataPO("WxAgentMessage");
				eventPo.set("agentKey", agentKey);
			}

			String[] type = request.getStrings("message.type");
			if (ArrayUtils.contains(type, "event")) {
				eventPo.set("eventFlag", 1);
			} else {
				eventPo.set("eventFlag", 0);
			}
			if (ArrayUtils.contains(type, "log")) {
				eventPo.set("logFlag", 1);
			} else {
				eventPo.set("logFlag", 0);
			}

			eventPo.set("eventType", StringUtils.join(request.getStrings("message.eventType"), ";"));
			eventPo.set("logType", StringUtils.join(request.getStrings("message.logType"), ";"));

			eventPo.set("description", request.getString("message.description"));
			eventPo.set("commandKey", request.getString("message.commandKey"));
			eventPo.set("paramType", request.getInteger("message.paramType"));
			eventPo.set("paramScript", request.getString("message.paramScript"));
			eventPo.set("logTable", request.getString("message.logTable"));

			ORMService.getInstance().saveOrUpdate(eventPo.toEntity());
		}

		// 菜单
		List<HashMap<String, Object>> deleteMenus = request.getJsons("deleteMenu");
		List<HashMap<String, Object>> waitMenus = request.getJsons("waitMenu");
		// 删除
		if (deleteMenus != null) {
			for (HashMap<String, Object> menu : deleteMenus) {
				ORMService.getInstance().removeByPk("WxAgentMenu", (Serializable) menu.get("menuKey"));
			}
		}
		if (waitMenus != null) {
			for (HashMap<String, Object> menu : waitMenus) {
				String menuKey = menu.get("menuKey").toString();
				String parentKey = (String) menu.get("parentKey");
				Integer sort = ((Number) menu.get("sort")).intValue();

				DataPO menuPO;
				Map<String, Object> o = (Map<String, Object>) ORMService.getInstance().findByPk("WxAgentMenu", menuKey);
				if (o == null) {// 新增
					menuPO = new DataPO("WxAgentMenu");
					menuPO.set("menuKey", menuKey);
					menuPO.set("agentKey", agentKey);
				} else {
					menuPO = new DataPO("WxAgentMenu", o);
				}
				menuPO.set("parentKey", parentKey);
				menuPO.set("sort", sort);

				if (StringUtils.equalsIgnoreCase(request.getString(menuKey + ".flag"), "true")) {
					Integer menuType = request.getInteger(menuKey + ".menuType");
					if (menuType >= 99) {// 事件
						menuType = request.getInteger(menuKey + ".commandType");
					}
					menuPO.set("menuType", menuType);
					menuPO.set("busiName", request.getString(menuKey + ".busiName"));
					menuPO.set("description", request.getString(menuKey + ".description"));
					menuPO.set("commandKey", request.getString(menuKey + ".commandKey"));
					menuPO.set("paramType", request.getInteger(menuKey + ".paramType"));
					menuPO.set("paramScript", request.getString(menuKey + ".paramScript"));
					String actionStr = "";
					if (menuType == 1) {
						actionStr = request.getString(menuKey + ".action");
						if (StringUtils.isEmpty(actionStr)) {// 超链接模式
							throw new SystemRuntimeException(ExceptionType.CONFIG, "菜单[" + menuPO.getString("busiName") + "]无选择视图.");
						}
					} else if (menuType == 2) {
						actionStr = request.getString(menuKey + ".defUrl");
						if (StringUtils.isEmpty(actionStr)) {// 自定义网址
							throw new SystemRuntimeException(ExceptionType.CONFIG, "菜单[" + menuPO.getString("busiName") + "]无设置网址.");
						}
					}
					menuPO.set("action", actionStr);
				}

				ORMService.getInstance().saveOrUpdate(menuPO.toEntity());
			}
		}
		// 菜单校验,根据微信要求,一级菜单只允许3个,二级菜单最多5个
		Long menuCount = (Long) ORMService.getInstance().findHQL("select count(1) from WxAgentMenu where agentKey = ? and parentKey is null", agentKey);
		if (menuCount > 3) {
			throw new SystemRuntimeException(ExceptionType.CONFIG, "一级菜单不允许超过3个.");
		}
		List<Object[]> subMenuCount = ORMService.getInstance().queryHQL("select parentKey,count(1) as subCount from WxAgentMenu where agentKey = ? and parentKey is not null group by parentKey",
				agentKey);
		for (Object[] o : subMenuCount) {
			Long subCount = (Long) o[1];
			if (subCount > 5) {
				throw new SystemRuntimeException(ExceptionType.CONFIG, "每组二级菜单不允许超过5个.");
			}
		}

		return po.toEntity();
	}

	/**
	 * 同步企业号
	 * 
	 * @param agentKey
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public boolean executeSync(String agentKey) {
		Map<String, Object> config = ((Map<String, Object>) ORMService.getInstance().findByPk("WxAgent", agentKey));
		if (config == null) {
			throw new SystemRuntimeException(ExceptionType.BUSINESS, "应用[" + agentKey + "]不存在.");
		}

		DataPO po = new DataPO("WxAgent", config);
		Agent agent;
		try {
			agent = Agents.with(createAgentSetting(config)).get((Integer) config.get("agentId"));
			// 填入发布时间
			po.set("title", agent.getName());
			po.set("description", agent.getDescription());
			po.set("logoUrl", agent.getSquareLogUrl());
			po.set("closeFlag", agent.isClose() ? 1 : 0);
			po.set("publicDate", new Date());
		} catch (WxRuntimeException e) {// 无法同步,修改状态
			po.set("status", 2);
			ORMService.getInstance().update(po.toEntity());
			return false;
		}

		// 菜单同步
		// 一级菜单
		List<Map<String, Object>> list = ORMService.getInstance().queryHQL("from WxAgentMenu where agentKey = ? and parentKey is null order by sort asc", agentKey);
		if (list != null && list.size() > 0) {
			String domain = (StringUtils.equalsIgnoreCase(Config.get("wx.net.https", "false"), "true") ? "https://" : "http://") + Config.get("wx.net.domain") + "/wx/url/";// 网址前缀
			Menu menu = new Menu();
			for (Map<String, Object> o : list) {
				MenuItem item = new MenuItem();
				int menuType = (int) o.get("menuType");
				if (menuType == 1) {
					item.setType(MenuType.view);
					item.setUrl(domain + "qy_" + o.get("menuKey"));
				} else if (menuType == 2) { // 自定义网址
					item.setType(MenuType.view);
					item.setUrl(o.get("action").toString());
				} else if (menuType > 0) {
					item.setType(MenuType.valueOf(WxMenuCommandType.fromCode(menuType).name()));
				}
				item.setKey((String) o.get("menuKey"));
				item.setName((String) o.get("busiName"));
				menu.add(item);

				// 二级菜单
				List<Map<String, Object>> subList = ORMService.getInstance().queryHQL("from WxAgentMenu where agentKey = ? and parentKey = ? order by sort asc", agentKey, (String) o.get("menuKey"));
				if (subList != null) {
					for (Map<String, Object> sub : subList) {
						MenuItem subItem = new MenuItem();
						int subMenuType = (int) sub.get("menuType");
						if (subMenuType == 1) {
							subItem.setType(MenuType.view);
							subItem.setUrl(domain + "qy_" + sub.get("menuKey"));// 生成企业号专属链接
						} else if (subMenuType == 2) { // 自定义网址
							subItem.setType(MenuType.view);
							subItem.setUrl(sub.get("action").toString());
						} else {
							subItem.setType(MenuType.valueOf(WxMenuCommandType.fromCode(subMenuType).name()));
						}
						subItem.setKey((String) sub.get("menuKey"));
						subItem.setName((String) sub.get("busiName"));
						item.add(subItem);
					}
				}
			}
			Menus.with(createAgentSetting(config)).create((Integer) po.getIntger("agentId"), menu);
		}

		ORMService.getInstance().update(po.toEntity());
		return true;
	}

}
