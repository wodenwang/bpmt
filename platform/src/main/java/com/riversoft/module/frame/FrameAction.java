/*
 * $HeadURL: $
 * $Id: $
 * Copyright (c) 2013 by Riversoft System, all rights reserved.
 */
package com.riversoft.module.frame;

import static com.riversoft.core.web.Actions.includePage;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.riversoft.core.Config;
import com.riversoft.core.db.DataCondition;
import com.riversoft.core.db.ORMService;
import com.riversoft.core.exception.ExceptionType;
import com.riversoft.core.exception.SystemRuntimeException;
import com.riversoft.core.web.Actions;
import com.riversoft.core.web.Actions.Keys;
import com.riversoft.core.web.Actions.Util;
import com.riversoft.core.web.RequestUtils;
import com.riversoft.core.web.annotation.ActionAccess;
import com.riversoft.core.web.annotation.ActionMode;
import com.riversoft.core.web.annotation.ActionMode.Mode;
import com.riversoft.platform.SessionManager;
import com.riversoft.platform.db.ORMAdapterService;
import com.riversoft.platform.language.LanguageFitter;
import com.riversoft.platform.po.CmPri;
import com.riversoft.platform.po.UsGroup;
import com.riversoft.platform.po.UsRole;
import com.riversoft.platform.po.UsUser;
import com.riversoft.platform.script.ScriptHelper;
import com.riversoft.platform.script.ScriptTypes;
import com.riversoft.platform.web.WxUserManager;
import com.riversoft.platform.web.view.annotation.Conf;
import com.riversoft.platform.web.view.annotation.Conf.TargetType;
import com.riversoft.platform.web.view.annotation.Sys;
import com.riversoft.platform.web.view.annotation.Sys.SysMethod;
import com.riversoft.util.MD5;
import com.riversoft.util.jackson.JsonMapper;
import com.riversoft.weixin.common.oauth2.AccessToken;
import com.riversoft.weixin.common.oauth2.OpenUser;
import com.riversoft.weixin.open.base.AppSetting;
import com.riversoft.weixin.open.oauth2.OpenOAuth2s;
import com.riversoft.wx.mp.model.MpVisitorModelKeys;
import com.riversoft.wx.mp.model.OpenVisitorModelKeys;

/**
 * @author Woden
 * 
 */
@Sys
public class FrameAction {
	/**
	 * Logger for this class
	 */
	private static final Logger logger = LoggerFactory.getLogger(FrameAction.class);

	/**
	 * 页面框架首页
	 * 
	 * @param request
	 * @param response
	 */
	@SuppressWarnings("unchecked")
	public void index(HttpServletRequest request, HttpServletResponse response) {
		List<Map<String, Object>> list = ORMService.getInstance().query("CmDomain", new DataCondition().setOrderByAsc("sort").toEntity());
		if (list != null) {
			for (Map<String, Object> domain : list) {
				if (SessionManager.check((CmPri) domain.get("pri"))) {
					Actions.jump(request, response, "/" + (String) domain.get("domainKey") + ".xhtml");
					return;
				}
			}
		}

		// 设置首页信息
		setHomePageProperties(request);

		// 多账号切换
		String appIds = Config.get("wx.web.mp.appIds");
		List<Map<String, Object>> visitors = new ArrayList<>();
		if (StringUtils.isNotEmpty(appIds)) {
			String[] strs = StringUtils.split(appIds, ";");
			if (!SessionManager.getUser().isEntity() && strs.length > 1) {
				for (String appId : strs) {
					Map<String, Object> mpConfig = (Map<String, Object>) ORMService.getInstance().findByKey("WxMp", "appId", appId);
					if (mpConfig == null) {
						continue;
					}

					String visitorTable = (String) mpConfig.get("visitorTable");
					DataCondition condition = new DataCondition().setStringEqual(MpVisitorModelKeys.UNION_ID.getColumn().getName(), SessionManager.getUser().getUnionId())
							.setNumberEqual(MpVisitorModelKeys.SUBSCRIBE.name(), "1");

					Map<String, Object> visitor = (Map<String, Object>) ORMAdapterService.getInstance().find(visitorTable, condition.toEntity());
					if (visitor != null) {
						visitors.add(visitor);
					}
				}
			}
		}
		request.setAttribute("visitors", visitors);

		includePage(request, response, Util.getPagePath(request, FrameStyleSwitcher.getFrame()));
	}

