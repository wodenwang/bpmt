<%@ page language="java" pageEncoding="UTF-8"%>

<%-- 每个模块页面必须引入 --%>
<%@ include file="/include/common.jsp"%>
<%@ include file="/include/html_head.jsp"%>

<c:set var="context" value="${wcm:map(null,'mode',4)}" />

<form action="${_acp}/downloadBatch.shtml">
	<table class="ws-table">
		<tr>
			<th style="width: 20px;">${wpf:lan("#:zh[范围]:en[Range]#")}</th>
			<td><input type="radio" value="current" name="type" checked="checked" /><label>${wpf:lan("#:zh[当前条件]:en[Current condition]#")}</label><input type="radio" value="all" name="type" /><label>${wpf:lan("#:zh[所有]:en[All]#")}</label></td>
		</tr>
		<tr>
			<th check="true"></th>
			<th>${wpf:lan("#:zh[字段]:en[Field]#")}</th>
		</tr>
		<c:forEach items="${config.downloadList}" var="field" varStatus="status">
			<c:if test="${wpf:checkExt(field.pri,context)}">
				<tr>
					<td check="true" value="${status.index}" checkname="selectKey" checkstate="${field.listSort>-1}"></td>
					<td><c:if test="${field.name!=null}">[${field.name}]</c:if> ${wpf:lan(field.busiName)}</td>
				</tr>
			</c:if>
		</c:forEach>
	</table>
</form>

<%-- 每个模块页面必须引入 --%>
<%@ include file="/include/html_bottom.jsp"%>