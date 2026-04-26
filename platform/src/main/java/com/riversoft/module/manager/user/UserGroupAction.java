/*
 * $HeadURL: $
 * $Id: $
 * Copyright (c) 2013 by Riversoft System, all rights reserved.
 */
package com.riversoft.module.manager.user;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang3.StringUtils;

import com.riversoft.core.BeanFactory;
import com.riversoft.core.Config;
import com.riversoft.core.db.DataCondition;
import com.riversoft.core.db.DataPO;
import com.riversoft.core.db.DataPackage;
import com.riversoft.core.db.ORMService;
import com.riversoft.core.exception.ExceptionType;
import com.riversoft.core.exception.SystemRuntimeException;
import com.riversoft.core.web.Actions;
import com.riversoft.core.web.Actions.Util;
import com.riversoft.core.web.RequestUtils;
import com.riversoft.core.web.annotation.ActionAccess;
import com.riversoft.core.web.annotation.ActionAccess.SafeLevel;
import com.riversoft.platform.po.UsGroup;
import com.riversoft.platform.po.UsRole;
import com.riversoft.platform.po.UsTag;
import com.riversoft.platform.po.UsUser;
import com.riversoft.platform.script.function.UserHelper;
import com.riversoft.platform.translate.ContactMode;
import com.riversoft.util.MD5;
import com.riversoft.util.ValueConvertUtils;
import com.riversoft.util.dynamicbean.DynamicBeanUtils;
import com.riversoft.util.jackson.JsonMapper;
import com.riversoft.wx.qy.service.ContactService;

/**
 * 用户,组织,角色模型管理
 * 
 * @author woden
 * 
 */
public class UserGroupAction {

	/**
	 * 框架页
	 * 
	 * @param request
	 * @param response
	 */
	public void index(HttpServletRequest request, HttpServletResponse response) {
		Actions.includePage(request, response, Util.getPagePath(request, "main.jsp"));
	}

	/**
	 * 组织架构树
	 * 
	 * @param request
	 * @param response
	 */
	@SuppressWarnings("unchecked")
	public void tree(HttpServletRequest request, HttpServletResponse response) {
		List<UsGroup> groups = ORMService.getInstance().query(UsGroup.class.getName(), new DataCondition().setOrderByAsc("sort").toEntity());
		final String cp = Actions.Util.getContextPath(request);
		List<Map<String, Object>> treeList = new ArrayList<>();
		for (UsGroup group : groups) {
			Map<String, Object> obj = new HashMap<>();
			obj.put("icon", cp + "/css/icon/house.png");
			obj.put("busiName", group.getBusiName());
			obj.put("id", group.getGroupKey());
			obj.put("parentId", group.getParentKey());
			obj.put("groupKey", group.getGroupKey());
			obj.put("sysFlag", group.getSysFlag());
			obj.put("sort", group.getSort());
			treeList.add(obj);

			List<Map<String, Object>> relates = ORMService.getInstance().query("UsGroupRole", new DataCondition().setStringEqual("groupKey", group.getGroupKey()).toEntity());

			for (Map<String, Object> relate : relates) {
				Map<String, Object> o = new HashMap<>();
				UsRole role = (UsRole) ORMService.getInstance().findByPk(UsRole.class.getName(), (Serializable) relate.get("roleKey"));
				if (role == null) {
					continue;
				}
				o.put("icon", cp + "/css/icon/vcard.png");
				o.put("busiName", role.getBusiName());
				o.put("id", "role_" + role.getRoleKey());
				o.put("parentId", group.getGroupKey());
				o.put("roleKey", role.getRoleKey());
				o.put("sysFlag", relate.get("sysFlag"));
				o.put("sort", role.getSort() - 999);// 部门里面角色要排在组织上面
				treeList.add(o);
			}
		}

		// 排序
		Collections.sort(treeList, new Comparator<Map<String, Object>>() {
			@Override
			public int compare(Map<String, Object> o1, Map<String, Object> o2) {
				int sort1 = o1.containsKey("sort") ? (int) o1.get("sort") : -999;
				int sort2 = o2.containsKey("sort") ? (int) o2.get("sort") : -999;
				if (sort1 < sort2) {
					return -1;
				} else if (sort1 == sort2) {
					return 0;
				} else {
					return 1;
				}
			}
		});

		request.setAttribute("tree", JsonMapper.defaultMapper().toJson(treeList));
		Actions.includePage(request, response, Util.getPagePath(request, "group_tree.jsp"));
	}

	/**
	 * 保存位置
	 * 
	 * @param request
	 * @param response
	 */
	public void saveSort(HttpServletRequest request, HttpServletResponse response) {
		HashMap<String, Object> tree = RequestUtils.getJsonValue(request, "tree");
		BeanFactory.getInstance().getBean(GroupService.class).executeSaveSort((List<HashMap<String, Object>>) tree.get("nodes"));
		Actions.redirectInfoPage(request, response, "保存位置成功.");
	}

