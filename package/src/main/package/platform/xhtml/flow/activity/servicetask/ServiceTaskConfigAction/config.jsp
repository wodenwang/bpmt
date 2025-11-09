<%@ page language="java" pageEncoding="UTF-8"%>

<%-- 每个模块页面必须引入 --%>
<%@ include file="/include/common.jsp"%>
<%@ include file="/include/html_head.jsp"%>

<script type="text/javascript">
	$(function() {

		Ui.changeCurrentTitle('${_zone}', '${node.nodeType.name}[${node.name}]设置');

		var $zone = $('#${_zone}');
		$("[name$='.description']", $zone).blur(function() {
			var $this = $(this);
			var val = $(this).val();
			if (val != null && val != '') {
				$('a', $("li[aria-controls='" + $this.parents('div[tab]:first').attr('id') + "']", $this.parents('[tabs=true]:first'))).html(val);
			}
		});

		$('button[name=itemAdd]', $zone).click(function() {
			var $tabs = $('#${_zone}_logic_tabs');
			Ajax.tab($tabs, '${_acp}/addLogicForm.shtml', {
				data : {
					pixel : 'B' + Core.nextSeq()
				}
			});
		});
	});
</script>
<div id="${_zone}_msg_zone"></div>
<form action="${_acp}/submitForm.shtml" sync="true" option="{confirmMsg:'是否保存?',errorZone:'${_zone}_msg_zone'}">
	<div tabs="true">
		<div title="${node.nodeType.name}[${node.name}]设置">
			<input type="hidden" name="pdId" value="${param.pdId}" /> <input type="hidden" name="activityId" value="${param.activityId}" />
			<table class="ws-table">
				<tr>
					<th>节点类型</th>
					<td>${node.nodeType.name}</td>
				</tr>
				<tr>
					<th>节点名</th>
					<td>${node.name}</td>
				</tr>
				<tr>
					<th>备注</th>
					<td><wcm:widget name="description" cmd="textarea" value="${vo.description}" /></td>
				</tr>
			</table>
		</div>
		<div title="处理逻辑">
			<div class="ws-bar">
				<div class="ws-group left">
					<button icon="plus" type="button" name="itemAdd">新增逻辑</button>
				</div>
			</div>
			<div tabs="true" button="left" sort="y" id="${_zone}_logic_tabs">
				<c:forEach items="${logics}" var="item" varStatus="state">
					<c:set var="pixel" value="A${state.index}" />
					<div title="${item.description}" close="true">
						<input type="hidden" name="logics" value="${pixel}" />
						<table class="ws-table">
							<tr>
								<th>处理逻辑(脚本类型)</th>
								<td><wcm:widget name="${pixel}.logicType" cmd="select[@com.riversoft.platform.script.ScriptTypes]{required:true}" value="${item.logicType}"></wcm:widget></td>
							</tr>
							<tr>
								<th>处理逻辑(脚本)<br /> <font color="red" tip="true" title="vo:订单实体;">(提示)</font></th>
								<td><wcm:widget name="${pixel}.logicScript" cmd="codemirror[groovy]{required:true}" value="${item.logicScript}"></wcm:widget></td>
							</tr>
							<tr>
								<th>出错处理方式</th>
								<td><wcm:widget name="${pixel}.errorType" cmd="radio[@com.riversoft.module.flow.activity.servicetask.ErrorType]{required:true}" value="${item.errorType}" /></td>
							</tr>
							<tr>
								<th>描述</th>
								<td><wcm:widget name="${pixel}.description" cmd="textarea{required:true}" value="${item.description}" /></td>
							</tr>
						</table>
					</div>
				</c:forEach>
			</div>
		</div>
	</div>
</form>

<%-- 每个模块页面必须引入 --%>
<%@ include file="/include/html_bottom.jsp"%>