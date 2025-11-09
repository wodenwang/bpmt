<%@ page language="java" pageEncoding="UTF-8"%>

<%-- 每个模块页面必须引入 --%>
<%@ include file="/include/common.jsp"%>
<%@ include file="/include/html_head.jsp"%>

<c:set var="pixel" value="${param.pixel}" />

<script type="text/javascript">
	$(function() {
		var $zone = $('#${_zone}');
	});
</script>

<input type="hidden" name="${pixel}.flag" value="true" />
<input type="hidden" name="${pixel}.id" value="${vo.id}" />
<table class="ws-table">
	<c:if test="${type=='form'}">
		<tr>
			<th>表单内容(脚本类型)</th>
			<td><wcm:widget name="${pixel}.contentType" cmd="select[@com.riversoft.platform.script.ScriptTypes]" value="${vo.contentType}"></wcm:widget></td>
		</tr>
		<tr>
			<th>表单内容(脚本内容)<br /> <font color="red" tip="true" title="新增时有效,无法使用vo.">(提示)</font></th>
			<td><wcm:widget name="${pixel}.contentScript" cmd="codemirror[groovy]" value="${vo.contentScript}"></wcm:widget></td>
		</tr>
	</c:if>
	<c:if test="${type=='show'}">
		<tr>
			<th>展示内容(脚本类型)</th>
			<td><wcm:widget name="${pixel}.contentType" cmd="select[@com.riversoft.platform.script.ScriptTypes]" value="${vo.contentType}"></wcm:widget></td>
		</tr>
		<tr>
			<th>展示内容(脚本内容)<br /> <font color="red" tip="true" title="新增时有效,无法使用vo.">(提示)</font></th>
			<td><wcm:widget name="${pixel}.contentScript" cmd="codemirror[groovy]" value="${vo.contentScript}"></wcm:widget></td>
		</tr>
	</c:if>
	<tr>
		<th>备注</th>
		<td><wcm:widget name="${pixel}.description" cmd="textarea" value="${vo.description}" /></td>
	</tr>
</table>

<%-- 每个模块页面必须引入 --%>
<%@ include file="/include/html_bottom.jsp"%>