	/**
	 * 删除组织下角色
	 * 
	 * @param request
	 * @param response
	 */
	@ActionAccess(level = SafeLevel.BUSI_W)
	public void delGroupRole(HttpServletRequest request, HttpServletResponse response) {
		String groupKey = RequestUtils.getStringValue(request, "groupKey");
		String roleKey = RequestUtils.getStringValue(request, "roleKey");

		BeanFactory.getInstance().getBean(GroupService.class).executeRemoveRoleRelate(groupKey, roleKey);
		Actions.redirectInfoPage(request, response, "移除角色成功.");
	}

	/**
	 * 删除组织
	 * 
	 * @param request
	 * @param response
	 */
	@ActionAccess(level = SafeLevel.BUSI_W)
	public void delGroup(HttpServletRequest request, HttpServletResponse response) {
		String groupKey = RequestUtils.getStringValue(request, "groupKey");
		BeanFactory.getInstance().getBean(GroupService.class).executeRemoveGroup(groupKey);
		Actions.redirectInfoPage(request, response, "移除组织成功.");
	}

	/**
	 * 创建组织
	 * 
	 * @param request
	 * @param response
	 */
	public void createGroupZone(HttpServletRequest request, HttpServletResponse response) {
		Actions.includePage(request, response, Util.getPagePath(request, "group_form.jsp"));
	}

	/**
	 * 编辑组织
	 * 
	 * @param request
	 * @param response
	 */
	@SuppressWarnings("unchecked")
	public void editGroupZone(HttpServletRequest request, HttpServletResponse response) {
		String groupKey = RequestUtils.getStringValue(request, "groupKey");
		request.setAttribute("vo", ORMService.getInstance().findByPk(UsGroup.class.getName(), groupKey));
		List<Map<String, Object>> groupRoleList = ORMService.getInstance().query("UsGroupRole", new DataCondition().setStringEqual("groupKey", groupKey).toEntity());
		StringBuffer buff = new StringBuffer();
		List<String> sysList = new ArrayList<>();
		if (groupRoleList != null) {
			for (Map<String, Object> vo : groupRoleList) {
				if (((Integer) vo.get("sysFlag")) == 1) {
					sysList.add((String) vo.get("roleKey"));
				}
				buff.append(";").append(vo.get("roleKey"));
			}
		}
		request.setAttribute("sysList", sysList);
		request.setAttribute("roles", buff.length() > 0 ? buff.substring(1) : "");
		Actions.includePage(request, response, Util.getPagePath(request, "group_form.jsp"));
	}

	/**
	 * 提交保存
	 * 
	 * @param request
	 * @param response
	 */
	@ActionAccess(level = SafeLevel.BUSI_W)
	public void submitGroupForm(HttpServletRequest request, HttpServletResponse response) {
		int isCreate = RequestUtils.getIntegerValue(request, "isCreate");
		String groupKey = RequestUtils.getStringValue(request, "groupKey");
		String busiName = RequestUtils.getStringValue(request, "busiName");

		// 前置规则校验
		if (StringUtils.isEmpty(groupKey) || groupKey.equalsIgnoreCase("null")) {
			throw new SystemRuntimeException(ExceptionType.BUSINESS, "组织主键不允许使用关键字[null].");
		}

		GroupService service = BeanFactory.getInstance().getBean(GroupService.class);
		// roles在service端处理
		if (isCreate == 1) {
			UsGroup po = new UsGroup();
			po.setParentKey(null);
			po.setBusiName(busiName);
			po.setGroupKey(groupKey);
			po.setSort(999);

			List<Integer> maxes = service.queryHQL("select max(wxDepartmentId) from " + UsGroup.class.getName());
			if (maxes == null || maxes.isEmpty() || maxes.get(0) == null || maxes.get(0) == 0) {
				po.setWxDepartmentId(100);
			} else {
				po.setWxDepartmentId(maxes.get(0) + 1);
			}

			service.savePO(po);
		} else {
			UsGroup po = (UsGroup) service.findByPk(UsGroup.class.getName(), groupKey);
			po.setBusiName(busiName);
			service.updatePO(po);
		}

		Actions.redirectInfoPage(request, response, "保存组织[" + busiName + "]成功.");
	}

	/**
	 * 组织下角色设置
	 * 
	 * @param request
	 * @param response
	 */
	public void editGroupRoleZone(HttpServletRequest request, HttpServletResponse response) {
		Actions.includePage(request, response, Util.getPagePath(request, "group_role_main.jsp"));
	}

