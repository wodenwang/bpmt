package com.riversoft.wx.builder;

import java.util.Date;
import java.util.Map;
import java.util.Random;

import org.apache.commons.lang3.StringUtils;

import com.riversoft.util.Formatter;
import com.riversoft.weixin.pay.redpack.bean.RedPackRequest;

/**
 * Created by exizhai on 12/4/2015.
 */
public class RedPackRequestBuilder {

	public static RedPackRequest build(String mchId, Map<String, Object> map) {
		RedPackRequest redPackRequest = new RedPackRequest();

		String activity = (String) map.get("activity");
		String remark = (String) map.get("remark");
		if (StringUtils.isEmpty(remark)) {
			remark = activity;
		}
		String wishing = (String) map.get("wishing");
		if (StringUtils.isEmpty(wishing)) {
			wishing = activity;
		}
		String sender = (String) map.get("sender");

		redPackRequest.setActivityName(activity);
		redPackRequest.setRemark(remark);
		redPackRequest.setWishing(wishing);
		redPackRequest.setSendName(sender);

		redPackRequest.setAmount(((Number) map.get("amount")).intValue());

		String billNumber = null;
		if (map.containsKey("billNumber")) {
			billNumber = map.get("billNumber").toString();
		} else {
			billNumber = mchId + Formatter.formatDatetime(new Date(), "yyyyMMddHHmmssSSS") + new Random().nextInt(10);
		}
		redPackRequest.setBillNumber(billNumber);

		return redPackRequest;
	}
}
