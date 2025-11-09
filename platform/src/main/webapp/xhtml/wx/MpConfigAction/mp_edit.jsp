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

		var scaninEventFlag = "${scanin.eventFlag}";
		if (subscribeEventFlag != '1') {
			$("input:checkbox[name='scanin.type'][value='event']").iCheck('uncheck');
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

		var orderEventFlag = "${order.eventFlag}";
		if (orderEventFlag != '1') {
			$("input:checkbox[name='order.type'][value='event']").iCheck('uncheck');
		}

		var orderLogFlag = "${order.logFlag}";
		if (orderLogFlag != '1') {
			$("input:checkbox[name='order.type'][value='log']").iCheck('uncheck');
		}

		var payNotifyEventFlag = "${payNotify.eventFlag}";
		if (payNotifyEventFlag != '1') {
			$("input:checkbox[name='payNotify.type'][value='event']").iCheck('uncheck');
		}

		var payNotifyLogFlag = "${payNotify.logFlag}";
		if (payNotifyLogFlag != '1') {
			$("input:checkbox[name='payNotify.type'][value='log']").iCheck('uncheck');
		}

	});
</script>

<div id="${_zone}_msg_zone" name="msgZone"></div>

<table class="ws-table">
	<tr>
		<th>逻辑主键</th>
		<td>${vo.mpKey}</td>
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
	<input type="hidden" name="mpKey" value="${vo.mpKey}" /> <input type="hidden" name="appId" value="${vo.appId}" />
	<div tabs="true">

		<div title="菜单设置" init="${_acp}/setMenu.shtml?mpKey=${vo.mpKey}"></div>

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
							<td><wcm:widget name="message.commandKey" cmd="wxcommand[mp;MESSAGE]" value="${message.commandKey}" /></td>
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

		<div title="公众号事件">
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
									<td><wcm:widget name="subscribe.commandKey" cmd="wxcommand[mp;SUBSCRIBE]" value="${subscribe.commandKey}" /></td>
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
									<td><wcm:widget name="unsubscribe.commandKey" cmd="wxcommand[mp;UNSUBSCRIBE]" value="${unsubscribe.commandKey}" /></td>
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
				<div title="扫码进入(旧用户)">
					<table class="ws-table">
						<tr>
							<th>处理类型</th>
							<td><input type="checkbox" name="scanin.type" value="event" checked="checked" /> <label>处理器</label></td>
						</tr>
						<tr>
							<th>描述</th>
							<td><wcm:widget name="scanin.description" cmd="textarea" value="${scanin.description}" /></td>
						</tr>
					</table>
					<div accordion="true" multi="true">
						<div title="处理器绑定" name="scanin.type" type="event">
							<table class="ws-table">
								<tr>
									<th>事件处理器</th>
									<td><wcm:widget name="scanin.commandKey" cmd="wxcommand[mp;SCAN]" value="${scanin.commandKey}" /></td>
								</tr>
								<tr>
									<th>动态入参(脚本类型)</th>
									<td><wcm:widget name="scanin.paramType" cmd="radio[@com.riversoft.platform.script.ScriptTypes]" value="${scanin.paramType}" /></td>
								</tr>
								<tr>
									<th>动态入参(脚本)</th>
									<td><wcm:widget name="scanin.paramScript" cmd="codemirror[groovy]" value="${scanin.paramScript}" /></td>
								</tr>
							</table>
						</div>
					</div>
				</div>
				<div title="地理位置上报">
					<table class="ws-table">
						<tr>
							<th>开关</th>
							<td><span style="color: red;">(请登录公众号后台设置,支持"进入公众号上报位置"和"每5秒上报位置"两种模式.)</span></td>
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
									<td><wcm:widget name="location.commandKey" cmd="wxcommand[mp;LOCATION]" value="${location.commandKey}" /></td>
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

		<%-- 人员模型绑定 --%>
		<div title="访客与权限">
			<div tabs="true" button="left">
				<div title="访客模型">
					<script type="text/javascript">
						$(function() {
							var $zone = $('#${_zone}');
							$('[name=visitorTable]', $zone).prop('disabled', true).trigger("liszt:updated");
							$('[name=visitorTagTable]', $zone).prop('disabled', true).trigger("liszt:updated");

							$('[name=visitorTableBindingFlag]', $zone).on('ifChecked', function(event) {
								$('[name=visitorTable]', $zone).prop('disabled', false).trigger("liszt:updated");
								$('[name=visitorTagTable]', $zone).prop('disabled', false).trigger("liszt:updated");
							}).on('ifUnchecked', function(event) {
								$('[name=visitorTable]', $zone).prop('disabled', true).trigger("liszt:updated");
								$('[name=visitorTagTable]', $zone).prop('disabled', true).trigger("liszt:updated");
							});
						})
					</script>
					<table class="ws-table">
						<tr>
							<th>绑定数据表</th>
							<td><input type="checkbox" name="visitorTableBindingFlag" value="true" /><label>重新绑定</label></td>
						</tr>
						<tr>
							<th>绑定人员表</th>
							<td><select name="visitorTable" class="chosen needValid {required:true}">
									<option value="">请选择</option>
									<c:forEach items="${visitorTables}" var="o">
										<c:choose>
											<c:when test="${vo.visitorTable==o.name}">
												<option value="${o.name}" selected="selected">[${o.name}]${o.description}</option>
											</c:when>
											<c:otherwise>
												<option value="${o.name}">[${o.name}]${o.description}</option>
											</c:otherwise>
										</c:choose>
									</c:forEach>
							</select></td>
						</tr>
						<tr>
							<th>绑定人员分组表</th>
							<td><select name="visitorTagTable" class="chosen needValid {required:true}">
									<option value="">请选择</option>
									<c:forEach items="${visitorTagTables}" var="o">
										<c:choose>
											<c:when test="${vo.visitorTagTable==o.name}">
												<option value="${o.name}" selected="selected">[${o.name}]${o.description}</option>
											</c:when>
											<c:otherwise>
												<option value="${o.name}">[${o.name}]${o.description}</option>
											</c:otherwise>
										</c:choose>
									</c:forEach>
							</select></td>
						</tr>
					</table>
				</div>
				<div title="绑定权限">
					<table class="ws-table">
						<tr>
							<th>绑定系统组</th>
							<td><wcm:widget name="groupKey" cmd="group" value="${vo.groupKey}" /></td>
						</tr>
						<tr>
							<th>绑定系统角色</th>
							<td><wcm:widget name="roleKey" cmd="select[$com.riversoft.platform.po.UsRole(请选择);roleKey;busiName]" value="${vo.roleKey}" /></td>
						</tr>
					</table>
				</div>
			</div>
		</div>

		<div title="微信小店设置">
			<div tabs="true" button="left">
				<div title="订单事件">
					<table class="ws-table">
						<tr>
							<th>处理类型</th>
							<td><input type="checkbox" name="order.type" value="event" checked="checked" /> <label>处理器</label> <input type="checkbox" name="order.type" value="log" checked="checked" /> <label>记录到表</label></td>
						</tr>
						<tr>
							<th>描述</th>
							<td><wcm:widget name="order.description" cmd="textarea" value="${order.description}" /></td>
						</tr>
					</table>
					<div accordion="true" multi="true">
						<div title="处理器绑定" name="order.type" type="event">
							<table class="ws-table">
								<tr>
									<th>事件处理器</th>
									<td><wcm:widget name="order.commandKey" cmd="wxcommand[mp;ORDER]" value="${order.commandKey}" /></td>
								</tr>
								<tr>
									<th>动态入参(脚本类型)</th>
									<td><wcm:widget name="order.paramType" cmd="radio[@com.riversoft.platform.script.ScriptTypes]" value="${order.paramType}" /></td>
								</tr>
								<tr>
									<th>动态入参(脚本)</th>
									<td><wcm:widget name="order.paramScript" cmd="codemirror[groovy]" value="${order.paramScript}" /></td>
								</tr>
							</table>
						</div>
						<div title="处理器绑定" name="order.type" type="log">
							<table class="ws-table">
								<tr>
									<th>绑定数据表</th>
									<td><select name="order.logTable" class="chosen needValid ">
											<option>请选择</option>
											<c:forEach items="${orderTables}" var="model">
												<c:choose>
													<c:when test="${model.name==order.logTable}">
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

		<div title="模板消息设置">
			<script type="text/javascript">
				$(function() {
					var $zone = $('#${_zone}');
					$('[name=templateMsgLogTable]', $zone).prop('disabled', true).trigger("liszt:updated");

					$('[name=templateMsgLogTableBindingFlag]', $zone).on('ifChecked', function(event) {
						$('[name=templateMsgLogTable]', $zone).prop('disabled', false).trigger("liszt:updated");
					}).on('ifUnchecked', function(event) {
						$('[name=templateMsgLogTable]', $zone).prop('disabled', true).trigger("liszt:updated");
					});
				})
			</script>
			<table class="ws-table">
				<tr>
					<th>绑定数据表</th>
					<td><input type="checkbox" name="templateMsgLogTableBindingFlag" value="true" /><label>重新绑定</label></td>
				</tr>
				<tr>
					<th>绑定消息发送日志表</th>
					<td><select name="templateMsgLogTable" class="chosen needValid {required:true}">
							<option value="">请选择</option>
							<c:forEach items="${templateMsgLogTables}" var="o">
								<c:choose>
									<c:when test="${vo.templateMsgLogTable==o.name}">
										<option value="${o.name}" selected="selected">[${o.name}]${o.description}</option>
									</c:when>
									<c:otherwise>
										<option value="${o.name}">[${o.name}]${o.description}</option>
									</c:otherwise>
								</c:choose>
							</c:forEach>
					</select></td>
				</tr>
			</table>
		</div>

		<%-- 设置微信支付相关 --%>
		<div title="微信支付">
			<div tabs="true" button="left">
				<div title="基础设置">
					<script type="text/javascript">
						$(function() {
							var $zone = $('#${_zone}');
							$('[name="pay.mchId"]', $zone).prop('disabled', true);
							$('[name="pay.paySecret"]', $zone).prop('disabled', true);
							$('[name="pay.certPath"]', $zone).prop('disabled', true);
							$('[name="pay.certPassword"]', $zone).prop('disabled', true);

							$('[name=payFlag]', $zone).on('ifChecked', function(event) {
								$('[name="pay.mchId"]', $zone).prop('disabled', false);
								$('[name="pay.paySecret"]', $zone).prop('disabled', false);
								$('[name="pay.certPath"]', $zone).prop('disabled', false);
								$('[name="pay.certPassword"]', $zone).prop('disabled', false);
							}).on('ifUnchecked', function(event) {
								$('[name="pay.mchId"]', $zone).prop('disabled', true);
								$('[name="pay.paySecret"]', $zone).prop('disabled', true);
								$('[name="pay.certPath"]', $zone).prop('disabled', true);
								$('[name="pay.certPassword"]', $zone).prop('disabled', true);
							});
						})
					</script>

					<table class="ws-table">
						<tr>
							<th>参数设置</th>
							<td><input type="checkbox" name="payFlag" value="true" /><label>重新填写</label></td>
						</tr>
						<tr>
							<th>AppID</th>
							<td><c:choose>
								<c:when test="${pay!=null}">${pay.appId}</c:when>
								<c:otherwise>
									<font color="red">(未启用微信支付)</font>
								</c:otherwise>
							</c:choose></td>
						</tr>
						<tr>
							<th>商户ID</th>
							<td><input name="pay.mchId" type="text" value="${pay.mchId}" class="{required:true}" /></td>
						</tr>
						<tr>
							<th>支付秘钥</th>
							<td><textarea name="pay.paySecret" class="{required:true}">${pay.paySecret}</textarea></td>
						</tr>
						<tr>
							<th>证书路径</th>
							<td><input name="pay.certPath" type="text" value="${pay.certPath}" class="{required:true}" /></td>
						</tr>
						<tr>
							<th>证书密码</th>
							<td><input name="pay.certPassword" type="password" value="${pay.certPassword}" class="{required:true}" /></td>
						</tr>
					</table>
				</div>
				<div title="支付通知事件">
					<table class="ws-table">
						<tr>
							<th>处理类型</th>
							<td><input type="checkbox" name="payNotify.type" value="event" checked="checked" /> <label>处理器</label> <input type="checkbox" name="payNotify.type" value="log" checked="checked" /> <label>记录到表</label></td>
						</tr>
						<tr>
							<th>描述</th>
							<td><wcm:widget name="payNotify.description" cmd="textarea" value="${payNotify.description}" /></td>
						</tr>
					</table>
					<div accordion="true" multi="true">
						<div title="处理器绑定" name="payNotify.type" type="event">
							<table class="ws-table">
								<tr>
									<th>事件处理器</th>
									<td><wcm:widget name="payNotify.commandKey" cmd="wxcommand[mp;PAYNOTIFY]" value="${payNotify.commandKey}" /></td>
								</tr>
								<tr>
									<th>动态入参(脚本类型)</th>
									<td><wcm:widget name="payNotify.paramType" cmd="radio[@com.riversoft.platform.script.ScriptTypes]" value="${payNotify.paramType}" /></td>
								</tr>
								<tr>
									<th>动态入参(脚本)</th>
									<td><wcm:widget name="payNotify.paramScript" cmd="codemirror[groovy]" value="${payNotify.paramScript}" /></td>
								</tr>
							</table>
						</div>
						<div title="处理器绑定" name="payNotify.type" type="log">
							<table class="ws-table">
								<tr>
									<th>绑定数据表</th>
									<td><select name="payNotify.logTable" class="chosen needValid ">
										<option>请选择</option>
										<c:forEach items="${payResultTables}" var="model">
											<c:choose>
												<c:when test="${model.name==payNotify.logTable}">
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

		<%-- 开发连接类的所有设置.包括appid,appsecrect,token,aeskey等 --%>
		<div title="链接参数">
			<script type="text/javascript">
				$(function() {
					var $zone = $('#${_zone}');
					$('[name=token]', $zone).prop('disabled', true);
					$('[name=appSecret]', $zone).prop('disabled', true);
					$('[name=encodingAESKey]', $zone).prop('disabled', true);

					$('[name=tokenFlag]', $zone).on('ifChecked', function(event) {
						$('[name=token]', $zone).prop('disabled', false);
						$('[name=appSecret]', $zone).prop('disabled', false);
						$('[name=encodingAESKey]', $zone).prop('disabled', false);
					}).on('ifUnchecked', function(event) {
						$('[name=token]', $zone).prop('disabled', true);
						$('[name=appSecret]', $zone).prop('disabled', true);
						$('[name=encodingAESKey]', $zone).prop('disabled', true);
					});
				})
			</script>
			<table class="ws-table">
				<tr>
					<th>回调验证</th>
					<td><input type="checkbox" name="tokenFlag" value="true" /><label>重新验证</label></td>
				</tr>
				<tr>
					<th>AppID</th>
					<td>${vo.appId}</td>
				</tr>
				<tr>
					<th>AppSecret</th>
					<td><textarea name="appSecret" class="{required:true}">${vo.appSecret}</textarea></td>
				</tr>
				<tr>
					<th>Token</th>
					<td><input name="token" type="text" class="{required:true}" value="${vo.token}" /></td>
				</tr>
				<tr>
					<th>EncodingAESKey</th>
					<td><textarea name="encodingAESKey" class="{required:true}">${vo.encodingAESKey}</textarea></td>
				</tr>
			</table>
		</div>
		
		<%-- 用于自定义accessToken获取的url --%>
		<div title="高级">
		    <table class="ws-table">
		        <tr>
				    <th>AccessTokenUrl<font color="red" tip="true" title="用于自定义微信accessToken的获取地址,若留空为自动去微信官方处获取">(提示)</font></th>
				    <td><textarea name="accessTokenUrl" >${vo.accessTokenUrl}</textarea></td>
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