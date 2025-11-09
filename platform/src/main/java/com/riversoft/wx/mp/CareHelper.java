package com.riversoft.wx.mp;

import com.riversoft.weixin.common.message.Article;
import com.riversoft.weixin.common.message.News;
import com.riversoft.weixin.mp.base.AppSetting;
import com.riversoft.weixin.mp.care.Accounts;
import com.riversoft.weixin.mp.care.CareMessages;
import com.riversoft.weixin.mp.care.Sessions;
import com.riversoft.weixin.mp.care.bean.Music;
import com.riversoft.weixin.mp.care.bean.Video;
import com.riversoft.wx.mp.service.MpAppService;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by exizhai on 12/11/2015.
 */
public class CareHelper {

	private AppSetting appSetting;

	public CareHelper() {
	}

	public CareHelper(String appKey) {
		this.appSetting = MpAppService.getInstance().getAppSettingByPK(appKey);
	}

	public CareHelper(AppSetting appSetting) {
		this.appSetting = appSetting;
	}

	public void setAppSetting(AppSetting appSetting) {
		this.appSetting = appSetting;
	}

	/**
	 * 客服会话管理接口，直接提供基础能力
	 *
	 * @return
	 */
	public Sessions getSessions() {
		return Sessions.with(appSetting);
	}

	/**
	 * 客服管理接口，直接提供基础能力
	 *
	 * @return
	 */
	public Accounts getAccounts() {
		return Accounts.with(appSetting);
	}

	public void text(Map<String, Object> map) {
		if (map.containsKey("from")) {
			CareMessages.with(appSetting).text((String) map.get("user"), (String) map.get("text"), (String) map.get("from"));
		} else {
			CareMessages.with(appSetting).text((String) map.get("user"), (String) map.get("text"));
		}
	}

	public void image(Map<String, Object> map) {
		if (map.containsKey("from")) {
			CareMessages.with(appSetting).image((String) map.get("user"), (String) map.get("image"), (String) map.get("from"));
		} else {
			CareMessages.with(appSetting).image((String) map.get("user"), (String) map.get("image"));
		}
	}

	public void voice(Map<String, Object> map) {
		if (map.containsKey("from")) {
			CareMessages.with(appSetting).voice((String) map.get("user"), (String) map.get("voice"), (String) map.get("from"));
		} else {
			CareMessages.with(appSetting).voice((String) map.get("user"), (String) map.get("voice"));
		}
	}

	public void music(Map<String, Object> map) {
		Music music = new Music();
		music.setTitle((String) map.get("title"));
		music.setThumbMediaId((String) map.get("thumb"));
		music.setDescription((String) map.get("desc"));
		music.setMusicUrl((String) map.get("musicUrl"));
		music.setHqMusicUrl((String) map.get("hqMusicUrl"));

		if (map.containsKey("from")) {
			CareMessages.with(appSetting).music((String) map.get("user"), music, (String) map.get("from"));
		} else {
			CareMessages.with(appSetting).music((String) map.get("user"), music);
		}
	}

	public void video(Map<String, Object> map) {
		Video video = new Video();
		video.setDescription((String) map.get("desc"));
		video.setMediaId((String) map.get("video"));
		video.setTitle((String) map.get("title"));
		video.setThumbMediaId((String) map.get("thumb"));

		if (map.containsKey("from")) {
			CareMessages.with(appSetting).video((String) map.get("user"), video, (String) map.get("from"));
		} else {
			CareMessages.with(appSetting).video((String) map.get("user"), video);
		}
	}

	public void news(Map<String, Object> map) {
		List<Map<String, Object>> list;
		if (map.containsKey("news")) {
			Object obj = map.get("news");
			if (obj instanceof List) {
				list = (List<Map<String, Object>>) map.get("news");
			} else if (obj instanceof Map) {
				list = new ArrayList<>();
				list.add((Map) obj);
			} else {
				list = new ArrayList<>();
			}
		} else {
			list = new ArrayList<>();
			list.add(map);
		}

		News news = new News();
		for (Map<String, Object> o : list) {
			Article article = new Article();
			article.title((String) o.get("title")).description((String) o.get("desc")).picUrl((String) o.get("picUrl")).url((String) o.get("url"));
			news.add(article);
		}

		if (map.containsKey("from")) {
			CareMessages.with(appSetting).news((String) map.get("user"), news, (String) map.get("from"));
		} else {
			CareMessages.with(appSetting).news((String) map.get("user"), news);
		}
	}

	public void mpnews(Map<String, Object> map) {
		if (map.containsKey("from")) {
			CareMessages.with(appSetting).mpNews((String) map.get("user"), (String) map.get("mpnews"), (String) map.get("from"));
		} else {
			CareMessages.with(appSetting).mpNews((String) map.get("user"), (String) map.get("mpnews"));
		}
	}
}
