<%@ page language="java" pageEncoding="UTF-8"%>

<%-- 每个模块页面必须引入 --%>
<%@ include file="/include/common.jsp"%>
<%@ include file="/include/html_head.jsp"%>

<c:set var="editFlag" value="${vo!=null}" />
<div name="msgZone" id="${_zone}_err_zone"></div>
<form aync="true" action="${_acp}/submitTypeForm.shtml" method="post">
	<input type="hidden" name="sort" value="${editFlag?vo.sort:999}" /> <input type="hidden" name="editFlag" value="${editFlag?1:0}" />
	<table class="ws-table">
		<tr>
			<th>类型主键</th>
			<td><wcm:widget name="dataType" cmd="text{required:true}" value="${editFlag?vo.dataType:''}" state="${editFlag?'readonly':'normal'}"></wcm:widget></td>
		</tr>
		<tr>
			<th>展示名</th>
			<td><wcm:widget name="busiName" cmd="text{required:true}" value="${editFlag?vo.busiName:''}"></wcm:widget></td>
		</tr>
		<tr>
			<th>分类</th>
			<td><wcm:widget name="catelog" cmd="select[$CmBaseCatelog(请选择);id;busiName]" value="${editFlag?vo.catelog:''}"></wcm:widget></td>
		</tr>
		<tr>
			<th>描述</th>
			<td><wcm:widget name="description" cmd="textarea" value="${editFlag?vo.description:''}"></wcm:widget></td>
		</tr>
	</table>
</form>


<%-- 每个模块页面必须引入 --%>
<%@ include file="/include/html_bottom.jsp"%>