<%@ page language="java" pageEncoding="UTF-8"%>

<%-- 每个模块页面必须引入 --%>
<%@ include file="/include/common.jsp"%>
<%@ include file="/include/html_head.jsp"%>

<script type="text/javascript">
	$(function() {
		var $zone = $('#${_zone}');
		$("[name$='.var']", $zone).blur(function() {
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
		<th>变量名</th>
		<td><wcm:widget name="${pixel}.var" cmd="text{required:true}" /></td>
	</tr>
	<tr>
		<th>脚本类型</th>
		<td><wcm:widget name="${pixel}.execType" cmd="select[@com.riversoft.platform.script.ScriptTypes]{required:true}" /></td>
	</tr>
	<tr>
		<th>脚本<br /> <font color="red" tip="true" title="可用前一个处理器的变量.">(提示)</font></th>
		<td><wcm:widget name="${pixel}.execScript" cmd="codemirror[groovy]{required:true}" /></td>
	</tr>
	<tr>
		<th>备注</th>
		<td><wcm:widget cmd="textarea" name="${pixel}.description" /></td>
	</tr>
</table>

<%-- 每个模块页面必须引入 --%>
<%@ include file="/include/html_bottom.jsp"%>