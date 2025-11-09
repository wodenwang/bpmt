package com.riversoft.wx.mp;

import com.riversoft.core.BeanFactory;
import com.riversoft.core.context.SessionContext;
import com.riversoft.core.db.DataCondition;
import com.riversoft.core.db.ORMService;
import com.riversoft.platform.db.ORMAdapterService;
import com.riversoft.platform.web.WxUserManager;
import com.riversoft.util.jackson.JsonMapper;
import com.riversoft.weixin.common.event.ClickEvent;
import com.riversoft.weixin.common.event.EventRequest;
import com.riversoft.weixin.common.event.LocationReportEvent;
import com.riversoft.weixin.common.message.XmlMessageHeader;
import com.riversoft.weixin.common.request.*;
import com.riversoft.weixin.mp.base.AppSetting;
import com.riversoft.weixin.mp.care.CareMessages;
import com.riversoft.weixin.mp.event.shop.OrderEvent;
import com.riversoft.weixin.mp.event.ticket.SceneScanEvent;
import com.riversoft.weixin.mp.event.ticket.SceneSubEvent;
import com.riversoft.weixin.mp.message.xml.Forward2CareXmlMessage;
import com.riversoft.weixin.mp.request.LinkRequest;
import com.riversoft.weixin.mp.user.Users;
import com.riversoft.weixin.mp.user.bean.User;
import com.riversoft.wx.context.*;
import com.riversoft.wx.mp.command.*;
import com.riversoft.wx.mp.command.UserSubEventCommand;
import com.riversoft.wx.mp.context.OrderPay;
import com.riversoft.wx.mp.model.MpVisitorModelKeys;
import com.riversoft.wx.mp.service.MpAppService;
import com.riversoft.wx.session.WxSession;
import com.riversoft.wx.session.WxSessionManager;
import com.riversoft.wx.util.DuplicatedMessageChecker;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 公众号回调分发
 * <p/>
 * Created by exizhai on 10/3/2015.
 */
public class MpRequestDispatcher {

	private Logger logger = LoggerFactory.getLogger(MpRequestDispatcher.class);

	private WxSessionManager wxSessionManager;

	private DuplicatedMessageChecker duplicatedMessageChecker;

	private ExecutorService executorService = Executors.newFixedThreadPool(5);

	/**
	 * spring IOC需要
	 *
	 * @param duplicatedMessageChecker
	 */
	public void setDuplicatedMessageChecker(DuplicatedMessageChecker duplicatedMessageChecker) {
		this.duplicatedMessageChecker = duplicatedMessageChecker;
	}

	public void setWxSessionManager(WxSessionManager wxSessionManager) {
		this.wxSessionManager = wxSessionManager;
	}

	/**
	 * 获取单例
	 *
	 * @return
	 */
	public static MpRequestDispatcher getInstance() {
		return BeanFactory.getInstance().getSingleBean(MpRequestDispatcher.class);
	}

	/**
	 * 分发
	 *
	 * @param appSetting
	 * @param xmlRequest
	 */
	public XmlMessageHeader dispatch(final AppSetting appSetting, XmlMessageHeader xmlRequest) {
		if (!duplicatedMessageChecker.isDuplicated(appSetting.getAppId() + xmlRequest.getFromUser() + xmlRequest.getCreateTime().getTime())) {
			final String openId = xmlRequest.getFromUser();
			if (wxSessionManager.get(openId) == null) {
				initSession(appSetting, openId);
			}
			SessionContext.init(wxSessionManager.get(openId).getAll());

			// 获取包装请求类
			MpRequest request = buildRequestBean(appSetting.getAppId(), xmlRequest);

			// ====== 执行系统级command
			// 关注/取消关注
			if (request.isSubscribe() || request.isUnSubscribe()) {
				MpCommand command = new UserSubEventCommand();
				command.execute(request);
			}

			// 模板消息的发送结果通知
			if (request.isTemplateMsgCompleted()) {
				MpCommand command = new TemplateMsgEventCommand();
				command.execute(request);
			}

			// 获取通用mp command
			MpCommand mpCommand = BeanFactory.getInstance().getSingleBean(MpConfigCommand.class);
			if (mpCommand != null) {
				try {
					final MpResponse response = mpCommand.execute(request);
					if (response != null) {
						if (isKf(response.getType())) { // 转交给客服 [kf:true] 或者
														// [kf: 'woden']
							logger.debug("需要转交到客服系统.");
							Forward2CareXmlMessage kfMessage = new Forward2CareXmlMessage();
							kfMessage.setFromUser(xmlRequest.getToUser());
							kfMessage.setToUser(xmlRequest.getFromUser());
							kfMessage.setCreateTime(xmlRequest.getCreateTime());
							if (!"true".equalsIgnoreCase(response.getKf())) {
								kfMessage.setAccount(response.getKf());// 指定客服
							}
							return kfMessage;
						} else {
							// 下发消息
							sendMessage(appSetting, openId, response);
						}
					}
				} catch (Exception e) {
					logger.error("执行mp command失败", e);
				}
			} else {
				logger.debug("没有配置mp command");
			}
		} else {
			logger.warn("重复消息:{}", JsonMapper.nonEmptyMapper().toJson(xmlRequest));
		}
		return null;
	}

