package com.riversoft.wx;

import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.riversoft.core.Config;
import com.riversoft.core.db.ORMService;
import com.riversoft.core.exception.ExceptionType;
import com.riversoft.core.exception.SystemRuntimeException;
import com.riversoft.core.script.annotation.ScriptSupport;
import com.riversoft.wx.app.AppHelper;
import com.riversoft.wx.mp.MpHelper;
import com.riversoft.wx.qy.AgentHelper;
import com.riversoft.wx.qy.QyHelper;

/**
 * Created by exizhai on 12/11/2015.
 */
@ScriptSupport("wx")
public class WxHelper {

	public AgentHelper agent(String key) {
		Map<String, Object> config = ((Map<String, Object>) ORMService.getInstance().findByPk("WxAgent", key));
		if (config == null) {
			throw new SystemRuntimeException(ExceptionType.WX, "agent 配置不存在.");
		}
		String corpId = Config.get("wx.qy.corpId");
		String secret = StringUtils.isNotEmpty((String) config.get("agentSecret")) ? (String) config.get("agentSecret") : Config.get("wx.qy.corpSecret");
		return new AgentHelper(corpId, secret, (Integer) config.get("agentId"));
	}

	public MpHelper mp(String key) {
		return new MpHelper(key);
	}

	/**
	 * 获取企业号资源管理
	 * 
	 * @return
	 */
	public QyHelper getQy() {
		return QyHelper.getInstance();
	}

	/**
	 * 获取小程序资源管理
	 * 
	 * @return
	 */
	public AppHelper app() {
		return new AppHelper();
	}

	// TODO 后续考虑单例
}
