<%@ page language="java" pageEncoding="UTF-8"%>

<%-- 每个模块页面必须引入 --%>
<%@ include file="/include/common.jsp"%>
<%@ include file="/include/html_head.jsp"%>

<div tabs="true">
	<div title="收件箱" init="${_acp}/inboxMain.shtml"></div>
	<div title="发件箱" init="${_acp}/outboxMain.shtml"></div>
	<div title="邮箱设置" init="${_acp}/setting.shtml"></div>
</div>

<%-- 每个模块页面必须引入 --%>
<%@ include file="/include/html_bottom.jsp"%>