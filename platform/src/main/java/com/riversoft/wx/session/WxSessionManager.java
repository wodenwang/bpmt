package com.riversoft.wx.session;

/**
 * Created by exizhai on 10/3/2015.
 */
public interface WxSessionManager {

    public WxSession get(String sessionId);

    public WxSession newSession(String sessionId);

}
