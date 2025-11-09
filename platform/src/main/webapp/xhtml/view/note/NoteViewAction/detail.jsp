<%@ page language="java" pageEncoding="UTF-8"%>

<%-- 每个模块页面必须引入 --%>
<%@ include file="/include/common.jsp"%>
<%@ include file="/include/html_head.jsp"%>

<h2 style="text-align: center; margin-top: 20px;">
	<c:if test="${vo.topFlag}">
		<span style="color: red; font-weight: bold;">[${wpf:lan("#:zh[置顶]:en[Top]#")}]</span>
	</c:if>
	<font color="${vo.COLOR}">${vo.TITLE}</font>
</h2>

<h4 style="text-align: right; margin-right: 10px; margin-top: 10px;">[${wpf:lan("#:zh[发布日期]:en[Release date]#")}: ${wcm:widget('date',vo.PUBLISH_DATE)}]</h4>
<h4 style="text-align: right; margin-right: 10px; margin-top: 5px;">[${wpf:lan("#:zh[编辑者]:en[Editor]#")}: ${vo.AUTHOR}]</h4>

<div style="margin: 10px 10px 10px 10px;">${vo.CONTENT}</div>

<c:if test="${not empty vo.ATTACHMENT}">
	<h4 style="text-left: right; margin-left: 10px; margin-top: 5px;">[${wpf:lan("#:zh[下载附件]:en[Download attachments]#")}:]</h4>
	<div style="margin: 10px 10px 10px 10px;">
		<wcm:widget name="attachments" cmd="multifilemanager" state="readonly" value="${vo.ATTACHMENT}"></wcm:widget>
	</div>
</c:if>

<%-- 每个模块页面必须引入 --%>
<%@ include file="/include/html_bottom.jsp"%>