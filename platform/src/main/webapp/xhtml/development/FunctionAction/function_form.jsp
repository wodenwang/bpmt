<%@ page language="java" pageEncoding="UTF-8"%>

<%-- 每个模块页面必须引入 --%>
<%@ include file="/include/common.jsp"%>
<%@ include file="/include/html_head.jsp"%>

<c:set var="editFlag" value="${vo!=null}" />
<div name="msgZone" id="${_zone}_err_zone"></div>
<form aync="true" action="${_acp}/submitFunctionForm.shtml" method="post">
	<input type="hidden" name="editFlag" value="${editFlag?1:0}" />
	<table class="ws-table">
		<tr>
			<th>类型</th>
			<td><wcm:widget name="catelog" cmd="tree[$DevFunctionCatelog;cateKey;parentKey;busiName;1=1 order by sort asc]{required:true}" value="${editFlag?vo.catelog:catelog}"></wcm:widget></td>
		</tr>
		<tr>
			<th>函数名</th>
			<td><wcm:widget name="functionKey" cmd="text{required:true}" value="${editFlag?vo.functionKey:''}" state="${editFlag?'readonly':''}"></wcm:widget></td>
		</tr>
		<tr>
			<th>描述</th>
			<td><wcm:widget name="description" cmd="textarea" value="${editFlag?vo.description:''}"></wcm:widget></td>
		</tr>

		<tr>
			<th>脚本类型</th>
			<td><wcm:widget name="functionType" cmd="select[@com.riversoft.platform.script.ScriptTypes]{required:true}" value="${editFlag?vo.functionType:''}"></wcm:widget></td>
		</tr>
		<tr>
			<th>脚本</th>
			<td><wcm:widget name="functionScript" cmd="codemirror[groovy]{required:true}" value="${editFlag?vo.functionScript:''}"></wcm:widget></td>
		</tr>
		<tr>
			<th>举例</th>
			<td><wcm:widget name="example" cmd="codemirror[groovy]" value="${editFlag?vo.example:''}"></wcm:widget></td>
		</tr>

		<c:if test="${editFlag}">
			<tr>
				<th>创建时间</th>
				<td>${wcm:widget('date',vo.createDate)}</td>
			</tr>
			<tr>
				<th>更新时间</th>
				<td>${wcm:widget('date',vo.updateDate)}</td>
			</tr>
		</c:if>
	</table>
</form>


<%-- 每个模块页面必须引入 --%>
<%@ include file="/include/html_bottom.jsp"%>