/*
 * $HeadURL: $
 * $Id: $
 * Copyright (c) 2013 by Riversoft System, all rights reserved.
 */
package com.riversoft.module.widget;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

import com.riversoft.platform.SessionManager;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.riversoft.core.Config;
import com.riversoft.core.db.po.Tree;
import com.riversoft.core.exception.ExceptionType;
import com.riversoft.core.exception.SystemRuntimeException;
import com.riversoft.core.web.Actions;
import com.riversoft.core.web.Actions.Keys;
import com.riversoft.core.web.Actions.Util;
import com.riversoft.core.web.RequestUtils;
import com.riversoft.core.web.annotation.ActionAccess;
import com.riversoft.core.web.annotation.ActionMode;
import com.riversoft.core.web.annotation.ActionMode.Mode;
import com.riversoft.platform.SessionManager.SessionAttributeKey;
import com.riversoft.platform.office.ConverterHelper;
import com.riversoft.platform.po.UsUser;
import com.riversoft.platform.script.function.UserHelper;
import com.riversoft.platform.web.FileManager;
import com.riversoft.platform.web.FileManager.DevFile;
import com.riversoft.platform.web.FileManager.UploadFile;
import com.riversoft.platform.web.WebLogManager;
import com.riversoft.util.Formatter;
import com.riversoft.util.jackson.JsonMapper;
import com.riversoft.weixin.mp.base.AppSetting;
import com.riversoft.weixin.qy.media.Medias;
import com.riversoft.wx.mp.service.MpAppService;
import com.riversoft.wx.qy.AgentHelper;
import com.riversoft.wx.qy.QyMediaHelper;

/**
 * 系统文件管理组件
 * 
 * @author Woden
 * 
 */
public class FileAction {

	/**
	 * 树菜单节点
	 * 
	 * @author Woden
	 * 
	 */
	public static class TreeVO implements Tree {

		private static final long serialVersionUID = 2714115058761334385L;
		private String id;
		private String parentId;
		private String name;
		private Integer sort;
		private Long size;// 单位:b

		// 扩展属性
		private boolean fileFlag;
		private String icon;

		/**
		 * 构造函数
		 * 
		 * @param id
		 * @param parentId
		 * @param name
		 * @param size
		 * @param sort
		 * @param fileFlag
		 */
		public TreeVO(String id, String parentId, String name, Long size, Integer sort, boolean fileFlag) {
			this.id = id;
			this.parentId = parentId;
			this.name = name;
			this.sort = sort;
			this.fileFlag = fileFlag;
			this.size = size;

			// 设置图标
			if (id.lastIndexOf(".") > 0) {
				String pixel = id.substring(id.lastIndexOf(".") + 1).toLowerCase();
				icon = pixel;
			}
		}

		/**
		 * @return the serialversionuid
		 */
		public static long getSerialversionuid() {
			return serialVersionUID;
		}

		/**
		 * @return the size
		 */
		public Long getSize() {
			return size;
		}

		/**
		 * @return the id
		 */
		public String getId() {
			return id;
		}

		/**
		 * @return the parentId
		 */
		public String getParentId() {
			return parentId;
		}

		/**
		 * @return the name
		 */
		public String getName() {
			return name;
		}

		/**
		 * @return the sort
		 */
		public Integer getSort() {
			return sort;
		}

		/**
		 * @return the fileFlag
		 */
		public boolean isFileFlag() {
			return fileFlag;
		}

		public String getIcon() {
			return icon;
		}

		/**
		 * 文件夹不需要单选/多选框
		 * 
		 * @return
		 */
		public boolean getNocheck() {
			return !fileFlag;
		}
	}

	/**
	 * 生成树分类类型
	 * 
	 * @author Woden
	 * 
	 */
	public static enum TreeType {
		BY_DATE,
		BY_FILE_TYPE,
		BY_FILE_SIZE,
		BY_CLIENT
	}

	/**
	 * 获取树
	 * 
	 * @return
	 */
	public static List<TreeVO> getTree(TreeType type) {
		switch (type) {
		case BY_DATE:
			return getTreeByDate();
		case BY_FILE_TYPE:
			return getTreeByFile();
		case BY_FILE_SIZE:
			return getTreeBySize();
		case BY_CLIENT:
			return getTreeByClient();
		default:
			throw new SystemRuntimeException(ExceptionType.CODING, "方法不支持.");
		}
	}