	/**
	 * 设置组织下角色权限
	 * 
	 * @param request
	 * @param response
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void editGroupRolePri(HttpServletRequest request, HttpServletResponse response) {
		String groupKey = RequestUtils.getStringValue(request, "groupKey");
		String roleKey = RequestUtils.getStringValue(request, "roleKey");

		request.setAttribute("roleKey", roleKey);
		request.setAttribute("groupKey", groupKey);

		// 权限组树
		List<Map<String, Object>> list = ORMService.getInstance().query("CmPriGroup", new DataCondition().setOrderByAsc("sort").toEntity());

		// 角色固有
		List<Map<String, Object>> roleTree = new ArrayList<>();
		List<String> roleGroupIds = ORMService.getInstance().queryHQL("select groupId from UsRolePriGroupRelate where roleKey = ?", roleKey);
		String cp = Actions.Util.getContextPath(request);
		for (Map<String, Object> node : list) {
			Map<String, Object> obj = DynamicBeanUtils.cloneMap(node);
			if (((Integer) node.get("leafFlag")).intValue() == 1) {
				obj.put("icon", cp + "/css/icon/user_key.png");
			} else {
				obj.put("icon", cp + "/css/icon/folder_key.png");
			}
			obj.put("chkDisabled", true);// 角色固有的,不允许选择
			if (roleGroupIds.contains(obj.get("groupId"))) {// 默认选中
				obj.put("checked", true);
			}
			roleTree.add(obj);
		}
		request.setAttribute("roleTree", JsonMapper.defaultMapper().toJson(roleTree));

		// 组织内特有
		List<Map<String, Object>> groupRoleTree = new ArrayList<>();
		List<String> groupRolegroupIds = ORMService.getInstance().queryHQL("select groupId from UsRoleGroupPriRelate where roleKey = ? and groupKey = ?", roleKey, groupKey);
		for (Map<String, Object> node : list) {
			Map<String, Object> obj = DynamicBeanUtils.cloneMap(node);
			if (((Integer) node.get("leafFlag")).intValue() == 1) {
				obj.put("icon", cp + "/css/icon/user_key.png");
			} else {
				obj.put("icon", cp + "/css/icon/folder_key.png");
			}
			if (roleGroupIds.contains(obj.get("groupId"))) {// 默认选中
				obj.put("chkDisabled", true);// 角色固有的,不允许选择
			} else {
				if (groupRolegroupIds.contains(obj.get("groupId"))) {// 默认选中
					obj.put("checked", true);
				}
			}
			groupRoleTree.add(obj);
		}
		request.setAttribute("groupRoleTree", JsonMapper.defaultMapper().toJson(groupRoleTree));

		Actions.includePage(request, response, Util.getPagePath(request, "group_role_form.jsp"));
	}

	/**
	 * 提交权限组关联
	 * 
	 * @param request
	 * @param response
	 */
	@ActionAccess(level = SafeLevel.BUSI_W)
	public void submitGroupRolePri(HttpServletRequest request, HttpServletResponse response) {
		String[] groupIds = RequestUtils.getStringValues(request, "groupId");
		String roleKey = RequestUtils.getStringValue(request, "roleKey");
		String groupKey = RequestUtils.getStringValue(request, "groupKey");
		GroupService service = BeanFactory.getInstance().getBean(GroupService.class);
		service.executeUpdatePriGroup(groupKey, roleKey, groupIds);
		Actions.redirectInfoPage(request, response, "分配权限成功.");
	}

	/**
	 * 分配用户
	 * 
	 * @param request
	 * @param response
	 */
	@SuppressWarnings("unchecked")
	public void setGroupRoleUser(HttpServletRequest request, HttpServletResponse response) {
		String roleKey = RequestUtils.getStringValue(request, "roleKey");
		String groupKey = RequestUtils.getStringValue(request, "groupKey");

		request.setAttribute("role", ORMService.getInstance().findByPk(UsRole.class.getName(), roleKey));
		request.setAttribute("group", ORMService.getInstance().findByPk(UsGroup.class.getName(), groupKey));

		// 关联其他组织
		List<String> groupKeys = ORMService.getInstance().queryHQL("select groupKey from UsGroupRole where roleKey = ? and groupKey != ?", roleKey, groupKey);
		List<String> otherGroupNames = new ArrayList<>();
		if (groupKeys != null) {
			for (String key : groupKeys) {
				otherGroupNames.add(UserHelper.findGroup(key).getBusiName());
			}
		}
		request.setAttribute("otherGroups", StringUtils.join(otherGroupNames, " | "));

		// 找到已分配的用户数据
		List<Map<String, Object>> list = ORMService.getInstance().query("UsUserGroupRole",
				new DataCondition().setStringEqual("roleKey", roleKey).setStringEqual("groupKey", groupKey).setOrderByAsc("sort").toEntity());
		request.setAttribute("list", list);

		Actions.includePage(request, response, Util.getPagePath(request, "group_role_user_setting.jsp"));
	}

