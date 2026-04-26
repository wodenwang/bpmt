package com.riversoft.wx.mp;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;

import com.riversoft.weixin.common.util.XmlObjectMapper;
import com.riversoft.weixin.mp.base.AppSetting;
import com.riversoft.weixin.mp.care.CareMessages;
import com.riversoft.weixin.pay.payment.Payments;
import com.riversoft.weixin.pay.payment.bean.PaymentNotification;
import com.riversoft.wx.mp.command.MpConfigCommand;
import com.riversoft.wx.mp.command.MpRequest;
import com.riversoft.wx.mp.command.MpResponse;
import com.riversoft.wx.mp.context.PayResult;
import com.riversoft.wx.mp.service.MpAppService;

/**
 * 微信支付相关
 * @borball on 5/15/2016.
 */
@WebServlet(name = "mpPayCallbackServlet", urlPatterns = { "/wx/pay/mp/*" }, loadOnStartup = 3)
public class MpPayCallbackServlet extends HttpServlet {

    private Logger logger = LoggerFactory.getLogger(MpPayCallbackServlet.class);
    private ExecutorService executorService = Executors.newFixedThreadPool(5);

    @Override
    public void init() throws ServletException {
        super.init();
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        handle(req, resp);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        handle(req, resp);
    }

    private void handle(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = getActionFromPath(request);

        switch (action) {
            case "notify":
                handleNotify(request, response);
                break;
            case "scan":
                handleScan(request, response);
                break;
            default:
                throw new ServletException("invalid url action");
        }
    }

    private void handleScan(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        execute(request, response, false);
    }

    private void handleNotify(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        execute(request, response, true);
    }

    private void execute(HttpServletRequest request, HttpServletResponse response, boolean mpPay) throws ServletException, IOException {
        String content = IOUtils.toString(request.getInputStream(), "UTF-8");
        logger.info("微信支付通知结果:\n{}", content);
        PaymentNotification paymentNotification = XmlObjectMapper.defaultMapper().fromXml(content, PaymentNotification.class);

        String appId = paymentNotification.getAppId();
        MpPaySetting paySetting = MpAppService.getInstance().getPaySettingByAppId(appId);

        String template = "<xml><return_code><![CDATA[%s]]></return_code><return_msg><![CDATA[%s]]></return_msg></xml>";

        if(paySetting != null) {
            if(Payments.with(paySetting).checkSignature(paymentNotification)) {
                if(paymentNotification.success()) { //成功才做处理?
                    MpRequest mpRequest = new MpRequest();
                    mpRequest.setAppId(paymentNotification.getAppId());
                    mpRequest.setMpKey(paySetting.getMpKey());
                    mpRequest.setOpenId(paymentNotification.getOpenId());
                    PayResult payResult = new PayResult();
                    try {
                        BeanUtils.copyProperties(paymentNotification, payResult);

                        payResult.setMpPay(mpPay);
                        payResult.setScanPay(!mpPay);
                        mpRequest.setPayResult(payResult);
                        MpConfigCommand mpConfigCommand = new MpConfigCommand();
                        MpResponse mpResponse = mpConfigCommand.execute(mpRequest);
                        if (mpResponse != null) {
                            sendMessage(MpAppService.getInstance().getAppSettingByAppID(paySetting.getAppId()), paymentNotification.getOpenId(), mpResponse);
                        }
                    } catch (Exception e) {
                        logger.error("微信支付处理器处理失败", e);
                    }
                } else {
                    logger.warn("微信支付通知结果:支付没有成功");
                }
                response.getWriter().println(String.format(template, "SUCCESS", "OK"));
            } else {
                logger.warn("无法处理微信支付通知结果，签名错误");
                response.getWriter().println(String.format(template, "FAIL", "无法处理微信支付通知结果，签名错误"));
            }
        } else {
            logger.warn("无法处理微信支付通知结果，无相应的配置");
            response.getWriter().println(String.format(template, "FAIL", "无法处理微信支付通知结果，无相应的配置"));
        }
    }

    /**
     * callback url格式： http://bpmt.borball.me/wx/pay/mp/action
     *
     * @param request
     * @return
     */
    public String getActionFromPath(HttpServletRequest request) {
        String pathInfo = request.getPathInfo();
        return pathInfo.replaceAll("/", "");
    }

    private void sendMessage(final AppSetting appSetting, final String openId, final MpResponse response) {
        String type = response.getType();
        if (StringUtils.isNotEmpty(type)) {
            switch (type) {
                case "text":
                    executorService.submit(new Runnable() {
                        @Override
                        public void run() {
                            CareMessages.with(appSetting).text(openId, response.getText());
                        }
                    });
                    break;
                case "image":
                    executorService.submit(new Runnable() {
                        @Override
                        public void run() {
                            CareMessages.with(appSetting).image(openId, response.getImage());
                        }
                    });
                    break;
                case "voice":
                    executorService.submit(new Runnable() {
                        @Override
                        public void run() {
                            CareMessages.with(appSetting).voice(openId, response.getVoice());
                        }
                    });
                    break;
                case "video":
                    executorService.submit(new Runnable() {
                        @Override
                        public void run() {
                            CareMessages.with(appSetting).video(openId, response.getVideo());
                        }
                    });
                    break;
                case "music":
                    executorService.submit(new Runnable() {
                        @Override
                        public void run() {
                            CareMessages.with(appSetting).music(openId, response.getMusic());
                        }
                    });
                    break;
                case "news":
                    executorService.submit(new Runnable() {
                        @Override
                        public void run() {
                            CareMessages.with(appSetting).news(openId, response.getNews());
                        }
                    });
                    break;
                case "mpnews":
                    executorService.submit(new Runnable() {
                        @Override
                        public void run() {
                            CareMessages.with(appSetting).mpNews(openId, response.getMpnews());
                        }
                    });
                    break;
                case "card":
                    executorService.submit(new Runnable() {
                        @Override
                        public void run() {
                            CareMessages.with(appSetting).card(openId, response.getCard());
                        }
                    });
                    break;
                default:
                    logger.warn("消息类型:{}不在支持的范围内，请检查。", type);
                    break;
            }
        } else {
            logger.error("response需要遵循一定规范返回消息的类型，请检查微信相关的配置。");
        }
    }
}
