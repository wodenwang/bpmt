package com.riversoft.wx.qy.service;

import com.riversoft.core.BeanFactory;
import com.riversoft.core.Config;
import com.riversoft.core.db.ORMService;
import com.riversoft.weixin.pay.base.PaySetting;
import com.riversoft.weixin.qy.base.AgentSetting;

import java.util.Map;

/**
 * 企业号应用相关服务
 * Created by exizhai on 11/1/2015.
 */
public class QyAppService {

    public static QyAppService getInstance() {
        return BeanFactory.getInstance().getSingleBean(QyAppService.class);
    }

    public AgentSetting getAgentSetting(int agentId) {
        Map<String, Object> config = ((Map<String, Object>) ORMService.getInstance().findByKey("WxAgent", "agentId", agentId));
        if (config != null) {
            return buildAgent(config);
        }

        return null;
    }

    public AgentSetting getAgentSetting(String agentKey) {
        Map<String, Object> config = ((Map<String, Object>) ORMService.getInstance().findByPk("WxAgent", agentKey));
        if (config != null) {
            return buildAgent(config);
        }

        return null;
    }

    private AgentSetting buildAgent(Map<String, Object> config) {
        AgentSetting agentSetting = new AgentSetting();
        agentSetting.setName(config.get("agentKey").toString());
        agentSetting.setAgentId(Integer.valueOf(config.get("agentId").toString()));
        agentSetting.setToken(config.get("token").toString());
        agentSetting.setAesKey(config.get("encodingAESKey").toString());
        return agentSetting;
    }

    public void updateAgentStatus(int agentId, int status) {
        Map<String, Object> config = ((Map<String, Object>) ORMService.getInstance().findByKey("WxAgent", "agentId", agentId));
        if (config != null) {
            config.put("status", status);
            ORMService.getInstance().update(config);
        }
    }

    /**
     * 获取支付相关配置
     *
     * @return
     */
    public PaySetting getPaySetting() {
        //企业号支付,移到数据库?
        if (Boolean.valueOf(Config.get("wx.qy.pay.flag", "false"))) {
            String mchId = Config.get("wx.qy.pay.mchId");
            String key = Config.get("wx.qy.pay.key");
            String certPath = Config.get("wx.qy.pay.certPath");
            String certPassword = Config.get("wx.qy.pay.certPassword");

            PaySetting qydevPay = new PaySetting();
            qydevPay.setKey(key);
            qydevPay.setMchId(mchId);
            qydevPay.setCertPath(certPath);
            qydevPay.setCertPassword(certPassword);

            return qydevPay;
        }
        return null;
    }
}
