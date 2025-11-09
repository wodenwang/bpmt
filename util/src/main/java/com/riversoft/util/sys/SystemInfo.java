/*
 * $HeadURL: $
 * $Id: $
 * Copyright (c) 2013 by Riversoft System, all rights reserved.
 */
package com.riversoft.util.sys;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Properties;
import java.util.Set;
import java.util.TreeSet;

import org.hyperic.sigar.CpuInfo;
import org.hyperic.sigar.Mem;
import org.hyperic.sigar.Sigar;
import org.hyperic.sigar.SigarException;

/**
 * @author Borball
 * 
 */
public final class SystemInfo {

    public static String getMacAddress() throws SocketException {
        Enumeration<NetworkInterface> networks = NetworkInterface.getNetworkInterfaces();
        Set<String> macs = new TreeSet<>();
        while (networks.hasMoreElements()) {
            NetworkInterface network = networks.nextElement();

            if (!network.isLoopback()) {
                byte[] mac = network.getHardwareAddress();
                if (mac != null) {
                    StringBuilder sb = new StringBuilder();
                    for (int i = 0; i < mac.length; i++) {
                        sb.append(String.format("%02X%s", mac[i], (i < mac.length - 1) ? "-" : ""));
                    }
                    macs.add(sb.toString());
                }
            }

        }

        StringBuffer sb = new StringBuffer();
        Iterator<String> it = macs.iterator();
        while (it.hasNext()) {
            sb.append(it.next()).append("|");
        }
        return sb.toString();
    }

    public static String getOSInfo() {
        Properties props = System.getProperties(); // 获得系统属性集
        String osName = props.getProperty("os.name"); // 操作系统名称
        String osArch = props.getProperty("os.arch"); // 操作系统构架
        return osName + "-" + osArch;
    }

    public static String getHostName() {
        try {
            return InetAddress.getLocalHost().getCanonicalHostName();
        } catch (UnknownHostException e) {
            Sigar sigar = null;
            try {
                sigar = new Sigar();
                return sigar.getFQDN();
            } catch (SigarException ex) {
                return null;
            } finally {
                if (sigar != null) {
                    sigar.close();
                }
            }
        }
    }

    public static String getCPUInfo() {
        StringBuffer sb = new StringBuffer();
        Sigar sigar = new Sigar();
        try {
            CpuInfo[] cpus = sigar.getCpuInfoList();

            for (CpuInfo cpuInfo : cpus) {
                sb.append(cpuInfo.getVendor()).append("-").append(cpuInfo.getModel()).append("+");
            }
        } catch (SigarException e) {
            // ignore
        }
        return sb.toString();
    }

    public static Mem getMem() throws SigarException {
        Sigar sigar = new Sigar();
        return sigar.getMem();
    }

    public static String getIdentifier() {
        try {
            String mac = getMacAddress();
            String osVersion = getOSInfo();
            return mac + osVersion;
        } catch (Exception e) {
            return "";
        }
    }

}
