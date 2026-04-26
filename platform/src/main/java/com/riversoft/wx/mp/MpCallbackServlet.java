package com.riversoft.wx.mp;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.riversoft.weixin.common.decrypt.AesException;
import com.riversoft.weixin.common.decrypt.MessageDecryption;
import com.riversoft.weixin.common.decrypt.SHA1;
import com.riversoft.weixin.common.message.XmlMessageHeader;
import com.riversoft.weixin.common.util.XmlObjectMapper;
import com.riversoft.weixin.mp.base.AppSetting;
import com.riversoft.weixin.mp.message.MpXmlMessages;
import com.riversoft.wx.mp.service.MpAppService;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 微信公众平台的callback入口
 * <p/>
 * Created by exizhai on 11/28/2015.
 */
@WebServlet(name = "mpCallbackServlet", urlPatterns = { "/wx/mp/*" }, loadOnStartup = 2)
public class MpCallbackServlet extends HttpServlet {

    private Logger logger = LoggerFactory.getLogger(MpCallbackServlet.class);

    @Override
    public void init() throws ServletException {
        super.init();
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        handle(request, response);
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        handle(request, response);
    }

    protected void handle(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String appId = getAppIdFromPath(request);

        String signature = request.getParameter("signature");
        String nonce = request.getParameter("nonce");
        String timestamp = request.getParameter("timestamp");
        String msg_signature = request.getParameter("msg_signature");
        String echostr = request.getParameter("echostr");
        String encrypt_type = request.getParameter("encrypt_type");

        logger.debug("appId={}, signature={}, msg_signature={}, timestamp={}, nonce={}, echostr={}, encrypt_type={}",
                appId, signature, msg_signature, timestamp, nonce, echostr, encrypt_type);

        AppSetting appSetting = MpAppService.getInstance().getAppSettingByAppID(appId);

        if (appSetting != null) {
            try {
                if (!SHA1.getSHA1(appSetting.getToken(), timestamp, nonce).equals(signature)) {
                    logger.warn("非法请求.");
                    response.getWriter().println("非法请求.");
                    return;
                }
            } catch (AesException e) {
                logger.error("check signature failed:", e);
                response.getWriter().println("非法请求.");
                return;
            }

            if (!StringUtils.isEmpty(echostr)) {
                MpAppService.getInstance().updateStatus(appId, 1);
                response.getWriter().println(echostr);
                return;
            }

            XmlMessageHeader xmlRequest = null;
            String content = IOUtils.toString(request.getInputStream(), "UTF-8");
            if ("aes".equals(encrypt_type)) {
                try {
                    MessageDecryption messageDecryption = new MessageDecryption(appSetting.getToken(), appSetting.getAesKey(), appSetting.getAppId());
                    xmlRequest = MpXmlMessages.fromXml(messageDecryption.decrypt(msg_signature, timestamp, nonce, content));
                    XmlMessageHeader xmlResponse = dispatch(appSetting, xmlRequest);

                    if (xmlResponse != null) {
                        try {
                            response.getWriter().println(messageDecryption.encrypt(XmlObjectMapper.defaultMapper().toXml(xmlResponse), timestamp, nonce));
                        } catch (JsonProcessingException e) {
                        }
                    }
                } catch (AesException e) {
                }
            } else {
                xmlRequest = MpXmlMessages.fromXml(content);
                XmlMessageHeader xmlResponse = dispatch(appSetting, xmlRequest);
                if (xmlResponse != null) {
                    try {
                        response.getWriter().println(XmlObjectMapper.defaultMapper().toXml(xmlResponse));
                    } catch (JsonProcessingException e) {
                    }
                }
            }
        } else {
            logger.warn("没有找到{}对应的app", appId);
            response.getWriter().println("没有找到对应的app");
        }

    }

    private XmlMessageHeader dispatch(AppSetting appSetting, XmlMessageHeader xmlRequest) {
        return MpRequestDispatcher.getInstance().dispatch(appSetting, xmlRequest);
    }

    /**
     * callback url格式： http://bpmt.borball.me/wx/mp/appId
     *
     * @param request
     * @return
     */
    public String getAppIdFromPath(HttpServletRequest request) {
        String pathInfo = request.getPathInfo();
        return pathInfo.replaceAll("/", "");
    }

}
