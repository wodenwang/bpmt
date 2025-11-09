package com.riversoft.module.manager.queue;

import com.riversoft.core.db.DataCondition;
import com.riversoft.core.db.DataPackage;
import com.riversoft.core.db.ORMService;
import com.riversoft.core.exception.ExceptionType;
import com.riversoft.core.exception.SystemRuntimeException;
import com.riversoft.core.web.Actions;
import com.riversoft.core.web.RequestUtils;
import com.riversoft.core.web.annotation.ActionAccess;
import com.riversoft.platform.SessionManager;
import com.riversoft.platform.db.ORMAdapterService;
import com.riversoft.platform.db.model.ModelKeyUtils;
import com.riversoft.platform.queue.LogTableModelKeys;
import com.riversoft.platform.po.DevQueue;
import com.riversoft.platform.po.TbTable;
import com.riversoft.platform.queue.QueueTableModelKeys;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.riversoft.core.web.Actions.includePage;

/**
 * Created by exizhai on 7/11/2015.
 */
@ActionAccess(level = ActionAccess.SafeLevel.DEV_R)
public class QueueAction {

    /**
     * 队列管理
     *
     * @param request
     * @param response
     */
    public void index(HttpServletRequest request, HttpServletResponse response) {
        includePage(request, response, Actions.Util.getPagePath(request, "main.jsp"));
    }

    /**
     * 列表
     *
     * @param request
     * @param response
     */
    public void list(HttpServletRequest request, HttpServletResponse response) {
        // 获取分页信息
        int start = Actions.Util.getStart(request);
        int limit = Actions.Util.getLimit(request);

        // 获取排序信息
        String field = Actions.Util.getSortField(request);
        String dir = Actions.Util.getSortDir(request);

        // 查询条件
        DataCondition condition = new DataCondition(Actions.Util.buildQueryMap(new HashMap<String, Object>(), request));
        condition.setOrderBy(field, dir);

        DataPackage dp = ORMService.getInstance().queryPackage(DevQueue.class.getName(), start, limit,
                condition.toEntity());
        // 设置到页面
        request.setAttribute("dp", dp);

        Actions.includePage(request, response, Actions.Util.getPagePath(request, "list.jsp"));
    }

    /**
     * 删除队列
     *
     * @param request
     * @param response
     */
    @ActionAccess(level = ActionAccess.SafeLevel.DEV_W)
    public void delete(HttpServletRequest request, HttpServletResponse response) {
        String queueKey = RequestUtils.getStringValue(request, "queueKey");

        DevQueue devQueue = (DevQueue)ORMService.getInstance().findByPk(DevQueue.class.getName(), queueKey);
        if(devQueue != null){
            DataCondition condition = new DataCondition();

            condition.setStringEqual(QueueTableModelKeys.TYPE.name(), devQueue.getQueueKey());
            long count = ORMAdapterService.getInstance().getCount(devQueue.getTableName(), condition.toEntity());
            if(count > 0) {
                throw new SystemRuntimeException(ExceptionType.QUEUE, "表[" + devQueue.getTableName() + "]中还有未完成的[" + queueKey + "]任务,不能删除.");
            }
        }
        ORMService.getInstance().removeByPk(DevQueue.class.getName(), queueKey);
        Actions.redirectInfoPage(request, response, "删除成功.");
    }

    /**
     * 新建(页面)
     *
     * @param request
     * @param response
     */
    @SuppressWarnings("unchecked")
    public void createZone(HttpServletRequest request, HttpServletResponse response) {
        List<TbTable> queueTables = new ArrayList<>();
        List<TbTable> hisTables = new ArrayList<>();
        List<TbTable> sysTables = (List<TbTable>) ORMService.getInstance().queryAll(TbTable.class.getName());
        for (TbTable model : sysTables) {
            if (ModelKeyUtils.checkModel(QueueTableModelKeys.class, model)) {
                queueTables.add(model);
            }
            if(ModelKeyUtils.checkModel(LogTableModelKeys.class, model)) {
                hisTables.add(model);
            }
        }
        request.setAttribute("queueTables", queueTables);
        request.setAttribute("hisTables", hisTables);
        Actions.includePage(request, response, Actions.Util.getPagePath(request, "form.jsp"));
    }

    /**
     * 编辑(页面)
     *
     * @param request
     * @param response
     */
    @SuppressWarnings("unchecked")
    public void updateZone(HttpServletRequest request, HttpServletResponse response) {
        String queueKey = RequestUtils.getStringValue(request, "queueKey");
        DevQueue vo = (DevQueue) ORMService.getInstance().findByPk(DevQueue.class.getName(), queueKey);
        if (vo == null) {
            throw new SystemRuntimeException(ExceptionType.QUEUE, "异步队列[" + queueKey + "]不存在.");
        }
        request.setAttribute("vo", vo);

        List<TbTable> queueTables = new ArrayList<>();
        List<TbTable> hisTables = new ArrayList<>();
        List<TbTable> sysTables = (List<TbTable>) ORMService.getInstance().queryAll(TbTable.class.getName());
        for (TbTable model : sysTables) {
            if (ModelKeyUtils.checkModel(QueueTableModelKeys.class, model)) {
                queueTables.add(model);
            }
            if(ModelKeyUtils.checkModel(LogTableModelKeys.class, model)) {
                hisTables.add(model);
            }
        }

        request.setAttribute("queueTables", queueTables);
        request.setAttribute("hisTables", hisTables);

        Actions.includePage(request, response, Actions.Util.getPagePath(request, "form.jsp"));
    }

    /**
     * 提交表单
     *
     * @param request
     * @param response
     */
    @ActionAccess(level = ActionAccess.SafeLevel.DEV_W)
    public void submitForm(HttpServletRequest request, HttpServletResponse response) {
        String queueKey = RequestUtils.getStringValue(request, "queueKey");
        DevQueue vo = (DevQueue) ORMService.getInstance().findByPk(DevQueue.class.getName(), queueKey);

        if("0".equals(RequestUtils.getStringValue(request, "edit"))) {//新增
            if(vo != null) {
                throw new SystemRuntimeException(ExceptionType.QUEUE, "异步队列[" + queueKey + "]已经存在.");
            } else {
                vo = new DevQueue();
                vo.setQueueKey(queueKey);
            }
        }

        vo.setDescription(RequestUtils.getStringValue(request, "description"));
        vo.setCreateUid(SessionManager.getUser().getUid());
        vo.setTableName(RequestUtils.getStringValue(request, "tableName"));
        vo.setLogTableName(RequestUtils.getStringValue(request, "logTableName"));
        vo.setExecType(RequestUtils.getIntegerValue(request, "execType"));
        vo.setExecScript(RequestUtils.getStringValue(request, "execScript"));

        ORMService.getInstance().saveOrUpdatePO(vo);
    }

}
