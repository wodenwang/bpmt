<%@ page language="java" pageEncoding="UTF-8"%>

<%-- 每个模块页面必须引入 --%>
<%@ include file="/include/common.jsp"%>
<%@ include file="/include/html_head.jsp"%>

<script type="text/javascript">
	$(function() {
		var $zone = $('#${_zone}');
		$("[name$='.busiName']", $zone).blur(function() {
			var $this = $(this);
			var val = $(this).val();
			if (val != null && val != '') {
				$('a', $("li[aria-controls='${_zone}']", $this.parents('[tabs=true]:first'))).html(val);
			}
		});
	});
</script>

<c:set var="pixel" value="${param.pixel}" />
<input type="hidden" name="${param.type}" value="${pixel}" />
<table class="ws-table">
	<tr>
		<th>展示名</th>
		<td><wcm:widget name="${pixel}.busiName" cmd="text{required:true}"></wcm:widget></td>
	</tr>
	<tr>
		<th>控件</th>
		<td><wcm:widget name="${pixel}.widget" cmd="widget{required:true}" value="text"></wcm:widget></td>
	</tr>
	<tr>
		<th>控件动态入参(脚本类型)</th>
		<td><wcm:widget name="${pixel}.widgetParamType" cmd="select[@com.riversoft.platform.script.ScriptTypes]"></wcm:widget></td>
	</tr>
	<tr>
		<th>控件动态入参(脚本)</th>
		<td><wcm:widget name="${pixel}.widgetParamScript" cmd="codemirror[groovy]"></wcm:widget></td>
	</tr>
	<tr>
		<th>默认值</th>
		<td><wcm:widget name="${pixel}.defVal" cmd="textarea"></wcm:widget></td>
	</tr>
	<tr>
		<th>SQL片段(脚本类型)</th>
		<td><wcm:widget name="${pixel}.sqlType" cmd="select[@com.riversoft.platform.script.ScriptTypes]{required:true}"></wcm:widget></td>
	</tr>
	<tr>
		<th>SQL片段(脚本)<font color="red" tip="true" title="value:提交后表单值;values:多选框表单值">(提示)</font></th>
		<td><wcm:widget name="${pixel}.sqlScript" cmd="codemirror[groovy]{required:true}"></wcm:widget></td>
	</tr>
	<tr>
		<th>备注</th>
		<td><wcm:widget name="${pixel}.description" cmd="textarea"></wcm:widget></td>
	</tr>
</table>
<%-- 每个模块页面必须引入 --%>
<%@ include file="/include/html_bottom.jsp"%>