	/**
	 * 根据日期分类返回文件树
	 * 
	 * @return
	 */
	private static List<TreeVO> getTreeByDate() {
		List<TreeVO> list = new ArrayList<>();
		Integer sort = 0;
		// 设置根节点
		File[] files = FileManager.listCurrentUserFiles();
		Set<String> keys = new HashSet<>();
		if (files != null && files.length > 0) {
			for (File file : files) {
				Date date = new Date(file.lastModified());
				String parentId = Formatter.formatDatetime(date, "yyyyMMdd");
				if (!keys.contains("_" + parentId)) {
					keys.add("_" + parentId);
					list.add(new TreeVO("_" + parentId, null, parentId, 0L, sort++, false));
				}
				list.add(new TreeVO(file.getName(), "_" + parentId, file.getName(), file.length(), sort++, true));
			}
		}

		return list;
	}

	/**
	 * 根据文件类型分类返回文件树
	 * 
	 * @return
	 */
	private static List<TreeVO> getTreeByFile() {
		List<TreeVO> list = new ArrayList<>();
		Integer sort = 0;
		// 设置根节点
		File[] files = FileManager.listCurrentUserFiles();
		Set<String> keys = new HashSet<>();
		if (files != null && files.length > 0) {
			for (File file : files) {
				int pixel = file.getName().lastIndexOf(".");
				if (pixel < 1) {
					continue;
				}
				String parentId = file.getName().substring(pixel + 1).toLowerCase();
				if (!keys.contains("_" + parentId)) {
					keys.add("_" + parentId);
					list.add(new TreeVO("_" + parentId, null, parentId, 0L, sort++, false));
				}
				list.add(new TreeVO(file.getName(), "_" + parentId, file.getName(), file.length(), sort++, true));
			}
		}

		return list;
	}

	/**
	 * 根据文件尺寸渲染树
	 * 
	 * @return
	 */
	private static List<TreeVO> getTreeBySize() {
		List<TreeVO> list = new ArrayList<>();
		// 设置根节点
		File[] files = FileManager.listCurrentUserFiles();
		Set<String> keys = new HashSet<>();
		if (files != null && files.length > 0) {
			for (File file : files) {
				Long length = file.length();
				String parentId, parentName;
				int sort;
				if (length > 50 * 1024 * 1024) {// 大于50M
					parentId = "b1";
					parentName = "超大附件(50M以上)";
					sort = 3;
				} else if (length > 10 * 1024 * 1024) {// 大于10M
					parentId = "b2";
					parentName = "大附件(10M-50M)";
					sort = 2;
				} else {
					parentId = "b3";
					parentName = "普通尺寸(10M以下)";
					sort = 1;
				}

				if (!keys.contains("_" + parentId)) {
					keys.add("_" + parentId);
					list.add(new TreeVO("_" + parentId, null, parentName, 0L, sort, false));
				}
				list.add(new TreeVO(file.getName(), "_" + parentId, file.getName(), file.length(), length.intValue(), true));
			}
		}
		Collections.sort(list, new Comparator<TreeVO>() {
			@Override
			public int compare(TreeVO o1, TreeVO o2) {
				if (o1.getSort() < o2.getSort()) {
					return 1;
				} else if (o1.getSort() > o2.getSort()) {
					return -1;
				}
				return 0;
			}
		});

		return list;
	}

	/**
	 * 根据客户端分类
	 * 
	 * @return
	 */
	private static List<TreeVO> getTreeByClient() {
		List<TreeVO> list = new ArrayList<>();
		Integer sort = 0;
		// 设置根节点
		File[] files = FileManager.listCurrentUserFiles();
		Set<String> keys = new HashSet<>();
		if (files != null && files.length > 0) {
			for (File file : files) {
				String parentId = "来自网页";
				if (file.getName().contains("WX_")) {
					parentId = "来自微信";
				}
				if (!keys.contains("_" + parentId)) {
					keys.add("_" + parentId);
					list.add(new TreeVO("_" + parentId, null, parentId, 0L, sort++, false));
				}
				list.add(new TreeVO(file.getName(), "_" + parentId, file.getName(), file.length(), sort++, true));
			}
		}

		return list;

	}