	/**
	 * 保存用户分配
	 * 
	 * @param request
	 * @param response
	 */
	@ActionAccess(level = SafeLevel.BUSI_W)
	public void submitGroupRoleUserSetting(HttpServletRequest request, HttpServletResponse response) {
		GroupService service = BeanFactory.getInstance().getBean(GroupService.class);
		service.executeUserSetting();
		Actions.redirectInfoPage(request, response, "用户分配成功.");
	}

	/**
	 * 用户选择界面
	 * 
	 * @param request
	 * @param response
	 */
	public void selectUser(HttpServletRequest request, HttpServletResponse response) {
		Actions.includePage(request, response, Util.getPagePath(request, "user_select_main.jsp"));
	}

	/**
	 * 用户选择-列表
	 * 
	 * @param request
	 * @param response
	 */
	public void selectUserList(HttpServletRequest request, HttpServletResponse response) {
		int start = Util.getStart(request);
		int limit = Util.getLimit(request);

		// 获取排序信息
		String field = Util.getSortField(request);
		String dir = Util.getSortDir(request);

		// 查询条件
		DataCondition condition = new DataCondition(Util.buildQueryMap(new HashMap<String, Object>(), request));
		condition.setOrderBy(field, dir);

		DataPackage dp = ORMService.getInstance().queryPackage(UsUser.class.getName(), start, limit, condition.toEntity());
		request.setAttribute("dp", dp);

		Actions.includePage(request, response, Util.getPagePath(request, "user_select_list.jsp"));
	}

	/**
	 * 角色管理
	 * 
	 * @param request
	 * @param response
	 */
	public void roleManage(HttpServletRequest request, HttpServletResponse response) {
		Actions.includePage(request, response, Util.getPagePath(request, "role_main.jsp"));
	}

	/**
	 * 使用树形结构展示树
	 * 
	 * @param request
	 * @param response
	 */
	@SuppressWarnings("unchecked")
	public void roleTree(HttpServletRequest request, HttpServletResponse response) {
		List<UsRole> roles = ORMService.getInstance().query(UsRole.class.getName(), new DataCondition().setOrderByAsc("sort").toEntity());
		final String cp = Actions.Util.getContextPath(request);
		List<Map<String, Object>> treeList = new ArrayList<>();
		for (UsRole role : roles) {
			Map<String, Object> obj = new HashMap<>();
			obj.put("icon", cp + "/css/icon/vcard.png");
			obj.put("busiName", role.getBusiName());
			obj.put("roleKey", role.getRoleKey());
			obj.put("parentId", null);
			obj.put("sysFlag", role.getSysFlag());
			treeList.add(obj);
		}
		request.setAttribute("tree", JsonMapper.defaultMapper().toJson(treeList));
		Actions.includePage(request, response, Util.getPagePath(request, "role_tree.jsp"));
	}

	/**
	 * 保存角色位置
	 * 
	 * @param request
	 * @param response
	 */
	@ActionAccess(level = SafeLevel.BUSI_W)
	public void saveRoleSort(HttpServletRequest request, HttpServletResponse response) {
		HashMap<String, Object> tree = RequestUtils.getJsonValue(request, "tree");
		BeanFactory.getInstance().getBean(RoleService.class).executeSaveSort((List<HashMap<String, Object>>) tree.get("nodes"));
		Actions.redirectInfoPage(request, response, "保存位置成功.");
	}

	/**
	 * 创建角色
	 * 
	 * @param request
	 * @param response
	 */
	public void createRoleZone(HttpServletRequest request, HttpServletResponse response) {
		Actions.includePage(request, response, Util.getPagePath(request, "role_form.jsp"));
	}

	/**
	 * 编辑角色
	 * 
	 * @param request
	 * @param response
	 */
	@SuppressWarnings("unchecked")
	public void editRoleZone(HttpServletRequest request, HttpServletResponse response) {
		String roleKey = RequestUtils.getStringValue(request, "roleKey");
		request.setAttribute("vo", ORMService.getInstance().findByPk(UsRole.class.getName(), roleKey));

		// 关联其他组织
		List<String> groupKeys = ORMService.getInstance().queryHQL("select groupKey from UsGroupRole where roleKey = ?", roleKey);
		List<String> otherGroupNames = new ArrayList<>();
		if (groupKeys != null) {
			for (String key : groupKeys) {
				otherGroupNames.add(UserHelper.findGroup(key).getBusiName());
			}
		}
		request.setAttribute("otherGroups", StringUtils.join(otherGroupNames, " | "));

		Actions.includePage(request, response, Util.getPagePath(request, "role_form.jsp"));
	}

