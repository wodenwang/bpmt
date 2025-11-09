<%@ page language="java" pageEncoding="UTF-8"%>

<%-- 每个模块页面必须引入 --%>
<%@ include file="/include/common.jsp"%>
<%@ include file="/include/html_head.jsp"%>

<script type="text/javascript">
	$(function() {
		var $zone = $('#${_zone}');
		$.each($('td[colindex] :checkbox', $zone), function() {
			var $checkbox = $(this);
			var $td = $checkbox.parent();
			$td.addClass("ws-checkbox");

			$checkbox.on('change', function() {
				if ($(this).prop("checked")) {
					$td.addClass("ui-state-focus");
					$td.next().addClass("ui-state-focus");
				} else {
					$td.removeClass("ui-state-focus");
					$td.next().removeClass("ui-state-focus");
				}
			});
			var checkstate = $checkbox.attr('checkstate');
			if (checkstate != undefined && checkstate == 'true') {
				$checkbox.click();
			}
		});

		$.each($('th[colindex] :checkbox', $zone), function() {
			var $checkbox = $(this);
			var $th = $checkbox.parent();
			$th.addClass("ws-checkbox");

			var colindex = $th.attr('colindex');
			$checkbox.click(function() {
				if ($(this).prop("checked")) {
					$("td[colindex='" + colindex + "'] input:unchecked", $zone).click();
				} else {
					$("td[colindex='" + colindex + "'] input:checked", $zone).click();
				}
			});
		});
	});
</script>

<input type="hidden" name="groupId" value="${param.groupId}" />

<div accordion="true" multi="true">
	<div title="其他">
		<table class="ws-table">
			<tr>
				<th colspan="4">数据筛选权限</th>
			</tr>
			<tr>
				<th></th>
				<th colindex="limit"><input type="checkbox" /></th>
				<th>特殊设置</th>
			</tr>
			<c:forEach items="${table.limits}" var="vo">
				<tr>
					<th>${vo.description}</th>
					<td colindex="limit"><c:if test="${!vo.pri.scriptOnly}">
							<input type="checkbox" value="${vo.pri.priKey}" name="priKey" checkstate="${wcm:contains(priKeys,vo.pri.priKey)}" />
						</c:if></td>
					<td class="right"><wcm:widget name="relate" cmd="prigroup[${groupId};${vo.pri.priKey}]"></wcm:widget></td>
				</tr>
			</c:forEach>
		</table>
	</div>
</div>

<%-- 每个模块页面必须引入 --%>
<%@ include file="/include/html_bottom.jsp"%>