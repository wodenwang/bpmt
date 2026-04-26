/*
 * $HeadURL: $
 * $Id: $
 * Copyright (c) 2014 by Riversoft System, all rights reserved.
 */
package com.riversoft.module.mail;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import jodd.mail.Email;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.riversoft.core.BeanFactory;
import com.riversoft.core.Config;
import com.riversoft.core.db.DataCondition;
import com.riversoft.core.db.DataPO;
import com.riversoft.core.db.DataPackage;
import com.riversoft.core.exception.ExceptionType;
import com.riversoft.core.exception.SystemRuntimeException;
import com.riversoft.core.web.Actions;
import com.riversoft.core.web.Actions.Util;
import com.riversoft.core.web.FreeMarkerUtils;
import com.riversoft.core.web.RequestUtils;
import com.riversoft.platform.SessionManager;
import com.riversoft.platform.db.ORMAdapterService;
import com.riversoft.platform.mail.PostOffice;
import com.riversoft.platform.mail.Sender;
import com.riversoft.platform.mail.model.AccountModelKeys;
import com.riversoft.platform.mail.model.InboxModelKeys;
import com.riversoft.platform.mail.model.OutboxModelKeys;
import com.riversoft.platform.web.FileManager;
import com.riversoft.platform.web.FileManager.UploadFile;
import com.riversoft.platform.web.view.annotation.Conf;
import com.riversoft.platform.web.view.annotation.Conf.TargetType;
import com.riversoft.platform.web.view.annotation.Sys;
import com.riversoft.platform.web.view.annotation.Sys.SysMethod;

/**
 * @author woden
 * 
 */
@Sys
public class MailAction {
	/**
	 * Logger for this class
	 */
	static final Logger logger = LoggerFactory.getLogger(MailAction.class);

	/**
	 * 邮箱框
	 * 
	 * @param request
	 * @param response
	 */
	@SysMethod
	@Conf(description = "个人邮件", sort = 2, target = { TargetType.MENU, TargetType.HOME })
	public void index(HttpServletRequest request, HttpServletResponse response) {

		String accountTableName = Config.get("mail.table.account");// 账号表
		if (StringUtils.isEmpty(accountTableName)) {
			throw new SystemRuntimeException(ExceptionType.BUSINESS, "未设置邮件服务,请联系管理员处理.");
		}

		Actions.includePage(request, response, Util.getPagePath(request, "main.jsp"));
	}

	/**
	 * 邮箱设置
	 * 
	 * @param request
	 * @param response
	 */
	@SuppressWarnings("unchecked")
	public void setting(HttpServletRequest request, HttpServletResponse response) {
		String accountTableName = Config.get("mail.table.account");// 账号表
		if (StringUtils.isEmpty(accountTableName)) {
			throw new SystemRuntimeException(ExceptionType.BUSINESS, "未设置邮件服务,请联系管理员处理.");
		}

		Map<String, Object> account = (Map<String, Object>) ORMAdapterService.getInstance().findByPk(accountTableName,
				SessionManager.getUser().getUid());

		if (account == null) {
			throw new SystemRuntimeException(ExceptionType.BUSINESS, "管理员未在系统帮您分配邮箱账号,请联系管理员..");
		}

		request.setAttribute("account", account);

		Actions.includePage(request, response, Util.getPagePath(request, "setting.jsp"));
	}

	/**
	 * 保存设置
	 * 
	 * @param request
	 * @param response
	 */
	@SuppressWarnings("unchecked")
	public void submitSetting(HttpServletRequest request, HttpServletResponse response) {
		String password = RequestUtils.getStringValue(request, "password");
		int testFlag = RequestUtils.getIntegerValue(request, "testFlag");

		String accountTableName = Config.get("mail.table.account");// 账号表
		Map<String, Object> account = (Map<String, Object>) ORMAdapterService.getInstance().findByPk(accountTableName,
				SessionManager.getUser().getUid());
		account.put(AccountModelKeys.MAIL_PASSWORD.getColumn().getName(), password);

		String address = (String) account.get(AccountModelKeys.MAIL_NAME.getColumn().getName());
		if (testFlag == 1) {
			try {
				Email email = Email.create().from(address).to(address).subject("BPMT邮箱连接测试").addText("BPMT邮箱连接测试");
				Sender sender = PostOffice.getInstance().getSender();
				sender.send(email, (String) account.get(AccountModelKeys.MAIL_ACCOUNT.getColumn().getName()),
						(String) account.get(AccountModelKeys.MAIL_PASSWORD.getColumn().getName()));
			} catch (Exception e) {
				throw new SystemRuntimeException(ExceptionType.MAIL, "发送测试邮件失败.", e);
			}
		}

		ORMAdapterService.getInstance().update(account);
		Actions.redirectInfoPage(request, response, "保存设置成功.");

	}

