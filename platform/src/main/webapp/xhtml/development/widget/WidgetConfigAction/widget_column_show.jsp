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

		//处理checkbox
		if ('${vo.inWait}' == '1') {
			$("[name$='.inWait']", $zone).iCheck("check");
		}
		if ('${vo.inSelected}' == '1') {
			$("[name$='.inSelected']", $zone).iCheck("check");
		}
		if ('${vo.inResult}' == '1') {
			$("[name$='.inResult']", $zone).iCheck("check");
		}

	});
</script>

<input type="hidden" name="${pixel}.flag" value="true" />
<table class="ws-table">
	<tr>
		<th>展示名</th>
		<td><input type="text" name="${pixel}.busiName" class="{required:true}" value="${vo.busiName}" /></td>
	</tr>
	<tr>
		<th>字段位置</th>
		<td><input type="checkbox" value="1" name="${pixel}.inWait" /><label>待选</label><input type="checkbox" value="1" name="${pixel}.inSelected" /><label>已选</label><input type="checkbox" value="1"
			name="${pixel}.inResult" /><label>结果</label></td>
	</tr>
</table>
<div accordion="true" multi="true">
	<div title="展示">
		<table class="ws-table">
			<tr>
				<th>展示内容(脚本类型)</th>
				<td><wcm:widget name="${pixel}.contentType" cmd="select[@com.riversoft.platform.script.ScriptTypes]{required:true}" value="${vo.contentType}"></wcm:widget></td>
			</tr>
			<tr>
				<th>展示内容(脚本内容)<br /> <font color="red" tip="true" title="vo:实体;">(提示)</font></th>
				<td><wcm:widget name="${pixel}.contentScript" cmd="codemirror[groovy]{required:true}" value="${vo.contentScript}"></wcm:widget></td>
			</tr>
			<tr>
				<th>排序字段</th>
				<td><wcm:widget name="${pixel}.sortField" cmd="text" value="${vo.sortField}" /></td>
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
				<td><wcm:widget name="${pixel}.pri" cmd="pri{required:true}" value="${isCopy?null:vo.pri}"></wcm:widget></td>
			</tr>
		</table>
	</div>
</div>

<%-- 每个模块页面必须引入 --%>
<%@ include file="/include/html_bottom.jsp"%>