<%@ page language="java" pageEncoding="UTF-8"%>

<%-- 每个模块页面必须引入 --%>
<%@ include file="/include/common.jsp"%>
<%@ include file="/include/html_head.jsp"%>

<script type="text/javascript">
	$(function() {
		var $zone = $('#${_zone}');

		//初始化
		Ajax.post('${_zone}_main', '${_acp}/main.shtml', {
			data : {
				_params : $('#${_zone}_params').val(),
				_main : '${_zone}',
				type : '${param.type}'
			}
		});
	});
</script>

<div tabs="true" main="true" id="${_zone}_tab">
	<div title="${wpf:lan(title)}" id="${_zone}_main">
		<textarea style="display: none;" name="_params" id="${_zone}_params">${_params}</textarea>
	</div>
</div>

<%-- 每个模块页面必须引入 --%>
<%@ include file="/include/html_bottom.jsp"%>