package com.riversoft.ali;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.riversoft.core.Config;
import com.riversoft.core.script.annotation.ScriptSupport;
import com.riversoft.util.RandomUtils;
import com.riversoft.util.jackson.JsonMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @borball on 2/23/2016.
 */
@ScriptSupport("sms")
public class SMSHelper {

    private static Logger logger = LoggerFactory.getLogger(SMSHelper.class);

    //暂时基于内存，可靠性考虑可以移到数据库
    private static Cache<String, String> cache = CacheBuilder.newBuilder().expireAfterWrite(120, TimeUnit.SECONDS).maximumSize(10000).build();

    /**
     * 发送模板短信给指定手机号
     *
     * @param templateId 模板ID
     * @param mobile 手机号
     * @param params 模板参数
     */
    public static void send(String templateId, String mobile, Map<String, String> params) {
        if(Boolean.valueOf(Config.get("sms.ali.enable", "false"))) {
            logger.info("sms send({}, {}, {}) with ali dayu implementation.", templateId, mobile, JsonMapper.defaultMapper().toJson(params));
            SmsClient.getInstance().send(templateId, mobile, params);
        } else {
            logger.debug("sms send({}, {}, {}) without being implemented.", templateId, mobile, JsonMapper.defaultMapper().toJson(params));
        }
    }

    /**
     * 发送验证码
     * @param mobile 手机号
     */
    public static void code(String mobile) {
        String code = RandomUtils.createRandomCode(Integer.valueOf(Config.get("sms.verified.length", "6")));
        String product = Config.get("sms.verified.system", "");
        cache.put(mobile, code);

        Map<String, String> params = new HashMap<>();
        params.put("code", code);
        params.put("product", product);
        send(Config.get("sms.verified.template.default"), mobile, params);
    }

    /**
     * 校验验证码
     * @param mobile 手机号
     * @param code 用户输入的验证码
     * @return
     */
    public static boolean verify(String mobile, String code) {
        return code.equals(cache.getIfPresent(mobile));
    }
}