	/**
	 * 设置首页所需信息
	 * 
	 * @param request
	 */
	private void setHomePageProperties(HttpServletRequest request) {
		// logo与copyright
		request.setAttribute("logoUrl", Config.get("page.logo.url", Actions.Util.getContextPath(request) + FrameStyleSwitcher.getLogoUrl()));
		request.setAttribute("copyRight", Config.getChinese("page.copyright", ""));

		// 任务控制台
		request.setAttribute("taskPanel", !"false".equalsIgnoreCase(Config.get("page.taskpanel")));

		// 浏览器提示
		request.setAttribute("browserMsg", Config.getChinese("page.browser.msg", ""));
		request.setAttribute("browserUrl", Config.get("page.browser.url", ""));

		// 来源菜单
		request.setAttribute("menuKey", RequestUtils.getStringValue(request, "menu"));

		// 语言切换区域
		String[] languages = StringUtils.split(Config.get("page.language", ""), ";");
		if (languages == null || languages.length < 2) {// 只有一种语言则不需要配置的菜单
			languages = null;
		}
		request.setAttribute("currentLanguage", SessionManager.getCurrentLanguage());
		request.setAttribute("languages", languages);
	}

	/**
	 * 域入口
	 * 
	 * @param request
	 * @param response
	 */
	@SuppressWarnings("unchecked")
	public void domain(HttpServletRequest request, HttpServletResponse response) {
		String domainKey = RequestUtils.getStringValue(request, "domain");
		List<Map<String, Object>> list = ORMService.getInstance().query("CmDomain", new DataCondition().setOrderByAsc("sort").toEntity());
		Map<String, Object> domain = null;
		List<Map<String, Object>> domains = new ArrayList<>();

		for (Map<String, Object> vo : list) {
			try {
				if (!SessionManager.check((CmPri) vo.get("pri"))) {
					logger.debug("域[" + vo.get("busiName") + "]没有权限访问,跳过.");
					continue;
				}
			} catch (Exception e) {
				logger.warn("域[" + vo.get("busiName") + "]没有权限访问,跳过.", e);
				continue;
			}

			domains.add(vo);

			if (domain == null && domainKey.equals(vo.get("domainKey"))) {
				domain = vo;
			}
		}

		if (domain == null) {
			throw new SystemRuntimeException(ExceptionType.BUSINESS_PRIVILAGE, "域[" + domainKey + "]不存在或无权限访问.");
		}

		// 设置首页信息
		setHomePageProperties(request);

		// 多账号切换
		String appIds = Config.get("wx.web.mp.appIds");
		List<Map<String, Object>> visitors = new ArrayList<>();
		if (StringUtils.isNotEmpty(appIds)) {
			String[] strs = StringUtils.split(appIds, ";");
			if (!SessionManager.getUser().isEntity() && strs.length > 1) {
				for (String appId : strs) {
					Map<String, Object> mpConfig = (Map<String, Object>) ORMService.getInstance().findByKey("WxMp", "appId", appId);
					if (mpConfig == null) {
						continue;
					}

					String visitorTable = (String) mpConfig.get("visitorTable");
					DataCondition condition = new DataCondition().setStringEqual(MpVisitorModelKeys.UNION_ID.getColumn().getName(), SessionManager.getUser().getUnionId())
							.setNumberEqual(MpVisitorModelKeys.SUBSCRIBE.name(), "1");

					Map<String, Object> visitor = (Map<String, Object>) ORMAdapterService.getInstance().find(visitorTable, condition.toEntity());
					if (visitor != null) {
						visitors.add(visitor);
					}
				}
			}
		}
		request.setAttribute("visitors", visitors);

		// 验证成功
		request.setAttribute("domains", domains);
		request.setAttribute("domain", domain);

		includePage(request, response, Util.getPagePath(request, FrameStyleSwitcher.getFrame()));
	}

