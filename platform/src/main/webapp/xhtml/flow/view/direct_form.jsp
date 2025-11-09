<%@ page language="java" pageEncoding="UTF-8"%>

<%-- 每个模块页面必须引入 --%>
<%@ include file="/include/common.jsp"%>
<%@ include file="/include/html_head.jsp"%>

<script type="text/javascript">
	$(function() {
		var $zone = $('#${_zone}');

		//表单提交
		var autoSubmit = function() {
			var $form = $('form', $zone);
			Ajax.form('${_zone}_error', $form, {
				dataType : 'json',
				successFn : function(returnObj) {
					//回调
					var fn = Core.fn($zone, 'callback');
					if ($.isFunction(fn)) {
						fn();
					}

					Ajax.post($zone, _cp + returnObj.url, {
						errorZone : '${_zone}_error'
					});
				}
			});
		};

		autoSubmit();
	});
</script>

<%--错误提示区域 --%>
<div id="${_zone}_error"></div>

<%--表单 --%>
<form action="${_acp}/submit.shtml" method="post" sync="true">
	<textarea style="display: none;" name="_FO">${wcm:json(fo)}</textarea>
	<input type="hidden" name="_INIT" value="1" />
	<%-- 订单系统内置字段,不需要表单传递值 --%>
	<c:forEach items="${ordKeys}" var="ordKey">
		<input type="hidden" name="${ordKey.name}_" value="true">
	</c:forEach>
	<textarea style="display: none;" name="_params">${param._params}</textarea>
	<c:if test="${json!=null}">
		<c:forEach items="${json}" var="entry">
			<textarea style="display: none;" name="${entry.key}">${entry.value}</textarea>
		</c:forEach>
	</c:if>
</form>

<%-- 每个模块页面必须引入 --%>
<%@ include file="/include/html_bottom.jsp"%>