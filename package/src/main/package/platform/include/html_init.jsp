<%@ page language="java" pageEncoding="UTF-8"%>

<%-- 初始化当前区域 --%>
<script type="text/javascript">
	if ($) {
		$(function() {
			Core.init('${_zone}');
		});
	}
</script>