	/**
	 * 菜单
	 * 
	 * @param request
	 * @param response
	 */
	@SuppressWarnings("unchecked")
	public void menu(HttpServletRequest request, HttpServletResponse response) {

		final String cp = Actions.Util.getContextPath(request);
		String domainKey = RequestUtils.getStringValue(request, "domain");
		String currentKey = RequestUtils.getStringValue(request, "menu");// 当前展示的菜单
		DataCondition condition = new DataCondition().setStringEqual("domainKey", domainKey).setOrderByAsc("sort");
		List<Map<String, Object>> list = ORMService.getInstance().query("CmMenu", condition.toEntity());
		List<Map<String, Object>> menus = new ArrayList<>();

		if (StringUtils.isEmpty(currentKey)) {
			currentKey = "_home_";// 默认值是"首页"
		}
		// 加入"首页"
		{
			Map<String, Object> o = new HashMap<>();
			o.put("id", "_home_");
			o.put("parentId", null);
			o.put("name", LanguageFitter.fit("#:zh[首页]:en[Home page]#"));
			o.put("icon", cp + "/css/icon/house.png");
			o.put("action", Actions.Util.getActionUrl(request) + "/panel.shtml?domain=" + domainKey);
			o.put("openType", 1);
			menus.add(o);
		}

		for (Map<String, Object> menu : list) {
			if (!SessionManager.check((CmPri) menu.get("pri"))) {
				logger.debug("菜单[" + menu.get("name") + "]没有权限访问,跳过.");
				continue;
			}
			String name = (String) menu.get("name");
			if (StringUtils.isNotEmpty(name)) {
				menu.put("name", LanguageFitter.fit((String) menu.get("name"))); // 根据语言翻译菜单名
			}
			String icon = (String) menu.get("icon");
			if (StringUtils.isNotEmpty(icon)) {
				menu.put("icon", cp + "/css/icon/" + icon);
			}
			String paramScript = (String) menu.get("paramScript");
			if (StringUtils.isNotEmpty(paramScript)) {
				String params = (String) ScriptHelper.evel(ScriptTypes.forCode((Integer) menu.get("paramType")), paramScript);
				menu.put("params", params);
			}
			menus.add(menu);
		}
		request.setAttribute("menus", menus);

		request.setAttribute("currentKey", currentKey);
		request.setAttribute("domainKey", domainKey);
		includePage(request, response, Util.getPagePath(request, FrameStyleSwitcher.getMenu()));
	}

	/**
	 * 用户设置界面
	 * 
	 * @param request
	 * @param response
	 */
	public void userSetting(HttpServletRequest request, HttpServletResponse response) {
		request.setAttribute("wxQRcode", Boolean.valueOf(Config.get("wx.web.login.qrcode", "false")));
		request.setAttribute("openFlag", Boolean.valueOf(Config.get("wx.open.flag", "false")));
		request.setAttribute("user", SessionManager.getUser());
		includePage(request, response, Util.getPagePath(request, "/frame/user_setting.jsp"));
	}

	/**
	 * 绑定微信公众号
	 * 
	 * @param request
	 * @param response
	 */
	public void wxBinding(HttpServletRequest request, HttpServletResponse response) {
		request.setAttribute("wxDomain", Config.get("wx.net.domain", "gzriver.com"));
		request.setAttribute("wxWebAppId", Config.get("wx.open.appId"));
		includePage(request, response, Util.getPagePath(request, "/frame/wx_binding.jsp"));
	}

