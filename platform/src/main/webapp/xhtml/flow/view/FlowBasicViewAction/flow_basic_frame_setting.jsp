<%@ page language="java" pageEncoding="UTF-8"%>

<%-- 每个模块页面必须引入 --%>
<%@ include file="/include/common.jsp"%>
<%@ include file="/include/html_head.jsp"%>

<div tabs="true" button="left">
	<div title="列表页排版" init="${_acp}/sortList.shtml?key=${param.key}"></div>
	<div title="明细页排版" init="${_acp}/columnSort.shtml?key=${param.key}"></div>
</div>

<%-- 每个模块页面必须引入 --%>
<%@ include file="/include/html_bottom.jsp"%>