package com.riversoft.wx.mp.command;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.riversoft.core.db.DataPO;
import com.riversoft.core.db.JdbcService;
import com.riversoft.core.db.ORMService;
import com.riversoft.platform.db.ORMAdapterService;
import com.riversoft.weixin.mp.user.Users;
import com.riversoft.weixin.mp.user.bean.User;
import com.riversoft.wx.mp.model.MpVisitorModelKeys;
import com.riversoft.wx.mp.service.MpAppService;

/**
 * Created by exizhai on 10/26/2015.
 */
public class UserSubEventCommand implements MpCommand {

	private static Logger logger = LoggerFactory.getLogger(UserSubEventCommand.class);

	@Override
	public MpResponse execute(MpRequest mpRequest) {

		Map<String, Object> config = (Map<String, Object>) ORMService.getInstance().findByPk("WxMp", mpRequest.getMpKey());
		String visitorTable = (String) config.get("visitorTable");
		if (mpRequest.isSubscribe()) {
			// 用户关注
			User u = Users.with(MpAppService.getInstance().getAppSetting(config)).get(mpRequest.getOpenId());
			Map<String, Object> o = (Map<String, Object>) ORMAdapterService.getInstance().findByPk(visitorTable, mpRequest.getOpenId());
			boolean createFlag = false;
			if (o == null) {
				o = new DataPO(visitorTable).toEntity();
				o.put(MpVisitorModelKeys.MP_KEY.name(), mpRequest.getMpKey());
				o.put(MpVisitorModelKeys.OPEN_ID.name(), mpRequest.getOpenId());
				o.put(MpVisitorModelKeys.CREATE_TIME.name(), new Date()); // 新增时增加创建时间
				createFlag = true;
			}
			o.put(MpVisitorModelKeys.NICK_NAME.name(), u.getNickName());
			o.put(MpVisitorModelKeys.HEAD_IMG_URL.name(), u.getHeadImgUrl());
			o.put(MpVisitorModelKeys.COUNTRY.name(), u.getCountry());
			o.put(MpVisitorModelKeys.PROVINCE.name(), u.getProvince());
			o.put(MpVisitorModelKeys.CITY.name(), u.getCity());
			o.put(MpVisitorModelKeys.SEX.name(), u.getSex().getCode());
			o.put(MpVisitorModelKeys.SUBSCRIBE.name(), u.isSubscribed() ? 1 : 0);
			o.put(MpVisitorModelKeys.SUBSCRIBE_TIME.name(), u.getSubscribedTime());
			o.put(MpVisitorModelKeys.LANGUAGE.name(), u.getLanguage());

			List<Integer> tags = u.getTags();
			if(tags != null && !tags.isEmpty()) {
				o.put(MpVisitorModelKeys.TAGS.name(), buildTags(tags));
			}

			o.put(MpVisitorModelKeys.UNION_ID.name(), u.getUnionId());
			o.put(MpVisitorModelKeys.REMARK.name(), u.getRemark());
			if (createFlag) {
				ORMAdapterService.getInstance().save(o);
			} else {
				ORMAdapterService.getInstance().update(o);
			}

		} else if (mpRequest.isUnSubscribe()) {
			// 用户取消关注
			logger.warn("用户:{}取消关注", mpRequest.getOpenId());
			JdbcService.getInstance().executeSQL("update " + visitorTable + " set " + MpVisitorModelKeys.SUBSCRIBE.name() + " = ?, "
					+ MpVisitorModelKeys.UNSUBSCRIBE_TIME.name() + " = ? where "
					+ MpVisitorModelKeys.OPEN_ID.name() + " = ?", 0, new Date(), mpRequest.getOpenId());
		}

		return null;
	}

	private String buildTags(List<Integer> tags) {
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