	/**
	 * 提交微信绑定
	 * 
	 * @param request
	 * @param response
	 */
	@SuppressWarnings("unchecked")
	public void submitBinding(HttpServletRequest request, HttpServletResponse response) {
		String code = RequestUtils.getStringValue(request, "code");
		String webAppId = Config.get("wx.open.appId");
		String webAppSecret = Config.get("wx.open.appSecret");
		OpenOAuth2s auth2s = OpenOAuth2s.with(new AppSetting(webAppId, webAppSecret));
		AccessToken accessToken = auth2s.getAccessToken(code);

		String table = Config.get("wx.open.table");
		if (StringUtils.isEmpty(table)) {
			throw new SystemRuntimeException(ExceptionType.BUSINESS, "无法绑定开放平台.");
		}
		Map<String, Object> u = (Map<String, Object>) ORMAdapterService.getInstance().findByPk(table, accessToken.getOpenId());
		OpenUser ou = auth2s.userInfo(accessToken.getAccessToken(), accessToken.getAccessToken());
		if (u == null) {
			u = WxUserManager.buildOpenVisitor(table, ou);
		} else {
			u.put(OpenVisitorModelKeys.UPDATE_DATE.name(), new Date());
			u.put(OpenVisitorModelKeys.NICK_NAME.name(), ou.getNickName());
			u.put(OpenVisitorModelKeys.HEAD_IMG_URL.name(), ou.getHeadImgUrl());
			u.put(OpenVisitorModelKeys.USER_ID.name(), SessionManager.getUser().getUid());
			ORMAdapterService.getInstance().update(u);
		}

		SessionManager.doLogout(request);
		Actions.jump(request, response, "/");
	}

	/**
	 * 通知设置
	 * 
	 * @param request
	 * @param response
	 */
	public void notifySetting(HttpServletRequest request, HttpServletResponse response) {
		UsUser user = (UsUser) ORMService.getInstance().findByPk(UsUser.class.getName(), SessionManager.getUser().getUid());
		request.setAttribute("user", user);
		request.setAttribute("allowSetting", !Config.get("mail.notify.user.setting", "true").equalsIgnoreCase("false"));
		includePage(request, response, Util.getPagePath(request, "/frame/notify_setting.jsp"));
	}

	/**
	 * 通知设置提交
	 * 
	 * @param request
	 * @param response
	 */
	public void submitNotifySetting(HttpServletRequest request, HttpServletResponse response) {
		String mail = RequestUtils.getStringValue(request, "mail");
		String[] msgType = RequestUtils.getStringValues(request, "msgType");
		String[] receiveType = RequestUtils.getStringValues(request, "receiveType");
		UsUser user = (UsUser) ORMService.getInstance().findByPk(UsUser.class.getName(), SessionManager.getUser().getUid());
		if (!Config.get("mail.notify.user.setting", "true").equalsIgnoreCase("false")) {// 允许设置邮箱
			user.setMail(mail);
		}
		user.setMsgType(StringUtils.join(msgType, ";"));
		user.setReceiveType(StringUtils.join(receiveType, ";"));
		ORMService.getInstance().updatePO(user);
		Actions.redirectInfoPage(request, response, "通知设置成功.");
	}

	/**
	 * 密码修改
	 * 
	 * @param request
	 * @param response
	 */
	@ActionMode(Mode.FIT)
	@SysMethod
	@Conf(description = "修改密码", sort = 1, target = { TargetType.MENU, TargetType.WX })
	public void changePwd(HttpServletRequest request, HttpServletResponse response) {
		Util.setTitle(request, "密码修改");
		request.setAttribute(Keys.H5_JS.toString(), "amaze");// 视图使用amaze ui
		request.setAttribute("user", SessionManager.getUser());
		request.setAttribute("group", SessionManager.getGroup());
		request.setAttribute("role", SessionManager.getRole());
		includePage(request, response, Util.getPagePath(request, "/frame/pwd_config.jsp"));
	}