	private boolean isKf(String type) {
		return "kf".equalsIgnoreCase(type);
	}

	private MpRequest buildRequestBean(String appId, XmlMessageHeader xmlRequest) {
		MpRequest request = new MpRequest();
		request.setAppId(appId);
		request.setMpKey(MpAppService.getInstance().getAppSettingByAppID(appId).getMpKey());
		request.setOpenId(xmlRequest.getFromUser());

		if (xmlRequest instanceof EventRequest) {
			EventRequest eventRequest = (EventRequest) xmlRequest;
			logger.debug("请求来自[{}]， 事件[{}]", appId, eventRequest.getEventType().name());
			setupEventRequest(eventRequest, request);
		} else {
			logger.debug("请求来自[{}]， 普通消息[{}]", appId, xmlRequest.getMsgType().name());
			setupMessageRequest(xmlRequest, request);
		}

		return request;
	}

	private void setupMessageRequest(XmlMessageHeader xmlRequest, MpRequest request) {
		request.setMessage(true);
		switch (xmlRequest.getMsgType()) {
		case location:
			request.setLocation(toLocation((LocationRequest) xmlRequest));
			break;
		case text:
			request.setText(((TextRequest) xmlRequest).getContent());
			break;
		case image:
			request.setImage(toImage((ImageRequest) xmlRequest));
			break;
		case voice:
			request.setVoice(toVoice((VoiceRequest) xmlRequest));
			break;
		case video:
			request.setVideo(toVideo((VideoRequest) xmlRequest));
			break;
		case shortvideo:
			request.setShortVideo(toShortVideo((ShortVideoRequest) xmlRequest));
			break;
		case link:
			request.setLink(toLink((LinkRequest) xmlRequest));
		default:
			break;
		}
	}

	private void setupEventRequest(EventRequest eventRequest, MpRequest request) {
		switch (eventRequest.getEventType()) {
		// 关注或者首次扫带场景值的二维码
		case subscribe:
			request.setSubscribe(true);
			SceneSubEvent sceneSubEvent = (SceneSubEvent) eventRequest;
			if (StringUtils.isNotEmpty(sceneSubEvent.getTicket())) {
				request.setScene(toScene(sceneSubEvent));
			}
			break;
		// 取消关注
		case unsubscribe:
			request.setUnSubscribe(true);
			break;
		// 再次扫待场景值的二维码
		case SCAN:
			request.setSceneScan(true);
			request.setEventKey(((SceneScanEvent) eventRequest).getEventKey());
			request.setScene(toScene((SceneScanEvent) eventRequest));
			break;
		// 上报位置信息
		case LOCATION:
			request.setLocationEvent(true);
			request.setLocation(toLocation((LocationReportEvent) eventRequest));
			break;
		// 菜单超链接
		case CLICK:
			request.setMenu(true);
			request.setEventKey(((ClickEvent) eventRequest).getEventKey());
			break;
		// 菜单超链接
		case VIEW:
			request.setMenu(true);
			// request.setEventKey(((ViewEvent) eventXmlRequest).getUrl());
			// 超链接暂时不需要拦截事件
			break;
		case TEMPLATESENDJOBFINISH:
			request.setTemplateMsgCompleted(true);
			break;
		case ORDER:
			OrderEvent orderEvent = (OrderEvent) eventRequest;
			OrderPay orderPay = new OrderPay();
			orderPay.setOrderId(orderEvent.getOrderId());
			orderPay.setFromUser(orderEvent.getFromUser());
			orderPay.setCreateTime(orderEvent.getCreateTime());
			orderPay.setOrderStatus(orderEvent.getOrderStatus());
			orderPay.setProductId(orderEvent.getProductId());
			orderPay.setSkuInfo(orderEvent.getSkuInfo());
			orderPay.setToUser(orderEvent.getToUser());
			request.setOrderPay(orderPay);
			break;
		default:
			break;
		}
	}

	/**
	 * 转换场景值
	 *
	 * @param eventRequest
	 * @return
	 */
	private Scene toScene(SceneScanEvent eventRequest) {
		Scene scene = new Scene();
		scene.setSceneId(eventRequest.getEventKey());
		scene.setTicket(eventRequest.getTicket());
		return scene;
	}

	/**
	 * 转换场景值
	 *
	 * @param sceneSubEvent
	 * @return
	 */
	private Scene toScene(SceneSubEvent sceneSubEvent) {
		Scene scene = new Scene();
		String sceneId = sceneSubEvent.getEventKey().substring(8);
		scene.setSceneId(sceneId);
		scene.setTicket(sceneSubEvent.getTicket());
		return scene;
	}

	/**
	 * 转换地理位置对象
	 *
	 * @param locationReportEvent
	 * @return
	 */
	private Location toLocation(LocationReportEvent locationReportEvent) {
		Location location = new Location();
		location.setX(locationReportEvent.getLatitude());
		location.setY(locationReportEvent.getLongitude());
		location.setScale(locationReportEvent.getPrecision());
		location.setLabel("");
		return location;
	}

