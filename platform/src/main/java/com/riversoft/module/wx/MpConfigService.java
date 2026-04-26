package com.riversoft.module.wx;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import com.riversoft.core.BeanFactory;
import com.riversoft.core.Config;
import com.riversoft.core.context.RequestContext;
import com.riversoft.core.db.DataPO;
import com.riversoft.core.db.ORMService;
import com.riversoft.core.exception.ExceptionType;
import com.riversoft.core.exception.SystemRuntimeException;
import com.riversoft.platform.SessionManager;
import com.riversoft.platform.db.model.ModelKeyUtils;
import com.riversoft.platform.po.TbTable;
import com.riversoft.platform.service.TableService;
import com.riversoft.platform.translate.WxMenuCommandType;
import com.riversoft.weixin.common.exception.WxRuntimeException;
import com.riversoft.weixin.common.menu.Menu;
import com.riversoft.weixin.common.menu.MenuItem;
import com.riversoft.weixin.common.menu.MenuType;
import com.riversoft.weixin.mp.base.AppSetting;
import com.riversoft.weixin.mp.media.Materials;
import com.riversoft.weixin.mp.menu.Menus;
import com.riversoft.weixin.mp.ticket.Tickets;
import com.riversoft.weixin.mp.ticket.bean.Ticket;
import com.riversoft.wx.mp.model.MpVisitorModelKeys;
import com.riversoft.wx.mp.model.MpVisitorTagModelKeys;
import com.riversoft.wx.mp.service.MpAppService;

/**
 * Created by exizhai on 12/27/2015.
 */
public class MpConfigService {

	/**
	 * 获取单例
	 *
	 * @return
	 */
	public static MpConfigService getInstance() {
		return BeanFactory.getInstance().getBean(MpConfigService.class);
	}

