<%@ page language="java" pageEncoding="UTF-8"%>

<%-- 每个模块页面必须引入 --%>
<%@ include file="/include/common.jsp"%>
<%@ include file="/include/html_head.jsp"%>

<script type="text/javascript">
	$(function() {
		var $zone = $('#${_zone}');

		$('button[name=del]', $zone).click(function() {
			var $btn = $(this);
			$btn.parents('tr:first').remove();
		});

		//初始化选中状态
		Core.fn($zone, 'initState', function() {
			var $tr = $('tr[pk]', $zone);
			$tr.removeClass("ui-state-focus");
			$('td', $tr).removeClass("ui-state-focus");
		});

		//高亮对应选项
		Core.fn($zone, 'highlight', function($checkbox) {
			var pk = $checkbox.val();
			var state = $checkbox.prop('checked');
			var $tr = $('tr[pk="' + pk + '"]', $zone);

			if ($tr != null && $tr.size() > 0) {
				if (state) {
					$tr.addClass("ui-state-focus");
					$('td', $tr).addClass("ui-state-focus");
				} else {
					$tr.removeClass("ui-state-focus");
					$('td', $tr).removeClass("ui-state-focus");
				}
			}
		});

		//增加行
		Core.fn($zone, 'addTr', function($tr) {
			var pk = $tr.attr('pk');
			if ($('tr[pk="' + pk + '"]', $zone).size() > 0) {//存在则不处理
				return;
			}

			$('table.ws-table', $zone).append($tr);
		});

	});
</script>

<table class="ws-table">
	<tr>
		<c:if test="${allowDelete}">
			<th style="width: 3em;">${wpf:lan("#:zh[操作]:en[Operation]#")}</th>
		</c:if>
		<c:forEach items="${fields}" var="field">
			<c:if test="${wpf:check(field.pri)}">
				<th>${wpf:lan(field.busiName)}<c:if test="${field.tipScript!=null&&field.tipScript!=''}">
						<span style="color: red; font-weight: bold; cursor: help;" tip="true" title="${wpf:script(field.tipType,field.tipScript,context)}">(${wpf:lan("#:zh[提示]:en[TIPS]#")})</span>
					</c:if></th>
			</c:if>
		</c:forEach>
	</tr>
	<c:forEach items="${list}" var="vo" varStatus="status">
		<c:set var="context" value="${wcm:map(null,'vo',vo)}" />
		<c:set var="pixel" value="${wpf:script(detail.pkType,detail.pkScript,context)}" />
		<tr pk="${pixel}">
			<td style="display: none;"><textarea name="pk">${pixel}</textarea> <textarea name="${pixel}.vo">${wcm:json(vo)}</textarea></td>
			<c:if test="${allowDelete}">
				<td class="center"><button icon="trash" type="button" text="false" name="del">${wpf:lan("#:zh[删除]:en[Delete]#")}</button></td>
			</c:if>
			<c:forEach items="${fields}" var="field">
				<c:if test="${wpf:check(field.pri)}">
					<c:choose>
						<c:when test="${field.name!=null}">
							<c:set var="editDecideResult" value="${wpf:checkExt(field.editPri,context)}" />
							<td class="center" style="${wcm:widget('style[height]',field.style)}"><wcm:widget name="${pixel}.${field.name}" cmd="${field.widget}"
									value="${wpf:script(field.contentType,field.contentScript,context)}" state="${editDecideResult?'normal':'readonly'}"
									params="${field.widgetParamScript!=null?(wpf:script(field.widgetParamType,field.widgetParamScript,context)):null}" /></td>
						</c:when>
						<c:otherwise>
							<td class="center" style="${wcm:widget('style[height]',field.style)}"><wpf:script script="${field.contentScript}" type="${field.contentType}" context="${context}" /></td>
						</c:otherwise>
					</c:choose>
				</c:if>
			</c:forEach>
		</tr>
	</c:forEach>
</table>

<%-- 每个模块页面必须引入 --%>
<%@ include file="/include/html_bottom.jsp"%>