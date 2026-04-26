/*
 * $HeadURL: $
 * $Id: $
 * Copyright (c) 2013 by Riversoft System, all rights reserved.
 */
package com.riversoft.platform;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.riversoft.core.Config;
import com.riversoft.core.context.SessionContext;
import com.riversoft.core.db.DataCondition;
import com.riversoft.core.db.ORMService;
import com.riversoft.core.exception.ExceptionType;
import com.riversoft.core.exception.SystemRuntimeException;
import com.riversoft.core.web.Actions.Util;
import com.riversoft.core.web.RequestUtils;
import com.riversoft.platform.po.CmPri;
import com.riversoft.platform.po.CmPriGroupRelate;
import com.riversoft.platform.po.UsGroup;
import com.riversoft.platform.po.UsRole;
import com.riversoft.platform.po.UsUser;
import com.riversoft.platform.script.ScriptHelper;
import com.riversoft.platform.script.ScriptTypes;
import com.riversoft.util.MD5;

/**
 * 当前会话管理
 *
 * @author Woden
 *
 */
public class SessionManager {
	/**
	 * Logger for this class
	 */
	private static final Logger logger = LoggerFactory.getLogger(SessionManager.class);

	/**
	 * 当前会话中使用的属性值
	 *
	 * @author Woden
	 *
	 */
	public static enum SessionAttributeKey {
		/**
		 * 当前用户{@UsUser}
		 */
		USER,
		/**
		 * 当前组织{@UsGroup}
		 */
		GROUP,
		/**
		 * 当前角色{@UsRole}
		 */
		ROLE,
		/**
		 * 当前用户,组织,角色关系.Map类型.详见{@link hbm/user/US_USER_GROUP_ROLE.hbm.xml}
		 */
		RELATION_SHIP,
		/**
		 * 权限组列表
		 */
		PRI_GROUP,

		/**
		 * 功能点权限缓存
		 */
		PRI_POINT_LIST,
		/**
		 * 超级权限标识
		 */
		SUPER_PRI_FLAG,
		/**
		 * 登陆时间
		 */
		DATE,
		/**
		 * 登陆IP
		 */
		IP,
		/**
		 * 界面请求LOG堆栈(保存一个hashmap)
		 */
		LOG,
		/**
		 * 随机验证码
		 */
		RANDOM_CODE,
		/**
		 * 登录出错次数
		 */
		LOGIN_ERROR_COUNT,
		/**
		 * 当前系统语言.默认zh_CN
		 */
		LANGUAGE,
		/**
		 * 可选值:agent/mp
		 */
		WX_TYPE,
		/**
		 * MP_KEY或AGENT_KEY
		 */
		WX_KEY,
		/**
		 * WX_SCOPE
		 */
		WX_SCOPE;

		@Override
		public String toString() {
			return name();
		}
	}

	private static ORMService service = ORMService.getInstance();

	/**
	 * 验证权限
	 *
	 * @param pri
	 *            权限资源实体
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static boolean check(CmPri pri) {
		return check(pri, MapUtils.EMPTY_MAP);
	}

	/**
	 * 验证权限
	 *
	 * @param pri
	 *            权限资源实体
	 * @param context
	 *            上下文.
	 * @return
	 */
	public static boolean check(CmPri pri, Map<String, Object> context) {

		if (pri == null) {
			return false;
		}

		switch (CmPri.Types.fromCode(pri.getType())) {
		case BASIC:// 权限点
			return checkPoint(pri, context);
		case ONLY_SCRIPT:// 仅使用脚本
			return checkScript(pri, context);
		case AND_SCRIPT:// 脚本和权限
			return checkPoint(pri, context) && checkScript(pri, context);
		case OR_SCRIPT:// 脚本或权限
			return checkScript(pri, context) || checkPoint(pri, context);
		default:
			return false;
		}
	}

	private static boolean checkScript(CmPri pri, Map<String, Object> context) {
		ScriptTypes type = ScriptTypes.forCode(pri.getCheckType());
		String checkScript = pri.getCheckScript();
		return Boolean.valueOf(ScriptHelper.evel(type, checkScript, context).toString());
	}

	/**
	 * 缓存权限结果
	 * 
	 * @param priKey
	 */
	private static void storePriToCache(String priKey) {
		SessionContext session = SessionContext.getCurrent();
		Set<String> set = (Set<String>) session.get(SessionAttributeKey.PRI_POINT_LIST.toString());
		set.add(priKey);
	}

