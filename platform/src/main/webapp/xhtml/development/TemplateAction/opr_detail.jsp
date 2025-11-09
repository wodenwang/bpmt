<%@ page language="java" pageEncoding="UTF-8"%>

<%-- 每个模块页面必须引入 --%>
<%@ include file="/include/common.jsp"%>
<%@ include file="/include/html_head.jsp"%>

<table class="ws-table">
	<tr>
		<th field="version">版本</th>
		<td>${vo.version}</td>
	</tr>
	<tr>
		<th>执行描述</th>
		<td>${vo.oprMemo}</td>
	</tr>
	<tr>
		<th>执行类</th>
		<td>${vo.oprClass}</td>
	</tr>
	<tr>
		<th>执行方法</th>
		<td>${vo.oprMethod}</td>
	</tr>
	<tr>
		<th>入参</th>
		<td><textarea name="_tmp" width="800" height="100" code="true" option="{mode:'json',readOnly:true,showCursorWhenSelecting:true}" class="CodeMirror-normal" readonly="readonly">${vo.oprArgs}</textarea></td>
	</tr>
	<tr>
		<th>创建人</th>
		<td>${wcm:widget('select[$com.riversoft.platform.po.UsUser;uid;busiName]',vo.createUid)}</td>
	</tr>
	<tr>
		<th>创建时间</th>
		<td>${wcm:widget('date',vo.createDate)}</td>
	</tr>
</table>

<%-- 每个模块页面必须引入 --%>
<%@ include file="/include/html_bottom.jsp"%>