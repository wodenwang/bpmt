/*
 * $HeadURL: $
 * $Id: $
 * Copyright (c) 2013 by Riversoft System, all rights reserved.
 */
package com.riversoft.platform.web;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.riversoft.core.Config;
import com.riversoft.core.IDGenerator;
import com.riversoft.core.context.RequestContext;
import com.riversoft.core.exception.SystemRuntimeException;
import com.riversoft.core.web.RequestUtils;
import com.riversoft.platform.Platform;
import com.riversoft.platform.SessionManager;
import com.riversoft.util.Formatter;
import com.riversoft.util.jackson.JsonMapper;

/**
 * 文件管理器
 * 
 * @author Woden
 * 
 */
public class FileManager {
	/**
	 * Logger for this class
	 */
	private static final Logger logger = LoggerFactory.getLogger(FileManager.class);

	private static String TEMP_FILE_TYPE = "temp";// 未持久化的文件数据
	private static String DB_FILE_TYPE = "db";// 数据库文件临时中转(BLOB)
	private static String SYS_FILE_TYPE = "sys";// 系统固定保存数据

	public static String DISK_MODE = "disk";// 保存在磁盘
	public static String DB_MODE = "db";// 保存在库表

	/**
	 * 计算字节的MD5特征码
	 * 
	 * @param bytes
	 * @return
	 */
	private static String getMd5ByFile(byte[] bytes) {
		try {
			MessageDigest md5 = MessageDigest.getInstance("MD5");
			md5.update(bytes);
			BigInteger bi = new BigInteger(1, md5.digest());
			return bi.toString(16);
		} catch (Exception e) {
			logger.error("计算字节流特征码出错.", e);
			return IDGenerator.uuid();
		}
	}

	/**
	 * bytes转换成int
	 * 
	 * @param bytes
	 * @return
	 */
	private static int bytes2int(byte[] bytes) {
		int num = bytes[0] & 0xFF;
		num |= ((bytes[1] << 8) & 0xFF00);
		num |= ((bytes[2] << 16) & 0xFF0000);
		num |= ((bytes[3] << 24) & 0xFF000000);
		return num;
	}

	/**
	 * int转换成bytes
	 * 
	 * @param i
	 * @return
	 */
	private static byte[] int2bytes(int i) {
		byte[] b = new byte[4];

		b[0] = (byte) (0xff & i);
		b[1] = (byte) ((0xff00 & i) >> 8);
		b[2] = (byte) ((0xff0000 & i) >> 16);
		b[3] = (byte) ((0xff000000 & i) >> 24);
		return b;
	}

	/**
	 * 获取附件路径
	 * 
	 * @return
	 */
	private static File getAttachmentRootPath() {
		String configPath = Config.get("file.attachment.path");
		if (StringUtils.isNotEmpty(configPath)) {
			return new File(configPath);
		} else {
			return Platform.getDefaultAttachmentPath();
		}
	}

	/**
	 * 创建文件
	 * 
	 * @param file
	 * @return
	 * @throws IOException
	 */
	private static File getNewNameFile(File file) throws IOException {
		if (!file.exists()) {
			return file;
		}

		int indexOfSplit = file.getName().lastIndexOf(".");
		String newName = file.getName().substring(0, indexOfSplit) + "("
				+ Formatter.formatDatetime(new Date(), "yyyyMMddHHmmssSSS") + ")"
				+ file.getName().substring(indexOfSplit);
		return getNewNameFile(new File(file.getParentFile(), newName));
	}

	/**
	 * 获取用户文件空间根目录
	 * 
	 * @return
	 */
	public static File getRootUserFileSpace() {
		File file = new File(getAttachmentRootPath(), "user_file");
		if (!file.exists()) {
			file.mkdirs();
		}
		return file;
	}

	/**
	 * 获取用户临时文件根目录
	 * 
	 * @return
	 */
	public static File getCurrentUserTempSpace() {
		return getCurrentUserTempSpace(null);
	}