	/**
	 * 从缓存中快速获取权限结果
	 * 
	 * @param priKey
	 * @return
	 */
	private static boolean checkPriFromCache(String priKey) {
		SessionContext session = SessionContext.getCurrent();
		Set<String> set = (Set<String>) session.get(SessionAttributeKey.PRI_POINT_LIST.toString());
		return set.contains(priKey);
	}

	@SuppressWarnings("unchecked")
	private static boolean checkPoint(CmPri pri, Map<String, Object> context) {
		if (isAdmin()) {// 管理员直接跳过
			return true;
		}

		if (checkPriFromCache(pri.getPriKey())) {// 缓存中存在则跳过
			return true;
		}

		SessionContext session = SessionContext.getCurrent();
		Set<String> priGroupIds = (Set<String>) session.get(SessionAttributeKey.PRI_GROUP.toString());
		if (priGroupIds != null && priGroupIds.size() > 0) {
			boolean basicResult = false;
			// 匹配关系
			List<CmPriGroupRelate> list = ORMService.getInstance().queryHQL("from " + CmPriGroupRelate.class.getName() + " where priKey = ? and groupId in (:list)", pri.getPriKey(),
					new ORMService.QueryVO("list", priGroupIds));
			if (list != null && list.size() > 0) {
				for (CmPriGroupRelate vo : list) {
					if (StringUtils.isNotEmpty(vo.getCheckScript())) {
						try {
							ScriptTypes type = ScriptTypes.forCode(vo.getCheckType());
							String checkScript = vo.getCheckScript();
							basicResult = Boolean.valueOf(ScriptHelper.evel(type, checkScript, context).toString());
						} catch (SystemRuntimeException e) {
							logger.warn("执行权限脚本出错.", e);
							basicResult = false;
						}

						if (basicResult) {// 有一个执行通过则通过
							return true;
						}

					} else {// 无脚本权限,则直接设置为成功
						storePriToCache(pri.getPriKey());
						return true;
					}
				}
			}
		}

		return false;
	}

	/**
	 * 判断当前是否登陆状态
	 *
	 * @return
	 */
	public static boolean checkUserLogin() {
		UsUser user = (UsUser) SessionContext.getCurrent().get(SessionAttributeKey.USER.toString());
		if (user == null) {
			return false;
		}

		String wxType = SessionContext.getCurrent().getString(SessionAttributeKey.WX_TYPE.name());
		String wxKey = SessionContext.getCurrent().getString(SessionAttributeKey.WX_KEY.name());
		if (StringUtils.isNotEmpty(wxType)) {// 来源于微信
			if (StringUtils.equalsIgnoreCase("mp", wxType)) {
				return StringUtils.equals(wxKey, user.getMpKey());// 同一个mp登录
			} else if (StringUtils.equalsIgnoreCase("agent", wxType)) {
				return StringUtils.isEmpty(user.getMpKey());// 企业号登录的
			}
		}

		return true;
	}

	/**
	 * 判断是否工程管理员(超级权限)
	 *
	 * @return
	 */
	public static boolean isAdmin() {
		return SessionContext.getCurrent().get(SessionAttributeKey.USER.toString()) != null && (boolean) SessionContext.getCurrent().get(SessionAttributeKey.SUPER_PRI_FLAG.toString()) == true;
	}

	/**
	 * 校验当前Action是否有权限访问
	 *
	 * @param actionUrl
	 *            当前action地址
	 * @return
	 */
	public static boolean checkAction(String actionUrl) {
		return true;
	}

	/**
	 * 获取当前用户
	 *
	 * @return
	 */
	public static UsUser getUser() {
		SessionContext session = SessionContext.getCurrent();
		if (session == null) {
			return null;
		}
		return (UsUser) session.get(SessionAttributeKey.USER.toString());
	}

	/**
	 * 获取当前组织
	 *
	 * @return
	 */
	public static UsGroup getGroup() {
		SessionContext session = SessionContext.getCurrent();
		if (session == null) {
			return null;
		}
		return (UsGroup) session.get(SessionAttributeKey.GROUP.toString());
	}

	/**
	 * 获取当前角色
	 *
	 * @return
	 */
	public static UsRole getRole() {
		SessionContext session = SessionContext.getCurrent();
		if (session == null) {
			return null;
		}
		return (UsRole) session.get(SessionAttributeKey.ROLE.toString());
	}

