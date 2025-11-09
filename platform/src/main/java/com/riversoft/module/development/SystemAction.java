/*
 * $HeadURL: $
 * $Id: $
 * Copyright (c) 2014 by Riversoft System, all rights reserved.
 */
package com.riversoft.module.development;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.DatabaseMetaData;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import javax.sql.DataSource;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

import com.riversoft.core.BeanFactory;
import com.riversoft.core.Config;
import com.riversoft.core.db.ORMService;
import com.riversoft.core.exception.ExceptionType;
import com.riversoft.core.exception.SystemRuntimeException;
import com.riversoft.core.web.Actions;
import com.riversoft.core.web.Actions.Util;
import com.riversoft.core.web.RequestUtils;
import com.riversoft.core.web.annotation.ActionAccess;
import com.riversoft.core.web.annotation.ActionAccess.SafeRole;
import com.riversoft.license.api.Magic;
import com.riversoft.platform.Platform;
import com.riversoft.platform.db.model.ModelKeyUtils;
import com.riversoft.platform.mail.model.AccountModelKeys;
import com.riversoft.platform.mail.model.InboxModelKeys;
import com.riversoft.platform.mail.model.OutboxModelKeys;
import com.riversoft.platform.po.TbTable;
import com.riversoft.platform.template.Template;
import com.riversoft.util.Formatter;
import com.riversoft.util.jackson.JsonMapper;
import com.riversoft.wx.mp.model.OpenVisitorModelKeys;

/**
 * 系统设置
 * 
 * @author woden
 * 
 */
public class SystemAction {

	/**
	 * 系统维护
	 * 
	 * @param request
	 * @param response
	 */
	@ActionAccess(admin = true)
	public void index(HttpServletRequest request, HttpServletResponse response) {

		// JDK版本
		request.setAttribute("jdk", System.getProperty("java.version"));

		request.setAttribute("osName", System.getProperty("os.name")); // 操作系统名称
		request.setAttribute("osArch", System.getProperty("os.arch")); // 操作系统构架
		request.setAttribute("osVersion", System.getProperty("os.version")); // 操作系统版本
		String totalSpace = Formatter.formatNumber(new BigDecimal(Platform.getRoot().getTotalSpace()).divide(new BigDecimal(1024 * 1024 * 1024)), "#,##0.00");
		String usableSpace = Formatter.formatNumber(new BigDecimal(Platform.getRoot().getUsableSpace()).divide(new BigDecimal(1024 * 1024 * 1024)), "#,##0.00");
		request.setAttribute("totalSpace", totalSpace);
		request.setAttribute("usableSpace", usableSpace);
		request.setAttribute("version", Platform.getVersion());

		// magic信息
		Magic magic = Platform.getMagic();
		request.setAttribute("magic", magic);
		request.setAttribute("allowUpdate", magic.canAutoUpdate());
		request.setAttribute("identifier", magic.currentIdentifier());

		// 系统日志
		List<Map<String, Object>> logs = new ArrayList<>();
		buildLogFileList(logs, Platform.getLogPath(), "");
		Collections.sort(logs, new Comparator<Map<String, Object>>() {
			@Override
			public int compare(Map<String, Object> o1, Map<String, Object> o2) {
				int leaf1 = (int) o1.get("leaf");
				int leaf2 = (int) o2.get("leaf");
				// 文件夹放前面
				if (leaf1 < leaf2) {
					return -1;
				}

				if (leaf2 < leaf1) {
					return 1;
				}

				String name1 = (String) o1.get("name");
				String name2 = (String) o2.get("name");
				return name1.compareTo(name2);
			}
		});
		request.setAttribute("logs", logs);

		// 数据库版本
		try {
			DataSource ds = (DataSource) BeanFactory.getInstance().getBean("dataSource");
			DatabaseMetaData databaseMetaData = ds.getConnection().getMetaData();
			String databaseProductName = databaseMetaData.getDatabaseProductName();
			if (StringUtils.isNotEmpty(databaseProductName)) {
				request.setAttribute("db", databaseProductName + " " + databaseMetaData.getDatabaseProductVersion());
			}

		} catch (Exception ignore) {
			// do nothing
		}

		// 是否暂停
		request.setAttribute("pause", Platform.checkPause());
		SafeRole role = SafeRole.forName(Config.get("safe.role"));
		request.setAttribute("safeRoleType", role.name());
		request.setAttribute("safeRole", role);
		request.setAttribute("roleIsPro", role.equals(SafeRole.PRO_SYS));// 生产系统
		request.setAttribute("template", Template.getCurrent());

		// 组件信息
		List<Platform.Component> list = Platform.getComponents();
		request.setAttribute("list", list);
		Actions.includePage(request, response, Util.getPagePath(request, "manage.jsp"));
	}

