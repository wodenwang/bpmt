/*
 * $HeadURL: $
 * $Id: $
 * Copyright (c) 2013 by Riversoft System, all rights reserved.
 */
package com.riversoft.module.development;

import com.riversoft.core.db.po.Tree;
import com.riversoft.core.exception.SystemRuntimeException;
import com.riversoft.platform.Platform;
import com.riversoft.util.FileCompress;
import com.riversoft.util.Formatter;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.*;

/**
 * 文件管理器
 * 
 * @author Woden
 */
public class DownloadFileManager {
	/**
	 * Logger for this class
	 */
	private static final Logger logger = LoggerFactory.getLogger(DownloadFileManager.class);

	/**
	 * 树菜单节点
	 * 
	 * @author Woden
	 */
	public static class TreeVO implements Tree {

		private static final long serialVersionUID = 2714115058761334385L;
		private String id;
		private String parentId;
		private String name;
		private Integer sort;

		// 扩展属性
		private boolean fileFlag;
		private String icon;

		/**
		 * 构造函数
		 * 
		 * @param id
		 * @param parentId
		 * @param name
		 * @param sort
		 * @param fileFlag
		 */
		public TreeVO(String id, String parentId, String name, Integer sort, boolean fileFlag) {
			this.id = id;
			this.parentId = parentId;
			this.name = name;
			this.sort = sort;
			this.fileFlag = fileFlag;

			// 设置图标
			if (fileFlag && id.lastIndexOf(".") > 0) {
				String pixel = id.substring(id.lastIndexOf(".") + 1).toLowerCase();
				icon = pixel;
			}
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
	 * 获取附件路径
	 * 
	 * @return
	 */
	private static File getDownloadRootPath() {
		return Platform.getDefaultDownloadPath();
	}

	/**
	 * 创建文件
	 * 
	 * @param file
	 * @return
	 * @throws IOException
	 */
	private static File createNewNameFile(File file) throws IOException {
		if (!file.exists()) {
			file.createNewFile();
			return file;
		}

		int indexOfSplit = file.getName().lastIndexOf(".");
		String newName = file.getName().substring(0, indexOfSplit) + "("
				+ Formatter.formatDatetime(new Date(), "yyyyMMddHHmmssSSS") + ")"
				+ file.getName().substring(indexOfSplit);
		return createNewNameFile(new File(file.getParentFile(), newName));
	}

	/**
	 * 获取文件
	 * 
	 * @param fileName
	 * @return
	 */
	public static File getFile(String fileName) {
		if (StringUtils.isEmpty(fileName)) {
			return null;
		}
		File file = new File(getDownloadRootPath(), fileName);
		if (file.exists()) {
			return file;
		}

		return null;
	}

	public static boolean rename(File file, String newName) {
		File newFile = new File(getDownloadRootPath(), newName);
		if (file.exists()) {
			return file.renameTo(newFile);
		}
		return false;
	}

	public static void unzip(File file) throws Exception {
		if (file.exists()) {
			FileCompress.unzip(file, getDownloadRootPath());
		}
	}

	/**
	 * 保存文件
	 * 
	 * @param name
	 * @param is
	 * @return
	 * @throws IOException
	 */
	public static File saveFile(String name, InputStream is) {
		File dir = getDownloadRootPath();
		File file;
		try {
			file = createNewNameFile(new File(dir, name));
		} catch (IOException e) {
			throw new SystemRuntimeException("文件[" + name + "]无法创建.", e);
		}
		try (FileOutputStream fos = new FileOutputStream(file); BufferedInputStream bis = new BufferedInputStream(is);) {
			int BUFFER_SIZE = 1024;
			byte[] buf = new byte[BUFFER_SIZE];
			int size = 0;

			while ((size = bis.read(buf)) != -1) {
				fos.write(buf, 0, size);
			}
		} catch (IOException e) {
			throw new SystemRuntimeException("文件[" + file.getName() + "]无法创建.", e);
		}

		return file;

	}

	/**
	 * 获取树
	 * 
	 * @return
	 */
	public static List<TreeVO> getTree() {
		CustomFileVisitor customFileVisitor = new CustomFileVisitor();

		try {
			if(Files.isSymbolicLink(getDownloadRootPath().toPath())) {
				Files.walkFileTree(Files.readSymbolicLink(getDownloadRootPath().toPath()), customFileVisitor);
			} else {
				Files.walkFileTree(getDownloadRootPath().toPath(), customFileVisitor);
			}
			return customFileVisitor.getResult();
		} catch (IOException e) {
			throw new SystemRuntimeException("无法构建文件树.", e);
		}
	}

	static class CustomFileVisitor extends SimpleFileVisitor<Path> {

		private Path rootPath = getDownloadRootPath().toPath().normalize();

		public CustomFileVisitor(){
			if(Files.isSymbolicLink(getDownloadRootPath().toPath())) {
				try {
					rootPath = Files.readSymbolicLink(getDownloadRootPath().toPath().normalize());
				} catch (IOException e) {
				}
			} else {
				rootPath = getDownloadRootPath().toPath().normalize();
			}

		}

		private List<TreeVO> list = new ArrayList<>();

		@Override
		public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) {
			if (!rootPath.equals(dir)) {// ignore download
				Path relativePath = rootPath.relativize(dir.normalize());

				String id = relativePath.toString();
				String parentId = rootPath.relativize(dir.normalize().getParent()).toString();
				String name = dir.toFile().getName();

				logger.debug("目录:id:{}, parentId:{}, name:{}", id, parentId, name);
				TreeVO treeVO = new TreeVO(id, parentId, name, id.length(), false);

				list.add(treeVO);
			}
			return FileVisitResult.CONTINUE;
		}

		@Override
		public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
			Path relativePath = rootPath.relativize(file.normalize());

			String id = relativePath.toString();
			String parentId = rootPath.relativize(file.normalize().getParent()).toString();
			String name = file.toFile().getName();

			logger.debug("文件:id:{}, parentId:{}, name:{}", id, parentId, name);
			TreeVO treeVO = new TreeVO(id, parentId, name, id.length(), true);

			list.add(treeVO);
			return FileVisitResult.CONTINUE;
		}

		public List<TreeVO> getResult() {
			return this.list;
		}

	}

}
