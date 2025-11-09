<%@ page language="java" pageEncoding="UTF-8"%>

<%-- 每个模块页面必须引入 --%>
<%@ include file="/include/common.jsp"%>
<%@ include file="/include/html_head.jsp"%>

<script type="text/javascript">
	$(function() {
		var $zone = $('#${_zone}');
		var $select = $('select', $zone);
		if ($select.size() > 0) {
			$select.chosen({
				no_results_text : "${wpf:lan('#:zh[没有匹配结果]:en[No matching results]#')}",
				placeholder_text : "${wpf:lan('#:zh[请选择]:en[Please select]#')}"
			});

			$select.change(function() {
				var msg = $(this).val();
				if (msg != null && msg != '') {
					$('[name=_OPINION]', $zone).val(msg);
				}
			});
		}

	});
</script>

<div class="ws-msg info">${param.confirmMsg}</div>

<table class="ws-table">
	<c:if test="${opinions!=null}">
		<tr>
			<th>${wpf:lan("#:zh[常用意见]:en[Common opinions]#")}</th>
			<td><select style="width: 95%;">
					<option value="">--${wpf:lan("#:zh[快速选择]:en[Quick select]#")}--</option>
					<c:forEach items="${opinions}" var="msg">
						<option value="${msg}">${msg}</option>
					</c:forEach>
			</select></td>
		</tr>
	</c:if>
	<tr>
		<th>${wpf:lan("#:zh[流程意见]:en[Process the opinion]#")}</th>
		<td><wcm:widget name="_OPINION" cmd="textarea[95%;200px]" value="${wpf:lan(btn.busiName)}" /></td>
	</tr>
</table>

<%-- 每个模块页面必须引入 --%>
<%@ include file="/include/html_bottom.jsp"%>