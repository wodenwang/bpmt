/*
 * $HeadURL: $
 * $Id: $
 * Copyright (c) 2014 by Riversoft System, all rights reserved.
 */
package com.riversoft.platform.web;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author woden
 * 
 */
@SuppressWarnings("serial")
public class SessionLogTag extends TagSupport {
	/**
	 * Logger for this class
	 */
	@SuppressWarnings("unused")
	private static final Logger logger = LoggerFactory.getLogger(SessionLogTag.class);

	private String msg;
	private Integer size;

	private int type = 0;

	protected SessionLogTag(int type) {
		this.type = type;
	}

	public int doStartTag() throws JspException {
		switch (type) {
		case 0:
			WebLogManager.log(msg);
			break;
		case 1:
			WebLogManager.beginLoop(msg, size);
			break;
		case 2:
			WebLogManager.signalLoop();
			break;
		default:
			break;
		}

		return SKIP_BODY;
	}

	/**
	 * @param msg
	 *            the msg to set
	 */
	public void setMsg(String msg) {
		this.msg = msg;
	}

	/**
	 * @param size
	 *            the size to set
	 */
	public void setSize(Integer size) {
		this.size = size;
	}

	/**
	 * 标记
	 * 
	 * @author woden
	 * 
	 */
	public static class LogTag extends SessionLogTag {
		public LogTag() {
			super(0);
		}

	}

	/**
	 * 标记
	 * 
	 * @author woden
	 * 
	 */
	public static class LoopTag extends SessionLogTag {
		public LoopTag() {
			super(1);
		}

	}

	/**
	 * 标记
	 * 
	 * @author woden
	 * 
	 */
	public static class SignTag extends SessionLogTag {
		public SignTag() {
			super(2);
		}

	}
}
