/*
 * $HeadURL: $
 * $Id: $
 * Copyright (c) 2013 by Riversoft System, all rights reserved.
 */
package com.riversoft.module.widget;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;

import com.riversoft.core.db.DataCondition;
import com.riversoft.core.db.DataPackage;
import com.riversoft.core.db.JdbcService;
import com.riversoft.core.db.ORMService;
import com.riversoft.core.web.Actions;
import com.riversoft.core.web.Actions.Keys;
import com.riversoft.core.web.Actions.Util;
import com.riversoft.core.web.RequestUtils;
import com.riversoft.platform.SessionManager;
import com.riversoft.platform.po.UsGroup;
import com.riversoft.platform.po.UsUser;
import com.riversoft.platform.script.function.UserHelper;
import com.riversoft.util.jackson.JsonMapper;

/**
 * 用户选择控件
 * 
 * @author woden
 * 
 */
public class UserAction {

	/**
	 * 构建树
	 * 
	 * @param dest
	 * @param src
	 * @param root
	 */
	private void buildTree(List<UsGroup> dest, List<UsGroup> src, String root) {
		for (UsGroup group : src) {
			if (root.equals(group.getParentKey())) {
				buildTree(dest, src, group.getGroupKey());
			} else if (root.equals(group.getGroupKey())) {
				dest.add(group);
			}
		}
	}

	/**
	 * 用户内页
	 * 
	 * @param request
	 * @param response
	 */
	public void userMain(HttpServletRequest request, HttpServletResponse response) {
		String values = RequestUtils.getStringValue(request, "value");
		HashMap<String, Object> param = RequestUtils.getJsonValue(request, Keys.PARAMS.toString());
		List<?> list;
		if (StringUtils.isNotEmpty(values)) {
			list = ORMService.getInstance().query(UsUser.class.getName(), new DataCondition().setStringIn("uid", StringUtils.split(values, ";")).toEntity());
		} else if (param != null && param.containsKey("val")) {
			Object val = param.get("val");
			if (val instanceof String) {
				list = ORMService.getInstance().query(UsUser.class.getName(), new DataCondition().setStringEqual("uid", (String) val).toEntity());
			} else if (val instanceof List) {
				list = ORMService.getInstance().query(UsUser.class.getName(), new DataCondition().setStringIn("uid", ((List<?>) val).toArray(new String[0])).toEntity());
			} else {
				list = new ArrayList<>();
			}
		} else {
			list = new ArrayList<>();
		}

		request.setAttribute("list", list);
		Actions.includePage(request, response, Util.getPagePath(request, "user_widget.jsp"));
	}

	/**
	 * 用户选择
	 * 
	 * @param request
	 * @param response
	 */
	@SuppressWarnings("unchecked")
	public void userSelect(HttpServletRequest request, HttpServletResponse response) {

		String[] values = RequestUtils.getStringValues(request, "value");
		HashMap<String, Object> param = RequestUtils.getJsonValue(request, Keys.PARAMS.toString());

		StringBuffer hql = new StringBuffer("from " + UsGroup.class.getName() + " where 1 = 1");
		List<UsGroup> list = ORMService.getInstance().queryHQL(hql.toString());
		List<UsGroup> result = new ArrayList<>();

		boolean groupFlag = true;// 是否展示group查询
		String rootGroupKey = null;// 组织根路径
		boolean queryFlag = true;// 是否展示查询
		boolean mygroupFlag = true;// 我的组织
		boolean myselfFlag = true;// 我自己

		if (param != null) {
			if (param.containsKey("group")) {
				if (param.containsKey("group") && !(Boolean) param.get("group")) {// 开关
					groupFlag = false;
				}
			}
			if (param.containsKey("root")) {
				rootGroupKey = (String) param.get("root");
			}

			if (param.containsKey("query")) {
				if (param.containsKey("query") && !(Boolean) param.get("query")) {// 开关
					queryFlag = false;
				}
			}

			if (param.containsKey("mygroup")) {
				mygroupFlag = (Boolean) param.get("mygroup");
			}

			if (param.containsKey("myself")) {
				myselfFlag = (Boolean) param.get("myself");
			}
		}

		if (StringUtils.isNotEmpty(rootGroupKey)) {
			buildTree(result, list, rootGroupKey);
		} else {
			result.addAll(list);
		}

		request.setAttribute("queryFlag", queryFlag);
		request.setAttribute("groupFlag", groupFlag);
		request.setAttribute("mygroupFlag", mygroupFlag);
		request.setAttribute("myselfFlag", myselfFlag);

		Collections.sort(result, new Comparator<UsGroup>() {
			@Override
			public int compare(UsGroup o1, UsGroup o2) {
				if (o1.getSort() < o2.getSort()) {
					return -1;
				} else if (o1.getSort() > o2.getSort()) {
					return 1;
				} else {
					return 0;
				}
			}
		});

		request.setAttribute("value", values != null ? StringUtils.join(values, ";") : "");
		request.setAttribute("tree", result);
		request.setAttribute("user", SessionManager.getUser());
		request.setAttribute("group", SessionManager.getGroup());

		Actions.includePage(request, response, Util.getPagePath(request, "user.jsp"));
	}

