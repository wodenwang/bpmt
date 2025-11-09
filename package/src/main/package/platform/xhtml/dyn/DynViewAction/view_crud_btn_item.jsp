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
		<th>图标</th>
		<td><wcm:widget name="${pixel}.icon" cmd="icon{required:true}" value="document"></wcm:widget></td>
	</tr>
	<tr>
		<th>打开类型</th>
		<td><wcm:widget name="${pixel}.openType" cmd="radio[@com.riversoft.platform.translate.BtnOpenType]"></wcm:widget></td>
	</tr>
	<tr>
		<th>绑定视图</th>
		<td><wcm:widget name="${pixel}.action" cmd="view[BTN]{required:true}"></wcm:widget></td>
	</tr>
	<tr>
		<th>动态参数(脚本类型)</th>
		<td><wcm:widget name="${pixel}.paramType" cmd="select[@com.riversoft.platform.script.ScriptTypes]"></wcm:widget></td>
	</tr>
	<tr>
		<th>动态参数(脚本)<font color="red" tip="true" title="vo:实体;在request中通过[_params]命令字获取.">(提示)</font></th>
		<td><wcm:widget name="${pixel}.paramScript" cmd="codemirror[groovy]"></wcm:widget></td>
	</tr>
	<tr>
		<th>提示确认信息<br /> <font color="red" tip="true" title="无需确认框则留空.">(提示)</font></th>
		<td><wcm:widget name="${pixel}.confirmMsg" cmd="textarea"></wcm:widget></td>
	</tr>
	<tr>
		<th>备注</th>
		<td><wcm:widget name="${pixel}.description" cmd="textarea"></wcm:widget></td>
	</tr>
	<tr>
		<th>功能点</th>
		<td><wcm:widget name="${pixel}.pri" cmd="pri[vo:实体]{required:true}"></wcm:widget></td>
	</tr>
</table>
<%-- 每个模块页面必须引入 --%>
<%@ include file="/include/html_bottom.jsp"%>