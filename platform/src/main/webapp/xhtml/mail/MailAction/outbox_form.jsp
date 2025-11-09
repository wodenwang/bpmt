<%@ page language="java" pageEncoding="UTF-8"%>

<%-- 每个模块页面必须引入 --%>
<%@ include file="/include/common.jsp"%>
<%@ include file="/include/html_head.jsp"%>

<style type="text/css">
.mail_box {
	margin-left: 0px;
}

.mail_box  ul {
	list-style: none;
	margin-left: 0px;
}

.mail_box  li {
	margin-left: 0px;
}
</style>

<script>
	$(function() {
		var $zone = $("#${_zone}");

		//关闭
		$('button[name=close]', $zone).click(function() {
			Ui.confirm('您当前编辑的内容将不会被保存,确认关闭窗口?', function() {
				Ui.closeCurrent($zone);
			});
		});

		//发送
		$('button[name=send]', $zone).click(function() {
			Ui.confirm('是否确认发送邮件?', function() {
				Core.fn($zone, 'submitForm')(1);
			});
		});

		//暂存
		$('button[name=save]', $zone).click(function() {
			Ui.confirm('暂存邮件(此次邮件未发送)?', function() {
				Core.fn($zone, 'submitForm')(0);
			});
		});

		var subject = $('[name=SUBJECT]', $zone).val();
		if (subject != '') {
			Ui.changeCurrentTitle($zone, subject);
		}

	});
</script>

<div class="ws-bar">
	<div class="left ws-group">
		<button icon="mail-open" type="button" name="send">发送</button>
		<button icon="disk" type="button" name="save">暂存</button>
	</div>
	<div class="right">
		<button icon="closethick" type="button" name="close">关闭</button>
	</div>
</div>

<div name="errorZone" id="${_zone}_error_zone"></div>

<form action="${_acp}/submitOutboxForm.shtml" sync="true">
	<input type="hidden" name="id" value="${id}">
	<table class="ws-table">
		<tr>
			<th>标题</th>
			<td><wcm:widget name="SUBJECT" cmd="text[50%]{required:true}" value="${vo.SUBJECT}" /></td>
		</tr>
		<tr>
			<th>收件人<font color="red" tip="true" title="多个地址用分号分隔">(提示)</font></th>
			<td><wcm:widget name="TO_ADDRS" cmd="text[90%]" value="${vo.TO_ADDRS}" /></td>
		</tr>
		<tr>
			<th>抄送<font color="red" tip="true" title="多个地址用分号分隔">(提示)</font></th>
			<td><wcm:widget name="CC_ADDRS" cmd="text[90%]" value="${vo.CC_ADDRS}" /></td>
		</tr>
		<tr>
			<th>密送<font color="red" tip="true" title="多个地址用分号分隔">(提示)</font></th>
			<td><wcm:widget name="BCC_ADDRS" cmd="text[90%]" value="${vo.BCC_ADDRS}" /></td>
		</tr>
		<tr>
			<th>附件</th>
			<td><wcm:widget name="ATTACHMENT" cmd="multifilemanager" value="${vo.ATTACHMENT}" /></td>
		</tr>
	</table>

	<wcm:widget name="CONTENT" cmd="editor[100%;400px]" value="${content}" />
</form>

<%-- 每个模块页面必须引入 --%>
<%@ include file="/include/html_bottom.jsp"%>