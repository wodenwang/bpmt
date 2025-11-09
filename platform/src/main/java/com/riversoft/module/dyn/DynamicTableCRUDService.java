/*
 * $HeadURL: $
 * $Id: $
 * Copyright (c) 2013 by Riversoft System, all rights reserved.
 */
package com.riversoft.module.dyn;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.riversoft.core.db.DataPO;
import com.riversoft.core.db.ORMService;
import com.riversoft.core.db.po.Code2NameVO;
import com.riversoft.core.exception.ExceptionType;
import com.riversoft.core.exception.SystemRuntimeException;
import com.riversoft.core.web.ELUtils;
import com.riversoft.platform.SessionManager;
import com.riversoft.platform.db.ORMAdapterService;
import com.riversoft.platform.script.ScriptHelper;
import com.riversoft.platform.script.ScriptTypes;
import com.riversoft.util.MD5;
import com.riversoft.util.dynamicbean.DynamicBeanUtils;

/**
 * 动态表查询服务
 * 
 * @author woden
 * 
 */
class DynamicTableCRUDService extends ORMAdapterService {
	/**
	 * Logger for this class
	 */
	private static final Logger logger = LoggerFactory.getLogger(DynamicTableCRUDService.class);

	enum OprType implements Code2NameVO {
		ADD(1, "新增"),
		UPDATE(2, "修改"),
		DELETE(3, "删除");

		private int code;
		private String name;

		@Override
		public Object getCode() {
			return this.code;
		}

		@Override
		public String getShowName() {
			return name;
		}

		OprType(int code, String name) {
			this.code = code;
			this.name = name;
		}

	}

	private String viewKey;