	/**
	 * 收件箱
	 * 
	 * @param request
	 * @param response
	 */
	public void inboxMain(HttpServletRequest request, HttpServletResponse response) {
		Actions.includePage(request, response, Util.getPagePath(request, "inbox_main.jsp"));
	}

	/**
	 * 收件箱列表
	 * 
	 * @param request
	 * @param response
	 */
	public void listInbox(HttpServletRequest request, HttpServletResponse response) {

		String inboxTableName = Config.get("mail.table.inbox");// 收件箱表
		if (StringUtils.isEmpty(inboxTableName)) {
			throw new SystemRuntimeException(ExceptionType.BUSINESS, "未设置邮件服务,请联系管理员处理.");
		}

		// 获取分页信息
		int start = Util.getStart(request);
		int limit = Util.getLimit(request);

		// 获取排序信息
		String field = Util.getSortField(request);
		String dir = Util.getSortDir(request);

		// 查询条件
		DataCondition condition = new DataCondition(Util.buildQueryMap(new HashMap<String, Object>(), request));
		condition.setOrderBy(field, dir);
		condition.setStringEqual(InboxModelKeys.USER_ID.name(), SessionManager.getUser().getUid());// 当前用户的邮件

		DataPackage dp = ORMAdapterService.getInstance().queryPackage(inboxTableName, start, limit,
				condition.toEntity());
		request.setAttribute("dp", dp);

		Actions.includePage(request, response, Util.getPagePath(request, "inbox_list.jsp"));
	}

	/**
	 * 收邮件
	 * 
	 * @param request
	 * @param response
	 */
	public void receiveMail(HttpServletRequest request, HttpServletResponse response) {
		MailService service = BeanFactory.getInstance().getBean(MailService.class);
		int size = service.executeReceive();
		Actions.redirectInfoPage(request, response, "已接收[" + size + "]封邮件.");
	}

	/**
	 * 查看收件
	 * 
	 * @param request
	 * @param response
	 */
	@SuppressWarnings("unchecked")
	public void showReceiveMail(HttpServletRequest request, HttpServletResponse response) {
		Long id = RequestUtils.getLongValue(request, "id");
		String inboxTableName = Config.get("mail.table.inbox");// 收件箱表
		if (StringUtils.isEmpty(inboxTableName)) {
			throw new SystemRuntimeException(ExceptionType.BUSINESS, "未设置邮件服务,请联系管理员处理.");
		}

		Map<String, Object> vo = (Map<String, Object>) ORMAdapterService.getInstance().findByPk(inboxTableName, id);
		request.setAttribute("vo", vo);

		// check user
		if (!SessionManager.getUser().getUid().equals(vo.get(InboxModelKeys.USER_ID.name()))) {
			throw new SystemRuntimeException(ExceptionType.BUSINESS, "无权查看此邮件.");
		}

		vo.put(InboxModelKeys.STATE.name(), 1);// 标记为已读
		ORMAdapterService.getInstance().update(vo);

		String acp = Util.getContextPath(request) + Util.getActionUrl(request);
		request.setAttribute(
				"content",
				createContentWithImage(acp, (String) vo.get(InboxModelKeys.CONTENT.name()),
						(Long) vo.get(InboxModelKeys.ID.name())));

		Actions.includePage(request, response, Util.getPagePath(request, "inbox_detail.jsp"));
	}

	/**
	 * 删除收件
	 * 
	 * @param request
	 * @param response
	 */
	public void removeReceiveMail(HttpServletRequest request, HttpServletResponse response) {
		List<Long> ids = RequestUtils.getLongValues(request, "_keys");
		MailService service = BeanFactory.getInstance().getBean(MailService.class);
		service.executeRemoveReceiveMail(ids.toArray(new Long[0]));
		Actions.redirectInfoPage(request, response, "邮件已删除.");
	}

