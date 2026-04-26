package com.riversoft.wx.qy;

import com.riversoft.core.BeanFactory;
import com.riversoft.core.context.SessionContext;
import com.riversoft.core.db.ORMService;
import com.riversoft.core.exception.ExceptionType;
import com.riversoft.core.exception.SystemRuntimeException;
import com.riversoft.platform.po.UsGroup;
import com.riversoft.platform.po.UsRole;
import com.riversoft.platform.po.UsUser;
import com.riversoft.util.jackson.JsonMapper;
import com.riversoft.weixin.common.event.EventRequest;
import com.riversoft.weixin.common.event.LocationSelectEvent;
import com.riversoft.weixin.common.message.XmlMessageHeader;
import com.riversoft.weixin.qy.event.*;
import com.riversoft.weixin.qy.message.Messages;
import com.riversoft.weixin.qy.message.json.JsonMessage;
import com.riversoft.weixin.qy.request.*;
import com.riversoft.wx.context.Image;
import com.riversoft.wx.context.Location;
import com.riversoft.wx.context.Video;
import com.riversoft.wx.context.Voice;
import com.riversoft.wx.qy.command.AgentConfigCommand;
import com.riversoft.wx.qy.command.UserSubEventCommand;
import com.riversoft.wx.qy.command.QyCommand;
import com.riversoft.wx.qy.command.QyRequest;
import com.riversoft.wx.qy.command.QyResponse;
import com.riversoft.wx.qy.service.QyAppService;
import com.riversoft.wx.session.WxSession;
import com.riversoft.wx.session.WxSessionManager;
import com.riversoft.wx.util.DuplicatedMessageChecker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 企业号回调分发
 * <p/>
 * Created by exizhai on 10/3/2015.
 */
public class QyWxRequestDispatcher {

    private Logger logger = LoggerFactory.getLogger(QyWxRequestDispatcher.class);

    private WxSessionManager wxSessionManager;
    private DuplicatedMessageChecker duplicatedMessageChecker;
    private ExecutorService executorService = Executors.newFixedThreadPool(5);

    /**
     * 获取单例
     *
     * @return
     */
    public static QyWxRequestDispatcher getInstance() {
        return BeanFactory.getInstance().getSingleBean(QyWxRequestDispatcher.class);
    }

    /**
     * spring IOC需要
     *
     * @param wxSessionManager
     */
    public void setWxSessionManager(WxSessionManager wxSessionManager) {
        this.wxSessionManager = wxSessionManager;
    }

    /**
     * spring IOC需要
     *
     * @param duplicatedMessageChecker
     */
    public void setDuplicatedMessageChecker(DuplicatedMessageChecker duplicatedMessageChecker) {
        this.duplicatedMessageChecker = duplicatedMessageChecker;
    }

    /**
     * 分发
     *
     * @param agent
     * @param xmlRequest
     */
    public void dispatch(String agent, XmlMessageHeader xmlRequest) {
        if (!duplicatedMessageChecker.isDuplicated(xmlRequest.getFromUser() + xmlRequest.getCreateTime().getTime())) {
            // 设置用户会话
            String fromUser = xmlRequest.getFromUser();
            if (wxSessionManager.get(fromUser) == null) {
                initSession(fromUser);
            }
            SessionContext.init(wxSessionManager.get(fromUser).getAll());

            // 获取包装请求类
            QyRequest request = buildRequestBean(agent, xmlRequest);

            // 获取command
            QyCommand qyCommand = findCommand(request);
            if (qyCommand != null) {
                try {
                    QyResponse qyResponse = qyCommand.execute(request);
                    if (qyResponse != null && qyResponse.getMessage() != null) {
                        final JsonMessage message = qyResponse.getMessage();
                        executorService.submit(new Runnable() {
                            @Override
                            public void run() {
                                Messages.defaultMessages().send(message);

                            }
                        });
                    }
                } catch (Exception e) {
                    logger.error("exec command failed.", e);
                }
            }
        } else {
            logger.warn("重复消息:{}", JsonMapper.nonEmptyMapper().toJson(xmlRequest));
        }

    }