	/**
	 * office文件异步装换
	 * 
	 * @author woden
	 * 
	 */
	private static class PrepareOfficeConverter {

		private LinkedBlockingQueue<OfficeFile> queue = new LinkedBlockingQueue<>();
		private final static PrepareOfficeConverter instance = new PrepareOfficeConverter();

		private static class OfficeFile {
			private String fileName;
			private File file;

			OfficeFile(String fileName, File file) {
				this.file = file;
				this.fileName = fileName;
			}
		}

		private static class ConverterTask implements Runnable {
			private LinkedBlockingQueue<OfficeFile> queue;

			public ConverterTask(LinkedBlockingQueue<OfficeFile> queue) {
				this.queue = queue;
			}

			public void run() {
				while (true) {
					try {
						TimeUnit.MILLISECONDS.sleep(100);// 100毫秒等待
						OfficeFile of = queue.take();
						if (logger.isDebugEnabled()) {
							logger.debug("线程[{}]开始转换文件[{}].", Thread.currentThread().getName(), of.fileName);
						}
						convertOfficeFile(of.fileName, of.file);
						if (logger.isDebugEnabled()) {
							logger.debug("线程[{}]转换文件[{}]成功.", Thread.currentThread().getName(), of.fileName);
						}
					} catch (InterruptedException ignore) {
						if (logger.isDebugEnabled()) {
							logger.debug("异步线程中断.", ignore);
						}
						return;
					} catch (Throwable ignore) {
						// do nothing
						if (logger.isDebugEnabled()) {
							logger.debug("异步线程[{}]转换过程出错.", Thread.currentThread().getName(), ignore);
						}
					}
				}
			}
		}

		/**
		 * 启动
		 */
		private PrepareOfficeConverter() {
			ExecutorService executor = Executors.newFixedThreadPool(10);

			for (int i = 0; i < 10; i++) {// 10条线程
				executor.execute(new ConverterTask(this.queue));
			}
		}

		/**
		 * 添加一个文件
		 * 
		 * @param fileName
		 * @param file
		 */
		static void addFile(String fileName, File file) {
			try {
				instance.queue.put(new OfficeFile(fileName, file));
			} catch (InterruptedException ignore) {
				// do nothing
				if (logger.isDebugEnabled()) {
					logger.debug("异步线程中断.", ignore);
				}
			}
		}

	}

	/**
	 * 对office文件预处理(异步)
	 * 
	 * @param fileName
	 * @param file
	 */
	private static void prepareOfficeFile(String fileName, File file) {
		if (!"true".equalsIgnoreCase(Config.get("office.prepare"))) {
			return;
		}

		String pixel = fileName.substring(fileName.lastIndexOf(".") + 1);
		List<String> allowPixels = Arrays.asList("pdf,doc,docx,xls,xlsx,ppt,pptx".split(","));
		if (!allowPixels.contains(pixel.toLowerCase())) {
			return;
		}

		// 增加一个转换文件
		PrepareOfficeConverter.addFile(fileName, file);
	}

	/**
	 * 对office文件进行装换(同步)
	 * 
	 * @param fileName
	 * @param file
	 */
	private static File convertOfficeFile(String fileName, File file) {
		String pixel = fileName.substring(fileName.lastIndexOf(".") + 1);
		List<String> allowPixels = Arrays.asList("pdf,doc,docx,xls,xlsx,ppt,pptx".split(","));
		if (!allowPixels.contains(pixel.toLowerCase())) {
			throw new SystemRuntimeException(ExceptionType.PDF, "后缀名[" + pixel + "]不支持转换.");
		}

		// office文件缓存
		try (InputStream in = new FileInputStream(file);) {
			String md5 = DigestUtils.md5Hex(IOUtils.toByteArray(in));// 源文件的md5码
			File pdfDir = new File(file.getParentFile(), "pdf");
			if (!pdfDir.exists()) {
				pdfDir.mkdirs();
			}
			File pdfFile = new File(pdfDir, "_" + md5 + ".pdf");
			if (pdfFile.exists() && pdfFile.length() < 1) {// 无长度文件,删掉
				pdfFile.delete();
			}

			if (!pdfFile.exists()) {// 不存在则创建
				pdfFile.createNewFile();
				if (logger.isDebugEnabled()) {
					logger.debug("开始转换office文件:[" + file.getName() + "]");
				}
				if (!ConverterHelper.convert(file, pdfFile)) {
					logger.error("转换office文档失败.[" + pixel + "]->[pdf]");
					throw new SystemRuntimeException(ExceptionType.PDF, "无法找到Office文档转换服务,无法输出该类型[" + pixel + "]文件.");
				}
			}
			return pdfFile;
		} catch (IOException e) {
			throw new SystemRuntimeException(ExceptionType.PDF, "无法转换类型[" + pixel + "]文件.", e);
		}
	}

