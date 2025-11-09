<%@ page language="java" pageEncoding="UTF-8"%>

<%-- 每个模块页面必须引入 --%>
<%@ include file="/include/common.jsp"%>
<%@ include file="/include/html_head.jsp"%>


<script type="text/javascript">
	$(function() {
		var $zone = $('#${_zone}');

		//修改
		$('button[name=edit]').click(function() {
			var val = $(this).val();
			Core.fn($zone, 'edit')(val);
		});

		//删除
		$('button[name=del]').click(function() {
			var val = $(this).val();
			Core.fn($zone, 'del')(val);
		});

		//新建
		$('button[name=create]').click(function() {
			Core.fn($zone, 'create')();
		});

	});
</script>

<%--数据表格 --%>
<table class="ws-table" form="${_zone}_form">
	<thead>
		<tr>
			<th style="width: 75px;">操作</th>
			<th field="urlKey">逻辑主键</th>
			<th field="description">描述</th>
			<th field="wxKey">登录体系</th>
			<th field="urlKey">超链接</th>
			<th field="action">绑定视图</th>
			<th field="createDate">创建时间</th>
			<th field="updateDate">更新时间</th>
			<th field="createUid">创建人</th>
		</tr>
	</thead>
	<tbody>
		<c:forEach items="${dp.list}" var="vo">
			<tr>
				<td class="center ws-group">
					<button icon="trash" text="false" type="button" name="del" value="${vo.urlKey}">删除</button>
					<button icon="wrench" text="false" type="button" name="edit" value="${vo.urlKey}">修改</button>
				</td>
				<td class="center">${vo.urlKey}</td>
				<td class="left">${vo.description}</td>
				<c:if test="${vo.wxType == 0}">
					<td class="center">企业号</td>
				</c:if>
				<c:if test="${vo.wxType == 1}">
					<td class="center">公众号(${wcm:widget('select[$WxMp;mpKey;title]',vo.wxKey)}) - <span style="color: red;">${wcm:widget('radio[@com.riversoft.platform.translate.WxScope]',vo.wxScope)}</span></td>
				</c:if>
				<td class="left"><span style="color: red;">${domain}${vo.urlKey}</span></td>
				<td class="center">${wcm:widget('view[WX]',vo.action)}</td>
				<td class="right">${wcm:widget('date[datetime]',vo.createDate)}</td>
				<td class="right">${wcm:widget('date[datetime]',vo.updateDate)}</td>
				<td class="center">${wcm:widget('select[$com.riversoft.platform.po.UsUser;uid;busiName]',vo.createUid)}</td>
			</tr>
		</c:forEach>
	</tbody>
	<tr>
		<th class="ws-bar">
			<div class="ws-group right">
				<button type="button" icon="plus" text="true" name="create">新建超链接</button>
			</div>
		</th>
	</tr>
</table>

<%-- 分页  --%>
<wcm:page dp="${dp}" form="${_zone}_form" />

<%-- 每个模块页面必须引入 --%>
<%@ include file="/include/html_bottom.jsp"%>