    /**
     * 获取处理器
     *
     * @param request
     * @return
     */
    private QyCommand findCommand(QyRequest request) {
        // 关注/取消关注
        if (request.isSubscribe() || request.isUnSubscribe()) {
            QyCommand command = new UserSubEventCommand();
            command.execute(request);
        }

        return BeanFactory.getInstance().getSingleBean(AgentConfigCommand.class);
    }

    /**
     * 包装请求
     *
     * @param agentKey
     * @param xmlRequest
     * @return
     */
    private QyRequest buildRequestBean(String agentKey, XmlMessageHeader xmlRequest) {
        QyRequest request = new QyRequest();
        request.setAgentId(QyAppService.getInstance().getAgentSetting(agentKey).getAgentId());
        request.setAgentKey(agentKey);
        request.setUid(xmlRequest.getFromUser());

        if (xmlRequest instanceof EventRequest) {
            setupEventRequest((EventRequest) xmlRequest, request);
        } else {
            setupMessageRequest(xmlRequest, request);
        }

        return request;
    }

    /**
     * 对话框事件
     *
     * @param xmlRequest
     * @param request
     */
    private void setupMessageRequest(XmlMessageHeader xmlRequest, QyRequest request) {
        request.setMessage(true);
        switch (xmlRequest.getMsgType()) {
            case location:
                request.setLocation(toLocation((QyLocationRequest) xmlRequest));
                break;
            case text:
                request.setText(((QyTextRequest) xmlRequest).getContent());
                break;
            case image:
                request.setImage(toImage((QyImageRequest) xmlRequest));
                break;
            case voice:
                request.setVoice(toVoice((QyVoiceRequest) xmlRequest));
                break;
            case video:
                request.setVideo(toVideo((QyVideoRequest) xmlRequest));
                break;
            case shortvideo:
                request.setShortVideo(toShortVideo((QyShortVideoRequest) xmlRequest));
                break;
            default:
                break;
        }
    }

    /**
     * 应用事件+菜单事件
     *
     * @param eventRequest
     * @param request
     */
    private void setupEventRequest(EventRequest eventRequest, QyRequest request) {
        switch (eventRequest.getEventType()) {
            // 应用事件
            case enter_agent:
                request.setEnterAgent(true);
                break;
            case subscribe:
                request.setSubscribe(true);
                break;
            case unsubscribe:
                request.setUnSubscribe(true);
                break;
            case LOCATION:
                request.setLocationEvent(true);
                request.setLocation(toLocation((QyLocationReportEvent) eventRequest));
                break;

            // 菜单事件
            case scancode_push:
            case scancode_waitmsg:
                request.setMenu(true);
                request.setEventKey(((QyScanEvent) eventRequest).getEventKey());
                request.setQrCode(toQrCode((QyScanEvent) eventRequest));
                break;
            case location_select:
                request.setMenu(true);
                request.setEventKey(((QyLocationSelectEvent) eventRequest).getEventKey());
                request.setLocation(toLocation(((QyLocationSelectEvent) eventRequest).getSendLocationInfo()));
                break;
            case click:
                request.setMenu(true);
                request.setEventKey(((QyClickEvent) eventRequest).getEventKey());
                break;
            case pic_sysphoto:
            case pic_photo_or_album:
            case pic_weixin:
                request.setMenu(true);
                request.setEventKey(((QyPhotoEvent) eventRequest).getEventKey());
                request.setExtra(((QyPhotoEvent) eventRequest).getSendPicsInfo());
                break;

            // 菜单超链接
            case view:
                request.setMenu(true);
                // request.setEventKey(((ViewEvent) eventXmlRequest).getUrl());
                // 超链接暂时不需要拦截事件
                break;

            default:
                break;
        }
    }