	/**
	 * 删除角色
	 * 
	 * @param request
	 * @param response
	 */
	@ActionAccess(level = SafeLevel.BUSI_W)
	public void delRole(HttpServletRequest request, HttpServletResponse response) {
		String roleKey = RequestUtils.getStringValue(request, "roleKey");
		BeanFactory.getInstance().getBean(RoleService.class).executeRemoveRole(roleKey);
		Actions.redirectInfoPage(request, response, "删除角色成功.");
	}

	/**
	 * 提交角色保存
	 * 
	 * @param request
	 * @param response
	 */
	@ActionAccess(level = SafeLevel.BUSI_W)
	public void submitRoleForm(HttpServletRequest request, HttpServletResponse response) {
		int isCreate = RequestUtils.getIntegerValue(request, "isCreate");
		String roleKey = RequestUtils.getStringValue(request, "roleKey");
		String busiName = RequestUtils.getStringValue(request, "busiName");
		RoleService service = BeanFactory.getInstance().getBean(RoleService.class);
		if (isCreate == 1) {
			UsRole po = new UsRole();
			po.setBusiName(busiName);
			po.setRoleKey(roleKey);
			po.setSort(999);
			service.savePO(po);
		} else {
			UsRole po = (UsRole) service.findByPk(UsRole.class.getName(), roleKey);
			po.setBusiName(busiName);
			service.updatePO(po);
		}
		Actions.redirectInfoPage(request, response, "保存角色[" + busiName + "]成功.");
	}

	/**
	 * 角色固有权限设置
	 * 
	 * @param request
	 * @param response
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void editRolePri(HttpServletRequest request, HttpServletResponse response) {
		String roleKey = RequestUtils.getStringValue(request, "roleKey");

		request.setAttribute("roleKey", roleKey);

		// 权限组树
		List<Map<String, Object>> list = ORMService.getInstance().query("CmPriGroup", new DataCondition().setOrderByAsc("sort").toEntity());

		// 角色固有
		List<Map<String, Object>> roleTree = new ArrayList<>();
		List<String> roleGroupIds = ORMService.getInstance().queryHQL("select groupId from UsRolePriGroupRelate where roleKey = ? and sysFlag = 0", roleKey);
		List<String> sysFlagIds = ORMService.getInstance().queryHQL("select groupId from UsRolePriGroupRelate where roleKey = ? and sysFlag = 1", roleKey);
		String cp = Actions.Util.getContextPath(request);
		for (Map<String, Object> node : list) {
			Map<String, Object> obj = DynamicBeanUtils.cloneMap(node);
			if (((Integer) node.get("leafFlag")).intValue() == 1) {
				obj.put("icon", cp + "/css/icon/user_key.png");
			} else {
				obj.put("icon", cp + "/css/icon/folder_key.png");
			}
			if (roleGroupIds.contains(obj.get("groupId"))) {// 默认选中
				obj.put("checked", true);
			}

			if (sysFlagIds.contains(obj.get("groupId"))) {
				obj.put("checked", true);
				obj.put("chkDisabled", true);// 不允许选择
			}

			roleTree.add(obj);
		}
		request.setAttribute("roleTree", JsonMapper.defaultMapper().toJson((roleTree)));

		Actions.includePage(request, response, Util.getPagePath(request, "role_pri.jsp"));
	}

	/**
	 * 提交权限组关联
	 * 
	 * @param request
	 * @param response
	 */
	@ActionAccess(level = SafeLevel.BUSI_W)
	public void submitRolePri(HttpServletRequest request, HttpServletResponse response) {
		String[] groupIds = RequestUtils.getStringValues(request, "groupId");
		String roleKey = RequestUtils.getStringValue(request, "roleKey");
		RoleService service = BeanFactory.getInstance().getBean(RoleService.class);
		service.executeUpdatePriGroup(roleKey, groupIds);
		Actions.redirectInfoPage(request, response, "分配权限成功.");
	}

	/**
	 * 用户管理
	 * 
	 * @param request
	 * @param response
	 */
	public void userManage(HttpServletRequest request, HttpServletResponse response) {
		Actions.includePage(request, response, Util.getPagePath(request, "user_main.jsp"));
	}

