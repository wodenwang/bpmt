<%@ page language="java" pageEncoding="UTF-8"%>

<%-- 每个模块页面必须引入 --%>
<%@ include file="/include/common.jsp"%>
<%@ include file="/include/html_head.jsp"%>

<form action="${_acp}/preview.shtml" id="${_zone}_form">
	<input type="hidden" name="name" value="${table.name}" />
</form>

<%--数据表格 --%>
<table class="ws-table" form="${_zone}_form">
	<thead>
		<tr>
			<c:forEach items="${table.tbColumns}" var="field">
				<th field="${field.name}"
					style="${field.primaryKey?'color: red;':''}">[${field.name}]${field.description}</th>
			</c:forEach>
		</tr>
	</thead>
	<tbody>
		<c:forEach items="${dp.list}" var="vo">
			<tr>
				<c:forEach items="${table.tbColumns}" var="field">
					<td class="center">${vo[field.name]}</td>
				</c:forEach>
			</tr>
		</c:forEach>
	</tbody>
</table>

<%-- 分页  --%>
<wcm:page dp="${dp}" form="${_zone}_form" />

<%@ include file="/include/html_bottom.jsp"%>