	/**
	 * 由action设置
	 * 
	 * @param viewKey
	 */
	void setViewKey(String viewKey) {
		this.viewKey = viewKey;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Map<String, Object> saveAndReturnPk(Map<String, Object> po) {

		// 查找处理器
		Map<String, Object> table = (Map<String, Object>) ORMService.getInstance().findByPk("VwDynTable", viewKey);
		if (table == null) {
			throw new SystemRuntimeException(ExceptionType.CONFIG, "视图已删除.无法执行此操作.");
		}

		// 前置处理
		for (Map<String, Object> exec : (Set<Map<String, Object>>) table.get("beforeExecs")) {
			Integer type = (Integer) exec.get("execType");
			String script = (String) exec.get("execScript");
			if (StringUtils.isNotEmpty(script)) {
				Map<String, Object> context = new HashMap<String, Object>();
				context.put("vo", po);
				context.put("mode", ExecMode.CREATE.getValue());// 新增
				ScriptHelper.evel(ScriptTypes.forCode(type), script, context);
			}
		}

		Map<String, Object> pk = super.saveAndReturnPk(po);

		String logTable = (String) table.get("logTable");
		if (StringUtils.isNotEmpty(logTable)) {
			addInsertLog(table, pk, po);
		}

		// 后置处理
		for (Map<String, Object> exec : (Set<Map<String, Object>>) table.get("afterExecs")) {
			Integer type = (Integer) exec.get("execType");
			String script = (String) exec.get("execScript");
			if (StringUtils.isNotEmpty(script)) {
				Map<String, Object> context = new HashMap<String, Object>();
				context.put("vo", po);
				context.put("mode", ExecMode.CREATE.getValue());// 新增
				ScriptHelper.evel(ScriptTypes.forCode(type), script, context);
			}
		}

		return pk;
	}

	private void addInsertLog(Map<String, Object> table, Map<String, Object> pk, Map<String, Object> record) {
		Set<Map<String, Object>> columns = (Set<Map<String, Object>>) table.get("columns");

		Collection<Map<String, Object>> pos = new ArrayList<>();
		long batchId = System.currentTimeMillis();
		Date now = new Date();

		for (Map<String, Object> column : columns) {
			String columnName = (String) column.get("name");
			if (record.get(columnName) != null) {
				DataPO logPO = new DataPO((String) table.get("logTable"));
				logPO.set(DynLogModelKeys.TABLE_NAME.name(), table.get("name"));
				logPO.set(DynLogModelKeys.OPR_UID.name(), SessionManager.getUser().getUid());
				logPO.set(DynLogModelKeys.OPR_TYPE.name(), OprType.ADD.code);
				for (String pki : pk.keySet()) {
					logPO.set(pki, pk.get(pki));
				}
				logPO.set(DynLogModelKeys.BATCH_ID.name(), batchId);
				logPO.set(DynLogModelKeys.OPR_TIME.name(), now);
				logPO.set(DynLogModelKeys.FIELD_VAL.name(), columnName);
				logPO.set(DynLogModelKeys.FIELD_DISPLAY.name(), column.get("busiName"));

				if (record.get(columnName) instanceof byte[]) {
					logPO.set(DynLogModelKeys.NEW_VAL.name(), "BLOB");
					logPO.set(DynLogModelKeys.NEW_DISPLAY.name(), "[文件数据]");
				} else {
					logPO.set(DynLogModelKeys.NEW_VAL.name(), record.get(columnName).toString());
					logPO.set(DynLogModelKeys.NEW_DISPLAY.name(),
							ELUtils.widget((String) column.get("widget"), record.get(columnName)));
				}

				pos.add(logPO.toEntity());
			}
		}
		ORMAdapterService.getInstance().saveBatch(pos);
	}

	@Override
	public Map<String, Object> updateAndReturnPk(Map<String, Object> po) {

		// 查找处理器
		Map<String, Object> table = (Map<String, Object>) ORMService.getInstance().findByPk("VwDynTable", viewKey);
		if (table == null) {
			throw new SystemRuntimeException(ExceptionType.CONFIG, "视图已删除.无法执行此操作.");
		}

		String entityName = getEntityName(po);

		Map<String, Object> oldData = (Map<String, Object>) findByPk(entityName, (Serializable) po);

		// 前置处理
		for (Map<String, Object> exec : (Set<Map<String, Object>>) table.get("beforeExecs")) {
			Integer type = (Integer) exec.get("execType");
			String script = (String) exec.get("execScript");
			if (StringUtils.isNotEmpty(script)) {
				Map<String, Object> context = new HashMap<String, Object>();
				context.put("vo", po);
				context.put("mode", ExecMode.EDIT.getValue());// 修改
				ScriptHelper.evel(ScriptTypes.forCode(type), script, context);
			}
		}

		Map<String, Object> pk = super.updateAndReturnPk(po);

		String logTable = (String) table.get("logTable");
		if (StringUtils.isNotEmpty(logTable)) {
			Map<String, Object> newData = new HashMap<>(po);
			addUpdateHistory(table, pk, oldData, newData);
		}

		// 后置处理
		for (Map<String, Object> exec : (Set<Map<String, Object>>) table.get("afterExecs")) {
			Integer type = (Integer) exec.get("execType");
			String script = (String) exec.get("execScript");
			if (StringUtils.isNotEmpty(script)) {
				Map<String, Object> context = new HashMap<String, Object>();
				context.put("vo", po);
				context.put("mode", ExecMode.EDIT.getValue());// 修改
				ScriptHelper.evel(ScriptTypes.forCode(type), script, context);
			}
		}

		return pk;
	}

	private void addUpdateHistory(Map<String, Object> table, Map<String, Object> pk, Map<String, Object> oldData,
			Map<String, Object> newData) {
		Set<Map<String, Object>> columns = (Set<Map<String, Object>>) table.get("columns");

		Collection<Map<String, Object>> pos = new ArrayList<>();
		long batchId = System.currentTimeMillis();
		Date now = new Date();

		for (Map<String, Object> column : columns) {
			String columnName = (String) column.get("name");
			if (!same(oldData.get(columnName), newData.get(columnName))) {
				DataPO logPO = new DataPO((String) table.get("logTable"));
				logPO.set(DynLogModelKeys.TABLE_NAME.name(), table.get("name"));
				logPO.set(DynLogModelKeys.OPR_UID.name(), SessionManager.getUser().getUid());
				logPO.set(DynLogModelKeys.OPR_TYPE.name(), OprType.UPDATE.code);
				for (String pki : pk.keySet()) {
					logPO.set(pki, pk.get(pki));
				}
				logPO.set(DynLogModelKeys.BATCH_ID.name(), batchId);
				logPO.set(DynLogModelKeys.OPR_TIME.name(), now);
				logPO.set(DynLogModelKeys.FIELD_VAL.name(), columnName);
				logPO.set(DynLogModelKeys.FIELD_DISPLAY.name(), column.get("busiName"));

				if (oldData.get(columnName) != null) {
					if (oldData.get(columnName) instanceof byte[]) {
						logPO.set(DynLogModelKeys.OLD_VAL.name(), "BLOB");
						logPO.set(DynLogModelKeys.OLD_DISPLAY.name(), "[文件数据]");
					} else {
						logPO.set(DynLogModelKeys.OLD_VAL.name(), oldData.get(columnName).toString());
						logPO.set(DynLogModelKeys.OLD_DISPLAY.name(),
								ELUtils.widget((String) column.get("widget"), oldData.get(columnName)));
					}
				}

				if (newData.get(columnName) != null) {
					if (newData.get(columnName) instanceof byte[]) {
						logPO.set(DynLogModelKeys.NEW_VAL.name(), "BLOB");
						logPO.set(DynLogModelKeys.NEW_DISPLAY.name(), "[文件数据]");
					} else {
						logPO.set(DynLogModelKeys.NEW_VAL.name(), newData.get(columnName).toString());
						logPO.set(DynLogModelKeys.NEW_DISPLAY.name(),
								ELUtils.widget((String) column.get("widget"), newData.get(columnName)));
					}
				}

				pos.add(logPO.toEntity());
			}

		}
		ORMAdapterService.getInstance().saveBatch(pos);
	}

	private boolean same(Object o1, Object o2) {
		if (o1 == null) {
			return o2 == null;
		} else {
			if (o2 == null) {
				return false;
			} else {
				if (o1 instanceof byte[]) {
					if (o2 instanceof byte[]) {
						return MD5.md5((byte[]) o1).equals(MD5.md5((byte[]) o2));
					} else {
						return false;
					}
				} else if (o1 instanceof Date) {
					if (o2 instanceof Date) {
						return ((Date) o1).compareTo((Date) o2) == 0;
					} else {
						return false;
					}
				} else if (o1 instanceof Number) {
					if (o2 instanceof Number) {
						return ((Number) o1).doubleValue() == ((Number) o2).doubleValue();
					} else {
						return false;
					}
				} else {
					return o1.equals(o2);
				}
			}
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public void removeByPkBath(String entityName, Collection<Serializable> list) {
		if (list == null || list.size() == 0) {
			return;
		}
		try {
			for (Serializable pk : list) {
				Map<String, Object> po = (Map<String, Object>) findByPk(entityName, pk);
				remove(po, System.currentTimeMillis());
			}
		} catch (SystemRuntimeException e) {
			throw e;
		} catch (Throwable e) {
			throw new SystemRuntimeException(ExceptionType.DB, "批量删除出错.", e);
		}
	}

	public void remove(Map<String, Object> po, long historyBatchId) {
		// 查找处理器
		Map<String, Object> table = (Map<String, Object>) ORMService.getInstance().findByPk("VwDynTable", viewKey);
		if (table == null) {
			throw new SystemRuntimeException(ExceptionType.CONFIG, "视图已删除.无法执行此操作.");
		}

		// 前置处理
		for (Map<String, Object> exec : (Set<Map<String, Object>>) table.get("beforeExecs")) {
			Integer type = (Integer) exec.get("execType");
			String script = (String) exec.get("execScript");
			if (StringUtils.isNotEmpty(script)) {
				Map<String, Object> context = new HashMap<String, Object>();
				context.put("vo", po);
				context.put("mode", ExecMode.DELETE.getValue());// 删除
				ScriptHelper.evel(ScriptTypes.forCode(type), script, context);
			}
		}

		Map<String, Object> clonePO = DynamicBeanUtils.cloneMap(po);
		super.remove(po);

		String logTable = (String) table.get("logTable");
		if (StringUtils.isNotEmpty(logTable)) {
			addRemoveHistory(table, historyBatchId, po);
		}

		// 后置处理
		for (Map<String, Object> exec : (Set<Map<String, Object>>) table.get("afterExecs")) {
			Integer type = (Integer) exec.get("execType");
			String script = (String) exec.get("execScript");
			if (StringUtils.isNotEmpty(script)) {
				Map<String, Object> context = new HashMap<String, Object>();
				context.put("vo", clonePO);
				context.put("mode", ExecMode.DELETE.getValue());// 删除
				ScriptHelper.evel(ScriptTypes.forCode(type), script, context);
			}
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public void remove(Map<String, Object> po) {
		remove(po, -1);
	}

	private void addRemoveHistory(Map<String, Object> table, long historyBatchId, Map<String, Object> record) {
		Set<Map<String, Object>> columns = (Set<Map<String, Object>>) table.get("columns");

		Collection<Map<String, Object>> pos = new ArrayList<>();

		long batchId = historyBatchId > 0 ? historyBatchId : System.currentTimeMillis();
		Date now = new Date();

		for (Map<String, Object> column : columns) {
			String columnName = (String) column.get("name");
			DataPO logPO = new DataPO((String) table.get("logTable"));
			logPO.set(DynLogModelKeys.TABLE_NAME.name(), table.get("name"));

			Map<String, Object> pk = getPk(record);
			for (String pki : pk.keySet()) {
				logPO.set(pki, pk.get(pki));
			}

			logPO.set(DynLogModelKeys.OPR_UID.name(), SessionManager.getUser().getUid());
			logPO.set(DynLogModelKeys.OPR_TYPE.name(), OprType.DELETE.code);
			logPO.set(DynLogModelKeys.BATCH_ID.name(), batchId);
			logPO.set(DynLogModelKeys.OPR_TIME.name(), now);
			logPO.set(DynLogModelKeys.FIELD_VAL.name(), columnName);
			logPO.set(DynLogModelKeys.FIELD_DISPLAY.name(), column.get("busiName"));

			if (record.get(columnName) != null) {
				if (record.get(columnName) instanceof byte[]) {
					logPO.set(DynLogModelKeys.OLD_VAL.name(), "BLOB");
					logPO.set(DynLogModelKeys.OLD_DISPLAY.name(), "[文件数据]");
				} else {
					logPO.set(DynLogModelKeys.OLD_VAL.name(), record.get(columnName).toString());
					logPO.set(DynLogModelKeys.OLD_DISPLAY.name(),
							ELUtils.widget((String) column.get("widget"), record.get(columnName)));
				}
			}

			logPO.set(DynLogModelKeys.NEW_VAL.name(), "");
			logPO.set(DynLogModelKeys.NEW_DISPLAY.name(), "");

			pos.add(logPO.toEntity());
		}
		ORMAdapterService.getInstance().saveBatch(pos);
	}

	/**
	 * PO前置处理
	 * 
	 * @param po
	 * @param editMode
	 */
	@SuppressWarnings("unchecked")
	private void preparePO(Map<String, Object> po, boolean editMode) {

		// 查找处理器
		Map<String, Object> table = (Map<String, Object>) ORMService.getInstance().findByPk("VwDynTable", viewKey);
		if (table == null) {
			throw new SystemRuntimeException(ExceptionType.CONFIG, "视图已删除.无法执行此操作.");
		}

		// 调用字段后置处理器
		for (Map<String, Object> column : (Set<Map<String, Object>>) table.get("columns")) {
			String execScript = (String) column.get("execScript");
			Integer execType = (Integer) column.get("execType");
			if (StringUtils.isEmpty(execScript) || execType == null) {
				continue;
			}

			// 需要前置粗粒
			Map<String, Object> context = new HashMap<>();
			context.put("vo", po);
			context.put("mode", editMode ? ExecMode.EDIT.getValue() : ExecMode.CREATE.getValue());
			Object value = ScriptHelper.evel(ScriptTypes.forCode(execType), execScript, context);
			po.put((String) column.get("name"), value);
		}
	}

	/**
	 * 批量提交
	 * 
	 * @param list
	 * @param useExec
	 *            是否调用字段处理器
	 * @param editMode
	 *            是否编辑
	 */
	public void executeBatchUpload(List<Map<String, Object>> list, boolean useExec, boolean editMode) {
		for (Map<String, Object> po : list) {
			executeSubmit(po, useExec, editMode);
		}
	}

	/**
	 * 表单提交操作
	 * 
	 * @param po
	 * @param useExec
	 *            是否调用字段处理器
	 * @param editMode
	 *            是否编辑
	 * @return
	 */
	public Map<String, Object> executeSubmit(Map<String, Object> po, boolean useExec, boolean editMode) {

		// 字段处理器
		if (useExec) {
			preparePO(po, editMode);
		}

		if (editMode) {
			return updateAndReturnPk(po);
		} else {
			return saveAndReturnPk(po);
		}
	}
}
