<%@ page language="java" pageEncoding="UTF-8"%>

<%-- 每个模块页面必须引入 --%>
<%@ include file="/include/common.jsp"%>
<%@ include file="/include/html_head.jsp"%>

<form action="${_acp}/create.shtml">
	<table class="ws-table">
		<tr>
			<th>展示名</th>
			<td><wcm:widget name="name" cmd="text{required:true}"></wcm:widget></td>
		</tr>
		<tr>
			<th>类别</th>
			<td><wcm:widget name="category" cmd="text" /></td>
		</tr>
		<tr>
			<th>导入流程</th>
			<td><wcm:widget name="file" cmd="filemanager"></wcm:widget></td>
		</tr>
		<tr>
			<th>描述</th>
			<td><wcm:widget name="description" cmd="textarea"></wcm:widget></td>
		</tr>
	</table>
</form>

<%-- 每个模块页面必须引入 --%>
<%@ include file="/include/html_bottom.jsp"%>