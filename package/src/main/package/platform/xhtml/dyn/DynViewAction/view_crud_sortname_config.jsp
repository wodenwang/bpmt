<%@ page language="java" pageEncoding="UTF-8"%>

<%-- 每个模块页面必须引入 --%>
<%@ include file="/include/common.jsp"%>
<%@ include file="/include/html_head.jsp"%>

<select name="table.sortName" class="chosen">
	<c:forEach items="${table.tbColumns}" var="column">
		<c:choose>
			<c:when test="${column.name==param.sortName}">
				<option value="${column.name}" selected="selected">[${column.name}]${column.description}</option>
			</c:when>
			<c:otherwise>
				<option value="${column.name}">[${column.name}]${column.description}</option>
			</c:otherwise>
		</c:choose>
	</c:forEach>
</select>

<%-- 每个模块页面必须引入 --%>
<%@ include file="/include/html_bottom.jsp"%>