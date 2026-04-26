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
class Pop3Receiver implements Receiver {

    private Logger logger = LoggerFactory.getLogger(Pop3Receiver.class);

    private String defaultPort;
    private boolean isSSL = false;

    /**
     * 指定默认端口
     */
    Pop3Receiver() {
        String protocol = Config.get(RECEIVER_SERVER_PROTOCOL, "pop");
        switch (protocol) {
            case "pops":// ssl
                this.defaultPort = "995";
                this.isSSL = true;
                break;
            default:
                this.defaultPort = "110";
                break;
        }
    }

    @Override
    public ReceivedEmail[] receive(String user, String password) {
        return receive(null, user, password);
    }

    @Override
    public ReceivedEmail[] receive(EmailFilter emailFilter, String user, String password) {
        Pop3Server popServer = null;
        if (isSSL) {
            popServer = new Pop3SslServer(Config.get(RECEIVER_SERVER_HOSTS), Integer.valueOf(Config.get(
                    RECEIVER_SERVER_PORT, defaultPort)), user, password);
        } else {
            popServer = new Pop3Server(Config.get(RECEIVER_SERVER_HOSTS), Integer.valueOf(Config.get(
                    RECEIVER_SERVER_PORT, defaultPort)), new SimpleAuthenticator(user, password));
        }

        ReceiveMailSession session = null;
        try {
            session = popServer.createSession();
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
