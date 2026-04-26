/*
 * $HeadURL: $
 * $Id: $
 * Copyright (c) 2013 by Riversoft System, all rights reserved.
 */
package com.riversoft.dtask.tasks;

import com.riversoft.license.api.DefaultMagicImpl;
import com.riversoft.license.api.Identifier;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.tools.ant.BuildException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Borball
 */
public class RegisterTask extends RiverSupportTask {

    private Logger logger = LoggerFactory.getLogger("RegisterTask");

    private String name;
    private String password;
    private File[] magicPlaces;

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @param password the password to set
     */
    public void setPassword(String password) {
        this.password = password;
    }

    private void initMagicPlaces() {
        magicPlaces = new File[2];

        Path antLibsPath = Paths.get(installationRoot.getAbsolutePath(), "tools", "internal", "libs");
        File antLibsFir = antLibsPath.toFile();

        magicPlaces[0] = antLibsFir;

        Path platformLibsPath = Paths.get(installationRoot.getAbsolutePath(), "platform", "WEB-INF", "lib");
        File platformLibsFir = platformLibsPath.toFile();

        magicPlaces[1] = platformLibsFir;
    }

    @Override
    protected void support() {
        initMagicPlaces();

        register();
    }

    private void register() {
        try {
            HttpResponse registerResp;

            Map<String, String> headers = new HashMap<>();
            headers.put(RiverHttpHeaders.PLATFORM, getIdentifier().getPlatform());
            headers.put(RiverHttpHeaders.VERSION, getIdentifier().getVersion());

            registerResp = httpGet(REGISTRATION_URL_PREFIX, headers);

            HttpEntity entity = registerResp.getEntity();

            int status = registerResp.getStatusLine().getStatusCode();
            if (HttpStatus.SC_OK == status) { // 激活成功
                InputStream is = entity.getContent();

                String magicFileName = getFileName(registerResp);
                if (StringUtils.isEmpty(magicFileName)) {
                    throw new IOException("用户激活失败， 获取激活文件名失败。");
                }
                File magicFile = new File(magicPlaces[0], magicFileName);

                IOUtils.copy(is, new FileOutputStream(magicFile));

                FileUtils.copyFileToDirectory(magicFile, magicPlaces[1], true);
                logger.info("账号激活成功。");
            } else if (HttpStatus.SC_UNAUTHORIZED == status) { // 401
                logger.error("账号激活失败: 账号或者密码不正确。");
            } else if (HttpStatus.SC_FORBIDDEN == status) { // 403
                logger.error("账号激活失败: 该账号已经激活过。");
            } else {
                logger.error("账号激活失败: " + registerResp.getStatusLine().getStatusCode() + ":"
                        + registerResp.getStatusLine().getReasonPhrase());
            }
        } catch (IOException e) {
            logger.error("账号激活失败，其他错误：", e);
        }

    }

    @Override
    public Identifier getIdentifier() {
        Identifier identifier = new Identifier();
        identifier.setName(name);
        identifier.setPassword(password);
        identifier.setVersion(StringUtils.isNotEmpty(getPlatformVersionFromJar()) ? getPlatformVersionFromJar() : "unknown");
        try {
            identifier.setPlatform(FileUtils.readFileToString(platformFile));
        } catch (IOException e) {
            throw new BuildException("获取当前platform信息失败,系统文件被破坏?");
        }

        return identifier;
    }

    @Override
    protected void injectMagic() {
        magic = new DefaultMagicImpl();
    }
}
