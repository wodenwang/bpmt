<%@ page language="java" pageEncoding="UTF-8"%>

<%-- 每个模块页面必须引入 --%>
<%@ include file="/include/common.jsp"%>
<%@ include file="/include/html_head.jsp"%>

<script type="text/javascript">
	$(function() {
		var $zone = $('#${_zone}');

		$('button[name=create]', $zone).click(function() {
			Core.fn($zone, 'create')();
		});

		$('button[name=edit]', $zone).click(function() {
			var widgetKey = $(this).val();
			Core.fn($zone, 'edit')(widgetKey);
		});

		$('button[name=del]', $zone).click(function() {
			var widgetKey = $(this).val();
			Core.fn($zone, 'del')(widgetKey);
		});

	});
</script>

<%--数据表格 --%>
<table class="ws-table" form="${_form}">
	<thead>
		<tr>
			<th style="width: 200px;">操作</th>
			<th field="widgetKey">控件主键</th>
			<th field="busiName">标题</th>
			<th field="description">描述</th>
			<th field="createUid">创建人</th>
			<th field="createDate">创建时间</th>
			<th field="updateDate">更新时间</th>
		</tr>
	</thead>
	<tbody>
		<c:forEach items="${dp.list}" var="vo">
			<tr>
				<td class="center ws-group">
					<button icon="wrench" text="false" type="button" name="edit" value="${vo.widgetKey}">修改</button>
					<button icon="trash" text="false" type="button" name="del" value="${vo.widgetKey}">删除</button>
				</td>
				<td class="center">${vo.widgetKey}</td>
				<td class="left">${vo.busiName}</td>
				<td class="left">${vo.description}</td>
				<td class="center">${wcm:widget('select[$com.riversoft.platform.po.UsUser;uid;busiName]',vo.createUid)}</td>
				<td class="center">${wcm:widget('date',vo.createDate)}</td>
				<td class="center">${wcm:widget('date',vo.updateDate)}</td>
			</tr>
		</c:forEach>
	</tbody>
	<tr>
		<th class="ws-bar">
			<div class="ws-group right" style="float: right;">
				<button type="button" icon="plus" text="true" name="create">新增控件</button>
			</div>
		</th>
	</tr>
</table>

<%-- 分页  --%>
<wcm:page dp="${dp}" form="${_form}" />

<%-- 每个模块页面必须引入 --%>
<%@ include file="/include/html_bottom.jsp"%>