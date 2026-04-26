/*
 * $HeadURL: $
 * $Id: $
 * Copyright (c) 2013 by Riversoft System, all rights reserved.
 */
package com.riversoft.module.development.view;

import com.riversoft.core.BeanFactory;
import com.riversoft.core.IDGenerator;
import com.riversoft.core.db.ORMService;
import com.riversoft.platform.SessionManager;
import com.riversoft.platform.po.VwUrl;
import com.riversoft.platform.template.DevelopmentOperation;
import com.riversoft.platform.web.view.BaseDynamicViewAction;
import com.riversoft.platform.web.view.ViewActionBuilder;

/**
 * @author Woden
 * 
 */
public class ViewConfigService extends ORMService {

	/**
	 * 获取模块绑定对象
	 * 
	 * @param viewClass
	 * @return
	 */
	private BaseDynamicViewAction getBaseDynamicView(String viewClass) {
		return (BaseDynamicViewAction) BeanFactory.getInstance().getSingleBean(
				ViewActionBuilder.getInstance().getViewClass(viewClass));
	}

	/**
	 * 保存
	 * 
	 * @param po
	 */
	@DevelopmentOperation("保存视图")
	public void executeSaveConfig(VwUrl po) {

		// 设置创建人
		po.setCreateUid(SessionManager.getUser().getUid());
		po.setViewKey(IDGenerator.next());
		po.setLockFlag(0);
		super.savePO(po);

		// 创建模块参数
		getBaseDynamicView(po.getViewClass()).saveConfig(po.getViewKey());
	}

	/**
	 * 更新
	 * 
	 * @param po
	 */
	@DevelopmentOperation("更新视图")
	public void executeUpdateConfig(VwUrl po) {
		VwUrl old = (VwUrl) findByPk(VwUrl.class.getName(), po.getViewKey());
		po.setCreateUid(old.getCreateUid());
		po.setViewClass(old.getViewClass());
		po.setCreateDate(old.getCreateDate());
		po.setLockFlag(old.getLockFlag());
		super.mergePO(po);

		// 更新模块参数
		getBaseDynamicView(po.getViewClass()).updateConfig(po.getViewKey());
	}

	/**
	 * 删除
	 * 
	 * @param viewKey
	 */
	@DevelopmentOperation("删除视图")
	public void executeRemoveConfig(String viewKey) {
		VwUrl po = (VwUrl) findByPk(VwUrl.class.getName(), viewKey);

		// 删除模块参数
		getBaseDynamicView(po.getViewClass()).removeConfig(po.getViewKey());

		super.removePO(po);

	}

	/**
	 * 复制
	 * 
	 * @param viewKey
	 * @return
	 */
	@DevelopmentOperation("复制视图")
	public String executeCopyConfig(String viewKey) {
		VwUrl po = (VwUrl) findByPk(VwUrl.class.getName(), viewKey);
		return getBaseDynamicView(po.getViewClass()).copyConfig(po.getViewKey());
	}
}