	public static File getCurrentUserTempSpace(String uid) {
		if (StringUtils.isEmpty(uid)) {
			if(SessionManager.getUser() != null) {
				uid = SessionManager.getUser().getUid();
			} else {
				uid = "_default";
			}

		}
		File file = new File(getDbFileSpace(), uid);
		if (!file.exists()) {
			file.mkdirs();
		}
		return file;
	}

	/**
	 * 获取当前用户文件空间
	 * 
	 * @return
	 */
	public static File getCurrentUserFileSpace() {
		return getCurrentUserFileSpace(null);
	}

	public static File getCurrentUserFileSpace(String uid) {
		if (StringUtils.isEmpty(uid)) {
			if(SessionManager.getUser() != null) {
				uid = SessionManager.getUser().getUid();
			} else {
				uid = "_default";
			}

		}

		File file = new File(getRootUserFileSpace(), uid);
		if (!file.exists()) {
			file.mkdirs();
		}
		return file;
	}

	/**
	 * 获取附件永久保存目录
	 * 
	 * @return
	 */
	public static File getSysFileSpace() {
		File file = new File(getAttachmentRootPath(), "sys_file");
		if (!file.exists()) {
			file.mkdirs();
		}
		return file;
	}

	/**
	 * 获取数据库文件临时保存控件
	 * 
	 * @return
	 */
	public static File getDbFileSpace() {
		File file = new File(getAttachmentRootPath(), "temp_file");
		if (!file.exists()) {
			file.mkdirs();
		}
		return file;
	}

	/**
	 * 获取文件
	 * 
	 * @param type
	 * @param sysName
	 * @return
	 */
	public static File getFile(String type, String sysName) {
		if (StringUtils.isEmpty(type) || StringUtils.isEmpty(sysName)) {
			return null;
		}
		if (DB_FILE_TYPE.equalsIgnoreCase(type)) {
			return getDbFile(sysName);
		} else if (SYS_FILE_TYPE.equalsIgnoreCase(type)) {
			return getSysFile(sysName);
		}
		return null;
	}

	/**
	 * 获取数据库临时文件
	 * 
	 * @param val
	 * @return
	 */
	private static File getDbFile(String val) {
		if (val == null) {
			return null;
		}
		return new File(getDbFileSpace().getAbsoluteFile() + File.separator + val);
	}

	/**
	 * 获取永久文件
	 * 
	 * @param val
	 * @return
	 */
	private static File getSysFile(String val) {
		if (val == null) {
			return null;
		}
		File file = new File(getSysFileSpace().getAbsoluteFile() + File.separator + val);
		return file;
	}

	/**
	 * 获取文件
	 * 
	 * @param fileName
	 * @return
	 */
	public static File findCurrentUserFile(String fileName) {
		if (StringUtils.isEmpty(fileName)) {
			return null;
		}
		File file = new File(getCurrentUserFileSpace(), fileName);
		if (file.exists()) {
			return file;
		}

		return null;
	}

	/**
	 * 保存临时DB文件
	 * 
	 * @param name
	 * @param bytes
	 * @return
	 */
	public static UploadFile saveDbFile(String name, byte[] bytes) {
		File dir = getDbFileSpace();
		String dateDir = Formatter.formatDatetime(new Date(), "yyyyMMdd");
		new File(dir, dateDir).mkdirs();

		String sysName = dateDir + "/_" + getMd5ByFile(bytes) + ".tmp";// 下划线开头+md5+.tmp
		File file = new File(dir, sysName);

		try {
			if (!file.exists()) {// 文件不存在才创建
				file.createNewFile();
				FileUtils.writeByteArrayToFile(file, bytes);
			}
		} catch (IOException e) {
			throw new SystemRuntimeException("文件[" + file.getName() + "]无法创建.", e);
		}

		UploadFile result = new UploadFile();
		result.setName(name);
		result.setType(DB_FILE_TYPE);
		result.setMode(DB_MODE);
		result.setSysName(sysName);

		return result;
	}

