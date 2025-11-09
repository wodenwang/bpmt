<%@ page language="java" pageEncoding="UTF-8"%>

<%-- 每个模块页面必须引入 --%>
<%@ include file="/include/common.jsp"%>
<%@ include file="/include/html_head.jsp"%>

<script type="text/javascript">
	$(function() {
		var $zone = $('#${_zone}');

		//绑定提交事件
		$("#${_zone}_form").submit(function() {
			var $this = $(this);
			Core.fn($zone, 'submitEdit')($this);
			return false;
		});

		//事件绑定类型
		$("input:checkbox[name$='.type']", $zone).on('ifChanged', function(event) {
			var val = $(this).val();
			var name = $(this).attr("name");
			var flag = $(this).prop("checked");
			if (flag) {
				$("div[name='" + name + "'][type='" + val + "']", $zone).show().prev().show();
			} else {
				$("div[name='" + name + "'][type='" + val + "']", $zone).hide().prev().hide();
			}
		});

		var subscribeEventFlag = "${subscribe.eventFlag}";
		if (subscribeEventFlag != '1') {
			$("input:checkbox[name='subscribe.type'][value='event']").iCheck('uncheck');
		}

		var unsubscribeEventFlag = "${unsubscribe.eventFlag}";
		if (unsubscribeEventFlag != '1') {
			$("input:checkbox[name='unsubscribe.type'][value='event']").iCheck('uncheck');
		}

		//初始化checkbox
		var enterEventFlag = "${enter.eventFlag}";
		if (enterEventFlag != '1') {
			$("input:checkbox[name='enter.type'][value='event']").iCheck('uncheck');
		}

		var enterLogFlag = "${enter.logFlag}";
		if (enterLogFlag != '1') {
			$("input:checkbox[name='enter.type'][value='log']").iCheck('uncheck');
		}

		var locationEventFlag = "${location.eventFlag}";
		if (locationEventFlag != '1') {
			$("input:checkbox[name='location.type'][value='event']").iCheck('uncheck');
		}

		var locationLogFlag = "${location.logFlag}";
		if (locationLogFlag != '1') {
			$("input:checkbox[name='location.type'][value='log']").iCheck('uncheck');
		}

		var messageEventFlag = "${message.eventFlag}";
		if (messageEventFlag != '1') {
			$("input:checkbox[name='message.type'][value='event']").iCheck('uncheck');
		}

		var messageLogFlag = "${message.logFlag}";
		if (messageLogFlag != '1') {
			$("input:checkbox[name='message.type'][value='log']").iCheck('uncheck');
		}

	});
</script>

<div id="${_zone}_msg_zone" name="msgZone"></div>

<table class="ws-table">
	<tr>
		<th>逻辑主键</th>
		<td>${vo.agentKey}</td>
	</tr>
	<tr>
		<th>企业号应用ID</th>
		<td>${vo.agentId}</td>
	</tr>
	<tr>
		<th>应用LOGO</th>
		<td><img style="width: 100px" src="${vo.logoUrl}" /></td>
	</tr>
	<tr>
		<th>名称</th>
		<td>${vo.title}</td>
	</tr>
	<tr>
		<th>描述</th>
		<td>${vo.description}</td>
	</tr>
</table>

