<%@ page language="java" pageEncoding="UTF-8"%>
<%@ include file="/include/common.jsp"%>

<script>
	$(function(){
		var $zone = $('#${_zone}');
		var $form = $('#${_form}');
		var _mode = '${_mode}';

		//注册控件
		Widget.initAll($form);
		
		${value}
	});
</script>
