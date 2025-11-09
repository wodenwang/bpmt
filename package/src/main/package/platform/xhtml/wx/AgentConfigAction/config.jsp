<%@ page language="java" pageEncoding="UTF-8"%>

<%-- 每个模块页面必须引入 --%>
<%@ include file="/include/common.jsp"%>
<%@ include file="/include/html_head.jsp"%>
<script type="text/javascript">
	$(function() {
		var $zone = $('#${_zone}');

		//测试企业号连接
		$('button[name=touch]', $zone).click(function() {
			var corpId = $("[name='wx.qy.corpId']", $zone).val();
			var corpSecret = $("[name='wx.qy.corpSecret']", $zone).val();
			if (corpId == '' || corpSecret == '') {
				Ui.alert('请填写corpId和corpSecret.');
				return;
			}
			Ui.confirm('确认提交微信服务器测试连接?', function() {
				Ajax.post('${_zone}_setting_msg_zone', '${_acp}/touch.shtml', {
					data : {
						corpId : corpId,
						corpSecret : corpSecret
					}
				});
			});
		});

		//测试agent连接
		$('button[name=testAgent]', $zone).click(function() {
			var corpId = $("[name='wx.qy.corpId']", $zone).val();
			var corpSecret = $("[name='wx.qy.corpSecret']", $zone).val();
			if (corpId == '' || corpSecret == '') {
				Ui.alert('请填写corpId和corpSecret.');
				return;
			}
			var agent = $("[name='wx.qy.default']", $zone).val();
			if (agent == '') {
				Ui.alert('请填写应用id.');
				return;
			}
			Ui.confirm('确认提交微信服务器测试连接?', function() {
				Ajax.post('${_zone}_setting_msg_zone', '${_acp}/testAgent.shtml', {
					data : {
						corpId : corpId,
						corpSecret : corpSecret,
						agentId : agent
					}
				});
			});
		});
	});
</script>

<div id="${_zone}_setting_msg_zone" name="msgZone"></div>

<form zone="${_zone}_setting_msg_zone" action="${_acp}/saveConfig.shtml" sync="true">
	<div tabs=true button=left>
		<div title="企业号">
			<table class="ws-table">
				<tr>
					<th>企业号总开关</th>
					<td><wcm:widget name="wx.qy.flag" cmd="radio[YES_NO]" value="${config['wx.qy.flag']}" /></td>
				</tr>
				<tr>
					<th>corpId</th>
					<td><wcm:widget cmd="text[300px]" name="wx.qy.corpId" value="${config['wx.qy.corpId']}" />
						<button icon="weixin.png" type="button" name="touch">测试连接</button></td>
				</tr>
				<tr>
					<th>corpSecret</th>
					<td><wcm:widget cmd="textarea[300px]" name="wx.qy.corpSecret" value="${config['wx.qy.corpSecret']}" /></td>
				</tr>
				<tr>
					<th>通讯录管理模式</th>
					<td><wcm:widget name="wx.qy.contactmode" cmd="radio[@com.riversoft.platform.translate.ContactMode]" value="${config['wx.qy.contactmode']}" /></td>
				</tr>
			</table>
		</div>
		<div title="企业号支付">
			<table class="ws-table">
				<tr>
					<th>企业号支付开关</th>
					<td><wcm:widget name="wx.qy.pay.flag" cmd="radio[YES_NO]" value="${config['wx.qy.pay.flag']}" /></td>
				</tr>
				<tr>
					<th>商户ID</th>
					<td><wcm:widget cmd="text[300px]" name="wx.qy.pay.mchId" value="${config['wx.qy.pay.mchId']}" /></td>
				</tr>
				<tr>
					<th>支付秘钥</th>
					<td><wcm:widget cmd="textarea[300px]" name="wx.qy.pay.key" value="${config['wx.qy.pay.key']}" /></td>
				</tr>
				<tr>
					<th>证书路径</th>
					<td><wcm:widget cmd="text[300px]" name="wx.qy.pay.certPath" value="${config['wx.qy.pay.certPath']}" /></td>
				</tr>
				<tr>
					<th>证书密码</th>
					<td><input type="password" name="wx.qy.pay.certPassword" value="${config['wx.qy.pay.certPassword']}" /></td>
				</tr>
			</table>
		</div>
	</div>
</form>

<%-- 每个模块页面必须引入 --%>
<%@ include file="/include/html_bottom.jsp"%>