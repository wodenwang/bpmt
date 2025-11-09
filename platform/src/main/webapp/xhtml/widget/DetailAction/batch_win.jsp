<%@ page language="java" pageEncoding="UTF-8"%>

<%-- 每个模块页面必须引入 --%>
<%@ include file="/include/common.jsp"%>
<%@ include file="/include/html_head.jsp"%>

<script type="text/javascript">
	$(function() {
		var $zone = $('#${_zone}');
		$('button[name=downloadTemplate]', $zone).click(function() {
			Ui.confirm('${wpf:lan("#:zh[确认下载]:en[Confirm Download]#")}?', function() {
				Ajax.download('${_acp}/downloadTemplate.shtml', {
					data : {
						widgetKey : '${param.widgetKey}'
					}
				});
			});
		});

		$('button[name=downloadData]', $zone).click(function() {
			Ui.confirm('${wpf:lan("#:zh[确认下载]:en[Confirm Download]#")}?', function() {
				Ajax.download('${_acp}/downloadData.shtml', {
					data : {
						widgetKey : '${param.widgetKey}',
						list : $('[name=list]', $zone).val()
					}
				});
			});
		});
	});
</script>

<div name="errorZone" id="${_zone}_error_zone"></div>

<textarea style="display: none;" name="list">${param.list}</textarea>
<form action="${_acp}/saveBatch.shtml" method="post" name="editForm">
	<table class="ws-table">
		<tr>
			<th>${wpf:lan("#:zh[下载]:en[Download]#")}</th>
			<td class="ws-group"><button icon="arrowthickstop-1-s" type="button" name="downloadTemplate">${wpf:lan("#:zh[模板下载]:en[Template download]#")}</button>
				<button icon="arrowthickstop-1-s" type="button" name="downloadData">${wpf:lan("#:zh[数据下载]:en[Data download]#")}</button></td>
		</tr>
		<tr>
			<th>${wpf:lan("#:zh[批量文件]:en[Batch file]#")}</th>
			<td><wcm:widget cmd="filemanager{required:true}" name="file" /></td>
		</tr>
	</table>
</form>

<%-- 每个模块页面必须引入 --%>
<%@ include file="/include/html_bottom.jsp"%>