	/**
	 * 处理登陆
	 *
	 * @param request
	 */
	public static void doUserLogin(HttpServletRequest request) {
		String username = RequestUtils.getStringValue(request, "username");
		username = StringUtils.trim(username);// 去除空格

		// 登录出错次数
		Integer errorCount = (Integer) request.getSession().getAttribute(SessionAttributeKey.LOGIN_ERROR_COUNT.toString());
		if (errorCount == null) {
			errorCount = 0;
		}

		// 验证码验证
		Integer allowErrorCount = NumberUtils.toInt(Config.get("page.randomcode"), 3);// 0表示必须验证码;默认不需要验证码.
		if (allowErrorCount <= errorCount) {// 需要验证码
			String randomCode = RequestUtils.getStringValue(request, "randomcode");
			String currentRandomCode = (String) request.getSession().getAttribute(SessionAttributeKey.RANDOM_CODE.toString());
			if (StringUtils.isEmpty(randomCode) || !randomCode.equalsIgnoreCase(currentRandomCode)) {
				errorCount++;
				request.getSession().setAttribute(SessionAttributeKey.LOGIN_ERROR_COUNT.toString(), errorCount);
				throw new SystemRuntimeException(ExceptionType.BUSINESS, "验证码错误.");
			}
		}

		if (StringUtils.isEmpty("username") || StringUtils.isEmpty("password")) {
			errorCount++;
			request.getSession().setAttribute(SessionAttributeKey.LOGIN_ERROR_COUNT.toString(), errorCount);
			throw new SystemRuntimeException(ExceptionType.BUSINESS, "用户名或密码不允许为空.");
		}

		// 校验用户名
		UsUser user = (UsUser) service.findByPk(UsUser.class.getName(), username);
		if (user == null) {
			errorCount++;
			request.getSession().setAttribute(SessionAttributeKey.LOGIN_ERROR_COUNT.toString(), errorCount);
			throw new SystemRuntimeException(ExceptionType.BUSINESS, "找不到用户[" + username + "].");
		}

		// 校验密码,校验用户是否过期等
		String password = RequestUtils.getStringValue(request, "password");
		if (!MD5.md5(password).equals(user.getPassword())) {
			errorCount++;
			request.getSession().setAttribute(SessionAttributeKey.LOGIN_ERROR_COUNT.toString(), errorCount);
			throw new SystemRuntimeException(ExceptionType.BUSINESS, "用户[" + username + "]密码错误.");
		}

		request.getSession().setAttribute(SessionAttributeKey.LOGIN_ERROR_COUNT.toString(), 0);
		doUserLogin(request, user);
	}

	/**
	 * 处理登录
	 *
	 * @param request
	 * @param username
	 */
	public static void doUserLogin(HttpServletRequest request, String username) {
		// 校验用户名
		UsUser user = (UsUser) service.findByPk(UsUser.class.getName(), username);
		if (user == null) {
			throw new SystemRuntimeException(ExceptionType.BUSINESS, "找不到用户[" + username + "].");
		}

		doUserLogin(request, user);
	}

