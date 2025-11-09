<%@ page language="java" pageEncoding="UTF-8"%>

<%-- 每个模块页面必须引入 --%>
<%@ include file="/include/common.jsp"%>
<%@ include file="/include/html_head.jsp"%>

<input type="hidden" name="groupId" value="${groupId}" />
<table class="ws-table">
	<tr>
		<th check="true"></th>
		<th>特殊权限</th>
		<th>权限名称</th>
		<th>描述</th>
	</tr>
	<c:forEach items="${pris}" var="vo">
		<tr>
			<td check="${!vo.scriptOnly}" value="${vo.priKey}" checkname="priKey" checkstate="${wcm:contains(priKeys,vo.priKey)}"></td>
			<td class="center"><wcm:widget name="relate" cmd="prigroup[${groupId};${vo.priKey}]" /></td>
			<td>${vo.busiName}</td>
			<td>${vo.description}</td>
		</tr>
	</c:forEach>
</table>

<%-- 每个模块页面必须引入 --%>
<%@ include file="/include/html_bottom.jsp"%>