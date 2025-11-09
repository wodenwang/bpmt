<%@ page language="java" pageEncoding="UTF-8"%>

<%-- 每个模块页面必须引入 --%>
<%@ include file="/include/common.jsp"%>
<%@ include file="/include/html_head.jsp"%>

<%--错误提示区域 --%>
<div id="${_zone}_error"></div>

<%--表单 --%>
<form action="${_acp}/submitChangePwd.shtml" method="post" id="${_zone}_form" zone="${_zone}_error" option="{errorZone:'${_zone}_error',confirmMsg:'${wpf:lan("#:zh[确认提交？]:en[Confirm to submit?]#")}'}">
	<table class="ws-table">
		<div>
			<tr>
				<th>${wpf:lan("#:zh[当前密码]:en[Current password]#")}</th>
				<td><input type="password" name="oldPassword" class="{required:true}" /></td>
			</tr>
			<tr>
				<th>${wpf:lan("#:zh[新密码]:en[New password]#")}</th>
				<td><input type="password" name="newPassword" class="{required:true, maxlength:20}" /></td>
			</tr>
			<tr>
				<th>${wpf:lan("#:zh[确认新密码]:en[Confirm new password]#")}</th>
				<td><input type="password" name="confirmedPassword" class="{equalTo:'#${_zone} :password[name=newPassword]',maxlength:20}" /></td>
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