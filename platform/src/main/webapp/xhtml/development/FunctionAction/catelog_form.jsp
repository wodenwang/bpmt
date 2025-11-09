<%@ page language="java" pageEncoding="UTF-8"%>

<%-- 每个模块页面必须引入 --%>
<%@ include file="/include/common.jsp"%>
<%@ include file="/include/html_head.jsp"%>

<c:set var="editFlag" value="${vo!=null}" />
<div name="msgZone" id="${_zone}_err_zone"></div>
<form aync="true" action="${_acp}/submitCatelogForm.shtml" method="post">
	<input type="hidden" name="parentKey" value="${vo.parentKey}" /> <input
		type="hidden" name="sort" value="${editFlag?vo.sort:999}" />
	<table class="ws-table">
		<tr>
			<th>类别主键</th>
			<td><c:choose>
					<c:when test="${editFlag}">
						<wcm:widget name="cateKey" cmd="text{required:true}"
							value="${vo.cateKey}" state="readonly"></wcm:widget>
					</c:when>
					<c:otherwise>
						<font color="red">(系统自动生成)</font>
					</c:otherwise>
				</c:choose></td>
		</tr>
		<tr>
			<th>展示名</th>
			<td><wcm:widget name="busiName" cmd="text{required:true}"
					value="${editFlag?vo.busiName:''}"></wcm:widget></td>
		</tr>
		<tr>
			<th>描述</th>
			<td><wcm:widget name="description" cmd="textarea"
					value="${editFlag?vo.description:''}"></wcm:widget></td>
		</tr>
	</table>
</form>


<%-- 每个模块页面必须引入 --%>
<%@ include file="/include/html_bottom.jsp"%>