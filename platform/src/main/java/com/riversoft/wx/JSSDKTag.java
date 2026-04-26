/*
 * $HeadURL: $
 * $Id: $
 * Copyright (c) 2016 by Riversoft System, all rights reserved.
 */
package com.riversoft.wx;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.riversoft.core.script.function.FormatterFunction;
import com.riversoft.core.web.Actions.Util;
import com.riversoft.platform.SessionManager.SessionAttributeKey;
import com.riversoft.weixin.common.jsapi.JsAPISignature;
import com.riversoft.weixin.qy.jsapi.JsAPIs;
import com.riversoft.wx.mp.service.MpAppService;

/**
 * 微信JSSDK引入
 * 
 * @author woden
 */
@SuppressWarnings("serial")
public class JSSDKTag extends TagSupport {

	private static final Logger logger = LoggerFactory.getLogger(JSSDKTag.class);
	private static final String[] QY_API_LIST = new String[] { "chooseImage", "previewImage", "uploadImage", "downloadImage", "getNetworkType", "openLocation", "getLocation", "closeWindow",
			"scanQRCode" };
	private static final String[] MP_API_LIST = new String[] { "chooseImage", "previewImage", "uploadImage", "downloadImage", "getNetworkType", "openLocation", "getLocation", "closeWindow",
			"scanQRCode", "chooseWXPay" };

	private String url;
	private String apiList;

	@Override
	public int doEndTag() throws JspException {
		HttpServletRequest request = (HttpServletRequest) pageContext.getRequest();

		String wxType = (String) request.getSession().getAttribute(SessionAttributeKey.WX_TYPE.name());
		String wxKey = (String) request.getSession().getAttribute(SessionAttributeKey.WX_KEY.name());

		String[] apiListArray = new String[] {};
		JsAPISignature jsAPISignature = null;
		if (StringUtils.equalsIgnoreCase("agent", wxType)) {
			jsAPISignature = JsAPIs.defaultJsAPIs().createJsAPISignature(url);
			apiListArray = QY_API_LIST;
		} else if (StringUtils.equalsIgnoreCase("mp", wxType)) {
			jsAPISignature = com.riversoft.weixin.mp.jsapi.JsAPIs.with(MpAppService.getInstance().getAppSettingByPK(wxKey)).createJsAPISignature(url);
			apiListArray = MP_API_LIST;
		}

		if (StringUtils.isNotEmpty(apiList)) {
			apiListArray = StringUtils.split(apiList, ",");
		}

		request.setAttribute("apiList", FormatterFunction.formatJson(apiListArray));
		request.setAttribute("signature", jsAPISignature);
		request.setAttribute("fromWx", Util.fromWx(request));

		try {
			pageContext.include("/h5/common/jssdk.jsp");
		} catch (ServletException | IOException e) {
			logger.error("打印出错。", e);
			return EVAL_BODY_INCLUDE;
		}
		return SKIP_BODY;
	}

	/**
	 * @param url
	 *            the url to set
	 */
	public void setUrl(String url) {
		this.url = url;
	}

	/**
	 * @param apiList
	 *            the apiList to set
	 */
	public void setApiList(String apiList) {
		this.apiList = apiList;
	}

}
