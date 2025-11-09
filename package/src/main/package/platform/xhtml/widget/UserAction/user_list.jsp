<%@ page language="java" pageEncoding="UTF-8"%>

<%-- 每个模块页面必须引入 --%>
<%@ include file="/include/common.jsp"%>
<%@ include file="/include/html_head.jsp"%>

<script type="text/javascript">
	$(function() {
		var $zone = $('#${_zone}');

		var $selectedZone = $("#${_zone}_selected_zone", $zone);
		$.each($(':checkbox[name=uids]', $zone), function() {
			var $checkbox = $(this);
			var val = $checkbox.val();
			if ($('li[name=' + val + ']', $selectedZone).size() > 0) {
				$checkbox.click();
			}
		});

		if ('${param.checkType}' == 'radio') {//关闭多选
			$('button[name=check]', $zone).click(function() {
				var $this = $(this);
				var busiName = $this.parents('tr:first').attr("showName");
				Core.fn($zone, 'confirmFn')($zone, {
					val : $this.val(),
					busiName : busiName
				});
			});

			$.each($('li', $selectedZone), function() {
				var $this = $(this);
				var $btn = $("button[value='" + $this.attr('name') + "']", $zone);
				if ($btn.size() > 0) {
					$btn.parents('tr:first').find('td').css('color', 'blue');
				}
			});
		}
	});
</script>

<ul style="display: none;" id="${_zone}_selected_zone">
	<c:forEach items="${values}" var="v">
		<li name="${v}">${v}</li>
	</c:forEach>
</ul>
<table class="ws-table" form="${_form}">
	<tr>
		<th style="width: 60px;" check="${param.checkType!='radio'}"></th>
		<th field="uid">${cm.lan("#:zh[用户]:en[User]#")} ID</th>
		<th field="busiName">${cm.lan("#:zh[名称]:en[Name]#")}</th>
	</tr>
	<c:forEach items="${dp.list}" var="vo">
		<tr showName="${vo.busiName}">
			<c:choose>
				<c:when test="${param.checkType!='radio'}">
					<td class="center" check="true" value="${vo.uid}" checkname="uids"></td>
				</c:when>
				<c:otherwise>
					<td class="center">
						<button icon="radio-off" type="button" value="${vo.uid}" name="check" text="false">选中</button>
					</td>
				</c:otherwise>
			</c:choose>
			<td class="center">${vo.uid}</td>
			<td>${vo.busiName}</td>
		</tr>
	</c:forEach>
</table>

<wcm:page form="${_form}" dp="${dp}" />

<%-- 每个模块页面必须引入 --%>
<%@ include file="/include/html_bottom.jsp"%>