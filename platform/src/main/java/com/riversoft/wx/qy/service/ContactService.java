package com.riversoft.wx.qy.service;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import com.google.common.io.Files;
import com.riversoft.core.BeanFactory;
import com.riversoft.core.Config;
import com.riversoft.core.db.DataCondition;
import com.riversoft.core.db.ORMService;
import com.riversoft.platform.po.UsGroup;
import com.riversoft.platform.po.UsRole;
import com.riversoft.platform.po.UsUser;
import com.riversoft.platform.translate.ContactMode;
import com.riversoft.platform.translate.WxStatus;
import com.riversoft.util.jackson.JsonMapper;
import com.riversoft.weixin.qy.contact.Departments;
import com.riversoft.weixin.qy.contact.Jobs;
import com.riversoft.weixin.qy.contact.Users;
import com.riversoft.weixin.qy.contact.department.Department;
import com.riversoft.weixin.qy.contact.job.JobResult;
import com.riversoft.weixin.qy.contact.user.CreateUser;
import com.riversoft.weixin.qy.contact.user.ReadUser;
import com.riversoft.weixin.qy.contact.user.SimpleUser;
import com.riversoft.weixin.qy.contact.user.UserStatus;

/**
 * Created by exizhai on 9/5/2015.
 */
public class ContactService {

	private final CsvSchema DEPARTMENT_SCHEMA = new CsvSchema.Builder().addColumn("name").addColumn("id").addColumn("parentid").addColumn("order").build();
	private final CsvSchema USER_SCHEMA = new CsvSchema.Builder().addColumn("name").addColumn("userid").addColumn("weixinid").addColumn("mobile").addColumn("email").addArrayColumn("department")
			.addColumn("position").build();
	private final CsvMapper csvMapper = new CsvMapper();
	private Logger logger = LoggerFactory.getLogger(ContactService.class);

	private List<Department> backupDepartments = null;
	private List<ReadUser> backupUsers = null;

	public static ContactService getInstance() {
		return BeanFactory.getInstance().getSingleBean(ContactService.class);
	}

	/**
	 * 同步组织架构+用户+标签
	 *
	 * @return
	 */
	private boolean syncAll() {
		backup();

		boolean result = false;
		if (syncGroups()) {
			result = syncUsers(true);
		}

		if (!result) {
			rollback();
		}
		return result;
	}

	/**
	 * 备份微信侧数据以免需要回滚
	 */
	private void backup() {
		logger.info("备份微信侧数据");
		backupGroups();
		backupUsers();
	}

	private void backupUsers() {
		logger.info("备份用户");
		backupUsers = Users.defaultUsers().list();
	}

	private void backupGroups() {
		logger.info("备份部门");
		backupDepartments = Departments.defaultDepartments().list();
	}

	private void rollback() {
		logger.info("准备回滚");
		rollbackGroups();
		rollbackUsers();
	}

	private List<String> toUsers(List<SimpleUser> users) {
		List<String> list = new ArrayList<>();
		if (users == null || users.isEmpty()) {
		} else {
			for (SimpleUser simpleUser : users) {
				list.add(simpleUser.getUserId());
			}
		}
		return list;
	}

	private void rollbackUsers() {
		logger.info("回滚用户");
		List<CreateUser> createUsers = new ArrayList<>();
		for (ReadUser readUser : backupUsers) {
			CreateUser createUser = new CreateUser();
			BeanUtils.copyProperties(readUser, createUser);
			createUsers.add(createUser);
		}
		push2WX(true, createUsers);
	}

	private void rollbackGroups() {
		logger.info("回滚部门");
		push2WX(backupDepartments);
	}

	/**
	 * 组织同步
	 *
	 * @return
	 */
	private boolean syncGroups() {
		executeWxDepartmentIdValidation();
		List<UsGroup> groupList = ORMService.getInstance().queryAll(UsGroup.class.getName());
		List<Department> departments = toWxDepartments(groupList);
		logger.info("{}个组织需要同步.", departments.size());

		return push2WX(departments);
	}

