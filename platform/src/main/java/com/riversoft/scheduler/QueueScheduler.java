package com.riversoft.scheduler;

import com.riversoft.core.Config;
import com.riversoft.core.db.DataCondition;
import com.riversoft.core.db.DataPO;
import com.riversoft.core.db.DataPackage;
import com.riversoft.core.db.JdbcService;
import com.riversoft.core.db.ORMService;
import com.riversoft.platform.db.ORMAdapterService;
import com.riversoft.platform.po.DevQueue;
import com.riversoft.platform.po.TbTable;
import com.riversoft.platform.queue.QueueTableModelKeys;
import com.riversoft.platform.script.ScriptHelper;
import com.riversoft.platform.script.ScriptTypes;
import com.riversoft.platform.service.TableService;
import com.riversoft.scheduler.annotation.QuartzJob;
import com.riversoft.util.Formatter;
import com.riversoft.util.jackson.JsonMapper;
import org.apache.commons.lang3.StringUtils;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by exizhai on 7/5/2015.
 */
@QuartzJob(cronExp = "0 0/1 * * * ?", name = "QueueHandler", group = "System", desc = "异步队列处理器")
@DisallowConcurrentExecution
public class QueueScheduler implements Job {

	private static Logger logger = LoggerFactory.getLogger(QueueScheduler.class);
	private ExecutorService executorService = null;

	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		List<DevQueue> list = (List<DevQueue>) ORMService.getInstance().queryAll(DevQueue.class.getName());
		if (list == null || list.isEmpty()) {
			// logger.info("当前系统没有配置异步队列处理器");
		} else {
			long start = System.currentTimeMillis();
			long tasks = 0;
			executorService = Executors.newFixedThreadPool(Integer.valueOf(Config.get("queue.threads.size", "10")));

			int pageSize = Integer.valueOf(Config.get("queue.page.size", "100"));

			for (DevQueue queue : list) {
				int fromItem = 0;
				long total = Long.MAX_VALUE;

				TbTable tbTable = (TbTable) TableService.getInstance().findByPk(TbTable.class.getName(), queue.getTableName());

				if (tbTable != null) {
					DataCondition condition = prepareCondition();
					while (fromItem <= total) {
						DataPackage dataPackage = ORMAdapterService.getInstance().queryPackage(queue.getTableName(),
								fromItem, pageSize, condition.toEntity());

						if (!dataPackage.getList().isEmpty()) {
							for (Map<String, Object> record : (List<Map<String, Object>>) dataPackage.getList()) {
								executorService.submit(new QueueHandler(queue, record));
								tasks++;
							}

							total = dataPackage.getTotalRecord();
							fromItem = fromItem + pageSize;
						} else {
							break;
						}
					}
				}

			}

			executorService.shutdown();

			while (!executorService.isTerminated()) {
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
				}
			}

