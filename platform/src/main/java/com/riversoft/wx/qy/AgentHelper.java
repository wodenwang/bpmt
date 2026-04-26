package com.riversoft.wx.qy;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.riversoft.core.exception.ExceptionType;
import com.riversoft.core.exception.SystemRuntimeException;
import com.riversoft.platform.web.FileManager;
import com.riversoft.platform.web.FileManager.UploadFile;
import com.riversoft.util.OfficeUtils;
import com.riversoft.weixin.common.media.MediaType;
import com.riversoft.weixin.common.media.MpArticle;
import com.riversoft.weixin.common.media.MpNews;
import com.riversoft.weixin.qy.base.CorpSetting;
import com.riversoft.weixin.qy.media.Materials;
import com.riversoft.weixin.qy.media.bean.Counts;
import com.riversoft.weixin.qy.media.bean.MaterialPagination.Material;
import com.riversoft.weixin.qy.media.bean.MpNewsPagination.MpNewsItem;
import com.riversoft.weixin.qy.message.Messages;
import com.riversoft.wx.qy.builder.MessageBuilder;

/**
 * Created by exizhai on 11/10/2015.
 */
public class AgentHelper {

	private Logger logger = LoggerFactory.getLogger(AgentHelper.class);

	private int id;
	private String secret;
	private String corpId;

	public AgentHelper(String corpId, String secret, int id) {
		this.corpId = corpId;
		this.secret = secret;
		this.id = id;
	}

