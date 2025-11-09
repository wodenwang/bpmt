package com.riversoft.wx;

import com.riversoft.core.BeanFactory;
import com.riversoft.core.Config;
import com.riversoft.weixin.qy.base.CorpSetting;
import com.riversoft.wx.qy.service.ContactService;

import org.apache.commons.lang3.StringUtils;
import org.junit.Ignore;

/**
 * Created by exizhai on 10/26/2015.
 */
public class ContactServiceTest {

	// @BeforeClass
	public static void beforeClass() {
		BeanFactory.init("classpath:applicationContext.xml");
		if (Boolean.valueOf(Config.get("wx.qy.flag", "false"))) {
			String corpId = Config.get("wx.qy.corpId", "");
			String corpSecret = Config.get("wx.qy.corpSecret", "");

			if (StringUtils.isEmpty(corpId) || StringUtils.isEmpty(corpSecret)) {
			} else {
				CorpSetting corpSetting = new CorpSetting(corpId, corpSecret);
				CorpSetting.setDefault(corpSetting);
			}
		}
	}

	@Ignore
	public void testSync() {
		ContactService.getInstance().executeSync();
	}

}
