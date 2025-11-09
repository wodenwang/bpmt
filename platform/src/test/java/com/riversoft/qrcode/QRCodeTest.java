package com.riversoft.qrcode;

import com.riversoft.platform.script.function.QRCodeHelper;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;

/**
 * Created by exizhai on 1/30/2016.
 */
public class QRCodeTest {

    @Test
    public void testUsage(){
        File file = QRCodeHelper.file("这是一段中文", 300, 300, 0, 40000);
        Assert.assertNotNull(file);

        file = QRCodeHelper.from("http://docs.gzriver.com").withSize(200,200).withColor(100, 30000).file();
        Assert.assertNotNull(file);

    }
}
