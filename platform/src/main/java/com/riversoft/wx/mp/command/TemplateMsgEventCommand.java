package com.riversoft.wx.mp.command;

import com.riversoft.core.db.ORMService;
import com.riversoft.platform.db.ORMAdapterService;
import com.riversoft.weixin.common.message.XmlMessageHeader;
import com.riversoft.weixin.mp.event.template.JobFinishedEvent;
import com.riversoft.wx.mp.model.TemplateMsgLogModelKeys;
import org.apache.commons.lang3.StringUtils;

import java.util.Map;

/**
 * 处理模板消息下发结果通知
 *
 * @borball on 3/10/2016.
 */
public class TemplateMsgEventCommand implements MpCommand {

    @Override
    public MpResponse execute(MpRequest mpRequest) {
        Map<String, Object> config = (Map<String, Object>) ORMService.getInstance().findByPk("WxMp", mpRequest.getMpKey());
        String tableName = (String) config.get("templateMsgLogTable");
        if (StringUtils.isNotBlank(tableName) && mpRequest.isTemplateMsgCompleted()) {
            XmlMessageHeader messageHeader = mpRequest.getSource();
            if (messageHeader instanceof JobFinishedEvent) {
                JobFinishedEvent jobFinishedEvent = (JobFinishedEvent) messageHeader;
                Map<String, Object> o = (Map<String, Object>) ORMAdapterService.getInstance().findByPk(tableName, jobFinishedEvent.getMsgId());
                if (o != null) {
                    o.put(TemplateMsgLogModelKeys.RESULT.name(), jobFinishedEvent.getStatus());
                    o.put(TemplateMsgLogModelKeys.RESPOND_DATE.name(), jobFinishedEvent.getCreateTime());
                    ORMAdapterService.getInstance().update(o);
                }
            }
        }
        return null;
    }

}