	/**
	 * 递归创建文件树
	 * 
	 * @param logs
	 * @param dir
	 * @param pname
	 */
	private void buildLogFileList(List<Map<String, Object>> logs, File dir, String pname) {
		if (!dir.isDirectory()) {// 只处理文件夹
			return;
		}

		for (File file : dir.listFiles(new FileFilter() {
			@Override
			public boolean accept(File f) {
				if (f.isDirectory()) {// 文件夹不过滤
					return true;
				}

				if (f.getName().toLowerCase().endsWith(".log")) {// 文件名是log结尾
					return true;
				}

				return false;
			}
		})) {
			Map<String, Object> o = new HashMap<>();
			o.put("id", pname + "/" + file.getName());
			o.put("name", file.getName());
			o.put("leaf", file.isFile() ? 1 : 0);
			o.put("pname", pname);
			if (file.isFile()) {
				o.put("title", "双击下载");
			}
			logs.add(o);
			buildLogFileList(logs, file, pname + "/" + file.getName());
		}
	}

	/**
	 * 下载日志文件
	 * 
	 * @param request
	 * @param response
	 * @throws FileNotFoundException
	 */
	@ActionAccess(admin = true)
	public void downloadLog(HttpServletRequest request, HttpServletResponse response) throws FileNotFoundException {
		String fileName = RequestUtils.getStringValue(request, "fileName");
		File logFile = new File(Platform.getLogPath(), fileName);
		if (!logFile.exists()) {
			throw new SystemRuntimeException(ExceptionType.BUSINESS, "文件[" + fileName + "]不存在.");
		}

		Actions.download(request, response, logFile.getName(), new FileInputStream(logFile));
	}

	/**
	 * 日志查看
	 * 
	 * @param request
	 * @param response
	 * @throws FileNotFoundException
	 */
	@ActionAccess(admin = true)
	public void showLog(HttpServletRequest request, HttpServletResponse response) throws FileNotFoundException {
		String fileName = RequestUtils.getStringValue(request, "fileName");
		File logFile = new File(Platform.getLogPath(), fileName);
		if (!logFile.exists()) {
			throw new SystemRuntimeException(ExceptionType.BUSINESS, "文件[" + fileName + "]不存在.");
		}

		try {
			int firstLine = 1;
			List<String> logs = FileUtils.readLines(logFile);
			if (logs.size() > 500) {// 仅截取500行
				firstLine = logs.size() - 500 + 1;
				logs = logs.subList(firstLine - 1, logs.size());
			}
			request.setAttribute("firstLine", firstLine);
			request.setAttribute("logs", StringUtils.join(logs, System.getProperty("line.separator")));
		} catch (IOException e) {
			throw new SystemRuntimeException(ExceptionType.BUSINESS, "获取日志[" + fileName + "]出错.", e);
		}

		Actions.includePage(request, response, Util.getPagePath(request, "log_detail.jsp"));
	}

	/**
	 * 暂停平台
	 * 
	 * @param request
	 * @param response
	 */
	@ActionAccess(admin = true)
	public void pausePlatform(HttpServletRequest request, HttpServletResponse response) {
		int pauseFlag = RequestUtils.getIntegerValue(request, "pause");
		if (pauseFlag == 1) {
			Platform.pause();
		} else {
			Platform.run();
		}

		Actions.redirectInfoPage(request, response, "操作成功,系统当前状态[" + (Platform.checkPause() ? "已暂停" : "运行中") + "].");
	}

