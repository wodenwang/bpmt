/*
 * $HeadURL: $
 * $Id: $
 * Copyright (c) 2013 by Riversoft System, all rights reserved.
 */
package com.riversoft.platform.web;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.riversoft.core.IDGenerator;
import com.riversoft.core.context.RequestContext;
import com.riversoft.core.context.SessionContext;
import com.riversoft.core.db.DataPO;
import com.riversoft.core.db.ORMService;
import com.riversoft.core.db.po.Code2NameVO;
import com.riversoft.core.exception.ExceptionType;
import com.riversoft.core.exception.ScriptRuntimeException;
import com.riversoft.core.exception.SystemRuntimeException;
import com.riversoft.core.script.annotation.ScriptSupport;
import com.riversoft.core.web.Actions;
import com.riversoft.core.web.Actions.Keys;
import com.riversoft.core.web.FreeMarkerUtils;
import com.riversoft.core.web.widget.FormWidget;
import com.riversoft.core.web.widget.WidgetState;
import com.riversoft.platform.db.BaseDataService;
import com.riversoft.platform.db.Types;
import com.riversoft.platform.language.LanguageFitter;
import com.riversoft.platform.po.TbColumn;
import com.riversoft.platform.po.TbTable;
import com.riversoft.platform.script.ScriptHelper;
import com.riversoft.platform.script.ScriptTypes;
import com.riversoft.platform.web.FileManager.UploadFile;
import com.riversoft.util.jackson.JsonMapper;

/**
 * 提供给界面函数使用的数据库辅助工具
 * 
 * @author Woden
 * 
 */
@ScriptSupport("cm")
public class CommonHelper {
	/**
	 * Logger for this class
	 */
	private static final Logger logger = LoggerFactory.getLogger(CommonHelper.class);

	/**
	 * 从request获取表动态信息
	 * 
	 * @param po
	 * @param tableName
	 * @return
	 */
	public static Map<String, Object> map(Map<String, Object> po, String tableName) {
		RequestContext request = RequestContext.getCurrent();
		TbTable table = (TbTable) ORMService.getInstance().findByPk(TbTable.class.getName(), tableName);
		if (table == null) {
			throw new SystemRuntimeException(ExceptionType.CONFIG, "表[" + tableName + "]不是合法动态表.");
		}
		po = new DataPO(tableName, po).toEntity();
		for (TbColumn column : table.getTbColumns()) {
			String name = column.getName();
			if (request.get(name) == null || StringUtils.isEmpty(request.getString(name))) {
				if (WidgetState.normal.name().equalsIgnoreCase(request.getString(name + "$")) || WidgetState.readonly.name().equalsIgnoreCase(request.getString(name + "$"))) {// 有表单值的情况,需要是从自定义表单传递值过来才会写入
					po.put(name, null);
				}
				continue;
			}

			switch (Types.findByCode(column.getMappedTypeCode())) {
			case BigDecimal:
				po.put(column.getName(), request.getBigDecimal(column.getName()));
				break;
			case Integer:
				po.put(column.getName(), request.getInteger(column.getName()));
				break;
			case Long:
				po.put(column.getName(), request.getLong(column.getName()));
				break;
			case String:
			case Clob:
				po.put(column.getName(), request.getString(column.getName()));
				break;
			case Date:
				po.put(column.getName(), request.getDate(column.getName()));
				break;
			case Blob:
				List<UploadFile> files = FileManager.getUploadFiles(request, column.getName());
				String id = request.getString(column.getName() + "~ID");
				po.put(column.getName(), FileManager.toBytes(id, files, tableName + "/" + column.getName() + "/" + id));// 表名+字段名做路径
				break;
			default:
				break;
			}
		}

		return po;
	}

	/**
	 * 从request获取表动态信息
	 * 
	 * @param tableName
	 * @return
	 */
	public static Map<String, Object> map(String tableName) {
		return map(new HashMap<String, Object>(), tableName);
	}

	/**
	 * 自定义函数调用
	 * 
	 * @param key
	 * @param args
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static Object invoke(String key, Object... args) {
		Map<String, Object> functionPO = (Map<String, Object>) ORMService.getInstance().findByPk("DevFunction", key);

		if (functionPO == null) {
			throw new SystemRuntimeException(ExceptionType.SCRIPT_METHOD_EMPTY, "自定义函数[" + key + "]不存在.");
		}
		try {
			if (logger.isDebugEnabled()) {
				logger.debug("自定义函数:====[" + key + "]====[开始]====");
			}

			Map<String, Object> context = new HashMap<String, Object>();
			context.put("args", args);

			return ScriptHelper.evel(ScriptTypes.forCode((Integer) functionPO.get("functionType")), (String) functionPO.get("functionScript"), context);
		} catch (ScriptRuntimeException e) {
			e.setMethod(key);
			throw e;
		} finally {
			if (logger.isDebugEnabled()) {
				logger.debug("自定义函数:====[" + key + "]====[结束]====");
			}
		}
	}

	/**
	 * 控件翻译的EL函数调用
	 * 
	 * @param cmd
	 * @param value
	 * @return
	 */
	public static String widget(String cmd, Object value) {
		return widget(cmd, value, null);
	}