    /**
     * 转换地理位置对象
     *
     * @param sendLocationInfo
     * @return
     */
    private Location toLocation(LocationSelectEvent.SendLocationInfo sendLocationInfo) {
        Location location = new Location();
        location.setX(sendLocationInfo.getX());
        location.setY(sendLocationInfo.getY());
        location.setScale(sendLocationInfo.getScale());
        location.setLabel(sendLocationInfo.getLabel());
        location.setPoiName(sendLocationInfo.getPoiName());
        return location;
    }

    /**
     * 转换地理位置对象
     *
     * @param locationReportEvent
     * @return
     */
    private Location toLocation(QyLocationReportEvent locationReportEvent) {
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
    private Location toLocation(QyLocationRequest locationRequest) {
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
    private Video toShortVideo(QyShortVideoRequest shortVideoRequest) {
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
    private Video toVideo(QyVideoRequest videoRequest) {
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
    private Voice toVoice(QyVoiceRequest voiceRequest) {
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
    private Image toImage(QyImageRequest imageRequest) {
        Image image = new Image();
        image.setPicUrl(imageRequest.getPicUrl());
        image.setMediaId(imageRequest.getMediaId());
        return image;
    }

    /**
     * 扫码
     *
     * @param scanEvent
     * @return
     */
    private String toQrCode(QyScanEvent scanEvent) {
        return scanEvent.getScanCodeInfo().getScanResult();
    }

    /**
     * 初始化用户会话
     *
     * @param username
     */
    private void initSession(String username) {
        // 校验用户名
        UsUser user = (UsUser) ORMService.getInstance().findByPk(UsUser.class.getName(), username);
        if (user == null) {
            throw new SystemRuntimeException(ExceptionType.BUSINESS, "找不到用户[" + username + "].");
        }

        // 是否失效.超级权限无需此校验
        if (user.getActiveFlag().intValue() != 1) {
            throw new SystemRuntimeException(ExceptionType.BUSINESS, "用户[" + username + "]账号已失效.");
        }

        // 查找当前组织与用户
        List<?> relationshipList = ORMService.getInstance().queryHQL("from UsUserGroupRole where uid = ? order by defaultFlag desc,sort asc", username);
        if (relationshipList == null || relationshipList.size() < 1) {
            throw new SystemRuntimeException(ExceptionType.BUSINESS, "无法找到用户[" + username + "]归属的组织与角色,无法登陆系统.");
        }
        Map<String, Object> relationship = (Map<String, Object>) relationshipList.get(0);
        UsGroup group = (UsGroup) ORMService.getInstance().findByPk(UsGroup.class.getName(), relationship.get("groupKey").toString());
        if (group == null) {
            throw new SystemRuntimeException(ExceptionType.BUSINESS, "用户[" + username + "]归属的组织已被注销,无法登陆系统.");
        }
        UsRole role = (UsRole) ORMService.getInstance().findByPk(UsRole.class.getName(), relationship.get("roleKey").toString());
        if (role == null) {
            throw new SystemRuntimeException(ExceptionType.BUSINESS, "用户[" + username + "]归属的角色已被注销,无法登陆系统.");
        }

        // 设置权限
        Set<String> priGroups = new HashSet<>();
        {
            // 角色固有权限组
            List<String> groupIds = ORMService.getInstance().queryHQL("select groupId from UsRolePriGroupRelate where roleKey = ?", role.getRoleKey());
            priGroups.addAll(groupIds);
        }
        {
            // 组织内角色特有权限组
            List<String> groupIds = ORMService.getInstance().queryHQL("select groupId from UsRoleGroupPriRelate where roleKey = ? and groupKey = ?", role.getRoleKey(), group.getGroupKey());
            priGroups.addAll(groupIds);
        }

        WxSession wxSession = wxSessionManager.newSession(username);
        wxSession.set("RELATION_SHIP", relationship);
        wxSession.set("USER", user);
        wxSession.set("GROUP", group);
        wxSession.set("ROLE", role);
        wxSession.set("DATE", new Date());

    }

}
