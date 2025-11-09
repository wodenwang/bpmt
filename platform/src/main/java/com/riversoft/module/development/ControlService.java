/*
 * $HeadURL: $
 * $Id: $
 * Copyright (c) 2013 by Riversoft System, all rights reserved.
 */
package com.riversoft.module.development;

import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.riversoft.core.db.JdbcService;
import com.riversoft.core.exception.ExceptionType;
import com.riversoft.core.exception.SystemRuntimeException;
import com.riversoft.platform.script.ScriptHelper;
import com.riversoft.platform.script.ScriptTypes;

/**
 * 脚本调试沙箱
 * 
 * @author woden
 * 
 */
public class ControlService {

	/**
	 * 沙箱调用
	 * 
	 * @param type
	 * @param script
	 * @param context
	 * @throws SandboxException
	 */
	public void executeScript(Integer type, String script, Map<String, Object> context) throws SandboxException {
		Object result = ScriptHelper.evel(ScriptTypes.forCode(type), script, context);
		throw new SandboxException(result);
	}

	/**
	 * 真实执行
	 * 
	 * @param type
	 * @param script
	 * @param context
	 * @return
	 */
	public Object executeScriptReal(Integer type, String script, Map<String, Object> context) {
		Object result = ScriptHelper.evel(ScriptTypes.forCode(type), script, context);
		return result;
	}

	/**
	 * 沙箱执行SQL
	 * 
	 * @param sql
	 * @throws SandboxException
	 */
	public void executeSQL(String sql) throws SandboxException {

		// 校验SQL中是否含有DDL关键词
		String tmp = StringUtils.remove(sql, " ").toString();
		tmp = StringUtils.remove(tmp, "\n");
		tmp = StringUtils.remove(tmp, "\t");

		if (StringUtils.contains(tmp, "altertable") || StringUtils.contains(tmp, "droptable")
				|| StringUtils.contains(tmp, "createtable")) {
			throw new SystemRuntimeException(ExceptionType.BUSINESS, "不允许执行DDL语句.");
		}

		JdbcService.getInstance().executeSQL(sql);
		throw new SandboxException("执行[" + sql + "]成功.");
	}
}

/**
 * 沙箱异常类.实际是带出成功结果
 * 
 * @author woden
 * 
 */
@SuppressWarnings("serial")
class SandboxException extends Exception {
	private Object result;

	SandboxException(Object result) {
		this.result = result;
	}

	Object getResult() {
		return result;
	}
}