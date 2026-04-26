/*
 * $HeadURL: $
 * $Id: $
 * Copyright (c) 2013 by Riversoft System, all rights reserved.
 */
package com.riversoft.module.widget;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;

import com.riversoft.core.web.Actions;
import com.riversoft.core.web.Actions.Util;

/**
 * @author woden
 * 
 */
public class IconAction {

	/**
	 * jquery ui图标表单
	 * 
	 * @param request
	 * @param response
	 */
	public void jqueryIcon(HttpServletRequest request, HttpServletResponse response) {
		String iconCp = Actions.Util.getContextPath(request) + "/css/icon/";
		request.setAttribute("iconCp", iconCp);
		Actions.includePage(request, response, Util.getPagePath(request, "jquery_icon.jsp"));
	}

	/**
	 * jquery ui图标选择窗
	 * 
	 * @param request
	 * @param response
	 */
	public void jqueryIconList(HttpServletRequest request, HttpServletResponse response) {
		Actions.includePage(request, response, Util.getPagePath(request, "jquery_icon_list.jsp"));
	}

	/**
	 * 框架图标表单
	 * 
	 * @param request
	 * @param response
	 */
	public void sysIcon(HttpServletRequest request, HttpServletResponse response) {
		String iconCp = Actions.Util.getContextPath(request) + "/css/icon/";
		request.setAttribute("iconCp", iconCp);
		Actions.includePage(request, response, Util.getPagePath(request, "sys_icon.jsp"));
	}

	/**
	 * 框架图标选择窗
	 * 
	 * @param request
	 * @param response
	 */
	public void sysIconList(HttpServletRequest request, HttpServletResponse response) {
		String iconCp = Actions.Util.getContextPath(request) + "/css/icon/";
		File iconPath = new File(request.getServletContext().getRealPath("/css/icon/"));

		Map<String, List<File>> map = new LinkedHashMap<String, List<File>>();
		List<File> list = Arrays.asList(iconPath.listFiles(new FileFilter() {
			@Override
			public boolean accept(File file) {
				return file.isFile();
			}
		}));
		Collections.sort(list, new Comparator<File>() {
			@Override
			public int compare(File o1, File o2) {
				return o1.compareTo(o2);
			}
		});

		for (File file : list) {
			String pixel = StringUtils.substring(file.getName(), 0, 3).toUpperCase();
			if (!map.containsKey(pixel)) {
				map.put(pixel, new ArrayList<File>());
			}
			map.get(pixel).add(file);
		}

		request.setAttribute("map", map);
		request.setAttribute("iconCp", iconCp);
		Actions.includePage(request, response, Util.getPagePath(request, "sys_icon_list.jsp"));
	}
}