	/**
	 * 界面设置
	 * 
	 * @param request
	 * @param response
	 */
	public void setPage(HttpServletRequest request, HttpServletResponse response) {

		Map<String, Object> config = new HashMap<>();
		config.put("page.title", Config.getChinese("page.title", ""));
		config.put("page.copyright", Config.getChinese("page.copyright", ""));
		config.put("page.browser.msg", Config.getChinese("page.browser.msg", ""));
		config.put("page.browser.url", Config.getChinese("page.browser.url", ""));
		config.put("page.randomcode", Config.get("page.randomcode"));
		config.put("page.taskpanel", Config.get("page.taskpanel", "true").equalsIgnoreCase("false") ? 0 : 1);
		config.put("page.theme", Config.get("page.theme", "smoothness"));
		config.put("page.theme.ext", Config.get("page.theme.ext"));
		config.put("page.theme.backgroud", Config.get("page.theme.backgroud", "0"));
		config.put("page.logo.url", Config.get("page.logo.url"));
		config.put("page.ico.url", Config.get("page.ico.url"));
		config.put("page.tips", Config.getChinese("page.tips", ""));

		request.setAttribute("config", config);
		Actions.includePage(request, response, Util.getPagePath(request, "page_setting.jsp"));
	}

	/**
	 * 界面设置提交
	 * 
	 * @param request
	 * @param response
	 */
	public void submitPageSetting(HttpServletRequest request, HttpServletResponse response) {

		Config.setChinese("page.title", RequestUtils.getStringValue(request, "page.title"));
		Config.setChinese("page.copyright", RequestUtils.getStringValue(request, "page.copyright"));
		Config.setChinese("page.browser.msg", RequestUtils.getStringValue(request, "page.browser.msg"));
		Config.set("page.browser.url", RequestUtils.getStringValue(request, "page.browser.url"));
		Config.set("page.randomcode", RequestUtils.getStringValue(request, "page.randomcode"));
		Config.set("page.taskpanel", RequestUtils.getIntegerValue(request, "page.taskpanel").intValue() == 1 ? "true" : "false");
		Config.set("page.theme", RequestUtils.getStringValue(request, "page.theme"));
		Config.set("page.theme.ext", RequestUtils.getStringValue(request, "page.theme.ext"));
		Config.set("page.theme.backgroud", RequestUtils.getStringValue(request, "page.theme.backgroud"));
		Config.set("page.logo.url", RequestUtils.getStringValue(request, "page.logo.url"));
		Config.set("page.ico.url", RequestUtils.getStringValue(request, "page.ico.url"));
		Config.setChinese("page.tips", RequestUtils.getStringValue(request, "page.tips"));

		Config.store("page");// 保存

		Actions.redirectInfoPage(request, response, "设置成功,请刷新页面.");
	}

