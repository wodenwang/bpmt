<%@ page language="java" pageEncoding="UTF-8"%>

<%-- 每个模块页面必须引入 --%>
<%@ include file="/include/common.jsp"%>
<%@ include file="/include/html_head.jsp"%>

<div id="${_zone}_msg" name="msgZone"></div>
<form action="${_acp}/submitLink.shtml" method="post" sync="true">
	<table class="ws-table">
		<tr>
			<th>表名<br /> <font color="red">(纳入管理的表名)</font>
			</th>
			<td tip="true" title="仅允许大写字符与下划线,必须指定域前缀,如RV_."><wcm:widget
					name="name"
					cmd="text{required:true,maxlength:20,pattern2:['[A-Z]{2,5}_[A-Z0-9_]+','仅允许大写字符与下划线,必须指定域前缀,如RV_.']}"
					value="${editFlag?table.name:'RV_'}"
					state="${editFlag?'readonly':'normal'}"></wcm:widget></td>
		</tr>
		<tr>
			<th>展示名</th>
			<td><wcm:widget name="description"
					cmd="text{required:true,maxlength:20}"
					value="${editFlag?table.description:''}"></wcm:widget></td>
		</tr>
	</table>
</form>

<%-- 每个模块页面必须引入 --%>
<%@ include file="/include/html_bottom.jsp"%>