package com.riversoft.platform.mail;

import com.riversoft.core.Config;
import com.riversoft.core.exception.ExceptionType;
import com.riversoft.core.exception.SystemRuntimeException;
import jodd.mail.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by exizhai on 24/12/2014.
 */
class ImapReceiver implements Receiver {

    private Logger logger = LoggerFactory.getLogger(ImapReceiver.class);
    private String defaultPort;
    private boolean isSSL;

    /**
     * 指定默认端口
     */
    ImapReceiver() {
        String protocol = Config.get(RECEIVER_SERVER_PROTOCOL, "imap");
        switch (protocol) {
            case "imaps":// ssl
                this.defaultPort = "993";
                this.isSSL = true;
                break;
            default:
                this.defaultPort = "143";
                break;
        }
    }

    @Override
    public ReceivedEmail[] receive(String user, String password) {
        return receive(null, user, password);
    }

    @Override
    public ReceivedEmail[] receive(EmailFilter emailFilter, String user, String password) {
        ImapServer imapServer = null;
        if (isSSL) {
            imapServer = new ImapSslServer(Config.get(RECEIVER_SERVER_HOSTS), Integer.valueOf(Config.get(
                    RECEIVER_SERVER_PORT, defaultPort)), user, password);
        } else {
            imapServer = new ImapServer(Config.get(RECEIVER_SERVER_HOSTS), Integer.valueOf(Config.get(
                    RECEIVER_SERVER_PORT, defaultPort)), new SimpleAuthenticator(user, password));
        }
        ReceiveMailSession session = null;
        try {
            session = imapServer.createSession();
            session.open();

            ReceivedEmail[] emails = session.receiveEmailAndMarkSeen(emailFilter);
            return emails;
        } catch (Exception e) {
            logger.error("接收邮件出错,", e);
            throw new SystemRuntimeException(ExceptionType.MAIL, e);
        } finally {
            if (session != null) {
                session.close();
            }
        }
    }
}
