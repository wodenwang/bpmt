package com.riversoft.wx.mp;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.riversoft.core.context.RequestContext;
import com.riversoft.core.db.ORMService;
import com.riversoft.core.exception.ExceptionType;
import com.riversoft.core.exception.SystemRuntimeException;
import com.riversoft.core.web.Actions.Keys;
import com.riversoft.platform.web.FileManager;
import com.riversoft.util.OfficeUtils;
import com.riversoft.weixin.common.AccessToken;
import com.riversoft.weixin.common.jsapi.JsAPISignature;
import com.riversoft.weixin.common.media.MediaType;
import com.riversoft.weixin.common.media.MpArticle;
import com.riversoft.weixin.common.media.MpNews;
import com.riversoft.weixin.mp.MpWxClientFactory;
import com.riversoft.weixin.mp.base.AppSetting;
import com.riversoft.weixin.mp.jsapi.JsAPIs;
import com.riversoft.weixin.mp.media.Materials;
import com.riversoft.weixin.mp.media.bean.Counts;
import com.riversoft.weixin.mp.media.bean.MaterialPagination;
import com.riversoft.weixin.mp.media.bean.MpNewsPagination.MpNewsItem;
import com.riversoft.weixin.mp.message.MpMessages;
import com.riversoft.weixin.mp.ticket.Tickets;
import com.riversoft.weixin.mp.url.Urls;
import com.riversoft.weixin.pay.base.PaySetting;
import com.riversoft.wx.mp.service.MpAppService;

/**
 * 公众号函数库
 * <p/>
 * Created by exizhai on 12/4/2015.
 */
public class MpHelper {

	private Logger logger = LoggerFactory.getLogger(MpHelper.class);

	private String mpKey;
	private AppSetting appSetting;
	private PaySetting paySetting;
	private Map<String, Object> config;

	public MpHelper() {
	}

	@SuppressWarnings("unchecked")
	public MpHelper(String mpKey) {
		this.mpKey = mpKey;
		this.appSetting = MpAppService.getInstance().getAppSettingByPK(mpKey);
		this.paySetting = MpAppService.getInstance().getPaySettingByPK(mpKey);
		this.config = (Map<String, Object>) ORMService.getInstance().findByPk("WxMp", mpKey);
	}

	public void setAppSetting(AppSetting appSetting) {
		this.appSetting = appSetting;
	}

	public void setPaySetting(PaySetting paySetting) {
		this.paySetting = paySetting;
	}

	/**
	 * 获取配置
	 * 
	 * @return
	 */
	public Map<String, Object> getConfig() {
		return config;
	}

	/**
	 * 客服接口
	 *
	 * @return
	 */
	public CareHelper getCare() {
		return new CareHelper(appSetting);
	}

	/**
	 * 临时素材管理
	 *
	 * @return
	 */
	public MpMediaHelper getMedia() {
		return new MpMediaHelper(appSetting);
	}

	/**
	 * 支付接口
	 * 
	 * @return
	 */
	public MpPayHelper getPay() {
		return new MpPayHelper(paySetting);
	}

	/**
	 * 模板消息接口
	 *
	 * @return
	 */
	public TemplateMsgHelper getTemplateMsg() {
		return new TemplateMsgHelper(appSetting);
	}

	// ================= 群发接口

	/**
	 * 群发文本消息
	 *
	 * @param map
	 * @return 消息ID
	 */
	@SuppressWarnings("unchecked")
	public long text(Map<String, Object> map) {
		String text = map.get("text").toString();
		if (map.containsKey("group")) {
			return MpMessages.with(appSetting).text((Integer) map.get("group"), text);
		} else if (map.containsKey("users")) {
			List<String> users = (List<String>) map.get("users");
			return MpMessages.with(appSetting).text(users, text);
		} else {
			return MpMessages.with(appSetting).text(text);
		}
	}

	/**
	 * 群发图片消息
	 *
	 * @param map
	 * @return 消息ID
	 */
	@SuppressWarnings("unchecked")
	public long image(Map<String, Object> map) {
		String image = map.get("image").toString();
		if (map.containsKey("group")) {
			return MpMessages.with(appSetting).image((Integer) map.get("group"), image);
		} else if (map.containsKey("users")) {
			List<String> users = (List<String>) map.get("users");
			return MpMessages.with(appSetting).image(users, image);
		} else {
			return MpMessages.with(appSetting).image(image);
		}
	}

