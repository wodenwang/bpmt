<%@ page language="java" pageEncoding="UTF-8"%>

<%-- 每个模块页面必须引入 --%>
<%@ include file="/include/common.jsp"%>
<%@ include file="/include/html_head.jsp"%>

<script type="text/javascript">
	$(function() {
		var $zone = $("#${_zone}");

		//删除资源
		$('button[name=del]', $zone).click(function() {
			var mediaId = $(this).val();
			Core.fn($zone, 'delete')(mediaId);
		});
	})
</script>

<table class="ws-table">
	<input type="hidden" name="media" value="${type}" />

	<c:if test="${type != 'mpnews'}">
		<tr>
			<th style="width: 20px;">操作</th>
			<th>资源ID</th>
			<th>名称</th>
			<th>上传时间</th>
		</tr>
		<c:forEach items="${dp.list}" var="vo">
			<tr>
				<td class="center"><button type="button" name="del" value="${vo.mediaId}" icon="trash" text="false">删除</button></td>
				<td class="left">${vo.mediaId}</td>
				<td class="left"><a href="javascript:void(0);" onclick="Ajax.download('${_acp}/downloadResource.shtml?agentKey=${param.agentKey}&&mediaId=${vo.mediaId}');">${vo.fileName}</a></td>
				<td class="right"><f:formatDate value="${vo.updateTime}" pattern="yyyy-MM-dd HH:mm:ss" /></td>
			</tr>
		</c:forEach>
	</c:if>
	<c:if test="${type == 'mpnews'}">
		<tr>
			<th style="width: 20px;">操作</th>
			<th>资源ID</th>
			<th>内容简述</th>
			<th>上传时间</th>
		</tr>
		<c:forEach items="${dp.list}" var="vo">
			<tr>
				<td class="center"><button type="button" name="del" value="${vo.mediaId}" icon="trash" text="false">删除</button></td>
				<td class="left">${vo.mediaId}</td>
				<td class="left">
					<c:forEach items="${vo.content.news}" var="news" varStatus="loop">
						<span style="color: red">[${loop.index + 1}/${fn:length(vo.content.news)}]</span><span>${news.title}</span><br >
					</c:forEach>
				</td>
				<td class="right"><f:formatDate value="${vo.updateTime}" pattern="yyyy-MM-dd HH:mm:ss" /></td>
			</tr>
		</c:forEach>
	</c:if>
</table>

<wcm:page dp="${dp}" />

<%-- 每个模块页面必须引入 --%>
<%@ include file="/include/html_bottom.jsp"%>