	/**
	 * 使用树形结构展示用户
	 * 
	 * @param request
	 * @param response
	 */
	@SuppressWarnings("unchecked")
	public void userTree(HttpServletRequest request, HttpServletResponse response) {

		final String cp = Actions.Util.getContextPath(request);
		List<Map<String, Object>> treeList = new ArrayList<>();
		int type = RequestUtils.getIntegerValue(request, "type");
		String search = RequestUtils.getStringValue(request, "search");
		List<UsUser> users;

		if (type == 1) {// 按名字分组
			if (StringUtils.isNotEmpty(search)) {
				users = ORMService.getInstance().query(UsUser.class.getName(),
						new DataCondition().addSql("uid like '%" + search + "%' or busiName like '%" + search + "%'").setOrderByAsc("sort").toEntity());
			} else {
				users = ORMService.getInstance().query(UsUser.class.getName(), new DataCondition().setOrderByAsc("sort").toEntity());
			}

			// 保存需要展示的文件夹
			Set<UserFolder> folders = new HashSet<>();
			for (UsUser user : users) {
				Map<String, Object> obj = new HashMap<>();
				obj.put("icon", cp + "/css/icon/user.png");
				obj.put("busiName", "[" + user.getUid() + "]" + user.getBusiName());
				obj.put("uid", user.getUid());
				{
					UserFolder folder = UserFolder.findFolder(user.getUid());
					if (!folders.contains(folder)) {
						folders.add(folder);
					}
					obj.put("parentId", folder.getKey());
				}
				obj.put("sysFlag", user.getSysFlag());
				treeList.add(obj);
			}

			// 设置文件夹
			for (UserFolder folder : UserFolder.values()) {
				if (!folders.contains(folder)) {
					continue;
				}

				Map<String, Object> obj = new HashMap<>();
				obj.put("busiName", folder.getBusiName());
				obj.put("uid", folder.getKey());
				obj.put("parentId", null);
				obj.put("sysFlag", 1);
				treeList.add(obj);
			}
		} else {// 平铺排序
			users = ORMService.getInstance().query(UsUser.class.getName(), new DataCondition().setOrderByAsc("sort").toEntity());
			for (UsUser user : users) {
				Map<String, Object> obj = new HashMap<>();
				obj.put("icon", cp + "/css/icon/user.png");
				obj.put("busiName", user.getBusiName());
				obj.put("uid", user.getUid());
				obj.put("parentId", null);
				obj.put("sysFlag", user.getSysFlag());
				treeList.add(obj);
			}
		}
		request.setAttribute("type", type);
		request.setAttribute("tree", JsonMapper.defaultMapper().toJson(treeList));
		Actions.includePage(request, response, Util.getPagePath(request, "user_tree.jsp"));
	}

	/**
	 * 保存角色位置
	 * 
	 * @param request
	 * @param response
	 */
	@ActionAccess(level = SafeLevel.BUSI_W)
	public void saveUserSort(HttpServletRequest request, HttpServletResponse response) {
		HashMap<String, Object> tree = RequestUtils.getJsonValue(request, "tree");
		BeanFactory.getInstance().getBean(UserService.class).executeSaveSort((List<HashMap<String, Object>>) tree.get("nodes"));
		Actions.redirectInfoPage(request, response, "重新排序成功.");
	}

	/**
	 * 创建用户
	 * 
	 * @param request
	 * @param response
	 */
	public void createUserZone(HttpServletRequest request, HttpServletResponse response) {
		Actions.includePage(request, response, Util.getPagePath(request, "user_form.jsp"));
	}

	/**
	 * 编辑用户
	 * 
	 * @param request
	 * @param response
	 */
	public void editUserZone(HttpServletRequest request, HttpServletResponse response) {
		String uid = RequestUtils.getStringValue(request, "uid");
		request.setAttribute("vo", ORMService.getInstance().findByPk(UsUser.class.getName(), uid));
		// tags
		List<String> tagKes = ORMService.getInstance().queryHQL("select tagKey from UsUserTag where uid = ?", uid);
		request.setAttribute("tags", StringUtils.join(tagKes, ";"));
		Actions.includePage(request, response, Util.getPagePath(request, "user_form.jsp"));
	}

	/**
	 * 删除用户
	 * 
	 * @param request
	 * @param response
	 */
	@ActionAccess(level = SafeLevel.BUSI_W)
	public void delUser(HttpServletRequest request, HttpServletResponse response) {
		String uid = RequestUtils.getStringValue(request, "uid");
		BeanFactory.getInstance().getBean(UserService.class).executeRemoveUser(uid);
		Actions.redirectInfoPage(request, response, "删除用户成功.");
	}

