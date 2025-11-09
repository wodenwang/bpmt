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

<%-- 数据准备 --%>
<c:set var="context" value="${wcm:map(null,'vo',vo)}" />
<c:set var="context" value="${wcm:map(context,'fo',fo)}" />

<c:set var="widgetEnableFlag" value="${btn.widgetEnableScript!=null?(wpf:script(btn.widgetEnableType,btn.widgetEnableScript,context)):true}" />
<c:set var="widgetVal" value="${btn.widgetValScript!=null?(wpf:script(btn.widgetValType,btn.widgetValScript,context)):null}" />
<c:set var="widgetParam" value="${btn.widgetParamScript!=null?(wpf:script(btn.widgetParamType,btn.widgetParamScript,context)):null}" />

<div class="ws-msg info" name="errorZone" id="${_zone}_error_zone">${wpf:lan("#:zh[是否将当前任务(转交/提交)给指定的用户？]:en[Will the current task transfer or submit to the user specified?]#")}</div>

<table class="ws-table">
	<tr>
		<th>${wpf:lan("#:zh[接收人]:en[Recipient]#")}</th>
		<td><wcm:widget name="_FORWARD_UID" cmd="${btn.widget}" value="${widgetVal}" state="${widgetEnableFlag?'normal':'readonly'}" params="${widgetParam}" /></td>
	</tr>
	<tr>
		<th>${wpf:lan("#:zh[表单选项]:en[Form options]#")}</th>
		<td><input type="radio" name="formFlag" value="1" checked="checked" /> <label>${wpf:lan("#:zh[包含填写内容]:en[Have fill out the content]#")}<font color="red" tip="true" title="将转发人录入的信息一并提交给接收人">${wpf:lan("#:zh[(默认)]:en[(Default)]#")}</font></label> <input type="radio"
			name="formFlag" value="0" /> <label>${wpf:lan("#:zh[不含填写内容]:en[Fill in the content]#")}<font color="red" tip="true" title="不带录入信息直接转交,转发人无需填写表单要求必填的内容">${wpf:lan("#:zh[(提示)]:en[(TIPS)]#")}</font></label></td>
	</tr>
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