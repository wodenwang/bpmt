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
				<th>展示内容(脚本类型)</th>
				<td><wcm:widget name="${pixel}.contentType" cmd="select[@com.riversoft.platform.script.ScriptTypes]{required:true}" value="${vo.contentType}"></wcm:widget></td>
			</tr>
			<tr>
				<th>展示内容(脚本内容)<br /> <font color="red" tip="true" title="vo:实体;mode:列表页=1;明细页=2;导出时=4;">(提示)</font></th>
				<td><wcm:widget name="${pixel}.contentScript" cmd="codemirror[groovy]{required:true}" value="${vo.contentScript}"></wcm:widget></td>
			</tr>
			<tr>
				<th>排序字段</th>
				<td><wcm:widget name="${pixel}.sortField" cmd="text" value="${vo.sortField}" /></td>
			</tr>
		</table>
	</div>
	<div title="汇集" show="false">
		<table class="ws-table">
			<tr>
				<th>汇集内容(脚本类型)</th>
				<td><wcm:widget name="${pixel}.summaryContentType" cmd="select[@com.riversoft.platform.script.ScriptTypes]" value="${vo.summaryContentType}"></wcm:widget></td>
			</tr>
			<tr>
				<th>汇集内容(脚本内容)<br /> <font color="red" tip="true" title="list:当前页数据列表">(提示)</font></th>
				<td><wcm:widget name="${pixel}.summaryContentScript" cmd="codemirror[groovy]" value="${vo.summaryContentScript}"></wcm:widget></td>
			</tr>
		</table>
	</div>
	<div title="样式布局">
		<table class="ws-table">
			<tr>
				<th>单元格样式</th>
				<td><wcm:widget name="${pixel}.style" cmd="style" value="${vo.style}"></wcm:widget></td>
			</tr>
			<tr>
				<th>行展示模式</th>
				<td><wcm:widget name="${pixel}.whole" cmd="radio[@com.riversoft.platform.translate.TableLineMode]" value="${vo!=null?vo.whole:0}"></wcm:widget></td>
			</tr>
		</table>
	</div>
	<div title="权限" msg="mode!=1时上下文可使用vo.">
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