	/**
	 * 保存用户文件
	 * 
	 * @param tmpFile
	 * @param name
	 * @return
	 * @throws IOException
	 */
	public static File moveToUserFileSpace(File tmpFile, String name) throws IOException {
		File dir = getCurrentUserFileSpace();
		File file = getNewNameFile(new File(dir, name));
		FileUtils.moveFile(tmpFile, file);
		return file;
	}

	/**
	 * 获取当前用户归属文件
	 * 
	 * @return
	 */
	public static File[] listCurrentUserFiles() {
		File dir = getCurrentUserFileSpace();
		File[] files = dir.listFiles(new FileFilter() {
			@Override
			public boolean accept(File f) {
				return f.isFile();
			}
		});
		// 日期排序
		Arrays.sort(files, new Comparator<File>() {
			@Override
			public int compare(File f1, File f2) {
				long diff = f1.lastModified() - f2.lastModified();
				if (diff > 0) {
					return -1;
				} else if (diff == 0) {
					return 0;
				} else
					return 1;
			}
		});

		return files;
	}

	/**
	 * 界面上传文件
	 * 
	 * @author Woden
	 * 
	 */
	public static class UploadFile {
		private String type;// 文件类型
		private String name;// 文件名
		private String sysName;// 系统文件名
		private String mode;// 保存类型,disk或db

		/**
		 * @return the mode
		 */
		public String getMode() {
			return mode;
		}

		/**
		 * @param mode
		 *            the mode to set
		 */
		public void setMode(String mode) {
			this.mode = mode;
		}

		/**
		 * 获取永久文件路径
		 * 
		 * @return
		 */
		public String getValue() {
			return "{name:'" + StringUtils.replaceEach(name, new String[] { "'", "\"" }, new String[] { "\\'", "\\\"" }) + "',type:'" + type + "',sysName:'" + sysName + "',mode:'" + mode + "',size:"
					+ getSize() + "}";
		}

		/**
		 * @return the type
		 */
		public String getType() {
			return type;
		}

		/**
		 * @param type
		 *            the type to set
		 */
		public void setType(String type) {
			this.type = type;
		}

		/**
		 * @return the name
		 */
		public String getName() {
			return StringUtils.replaceEach(name, new String[] { "'", "\"" }, new String[] { "\\'", "\\\"" });
		}

		/**
		 * @param name
		 *            the name to set
		 */
		public void setName(String name) {
			this.name = name;
		}

		public void setSysName(String sysName) {
			this.sysName = sysName;
		}

		public String getSysName() {
			return sysName;
		}

		/**
		 * 文件大小
		 * 
		 * @return
		 */
		public long getSize() {
			return getFile().length();
		}

		/**
		 * 获取文件路径
		 * 
		 * @return
		 */
		public File getFile() {
			if (TEMP_FILE_TYPE.equals(type)) {
				return findCurrentUserFile(name);
			} else if (DB_FILE_TYPE.equals(type)) {
				return getDbFile(sysName);
			} else if (SYS_FILE_TYPE.equals(type)) {
				return getSysFile(sysName);
			} else {
				return null;
			}
		}

		/**
		 * 获取流
		 * 
		 * @return
		 * @throws FileNotFoundException
		 */
		public InputStream getInputStream() throws FileNotFoundException {
			return new FileInputStream(getFile());
		}

		/**
		 * 获取字节
		 * 
		 * @return
		 * @throws IOException
		 */
		public byte[] toBytes() throws IOException {
			return toBytes(null);
		}

		/**
		 * 获取字节
		 * 
		 * @param path
		 * @return
		 * @throws IOException
		 */
		public byte[] toBytes(String path) throws IOException {
			if (DISK_MODE.equalsIgnoreCase(mode)) {// 存硬盘模式
				return save(StringUtils.isNotEmpty(path) ? path : Formatter.formatDatetime(new Date(), "yyyyMMdd"))
						.getSysName().getBytes();
			} else {
				return FileUtils.readFileToByteArray(getFile());
			}
		}

