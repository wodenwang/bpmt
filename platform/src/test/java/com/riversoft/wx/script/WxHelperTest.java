package com.riversoft.wx.script;

import com.riversoft.core.BeanFactory;
import com.riversoft.core.Config;
import com.riversoft.platform.script.ScriptHelper;
import com.riversoft.platform.script.ScriptTypes;
import com.riversoft.weixin.qy.base.CorpSetting;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

/**
 * Created by exizhai on 1/10/2016.
 */
@Ignore
public class WxHelperTest {

    @BeforeClass
    public static void beforeClass(){
        BeanFactory.init("classpath:applicationContext-wx-test.xml");

        if(Boolean.valueOf(Config.get("wx.qy.flag", "false"))) {
            String corpId = Config.get("wx.qy.corpId", "");
            String corpSecret = Config.get("wx.qy.corpSecret", "");

            if(StringUtils.isEmpty(corpId) || StringUtils.isEmpty(corpSecret)) {
            } else {
                CorpSetting corpSetting = new CorpSetting(corpId, corpSecret);
                CorpSetting.setDefault(corpSetting);
            }
        }
    }

    @Ignore
    public void testAgentText() throws IOException {
        try {
            String text = readGroovy(this.getClass().getClassLoader().getResource("groovy/wx/agent/message/text.groovy").getFile());
            ScriptHelper.evel(ScriptTypes.GROOVY, text);
        } catch (Exception e) {
            Assert.fail(e.getMessage());
        }
    }

    @Ignore
    public void testAgentNews(){
        try {
            String news = readGroovy(this.getClass().getClassLoader().getResource("groovy/wx/agent/message/news.groovy").getFile());
            ScriptHelper.evel(ScriptTypes.GROOVY, news);
        } catch (Exception e) {
            Assert.fail(e.getMessage());
        }
    }

    @Ignore
    public void testAgentPaySingle(){
        try {
            String news = readGroovy(this.getClass().getClassLoader().getResource("groovy/wx/agent/pay/single.groovy").getFile());
            ScriptHelper.evel(ScriptTypes.GROOVY, news);
        } catch (Exception e) {
            Assert.fail(e.getMessage());
        }
    }

    @Ignore
    public void testAgentPayGroup(){
        try {
            String news = readGroovy(this.getClass().getClassLoader().getResource("groovy/wx/agent/pay/group.groovy").getFile());
            ScriptHelper.evel(ScriptTypes.GROOVY, news);
        } catch (Exception e) {
            Assert.fail(e.getMessage());
        }
    }

    @Ignore
    public void testMpText() throws IOException {
        try {
            String text = readGroovy(this.getClass().getClassLoader().getResource("groovy/wx/mp/message/text.groovy").getFile());
            ScriptHelper.evel(ScriptTypes.GROOVY, text);
        } catch (Exception e) {
            Assert.fail(e.getMessage());
        }
    }

    @Ignore
    public void testMpNews(){
        try {
            String news = readGroovy(this.getClass().getClassLoader().getResource("groovy/wx/mp/message/news.groovy").getFile());
            ScriptHelper.evel(ScriptTypes.GROOVY, news);
        } catch (Exception e) {
            Assert.fail(e.getMessage());
        }
    }

    @Ignore
    public void testMpPaySingle(){
        try {
            String news = readGroovy(this.getClass().getClassLoader().getResource("groovy/wx/script/mp/pay/single.groovy").getFile());
            ScriptHelper.evel(ScriptTypes.GROOVY, news);
        } catch (Exception e) {
            Assert.fail(e.getMessage());
        }
    }

    @Ignore
    public void testMpPayGroup(){
        try {
            String news = readGroovy(this.getClass().getClassLoader().getResource("groovy/wx/script/mp/pay/group.groovy").getFile());
            ScriptHelper.evel(ScriptTypes.GROOVY, news);
        } catch (Exception e) {
            Assert.fail(e.getMessage());
        }
    }

    @Ignore
    public void testMpListImages(){
        try {
            String images =
                    "def images = wx.mp('k1YOwdUYL9X').listImages();\n" +
                    "log.info(images);";
            ScriptHelper.evel(ScriptTypes.GROOVY, images);
        } catch (Exception e) {
            Assert.fail(e.getMessage());
        }
    }

    private String readGroovy(String name) throws IOException {
        return FileUtils.readFileToString(new File(name));
    }

}