	/**
	 * 用户普通查询
	 * 
	 * @param request
	 * @param response
	 */
	public void userList(HttpServletRequest request, HttpServletResponse response) {
		String value = RequestUtils.getStringValue(request, "value");
		HashMap<String, Object> param = RequestUtils.getJsonValue(request, Keys.PARAMS.toString());

		int start = Util.getStart(request);
		int limit = Util.getLimit(request);

		String field = Util.getSortField(request);
		String dir = Util.getSortDir(request);

		DataCondition condition = new DataCondition(Util.buildQueryMap(new HashMap<String, Object>(), request));
		if (StringUtils.isEmpty(field)) {
			field = "sort";
		}
		condition.setOrderBy(field, dir);

		if (param != null) {
			if (param.containsKey("black")) {// 黑名单
				try {
					List<String> fields = new ArrayList<>();
					List<Object> blacks = (List<Object>) param.get("black");
					for (Object o : blacks) {
						if (o instanceof Map) {
							fields.add(JsonMapper.defaultMapper().toJson(o));
						} else if (o instanceof String) {
							fields.add((String) o);
						}
					}
					condition.setStringNotIn("uid", fields.toArray(new String[0]));
				} catch (Exception e) {
					condition.setStringNotEqual("uid", param.get("black").toString());
				}
			}
			if (param.containsKey("white")) {// 白名单
				try {
					List<String> fields = new ArrayList<>();
					List<Object> whites = (List<Object>) param.get("white");
					for (Object o : whites) {
						if (o instanceof Map) {
							fields.add(JsonMapper.defaultMapper().toJson(o));
						} else if (o instanceof String) {
							fields.add((String) o);
						}
					}
					condition.setStringIn("uid", fields.toArray(new String[0]));
				} catch (Exception e) {
					condition.setStringEqual("uid", param.get("white").toString());
				}
			}
			if (param.containsKey("root")) {// 锁定根
				Collection<String> groupKeys = UserHelper.listSubGroupKey(param.get("root").toString());
				List<String> uids = ORMService.getInstance().queryHQL("select distinct uid from UsUserGroupRole where groupKey in (:list)", new ORMService.QueryVO("list", groupKeys));
				if (uids != null && uids.size() > 0) {
					condition.addSql("uid in ('" + StringUtils.join(uids, "','") + "')");
				}
			}
		}

		condition.setNumberEqual("selectFlag", "1");// 只查到允许查询的
		DataPackage dp = ORMService.getInstance().queryPackage(UsUser.class.getName(), start, limit, condition.toEntity());
		request.setAttribute("dp", dp);
		request.setAttribute("values", StringUtils.isNotEmpty(value) ? StringUtils.split(value, ";") : null);
		Actions.includePage(request, response, Util.getPagePath(request, "user_list.jsp"));
	}

