<%@ page language="java" pageEncoding="UTF-8"%>

<%-- 每个模块页面必须引入 --%>
<%@ include file="/include/common.jsp"%>
<%@ include file="/include/html_head.jsp"%>

<div name="msgZone" id="${_zone}_err_zone"></div>
<form aync="true" action="${_acp}/submitBatch.shtml" method="post">
	<table class="ws-table">
		<tr>
			<th>批处理类型</th>
			<td><input type="radio" name="type" value="1" checked="checked" /><label>新增</label> <input type="radio" name="type" value="2" /><label>修改</label></td>
		</tr>
		<tr>
			<th>选择文件</th>
			<td><wcm:widget name="file" cmd="filemanager{required:true}"></wcm:widget></td>
		</tr>
	</table>
</form>


<%-- 每个模块页面必须引入 --%>
<%@ include file="/include/html_bottom.jsp"%>