/*
 * $HeadURL: $
 * $Id: $
 * Copyright (c) 2013 by Riversoft System, all rights reserved.
 */
package com.riversoft.module.manager.user;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.riversoft.core.BeanFactory;
import com.riversoft.core.db.ORMService;
import com.riversoft.platform.po.UsGroup;
import com.riversoft.platform.po.UsTag;
import com.riversoft.weixin.qy.contact.Tags;
import com.riversoft.weixin.qy.contact.tag.Tag;
import com.riversoft.weixin.qy.contact.tag.TagUsers;
import com.riversoft.weixin.qy.contact.user.SimpleUser;

/**
 * @author Woden
 * 
 */
public class TagService extends ORMService {

	private Logger logger = LoggerFactory.getLogger(TagService.class);

	public static TagService getInstance() {
		return BeanFactory.getInstance().getSingleBean(TagService.class);
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

	/**
	 * 计算较大值 wxTagId
	 * 
	 * @param begin
	 * @param current
	 * @return
	 */
	private Integer bigger(int begin, int current) {
		return begin > current ? begin : current;
	}

	/**
	 * 找出当前企业号最大的wxTagId
	 * 
	 * @param tags
	 * @return
	 */
	private int maxTagId(List<Tag> tags) {
		int max = 0;
		if (tags != null && !tags.isEmpty()) {
			for (Tag tag : tags) {
				if (tag.getId() > max) {
					max = tag.getId();
				}
			}
		}
		return max;
	}

	/**
	 * 全面清除微信企业号的标签及下面的组织人员 (谨慎)
	 */
	private void cleanTags() {
		logger.info("全面清除微信侧标签");
		List<Tag> tags = Tags.defaultTags().list();
		for (Tag tag : tags) {
			logger.info("清除微信侧标签:[{}:{}]", tag.getId(), tag.getName());
			TagUsers tagUsers = Tags.defaultTags().getUsers(tag.getId());
			if ((tagUsers.getDepartments() == null || tagUsers.getDepartments().isEmpty())
					&& (tagUsers.getUsers() == null || tagUsers.getUsers().isEmpty())) {
				logger.info("空的tag:{}，不做任何操作", tag.getId());
			} else {
				Tags.defaultTags().deleteUsers(tagUsers.getTagId(), toUsers(tagUsers.getUsers()),
						tagUsers.getDepartments());
			}
			Tags.defaultTags().delete(tag.getId());
		}
	}

	/**
	 * 清除
	 * 
	 * @param usTags
	 */
	private void cleanTags(List<UsTag> usTags) {
		for (UsTag usTag : usTags) {
			logger.info("清除微信端标签");
			Tags.defaultTags().delete(usTag.getWxTagId());
		}
	}

	/**
	 * 清除标签中人员/组织
	 * 
	 * @param usTags
	 */
	private void cleanTagsUserGroups(List<UsTag> usTags) {
		for (UsTag usTag : usTags) {
			logger.info("清除微信侧标签人员:[{}:{}]", usTag.getWxTagId(), usTag.getBusiName());
			TagUsers tagUsers = Tags.defaultTags().getUsers(usTag.getWxTagId());
			if ((tagUsers.getDepartments() == null || tagUsers.getDepartments().isEmpty())
					&& (tagUsers.getUsers() == null || tagUsers.getUsers().isEmpty())) {
				logger.info("空的tag:{}，不做任何操作", usTag.getWxTagId());
			} else {
				Tags.defaultTags().deleteUsers(tagUsers.getTagId(), toUsers(tagUsers.getUsers()),
						tagUsers.getDepartments());
			}
		}
	}

	/**
	 * 更新
	 * 
	 * @param usTags
	 */
	private void updateTags(List<UsTag> usTags) {
		for (UsTag usTag : usTags) {
			logger.info("更新tag名称");
			Tags.defaultTags().update(new Tag(usTag.getWxTagId(), usTag.getBusiName()));
		}
	}

	/**
	 * 新建标签
	 * 
	 * @param usTags
	 */
	private void createTags(List<UsTag> usTags) {
		for (UsTag usTag : usTags) {
			logger.info("准备创建标签:[{}:{}]", usTag.getWxTagId(), usTag.getBusiName());
			Tags.defaultTags().create(new Tag(usTag.getWxTagId(), usTag.getBusiName()));
		}
	}

	/**
	 * 新建标签中人员/组织
	 * 
	 * @param usTags
	 */
	private void createTagsUserGroups(List<UsTag> usTags) {
		for (UsTag usTag : usTags) {
			logger.info("新建标签人员/组织");
			List<?> tagUsers = ORMService.getInstance().queryHQL("from UsUserTag where tagKey = ?", usTag.getTagKey());

			List<String> userIds = new ArrayList<>();
			if (tagUsers == null || tagUsers.isEmpty()) {
				logger.info("tag:[{}:{}]下没有用户", usTag.getTagKey(), usTag.getBusiName());
			} else {
				for (Object o : tagUsers) {
					Map<String, Object> tagUser = (Map<String, Object>) o;
					userIds.add((String) tagUser.get("uid"));
				}
			}
			List<?> tagGroups = ORMService.getInstance().queryHQL("from UsGroupTag where tagKey = ?",
					usTag.getTagKey());
			List<Integer> groupIds = new ArrayList<>();
			if (tagGroups == null || tagGroups.isEmpty()) {
				logger.info("tag:[{}:{}]下没有组织", usTag.getTagKey(), usTag.getBusiName());
			} else {
				for (Object o : tagGroups) {
					Map<String, Object> tagGroup = (Map<String, Object>) o;
					String groupKey = (String) tagGroup.get("groupKey");
					UsGroup usGroup = (UsGroup) ORMService.getInstance().findByPk(UsGroup.class.getName(), groupKey);
					groupIds.add(usGroup.getWxDepartmentId());
				}
			}

			if (groupIds.isEmpty() && userIds.isEmpty()) {
				logger.warn("tag:[{}:{}]既没用户也没组织", usTag.getTagKey(), usTag.getBusiName());
			} else {
				logger.info("准备同步标签用户:[{}:{}]:[{}, {}]", usTag.getWxTagId(), usTag.getBusiName(), userIds, groupIds);
				Tags.defaultTags().addUsers(usTag.getWxTagId(), userIds, groupIds);
			}
		}
	}

	/**
	 * 更新bpmt端的标签中的wxTagId
	 * 
	 * @param usTags
	 */
	private void executeWxTagIdValidation(List<UsTag> usTags) {
		// 可能有些tag删不掉
		List<Tag> tags = Tags.defaultTags().list();
		int maxTagId = maxTagId(tags);
		int begin = 100;
		if (maxTagId > 0) {
			begin = maxTagId + 1; // 起始值从maxTagId+1开始
		}

		for (UsTag usTag : usTags) {
			if (usTag.getWxTagId() == null) {
				List<Integer> maxes = ORMService.getInstance().queryHQL("select max(wxTagId) from " + UsTag.class.getName());
				if (maxes == null || maxes.isEmpty() || maxes.get(0) == null || maxes.get(0) == 0) {
					usTag.setWxTagId(begin);
				} else {
					usTag.setWxTagId(bigger(begin, maxes.get(0) + 1));
				}

				ORMService.getInstance().updatePO(usTag);
			}
		}
	}

	/**
	 * 删除单个bpmt标签
	 * 
	 * @param tagKey
	 */
	public void executeRemove(String tagKey) {
		super.executeHQL("delete from UsUserTag where tagKey = ?", tagKey);
		super.executeHQL("delete from UsGroupTag where tagKey = ?", tagKey);
		super.removeByPk(UsTag.class.getName(), tagKey);
	}

	/**
	 * 安全更新企业号标签
	 */
	public boolean updateTag() {
		boolean result = true;
		// bpmt当前的tags
		List<UsTag> usTags = ORMService.getInstance().queryAll(UsTag.class.getName());
		// 待新增的bpmtTags
		List<UsTag> newTags = new ArrayList<>();
		// 待更新名称的bpmtTags
		List<UsTag> updateTags = new ArrayList<>();
		// 微信tags
		List<Tag> tags = Tags.defaultTags().list();
		// 微信tagsId
		List<Integer> tagsIds = new ArrayList<>();

		for (Tag tag : tags) {
			tagsIds.add(tag.getId());
		}

		if (usTags != null && !usTags.isEmpty()) {
			for (UsTag usTag : usTags) {
				if (usTag.getWxTagId() == null) {// 若为空即肯定是更新的
					newTags.add(usTag);
					continue;
				} else if (tagsIds.contains(usTag.getWxTagId())) {
					updateTags.add(usTag);
				} else {// 微信端可能已删除
					newTags.add(usTag);
				}
			}

			try {
				executeWxTagIdValidation(newTags); // 更新需要新增的wxId
				createTags(newTags); // 新建标签
				updateTags(updateTags); // 更新标签名称
				cleanTagsUserGroups(usTags); // 清除人员及组织
				createTagsUserGroups(usTags); // 新建组织及人员
			} catch (Exception e) {
				logger.error("更新标签失败.", e);
				result = false;
			}
		}
		return result;
	}

	/**
	 * 覆盖企业号标签
	 */
	public boolean coverTag() {
		boolean result = true;
		try {
			// 重新创建tag
			List<UsTag> usTags = ORMService.getInstance().queryAll(UsTag.class.getName());

			if (usTags != null && !usTags.isEmpty()) {
				logger.info("准备重新创建标签，共{}个", usTags.size());

				// 暴力清除微信端记录
				cleanTags();
				// 确认tagId的最大值
				executeWxTagIdValidation(usTags);
				// 新建标签
				createTags(usTags);
				// 新建人员
				createTagsUserGroups(usTags);
			}
		} catch (Exception e) {
			logger.error("覆盖标签失败.", e);
			result = false;
		}
		return result;
	}

}
