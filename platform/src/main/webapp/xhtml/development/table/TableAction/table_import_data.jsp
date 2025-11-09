<%@ page language="java" pageEncoding="UTF-8"%>

<%-- 每个模块页面必须引入 --%>
<%@ include file="/include/common.jsp"%>
<%@ include file="/include/html_head.jsp"%>

<div name="msgZone" id="${_zone}_err_zone"></div>
<form aync="true" action="${_acp}/submitImportData.shtml" method="post">
	<table class="ws-table">
		<tr>
			<th>导入前清空数据</th>
			<td><wcm:widget name="clearBeforeImport" cmd="radio[YES_NO]"
					value="0"></wcm:widget></td>
		</tr>
		<tr>
			<th>替换旧数据</th>
			<td><wcm:widget name="replaceIfConflict" cmd="radio[YES_NO]"
					value="1"></wcm:widget></td>
		</tr>
		<tr>
			<th>有错误则跳过</th>
			<td><wcm:widget name="exitIfError" cmd="radio[YES_NO]" value="1"></wcm:widget></td>
		</tr>
		<tr>
			<th>选择文件</th>
			<td><wcm:widget name="file" cmd="filemanager{required:true}"></wcm:widget></td>
		</tr>
	</table>
</form>


<%-- 每个模块页面必须引入 --%>
<%@ include file="/include/html_bottom.jsp"%>