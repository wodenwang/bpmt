package com.riversoft.platform.mail;

import com.riversoft.core.BeanFactory;
import com.riversoft.core.Config;

/**
 * Created by exizhai on 24/12/2014.
 */
public class PostOffice {

	private static Sender sender;
	private static Receiver receiver;

	public static PostOffice getInstance() {
		PostOffice postOffice = BeanFactory.getInstance().getSingleBean(PostOffice.class);
		return postOffice;
	}

	public Sender getSender() {
		if (sender == null) {
			sender = new SmtpSender();
		}
		return sender;
	}

	public Receiver getReceiver() {
		if (pop3()) {
			receiver = new Pop3Receiver();
		} else {
			receiver = new ImapReceiver();
		}
		return receiver;
	}

	private boolean pop3() {
		String protocol = Config.get(Receiver.RECEIVER_SERVER_PROTOCOL);
		return protocol == null || protocol.toLowerCase().startsWith("pop");
	}
}