	/**
	 * 展示图片
	 * 
	 * @param request
	 * @param response
	 */
	@SuppressWarnings("unchecked")
	public void showContentPic(HttpServletRequest request, HttpServletResponse response) {
		Long id = RequestUtils.getLongValue(request, "id");
		String cid = RequestUtils.getStringValue(request, "cid");
		String inboxTableName = Config.get("mail.table.inbox");// 收件箱表
		if (StringUtils.isEmpty(inboxTableName)) {
			throw new SystemRuntimeException(ExceptionType.BUSINESS, "未设置邮件服务,请联系管理员处理.");
		}
		Map<String, Object> vo = (Map<String, Object>) ORMAdapterService.getInstance().findByPk(inboxTableName, id);
		// check user
		if (!SessionManager.getUser().getUid().equals(vo.get(InboxModelKeys.USER_ID.name()))) {
			throw new SystemRuntimeException(ExceptionType.BUSINESS, "无权查看此邮件.");
		}

		List<UploadFile> files = FileManager.toFiles((byte[]) vo.get(InboxModelKeys.CONTENT_ATTACHMENT.name()));

		InputStream is = null;
		for (UploadFile file : files) {
			if (file.getName().equals(cid)) {
				try {
					is = file.getInputStream();
				} catch (FileNotFoundException e) {
					throw new SystemRuntimeException(e);
				}
			}
		}

		Actions.download(request, response, cid + ".jpg", is);
	}

	/**
	 * 发件箱
	 * 
	 * @param request
	 * @param response
	 */
	public void outboxMain(HttpServletRequest request, HttpServletResponse response) {
		Actions.includePage(request, response, Util.getPagePath(request, "outbox_main.jsp"));
	}

	/**
	 * 发件箱列表
	 * 
	 * @param request
	 * @param response
	 */
	public void listOutbox(HttpServletRequest request, HttpServletResponse response) {

		String outboxTableName = Config.get("mail.table.outbox");// 收件箱表
		if (StringUtils.isEmpty(outboxTableName)) {
			throw new SystemRuntimeException(ExceptionType.BUSINESS, "未设置邮件服务,请联系管理员处理.");
		}

		// 获取分页信息
		int start = Util.getStart(request);
		int limit = Util.getLimit(request);

		// 获取排序信息
		String field = Util.getSortField(request);
		String dir = Util.getSortDir(request);

		// 查询条件
		DataCondition condition = new DataCondition(Util.buildQueryMap(new HashMap<String, Object>(), request));
		condition.setOrderBy(field, dir);
		condition.setStringEqual(InboxModelKeys.USER_ID.name(), SessionManager.getUser().getUid());// 当前用户的邮件

		DataPackage dp = ORMAdapterService.getInstance().queryPackage(outboxTableName, start, limit,
				condition.toEntity());
		request.setAttribute("dp", dp);

		Actions.includePage(request, response, Util.getPagePath(request, "outbox_list.jsp"));
	}

	/**
	 * 删除收件
	 * 
	 * @param request
	 * @param response
	 */
	public void removeSendMail(HttpServletRequest request, HttpServletResponse response) {
		List<Long> ids = RequestUtils.getLongValues(request, "_keys");
		MailService service = BeanFactory.getInstance().getBean(MailService.class);
		service.executeRemoveSendMail(ids.toArray(new Long[0]));
		Actions.redirectInfoPage(request, response, "邮件已删除.");
	}

