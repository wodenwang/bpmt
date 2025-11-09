<%@ page language="java" pageEncoding="UTF-8"%>

<%-- 每个模块页面必须引入 --%>
<%@ include file="/include/common.jsp"%>
<%@ include file="/include/html_head.jsp"%>

<table class="ws-table" form="${_zone}_form">
	<tr>
		<th check="true"></th>
		<th field="uid">用户名</th>
		<th field="busiName">展示名</th>
		<th field="sort">排序</th>
		<th field="createDate">录入时间</th>
		<th field="updateDate">修改时间</th>
	</tr>
	<c:forEach items="${dp.list}" var="vo">
		<tr>
			<td check="true" value="${vo.uid}"></td>
			<td class="center">${vo.uid} <input type="hidden" name="uid"
				value="${vo.uid}" /> <input type="hidden" name="busiName"
				value="${vo.busiName}" />
			</td>
			<td class="center">${vo.busiName}</td>
			<td class="right">${vo.sort}</td>
			<td class="center">${wcm:widget('date',vo.createDate)}</td>
			<td class="center">${wcm:widget('date',vo.updateDate)}</td>
		</tr>
	</c:forEach>
</table>

<wcm:page dp="${dp}" form="${_zone}_form"></wcm:page>

<%-- 每个模块页面必须引入 --%>
<%@ include file="/include/html_bottom.jsp"%>