		/**
		 * 保存临时文件到系统路径
		 * 
		 * @param path
		 * @return
		 */
		private UploadFile save(String path) {
			if (!SYS_FILE_TYPE.equalsIgnoreCase(type)) {
				File srcFile = getFile();
				File dir = new File(getSysFileSpace(), path);
				if (!dir.exists()) {
					dir.mkdirs();
				}
				try {
					String sysName = "_" + getMd5ByFile(FileUtils.readFileToByteArray(getFile())) + ".sys";// 下划线开头+md5+.sys
					File file = new File(dir, sysName);
					if (!file.exists()) {// 文件不存在才创建
						FileUtils.copyFile(srcFile, file);
					}

					this.sysName = path + "/" + sysName;
					this.type = SYS_FILE_TYPE;
				} catch (IOException e) {
					throw new SystemRuntimeException(e);
				}
			}
			return this;
		}
	}

	public static class DevFile extends UploadFile {
		private byte[] bytes;

		public DevFile(String name, byte[] bytes) {
			super.mode = DB_MODE;
			super.name = name;
			super.sysName = name;
			super.type = TEMP_FILE_TYPE;
			this.bytes = bytes;
		}

		/**
		 * 获取字节
		 * 
		 * @return
		 * @throws IOException
		 */
		@Override
		public byte[] toBytes() throws IOException {
			return bytes;
		}

		/**
		 * 获取字节
		 * 
		 * @param path
		 * @return
		 * @throws IOException
		 */
		@Override
		public byte[] toBytes(String path) throws IOException {
			return toBytes();
		}
	}

	/**
	 * 获取上传文件
	 * 
	 * @param request
	 * @param name
	 * @return
	 */
	public static UploadFile getUploadFile(HttpServletRequest request, String name) {
		String value = RequestUtils.getStringValue(request, name);
		return getUploadFile(value);
	}

	/**
	 * 获取上传文件
	 * 
	 * @param request
	 * @param name
	 * @return
	 */
	public static UploadFile getUploadFile(RequestContext request, String name) {
		String value = request.getString(name);
		return getUploadFile(value);
	}

	/**
	 * 获取上传文件
	 * 
	 * @param value
	 * @return
	 */
	public static UploadFile getUploadFile(String value) {
		List<UploadFile> list = getUploadFiles(value);
		if (list == null || list.size() < 1) {
			return null;
		} else {
			return list.get(0);
		}
	}

	/**
	 * 获取上传文件数组
	 * 
	 * @param request
	 * @param name
	 * @return
	 */
	public static List<UploadFile> getUploadFiles(HttpServletRequest request, String name) {
		String value = RequestUtils.getStringValue(request, name);
		return getUploadFiles(value);
	}

	/**
	 * 获取上传文件数组
	 * 
	 * @param request
	 * @param name
	 * @return
	 */
	public static List<UploadFile> getUploadFiles(RequestContext request, String name) {
		String value = request.getString(name);
		return getUploadFiles(value);
	}

	/**
	 * 获取上传文件数组
	 * 
	 * @param value
	 * @return
	 */
	public static List<UploadFile> getUploadFiles(String value) {
		if (StringUtils.isEmpty(value)) {
			return Collections.emptyList();
		}

		List<UploadFile> files = null;
		try {
			files = JsonMapper.defaultMapper().fromJsons(value, UploadFile.class);
		} catch (IOException e) {
			logger.error("转换JSON[" + value + "]出错.", e);
		}
		return files;
	}

	/**
	 * 获取路径
	 * 
	 * @param bytes
	 * @return
	 */
	public static String toId(byte[] bytes) {
		try (InputStream is = new ByteArrayInputStream(bytes)) {
			return toId(is);
		} catch (IOException e) {
			logger.error("字节转换成文件出错.", e);
			return null;
		}
	}

	/**
	 * 获取路径
	 * 
	 * @param is
	 * @return
	 */
	public static String toId(InputStream is) {
		try {
			// 先拿100位
			byte[] buff = new byte[100];
			is.read(buff);
			String path = new String(buff);
			return StringUtils.trim(path);
		} catch (IOException e) {
			logger.error("数据流转换文件出错.", e);
			return null;
		}
	}

