<%@ page language="java" pageEncoding="UTF-8"%>

<%-- 每个模块页面必须引入 --%>
<%@ include file="/include/common.jsp"%>
<%@ include file="/include/html_head.jsp"%>

<script type="text/javascript">
	$(function() {
		var $zone = $('#${_zone}');

		$('button[name=download]', $zone).click(function() {
			var $this = $(this);
			Ui.confirm('确认下载快照包?', function() {
				Ajax.download('${_acp}/download.shtml', {
					data : {
						id : $this.val()
					}
				});
			});
		});

		$('button[name=devlog]', $zone).click(function() {
			var $this = $(this);
			Ajax.win('${_acp}/oprList.shtml?_ne_version=' + $this.val(), {
				title : '操作日志列表',
				minWidth : 1024
			});
		});
	});
</script>

<%--数据表格 --%>
<table class="ws-table" form="${_form}">
	<thead>
		<tr>
			<th style="width: 80px;">操作</th>
			<th field="name">名称</th>
			<th field="description">描述</th>
			<th field="version">版本</th>
			<th field="platformVersion">平台版本</th>
			<th field="createUid">创建人</th>
			<th field="createDate">创建时间</th>
		</tr>
	</thead>
	<tbody>
		<c:forEach items="${dp.list}" var="vo">
			<tr>
				<td class="center ws-group">
					<button type="button" name="download" icon="arrowthickstop-1-s" text="false" value="${vo.id}">下载快照</button>
					<button type="button" name="devlog" icon="comment" text="false" value="${vo.version}">开发日志</button>
				</td>
				<td class="left">${vo.name}</td>
				<td class="left">${vo.description}</td>
				<td class="center">${vo.version}</td>
				<td class="center">${vo.platformVersion}</td>
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