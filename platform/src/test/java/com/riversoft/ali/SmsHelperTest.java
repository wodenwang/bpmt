package com.riversoft.ali;

import com.riversoft.core.BeanFactory;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

/**
 * @borball on 3/7/2016.
 */
@Ignore
public class SmsHelperTest {

    @BeforeClass
    public static void beforeClass() {
        BeanFactory.init("classpath:applicationContext-ali-test.xml");
    }

    @Test
    public void testCode(){
        SMSHelper.code("13926012004");
    }
}
