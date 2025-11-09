<%@ page language="java" pageEncoding="UTF-8"%>

<%-- 每个模块页面必须引入 --%>
<%@ include file="/include/common.jsp"%>
<%@ include file="/include/html_head.jsp"%>

<table class="ws-table">
	<tr>
		<th style="width: 20px;">序号</th>
		<th>所属权限组</th>
		<th>权限组描述</th>
	</tr>
	<c:forEach items="${list}" var="vo" varStatus="status">
		<tr>
			<td class="center">${status.index+1}</td>
			<td class="left"><font color="${param.groupId==vo.groupId?'blue':''}" tip="true" selector=".select"><span class="select">${vo.tips}</span> ${vo.name} </font></td>
			<td class="left">${vo.description}</td>
		</tr>
	</c:forEach>
</table>

<%-- 每个模块页面必须引入 --%>
<%@ include file="/include/html_bottom.jsp"%>