	/**
	 * Logger for this class
	 */
	static final Logger logger = LoggerFactory.getLogger(FileAction.class);

	/**
	 * 控件内页
	 * 
	 * @param request
	 * @param response
	 */
	@ActionAccess(login = false)
	public void index(HttpServletRequest request, HttpServletResponse response) {
		Map<String, Object> params = RequestUtils.getJsonValue(request, Keys.PARAMS.toString());
		long maxSize = -1;
		if (params != null && params.containsKey("max")) {
			try {
				maxSize = Long.parseLong(params.get("max").toString());
				maxSize = maxSize * 1024 * 1024;
			} catch (Exception ignore) {
				// do nothing
			}
		}
		request.setAttribute("maxSize", maxSize);
		String agent = Config.get("wx.qy.default", "");
		request.setAttribute("agent", agent);
		request.setAttribute("login", SessionManager.getUser() != null);
		Actions.includePage(request, response, Actions.Util.getPagePath(request, "file_widget.jsp"));
	}

	/**
	 * 文件选择
	 * 
	 * @param request
	 * @param response
	 */
	public void selectFile(HttpServletRequest request, HttpServletResponse response) {
		String checkType = RequestUtils.getStringValue(request, "checkType");
		if (StringUtils.isEmpty(checkType)) {
			checkType = "radio";// 默认单选
		}
		request.setAttribute("checkType", checkType);
		Actions.includePage(request, response, Actions.Util.getPagePath(request, "file_select.jsp"));
	}

