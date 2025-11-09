<%@ page language="java" pageEncoding="UTF-8"%>

<%-- 每个模块页面必须引入 --%>
<%@ include file="/include/common.jsp"%>
<%@ include file="/include/html_head.jsp"%>

<script type="text/javascript">
	$(function() {
		var $zone = $('#${_zone}');
		$('button[name=analyse]', $zone).click(
				function() {
					var file = $('[name=file]', $zone).val();
					Ajax.post('${_zone}_table_name_zone',
							'${_acp}/tableNameZone.shtml', {
								data : {
									file : file
								}
							});
				});

	});
</script>
<div name="msgZone" id="${_zone}_err_zone"></div>
<form aync="true" action="${_acp}/submitBatch.shtml" method="post">
	<table class="ws-table">
		<tr>
			<th>选择文件</th>
			<td><wcm:widget name="file" cmd="filemanager{required:true}"></wcm:widget>
			</td>
		</tr>
	</table>
	<div class="ws-bar">
		<button icon="lightbulb" type="button" name="analyse">分析文件</button>
	</div>
	<div id="${_zone}_table_name_zone"></div>
</form>


<%-- 每个模块页面必须引入 --%>
<%@ include file="/include/html_bottom.jsp"%>