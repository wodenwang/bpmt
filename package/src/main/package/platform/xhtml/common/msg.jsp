<%@ page language="java" pageEncoding="UTF-8"%>
<%@ include file="/include/common.jsp"%>
<%@ include file="/include/html_head.jsp"%>
<script type="text/javascript">
	$(function() {
		var $close = $('a.ui-corner-all', $('#${_zone}'));
		$close.click(function(event) {
			event.preventDefault();
			$('#${_zone}').fadeOut(500);
		});
		$close.hover(function() {
			$(this).toggleClass('ui-state-hover');
		});

		//10秒后自动淡出

		// 		setTimeout(function() {
		// 			$('#${_zone}').fadeOut(500);
		// 		}, 10000);

	});
</script>

<div class="ws-msg ${_msg_type}" style="position: relative;">
	${_msg} <a href="javascript:void(0);" class="ui-corner-all msgPage"><span class="ui-icon ui-icon-closethick">close</span></a>
</div>
<%@ include file="/include/html_bottom.jsp"%>