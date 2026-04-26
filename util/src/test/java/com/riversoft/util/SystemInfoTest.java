/*
 * $HeadURL: $
 * $Id: $
 * Copyright (c) 2013 by Riversoft System, all rights reserved.
 */
package com.riversoft.util;

import java.net.SocketException;

import org.junit.Test;

import com.riversoft.util.sys.SystemInfo;
 
/**
 * @author Borball
 *
 */
public class SystemInfoTest {

    @Test
    public void testGetMacAddress() throws SocketException {
        System.out.println("MAC:" + SystemInfo.getMacAddress());
    }
    
    @Test
    public void testGetOS() {
        System.out.println("OS:" + SystemInfo.getOSInfo());
    }
    
    @Test
    public void testGetIdentifier() {
        System.out.println("Identifier:" + SystemInfo.getIdentifier());
    }

}