	/**
	 * 写邮件表单
	 * 
	 * @param request
	 * @param response
	 */
	@SuppressWarnings("unchecked")
	public void outboxForm(HttpServletRequest request, HttpServletResponse response) {

		Long id = RequestUtils.getLongValue(request, "id");
		Integer type = RequestUtils.getIntegerValue(request, "type");// 发送类型.0:新邮件,1:回复全部,2:回复,3:转发,4:编辑,5:转发已发送
		String acp = Util.getContextPath(request) + Util.getActionUrl(request);
		String inboxTableName = Config.get("mail.table.inbox");// 收件箱表
		String outboxTableName = Config.get("mail.table.outbox");// 发件箱表
		if (StringUtils.isEmpty(inboxTableName) || StringUtils.isEmpty(outboxTableName)) {
			throw new SystemRuntimeException(ExceptionType.BUSINESS, "未设置邮件服务,请联系管理员处理.");
		}

		Map<String, Object> vo = new HashMap<>();
		String content = null;
		switch (type) {
		case 0: {// 新邮件
		}
			break;
		case 1: {// 回复全部
			Map<String, Object> inboxMail = (Map<String, Object>) ORMAdapterService.getInstance().findByPk(
					inboxTableName, id);
			// check user
			if (!SessionManager.getUser().getUid().equals(inboxMail.get(InboxModelKeys.USER_ID.name()))) {
				throw new SystemRuntimeException(ExceptionType.BUSINESS, "无权查看此邮件.");
			}
			vo.put(OutboxModelKeys.TO_ADDRS.name(), inboxMail.get(InboxModelKeys.FROM_ADDR.name()));
			vo.put(OutboxModelKeys.CC_ADDRS.name(), inboxMail.get(InboxModelKeys.CC_ADDRS.name()));
			vo.put(OutboxModelKeys.SUBJECT.name(), "RE:" + inboxMail.get(InboxModelKeys.SUBJECT.name()));
			content = createReContent(inboxMail);
			content = createContentWithImage(acp, content, id);
		}
			break;
		case 2: {// 回复
			Map<String, Object> inboxMail = (Map<String, Object>) ORMAdapterService.getInstance().findByPk(
					inboxTableName, id);
			// check user
			if (!SessionManager.getUser().getUid().equals(inboxMail.get(InboxModelKeys.USER_ID.name()))) {
				throw new SystemRuntimeException(ExceptionType.BUSINESS, "无权查看此邮件.");
			}
			vo.put(OutboxModelKeys.TO_ADDRS.name(), inboxMail.get(InboxModelKeys.FROM_ADDR.name()));
			vo.put(OutboxModelKeys.SUBJECT.name(), "RE:" + inboxMail.get(InboxModelKeys.SUBJECT.name()));
			content = createReContent(inboxMail);
			content = createContentWithImage(acp, content, id);
		}
			break;
		case 3: {// 转发
			Map<String, Object> inboxMail = (Map<String, Object>) ORMAdapterService.getInstance().findByPk(
					inboxTableName, id);
			// check user
			if (!SessionManager.getUser().getUid().equals(inboxMail.get(InboxModelKeys.USER_ID.name()))) {
				throw new SystemRuntimeException(ExceptionType.BUSINESS, "无权查看此邮件.");
			}
			vo.put(OutboxModelKeys.SUBJECT.name(), "FW:" + inboxMail.get(InboxModelKeys.SUBJECT.name()));
			vo.put(OutboxModelKeys.ATTACHMENT.name(), inboxMail.get(InboxModelKeys.ATTACHMENT.name()));
			content = createReContent(inboxMail);
			content = createContentWithImage(acp, content, id);
		}
			break;
		case 4: {// 编辑
			vo = (Map<String, Object>) ORMAdapterService.getInstance().findByPk(outboxTableName, id);
			// check user
			if (!SessionManager.getUser().getUid().equals(vo.get(InboxModelKeys.USER_ID.name()))) {
				throw new SystemRuntimeException(ExceptionType.BUSINESS, "无权查看此邮件.");
			}
			content = (String) vo.get(OutboxModelKeys.CONTENT.name());
			request.setAttribute("id", vo.get(OutboxModelKeys.ID.name()));
		}
			break;
		case 5: {// 转发已发送
			Map<String, Object> outboxMail = (Map<String, Object>) ORMAdapterService.getInstance().findByPk(
					outboxTableName, id);
			// check user
			if (!SessionManager.getUser().getUid().equals(outboxMail.get(InboxModelKeys.USER_ID.name()))) {
				throw new SystemRuntimeException(ExceptionType.BUSINESS, "无权查看此邮件.");
			}
			vo.put(OutboxModelKeys.SUBJECT.name(), "FW:" + outboxMail.get(InboxModelKeys.SUBJECT.name()));
			vo.put(OutboxModelKeys.ATTACHMENT.name(), outboxMail.get(InboxModelKeys.ATTACHMENT.name()));
			content = createReContent(outboxMail);
		}
			break;
		default:
			break;
		}

		request.setAttribute("content", content);
		request.setAttribute("vo", vo);
		Actions.includePage(request, response, Util.getPagePath(request, "outbox_form.jsp"));
	}

