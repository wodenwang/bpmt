<%@ page language="java" pageEncoding="UTF-8"%>

<%-- 每个模块页面必须引入 --%>
<%@ include file="/include/common.jsp"%>
<%@ include file="/include/html_head.jsp"%>

<script type="text/javascript">
	$(function() {
		var $zone = $('#${_zone}');
		//新增事件
		$('button[name=add]', $zone).click(function() {
			Core.fn($zone, 'add')();
		});

		//编辑事件
		$('button[name=edit]', $zone).click(function() {
			Core.fn($zone, 'edit')($(this).val());
		});

		//删除事件
		$('button[name=del]', $zone).click(function() {
			Core.fn($zone, 'delete')();
		});

		//展示明细
		$('a.ws-link', $zone).click(function() {
			Core.fn($zone, 'detail')($(this).html());
		});

	});
</script>

<%--数据表格 --%>
<table class="ws-table" form="${_zone}_form">
	<thead>
		<tr>
			<th check="true"></th>
			<th style="width: 60px;">操作</th>
			<th field="functionKey">函数名</th>
			<th field="description">描述</th>
		</tr>
	</thead>
	<tbody>
		<c:forEach items="${dp.list}" var="vo">
			<tr>
				<td check="true" value="${vo.functionKey}" checkname="functionKey"></td>
				<td class="center ws-group">
					<button icon="wrench" text="false" type="button" name="edit" value="${vo.functionKey}">编辑</button>
				</td>
				<td class="left"><a class="ws-link" href="javascript:void(0);">${vo.functionKey}</a></td>
				<td class="left">${vo.description}</td>
			</tr>
		</c:forEach>
	</tbody>
	<tr>
		<th class="ws-bar">
			<div class="left">
				<span class="ws-group">
					<button type="button" icon="trash" text="true" name="del">删除</button>
				</span>
			</div>
			<div class="ws-group right">
				<button type="button" icon="plus" text="true" name="add">新增</button>
			</div>
		</th>
	</tr>
</table>

<%-- 分页  --%>
<wcm:page dp="${dp}" form="${_zone}_form" />

<%-- 每个模块页面必须引入 --%>
<%@ include file="/include/html_bottom.jsp"%>