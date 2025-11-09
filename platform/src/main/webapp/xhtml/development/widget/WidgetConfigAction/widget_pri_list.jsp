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

<input type="hidden" name="groupId" value="${groupId}" />
<div accordion="true" multi="true">
	<div title="字段权限">
		<table class="ws-table">
			<tr>
				<th rowspan="2">字段</th>
				<th colspan="2">展示权限</th>
				<th colspan="2">编辑表单</th>
			</tr>
			<tr>
				<th colindex="field_show"><input type="checkbox" /></th>
				<th></th>
				<th colindex="field_edit"><input type="checkbox" /></th>
				<th></th>
			</tr>

			<c:forEach items="${fields}" var="field">
				<tr>
					<td class="center"><c:choose>
							<c:when test="${fn:endsWith(field['$type$'],'Form')}">
								<img src="${_cp}/css/icon/application_form.png" tip="true" title="表单字段" />
							</c:when>
							<c:when test="${fn:endsWith(field['$type$'],'Line')}">
								<img src="${_cp}/css/icon/bookmark.png" tip="true" title="分割线字段" />
							</c:when>
							<c:when test="${fn:endsWith(field['$type$'],'Show')}">
								<img src="${_cp}/css/icon/application.png" tip="true" title="展示字段" />
							</c:when>
							<c:otherwise>
								<img src="${_cp}/css/icon/table.png" tip="true" title="固定字段" />
							</c:otherwise>
						</c:choose>${field.busiName}</td>

					<td colindex="field_show"><c:if test="${!field.pri.scriptOnly}">
							<input type="checkbox" value="${field.pri.priKey}" name="priKey" checkstate="${wcm:contains(priKeys,field.pri.priKey)}" />
						</c:if></td>
					<td class="right"><wcm:widget name="relate" cmd="prigroup[${groupId};${field.pri.priKey}]" /></td>

					<td colindex="field_edit"><c:if test="${field.editPri != null && (!field.editPri.scriptOnly)}">
							<input type="checkbox" value="${field.editPri.priKey}" name="priKey" checkstate="${wcm:contains(priKeys,field.editPri.priKey)}" />
						</c:if></td>
					<td class="right"><c:if test="${field.editPri != null}">
							<wcm:widget name="relate" cmd="prigroup[${groupId};${field.editPri.priKey};vo:实体]" />
						</c:if></td>
				</tr>
			</c:forEach>
		</table>
	</div>
	<div title="数据约束">
		<table class="ws-table">
			<tr>
				<th></th>
				<th colindex="limit"><input type="checkbox" /></th>
				<th>特殊设置</th>
				<c:forEach items="${config.limits}" var="vo">
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