	/**
	 * 保存
	 *
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public Map<String, Object> executeCreate() {
		RequestContext request = RequestContext.getCurrent();
		String appId = request.getString("appId");
		String appSecret = request.getString("appSecret");
		String title = request.getString("title");
		String description = request.getString("description");
		String token = request.getString("token");
		String encodingAESKey = request.getString("encodingAESKey");
		String accessTokenUrl = request.getString("accessTokenUrl");
		String mpKey = request.getString("mpKey");

		Integer visitorTableFlag = request.getInteger("visitorTableFlag");
		String visitorTable = request.getString("visitorTable");
		String visitorTableText = request.getString("visitorTable_text");
		Integer visitorTagTableFlag = request.getInteger("visitorTagTableFlag");
		String visitorTagTable = request.getString("visitorTagTable");
		String visitorTagTableText = request.getString("visitorTagTable_text");

		// 找mp
		Map<String, Object> config = ((Map<String, Object>) ORMService.getInstance().findByKey("WxMp", "appId", appId));
		if (config != null) {
			throw new SystemRuntimeException(ExceptionType.BUSINESS, "公众号[" + appId + "]已存在,无法重复添加");
		}

		try {
			if (accessTokenUrl == null || accessTokenUrl == "") { // 若无自定义接口,
																	// 就用原生的来校验
				Materials.with(new AppSetting(appId, appSecret)).count();// 使用素材接口来检测
			}
		} catch (WxRuntimeException wxRuntimeException) {
			throw new SystemRuntimeException(ExceptionType.BUSINESS, "公众号[" + appId + "]连通失败，请检查AppId和AppSecret, 以及自定义的accessTokenUrl");
		}

		if (visitorTableFlag != null && 1 == visitorTableFlag) {// 自动创建
			TableService service = BeanFactory.getInstance().getBean(TableService.class);
			TbTable table = ModelKeyUtils.buildTable(MpVisitorModelKeys.class, visitorTableText, title + "-访客表");
			service.executeCreateTable(table);
			visitorTable = visitorTableText;
		}

		if (visitorTagTableFlag != null && 1 == visitorTagTableFlag) {// 自动创建
			TableService service = BeanFactory.getInstance().getBean(TableService.class);
			TbTable table = ModelKeyUtils.buildTable(MpVisitorTagModelKeys.class, visitorTagTableText, title + "-访客分组表");
			service.executeCreateTable(table);
			visitorTagTable = visitorTagTableText;
		}

		DataPO po = new DataPO("WxMp");
		po.set("appId", appId);
		po.set("appSecret", appSecret);
		po.set("title", title);
		po.set("description", description);
		po.set("token", token);
		po.set("encodingAESKey", encodingAESKey);
		po.set("status", 0);
		po.set("createUid", SessionManager.getUser().getUid());
		po.set("mpKey", mpKey);
		po.set("visitorTable", visitorTable);
		po.set("visitorTagTable", visitorTagTable);

		if (accessTokenUrl != null && accessTokenUrl != "") { // 若有填入accessTokenUrl
			po.set("accessTokenUrl", accessTokenUrl);
		}

		// 图片logo,暂时用二维码
		try {
			Tickets tickets = Tickets.with(MpAppService.getInstance().getAppSetting(po.toEntity()));
			Ticket ticket = tickets.permanent("dev");// 永久二维码
			po.set("logoUrl", "https://mp.weixin.qq.com/cgi-bin/showqrcode?ticket=" + ticket.getTicket());
		} catch (Throwable e) {
			po.set("logoUrl", "/css/images/logo.png");
		}

		ORMService.getInstance().save(po.toEntity());// 保存
		return po.toEntity();
	}

	/**
	 * 删除
	 *
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public Map<String, Object> executeRemove() {
		RequestContext request = RequestContext.getCurrent();
		String mpKey = request.getString("mpKey");
		Map<String, Object> mp = ((Map<String, Object>) ORMService.getInstance().findByPk("WxMp", mpKey));

		ORMService.getInstance().remove(mp);
		ORMService.getInstance().removeByPk("WxMpLocation", mpKey);
		ORMService.getInstance().removeByPk("WxMpMessage", mpKey);
		ORMService.getInstance().executeHQL("delete from WxMpMenu where mpKey = ?", mpKey);

		return mp;
	}

	/**
	 * 提交修改
	 *
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public Map<String, Object> executeEdit() {
		RequestContext request = RequestContext.getCurrent();
		String mpKey = request.getString("mpKey");

		// 找公众号
		Map<String, Object> config = ((Map<String, Object>) ORMService.getInstance().findByPk("WxMp", mpKey));
		if (config == null) {
			throw new SystemRuntimeException(ExceptionType.BUSINESS, "公众号不存在.");
		}

		// 查找公众号
		DataPO po = new DataPO("WxMp", config);

		// 是否重新设置绑定数据表
		if (StringUtils.equalsIgnoreCase(request.getString("visitorTableBindingFlag"), "true")) {
			po.set("visitorTable", request.getString("visitorTable"));
			po.set("visitorTagTable", request.getString("visitorTagTable"));
		}

		// 是否重新设置绑定数据表
		if (StringUtils.equalsIgnoreCase(request.getString("templateMsgLogTableBindingFlag"), "true")) {
			po.set("templateMsgLogTable", request.getString("templateMsgLogTable"));
		}

		// 看看是否重新验证
		if (StringUtils.equalsIgnoreCase(request.getString("tokenFlag"), "true")) {
			String appId = request.getString("appId");
			String appSecret = request.getString("appSecret");

			try {
				if (request.getString("accessTokenUrl") == null || request.getString("accessTokenUrl") == "") { // 若无自定义的接口,
																												// 就用原生的接口来测试
					Materials.with(new AppSetting(appId, appSecret)).count();// 使用素材接口来检测
				}
			} catch (WxRuntimeException wxRuntimeException) {
				throw new SystemRuntimeException(ExceptionType.BUSINESS, "公众号[" + appId + "]连通失败，请检查AppId和AppSecret.");
			}

			po.set("appSecret", request.getString("appSecret"));
			po.set("token", request.getString("token"));
			po.set("encodingAESKey", request.getString("encodingAESKey"));
			po.set("status", 0);
		}

		// 是否设置accessTokenUrl
		if (!StringUtils.isEmpty(request.getString("accessTokenUrl"))) {
			po.set("accessTokenUrl", request.getString("accessTokenUrl"));
		}

		if (StringUtils.isEmpty(po.getString("logoUrl"))) {
			// 图片logo,暂时用二维码
			Tickets tickets = Tickets.with(MpAppService.getInstance().getAppSetting(po.toEntity()));
			Ticket ticket = tickets.permanent("dev");// 永久二维码
			po.set("logoUrl", "https://mp.weixin.qq.com/cgi-bin/showqrcode?ticket=" + ticket.getTicket());
		}

		// 绑定系统组织架构
		if (true) {
			String groupKey = request.getString("groupKey");
			String roleKey = request.getString("roleKey");
			if (StringUtils.isEmpty(groupKey) && StringUtils.isEmpty(roleKey)) {// 都空表示不设置
				groupKey = null;
				roleKey = null;
			} else if (StringUtils.isEmpty(groupKey)) {// 其中一个空则报错
				throw new SystemRuntimeException(ExceptionType.BUSINESS, "绑定权限时[系统组织]为空.");
			} else if (StringUtils.isEmpty(roleKey)) {
				throw new SystemRuntimeException(ExceptionType.BUSINESS, "绑定权限时[系统角色]为空.");
			} else {// 校验
				if (ORMService.getInstance().findHQL("from UsGroupRole where groupKey = ? and roleKey = ?", groupKey, roleKey) == null) {
					throw new SystemRuntimeException(ExceptionType.BUSINESS, "绑定权限时选择的角色不在组织中.");
				}
				po.set("groupKey", groupKey);
				po.set("roleKey", roleKey);
			}
		}

		ORMService.getInstance().update(po.toEntity());// 保存

		// 支付
		{
			// 看看是否重新绑定
			if (StringUtils.equalsIgnoreCase(request.getString("payFlag"), "true")) {

				DataPO payPO;
				Map<String, Object> mpPay = (Map<String, Object>) ORMService.getInstance().findByPk("WxMpPay", mpKey);
				if (mpPay != null) {
					payPO = new DataPO("WxMpPay", mpPay);
				} else {
					payPO = new DataPO("WxMpPay");
					payPO.set("mpKey", mpKey);
				}

				payPO.set("appId", request.getString("appId"));
				payPO.set("mchId", request.getString("pay.mchId"));
				payPO.set("paySecret", request.getString("pay.paySecret"));
				payPO.set("certPath", request.getString("pay.certPath"));
				payPO.set("certPassword", request.getString("pay.certPassword"));

				ORMService.getInstance().saveOrUpdate(payPO.toEntity());
			}
		}
		// 微信支付通知
		{
			DataPO eventPo;
			Map<String, Object> event = (Map<String, Object>) ORMService.getInstance().findByPk("WxMpPayNotify", mpKey);
			if (event != null) {
				eventPo = new DataPO("WxMpPayNotify", event);
			} else {
				eventPo = new DataPO("WxMpPayNotify");
				eventPo.set("mpKey", mpKey);
			}

			String[] type = request.getStrings("payNotify.type");
			if (ArrayUtils.contains(type, "event")) {
				eventPo.set("eventFlag", 1);
			} else {
				eventPo.set("eventFlag", 0);
			}
			if (ArrayUtils.contains(type, "log")) {
				eventPo.set("logFlag", 1);
			} else {
				eventPo.set("logFlag", 0);
			}

			eventPo.set("description", request.getString("payNotify.description"));
			eventPo.set("commandKey", request.getString("payNotify.commandKey"));
			eventPo.set("paramType", request.getInteger("payNotify.paramType"));
			eventPo.set("paramScript", request.getString("payNotify.paramScript"));
			eventPo.set("logTable", request.getString("payNotify.logTable"));

			ORMService.getInstance().saveOrUpdate(eventPo.toEntity());
		}

		// 用户关注
		{
			DataPO eventPo;
			Map<String, Object> event = (Map<String, Object>) ORMService.getInstance().findByPk("WxMpSubscribe", mpKey);
			if (event != null) {
				eventPo = new DataPO("WxMpSubscribe", event);
			} else {
				eventPo = new DataPO("WxMpSubscribe");
				eventPo.set("mpKey", mpKey);
			}

			String[] type = request.getStrings("subscribe.type");
			if (ArrayUtils.contains(type, "event")) {
				eventPo.set("eventFlag", 1);
			} else {
				eventPo.set("eventFlag", 0);
			}
			eventPo.set("description", request.getString("subscribe.description"));
			eventPo.set("commandKey", request.getString("subscribe.commandKey"));
			eventPo.set("paramType", request.getInteger("subscribe.paramType"));
			eventPo.set("paramScript", request.getString("subscribe.paramScript"));

			ORMService.getInstance().saveOrUpdate(eventPo.toEntity());
		}

		// 用户取消关注
		{
			DataPO eventPo;
			Map<String, Object> event = (Map<String, Object>) ORMService.getInstance().findByPk("WxMpUnSubscribe", mpKey);
			if (event != null) {
				eventPo = new DataPO("WxMpUnSubscribe", event);
			} else {
				eventPo = new DataPO("WxMpUnSubscribe");
				eventPo.set("mpKey", mpKey);
			}

			String[] type = request.getStrings("unsubscribe.type");
			if (ArrayUtils.contains(type, "event")) {
				eventPo.set("eventFlag", 1);
			} else {
				eventPo.set("eventFlag", 0);
			}

			eventPo.set("description", request.getString("unsubscribe.description"));
			eventPo.set("commandKey", request.getString("unsubscribe.commandKey"));
			eventPo.set("paramType", request.getInteger("unsubscribe.paramType"));
			eventPo.set("paramScript", request.getString("unsubscribe.paramScript"));

			ORMService.getInstance().saveOrUpdate(eventPo.toEntity());
		}

		// 用户扫码进入
		{
			DataPO eventPo;
			Map<String, Object> event = (Map<String, Object>) ORMService.getInstance().findByPk("WxMpScanIn", mpKey);
			if (event != null) {
				eventPo = new DataPO("WxMpScanIn", event);
			} else {
				eventPo = new DataPO("WxMpScanIn");
				eventPo.set("mpKey", mpKey);
			}

			String[] type = request.getStrings("scanin.type");
			if (ArrayUtils.contains(type, "event")) {
				eventPo.set("eventFlag", 1);
			} else {
				eventPo.set("eventFlag", 0);
			}

			eventPo.set("description", request.getString("scanin.description"));
			eventPo.set("commandKey", request.getString("scanin.commandKey"));
			eventPo.set("paramType", request.getInteger("scanin.paramType"));
			eventPo.set("paramScript", request.getString("scanin.paramScript"));

			ORMService.getInstance().saveOrUpdate(eventPo.toEntity());
		}

		// 位置上报事件
		{
			DataPO eventPo;
			Map<String, Object> event = (Map<String, Object>) ORMService.getInstance().findByPk("WxMpLocation", mpKey);
			if (event != null) {
				eventPo = new DataPO("WxMpLocation", event);
			} else {
				eventPo = new DataPO("WxMpLocation");
				eventPo.set("mpKey", mpKey);
			}

			String[] type = request.getStrings("location.type");
			if (ArrayUtils.contains(type, "event")) {
				eventPo.set("eventFlag", 1);
			} else {
				eventPo.set("eventFlag", 0);
			}
			if (ArrayUtils.contains(type, "log")) {
				eventPo.set("logFlag", 1);
			} else {
				eventPo.set("logFlag", 0);
			}

			eventPo.set("description", request.getString("location.description"));
			eventPo.set("commandKey", request.getString("location.commandKey"));
			eventPo.set("paramType", request.getInteger("location.paramType"));
			eventPo.set("paramScript", request.getString("location.paramScript"));
			eventPo.set("logTable", request.getString("location.logTable"));

			ORMService.getInstance().saveOrUpdate(eventPo.toEntity());
		}

		// 订单支付通知
		{
			DataPO eventPo;
			Map<String, Object> event = (Map<String, Object>) ORMService.getInstance().findByPk("WxMpOrder", mpKey);
			if (event != null) {
				eventPo = new DataPO("WxMpOrder", event);
			} else {
				eventPo = new DataPO("WxMpOrder");
				eventPo.set("mpKey", mpKey);
			}

			String[] type = request.getStrings("order.type");
			if (ArrayUtils.contains(type, "event")) {
				eventPo.set("eventFlag", 1);
			} else {
				eventPo.set("eventFlag", 0);
			}
			if (ArrayUtils.contains(type, "log")) {
				eventPo.set("logFlag", 1);
			} else {
				eventPo.set("logFlag", 0);
			}

			eventPo.set("description", request.getString("order.description"));
			eventPo.set("commandKey", request.getString("order.commandKey"));
			eventPo.set("paramType", request.getInteger("order.paramType"));
			eventPo.set("paramScript", request.getString("order.paramScript"));
			eventPo.set("logTable", request.getString("order.logTable"));

			ORMService.getInstance().saveOrUpdate(eventPo.toEntity());
		}

		// 对话框
		{
			DataPO eventPo;
			Map<String, Object> event = (Map<String, Object>) ORMService.getInstance().findByPk("WxMpMessage", mpKey);
			if (event != null) {
				eventPo = new DataPO("WxMpMessage", event);
			} else {
				eventPo = new DataPO("WxMpMessage");
				eventPo.set("mpKey", mpKey);
			}

			String[] type = request.getStrings("message.type");
			if (ArrayUtils.contains(type, "event")) {
				eventPo.set("eventFlag", 1);
			} else {
				eventPo.set("eventFlag", 0);
			}
			if (ArrayUtils.contains(type, "log")) {
				eventPo.set("logFlag", 1);
			} else {
				eventPo.set("logFlag", 0);
			}

			eventPo.set("eventType", StringUtils.join(request.getStrings("message.eventType"), ";"));
			eventPo.set("logType", StringUtils.join(request.getStrings("message.logType"), ";"));

			eventPo.set("description", request.getString("message.description"));
			eventPo.set("commandKey", request.getString("message.commandKey"));
			eventPo.set("paramType", request.getInteger("message.paramType"));
			eventPo.set("paramScript", request.getString("message.paramScript"));
			eventPo.set("logTable", request.getString("message.logTable"));

			ORMService.getInstance().saveOrUpdate(eventPo.toEntity());
		}

		// 菜单
		List<HashMap<String, Object>> deleteMenus = request.getJsons("deleteMenu");
		List<HashMap<String, Object>> waitMenus = request.getJsons("waitMenu");
		// 删除
		if (deleteMenus != null) {
			for (HashMap<String, Object> menu : deleteMenus) {
				ORMService.getInstance().removeByPk("WxMpMenu", (Serializable) menu.get("menuKey"));
			}
		}
		if (waitMenus != null) {
			for (HashMap<String, Object> menu : waitMenus) {
				String menuKey = menu.get("menuKey").toString();
				String parentKey = (String) menu.get("parentKey");
				Integer sort = ((Number) menu.get("sort")).intValue();

				DataPO menuPO;
				Map<String, Object> o = (Map<String, Object>) ORMService.getInstance().findByPk("WxMpMenu", menuKey);
				if (o == null) {// 新增
					menuPO = new DataPO("WxMpMenu");
					menuPO.set("menuKey", menuKey);
					menuPO.set("mpKey", mpKey);
				} else {
					menuPO = new DataPO("WxMpMenu", o);
				}
				menuPO.set("parentKey", parentKey);
				menuPO.set("sort", sort);

				if (StringUtils.equalsIgnoreCase(request.getString(menuKey + ".flag"), "true")) {
					Integer menuType = request.getInteger(menuKey + ".menuType");
					if (menuType >= 99) {// 事件
						menuType = request.getInteger(menuKey + ".commandType");
					}

					menuPO.set("menuType", menuType);
					menuPO.set("busiName", request.getString(menuKey + ".busiName"));
					menuPO.set("description", request.getString(menuKey + ".description"));
					menuPO.set("commandKey", request.getString(menuKey + ".commandKey"));
					menuPO.set("paramType", request.getInteger(menuKey + ".paramType"));
					menuPO.set("paramScript", request.getString(menuKey + ".paramScript"));
					String actionStr = "";
					String pagePath = "";
					if (menuType == 1) {
						actionStr = request.getString(menuKey + ".action");
						if (StringUtils.isEmpty(actionStr)) {// 超链接模式
							throw new SystemRuntimeException(ExceptionType.CONFIG, "菜单[" + menuPO.getString("busiName") + "]无选择视图.");
						}
					} else if (menuType == 2) {
						actionStr = request.getString(menuKey + ".defUrl");
						if (StringUtils.isEmpty(actionStr)) {// 自定义网址
							throw new SystemRuntimeException(ExceptionType.CONFIG, "菜单[" + menuPO.getString("busiName") + "]无设置网址.");
						}
					} else if (menuType == 3) {
						pagePath = request.getString(menuKey + ".defPath");
						actionStr = request.getString(menuKey + ".appUrl");
						menuPO.set("pagepath", pagePath);
						if (StringUtils.isEmpty(actionStr)) { // 小程序网址
							throw new SystemRuntimeException(ExceptionType.CONFIG, "菜单[" + menuPO.getString("busiName") + "]无设置网址");
						}
						if (StringUtils.isEmpty(pagePath)) {// 小程序页面
							throw new SystemRuntimeException(ExceptionType.CONFIG, "菜单[" + menuPO.getString("busiName") + "]无设置页面");
						}
					}
					menuPO.set("action", actionStr);
				}

				ORMService.getInstance().saveOrUpdate(menuPO.toEntity());
			}
		}
		// 菜单校验,根据微信要求,一级菜单只允许3个,二级菜单最多5个
		Long menuCount = (Long) ORMService.getInstance().findHQL("select count(1) from WxMpMenu where mpKey = ? and parentKey is null", mpKey);
		if (menuCount > 3) {
			throw new SystemRuntimeException(ExceptionType.CONFIG, "一级菜单不允许超过3个.");
		}
		List<Object[]> subMenuCount = ORMService.getInstance().queryHQL("select parentKey,count(1) as subCount from WxMpMenu where mpKey = ? and parentKey is not null group by parentKey",
				mpKey);
		for (Object[] o : subMenuCount) {
			Long subCount = (Long) o[1];
			if (subCount > 5) {
				throw new SystemRuntimeException(ExceptionType.CONFIG, "每组二级菜单不允许超过5个.");
			}
		}

		return po.toEntity();
	}

	/**
	 * 同步公众号
	 *
	 * @param mpKey
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public boolean executeSync(String mpKey) {
		Map<String, Object> config = ((Map<String, Object>) ORMService.getInstance().findByPk("WxMp", mpKey));
		if (config == null) {
			throw new SystemRuntimeException(ExceptionType.BUSINESS, "公众号[" + mpKey + "]不存在.");
		}

		DataPO po = new DataPO("WxMp", config);

		// 菜单同步
		// 一级菜单
		List<Map<String, Object>> list = ORMService.getInstance().queryHQL("from WxMpMenu where mpKey = ? and parentKey is null order by sort asc", mpKey);
		if (list != null && list.size() > 0) {
			String domain = (StringUtils.equalsIgnoreCase(Config.get("wx.net.https", "false"), "true") ? "https://" : "http://") + Config.get("wx.net.domain") + "/wx/url/";// 网址前缀
			Menu menu = new Menu();
			for (Map<String, Object> o : list) {
				MenuItem item = new MenuItem();
				int menuType = (int) o.get("menuType");
				if (menuType == 1) {
					item.setType(MenuType.view);
					item.setUrl(domain + "mp_" + o.get("menuKey"));
				} else if (menuType == 2) { // 自定义网址
					item.setType(MenuType.view);
					item.setUrl(o.get("action").toString());
				} else if (menuType == 3) { // 小程序页面, 小程序必须有appid, 网址, 还有页面path
					item.setType(MenuType.miniprogram);
					item.setUrl(o.get("action").toString());
					item.setAppId((String)o.get("description"));
					item.setPagePath(o.get("pagepath").toString());
				} else if (menuType > 0) {
					item.setType(MenuType.valueOf(WxMenuCommandType.fromCode(menuType).name()));
				}
				item.setKey((String) o.get("menuKey"));
				item.setName((String) o.get("busiName"));
				menu.add(item);

				// 二级菜单
				List<Map<String, Object>> subList = ORMService.getInstance().queryHQL("from WxMpMenu where mpKey = ? and parentKey = ? order by sort asc", mpKey, (String) o.get("menuKey"));
				if (subList != null) {
					for (Map<String, Object> sub : subList) {
						MenuItem subItem = new MenuItem();
						int subMenuType = (int) sub.get("menuType");
						if (subMenuType == 1) {
							subItem.setType(MenuType.view);
							subItem.setUrl(domain + "mp_" + sub.get("menuKey"));
						} else if (subMenuType == 2) { // 自定义网址
							subItem.setType(MenuType.view);
							subItem.setUrl(sub.get("action").toString());
						} else if (subMenuType == 3) { // 小程序页面, 小程序必须有appid,
														// 网址, 还有页面path
							subItem.setType(MenuType.miniprogram);
							subItem.setUrl(sub.get("action").toString());
							subItem.setAppId(sub.get("description").toString());//暂时用描述
							subItem.setPagePath(sub.get("pagepath").toString());
						} else if (subMenuType > 0) {
							subItem.setType(MenuType.valueOf(WxMenuCommandType.fromCode(subMenuType).name()));
						}
						subItem.setKey((String) sub.get("menuKey"));
						subItem.setName((String) sub.get("busiName"));
						item.add(subItem);
					}
				}
			}

			String appId = (String) config.get("appId");
			String appSecret = (String) config.get("appSecret");

			Menus.with(new AppSetting(appId, appSecret)).create(menu);
		}

		po.set("publicDate", new Date());
		ORMService.getInstance().update(po.toEntity());
		return true;
	}

}
