package com.riversoft.wx.session;

import java.util.Map;

/**
 * Created by exizhai on 10/3/2015.
 */
public interface WxSession {

    public Object get(String name);

    public void set(String name, Object value);

    public void remove(String name);

    public Map<String, Object> getAll();

    public void destroy();

    public void touch();

    public boolean expired();

}
