/*
 * $HeadURL: $
 * $Id: $
 * Copyright (c) 2013 by Riversoft System, all rights reserved.
 */
package com.riversoft.util;

import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

/**
 * @author Borball
 * 
 */
public class NetworkUtils {

    public static String getMacAddress() throws SocketException {
        Enumeration<NetworkInterface> networks = NetworkInterface.getNetworkInterfaces();

        while (networks.hasMoreElements()) {
            NetworkInterface network = networks.nextElement();

            if (network.isUp() && !network.isLoopback()) {
                byte[] mac = network.getHardwareAddress();
                if (mac != null) {
                    StringBuilder sb = new StringBuilder();
                    for (int i = 0; i < mac.length; i++) {
                        sb.append(String.format("%02X%s", mac[i], (i < mac.length - 1) ? "-" : ""));
                    }
                    return sb.toString();
                }
            }

        }

        return null;
    }
}
