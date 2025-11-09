package com.riversoft.platform.web;

import java.lang.reflect.Method;
import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;

import com.riversoft.core.db.ORMService;
import com.riversoft.core.exception.ExceptionType;
import com.riversoft.core.exception.SystemRuntimeException;
import com.riversoft.core.web.Actions.Util;
import com.riversoft.core.web.annotation.ActionAccess;
import com.riversoft.platform.SessionManager;
import com.riversoft.platform.SessionManager.SessionAttributeKey;
import com.riversoft.platform.db.ORMAdapterService;
import com.riversoft.weixin.common.exception.WxRuntimeException;
import com.riversoft.weixin.common.oauth2.AccessToken;
import com.riversoft.weixin.common.oauth2.OpenUser;
import com.riversoft.weixin.mp.oauth2.MpOAuth2s;
import com.riversoft.weixin.mp.user.Users;
import com.riversoft.weixin.mp.user.bean.User;
import com.riversoft.weixin.qy.oauth2.QyOAuth2s;
import com.riversoft.weixin.qy.oauth2.bean.QyUser;
import com.riversoft.wx.mp.MpAppSetting;
import com.riversoft.wx.mp.service.MpAppService;

/**
 * Created by exizhai on 9/21/2015.
 */
public class WxActionAspect extends ActionAspect {

	private Logger logger = LoggerFactory.getLogger(WxActionAspect.class);

	private int secureCookieMaxAgeInSecond = 86400;// 1天

	private AccessTokenManager accessTokenManager = null;

	@Required
	public void setAccessTokenManager(AccessTokenManager accessTokenManager) {
		this.accessTokenManager = accessTokenManager;
	}