	/**
	 * 密码修改提交
	 * 
	 * @param request
	 * @param response
	 */
	@ActionMode(Mode.FIT)
	public void submitChangePwd(HttpServletRequest request, HttpServletResponse response) {
		String currentPwd = RequestUtils.getStringValue(request, "oldPassword");
		String newPwd = RequestUtils.getStringValue(request, "newPassword");

		UsUser user = (UsUser) ORMService.getInstance().findByPk(UsUser.class.getName(), SessionManager.getUser().getUid());

		if (user == null) {
			throw new SystemRuntimeException(ExceptionType.BUSINESS, "找不到用户[" + SessionManager.getUser().getUid() + "].");
		}

		if (user.getPassword().equals(MD5.md5(currentPwd))) {
			if (user.getPassword().equals(MD5.md5(newPwd))) {
				Actions.redirectErrorPage(request, response, "不能使用原有密码,请重新输入密码.");
			} else {
				user.setPassword(MD5.md5(newPwd));
				ORMService.getInstance().updatePO(user);
				Actions.redirectInfoPage(request, response, "密码修改成功.");
			}
		} else {
			Actions.redirectErrorPage(request, response, "当前密码错误,请核对!");
		}

	}

	/**
	 * 主面板
	 * 
	 * @param request
	 * @param response
	 */
	@SuppressWarnings("unchecked")
	public void panel(HttpServletRequest request, HttpServletResponse response) {
		String domainKey = RequestUtils.getStringValue(request, "domain");

		// 域对象
		request.setAttribute("vo", ORMService.getInstance().findByPk("CmDomain", domainKey));

		List<Map<String, Object>> homes = new ArrayList<>();
		List<Map<String, Object>> list = ORMService.getInstance().query("CmHome", new DataCondition().setStringEqual("domainKey", domainKey).setOrderByAsc("sort").toEntity());
		for (Map<String, Object> home : list) {
			if (!SessionManager.check((CmPri) home.get("pri"))) {
				logger.debug("首页标签[" + home.get("name") + "]没有权限访问,跳过.");
				continue;
			}

			String paramScript = (String) home.get("paramScript");
			String params = null;
			if (StringUtils.isNotEmpty(paramScript)) {
				params = (String) ScriptHelper.evel(ScriptTypes.forCode((Integer) home.get("paramType")), paramScript);
			}
			if (StringUtils.isNotEmpty(params)) {
				home.put("params", params);
			}
			homes.add(home);
		}

		request.setAttribute("homes", homes);

		Actions.includePage(request, response, Util.getPagePath(request, FrameStyleSwitcher.getPanel()));
	}

