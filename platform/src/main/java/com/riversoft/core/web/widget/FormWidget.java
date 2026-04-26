/*
 * $HeadURL: $
 * $Id: $
 * Copyright (c) 2013 by Riversoft System, all rights reserved.
 */
package com.riversoft.core.web.widget;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.riversoft.core.context.RequestContext;
import com.riversoft.core.exception.ExceptionType;
import com.riversoft.core.exception.SystemRuntimeException;
import com.riversoft.core.web.Actions.Keys;
import com.riversoft.core.web.FreeMarkerUtils;
import com.riversoft.util.jackson.JsonMapper;

/**
 * 表单控件<br>
 * 
 * <pre>
 *  text{required}<br>
 *  date[yyyy-MM-dd]{required:true}<br>
 *  select[TYPE_A]<br>
 *  select[Demo;name;value]<br>
 *  tree[TYPE_A]
 * </pre>
 * 
 * @author Woden
 */
public class FormWidget {
	/**
	 * Logger for this class
	 */
	static final Logger logger = LoggerFactory.getLogger(FormWidget.class);

	private String formCmd;
	/**
	 * 控件名<br>
	 * 对应模板：classpath:widget/cmd.ftl<br>
	 * 对应processor：com.river.widget.CmdProcessor
	 */
	private String cmd;

	/**
	 * 控件参数
	 */
	private List<FormValue> formParams = new ArrayList<>();

	/**
	 * 动态入参
	 */
	private String dyncParams;

	/**
	 * 参数处理器
	 */
	private Widget widget;

	/**
	 * 表单验证串
	 */
	private String validateParam;

	public FormWidget(String formCmd) {

		this.formCmd = formCmd;

		// 解析命令
		parseCmd(formCmd);

		try {
			Class<?> klass = AnnotatedWidgetProcessorsHolder.getInstance().getWidgets().get(cmd);
			// 判断接口
			if (klass.getInterfaces() != null && klass.getInterfaces().length > 0) {
				if (klass.getInterfaces()[0] != Widget.class) {
					throw new Exception();
				}
			}
			// 实例化
			Widget instance = (Widget) klass.newInstance();
			this.widget = instance;
		} catch (Exception e) {
			this.widget = new DefaultWidget();
		}

		// 初始化数据处理器
		this.widget.setParams(this.formParams.toArray(new FormValue[this.formParams.size()]));

	}

	/**
	 * @return the cmd
	 */
	public String getCmd() {
		return cmd;
	}

	/**
	 * @return the formParams
	 */
	List<FormValue> getFormParams() {
		return formParams;
	}

	/**
	 * @return the validateParam
	 */
	String getValidateParam() {
		return validateParam;
	}

	/**
	 * 解析命令
	 * 
	 * @param formCmd
	 */
	private void parseCmd(String formCmd) {
		String[] cmds = formCmd.trim().split("\\[|\\{");
		this.cmd = cmds[0];
		// " ] "后面就是验证串
		if (formCmd.indexOf("{") > 0) {
			this.validateParam = formCmd.substring(formCmd.indexOf("{"));
			formCmd = formCmd.substring(0, formCmd.indexOf("{"));// 去掉验证语句
		}

		if (formCmd.indexOf("[") > 0) {
			String[] params = formCmd.substring(formCmd.indexOf("[") + 1, formCmd.lastIndexOf("]")).split(";");
			for (String str : params) {
				this.formParams.add(new FormValue(str));
			}
		}
	}

	/**
	 * @param cmd
	 *            the cmd to set
	 */
	public void setCmd(String cmd) {
		this.cmd = cmd;
	}

	/**
	 * @param formParams
	 *            the formParams to set
	 */
	public void setFormParams(List<FormValue> formParams) {
		this.formParams = formParams;
	}

	/**
	 * @param dyncParams
	 *            the dyncParams to set
	 */
	public void setDyncParams(String dyncParams) {
		this.dyncParams = dyncParams;
	}

	/**
	 * 生成html
	 * 
	 * @param fieldName
	 *            html表单控件name
	 * @param state
	 *            控件状态
	 * @param value
	 *            传入值
	 * @return
	 */
	public String toHtml(String fieldName, WidgetState state, Object value, String actionMode) {
		Map<String, Object> model = new HashMap<>();

		model.put(FtlKey.validate.name(), validateParam);
		if (StringUtils.isNotEmpty(validateParam)) {
			Map<String, Object> validateJson = JsonMapper.defaultMapper().json2Map(validateParam);
			model.put(FtlKey.validateObj.name(), validateJson);
		} else {
			model.put(FtlKey.validateObj.name(), new HashMap<>());// 避免ftl中空指针,给ftl开发者一个方便
		}

		model.put(FtlKey.name.name(), fieldName);
		model.put(FtlKey.value.name(), "".equals(value) ? null : value);
		// state会通过${name}_传递到服务端
		model.put(FtlKey.state.name(), state != null ? state : WidgetState.normal);
		model.put(FtlKey.params.name(), formParams.toArray());
		model.put(FtlKey.uuid.name(), UUID.randomUUID());
		model.put(FtlKey.dyncParams.name(), this.dyncParams);
		model.put(FtlKey.cp.name(), RequestContext.getCurrent().getString(Keys.CP.toString()));
		widget.prepareMap(model);

		// 优先从开发资源获取数据
		if (widget instanceof WidgetResource) {
			InputStream resourceIs = ((WidgetResource) widget).getFtl();
			if (resourceIs != null) {
				return FreeMarkerUtils.process(formCmd, resourceIs, model);
			}
		}

		// 无开发资源再从配置列表中获取
		String resourceName;
		if (AnnotatedWidgetProcessorsHolder.getInstance().getWidgetResources().containsKey(cmd)) {// 优先查找控件
			resourceName = AnnotatedWidgetProcessorsHolder.getInstance().getWidgetResources().get(cmd);
			// 转换ftl路径适配
			if (StringUtils.isEmpty(actionMode)) {
				actionMode = "xhtml";
			}
			resourceName = StringUtils.replace(resourceName, "{mode}", actionMode);// 把mode转换成对应目录
		} else {
			throw new SystemRuntimeException(ExceptionType.CODING, "控件[" + cmd + "]没有模板.");
		}

		return FreeMarkerUtils.process(resourceName, model);
	}

	/**
	 * 转换格式
	 * 
	 * @param value
	 * @return
	 */
	public String show(Object value) {
		if (value == null) {
			return "";
		}

		// 转换展示数据，source入参false
		return this.widget.show(value);
	}

	/**
	 * 展示名转换成原始值
	 * 
	 * @param showName
	 * @return
	 */
	public Object code(String showName) {
		if (showName == null) {
			return null;
		}

		return this.widget.code(showName);
	}

}