	/**
	 * 提交角色保存
	 * 
	 * @param request
	 * @param response
	 */
	@ActionAccess(level = SafeLevel.BUSI_W)
	public void submitUserForm(HttpServletRequest request, HttpServletResponse response) {
		int isCreate = RequestUtils.getIntegerValue(request, "isCreate");
		String uid = RequestUtils.getStringValue(request, "uid");
		String busiName = RequestUtils.getStringValue(request, "busiName");
		String password = RequestUtils.getStringValue(request, "password");
		Integer activeFlag = RequestUtils.getIntegerValue(request, "activeFlag");
		String allowIp = RequestUtils.getStringValue(request, "allowIp");
		Integer selectFlag = RequestUtils.getIntegerValue(request, "selectFlag");
		String mail = RequestUtils.getStringValue(request, "mail");
		String mobile = RequestUtils.getStringValue(request, "mobile");
		String wxid = RequestUtils.getStringValue(request, "wxid");
		Integer wxEnable = RequestUtils.getIntegerValue(request, "wxEnable");
		String[] msgType = RequestUtils.getStringValues(request, "msgType");
		String[] receiveType = RequestUtils.getStringValues(request, "receiveType");
		String[] tags = RequestUtils.getStringValues(request, "tags");// 标签

		UserService service = BeanFactory.getInstance().getBean(UserService.class);
		if (isCreate == 1) {
			UsUser po = new UsUser();
			po.setBusiName(busiName);
			po.setActiveFlag(activeFlag);
			po.setPassword(MD5.md5(password));
			po.setSysFlag(0);
			po.setSelectFlag(selectFlag);
			po.setUid(uid);
			po.setSort(999);
			po.setEffDate(ValueConvertUtils.convert("1900-01-01", Date.class));
			po.setEndDate(ValueConvertUtils.convert("2099-12-31", Date.class));
			po.setAllowIp(allowIp);
			po.setMail(mail);
			if (StringUtils.isNotEmpty(wxid)) {
				if ((Long) ORMService.getInstance().findHQL("select count(1) from " + UsUser.class.getName() + " where wxid = ?", wxid) > 0) {
					throw new SystemRuntimeException(ExceptionType.BUSINESS, "微信[" + wxid + "]已被绑定,无法创建用户.");
				}
			}
			po.setWxid(wxid);
			po.setWxEnable(wxEnable);
			po.setMobile(mobile);
			po.setMsgType(StringUtils.join(msgType));
			po.setReceiveType(StringUtils.join(receiveType, ";"));
			service.executeSaveUser(po, tags);
		} else {
			UsUser po = (UsUser) service.findByPk(UsUser.class.getName(), uid);
			po.setBusiName(busiName);
			po.setActiveFlag(activeFlag);
			po.setSelectFlag(selectFlag);
			po.setAllowIp(allowIp);
			po.setMail(mail);
			po.setWxid(wxid);
			po.setWxEnable(wxEnable);
			po.setMobile(mobile);
			po.setMsgType(StringUtils.join(msgType, ";"));
			po.setReceiveType(StringUtils.join(receiveType, ";"));
			if (StringUtils.isNotEmpty(password)) {
				po.setPassword(MD5.md5(password));
			}
			service.executeUpdateUser(po, tags);
		}
		Actions.redirectInfoPage(request, response, "保存用户[" + busiName + "]成功.");
	}

	/**
	 * 用户角色管理
	 * 
	 * @param request
	 * @param response
	 */
	public void userRoleGroupSetting(HttpServletRequest request, HttpServletResponse response) {
		String uid = RequestUtils.getStringValue(request, "uid");
		request.setAttribute("list", ORMService.getInstance().query("UsUserGroupRole", new DataCondition().setStringEqual("uid", uid).setOrderByAsc("sort").toEntity()));
		request.setAttribute("uid", uid);
		Actions.includePage(request, response, Util.getPagePath(request, "user_role_setting.jsp"));
	}

	/**
	 * 提交角色分配方案
	 * 
	 * @param request
	 * @param response
	 */
	@ActionAccess(level = SafeLevel.BUSI_W)
	public void submitUserRoleSetting(HttpServletRequest request, HttpServletResponse response) {
		UserService service = BeanFactory.getInstance().getBean(UserService.class);
		service.executeUpdateGroupRoleRelate();
		Actions.redirectInfoPage(request, response, "角色分配成功.");
	}

	/**
	 * 同步微信通信录状态
	 * 
	 * @param request
	 * @param response
	 */
	public void syncGroup(HttpServletRequest request, HttpServletResponse response) {
		ContactService.getInstance().executeSync();

		ContactMode mode = ContactMode.fromCode(Integer.valueOf(Config.get("wx.qy.contactmode", "0")));
		if (mode == ContactMode.all) {
			Actions.redirectInfoPage(request, response, "组织架构已同步至微信企业号.");
		} else {
			Actions.redirectInfoPage(request, response, "微信端组织架构采用非托管模式,仅增量同步用户信息.");
		}
	}

	/**
	 * 标签管理
	 * 
	 * @param request
	 * @param response
	 */
	public void tagManage(HttpServletRequest request, HttpServletResponse response) {
		Actions.includePage(request, response, Util.getPagePath(request, "tag_main.jsp"));
	}

	/**
	 * 创建标签
	 * 
	 * @param request
	 * @param response
	 */
	public void createTagZone(HttpServletRequest request, HttpServletResponse response) {
		Actions.includePage(request, response, Util.getPagePath(request, "tag_form.jsp"));
	}

