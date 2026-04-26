/*
 * $HeadURL: $
 * $Id: $
 * Copyright (c) 2015 by Riversoft System, all rights reserved.
 */
package com.riversoft.module.mail;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import jodd.mail.Email;
import jodd.mail.EmailAttachment;
import jodd.mail.EmailMessage;
import jodd.mail.MailAddress;
import jodd.mail.ReceivedEmail;
import jodd.mail.att.FileAttachment;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StreamUtils;

import com.riversoft.core.Config;
import com.riversoft.core.db.DataPO;
import com.riversoft.core.exception.ExceptionType;
import com.riversoft.core.exception.SystemRuntimeException;
import com.riversoft.platform.SessionManager;
import com.riversoft.platform.db.ORMAdapterService;
import com.riversoft.platform.mail.CustomerEmailFilter;
import com.riversoft.platform.mail.PostOffice;
import com.riversoft.platform.mail.Receiver;
import com.riversoft.platform.mail.Sender;
import com.riversoft.platform.mail.model.AccountModelKeys;
import com.riversoft.platform.mail.model.InboxModelKeys;
import com.riversoft.platform.mail.model.OutboxModelKeys;
import com.riversoft.platform.web.FileManager;
import com.riversoft.platform.web.FileManager.UploadFile;

/**
 * 邮件服务
 * 
 * @author woden
 * 
 */
public class MailService {
	/**
	 * Logger for this class
	 */
	private static final Logger logger = LoggerFactory.getLogger(MailService.class);

