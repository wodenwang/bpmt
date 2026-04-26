package com.riversoft.wx.qy;

import com.riversoft.core.context.RequestContext;
import com.riversoft.core.web.Actions.Keys;
import com.riversoft.weixin.common.jsapi.JsAPISignature;
import com.riversoft.weixin.qy.agent.Agents;
import com.riversoft.weixin.qy.contact.Departments;
import com.riversoft.weixin.qy.contact.Tags;
import com.riversoft.weixin.qy.contact.Users;
import com.riversoft.weixin.qy.jsapi.JsAPIs;
import com.riversoft.weixin.qy.shake.Shakes;

/**
 * Created by exizhai on 12/28/2015.
 */
public class QyHelper {

	// 改成单例
	private static final QyHelper qyHelper = new QyHelper();

	private QyHelper() {

	}

	public static QyHelper getInstance() {
		return qyHelper;
	}

	public QyMediaHelper getMedia() {
		return QyMediaHelper.getInstance();
	}

	private JsAPIs jsAPIs;

	private synchronized JsAPIs getJsAPIs() {
		if (jsAPIs == null) {
			jsAPIs = JsAPIs.defaultJsAPIs();
		}
		return jsAPIs;
	}

	/**
	 * 获取jssdk验证串
	 * 
	 * @param url
	 * @return
	 */
	public JsAPISignature signature(String url) {
		return getJsAPIs().createJsAPISignature(url);
	}

	/**
	 * 获取jssdk验证串
	 * 
	 * @return
	 */
	public JsAPISignature signature() {
		String url = RequestContext.getCurrent().getString(Keys.FULL_URL.toString());
		return signature(url);
	}

	/**
	 * 企业号管理组权限验证
	 * 
	 * @param url
	 * @return
	 */
	public JsAPISignature groupSignature(String url) {
		return getJsAPIs().createJsAPIGroupSignature(url);
	}

	/**
	 * 企业号管理组权限验证
	 * 
	 * @return
	 */
	public JsAPISignature groupSignature() {
		String url = RequestContext.getCurrent().getString(Keys.FULL_URL.toString());
		return getJsAPIs().createJsAPIGroupSignature(url);
	}

	// =================其他SDK基础能力,直接提供

	public Users getUsers() {
		return Users.defaultUsers();
	}

	public Departments getDepartments() {
		return Departments.defaultDepartments();
	}

	public Tags getTags() {
		return Tags.defaultTags();
	}

	public Agents getAgents() {
		return Agents.defaultAgents();
	}

	public Shakes getShakes() {
		return Shakes.defaultShakes();
	}

}
