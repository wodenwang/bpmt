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
<input type="hidden" name="detail.batchColumns" value="${pixel}" />
<table class="ws-table">
	<tr>
		<th>列名</th>
		<td><wcm:widget name="${pixel}.name" cmd="text{required:true}" /></td>
	</tr>
	<tr>
		<th>展示名</th>
		<td><wcm:widget name="${pixel}.busiName" cmd="text{required:true}" /></td>
	</tr>
	<tr>
		<th>示例</th>
		<td><wcm:widget name="${pixel}.example" cmd="text" /></td>
	</tr>
	<tr>
		<th>备注</th>
		<td><wcm:widget cmd="textarea" name="${pixel}.description" /></td>
	</tr>
</table>

<%-- 每个模块页面必须引入 --%>
<%@ include file="/include/html_bottom.jsp"%>