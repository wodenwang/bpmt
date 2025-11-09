<%@ page language="java" pageEncoding="UTF-8"%>

<%-- 每个模块页面必须引入 --%>
<%@ include file="/include/common.jsp"%>
<%@ include file="/include/html_head.jsp"%>

<input type="hidden" name="combo.flag" value="true" />
<div accordion="true" multi="true">
	<div title="CODE设置">
		<table class="ws-table">
			<tr>
				<th>CODE(脚本类型)</th>
				<td><wcm:widget name="combo.codeType" cmd="select[@com.riversoft.platform.script.ScriptTypes]{required:true}" value="${vo.codeType}" /></td>
			</tr>
			<tr>
				<th>CODE (脚本)<br /> <font color="red" tip="true" title="vo:单条数据.">(提示)</font></th>
				<td><wcm:widget name="combo.codeScript" cmd="codemirror[groovy]{required:true}" value="${vo.codeScript}" /></td>
			</tr>
		</table>
	</div>
	<div title="NAME设置">
		<table class="ws-table">
			<tr>
				<th>NAME(脚本类型)</th>
				<td><wcm:widget name="combo.nameType" cmd="select[@com.riversoft.platform.script.ScriptTypes]{required:true}" value="${vo.nameType}" /></td>
			</tr>
			<tr>
				<th>NAME (脚本)<br /> <font color="red" tip="true" title="vo:单条数据.">(提示)</font></th>
				<td><wcm:widget name="combo.nameScript" cmd="codemirror[groovy]{required:true}" value="${vo.nameScript}" /></td>
			</tr>
		</table>
	</div>

	<div title="翻译设置">
		<table class="ws-table">
			<tr>
				<th>SQL片段(脚本类型)</th>
				<td><wcm:widget name="combo.pkSqlType" cmd="select[@com.riversoft.platform.script.ScriptTypes]" value="${vo.pkSqlType}" /></td>
			</tr>
			<tr>
				<th>SQL片段 (脚本)<br /> <font color="red" tip="true" title="value:传入的code值.">(提示)</font></th>
				<td><wcm:widget name="combo.pkSqlScript" cmd="codemirror[groovy]" value="${vo.pkSqlScript}" /></td>
			</tr>
		</table>
	</div>
</div>

<%-- 每个模块页面必须引入 --%>
<%@ include file="/include/html_bottom.jsp"%>