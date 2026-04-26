/*
 * $HeadURL: $
 * $Id: $
 * Copyright (c) 2015 by Riversoft System, all rights reserved.
 */
package com.riversoft.platform.web;

import java.io.Serializable;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.StringUtils;

import com.riversoft.core.context.RequestContext;
import com.riversoft.core.web.Actions.Keys;
import com.riversoft.platform.SessionManager;

/**
 * 登记业务处理log
 * 
 * @author woden
 * 
 */
public class WebLogManager {

	public static class LogVO implements Serializable {
		private static final LogVO DEFAULT = new LogVO(true, "已完成", 0);

		/**
		 * 是否完成
		 */
		private boolean complete;
		/**
		 * 提示
		 */
		private String msg;
		/**
		 * 循环最长,0标识无循环
		 */
		private Integer max = 0;
		/**
		 * 当前循环值
		 */
		private AtomicInteger current = new AtomicInteger(0);

		/**
		 * 是否成功
		 */
		private boolean success = true;

		/**
		 * 构造函数
		 * 
		 * @param complete
		 * @param msg
		 * @param max
		 */
		private LogVO(boolean complete, String msg, Integer max) {
			super();
			this.complete = complete;
			this.msg = msg;
			this.max = max;
		}

		/**
		 * @return the success
		 */
		public boolean isSuccess() {
			return success;
		}

		/**
		 * @return the complete
		 */
		public boolean isComplete() {
			return complete;
		}

		/**
		 * @return the msg
		 */
		public String getMsg() {
			return msg;
		}

		/**
		 * @return the max
		 */
		public Integer getMax() {
			return max;
		}

		/**
		 * @return the current
		 */
		public Integer getCurrent() {
			return current.get();
		}
	}

	/**
	 * 每次http调用周期所使用的request信息
	 */
	private static ThreadLocal<HttpSession> sessionValue = new ThreadLocal<>();

	/**
	 * 登记文字日志
	 * 
	 * @param msg
	 */
	public static void log(String msg) {
		LogVO vo = getLog(RequestContext.getCurrent().getString(Keys.RANDOM.toString()));
		if (!vo.complete) {
			synchronized (vo) {
				vo.msg = msg;
				vo.max = 0;
			}
		}
	}

	/**
	 * 登记异常
	 * 
	 * @param msg
	 */
	public static void error(String msg) {
		LogVO vo = getLog(RequestContext.getCurrent().getString(Keys.RANDOM.toString()));
		if (!vo.complete) {
			synchronized (vo) {
				vo.msg = msg;
				vo.max = 0;
				vo.success = false;
				vo.complete = true;
			}
		}
	}

	/**
	 * 开始登记循环日志
	 * 
	 * @param msg
	 *            文字提示
	 * @param max
	 *            循环长度
	 */
	public static void beginLoop(String msg, Integer max) {
		LogVO vo = getLog(RequestContext.getCurrent().getString(Keys.RANDOM.toString()));
		if (!vo.complete) {
			synchronized (vo) {
				vo.max = max;
				vo.current.set(0);
				vo.msg = msg;
			}
		}
	}

	/**
	 * 标识循环信号
	 * 
	 */
	public static void signalLoop() {
		LogVO vo = getLog(RequestContext.getCurrent().getString(Keys.RANDOM.toString()));
		if (!vo.complete) {
			vo.current.incrementAndGet();
		}
	}

	/**
	 * 获取LOG对象
	 * 
	 * @param random
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static LogVO getLog(String random) {
		HttpSession session = sessionValue.get();
		if (StringUtils.isNotEmpty(random) && session != null) {
			Map<String, Object> map = (Map<String, Object>) session.getAttribute(SessionManager.SessionAttributeKey.LOG
					.toString());
			if (map != null && map.containsKey(random)) {
				return (LogVO) map.get(random);
			}
		}

		return LogVO.DEFAULT;
	}

	/**
	 * 初始化,系统框架调用
	 */
	@SuppressWarnings("unchecked")
	public static void init(HttpSession session) {
		String random = RequestContext.getCurrent().getString(Keys.RANDOM.toString());
		if (StringUtils.isNotEmpty(random)) {
			sessionValue.set(session);
			Map<String, Object> map = (Map<String, Object>) sessionValue.get().getAttribute(
					SessionManager.SessionAttributeKey.LOG.toString());
			if (map != null) {
				map.put(random, new LogVO(false, "加载中.", 0));
			}
		}
	}

	/**
	 * 销毁,系统框架调用
	 */
	@SuppressWarnings("unchecked")
	public static void destroy() {
		String random = RequestContext.getCurrent().getString(Keys.RANDOM.toString());
		if (StringUtils.isNotEmpty(random)) {
			LogVO vo = getLog(random);
			if (!vo.complete) {
				synchronized (vo) {
					Map<String, Object> map = (Map<String, Object>) sessionValue.get().getAttribute(
							SessionManager.SessionAttributeKey.LOG.toString());
					if (map != null) {
						map.remove(random);
					}
				}
			}
			sessionValue.remove();
		}
	}

}
