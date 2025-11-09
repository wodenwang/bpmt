package com.riversoft.platform.mail;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import com.riversoft.platform.mail.script.MailHelper;
import jodd.mail.Email;
import jodd.mail.EmailMessage;
import jodd.mail.ReceivedEmail;

import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import com.riversoft.core.BeanFactory;
import com.riversoft.core.Config;

/**
 * Created by exizhai on 22/12/2014.
 */
@Ignore
public class MailTest {

	@BeforeClass
	public static void beforeClass() {
		BeanFactory.init("classpath:mail/applicationContext-mail.xml");
	}

	@Test
	public void sendSimple() {
		Config.set("mail.sender.protocol", "smtp");

		Email email = Email.create().from("zhongzhifeng@boomsense.com").to("borball@foxmail.com").subject("这是一封测试邮件")
				.addText("Hi: \n 这里是正文.\n\nBorball");

		Sender sender = PostOffice.getInstance().getSender();

		sender.send(email, "zhongzhifeng@boomsense.com", "riversoft123456");
	}

	@Ignore
	public void sendSSLSimple() {
		Config.set("mail.sender.protocol", "smtps");

		Email email = Email.create().from("zhongzhifeng@boomsense.com").to("borball@foxmail.com")
				.subject("这是一封测试邮件(SSL)").addText("Hi: \n 这里是正文.\n\nBorball");

		Sender sender = PostOffice.getInstance().getSender();

		sender.send(email, "zhongzhifeng@boomsense.com", "riversoft123456");
	}

	@Test
	public void receivePop3() throws ParseException {
		Config.set("mail.receiver.protocol", "pop");

		Receiver receiver = PostOffice.getInstance().getReceiver();
		CustomerEmailFilter emailFilter = new CustomerEmailFilter();
		emailFilter.after(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse("2014-12-05 00:00:00"));

		ReceivedEmail[] emails = receiver.receive(emailFilter, "zhongzhifeng@boomsense.com", "riversoft123456");

		if (emails != null) {
			for (ReceivedEmail email : emails) {
				System.out.println("=====begin[" + email.getMessageNumber() + "]=====");
				System.out.println("=subject:" + email.getSubject());
				System.out.println("=from:" + email.getFrom().getEmail());
				for (EmailMessage message : email.getAllMessages()) {
					System.out.println("++++++++++++++++分割线+++++++++++++++++++++");
					System.out.println(message.getMimeType());
					System.out.println(message.getContent());
				}
				System.out.println(email.getSentDate());
				System.out.println(email.getReceiveDate());
				System.out.println("=====end[" + email.getMessageNumber() + "]=====");
			}
		}
	}

	@Ignore
	public void receiveImap() throws ParseException {
		Config.set("mail.receiver.protocol", "imap");

		Receiver receiver = PostOffice.getInstance().getReceiver();
		CustomerEmailFilter emailFilter = new CustomerEmailFilter();
		emailFilter.after(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse("2014-12-05 00:00:00"));

		ReceivedEmail[] emails = receiver.receive(emailFilter, "zhongzhifeng@boomsense.com", "riversoft123456");

		if (emails != null) {
			for (ReceivedEmail email : emails) {
				System.out.print(email.getMessageNumber());
				System.out.print(",");
				System.out.print(email.getSentDate());
				System.out.print(",");
				System.out.print(email.getFrom().getEmail());
				System.out.print(",");
				System.out.println(email.getSubject());
			}
		}
	}

	@Ignore
	public void testHelper(){
		Config.set("mail.sender.host", "smtp.qq.com");
		Config.set("mail.sender.account", "borball@foxmail.com");
		Config.set("mail.sender.password", "12345678");

		MailHelper.asyncSystemSend("subject", "content", "52658481@qq.com");

		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