	/**
	 * 邮箱设置
	 * 
	 * @param request
	 * @param response
	 */
	@SuppressWarnings("unchecked")
	public void setMail(HttpServletRequest request, HttpServletResponse response) {

		Map<String, Object> config = new HashMap<>();
		config.put("mail.sender.protocol", Config.get("mail.sender.protocol", "smtp"));
		config.put("mail.sender.host", Config.get("mail.sender.host", ""));
		config.put("mail.sender.port", Config.get("mail.sender.port", ""));
		config.put("mail.receiver.protocol", Config.get("mail.receiver.protocol", "pop"));
		config.put("mail.receiver.host", Config.get("mail.receiver.host", ""));
		config.put("mail.receiver.port", Config.get("mail.receiver.port", ""));

		config.put("mail.table.account", Config.get("mail.table.account"));
		config.put("mail.table.inbox", Config.get("mail.table.inbox"));
		config.put("mail.table.outbox", Config.get("mail.table.outbox"));

		config.put("mail.sender.account", Config.get("mail.sender.account"));
		config.put("mail.sender.password", Config.get("mail.sender.password"));

		List<TbTable> tables = ORMService.getInstance().queryAll(TbTable.class.getName());
		Set<TbTable> accountTables = new HashSet<>();
		Set<TbTable> inboxTables = new HashSet<>();
		Set<TbTable> outboxTables = new HashSet<>();
		for (TbTable table : tables) {
			if (ModelKeyUtils.checkModel(AccountModelKeys.class, table)) {
				accountTables.add(table);
			}
			if (ModelKeyUtils.checkModel(InboxModelKeys.class, table)) {
				inboxTables.add(table);
			}
			if (ModelKeyUtils.checkModel(OutboxModelKeys.class, table)) {
				outboxTables.add(table);
			}
		}

		request.setAttribute("accountTables", accountTables);
		request.setAttribute("inboxTables", inboxTables);
		request.setAttribute("outboxTables", outboxTables);
		config.put("mail.notify.flag", Config.get("mail.notify.flag", "true").equalsIgnoreCase("false") ? 0 : 1);
		config.put("mail.flow.subject.type", Config.get("mail.flow.subject.type", ""));
		config.put("mail.flow.subject.script", Config.getChinese("mail.flow.subject.script", ""));
		config.put("mail.flow.content.type", Config.get("mail.flow.content.type", ""));
		config.put("mail.flow.content.script", Config.getChinese("mail.flow.content.script", ""));

		config.put("mail.notify.user.setting", Config.get("mail.notify.user.setting", "true").equalsIgnoreCase("false") ? 0 : 1);

		request.setAttribute("config", config);
		Actions.includePage(request, response, Util.getPagePath(request, "mail_setting.jsp"));
	}

	/**
	 * 界面设置提交
	 * 
	 * @param request
	 * @param response
	 */
	public void submitMailSetting(HttpServletRequest request, HttpServletResponse response) {

		Config.set("mail.sender.protocol", RequestUtils.getStringValue(request, "mail.sender.protocol"));
		Config.set("mail.sender.host", RequestUtils.getStringValue(request, "mail.sender.host"));
		Config.set("mail.sender.port", RequestUtils.getStringValue(request, "mail.sender.port"));
		Config.set("mail.receiver.protocol", RequestUtils.getStringValue(request, "mail.receiver.protocol"));
		Config.set("mail.receiver.host", RequestUtils.getStringValue(request, "mail.receiver.host"));
		Config.set("mail.receiver.port", RequestUtils.getStringValue(request, "mail.receiver.port"));

		Config.set("mail.table.account", RequestUtils.getStringValue(request, "mail.table.account"));
		Config.set("mail.table.inbox", RequestUtils.getStringValue(request, "mail.table.inbox"));
		Config.set("mail.table.outbox", RequestUtils.getStringValue(request, "mail.table.outbox"));

		Config.set("mail.notify.flag", RequestUtils.getIntegerValue(request, "mail.notify.flag").intValue() == 1 ? "true" : "false");
		Config.set("mail.flow.subject.type", RequestUtils.getStringValue(request, "mail.flow.subject.type"));
		Config.setChinese("mail.flow.subject.script", RequestUtils.getStringValue(request, "mail.flow.subject.script"));
		Config.set("mail.flow.content.type", RequestUtils.getStringValue(request, "mail.flow.content.type"));
		Config.setChinese("mail.flow.content.script", RequestUtils.getStringValue(request, "mail.flow.content.script"));

		// 系统发送账号
		String password = RequestUtils.getStringValue(request, "mail.sender.password");
		String account = RequestUtils.getStringValue(request, "mail.sender.account");
		Config.set("mail.sender.account", account);
		if (StringUtils.isEmpty(account)) {
			Config.set("mail.sender.password", "");
		} else if (StringUtils.isNotEmpty(password)) {
			Config.set("mail.sender.password", password);
		}

		Config.set("mail.notify.user.setting", RequestUtils.getIntegerValue(request, "mail.notify.user.setting").intValue() == 1 ? "true" : "false");

		Config.store("mail");// 保存

		Actions.redirectInfoPage(request, response, "设置成功,请刷新页面.");
	}

	/**
	 * 文件空间管理
	 * 
	 * @param request
	 * @param response
	 */
	public void setFileSpace(HttpServletRequest request, HttpServletResponse response) {
		Actions.includePage(request, response, Util.getPagePath(request, "file_main.jsp"));
	}

