<%@ page language="java" pageEncoding="UTF-8"%>

<%-- 每个模块页面必须引入 --%>
<%@ include file="/include/common.jsp"%>
<%@ include file="/include/html_head.jsp"%>

<script type="text/javascript">
	$(function() {
		var $zone = $('#${_zone}');

		//高亮所选
		$(':checkbox[name=pk]', $zone).click(function() {
			var $this = $(this);
			Core.fn($zone, 'highlight')($this);
		});

		//批量选中
		$('button[name=select]', $zone).click(function() {
			Core.fn($zone, 'submitSelect')($('#${_zone}_select_form'));
		});

	});
</script>

<form action="${_acp}/submitSelect.shtml" method="post" sync="true" id="${_zone}_select_form">
	<%--数据表格 --%>
	<table class="ws-table" form="${_form}">
		<thead>
			<tr>
				<th check="true"></th>
				<c:forEach items="${fields}" var="field">
					<c:if test="${wpf:check(field.pri)}">
						<th field="${field.sortField}">${wpf:lan(field.busiName)}</th>
					</c:if>
				</c:forEach>
			</tr>
		</thead>
		<tbody>
			<c:forEach items="${dp.list}" var="vo">
				<%-- 数据准备 --%>
				<c:set var="context" value="${wcm:map(null,'vo',vo)}" />
				<c:set var="pixel" value="${wpf:script(detail.pkType,detail.pkScript,context)}" />
				<tr>
					<td style="display: none;"><textarea name="${pixel}.vo">${wcm:json(vo)}</textarea></td>
					<td check="true" value="${pixel}" checkname="pk"></td>
					<c:forEach items="${fields}" var="field">
						<c:if test="${wpf:check(field.pri)}">
							<td class="center" style="${wcm:widget('style[height]',field.style)}"><wpf:script script="${field.contentScript}" type="${field.contentType}" context="${context}" /></td>
						</c:if>
					</c:forEach>
				</tr>
			</c:forEach>
			<tr>
				<th class="ws-bar left">
					<button icon="check" text="true" type="button" name="select">${wpf:lan("#:zh[选中]:en[Choose]#")}</button>
				</th>
			</tr>
		</tbody>
	</table>
</form>

<%-- 分页  --%>
<wcm:page dp="${dp}" form="${_form}" />

<%-- 每个模块页面必须引入 --%>
<%@ include file="/include/html_bottom.jsp"%>