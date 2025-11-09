<%@ page language="java" pageEncoding="UTF-8"%>

<%-- 每个模块页面必须引入 --%>
<%@ include file="/include/common.jsp"%>
<%@ include file="/include/html_head.jsp"%>

<div tabs="true">
	<div title="SQL控制台" init="${_acp}/sqlPanel.shtml"></div>
	<div title="脚本控制台" init="${_acp}/scriptPanel.shtml"></div>
</div>

<%-- 每个模块页面必须引入 --%>
<%@ include file="/include/html_bottom.jsp"%>