/*
 * $HeadURL: $
 * $Id: $
 * Copyright (c) 2015 by Riversoft System, all rights reserved.
 */
package com.riversoft.platform.mail.script;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.riversoft.core.Config;
import com.riversoft.core.db.DataPO;
import com.riversoft.core.exception.ExceptionType;
import com.riversoft.core.exception.SystemRuntimeException;
import com.riversoft.core.script.annotation.ScriptSupport;
import com.riversoft.platform.SessionManager;
import com.riversoft.platform.db.ORMAdapterService;
import com.riversoft.platform.mail.PostOffice;
import com.riversoft.platform.mail.Sender;
import com.riversoft.platform.mail.model.AccountModelKeys;
import com.riversoft.platform.mail.model.OutboxModelKeys;
import com.riversoft.platform.web.FileManager;
import com.riversoft.platform.web.FileManager.UploadFile;

import jodd.mail.Email;
import jodd.mail.MailAddress;
import jodd.mail.att.FileAttachment;

/**
 * @author woden
 * 
 */
@ScriptSupport(value = "mail", description = "邮件函数")
public class MailHelper {
	/**
	 * Logger for this class
	 */
	private static final Logger logger = LoggerFactory.getLogger(MailHelper.class);

	/**
	 * 邮件发送
	 * 
	 * @param subject
	 * @param content
	 * @param attachment
	 * @param toAddrs
	 */
	@SuppressWarnings("unchecked")
	public static void send(String subject, String content, byte[] attachment, String... toAddrs) {
		String accountTableName = Config.get("mail.table.account");// 账号表
		String uid = SessionManager.getUser().getUid();
		Map<String, Object> account = (Map<String, Object>) ORMAdapterService.getInstance().findByPk(accountTableName,
				uid);
		if (account == null) {
			throw new SystemRuntimeException(ExceptionType.MAIL, "用户[" + uid + "]没有设置邮箱账号.");
		}

		String user = (String) account.get(AccountModelKeys.MAIL_NAME.name());
		String password = (String) account.get(AccountModelKeys.MAIL_PASSWORD.name());

		send(subject, content, attachment, user, password, toAddrs, null);

		String outboxTableName = Config.get("mail.table.outbox");// 发件箱
		if (StringUtils.isNotEmpty(outboxTableName)) {
			DataPO mail = new DataPO(outboxTableName);
			mail.set(OutboxModelKeys.USER_ID.name(), SessionManager.getUser().getUid());
			mail.set(OutboxModelKeys.CREATE_DATE.name(), new Date());
			mail.set(OutboxModelKeys.STATE.name(), 1);// 已发送
			mail.set(OutboxModelKeys.SENT_DATE.name(), new Date());
			mail.set(OutboxModelKeys.SUBJECT.name(), subject);
			mail.set(OutboxModelKeys.CONTENT.name(), content);
			mail.set(OutboxModelKeys.ATTACHMENT.name(), attachment);
			mail.set(OutboxModelKeys.TO_ADDRS.name(), StringUtils.join(toAddrs));
			ORMAdapterService.getInstance().save(mail.toEntity());
		}
	}

	private static void send(String subject, String content, byte[] attachment, String user, String password,
			String[] toAddrs, String[] toCC) {
		// 发送
		Email email = Email.create().from(user);
		email.subject(subject);
		email.addHtml(content);
		for (String addr : toAddrs) {
			email.addTo(new MailAddress(addr));
		}

		for (String cc : toCC) {
			email.addCc(new MailAddress(cc));
		}

		// 附件
		if (attachment != null) {
			List<UploadFile> files = FileManager.toFiles(attachment);
			if (files != null) {
				for (UploadFile file : files) {
					email.attach(new FileAttachment(file.getFile(), file.getName(), null));
				}
			}
		}

		Sender sender = PostOffice.getInstance().getSender();
		try {
			sender.send(email, user, password);
		} catch (Exception e) {
			logger.warn("邮件发送失败", e);
			throw new SystemRuntimeException(ExceptionType.MAIL, e);
		}
	}

