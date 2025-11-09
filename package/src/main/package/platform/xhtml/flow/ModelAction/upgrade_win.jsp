<%@ page language="java" pageEncoding="UTF-8"%>

<%-- 每个模块页面必须引入 --%>
<%@ include file="/include/common.jsp"%>
<%@ include file="/include/html_head.jsp"%>

<div name="errorZone" id="${_zone}_msg_zone"></div>

<form action="${_acp}/submitUpgrade.shtml" sync="true"
	onsubmit="return false;">
	<input type="hidden" name="id" value="${param.id}" /> <input
		type="hidden" name="key" value="${key}" /> <input type="hidden"
		name="type" value="0" />
	<table class="ws-table">
		<tr>
			<th>选择一个已部署版本</th>
			<td><select name="version"
				class="chosen {required:true} needValid">
					<option value="">请选择</option>
					<c:forEach items="${syncList}" var="o">
						<option value="${o.version}">${o.name}-版本:${o.version}</option>
					</c:forEach>
			</select></td>
		</tr>
		<tr>
			<th>流程唯一KEY</th>
			<td>${key}</td>
		</tr>
		<tr>
			<th>新的流程名称</th>
			<td>${name}</td>
		</tr>

	</table>
</form>

<%-- 每个模块页面必须引入 --%>
<%@ include file="/include/html_bottom.jsp"%>