<%@ page language="java" pageEncoding="UTF-8"%>

<%-- 每个模块页面必须引入 --%>
<%@ include file="/include/common.jsp"%>
<%@ include file="/include/html_head.jsp"%>

<script type="text/javascript">
	$(function() {
		Ui.changeCurrentTitle('${_zone}', '${node.nodeType.name}[${node.name}]设置');
	});
</script>

<div id="${_zone}_msg_zone"></div>
<form action="${_acp}/submitForm.shtml" sync="true"
	option="{confirmMsg:'是否保存?',errorZone:'${_zone}_msg_zone'}">
	<div tabs="true">
		<div title="${node.nodeType.name}[${node.name}]设置">
			<input type="hidden" name="pdId" value="${param.pdId}" /> <input
				type="hidden" name="activityId" value="${param.activityId}" />
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
					<td><wcm:widget name="description" cmd="textarea"
							value="${vo.description}" /></td>
				</tr>
			</table>
		</div>
		<div title="条件设置">
			<div tabs="true" sort="x" id="${_zone}_deside_zone">
				<c:forEach items="${items}" var="item" varStatus="state">
					<c:set var="pixel" value="A${state.index}" />
					<div title="${item.flowName}">
						<input type="hidden" name="decides" value="${pixel}" /> <input
							type="hidden" name="${pixel}.flowId" value="${item.flowId}" />
						<table class="ws-table">
							<tr>
								<th>连线名称</th>
								<td>${item.flowName}</td>
							</tr>
							<tr>
								<th>成立条件(脚本类型)</th>
								<td><wcm:widget name="${pixel}.decideType"
										cmd="select[@com.riversoft.platform.script.ScriptTypes]{required:true}"
										value="${item.decideType}"></wcm:widget></td>
							</tr>
							<tr>
								<th>成立条件(脚本)<br /> <font color="red" tip="true"
									title="脚本返回boolean值.vo:订单实体;">(提示)</font></th>
								<td><wcm:widget name="${pixel}.decideScript"
										cmd="codemirror[groovy]{required:true}"
										value="${item.decideScript!=null?item.decideScript:'return true;'}"></wcm:widget></td>
							</tr>
							<tr>
								<th>描述</th>
								<td><wcm:widget name="${pixel}.description" cmd="textarea"
										value="${item.description}" /></td>
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