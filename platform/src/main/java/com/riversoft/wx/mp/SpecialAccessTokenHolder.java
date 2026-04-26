package com.riversoft.wx.mp;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ser.std.StdKeySerializers.Default;
import com.riversoft.core.db.ORMService;
import com.riversoft.weixin.common.AccessToken;
import com.riversoft.weixin.common.AccessTokenHolder;
import com.riversoft.weixin.common.DefaultAccessTokenHolder;

/**
 * 
 * @author Chris
 * 该方法为特殊获取accessToken, 可以从外部一个url接入一个accessToken, 便于多应用管理
 */

public class SpecialAccessTokenHolder extends DefaultAccessTokenHolder {
    
	private static Logger logger = LoggerFactory.getLogger(SpecialAccessTokenHolder.class);
	
	private String appId = "";
	
	public SpecialAccessTokenHolder(){
		super(null,null,null);
	}
	
	public SpecialAccessTokenHolder(String tokenUrl, String clientId, String clientSecret) {
		super(tokenUrl, clientId, clientSecret);
	}
	
    public void setClientId(String clientId) {
        super.setClientId(clientId);
        this.appId = clientId;
    }
	
    public void setTokenUrl(String tokenUrl) {
		Map<String, Object> config = ((Map<String, Object>) ORMService.getInstance().findByKey("WxMp", "appId", appId));
		logger.debug("新接口的地址为[{}]", config.get("accessTokenUrl"));
		if (config != null) {
		   if (config.get("accessTokenUrl") != null && config.get("accessTokenUrl") != "") {
			super.setTokenUrl((String) config.get("accessTokenUrl"));
		   }
		} else {
    	super.setTokenUrl(tokenUrl);
		}
    }
}
