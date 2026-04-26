/*
 * $HeadURL: $
 * $Id: $
 * Copyright (c) 2013 by Riversoft System, all rights reserved.
 */
package com.riversoft.dtask.tasks;

import org.apache.commons.io.FileUtils;
import org.apache.tools.ant.BuildException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;

/**
 * @author Borball
 */
public class NetworkSettingTask extends BaseRiverTask {

    private Logger logger = LoggerFactory.getLogger(NetworkSettingTask.class);

    private String proxyServer;
    private int proxyPort;
    private String proxyType;

    private String proxyUsername;
    private String proxyPassword;

    private File networkSettingsFile;

    /**
     * @param proxyServer the proxyServer to set
     */
    public void setProxyServer(String proxyServer) {
        try {
            InetAddress inetAddress = InetAddress.getByName(proxyServer);
            if (inetAddress.isReachable(5000)) {
                this.proxyServer = proxyServer;
            }
        } catch (IOException e) {
            throw new BuildException("代理服务器" + proxyServer + "连接失败:" + e.getLocalizedMessage());
        }
    }

    /**
     * @param proxyPort the proxyPort to set
     */
    public void setProxyPort(int proxyPort) {
        if (proxyPort >= 80) {
            this.proxyPort = proxyPort;
        } else {
            throw new BuildException("proxy port 不能小于80");
        }
    }

    /**
     * @param proxyType the proxyType to set
     */
    public void setProxyType(String proxyType) {
        this.proxyType = proxyType;
    }

    /**
     * @param proxyUsername the proxyUsername to set
     */
    public void setProxyUsername(String proxyUsername) {
        this.proxyUsername = proxyUsername;
    }

    /**
     * @param proxyPassword the proxyPassword to set
     */
    public void setProxyPassword(String proxyPassword) {
        this.proxyPassword = proxyPassword;
    }

    /**
     * @param networkSettingsFilePath the networkSettingsFile to set
     */
    public void setNetworkSettingsFile(String networkSettingsFilePath) {
        this.networkSettingsFile = new File(networkSettingsFilePath);
        if (networkSettingsFile.exists() && networkSettingsFile.isFile()) {

        } else {
            try {
                networkSettingsFile.createNewFile();
            } catch (IOException e) {
                throw new BuildException("创建文件" + networkSettingsFilePath + "失败。");
            }
        }
    }

    @Override
    protected void doExecute() throws BuildException {
        try {
            String content = prepareProxyFileContent();

            FileUtils.writeStringToFile(networkSettingsFile, content, false);

            logger.info("代理设置成功:\n*******************************\n" + content + "\n*******************************");

        } catch (IOException e) {
            throw new BuildException("网络代理设置失败:" + e.getLocalizedMessage());
        }
    }

    private String prepareProxyFileContent() {
        StringBuffer sb = new StringBuffer();
        sb.append("#proxy server address\n");
        sb.append("proxy.server=").append(proxyServer).append("\n\n");
        sb.append("#proxy port\n");
        sb.append("proxy.port=").append(proxyPort).append("\n\n");
        sb.append("#proxy type\n");
        sb.append("proxy.type=").append(proxyType).append("\n\n");
        sb.append("#proxy user name\n");
        sb.append("proxy.username=").append(proxyUsername).append("\n\n");
        sb.append("#proxy password\n");
        sb.append("proxy.password=").append(proxyPassword).append("\n\n");
        return sb.toString();
    }
}
