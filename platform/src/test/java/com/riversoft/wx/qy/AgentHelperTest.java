package com.riversoft.wx.qy;

import com.riversoft.weixin.qy.base.CorpSetting;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

/**
 * @borball on 4/4/2016.
 */
public class AgentHelperTest {

	private static CorpSetting corpSetting;
	private static AgentHelper agentHelper;

	@BeforeClass
	public static void beforeClass() {
		corpSetting = new CorpSetting("wxf0e27b947cdba6f4", "586JbRwtF9Ndsch6wPihzMKfu_Hv9r0UmjZXrELbBsXQCY2Bgc33Dln4gvlRsgAw");
		CorpSetting.setDefault(corpSetting);
		agentHelper = new AgentHelper("wxf0e27b947cdba6f4", "586JbRwtF9Ndsch6wPihzMKfu_Hv9r0UmjZXrELbBsXQCY2Bgc33Dln4gvlRsgAw", 84);
	}

	@Ignore
	public void testPpt2MpNews() {
		Path ppt = Paths.get("src/test/resources/wx/高中物理第二册电场总复习3.ppt");
		Map<String, Object> map = new HashMap<>();
		map.put("author", "riversoft");
		map.put("title", "高中物理第二册电场总复习3");
		map.put("digest", "⑴了解两种电荷，电荷守恒；理解真空中的库仑定律，知道基本电荷。" +
				"⑵理解电场力的性质、电场强度、电场线、点电荷的场强、匀强电场、电场强度的叠加。" +
				"⑶理解电场能的性质、电势能、电势差、电势、等势面；理解匀强电场中电势差跟电场强度的关系。");
		Map<String, Object> option = new HashMap<>();
		option.put("footer", "<br/>识别二维码关注创河软件.");
		String mediaId = agentHelper.file2mpnews(map, ppt.toFile(), option);
		Assert.assertNotNull(mediaId);
	}

	@Ignore
	public void testPdf2MpNews() throws FileNotFoundException {
		Path pdf = Paths.get("src/test/resources/wx/一步到位实现MySQL优化20141024.pdf");
		Map<String, Object> map = new HashMap<>();
		map.put("author", "riversoft");
		map.put("title", "一步到位实现MySQL优化");
		map.put("digest", "一步到位实现MySQL优化");

		Map<String, Object> option = new HashMap<>();
		option.put("footer", "<br/>识别二维码关注创河软件.");
		option.put("filetype", "pdf");

		String mediaId = agentHelper.file2mpnews(map, new FileInputStream(pdf.toFile()), option);
		Assert.assertNotNull(mediaId);
	}

	@Ignore
	public void testWord2MpNews() {
		Path word = Paths.get("src/test/resources/wx/BPM-Table用户手册-动态脚本.doc");
		Map<String, Object> map = new HashMap<>();
		map.put("author", "riversoft");
		map.put("title", "BPM-Table用户手册-动态脚本");
		map.put("digest", "在BPM-Table系统中，支持开发、配置人员通过界面直接编写逻辑代码，这种代码称为动态脚本。" +
				"动态代码在整个BPM-Table系统中无处不在，无论在视图、控件、工作流配置中都会大量使用动态脚本。");
		Map<String, Object> option = new HashMap<>();
		option.put("footer", "<br/>识别二维码关注创河软件.");

		String mediaId = agentHelper.file2mpnews(map, word.toFile(), option);
		Assert.assertNotNull(mediaId);
	}
}
