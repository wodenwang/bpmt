/*
 * $HeadURL: $
 * $Id: $
 * Copyright (c) 2013 by Riversoft System, all rights reserved.
 */
package com.riversoft.dtask.tasks;

import com.riversoft.license.api.DefaultMagicImpl;
import com.riversoft.license.api.Identifier;
import com.riversoft.license.api.Magic;
import com.riversoft.util.NetworkSetting;
import com.riversoft.util.PropertiesLoader;
import com.riversoft.util.sys.SystemInfo;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.*;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.params.ConnRoutePNames;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.tools.ant.BuildException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * A abstract class to handle Riversoft supports, such as customer registration, update checking and patch download
 *
 * @author Borball
 */
public abstract class RiverSupportTask extends BaseRiverTask {

    private Logger logger = LoggerFactory.getLogger("RiverSupportTask");

    private static final String FILE_RESOURCE_SCHEMA = "file:";

    private static final String CHECK_UPDATE_URL_KEY = "checkupdate.url";
    private static final String DOWNLOAD_URL_KEY = "download.url";
    private static final String REGISTRATION_URL_KEY = "registration.url";

    private static final String RIVER_SERVER_KEY = "river.server";
    private static final String RIVER_PORT_KEY = "river.port";
    protected static String CHECK_UPDADE_URL_PREFIX = "supports.gzriver.com/services/checkupdate";
    protected static String PATCH_DOWNLOAD_URL_PREFIX = "supports.gzriver.com/services/downlaod";
    protected static String REGISTRATION_URL_PREFIX = "supports.gzriver.com/services/register";
    protected static String RIVER_SERVER = "";
    protected static int RIVER_PORT = 80;

    private static final String PROXY_TYPE_KEY = "proxy.type";
    private static final String PROXY_SERVER_KEY = "proxy.server";
    private static final String PROXY_PORT_KEY = "proxy.port";
    private static final String PROXY_USERNAME_KEY = "proxy.username";
    private static final String PROXY_PASSWORD_KEY = "proxy.password";

    protected File installationRoot;
    protected File confDir;
    protected File downloadDir;
    protected File commonDir;
    protected File systemSettingsFile;
    protected File networkSettingsFile;
    protected File platformFile;
    protected NetworkSetting networkSetting;
    protected boolean hasNetworkSetting = false;
    protected Magic magic;

    public void setInstallationRoot(String installationRootPath) {
        this.installationRoot = new File(installationRootPath);
        if (installationRoot.exists() && installationRoot.isDirectory()) {
            // do nothing
        } else {
            throw new BuildException(installationRootPath + " 不存在或者不是一个目录，请检查!");
        }
    }

    @Override
    protected void doExecute() throws BuildException {
        loadSettings();

        support();
    }

    protected abstract void support();

    private void loadSettings() {
        loadConfDir();
        loadDownloadDir();
        loadCommonDir();

        loadSystemSettingsFile();

        loadSystemSettings();
        loadNetworkSettings();
        injectMagic();
    }

    private void loadConfDir() {
        this.confDir = new File(installationRoot, "conf");
        if (confDir.exists() && confDir.isDirectory()) {
            this.networkSettingsFile = new File(confDir, "network.properties");
            if (networkSettingsFile.exists()) {
                hasNetworkSetting = true;
            }

            Collection<File> files = FileUtils.listFiles(confDir, new String[]{"plat"}, false);
            if (files == null || files.isEmpty()) {
                throw new BuildException("获取platform信息失败，conf/*plat 文件可能不存在，请检查!");
            } else {
                platformFile = files.iterator().next();
            }

        } else {
            throw new BuildException("conf 不存在或者不是一个目录，请检查!");
        }
    }

    private void loadDownloadDir() {
        this.downloadDir = new File(installationRoot, "upgrade");
        if (downloadDir.exists() && downloadDir.isDirectory()) {
            // do nothing
        } else {
            throw new BuildException("upgrade不存在或者不是一个目录，请检查!");
        }
    }

    private void loadCommonDir() {
        this.commonDir = new File(installationRoot, "common");
        if (commonDir.exists() && commonDir.isDirectory()) {
            // do nothing
        } else {
            throw new BuildException("common不存在或者不是一个目录，请检查!");
        }
    }

    private void loadSystemSettingsFile() {
        this.systemSettingsFile = new File(commonDir, "system.properties");
        if (systemSettingsFile.exists() && systemSettingsFile.isFile()) {
            // do nothing
        } else {
            throw new BuildException("common/system.properties 不存在或者不是一个文件，请检查!");
        }
    }