	/**
	 * 生成回复/转发内容
	 * 
	 * @param inboxMail
	 * @return
	 */
	private String createReContent(Map<String, Object> inboxMail) {
		return FreeMarkerUtils.process("classpath:ftl/replyMail.ftl", inboxMail);
	}

	/**
	 * 获取转换了图片的内容
	 * 
	 * @param acp
	 * @param content
	 * @param inboxId
	 * @return
	 */
	private String createContentWithImage(String acp, String content, Long inboxId) {
		// 解析content,过滤cid
		String regex = "<\\s*img[^>]+src\\s*=\\s*['\"]([^\"'>]+)['\"][^>]*>";
		Pattern p = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
		Matcher m = p.matcher(content);
		List<String> imgs = new ArrayList<>();
		List<String> newImgs = new ArrayList<>();
		while (m.find()) {
			String img = m.group(0);
			String url = m.group(1);
			imgs.add(img);
			newImgs.add(m.group(0).replaceAll(url,
					acp + "/showContentPic.shtml?id=" + inboxId + "&cid=" + url.substring(4)));
		}
		content = StringUtils.replaceEach(content, imgs.toArray(new String[0]), newImgs.toArray(new String[0]));
		return content;
	}

	/**
	 * 发信提交
	 * 
	 * @param request
	 * @param response
	 */
	@SuppressWarnings("unchecked")
	public void submitOutboxForm(HttpServletRequest request, HttpServletResponse response) {
		Long id = RequestUtils.getLongValue(request, "id");
		Integer sendFlag = RequestUtils.getIntegerValue(request, "sendFlag");

		String inboxTableName = Config.get("mail.table.inbox");// 收件箱表
		String outboxTableName = Config.get("mail.table.outbox");// 发件箱表
		if (StringUtils.isEmpty(inboxTableName) || StringUtils.isEmpty(outboxTableName)) {
			throw new SystemRuntimeException(ExceptionType.BUSINESS, "未设置邮件服务,请联系管理员处理.");
		}
		MailService service = BeanFactory.getInstance().getBean(MailService.class);
		DataPO po;
		if (id != null && id.longValue() > 0) {
			po = new DataPO(outboxTableName, (Map<String, Object>) ORMAdapterService.getInstance().findByPk(
					outboxTableName, id)); // check user
			if (!SessionManager.getUser().getUid().equals(po.get(InboxModelKeys.USER_ID.name()))) {
				throw new SystemRuntimeException(ExceptionType.BUSINESS, "无权查看此邮件.");
			}
		} else {
			po = new DataPO(outboxTableName);
		}

		// 设值
		po.set(OutboxModelKeys.ID.name(), id == null || id.intValue() == 0 ? null : id);
		po.set(OutboxModelKeys.SUBJECT.name(), RequestUtils.getStringValue(request, OutboxModelKeys.SUBJECT.name()));
		po.set(OutboxModelKeys.TO_ADDRS.name(), RequestUtils.getStringValue(request, OutboxModelKeys.TO_ADDRS.name()));
		po.set(OutboxModelKeys.CC_ADDRS.name(), RequestUtils.getStringValue(request, OutboxModelKeys.CC_ADDRS.name()));
		po.set(OutboxModelKeys.BCC_ADDRS.name(), RequestUtils.getStringValue(request, OutboxModelKeys.BCC_ADDRS.name()));
		po.set(OutboxModelKeys.CONTENT.name(), RequestUtils.getStringValue(request, OutboxModelKeys.CONTENT.name()));
		po.set(OutboxModelKeys.ATTACHMENT.name(),
				FileManager.toBytes(null, FileManager.getUploadFiles(request, OutboxModelKeys.ATTACHMENT.name())));

		if (sendFlag != null && sendFlag.intValue() == 1) {
			service.executeSendMail(po.toEntity());
			Actions.redirectInfoPage(request, response, "邮件已暂存,您可以在发件箱查询到相应记录.");
		} else {
			service.executeSaveSendMail(po.toEntity());
			Actions.redirectInfoPage(request, response, "邮件已发送成功,您可以在发件箱查询到相应记录.");
		}
	}
}
