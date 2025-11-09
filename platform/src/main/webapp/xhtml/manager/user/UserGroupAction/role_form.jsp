<%@ page language="java" pageEncoding="UTF-8"%>

<%-- 每个模块页面必须引入 --%>
<%@ include file="/include/common.jsp"%>
<%@ include file="/include/html_head.jsp"%>

<%--定义变量 --%>
<c:set var="isCreate" value="${vo==null}" />

<script type="text/javascript">
	$(function() {
		var $zone = $('#${_zone}');

		//表单动作
		$('form', $zone).submit(function() {
			var $form = $(this);
			Ajax.form('${_zone}', $form, {
				errorZone : '${_zone}_msg',
				confirmMsg : '是否保存?',
				callback : function(flag) {
					if (flag) {
						Core.fn($zone, 'refresh')();
					}
				}
			});
			return false;
		});
	});
</script>

<div tabs="true">
	<c:choose>
		<c:when test="${isCreate}">
			<c:set var="title" value="添加角色" />
		</c:when>
		<c:otherwise>
			<c:set var="title" value="角色[${vo.busiName}]编辑" />
		</c:otherwise>
	</c:choose>
	<div title="${title}">
		<%--错误提示区域 --%>
		<div id="${_zone}_msg"></div>
		<%--表单 --%>
		<form action="${_acp}/submitRoleForm.shtml" method="post" sync="true">
			<input type="hidden" name="isCreate" value="${isCreate?1:0}" />
			<table class="ws-table">
				<tr>
					<th>角色KEY</th>
					<td><wcm:widget name="roleKey" cmd="key{required:true}" value="${vo.roleKey}" state="${isCreate?'normal':'readonly'}" /></td>
				</tr>
				<tr>
					<th>展示名</th>
					<td><wcm:widget name="busiName" cmd="text{required:true}" value="${vo.busiName}" /></td>
				</tr>
				<c:if test="${!isCreate}">
					<tr>
						<th>关联组织</th>
						<td>${otherGroups}</td>
					</tr>
				</c:if>
			</table>
			<div class="ws-bar">
				<button type="submit" icon="disk">保存</button>
			</div>
		</form>
	</div>
	<c:if test="${!isCreate}">
		<div title="权限设置" init="${_acp}/editRolePri.shtml?roleKey=${vo.roleKey}"></div>
	</c:if>
</div>
<%-- 每个模块页面必须引入 --%>
<%@ include file="/include/html_bottom.jsp"%>