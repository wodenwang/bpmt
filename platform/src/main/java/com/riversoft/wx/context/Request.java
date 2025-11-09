package com.riversoft.wx.context;

import com.riversoft.weixin.common.message.XmlMessageHeader;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by exizhai on 12/13/2015.
 */
public class Request {

    private XmlMessageHeader source;

    /**
     * 是否菜单回调
     */
    private boolean menu;
    private String eventKey;

    /**
     * 是否对话框回调
     */
    private boolean message;

    /**
     * 各种事件回调
     */
    private boolean subscribe;
    private boolean unSubscribe;
    //用户位置信息上报事件
    private boolean locationEvent;

    /**
     * 各种数据
     */
    private String text;
    private Image image;
    //位置主动共享或者发送位置信息
    private Location location;
    private Voice voice;
    private Video video;
    private Video shortVideo;

    /**
     * 额外辅助信息
     */
    private Object extra;

    private Map<String, Object> attrs = new HashMap<>();

    public boolean isMenu() {
        return menu;
    }

    public void setMenu(boolean menu) {
        this.menu = menu;
    }

    public String getEventKey() {
        return eventKey;
    }

    public void setEventKey(String eventKey) {
        this.eventKey = eventKey;
    }

    public boolean isMessage() {
        return message;
    }

    public void setMessage(boolean message) {
        this.message = message;
    }

    public boolean isSubscribe() {
        return subscribe;
    }

    public void setSubscribe(boolean subscribe) {
        this.subscribe = subscribe;
    }

    public boolean isUnSubscribe() {
        return unSubscribe;
    }

    public void setUnSubscribe(boolean unSubscribe) {
        this.unSubscribe = unSubscribe;
    }

    public boolean isLocationEvent() {
        return locationEvent;
    }

    public void setLocationEvent(boolean locationEvent) {
        this.locationEvent = locationEvent;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Image getImage() {
        return image;
    }

    public void setImage(Image image) {
        this.image = image;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public Voice getVoice() {
        return voice;
    }

    public void setVoice(Voice voice) {
        this.voice = voice;
    }

    public Video getVideo() {
        return video;
    }

    public void setVideo(Video video) {
        this.video = video;
    }

    public Video getShortVideo() {
        return shortVideo;
    }

    public void setShortVideo(Video shortVideo) {
        this.shortVideo = shortVideo;
    }

    public Object getExtra() {
        return extra;
    }

    public void setExtra(Object extra) {
        this.extra = extra;
    }

    public Map<String, Object> getAttrs() {
        return attrs;
    }

    public void setAttrs(Map<String, Object> attrs) {
        this.attrs = attrs;
    }

    public XmlMessageHeader getSource() {
        return source;
    }

    public void setSource(XmlMessageHeader source) {
        this.source = source;
    }
}
