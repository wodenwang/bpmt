package com.riversoft.wx.qy;

import com.riversoft.weixin.pay.base.PaySetting;
import com.riversoft.weixin.pay.redpack.RedPacks;
import com.riversoft.weixin.pay.redpack.bean.RedPackRequest;
import com.riversoft.weixin.pay.redpack.bean.RedPackResponse;
import com.riversoft.weixin.pay.redpack.bean.RedPackResult;
import com.riversoft.weixin.qy.oauth2.QyOAuth2s;
import com.riversoft.wx.builder.RedPackRequestBuilder;
import com.riversoft.wx.qy.service.QyAppService;

import java.net.Inet4Address;
import java.net.UnknownHostException;
import java.util.Map;

/**
 * Created by exizhai on 12/28/2015.
 */
public class AgentPayHelper {

    private int agentId;

    public AgentPayHelper(int agentId) {
        this.agentId = agentId;
    }

    public RedPackResponse red(Map<String, Object> params) {
        if(!params.containsKey("number")) {
            params.put("number", 1);
        }

        int number = (Integer) params.get("number");
        if(number == 1) {
            return single(params);
        } else {
            return group(params);
        }
    }

    /**
     * 红包查询
     * @param billNumber
     * @return
     */
    public RedPackResult query(String billNumber) {
        PaySetting paySetting = QyAppService.getInstance().getPaySetting();
        return RedPacks.with(paySetting).query(billNumber);
    }

    /**
     * 发单个红包
     *
     * @param params 单个红包所需参数
     * @return
     */
    private RedPackResponse single(Map<String, Object> params) {
        PaySetting paySetting = QyAppService.getInstance().getPaySetting();
        RedPackRequest redPackRequest = RedPackRequestBuilder.build(paySetting.getMchId(), params);
        redPackRequest.setNumber(1);
        try {
            redPackRequest.setClientIp(Inet4Address.getLocalHost().getHostAddress());
        } catch (UnknownHostException e) {
            redPackRequest.setClientIp("127.0.0.1");
        }
        Map<String, String> openIds = QyOAuth2s.defaultOAuth2s().toOpenId(agentId, params.get("user").toString());
        redPackRequest.setAppId(openIds.get("appid"));
        redPackRequest.setOpenId(openIds.get("openid"));

        return RedPacks.with(paySetting).sendSingle(redPackRequest);
    }

    /**
     * 发裂变红包
     *
     * @param params 裂变红包所需参数
     * @return
     */
    private RedPackResponse group(Map<String, Object> params) {
        PaySetting paySetting = QyAppService.getInstance().getPaySetting();
        RedPackRequest redPackRequest = RedPackRequestBuilder.build(paySetting.getMchId(), params);
        redPackRequest.setNumber((Integer) params.get("number"));
        Map<String, String> openIds = QyOAuth2s.defaultOAuth2s().toOpenId(agentId, params.get("user").toString());
        redPackRequest.setAppId(openIds.get("appid"));
        redPackRequest.setOpenId(openIds.get("openid"));

        return RedPacks.with(paySetting).sendGroup(redPackRequest);
    }
}
