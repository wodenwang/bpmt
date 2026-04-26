/*
 * $HeadURL: $
 * $Id: $
 * Copyright (c) 2013 by Riversoft System, all rights reserved.
 */
package com.riversoft.core.exception;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import jodd.mail.MailException;

import org.hibernate.JDBCException;
import org.jpedal.exception.PdfException;
import org.quartz.SchedulerException;

/**
 * 异常类型枚举
 * 
 * @author wodenwang
 * 
 */
@SuppressWarnings("unchecked")
public enum ExceptionType {
	/**
	 * 获取到异常,界面采用友好提示
	 */
	INFO(0, "非异常"),
	/**
	 * 页面采用警告样式
	 */
	WARN(1, "非异常"),

	/**
	 * 默认异常
	 */
	DEFAULT(9900, "系统未知异常。"),

	/**
	 * 编码校验异常
	 */
	CODING(9800, "编码异常。"),

	/**
	 * 数据库异常
	 */
	DB(100, "数据库错误。", JDBCException.class, SQLException.class),
	DB_INSERT(101, "数据库主键重复。"),
	DB_DDL(102, "建表规则有误."),

	/**
	 * web层异常
	 */
	WEB(200, "页面异常。"),

	/**
	 * 上下文属性异常
	 */
	CONTEXT(300, "上下文属性异常。"),
	CONTEXT_EMPTY(301, "属性不存在。"),

	/**
	 * 脚本执行属性异常
	 */
	SCRIPT(400, "脚本执行异常。"),
	SCRIPT_ATTRIBUTE_EMPTY(401, "属性不存在。"),
	SCRIPT_METHOD_EMPTY(402, "方法不存在。"),
	SCRIPT_COMPILE_ERROR(
			403, "脚本编译出错。"),
	SCRIPT_BUSI(404, "脚本业务异常。"),

	/**
	 * 配置类异常
	 */
	CONFIG(500, "数据配置出错。"),
	CONFIG_WIDGET(501, "Form组件配置出错。"),

	/**
	 * 数据格式化异常
	 */
	FORMAT(600, "数据格式化异常。"),
	FORMAT_NUMBER(601, "数字转换出错。", NumberFormatException.class),

	/**
	 * XML处理异常
	 */
	HBM_PROCESS(700, "hbm文件处理异常。"),
	HBM_PROCESS_CLASSNOTFOUND(701, "类定义没有找到。"),

	/**
	 * 业务校验异常(主动异常)
	 */
	BUSINESS(800, "业务规则异常。"),
	BUSINESS_PRIVILAGE(801, "权限不足。"),

	/**
	 * 编译代码异常
	 */
	COMPILIATION(900, "编译错误。"),

	/**
	 * 工作流错误
	 */
	FLOW(1000, "工作流异常."),
	FLOW_START_REPEATED(1001, "流程无法重复启动."),
	FLOW_CREATE_ORD_ID(1002, "创建订单号出错."),
	FLOW_PD_NOT_FOUND(
			1003, "找不到流程定义ID."),
	FLOW_ORD_NOT_FOUND(1004, "订单不存在."),
	FLOW_ORD_PAUSE(1005, "流程无法分配处理人或已被挂起,暂时无法处理."),
	FLOW_NO_CONFIG(
			1006, "找不到对应的流程配置."),
	FLOW_CONFIG_ERROR(1007, "流程配置逻辑有误."),
	FLOW_LOGIC_ERROR(1008, "流程处理逻辑出错."),
	FLOW_TASK_ASSIGNEE(
			1009, "任务已被他人独占."),

	/**
	 * 调度框架异常
	 */
	JOB(1100, "调度框架异常.", SchedulerException.class),

	/**
	 * PDF文档处理相关异常
	 */
	PDF(1200, "PDF文件处理异常", PdfException.class),

	/**
	 * 邮件收发相关异常
	 */
	MAIL(1300, "文件收发异常", MailException.class),

	/**
	 * 调度框架异常
	 */
	QUEUE(1400, "异步队列异常."),

	/**
	 * 微信交互相关异常
	 */
	WX(1500, "微信交互相关异常."),

	/**
	 * 淘宝交互相关异常
	 */
	TAOBAO(1600, "淘宝交互相关异常.")

	/**/
	;
	/**
	 * 异常码，格式如：100,1203.<br>
	 * 前两位为类型编码，后两位为明细编码。如：100表示数据库类型异常，101表示数据库新增异常。
	 */
	private int code;
	/**
	 * 通用提示信息。
	 */
	private String msg;
	/**
	 * 异常类型匹配。 考虑使用类名正则匹配，如DataAccess*Exception之类。
	 */
	private List<Class<? extends Throwable>> clss;

	/**
	 * @return the code
	 */
	public int getCode() {
		return code;
	}

	/**
	 * @return the msg
	 */
	public String getMsg() {
		return msg;
	}

	/**
	 * @return the clss
	 */
	public List<Class<? extends Throwable>> getClss() {
		return clss;
	}

	/**
	 * 枚举构造
	 * 
	 * @param code
	 * @param msg
	 * @param clss
	 */
	private ExceptionType(int code, String msg, Class<? extends Throwable>... clss) {
		this.code = code;
		this.msg = msg;
		if (clss != null && clss.length > 0) {
			this.clss = Arrays.asList(clss);
		} else {
			this.clss = new ArrayList<>();
		}
	}

}
