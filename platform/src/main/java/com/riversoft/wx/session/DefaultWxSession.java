package com.riversoft.wx.session;

import com.riversoft.core.Config;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by exizhai on 10/3/2015.
 */
public class DefaultWxSession implements WxSession {

    private ConcurrentHashMap<String, Object> session = null;
    private long validTo;

    public DefaultWxSession() {
        this.session = new ConcurrentHashMap<>();
        this.validTo = System.currentTimeMillis() + timeout();
    }

    @Override
    public Object get(String name) {
        return session.get(name);
    }

    @Override
    public void set(String name, Object value) {
        session.putIfAbsent(name, value);
    }

    @Override
    public void remove(String name) {
        session.remove(name);
    }

    @Override
    public Map<String, Object> getAll() {
        return session;
    }

    @Override
    public void destroy() {
        session.clear();
    }

    @Override
    public void touch() {
        extend();
    }

    @Override
    public boolean expired() {
        return System.currentTimeMillis() >= validTo;
    }

    public void extend() {
        if (expired()) {
            validTo = System.currentTimeMillis() + timeout();
        } else {
            validTo += timeout();
        }
    }

    private long timeout() {
        return Long.valueOf(Config.get("wx.session.timeout", "1800")) * 1000;
    }

}
