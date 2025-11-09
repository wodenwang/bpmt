<%@ page language="java" pageEncoding="UTF-8"%>

<%-- 每个模块页面必须引入 --%>
<%@ include file="/include/common.jsp"%>
<%@ include file="/include/html_head.jsp"%>


<table class="ws-table">
	<tr>
		<th style="width: 30%;">文件名</th>
		<td>${vo.name}</td>
	</tr>
	<tr>
		<th style="width: 30%;">文件大小</th>
		<td>${vo.size}</td>
	</tr>
	<tr>
		<th style="width: 30%;">最后修改时间</th>
		<td>${vo.date}</td>
	</tr>
</table>


<%-- 每个模块页面必须引入 --%>
<%@ include file="/include/html_bottom.jsp"%>