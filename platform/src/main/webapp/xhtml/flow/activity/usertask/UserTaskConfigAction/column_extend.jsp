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
	<tr>
		<th>备注</th>
		<td><wcm:widget name="${pixel}.description" cmd="textarea" value="${vo.description}" /></td>
	</tr>
</table>

<%-- 每个模块页面必须引入 --%>
<%@ include file="/include/html_bottom.jsp"%>