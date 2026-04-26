package com.riversoft.ali;

import com.riversoft.core.exception.ExceptionType;
import com.riversoft.core.exception.SystemRuntimeException;
import com.taobao.api.*;

/**
 * 阿里的SDK调用客户端
 *
 * @borball on 3/6/2016.
 */
public class AliClient {

    private String endpoint;
    private String appKey;
    private String appSecret;

    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }

    public void setAppKey(String appKey) {
        this.appKey = appKey;
    }

    public void setAppSecret(String appSecret) {
        this.appSecret = appSecret;
    }

    public TaobaoResponse execute(TaobaoRequest taobaoRequest) {
        TaobaoClient client = new DefaultTaobaoClient(endpoint, appKey, appSecret);
        try {
            return client.execute(taobaoRequest);
        } catch (ApiException e) {
            throw new SystemRuntimeException(ExceptionType.TAOBAO, e);
        }
    }

}
