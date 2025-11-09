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
<input type="hidden" name="${pixel}.flowId" value="${param.flowId}" />
<table class="ws-table">
	<tr>
		<th>执行处理器(脚本类型)</th>
		<td><wcm:widget name="${pixel}.execType"
				cmd="select[@com.riversoft.platform.script.ScriptTypes]{required:true}"></wcm:widget></td>
	</tr>
	<tr>
		<th>执行处理器(脚本)<br /> <font color="red" tip="true"
			title="mode:1:新增时,2:修改时;vo:实体;fo:节点工作流信息;">(提示)</font></th>
		<td><wcm:widget name="${pixel}.execScript"
				cmd="codemirror[groovy]{required:true}"></wcm:widget></td>
	</tr>
	<tr>
		<th>备注</th>
		<td><wcm:widget cmd="textarea{required:true}"
				name="${pixel}.description" /></td>
	</tr>
</table>

<%-- 每个模块页面必须引入 --%>
<%@ include file="/include/html_bottom.jsp"%>