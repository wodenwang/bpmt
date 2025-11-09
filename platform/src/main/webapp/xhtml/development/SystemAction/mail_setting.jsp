<%@ page language="java" pageEncoding="UTF-8"%>

<%-- 每个模块页面必须引入 --%>
<%@ include file="/include/common.jsp"%>
<%@ include file="/include/html_head.jsp"%>

<script type="text/javascript">
	$(function() {
		var $zone = $('#${_zone}');

		$('button[name=reload]', $zone).click(function() {
			Ajax.post($zone, '${_acp}/setMail.shtml');
		});
	});
</script>

<form zone="${_zone}_msg" action="${_acp}/submitMailSetting.shtml" option="{confirmMsg:'确认保存?'}">
	<div class="ws-bar">
		<div class="left"></div>
		<div class="right">
			<button type="button" icon="refresh" name="reload">刷新</button>
			<button type="submit" icon="disk">保存</button>
		</div>
	</div>
	<div id="${_zone}_msg"></div>
	<div tabs="true" button="left">
		<div title="基础设置">
			<div accordion="true" multi="true">
				<div title="发送服务器">
					<table class="ws-table">
						<tr>
							<th>协议</th>
							<td><wcm:widget name="mail.sender.protocol" cmd="radio[@com.riversoft.platform.translate.MailSenderProtocol]" value="${config['mail.sender.protocol']}" /></td>
						</tr>
						<tr>
							<th>服务器地址</th>
							<td><wcm:widget name="mail.sender.host" cmd="text" value="${config['mail.sender.host']}" /></td>
						</tr>
						<tr>
							<th>端口<font color="red" tip="true" title="留空则使用协议默认端口.">(提示)</font></th>
							<td><wcm:widget name="mail.sender.port" cmd="text{digits:true}" value="${config['mail.sender.port']}" /></td>
						</tr>
					</table>
				</div>
				<div title="接收服务器">
					<table class="ws-table">
						<tr>
							<th>协议</th>
							<td><wcm:widget name="mail.receiver.protocol" cmd="radio[@com.riversoft.platform.translate.MailReceiverProtocol]" value="${config['mail.receiver.protocol']}" /></td>
						</tr>
						<tr>
							<th>服务器地址</th>
							<td><wcm:widget name="mail.receiver.host" cmd="text" value="${config['mail.receiver.host']}" /></td>
						</tr>
						<tr>
							<th>端口<font color="red" tip="true" title="留空则使用协议默认端口.">(提示)</font></th>
							<td><wcm:widget name="mail.receiver.port" cmd="text{digits:true}" value="${config['mail.receiver.port']}" /></td>
						</tr>
					</table>
				</div>
				<div title="系统账号">
					<table class="ws-table">
						<tr>
							<th>系统账号</th>
							<td><wcm:widget name="mail.sender.account" cmd="text[300px]" value="${config['mail.sender.account']}" /></td>
						</tr>
						<tr>
							<th>密码</th>
							<td><input type="password" style="width: 300px;" name="mail.sender.password" value="" /> <c:if test="${config['mail.sender.password']!=null&&config['mail.sender.password']!=''}">
									<font color="red" tip="true" title="如果密码不需要修改则留空.">(已设值)</font>
								</c:if></td>
						</tr>
					</table>
				</div>
			</div>
		</div>
		<div title="系统通知">
			<div accordion="true" multi="true">
				<div title="功能设置">
					<table class="ws-table">
						<tr>
							<th>通知开关</th>
							<td><wcm:widget name="mail.notify.flag" cmd="radio[YES_NO]" value="${config['mail.notify.flag']}" /></td>
						</tr>
						<tr>
							<th>允许用户修改邮箱</th>
							<td><wcm:widget name="mail.notify.user.setting" cmd="radio[YES_NO]" value="${config['mail.notify.user.setting']}" /></td>
						</tr>
					</table>
				</div>
				<div title="工作流通知模板">
					<table class="ws-table">
						<tr>
							<th>标题(脚本类型)</th>
							<td><wcm:widget name="mail.flow.subject.type" cmd="radio[@com.riversoft.platform.script.ScriptTypes]" value="${config['mail.flow.subject.type']}" /></td>
						</tr>
						<tr>
							<th>标题(脚本)<br /> <font color="red" tip="true" title="vo:实体;fo:流程实体;task:当前任务实体;">(提示)</font></th>
							<td><wcm:widget name="mail.flow.subject.script" cmd="codemirror[groovy]" value="${config['mail.flow.subject.script']}" /></td>
						</tr>
						<tr>
							<th>内容(脚本类型)</th>
							<td><wcm:widget name="mail.flow.content.type" cmd="radio[@com.riversoft.platform.script.ScriptTypes]" value="${config['mail.flow.content.type']}" /></td>
						</tr>
						<tr>
							<th>内容(脚本)<br /> <font color="red" tip="true" title="vo:实体;fo:流程实体;task:当前任务实体;">(提示)</font></th>
							<td><wcm:widget name="mail.flow.content.script" cmd="codemirror[html]" value="${config['mail.flow.content.script']}" /></td>
						</tr>
					</table>
				</div>
			</div>
		</div>
		<div title="外挂邮箱">
			<table class="ws-table">
				<tr>
					<th>账号表</th>
					<td><select name="mail.table.account" class="chosen">
							<option value="">请选择</option>
							<c:forEach items="${accountTables}" var="model">
								<c:choose>
									<c:when test="${model.name==config['mail.table.account']}">
										<option value="${model.name}" selected="selected">[${model.name}]${model.description}</option>
									</c:when>
									<c:otherwise>
										<option value="${model.name}">[${model.name}]${model.description}</option>
									</c:otherwise>
								</c:choose>
							</c:forEach>
					</select></td>
				</tr>
				<tr>
					<th>收件箱表<font color="red" tip="true" title="留空无法使用邮件接收功能.">(提示)</font></th>
					<td><select name="mail.table.inbox" class="chosen">
							<option value="">请选择</option>
							<c:forEach items="${inboxTables}" var="model">
								<c:choose>
									<c:when test="${model.name==config['mail.table.inbox']}">
										<option value="${model.name}" selected="selected">[${model.name}]${model.description}</option>
									</c:when>
									<c:otherwise>
										<option value="${model.name}">[${model.name}]${model.description}</option>
									</c:otherwise>
								</c:choose>
							</c:forEach>
					</select></td>
				</tr>
				<tr>
					<th>发件箱表<font color="red" tip="true" title="留空则无法使用邮件发送功能.">(提示)</font></th>
					<td><select name="mail.table.outbox" class="chosen">
							<option value="">请选择</option>
							<c:forEach items="${outboxTables}" var="model">
								<c:choose>
									<c:when test="${model.name==config['mail.table.outbox']}">
										<option value="${model.name}" selected="selected">[${model.name}]${model.description}</option>
									</c:when>
									<c:otherwise>
										<option value="${model.name}">[${model.name}]${model.description}</option>
									</c:otherwise>
								</c:choose>
							</c:forEach>
					</select></td>
				</tr>
			</table>
		</div>
	</div>
</form>
<%-- 每个模块页面必须引入 --%>
<%@ include file="/include/html_bottom.jsp"%>