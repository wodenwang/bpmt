/*
 * $HeadURL: $
 * $Id: $
 * Copyright (c) 2014 by Riversoft System, all rights reserved.
 */
package com.riversoft.module.widget;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;

import com.riversoft.core.db.po.Code2NameVO;
import com.riversoft.core.exception.ExceptionType;
import com.riversoft.core.exception.SystemRuntimeException;
import com.riversoft.core.web.Actions;
import com.riversoft.core.web.RequestUtils;
import com.riversoft.platform.db.BaseDataService;
import com.riversoft.platform.db.Code2NameService;
import com.riversoft.util.ReflectionUtils;

/**
 * @author woden
 * 
 */
public class SelectAction {

	/**
	 * 重新加载select
	 * 
	 * @param request
	 * @param response
	 */
	public void reload(HttpServletRequest request, HttpServletResponse response) {
		String type = RequestUtils.getStringValue(request, "type");
		String code = RequestUtils.getStringValue(request, "code");
		String name = RequestUtils.getStringValue(request, "name");
		String condition = RequestUtils.getStringValue(request, "condition");

		if (StringUtils.isEmpty(type)) {
			throw new SystemRuntimeException(ExceptionType.BUSINESS, "命令出错.");
		}

		List<Code2NameVO> list = new ArrayList<>();
		if (type.startsWith("$")) {// ORM模式
			String entity = type.substring(1);
			list.addAll(Code2NameService.getInstance().getListORM(entity, code, name, condition));
		} else if (type.startsWith("#")) {// JDBC模式
			String table = type.substring(1);
			list.addAll(Code2NameService.getInstance().getListJDBC(table, code, name, condition));
		} else if (type.startsWith("@")) {// 枚举翻译
			String className = type.substring(1);
			Class<?> klass;
			try {
				klass = Class.forName(className);
			} catch (ClassNotFoundException e) {
				throw new SystemRuntimeException("枚举类[" + className + "]不存在。", e);
			}
			Code2NameVO[] vos = (Code2NameVO[]) ReflectionUtils.getMethodValue(klass, klass, "values", new Class[] {},
					new Object[] {});
			list.addAll(Arrays.asList(vos));
		} else {
			list.addAll(BaseDataService.getInstance().getList(type, condition));
		}
		Actions.showJson(request, response, list);
	}
}
