<%@ page language="java" pageEncoding="UTF-8"%>

<%-- 每个模块页面必须引入 --%>
<%@ include file="/include/common.jsp"%>
<%@ include file="/include/html_head.jsp"%>

<table class="ws-table">
	<tr>
		<c:forEach items="${fields}" var="field">
			<th>${field}</th>
		</c:forEach>
	</tr>

	<c:forEach items="${list}" var="vo">
		<tr>
			<c:forEach items="${fields}" var="field">
				<td>${vo[field]}</td>
			</c:forEach>
		</tr>
	</c:forEach>
</table>

<%-- 每个模块页面必须引入 --%>
<%@ include file="/include/html_bottom.jsp"%>