	/**
	 * 转换地理位置对象
	 *
	 * @param locationRequest
	 * @return
	 */
	private Location toLocation(LocationRequest locationRequest) {
		Location location = new Location();
		location.setX(locationRequest.getX());
		location.setY(locationRequest.getY());
		location.setScale(locationRequest.getScale());
		location.setLabel(locationRequest.getLabel());
		return location;
	}

	/**
	 * 小视频
	 *
	 * @param shortVideoRequest
	 * @return
	 */
	private Video toShortVideo(ShortVideoRequest shortVideoRequest) {
		Video video = new Video();
		video.setMediaId(shortVideoRequest.getMediaId());
		video.setThumbMediaId(shortVideoRequest.getThumbMediaId());
		return video;
	}

	/**
	 * 视频文件
	 *
	 * @param videoRequest
	 * @return
	 */
	private Video toVideo(VideoRequest videoRequest) {
		Video video = new Video();
		video.setMediaId(videoRequest.getMediaId());
		video.setThumbMediaId(videoRequest.getThumbMediaId());
		return video;
	}

	/**
	 * 录音
	 *
	 * @param voiceRequest
	 * @return
	 */
	private Voice toVoice(VoiceRequest voiceRequest) {
		Voice voice = new Voice();
		voice.setFormat(voiceRequest.getFormat());
		voice.setMediaId(voiceRequest.getMediaId());
		return voice;
	}

	/**
	 * 图片
	 *
	 * @param imageRequest
	 * @return
	 */
	private Image toImage(ImageRequest imageRequest) {
		Image image = new Image();
		image.setPicUrl(imageRequest.getPicUrl());
		image.setMediaId(imageRequest.getMediaId());
		return image;
	}

	/**
	 * 链接
	 *
	 * @param xmlRequest
	 * @return
	 */
	private Link toLink(LinkRequest xmlRequest) {
		Link link = new Link();
		link.setUrl(xmlRequest.getUrl());
		link.setTitle(xmlRequest.getTitle());
		link.setDesc(xmlRequest.getDesc());
		return link;
	}

	private void sendMessage(final AppSetting appSetting, final String openId, final MpResponse response) {
		String type = response.getType();
		if (StringUtils.isNotEmpty(type)) {
			switch (type) {
			case "text":
				executorService.submit(new Runnable() {
					@Override
					public void run() {
						CareMessages.with(appSetting).text(openId, response.getText());
					}
				});
				break;
			case "image":
				executorService.submit(new Runnable() {
					@Override
					public void run() {
						CareMessages.with(appSetting).image(openId, response.getImage());
					}
				});
				break;
			case "voice":
				executorService.submit(new Runnable() {
					@Override
					public void run() {
						CareMessages.with(appSetting).voice(openId, response.getVoice());
					}
				});
				break;
			case "video":
				executorService.submit(new Runnable() {
					@Override
					public void run() {
						CareMessages.with(appSetting).video(openId, response.getVideo());
					}
				});
				break;
			case "music":
				executorService.submit(new Runnable() {
					@Override
					public void run() {
						CareMessages.with(appSetting).music(openId, response.getMusic());
					}
				});
				break;
			case "news":
				executorService.submit(new Runnable() {
					@Override
					public void run() {
						CareMessages.with(appSetting).news(openId, response.getNews());
					}
				});
				break;
			case "mpnews":
				executorService.submit(new Runnable() {
					@Override
					public void run() {
						CareMessages.with(appSetting).mpNews(openId, response.getMpnews());
					}
				});
				break;
			case "card":
				executorService.submit(new Runnable() {
					@Override
					public void run() {
						CareMessages.with(appSetting).card(openId, response.getCard());
					}
				});
				break;
			default:
				logger.warn("消息类型:{}不在支持的范围内，请检查。", type);
				break;
			}
		} else {
			logger.error("response需要遵循一定规范返回消息的类型，请检查微信相关的配置。");
		}
	}

	private void initSession(AppSetting appSetting, String openId) {
		WxSession wxSession = wxSessionManager.newSession(openId);

		Map<String, Object> mpConfig = (Map<String, Object>) ORMService.getInstance().findByKey("WxMp", "appId", appSetting.getAppId());
		if (mpConfig != null) {
			String visitorTable = (String) mpConfig.get("visitorTable");
			String visitorTagTable = (String) mpConfig.get("visitorTagTable");
			DataCondition condition = new DataCondition().setStringEqual(MpVisitorModelKeys.OPEN_ID.getColumn().getName(), openId);
			Map<String, Object> visitor = (Map<String, Object>) ORMAdapterService.getInstance().find(visitorTable, condition.toEntity());
			if (visitor != null) {
				wxSession.set("USER", WxUserManager.buildMpUser(visitor, (String) mpConfig.get("groupKey"), (String) mpConfig.get("roleKey")));
			}
		}
		wxSession.set("DATE", new Date());
	}

}
