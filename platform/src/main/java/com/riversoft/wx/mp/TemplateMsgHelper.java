/*
 * $HeadURL: $
 * $Id: $
 * Copyright (c) 2016 by Riversoft System, all rights reserved.
 */
package com.riversoft.wx.mp;

import com.riversoft.core.Config;
import com.riversoft.core.db.JdbcService;
import com.riversoft.core.db.ORMService;
import com.riversoft.util.jackson.JsonMapper;
import com.riversoft.weixin.mp.base.AppSetting;
import com.riversoft.weixin.mp.template.Data;
import com.riversoft.weixin.mp.template.Templates;
import com.riversoft.weixin.mp.template.MiniProgram;
import com.riversoft.wx.mp.model.TemplateMsgLogModelKeys;
import com.riversoft.wx.mp.service.MpAppService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 公众号模板消息
 * 
 * @author woden
 *
 */
public class TemplateMsgHelper {

	private AppSetting appSetting;
	
	private static Logger logger = LoggerFactory.getLogger(TemplateMsgHelper.class);

	public TemplateMsgHelper() {
	}

	public TemplateMsgHelper(String appKey) {
		this.appSetting = MpAppService.getInstance().getAppSettingByPK(appKey);
	}

	public TemplateMsgHelper(AppSetting appSetting) {
		this.appSetting = appSetting;
	}

	public void setAppSetting(AppSetting appSetting) {
		this.appSetting = appSetting;
	}

	/**
	 * 发送模板消息
	 *
	 * @param message
	 *            要发送的模板消息
	 * @return 返回模板消息ID
	 */
	public long send(Map<String, Object> message) {
		MiniProgram miniProgram = new MiniProgram();
		String toUser = (String) message.get("user");
		String templateId = (String) message.get("template");
		String url = (String) message.get("url");
		String pagePath = (String) message.get("pagepath");
		Boolean miniProgramType = false; //小程序标示位,默认false
		if (message.get("miniprogram") != null) {
			miniProgramType = Boolean.valueOf(message.get("miniprogram").toString()); 
		}
		if (miniProgramType & pagePath == null) {
			logger.error("设置小程序跳转,但无pagepath传参,请检查");
		}
		
		Map<String, Map<String, String>> params = (Map<String, Map<String, String>>) message.get("data");
		Map<String, Data> data = new HashMap<>();
		if (params != null) {
			for (String type : params.keySet()) {
				Map<String, String> value = params.get(type);
				Data item = new Data(value.get("value"), value.get("color"));
				data.put(type, item);
			}
		}

		if (miniProgramType & pagePath != null) { // 判断有无小程序页面传参
			miniProgram.setAppId((String) Config.get("wx.app.appId"));
			miniProgram.setPagePath(pagePath); 
		} 		
		 // 若url和小程序两者都传参, 优先采取小程序, 若微信版本不支持会启用url
		long msgId = Templates.with(appSetting).send(toUser, templateId, url, miniProgram, data);

		// 登记到DB
		logMessage(msgId, templateId, toUser, data);

		return msgId;
	}

	private void logMessage(long msgId, String templateId, String openUser, Map<String, Data> items) {
		String mpKey = MpAppService.getInstance().getAppSettingByAppID(appSetting.getAppId()).getMpKey();
		Map<String, Object> config = (Map<String, Object>) ORMService.getInstance().findByPk("WxMp", mpKey);
		String tableName = (String) config.get("templateMsgLogTable");
		if (StringUtils.isNotBlank(tableName)) {
			StringBuffer sql = new StringBuffer();
			sql.append("insert into ").append(tableName).append(" (");
			sql.append(TemplateMsgLogModelKeys.MSG_ID.name()).append(",");

			sql.append(TemplateMsgLogModelKeys.MP_KEY.name()).append(",");
			sql.append(TemplateMsgLogModelKeys.OPEN_ID.name()).append(",");
			sql.append(TemplateMsgLogModelKeys.LOG_DATE.name()).append(",");

			sql.append(TemplateMsgLogModelKeys.TEMPLATE_ID.name()).append(",");
			sql.append(TemplateMsgLogModelKeys.MSG_CONTENT.name()).append(",");
			sql.append(TemplateMsgLogModelKeys.RESULT.name());

			sql.append(") values (?,?,?,?,?,?,?)");

			JdbcService.getInstance().executeSQL(sql.toString(), msgId, mpKey, openUser, new Date(), templateId,
					JsonMapper.defaultMapper().toJson(items), 0);
		}
	}
}
