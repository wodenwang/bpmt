<%@ page language="java" pageEncoding="UTF-8"%>

<%-- 每个模块页面必须引入 --%>
<%@ include file="/include/common.jsp"%>
<%@ include file="/include/html_head.jsp"%>

<script type="text/javascript">
	$(function() {
		var $zone = $("#${_zone}");
		$('form', $zone).submit(function(event) {
			event.preventDefault();
			var $form = $(this);
			Ajax.form('${_zone}_msg', $form, {
				confirmMsg : '更新密码?',
				callback : function(flag) {
					if (flag) {
						$('input[name=password]', $form).val('');//清空密码
					}
				}
			});
		});
	});
</script>
<div id="${_zone}_msg"></div>
<form action="${_acp}/submitSetting.shtml" sync="true">
	<table class="ws-table">
		<tr>
			<th>用户ID</th>
			<td>${account.USER_ID}</td>
		</tr>
		<tr>
			<th>邮箱地址</th>
			<td>${account.MAIL_NAME}</td>
		</tr>
		<tr>
			<th>邮箱账号</th>
			<td>${account.MAIL_ACCOUNT}</td>
		</tr>
		<tr>
			<th>邮箱密码</th>
			<td><input name="password" type="password" class="{required:true}" /></td>
		</tr>
		<tr>
			<th>校验账号<font color="red" tip="true" title="发送一封测试邮件以校验邮箱账号是否正确.">(提示)</font></th>
			<td><wcm:widget name="testFlag" cmd="radio[YES_NO]" /></td>
		</tr>
		<tr>
			<th class="ws-bar">
				<button type="submit" icon="disk">保存设置</button>
			</th>
		</tr>
	</table>

</form>

<%-- 每个模块页面必须引入 --%>
<%@ include file="/include/html_bottom.jsp"%>