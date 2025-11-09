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
			<th field="commandKey">逻辑主键</th>
			<th field="busiName">展示名</th>
			<th field="description">描述</th>
			<th style="width: 80px;">适用范围</th>
			<th>用途</th>
			<th field="createDate">创建时间</th>
			<th field="updateDate">更新时间</th>
			<th field="createUid">创建者</th>
		</tr>
	</thead>
	<tbody>
		<c:forEach items="${dp.list}" var="vo">
			<tr>
				<td class="center ws-group">
					<button icon="circle-check" text="false" type="button" name="select" value="${vo.commandKey}">选中</button> <textarea style="display: none;" name="${vo.commandKey}">{commandKey:'${vo.commandKey}',busiName:'${vo.busiName}'}</textarea>
				</td>
				<td class="left">${vo.commandKey}</td>
				<td class="left">${vo.busiName}</td>
				<td class="left">${vo.description}</td>
				<td class="center"><c:choose>
						<c:when test="${vo.mpFlag==1}">公众号</c:when>
						<c:otherwise>企业号</c:otherwise>
					</c:choose></td>
				<td class="left">${wcm:widget('multiselect[@com.riversoft.platform.translate.WxCommandSupportType]',vo.supportType)}</td>
				<td class="right">${wcm:widget('date[datetime]',vo.createDate)}</td>
				<td class="right">${wcm:widget('date[datetime]',vo.updateDate)}</td>
				<td class="center">${wcm:widget('select[$com.riversoft.platform.po.UsUser;uid;busiName]',vo.createUid)}</td>
			</tr>
		</c:forEach>
	</tbody>
</table>

<%-- 分页  --%>
<wcm:page dp="${dp}" form="${_form}" />

<%-- 每个模块页面必须引入 --%>
<%@ include file="/include/html_bottom.jsp"%>