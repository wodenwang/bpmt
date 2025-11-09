<%@ page language="java" pageEncoding="UTF-8"%>

<%-- 每个模块页面必须引入 --%>
<%@ include file="/include/common.jsp"%>
<%@ include file="/include/html_head.jsp"%>

<script type="text/javascript">
	$(function() {
		var $zone = $("#${_zone}");
	});
</script>

<table class="ws-table" form="${_form}">
	<tr>
		<th field="NICK_NAME" style="width: 120px;">昵称</th>
		<th field="SEX" style="width: 30px;">性别</th>
		<th field="CITY" style="width: 60px;">地区</th>
		<th field="SUBSCRIBE_TIME" style="width: 30px;">关注</th>
		<th field="SUBSCRIBE_TIME" style="width: 80px;">关注时间</th>
		<th field="UNSUBSCRIBE_TIME" style="width: 80px;">取消关注时间</th>
		<th field="TAGS" style="width: 80px;">标签</th>
		<th field="REMARK">备注</th>
		<th>OPEN_ID</th>
		<th>UNION_ID</th>
		<th>USER_ID</th>
	</tr>
	<c:forEach items="${dp.list}" var="vo">
		<tr>
			<td class="left"><span tip="true" selector=".logo"><img src="${vo.HEAD_IMG_URL}" style="width: 30px; margin-bottom: -8px;" />${vo.NICK_NAME}<div class="logo">
						<img src="${vo.HEAD_IMG_URL}" style="width: 100px;" />
					</div></span></td>
			<td class="center">${wcm:widget('select[SEX]',vo.SEX)}</td>
			<td class="center"><span tip="true" title="${vo.COUNTRY}-${vo.PROVINCE}-${vo.CITY}">${vo.CITY}</span></td>
			<td class="center">${wcm:widget('select[YES_NO]',vo.SUBSCRIBE)}</td>
			<td class="right"><f:formatDate value="${vo.SUBSCRIBE_TIME}" pattern="yyyy-MM-dd HH:mm:ss" /></td>
			<td class="right"><f:formatDate value="${vo.UNSUBSCRIBE_TIME}" pattern="yyyy-MM-dd HH:mm:ss" /></td>
			<td class="center">${wcm:widget(tagWidget,vo.TAGS)}</td>
			<td class="left">${vo.REMARK}</td>
			<td class="left">${vo.OPEN_ID}</td>
			<td class="left">${vo.UNION_ID}</td>
			<td class="left">${vo.USER_ID}</td>
		</tr>
	</c:forEach>
</table>

<wcm:page dp="${dp}" form="${_form}" />

<%-- 每个模块页面必须引入 --%>
<%@ include file="/include/html_bottom.jsp"%>