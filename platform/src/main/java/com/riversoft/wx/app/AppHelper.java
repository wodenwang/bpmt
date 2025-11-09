package com.riversoft.wx.app;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.riversoft.core.Config;
import com.riversoft.core.IDGenerator;
import com.riversoft.core.exception.SystemRuntimeException;
import com.riversoft.platform.web.FileManager;
import com.riversoft.platform.web.FileManager.UploadFile;
import com.riversoft.weixin.app.base.AppSetting;
import com.riversoft.weixin.app.qrcode.QrCodes;
import com.riversoft.weixin.app.template.Message;
import com.riversoft.weixin.app.template.Message.Data;
import com.riversoft.weixin.app.template.Templates;

/**
 * 小程序函数库
 * 
 * Created by Chris on 2/20/2017.
 */

public class AppHelper {

	private static Logger logger = LoggerFactory.getLogger(AppHelper.class);

	private AppSetting appSetting;

	public AppHelper() {
		this.appSetting = setAppSetting();
	}

	private AppSetting setAppSetting() {
		AppSetting appSetting = new AppSetting();
		appSetting.setAppId((String) Config.get("wx.app.appId"));
		appSetting.setSecret((String) Config.get("wx.app.appSecrept"));
		return appSetting;
	}

	/**
	 * 获取小程序页面二维码
	 * 
	 * @param path
	 * @return
	 */
	public InputStream createQrCodeStream(String path) {
		return QrCodes.with(appSetting).create(path);
	}

	/**
	 * 获取小程序页面二维码
	 * 
	 * @param path
	 * @param size
	 * @return
	 */
	public InputStream createQrCodeStream(String path, int size) {
		return QrCodes.with(appSetting).create(path, size);
	}

	/**
	 * 获取小程序页面二维码(可存数据库数据)
	 * 
	 * @param path
	 * @return
	 */
	public byte[] createQrCode(String path) {
		List<UploadFile> fileList = new ArrayList<>();
		try {
			UploadFile uploadFile = FileManager.saveDbFile(IDGenerator.uuid(),
					IOUtils.toByteArray(createQrCodeStream(path)));
			fileList.add(uploadFile);
		} catch (IOException e) {
			throw new SystemRuntimeException(e);
		}
		return FileManager.toBytes(null, fileList);
	}

	/**
	 * 获取小程序页面二维码(可存数据库数据)
	 * 
	 * @param path
	 * @param size
	 * @return
	 */
	public byte[] createQrCode(String path, int size) {
		List<UploadFile> fileList = new ArrayList<>();
		try {
			UploadFile uploadFile = FileManager.saveDbFile(IDGenerator.uuid(),
					IOUtils.toByteArray(createQrCodeStream(path, size)));
			fileList.add(uploadFile);
		} catch (IOException e) {
			throw new SystemRuntimeException(e);
		}
		return FileManager.toBytes(null, fileList);
	}

	/**
	 * 模板消息
	 * 
	 * @param param
	 */
	public void sendTemplateMsg(Map<String, Object> param) {
		Message message = new Message();
		message.setToUser((String) param.get("openId"));
		message.setFormId((String) param.get("formId"));
		message.setTemplateId((String) param.get("templateId"));
		message.setHighlight((String) param.get("highlight"));
		message.setPage((String) param.get("page"));

		Map<String, Map<String, String>> params = (Map<String, Map<String, String>>) param.get("data");
		Map<String, Data> data = new HashMap<>();
		if (params != null) {
			for (String type : params.keySet()) {
				Map<String, String> value = params.get(type);
				Data item = new Data(value.get("value"), value.get("color"));
				data.put(type, item);
			}
		}
		message.setData(data);
		Templates.with(appSetting).send(message);
	}
}
