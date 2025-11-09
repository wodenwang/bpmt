<%@ page language="java" pageEncoding="UTF-8"%>

<%-- 每个模块页面必须引入 --%>
<%@ include file="/include/common.jsp"%>
<%@ include file="/include/html_head.jsp"%>

<script type="text/javascript">
	$(function() {
		Ui.changeCurrentTitle('${_zone}', '${node.nodeType.name}[${node.name}]设置');
	});
</script>

<table class="ws-table">
	<tr>
		<th>节点类型</th>
		<td>${node.nodeType.name}</td>
	</tr>
	<tr>
		<th>节点名</th>
		<td>${node.name}</td>
	</tr>
</table>

<div class="ws-msg info">此节点无需设置.</div>

<%-- 每个模块页面必须引入 --%>
<%@ include file="/include/html_bottom.jsp"%>