	/**
	 * 处理登录
	 *
	 * @param request
	 * @param user
	 */
	@SuppressWarnings("unchecked")
	public static void doUserLogin(HttpServletRequest request, UsUser user) {

		// 超级权限
		String[] admins = Config.get("safe.admin", "admin").split(";|,");
		boolean hasSuperPri = Arrays.asList(admins).contains(user.getUid());

		// 超级权限不受系统维护约束
		if (!hasSuperPri && Platform.checkPause()) {
			throw new SystemRuntimeException(ExceptionType.BUSINESS, "系统维护中,暂停用户登陆.");
		}

		// 校验安全ip.超级权限无需此校验
		String currentIp = Util.getRealIpAddr(request);
		if (!hasSuperPri && StringUtils.isNotEmpty(user.getAllowIp())) {
			Pattern pattern = Pattern.compile(user.getAllowIp());
			Matcher matcher = pattern.matcher(currentIp);
			if (!matcher.matches()) {
				throw new SystemRuntimeException(ExceptionType.BUSINESS, "用户[" + user.getUid() + "]当前网络环境不安全,请更换网络环境登陆.");
			}
		}

		// 是否失效.超级权限无需此校验
		if (!hasSuperPri && user.getActiveFlag().intValue() != 1) {
			throw new SystemRuntimeException(ExceptionType.BUSINESS, "用户[" + user.getUid() + "]账号已失效.");
		}

		List<?> relationshipList;
		UsGroup group;
		UsRole role;
		// 查找当前组织与用户
		if (user.isEntity()) {// 实体
			relationshipList = service.queryHQL("from UsUserGroupRole where uid = ? order by defaultFlag desc,sort asc", user.getUid());
			if (relationshipList == null || relationshipList.size() < 1) {
				throw new SystemRuntimeException(ExceptionType.BUSINESS, "无法找到用户[" + user.getUid() + "]归属的组织与角色,无法登陆系统.");
			}
			Map<String, Object> relationship = (Map<String, Object>) relationshipList.get(0);
			group = (UsGroup) service.findByPk(UsGroup.class.getName(), relationship.get("groupKey").toString());
			role = (UsRole) service.findByPk(UsRole.class.getName(), relationship.get("roleKey").toString());
		} else {// 虚拟
			relationshipList = new ArrayList<Map<String, Object>>();
			if (StringUtils.isEmpty(user.getVisitorGroupKey()) || StringUtils.isEmpty(user.getVisitorRoleKey())) {
				user.setVisitorGroupKey("visitor");
				user.setVisitorRoleKey("visitor");
			} else {
				List<?> groupRoleRelation = service.queryHQL("from UsGroupRole where groupKey = ? and roleKey = ?", user.getVisitorGroupKey(), user.getVisitorRoleKey());
				if (groupRoleRelation.size() < 1) {
					user.setVisitorGroupKey("visitor");
					user.setVisitorRoleKey("visitor");
				}
			}
			group = (UsGroup) service.findByPk(UsGroup.class.getName(), user.getVisitorGroupKey());
			role = (UsRole) service.findByPk(UsRole.class.getName(), user.getVisitorRoleKey());
		}

		if (group == null) {
			throw new SystemRuntimeException(ExceptionType.BUSINESS, "用户[" + user.getUid() + "]归属的组织已被注销,无法登陆系统.");
		}
		if (role == null) {
			throw new SystemRuntimeException(ExceptionType.BUSINESS, "用户[" + user.getUid() + "]归属的角色已被注销,无法登陆系统.");
		}

		// 设置权限
		Set<String> priGroups = new HashSet<>();
		{
			// 角色固有权限组
			List<String> groupIds = service.queryHQL("select groupId from UsRolePriGroupRelate where roleKey = ?", role.getRoleKey());
			priGroups.addAll(groupIds);
		}
		{
			// 组织内角色特有权限组
			List<String> groupIds = service.queryHQL("select groupId from UsRoleGroupPriRelate where roleKey = ? and groupKey = ?", role.getRoleKey(), group.getGroupKey());
			priGroups.addAll(groupIds);
		}

		// 设置会话
		request.getSession().setAttribute(SessionAttributeKey.RELATION_SHIP.toString(), relationshipList);
		request.getSession().setAttribute(SessionAttributeKey.USER.toString(), user);
		request.getSession().setAttribute(SessionAttributeKey.GROUP.toString(), group);
		request.getSession().setAttribute(SessionAttributeKey.ROLE.toString(), role);
		request.getSession().setAttribute(SessionAttributeKey.SUPER_PRI_FLAG.toString(), hasSuperPri);
		request.getSession().setAttribute(SessionAttributeKey.PRI_GROUP.toString(), priGroups);
		request.getSession().setAttribute(SessionAttributeKey.PRI_POINT_LIST.toString(), new HashSet<String>());
		request.getSession().setAttribute(SessionAttributeKey.DATE.toString(), new Date());
		request.getSession().setAttribute(SessionAttributeKey.IP.toString(), currentIp);
		request.getSession().setAttribute(SessionAttributeKey.LOG.toString(), new HashMap<String, Object>());
		logger.info("用户[{}]登陆成功.IP地址:[{}]", user.getUid(), currentIp);
	}

	/**
	 * 处理登出
	 *
	 * @param request
	 */
	public static void doLogout(HttpServletRequest request) {
		Enumeration<String> enumeration = request.getSession().getAttributeNames();
		while (enumeration.hasMoreElements()) {
			String key = enumeration.nextElement();
			request.getSession().removeAttribute(key);
		}
	}

