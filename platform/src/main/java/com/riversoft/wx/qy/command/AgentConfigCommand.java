package com.riversoft.wx.qy.command;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.riversoft.core.Config;
import com.riversoft.core.db.JdbcService;
import com.riversoft.core.db.ORMService;
import com.riversoft.core.exception.ExceptionType;
import com.riversoft.core.exception.SystemRuntimeException;
import com.riversoft.platform.script.ScriptHelper;
import com.riversoft.platform.script.ScriptTypes;
import com.riversoft.platform.translate.WxSendDataType;
import com.riversoft.util.jackson.JsonMapper;
import com.riversoft.wx.annotation.WxAnnotatedCommandsHolder;
import com.riversoft.wx.command.CommandExecutionService;
import com.riversoft.wx.context.Location;
import com.riversoft.wx.qy.AgentHelper;
import com.riversoft.wx.qy.QyMediaHelper;
import com.riversoft.wx.qy.model.EnterAgentLogModelKeys;
import com.riversoft.wx.qy.model.LocationReportLogModelKeys;
import com.riversoft.wx.qy.model.MessageLogModelKeys;

/**
 * 企业号自定义处理器<br>
 * Created by exizhai on 10/24/2015.
 */
public class AgentConfigCommand implements QyCommand {

	private static Logger logger = LoggerFactory.getLogger(AgentConfigCommand.class);

