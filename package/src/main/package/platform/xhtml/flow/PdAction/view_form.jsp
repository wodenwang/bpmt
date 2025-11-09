<%@ page language="java" pageEncoding="UTF-8"%>

<%-- 每个模块页面必须引入 --%>
<%@ include file="/include/common.jsp"%>
<%@ include file="/include/html_head.jsp"%>

<script type="text/javascript">
	$(function() {
		var $zone = $('#${_zone}');

		var initConfig = function() {
			Ajax.post('${_zone}_config', '${_cp}${action}?key=${vo.viewKey}');
		};

		$('form', $zone).submit(function() {
			var $form = $(this);
			Ajax.form('${_zone}_error', $form, {
				confirmMsg : '确认保存视图?',
				callback : function(flag) {
					if (flag) {
						initConfig();
					}
				}
			});
			return false;
		});

		initConfig();

	});
</script>

<%--错误提示区域 --%>
<div id="${_zone}_error"></div>

<%--表单 --%>
<form action="${_cp}${viewConfigAction}/submit.shtml" method="post"
	sync="true">
	<input type="hidden" name="viewKey" value="${vo.viewKey}" />
	<table class="ws-table">
		<tr>
			<th>视图主键(自动生成)</th>
			<td><font color="red">${vo.viewKey}</font></td>
		</tr>
		<tr>
			<th>描述</th>
			<td><wcm:widget name="description" cmd="textarea{required:true}"
					value="${vo.description}"></wcm:widget></td>
		</tr>
		<tr>
			<th>绑定模块</th>
			<td><input type="hidden" name="viewClass"
				value="${vo.viewClass}" /> <span
				style="color: red; font-weight: bold;">${module.description}</span>
			</td>
		</tr>
	</table>

	<div id="${_zone}_config"></div>

	<div class="ws-bar">
		<div class=" ws-group">
			<button type="submit" icon="disk" text="true">保存</button>
		</div>
	</div>
</form>

<%-- 每个模块页面必须引入 --%>
<%@ include file="/include/html_bottom.jsp"%>