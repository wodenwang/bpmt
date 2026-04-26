/*
 * $HeadURL: $
 * $Id: $
 * Copyright (c) 2013 by Riversoft System, all rights reserved.
 */
package com.riversoft.util;

/**
 * TODO:考虑搞个界面来设置这些参数
 * 
 * @author Borball
 * 
 */
public final class NetworkSetting {

    private boolean useProxy;
    private String proxyServer;
    private int proxyPort;
    private String proxyType;

    private String proxyUsername;
    private String proxyPassword;

    /**
     * @return the useProxy
     */
    public boolean isUseProxy() {
        return useProxy;
    }

    /**
     * @param useProxy the useProxy to set
     */
    public void setUseProxy(boolean useProxy) {
        this.useProxy = useProxy;
    }

    /**
     * @return the proxyServer
     */
    public String getProxyServer() {
        return proxyServer;
    }

    /**
     * @param proxyServer the proxyServer to set
     */
    public void setProxyServer(String proxyServer) {
        this.proxyServer = proxyServer;
    }

    /**
     * @return the proxyPort
     */
    public int getProxyPort() {
        return proxyPort;
    }

    /**
     * @param proxyPort the proxyPort to set
     */
    public void setProxyPort(int proxyPort) {
        this.proxyPort = proxyPort;
    }

    /**
     * @return the proxyType
     */
    public String getProxyType() {
        return proxyType;
    }

    /**
     * @param proxyType the proxyType to set
     */
    public void setProxyType(String proxyType) {
        this.proxyType = proxyType;
    }

    /**
     * @return the proxyUsername
     */
    public String getProxyUsername() {
        return proxyUsername;
    }

    /**
     * @param proxyUsername the proxyUsername to set
     */
    public void setProxyUsername(String proxyUsername) {
        this.proxyUsername = proxyUsername;
    }

    /**
     * @return the proxyPassword
     */
    public String getProxyPassword() {
        return proxyPassword;
    }

    /**
     * @param proxyPassword the proxyPassword to set
     */
    public void setProxyPassword(String proxyPassword) {
        this.proxyPassword = proxyPassword;
    }
}
