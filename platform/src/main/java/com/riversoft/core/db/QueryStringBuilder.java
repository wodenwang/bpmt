/*
 * $HeadURL: $
 * $Id: $
 * Copyright (c) 2013 by Riversoft System, all rights reserved.
 */
package com.riversoft.core.db;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

/**
 * HQL和SQL语句处理器.参考 {@link DataCondition}
 * 
 * @author Woden
 * 
 */
public class QueryStringBuilder {

	/**
	 * 构建查询语句. <br>
	 * 
	 * @param queryMap
	 *            查询MAP
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public static String build(Map queryMap) {
		return buildWhere(queryMap) + buildOrder(queryMap);
	}

	/**
	 * 构建查询语句.<br>
	 * 
	 * @param pixel
	 *            指定默认别名
	 * @param queryMap
	 * @return
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static String build(String pixel, Map queryMap) {
		Map<String, Object> pixelQueryMap = new HashMap<>();
		for (Map.Entry<String, ?> entry : ((Map<String, ?>) queryMap).entrySet()) {
			String fileName = getFieldName(entry.getKey());
			String cmd = getCommand(entry.getKey());
			if (fileName.indexOf(".") < 0) {// 没有点,说明本身不带别名
				fileName = pixel + "." + fileName;
			}
			pixelQueryMap.put("_" + cmd + "_" + fileName, entry.getValue());
		}

		return build(pixelQueryMap);
	}

	/**
	 * 生成排序字段 排序格式： _orderby_fieldname:asc/desc
	 * 
	 * @param queryMap
	 *            查询MAP
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public static String buildOrder(Map queryMap) {
		if (null == queryMap || queryMap.isEmpty())
			return "";

		Set entitySet = queryMap.entrySet();
		Iterator it = entitySet.iterator();
		StringBuffer ordbyBuff = new StringBuffer(" order by ");
		while (it.hasNext()) {
			Map.Entry entity = (Map.Entry) it.next();
			String key = (String) entity.getKey();
			String value = null;
			if (entity.getValue() instanceof String) {
				value = (String) entity.getValue();
			}
			value = StringUtils.trim(value);

			if (key.startsWith("_orderby_")) {// 不是_orderby打头的就直接路过
				ordbyBuff.append(getFieldName(key));
				if ("desc".equalsIgnoreCase(value)) {
					ordbyBuff.append(" desc");
				} else {
					ordbyBuff.append(" asc");
				}
				ordbyBuff.append(",");
			} else if (key.startsWith("_orderbysql_") && StringUtils.isNotEmpty(value)) {
				ordbyBuff.append(value).append(",");
			}
		}

		if (ordbyBuff.toString().endsWith(",")) {// 去掉最后的逗号
			return ordbyBuff.substring(0, ordbyBuff.lastIndexOf(","));
		} else {
			return ordbyBuff.toString().equals(" order by ") ? "" : ordbyBuff.toString();
		}

	}

	/**
	 * 构建查询语句. <br>
	 * 
	 * <pre>
	 * eg:
	 * where name = value order by name asc.
	 * </pre>
	 * 
	 * @param queryMap
	 *            查询MAP
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public static String buildWhere(Map queryMap) {

		// 无查询条件调用
		if (null == queryMap || queryMap.isEmpty())
			return "";

		Set entitySet = queryMap.entrySet();
		Iterator it = entitySet.iterator();
		StringBuffer whereBuff = new StringBuffer();
		while (it.hasNext()) {
			Map.Entry entity = (Map.Entry) it.next();
			String key = (String) entity.getKey();

			String value = null;
			if (entity.getValue().getClass() == String.class) {
				value = (String) entity.getValue();
			} else if (entity.getValue().getClass() == String[].class) {
				value = "not null";
			}

			if (null == value || value.trim().equals("")) {// 无参数传入则不做为查询条件
				continue;
			} else if (getCommand(key).equals("sql")) {
				whereBuff.append(getConnectType(key)).append("(" + value + ")");
			} else if (getCommand(key).equals("sisn")) {
				if ("0".equals(value)) {
					whereBuff.append(getConnectType(key)).append(getFieldName(key)).append(" is null ");
				} else if ("1".equals(value)) {
					whereBuff.append(getConnectType(key)).append(getFieldName(key)).append(" is not null ");
				}
			} else if (getCommand(key).equals("se")) {
				whereBuff.append(getConnectType(key)).append(getFieldName(key)).append(" = ").append("'").append(value)
						.append("'");
			} else if (getCommand(key).equals("sl")) {
				whereBuff.append(getConnectType(key)).append(getFieldName(key)).append(" like ").append("'%")
						.append(value).append("%'");
			} else if (getCommand(key).equals("sne")) {
				whereBuff.append(getConnectType(key)).append(getFieldName(key)).append(" != ").append("'")
						.append(value).append("'");
			} else if (getCommand(key).equals("snl")) {
				whereBuff.append(getConnectType(key)).append(getFieldName(key)).append(" not like ").append("'%")
						.append(value).append("%'");
			} else if (getCommand(key).equals("snn")) {
				whereBuff.append(getConnectType(key)).append(getFieldName(key)).append(" is not null ");
			} else if (getCommand(key).equals("sn")) {
				whereBuff.append(getConnectType(key)).append(getFieldName(key)).append(" is null ");
			} else if (getCommand(key).equals("sin")) {
				whereBuff.append(getConnectType(key)).append(getFieldName(key)).append(" in (");
				String[] vals = (String[]) entity.getValue();
				for (int i = 0; i < vals.length; i++) {
					if (i != 0) {
						whereBuff.append(",");
					}
					whereBuff.append("'").append(vals[i]).append("'");

					if (i == vals.length - 1) {
						whereBuff.append(")");
					}
				}
			} else if (getCommand(key).equals("snin")) {
				whereBuff.append(getConnectType(key)).append(getFieldName(key)).append(" not in (");
				String[] vals = (String[]) entity.getValue();
				for (int i = 0; i < vals.length; i++) {
					if (i != 0) {
						whereBuff.append(",");
					}
					whereBuff.append("'").append(vals[i]).append("'");

					if (i == vals.length - 1) {
						whereBuff.append(")");
					}
				}
			} else if (getCommand(key).equals("nisn")) {
				if ("0".equals(value)) {
					whereBuff.append(getConnectType(key)).append(getFieldName(key)).append(" is null ");
				} else if ("1".equals(value)) {
					whereBuff.append(getConnectType(key)).append(getFieldName(key)).append(" is not null ");
				}
			} else if (getCommand(key).equals("ne")) {
				whereBuff.append(getConnectType(key)).append(getFieldName(key)).append(" = ").append(value);
			} else if (getCommand(key).equals("nb")) {
				whereBuff.append(getConnectType(key)).append(getFieldName(key)).append(" > ").append(value);
			} else if (getCommand(key).equals("nbe")) {
				whereBuff.append(getConnectType(key)).append(getFieldName(key)).append(" >= ").append(value);
			} else if (getCommand(key).equals("ns")) {
				whereBuff.append(getConnectType(key)).append(getFieldName(key)).append(" < ").append(value);
			} else if (getCommand(key).equals("nse")) {
				whereBuff.append(getConnectType(key)).append(getFieldName(key)).append(" <= ").append(value);
			} else if (getCommand(key).equals("nnn")) {
				whereBuff.append(getConnectType(key)).append(getFieldName(key)).append(" is not null ");
			} else if (getCommand(key).equals("nne")) {
				whereBuff.append(getConnectType(key)).append(getFieldName(key)).append(" != ").append(value);
			} else if (getCommand(key).equals("nn")) {
				whereBuff.append(getConnectType(key)).append(getFieldName(key)).append(" is null ");
			} else if (getCommand(key).equals("nin")) {
				whereBuff.append(getConnectType(key)).append(getFieldName(key)).append(" in (");
				String[] vals = (String[]) entity.getValue();
				for (int i = 0; i < vals.length; i++) {
					if (i != 0) {
						whereBuff.append(",");
					}
					whereBuff.append(vals[i]);

					if (i == vals.length - 1) {
						whereBuff.append(")");
					}
				}
			} else if (getCommand(key).equals("nnin")) {
				whereBuff.append(getConnectType(key)).append(getFieldName(key)).append(" not in (");
				String[] vals = (String[]) entity.getValue();
				for (int i = 0; i < vals.length; i++) {
					if (i != 0) {
						whereBuff.append(",");
					}
					whereBuff.append(vals[i]);

					if (i == vals.length - 1) {
						whereBuff.append(")");
					}
				}
			} else if (getCommand(key).equals("disn")) {
				if ("0".equals(value)) {
					whereBuff.append(getConnectType(key)).append(getFieldName(key)).append(" is null ");
				} else if ("1".equals(value)) {
					whereBuff.append(getConnectType(key)).append(getFieldName(key)).append(" is not null ");
				}
			} else if (getCommand(key).equals("dnn")) {
				whereBuff.append(getConnectType(key)).append(getFieldName(key)).append(" is not null ");
			} else if (getCommand(key).equals("dn")) {
				whereBuff.append(getConnectType(key)).append(getFieldName(key)).append(" is null ");
			} else if (getCommand(key).equals("dnm")) {
				whereBuff.append(getConnectType(key)).append(getFieldName(key)).append(" <= '").append(value)
						.append("'");
			} else if (getCommand(key).equals("dnl")) {
				whereBuff.append(getConnectType(key)).append(getFieldName(key)).append(" >= '").append(value)
						.append("'");
			} else if (getCommand(key).equals("de")) {
				whereBuff.append(getConnectType(key)).append(getFieldName(key)).append(" between '").append(value)
						.append("' and dateadd(day,1,'").append(value).append("')");

			} else if (getCommand(key).equals("dne")) {
				whereBuff.append(getConnectType(key)).append(getFieldName(key)).append(" != '").append(value)
						.append("'");
			}

		}

		String hql = null;
		if (whereBuff.toString().startsWith(" and ")) {// 去掉最开始的and连接符
			hql = whereBuff.substring(whereBuff.indexOf(" and") + 4, whereBuff.length());
		}

		if (hql != null) {
			return "where" + hql;
		}

		return "";
	}

	/**
	 * 获取查询连接符
	 * 
	 * @param key
	 * @return
	 */
	private static String getConnectType(String key) {
		return " and ";
	}

	/**
	 * 获取查询命令.
	 * 
	 * @param key
	 * @return
	 */
	private static String getCommand(String key) {
		return key.substring(key.indexOf("_") + 1, key.indexOf("_", 1));
	}

	/**
	 * 获取查询字段PO对应属性名
	 * 
	 * @param key
	 * @return
	 */
	private static String getFieldName(String key) {
		return key.substring(key.indexOf("_", 1) + 1, key.length());// 第二个_开始
	}
}
