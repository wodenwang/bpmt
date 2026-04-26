package com.riversoft.wx.session;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by exizhai on 10/3/2015.
 */
public class DefaultWxSessionManager implements WxSessionManager {

    private ConcurrentHashMap<String, WxSession> sessions = null;

    public DefaultWxSessionManager() {
        this.sessions = new ConcurrentHashMap();
    }

    @Override
    public WxSession get(String sessionId) {
        WxSession wxSession = sessions.get(sessionId);
        if (wxSession != null) {
            wxSession.touch();
        }
        return wxSession;
    }

    @Override
    public synchronized WxSession newSession(String sessionId) {
        sessions.putIfAbsent(sessionId, new DefaultWxSession());
        return sessions.get(sessionId);
    }

}
