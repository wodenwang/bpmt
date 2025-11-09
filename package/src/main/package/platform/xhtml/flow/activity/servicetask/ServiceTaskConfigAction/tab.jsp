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
				$('a', $("li[aria-controls='" + $this.parents('div[tab]:first').attr('id') + "']", $this.parents('[tabs=true]:first'))).html(val);
			}
		});

	});
</script>

<c:set var="pixel" value="${param.pixel}" />
<input type="hidden" name="logics" value="${pixel}" />
<table class="ws-table">
	<tr>
		<th>处理逻辑(脚本类型)</th>
		<td><wcm:widget name="${pixel}.logicType" cmd="select[@com.riversoft.platform.script.ScriptTypes]{required:true}"></wcm:widget></td>
	</tr>
	<tr>
		<th>处理逻辑(脚本)<br /> <font color="red" tip="true" title="vo:订单实体;">(提示)</font></th>
		<td><wcm:widget name="${pixel}.logicScript" cmd="codemirror[groovy]{required:true}"></wcm:widget></td>
	</tr>
	<tr>
		<th>出错处理方式</th>
		<td><wcm:widget name="${pixel}.errorType" cmd="select[@com.riversoft.module.flow.activity.servicetask.ErrorType]{required:true}" /></td>
	</tr>
	<tr>
		<th>描述</th>
		<td><wcm:widget name="${pixel}.description" cmd="textarea{required:true}" /></td>
	</tr>
</table>

<%@ include file="/include/html_bottom.jsp"%>