			logger.info("本次任务执行完毕,任务数:{}, 时间:{} ms.", tasks, System.currentTimeMillis() - start);
		}
	}

	private DataCondition prepareCondition() {
		DataCondition condition = new DataCondition();
		condition.setNumberIn(QueueTableModelKeys.STATUS.name(), "0", "2"); // 等待处理或者重试
		condition.setDateSmallEqual(QueueTableModelKeys.NEXTACTION_DATE.name(), Formatter.formatDatetime(new Date()));
		condition.setOrderByAsc(QueueTableModelKeys.ID.name());

		return condition;
	}

	public class QueueHandler implements Runnable {
		private Logger logger = LoggerFactory.getLogger(QueueHandler.class);

		private DevQueue devQueue;
		private Map<String, Object> record;

		public QueueHandler(DevQueue devQueue, Map<String, Object> record) {
			this.devQueue = devQueue;
			this.record = record;
		}

		@Override
		public void run() {
			Date begin = new Date();
			Number id = (Number) record.get(QueueTableModelKeys.ID.name());

			try {
				logger.debug("处理数据 [{} -> {}:{}]", devQueue.getQueueKey(), devQueue.getTableName(), id);

				markProcessing(id);

				Map<String, Object> context = new HashMap<>();
				context.put("vo", record);
				ScriptHelper.evel(ScriptTypes.forCode(devQueue.getExecType()), devQueue.getExecScript(), context);

				if (recordHistory()) {
					DataPO historyPO = new DataPO(devQueue.getLogTableName(), (Map<String, Object>)  record);
					historyPO.set("QUEUE_ID", id);
                    historyPO.set("QUEUE_DATA", JsonMapper.defaultMapper().toJson(record));
                    historyPO.set("BEGIN_DATE", begin);
                    historyPO.set("END_DATE", new Date());
                    historyPO.set("SUCCESS_FLAG", 1);
                    historyPO.set("LOG_MSG", "执行成功");
					ORMAdapterService.getInstance().save(historyPO.toEntity());
				}

				deleteQueue(id);
				logger.debug("执行成功 [{} -> {}:{}]", devQueue.getQueueKey(), devQueue.getTableName(), id);
			} catch (Exception e) {
				logger.error("[{}:{}] 执行失败", devQueue.getTableName(), id, e);
				int retries = 0;
				if (record.containsKey(QueueTableModelKeys.RETRIES.name())) {
					retries = (Integer) record.get(QueueTableModelKeys.RETRIES.name());
				}

				retries = retries + 1;

				try {
					if (lastRetry(retries)) {
						logger.debug("最后一次 [{} -> {}:{}]", devQueue.getQueueKey(), devQueue.getTableName(), id);
						deleteQueue(id);
					} else {
						logger.debug("重试 [{} -> {}:{}]", devQueue.getQueueKey(), devQueue.getTableName(), id);
						reschedule(id, retries);
					}

					if (recordHistory()) {
						logger.error("执行失败 [{} -> {}:{}], 登记日志。", devQueue.getQueueKey(), devQueue.getTableName(), id);
						DataPO historyPO = new DataPO(devQueue.getLogTableName(), (Map<String, Object>)  record);
						historyPO.set("QUEUE_ID", id);
	                    historyPO.set("QUEUE_DATA", JsonMapper.defaultMapper().toJson(record));
	                    historyPO.set("BEGIN_DATE", begin);
	                    historyPO.set("END_DATE", new Date());
	                    historyPO.set("SUCCESS_FLAG", 0);
	                    historyPO.set("LOG_MSG", "[" + retries + "]执行失败:" + e.getMessage());
						ORMAdapterService.getInstance().save(historyPO.toEntity());
					}
				} catch (Exception ex) {
					logger.error("unexpected exception.", ex);
				}
			}
		}

		private void markProcessing(Number id) {
			JdbcService.getInstance().executeSQL(updateStatusSQL(), 1, id);// 正在处理
		}

		private void deleteQueue(Number id) {
			JdbcService.getInstance().executeSQL(deleteSQL(), id);
		}

		boolean recordHistory() {
			return StringUtils.isNotEmpty(devQueue.getLogTableName());
		}

		private void reschedule(Number id, int retries) {
			Calendar calendar = Calendar.getInstance();
			calendar.add(Calendar.MINUTE, Integer.valueOf(Config.get("queue.retry.interval", "1")));
			markNextRetry(id, retries, calendar);
		}

		private void markNextRetry(Number id, int retries, Calendar calendar) {
			JdbcService.getInstance().executeSQL(updateSQL(), 2, calendar.getTime(), retries, id);
		}

		private boolean lastRetry(int retries) {
			return retries == Integer.valueOf(Config.get("queue.retry.interval", "7"));
		}

		private String historySQL() {
			return "insert into " + devQueue.getLogTableName() + " (QUEUE_ID,QUEUE_DATA,BEGIN_DATE,END_DATE,SUCCESS_FLAG,LOG_MSG) values (?,?,?,?,?,?)";
		}
		
		private String updateStatusSQL() {
			return "update " + devQueue.getTableName() + " set STATUS =? where ID =?";
		}

		private String updateSQL() {
			return "update " + devQueue.getTableName() + " set STATUS =?, NEXTACTION_DATE =?, RETRIES =? where ID =?";
		}

		private String deleteSQL() {
			return "delete from " + devQueue.getTableName() + " where ID =?";
		}
	}

}
