<%@ page language="java" pageEncoding="UTF-8"%>

<%-- 每个模块页面必须引入 --%>
<%@ include file="/include/common.jsp"%>
<%@ include file="/include/html_head.jsp"%>

<div tabs="true" button="left">
	<div title="${wpf:lan('#:zh[通知设置]:en[Notification Settings]#')}" init="${_acp}/notifySetting.shtml"></div>
	<div title="${wpf:lan('#:zh[密码修改]:en[Password change]#')}" init="${_acp}/changePwd.shtml"></div>
	<c:if test="${wxQRcode && openFlag}">
		<div title="${wpf:lan('#:zh[绑定微信]:en[Wechat Binding]#')}" init="${_acp}/wxBinding.shtml"></div>
	</c:if>
</div>

<%-- 每个模块页面必须引入 --%>
<%@ include file="/include/html_bottom.jsp"%>