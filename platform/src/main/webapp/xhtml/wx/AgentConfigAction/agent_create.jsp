<%@ page language="java" pageEncoding="UTF-8"%>

<%-- 每个模块页面必须引入 --%>
<%@ include file="/include/common.jsp"%>
<%@ include file="/include/html_head.jsp"%>

<script type="text/javascript">
	$(function() {
		var $zone = $('#${_zone}');
		var $submit = $('button:submit', $zone);
		var $token = $('[name=token]', $zone);
		var $encodingAESKey = $('[name=encodingAESKey]', $zone);

		//校验
		$('button[name=checkAgent]', $zone).click(function() {
			var $button = $(this);
			var $input = $('input[name=testAgent]', $zone);
			var $secret = $('textarea[name=agentSecret]', $zone);

			if ($input.val() == '' || !$.isNumeric($input.val())) {
				Ui.alert('请正确填写[企业号应用ID].');
				return false;
			}

			if ($secret.val() == '') {
				Ui.alert('请填写应用的Secret.');
				return false;
			}

			Ajax.json('${_acp}/checkCreate.shtml', function(result) {
				if (result.flag) {
					var agent = result.agent;
					$('input:hidden[name=agentId]', $zone).val(agent.agentid);
					$('[name=description]', $zone).val(agent.description);
					$('[name=title]', $zone).val(agent.name);
					var $img = $('<img style="width:100px;"/>');
					$img.attr('src', agent.square_logo_url);
					$("#${_zone}_logo_zone").html('');
					$("#${_zone}_logo_zone").append($img);

					if (agent.close == 0) {//企业号可用
						$input.prop('disabled', true);
						$secret.prop('readonly', true);
						$button.button("option", "disabled", true);
						$submit.button("option", "disabled", false);
						$token.prop("disabled", false);
						$encodingAESKey.prop("disabled", false);
					} else {
						Ui.alert('应用[' + agent.agentId + ']被禁用,请先联系企业号管理员打开.');
					}
				}
			}, {
				errorZone : '${_zone}_msg_zone',
				data : {
					agentId : $input.val(),
					agentSecret : $secret.val()
				}
			});
		});

		//绑定提交事件
		$("#${_zone}_form").submit(function() {
			var $this = $(this);
			Core.fn($zone, 'submitCreate')($this);
			return false;
		});

		//初始化表单
		$submit.button("option", "disabled", true);
		$token.prop("disabled", true);
		$encodingAESKey.prop("disabled", true);
	});
</script>

<div id="${_zone}_msg_zone" name="msgZone"></div>

<form action="${_acp}/submitCreate.shtml" sync="true" id="${_zone}_form">
	<input type="hidden" name="agentId" value="" />
	<div accordion="true" multi="true">
		<div title="基础信息">
			<table class="ws-table">
				<tr>
					<th>逻辑主键</th>
					<td><wcm:widget name="agentKey" cmd="key{required:true}" /></td>
				</tr>
				<tr>
					<th>企业号应用ID</th>
					<td><input name="testAgent" type="text" />
						<button type="button" name="checkAgent" icon="weixin.png">验证</button></td>
				</tr>
				<tr>
					<th>Secret</th>
					<td><textarea name="agentSecret" class="{required:true}"></textarea></td>
				</tr>
				<tr>
					<th>应用LOGO</th>
					<td>
						<div id="${_zone}_logo_zone">(请先输入应用ID并验证)</div>
					</td>
				</tr>
				<tr>
					<th>名称</th>
					<td><input name="title" type="text" disabled="disabled" /></td>
				</tr>
				<tr>
					<th>描述</th>
					<td><textarea name="description" disabled="disabled"></textarea></td>
				</tr>
			</table>
		</div>
		<div title="开发参数">
			<table class="ws-table">
				<tr>
					<th>Token</th>
					<td><input name="token" type="text" class="{required:true}" /></td>
				</tr>
				<tr>
					<th>EncodingAESKey</th>
					<td><textarea name="encodingAESKey" class="{required:true}"></textarea></td>
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