	private boolean push2WX(List<Department> departments) {
		boolean success = false;
		File tmpDir = Files.createTempDir();
		File groups = new File(tmpDir, "group.csv");

		PrintWriter groupPrintWriter = null;
		try {
			groupPrintWriter = new PrintWriter(new BufferedWriter(new FileWriter(groups, false)));
		} catch (IOException e) {
			logger.error("准备临时文件失败:", e);
			return false;
		}

		groupPrintWriter.append("部门名称,部门ID,父部门ID,排序").append("\n");

		try {
			for (Department department : departments) {
				groupPrintWriter.append(csv(department));
			}
			groupPrintWriter.close();

			logger.info("要同步的部门文件:\n {}", FileUtils.readFileToString(groups));

			String job = Jobs.defaultJobs().replaceDepartments(groups);
			logger.info("创建异步任务[同步组织][{}].", job);

			while (true) {
				JobResult jobResult = Jobs.defaultJobs().getResult(job);
				if (3 == jobResult.getStatus()) {
					logger.info("异步任务[同步组织]处理完成.");
					if (100 == jobResult.getPercentage()) {
						success = true;
					}
					break;
				} else {
					logger.info("异步任务[同步组织]已处理:{}%, ETA:{}", jobResult.getPercentage(), jobResult.getRemainTime());
					try {
						Thread.sleep(500);
					} catch (InterruptedException e) {
					}
				}
			}
		} catch (Exception e) {
			logger.error("同步组织失败.", e);
		} finally {
			try {
				FileUtils.forceDelete(tmpDir);
			} catch (IOException e) {
				logger.error("清理文件失败:", e);
			}
		}
		return success;
	}

	private List<Department> toWxDepartments(List<UsGroup> groupList) {
		List<Department> departs = new ArrayList<>();
		for (UsGroup group : groupList) {
			if (group.getWxDepartmentId() != null) {
				Department department = new Department();
				department.setName(group.getBusiName());
				department.setOrder(group.getSort());
				department.setId(group.getWxDepartmentId());

				if (StringUtils.isNotEmpty(group.getParentKey())) {
					UsGroup parent = (UsGroup) ORMService.getInstance().findByPk(UsGroup.class.getName(), group.getParentKey());
					if (parent != null) {
						department.setParentId(parent.getWxDepartmentId());
					}
				} else {
					department.setParentId(1);
				}
				departs.add(department);
			}
		}

		return departs;
	}

	private void executeWxDepartmentIdValidation() {
		List<UsGroup> groupList = ORMService.getInstance().queryAll(UsGroup.class.getName());
		for (UsGroup group : groupList) {
			if (group.getWxDepartmentId() == null) {
				List<Integer> maxes = ORMService.getInstance().queryHQL("select max(wxDepartmentId) from " + UsGroup.class.getName());
				if (maxes == null || maxes.isEmpty() || maxes.get(0) == null || maxes.get(0) == 0) {
					group.setWxDepartmentId(100);
				} else {
					group.setWxDepartmentId(maxes.get(0) + 1);
				}

				ORMService.getInstance().updatePO(group);
			}
		}
	}

	/**
	 * 用户同步
	 *
	 * @param replace
	 *            是否覆盖
	 * @return
	 */
	private boolean syncUsers(boolean replace) {
		List<UsUser> userList = ORMService.getInstance().queryAll(UsUser.class.getName());
		List<CreateUser> allUsers = toWxUser(userList);

		logger.info("{}个用户需要同步.", allUsers.size());
		return push2WX(replace, allUsers);
	}

	private boolean push2WX(boolean replace, List<CreateUser> allUsers) {
		boolean success = false;
		File tmpDir = Files.createTempDir();
		File users = new File(tmpDir, "users.csv");

		PrintWriter userPrintWriter = null;
		try {
			userPrintWriter = new PrintWriter(new BufferedWriter(new FileWriter(users, false)));
		} catch (IOException e) {
			logger.error("准备临时文件[用户]失败:", e);
			return false;
		}

		userPrintWriter.append("姓名,帐号,微信号,手机号,邮箱,所在部门,职位").append("\n");
		try {

			for (CreateUser user : allUsers) {
				userPrintWriter.append(csv(user));
			}
			userPrintWriter.close();

			logger.info("要同步的用户文件:\n {}", FileUtils.readFileToString(users));

			String job;
			if (replace) {
				job = Jobs.defaultJobs().replaceUsers(users);
			} else {
				job = Jobs.defaultJobs().syncUsers(users);
			}
			logger.info("创建异步任务[同步用户][{}].", job);

			while (true) {
				JobResult jobResult = Jobs.defaultJobs().getResult(job);
				if (3 == jobResult.getStatus()) {
					logger.info("异步任务[同步用户]处理完成.");
					if (100 == jobResult.getPercentage()) {
						success = true;
					}
					break;
				} else {
					logger.info("异步任务[同步用户]已处理:{}%, ETA:{}", jobResult.getPercentage(), jobResult.getRemainTime());
					try {
						Thread.sleep(500);
					} catch (InterruptedException e) {
					}
				}
			}
		} catch (Exception e) {
			logger.error("同步用户失败.", e);
		} finally {
			try {
				FileUtils.forceDelete(tmpDir);
			} catch (IOException e) {
				logger.warn("删除文件失败:", e);
			}
		}
		return success;
	}

