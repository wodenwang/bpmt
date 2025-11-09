package com.riversoft.wx.qy;

import com.riversoft.core.Config;
import com.riversoft.weixin.common.decrypt.MessageDecryption;
import com.riversoft.weixin.common.message.XmlMessageHeader;
import com.riversoft.weixin.qy.base.AgentSetting;
import com.riversoft.weixin.qy.base.CorpSetting;
import com.riversoft.weixin.qy.message.QyXmlMessages;
import com.riversoft.wx.qy.service.QyAppService;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by exizhai on 8/28/2015.
 */
@WebServlet(name = "qyCallbackServlet", urlPatterns = { "/wx/qy/*" }, loadOnStartup = 1)
public class QyCallbackServlet extends HttpServlet {

    private Logger logger = LoggerFactory.getLogger(QyCallbackServlet.class);

    @Override
    public void init() throws ServletException {
        super.init();

        //默认系统中只有一个企业号，入口设置默认企业号和默认agent相关配置
        if (Boolean.valueOf(Config.get("wx.qy.flag", "false"))) {
            String corpId = Config.get("wx.qy.corpId", "");
            String corpSecret = Config.get("wx.qy.corpSecret", "");

            if (StringUtils.isEmpty(corpId) || StringUtils.isEmpty(corpSecret)) {
                logger.warn("wx.qy.corpId, wx.qy.corpSecret和wx.qy.default全是空。");
            } else {
                CorpSetting corpSetting = new CorpSetting(corpId, corpSecret);
                logger.info("当前系统的默认微信企业号配置:[{}:{}]", corpId);
                CorpSetting.setDefault(corpSetting);
            }
        }
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        handle(request, response);
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        handle(request, response);
    }

    protected void handle(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html;charset=utf-8");
        response.setStatus(HttpServletResponse.SC_OK);

        int agentId = getAgentIdFromPath(request);
        AgentSetting agentSetting = QyAppService.getInstance().getAgentSetting(agentId);

        if (agentSetting != null) {
            String msgSignature = request.getParameter("msg_signature");
            String nonce = request.getParameter("nonce");
            String timestamp = request.getParameter("timestamp");
            String echostr = request.getParameter("echostr");

            logger.debug("agentId={}, msg_signature={}, nonce={}, timestamp={}, echostr={}", agentId, msgSignature, nonce, timestamp, echostr);

            try {
                MessageDecryption messageDecryption = new MessageDecryption(agentSetting.getToken(), agentSetting.getAesKey(), CorpSetting.defaultSettings().getCorpId());
                if (StringUtils.isNotBlank(echostr)) {
                    String echo = messageDecryption.decryptEcho(msgSignature, timestamp, nonce, echostr);
                    logger.debug("消息签名验证成功.");
                    QyAppService.getInstance().updateAgentStatus(agentId, 1);
                    response.getWriter().println(echo);
                } else {
                    String content = IOUtils.toString(request.getInputStream(), "UTF-8");
                    XmlMessageHeader xmlRequest = QyXmlMessages.fromXml(messageDecryption.decrypt(msgSignature, timestamp, nonce, content));
                    QyWxRequestDispatcher.getInstance().dispatch(agentSetting.getName(), xmlRequest);
                }
            } catch (Exception e) {
                logger.error("callback失败.", agentId, e);
                response.getWriter().println("callback失败.");
            }
        } else {
            logger.warn("没有找到{}对应的agent", agentId);
            response.getWriter().println("没有找到对应的agent");
        }

    }

    public int getAgentIdFromPath(HttpServletRequest request) {
        String pathInfo = request.getPathInfo();
        return Integer.valueOf(pathInfo.replaceAll("/", ""));
    }

}
