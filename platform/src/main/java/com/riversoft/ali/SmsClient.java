package com.riversoft.ali;

import com.riversoft.core.BeanFactory;
import com.riversoft.core.exception.ExceptionType;
import com.riversoft.core.exception.SystemRuntimeException;
import com.riversoft.util.jackson.JsonMapper;
import com.taobao.api.TaobaoResponse;
import com.taobao.api.request.AlibabaAliqinFcSmsNumSendRequest;
import com.taobao.api.response.AlibabaAliqinFcSmsNumSendResponse;

import java.util.Map;

/**
 * 阿里大鱼短信发送
 *
 * @borball on 3/7/2016.
 */
public class SmsClient {

    /**
     * 短信签名
     */
    private String signName;

    private AliClient aliClient;

    public void setSignName(String signName) {
        this.signName = signName;
    }

    public void setAliClient(AliClient aliClient) {
        this.aliClient = aliClient;
    }

    public static SmsClient getInstance(){
        return (SmsClient)BeanFactory.getInstance().getBean("smsClient");
    }

    /**
     * 发送短信
     * @param templateId 模板ID
     * @param mobile 手机号码
     * @param params 模板参数
     */
    public void send(String templateId, String mobile, Map<String, String> params) {
        AlibabaAliqinFcSmsNumSendRequest request = new AlibabaAliqinFcSmsNumSendRequest();
        request.setSmsTemplateCode(templateId);
        request.setRecNum(mobile);
        request.setSmsFreeSignName(signName);
        request.setSmsParamString(JsonMapper.defaultMapper().toJson(params));
        request.setSmsType("normal");
        TaobaoResponse response = aliClient.execute(request);
        if(response instanceof AlibabaAliqinFcSmsNumSendResponse) {
            AlibabaAliqinFcSmsNumSendResponse smsNumSendResponse = (AlibabaAliqinFcSmsNumSendResponse)response;
            if(!smsNumSendResponse.getResult().getSuccess()) {
                throw new SystemRuntimeException(ExceptionType.TAOBAO, smsNumSendResponse.getResult().getErrCode() + ":" + smsNumSendResponse.getResult().getMsg());
            }
        }  else {
            throw new SystemRuntimeException(ExceptionType.TAOBAO);
        }
    }
}
