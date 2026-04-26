/*
 * $HeadURL: $
 * $Id: $
 * Copyright (c) 2013 by Riversoft System, all rights reserved.
 */
package com.riversoft.scheduler;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;

import org.apache.commons.io.FileUtils;
import org.quartz.Job;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.riversoft.platform.web.FileManager;
import com.riversoft.util.Formatter;

/**
 * 附件处理任务
 * 
 * @author woden
 * 
 */
public abstract class AbstractFileCleanupTask implements Job {

	private static final Logger logger = LoggerFactory.getLogger(AbstractFileCleanupTask.class);

	/**
	 * 二进制临时文件清理线程
	 * 
	 * @author woden
	 * 
	 */
	protected static class DbTempFileCleanThread extends Thread {

		@Override
		public void run() {
			deleteOldDbFile();
		}

		/**
		 * 删除二进制临时文件<br>
		 * 删除小于今天的所有文件夹
		 */
		private void deleteOldDbFile() {
			File dbSpace = FileManager.getDbFileSpace();
			final int current = Integer.parseInt(Formatter.formatDatetime(new Date(), "yyyyMMdd"));
			for (File file : dbSpace.listFiles(new FilenameFilter() {
				@Override
				public boolean accept(File dir, String name) {
					try {
						int fileDate = Integer.parseInt(name);
						return current > fileDate;
					} catch (Exception e) {
						return false;
					}
				}
			})) {
				try {
					logger.info("删除文件夹:" + file.getName());
					FileUtils.deleteDirectory(file);
				} catch (IOException e) {
					logger.error("删除文件夹[" + file.getName() + "]出错.", e);
					// 删不掉也不要紧
					// do nothing
				}
			}
			logger.info("清除二进制临时文件完毕.");
		}
	}

	/**
	 * 文件夹清理线程
	 * 
	 * @author woden
	 * 
	 */
	protected static class DirectorCleanThread extends Thread {

		private int limitDay = 5;
		private File root;

		DirectorCleanThread(File root, int limitDay) {
			this.root = root;
			this.limitDay = limitDay;
		}

		@Override
		public void run() {
			deleteOldUserFile(root, getLimitDate());
		}

		/**
		 * 删除中转区旧文件
		 * 
		 * @param dir
		 * @param date
		 *            删除某个时间点之前的文件
		 */
		private void deleteOldUserFile(File dir, Date date) {

			for (File file : dir.listFiles()) {
				if (file.isDirectory()) {// 遇到文件夹则递归
					deleteOldUserFile(file, date);
				} else {
					Date fileDate = new Date(file.lastModified());// 文件最后修改时间
					if (fileDate.before(date)) {// 比指定时间要早的,则删除.
						logger.info("删除中转区文件:" + file.getAbsolutePath());
						file.delete();
					}
				}
			}
			logger.info("清除中转区完毕.");
		}

		/**
		 * 获取待删除的零界时间
		 * 
		 * @return
		 */
		private Date getLimitDate() {
			Calendar now = Calendar.getInstance();
			now.setTime(new Date());
			now.set(Calendar.DATE, now.get(Calendar.DATE) - limitDay);// 删除N天前的用户临时文件
			return now.getTime();
		}
	}

}
