package com.riversoft.wx.qy.builder;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.riversoft.weixin.common.message.Article;
import com.riversoft.weixin.common.message.MpArticle;
import com.riversoft.weixin.common.message.MpNews;
import com.riversoft.weixin.common.message.News;
import com.riversoft.weixin.common.message.Video;
import com.riversoft.weixin.qy.message.json.FileMessage;
import com.riversoft.weixin.qy.message.json.ImageMessage;
import com.riversoft.weixin.qy.message.json.JsonMessage;
import com.riversoft.weixin.qy.message.json.MpNewsMediaIdMessage;
import com.riversoft.weixin.qy.message.json.MpNewsMessage;
import com.riversoft.weixin.qy.message.json.NewsMessage;
import com.riversoft.weixin.qy.message.json.TextMessage;
import com.riversoft.weixin.qy.message.json.VideoMessage;
import com.riversoft.weixin.qy.message.json.VoiceMessage;

/**
 * Created by exizhai on 10/25/2015.
 */
public class MessageBuilder {

	public static TextMessage text(Map<String, Object> map) {
		TextMessage textMessage = new TextMessage();
		textMessage.text((String) map.get("text"));
		if (map.containsKey("safe")) {
			textMessage.setSafe((Boolean) map.get("safe"));
		}
		setGeneral(textMessage, map);
		return textMessage;
	}

	public static ImageMessage image(Map<String, Object> map) {
		ImageMessage imageMessage = new ImageMessage();
		imageMessage.image((String) map.get("image"));
		if (map.containsKey("safe")) {
			imageMessage.setSafe((Boolean) map.get("safe"));
		}
		setGeneral(imageMessage, map);
		return imageMessage;
	}

	public static VoiceMessage voice(Map<String, Object> map) {
		VoiceMessage voiceMessage = new VoiceMessage();
		voiceMessage.voice((String) map.get("voice"));
		if (map.containsKey("safe")) {
			voiceMessage.setSafe((Boolean) map.get("safe"));
		}
		setGeneral(voiceMessage, map);
		return voiceMessage;
	}

	public static JsonMessage file(Map<String, Object> map) {
		FileMessage fileMessage = new FileMessage();
		fileMessage.file((String) map.get("file"));
		if (map.containsKey("safe")) {
			fileMessage.setSafe((Boolean) map.get("safe"));
		}
		setGeneral(fileMessage, map);
		return fileMessage;
	}

	public static VideoMessage video(Map<String, Object> map) {
		VideoMessage videoMessage = new VideoMessage();
		Video video = new Video();
		video.title((String) map.get("title")).description((String) map.get("desc")).mediaId((String) map.get("video"));
		videoMessage.video(video);
		if (map.containsKey("safe")) {
			videoMessage.setSafe((Boolean) map.get("safe"));
		}
		setGeneral(videoMessage, map);
		return videoMessage;
	}

	/**
	 * news信息
	 *
	 * @param map
	 * @return
	 */
	public static NewsMessage news(Map<String, Object> map) {
		List<Map<String, Object>> list;
		if (map.containsKey("news")) {
			Object news = map.get("news");
			if (news instanceof List) {
				list = (List<Map<String, Object>>) map.get("news");
			} else if (news instanceof Map) {
				list = new ArrayList<>();
				list.add((Map) news);
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
			news.article(article);
		}

		NewsMessage newsMessage = new NewsMessage().news(news);

		setGeneral(newsMessage, map);
		return newsMessage;
	}

	/**
	 * 创建多图文
	 *
	 * @param map
	 * @return
	 */
	public static MpNews toMpNews(Map<String, Object> map) {
		List<Map<String, Object>> list;
		if (map.containsKey("mpnews")) {
			Object news = map.get("mpnews");
			if (news instanceof List) {
				list = (List<Map<String, Object>>) map.get("mpnews");
			} else if (news instanceof Map) {
				list = new ArrayList<>();
				list.add((Map) news);
			} else {
				list = new ArrayList<>();
			}
		} else {
			list = new ArrayList<>();
			list.add(map);
		}

		MpNews mpNews = new MpNews();
		for (Map<String, Object> o : list) {
			MpArticle article = new MpArticle();
			article.setTitle((String) o.get("title"));
			article.setDigest((String) o.get("digest"));
			article.setContent((String) o.get("content"));
			article.contentSourceUrl((String) o.get("url"));
			article.setAuthor((String) o.get("author"));
			article.thumbMediaId((String) o.get("thumbMediaId"));
			if (o.containsKey("cover")) {
				article.setShowCover((Boolean) o.get("cover"));
			} else {
				article.setShowCover(false);
			}
			mpNews.article(article);
		}
		return mpNews;
	}

	/**
	 * 多图文
	 *
	 * @param map
	 * @return
	 */
	public static MpNewsMessage mpnews(Map<String, Object> map) {
		MpNewsMessage mpNewsMessage = new MpNewsMessage();
		mpNewsMessage.mpNews(toMpNews(map));
		setGeneral(mpNewsMessage, map);

		if (map.containsKey("safe")) {
			mpNewsMessage.setSafe((Boolean) map.get("safe"));
		}

		return mpNewsMessage;
	}

	/**
	 * 多图文
	 *
	 * @param mediaId
	 * @return
	 */
	public static MpNewsMediaIdMessage mpnews(String mediaId) {
		MpNewsMediaIdMessage mpNewsMediaIdMessage = new MpNewsMediaIdMessage(mediaId);
		return mpNewsMediaIdMessage;
	}

	/**
	 * 设置人员相关
	 *
	 * @param jsonMessage
	 * @param map
	 */
	public static void setGeneral(JsonMessage jsonMessage, Map<String, Object> map) {
		if (map.containsKey("user")) {
			if (map.get("user") instanceof Collection) {
				jsonMessage.toUser(StringUtils.join((Collection[]) map.get("user"), "|"));
			} else if (map.get("user") instanceof String[]) {
				jsonMessage.toUser(StringUtils.join((String[]) map.get("user"), "|"));
			} else {
				jsonMessage.toUser((String) map.get("user"));
			}
		}
		if (map.containsKey("group")) {
			if (map.get("group") instanceof Collection) {
				jsonMessage.toParty(StringUtils.join((Collection[]) map.get("group"), "|"));
			} else if (map.get("group") instanceof String[]) {
				jsonMessage.toParty(StringUtils.join((String[]) map.get("group"), "|"));
			} else {
				jsonMessage.toParty((String) map.get("group"));
			}
		}
		if (map.containsKey("tag")) {
			if (map.get("tag") instanceof Collection) {
				jsonMessage.toTag(StringUtils.join((Collection[]) map.get("tag"), "|"));
			} else if (map.get("tag") instanceof String[]) {
				jsonMessage.toTag(StringUtils.join((String[]) map.get("tag"), "|"));
			} else {
				jsonMessage.toTag((String) map.get("tag"));
			}
		}
	}

}
