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
		<th>字段名</th>
		<td><wcm:widget name="${pixel}.name" cmd="text{required:true}" value="${vo.name}"></wcm:widget></td>
	</tr>
	<tr>
		<th>展示名</th>
		<td><input type="text" name="${pixel}.busiName" class="{required:true}" value="${vo.busiName}" /></td>
	</tr>
</table>

<div accordion="true" multi="true">
	<div title="表单">
		<table class="ws-table">
			<tr>
				<th>绑定控件</th>
				<td><wcm:widget name="${pixel}.widget" cmd="widget{required:true}" value="${vo!=null?vo.widget:'text'}"></wcm:widget>
			</tr>
			<tr>
				<th>控件动态入参(脚本类型)</th>
				<td><wcm:widget name="${pixel}.widgetParamType" cmd="select[@com.riversoft.platform.script.ScriptTypes]" value="${vo.widgetParamType}"></wcm:widget></td>
			</tr>
			<tr>
				<th>控件动态入参(脚本)<br /> <font color="red" tip="true" title="vo:实体;返回字符串,在自定义控件中在request中通过[_params]命令字获取.">(提示)</font></th>
				<td><wcm:widget name="${pixel}.widgetParamScript" cmd="codemirror[groovy]" value="${vo.widgetParamScript}"></wcm:widget></td>
			</tr>
			<tr>
				<th>表单内容(脚本类型)</th>
				<td><wcm:widget name="${pixel}.contentType" cmd="select[@com.riversoft.platform.script.ScriptTypes]" value="${vo.contentType}"></wcm:widget></td>
			</tr>
			<tr>
				<th>表单内容(脚本内容)<br /> <font color="red" tip="true" title="vo:实体;">(提示)</font></th>
				<td><wcm:widget name="${pixel}.contentScript" cmd="codemirror[groovy]" value="${vo.contentScript}"></wcm:widget></td>
			</tr>
			<tr>
				<th>填写提示(脚本类型)</th>
				<td><wcm:widget name="${pixel}.tipType" cmd="select[@com.riversoft.platform.script.ScriptTypes]" value="${vo.tipType}"></wcm:widget></td>
			</tr>
			<tr>
				<th>填写提示(脚本)<br /> <font color="red" tip="true" title="vo:实体;">(提示)</font></th>
				<td><wcm:widget name="${pixel}.tipScript" cmd="codemirror[groovy]" value="${vo.tipScript}"></wcm:widget></td>
			</tr>
		</table>
	</div>
	<div title="处理器">
		<table class="ws-table">
			<tr>
				<th>字段数据处理器(脚本类型)</th>
				<td><wcm:widget name="${pixel}.execType" cmd="select[@com.riversoft.platform.script.ScriptTypes]{required:true}" value="${vo.execType}"></wcm:widget></td>
			</tr>
			<tr>
				<th>字段数据处理器(脚本)<br /> <font color="red" tip="true" title="返回当前字段的期望值.vo:实体;">(提示)</font></th>
				<td><wcm:widget name="${pixel}.execScript" cmd="codemirror[groovy]" value="${vo.execScript}"></wcm:widget></td>
			</tr>
		</table>
	</div>
	<div title="样式布局">
		<table class="ws-table">
			<tr>
				<th>单元格样式</th>
				<td><wcm:widget name="${pixel}.style" cmd="style" value="${vo.style}"></wcm:widget></td>
			</tr>
		</table>
	</div>
	<div title="权限">
		<table class="ws-table">
			<tr>
				<th>功能点(展示)</th>
				<td><wcm:widget name="${pixel}.pri" cmd="pri{required:true}" value="${vo.pri}"></wcm:widget></td>
			</tr>
			<tr>
				<th>功能点(编辑)</th>
				<td><wcm:widget name="${pixel}.editPri" cmd="pri{required:true}" value="${vo.editPri}"></wcm:widget></td>
			</tr>
		</table>
	</div>
</div>

<%-- 每个模块页面必须引入 --%>
<%@ include file="/include/html_bottom.jsp"%>