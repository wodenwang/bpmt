<%@ page language="java" pageEncoding="UTF-8"%>

<%-- 每个模块页面必须引入 --%>
<%@ include file="/include/common.jsp"%>
<%@ include file="/include/html_head.jsp"%>
<script type="text/javascript">
	$(function() {
		Ui.changeCurrentTitle('${_zone}', '${node.nodeType.name}[${node.name}]设置');
	});
</script>
<div id="${_zone}_msg_zone"></div>
<form action="${_acp}/submitForm.shtml" sync="true"
	option="{confirmMsg:'是否保存?',errorZone:'${_zone}_msg_zone'}">
	<input type="hidden" name="pdId" value="${param.pdId}" /> <input
		type="hidden" name="activityId" value="${param.activityId}" />
	<table class="ws-table">
		<tr>
			<th>节点类型</th>
			<td>${node.nodeType.name}</td>
		</tr>
		<tr>
			<th>节点名</th>
			<td>${node.name}</td>
		</tr>
		<tr>
			<th>完成类型</th>
			<td><wcm:widget name="stateType"
					cmd="select[@com.riversoft.module.flow.activity.endevent.StateType]{required:true}"
					value="${vo.stateType}" /></td>
		</tr>
		<tr>
			<th>备注</th>
			<td><wcm:widget name="description" cmd="textarea"
					value="${vo.description}" /></td>
		</tr>
	</table>
</form>

<%-- 每个模块页面必须引入 --%>
<%@ include file="/include/html_bottom.jsp"%>