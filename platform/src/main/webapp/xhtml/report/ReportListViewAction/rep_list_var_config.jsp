<%@ page language="java" pageEncoding="UTF-8"%>

<%-- 每个模块页面必须引入 --%>
<%@ include file="/include/common.jsp"%>
<%@ include file="/include/html_head.jsp"%>
<c:set var="isCopy" value="${param.copy==1}" />
<script type="text/javascript">
	$(function() {
		var $zone = $('#${_zone}');

		$("[name$='.var']", $zone).blur(function() {
			var $this = $(this);
			var val = $(this).val();
			if (val != null && val != '') {
				$('a', $("li[aria-controls='" + $this.parents('div[tab]:first').attr('id') + "']", $this.parents('[tabs=true]:first'))).html(val);
			}
		});

		$('button[name=prepareExecAdd]', $zone).click(function() {
			var $tabs = $('#${_zone}_prepareExecs_tabs');
			Ajax.tab($tabs, '${_acp}/prepareExecsForm.shtml', {
				data : {
					type : 'prepareExecs',
					pixel : 'prepareExecs.B' + Core.nextSeq()
				}
			});
		});

	});
</script>

<input type="hidden" name="hasVars" value="true" />

<div class="ws-bar">
	<div class="ws-group left">
		<button icon="plus" type="button" name="prepareExecAdd" title="展示变量允许在界面展示中使用,包括展示字段,表单字段等." tip="true">新增变量</button>
	</div>
</div>

<div tabs="true" button="left" sort="y" id="${_zone}_prepareExecs_tabs">
	<c:if test="${prepareExecs!=null&&fn:length(prepareExecs)>0}">
		<c:forEach items="${prepareExecs}" var="vo" varStatus="status">
			<div title="${vo.var}" close="true">
				<c:set var="pixel" value="prepareExecs.A${status.index}" />
				<input type="hidden" name="prepareExecs" value="${pixel}" />
				<table class="ws-table">
					<tr>
						<th>变量名</th>
						<td><wcm:widget name="${pixel}.var" cmd="text{required:true}" value="${vo.var}"></wcm:widget></td>
					</tr>
					<tr>
						<th>脚本类型</th>
						<td><wcm:widget name="${pixel}.execType" cmd="select[@com.riversoft.platform.script.ScriptTypes]{required:true}" value="${vo.execType}"></wcm:widget></td>
					</tr>
					<tr>
						<th>脚本<br /> <font color="red" tip="true" title="vo:实体;mode:列表页=1;明细页=2;">(提示)</font></th>
						<td><wcm:widget name="${pixel}.execScript" cmd="codemirror[groovy]{required:true}" value="${vo.execScript}"></wcm:widget></td>
					</tr>
					<tr>
						<th>备注</th>
						<td><wcm:widget cmd="textarea" name="${pixel}.description" value="${vo.description}" /></td>
					</tr>
				</table>
			</div>
		</c:forEach>
	</c:if>
</div>
<%-- 每个模块页面必须引入 --%>
<%@ include file="/include/html_bottom.jsp"%>