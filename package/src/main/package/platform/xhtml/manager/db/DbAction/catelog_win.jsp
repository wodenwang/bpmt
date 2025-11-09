<%@ page language="java" pageEncoding="UTF-8"%>

<%-- 每个模块页面必须引入 --%>
<%@ include file="/include/common.jsp"%>
<%@ include file="/include/html_head.jsp"%>

<div name="errorZone" id="${_zone}_msg_zone"></div>
<form action="${_acp}/submitCatelog.shtml" sync="true"
	onsubmit="return false;">
	<input type="hidden" name="id" value="${vo.id}" />
	<table class="ws-table">
		<tr>
			<th>展示名</th>
			<td><wcm:widget name="busiName" cmd="text{required:true}"
					value="${vo.busiName}"></wcm:widget></td>
		</tr>
		<tr>
			<th>描述</th>
			<td><wcm:widget name="description" cmd="textarea"
					value="${vo.description}"></wcm:widget></td>
		</tr>
	</table>
</form>

<%-- 每个模块页面必须引入 --%>
<%@ include file="/include/html_bottom.jsp"%>