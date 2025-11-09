<%@ page language="java" pageEncoding="UTF-8"%>

<%-- 每个模块页面必须引入 --%>
<%@ include file="/include/common.jsp"%>
<%@ include file="/include/html_head.jsp"%>

<c:set var="pixel" value="${param.pixel}" />

<script type="text/javascript">
	$(function() {
		var $zone = $('#${_zone}');
		$('input[name$=".busiName"]', $zone).blur(function() {
			var val = $(this).val();
			if (val != null && val != '') {
				var ztree = $.fn.zTree.getZTreeObj("${param.treeId}");
				var node = ztree.getSelectedNodes();
				if (node.length > 0) {
					node[0].busiName = val;
					ztree.refresh();
					ztree.selectNode(node[0]);
				}
			}
		});
	});
</script>

<input type="hidden" name="${pixel}.flag" value="true" />
<table class="ws-table">
	<tr>
		<th>展示名</th>
		<td><input type="text" name="${pixel}.busiName" class="{required:true}" value="${vo.busiName}" /></td>
	</tr>
</table>

<div accordion="true" multi="true">
	<div title="展示">
		<table class="ws-table">
			<tr>
				<th>提示(脚本类型)</th>
				<td><wcm:widget name="${pixel}.tipType" cmd="select[@com.riversoft.platform.script.ScriptTypes]" value="${vo.tipType}"></wcm:widget></td>
			</tr>
			<tr>
				<th>提示(脚本)<br /> <font color="red" tip="true" title="新增时有效,无法使用vo.">(提示)</font></th>
				<td><wcm:widget name="${pixel}.tipScript" cmd="codemirror[groovy]" value="${vo.tipScript}"></wcm:widget></td>
			</tr>
			<tr>
				<th>默认展开</th>
				<td><wcm:widget name="${pixel}.expandFlag" cmd="radio[YES_NO]" value="${vo.expandFlag}" /></td>
			</tr>
		</table>
	</div>
	<div title="条件">
		<table class="ws-table">
			<tr>
				<th>展示条件(脚本类型)</th>
				<td><wcm:widget name="${pixel}.decideType" cmd="select[@com.riversoft.platform.script.ScriptTypes]{required:true}" value="${vo!=null?vo.decideType:1}"></wcm:widget></td>
			</tr>
			<tr>
				<th>展示条件(脚本)<br /> <font color="red" tip="true" title="新增时有效,无法使用vo.">(提示)</font></th>
				<td><wcm:widget name="${pixel}.decideScript" cmd="codemirror[groovy]{required:true}" value="${vo!=null?vo.decideScript:'return true;'}"></wcm:widget></td>
			</tr>
		</table>
	</div>
</div>

<%-- 每个模块页面必须引入 --%>
<%@ include file="/include/html_bottom.jsp"%>