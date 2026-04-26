package com.riversoft.wx.mp;

import java.net.Inet4Address;
import java.net.UnknownHostException;
import java.util.Map;

import com.riversoft.weixin.pay.base.BaseResponse;
import com.riversoft.weixin.pay.base.PaySetting;
import com.riversoft.weixin.pay.payment.Payments;
import com.riversoft.weixin.pay.payment.Signatures;
import com.riversoft.weixin.pay.payment.bean.OrderQueryRequest;
import com.riversoft.weixin.pay.payment.bean.OrderQueryResponse;
import com.riversoft.weixin.pay.payment.bean.Signature;
import com.riversoft.weixin.pay.payment.bean.UnifiedOrderRequest;
import com.riversoft.weixin.pay.payment.bean.UnifiedOrderResponse;
import com.riversoft.weixin.pay.redpack.RedPacks;
import com.riversoft.weixin.pay.redpack.bean.RedPackRequest;
import com.riversoft.weixin.pay.redpack.bean.RedPackResponse;
import com.riversoft.weixin.pay.redpack.bean.RedPackResult;
import com.riversoft.weixin.pay.transfer.Transfers;
import com.riversoft.weixin.pay.transfer.bean.TransferResponse;
import com.riversoft.weixin.pay.transfer.bean.TransferResult;
import com.riversoft.wx.builder.RedPackRequestBuilder;
import com.riversoft.wx.builder.TransferRequestBuilder;
import com.riversoft.wx.mp.builder.PayRequestBuilder;
import com.riversoft.wx.mp.service.MpAppService;

/**
 * Created by exizhai on 12/28/2015.
 */
public class MpPayHelper {

	private PaySetting paySetting;

	public MpPayHelper() {
	}

	public MpPayHelper(PaySetting paySetting) {
		this.paySetting = paySetting;
	}

	public MpPayHelper(String appKey) {
		this.paySetting = MpAppService.getInstance().getPaySettingByPK(appKey);
	}

	/**
	 * 发红包
	 * 
	 * @param params
	 * @return
	 */
	public RedPackResponse red(Map<String, Object> params) {
		if (!params.containsKey("number")) {
			params.put("number", 1);
		}

		int number = (Integer) params.get("number");
		if (number == 1) {
			return single(params);
		} else {
			return group(params);
		}
	}

	/**
	 * 红包查询
	 * 
	 * @param billNumber
	 * @return
	 */
	public RedPackResult query(String billNumber) {
		return RedPacks.with(this.paySetting).query(billNumber);
	}

	/**
	 * 企业转账
	 * 
	 * @param params
	 * @return
	 */
	public TransferResponse transfer(Map<String, Object> params) {
		return Transfers.with(paySetting).transfer(TransferRequestBuilder.build(paySetting.getMchId(), params));
	}

	/**
	 * 查询转账结果
	 * 
	 * @param tradeNumber
	 * @return
	 */
	public TransferResult transferResult(String tradeNumber) {
		return Transfers.with(paySetting).query(tradeNumber);
	}

	/**
	 * 发单个红包
	 *
	 * @param params
	 *            单个红包所需参数
	 * @return
	 */
	private RedPackResponse single(Map<String, Object> params) {
		RedPackRequest redPackRequest = RedPackRequestBuilder.build(paySetting.getMchId(), params);
		redPackRequest.setNumber(1);
		try {
			redPackRequest.setClientIp(Inet4Address.getLocalHost().getHostAddress());
		} catch (UnknownHostException e) {
			redPackRequest.setClientIp("127.0.0.1");
		}

		redPackRequest.setAppId(paySetting.getAppId());
		redPackRequest.setOpenId(params.get("user").toString());

		return RedPacks.with(paySetting).sendSingle(redPackRequest);
	}

	/**
	 * 发裂变红包
	 *
	 * @param params
	 *            裂变红包所需参数
	 * @return
	 */
	private RedPackResponse group(Map<String, Object> params) {
		RedPackRequest redPackRequest = RedPackRequestBuilder.build(paySetting.getMchId(), params);
		redPackRequest.setNumber((Integer) params.get("number"));

		redPackRequest.setAppId(paySetting.getAppId());
		redPackRequest.setOpenId(params.get("user").toString());

		return RedPacks.with(paySetting).sendGroup(redPackRequest);
	}

	/**
	 * 统一下单
	 * 
	 * @param params
	 * @return
	 */
	public UnifiedOrderResponse order(Map<String, Object> params) {
		UnifiedOrderRequest orderRequest = PayRequestBuilder.buildUnifiedOrderRequest(paySetting.getMchId(), params);
		return Payments.with(paySetting).unifiedOrder(orderRequest);
	}

	/**
	 * 根据微信transaction id查询订单
	 * 
	 * @param transactionId
	 * @return
	 */
	public OrderQueryResponse getOrderByTransactionId(String transactionId) {
		OrderQueryRequest orderQueryRequest = new OrderQueryRequest();
		orderQueryRequest.setTransactionId(transactionId);
		return Payments.with(paySetting).query(orderQueryRequest);
	}

	/**
	 * 根据tradeNumber查询订单
	 * 
	 * @param tradeNumber
	 * @return
	 */
	public OrderQueryResponse getOrderByTradeNumber(String tradeNumber) {
		OrderQueryRequest orderQueryRequest = new OrderQueryRequest();
		orderQueryRequest.setTradeNumber(tradeNumber);
		return Payments.with(paySetting).query(orderQueryRequest);
	}

	/**
	 * 关闭订单
	 * 
	 * @param tradeNumber
	 * @return
	 */
	public BaseResponse closeOrder(String tradeNumber) {
		return Payments.with(paySetting).close(tradeNumber);
	}

	/**
	 * 获取jssdk验证串
	 *
	 * @param prepayId
	 *            预付订单ID
	 * @return
	 */
	public Signature signature(String prepayId) {
		return Signatures.with(paySetting).createJsSignature(prepayId);
	}
}