	/**
	 * 控件翻译的EL函数调用
	 * 
	 * @param cmd
	 * @param value
	 * @param params
	 *            动态参数,支持字符和对象
	 * @return
	 */
	public static String widget(String cmd, Object value, Object params) {
		if (value == null) {
			return "";
		}
		FormWidget formWidget = new FormWidget(cmd);
		if (params != null) {
			if (params instanceof String) {
				formWidget.setDyncParams((String) params);
			} else if (params instanceof Map) {
				formWidget.setDyncParams(JsonMapper.defaultMapper().toJson(params));
			} else {
				formWidget.setDyncParams(params.toString());
			}
		}

		return formWidget.show(value);
	}

	/**
	 * 调用视图做链接
	 * 
	 * @param viewKey
	 * @param value
	 * @param params
	 * @return
	 */
	public static String view(String viewKey, Object value, Object params) {
		Map<String, Object> model = new HashMap<>();
		model.put("key", viewKey);
		if (value instanceof String) {
			model.put("value", value);
			model.put("title", value);
		} else if (value instanceof Map) {
			Map<String, ?> map = (Map) value;
			model.put("value", map.get("value"));
			model.put("title", map.get("title"));
			model.put("type", map.get("type"));
		}

		Object p;
		if (params == null) {
			p = null;
		} else if (params instanceof String) {
			p = params;
		} else {
			// TODO:JSON
			p = JsonMapper.defaultMapper().toJson(params);
		}
		model.put("params", p);
		model.put("uuid", IDGenerator.uuid());
		return FreeMarkerUtils.process("classpath:ftl/view.ftl", model);
	}

	/**
	 * 调用视图做链接
	 * 
	 * @param viewKey
	 * @param value
	 * @return
	 */
	public static String view(String viewKey, Object value) {
		return view(viewKey, value, null);
	}

	/**
	 * 获取动态参数
	 * 
	 * @return
	 */
	public static Object params() {
		List<HashMap<String, Object>> params = RequestContext.getCurrent().getJsons(Actions.Keys.PARAMS.toString());
		if (params == null || params.isEmpty()) {
			return new HashMap<String, Object>();
		}

		if (params.size() == 1) {
			return params.get(0);
		}

		return params;
	}

	/**
	 * 主动抛异常
	 * 
	 * @param msg
	 */
	public static void error(Object msg) {
		throw new SystemRuntimeException(ExceptionType.DEFAULT, msg != null ? msg.toString() : "<NULL>");
	}

	/**
	 * 主动抛出提示
	 * 
	 * @param msg
	 */
	public static void info(Object msg) {
		throw new SystemRuntimeException(ExceptionType.INFO, msg != null ? msg.toString() : "<NULL>");
	}

	/**
	 * 主动抛出警告
	 * 
	 * @param msg
	 */
	public static void warn(Object msg) {
		throw new SystemRuntimeException(ExceptionType.WARN, msg != null ? msg.toString() : "<NULL>");
	}

	/**
	 * 字典翻译
	 * 
	 * @param type
	 * @param code
	 * @return
	 */
	public static String db(String type, Object code) {
		return BaseDataService.getInstance().translate(type, code.toString());
	}

	/**
	 * 获取字典列表
	 * 
	 * @param type
	 * @return
	 */
	public static List<Code2NameVO> db(String type) {
		return BaseDataService.getInstance().getList(type);
	}

	/**
	 * 解析form控件内容
	 * 
	 * @param html
	 * @param params
	 *            填空
	 * @return
	 */
	public static String form(String html, Map<String, Object> params) {
		if (StringUtils.isEmpty(html)) {
			return html;
		}
		Document doc = Jsoup.parse(html);
		Elements textareas = doc.select("textarea");
		for (Element textarea : textareas) {
			String name = textarea.attr("name");
			if (params != null) {
				Object val = params.get(name);
				textarea.val(val != null ? val.toString() : "");
			} else {
				textarea.val(StringUtils.trimToEmpty(RequestContext.getCurrent().getString(name)));
			}
		}
		return doc.html();
	}

	/**
	 * 解析form控件内容
	 * 
	 * @param html
	 * @return
	 */
	public static String form(String html) {
		return form(html, null);
	}

	/**
	 * 自适应语言
	 * 
	 * @param str
	 */
	public static String lan(String str) {
		return LanguageFitter.fit(str);
	}

	/**
	 * xhtml客户端(pc版)
	 * 
	 * @return
	 */
	public static boolean xhtml() {
		return "xhtml".equalsIgnoreCase(RequestContext.getCurrent().getString(Actions.Keys.ACTION_MODE.toString()));
	}

	/**
	 * h5客户端(微信端)
	 * 
	 * @return
	 */
	public static boolean h5() {
		return "h5".equalsIgnoreCase(RequestContext.getCurrent().getString(Actions.Keys.ACTION_MODE.toString()));
	}

	/**
	 * 设值标题
	 * 
	 * @param title
	 */
	public static void title(String title) {
		RequestContext.getCurrent().set(Keys.TITLE.toString(), title);
	}

	/**
	 * 获取标题
	 * 
	 * @return
	 */
	public static String title() {
		return RequestContext.getCurrent().getString(Keys.TITLE.toString());
	}

	/**
	 * 获取request
	 * 
	 * @return
	 */
	public static RequestContext request() {
		return RequestContext.getCurrent();
	}

	/**
	 * 获取session
	 * 
	 * @return
	 */
	public static SessionContext session() {
		return SessionContext.getCurrent();
	}
}
