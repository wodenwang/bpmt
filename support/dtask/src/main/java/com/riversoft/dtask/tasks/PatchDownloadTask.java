/*
 * $HeadURL: $
 * $Id: $
 * Copyright (c) 2013 by Riversoft System, all rights reserved.
 */
package com.riversoft.dtask.tasks;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Borball
 */
public class PatchDownloadTask extends RiverSupportTask {

    private Logger logger = LoggerFactory.getLogger("PatchDownloadTask");

    private String currentPlatformVersion = "";
    private Map<String, String> currentCAVersions = new HashMap<>();
    private static String FIRST_SEPARATE = "&";
    private static String SECOND_SEPARATE = "|";

    @Override
    protected void support() {
        if (!getIdentifier().isRegister()) {
            logger.error("当前版本为非注册版本，不能自动升级，请联系BPMT销售支持。");
            return;
        }
        currentPlatformVersion = getPlatformVersionFromJar();

        if (StringUtils.isEmpty(currentPlatformVersion)) {
            logger.error("下载升级包失败，无法获取当前系统版本。");
            return;
        }

        currentCAVersions = getCANameAndVersionsFromJar();

        logger.info("基于以下版本信息检查更新。");
        logger.info("BPMT平台版本:" + currentPlatformVersion);
        for (String ca : currentCAVersions.keySet()) {
            logger.info("扩展包" + ca + ":" + currentCAVersions.get(ca));
        }

        try {
            chackUpdateAndDownload();
        } catch (IOException e) {
            logger.error("检查更新失败:", e);
        }
    }

    private void chackUpdateAndDownload() throws IOException {
        HttpResponse checkUpdateResp = checkUpdate();

        int status = checkUpdateResp.getStatusLine().getStatusCode();
        if (HttpStatus.SC_OK == status) {
            if (checkUpdateResp.getEntity() != null) {
                checkUpdateResp.getEntity().getContent().close();
            }
            if (checkUpdateResp.containsHeader(RiverHttpHeaders.PATCHES)) {
                downloadPlatform(checkUpdateResp.getHeaders(RiverHttpHeaders.PATCHES)[0].getValue());
            } else {
                logger.info("BPMT暂时没有可用更新.");
            }

            List<Header> caHeaders = getRespHeadersStartsWith(checkUpdateResp, RiverHttpHeaders.CA_PREFIX);
            if (caHeaders.isEmpty()) {
                logger.info("扩展包暂时没有可用更新.");
            } else {
                //有CA更新
                logger.info("扩展包有更新:");
                for (Header caHeader : caHeaders) {
                    //download ca file
                    downloadCAFile(caHeader.getName(), caHeader.getValue());
                }
            }
        } else if (HttpStatus.SC_NOT_FOUND == status) { // 没有更新
            logger.warn("软件暂时没有可用的更新。");
        } else if (HttpStatus.SC_FORBIDDEN == status) { // 没有授权
            logger.warn("检查更新失败，请检查是否有合法的授权文件。");
        } else { // 其他错误?
            logger.warn("检查更新失败，" + checkUpdateResp.getStatusLine().getReasonPhrase());
        }

    }

    private void downloadCAFile(String caHeaderName, String caFile) throws IOException {
        Map<String, String> headers = new HashMap<>();
        headers.put(caHeaderName, caFile);

        downloadFile(PATCH_DOWNLOAD_URL_PREFIX, headers);
    }

    private HttpResponse checkUpdate() throws IOException {
        HttpResponse checkUpdateResp;

        Map<String, String> headers = new HashMap<>();
        headers.put(RiverHttpHeaders.PLATFORM, getIdentifier().getPlatform());
        headers.put(RiverHttpHeaders.VERSION, currentPlatformVersion);


        for (String caName : currentCAVersions.keySet()) {
            headers.put(createCAHeaderName(caName), currentCAVersions.get(caName));
        }

        checkUpdateResp = httpGet(CHECK_UPDADE_URL_PREFIX, headers);
        return checkUpdateResp;
    }

    private void downloadPlatform(String downloadInfo) throws IOException {
        String[] patches = downloadInfo.split(FIRST_SEPARATE);

        for (String patch : patches) {
            String patchId = getPatchId(patch);
            logger.info("下载补丁ID:" + patchId);
            downloadPatch(patchId);
        }
    }

    private void downloadPatch(String patchId) throws IOException {
        Map<String, String> headers = new HashMap<>();
        headers.put(RiverHttpHeaders.PID, patchId);
        headers.put(RiverHttpHeaders.PLATFORM, getIdentifier().getPlatform());
        downloadFile(PATCH_DOWNLOAD_URL_PREFIX, headers);
    }

    private void downloadFile(String downloadUrl, Map<String, String> headers) throws IOException {
        HttpResponse downloadResp;
        downloadResp = httpGet(downloadUrl, headers);

        HttpEntity entity = downloadResp.getEntity();
        int status = downloadResp.getStatusLine().getStatusCode();
        if (HttpStatus.SC_OK == status) { // 200
            InputStream is = entity.getContent();

            String fileName = getFileName(downloadResp);
            if (StringUtils.isEmpty(fileName)) {
                throw new IOException("获取下载文件名失败。");
            }
            File patchFile = new File(downloadDir, fileName);

            IOUtils.copy(is, new FileOutputStream(patchFile));

            logger.info("成功下载一个文件:" + fileName);
        } else if (HttpStatus.SC_NOT_FOUND == status) { // 404
            logger.warn("暂时没有找到文件，请等候下次更新。");
        } else if (HttpStatus.SC_FORBIDDEN == status) { // 403
            logger.warn("用户没有授权，请确认是否有合法的授权文件。");
            throw new IOException("用户没有授权，请确认是否有合法的授权文件。");
        } else { // 其他错误?
            logger.warn("下载遇到其他未知错误：" + downloadResp.getStatusLine().getReasonPhrase());
            throw new IOException("下载遇到其他未知错误：" + downloadResp.getStatusLine().getReasonPhrase());
        }
    }

    public String getPatchId(String updateItem) {
        if (updateItem.contains(SECOND_SEPARATE)) {
            String[] items = updateItem.split(SECOND_SEPARATE);
            if (items.length == 2) {
                return items[0];
            }
        }
        return updateItem;
    }

    private String createCAHeaderName(String caName) {
        return RiverHttpHeaders.CA_PREFIX + caName.toLowerCase();
    }

}
