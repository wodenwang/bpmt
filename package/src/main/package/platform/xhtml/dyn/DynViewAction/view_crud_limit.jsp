<%@ page language="java" pageEncoding="UTF-8"%>

<%-- 每个模块页面必须引入 --%>
<%@ include file="/include/common.jsp"%>
<%@ include file="/include/html_head.jsp"%>

<script type="text/javascript">
	$(function() {
		var $zone = $('#${_zone}');
		$("[name$='.description']", $zone).blur(function() {
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
		<th>SQL片段(脚本类型)</th>
		<td><wcm:widget name="${pixel}.sqlType" cmd="select[@com.riversoft.platform.script.ScriptTypes]{required:true}"></wcm:widget></td>
	</tr>
	<tr>
		<th>SQL片段(脚本)</th>
		<td><wcm:widget name="${pixel}.sqlScript" cmd="codemirror[groovy]{required:true}"></wcm:widget></td>
	</tr>
	<tr>
		<th>备注</th>
		<td><wcm:widget name="${pixel}.description" cmd="textarea{required:true}"></wcm:widget></td>
	</tr>
	<tr>
		<th>功能点</th>
		<td><wcm:widget name="${pixel}.pri" cmd="pri{required:true}"></wcm:widget></td>
	</tr>
</table>
<%-- 每个模块页面必须引入 --%>
<%@ include file="/include/html_bottom.jsp"%>