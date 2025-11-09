package com.riversoft.wx.mp;

import com.riversoft.weixin.mp.base.AppSetting;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

/**
 * @borball on 4/2/2016.
 */
public class QRHelperTest {

    static AppSetting appSetting = new AppSetting("wxd1a32e23ee80bf7a", "bffee5473c92c1399367495cbb5bdbc2");
    static QRCodeHelper qrCodeHelper;

    @BeforeClass
    public static void beforeClass() throws Exception {
        qrCodeHelper = new QRCodeHelper(appSetting);
    }

    @Ignore
    public void testTemp(){
        String qrcodeMediaId = qrCodeHelper.temporary(7200, 100);
        Assert.assertNotNull(qrcodeMediaId);
    }

}