	/**
	 * 当前文件树
	 * 
	 * @param request
	 * @param response
	 */
	@SuppressWarnings("unchecked")
	public void fileTree(HttpServletRequest request, HttpServletResponse response) {
		List<DownloadFileManager.TreeVO> trees = DownloadFileManager.getTree();
		final String cp = Actions.Util.getContextPath(request);
		List<HashMap<String, Object>> treesList = new ArrayList<>();
		for (DownloadFileManager.TreeVO treeVO : trees) {
			treesList.add(JsonMapper.defaultMapper().convert(treeVO, HashMap.class));
		}

		for (HashMap<String, Object> tree : treesList) {
			tree.put("icon", cp + "/css/icon/filetype/" + tree.get("icon") + ".png");
		}

		request.setAttribute("trees", JsonMapper.defaultMapper().toJson(treesList));
		Actions.includePage(request, response, Actions.Util.getPagePath(request, "file_tree.jsp"));
	}

	/**
	 * 文件详情
	 * 
	 * @param request
	 * @param response
	 */
	public void fileDetail(HttpServletRequest request, HttpServletResponse response) {
		String fileName = RequestUtils.getStringValue(request, "fileName");
		File file = DownloadFileManager.getFile(fileName);

		Map<String, String> vo = new HashMap<>();

		if (file != null) {
			vo.put("name", file.getName());
			vo.put("date", Formatter.formatDatetime(new Date(file.lastModified())));
			BigDecimal size = new BigDecimal(1024);
			vo.put("size", "" + new BigDecimal(file.length()).divide(size).divide(size).toString() + " MB");
		}

		request.setAttribute("vo", vo);
		Actions.includePage(request, response, Actions.Util.getPagePath(request, "file_detail.jsp"));
	}