	@Override
	public Object doAround(ProceedingJoinPoint joinPoint) throws Throwable {
		Object[] args = joinPoint.getArgs();// 获取入参
		if (args == null || args.length != 2) {
			return joinPoint.proceed();// 不处理
		}

		if (!(args[0] instanceof HttpServletRequest && args[1] instanceof HttpServletResponse)) {
			return joinPoint.proceed();// 不处理
		}

		// 获取参数
		HttpServletRequest request = (HttpServletRequest) args[0];
		HttpServletResponse response = (HttpServletResponse) args[1];

		String strPathInfo = request.getServletPath();// 调用地址
		String methodName = joinPoint.getSignature().getName();// 调用方法
		logger.debug("request path: {}, method: {}", strPathInfo, methodName);

		initContext(request);

		// 非微信,不需要走此过滤器
		if (!Util.fromWx(request)) {
			return joinPoint.proceed();
		}

		// 前置验证处理
		ActionAccess access = null;
		Class<?> actionClass = joinPoint.getTarget().getClass();
		Method method = actionClass.getMethod(methodName, HttpServletRequest.class, HttpServletResponse.class);
		access = method.getAnnotation(ActionAccess.class);
		if (access == null) {// 方法无配置则使用类配置
			access = actionClass.getAnnotation(ActionAccess.class);
		}

		/* ======================================== */
		// 无需登录
		if (access != null && !access.login()) {
			return joinPoint.proceed();
		}

		// 来源方式
		String wxType = (String) request.getSession().getAttribute(SessionAttributeKey.WX_TYPE.name());
		/* ======================================== */
		// 已登录的需要判断登录目标是否匹配
		if (SessionManager.checkUserLogin()) {
			logger.debug("用户已经登录过");

			// 根据来源判断是否登录
			String key = (String) request.getSession().getAttribute(SessionAttributeKey.WX_KEY.name());
			if ("mp".equalsIgnoreCase(wxType)) {
				String userCookieName = getMpCookieName(key);
				String openId = extractUserName(request, response, userCookieName);
				if (StringUtils.isNotEmpty(openId) && StringUtils.equals(openId, SessionManager.getUser().getOpenId())) {
					return joinPoint.proceed();
				}
			} else if ("agent".equalsIgnoreCase(wxType)) {
				String agentKey = (String) request.getSession().getAttribute(SessionAttributeKey.WX_KEY.name());
				if (StringUtils.isEmpty(agentKey)) {
					agentKey = "_default";
				}
				String userCookieName = getQyCookieName(agentKey);
				String uid = extractUserName(request, response, userCookieName);
				if (StringUtils.isNotEmpty(uid) && StringUtils.equals(uid, SessionManager.getUser().getUid())) {
					return joinPoint.proceed();
				}
			}

			// 否则就继续走登录的模式
			logger.debug("登录过,但是切换到不同公众号.");
		}

		/* ======================================== */
		// 公众号
		if (StringUtils.equalsIgnoreCase(wxType, "mp")) {
			String mpKey = (String) request.getSession().getAttribute(SessionAttributeKey.WX_KEY.name());
			String userCookieName = getMpCookieName(mpKey);
			Map<String, Object> mpConfig = (Map<String, Object>) ORMService.getInstance().findByPk("WxMp", mpKey);
			MpAppSetting mpAppSetting = MpAppService.getInstance().getAppSetting(mpConfig);
			String visitorTable = (String) mpConfig.get("visitorTable");
			String visitorTagTable = (String) mpConfig.get("visitorTagTable");

			logger.debug("用户来自微信客户端:{}", mpKey);
			logger.debug("用户没有登录:{}.", mpKey);

			String openId = extractUserName(request, response, userCookieName);
			if (StringUtils.isNotEmpty(openId)) { // 基于cookie
				Map<String, Object> visitor = (Map<String, Object>) ORMAdapterService.getInstance().findByPk(visitorTable, openId);

				// cookie登录
				SessionManager.doUserLogin(request, WxUserManager.buildMpUser(visitor, (String) mpConfig.get("groupKey"), (String) mpConfig.get("roleKey")));
				setUserCookieOnResponse(response, userCookieName, openId);
				logger.info("用户:{}免登录成功", openId);
				return joinPoint.proceed();
			}

			// 微信传入处理
			String code = request.getParameter("code");
			if (StringUtils.isNotEmpty(code)) {
				logger.debug("微信公众号code自动登录");
				String userOpenId = mpCodeLogin(request, mpKey, code);
				setUserCookieOnResponse(response, userCookieName, userOpenId);
				return joinPoint.proceed();
			}

			logger.debug("cookie不存在");
			String wxScope = (String) request.getSession().getAttribute(SessionAttributeKey.WX_SCOPE.name());
			if (StringUtils.isEmpty(wxScope)) {
				wxScope = "snsapi_base";
			}
			String redirectUrl = MpOAuth2s.with(mpAppSetting).authenticationUrl(Util.getFullURL(request), wxScope);
			logger.info("重定向到微信:{}", redirectUrl);
			response.sendRedirect(redirectUrl);
			return null;
		}

		/* ======================================== */
		// 企业内部用户,企业号权限登录
		if (StringUtils.equalsIgnoreCase(wxType, "agent")) {
			String agentKey = (String) request.getSession().getAttribute(SessionAttributeKey.WX_KEY.name());
			if (StringUtils.isEmpty(agentKey)) {
				agentKey = "_default";
			}
			String userCookieName = getQyCookieName(agentKey);
			logger.debug("用户来自微信企业号,应用:{}", agentKey);

			// 基于cookie
			String uid = extractUserName(request, response, userCookieName);
			if (StringUtils.isNotEmpty(uid)) {
				logger.debug("cookie 中存在用户{}:", uid);
				SessionManager.doUserLogin(request, uid);
				logger.info("用户:{}免登录{}成功", uid, agentKey);
				return joinPoint.proceed();
			}

			// 微信传入处理
			String code = request.getParameter("code");
			if (StringUtils.isNotEmpty(code)) {
				try {
					QyUser qyUser = QyOAuth2s.defaultOAuth2s().userInfo(code);
					logger.info("获取微信用户信息: {}", qyUser.getUserId());
					SessionManager.doUserLogin(request, qyUser.getUserId());
					setUserCookieOnResponse(response, userCookieName, qyUser.getUserId());
					logger.info("用户:{}免登录{}成功", qyUser.getUserId(), agentKey);
				} catch (Exception e) {
					logger.error("用户:{}免登录{}失败: {}", code, agentKey, e);
				}
				return joinPoint.proceed();
			}

			// 初次连接企业号,重定向
			logger.debug("初次登陆:{}, 重定向到微信oauth.", agentKey);
			String redirectUrl = QyOAuth2s.defaultOAuth2s().authenticationUrl(Util.getFullURL(request), null);
			logger.info("重定向到微信:{}", redirectUrl);
			response.sendRedirect(redirectUrl);
			return null;
		}

		//无效链接,直接略过
		return joinPoint.proceed();
		//throw new SystemRuntimeException(ExceptionType.BUSINESS, "无效URL超链接.");
	}

