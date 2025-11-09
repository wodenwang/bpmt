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

<input type="hidden" name="subs" value="${param.pixel}" />
<table class="ws-table">
	<tr>
		<th>展示名</th>
		<td><input type="text" name="${param.pixel}.busiName" class="{required:true}" /></td>
	</tr>
	<tr>
		<th>标签样式</th>
		<td><wcm:widget name="${param.pixel}.style" cmd="style" /></td>
	</tr>
	<tr>
		<th>链接视图</th>
		<td><wcm:widget name="${param.pixel}.action" cmd="view[SUB]{required:true}" /></td>
	</tr>
	<tr>
		<th>动态入参(脚本类型)</th>
		<td><wcm:widget name="${param.pixel}.paramType" cmd="select[@com.riversoft.platform.script.ScriptTypes]"></wcm:widget></td>
	</tr>
	<tr>
		<th>动态入参(脚本)<br /> <font color="red" tip="true" title="在视图模块中在request中通过[_params]命令字获取.">(提示)</font></th>
		<td><wcm:widget name="${param.pixel}.paramScript" cmd="codemirror[groovy]"></wcm:widget></td>
	</tr>
	<tr>
		<th>功能点</th>
		<td><wcm:widget name="${param.pixel}.pri" cmd="pri[vo:实体]{required:true}" /></td>
	</tr>
</table>

<%-- 每个模块页面必须引入 --%>
<%@ include file="/include/html_bottom.jsp"%>