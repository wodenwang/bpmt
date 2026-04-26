/*
 * $HeadURL: $
 * $Id: $
 * Copyright (c) 2014 by Riversoft System, all rights reserved.
 */
package com.riversoft.platform.template;

import java.lang.reflect.Method;
import java.util.Arrays;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.reflect.MethodSignature;

import com.riversoft.core.db.DataPO;
import com.riversoft.core.db.ORMService;
import com.riversoft.platform.SessionManager;
import com.riversoft.util.jackson.JsonMapper;

/**
 * @author woden
 * 
 */
public class TemplateOperationAspect {

	/**
	 * 登记操作记录
	 * 
	 * @param joinPoint
	 * @return
	 * @throws Throwable
	 */
	public Object doAround(ProceedingJoinPoint joinPoint) throws Throwable {
		String methodName = joinPoint.getSignature().getName();// 调用方法
		Class<?> clazz = joinPoint.getTarget().getClass();
		Signature signature = joinPoint.getSignature();
		MethodSignature methodSignature = (MethodSignature) signature;
		Method method = methodSignature.getMethod();
		DevelopmentOperation annotation = method.getAnnotation(DevelopmentOperation.class);

		{
			Template current = Template.getCurrent();
			DataPO po = new DataPO("TplSnapshotRecord");
			po.set("version", current.getVersion());
			po.set("oprMemo", annotation.value());
			po.set("oprClass", clazz.getName());
			po.set("oprMethod", methodName);
			Object json = JsonMapper.defaultMapper().toJson(Arrays.asList(joinPoint.getArgs()));
			po.set("oprArgs", json != null ? json.toString() : null);
			po.set("createUid", SessionManager.getUser().getUid());
			ORMService.getInstance().save(po.toEntity());
		}

		// 执行
		return joinPoint.proceed();
	}
}
