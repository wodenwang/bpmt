<%@ page language="java" pageEncoding="UTF-8"%>

<%-- 每个模块页面必须引入 --%>
<%@ include file="/include/common.jsp"%>
<%@ include file="/include/html_head.jsp"%>

<script type="text/javascript">
	$(function() {
		var $zone = $('#${_zone}');

		$('button[name=select]').click(function() {
			var commandKey = $(this).val();
			var json = eval('(' + $("textarea[name='" + commandKey + "']", $zone).val() + ')');
			Core.fn($zone, 'select')(json);
		});

	});
</script>

<%--数据表格 --%>
<table class="ws-table" form="${_form}">
	<thead>
		<tr>
			<th style="width: 60px;">操作</th>
			<th field="busiName">功能模块名称</th>
			<th field="supports">适用范围</th>
			<th field="description">描述</th>
		</tr>
	</thead>
	<tbody>
		<c:forEach items="${sys}" var="vo">
			<tr>
				<td class="center ws-group">
					<button icon="circle-check" text="false" type="button" name="select" value="${vo.commandKey}">选中</button> <textarea style="display: none;" name="${vo.commandKey}">{commandKey:'${vo.commandKey}',busiName:'${vo.busiName}'}</textarea>
				</td>
				<td class="left">${vo.busiName}</td>
				<td class="center">${vo.supports}</td>
				<td class="left">${vo.desc}</td>
			</tr>
		</c:forEach>
	</tbody>
</table>

<%-- 每个模块页面必须引入 --%>
<%@ include file="/include/html_bottom.jsp"%>