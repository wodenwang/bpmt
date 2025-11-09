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

		$('button[name=close]', $zone).click(function() {
			Ui.closeCurrent($zone);
		});

		$('button[name=reAll]', $zone).click(function() {
			Ui.closeCurrent($zone);
			var id = $(this).val();
			Core.fn($zone, 'sendMailForm')(1, id);
		});

		$('button[name=re]', $zone).click(function() {
			Ui.closeCurrent($zone);
			var id = $(this).val();
			Core.fn($zone, 'sendMailForm')(2, id);
		});

		$('button[name=fw]', $zone).click(function() {
			Ui.closeCurrent($zone);
			var id = $(this).val();
			Core.fn($zone, 'sendMailForm')(3, id);
		});

	});
</script>

<div class="ws-bar">
	<div class="left ws-group">
		<button icon="arrowreturnthick-1-w" type="button" name="reAll" value="${vo.ID}">回复全部</button>
		<button icon="arrowreturn-1-w" type="button" name="re" value="${vo.ID}">回复</button>
		<button icon="arrowthick-1-w" type="button" name="fw" value="${vo.ID}">转发</button>
	</div>
	<div class="right">
		<button icon="closethick" type="button" name="close">关闭</button>
	</div>
</div>

<table class="ws-table">
	<tr>
		<th>标题</th>
		<td>${vo.SUBJECT}</td>
	</tr>
	<tr>
		<th>发送时间</th>
		<td><f:formatDate value="${vo.SENT_DATE}" pattern="yyyy-MM-dd HH:mm:ss" /></td>
	</tr>
	<tr>
		<th>发件人</th>
		<td>${vo.FROM_ADDR}</td>
	</tr>
	<c:if test="${vo.CC_ADDRS!=null&&vo.CC_ADDRS!=''}">
		<tr>
			<th>抄送人(CC)</th>
			<td>${vo.CC_ADDRS}</td>
		</tr>
	</c:if>
	<c:if test="${vo.BCC_ADDRS!=null&&vo.BCC_ADDRS!=''}">
		<tr>
			<th>密件送人(CC)</th>
			<td>${vo.BCC_ADDRS}</td>
		</tr>
	</c:if>
	<c:if test="${vo.ATTACHMENT!=null}">
		<tr>
			<th>附件</th>
			<td><wcm:widget name="_tmp" cmd="filemanager" state="readonly" value="${vo.ATTACHMENT}" /></td>
		</tr>
	</c:if>
</table>

<div>
	<wcm:widget name="_content" cmd="editor[100%;400px]" state="readonly" value="${content}" />
</div>

<%-- 每个模块页面必须引入 --%>
<%@ include file="/include/html_bottom.jsp"%>