	/**
	 * 用户选择列表
	 * 
	 * @param request
	 * @param response
	 */
	public void userGroupList(HttpServletRequest request, HttpServletResponse response) {
		HashMap<String, Object> param = RequestUtils.getJsonValue(request, Keys.PARAMS.toString());
		String value = RequestUtils.getStringValue(request, "value");
		String groupKey = RequestUtils.getStringValue(request, "groupKey");

		int start = Util.getStart(request);
		int limit = Util.getLimit(request);

		String field = Util.getSortField(request);
		String dir = Util.getSortDir(request);

		// 组织key
		String sql = "select a.*,b.BUSI_NAME as ROLE_NAME,c.BUSI_NAME as USER_NAME from US_USER_GROUP_ROLE a left join US_ROLE b on a.ROLE_KEY = b.ROLE_KEY left join US_USER c on c.USER_ID = a.USER_ID where 1=1";
		sql += " and c.SELECT_FLAG = 1";// 限制不可查
		UsGroup group = SessionManager.getGroup();
		if (StringUtils.isNotEmpty(groupKey)) {
			if ("$mygroup".equals(groupKey)) {// 我的组织
				sql += " and a.GROUP_KEY = '" + SessionManager.getGroup().getGroupKey() + "'";
			} else if ("$myself".equals(groupKey)) {// 我自己
				sql += " and a.GROUP_KEY = '" + SessionManager.getGroup().getGroupKey() + "' and a.USER_ID = '" + SessionManager.getUser().getUid() + "'";
			} else {
				sql += " and a.GROUP_KEY = '" + groupKey + "'";
				group = (UsGroup) ORMService.getInstance().findByPk(UsGroup.class.getName(), groupKey);
			}

			// 黑白名单
			if (param != null) {
				if (param.containsKey("black")) {// 黑名单
					try {
						List<String> fields = new ArrayList<>();
						List<Object> blacks = (List<Object>) param.get("black");
						for (Object o : blacks) {
							if (o instanceof String) {
								fields.add((String) o);
							}
						}
						if (fields.size() > 0) {
							sql += " and a.USER_ID not in ('" + StringUtils.join(fields, "','") + "')";
						}
					} catch (Exception e) {
						sql += " and a.USER_ID != '" + param.get("black").toString() + "'";
					}
				}
				if (param.containsKey("white")) {// 白名单
					try {
						List<String> fields = new ArrayList<>();
						List<Object> whites = (List<Object>) param.get("white");
						for (Object o : whites) {
							if (o instanceof String) {
								fields.add((String) o);
							}
						}
						if (fields.size() > 0) {
							sql += " and a.USER_ID in ('" + StringUtils.join(fields, "','") + "')";
						}
					} catch (Exception e) {
						sql += " and a.USER_ID = '" + param.get("white").toString() + "'";
					}
				}
				if (param.containsKey("root")) {// 锁定根
					Collection<String> groupKeys = UserHelper.listSubGroupKey(param.get("root").toString());
					sql += " and a.GROUP_KEY in ('" + StringUtils.join(groupKeys, "','") + "')";
				}
			}
		} else {
			sql += " and 1=2";// 强制查询不到结果
			group = null;
		}
		if (StringUtils.isEmpty(field)) {
			field = "b.SORT";
			dir = "asc";
		}
		sql += " order by " + field + " " + dir;

		DataPackage dp = JdbcService.getInstance().querySQLPackage(sql, start, limit);
		request.setAttribute("group", group);
		request.setAttribute("dp", dp);
		request.setAttribute("values", StringUtils.isNotEmpty(value) ? StringUtils.split(value, ";") : null);
		Actions.includePage(request, response, Util.getPagePath(request, "user_group_list.jsp"));
	}

	/**
	 * 组织内页
	 * 
	 * @param request
	 * @param response
	 */
	public void groupMain(HttpServletRequest request, HttpServletResponse response) {
		String values = RequestUtils.getStringValue(request, "value");

		HashMap<String, Object> param = RequestUtils.getJsonValue(request, Keys.PARAMS.toString());
		List<?> list;
		if (StringUtils.isNotEmpty(values)) {
			list = ORMService.getInstance().query(UsGroup.class.getName(), new DataCondition().setStringIn("groupKey", StringUtils.split(values, ";")).toEntity());
		} else if (param != null && param.containsKey("val")) {
			Object val = param.get("val");
			if (val instanceof String) {
				list = ORMService.getInstance().query(UsGroup.class.getName(), new DataCondition().setStringEqual("groupKey", (String) val).toEntity());
			} else if (val instanceof List) {
				list = ORMService.getInstance().query(UsGroup.class.getName(), new DataCondition().setStringIn("groupKey", ((List<?>) val).toArray(new String[0])).toEntity());
			} else {
				list = new ArrayList<>();
			}
		} else {
			list = new ArrayList<>();
		}
		request.setAttribute("list", list);
		Actions.includePage(request, response, Util.getPagePath(request, "group_widget.jsp"));
	}

	/**
	 * 组织选择
	 * 
	 * @param request
	 * @param response
	 */
	@SuppressWarnings("unchecked")
	public void groupSelect(HttpServletRequest request, HttpServletResponse response) {
		// 获取group
		HashMap<String, Object> param = RequestUtils.getJsonValue(request, Keys.PARAMS.toString());
		String[] values = RequestUtils.getStringValues(request, "value");

		StringBuffer hql = new StringBuffer("from " + UsGroup.class.getName() + " where 1 = 1");
		List<UsGroup> list = ORMService.getInstance().queryHQL(hql.toString());
		List<UsGroup> result = new ArrayList<>();
		String rootGroupKey = null;
		if (param != null && param.containsKey("root")) {
			rootGroupKey = (String) param.get("root");
		}
		if (StringUtils.isNotEmpty(rootGroupKey)) {
			buildTree(result, list, rootGroupKey);
		} else {
			result.addAll(list);
		}

		Collections.sort(result, new Comparator<UsGroup>() {
			@Override
			public int compare(UsGroup o1, UsGroup o2) {
				if (o1.getSort() < o2.getSort()) {
					return -1;
				} else if (o1.getSort() > o2.getSort()) {
					return 1;
				} else {
					return 0;
				}
			}
		});

		request.setAttribute("values", values);
		request.setAttribute("tree", result);
		Actions.includePage(request, response, Util.getPagePath(request, "group.jsp"));
	}
}
