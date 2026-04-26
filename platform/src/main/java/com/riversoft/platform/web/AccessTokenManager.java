package com.riversoft.platform.web;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.riversoft.weixin.common.oauth2.AccessToken;

/**
 * 考虑设置大小
 * Created by exizhai on 12/18/2015.
 */
public class AccessTokenManager {

    private static Cache<String, AccessToken> tokens = CacheBuilder.newBuilder().maximumSize(10000).build();

    public AccessToken get(String sessionId) {
        return tokens.getIfPresent(sessionId);
    }

    public void set(String sessionId, AccessToken accessToken) {
        tokens.put(sessionId, accessToken);
    }

}
