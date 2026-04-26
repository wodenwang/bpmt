/*
 * $HeadURL: $
 * $Id: $
 * Copyright (c) 2013 by Riversoft System, all rights reserved.
 */
package com.riversoft.license.impl;

import com.riversoft.util.jackson.JsonMapper;
import org.junit.Assert;
import org.junit.Test;

import com.riversoft.license.api.Identifier;
import com.riversoft.license.api.Magic;

/**
 * @author Borball
 * 
 */
public class MagicImplTest {

    @Test
    public void testRead() {
        Magic magic = new MagicImpl();

        Identifier identifier = magic.currentIdentifier();
        Assert.assertEquals("linux-test", identifier.getName());
        Assert.assertEquals("linux-test", identifier.getPassword());
        Assert.assertEquals("linux32", identifier.getPlatform());
        Assert.assertEquals(0, identifier.getMaxSessions());
        Assert.assertEquals(5, identifier.getLevel());
        Assert.assertEquals("test-mac|win64", identifier.getIdentifier());
        Assert.assertEquals("66e7db9eef62df93a729c542a7ce69cbce70ea", identifier.getSkey());
        Assert.assertTrue(identifier.isCommercial());
        Assert.assertTrue(identifier.isRegister());
    }
    
    @Test
    public void testWrite() {
        Identifier identifier = new Identifier();
        identifier.setName("linux-test");
        identifier.setPlatform("linux32");
        identifier.setLevel(5);
        identifier.setSkey("66e7db9eef62df93a729c542a7ce69cbce70ea");
        identifier.setPassword("linux-test");
        identifier.setCommercial(true);
        identifier.setRegister(true);

        System.out.println(JsonMapper.defaultMapper().toJson(identifier));
    }

}
