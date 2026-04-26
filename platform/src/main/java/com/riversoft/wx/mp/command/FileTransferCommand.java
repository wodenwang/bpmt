package com.riversoft.wx.mp.command;

import java.io.File;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.riversoft.platform.translate.WxCommandSupportType;
import com.riversoft.platform.web.FileManager;
import com.riversoft.util.Formatter;
import com.riversoft.weixin.mp.media.Medias;
import com.riversoft.wx.annotation.WxAnnotatedCommand;
import com.riversoft.wx.mp.MpAppSetting;
import com.riversoft.wx.mp.service.MpAppService;

/**
 * 文件中转处理器
 *
 * @borball on 4/15/2016.
 */
@WxAnnotatedCommand(name = "FileTransferCommand", types = { WxCommandSupportType.MESSAGE, WxCommandSupportType.MENU }, desc = "文件中转处理")
public class FileTransferCommand implements MpCommand {

	private Logger logger = LoggerFactory.getLogger(FileTransferCommand.class);

	@Override
	public MpResponse execute(MpRequest request) {
		MpAppSetting mpAppSetting = MpAppService.getInstance().getAppSettingByAppID(request.getAppId());
		String mediaId = null;
		if (request.getImage() != null) {
			mediaId = request.getImage().getMediaId();
		} else if (request.getShortVideo() != null) {
			mediaId = request.getShortVideo().getMediaId();
		} else if (request.getVideo() != null) {
			mediaId = request.getVideo().getMediaId();
		} else if (request.getVoice() != null) {
			mediaId = request.getVoice().getMediaId();
		}

		Map<String, Object> map = new HashMap<>();
		if (StringUtils.isNotBlank(mediaId)) {
			try {
				File file = Medias.with(mpAppSetting).download(mediaId);
				File newFile = new File(FileManager.getCurrentUserFileSpace(), newRandomName(file.getName()));
				FileUtils.copyFile(file, newFile);
				map.put("text", "文件上传成功,请通过PC端查看个人中转区.文件名:[" + newFile.getName() + "]");
			} catch (Exception e) {
				logger.error("保存文件失败.", e);
				map.put("text", "文件接收失败.");
			}
		} else {
			map.put("text", "请通过微信对话框将图片,语音,小视屏等文件直接上传到个人文件中转区.个人文件中转区中的文件可以在PC端网页版文件上传控件中直接使用.");
		}

		MpResponse mpResponse = new MpResponse(map);
		return mpResponse;
	}

	private String newRandomName(String fileName) {
		int index = fileName.lastIndexOf(".");
		String ext = null;
		if (index > 0) {
			ext = fileName.substring(index);
		}
		return "WX_" + Formatter.formatDatetime(new Date(), "yyyyMMddHHmmssSSS") + ext;
	}

}