	/**
	 * 生成保存到数据库的字节流
	 * 
	 * @param files
	 * @return
	 */
	public static byte[] toBytes(String id, List<UploadFile> files) {
		return toBytes(id, files, id);
	}

	/**
	 * 生成保存到数据库的字节流
	 * 
	 * @param files
	 * @param path
	 * @return
	 */
	public static byte[] toBytes(String id, List<UploadFile> files, String path) {
		try (ByteArrayOutputStream bos = new ByteArrayOutputStream();
				ByteArrayOutputStream fileBos = new ByteArrayOutputStream();) {

			// 首先预留100位长度放置路径名
			byte[] fileId = (StringUtils.isEmpty(id) || id.length() > 100) ? IDGenerator.uuid().getBytes() : id
					.getBytes();
			bos.write(fileId);
			bos.write(new byte[100 - fileId.length]);// 补充空白

			List<Map<String, Object>> jsons = new ArrayList<>();
			if (files != null) {
				for (UploadFile file : files) {
					Map<String, Object> map = new HashMap<>();
					byte[] fileBytes = file.toBytes(path);
					map.put("type", file.getType());
					map.put("name", file.getName());
					map.put("size", fileBytes.length);
					jsons.add(map);
					fileBos.write(fileBytes);
				}
			}

			String json = JsonMapper.defaultMapper().toJson(jsons);
			byte[] jsonBytes = json.getBytes();
			// byte前4位放JSON数据的长度
			bos.write(int2bytes(jsonBytes.length));// 占4位
			// 5-N放置JSON数据
			// [{name:'a.xml',size:9}]
			bos.write(jsonBytes);
			// N-end放置实际数据
			fileBos.writeTo(bos);

			return bos.toByteArray();
		} catch (IOException e) {
			logger.error("文件生成字节流出错.", e);
			return null;
		}
	}

	/**
	 * 将数据库中保存的流转换成临时文件
	 * 
	 * @param bytes
	 * @return
	 */
	public static List<UploadFile> toFiles(byte[] bytes) {
		try (InputStream is = new ByteArrayInputStream(bytes)) {
			return toFiles(is);
		} catch (IOException e) {
			logger.error("字节转换成文件出错.", e);
			return null;
		}
	}

	/**
	 * 将数据库中保存的流转换成临时文件
	 * 
	 * @param is
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static List<UploadFile> toFiles(InputStream is) {
		try {
			List<UploadFile> list = new ArrayList<>();
			// 先拿100位
			byte[] buff = new byte[100];
			is.read(buff);

			// 先拿前4位
			buff = new byte[4];
			is.read(buff);// 先读4位
			int size = bytes2int(buff);

			if (size > is.available()) {// 避免内存溢出
				return new ArrayList<FileManager.UploadFile>();
			}

			// 再拿JSON
			buff = new byte[size];
			is.read(buff);
			String json = new String(buff);// 构建JSON

			List<Map<String, Object>> array = JsonMapper.defaultMapper().fromJson(json, List.class);
			Iterator<Map<String, Object>> it = array.iterator();
			while (it.hasNext()) {
				Map<String, Object> o = it.next();
				buff = new byte[Integer.valueOf(o.get("size").toString())];
				is.read(buff);
				if (SYS_FILE_TYPE.equals(o.get("type"))) {
					UploadFile uploadFile = new UploadFile();
					uploadFile.setType((String) o.get("type"));
					uploadFile.setName((String) o.get("name"));
					uploadFile.setMode(DISK_MODE);
					uploadFile.setSysName(new String(buff));
					list.add(uploadFile);
				} else {
					list.add(saveDbFile(o.get("name").toString(), buff));
				}
			}
			return list;
		} catch (IOException e) {
			logger.error("数据流转换文件出错.", e);
			return null;
		}
	}

}
