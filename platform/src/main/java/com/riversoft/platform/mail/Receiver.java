package com.riversoft.platform.mail;

import jodd.mail.EmailFilter;
import jodd.mail.ReceivedEmail;

/**
 * Created by exizhai on 24/12/2014.
 */
public interface Receiver {

	static String RECEIVER_SERVER_HOSTS = "mail.receiver.host";
	static String RECEIVER_SERVER_PORT = "mail.receiver.port";
	static String RECEIVER_SERVER_PROTOCOL = "mail.receiver.protocol";

	public ReceivedEmail[] receive(String user, String password);

	public ReceivedEmail[] receive(EmailFilter emailFilter, String user, String password);

}
