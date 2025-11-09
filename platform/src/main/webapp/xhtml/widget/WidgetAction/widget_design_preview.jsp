<%@ page language="java" pageEncoding="UTF-8"%>

<%-- 每个模块页面必须引入 --%>
<%@ include file="/include/common.jsp"%>
<%@ include file="/include/html_head.jsp"%>

<style type="text/css">
table.ui-styled-table.doc tbody th {
	width: auto !important;
}
</style>

<script type="text/javascript">
	$(function() {
		var $zone = $('#${_zone}');
		var $apiZone = $('#${_zone}_api');
		$.each($('textarea', $apiZone), function() {
			$(this).attr('name', '_test_');
			$(this).prop('readonly', true);
			$(this).css('width', '90%');
			$(this).css('height', '50px');
			$(this).css('overflow', 'auto');
		});

		$('table.ws-table', $apiZone).addClass('doc');
		$('th:eq(0)', $apiZone).width(100);
		$('tr>th:eq(1)', $apiZone).width(150);
	});
</script>

<div tabs="true" style="margin-top: 5px;">
	<div title="效果预览">
		<span style="margin-right: 5px; color: red;">效果:</span>
		<wcm:widget name="_test_" cmd="${cmd}"></wcm:widget>
	</div>
	<div title="控件说明" id="${_zone}_api">
		<c:choose>
			<c:when test="${doc!=null&&doc!=''}">${doc}</c:when>
			<c:otherwise>
				<div class="ws-msg warning">此控件无在线API,请查阅离线文档.</div>
			</c:otherwise>
		</c:choose>
	</div>
</div>

<%-- 每个模块页面必须引入 --%>
<%@ include file="/include/html_bottom.jsp"%>