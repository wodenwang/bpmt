<%@ page language="java" pageEncoding="UTF-8"%>

<%-- 每个模块页面必须引入 --%>
<%@ include file="/include/common.jsp"%>
<%@ include file="/include/html_head.jsp"%>

<script type="text/javascript">
	$(function() {
		var $zone = $('#${_zone}');

		//新建
		$('button[name=create]').click(function() {
			Core.fn($zone, 'create')();
		});

		//删除
		$('button[name=del]').click(function() {
			var commandKey = $(this).val();
			Core.fn($zone, 'remove')(commandKey);
		});

		//设置
		$('button[name=edit]').click(function() {
			var commandKey = $(this).val();
			Core.fn($zone, 'edit')(commandKey);
		});

	});
</script>

<%--数据表格 --%>
<table class="ws-table" form="${_form}">
	<thead>
		<tr>
			<th style="width: 80px;">操作</th>
			<th field="commandKey">逻辑主键</th>
			<th field="busiName">展示名</th>
			<th field="description">描述</th>
			<th colspan="2">适用范围</th>
			<th field="createDate">创建时间</th>
			<th field="updateDate">更新时间</th>
			<th field="createUid">创建者</th>
		</tr>
	</thead>
	<tbody>
		<c:forEach items="${dp.list}" var="vo">
			<tr>
				<td class="center ws-group">
					<button icon="wrench" text="false" type="button" name="edit" value="${vo.commandKey}">设置</button>
					<button icon="trash" text="false" type="button" name="del" value="${vo.commandKey}">删除</button>
				</td>
				<td class="left">${vo.commandKey}</td>
				<td class="left">${vo.busiName}</td>
				<td class="left">${vo.description}</td>
				<td class="left">${wcm:widget('select[@com.riversoft.platform.translate.WxCommandMpFlag]',vo.mpFlag)}</td>
				<td class="left">${wcm:widget('multiselect[@com.riversoft.platform.translate.WxCommandSupportType]',vo.supportType)}</td>
				<td class="right">${wcm:widget('date[datetime]',vo.createDate)}</td>
				<td class="right">${wcm:widget('date[datetime]',vo.updateDate)}</td>
				<td class="center">${wcm:widget('select[$com.riversoft.platform.po.UsUser;uid;busiName]',vo.createUid)}</td>
			</tr>
		</c:forEach>
	</tbody>
	<tr>
		<th class="ws-bar">
			<div class="right">
				<span class="ws-group">
					<button type="button" icon="plus" text="true" name="create">新建处理器</button>
				</span>
			</div>
		</th>
	</tr>
</table>

<%-- 分页  --%>
<wcm:page dp="${dp}" form="${_form}" />

<%-- 每个模块页面必须引入 --%>
<%@ include file="/include/html_bottom.jsp"%>