    private void loadSystemSettings() {
        String systemSettingsUri = FILE_RESOURCE_SCHEMA + systemSettingsFile.getAbsolutePath();
        PropertiesLoader systemSettingsLoader = new PropertiesLoader(systemSettingsUri);

        CHECK_UPDADE_URL_PREFIX = systemSettingsLoader.getProperty(CHECK_UPDATE_URL_KEY,
                "http://supports.gzriver.com/services/checkupdate");
        PATCH_DOWNLOAD_URL_PREFIX = systemSettingsLoader.getProperty(DOWNLOAD_URL_KEY,
                "http://supports.gzriver.com/services/download");
        REGISTRATION_URL_PREFIX = systemSettingsLoader.getProperty(REGISTRATION_URL_KEY,
                "http://supports.gzriver.com/services/register");
        RIVER_SERVER = systemSettingsLoader.getProperty(RIVER_SERVER_KEY, "supports.gzriver.com");
        RIVER_PORT = systemSettingsLoader.getInteger(RIVER_PORT_KEY, 80);
    }

    protected void injectMagic() {
        ServiceLoader<Magic> sl = ServiceLoader.load(Magic.class);
        Iterator<Magic> magics = sl.iterator();

        if (magics.hasNext()) {
            Magic magic = magics.next();
            this.magic = magic;

            if (magics.hasNext()) {
                throw new BuildException("系统存在多个授权文件,系统文件被破坏?");
            }
        } else {
            magic = new DefaultMagicImpl();
        }

    }

    public Identifier getIdentifier() {
        Identifier identifier = magic.currentIdentifier();
        try {
            identifier.setPlatform(FileUtils.readFileToString(platformFile));
        } catch (IOException e) {
            throw new BuildException("获取当前platform信息失败,系统文件被破坏?");
        }
        return identifier;
    }

    private void loadNetworkSettings() {
        if (hasNetworkSetting) {
            String networkSetingsUri = FILE_RESOURCE_SCHEMA + networkSettingsFile.getAbsolutePath();
            PropertiesLoader loader = new PropertiesLoader(networkSetingsUri);
            networkSetting = new NetworkSetting();
            networkSetting.setUseProxy(true);
            networkSetting.setProxyType(loader.getProperty(PROXY_TYPE_KEY, "http"));
            networkSetting.setProxyServer(loader.getProperty(PROXY_SERVER_KEY));
            networkSetting.setProxyPort(loader.getInteger(PROXY_PORT_KEY, 80));
            networkSetting.setProxyUsername(loader.getProperty(PROXY_USERNAME_KEY, ""));
            networkSetting.setProxyPassword(loader.getProperty(PROXY_PASSWORD_KEY, ""));
        } else {
            networkSetting = new NetworkSetting();
            networkSetting.setUseProxy(false);
        }
    }

    protected HttpResponse httpGet(String url, Map<String, String> headers) throws IOException {
        DefaultHttpClient client = new DefaultHttpClient();
        HttpHost targetHost = new HttpHost(RIVER_SERVER, RIVER_PORT);

        if (networkSetting != null && networkSetting.isUseProxy()) {
            setProxy(client);
        }

        client.getCredentialsProvider().setCredentials(new AuthScope(RIVER_SERVER, RIVER_PORT),
                new UsernamePasswordCredentials(getIdentifier().getName(), getIdentifier().getPassword()));

        HttpGet httpget = new HttpGet(url);
        httpget.setHeader(RiverHttpHeaders.IDENTIFIER, SystemInfo.getIdentifier());
        if (getIdentifier().isRegister()) {
            httpget.setHeader(RiverHttpHeaders.SKEY, getIdentifier().getSkey());
        }

        for (String headerName : headers.keySet()) {
            httpget.setHeader(headerName, headers.get(headerName));
        }

        HttpResponse response = client.execute(targetHost, httpget);

        return response;
    }

    protected void setProxy(DefaultHttpClient httpclient) {
        HttpHost proxy = new HttpHost(networkSetting.getProxyServer(), networkSetting.getProxyPort(),
                networkSetting.getProxyType());
        if (!StringUtils.isEmpty(networkSetting.getProxyUsername())) {
            httpclient.getCredentialsProvider().setCredentials(
                    new AuthScope(networkSetting.getProxyServer(), networkSetting.getProxyPort()),
                    new UsernamePasswordCredentials(networkSetting.getProxyUsername(), networkSetting
                            .getProxyPassword()));
        }
        httpclient.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY, proxy);
    }

    protected String getFileName(HttpResponse response) {
        Header contentHeader = response.getFirstHeader("Content-Disposition");
        String filename = null;
        if (contentHeader != null) {
            HeaderElement[] values = contentHeader.getElements();
            if (values.length == 1) {
                NameValuePair param = values[0].getParameterByName("filename");
                if (param != null) {
                    try {
                        filename = param.getValue();
                    } catch (Exception e) {
                        logger.error("获取文件名失败:", e);
                    }
                }
            }
        }
        return filename;
    }

    protected List<Header> getRespHeadersStartsWith(HttpResponse resp, String headerPrefix) {
        List<Header> headers = new ArrayList<>();
        HeaderIterator it = resp.headerIterator();
        while (it.hasNext()) {
            Header header = it.nextHeader();

            if (header.getName().toLowerCase().startsWith(headerPrefix.toLowerCase())) {
                headers.add(header);
            }
        }

        return headers;
    }

}