	/**
	 * 群发语音消息
	 *
	 * @param map
	 * @return 消息ID
	 */
	@SuppressWarnings("unchecked")
	public long voice(Map<String, Object> map) {
		String voice = map.get("voice").toString();
		if (map.containsKey("group")) {
			return MpMessages.with(appSetting).voice((Integer) map.get("group"), voice);
		} else if (map.containsKey("users")) {
			List<String> users = (List<String>) map.get("users");
			return MpMessages.with(appSetting).voice(users, voice);
		} else {
			return MpMessages.with(appSetting).voice(voice);
		}
	}

	/**
	 * 群发卡券
	 *
	 * @param map
	 * @return 消息ID
	 */
	@SuppressWarnings("unchecked")
	public long card(Map<String, Object> map) {
		String card = map.get("card").toString();
		if (map.containsKey("group")) {
			return MpMessages.with(appSetting).card((Integer) map.get("group"), card);
		} else if (map.containsKey("users")) {
			List<String> users = (List<String>) map.get("users");
			return MpMessages.with(appSetting).card(users, card);
		} else {
			return MpMessages.with(appSetting).card(card);
		}
	}

	/**
	 * 群发视频
	 *
	 * @param map
	 * @return 消息ID
	 */
	@SuppressWarnings("unchecked")
	public long video(Map<String, Object> map) {
		String desc = (String) map.get("desc");
		String title = (String) map.get("title");
		String media = (String) map.get("video");

		if (map.containsKey("group")) {
			return MpMessages.with(appSetting).video((Integer) map.get("group"), media, title, desc);
		} else if (map.containsKey("users")) {
			List<String> users = (List<String>) map.get("users");
			return MpMessages.with(appSetting).video(users, media, title, desc);
		} else {
			return MpMessages.with(appSetting).video(media, title, desc);
		}
	}

	/**
	 * 群发图文消息
	 *
	 * @param map
	 * @return 消息ID
	 */
	@SuppressWarnings("unchecked")
	public long mpnews(Map<String, Object> map) {
		String mpnews = (String) map.get("mpnews");

		if (map.containsKey("group")) {
			return MpMessages.with(appSetting).mpNews((Integer) map.get("group"), mpnews);
		} else if (map.containsKey("users")) {
			List<String> users = (List<String>) map.get("users");
			return MpMessages.with(appSetting).mpNews(users, mpnews);
		} else {
			return MpMessages.with(appSetting).mpNews(mpnews);
		}
	}

	// ================= 永久素材上传管理

	/**
	 * 上传图文素材
	 *
	 * @param map
	 * @return
	 */
	public String addMpnews(Map<String, Object> map) {
		MpNews mpNews = toMpNews(map);
		return Materials.with(appSetting).addMpNews(mpNews);
	}

	/**
	 * 删除图文素材
	 *
	 * @param mediaId
	 * @return
	 */
	public void deleteMpnews(String mediaId) {
		Materials.with(appSetting).delete(mediaId);
	}

	/**
	 * 修改图文素材, 只能一个个article来做修改
	 *
	 * @param map
	 * @return
	 */
	public void updateMpnews(String mediaId, int index, Map<String, Object> map) {
		Materials.with(appSetting).updateMpNews(mediaId, index, toMpArticle(map));
	}

	/**
	 * ppt转换成图文素材
	 * 
	 * @param map
	 * @param file
	 *            ppt文件
	 * @return
	 */
	public String file2mpnews(Map<String, Object> map, Object file) {
		return file2mpnews(map, file, null);
	}

