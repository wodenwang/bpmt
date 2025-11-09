<%@ page language="java" pageEncoding="UTF-8"%>

<%-- 每个模块页面必须引入 --%>
<%@ include file="/include/common.jsp"%>
<%@ include file="/include/html_head.jsp"%>
<c:set var="isCopy" value="${param.copy==1}" />

<script type="text/javascript">
	$(function() {
		var $zone = $('#${_zone}');
		$("[name$='.description']", $zone).blur(
				function() {
					var $this = $(this);
					var val = $(this).val();
					if (val != null && val != '') {
						$(
								'a',
								$("li[aria-controls='" + $this.parents('div[tab]:first').attr('id') + "']", $this
										.parents('[tabs=true]:first'))).html(val);
					}
				});

		$('button[name=limitAdd]', $zone).click(function() {
			var $tabs = $('#${_zone}_limits_tabs');
			Ajax.tab($tabs, '${_acp}/limitForm.shtml', {
				data : {
					type : 'limits',
					pixel : 'limits.B' + Core.nextSeq()
				}
			});
		});
	});
</script>

<input type="hidden" name="hasLimits" value="true" />
<div class="ws-bar">
	<div class="ws-group left">
		<button icon="plus" type="button" name="limitAdd">新增筛选</button>
	</div>
</div>

<div tabs="true" button="left" sort="y" id="${_zone}_limits_tabs">
	<c:if test="${fn:length(config.limits)>0}">
		<c:forEach items="${config.limits}" var="vo" varStatus="status">
			<div title="${vo.description}" close="true">
				<c:set var="pixel" value="limits.A${status.index}" />
				<input type="hidden" name="limits" value="${pixel}" />
				<table class="ws-table">
					<tr>
						<th>SQL片段(脚本类型)</th>
						<td><wcm:widget name="${pixel}.sqlType"
								cmd="select[@com.riversoft.platform.script.ScriptTypes]{required:true}"
								value="${vo.sqlType}"></wcm:widget></td>
					</tr>
					<tr>
						<th>SQL片段(脚本)<br /> <font color="red" tip="true"
							title="query:查询条件.">(提示)</font></th>
						<td><wcm:widget name="${pixel}.sqlScript"
								cmd="codemirror[groovy]{required:true}"
								value="${vo.sqlScript}"></wcm:widget></td>
					</tr>
					<tr>
						<th>备注</th>
						<td><wcm:widget name="${pixel}.description" cmd="textarea"
								value="${vo.description}"></wcm:widget></td>
					</tr>
					<tr>
						<th>功能点</th>
						<td><wcm:widget name="${pixel}.pri"
								cmd="pri{required:true}" value="${isCopy?null:vo.pri}"></wcm:widget></td>
					</tr>
				</table>
			</div>
		</c:forEach>
	</c:if>
</div>

<%-- 每个模块页面必须引入 --%>
<%@ include file="/include/html_bottom.jsp"%>