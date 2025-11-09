package com.riversoft.wx.mp.service;

import java.util.Map;

import com.riversoft.core.BeanFactory;
import com.riversoft.core.db.ORMService;
import com.riversoft.weixin.pay.base.PaySetting;
import com.riversoft.wx.mp.MpAppSetting;
import com.riversoft.wx.mp.MpPaySetting;
import com.riversoft.wx.mp.SpecialAccessTokenHolder;

/**
 * Created by exizhai on 12/7/2015.
 */
public class MpAppService {

	public static MpAppService getInstance() {
		return BeanFactory.getInstance().getSingleBean(MpAppService.class);
	}

	/**
	 * 根据逻辑名获取appSetting
	 * 
	 * @param config
	 * @return
	 */
	public MpAppSetting getAppSetting(Map<String, Object> config) {
		if (config != null) {
			return buildAppSetting(config);
		}
		return null;
	}

	/**
	 * 根据逻辑名获取appSetting
	 *
	 * @param mpKey
	 *            逻辑名
	 * @return
	 */
	public MpAppSetting getAppSettingByPK(String mpKey) {
		Map<String, Object> config = ((Map<String, Object>) ORMService.getInstance().findByPk("WxMp", mpKey));
		if (config != null) {
			return buildAppSetting(config);
		}

		return null;
	}

	/**
	 * 根据appId获取appSetting
	 *
	 * @param appId
	 * @return
	 */
	public MpAppSetting getAppSettingByAppID(String appId) {
		Map<String, Object> config = ((Map<String, Object>) ORMService.getInstance().findByKey("WxMp", "appId", appId));
		if (config != null) {
			return buildAppSetting(config);
		}

		return null;
	}

	/**
	 * 根据逻辑名获取paySetting
	 *
	 * @param mpKey
	 *            逻辑名
	 * @return
	 */
	public PaySetting getPaySettingByPK(String mpKey) {
		Map<String, Object> config = ((Map<String, Object>) ORMService.getInstance().findByPk("WxMpPay", mpKey));
		if (config != null) {
			return buildPaySetting(config);
		}

		return null;
	}

	/**
	 * 根据appId获取paySetting
	 *
	 * @param appId
	 * @return
	 */
	public MpPaySetting getPaySettingByAppId(String appId) {
		Map<String, Object> config = ((Map<String, Object>) ORMService.getInstance().findByKey("WxMpPay", "appId",appId));
		if (config != null) {
			return buildPaySetting(config);
		}

		return null;
	}

	public void updateStatus(String appId, int status) {
		Map<String, Object> config = ((Map<String, Object>) ORMService.getInstance().findByKey("WxMp", "appId", appId));
		if (config != null) {
			config.put("status", status);
			ORMService.getInstance().update(config);
		}
	}

	private MpAppSetting buildAppSetting(Map<String, Object> config) {
		MpAppSetting appSetting = new MpAppSetting();
		appSetting.setMpKey((String) config.get("mpKey"));
		appSetting.setAppId((String) config.get("appId"));
		appSetting.setToken((String) config.get("token"));
		appSetting.setAesKey((String) config.get("encodingAESKey"));
		appSetting.setSecret((String) config.get("appSecret"));
		if (config.get("accessTokenUrl") != null && config.get("accessTokenUrl") != "") { //若有设置特殊的accessTokenUrl, 需要设置特殊的AccessTokenHolder
			appSetting.setTokenHolderClass(SpecialAccessTokenHolder.class.getName());  //反射类
		}
		appSetting.setScope("snsapi_base");
		return appSetting;
	}

	private MpPaySetting buildPaySetting(Map<String, Object> config) {
		MpPaySetting paySetting = new MpPaySetting();
		paySetting.setMpKey((String) config.get("mpKey"));
		paySetting.setAppId((String) config.get("appId"));
		paySetting.setMchId((String) config.get("mchId"));
		paySetting.setKey((String) config.get("paySecret"));
		paySetting.setCertPath((String) config.get("certPath"));
		paySetting.setCertPassword((String) config.get("certPassword"));

		return paySetting;
	}

}