	/**
	 * 接收邮件
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public int executeReceive() {
		String inboxTableName = Config.get("mail.table.inbox");// 收件箱表
		String accountTableName = Config.get("mail.table.account");// 账号表

		String uid = SessionManager.getUser().getUid();

		Map<String, Object> account = (Map<String, Object>) ORMAdapterService.getInstance().findByPk(accountTableName,
				uid);
		if (account == null) {
			throw new SystemRuntimeException(ExceptionType.MAIL, "用户[" + uid + "]没有设置邮箱账号.");
		}

		Receiver receiver = PostOffice.getInstance().getReceiver();
		Date now = new Date();// 最后保存到last_check
		CustomerEmailFilter emailFilter = new CustomerEmailFilter().after((Date) account
				.get(AccountModelKeys.LAST_CHECK.name()));
		ReceivedEmail[] emails = receiver.receive(emailFilter,
				(String) account.get(AccountModelKeys.MAIL_ACCOUNT.name()),
				(String) account.get(AccountModelKeys.MAIL_PASSWORD.name()));
		if (emails != null) {
			for (ReceivedEmail email : emails) {
				DataPO po = new DataPO(inboxTableName);
				po.set(InboxModelKeys.USER_ID.name(), uid);
				po.set(InboxModelKeys.SUBJECT.name(), email.getSubject());
				po.set(InboxModelKeys.FROM_ADDR.name(), email.getFrom().getEmail());
				po.set(InboxModelKeys.SENT_DATE.name(), email.getSentDate());
				po.set(InboxModelKeys.RECEIVE_DATE.name(), email.getReceiveDate());
				po.set(InboxModelKeys.STATE.name(), 0);// 未读
				{
					List<String> addrs = new ArrayList<>();
					for (MailAddress addr : email.getTo()) {
						addrs.add(addr.getEmail());
					}
					po.set(InboxModelKeys.TO_ADDRS.name(), StringUtils.join(addrs, ";"));
				}
				{
					List<String> addrs = new ArrayList<>();
					for (MailAddress addr : email.getCc()) {
						addrs.add(addr.getEmail());
					}
					po.set(InboxModelKeys.CC_ADDRS.name(), StringUtils.join(addrs, ";"));
				}
				{
					List<String> addrs = new ArrayList<>();
					for (MailAddress addr : email.getBcc()) {
						addrs.add(addr.getEmail());
					}
					po.set(InboxModelKeys.BCC_ADDRS.name(), StringUtils.join(addrs, ";"));
				}
				// 内容
				{
					String content = null;
					List<EmailMessage> allMessage = email.getAllMessages();
					for (EmailMessage message : allMessage) {
						if ("text/html".equalsIgnoreCase(message.getMimeType())) {
							content = message.getContent();
						}
					}
					if (content == null) {
						content = allMessage.get(0).getContent();
					}
					po.set(InboxModelKeys.CONTENT.name(), content);
				}
				// 附件
				{
					List<EmailAttachment> attachments = email.getAttachments();
					List<UploadFile> files = new ArrayList<>();
					List<UploadFile> contentFiles = new ArrayList<>();// 普通附件
					if (attachments != null) {
						for (EmailAttachment attachment : attachments) {
							try {
								if (StringUtils.isEmpty(attachment.getContentId())) {
									UploadFile file = FileManager.saveDbFile(attachment.getName(),
											StreamUtils.copyToByteArray(attachment.getDataSource().getInputStream()));
									files.add(file);
								} else {
									String cid = attachment.getContentId();
									if (cid.startsWith("<")) {
										cid = cid.substring(1);
									}
									if (cid.endsWith(">")) {
										cid = cid.substring(0, cid.length() - 1);
									}
									UploadFile file = FileManager.saveDbFile(cid,
											StreamUtils.copyToByteArray(attachment.getDataSource().getInputStream()));
									contentFiles.add(file);
								}
							} catch (IOException e) {
								logger.warn("解析附件[" + attachment.getName() + "]错误.", e);
								continue;
							}
						}
					}
					if (files.size() > 0) {
						po.set(InboxModelKeys.ATTACHMENT.name(), FileManager.toBytes(null, files));
					}
					if (contentFiles.size() > 0) {
						po.set(InboxModelKeys.CONTENT_ATTACHMENT.name(), FileManager.toBytes(null, contentFiles));
					}
				}
				ORMAdapterService.getInstance().save(po.toEntity());// 保存
			}
		}

		if (emails != null && emails.length > 0) {// 接收过邮件则修改最后接收时间
			account.put(AccountModelKeys.LAST_CHECK.name(), now);
			ORMAdapterService.getInstance().update(account);
		}

		return emails == null ? 0 : emails.length;
	}

	/**
	 * 批量删除
	 * 
	 * @param ids
	 */
	@SuppressWarnings("unchecked")
	public void executeRemoveReceiveMail(Long... ids) {
		String inboxTableName = Config.get("mail.table.inbox");// 收件箱表
		if (StringUtils.isEmpty(inboxTableName)) {
			throw new SystemRuntimeException(ExceptionType.BUSINESS, "未设置邮件服务,请联系管理员处理.");
		}

		for (Long id : ids) {
			Map<String, Object> vo = (Map<String, Object>) ORMAdapterService.getInstance().findByPk(inboxTableName, id);
			// check user
			if (SessionManager.getUser().getUid().equals(vo.get(InboxModelKeys.USER_ID.name()))) {
				ORMAdapterService.getInstance().remove(vo);
			} else {
				new SystemRuntimeException(ExceptionType.BUSINESS, "不允许删除用户[" + vo.get(InboxModelKeys.USER_ID.name())
						+ "]的邮件.");
			}
		}
	}

	/**
	 * 删除发件箱
	 * 
	 * @param ids
	 */
	@SuppressWarnings("unchecked")
	public void executeRemoveSendMail(Long... ids) {
		String outboxTableName = Config.get("mail.table.outbox");// 收件箱表
		if (StringUtils.isEmpty(outboxTableName)) {
			throw new SystemRuntimeException(ExceptionType.BUSINESS, "未设置邮件服务,请联系管理员处理.");
		}

		for (Long id : ids) {
			Map<String, Object> vo = (Map<String, Object>) ORMAdapterService.getInstance()
					.findByPk(outboxTableName, id);
			// check user
			if (SessionManager.getUser().getUid().equals(vo.get(InboxModelKeys.USER_ID.name()))) {
				ORMAdapterService.getInstance().remove(vo);
			} else {
				new SystemRuntimeException(ExceptionType.BUSINESS, "不允许删除用户[" + vo.get(InboxModelKeys.USER_ID.name())
						+ "]的邮件.");
			}
		}
	}

