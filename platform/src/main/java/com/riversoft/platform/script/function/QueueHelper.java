package com.riversoft.platform.script.function;

import com.riversoft.core.db.DataPO;
import com.riversoft.core.db.ORMService;
import com.riversoft.core.exception.ExceptionType;
import com.riversoft.core.exception.SystemRuntimeException;
import com.riversoft.core.script.annotation.ScriptSupport;
import com.riversoft.platform.db.ORMAdapterService;
import com.riversoft.platform.po.DevQueue;
import com.riversoft.platform.queue.QueueTableModelKeys;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by exizhai on 7/5/2015.
 */
@ScriptSupport("queue")
public class QueueHelper {

    /**
     * 增加一个element到队列中
     * @param queue
     * @param element
     */
    public static void add(String queue, Map<String, Object> element) {
        DevQueue devQueue = (DevQueue)ORMService.getInstance().findByPk(DevQueue.class.getName(), queue);
        if(devQueue != null) {
            DataPO po = new DataPO(devQueue.getTableName(), element);

            po.set(QueueTableModelKeys.STATUS.name(), 0);//等待处理
            po.set(QueueTableModelKeys.TYPE.name(), queue);
            po.set(QueueTableModelKeys.CREATED_DATE.name(), new Date());
            po.set(QueueTableModelKeys.NEXTACTION_DATE.name(), new Date());
            po.set(QueueTableModelKeys.RETRIES.name(), 0);

            ORMAdapterService.getInstance().save(po.toEntity());
        } else {
            throw new SystemRuntimeException(ExceptionType.QUEUE, "没有找到 " + queue + " 对应的异步队列表。");
        }
    }

    /**
     * 增加一批element到队列中
     * @param queue
     * @param elements
     */
    public static void add(String queue, List<Map<String, Object>> elements) {
        DevQueue devQueue = (DevQueue)ORMService.getInstance().findByPk(DevQueue.class.getName(), queue);
        if(devQueue != null) {
            for (Map<String, Object> element : elements) {
                DataPO po = new DataPO(devQueue.getTableName(), element);

                po.set(QueueTableModelKeys.STATUS.name(), 0);//等待处理
                po.set(QueueTableModelKeys.TYPE.name(), queue);
                po.set(QueueTableModelKeys.CREATED_DATE.name(), new Date());
                po.set(QueueTableModelKeys.NEXTACTION_DATE.name(), new Date());
                po.set(QueueTableModelKeys.RETRIES.name(), 0);

                ORMAdapterService.getInstance().save(po.toEntity());
            }
        } else {
            throw new SystemRuntimeException(ExceptionType.QUEUE, "没有找到 " + queue + " 对应的异步队列表。");
        }
    }

}
