package com.riversoft.wx.qy;

import com.riversoft.core.exception.ExceptionType;
import com.riversoft.core.exception.SystemRuntimeException;
import com.riversoft.platform.web.FileManager;
import com.riversoft.platform.web.FileManager.UploadFile;
import com.riversoft.weixin.common.media.MediaType;
import com.riversoft.weixin.qy.media.Medias;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * 企业号临时素材管理 Created by exizhai on 9/7/2015.
 */
public class QyMediaHelper {
	
	private final static QyMediaHelper INSTANCE = new QyMediaHelper();
	private QyMediaHelper(){}
	public static QyMediaHelper getInstance(){
		return INSTANCE;
	}

	/**
	 * 上传临时文件
	 *
	 * @param obj
	 * @return
	 */
	public static String upload(byte[] obj) {
		String[] mediaIds = uploads(obj);
		return mediaIds[0];
	}

	/**
	 * 上传临时文件
	 *
	 * @param obj
	 * @return
	 */
	public static String[] uploads(byte[] obj) {
		List<UploadFile> fileList = FileManager.toFiles(obj);
		if (fileList == null || fileList.size() < 1) {
			throw new SystemRuntimeException(ExceptionType.SCRIPT, "解析文件不存在.");
		}
		List<String> mediaIds = new ArrayList<>();
		for (UploadFile file : fileList) {
			String fileName = file.getName();
			String extName = fileName.substring(fileName.lastIndexOf(".") + 1);
			MediaType type;

			if (ArrayUtils.contains(StringUtils.split("png|jpg|jpeg", "|"), extName)) {
				type = MediaType.image;
			} else if (ArrayUtils.contains(StringUtils.split("mp4|avi|rmvb", "|"), extName)) {
				type = MediaType.video;
			} else if (ArrayUtils.contains(StringUtils.split("mp3|wma|wav|amr", "|"), extName)) {
				type = MediaType.voice;
			} else if (ArrayUtils.contains(StringUtils.split("doc|docx|ppt|pptx|xls|xlsx|txt|zip|xml|pdf", "|"), extName)) {
				type = MediaType.file;
			} else {
				throw new SystemRuntimeException(ExceptionType.BUSINESS, "文件[" + file.getName() + "]类型不支持上传到微信.");
			}

			try {
				String mediaId = Medias.defaultMedias().upload(type, file.getInputStream(), fileName);
				mediaIds.add(mediaId);
			} catch (FileNotFoundException e) {
				throw new SystemRuntimeException(e);
			}
		}
		return mediaIds.toArray(new String[0]);
	}

	/**
	 * 下载临时文件
	 *
	 * @param mediaIds
	 * @return
	 */
	public static byte[] download(String... mediaIds) {
		List<UploadFile> fileList = new ArrayList<>();
		for (String mediaId : mediaIds) {
			File file = Medias.defaultMedias().download(mediaId);
			try {
				UploadFile uploadFile = new FileManager.DevFile(file.getName(), FileUtils.readFileToByteArray(file));
				fileList.add(uploadFile);
			} catch (IOException e) {
				throw new SystemRuntimeException(e);
			}
		}
		return FileManager.toBytes(null, fileList);
	}

	/**
	 * 下载临时文件
	 * 
	 * @param mediaId
	 * @return
	 */
	public File downloadFile(String mediaId) {
		File file = Medias.defaultMedias().download(mediaId);
		return file;
	}
}