	/**
	 * 邮件发送
	 * 
	 * @param subject
	 * @param content
	 * @param toAddrs
	 */
	public static void send(String subject, String content, String... toAddrs) {
		send(subject, content, null, toAddrs);
	}

	/**
	 * 系统邮件发送
	 *
	 * @param subject
	 * @param content
	 * @param toAddrs
	 */
	public static void systemSend(String subject, String content, String... toAddrs) {
		systemSend(subject, content, null, Arrays.asList(toAddrs), null);
	}

	/**
	 * 系统邮件发送
	 *
	 * @param subject
	 * @param content
	 * @param attachment
	 * @param toAddrs
	 */
	public static void systemSend(String subject, String content, byte[] attachment, String... toAddrs) {
		systemSend(subject, content, null, Arrays.asList(toAddrs), null);
	}

	/**
	 * 系统邮件发送
	 * 
	 * @param subject
	 * @param content
	 * @param toAddrs
	 * @param toCCs
	 */
	public static void systemSend(String subject, String content, List<String> toAddrs, List<String> toCCs) {
		systemSend(subject, content, null, toAddrs, toCCs);
	}

	/**
	 * 系统邮件发送
	 * 
	 * @param subject
	 * @param content
	 * @param attachment
	 * @param toAddrs
	 * @param toCCs
	 */
	public static void systemSend(String subject, String content, byte[] attachment, List<String> toAddrs,
			List<String> toCCs) {
		String user = Config.get(Sender.SENDER_SERVER_USERNAME);
		String password = Config.get(Sender.SENDER_SERVER_PASSWORD);
		if (StringUtils.isEmpty(user)) {
			throw new SystemRuntimeException(ExceptionType.MAIL, "系统邮件发送端口没有设置,请联系管理员处理.");
		}
		send(subject, content, attachment, user, password, toAddrs.toArray(new String[] {}),
				toCCs != null ? toCCs.toArray(new String[] {}) : null);
	}

	/**
	 * 异步发送器
	 * 
	 * @author woden
	 *
	 */
	private static class AsyncSenders {
		/**
		 * 邮件对象
		 * 
		 * @author woden
		 *
		 */
		private static class Mail {
			private String subject;
			private String content;
			private byte[] attachment;
			private String[] toAddrs;

			Mail(String subject, String content, byte[] attachment, String... toAddrs) {
				this.subject = subject;
				this.content = content;
				this.attachment = attachment;
				this.toAddrs = toAddrs;
			}
		}

		/**
		 * 发送线程
		 * 
		 * @author woden
		 *
		 */
		private static class AsyncSender implements Runnable {
			private BlockingQueue<Mail> queue;

			AsyncSender(BlockingQueue<Mail> queue) {
				this.queue = queue;
			}

			public void run() {
				while (true) {
					try {
						Mail mail = queue.take();
						systemSend(mail.subject, mail.content, mail.attachment, mail.toAddrs);
					} catch (InterruptedException e) {
						logger.warn("获取待发邮件失败.", e);
					}
				}
			}
		}

		private final static AsyncSenders instance = new AsyncSenders();
		private final static int MAX_THREAD_SIZE = 20;
		private BlockingQueue<Mail> queue = new LinkedBlockingQueue<>();
		private ExecutorService exec = Executors.newFixedThreadPool(MAX_THREAD_SIZE);

		private AsyncSenders() {
			for (int i = 0; i < MAX_THREAD_SIZE; i++) {
				exec.submit(new AsyncSender(queue));
			}
		}

		static void add(Mail mail) {
			instance.queue.add(mail);
		}
	}

	/**
	 * 系统邮件发送(异步)
	 * 
	 * @param subject
	 * @param content
	 * @param attachment
	 * @param toAddrs
	 */
	public static void asyncSystemSend(String subject, String content, byte[] attachment, String... toAddrs) {
		AsyncSenders.add(new AsyncSenders.Mail(subject, content, attachment, toAddrs));
	}

	/**
	 * 系统邮件发送(异步)
	 * 
	 * @param subject
	 * @param content
	 * @param toAddrs
	 */
	public static void asyncSystemSend(String subject, String content, String... toAddrs) {
		asyncSystemSend(subject, content, null, toAddrs);
	}
}