	@SuppressWarnings("unchecked")
	@Override
	public QyResponse execute(QyRequest request) {

		Map<String, Object> config = null;

		if (request.isMenu()) {// 菜单
			if (StringUtils.isEmpty(request.getEventKey())) {
				return null;
			}
			config = (Map<String, Object>) ORMService.getInstance().findByPk("WxAgentMenu", request.getEventKey());
		} else if (request.isMessage()) {// 对话框
			config = (Map<String, Object>) ORMService.getInstance().findByPk("WxAgentMessage", request.getAgentKey());
		} else if (request.isEnterAgent()) {// 进入事件
			config = (Map<String, Object>) ORMService.getInstance().findByPk("WxAgentEnter", request.getAgentKey());
		} else if (request.isLocationEvent()) {// 上报位置
			config = (Map<String, Object>) ORMService.getInstance().findByPk("WxAgentLocation", request.getAgentKey());
		} else if (request.isSubscribe()) {
			config = (Map<String, Object>) ORMService.getInstance().findByPk("WxAgentSubscribe", request.getAgentKey());
		} else if (request.isUnSubscribe()) {
			config = (Map<String, Object>) ORMService.getInstance().findByPk("WxAgentUnSubscribe", request.getAgentKey());
		}

		if (config == null) {
			return null;
		}

		boolean eventFlag;
		boolean logFlag;
		if (request.isMenu()) {// 菜单不登记表,但必须处理事件
			eventFlag = true;
			logFlag = false;
		} else {
			eventFlag = (Integer) config.get("eventFlag") == 1;
			if (config.containsKey("logFlag")) {
				logFlag = (Integer) config.get("logFlag") == 1;
			} else {
				logFlag = false;
			}
		}

		// 登记表
		logStep: if (logFlag) {
			if (request.isMessage()) { // 消息模式下特殊有多一层类型拦截,登记日志
				String[] logType = StringUtils.split((String) config.get("logType"), ";");
				if (logType == null || logType.length < 1) {// 没有勾选拦截类型,不拦截
					break logStep;
				}
				if (request.getText() != null && !ArrayUtils.contains(logType, WxSendDataType.TEXT.name())) {
					break logStep;
				} else if (request.getImage() != null && !ArrayUtils.contains(logType, WxSendDataType.IMAGE.name())) {
					break logStep;
				} else if (request.getShortVideo() != null && !ArrayUtils.contains(logType, WxSendDataType.SHORT_VIDEO.name())) {
					break logStep;
				} else if (request.getVideo() != null && !ArrayUtils.contains(logType, WxSendDataType.VIDEO.name())) {
					break logStep;
				} else if (request.getVoice() != null && !ArrayUtils.contains(logType, WxSendDataType.VOICE.name())) {
					break logStep;
				} else if (request.getLocation() != null && !ArrayUtils.contains(logType, WxSendDataType.LOCATION.name())) {
					break logStep;
				}

				String tableName = (String) config.get("logTable");
				if (StringUtils.isNotEmpty(tableName)) {
					logMessage(tableName, request);
				}
			} else if (request.isEnterAgent()) { // 用户进入事件,登记日志
				String tableName = (String) config.get("logTable");
				if (StringUtils.isNotEmpty(tableName)) {
					logEnter(tableName, request);
				}
			} else if (request.isLocationEvent()) {// 位置上报事件登记日志
				String tableName = (String) config.get("logTable");
				if (StringUtils.isNotEmpty(tableName)) {
					logLocationReport(tableName, request);
				}
			}
		}

		// 处理事件
		eventStep: if (eventFlag) {

			// 消息模式下特殊有多一层类型拦截
			if (request.isMessage()) {
				String[] eventType = StringUtils.split((String) config.get("eventType"), ";");
				if (eventType == null || eventType.length < 1) {// 没有勾选拦截类型,不拦截
					break eventStep;
				}
				if (request.getText() != null && !ArrayUtils.contains(eventType, WxSendDataType.TEXT.name())) {
					break eventStep;
				} else if (request.getImage() != null && !ArrayUtils.contains(eventType, WxSendDataType.IMAGE.name())) {
					break eventStep;
				} else if (request.getShortVideo() != null && !ArrayUtils.contains(eventType, WxSendDataType.SHORT_VIDEO.name())) {
					break eventStep;
				} else if (request.getVideo() != null && !ArrayUtils.contains(eventType, WxSendDataType.VIDEO.name())) {
					break eventStep;
				} else if (request.getVoice() != null && !ArrayUtils.contains(eventType, WxSendDataType.VOICE.name())) {
					break eventStep;
				} else if (request.getLocation() != null && !ArrayUtils.contains(eventType, WxSendDataType.LOCATION.name())) {
					break eventStep;
				}
			}

			String commandKey = (String) config.get("commandKey");
			Integer paramType = (Integer) config.get("paramType");
			String paramScript = (String) config.get("paramScript");
			Map<String, Object> params = null;
			if (StringUtils.isNotEmpty(paramScript)) {
				Object o = ScriptHelper.evel(ScriptTypes.forCode(paramType), paramScript);
				if (o != null) {
					if (o instanceof Map) {
						params = (Map<String, Object>) o;
					} else {
						params = JsonMapper.defaultMapper().json2Map(o.toString());
					}
				}
			}

			if (StringUtils.isEmpty(commandKey)) {
				return null;
			}

			// 内置command
			if (isSystemCommand(commandKey)) {
				logger.info("系统内置command:{}", commandKey);
				QyCommand systemCommand = WxAnnotatedCommandsHolder.getInstance().getQyCommandInstanceByClassName(commandKey);

				if (params != null) {
					request.getAttrs().putAll(params);
				}

				return systemCommand.execute(request);
			} else {
				//
				logger.info("系统内置command:{}", commandKey);
				Map<String, Object> commandConfig = (Map<String, Object>) ORMService.getInstance().findByPk("WxCommand", commandKey);

				Map<String, Object> context = new HashMap<>();

				Map<String, Object> agentConfig = ((Map<String, Object>) ORMService.getInstance().findByKey("WxAgent", "agentId", request.getAgentId()));
				if (agentConfig == null) {
					throw new SystemRuntimeException(ExceptionType.WX, "agent id 不存在.");
				}
				String corpId = Config.get("wx.qy.corpId");
				String secret = StringUtils.isNotEmpty((String) agentConfig.get("agentSecret")) ? (String) agentConfig.get("agentSecret") : Config.get("wx.qy.corpSecret");
				context.put("agent", new AgentHelper(corpId, secret, request.getAgentId()));
				context.put("media", QyMediaHelper.getInstance());// 默认只有一个企业号
				context.put("wo", request);
				if (params != null) {
					context.putAll(params);
				}

				Integer logicType = (Integer) commandConfig.get("logicType");
				String logicScript = (String) commandConfig.get("logicScript");
				Object o = CommandExecutionService.getInstance().executeCommand(ScriptTypes.forCode(logicType), logicScript, context);

				Map<String, String> map = null;
				if (o != null) {
					if (o instanceof Map) {
						map = (Map<String, String>) o;
					} else if (o instanceof String) {
						map = new HashMap<>();
						map.put("text", (String) o);
					}
				}

				if (map != null) {
					QyResponse qyResponse = new QyResponse(request.getAgentId(), map).setUser(request.getUid());
					return qyResponse;
				}
			}
		}

		return null;
	}

	private boolean isSystemCommand(String commandKey) {
		return commandKey.startsWith("com.riversoft");
	}

