package com.riversoft.platform.web;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.riversoft.core.db.DataPO;
import com.riversoft.core.db.ORMService;
import com.riversoft.platform.db.ORMAdapterService;
import com.riversoft.platform.po.UsUser;
import com.riversoft.util.ValueConvertUtils;
import com.riversoft.weixin.common.oauth2.OpenUser;
import com.riversoft.weixin.mp.user.bean.User;
import com.riversoft.wx.mp.model.MpVisitorModelKeys;
import com.riversoft.wx.mp.model.OpenVisitorModelKeys;

/**
 * @borball on 3/27/2016.
 */
public class WxUserManager {

	/**
	 * 生成开放平台user
	 * 
	 * @param visitor
	 * @return
	 */
	public static UsUser buildOpenUser(Map<String, Object> visitor) {

		// 先看visitor有没对应USER_ID
		String uid = (String) visitor.get(OpenVisitorModelKeys.USER_ID.name());
		if (StringUtils.isNotEmpty(uid)) {
			UsUser user = (UsUser) ORMService.getInstance().findByPk(UsUser.class.getName(), uid);
			user.setEntity(true);
			user.setOpen(true);
			user.setOpenId((String) visitor.get(OpenVisitorModelKeys.OPEN_ID.name()));
			user.setUnionId((String) visitor.get(OpenVisitorModelKeys.UNION_ID.name()));
			return user;
		}

		// 看看对应TAG数据有没有绑定group和role
		UsUser user = new UsUser();
		user.setEntity(false);
		user.setOpen(true);
		user.setOpenId((String) visitor.get(OpenVisitorModelKeys.OPEN_ID.name()));
		user.setUnionId((String) visitor.get(OpenVisitorModelKeys.UNION_ID.name()));
		user.setUid("_open" + "$" + user.getOpenId());
		user.setBusiName((String) visitor.get(OpenVisitorModelKeys.NICK_NAME.name()));
		if (StringUtils.isEmpty(user.getBusiName())) {
			user.setBusiName("访客");
		}
		user.setWxAvatar((String) visitor.get(OpenVisitorModelKeys.HEAD_IMG_URL.name()));
		user.setActiveFlag(1);
		user.setWxEnable(1);
		user.setWxStatus(1);
		user.setCreateDate(new Date());
		user.setUpdateDate(new Date());
		user.setEffDate(ValueConvertUtils.convert("1900-01-01", Date.class));
		user.setEndDate(ValueConvertUtils.convert("2099-12-31", Date.class));

		return user;
	}

	/**
	 * 生成公众号user
	 * 
	 * @param visitor
	 * @param groupKey
	 * @param roleKey
	 * @return
	 */
	public static UsUser buildMpUser(Map<String, Object> visitor, String groupKey, String roleKey) {

		// 先看visitor有没对应USER_ID
		String uid = (String) visitor.get(MpVisitorModelKeys.USER_ID.name());
		if (StringUtils.isNotEmpty(uid)) {
			UsUser user = (UsUser) ORMService.getInstance().findByPk(UsUser.class.getName(), uid);
			user.setEntity(true);
			user.setMpKey((String) visitor.get(MpVisitorModelKeys.MP_KEY.name()));
			user.setOpenId((String) visitor.get(MpVisitorModelKeys.OPEN_ID.name()));
			user.setUnionId((String) visitor.get(MpVisitorModelKeys.UNION_ID.name()));
			return user;
		}

		// 看看对应TAG数据有没有绑定group和role
		UsUser user = new UsUser();
		user.setEntity(false);
		user.setMpKey((String) visitor.get(MpVisitorModelKeys.MP_KEY.name()));
		user.setOpenId((String) visitor.get(MpVisitorModelKeys.OPEN_ID.name()));
		user.setUnionId((String) visitor.get(MpVisitorModelKeys.UNION_ID.name()));
		user.setUid(user.getMpKey() + "$" + user.getOpenId());
		user.setBusiName((String) visitor.get(MpVisitorModelKeys.NICK_NAME.name()));
		user.setWxAvatar((String) visitor.get(MpVisitorModelKeys.HEAD_IMG_URL.name()));
		user.setActiveFlag(1);
		user.setWxEnable(1);
		user.setWxStatus(1);
		user.setCreateDate(new Date());
		user.setUpdateDate(new Date());
		user.setEffDate(ValueConvertUtils.convert("1900-01-01", Date.class));
		user.setEndDate(ValueConvertUtils.convert("2099-12-31", Date.class));
		// TODO 其他需要绑定拷贝的东西

		if (StringUtils.isNotEmpty(groupKey) || StringUtils.isNotEmpty(roleKey)) {
			user.setVisitorGroupKey(groupKey);
			user.setVisitorRoleKey(roleKey);
		}

		return user;
	}