	/**
	 * ppt转换成图文素材
	 *
	 * @param map
	 *            图文信息
	 * @param file
	 *            ppt文件
	 * @param extra
	 *            额外的配置内容:<br>
	 *            cover:封面<br>
	 *            header:内容头部<br>
	 *            fotter:内容底部
	 * 
	 * @return
	 */
	public String file2mpnews(Map<String, Object> map, Object file, Map<String, Object> extra) {
		String title = (String) map.get("title");

		String pixel;// 文件后缀,默认ppt
		try {
			InputStream fileStream = null;
			if (file instanceof byte[]) {
				List<FileManager.UploadFile> fileList = FileManager.toFiles((byte[]) file);
				if (fileList == null || fileList.size() < 1) {
					throw new SystemRuntimeException(ExceptionType.SCRIPT, "解析文件不存在.");
				}
				fileStream = fileList.get(0).getInputStream();
				String fileName = fileList.get(0).getName();
				pixel = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
			} else if (file instanceof InputStream) {
				fileStream = (InputStream) file;
				if (extra.containsKey("filetype")) {
					pixel = (String) extra.get("filetype");
				} else {
					throw new SystemRuntimeException(ExceptionType.SCRIPT, "extra 缺少文件类型定义[filetype].");
				}
			} else if (file instanceof File) {
				fileStream = new FileInputStream((File) file);
				String fileName = ((File) file).getName();
				pixel = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
			} else {
				throw new SystemRuntimeException(ExceptionType.SCRIPT, "解析文件不符合格式.");
			}

			String fileName = title + "." + pixel;
			List<File> images;
			if ("ppt".equalsIgnoreCase(pixel) || "pptx".equals(pixel)) {
				images = OfficeUtils.ppt2jpgs(fileStream, fileName, 3f);// 放大3倍
			} else if ("xls".equalsIgnoreCase(pixel) || "xlsx".equals(pixel)) {
				images = OfficeUtils.excel2jpgs(fileStream, fileName);
			} else if ("doc".equalsIgnoreCase(pixel) || "docx".equals(pixel)) {
				File pdf = OfficeUtils.word2pdf(fileStream, fileName);
				images = OfficeUtils.pdf2jpgs(pdf, 3f);
			} else if ("pdf".equalsIgnoreCase(pixel)) {
				images = OfficeUtils.pdf2jpgs(fileStream, fileName, 3f);
			} else {
				throw new SystemRuntimeException(ExceptionType.SCRIPT, "不支持" + pixel + "格式的解析.");
			}

			// 封面处理
			String coverMediaId = null;
			if (map.get("thumbMediaId") == null) {// 没有指定封面的时候需要处理
				String coverName = title + "-封面.jpg";
				InputStream coverStream = null;
				Object cover = extra != null ? extra.get("cover") : null;
				if (cover != null) {
					if (file instanceof byte[]) {
						List<FileManager.UploadFile> fileList = FileManager.toFiles((byte[]) file);
						if (fileList == null || fileList.size() < 1) {
							throw new SystemRuntimeException(ExceptionType.SCRIPT, "解析文件不存在.");
						}
						coverStream = fileList.get(0).getInputStream();
					} else if (file instanceof InputStream) {
						coverStream = (InputStream) file;
					} else if (file instanceof File) {
						coverStream = new FileInputStream((File) file);
					} else {
						throw new SystemRuntimeException(ExceptionType.SCRIPT, "解析文件不符合格式.");
					}
				} else {
					coverStream = new FileInputStream(images.get(0));
				}
				coverMediaId = Materials.with(appSetting).addImage(coverStream, coverName).getMediaId();
			}

			List<String> imageUrls = new ArrayList<>();
			for (File image : images) {
				logger.debug("上传[{}]", image);
				retry: for (int i = 1; i <= 5; i++) {
					try (InputStream fis = new FileInputStream(image)) {
						imageUrls.add(Materials.with(appSetting).addMpNewsImage(fis, image.getName()));
						break retry;
					} catch (Exception e) {
						logger.error("上传{}失败,即将第{}次重试.", image.getName(), i, e);
					}
				}
			}

			int pageSize = 50;
			int total = imageUrls.size();
			int articles = (total / pageSize) + (total % pageSize == 0 ? 0 : 1);
			logger.info("文件一共[{}]页, 拆分为[{}]篇", total, articles);
			MpNews mpNews = new MpNews();
			// 内容头部与尾部
			String header = extra != null ? (String) extra.get("header") : null;
			String footer = extra != null ? (String) extra.get("header") : null;

			for (int page = 0; page < articles; page++) {
				logger.info("正在处理第[{}]篇", page);
				MpArticle mpArticle = toMpArticle(map);
				if (StringUtils.isNotEmpty(coverMediaId)) {
					mpArticle.setThumbMediaId(coverMediaId);
				}
				if (articles == 1) {
					mpArticle.setTitle(title);
				} else {
					mpArticle.setTitle(title + "(" + (page + 1) + ")");
				}
				StringBuffer content = new StringBuffer();
				if (StringUtils.isNotEmpty(header)) {
					content.append(header);
				}
				for (int i = 0; i < pageSize; i++) {
					int index = page * pageSize + i;
					logger.info("正在处理总第[{}]页", index);
					String imageUrl = imageUrls.get(index);
					content.append("<img src=\"").append(imageUrl).append("\">\n<br/>\n");
					if (index == (total - 1)) {
						break;
					}
				}
				if (StringUtils.isNotEmpty(footer)) {
					content.append(footer);
				}

				mpArticle.content(content.toString());
				if (page == 0) {// 第一篇加描述
					mpArticle.digest((String) map.get("digest"));
				} else {
					mpArticle.digest(null);
				}
				mpNews.add(mpArticle);
			}

			String mpMediaId = Materials.with(appSetting).addMpNews(mpNews);
			return mpMediaId;
		} catch (Exception e) {
			throw new SystemRuntimeException(ExceptionType.WX, "文件转换图文素材失败:" + e.getMessage(), e);
		}
	}

