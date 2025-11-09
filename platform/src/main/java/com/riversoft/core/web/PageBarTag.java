/**
 * 
 */
package com.riversoft.core.web;

import java.util.Random;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.riversoft.core.db.DataPackage;
import com.riversoft.core.exception.ExceptionType;
import com.riversoft.core.exception.SystemRuntimeException;
import com.riversoft.core.web.Actions.Pages;
import com.riversoft.core.web.annotation.ActionMode;
import com.riversoft.core.web.annotation.ActionMode.Mode;

/**
 * 分页标签
 * 
 * @author Woden Wang
 * @since Feb 20, 2011
 */
@SuppressWarnings("serial")
public class PageBarTag extends TagSupport {

	private static final Logger logger = LoggerFactory.getLogger(PageBarTag.class);

	private String form;
	private String params;
	private DataPackage dp;
	private Integer defLimit;
	private String actionMode;// 对应的actionMode

	public int doEndTag() throws JspException {
		HttpServletRequest request = (HttpServletRequest) pageContext.getRequest();
		try {
			request.setAttribute("dp", dp);
			request.setAttribute("form", form);
			request.setAttribute("params", params);
			request.setAttribute("random", new Random().nextLong());
			request.setAttribute("defLimit", defLimit);

			String module;
			if (StringUtils.isNotEmpty(actionMode)) {
				ActionMode.Mode mode = Mode.valueOf(actionMode.toUpperCase());
				if (mode == Mode.XHTML) {
					module = "/xhtml";
				} else if (mode == Mode.H5) {
					module = "/h5";
				} else if (actionMode.startsWith("/")) {
					module = actionMode;
				} else {
					throw new SystemRuntimeException(ExceptionType.CODING, "标签不支持[actionMode=" + actionMode + "]");
				}
			} else {
				module = "/xhtml";
			}
			pageContext.include(module + Pages.PAGE_BAR_TAG.getPage());// 先不考虑H5/MODULE
		} catch (Exception e) {
			logger.warn("获取页面文件失败.", e);
		}

		return SKIP_BODY;
	}

	/**
	 * @param actionMode
	 *            the actionMode to set
	 */
	public void setActionMode(String actionMode) {
		this.actionMode = actionMode;
	}

	public void setForm(String form) {
		this.form = form;
	}

	public void setDp(DataPackage dp) {
		this.dp = dp;
	}

	public void setParams(String params) {
		this.params = params;
	}

	public void setDefLimit(Integer defLimit) {
		this.defLimit = defLimit;
	}

}
