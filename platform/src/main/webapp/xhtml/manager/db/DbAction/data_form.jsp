<%@ page language="java" pageEncoding="UTF-8"%>

<%-- 每个模块页面必须引入 --%>
<%@ include file="/include/common.jsp"%>
<%@ include file="/include/html_head.jsp"%>

<c:set var="editFlag" value="${vo!=null}" />
<div name="msgZone" id="${_zone}_err_zone"></div>
<form aync="true" action="${_acp}/submitDataForm.shtml" method="post">
	<table class="ws-table">
		<tr>
			<th>类型</th>
			<td><wcm:widget name="dataType"
					cmd="select[$CmBaseType;dataType;busiName]{required:true}"
					value="${dataType}" state="readonly"></wcm:widget></td>
		</tr>
		<tr>
			<th>代码</th>
			<td><wcm:widget name="dataCode" cmd="text{required:true}"
					value="${editFlag?vo.dataCode:''}"
					state="${editFlag?'readonly':''}"></wcm:widget></td>
		</tr>
		<tr>
			<th>父代码</th>
			<td><wcm:widget name="parentCode" cmd="text"
					value="${editFlag?vo.parentCode:''}"></wcm:widget></td>
		</tr>
		<tr>
			<th>翻译值</th>
			<td><wcm:widget name="showName" cmd="textarea{required:true}"
					value="${editFlag?vo.showName:''}"></wcm:widget></td>
		</tr>
		<tr>
			<th>排序</th>
			<td><wcm:widget name="sort"
					cmd="text{required:true,digits:true}"
					value="${editFlag?vo.sort:'0'}"></wcm:widget></td>
		</tr>
		<tr>
			<th>扩展字段(用于自定义条件)</th>
			<td><wcm:widget name="extra" cmd="textarea"
					value="${editFlag?vo.extra:''}"></wcm:widget></td>
		</tr>
		<tr>
			<th>描述</th>
			<td><wcm:widget name="description" cmd="textarea"
					value="${editFlag?vo.description:''}"></wcm:widget></td>
		</tr>
		<c:if test="${editFlag}">
			<tr>
				<th>创建时间</th>
				<td>${wcm:widget('datetime',vo.createDate)}</td>
			</tr>
			<tr>
				<th>更新时间</th>
				<td>${wcm:widget('datetime',vo.updateDate)}</td>
			</tr>
		</c:if>
	</table>
</form>


<%-- 每个模块页面必须引入 --%>
<%@ include file="/include/html_bottom.jsp"%>