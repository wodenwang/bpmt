/*
 * File Name  :InitServlet.java
 * Create Date:2012-11-6 上午12:06:17
 * Author     :woden
 */

package com.riversoft.platform.web;

import java.io.File;
import java.net.URL;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.riversoft.core.BeanFactory;
import com.riversoft.core.Config;
import com.riversoft.core.web.annotation.ActionAccess;
import com.riversoft.core.web.annotation.ActionAccess.SafeRole;
import com.riversoft.platform.Platform;

/**
 * 初始化Servlet容器.
 * 
 */
@SuppressWarnings("serial")
public class InitServlet extends HttpServlet {

	private String PRODUCTION_FILENAME = "production.properties";

	Logger logger = LoggerFactory.getLogger(InitServlet.class);

	public void init(ServletConfig config) throws ServletException {
		File root = null;
		URL initFileUrl = Thread.currentThread().getContextClassLoader().getResource("init0.properties");
		if (initFileUrl == null) { // 标准部署
			URL url = Thread.currentThread().getContextClassLoader().getResource(PRODUCTION_FILENAME);
			root = getPlatformRootPath(url, 4);
		} else {// 客户自定义部署
			root = getPlatformRootPath(initFileUrl, 2);
		}

		Platform.init(root);

		logger.info("========== BPMT Spring容器初始化  开始 ==========");
		BeanFactory.init("classpath:applicationContext.xml");
		logger.info("========== BPMT Spring容器初始化  结束 ==========");

		logger.info("========== BPMT 平台安全初始化  开始 ==========");
		SafeRole safeRole = ActionAccess.SafeRole.forName(Config.get("safe.role", ""));
		if (safeRole == SafeRole.DEV_SYS) {// B系统启动之后平台默认暂停
			logger.info("当前系统为[{}-{}],平台将自动暂停(仅允许管理员登录).", safeRole.name(), safeRole.getBusiName());
			Platform.pause();
		} else {
			logger.info("当前系统为[{}-{}].", safeRole.name(), safeRole.getBusiName());
		}
		logger.info("========== BPMT 平台安全初始化  结束 ==========");

	}

	/**
	 * 获取生产系统平台路径
	 * 
	 * @param url
	 * @param parentLevel
	 * @return
	 */
	private File getPlatformRootPath(URL url, int parentLevel) {
		if (url == null) {
			return null;
		}

		File file = new File(url.getFile());
		if (file.exists()) {

			File root = file;
			for (int i = 0; i < parentLevel; i++) {
				root = root.getParentFile();
				if (!root.exists()) {
					return null;
				}
			}
			if (root.exists() && root.isDirectory()) {
				return root;
			}
		}
		return null;
	}
}
