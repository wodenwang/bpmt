/*
 * $HeadURL: $
 * $Id: $
 * Copyright (c) 2013 by Riversoft System, all rights reserved.
 */
package com.riversoft.platform.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.riversoft.core.db.ORMService;
import com.riversoft.platform.po.CmPriGroupRelate;
import com.riversoft.platform.template.DevelopmentOperation;

/**
 * @author woden
 * 
 */
public class PriService {
	/**
	 * 保存权限组对应关系
	 * 
	 * @param groupId
	 * @param checkedPriKeys
	 * @param priGroupRelates
	 */
	@DevelopmentOperation("保存权限组对应关系")
	public void executePriGroupRelate(String groupId, String[] checkedPriKeys, List<CmPriGroupRelate> priGroupRelates) {
		ORMService service = ORMService.getInstance();
		List<String> checkedKeys = checkedPriKeys != null ? Arrays.asList(checkedPriKeys) : new ArrayList<String>();
		for (CmPriGroupRelate vo : priGroupRelates) {
			String priKey = vo.getPriKey();
			if (checkedKeys.contains(priKey)) {// 被选中
				service.saveOrUpdatePO(vo);
			} else {// 未被选中
				service.removePO(vo);
			}
		}

	}
}