	public AgentPayHelper getPay() {
		return new AgentPayHelper(id);
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void text(Map<String, Object> text) {
		Messages.with(new CorpSetting(corpId, secret)).send(MessageBuilder.text(text).agentId(id));
	}

	public void image(Map<String, Object> image) {
		Messages.with(new CorpSetting(corpId, secret)).send(MessageBuilder.image(image).agentId(id));
	}

	public void voice(Map<String, Object> voice) {
		Messages.with(new CorpSetting(corpId, secret)).send(MessageBuilder.voice(voice).agentId(id));
	}

	public void video(Map<String, Object> video) {
		Messages.with(new CorpSetting(corpId, secret)).send(MessageBuilder.video(video).agentId(id));
	}

	public void news(Map<String, Object> news) {
		Messages.with(new CorpSetting(corpId, secret)).send(MessageBuilder.news(news).agentId(id));
	}

	public void mpnews(Map<String, Object> map) {
		Messages.with(new CorpSetting(corpId, secret)).send(MessageBuilder.mpnews(map).agentId(id));
	}

	public void mpnews(String mediaId) {
		Messages.with(new CorpSetting(corpId, secret)).send(MessageBuilder.mpnews(mediaId).agentId(id));
	}

	public void file(Map<String, Object> file) {
		Messages.with(new CorpSetting(corpId, secret)).send(MessageBuilder.file(file).agentId(id));
	}

	// ===文件上传下载

	/**
	 * 上传永久文件
	 *
	 * @param obj
	 * @return
	 */
	public String upload(byte[] obj) {
		String[] mediaIds = uploads(obj);
		return mediaIds[0];
	}

	/**
	 * 上传永久文件
	 *
	 * @param obj
	 * @return
	 */
	public String[] uploads(byte[] obj) {
		List<UploadFile> fileList = FileManager.toFiles(obj);
		if (fileList == null || fileList.size() < 1) {
			throw new SystemRuntimeException(ExceptionType.SCRIPT, "解析文件不存在.");
		}
		List<String> mediaIds = new ArrayList<>();
		for (UploadFile file : fileList) {
			String fileName = file.getName();
			String extName = fileName.substring(fileName.lastIndexOf(".") + 1);
			MediaType type;

			if (ArrayUtils.contains(StringUtils.split("png|jpg|jpeg", "|"), extName)) {
				type = MediaType.image;
			} else if (ArrayUtils.contains(StringUtils.split("mp4|avi|rmvb", "|"), extName)) {
				type = MediaType.video;
			} else if (ArrayUtils.contains(StringUtils.split("mp3|wma|wav|amr", "|"), extName)) {
				type = MediaType.voice;
			} else if (ArrayUtils.contains(StringUtils.split("doc|docx|ppt|pptx|xls|xlsx|txt|zip|xml|pdf", "|"), extName)) {
				type = MediaType.file;
			} else {
				throw new SystemRuntimeException(ExceptionType.BUSINESS, "文件[" + file.getName() + "]类型不支持上传到微信.");
			}

			try {
				String mediaId = Materials.with(new CorpSetting(corpId, secret)).upload(id, type, file.getInputStream(), fileName);
				mediaIds.add(mediaId);
			} catch (FileNotFoundException e) {
				throw new SystemRuntimeException(e);
			}
		}
		return mediaIds.toArray(new String[0]);
	}

	/**
	 * 下载永久文件
	 *
	 * @param mediaIds
	 * @return
	 */
	public byte[] download(String... mediaIds) {
		List<UploadFile> fileList = new ArrayList<>();
		for (String mediaId : mediaIds) {
			File file = Materials.with(new CorpSetting(corpId, secret)).download(id, mediaId);
			try {
				UploadFile uploadFile = new FileManager.DevFile(file.getName(), FileUtils.readFileToByteArray(file));
				fileList.add(uploadFile);
			} catch (IOException e) {
				throw new SystemRuntimeException(e);
			}
		}
		return FileManager.toBytes(null, fileList);
	}

	/**
	 * 下载永久文件
	 * 
	 * @param mediaId
	 * @return
	 */
	public File downloadFile(String mediaId) {
		File file = Materials.with(new CorpSetting(corpId, secret)).download(id, mediaId);
		return file;
	}

	// ===图文编辑

	/**
	 * 上传mpnews里面要使用的图片
	 * 
	 * @param obj
	 * @return
	 */
	public String addMpnewsImage(byte[] obj) {
		String[] urls = addMpnewsImages(obj);
		return urls[0];
	}

	/**
	 * 上传mpnews里面要使用的图片
	 * 
	 * @param obj
	 * @return
	 */
	public String[] addMpnewsImages(byte[] obj) {
		List<UploadFile> fileList = FileManager.toFiles(obj);
		if (fileList == null || fileList.size() < 1) {
			throw new SystemRuntimeException(ExceptionType.SCRIPT, "解析文件不存在.");
		}
		List<String> urls = new ArrayList<>();
		for (UploadFile file : fileList) {
			String fileName = file.getName();
			String extName = fileName.substring(fileName.lastIndexOf(".") + 1);

			if (!ArrayUtils.contains(StringUtils.split("png|jpg|jpeg", "|"), extName)) {
				throw new SystemRuntimeException(ExceptionType.BUSINESS, "文件[" + file.getName() + "]不是微信支持的图片类型.");
			}

			try {
				String url = Materials.with(new CorpSetting(corpId, secret)).addMpNewsImage(file.getInputStream(), fileName);
				urls.add(url);
			} catch (FileNotFoundException e) {
				throw new SystemRuntimeException(e);
			}
		}
		return urls.toArray(new String[0]);
	}

	/**
	 * 上传图文
	 *
	 * @param map
	 * @return
	 */
	public String addMpnews(Map<String, Object> map) {
		MpNews mpNews = toMpNews(map);

		return Materials.with(new CorpSetting(corpId, secret)).addMpNews(id, mpNews);
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

		String pixel = "ppt";// 文件后缀,默认ppt
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
				coverMediaId = com.riversoft.weixin.qy.media.Materials.with(new CorpSetting(corpId, secret)).upload(id, MediaType.image, coverStream, coverName);
			}

			List<String> imageUrls = new ArrayList<>();
			for (File image : images) {
				logger.debug("上传[{}]", image);
				retry: for (int i = 1; i <= 5; i++) {
					try (InputStream fis = new FileInputStream(image)) {
						imageUrls.add(com.riversoft.weixin.qy.media.Materials.with(new CorpSetting(corpId, secret)).addMpNewsImage(fis, image.getName()));
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

			String mpMediaId = com.riversoft.weixin.qy.media.Materials.with(new CorpSetting(corpId, secret)).addMpNews(id, mpNews);
			return mpMediaId;
		} catch (Exception e) {
			throw new SystemRuntimeException(ExceptionType.WX, "文件转换图文素材失败:" + e.getMessage(), e);
		}
	}

	/**
	 * 删除图文
	 *
	 * @param mediaId
	 * @return
	 */
	public void deleteMpnews(String mediaId) {
		Materials.with(new CorpSetting(corpId, secret)).delete(id, mediaId);
	}

	/**
	 * 修改图文
	 *
	 * @param map
	 * @return
	 */
	public void updateMpnews(String mediaId, Map<String, Object> map) {
		MpNews mpNews = toMpNews(map);
		Materials.with(new CorpSetting(corpId, secret)).updateMpNews(id, mediaId, mpNews);
	}

	/**
	 * 获取图文
	 *
	 * @param mediaId
	 * @return
	 */
	public MpNews getMpnews(String mediaId) {
		return Materials.with(new CorpSetting(corpId, secret)).getMpNews(id, mediaId);
	}

	// ================= 素材列表

	/**
	 * 获取所有的图文消息
	 * 
	 * @return
	 */
	public List<MpNewsItem> listMpnews() {
		Counts counts = Materials.with(new CorpSetting(corpId, secret)).count(id);
		int total = counts.getMpNews();
		int pageSize = 50;
		List<MpNewsItem> items = new ArrayList<>();
		if (total != 0) {
			for (int page = 1; pageSize * page <= total; page++) {
				items.addAll(Materials.with(new CorpSetting(corpId, secret)).listMpNews(id, pageSize * (page - 1), pageSize).getItems());
			}
		}

		return items;
	}

	/**
	 * 获取所有的视频素材
	 * 
	 * @return
	 */
	public List<Material> listVideos() {
		return listMaterials(MediaType.video);
	}

	/**
	 * 获取素有的音频素材
	 * 
	 * @return
	 */
	public List<Material> listVoices() {
		return listMaterials(MediaType.voice);
	}

	/**
	 * 获取所有的image素材
	 * 
	 * @return
	 */
	public List<Material> listImages() {
		return listMaterials(MediaType.image);
	}

	/**
	 * 获取所有的files素材
	 * 
	 * @return
	 */
	public List<Material> listFiles() {
		return listMaterials(MediaType.file);
	}

	private List<Material> listMaterials(MediaType mediaType) {
		Counts counts = Materials.with(new CorpSetting(corpId, secret)).count(id);
		int total = 0;
		if (mediaType == MediaType.image) {
			total = counts.getImage();
		} else if (mediaType == MediaType.voice) {
			total = counts.getVoice();
		} else if (mediaType == MediaType.video) {
			total = counts.getVideo();
		} else if (mediaType == MediaType.file) {
			total = counts.getVideo();
		} else {
			throw new IllegalArgumentException("mediaType shall be image, voice, video or file.");
		}

		int pageSize = 50;
		List<Material> items = new ArrayList<>();
		if (total != 0) {
			for (int page = 0; pageSize * page <= total; page++) {
				items.addAll(Materials.with(new CorpSetting(corpId, secret)).list(id, mediaType, pageSize * page, pageSize).getItems());
			}
		}

		return items;
	}

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
}
