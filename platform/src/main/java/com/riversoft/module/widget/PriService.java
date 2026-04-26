/*
 * $HeadURL: $
 * $Id: $
 * Copyright (c) 2013 by Riversoft System, all rights reserved.
 */
package com.riversoft.module.widget;

import com.riversoft.core.db.DataPO;
import com.riversoft.core.db.ORMService;

/**
 * @author Woden
 * 
 */
public class PriService {

    /**
     * 根据权限资源保存与权限组关系
     * 
     * @param groupIds
     * @param priKey
     */
    public void executeSavePriGroup(String[] groupIds, String priKey) {
        ORMService.getInstance().executeHQL("delete from CmPriGroupRelate where priKey = ?", priKey);
        if (groupIds != null) {
            for (String groupId : groupIds) {
                DataPO po = new DataPO("CmPriGroupRelate");
                po.set("priKey", priKey);
                po.set("groupId", groupId);
                ORMService.getInstance().save(po.toEntity());
            }
        }
    }
}
