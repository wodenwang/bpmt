/*
 * $HeadURL: $
 * $Id: $
 * Copyright (c) 2013 by Riversoft System, all rights reserved.
 */
package com.riversoft.module.manager.user;

/**
 * 用户名称分类
 * 
 * @author woden
 * 
 */
enum UserFolder {
    A("A"), B("B"), C("C"), D("D"), E("E"), F("F"), G("G"), H("H"), I("I"), J("J"), K("K"), L("L"), M("M"), N("N"), O(
            "O"), P("P"), Q("Q"), R("R"), S("S"), T("T"), U("U"), V("V"), W("W"), X("X"), Y("Y"), Z("Z"), OTHER("其他");
    private String busiName;

    private UserFolder(String busiName) {
        this.busiName = busiName;
    }

    public String getBusiName() {
        return busiName;
    }

    /**
     * 根据uid获取对应文件夹
     * 
     * @param uid
     * @return
     */
    public static UserFolder findFolder(String uid) {
        if (uid == null || uid.length() == 0) {
            return OTHER;
        }
        // 截取第一个字符
        String startWord = uid.substring(0, 1).toUpperCase();
        try {
            UserFolder folder = UserFolder.valueOf(startWord);
            if (folder != null) {
                return folder;
            }
        } catch (Throwable e) {
            // do nothing
        }

        return OTHER;
    }

    /**
     * 获取文件夹KEY
     * 
     * @return
     */
    public String getKey() {
        return "_folder_" + this.name();
    }
}
