<%@ page language="java" pageEncoding="UTF-8"%>

<%-- 每个模块页面必须引入 --%>
<%@ include file="/include/common.jsp"%>
<%@ include file="/include/html_head.jsp"%>

<script type="text/javascript">
	$(function() {
		var $zone = $('#${_zone}');

		$('button[name=foreignAdd]', $zone).click(function() {
			var $tabs = $('#${_zone}_foreign_tabs');
			Ajax.tab($tabs, '${_acp}/parentViewForeignOneConfig.shtml', {
				data : {
					tableName : '${param.tableName}',
					parentTableName : '${param.parentTableName}',
					type : '${param.type}',
					pixel : '${param.pixel}'
				}
			});
		});

	});
</script>

<div class="ws-bar">
	<div class="ws-group left">
		<button icon="plus" type="button" name="foreignAdd">新增外键</button>
	</div>
</div>

<div id="${_zone}_foreign_tabs" tabs="true" button="left"></div>


<%-- 每个模块页面必须引入 --%>
<%@ include file="/include/html_bottom.jsp"%>