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

		//预览事件
		$('button[name=preview]', $zone).click(function() {
			Ajax.win('${_acp}/preview.shtml?type=${param._se_dataType}', {
				title : '控件预览',
				minWidth : 500
			});
		});

	});
</script>

<%--数据表格 --%>
<table class="ws-table" form="${_zone}_form">
	<thead>
		<tr>
			<th check="true"></th>
			<th style="width: 60px;">操作</th>
			<th field="dataCode">代码</th>
			<th field="parentCode">父代码</th>
			<th field="showName">翻译值</th>
			<th field="sort">排序</th>
			<th field="extra">扩展字段</th>
			<th field="description">描述</th>
		</tr>
	</thead>
	<tbody>
		<c:forEach items="${dp.list}" var="vo">
			<tr>
				<td check="true" value="${vo.dataCode}" checkname="dataCode"></td>
				<td class="center ws-group">
					<button icon="wrench" text="false" type="button" name="edit"
						value="${vo.dataCode}">编辑</button>
				</td>
				<td class="center"><c:choose>
						<c:when test="${vo.parentCode==null||vo.parentCode==''}">
							<b>${vo.dataCode}</b>
						</c:when>
						<c:otherwise>${vo.dataCode}</c:otherwise>
					</c:choose></td>
				<td class="center">${vo.parentCode}</td>
				<td class="center">${vo.showName}</td>
				<td class="right">${vo.sort}</td>
				<td class="left">${vo.extra}</td>
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
				<button type="button" icon="calculator" text="true" name="preview">数据预览</button>
				<button type="button" icon="plus" text="true" name="add">新增</button>
			</div>
		</th>
	</tr>
</table>

<%-- 分页  --%>
<wcm:page dp="${dp}" form="${_zone}_form" />

<%-- 每个模块页面必须引入 --%>
<%@ include file="/include/html_bottom.jsp"%>