	/**
	 * 切换组织
	 *
	 * @param request
	 */
	@SuppressWarnings("unchecked")
	public static void changeGroup(HttpServletRequest request) {

		String groupKey = RequestUtils.getStringValue(request, "groupKey");
		String roleKey = RequestUtils.getStringValue(request, "roleKey");

		if (StringUtils.isEmpty(groupKey) || StringUtils.isEmpty(roleKey)) {
			throw new SystemRuntimeException(ExceptionType.BUSINESS, "信息填写不全.");
		}

		// 查找关系
		Map<String, Object> relationship = ((Map<String, Object>) ORMService.getInstance().find("UsUserGroupRole",
				new DataCondition().setStringEqual("uid", getUser().getUid()).setStringEqual("groupKey", groupKey).setStringEqual("roleKey", roleKey).toEntity()));

		if (relationship == null) {
			throw new SystemRuntimeException(ExceptionType.BUSINESS, "无法切换到组织:[" + groupKey + "]角色:[" + roleKey + "].");
		}

		UsGroup group = (UsGroup) service.findByPk(UsGroup.class.getName(), relationship.get("groupKey").toString());
		if (group == null) {
			throw new SystemRuntimeException(ExceptionType.BUSINESS, "归属的组织已被注销,无法登陆系统.");
		}
		UsRole role = (UsRole) service.findByPk(UsRole.class.getName(), relationship.get("roleKey").toString());
		if (role == null) {
			throw new SystemRuntimeException(ExceptionType.BUSINESS, "归属的角色已被注销,无法登陆系统.");
		}

		// 设置权限
		Set<String> priGroups = new HashSet<>();
		{
			// 角色固有权限组
			List<String> groupIds = service.queryHQL("select groupId from UsRolePriGroupRelate where roleKey = ?", role.getRoleKey());
			priGroups.addAll(groupIds);
		}
		{
			// 组织内角色特有权限组
			List<String> groupIds = service.queryHQL("select groupId from UsRoleGroupPriRelate where roleKey = ? and groupKey = ?", role.getRoleKey(), group.getGroupKey());
			priGroups.addAll(groupIds);
		}

		request.getSession().setAttribute(SessionAttributeKey.RELATION_SHIP.toString(), relationship);
		request.getSession().setAttribute(SessionAttributeKey.GROUP.toString(), group);
		request.getSession().setAttribute(SessionAttributeKey.ROLE.toString(), role);
		request.getSession().setAttribute(SessionAttributeKey.PRI_GROUP.toString(), priGroups);
		request.getSession().setAttribute(SessionAttributeKey.PRI_POINT_LIST.toString(), new HashSet<String>());
	}

	/**
	 * 校验当前用户是否可以访问平台
	 *
	 * @return
	 */
	public static boolean checkPlatformState() {
		// 系统级别用户不受平台状态限制
		if (isAdmin()) {
			return true;
		}
		// 平台暂停时返回false
		return !Platform.checkPause();
	}

	/**
	 * 校验当前用户密码
	 *
	 * @param password
	 * @return
	 */
	public static boolean checkUserPassword(String password) {
		if (StringUtils.isEmpty(password)) {
			return false;
		}
		UsUser user = (UsUser) ORMService.getInstance().findByPk(UsUser.class.getName(), getUser().getUid());
		return user.getPassword().equals(MD5.md5(password));
	}

	/**
	 * 获取当前用户语言
	 *
	 * @return
	 */
	public static String getCurrentLanguage() {
		if (SessionContext.getCurrent() != null && StringUtils.isNotEmpty(SessionContext.getCurrent().getString(SessionAttributeKey.LANGUAGE.toString()))) {
			return SessionContext.getCurrent().getString(SessionAttributeKey.LANGUAGE.toString());
		}
		String[] languages = StringUtils.split(Config.get("page.language", ""), ";");
		if (languages == null || (languages != null && languages.length == 0)) {// 没有配置语言选项时,默认中文
			return "zh";
		} else {
			return languages[0]; // 若配置语言选项,默认为第一个为默认语言
		}
	}

	/**
	 * 更改当前语言
	 *
	 * @return
	 */
	public static void changeLanguage(HttpServletRequest request) {
		String lanKey = RequestUtils.getStringValue(request, "lanKey");
		request.getSession().setAttribute(SessionAttributeKey.LANGUAGE.toString(), lanKey);
	}

}
