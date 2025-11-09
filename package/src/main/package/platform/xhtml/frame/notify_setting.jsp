<%@ page language="java" pageEncoding="UTF-8"%>

<%-- 每个模块页面必须引入 --%>
<%@ include file="/include/common.jsp"%>
<%@ include file="/include/html_head.jsp"%>

<%--错误提示区域 --%>
<div id="${_zone}_error"></div>

<%--表单 --%>
<form action="${_acp}/submitNotifySetting.shtml" method="post" id="${_zone}_form" zone="${_zone}_error" option="{errorZone:'${_zone}_error',confirmMsg:'确认提交？'}">
	<table class="ws-table">
		<div>
			<tr>
				<th>${wpf:lan("#:zh[接收通知方式]:en[Receive notifications way]#")}</th>
				<td><wcm:widget name="msgType" cmd="checkbox[@com.riversoft.platform.translate.NotifyMsgType]" value="${user.msgType}" /></td>
			</tr>
			<tr>
				<th>${wpf:lan("#:zh[接收范围]:en[Receive range]#")}</th>
				<td><wcm:widget name="receiveType" cmd="checkbox[@com.riversoft.platform.translate.NotifyReceiveType]" value="${user.receiveType}" /></td>
			</tr>
			<tr>
				<th>${wpf:lan("#:zh[绑定邮箱]:en[Binding email]#")}</th>
				<td><wcm:widget name="mail" cmd="text{email:true}" value="${user.mail}" state="${allowSetting?'normal':'readonly'}" /></td>
			</tr>
		</div>
		<tr>
			<th class="ws-bar ">
				<div class="ws-group">
					<button type="reset" icon="arrowreturnthick-1-w" text="true">${wpf:lan("#:zh[重置]:en[Reset]#")}</button>
					<button type="submit" icon="check" text="true">${wpf:lan("#:zh[提交]:en[Submit]#")}</button>
				</div>
			</th>
		</tr>
	</table>
</form>

<%-- 每个模块页面必须引入 --%>
<%@ include file="/include/html_bottom.jsp"%>