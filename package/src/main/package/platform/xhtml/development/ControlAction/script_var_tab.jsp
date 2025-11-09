<%@ page language="java" pageEncoding="UTF-8"%>

<%-- 每个模块页面必须引入 --%>
<%@ include file="/include/common.jsp"%>
<%@ include file="/include/html_head.jsp"%>

<c:set var="pixel" value="${param.pixel}" />

<script type="text/javascript">
	$(function() {
		var $zone = $('#${_zone}');
		$("input[name$='.name']", $zone).blur(
				function() {
					var val = $(this).val();
					if (val != null && val != '') {
						var $tab = $zone;
						var $a = $('a', $("li[aria-controls='"
								+ $tab.attr("id") + "']", $tab.parent()));
						$a.html(val);
					}
				});
	});
</script>
<input type="hidden" name="vars" value="${pixel}" />
<table class="ws-table">
	<tr>
		<th>变量名</th>
		<td><wcm:widget name="${pixel}.name" cmd="text[required:true]"></wcm:widget></td>
	</tr>
	<tr>
		<th>语言类型</th>
		<td><wcm:widget name="${pixel}.type"
				cmd="select[@com.riversoft.platform.script.ScriptTypes]"></wcm:widget></td>
	</tr>
	<tr>
		<th>数据</th>
		<td><wcm:widget name="${pixel}.script"
				cmd="codemirror[groovy]"></wcm:widget></td>
	</tr>
</table>


<%-- 每个模块页面必须引入 --%>
<%@ include file="/include/html_bottom.jsp"%>