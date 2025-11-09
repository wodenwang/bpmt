<%@ page language="java" pageEncoding="UTF-8"%>

<%-- 每个模块页面必须引入 --%>
<%@ include file="/include/common.jsp"%>
<%@ include file="/include/html_head.jsp"%>

<script type="text/javascript">
	$(function() {
		var $zone = $('#${_zone}');

		$('button[name=detail]', $zone).click(function() {
			var $this = $(this);
			Ajax.win('${_acp}/oprDetail.shtml', {
				title : '操作日志详情',
				minWidth : 1024,
				data : {
					id : $this.val()
				}
			});
		});
	});
</script>

<%--数据表格 --%>
<table class="ws-table" form="${_form}">
	<thead>
		<tr>
			<th style="width: 50px;">操作</th>
			<th field="version">版本</th>
			<th field="oprMemo">执行描述</th>
			<th field="oprClass">执行类</th>
			<th field="oprMethod">执行方法</th>
			<th field="createUid">创建人</th>
			<th field="createDate">创建时间</th>
		</tr>
	</thead>
	<tbody>
		<c:forEach items="${dp.list}" var="vo">
			<tr>
				<td class="ws-group center">
					<button type="button" name="detail" icon="search" text="false" value="${vo.id}">查看</button>
				</td>
				<td class="center">${vo.version}</td>
				<td class="left">${vo.oprMemo}</td>
				<td class="left">${vo.oprClass}</td>
				<td class="left">${vo.oprMethod}</td>
				<td class="center">${wcm:widget('select[$com.riversoft.platform.po.UsUser;uid;busiName]',vo.createUid)}</td>
				<td class="center">${wcm:widget('date',vo.createDate)}</td>
			</tr>
		</c:forEach>
	</tbody>
</table>

<%-- 分页  --%>
<wcm:page dp="${dp}" form="${_form}" />

<%-- 每个模块页面必须引入 --%>
<%@ include file="/include/html_bottom.jsp"%>