<form action="${_acp}/submitEdit.shtml" sync="true" id="${_zone}_form">
	<input type="hidden" name="agentKey" value="${vo.agentKey}" />

	<div tabs="true">

		<div title="菜单设置" init="${_acp}/setMenu.shtml?agentKey=${vo.agentKey}"></div>
		<div title="消息对话框">
			<table class="ws-table">
				<tr>
					<th>处理类型</th>
					<td><input type="checkbox" name="message.type" value="event" checked="checked" /> <label>处理器</label> <input type="checkbox" name="message.type" value="log" checked="checked" /> <label>记录到表</label></td>
				</tr>
				<tr>
					<th>描述</th>
					<td><wcm:widget name="message.description" cmd="textarea" value="${message.description}" /></td>
				</tr>
			</table>
			<div accordion="true" multi="true">
				<div title="处理器绑定" name="message.type" type="event">
					<table class="ws-table">
						<tr>
							<th>处理文件类型</th>
							<td><wcm:widget name="message.eventType" cmd="checkbox[@com.riversoft.platform.translate.WxSendDataType]" value="${message.eventType}" /></td>
						</tr>
						<tr>
							<th>事件处理器</th>
							<td><wcm:widget name="message.commandKey" cmd="wxcommand[qy;MESSAGE]" value="${message.commandKey}" /></td>
						</tr>
						<tr>
							<th>动态入参(脚本类型)</th>
							<td><wcm:widget name="message.paramType" cmd="radio[@com.riversoft.platform.script.ScriptTypes]" value="${message.paramType}" /></td>
						</tr>
						<tr>
							<th>动态入参(脚本)</th>
							<td><wcm:widget name="message.paramScript" cmd="codemirror[groovy]" value="${message.paramScript}" /></td>
						</tr>
					</table>
				</div>
				<div title="数据表绑定" name="message.type" type="log">
					<table class="ws-table">
						<tr>
						<tr>
							<th>处理文件类型</th>
							<td><wcm:widget name="message.logType" cmd="checkbox[@com.riversoft.platform.translate.WxSendDataType]" value="${message.logType}" /></td>
						</tr>
						<tr>
							<th>绑定数据表</th>
							<td><select name="message.logTable" class="chosen needValid">
									<option>请选择</option>
									<c:forEach items="${messageLogTable}" var="model">
										<c:choose>
											<c:when test="${model.name==message.logTable}">
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
		</div>
		<div title="应用事件">
			<div tabs="true" button="left">
				<div title="新用户关注">
					<table class="ws-table">
						<tr>
							<th>处理类型</th>
							<td><input type="checkbox" name="subscribe.type" value="event" checked="checked" /> <label>处理器</label></td>
						</tr>
						<tr>
							<th>描述</th>
							<td><wcm:widget name="subscribe.description" cmd="textarea" value="${subscribe.description}" /></td>
						</tr>
					</table>
					<div accordion="true" multi="true">
						<div title="处理器绑定" name="subscribe.type" type="event">
							<table class="ws-table">
								<tr>
									<th>事件处理器</th>
									<td><wcm:widget name="subscribe.commandKey" cmd="wxcommand[qy;SUBSCRIBE]" value="${subscribe.commandKey}" /></td>
								</tr>
								<tr>
									<th>动态入参(脚本类型)</th>
									<td><wcm:widget name="subscribe.paramType" cmd="radio[@com.riversoft.platform.script.ScriptTypes]" value="${subscribe.paramType}" /></td>
								</tr>
								<tr>
									<th>动态入参(脚本)</th>
									<td><wcm:widget name="subscribe.paramScript" cmd="codemirror[groovy]" value="${subscribe.paramScript}" /></td>
								</tr>
							</table>
						</div>
					</div>
				</div>
				<div title="取消关注">
					<table class="ws-table">
						<tr>
							<th>处理类型</th>
							<td><input type="checkbox" name="unsubscribe.type" value="event" checked="checked" /> <label>处理器</label></td>
						</tr>
						<tr>
							<th>描述</th>
							<td><wcm:widget name="unsubscribe.description" cmd="textarea" value="${unsubscribe.description}" /></td>
						</tr>
					</table>
					<div accordion="true" multi="true">
						<div title="处理器绑定" name="unsubscribe.type" type="event">
							<table class="ws-table">
								<tr>
									<th>事件处理器</th>
									<td><wcm:widget name="unsubscribe.commandKey" cmd="wxcommand[qy;UNSUBSCRIBE]" value="${unsubscribe.commandKey}" /></td>
								</tr>
								<tr>
									<th>动态入参(脚本类型)</th>
									<td><wcm:widget name="unsubscribe.paramType" cmd="radio[@com.riversoft.platform.script.ScriptTypes]" value="${unsubscribe.paramType}" /></td>
								</tr>
								<tr>
									<th>动态入参(脚本)</th>
									<td><wcm:widget name="unsubscribe.paramScript" cmd="codemirror[groovy]" value="${unsubscribe.paramScript}" /></td>
								</tr>
							</table>
						</div>
					</div>
				</div>
				<div title="进入应用事件">
					<table class="ws-table">
						<tr>
							<th>是否开启</th>
							<td><wcm:widget name="reportUserEnter" cmd="radio[YES_NO]" value="${vo.reportUserEnter}" /></td>
						</tr>
						<tr>
							<th>处理类型</th>
							<td><input type="checkbox" name="enter.type" value="event" checked="checked" /> <label>处理器</label> <input type="checkbox" name="enter.type" value="log" checked="checked" /> <label>记录到表</label></td>
						</tr>
						<tr>
							<th>描述</th>
							<td><wcm:widget name="enter.description" cmd="textarea" value="${enter.description}" /></td>
						</tr>
					</table>
					<div accordion="true" multi="true">
						<div title="处理器绑定" name="enter.type" type="event">
							<table class="ws-table">
								<tr>
									<th>事件处理器</th>
									<td><wcm:widget name="enter.commandKey" cmd="wxcommand[qy;ENTER]" value="${enter.commandKey}" /></td>
								</tr>
								<tr>
									<th>动态入参(脚本类型)</th>
									<td><wcm:widget name="enter.paramType" cmd="radio[@com.riversoft.platform.script.ScriptTypes]" value="${enter.paramType}" /></td>
								</tr>
								<tr>
									<th>动态入参(脚本)</th>
									<td><wcm:widget name="enter.paramScript" cmd="codemirror[groovy]" value="${enter.paramScript}" /></td>
								</tr>
							</table>
						</div>
						<div title="数据表绑定" name="enter.type" type="log">
							<table class="ws-table">
								<tr>
									<th>绑定数据表</th>
									<td><select name="enter.logTable" class="chosen needValid ">
											<option>请选择</option>
											<c:forEach items="${enterLogTable}" var="model">
												<c:choose>
													<c:when test="${model.name==enter.logTable}">
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
				</div>
				<div title="地理位置上报">
					<table class="ws-table">
						<tr>
							<th>位置上报模式</th>
							<td><wcm:widget name="reportLocationFlag" cmd="radio[@com.riversoft.platform.translate.WxReportLocationFlag]" value="${vo.reportLocationFlag}" /></td>
						</tr>
						<tr>
							<th>处理类型</th>
							<td><input type="checkbox" name="location.type" value="event" checked="checked" /> <label>处理器</label> <input type="checkbox" name="location.type" value="log" checked="checked" /> <label>记录到表</label></td>
						</tr>
						<tr>
							<th>描述</th>
							<td><wcm:widget name="location.description" cmd="textarea" value="${location.description}" /></td>
						</tr>
					</table>
					<div accordion="true" multi="true">
						<div title="处理器绑定" name="location.type" type="event">
							<table class="ws-table">
								<tr>
									<th>事件处理器</th>
									<td><wcm:widget name="location.commandKey" cmd="wxcommand[qy;LOCATION]" value="${location.commandKey}" /></td>
								</tr>
								<tr>
									<th>动态入参(脚本类型)</th>
									<td><wcm:widget name="location.paramType" cmd="radio[@com.riversoft.platform.script.ScriptTypes]" value="${location.paramType}" /></td>
								</tr>
								<tr>
									<th>动态入参(脚本)</th>
									<td><wcm:widget name="location.paramScript" cmd="codemirror[groovy]" value="${location.paramScript}" /></td>
								</tr>
							</table>
						</div>
						<div title="处理器绑定" name="location.type" type="log">
							<table class="ws-table">
								<tr>
									<th>绑定数据表</th>
									<td><select name="location.logTable" class="chosen needValid ">
											<option>请选择</option>
											<c:forEach items="${locationLogTable}" var="model">
												<c:choose>
													<c:when test="${model.name==location.logTable}">
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
				</div>
			</div>
		</div>
		<div title="开发参数">
			<script type="text/javascript">
				$(function() {
					var $zone = $('#${_zone}');
					$('[name=token]', $zone).prop('disabled', true);
					$('[name=encodingAESKey]', $zone).prop('disabled', true);

					$('[name=tokenFlag]', $zone).on('ifChecked', function(event) {
						$('[name=token]', $zone).prop('disabled', false);
						$('[name=encodingAESKey]', $zone).prop('disabled', false);
						$('[name=agentSecret]', $zone).prop('disabled', false);
					}).on('ifUnchecked', function(event) {
						$('[name=token]', $zone).prop('disabled', true);
						$('[name=encodingAESKey]', $zone).prop('disabled', true);
						$('[name=agentSecret]', $zone).prop('disabled', true);
					});
				})
			</script>
			<table class="ws-table">
				<tr>
					<th>重新设置</th>
					<td><input type="checkbox" name="tokenFlag" value="true" /><label>是</label></td>
				</tr>
				<tr>
					<th>Token</th>
					<td><input name="token" type="text" class="{required:true}" value="${vo.token}" /></td>
				</tr>
				<tr>
					<th>EncodingAESKey</th>
					<td><textarea name="encodingAESKey" class="{required:true}">${vo.encodingAESKey}</textarea></td>
				</tr>
				<tr>
					<th>Secret<font color="red" title="留空则默认使用企业号CorpSecret" tip="true">(提示)</font></th>
					<td><textarea name="agentSecret">${vo.agentSecret}</textarea></td>
				</tr>
			</table>
		</div>

	</div>

	<div class="ws-bar">
		<button type="submit" icon="disk">保存</button>
	</div>
</form>

<%-- 每个模块页面必须引入 --%>
<%@ include file="/include/html_bottom.jsp"%>