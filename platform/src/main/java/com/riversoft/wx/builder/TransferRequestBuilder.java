package com.riversoft.wx.builder;

import com.riversoft.util.Formatter;
import com.riversoft.weixin.pay.transfer.bean.TransferRequest;

import java.net.Inet4Address;
import java.net.UnknownHostException;
import java.util.Date;
import java.util.Map;
import java.util.Random;

/**
 * @borball on 7/14/2016.
 */
public class TransferRequestBuilder {

    public static TransferRequest build(String mchId, Map<String, Object> map) {
        TransferRequest transferRequest = new TransferRequest();

        /*
          * 是否校验用户名
          * NO_CHECK：不校验真实姓名
          * FORCE_CHECK：强校验真实姓名（未实名认证的用户会校验失败，无法转账）
          * OPTION_CHECK：针对已实名认证的用户才校验真实姓名（未实名认证用户不校验，可以转账成功）
         */
        String checkName = (String) map.get("checkName");

        String desc = (String) map.get("desc");

        transferRequest.setCheckName(checkName);
        transferRequest.setDesc(desc);

        if(map.containsKey("userName")) {
            transferRequest.setUserName((String) map.get("userName"));
        }

        transferRequest.setOpenId(map.get("user").toString());

        if(map.containsKey("device")) {
            transferRequest.setDeviceInfo((String) map.get("device"));
        }

        try {
            transferRequest.setClientIp(Inet4Address.getLocalHost().getHostAddress());
        } catch (UnknownHostException e) {
            transferRequest.setClientIp("127.0.0.1");
        }

        transferRequest.setAmount(((Number) map.get("amount")).intValue());

        String tradeNumber = null;
        if (map.containsKey("billNumber")) {
            tradeNumber = map.get("billNumber").toString();
        } else {
            tradeNumber = mchId + Formatter.formatDatetime(new Date(), "yyyyMMddHHmmssSSS") + new Random().nextInt(6);
        }
        transferRequest.setPartnerTradeNo(tradeNumber);

        return transferRequest;
    }
}
