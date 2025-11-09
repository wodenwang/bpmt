<%@ page language="java" pageEncoding="UTF-8"%>

<%-- 每个模块页面必须引入 --%>
<%@ include file="/include/common.jsp"%>
<%@ include file="/include/html_head.jsp"%>

<script type="text/javascript">
	$(function() {
		var $zone = $('#${_zone}');

		$('button[name=ok]', $zone).click(function() {
			var val = $(this).val();
			Core.fn($zone, 'callback')(val);
		});

	});
</script>

<table class="ws-table">
	<tr>
		<th>选择</th>
		<th>调用KEY</th>
		<th>展示名</th>
		<th>描述</th>
	</tr>
	<c:forEach items="${dp.list}" var="vo" varStatus="index">
		<tr>
			<td class="center ws-group"><c:forEach items="${widgets}" var="widget">
					<button name="ok" type="button" value="${widget.name}[${vo.widgetKey}]">${widget.description}</button>
				</c:forEach></td>
			<td class="center">${vo.widgetKey}</td>
			<td class="center">${vo.busiName}</td>
			<td class="center">${vo.description}</td>
		</tr>
	</c:forEach>
</table>
<wcm:page dp="${dp}" form="${_zone}_form"></wcm:page>

<%-- 每个模块页面必须引入 --%>
<%@ include file="/include/html_bottom.jsp"%>