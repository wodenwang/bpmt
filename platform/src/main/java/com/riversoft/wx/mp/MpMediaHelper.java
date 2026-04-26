package com.riversoft.wx.mp;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import com.riversoft.core.exception.ExceptionType;
import com.riversoft.core.exception.SystemRuntimeException;
import com.riversoft.platform.web.FileManager;
import com.riversoft.platform.web.FileManager.UploadFile;
import com.riversoft.weixin.common.media.Media;
import com.riversoft.weixin.common.media.MediaType;
import com.riversoft.weixin.mp.base.AppSetting;
import com.riversoft.weixin.mp.media.Medias;
import com.riversoft.wx.mp.service.MpAppService;

/**
 * 公众号号临时素材管理
 * <p/>
 * 上传的临时多媒体文件有格式和大小限制，如下： 图片（image）: 1M，支持JPG格式
 * 语音（voice）：2M，播放长度不超过60s，支持AMR\MP3格式 视频（video）：10MB，支持MP4格式
 * 缩略图（thumb）：64KB，支持JPG格式 媒体文件在后台保存时间为3天，即3天后media_id失效
 * <p/>
 * Created by exizhai on 9/7/2015.
 */
public class MpMediaHelper {

	private AppSetting appSetting;

	public MpMediaHelper() {
	}

	public MpMediaHelper(String mpKey) {
		this.appSetting = MpAppService.getInstance().getAppSettingByPK(mpKey);
	}

	public MpMediaHelper(AppSetting appSetting) {
		this.appSetting = appSetting;
	}

	public void setAppSetting(AppSetting appSetting) {
		this.appSetting = appSetting;
	}

	/**
	 * 上传文件
	 * 
	 * @param name
	 * @param obj
	 * @return
	 */
	public String upload(String name, byte[] obj) throws FileNotFoundException {
		// UploadFile file = new DevFile(name, obj);

		String fileName = name;
		String extName = fileName.substring(fileName.lastIndexOf(".") + 1);
		MediaType type;

		if (ArrayUtils.contains(StringUtils.split("png|jpg|jpeg", "|"), extName)) {
			type = MediaType.image;
		} else if (ArrayUtils.contains(StringUtils.split("mp4|avi|rmvb", "|"), extName)) {
			type = MediaType.video;
		} else if (ArrayUtils.contains(StringUtils.split("mp3|wma|wav|amr", "|"), extName)) {
			type = MediaType.voice;
		} else {
			throw new SystemRuntimeException(ExceptionType.BUSINESS, "文件[" + name + "]类型不支持上传到微信.");
		}
		Media media = Medias.with(appSetting).upload(type, new ByteArrayInputStream(obj), fileName);
		return media.getMediaId();
	}

	/**
	 * 上传临时文件
	 *
	 * @param obj
	 * @return
	 */
	public String upload(byte[] obj) {
		String[] mediaIds = uploads(obj);
		return mediaIds[0];
	}

	/**
	 * 上传临时文件
	 *
	 * @param obj
	 * @return
	 */
	public String[] uploads(byte[] obj) {
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
			} else {
				throw new SystemRuntimeException(ExceptionType.BUSINESS, "文件[" + file.getName() + "]类型不支持上传到微信.");
			}

			try {
				Media media = Medias.with(appSetting).upload(type, file.getInputStream(), fileName);
				mediaIds.add(media.getMediaId());
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
	public byte[] download(String... mediaIds) {
		List<UploadFile> fileList = new ArrayList<>();
		for (String mediaId : mediaIds) {
			File file = Medias.with(appSetting).download(mediaId);
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
		File file = Medias.with(appSetting).download(mediaId);
		return file;
	}

}