	/**
	 * 登记对话框消息
	 *
	 * @param tableName
	 * @param request
	 */
	private void logMessage(String tableName, QyRequest request) {
		StringBuffer sql = new StringBuffer();
		sql.append("insert into ").append(tableName).append(" (");
		sql.append(MessageLogModelKeys.AGENT_KEY.name()).append(",");
		sql.append(MessageLogModelKeys.AGENT_ID.name()).append(",");
		sql.append(MessageLogModelKeys.USER_ID.name()).append(",");
		sql.append(MessageLogModelKeys.LOG_DATE.name()).append(",");

		sql.append(MessageLogModelKeys.MSG_TYPE.name()).append(",");
		sql.append(MessageLogModelKeys.MSG_TEXT.name()).append(",");
		sql.append(MessageLogModelKeys.MSG_ATTACHMENT.name()).append(",");
		sql.append(MessageLogModelKeys.MSG_X.name()).append(",");
		sql.append(MessageLogModelKeys.MSG_Y.name()).append(",");
		sql.append(MessageLogModelKeys.MSG_SCALE.name()).append(",");
		sql.append(MessageLogModelKeys.MSG_LABEL.name());

		sql.append(") values (?,?,?,?,?,?,?,?,?,?,?)");

		String type;
		String text = null;
		byte[] attachment = null;
		String x = null;
		String y = null;
		String scale = null;
		String label = null;

		if (request.getText() != null) {
			type = WxSendDataType.TEXT.name();
			text = request.getText();
		} else if (request.getImage() != null) {
			type = WxSendDataType.IMAGE.name();
			text = request.getImage().getMediaId();
			attachment = QyMediaHelper.download(text);
		} else if (request.getVoice() != null) {
			type = WxSendDataType.VOICE.name();
			text = request.getVoice().getMediaId();
			attachment = QyMediaHelper.download(text);
		} else if (request.getShortVideo() != null) {
			type = WxSendDataType.SHORT_VIDEO.name();
			text = request.getShortVideo().getMediaId();
			attachment = QyMediaHelper.download(text);
		} else if (request.getVideo() != null) {
			type = WxSendDataType.VIDEO.name();
			text = request.getVideo().getMediaId();
			attachment = QyMediaHelper.download(text);
		} else if (request.getLocation() != null) {
			type = WxSendDataType.LOCATION.name();
			x = request.getLocation().getX();
			y = request.getLocation().getY();
			scale = request.getLocation().getScale();
			label = request.getLocation().getLabel();
		} else {
			return;
		}

		JdbcService.getInstance().executeSQL(sql.toString(), request.getAgentKey(), request.getAgentId(), request.getUid(), new Date(), type, text, attachment, x, y, scale, label);
	}

	/**
	 * 登记地理位置上报
	 *
	 * @param tableName
	 * @param request
	 */
	private void logLocationReport(String tableName, QyRequest request) {
		Location location = request.getLocation();
		if (location == null) {
			return;
		}

		StringBuffer sql = new StringBuffer();
		sql.append("insert into ").append(tableName).append(" (");
		sql.append(LocationReportLogModelKeys.AGENT_KEY.name()).append(",");
		sql.append(LocationReportLogModelKeys.AGENT_ID.name()).append(",");
		sql.append(LocationReportLogModelKeys.USER_ID.name()).append(",");
		sql.append(LocationReportLogModelKeys.LOG_DATE.name()).append(",");
		sql.append(LocationReportLogModelKeys.X.name()).append(",");
		sql.append(LocationReportLogModelKeys.Y.name()).append(",");
		sql.append(LocationReportLogModelKeys.SCALE.name()).append(",");
		sql.append(LocationReportLogModelKeys.LABEL.name());
		sql.append(") values (?,?,?,?,?,?,?,?)");
		JdbcService.getInstance().executeSQL(sql.toString(), request.getAgentKey(), request.getAgentId(), request.getUid(), new Date(), location.getX(), location.getY(), location.getScale(),
				location.getLabel());

	}

	/**
	 * 登记应用进入事件
	 *
	 * @param tableName
	 * @param request
	 */
	private void logEnter(String tableName, QyRequest request) {
		StringBuffer sql = new StringBuffer();
		sql.append("insert into ").append(tableName).append(" (");
		sql.append(EnterAgentLogModelKeys.AGENT_KEY.name()).append(",");
		sql.append(EnterAgentLogModelKeys.AGENT_ID.name()).append(",");
		sql.append(EnterAgentLogModelKeys.USER_ID.name()).append(",");
		sql.append(EnterAgentLogModelKeys.LOG_DATE.name());
		sql.append(") values (?,?,?,?)");
		JdbcService.getInstance().executeSQL(sql.toString(), request.getAgentKey(), request.getAgentId(), request.getUid(), new Date());
	}
}
