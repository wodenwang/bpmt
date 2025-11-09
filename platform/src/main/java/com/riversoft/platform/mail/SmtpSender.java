package com.riversoft.platform.mail;

import com.riversoft.core.Config;
import com.riversoft.core.exception.ExceptionType;
import com.riversoft.core.exception.SystemRuntimeException;
import jodd.mail.Email;
import jodd.mail.SendMailSession;
import jodd.mail.SmtpServer;
import jodd.mail.SmtpSslServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by exizhai on 24/12/2014.
 */
class SmtpSender implements Sender {

    private Logger logger = LoggerFactory.getLogger(SmtpSender.class);
    private String defaultPort;
    private boolean isSSL = false;

    /**
     * 指定默认端口
     */
    SmtpSender() {
        String protocol = Config.get(SENDER_SERVER_PROTOCOL, "smtp");
        switch (protocol) {
            case "smtps":// ssl
                this.defaultPort = "465";
                this.isSSL = true;
                break;
            default:
                this.defaultPort = "25";
                break;
        }
    }

    @Override
    public void send(Email email, String user, String password) {
        SmtpServer smtpServer = null;
        if (isSSL) {
            smtpServer = SmtpSslServer.create(Config.get(SENDER_SERVER_HOSTS),
                    Integer.valueOf(Config.get(SENDER_SERVER_PORT, defaultPort))).authenticateWith(user, password);
        } else {
            smtpServer = SmtpServer.create(Config.get(SENDER_SERVER_HOSTS),
                    Integer.valueOf(Config.get(SENDER_SERVER_PORT, defaultPort))).authenticateWith(user, password);
        }

        SendMailSession session = null;
        try {
            session = smtpServer.createSession();
            session.open();
            session.sendMail(email);
        } catch (Exception e) {
            logger.error("发送邮件出错:", e);
            throw new SystemRuntimeException(ExceptionType.MAIL, e);
        } finally {
            if (session != null) {
                session.close();
            }
        }
    }
}
