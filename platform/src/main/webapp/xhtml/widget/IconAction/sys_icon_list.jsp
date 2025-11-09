<%@ page language="java" pageEncoding="UTF-8"%>

<%-- 每个模块页面必须引入 --%>
<%@ include file="/include/common.jsp"%>
<%@ include file="/include/html_head.jsp"%>

<script type="text/javascript">
	$(function() {
		var $zone = $('#${_zone}');
		$('ul.icon_ul_sys li', $zone).click(function() {
			var val = $(this).attr('iconName');
			if (val != undefined) {
				Core.fn($zone, 'callback')(val);
			}
		});
	});
</script>

<style type="text/css">
ul.icon_ul_sys {
	margin: 0;
	padding: 0;
}

ul.icon_ul_sys>li {
	cursor: pointer;
	display: inline-block;
	margin: 2px;
	padding: 4px 0;
}
</style>

<c:forEach var="item" items="${map}">
	<ul class="icon_ul_sys">
		<li style="font-weight: bold; color: blue;">${item.key}</li>
		<c:forEach items="${item.value}" var="file">
			<c:if test="${!file.directory}">
				<li iconName="${file.name}"><img src="${iconCp}/${file.name}" style="width: 16px; height: 16px; border-width: 0px;" title="${file.name}" /></li>
			</c:if>
		</c:forEach>
	</ul>
</c:forEach>

<%-- 每个模块页面必须引入 --%>
<%@ include file="/include/html_bottom.jsp"%>