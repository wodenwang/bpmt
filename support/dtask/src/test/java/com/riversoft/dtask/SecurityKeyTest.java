/*
 * $HeadURL: $
 * $Id: $
 * Copyright (c) 2013 by Riversoft System, all rights reserved.
 */
package com.riversoft.dtask;

import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

import org.junit.Test;

/**
 * @author Borball
 * 
 */
public class SecurityKeyTest {

    @Test
    public void testGeneration() {
        try {
            Enumeration<NetworkInterface> networks = NetworkInterface.getNetworkInterfaces();
            while (networks.hasMoreElements()) {
                NetworkInterface network = networks.nextElement();
                if (network.isUp() && !network.isLoopback()) {
                    byte[] mac = network.getHardwareAddress();

                    if (mac != null) {
                        System.out.println("DisplayName:" + network.getDisplayName());
                        System.out.println("Name:" + network.getName());
                        System.out.println("MTU:" + network.getMTU());
                        System.out.println("isLoopback:" + network.isLoopback());
                        System.out.println("isPointToPoint:" + network.isPointToPoint());
                        System.out.println("isUp:" + network.isUp());
                        System.out.println("isVirtual:" + network.isVirtual());
                        System.out.println("supportsMulticast:" + network.supportsMulticast());

                        StringBuilder sb = new StringBuilder();
                        for (int i = 0; i < mac.length; i++) {
                            sb.append(String.format("%02X%s", mac[i], (i < mac.length - 1) ? "-" : ""));
                        }
                        System.out.println(sb.toString());
                        System.out.println();
                    }
                }

            }
        } catch (SocketException e) {
            e.printStackTrace();
        }
    }
}
