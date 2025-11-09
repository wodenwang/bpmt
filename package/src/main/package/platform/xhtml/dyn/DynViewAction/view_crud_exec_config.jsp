<%@ page language="java" pageEncoding="UTF-8"%>

<%-- 每个模块页面必须引入 --%>
<%@ include file="/include/common.jsp"%>
<%@ include file="/include/html_head.jsp"%>
<c:set var="isCopy" value="${param.copy==1}" />
<script type="text/javascript">
	$(function() {
		var $zone = $('#${_zone}');

		$("#${_zone}_beforeExecs_tabs [name$='.description'],#${_zone}_afterExecs_tabs [name$='.description']", $zone).blur(function() {
			var $this = $(this);
			var val = $(this).val();
			if (val != null && val != '') {
				$('a', $("li[aria-controls='" + $this.parents('div[tab]:first').attr('id') + "']", $this.parents('[tabs=true]:first'))).html(val);
			}
		});

		$('button[name=beforeExecAdd]', $zone).click(function() {
			var $tabs = $('#${_zone}_beforeExecs_tabs');
			Ajax.tab($tabs, '${_acp}/beforeExecsForm.shtml', {
				data : {
					type : 'beforeExecs',
					pixel : 'beforeExecs.B' + Core.nextSeq()
				}
			});
		});

		$('button[name=afterExecAdd]', $zone).click(function() {
			var $tabs = $('#${_zone}_afterExecs_tabs');
			Ajax.tab($tabs, '${_acp}/afterExecsForm.shtml', {
				data : {
					type : 'afterExecs',
					pixel : 'afterExecs.B' + Core.nextSeq()
				}
			});
		});

	});
</script>

<input type="hidden" name="hasExecs" value="true" />
<div tabs="true">
	<div title="前置处理器">
		<div class="ws-bar">
			<div class="ws-group left">
				<button icon="plus" type="button" name="beforeExecAdd">新增处理器</button>
			</div>
		</div>
		<div tabs="true" button="left" sort="y" id="${_zone}_beforeExecs_tabs">
			<c:if test="${beforeExecs!=null&&fn:length(beforeExecs)>0}">
				<c:forEach items="${beforeExecs}" var="vo" varStatus="status">
					<div title="${vo.description}" close="true">
						<c:set var="pixel" value="beforeExecs.A${status.index}" />
						<input type="hidden" name="beforeExecs" value="${pixel}" />
						<table class="ws-table">
							<tr>
								<th>脚本类型</th>
								<td><wcm:widget name="${pixel}.execType" cmd="select[@com.riversoft.platform.script.ScriptTypes]{required:true}" value="${vo.execType}"></wcm:widget></td>
							</tr>
							<tr>
								<th>脚本<br /> <font color="red" tip="true" title="vo:实体;mode:新增时=1;修改时=2;删除时=3;">(提示)</font></th>
								<td><wcm:widget name="${pixel}.execScript" cmd="codemirror[groovy]{required:true}" value="${vo.execScript}"></wcm:widget></td>
							</tr>
							<tr>
								<th>备注</th>
								<td><wcm:widget cmd="textarea{required:true}" name="${pixel}.description" value="${vo.description}" /></td>
							</tr>
						</table>
					</div>
				</c:forEach>
			</c:if>
		</div>
	</div>
	<div title="后置处理器">
		<div class="ws-bar">
			<div class="ws-group left">
				<button icon="plus" type="button" name="afterExecAdd">新增处理器</button>
			</div>
		</div>
		<div tabs="true" button="left" sort="y" id="${_zone}_afterExecs_tabs">
			<c:if test="${afterExecs!=null&&fn:length(afterExecs)>0}">
				<c:forEach items="${afterExecs}" var="vo" varStatus="status">
					<div title="${vo.description}" close="true">
						<c:set var="pixel" value="afterExecs.A${status.index}" />
						<input type="hidden" name="afterExecs" value="${pixel}" />
						<table class="ws-table">
							<tr>
								<th>脚本类型</th>
								<td><wcm:widget name="${pixel}.execType" cmd="select[@com.riversoft.platform.script.ScriptTypes]{required:true}" value="${vo.execType}"></wcm:widget></td>
							</tr>
							<tr>
								<th>脚本<br /> <font color="red" tip="true" title="vo:实体;mode:新增时=1;修改时=2;删除时=3;">(提示)</font></th>
								<td><wcm:widget name="${pixel}.execScript" cmd="codemirror[groovy]{required:true}" value="${vo.execScript}"></wcm:widget></td>
							</tr>
							<tr>
								<th>备注</th>
								<td><wcm:widget cmd="textarea{required:true}" name="${pixel}.description" value="${vo.description}" /></td>
							</tr>
						</table>
					</div>
				</c:forEach>
			</c:if>
		</div>
	</div>
</div>

<%-- 每个模块页面必须引入 --%>
<%@ include file="/include/html_bottom.jsp"%>