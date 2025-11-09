<%@ page language="java" pageEncoding="UTF-8"%>

<%-- 每个模块页面必须引入 --%>
<%@ include file="/include/common.jsp"%>
<%@ include file="/include/html_head.jsp"%>

<script type="text/javascript">
	$(function() {
		var $zone = $('#${_zone}');
		$(':checkbox[name=code]', $zone).change(function() {
			var $this = $(this);
			if ($this.prop("checked")) {//若选中
				var $unselected = $(':checkbox[name=code]', $zone).not($this);
				$unselected.parents('tr').removeClass("ui-state-focus");
				$('td', $unselected.parents('tr')).removeClass("ui-state-focus");
				$unselected.prop("checked", false);
			}
		});

		$('th[check=true] :checkbox', $zone).prop("disabled", true);

	});
</script>

<div class="ws-scroll">
	<%--数据表格 --%>
	<table class="ws-table" form="${_form}">
		<thead>
			<tr>
				<th check="true"></th>
				<c:forEach items="${config.showColumns}" var="field">
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
				<c:set var="code" value="${wpf:script(combo.codeType,combo.codeScript,context)}" />
				<tr>
					<td check="true" value="${code}" checkname="code"></td>
					<c:forEach items="${config.showColumns}" var="field">
						<c:if test="${wpf:check(field.pri)}">
							<td class="center" style="${wcm:widget('style[height]',field.style)}"><wpf:script script="${field.contentScript}" type="${field.contentType}" context="${context}" /></td>
						</c:if>
					</c:forEach>
				</tr>
			</c:forEach>
		</tbody>
	</table>
</div>

<%-- 分页  --%>
<wcm:page dp="${dp}" form="${_form}" />

<%-- 每个模块页面必须引入 --%>
<%@ include file="/include/html_bottom.jsp"%>