	/**
	 * 当前文件树
	 * 
	 * @param request
	 * @param response
	 */
	@SuppressWarnings("unchecked")
	public void currentFileTree(HttpServletRequest request, HttpServletResponse response) {
		String type = RequestUtils.getStringValue(request, "type");
		TreeType types;
		try {
			types = TreeType.valueOf(type);
		} catch (Throwable e) {
			types = TreeType.BY_DATE;
		}

		List<TreeVO> trees = getTree(types);
		final String cp = Actions.Util.getContextPath(request);
		List<HashMap<String, Object>> treesList = new ArrayList<>();
		for (TreeVO treeVO : trees) {
			treesList.add(JsonMapper.defaultMapper().convert(treeVO, HashMap.class));
		}

		for (HashMap<String, Object> tree : treesList) {
			if (tree.get("icon") != null && !"null".equalsIgnoreCase(tree.get("icon").toString())) {
				tree.put("icon", cp + "/css/icon/filetype/" + tree.get("icon") + ".png");
			} else {
				tree.put("icon", null);
			}
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
		File file = FileManager.findCurrentUserFile(fileName);
		Map<String, String> vo = new HashMap<>();
		if (file != null) {
			vo.put("name", file.getName());
			vo.put("date", Formatter.formatDatetime(new Date(file.lastModified())));
			BigDecimal size = new BigDecimal(1024);
			vo.put("size", "" + Formatter.formatNumber(new BigDecimal(file.length()).divide(size), "#,##0.##") + " kb");
		}

		request.setAttribute("vo", vo);
		Actions.includePage(request, response, Actions.Util.getPagePath(request, "file_detail.jsp"));
	}

	/**
	 * 文件删除
	 * 
	 * @param request
	 * @param response
	 */
	public void delete(HttpServletRequest request, HttpServletResponse response) {
		String fileName = RequestUtils.getStringValue(request, "fileName");
		File file = FileManager.findCurrentUserFile(fileName);

		if (file.exists()) {
			file.delete();
		}

		Actions.redirectInfoPage(request, response, "删除成功.");
	}

	/**
	 * 永久文件下载
	 * 
	 * @param request
	 * @param response
	 * @throws IOException
	 * @throws ServletException
	 */
	@ActionAccess(login = false)
	@ActionMode(Mode.FIT)
	public void download(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		String name = RequestUtils.getStringValue(request, "name");
		String fileName = RequestUtils.getStringValue(request, "fileName");
		String type = RequestUtils.getStringValue(request, "type");

		File file = FileManager.getFile(type, name);
		if (file == null) {
			throw new SystemRuntimeException(ExceptionType.BUSINESS, "文件不存在.");
		}

		if (StringUtils.isEmpty(fileName)) {
			fileName = file.getName();
		}

		// 微信端展示遮罩
		if (Actions.Util.fromWx(request)) {
			String pixel = StringUtils.substring(fileName, StringUtils.lastIndexOf(fileName, ".") + 1);
			pixel = StringUtils.lowerCase(pixel);
			if (!ArrayUtils.contains("jpg;jpeg;png;gif;ppt;pptx;doc;docx;xls;xlsx;pdf;txt;xml;html;htm".split(";"), pixel)) {// 不能下载的类型
				Actions.includePage(request, response, Actions.Util.getPagePath(request, "file_wx_mask.jsp"));
				return;
			}
		}

		if (StringUtils.equalsIgnoreCase("true", RequestUtils.getStringValue(request, "download"))) {
			Actions.download(request, response, fileName, new FileInputStream(file));
		} else {
			Actions.showFile(request, response, fileName, new FileInputStream(file));
		}
	}

	/**
	 * 把文件发送到企业号默认接收消息的agent
	 * 
	 * @param request
	 * @param response
	 * @throws IOException
	 * @throws ServletException
	 */
	@Deprecated
	public void send2wx(HttpServletRequest request, HttpServletResponse response) throws IOException {
		String name = RequestUtils.getStringValue(request, "name");
		String type = RequestUtils.getStringValue(request, "type");
		String fileName = RequestUtils.getStringValue(request, "fileName");

		File file = FileManager.getFile(type, name);
		if (file == null) {
			throw new SystemRuntimeException(ExceptionType.BUSINESS, "文件不存在.");
		}

		DevFile devFile = new DevFile(fileName, FileUtils.readFileToByteArray(file));
		final List<UploadFile> list = new ArrayList<>();
		list.add(devFile);

		UsUser user = UserHelper.getUser();
		if (user.getWxEnable() != 1 || user.getWxStatus() != 1) {
			throw new SystemRuntimeException(ExceptionType.BUSINESS, "您不是微信企业号用户.");
		}

		final String agent = Config.get("wx.qy.default");
		final String uid = user.getUid();
		Map<String, Object> result = new HashMap<>();
		if (StringUtils.isNotEmpty(agent)) {
			final AgentHelper agentHelper = new AgentHelper("", "", Integer.valueOf(agent));

			Thread t = new Thread(new Runnable() {
				@Override
				public void run() {
					String mediaId = QyMediaHelper.upload(FileManager.toBytes(null, list));// 使用临时素材
					Map<String, Object> fileMessage = new HashMap<>();
					fileMessage.put("file", mediaId);
					fileMessage.put("user", uid);
					agentHelper.file(fileMessage);
				}
			});
			t.start();

			result.put("error", 0);
			result.put("msg", "正在下发文件,请稍后在微信端查收.");
			Actions.showJson(request, response, result);
		} else {
			throw new SystemRuntimeException(ExceptionType.BUSINESS, "当前系统企业号中没有配置默认接收消息的应用.");
		}
	}

	/**
	 * office文件获得
	 * 
	 * @param request
	 * @param response
	 * @throws IOException
	 * @throws ServletException
	 */
	public void downloadOffice(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		String name = RequestUtils.getStringValue(request, "name");
		String fileName = RequestUtils.getStringValue(request, "fileName");
		String type = RequestUtils.getStringValue(request, "type");

		File file = FileManager.getFile(type, name);
		if (file == null) {
			throw new SystemRuntimeException(ExceptionType.BUSINESS, "文件不存在.");
		}

		if (StringUtils.isEmpty(fileName)) {
			fileName = file.getName();
		}
		WebLogManager.log("开始加载(1/3).");
		String pixel = fileName.substring(fileName.lastIndexOf(".") + 1);
		if (!"pdf".equalsIgnoreCase(pixel)) {// 非PDF需要转换
			WebLogManager.log("正在转换格式(2/3).");
			File pdfFile = convertOfficeFile(fileName, file);
			WebLogManager.log("正在装载到阅读器(3/3).");
			Actions.showFile(request, response, fileName.substring(0, fileName.lastIndexOf(".")) + ".pdf", new FileInputStream(pdfFile));
		} else {// pdf直接输出
			WebLogManager.log("正在装载到阅读器(3/3).");
			Actions.showFile(request, response, fileName, new FileInputStream(file));
		}
	}

	/**
	 * office文件预览
	 * 
	 * @param request
	 * @param response
	 * @throws IOException
	 * @throws ServletException
	 */
	public void office(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		String name = RequestUtils.getStringValue(request, "name");
		String fileName = RequestUtils.getStringValue(request, "fileName");
		String type = RequestUtils.getStringValue(request, "type");

		File file = FileManager.getFile(type, name);
		if (file == null) {
			throw new SystemRuntimeException(ExceptionType.BUSINESS, "文件不存在.");
		}

		if (StringUtils.isEmpty(fileName)) {
			fileName = file.getName();
		}

		String pixel = fileName.substring(fileName.lastIndexOf(".") + 1);
		List<String> allowPixels = Arrays.asList("pdf,doc,docx,xls,xlsx,ppt,pptx".split(","));
		if (!allowPixels.contains(pixel.toLowerCase())) {
			throw new SystemRuntimeException(ExceptionType.PDF, "后缀名[" + pixel + "]不支持预览.");
		}

		Actions.includePage(request, response, Util.getPagePath(request, "file_office.jsp"));
	}

	/**
	 * 图片轮询展示
	 * 
	 * @param request
	 * @param response
	 */
	public void showimg(HttpServletRequest request, HttpServletResponse response) {
		List<UploadFile> list = FileManager.getUploadFiles(request, "files");

		request.setAttribute("list", list);
		Actions.includePage(request, response, Util.getPagePath(request, "file_img.jsp"));
	}

	/**
	 * 文件上传
	 * 
	 * @param request
	 * @param response
	 */
	@ActionAccess(login = false)
	public void uploadPage(HttpServletRequest request, HttpServletResponse response) {
		String fileSize = Config.get("office.upload.size");
		String filePixel = Config.get("office.upload.pixel");

		request.setAttribute("fileSize", fileSize);
		request.setAttribute("filePixel", filePixel);

		Actions.includePage(request, response, Util.getPagePath(request, "file_upload.jsp"));
	}

	/**
	 * 文件上传处理结果
	 * 
	 * @param request
	 * @param response
	 * @throws ServletException
	 * @throws IOException
	 */
	@ActionAccess(login = false)
	public void upload(HttpServletRequest request, HttpServletResponse response) {
		Map<String, Object> result = new HashMap<>();
		try {
			Part part = request.getPart("file");
			String fileName = RequestUtils.getStringValue(request, "name").replaceAll("[,//']", "");
			Integer chunks = RequestUtils.getIntegerValue(request, "chunks");
			Integer chunk = RequestUtils.getIntegerValue(request, "chunk");
			File dir = FileManager.getCurrentUserTempSpace();
			File tmpFile = new File(dir, fileName + ".part");

			if (chunk == 0 && tmpFile.exists()) {// 以前存在
				tmpFile.delete();
			}
			if (!tmpFile.exists()) {
				tmpFile.createNewFile();
			}

			try (FileOutputStream fos = new FileOutputStream(tmpFile, true); BufferedInputStream bis = new BufferedInputStream(part.getInputStream());) {
				int BUFFER_SIZE = 1024;
				byte[] buf = new byte[BUFFER_SIZE];
				int size = 0;
				while ((size = bis.read(buf)) != -1) {
					fos.write(buf, 0, size);
				}
				result.put("reset", false);// 传成功了,就算出问题也不需要重置
			} catch (IOException ex) {
				result.put("reset", true);// 传一半出问题,重置
			}

			if (chunk == chunks - 1) {// 最后一个
				File file = FileManager.moveToUserFileSpace(tmpFile, fileName);
				result.put("fileName", file.getName());
			}

			if (chunk == 60) {
				// throw new ServletException();
			}

			result.put("code", 0);
			result.put("info", "上传成功.");
			Actions.showJson(request, response, result);

		} catch (IOException | ServletException e) {
			logger.debug("出错提示", e);
			result.put("code", -1);
			result.put("info", "上传文件失败成功.");
			Actions.showJson(request, response, result);
		}
	}

	/**
	 * 整文件上传
	 * 
	 * @param request
	 * @param response
	 */
	public void uploadFile(HttpServletRequest request, HttpServletResponse response) {
		try {
			Part part = request.getPart("file");
			String mode = RequestUtils.getStringValue(request, "mode");
			String fileName = RequestUtils.getStringValue(request, "fileName");
			File dir = FileManager.getCurrentUserTempSpace();
			File tmpFile = new File(dir, fileName + ".part");

			if (tmpFile.exists()) {// 以前存在
				tmpFile.delete();
			}

			if (!tmpFile.exists()) {
				tmpFile.createNewFile();
			}

			try (FileOutputStream fos = new FileOutputStream(tmpFile, true); BufferedInputStream bis = new BufferedInputStream(part.getInputStream());) {
				int BUFFER_SIZE = 1024;
				byte[] buf = new byte[BUFFER_SIZE];
				int size = 0;
				while ((size = bis.read(buf)) != -1) {
					fos.write(buf, 0, size);
				}
			} catch (IOException ex) {
				throw new SystemRuntimeException(ex);
			}

			File file = FileManager.moveToUserFileSpace(tmpFile, fileName);

			Map<String, Object> result = new HashMap<>();
			result.put("name", file.getName());
			result.put("type", "temp");
			result.put("size", file.length());
			result.put("mode", mode);

			Actions.showJson(request, response, result);
		} catch (IOException | ServletException e) {
			throw new SystemRuntimeException(e);
		}
	}

	/**
	 * 从微信上传文件
	 * 
	 * @param request
	 * @param response
	 */
	@ActionMode(Mode.H5)
	public void uploadFromWX(HttpServletRequest request, HttpServletResponse response) {
		String mediaId = RequestUtils.getStringValue(request, "mediaId");
		String mode = RequestUtils.getStringValue(request, "mode");
		String wxType = (String) request.getSession().getAttribute(SessionAttributeKey.WX_TYPE.name());
		String wxKey = (String) request.getSession().getAttribute(SessionAttributeKey.WX_KEY.name());

		File file = null;
		if (StringUtils.equalsIgnoreCase("agent", wxType)) {// 企业号
			file = Medias.defaultMedias().download(mediaId);
		} else if (StringUtils.equalsIgnoreCase("mp", wxType)) {// 公众号
			AppSetting appSetting = MpAppService.getInstance().getAppSettingByPK(wxKey);
			file = com.riversoft.weixin.mp.media.Medias.with(appSetting).download(mediaId);
		}

		// {"name":"CRM
		// Frame团队说明.xlsx","type":"temp","size":0.013716697692871094,"mode":"disk"}
		Map<String, Object> result = new HashMap<>();
		try {
			file = FileManager.moveToUserFileSpace(file,
					Formatter.formatDatetime(new Date(), "yyyyMMddHHmmssSSS") + new Random().nextInt(10) + file.getName().substring(file.getName().lastIndexOf(".")));
			result.put("name", file.getName());
			result.put("type", "temp");
			result.put("size", file.length());
			result.put("mode", mode);
		} catch (IOException e) {
			logger.error("文件拷贝不成功", e);
		}

		Actions.showJson(request, response, result);
	}
}
