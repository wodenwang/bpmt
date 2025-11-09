/*
 * $HeadURL: $
 * $Id: $
 * Copyright (c) 2013 by Riversoft System, all rights reserved.
 */
package com.riversoft.core.db.hbm;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import com.riversoft.core.db.hbm.model.HbmModelConverter;

/**
 * @author Borball
 * 
 */
public class HbmModelConverterTest {

    @Test
    public void testHbmToBeanA() {
        InputStream in = this.getClass().getClassLoader().getResourceAsStream("hbm_test/A.hbm.xml");
        HbmClass processed = (HbmClass) HbmModelConverter.toBean(in);

        Assert.assertNotNull(processed);
    }

    @Test
    public void testHbmToBeanB() {
        InputStream in = this.getClass().getClassLoader().getResourceAsStream("hbm_test/B.hbm.xml");
        HbmClass processed = (HbmClass) HbmModelConverter.toBean(in);
        Assert.assertNotNull(processed);
    }
    
    
    @Test
    public void testHbmToBeanDemo() {
        InputStream in = this.getClass().getClassLoader().getResourceAsStream("hbm_test/DEMO.hbm.xml");
        HbmClass processed = (HbmClass) HbmModelConverter.toBean(in);
        Assert.assertNotNull(processed);
    }
    
    @Test
    public void testBean2HbmA() {
        bean2Hbm("hbm_test/A.hbm.xml");
    }
    
    @Test
    public void testBean2HbmB() {
        bean2Hbm("hbm_test/B.hbm.xml");
    }
    
    
    @Test
    public void testBean2HbmDemo() {
        bean2Hbm("hbm_test/DEMO.hbm.xml");
    }

    private void bean2Hbm(String testXml) {
        InputStream in = this.getClass().getClassLoader().getResourceAsStream(testXml);
        HbmClass processed = (HbmClass) HbmModelConverter.toBean(in);
        
        ByteArrayOutputStream baos = new ByteArrayOutputStream();  
        HbmModelConverter.toHbmFile(processed, baos);
        
        String xml = baos.toString(); 
        
        Assert.assertNotNull(xml);

        System.out.println(xml);
    }


}