	/**
	 * 暂存
	 * 
	 * @param mail
	 */
	public void executeSaveSendMail(Map<String, Object> mail) {
		mail.put(OutboxModelKeys.STATE.name(), 0);// 未发送
		if (mail.get(OutboxModelKeys.ID.name()) == null) {
			mail.put(OutboxModelKeys.USER_ID.name(), SessionManager.getUser().getUid());
			mail.put(OutboxModelKeys.CREATE_DATE.name(), new Date());
			ORMAdapterService.getInstance().save(mail);
		} else {
			ORMAdapterService.getInstance().update(mail);
		}
	}

	/**
	 * 发送
	 * 
	 * @param mail
	 */
	@SuppressWarnings("unchecked")
	public void executeSendMail(Map<String, Object> mail) {
		executeSaveSendMail(mail);

		String accountTableName = Config.get("mail.table.account");// 账号表
		String uid = SessionManager.getUser().getUid();
		Map<String, Object> account = (Map<String, Object>) ORMAdapterService.getInstance().findByPk(accountTableName,
				uid);
		if (account == null) {
			throw new SystemRuntimeException(ExceptionType.MAIL, "用户[" + uid + "]没有设置邮箱账号.");
		}

		// 发送
		Email email = Email.create().from((String) account.get(AccountModelKeys.MAIL_NAME.name()));

		email.subject((String) mail.get(OutboxModelKeys.SUBJECT.name()));
		email.addHtml((String) mail.get(OutboxModelKeys.CONTENT.name()));

		String toAddrs = (String) mail.get(OutboxModelKeys.TO_ADDRS.name());
		if (StringUtils.isNotEmpty(toAddrs)) {
			for (String addr : toAddrs.split(";")) {
				email.addTo(new MailAddress(addr));
			}
		}
		String ccAddrs = (String) mail.get(OutboxModelKeys.CC_ADDRS.name());
		if (StringUtils.isNotEmpty(ccAddrs)) {
			for (String addr : ccAddrs.split(";")) {
				email.addCc(new MailAddress(addr));
			}
		}
		String bccAddrs = (String) mail.get(OutboxModelKeys.BCC_ADDRS.name());
		if (StringUtils.isNotEmpty(bccAddrs)) {
			for (String addr : bccAddrs.split(";")) {
				email.addBcc(new MailAddress(addr));
			}
		}

		// 附件
		if (mail.get(OutboxModelKeys.ATTACHMENT.name()) != null) {
			List<UploadFile> files = FileManager.toFiles((byte[]) mail.get(OutboxModelKeys.ATTACHMENT.name()));
			if (files != null) {
				for (UploadFile file : files) {
					// 添加附件,TODO,后续可以设置contentId
					email.attach(new FileAttachment(file.getFile(), file.getName(), null));
				}
			}
		}

		Sender sender = PostOffice.getInstance().getSender();
		try {
			sender.send(email, (String) account.get(AccountModelKeys.MAIL_ACCOUNT.name()),
					(String) account.get(AccountModelKeys.MAIL_PASSWORD.name()));
		} catch (Exception e) {
			logger.warn("邮件发送失败", e);
			throw new SystemRuntimeException(ExceptionType.MAIL, e);
		}

		mail.put(OutboxModelKeys.STATE.name(), 1);// 已发送
		mail.put(OutboxModelKeys.SENT_DATE.name(), new Date());
		ORMAdapterService.getInstance().update(mail);

	}
}
