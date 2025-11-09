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
		<th style="width: 120px;">选择</th>
		<th>数据分类</th>
		<th>展示名</th>
	</tr>
	<c:forEach items="${dp.list}" var="vo" varStatus="index">
		<tr>
			<td class="center ws-group">
				<button name="ok" type="button" value="select[${vo.dataType}(请选择)]">单选下拉</button>
				<button name="ok" type="button" value="radio[${vo.dataType}]">单选卡</button>
				<button name="ok" type="button" value="multiselect[${vo.dataType}]">多选下拉</button>
				<button name="ok" type="button" value="checkbox[${vo.dataType}]">多选卡</button>
				<button name="ok" type="button" value="tree[${vo.dataType}]">树形</button>
			</td>
			<td class="center">${vo.dataType}</td>
			<td class="center">${vo.busiName}</td>
		</tr>
	</c:forEach>
</table>

<wcm:page dp="${dp}" form="${_zone}_form"></wcm:page>


<%-- 每个模块页面必须引入 --%>
<%@ include file="/include/html_bottom.jsp"%>