/**
 * 
 */
package com.riversoft.platform.web;

import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.riversoft.core.exception.ExceptionType;
import com.riversoft.core.exception.SystemRuntimeException;
import com.riversoft.core.web.Actions;
import com.riversoft.core.web.annotation.ActionMode;
import com.riversoft.core.web.annotation.ActionMode.Mode;
import com.riversoft.platform.script.ScriptHelper;
import com.riversoft.platform.script.ScriptTypes;

/**
 * 分页标签
 * 
 * @author Woden Wang
 * @since Feb 20, 2011
 */
@SuppressWarnings("serial")
public class JavaScriptTag extends TagSupport {

	private static final Logger logger = LoggerFactory.getLogger(JavaScriptTag.class);

	private Integer type;
	private String script;
	private Map<String, Object> context;

	private String zone;
	private String form;

	private String actionMode;// 对应的actionMode

	public int doEndTag() throws JspException {
		HttpServletRequest request = (HttpServletRequest) pageContext.getRequest();

		if (type == null || StringUtils.isEmpty(script)) {
			return SKIP_BODY;
		}

		try {
			ScriptTypes scriptTypes = ScriptTypes.forCode(type);
			Object value = ScriptHelper.evel(scriptTypes, script, context);

			if (StringUtils.isNotEmpty(zone)) {
				request.setAttribute("_zone", zone);
			}
			if (StringUtils.isNotEmpty(form)) {
				request.setAttribute("_form", form);
			} else {
				request.setAttribute("_form", UUID.randomUUID().toString());
			}

			request.setAttribute("_mode", actionMode);

			request.setAttribute("value", value);

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
			pageContext.include(module + Actions.Pages.JAVA_SCRIPT_TAG.getPage());
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

	/**
	 * @param zone
	 *            the zone to set
	 */
	public void setZone(String zone) {
		this.zone = zone;
	}

	/**
	 * @param form
	 *            the form to set
	 */
	public void setForm(String form) {
		this.form = form;
	}

	/**
	 * @param type
	 *            the type to set
	 */
	public void setType(Integer type) {
		this.type = type;
	}

	/**
	 * @param script
	 *            the script to set
	 */
	public void setScript(String script) {
		this.script = script;
	}

	/**
	 * @param context
	 *            the context to set
	 */
	public void setContext(Map<String, Object> context) {
		this.context = context;
	}

}
