/*
 * $HeadURL: $
 * $Id: $
 * Copyright (c) 2013 by Riversoft System, all rights reserved.
 */
package com.riversoft.widget.db;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.riversoft.core.db.po.Code2NameVO;
import com.riversoft.core.exception.ExceptionType;
import com.riversoft.core.exception.SystemRuntimeException;
import com.riversoft.core.web.widget.FormValue;
import com.riversoft.core.web.widget.Widget;
import com.riversoft.core.web.widget.WidgetAnnotation;
import com.riversoft.platform.db.BaseDataService;
import com.riversoft.platform.db.Code2NameService;
import com.riversoft.util.ReflectionUtils;
import com.riversoft.util.jackson.JsonMapper;

/**
 * select 控件处理器<br>
 * select[DB_TYPE(请选择)]:字典翻译<br>
 * select[$Demo,code,name,condition]:ORM对象翻译<br>
 * select[#DEMO,code,name,condition]:JDBC数据库翻译<br>
 * select[@com.riversoft.core.db.po.VarItemName]:枚举类型翻译
 * 
 * @author Woden
 * 
 */
@WidgetAnnotation(cmd = "select", ftl = "classpath:widget/{mode}/db/select.ftl")
public class Select implements Widget {

	private static final Logger logger = LoggerFactory.getLogger(Select.class);

	private static class VO implements Code2NameVO {

		protected Object code;
		protected String showName;

		/**
		 * @return the showName
		 */
		public String getShowName() {
			return showName;
		}

		/**
		 * @param showName
		 *            the showName to set
		 */
		public void setShowName(String showName) {
			this.showName = showName;
		}

		/**
		 * @return the code
		 */
		public Object getCode() {
			return code;
		}

		/**
		 * @param code
		 *            the code to set
		 */
		public void setCode(Object code) {
			this.code = code;
		}

	}

	/**
	 * code2name所需几项
	 */
	protected String type;
	protected String code;
	protected String name;
	protected String condition;
	protected String width;// 宽

	/**
	 * 默认值列表
	 */
	private List<Code2NameVO> defaultList = new ArrayList<>();

	@Override
	public void prepareMap(Map<String, Object> map) {

		String dyncParams = (String) map.get("dyncParams");
		if (StringUtils.isNotEmpty(dyncParams)) {
			Map<String, Object> json;
			try {
				json = JsonMapper.defaultMapper().fromJson(dyncParams, Map.class);
			} catch (Exception e) {
				throw new SystemRuntimeException(ExceptionType.CONFIG_WIDGET, "控件动态入参出错.", e);
			}

			if (json != null) {
				if (json.containsKey("type")) {
					type = json.get("type").toString();
				}
				if (json.containsKey("code")) {
					code = json.get("code").toString();
				}
				if (json.containsKey("name")) {
					name = json.get("name").toString();
				}
				if (json.containsKey("condition")) {
					condition = json.get("condition").toString();
				}
			}
		}

		map.put("width", width);
		map.put("list", getList());
	}

	/**
	 * 获取列表
	 * 
	 * @return
	 */
	protected List<Code2NameVO> getList() {
		List<Code2NameVO> list = new ArrayList<>();
		list.addAll(defaultList);// 增加默认列表在最前面
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
		return list;
	}

	@Override
	public void setParams(FormValue... values) {
		if (values == null || values.length < 1 || values[0] == null || values[0].getName() == null) {
			return;
		}

		// 有param，设置默认列表
		if (values[0].getParam() != null) {
			String str = values[0].getParam();
			// name:code 这样的格式
			String[] params = str.split(",");
			for (String param : params) {
				VO vo = new VO();
				if (param.indexOf(":") > 0 && param.indexOf(":") < param.length() - 1) {
					vo.setShowName(param.substring(0, param.indexOf(":")));
					vo.setCode(param.substring(param.indexOf(":") + 1));
				} else {
					vo.setCode("");
					vo.setShowName(param);
				}
				this.defaultList.add(vo);
			}
		}

		// 处理参数
		this.type = values[0].getName();
		if (values[0].getName().startsWith("$")) {// ORM模式
			if (values.length < 3) {// 至少三个入参
				throw new SystemRuntimeException(ExceptionType.CONFIG_WIDGET, "select组件配置出错。");
			}
			code = values[1].getName();
			name = values[2].getName();
			condition = values.length > 3 ? values[3].getName() : null;
			if (values.length > 4) {
				width = values[4].getName();
			}

		} else if (values[0].getName().startsWith("#")) {// JDBC模式
			if (values.length < 3) {// 至少三个入参
				throw new SystemRuntimeException(ExceptionType.CONFIG_WIDGET, "select组件配置出错。");
			}
			code = values[1].getName();
			name = values[2].getName();
			condition = values.length > 3 ? values[3].getName() : null;
			if (values.length > 4) {
				width = values[4].getName();
			}
		} else if (values[0].getName().startsWith("@")) {// 枚举翻译
			if (values.length > 1) {
				width = values[1].getName();
			}
		} else {
			condition = values.length > 1 ? values[1].getName() : null;
			if (values.length > 2) {
				width = values[2].getName();
			}
		}

	}

	@Override
	public String show(Object value) {
		// 遍历翻译
		for (Code2NameVO vo : getList()) {
			if (value.toString().equals(vo.getCode().toString())) {
				return vo.getShowName();
			}
		}

		return value.toString();
	}

	@Override
	public Object code(String showName) {
		// 这里不需要反向翻译，只需把中括号[]的值解析出来即可
		Pattern p = Pattern.compile("\\[([^\\]]*)\\]");
		Matcher m = p.matcher(showName);
		logger.debug("解析showName:[" + showName + "]");
		while (m.find()) {
			String result = m.group(1);
			logger.debug("解析到值：[" + result + "]");
			return result;
		}

		return showName;
	}

}
