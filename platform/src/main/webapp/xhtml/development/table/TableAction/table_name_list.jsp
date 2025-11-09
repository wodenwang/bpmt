<%@ page language="java" pageEncoding="UTF-8"%>

<%-- 每个模块页面必须引入 --%>
<%@ include file="/include/common.jsp"%>
<%@ include file="/include/html_head.jsp"%>

<table class="ws-table">
	<c:forEach items="${sheetNames}" var="sheetName">
		<tr>
			<th>${sheetName}</th>
			<td><wcm:widget name="tableName" cmd="text{required:true,maxlength:20,pattern2:['[A-Z]{2,5}_[A-Z0-9_]+','仅允许大写字符与下划线.']}"></wcm:widget></td>
		</tr>
	</c:forEach>
</table>

<%-- 每个模块页面必须引入 --%>
<%@ include file="/include/html_bottom.jsp"%>