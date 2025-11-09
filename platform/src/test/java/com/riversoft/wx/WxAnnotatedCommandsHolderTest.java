package com.riversoft.wx;

import com.riversoft.core.BeanFactory;
import com.riversoft.util.Formatter;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Date;

/**
 * @borball on 4/15/2016.
 */
public class WxAnnotatedCommandsHolderTest {

    @BeforeClass
    public static void beforeClass(){
        BeanFactory.init("classpath:applicationContext-wx-test.xml");
    }

    @Test
    public void testInit(){
        String fileName = newRandomName("xxx.pdf");
        System.out.println(fileName);
    }

    private String newRandomName(String fileName){
        int index = fileName.lastIndexOf(".");
        String ext = null;
        if(index > 0) {
            ext = fileName.substring(index);
        }
        return "WX_" + Formatter.formatDatetime(new Date(), "yyyyMMddHHmmssSSS") + ext;
    }
}
