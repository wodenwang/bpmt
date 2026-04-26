package com.riversoft.module.wx;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;

import com.riversoft.core.Config;
import com.riversoft.core.db.DataCondition;
import com.riversoft.core.db.DataPO;
import com.riversoft.core.db.DataPackage;
import com.riversoft.core.db.ORMService;
import com.riversoft.core.exception.ExceptionType;
import com.riversoft.core.exception.SystemRuntimeException;
import com.riversoft.core.web.Actions;
import com.riversoft.core.web.RequestUtils;
import com.riversoft.core.web.annotation.ActionAccess;
import com.riversoft.platform.SessionManager;

/**
 * 微信URL管理 Created by exizhai on 2/25/2016.
 */
@ActionAccess(level = ActionAccess.SafeLevel.DEV_R)
public class UrlConfigAction {

	/**
	 * URL管理首页
	 *
	 * @param request
	 * @param response
	 */
	public void index(HttpServletRequest request, HttpServletResponse response) {
		Actions.includePage(request, response, Actions.Util.getPagePath(request, "main.jsp"));
	}

	/**
	 * 列表
	 *
	 * @param request
	 * @param response
	 */
	public void list(HttpServletRequest request, HttpServletResponse response) {
		// 获取分页信息
		int start = Actions.Util.getStart(request);
		int limit = Actions.Util.getLimit(request);

		// 获取排序信息
		String field = Actions.Util.getSortField(request);
		String dir = Actions.Util.getSortDir(request);

		// 查询条件
		DataCondition condition = new DataCondition(Actions.Util.buildQueryMap(new HashMap<String, Object>(), request));
		condition.setOrderBy(field, dir);

		DataPackage dp = ORMService.getInstance().queryPackage("WxUrl", start, limit, condition.toEntity());
		// 设置到页面
		request.setAttribute("dp", dp);

		String domain = (StringUtils.equalsIgnoreCase(Config.get("wx.net.https", "false"), "true") ? "https://" : "http://") + Config.get("wx.net.domain") + "/wx/url/";// 网址前缀
		request.setAttribute("domain", domain);

		Actions.includePage(request, response, Actions.Util.getPagePath(request, "list.jsp"));
	}

	/**
	 * 删除url
	 *
	 * @param request
	 * @param response
	 */
	@ActionAccess(level = ActionAccess.SafeLevel.DEV_W)
	public void delete(HttpServletRequest request, HttpServletResponse response) {
		String urlKey = RequestUtils.getStringValue(request, "urlKey");

		ORMService.getInstance().removeByPk("WxUrl", urlKey);
		Actions.redirectInfoPage(request, response, "删除成功.");
	}

	/**
	 * 新建(页面)
	 *
	 * @param request
	 * @param response
	 */
	@SuppressWarnings("unchecked")
	public void createZone(HttpServletRequest request, HttpServletResponse response) {
		String domain = (StringUtils.equalsIgnoreCase(Config.get("wx.net.https", "false"), "true") ? "https://" : "http://") + Config.get("wx.net.domain") + "/wx/url/";// 网址前缀
		request.setAttribute("domain", domain);

		Actions.includePage(request, response, Actions.Util.getPagePath(request, "form.jsp"));
	}

	/**
	 * 编辑(页面)
	 *
	 * @param request
	 * @param response
	 */
	@SuppressWarnings("unchecked")
	public void updateZone(HttpServletRequest request, HttpServletResponse response) {
		String urlKey = RequestUtils.getStringValue(request, "urlKey");
		Map<String, Object> vo = (Map<String, Object>) ORMService.getInstance().findByPk("WxUrl", urlKey);
		if (vo == null) {
			throw new SystemRuntimeException(ExceptionType.BUSINESS, "URL[" + urlKey + "]不存在.");
		}
		request.setAttribute("vo", vo);

		String domain = (StringUtils.equalsIgnoreCase(Config.get("wx.net.https", "false"), "true") ? "https://" : "http://") + Config.get("wx.net.domain") + "/wx/url/";// 网址前缀
		request.setAttribute("domain", domain);

		Actions.includePage(request, response, Actions.Util.getPagePath(request, "form.jsp"));
	}

	/**
	 * 提交表单
	 *
	 * @param request
	 * @param response
	 */
	@ActionAccess(level = ActionAccess.SafeLevel.DEV_W)
	public void submitForm(HttpServletRequest request, HttpServletResponse response) {
		String urlKey = RequestUtils.getStringValue(request, "urlKey");

		DataPO po;
		Map<String, Object> entity = (Map<String, Object>) ORMService.getInstance().findByPk("WxUrl", urlKey);

		boolean editFlag;
		if (entity == null) {
			po = new DataPO("WxUrl");
			po.set("urlKey", urlKey);
			editFlag = false;
		} else {
			po = new DataPO("WxUrl", entity);
			editFlag = true;
		}

		po.set("createUid", SessionManager.getUser().getUid());
		int wxType = RequestUtils.getIntegerValue(request, "wxType");
		po.set("wxType", wxType);

		if (wxType == 1) {// 公众号
			String wxKey = RequestUtils.getStringValue(request, "wxKey");
			String wxScope = RequestUtils.getStringValue(request, "wxScope");
			if (StringUtils.isEmpty(wxKey)) {
				throw new SystemRuntimeException(ExceptionType.BUSINESS, "公众号为必填");
			}
			po.set("wxKey", wxKey);
			po.set("wxScope", wxScope);

		}
		po.set("description", RequestUtils.getStringValue(request, "description"));
		po.set("action", RequestUtils.getStringValue(request, "action"));
		po.set("paramType", RequestUtils.getIntegerValue(request, "paramType"));
		po.set("paramScript", RequestUtils.getStringValue(request, "paramScript"));

		if (editFlag) {
			ORMService.getInstance().update(po.toEntity());
		} else {
			ORMService.getInstance().save(po.toEntity());
		}

		Actions.redirectInfoPage(request, response, editFlag ? "编辑成功." : "新增成功.");
	}
}