	/**
	 * 组织切换
	 * 
	 * @param request
	 * @param response
	 */
	@SuppressWarnings("unchecked")
	public void changeGroup(HttpServletRequest request, HttpServletResponse response) {
		List<Map<String, Object>> list = ORMService.getInstance().query("UsUserGroupRole",
				new DataCondition().setStringEqual("uid", SessionManager.getUser().getUid()).setOrderBy("groupKey", "asc").toEntity());
		if (list.size() < 2) {
			Actions.redirectInfoPage(request, response, "您只归属于组织[" + SessionManager.getGroup().getBusiName() + "],无需切换.");
		} else {
			String cp = Actions.Util.getContextPath(request);
			List<Map<String, Object>> tree = new ArrayList<>();
			List<String> groups = new ArrayList<>();// 保存已有组
			for (Map<String, Object> vo : list) {
				String groupKey = (String) vo.get("groupKey");
				String roleKey = (String) vo.get("roleKey");
				if (!groups.contains(groupKey)) {
					groups.add(groupKey);
					Map<String, Object> o = new HashMap<>();
					UsGroup group = (UsGroup) ORMService.getInstance().findByPk(UsGroup.class.getName(), groupKey);
					o.put("id", "_group_" + groupKey);
					o.put("name", group.getBusiName());
					o.put("icon", cp + "/css/icon/house.png");
					o.put("parentId", null);
					o.put("isRole", false);
					tree.add(o);
				}

				{
					UsRole role = (UsRole) ORMService.getInstance().findByPk(UsRole.class.getName(), roleKey);
					Map<String, Object> o = new HashMap<>();
					o.put("id", "_role_" + groupKey + "_" + roleKey);

					o.put("icon", cp + "/css/icon/user.png");
					o.put("parentId", "_group_" + groupKey);
					o.put("groupKey", groupKey);
					o.put("roleKey", roleKey);
					o.put("isRole", true);
					if (groupKey.equals(SessionManager.getGroup().getGroupKey()) && roleKey.equals(SessionManager.getRole().getRoleKey())) {
						o.put("font", "{'font-weight':'bold','color':'red'}");// 当前角色
						o.put("name", role.getBusiName() + "(当前角色)");
						o.put("currentRole", true);
					} else {
						o.put("name", role.getBusiName());
						o.put("currentRole", false);
					}
					tree.add(o);
				}
			}
			request.setAttribute("tree", tree);
			request.setAttribute("user", SessionManager.getUser());
			request.setAttribute("group", SessionManager.getGroup());
			request.setAttribute("role", SessionManager.getRole());
			Actions.includePage(request, response, Util.getPagePath(request, "/frame/group_change.jsp"));
		}
	}

	/**
	 * 提交切换组织
	 * 
	 * @param request
	 * @param response
	 */
	public void submitChangeGroup(HttpServletRequest request, HttpServletResponse response) {
		SessionManager.changeGroup(request);
		Actions.redirectInfoPage(request, response, "切换组织成功.");
	}

	/**
	 * 提交切换公众号
	 * 
	 * @param request
	 * @param response
	 */
	public void submitChangeMp(HttpServletRequest request, HttpServletResponse response) {
		String mpKey = RequestUtils.getStringValue(request, "mpKey");

		Map<String, Object> mpConfig = (Map<String, Object>) ORMService.getInstance().findByPk("WxMp", mpKey);
		if (mpConfig == null) {
			throw new SystemRuntimeException(ExceptionType.BUSINESS, "无法切换公众号.");
		}

		String visitorTable = (String) mpConfig.get("visitorTable");
		String visitorTagTable = (String) mpConfig.get("visitorTagTable");
		DataCondition condition = new DataCondition().setStringEqual(MpVisitorModelKeys.UNION_ID.getColumn().getName(), SessionManager.getUser().getUnionId())
				.setNumberEqual(MpVisitorModelKeys.SUBSCRIBE.name(), "1");

		Map<String, Object> visitor = (Map<String, Object>) ORMAdapterService.getInstance().find(visitorTable, condition.toEntity());
		if (visitor != null) {
			UsUser user = WxUserManager.buildMpUser(visitor, (String) mpConfig.get("groupKey"), (String) mpConfig.get("roleKey"));
			SessionManager.doUserLogin(request, user);
			Actions.redirectInfoPage(request, response, "切换成功");
			return;
		}

		throw new SystemRuntimeException(ExceptionType.BUSINESS, "无法登录到公众号[" + mpConfig.get("title") + "],请先关注.");
	}

	/**
	 * 切换语言
	 * 
	 * @param request
	 * @param response
	 */
	@ActionAccess(login = false)
	public void changeLanguage(HttpServletRequest request, HttpServletResponse response) {
		SessionManager.changeLanguage(request);
		Map<String, Object> result = new HashMap<>();
		result.put("flag", true);
		result.put("msg", "切换语言成功.");
		Actions.showJson(request, response, result);
	}

}
