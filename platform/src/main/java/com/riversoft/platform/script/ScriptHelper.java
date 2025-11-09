/*
 * $HeadURL: $
 * $Id: $
 * Copyright (c) 2013 by Riversoft System, all rights reserved.
 */
package com.riversoft.platform.script;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

import com.riversoft.core.BeanFactory;
import com.riversoft.core.exception.ExceptionType;
import com.riversoft.core.exception.SystemRuntimeException;
import com.riversoft.core.script.ExpressionAndScriptExecutors;
import com.riversoft.core.script.ExprlangAnnotationScanner;
import com.riversoft.core.script.ScriptType;
import com.riversoft.util.JxlsUtils;

/**
 * @author Woden
 * 
 */
public class ScriptHelper {

	/**
	 * 执行脚本
	 * 
	 * @param type
	 * @param script
	 * @return
	 */
	public static Object evel(ScriptTypes type, String script) {
		return evel(type, script, null);
	}

	/**
	 * 执行脚本
	 * 
	 * @param type
	 * @param script
	 * @param context
	 * @return
	 */
	public static Object evel(ScriptTypes type, String script, Map<String, Object> context) {
		ExpressionAndScriptExecutors executors = (ExpressionAndScriptExecutors) BeanFactory.getInstance().getBean("expressionAndScriptExecutors");
		Object value;
		switch (type) {
		case EL:
			value = executors.evaluateEL(script.trim(), new BasicScriptExecutionContext(context));
			break;
		case GROOVY:
			value = executors.evaluateScript(ScriptType.GROOVY, script, new BasicScriptExecutionContext(context));
			break;
		default:
			value = executors.evaluateScript(ScriptType.JSR223, script, new BasicScriptExecutionContext(context));
			break;
		}

		return value;
	}

	/**
	 * 导出excel
	 * 
	 * @param out
	 * @param template
	 * @param context
	 * @throws IOException
	 */
	public static void export(OutputStream out, InputStream template, Map<String, Object> context) throws IOException {
		Map<String, Object> newContext = new HashMap<>();
		ExprlangAnnotationScanner exprlangAnnotationScanner = (ExprlangAnnotationScanner) BeanFactory.getInstance().getBean("exprlangAnnotationScanner");
		newContext.putAll(exprlangAnnotationScanner.getElSupports());
		newContext.putAll(context);
		try {
			JxlsUtils.exportFromTemplate(out, template, new BasicScriptExecutionContext(newContext).getVariableContext());
		} catch (IOException e) {
			throw new SystemRuntimeException(ExceptionType.CONTEXT, "模板不存在,可能已被管理员删除,请刷新页面并重新选择.", e);
		}
	}
}