	private List<CreateUser> toWxUser(List<UsUser> userList) {
		List<CreateUser> wxUsers = new ArrayList<>();

		for (UsUser usUser : userList) {
			if (canSync(usUser)) {
				CreateUser user = new CreateUser();
				user.setName(usUser.getBusiName());
				user.setUserId(usUser.getUid());
				user.setWeixinId(usUser.getWxid());
				user.setMobile(usUser.getMobile());
				user.setEmail(usUser.getMail());

				Set<String> groups = getUserGroups(usUser.getUid());
				Set<Integer> departmentIds = new HashSet<>();

				for (String group : groups) {
					UsGroup usGroup = (UsGroup) ORMService.getInstance().findByPk(UsGroup.class.getName(), group);
					departmentIds.add(usGroup.getWxDepartmentId());
				}
				departmentIds.remove(null);

				int[] departIds;
				if (departmentIds.size() > 0) {
					departIds = new int[departmentIds.size()];
					int i = 0;
					for (Integer a : departmentIds) {
						departIds[i] = a;
						i++;
					}
				} else {
					// 没有设置groupid,则使用默认
					logger.info("用户:{}没有设置组织，将使用默认组织:[1]", usUser.getUid());
					departIds = new int[] { 1 };
					;
				}

				user.setDepartment(departIds);
				user.setPosition(getUserDefaultRole(usUser.getUid()));
				wxUsers.add(user);
			} else {
				logger.warn("{} 邮箱，手机号和微信号同时为空，不作同步。", usUser.getUid());
			}
		}

		return wxUsers;
	}

	private boolean canSync(UsUser usUser) {
		boolean invalid = StringUtils.isEmpty(usUser.getWxid()) && StringUtils.isEmpty(usUser.getMail()) && StringUtils.isEmpty(usUser.getMobile());
		boolean active = (1 == usUser.getActiveFlag() && 1 == usUser.getWxEnable());
		return (!invalid) && active;
	}

	private Set<String> getUserGroups(String uid) {
		List<Map<String, Object>> list = ORMService.getInstance().query("UsUserGroupRole", new DataCondition().setStringEqual("uid", uid).toEntity());

		logger.debug("getUserGroups: {}", JsonMapper.defaultMapper().toJson(list));
		Set<String> groups = new HashSet<>();
		for (Map<String, Object> record : list) {
			groups.add(record.get("groupKey").toString());
		}
		return groups;
	}

	private String getUserDefaultRole(String uid) {
		List<Map<String, Object>> list = ORMService.getInstance().query("UsUserGroupRole", new DataCondition().setStringEqual("uid", uid).setNumberIn("defaultFlag", "1").toEntity());

		logger.debug("getUserDefaultRole: {}", JsonMapper.defaultMapper().toJson(list));

		if (list == null || list.isEmpty()) {
			return "";
		} else {
			String roleKey = list.get(0).get("roleKey").toString();
			UsRole role = (UsRole) ORMService.getInstance().findByPk(UsRole.class.getName(), roleKey);
			return role.getBusiName();
		}
	}

	private String csv(CreateUser user) throws JsonProcessingException {
		csvMapper.enable(JsonGenerator.Feature.IGNORE_UNKNOWN);
		return csvMapper.writerFor(CreateUser.class).with(USER_SCHEMA).writeValueAsString(user);
	}

	private String csv(Department department) throws JsonProcessingException {
		csvMapper.enable(JsonGenerator.Feature.IGNORE_UNKNOWN);
		return csvMapper.writerFor(Department.class).with(DEPARTMENT_SCHEMA).writeValueAsString(department);
	}

	/**
	 * 微信端状态回写
	 */
	private void executeStatusBack() {
		List<ReadUser> list = Users.defaultUsers().list();
		if (list != null) {
			for (ReadUser o : list) {
				UserStatus userStatus = o.getStatus();
				ORMService.getInstance().executeHQL("update from " + UsUser.class.getName() + " set wxStatus = ?,wxid = ?,wxAvatar = ? where uid = ? and wxEnable = 1",
						WxStatus.valueOf(userStatus.name()).getCode(), o.getWeixinId(), o.getAvatar(), o.getUserId());
			}
		}
	}

	/**
	 * 执行同步,根据设置的模式wx.qy.contactmode<br>
	 * 参考:{@link com.riversoft.platform.translate.ContactMode}
	 */
	public void executeSync() {
		ContactMode mode = ContactMode.fromCode(Integer.valueOf(Config.get("wx.qy.contactmode", "0")));
		switch (mode) {
		case none: {// 不托管则查询微信端用户状态并回写
			executeStatusBack();
			break;
		}
		case user: {// 同步用户
			executeStatusBack();
			syncUsers(false);
			break;
		}
		case all: {// 同步组织架构与用户
			executeStatusBack();
			syncAll();
			break;
		}
		default:
			break;
		}
	}
}
