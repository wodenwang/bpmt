/*
 * $HeadURL: $
 * $Id: $
 * Copyright (c) 2012 by Riversoft System, all rights reserved.
 */
package com.riversoft.util;

import java.math.BigInteger;
import java.security.MessageDigest;

/**
 * @author Woden
 * 
 */
public class MD5 {

    public static String md5(String x) {
        try {
            MessageDigest m = MessageDigest.getInstance("MD5"); // 创建一个MD5消息文搞
            m.update(x.getBytes("UTF8")); // 更新被文搞描述的位元组

            byte[] s = m.digest(); // 最后更新使用位元组的被叙述的排列,然后完成文摘计算

            String result = "";

            for (int i = 0; i < s.length; i++) {
                result += Integer.toHexString((0x000000FF & s[i]) | 0xFFFFFF00).substring(6);
                // 进行十六进制转换
            }

            return result;
        } catch (Exception e) {
            return null;
        }
    }

    public static String md5(byte[] bytes) {
        try {
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            md5.update(bytes);
            BigInteger bi = new BigInteger(1, md5.digest());
            return bi.toString(16);
        } catch (Exception e) {
            return null;
        }
    }

}
