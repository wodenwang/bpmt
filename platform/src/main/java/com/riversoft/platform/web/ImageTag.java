/*
 * $HeadURL: $
 * $Id: $
 * Copyright (c) 2015 by Riversoft System, all rights reserved.
 */
package com.riversoft.platform.web;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.List;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;

import org.apache.commons.lang3.StringUtils;

import com.riversoft.core.web.Actions.Keys;
import com.riversoft.platform.po.UsUser;
import com.riversoft.platform.script.function.ImageHelper;
import com.riversoft.platform.web.FileManager.UploadFile;

/**
 * @author woden
 *
 */
@SuppressWarnings("serial")
public class ImageTag extends TagSupport {

	private Object value;
	private String style = "";
	private String cssClass = "";
	private String cssName = "";
	private String cssId = "";

	public int doEndTag() throws JspException {

		StringBuffer attr = new StringBuffer();
		if (StringUtils.isNotEmpty(cssId)) {
			attr.append(" id=\"" + cssId + "\" ");
		}
		if (StringUtils.isNotEmpty(style)) {
			attr.append(" style=\"" + style + "\" ");
		}
		if (StringUtils.isNotEmpty(cssName)) {
			attr.append(" name=\"" + cssName + "\" ");
		}
		if (StringUtils.isNotEmpty(cssClass)) {
			attr.append(" class=\"" + cssClass + "\" ");
		}

		if (value == null) {
			return SKIP_BODY;
		} else if (value instanceof byte[]) {
			List<UploadFile> list = FileManager.toFiles((byte[]) value);
			if (list != null && list.size() > 0) {
				UploadFile file = list.get(0);
				String cp = (String) pageContext.getRequest().getAttribute(Keys.CP.toString());
				try {
					String url = cp + "/widget/FileAction/download.shtml?name=" + file.getSysName() + "&type=" + file.getType() + "&fileName=" + URLEncoder.encode(file.getName(), "UTF-8");
					pageContext.getOut().append("<img src=\"" + url + "\" " + attr + "/>");
				} catch (IOException ignore) {
					// do nothing
				}
			}
		} else if (value instanceof UsUser) {
			try {
				UsUser u = (UsUser) value;
				String picUrl = u.getWxAvatar();
				if (StringUtils.isEmpty(picUrl)) {
					picUrl = ImageHelper.img(u.getBusiName());
				}
				pageContext.getOut().append("<img src=\"" + picUrl + "\" " + attr + "/>");
			} catch (IOException ignore) {
				// do nothing
			}
		} else if (value instanceof String) {
			if (StringUtils.startsWithAny((String) value, "http", "/", "data:image/")) {
				try {
					pageContext.getOut().append("<img src=\"" + value + "\" " + attr + "/>");
				} catch (IOException ignore) {
					// do nothing
				}
			} else {
				try {
					String string = (String) value;
					if (StringUtils.isNotBlank(string)) {
						pageContext.getOut().append("<img src=\"" + ImageHelper.img(string) + "\" " + attr + "/>");
					}
				} catch (IOException ignore) {
					// do nothing
				}
			}
		}

		return SKIP_BODY;
	}

	/**
	 * @param value
	 *            the value to set
	 */
	public void setValue(Object value) {
		this.value = value;
	}

	/**
	 * @param style
	 *            the style to set
	 */
	public void setStyle(String style) {
		this.style = style;
	}

	public void setCssClass(String cssClass) {
		this.cssClass = cssClass;
	}

	public void setCssName(String cssName) {
		this.cssName = cssName;
	}

	public void setCssId(String cssId) {
		this.cssId = cssId;
	}
}