	public static Map<String, Object> buildVisitor(String mpKey, String visitorTable, User u) {
		if (u == null) {
			return null;
		}

		Map<String, Object> o = new DataPO(visitorTable).toEntity();
		o.put(MpVisitorModelKeys.MP_KEY.name(), mpKey);
		o.put(MpVisitorModelKeys.OPEN_ID.name(), u.getOpenId());
		o.put(MpVisitorModelKeys.NICK_NAME.name(), u.getNickName());
		o.put(MpVisitorModelKeys.HEAD_IMG_URL.name(), u.getHeadImgUrl());
		o.put(MpVisitorModelKeys.COUNTRY.name(), u.getCountry());
		o.put(MpVisitorModelKeys.PROVINCE.name(), u.getProvince());
		o.put(MpVisitorModelKeys.CITY.name(), u.getCity());
		o.put(MpVisitorModelKeys.SEX.name(), u.getSex() != null ? u.getSex().getCode() : null);
		o.put(MpVisitorModelKeys.SUBSCRIBE.name(), u.isSubscribed() ? 1 : 0);
		o.put(MpVisitorModelKeys.SUBSCRIBE_TIME.name(), u.getSubscribedTime());
		o.put(MpVisitorModelKeys.LANGUAGE.name(), u.getLanguage());
		List<Integer> tags = u.getTags();
		if (tags != null && !tags.isEmpty()) {
			o.put(MpVisitorModelKeys.TAGS.name(), buildTags(tags));
		}
		o.put(MpVisitorModelKeys.UNION_ID.name(), u.getUnionId());
		o.put(MpVisitorModelKeys.REMARK.name(), u.getRemark());
		o.put(MpVisitorModelKeys.CREATE_TIME.name(), new Date()); // 新增时增加创建时间

		ORMAdapterService.getInstance().save(o);

		return o;
	}

	public static Map<String, Object> buildVisitor(String mpKey, String visitorTable, OpenUser u) {
		if (u == null) {
			return null;
		}

		Map<String, Object> o = new DataPO(visitorTable).toEntity();
		o.put(MpVisitorModelKeys.MP_KEY.name(), mpKey);
		o.put(MpVisitorModelKeys.OPEN_ID.name(), u.getOpenId());
		o.put(MpVisitorModelKeys.NICK_NAME.name(), StringUtils.isNotEmpty(u.getNickName()) ? u.getNickName() : "(无法获取)");
		o.put(MpVisitorModelKeys.HEAD_IMG_URL.name(), u.getHeadImgUrl());
		o.put(MpVisitorModelKeys.COUNTRY.name(), u.getCountry());
		o.put(MpVisitorModelKeys.PROVINCE.name(), u.getProvince());
		o.put(MpVisitorModelKeys.CITY.name(), u.getCity());
		o.put(MpVisitorModelKeys.SEX.name(), u.getSex().getCode());
		o.put(MpVisitorModelKeys.SUBSCRIBE.name(), 0);
		o.put(MpVisitorModelKeys.SUBSCRIBE_TIME.name(), null);
		o.put(MpVisitorModelKeys.LANGUAGE.name(), null);
		o.put(MpVisitorModelKeys.UNION_ID.name(), u.getUnionId());
		o.put(MpVisitorModelKeys.REMARK.name(), null);
		o.put(MpVisitorModelKeys.CREATE_TIME.name(), new Date()); // 新增时增加创建时间

		ORMAdapterService.getInstance().save(o);

		return o;
	}

	public static Map<String, Object> buildOpenVisitor(String visitorTable, OpenUser u) {
		if (u == null) {
			return null;
		}

		Map<String, Object> o = new DataPO(visitorTable).toEntity();
		o.put(OpenVisitorModelKeys.OPEN_ID.name(), u.getOpenId());
		o.put(OpenVisitorModelKeys.NICK_NAME.name(), u.getNickName());
		o.put(OpenVisitorModelKeys.HEAD_IMG_URL.name(), u.getHeadImgUrl());
		o.put(OpenVisitorModelKeys.UNION_ID.name(), u.getUnionId());
		o.put(OpenVisitorModelKeys.REMARK.name(), null);
		o.put(OpenVisitorModelKeys.CREATE_TIME.name(), new Date()); // 新增时增加创建时间
		o.put(OpenVisitorModelKeys.UPDATE_DATE.name(), new Date()); // 新增时增加创建时间
		ORMAdapterService.getInstance().save(o);

		return o;
	}

	private static String buildTags(List<Integer> tags) {
		Object[] integers = tags.toArray();
		Arrays.sort(integers);
		StringBuffer sb = new StringBuffer();
		for (Object tag : integers) {
			sb.append("[").append(tag).append("]").append(";");
		}
		sb.deleteCharAt(sb.length() - 1);
		return sb.toString();
	}
}
