<%@ page language="java" pageEncoding="UTF-8"%>

<%-- 每个模块页面必须引入 --%>
<%@ include file="/include/common.jsp"%>
<%@ include file="/include/html_head.jsp"%>

<input type="hidden" name="hasJs" value="true" />
<div tabs="true">
	<div title="列表页脚本(查询表单)">
		<table class="ws-table">
			<tr>
				<th>脚本类型</th>
				<td><wcm:widget name="js.listJsType" cmd="select[@com.riversoft.platform.script.ScriptTypes]" value="${vo.listJsType}" /></td>
			</tr>
			<tr>
				<th>脚本<br /> <font color="red" tip="true" title="$zone:当前区域;$form:目标表单;_mode:当前页面类型,可选:[xhtml,h5].">(提示)</font></th>
				<td><wcm:widget name="js.listJsScript" cmd="codemirror[javascript]" value="${vo.listJsScript}" /></td>
			</tr>
		</table>
	</div>
	<div title="表单页脚本">
		<table class="ws-table">
			<tr>
				<th>脚本类型</th>
				<td><wcm:widget name="js.formJsType" cmd="select[@com.riversoft.platform.script.ScriptTypes]" value="${vo.formJsType}" /></td>
			</tr>
			<tr>
				<th>脚本<br /> <font color="red" tip="true" title="$zone:当前区域;$form:目标表单;_mode:当前页面类型,可选:[xhtml,h5].vo:实体.">(提示)</font></th>
				<td><wcm:widget name="js.formJsScript" cmd="codemirror[javascript]" value="${vo.formJsScript}" /></td>
			</tr>
		</table>
	</div>
</div>

<%-- 每个模块页面必须引入 --%>
<%@ include file="/include/html_bottom.jsp"%>