<%@ page language="java" pageEncoding="UTF-8"%>

<%-- 每个模块页面必须引入 --%>
<%@ include file="/include/common.jsp"%>
<%@ include file="/include/html_head.jsp"%>

<script type="text/javascript">
	$(function() {
		var $zone = $('#${_zone}');
		$("[name$='.name']", $zone).blur(function() {
			var $this = $(this);
			var val = $(this).val();
			if (val != null && val != '') {
				$('a', $("li[aria-controls='${_zone}']", $this.parents('[tabs=true]:first'))).html(val);
			}
		});
	});
</script>

<c:set var="pixel" value="${param.pixel}" />
<input type="hidden" name="homes" value="${pixel}" />
<table class="ws-table">
	<tr>
		<th>主键</th>
		<td><font color="red">(系统自动生成)</font></td>
	</tr>
	<tr>
		<th>所属子系统</th>
		<td><wcm:widget name="${pixel}.domainKey" cmd="select[$CmDomain(请选择);domainKey;busiName]{required:true}" value="${param.domainKey}" state="readonly"></wcm:widget></td>
	</tr>
	<tr>
		<th>标签名</th>
		<td><input type="text" name="${pixel}.name" class="{required:true}" /></td>
	</tr>
	<tr>
		<th>位置</th>
		<td>第<span name="columnIndex" style="padding-left: 5px; padding-right: 5px; color: red; font-weight: bold;">${1+param.columnIndex}</span>列 第<span name="sort"
			style="padding-left: 5px; padding-right: 5px; color: blue; font-weight: bold;">${1+param.sort}</span>行 <input type="hidden" name="${pixel}.columnIndex" value="${param.columnIndex}" /> <input
			type="hidden" name="${pixel}.sort" value="${param.sort}" /></td>
	</tr>
	<tr>
		<th>高度</th>
		<td><select class="chosen" name="${pixel}.height">
				<option value="">自动适应</option>
				<c:forEach items="${'300,400,500,600,700,800,900'.split(',')}" var="v">
					<option value="${v}">${v}</option>
				</c:forEach>
		</select></td>
	</tr>
	<tr>
		<th>链接视图</th>
		<td><wcm:widget name="${pixel}.action" cmd="view[HOME]{required:true}" /></td>
	</tr>
	<tr>
		<th>动态入参(脚本类型)</th>
		<td><wcm:widget name="${pixel}.paramType" cmd="select[@com.riversoft.platform.script.ScriptTypes]"></wcm:widget></td>
	</tr>
	<tr>
		<th>动态入参(脚本)<br /> <font color="red" tip="true" title="在视图模块中在request中通过[_params]命令字获取.">(提示)</font></th>
		<td><wcm:widget name="${pixel}.paramScript" cmd="codemirror[groovy]"></wcm:widget></td>
	</tr>
	<tr>
		<th>功能点</th>
		<td><wcm:widget name="${pixel}.pri" cmd="pri{required:true}"></wcm:widget></td>
	</tr>
</table>

<%-- 每个模块页面必须引入 --%>
<%@ include file="/include/html_bottom.jsp"%>