	/**
	 * 获取图文素材
	 *
	 * @param mediaId
	 * @return
	 */
	public MpNews getMpnews(String mediaId) {
		return Materials.with(appSetting).getMpNews(mediaId);
	}

	// ================= 素材列表

	/**
	 * 获取所有的视频素材
	 * 
	 * @return
	 */
	public List<MaterialPagination.Material> listVideos() {
		return listMaterials(MediaType.video);
	}

	/**
	 * 获取素有的音频素材
	 * 
	 * @return
	 */
	public List<MaterialPagination.Material> listVoices() {
		return listMaterials(MediaType.voice);
	}

	/**
	 * 获取所有的image素材
	 * 
	 * @return
	 */
	public List<MaterialPagination.Material> listImages() {
		return listMaterials(MediaType.image);
	}

	/**
	 * 获取所有的图文消息
	 * 
	 * @return
	 */
	public List<MpNewsItem> listNews() {
		Counts counts = Materials.with(appSetting).count();
		int total = counts.getNews();
		int pageSize = 50;
		List<MpNewsItem> items = new ArrayList<>();
		if (total != 0) {
			for (int page = 1; pageSize * page <= total; page++) {
				items.addAll(Materials.with(appSetting).listMpNews(pageSize * (page - 1), pageSize).getItems());
			}
		}

		return items;
	}

	// ================= 其他SDK基础能力,直接提供

	public Tickets getTickets() {
		return Tickets.with(appSetting);
	}

	public Urls getUrls() {
		return Urls.with(appSetting);
	}

	/**
	 * 二维码生成助手
	 * 
	 * @return
	 */
	public QRCodeHelper getQrcode() {
		return new QRCodeHelper(appSetting);
	}

	// ================= 私有方法

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private MpNews toMpNews(Map<String, Object> map) {
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
			mpNews.article(toMpArticle(o));
		}
		return mpNews;
	}

	private MpArticle toMpArticle(Map<String, Object> map) {
		MpArticle article = new MpArticle();
		article.setTitle((String) map.get("title"));
		article.setDigest((String) map.get("digest"));
		article.setContent((String) map.get("content"));
		article.contentSourceUrl((String) map.get("url"));
		article.setAuthor((String) map.get("author"));
		article.thumbMediaId((String) map.get("thumbMediaId"));
		if (map.containsKey("cover")) {
			article.setShowCover((Boolean) map.get("cover"));
		} else {
			article.setShowCover(false);
		}

		return article;
	}

	private List<MaterialPagination.Material> listMaterials(MediaType mediaType) {
		Counts counts = Materials.with(appSetting).count();
		int total = 0;
		if (mediaType == MediaType.image) {
			total = counts.getImage();
		} else if (mediaType == MediaType.voice) {
			total = counts.getVoice();
		} else if (mediaType == MediaType.video) {
			total = counts.getVideo();
		} else {
			throw new IllegalArgumentException("mediaType shall be image, voice or video.");
		}
		int pageSize = 50;
		List<MaterialPagination.Material> items = new ArrayList<>();
		if (total != 0) {
			for (int page = 0; pageSize * page <= total; page++) {
				items.addAll(Materials.with(appSetting).list(mediaType, pageSize * page, pageSize).getItems());
			}
		}

		return items;
	}

	/**
	 * 获取jssdk验证串
	 * 
	 * @param url
	 * @return
	 */
	public JsAPISignature signature(String url) {
		return JsAPIs.with(appSetting).createJsAPISignature(url);
	}

	/**
	 * 获取jssdk验证串
	 * 
	 * @return
	 */
	public JsAPISignature signature() {
		String url = RequestContext.getCurrent().getString(Keys.FULL_URL.toString());
		return signature(url);
	}

	/**
	 * 获取accesstoken
	 * 
	 * @return
	 */
	public AccessToken accessToken() {
		return MpWxClientFactory.getInstance().with(appSetting).getAccessToken();
	}

}