	/**
	 * 编辑标签
	 * 
	 * @param request
	 * @param response
	 */
	public void editTagZone(HttpServletRequest request, HttpServletResponse response) {
		String tagKey = RequestUtils.getStringValue(request, "tagKey");
		request.setAttribute("vo", ORMService.getInstance().findByPk(UsTag.class.getName(), tagKey));

		// 关联其他组织
		List<String> uids = ORMService.getInstance().queryHQL("select uid from UsUserTag where tagKey = ? order by uid asc", tagKey);
		// 找到已分配的用户数据
		List<Map<String, Object>> list = new ArrayList<>();
		if (uids != null) {
			for (String uid : uids) {
				List<Map<String, Object>> tempList = ORMService.getInstance().query("UsUserGroupRole", new DataCondition().setStringEqual("uid", uid)
						.setNumberEqual("defaultFlag", "1").setOrderByAsc("sort").toEntity());
				list.addAll(tempList);
			}
		}
		request.setAttribute("list", list);
		Actions.includePage(request, response, Util.getPagePath(request, "tag_form.jsp"));
	}

	/**
	 * 删除标签
	 * 
	 * @param request
	 * @param response
	 */
	public void delTag(HttpServletRequest request, HttpServletResponse response) {
		String tagKey = RequestUtils.getStringValue(request, "tagKey");
		BeanFactory.getInstance().getBean(TagService.class).executeRemove(tagKey);
		Actions.redirectInfoPage(request, response, "删除标签成功.");
	}

	/**
	 * 标签树
	 * 
	 * @param request
	 * @param response
	 */
	public void tagTree(HttpServletRequest request, HttpServletResponse response) {
		List<UsTag> tags = ORMService.getInstance().query(UsTag.class.getName(), new DataCondition().setOrderByAsc("busiName").toEntity());
		final String cp = Actions.Util.getContextPath(request);
		List<Map<String, Object>> treeList = new ArrayList<>();
		for (UsTag tag : tags) {
			Map<String, Object> obj = new HashMap<>();
			obj.put("icon", cp + "/css/icon/tag.png");
			obj.put("busiName", tag.getBusiName());
			obj.put("tagKey", tag.getTagKey());
			obj.put("parentId", null);
			obj.put("wxTagId", tag.getWxTagId());
			treeList.add(obj);
		}
		request.setAttribute("tree", JsonMapper.defaultMapper().toJson(treeList));
		Actions.includePage(request, response, Util.getPagePath(request, "tag_tree.jsp"));
	}

	/**
	 * 提交标签保存
	 * 
	 * @param request
	 * @param response
	 */
	@ActionAccess(level = SafeLevel.BUSI_W)
	public void submitTagForm(HttpServletRequest request, HttpServletResponse response) {
		int isCreate = RequestUtils.getIntegerValue(request, "isCreate");
		String tagKey = RequestUtils.getStringValue(request, "tagKey");
		String busiName = RequestUtils.getStringValue(request, "busiName");
		String[] uids = RequestUtils.getStringValues(request, "uid");

		TagService service = BeanFactory.getInstance().getBean(TagService.class);
		if (isCreate == 1) {
			UsTag po = new UsTag();
			po.setBusiName(busiName);
			po.setTagKey(tagKey);
			service.savePO(po);
		} else {
			UsTag po = (UsTag) service.findByPk(UsTag.class.getName(), tagKey);
			po.setBusiName(busiName);
			service.updatePO(po);
		}
		ORMService.getInstance().executeHQL("delete from UsUserTag where tagKey = ?", tagKey); // 删除相关用户
		if (uids != null) {
			for (String uid : uids) {
				Date date = new Date();
				DataPO po = new DataPO("UsUserTag");
				po.set("tagKey", tagKey);
				po.set("uid", uid);
				po.set("createDate", date);
				po.set("updateDate", date);
				ORMService.getInstance().save(po.toEntity());
			}
		}

		Actions.redirectInfoPage(request, response, "保存标签[" + busiName + "]成功.");
	}

	/**
	 * 同步覆盖微信企业号标签
	 * 
	 * @param request
	 * @param response
	 */
	public void coverTag(HttpServletRequest request, HttpServletResponse response) {
		boolean coverType = false;
		coverType = TagService.getInstance().coverTag();
		if (coverType) {
			Actions.redirectInfoPage(request, response, "标签已全面覆盖进企业号");
		} else {
			Actions.redirectInfoPage(request, response, "标签覆盖失败!");
		}
	}

	/**
	 * 同步更新微信企业号标签
	 * 
	 * @param request
	 * @param response
	 */
	public void upgradeTag(HttpServletRequest request, HttpServletResponse response) {
		boolean upgradeType = false;
		upgradeType = TagService.getInstance().updateTag();
		if (upgradeType) {
			Actions.redirectInfoPage(request, response, "标签已成功更新");
		} else {
			Actions.redirectInfoPage(request, response, "标签更新失败!");
		}
	}

}