	// cookie name: u0-agentKey
	private String getQyCookieName(String agentKey) {
		return "u0-" + agentKey;
	}

	// cookie name: u1-mpKey
	private String getMpCookieName(String mpKey) {
		return "u1-" + mpKey;
	}

	// cookie name: u1-mpKey
	private String getOpenCookieName() {
		return "u2-open";
	}

	protected String extractUserName(HttpServletRequest request, HttpServletResponse httpServletResponse, String cookieName) {
		String username = null;
		Cookie[] a = request.getCookies();
		if (a != null) {
			for (Cookie c : a) {
				logger.info("cookie: {} -> {}", c.getName(), c.getValue());
				if (c.getName().equals(cookieName)) {
					username = extractUserName(c.getValue());
					if (username != null) {
						logger.info("retrieved username: {} from cookie :{}", username, c.getValue());
						if (shouldResetCookie(c.getValue())) {
							setUserCookieOnResponse(httpServletResponse, cookieName, username);
						}
						break;
					}
				}
			}
		}
		return username;
	}

	/**
	 * 要找个更安全的方式存储cookie
	 * 
	 * @param httpServletResponse
	 * @param name
	 * @param value
	 *
	 */
	private void setUserCookieOnResponse(HttpServletResponse httpServletResponse, String name, String value) {
		Cookie cookie = new Cookie(name, System.currentTimeMillis() + ":" + value);
		cookie.setPath("/");
		cookie.setMaxAge(secureCookieMaxAgeInSecond);
		cookie.setHttpOnly(true);
		logger.info("set cookie: {} -> {}", cookie.getName(), cookie.getValue());
		httpServletResponse.addCookie(cookie);
	}

	private boolean shouldResetCookie(String value) {
		try {
			long timestamp = Long.parseLong(value.substring(0, value.indexOf(':')));
			return System.currentTimeMillis() - timestamp > secureCookieMaxAgeInSecond * 1000L;
		} catch (Exception e) {

		}
		return true;
	}

	private String extractUserName(String value) {
		int nameStart = value.indexOf(':');
		return nameStart > -1 && nameStart < value.length() ? value.substring(nameStart + 1) : value;
	}

	/**
	 * 微信公众号code自动登录
	 * 
	 * @param request
	 * @param mpKey
	 * @param code
	 */
	public String mpCodeLogin(HttpServletRequest request, String mpKey, String code) {
		Map<String, Object> mpConfig = (Map<String, Object>) ORMService.getInstance().findByPk("WxMp", mpKey);
		MpAppSetting mpAppSetting = MpAppService.getInstance().getAppSetting(mpConfig);

		String visitorTable = (String) mpConfig.get("visitorTable");
		String visitorTagTable = (String) mpConfig.get("visitorTagTable");

		AccessToken accessToken = MpOAuth2s.with(mpAppSetting).getAccessToken(code);
		String openId = accessToken.getOpenId();

		Map<String, Object> visitor = (Map<String, Object>) ORMAdapterService.getInstance().findByPk(visitorTable, openId);
		// 表没有数据则调users接口并创建
		if (visitor == null) {
			User user = Users.with(mpAppSetting).get(openId);
			if (user != null && StringUtils.isNotEmpty(user.getNickName())) {
				visitor = WxUserManager.buildVisitor(mpKey, visitorTable, user);
			}
		}

		// 实在不行就只拿openid和nickname组装visitor,能装入多少就多少
		if (visitor == null) {
			try {
				OpenUser openUser = MpOAuth2s.with(mpAppSetting).userInfo(accessToken.getAccessToken(), accessToken.getOpenId());
				visitor = WxUserManager.buildVisitor(mpKey, visitorTable, openUser);
			} catch (WxRuntimeException e) {
				throw new SystemRuntimeException(ExceptionType.BUSINESS, "用户没有关注公众号,无法获取信息.");
			}
		}

		// 设值登录
		SessionManager.doUserLogin(request, WxUserManager.buildMpUser(visitor, (String) mpConfig.get("groupKey"), (String) mpConfig.get("roleKey")));

		return openId;
	}

}
