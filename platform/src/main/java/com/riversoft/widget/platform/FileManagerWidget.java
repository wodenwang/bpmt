/*
 * $HeadURL: $
 * $Id: $
 * Copyright (c) 2013 by Riversoft System, all rights reserved.
 */
package com.riversoft.widget.platform;

import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.riversoft.core.Config;
import com.riversoft.core.IDGenerator;
import com.riversoft.core.context.RequestContext;
import com.riversoft.core.web.Actions.Keys;
import com.riversoft.core.web.widget.FormValue;
import com.riversoft.core.web.widget.FormWidget;
import com.riversoft.core.web.widget.Widget;
import com.riversoft.core.web.widget.WidgetAnnotation;
import com.riversoft.core.web.widget.WidgetState;
import com.riversoft.platform.web.FileManager;
import com.riversoft.platform.web.FileManager.UploadFile;

/**
 * @author woden
 * 
 */
@WidgetAnnotation(cmd = "filemanager", ftl = "classpath:widget/{mode}/platform/filemanager.ftl")
public class FileManagerWidget implements Widget {
	/**
	 * Logger for this class
	 */
	private static final Logger logger = LoggerFactory.getLogger(FileManagerWidget.class);

	/**
	 * disk:存硬盘<br>
	 * db:存数据库,默认
	 */
	private String mode = FileManager.DB_MODE;

	/**
	 * 生成文件字符串
	 * 
	 * @param files
	 * @return
	 */
	public String toValue(UploadFile... files) {
		StringBuffer buff = new StringBuffer();
		buff.append("[");
		StringBuffer body = new StringBuffer();
		if (files != null && files.length > 0) {
			for (UploadFile file : files) {
				file.setMode(mode);
				body.append(",").append(file.getValue());
			}
		} else {
			body.append(",");
		}
		buff.append(body.substring(1));
		buff.append("]");
		return buff.toString();
	}

	@Override
	public void prepareMap(Map<String, Object> map) {
		Object value = map.get("value");
		if (value instanceof byte[]) {// 如果入参是流,则转换
			UploadFile[] list = FileManager.toFiles((byte[]) value).toArray(new UploadFile[0]);
			map.put("list", list);

			String str = toValue(list);
			map.put("value", str);
			String fileId = FileManager.toId((byte[]) value);
			map.put("fileId", fileId);
		}
		map.put("mode", mode);
		if (StringUtils.isEmpty((String) map.get("fileId"))) {
			map.put("fileId", IDGenerator.uuid());
		}

		// 默认下发agent
		String agent = Config.get("wx.qy.default", "");
		map.put("agent", agent);

		map.put("multiFlag", "false");
	}

	@Override
	public void setParams(FormValue... values) {
		if (values != null && values.length > 0) {
			if (values[0].getName().equalsIgnoreCase("disk")) {
				mode = FileManager.DISK_MODE;
			}
		}
	}

	@Override
	public String show(Object value) {
		RequestContext request = RequestContext.getCurrent();
		return new FormWidget("filemanager[" + mode + "]").toHtml("_tmp", WidgetState.readonly, value, request.getString(Keys.ACTION_MODE.toString()));
	}

	@Override
	public Object code(String showName) {
		return showName;
	}

}