	/**
	 * 文件上传处理结果
	 * 
	 * @param request
	 * @param response
	 * @throws javax.servlet.ServletException
	 * @throws IOException
	 */
	public void fileUpload(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {

		Part part = request.getPart("file");
		if (part.getSize() > 1024 * 1024 * 5) {
			throw new SystemRuntimeException(ExceptionType.BUSINESS, "系统拒绝超过5M的文件.");
		}
		// 文件名
		String fileName = RequestUtils.getStringValue(request, "fileName");
		if (fileName.lastIndexOf("/") > 0) {
			fileName = fileName.substring(fileName.lastIndexOf("/") + 1);
		}
		if (fileName.lastIndexOf("\\") > 0) {
			fileName = fileName.substring(fileName.lastIndexOf("\\") + 1);
		}
		File file = DownloadFileManager.saveFile(fileName, part.getInputStream());
		Actions.redirectInfoPage(request, response, "文件[" + file.getName() + "]上传成功.");
	}

	/**
	 * 文件删除
	 * 
	 * @param request
	 * @param response
	 */
	public void fileDelete(HttpServletRequest request, HttpServletResponse response) {
		String fileName = RequestUtils.getStringValue(request, "fileName");
		File file = DownloadFileManager.getFile(fileName);

		if (file.exists()) {
			try {
				if (file.isDirectory()) {
					FileUtils.deleteDirectory(file);
				} else {
					FileUtils.forceDelete(file);
				}
				Actions.redirectInfoPage(request, response, "删除成功.");
			} catch (IOException e) {
				throw new SystemRuntimeException(ExceptionType.BUSINESS, "删除失败:" + e.getMessage());
			}
		} else {
			throw new SystemRuntimeException(ExceptionType.BUSINESS, "删除失败,文件不存在.");
		}

	}

	/**
	 * 文件重命名
	 * 
	 * @param request
	 * @param response
	 */
	public void fileRename(HttpServletRequest request, HttpServletResponse response) {
		String fileName = RequestUtils.getStringValue(request, "fileName");
		String newFileName = RequestUtils.getStringValue(request, "newFileName");
		File file = DownloadFileManager.getFile(fileName);
		if (file != null) {
			if (DownloadFileManager.rename(file, newFileName)) {
				Actions.redirectInfoPage(request, response, "重命名成功.");
			} else {
				throw new SystemRuntimeException(ExceptionType.BUSINESS, "重命名失败.");
			}
		} else {
			throw new SystemRuntimeException(ExceptionType.BUSINESS, "重命名失败,原文件不存在.");
		}
	}

	/**
	 * 文件解压
	 * 
	 * @param request
	 * @param response
	 */
	public void fileUnzip(HttpServletRequest request, HttpServletResponse response) {
		String fileName = RequestUtils.getStringValue(request, "fileName");
		File file = DownloadFileManager.getFile(fileName);
		if (file != null) {
			try {
				DownloadFileManager.unzip(file);
				Actions.redirectInfoPage(request, response, "解压成功.");
			} catch (Exception e) {
				throw new SystemRuntimeException(ExceptionType.BUSINESS, "解压失败:" + e.getMessage());
			}
		} else {
			throw new SystemRuntimeException(ExceptionType.BUSINESS, "解压失败,文件不存在.");
		}
	}

	/**
	 * office配置
	 * 
	 * @param request
	 * @param response
	 */
	public void setOffice(HttpServletRequest request, HttpServletResponse response) {
		Map<String, Object> config = new HashMap<>();
		config.put("office.file.size", Config.getChinese("office.file.size", "10"));
		config.put("office.installation.path", Config.getChinese("office.installation.path", ""));
		config.put("office.port", Config.get("office.port", ""));
		config.put("office.flag", Config.get("office.flag", "true").equalsIgnoreCase("true") ? 1 : 0);
		config.put("office.prepare", Config.get("office.prepare", "true").equalsIgnoreCase("true") ? 1 : 0);

		config.put("office.upload.size", Config.get("office.upload.size", "100"));
		config.put("office.upload.pixel", Config.get("office.upload.pixel", ""));

		request.setAttribute("config", config);
		Actions.includePage(request, response, Util.getPagePath(request, "office_setting.jsp"));
	}

	/**
	 * 保存office设置
	 * 
	 * @param request
	 * @param response
	 */
	public void submitOfficeSetting(HttpServletRequest request, HttpServletResponse response) {
		Config.set("office.flag", RequestUtils.getIntegerValue(request, "office.flag").intValue() == 1 ? "true" : "false");
		Config.set("office.prepare", RequestUtils.getIntegerValue(request, "office.prepare").intValue() == 1 ? "true" : "false");
		Config.set("office.file.size", RequestUtils.getStringValue(request, "office.file.size"));
		Config.set("office.installation.path", RequestUtils.getStringValue(request, "office.installation.path"));
		Config.set("office.port", RequestUtils.getStringValue(request, "office.port"));

		Config.set("office.upload.size", RequestUtils.getStringValue(request, "office.upload.size"));
		Config.set("office.upload.pixel", RequestUtils.getStringValue(request, "office.upload.pixel"));

		Config.store("office");// 保存
		Actions.redirectInfoPage(request, response, "设置成功..");
	}

	/**
	 * 微信设置
	 * 
	 * @param request
	 * @param response
	 */
	@SuppressWarnings("unchecked")
	public void wxNetSetting(HttpServletRequest request, HttpServletResponse response) {
		Map<String, Object> config = new HashMap<>();
		config.put("wx.net.domain", Config.get("wx.net.domain"));
		config.put("wx.net.https", Config.get("wx.net.https"));

		config.put("wx.web.login.qrcode", Config.get("wx.web.login.qrcode", "false").equalsIgnoreCase("true") ? 1 : 0);
		config.put("wx.web.mp.appIds", Config.get("wx.web.mp.appIds"));

		config.put("wx.open.flag", Config.get("wx.open.flag", "false").equalsIgnoreCase("true") ? 1 : 0);
		config.put("wx.open.appId", Config.get("wx.open.appId"));
		config.put("wx.open.appSecret", Config.get("wx.open.appSecret"));
		config.put("wx.open.table", Config.get("wx.open.table"));

		List<TbTable> tables = ORMService.getInstance().queryAll(TbTable.class.getName());
		Set<TbTable> openTables = new HashSet<>();
		for (TbTable table : tables) {
			if (ModelKeyUtils.checkModel(OpenVisitorModelKeys.class, table)) {
				openTables.add(table);
			}
		}
		request.setAttribute("openTables", openTables);

		config.put("wx.qy.corpId", Config.get("wx.qy.corpId", ""));
		config.put("wx.qy.corpSecret", Config.get("wx.qy.corpSecret", ""));
		config.put("wx.qy.flag", Config.get("wx.qy.flag", "true").equalsIgnoreCase("true") ? 1 : 0);
		config.put("wx.qy.default", Config.get("wx.qy.default", "0"));
		config.put("wx.qy.contactmode", Config.get("wx.qy.contactmode"));

		config.put("wx.qy.pay.flag", Config.get("wx.qy.pay.flag", "true").equalsIgnoreCase("true") ? 1 : 0);
		config.put("wx.qy.pay.mchId", Config.get("wx.qy.pay.mchId", ""));
		config.put("wx.qy.pay.key", Config.get("wx.qy.pay.key", ""));
		config.put("wx.qy.pay.certPath", Config.get("wx.qy.pay.certPath", ""));
		config.put("wx.qy.pay.certPassword", Config.get("wx.qy.pay.certPassword", ""));

		request.setAttribute("config", config);
		Actions.includePage(request, response, Util.getPagePath(request, "wx_config.jsp"));
	}

	/**
	 * 保存微信设置
	 * 
	 * @param request
	 * @param response
	 */
	public void saveNetConfig(HttpServletRequest request, HttpServletResponse response) {
		Config.set("wx.net.domain", RequestUtils.getStringValue(request, "wx.net.domain"));
		Config.set("wx.net.https", RequestUtils.getStringValue(request, "wx.net.https"));

		Config.set("wx.web.login.qrcode", RequestUtils.getIntegerValue(request, "wx.web.login.qrcode").intValue() == 1 ? "true" : "false");
		Config.set("wx.web.mp.appIds", RequestUtils.getStringValue(request, "wx.web.mp.appIds"));

		Config.set("wx.open.appId", RequestUtils.getStringValue(request, "wx.open.appId"));
		Config.set("wx.open.appSecret", RequestUtils.getStringValue(request, "wx.open.appSecret"));
		Config.set("wx.open.flag", RequestUtils.getIntegerValue(request, "wx.open.flag").intValue() == 1 ? "true" : "false");
		Config.set("wx.open.table", RequestUtils.getStringValue(request, "wx.open.table"));

		// 企业号参数保存
		String corpId = RequestUtils.getStringValue(request, "wx.qy.corpId");
		String corpSecret = RequestUtils.getStringValue(request, "wx.qy.corpSecret");
		Integer agentId = RequestUtils.getIntegerValue(request, "wx.qy.default");
		Config.set("wx.qy.corpId", corpId);
		Config.set("wx.qy.corpSecret", corpSecret);
		Config.set("wx.qy.default", agentId.toString());
		Config.set("wx.qy.flag", RequestUtils.getIntegerValue(request, "wx.qy.flag").intValue() == 1 ? "true" : "false");
		Config.set("wx.qy.contactmode", RequestUtils.getStringValue(request, "wx.qy.contactmode"));

		Config.set("wx.qy.pay.flag", RequestUtils.getIntegerValue(request, "wx.qy.pay.flag").intValue() == 1 ? "true" : "false");
		Config.set("wx.qy.pay.mchId", RequestUtils.getStringValue(request, "wx.qy.pay.mchId"));
		Config.set("wx.qy.pay.key", RequestUtils.getStringValue(request, "wx.qy.pay.key"));
		Config.set("wx.qy.pay.certPath", RequestUtils.getStringValue(request, "wx.qy.pay.certPath"));
		Config.set("wx.qy.pay.certPassword", RequestUtils.getStringValue(request, "wx.qy.pay.certPassword"));

		Config.store("wx");// 保存
		Actions.redirectInfoPage(request, response, "网络设置已保存.");
	}

}
