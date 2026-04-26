package com.riversoft.platform.mail;

import jodd.mail.Email;

/**
 * Created by exizhai on 24/12/2014.
 */
public interface Sender {

	static String SENDER_SERVER_HOSTS = "mail.sender.host";
	static String SENDER_SERVER_PORT = "mail.sender.port";
	static String SENDER_SERVER_PROTOCOL = "mail.sender.protocol";
	static String SENDER_SERVER_USERNAME = "mail.sender.account";
	static String SENDER_SERVER_PASSWORD = "mail.sender.password";

	public void send(Email email, String user, String password);

}
