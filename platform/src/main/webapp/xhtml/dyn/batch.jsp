<%@ page language="java" pageEncoding="UTF-8"%>

<%-- 每个模块页面必须引入 --%>
<%@ include file="/include/common.jsp"%>
<%@ include file="/include/html_head.jsp"%>

<script type="text/javascript">
	$(function() {
		var $zone = $('#${_zone}');
	});
</script>

<%--错误提示区域 --%>
<div id="${_zone}_error_zone"></div>

<form action="${_acp}/submitBatch.shtml" method="post" option="{errorZone:'${_zone}_error_zone',confirmMsg:'确认执行批处理？',loading:true}">
	<table class="ws-table">
		<tr>
			<th>${wpf:lan("#:zh[模板下载]:en[Template Download]#")}</th>
			<td><a href="#" onclick="Ajax.download('${_acp}/getBatchFile.shtml');">[${wpf:lan("#:zh[点击下载]:en[Click the Download]#")}]</a></td>
		</tr>
		<tr>
			<th>${wpf:lan("#:zh[批处理类型]:en[Batch type]#")}</th>
			<td><input type="radio" name="type" value="1" checked="checked" /><label>${wpf:lan("#:zh[新增]:en[Add]#")}</label> <input type="radio" name="type" value="2" /><label>${wpf:lan("#:zh[修改]:en[Alter]#")}</label></td>
		</tr>
		<tr>
			<th>${wpf:lan("#:zh[数据文件]:en[Data file]#")}</th>
			<td><wcm:widget name="file" cmd="filemanager{required:true}" /></td>
		</tr>
	</table>
</form>

<%-- 每个模块页面必须引入 --%>
<%@ include file="/include/html_bottom.jsp"%>