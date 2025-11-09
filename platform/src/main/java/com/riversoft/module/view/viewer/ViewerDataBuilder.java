/*
 * $HeadURL: $
 * $Id: $
 * Copyright (c) 2014 by Riversoft System, all rights reserved.
 */
package com.riversoft.module.view.viewer;

import java.util.List;

import com.riversoft.core.context.RequestContext;
import com.riversoft.core.db.DataPO;
import com.riversoft.platform.web.FileManager;
import com.riversoft.platform.web.FileManager.UploadFile;
import com.riversoft.platform.web.handler.BaseDataBuilder;

/**
 * @author woden
 * 
 */
public class ViewerDataBuilder extends BaseDataBuilder {

	/**
	 * @param dataPO
	 */
	protected ViewerDataBuilder(DataPO dataPO) {
		super(dataPO);
	}

	@Override
	public void build() {
		RequestContext request = RequestContext.getCurrent();
		DataPO tablePO = getDataPO();
		{
			tablePO.set("busiName", request.getString("busiName"));
			tablePO.set("resultType", request.getInteger("resultType"));

			tablePO.set("tempFileType", request.getInteger("tempFileType"));
			tablePO.set("tempFilePath", request.getString("tempFilePath"));
			List<UploadFile> templateFile = FileManager.getUploadFiles(request, "templateFile");
			if (templateFile != null && !templateFile.isEmpty()) {
				tablePO.set("templateFile", FileManager.toBytes(null, templateFile));
			}

			tablePO.set("textType", request.getInteger("textType"));
			tablePO.set("textScript", request.getString("textScript"));

			tablePO.set("msgType", request.getInteger("msgType"));
			tablePO.set("msgScript", request.getString("msgScript"));

			tablePO.set("fileType", request.getInteger("fileType"));
			tablePO.set("fileScript", request.getString("fileScript"));

			tablePO.set("urlType", request.getInteger("urlType"));
			tablePO.set("urlScript", request.getString("urlScript"));
		}
	}
}
